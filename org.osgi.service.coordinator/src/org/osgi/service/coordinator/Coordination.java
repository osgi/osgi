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
package org.osgi.service.coordinator;

import java.util.List;
import java.util.Map;

/**
 * A Coordination object is used to coordinate a number of independent
 * Participants.
 * 
 * <p>
 * Once a Coordination is {@link Coordinator#create(String, long) created}, it
 * can be used to add {@link Participant} objects. When the Coordination is
 * ended, the participants are {@link Participant#ended(Coordination) notified}.
 * A Coordination can also fail for various reasons. When this occurs, the
 * participants are {@link Participant#failed(Coordination) notified} of the
 * failure.
 * 
 * <p>
 * A Coordination must be in one of two states, either ACTIVE or TERMINATED. The
 * transition between ACTIVE and TERMINATED must be atomic, ensuring that a
 * Participant can be guaranteed of either receiving an exception when adding
 * itself to a Coordination or of receiving notification the Coordination has
 * terminated.
 * 
 * <p>
 * A Coordination object is thread safe and can be passed as a parameter to
 * other parties regardless of the threads these parties use.
 * 
 * <p>
 * The following example code shows how a Coordination can be used.
 * 
 * <pre>
 * void foo() {
 * 	Coordination c = coordinator.create(&quot;work&quot;, 0);
 * 	try {
 * 		doWork(c);
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
 * @ThreadSafe
 * @noimplement
 * @version $Id$
 */

public interface Coordination {

	/**
	 * A singleton exception that will be the failure cause when a Coordination
	 * times out.
	 */
	Exception	TIMEOUT	= new Exception("TIMEOUT");

	/**
	 * A singleton exception that will be the failure cause when the
	 * Coordinations created by a bundle are terminated because the bundle
	 * released the Coordinator service.
	 */
	Exception	RELEASED	= new Exception("RELEASED");

	/**
	 * Returns the id assigned to this Coordination.
	 * 
	 * The id is assigned by the {@link Coordinator} service which created this
	 * Coordination and is unique among all the Coordinations created by the
	 * Coordinator service and must not be reused as long as the Coordinator
	 * service remains registered. The id must be positive and monotonically
	 * increases for each Coordination created by the Coordinator service.
	 * 
	 * @return The id assigned to this Coordination.
	 */
	long getId();

	/**
	 * Returns the name of this Coordination.
	 * 
	 * The name is specified when this Coordination was created.
	 * 
	 * @return The name of this Coordination.
	 * 
	 */
	String getName();

	/**
	 * Terminate this Coordination normally.
	 * 
	 * <p>
	 * If this Coordination is already terminated, a CoordinationException is
	 * thrown. If this Coordination was terminated as a failure, the
	 * {@link #getFailure() failure cause} will be the cause of the thrown
	 * CoordinationException.
	 * 
	 * <p>
	 * Otherwise, this Coordination is terminated normally. If this Coordination
	 * has been {@link #push() pushed} on a thread local Coordination stack then
	 * it must be removed from that stack during termination.
	 * 
	 * <p>
	 * After this Coordination is terminated, all registered
	 * {@link #getParticipants() Participants} are
	 * {@link Participant#ended(Coordination) notified} using the current
	 * thread. Participants should finalize any work associated with this
	 * Coordination. The successful return of this method indicates that the
	 * Coordination has terminated normally and all registered Participants have
	 * been notified of the normal termination.
	 * 
	 * <p>
	 * It is possible that one of the Participants throws an exception during
	 * notification. If this happens, this Coordination is considered to have
	 * partially failed and this method must throw a CoordinationException of
	 * type {@link CoordinationException#PARTIALLY_ENDED} after all the
	 * registered Participants have been notified.
	 * 
	 * @throws CoordinationException If this Coordination has failed, including
	 *         timed out, or partially failed.
	 */
	void end();

	/**
	 * Terminate this Coordination as a failure with the specified failure
	 * cause.
	 * 
	 * <p>
	 * If this Coordination is already {@link #isTerminated() terminated}, this
	 * method does nothing and returns {@code false}.
	 * 
	 * <p>
	 * Otherwise, this Coordination is terminated as a failure with the
	 * specified failure cause. If this Coordination has been {@link #push()
	 * pushed} on a thread local Coordination stack then it must be removed from
	 * that stack during termination.
	 * 
	 * <p>
	 * After this Coordination is terminated, all registered
	 * {@link #getParticipants() Participants} are
	 * {@link Participant#failed(Coordination) notified} using the current
	 * thread. Participants should discard any work associated with this
	 * Coordination. This method will return {@code true}.
	 * 
	 * @param cause The failure cause. The failure cause must not be
	 *        {@code null}.
	 * @return {@code true} if this Coordination was active and was terminated
	 *         by this method, otherwise {@code false}.
	 * @throws NullPointerException If cause is {@code null}.
	 */
	boolean fail(Throwable cause);

	/**
	 * Returns the failure cause of this Coordination.
	 * 
	 * <p>
	 * If this Coordination is {@link #isTerminated() terminated} with a
	 * {@link #fail(Throwable) failure cause}, this method will return the
	 * failure cause.
	 * 
	 * <p>
	 * If this Coordination timed out, this method will return {@link #TIMEOUT}
	 * as the failure cause. If this Coordination was active when the bundle
	 * that created it released the {@link Coordinator} service, this method
	 * will return {@link #RELEASED} as the failure cause.
	 * 
	 * @return The failure cause of this Coordination or {@code null} if this
	 *         Coordination has not terminated as a failure.
	 */
	Throwable getFailure();

	/**
	 * Returns whether this Coordination is terminated.
	 * 
	 * @return {@code true} if this Coordination is terminated, otherwise
	 *         {@code false} if this Coordination is active.
	 */
	boolean isTerminated();

	/**
	 * Register a Participant with this Coordination.
	 * 
	 * <p>
	 * Once a Participant is registered with this Coordination, it is guaranteed
	 * to receive a notification for either
	 * {@link Participant#ended(Coordination) normal} or
	 * {@link Participant#failed(Coordination) failure} termination when this
	 * Coordination is terminated.
	 * 
	 * <p>
	 * Participants are registered using their object identity. Once a
	 * Participant is registered with this Coordination, subsequent attempts to
	 * register the Participant again with this Coordination are ignored and the
	 * Participant is only notified once when this Coordination is terminated.
	 * 
	 * <p>
	 * A Participant can only be registered with a single active Coordination at
	 * a time. If a Participant is already registered with an active
	 * Coordination, attempts to register the Participation with another active
	 * Coordination will block until the Coordination the Participant is
	 * registered with terminates. Notice that in edge cases the notification to
	 * the Participant that this Coordination has terminated can happen before
	 * this method returns.
	 * 
	 * <p>
	 * Attempting to register a Participant with a {@link #isTerminated()
	 * terminated} Coordination will result in a CoordinationException being
	 * thrown.
	 * 
	 * <p>
	 * The ordering of notifying Participants must follow the order in which the
	 * Participants were registered.
	 * 
	 * @param participant The Participant to register with this Coordination.
	 *        The participant must not be {@code null}.
	 * @throws CoordinationException If the Participant could not be registered
	 *         with this Coordination. This exception should normally not be
	 *         caught by the caller but allowed to be caught by the initiator of
	 *         this Coordination.
	 * @throws NullPointerException If participant is {@code null}.
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission#PARTICIPATE} action for the current
	 *         Coordination, if any.
	 */
	void addParticipant(Participant participant);

	/**
	 * Returns a snapshot of the Participants registered with this Coordination.
	 * 
	 * @return A snapshot of the Participants registered with this Coordination.
	 *         If no Participants are registered with this Coordination, the
	 *         returned list will be empty. The list is ordered in the order the
	 *         Participants were registered. The returned list is the property
	 *         of the caller and can be modified by the caller.
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission#ADMIN} action for the
	 *         {@link CoordinationPermission}.
	 */
	List<Participant> getParticipants();

	/**
	 * Returns the variable map associated with this Coordination.
	 * 
	 * Each Coordination has a map that can be used for communicating between
	 * different Participants. The key of the map is a class, allowing for
	 * private data to be stored in the map by using implementation classes or
	 * shared data by using shared interfaces.
	 * 
	 * The returned map is not synchronized. Users of the map must synchronize
	 * on the Map object while making changes.
	 * 
	 * @return The variable map associated with this Coordination.
	 */
	Map<Class< ? >, Object> getVariables();

	/**
	 * Extend the time out of this Coordination.
	 * 
	 * <p>
	 * Participants can call this method to extend the timeout of this
	 * Coordination with at least the specified time. This can be done by
	 * Participants when they know a task will take more than normal time.
	 * 
	 * This method returns the new deadline. Specifying a timeout extension of 0
	 * will return the existing deadline.
	 * 
	 * @param timeMillis The time in milliseconds to extend the current timeout.
	 *        If the initial timeout was specified as 0, no extension must take
	 *        place. A zero must have no effect.
	 * @return The new deadline in milliseconds. If the specified time is 0, the
	 *         existing deadline is returned. If this Coordination was created
	 *         with an initial timeout of 0, no timeout is set and 0 is
	 *         returned.
	 * @throws CoordinationException If this Coordination
	 *         {@link #isTerminated() is terminated}.
	 * @throws IllegalArgumentException If the specified time is negative.
	 */
	long extendTimeout(long timeMillis);

	/**
	 * Wait until this Coordination is terminated and all registered
	 * Participants have been notified.
	 * 
	 * @param timeMillis Maximum time in milliseconds to wait. Specifying a time
	 *        of 0 will wait until this Coordination is terminated.
	 * @throws InterruptedException If the wait is interrupted.
	 * @throws IllegalArgumentException If the specified time is negative.
	 */

	void join(long timeMillis) throws InterruptedException;

	/**
	 * Push this Coordination object onto the thread local Coordination stack to
	 * make it the {@link Coordinator#peek() current Coordination}.
	 * 
	 * @return This Coordination.
	 * @throws CoordinationException If this Coordination is already on the any
	 *         thread's thread local Coordination stack.
	 */
	Coordination push();

	/**
	 * Returns the thread in whose thread local Coordination stack this
	 * Coordination has been {@link #push() pushed}.
	 * 
	 * @return The thread in whose thread local Coordination stack this
	 *         Coordination has been pushed or {@code null} if this Coordination
	 *         is not in any thread's thread local Coordination stack.
	 */
	Thread getThread();
}
