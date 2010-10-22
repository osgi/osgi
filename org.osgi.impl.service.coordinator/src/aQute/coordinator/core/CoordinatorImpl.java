package aQute.coordinator.core;

import java.util.*;
import java.util.concurrent.*;

import org.osgi.framework.*;
import org.osgi.service.component.*;
import org.osgi.service.coordinator.*;
import org.osgi.service.log.*;

import aQute.bnd.annotation.component.*;

@Component(servicefactory = true)
public class CoordinatorImpl implements Coordinator {
	static List<CoordinatorImpl>							coordinators	= new CopyOnWriteArrayList<CoordinatorImpl>();
	final List<CoordinationImpl>							coordinations	= new CopyOnWriteArrayList<CoordinationImpl>();
	final WeakHashMap<Thread, List<CoordinationImpl>>		stacks			= new WeakHashMap<Thread, List<CoordinationImpl>>();
	LogService												log;
	final IdentityHashMap<Participant, CoordinationImpl>	locks			= new IdentityHashMap<Participant, CoordinationImpl>();
	long													timeout;
	volatile boolean										gone;

	@Activate
	protected void activate(ComponentContext context) {
		coordinators.add(this);
	}

	@Deactivate
	protected void deactivate(ComponentContext context) {
		gone = true;
		coordinators.remove(this);
		for (Coordination c : coordinations) {
			c.fail(new ServiceException("Service is unregistered",
					ServiceException.UNREGISTERED));
		}
	}

	public CoordinationImpl create(String name, int timeout) {
		check();
		CoordinationImpl c = new CoordinationImpl(this, name, timeout);
		coordinations.add(c);
		return c;
	}

	public Coordination begin(String name, int timeoutInMillis) {
		check();
		CoordinationImpl c = create(name, timeoutInMillis);
		push(c);
		return c;
	}

	public List< ? extends Coordination> getCoordinations() {
		check();
		List<CoordinationImpl> l = new ArrayList<CoordinationImpl>();
		for (CoordinatorImpl coordinator : coordinators) {
			l.addAll(coordinator.coordinations);
		}
		return Collections.unmodifiableList(l);
	}

	@Reference
	protected void setLog(LogService log) {
		check();
		this.log = log;
	}

	public boolean addParticipant(Participant participant)
			throws CoordinationException {
		check();
		Coordination c = getCurrentCoordination();
		if (c == null)
			return false;

		c.addParticipant(participant);
		return true;
	}

	public boolean failed(Throwable reason) {
		check();
		Coordination c = getCurrentCoordination();
		if (c == null)
			return false;

		c.fail(reason);
		return false;
	}

	public Coordination getCurrentCoordination() {
		check();
		synchronized (stacks) {
			List<CoordinationImpl> stack = stacks.get(Thread.currentThread());
			if (stack == null)
				return null;

			int n = stack.size() - 1;
			if (n < 0)
				return null;

			return stack.get(n);
		}
	}

	public Coordination pop() {
		check();
		return pop(Thread.currentThread());
	}

	private CoordinationImpl pop(Thread thread) {
		check();
		synchronized (stacks) {
			List<CoordinationImpl> stack = stacks.get(thread);
			if (stack == null)
				return null;

			int n = stack.size() - 1;

			CoordinationImpl coordination = null;
			if (n >= 0) {
				coordination = stack.remove(n);
				coordination.stackThread = null;
			}
			if (stack.isEmpty())
				stacks.remove(stack);

			return coordination;
		}
	}

	public Coordination push(Coordination c) {
		check();
		synchronized (stacks) {
			CoordinationImpl cc = (CoordinationImpl) c;
			if (cc.terminated)
				error(cc, "Coordination is already terminated",
						CoordinationException.ALREADY_ENDED);

			if (cc.stackThread != null)
				error(cc, "Coordination already on stack",
						CoordinationException.ALREADY_PUSHED);

			cc.stackThread = Thread.currentThread();
			List<CoordinationImpl> stack = stacks.get(cc.stackThread);
			if (stack == null) {
				stack = new ArrayList<CoordinationImpl>();
				stacks.put(cc.stackThread, stack);
			}
			stack.add(cc); // push
			return cc;
		}
	}

	private void check() {
		if (gone)
			throw new ServiceException("Coordinator is ungotten",
					ServiceException.UNREGISTERED);
	}

	private void error(CoordinationImpl cc, String message, int reason) {
		CoordinationException e = new CoordinationException(message, cc, reason);
		log.log(LogService.LOG_ERROR, message, e);
		throw e;
	}

	/**
	 * Remove the coordination from the stack, if any.
	 * 
	 * @param c the coordination to remove
	 */
	void clear(final CoordinationImpl c) {
		coordinations.remove(c);
		synchronized (stacks) {
			if (c.stackThread == null)
				return;

			List<CoordinationImpl> stack = stacks.get(c.stackThread);
			assert stack != null;
			assert stack.size() > 0;

			stack.remove(c);
			c.stackThread = null;
			if (stack.isEmpty())
				stacks.remove(stack);
		}
	}

	public Coordination getCoordination(long id) {
		for (Coordination c : getCoordinations()) {
			if (c.getId() == id)
				return c;
		}
		return null;
	}

}
