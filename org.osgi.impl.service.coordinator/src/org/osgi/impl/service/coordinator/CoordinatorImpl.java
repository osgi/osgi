/*
 * Copyright (c) OSGi Alliance (2010, 2011). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.impl.service.coordinator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.CoordinationException;
import org.osgi.service.coordinator.CoordinationPermission;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.service.coordinator.Participant;
import org.osgi.service.log.LogService;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

/**
 *
 */
@Component(servicefactory = true)
public class CoordinatorImpl implements Coordinator {
	static List<CoordinatorImpl>							coordinators	= new CopyOnWriteArrayList<CoordinatorImpl>();
	static WeakHashMap<Thread, List<CoordinationImpl>>		stacks			= new WeakHashMap<Thread, List<CoordinationImpl>>();
	static IdentityHashMap<Participant, CoordinationImpl>	locks			= new IdentityHashMap<Participant, CoordinationImpl>();
	static SecurityManager									sm				= System
																					.getSecurityManager();
	final List<CoordinationImpl>							coordinations	= new CopyOnWriteArrayList<CoordinationImpl>();
	volatile Bundle											bundle;
	volatile LogService										log;
	volatile long											timeout;
	volatile boolean										gone;

	/**
	 * @param context
	 */
	final @Activate
	protected void activate(ComponentContext context) {
		coordinators.add(this);
		bundle = context.getUsingBundle();
	}

	/**
	 * @param context
	 */
	@Deactivate
	protected void deactivate(ComponentContext context) {
		gone = true;
		coordinators.remove(this);

		for (Coordination c : coordinations) {
			c.fail(new ServiceException("Service is unregistered",
					ServiceException.UNREGISTERED));
		}

		for (CoordinatorImpl impl : coordinators) {
			assert impl != this;
			impl.killDependentCoordinations(this);
		}
	}

	void killDependentCoordinations(CoordinatorImpl impl) {
		// TODO
		// assert impl != this;
		// for (CoordinationImpl c : coordinations) {
		// if ( c.participantDependency.contains(this))
		// c.fail( new CoordinationException("", c,
		// CoordinationException.PARTICIPANT_ENDED));
		// }
	}

	public CoordinationImpl create(String name, long t) {
		check(CoordinationPermission.INITIATE);
		CoordinationImpl c = new CoordinationImpl(this, name, t);
		coordinations.add(c);
		return c;
	}

	public Coordination begin(String name, long timeoutInMillis) {
		CoordinationImpl c = create(name, timeoutInMillis);
		push(c);
		return c;
	}

	public List<Coordination> getCoordinations() {
		check(CoordinationPermission.ADMIN);
		List<Coordination> l = new ArrayList<Coordination>();
		for (CoordinatorImpl coordinator : coordinators) {
			l.addAll(coordinator.coordinations);
		}
		return Collections.unmodifiableList(l);
	}

	/**
	 * @param log
	 */
	@Reference
	protected void setLog(LogService log) {
		this.log = log;
	}

	public boolean addParticipant(Participant participant)
			throws CoordinationException {
		check(CoordinationPermission.PARTICIPATE);
		Coordination c = peek();
		if (c == null)
			return false;

		c.addParticipant(participant);
		return true;
	}

	public boolean fail(Throwable reason) {
		check(CoordinationPermission.PARTICIPATE);
		Coordination c = peek();
		if (c == null)
			return false;

		c.fail(reason);
		return false;
	}

	public Coordination peek() {
		check(CoordinationPermission.PARTICIPATE);
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
		check(CoordinationPermission.INITIATE);
		return pop(Thread.currentThread());
	}

	public Coordination getCoordination(long id) {
		check(CoordinationPermission.ADMIN);
		for (Coordination c : getCoordinations()) {
			if (c.getId() == id)
				return c;
		}
		return null;
	}

	private CoordinationImpl pop(Thread thread) {
		check(CoordinationPermission.INITIATE);
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

	Coordination push(Coordination c) {
		check(CoordinationPermission.INITIATE);
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

	private void check(String action) throws CoordinationException {
		if (gone)
			throw new ServiceException("Coordinator is ungotten",
					ServiceException.UNREGISTERED);

		if (action == null || sm == null)
			return;

		CoordinationPermission cp = new CoordinationPermission(bundle, null,
				action);
		sm.checkPermission(cp);
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

}
