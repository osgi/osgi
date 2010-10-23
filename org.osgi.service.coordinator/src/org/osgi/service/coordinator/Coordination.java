/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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
package org.osgi.service.coordinator;

import java.util.List;
import java.util.Map;

/**
 * A Coordination object is used to coordinate a number of independent
 * participants. Once a Coordination is created (through
 * {@link Coordinator#begin(String, int)} or
 * {@link Coordinator#create(String, int)}, it can be used to add Participant
 * objects. When the Coordination is ended, the participants are called back on
 * the {@link Participant#ended(Coordination)} method. A Coordination can also
 * fail for various reasons, in that case the participants are informed of this
 * failure by calling their {@link Participant#failed(Coordination)} method.
 * 
 * A Coordination is in two states, either ACTIVE or TERMINATED. The transition
 * between this state must be atomic, ensuring that a participant can be
 * guaranteed of a callback or exception when registering.
 * 
 * A Coordination object is thread safe and can be passed as a parameter to
 * other parties regardless of the threads these parties use.
 * 
 * @ThreadSafe
 */

public interface Coordination {

	/**
	 * The TIMEOUT exception is a singleton exception that will the reason for
	 * the failure when the Coordination times out.
	 */
	public Exception	TIMEOUT	= new Exception("Singleton Timeout Exception");

	/**
	 * A system assigned ID unique for a registered Coordinator. This id must
	 * not be reused as long as the Coordinator is registered and must be
	 * monotonically increasing.
	 * 
	 * @return an id
	 */
	long getId();

	/**
	 * Return the name of this Coordination.
	 * 
	 * The name is given in the {@link Coordinator#begin(String,int)} or
	 * {@link Coordinator#create(String,int)} method. The name should follow the
	 * same naming pattern as a Bundle Symbolc Name.
	 * 
	 * @return the name of this Coordination
	 * 
	 */
	String getName();

	/**
	 * Fail this Coordination.
	 * 
	 * If this Coordination is not terminated, fail it and call the
	 * {@link Participant#failed(Coordination)} method on all participant on the
	 * current thread. Participants must assume that the Coordination failed and
	 * should discard and cleanup any work that was processed during this
	 * Coordination. The {@link #fail(Throwable)} method will return {@code
	 * true} if it caused the termination.
	 * 
	 * A fail method must return silently when the Coordination has already
	 * finished and return {@code false}.
	 * 
	 * The fail method must terminate the current Coordination before any of the
	 * failed methods are called. That is, the
	 * {@link Participant#failed(Coordination)} methods must be running outside
	 * the current coordination, adding participants during this phase will
	 * cause a Configuration Exception to be thrown.
	 * 
	 * If the Coordination is pushed on the Coordinator stack it is associated
	 * with a specific thread. If this is another thread than the current thread
	 * it must be interrupted.
	 * 
	 * @param reason The reason of the failure, must not be {@code null}
	 * @return {@code true} if the Coordination was active and this coordination
	 *         was terminated due to this call, otherwise {@code false}
	 */
	boolean fail(Throwable reason);

	/**
	 * End the current Coordination.
	 * 
	 * <pre>
	 * void foo() throws CoordinationException {
	 * 	Coordination c = coordinator.begin(&quot;work&quot;, 0);
	 * 	try {
	 * 		doWork();
	 * 	}
	 * 	catch (Exception e) {
	 * 		c.fail(e);
	 * 	}
	 * 	finally {
	 * 		c.end();
	 * 	}
	 * }
	 * </pre>
	 * 
	 * If the coordination was terminated this method throws a Configuration
	 * Exception.
	 * 
	 * Otherwise, any participants will be called on their
	 * {@link Participant#ended(Coordination)} method. A successful return of
	 * this {@link #end()} method indicates that the Coordination has properly
	 * terminated and any participants have been informed of the positive
	 * outcome.
	 * 
	 * It is possible that one of the participants throws an exception during
	 * the callback. If this happens, the coordination fails partially and this
	 * is reported with an exception.
	 * 
	 * This method must terminate the current Coordination before any of the
	 * {@link Participant#ended(Coordination)} methods are called. That is, the
	 * {@link Participant#ended(Coordination)} methods must be running outside
	 * the current coordination, no participants can be added during the
	 * termination phase.
	 * 
	 * If the Coordination is on a thread local stack then it must be removed
	 * from this stack during termination.
	 * 
	 * @throws CoordinationException when the Coordination has (partially)
	 *         failed or timed out.
	 *         <ol>
	 *         <li>{@link CoordinationException#PARTIALLY_ENDED}</li>
	 *         <li>{@link CoordinationException#ALREADY_ENDED}</li>
	 *         <li>{@link CoordinationException#FAILED}</li>
	 *         <li>{@link CoordinationException#UNKNOWN}</li>
	 *         </ol>
	 */
	void end() throws CoordinationException;

	/**
	 * Return an iterable list of the participants that joined the Coordination.
	 * This list is only valid as long as the Coordination has not been
	 * terminated. New participants can enter this list up until termination.
	 * The list must remain after termination for post-mortem debugging.
	 * 
	 * @return list of participants.
	 * 
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission#ADMIN} action for the
	 *         {@link CoordinationPermission}.
	 */
	List< ? extends Participant> getParticipants();

	/**
	 * If the coordination has failed because {@link #fail(Throwable)} was
	 * called then this method can provide the Throwable that was given as
	 * argument to the {@link #fail(Throwable)} method. A timeout on this
	 * Coordination will set the failure to a TimeoutException.
	 * 
	 * @return a Throwable if this Coordination has failed, otherwise {code
	 *         null} if no failure occurred.
	 */
	Throwable getFailure();

	/**
	 * Add a Participant to this Coordination.
	 * 
	 * Once a Participant is participating it is guaranteed to receive a call
	 * back on either the {@link Participant#ended(Coordination)} or
	 * {@link Participant#failed(Coordination)} method when the Coordination is
	 * terminated.
	 * 
	 * A participant can be added to the Coordination multiple times but it must
	 * only be called back once when the Coordination is terminated. A
	 * Participant can only participate at a single Coordination, if it attempts
	 * to block at another Coordination, then it will block until prior
	 * Coordinations are finished. Notice that in edge cases the call back can
	 * happen before this method returns.
	 * 
	 * The ordering of the call-backs must follow the order of participation. If
	 * participant is participating multiple times the first time it
	 * participates defines this order.
	 * 
	 * @param participant The participant of the Coordination
	 * @throws CoordinationException This exception should normally not be
	 *         caught by the caller but allowed to bubble up to the initiator of
	 *         the coordination, it is therefore a {@link RuntimeException}. It
	 *         signals that this participant could not participate the current
	 *         coordination. This can be cause by the following reasons:
	 *         <ol>
	 *         <li>{@link CoordinationException#DEADLOCK_DETECTED} - Tried to lock the participant but it was already locked on another Configuration on the same thread</li>
	 *         <li>{@link CoordinationException#LOCK_INTERRUPTED} - Received an interrupt while waiting for the lock to release</li>
	 *         <li>{@link CoordinationException#ALREADY_ENDED} - The Coordination has already ended</li>
	 *         <li>{@link CoordinationException#FAILED} - The Coordination has failed</li>
	 *         <li>{@link CoordinationException#UNKNOWN}</li>
	 *         </ol>
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission#INITIATE} action for the current
	 *         Coordination, if any.
	 */
	void addParticipant(Participant participant) throws CoordinationException;

	/**
	 * A utility map associated with the current Coordination.
	 * 
	 * Each coordination carries a map that can be used for communicating
	 * between different participants. To namespace of the map is a class,
	 * allowing for private date to be stored in the map by using implementation
	 * classes or shared data by interfaces. The returned map must be thread
	 * safe.
	 * 
	 * @return The thread safe map
	 */
	Map<Class< ? >, Object> getVariables();

	/**
	 * Extend the time out. Allows participants to extend the timeout of the
	 * coordination with at least the given amount. This can be done by
	 * participants when they know a task will take more than normal time.
	 * 
	 * This method returns the new deadline. Passing 0 will return the existing
	 * deadline.
	 * 
	 * @param timeInMillis Add this timeout to the current timeout. If the
	 *        timeout was 0, no extension takes place.
	 * @return the new deadline in the format of
	 *         {@link System#currentTimeMillis()} or 0 if no timeout was set.
	 * @throws CoordinationException Can throw
	 *         <ol>
	 *         <li>{@link CoordinationException#ALREADY_ENDED}</li>
	 *         <li>{@link CoordinationException#FAILED}</li>
	 *         <li>{@link CoordinationException#UNKNOWN}</li>
	 *         </ol>
	 */

	long extendTimeout(int timeInMillis) throws CoordinationException;

	/**
	 * @return true if this Coordination is terminated otherwise false
	 */
	boolean isTerminated();

	/**
	 * Answer the associated thread or {@code null}.
	 * 
	 * @return Associated thread or {@code null}
	 */
	Thread getThread();
	
	/**
	 * Wait until the Coordination is completely finished.
	 * @param timeoutInMillis Maximum time to wait, 0 is forever
	 * @throws InterruptedException If the wait is interrupted
	 */
	
	void join(long timeoutInMillis) throws InterruptedException;
}
