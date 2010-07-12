/*
 * Copyright (c) OSGi Alliance (2000, 2008). All Rights Reserved.
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
package org.osgi.service.coordination;

import java.util.*;

/**
 * A Coordinator service provides a facility to coordinate activities on a
 * thread.
 * 
 * An <em>initiator</em> thread can create a Coordination with the
 * {@link #begin(String)} method. This Coordination is a thread local object and
 * is not thread safe. Once the Coordination is active, any called party that
 * wants to participate on this Coordination can call
 * {@link #participate(Participant)}. A Participant can only participate on a
 * single thread. If a Participant tries to join multiple Coordinations it will
 * be blocked until the first Coordination has been finished. If blocking is not
 * desired, Participants should always use a unique object to participate.
 * 
 * Any party can always set the Coordination to always fail with the
 * {@link #alwaysFail()} method.
 * 
 * At the end of the Coordination, the initiator can successfully terminate the
 * Coordination by called {@link Coordination#end()} or
 * {@link Coordination#fail()}. Any participants will be notified of the
 * termination ({@link Participant#ended()} or {@link Participant#failed()})
 * depending on the outcome.
 * 
 * The initiator of a Coordination handles the Coordination object. All
 * participants only interact with the Coordinator service. That is, the
 * Coordination object is a capability for the initiator.
 * 
 * The usage of the Coordinator service is normally as follows:
 * 
 * <pre>
 * Coordination c = getCoordinator().begin(&quot;test-coordination&quot;);
 * try {
 * 	foo();
 * 	bar();
 * 	if (c.end() != OK)
 * 		log(&quot;Coordination failed to end properly: &quot; + c);
 * }
 * catch (SomeException e) {
 * 	c.fail(e.toString());
 * 	throw e;
 * }
 * finally {
 * 	c.terminate();
 * }
 * </pre>
 * 
 * A potential participant can join a Coordination as follows:
 * 
 * <pre>
 * public class FooImpl implements Participant {
 * 	Coordinator	coordinator;
 * 	Set&lt;String&gt;	queue	= new HashSet&lt;String&gt;();
 * 
 * 	public void doWork(String work) {
 * 		if (!coordinator.participate(this)) {
 * 			actualWork(work);
 * 		}
 * 		else {
 * 			queue.add(work);
 * 		}
 * 	}
 * 
 * 	public void failed() {
 * 		queue.clear();
 * 	}
 * 
 * 	public void ended() {
 * 		for (String work : queue) {
 * 			actualWork(work);
 * 		}
 * 	}
 * 
 * 	void actualWork(String work) { ... }
 * }
 * </pre>
 * 
 * This code is very simple because the Coordinator must lock participants that
 * attempt to participate on multiple threads. Due to this locking, a
 * participant does not require any synchronization.
 * 
 * A Participant that wants to initiate a Coordination of none is active can als
 * use the {@link #participateOrBegin(Participant)} method:
 * 
 * <pre>
 * public class FooImpl implements Participant {
 * 	Coordinator	coordinator;
 * 	Set&lt;String&gt;	queue	= new HashSet&lt;String&gt;();
 * 
 * 	public void doWork(String work) {
 * 		Coordination c = coordinator.beginOrParticipate(this);
 * 		if (c != null) {
 * 			try {
 * 				actualWork(work);
 * 				if (c.end() != OK)
 * 					log(&quot;oops&quot;);
 * 			}
 * 			catch (Throwable t) {
 * 				c.fail(e.toString());
 * 			}
 * 		}
 * 		else {
 * 			queue.add(work);
 * 		}
 * 	}
 * 
 * 	public void failed() {
 * 		queue.clear();
 * 	}
 * 
 * 	public void ended() {
 * 		for (String work : queue) {
 * 			actualWork(work);
 * 		}
 * 	}
 * 
 * 	void actualWork(String work) { ... }
 * }
 * </pre>
 * 
 * Life cycle. All Coordinations that are begun through this service must
 * automatically fail before this service is ungotten.
 * 
 * @ThreadSafe
 */
public interface Coordinator {
	/**
	 * Return value of {@link Coordination#end()}. The Coordination ended
	 * normally, no participant threw an exception.
	 */
	int	OK				= 0;

	/**
	 * Return value of {@link Coordination#end()}. The Coordination did not end
	 * normally, a participant threw an exception making the outcome unclear.
	 */
	int	PARTIALLY_ENDED	= 1;

	/**
	 * Return value of {@link Coordination#end()}. The Coordination was set to
	 * always fail ({@link #alwaysFail(String)}).
	 */
	int	FAILED			= 2;

	/**
	 * Return value of {@link Coordination#end()}. The Coordination failed
	 * because it had timed out.
	 */
	int	TIMEOUT			= 3;

	/**
	 * Begin a new Coordination on the current thread when the thread is
	 * currently not associated with a Coordination.
	 * 
	 * @param name The name of this coordination, a name does not have to be
	 *        unique.
	 * @throws IllegalStateException When the current thread is already
	 *         associated with a Coordination
	 * @return The new Coordination object
	 * 
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission.INITIATE} action, no bundle check
	 *         is done.
	 */
	Coordination begin(String name) throws IllegalStateException;

	/**
	 * Participate in the current Coordination or return false if there is none.
	 * 
	 * A Participant that wants to participate in an active Coordination can
	 * call this method. If this method returns <code>true</code> then there was
	 * an active Coordination and the participant has successfully joined it. If
	 * there was no active Coordination then <code>false</code> is returned.
	 * 
	 * Once a Participant is participating it is guaranteed to receive a
	 * callback on either the {@link Participant#ended()} or
	 * {@link Participant#failed()} method when the Coordination is terminated
	 * on the same thread as where it participates.
	 * 
	 * A Participant can only participate on one Coordination at a time. The
	 * Coordinator must block the participant if it attempts to participate on
	 * different threads. Blocking is based on object identity so if a
	 * Participant does not want to block it should always use different
	 * instances. Due to this blocking, a participant can be written as a not
	 * thread safe object because the Coordination ensures that all calls happen
	 * on the same thread.
	 * 
	 * A participant can be added to the Coordination multiple times but it must
	 * only be called back once when the Coordination is terminated.
	 * 
	 * The ordering of the callbacks must follow the order of participation. If
	 * participant is participating multiple times the first time it
	 * participates defines this order.
	 * 
	 * A Coordination will timeout after an implementation defined amount of
	 * time that must be higher than 30 seconds unless overridden with
	 * configuration. This time can be set on a per Coordination basis with the
	 * {@link Coordination#setTimeout(long)} method.
	 * 
	 * @param participant The participant of the Coordination
	 * @return <code>true</code> if there was an active Coordination on the
	 *         current thread that could be successfully used to participate,
	 *         otherwise <code>false</code>.
	 * @throws CoordinationException This exception should normally not be
	 *         caught by the caller but allowed to bubble up to the initiator of
	 *         the coordination, it is therefore a {@link RuntimeException}. It
	 *         signals that this participant could not participate the current
	 *         coordination. This can be cause by the following reasons:
	 *         <ol>
	 *         <li>{@link CoordinationException#DEADLOCK_DETECTED}</li>
	 *         <li>{@link CoordinationException#DEADLOCK_TIMEOUT}</li>
	 *         <li>{@link CoordinationException#DEADLOCK_UNKNOWN}</li>
	 *         </ol>
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission.PARTICIPATE} action for the current
	 *         Coordination, if any.
	 */
	boolean participate(Participant participant) throws CoordinationException;

	/**
	 * Participate if there is an active Coordination otherwise initiate a new
	 * Coordination.
	 * 
	 * If a method requires a Coordination to be active and is willing to begin
	 * one if not then this method is a convenience method representing the
	 * following code:
	 * 
	 * <pre>
	 * if (coordinator.isActive()) {
	 * 	coordinator.participate(participant);
	 * 	return null;
	 * }
	 * else {
	 * 	return coordinator.begin(&quot;...&quot;);
	 * }
	 * </pre>
	 * 
	 * This method makes it simple to start a new Coordination or to participate
	 * in an existing Coordination. See {@link #begin(String, long)} and
	 * {@link #participate(Participant)} for the details of those methods.
	 * 
	 * If a new Coordination is begun, the participant must <em>not</em> be
	 * added to the Coordination, it is only added to a prior active
	 * Coordination.
	 * 
	 * @param ifActive
	 * @return <code>null</code> if there is an active Coordination otherwise a
	 *         newly initiated Coordination.
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission.PARTICIPATE} action for the current
	 *         Coordination, if any. Otherwise it requires
	 *         {@link CoordinationPermission.INITIATE} to create a new
	 *         coordination.
	 */
	Coordination participateOrBegin(Participant ifActive);

	/**
	 * Always fail an active Coordination.
	 * 
	 * Must fail an active Coordination and return <code>true</code> or return
	 * <code>false</code> if there is no active Coordination.
	 * 
	 * @param reason The reason why it must always fail or <code>null</code>.
	 * @return <code>true</code> if a Coordination was active and
	 *         <code>false</code> if not.
	 */
	boolean alwaysFail(String reason);

	/**
	 * Test if the current thread is associated with an active Coordination.
	 * Return <code>true</code> if there is an active Coordination otherwise
	 * <code>false</code>.
	 * 
	 * @return <code>true</code> if there is an active Coordination otherwise
	 *         <code>false</code>
	 */
	boolean isActive();

	/**
	 * Test if there is an active Coordination and if so it has failed. This
	 * method returns <code>true</code> if there is an active Coordination and
	 * that Coordination has been set to fail. In all other cases, including
	 * when there is no active Coordination, this method must return
	 * <code>false</code>
	 * 
	 * @return <code>true</code> when the active Coordination is set to failed,
	 *         otherwise false.
	 */
	boolean isFailed();

	/**
	 * Provide a list of Coordination objects.
	 * 
	 * Answer a read only list of active Coordination. This list must be a
	 * mutable snapshot of the current situation. Changes to the list must not
	 * affect the original.
	 * 
	 * Coordination objects are capabilities and designed to be used only on the
	 * Coordination thread. The returned list must only contain the
	 * Coordinations for which the caller has
	 * {@link CoordinationPermission.ADMIN}, without this permission an empty
	 * list must be returned.
	 * 
	 * @return a list of Coordination objects filter by
	 *         {@link CoordinationPermission.ADMIN}.
	 */
	Collection<Coordination> getCoordinations();
}
