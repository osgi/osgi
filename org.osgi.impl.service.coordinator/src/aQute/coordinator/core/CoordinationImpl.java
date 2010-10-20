package aQute.coordinator.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import org.osgi.service.coordinator.*;
import org.osgi.service.log.*;

public class CoordinationImpl implements Coordination {
	static final AtomicInteger		counter			= new AtomicInteger(1000);
	final long						id;
	final List<Participant>			participants	= new CopyOnWriteArrayList<Participant>();
	final CoordinatorImpl			coordinator;
	final String					name;
	final Map<Class< ? >, Object>	variables		= new ConcurrentHashMap<Class< ? >, Object>();
	final TimerTask					timeouter		= new TimerTask() {
														public void run() {
															fail(new TimeoutException());
														}
													};
	volatile long					deadline		= 0;
	volatile Throwable				failure;
	boolean							terminated		= false;
	Thread							stackThread;

	CoordinationImpl(CoordinatorImpl coordinator, String name, int timeout) {
		this.coordinator = coordinator;
		this.name = name;
		this.id = counter.incrementAndGet();

		if (timeout > 0) {
			deadline = System.currentTimeMillis() + timeout;
			coordinator.timer.schedule(timeouter, new Date(deadline));
		}
	}

	public void end() throws CoordinationException {
		if (wasTerminated(null)) {
			if (failure != null)
				throw new CoordinationException(
						"Coordination had ended in a failure", this, failure);
			else
				throw new CoordinationException("Coordination already ended",
						this, CoordinationException.ALREADY_ENDED);
		}

		boolean partiallyFailed = false;

		for (Participant p : participants) {
			try {
				p.ended(this);
			}
			catch (Throwable t) {
				partiallyFailed = true;
				coordinator.log.log(LogService.LOG_ERROR, "Participant " + p
						+ " throws exception in ended(" + name + ")", t);
			}
			finally {
				unlock(p);
			}
		}
		if (partiallyFailed)
			throw new CoordinationException("Participants failed, see log",
					this, CoordinationException.PARTIALLY_ENDED);
	}

	public long extendTimeout(int timeInMillis) {
		if (deadline == 0 || timeInMillis == 0)
			return deadline;

		timeouter.cancel();
		deadline = deadline + timeInMillis;
		synchronized (this) {
			if (!terminated)
				coordinator.timer.schedule(timeouter, new Date(deadline));
		}
		return deadline;
	}

	private synchronized boolean wasTerminated(Throwable failure) {
		if (terminated)
			return true;

		this.failure = failure;
		timeouter.cancel();
		coordinator.clear(this);
		terminated = true;
		return false;
	}

	public boolean fail(Throwable failure) {
		if (wasTerminated(failure))
			return false;

		Thread thread = stackThread;

		for (Participant p : participants) {
			try {
				p.failed(this);
			}
			catch (Throwable t) {
				coordinator.log.log(LogService.LOG_ERROR, "Participant " + p
						+ " throws exception in failed(" + this + ")", t);
			}
			finally {
				unlock(p);
			}
		}

		if (thread != Thread.currentThread())
			thread.interrupt();

		return true;
	}

	public Throwable getFailure() {
		return failure;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Collection< ? extends Participant> getParticipants() {
		return new ArrayList<Participant>(participants);
	}

	public Map<Class< ? >, Object> getVariables() {
		return variables;
	}

	public void addParticipant(Participant p) {
		synchronized (this) {
			if (terminated)
				throw new CoordinationException("Coordination already ended",
						this, CoordinationException.ALREADY_ENDED);

			if (lock(p))
				participants.add(p); // Only adds once
		}
	}

	private boolean lock(Participant p) {
		synchronized (coordinator.locks) {
			while (true) {
				CoordinationImpl other = coordinator.locks.get(p);
				if (other == null)
					break;

				if (other == this)
					return false;

				if (other.stackThread == Thread.currentThread())
					throw new CoordinationException(
							"Trying to use the same participant on multiple coordinations in the same thread",
							this, CoordinationException.DEADLOCK_DETECTED);

				try {
					coordinator.locks.wait(coordinator.timeout);
				}
				catch (InterruptedException e) {
					throw new CoordinationException("Interrupted", this,
							CoordinationException.LOCK_INTERRUPTED);
				}
			}
			coordinator.locks.put(p, this);
			return true;
		}
	}

	private void unlock(Participant p) {
		synchronized (coordinator.locks) {
			CoordinationImpl c = coordinator.locks.remove(p);
			assert c == this;
			coordinator.locks.notifyAll();
		}
	}

	public synchronized boolean isTerminated() {
		return terminated;
	}

	public String toString() {
		return name + ":" + id;
	}

}
