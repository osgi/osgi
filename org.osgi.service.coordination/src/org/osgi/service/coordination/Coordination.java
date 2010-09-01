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
 * A Coordination object is used to coordinate a number of independent
 * participants. Once a Coordination is created, it can be used to add
 * Participant objects. When the Coordination is ended, the participants are
 * called back. A Coordination can also fail for various reasons, in that case
 * the participants are informed of this failure.
 * 
 * @ThreadSafe
 */

public interface Coordination {
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
	 * always fail ({@link #fail(String)}).
	 */
	int	FAILED			= 2;

	/**
	 * Return value of {@link Coordination#end()}. The Coordination failed
	 * because it had timed out.
	 */
	int	TIMEOUT			= 3;

	/**
	 * Return the name of this Coordination.
	 * 
	 * The name is given in the {@link Coordinator#begin(String)} or
	 * {@link Coordinator#create(String)} method.
	 * 
	 * @return the name of this Coordination
	 * 
	 */
	String getName();

	/**
	 * Fail and then end this Coordination while returning the outcome.
	 * 
	 * Any participants will be called on their {@link Participant#failed()}
	 * method. Participants must assume that the Coordination failed and should
	 * discard and cleanup any work that was processed during this Coordination.
	 * 
	 * The fail method must terminate the current Coordination before any of the
	 * failed methods are called. That is, the {@link Participant#failed()}
	 * methods must be running outside the current coordination, no participants
	 * can be added during the termination phase.
	 * 
	 * A fail method must return silently when the Coordination has already
	 * finished.
	 * 
	 * @param reason The reason of the failure for documentation
	 * @return {@code true} if the Coordination was still active, otherwise
	 *         {@code false}
	 */
	boolean fail(Throwable reason);

	/**
	 * If the Coordination is terminated then return, otherwise set the
	 * Coordination to fail. This method enables the following fail-safe pattern
	 * to ensure Coordinations are properly terminated.
	 * 
	 * <pre>
	 *   Coordination c = coordinator.begin("show_fail");
	 *   try {
	 *     work1();
	 *     work2();
	 *     if ( end() != OK )
	 *        log("...");
	 *   } catch( SomeException e) {
	 *      ...
	 *   } finally {
	 *      c.terminate();
	 *   }
	 * </pre>
	 * 
	 * With this pattern, it is easy to ensure that the coordination is always
	 * terminated.
	 * 
	 * @return {@code true} if this method actually terminated the coordination
	 *         (that is, it was not properly ended). {@code false} if the
	 *         Coordination was already properly terminate by an {@link #end()}
	 *         or {@link #fail(String)} method.
	 */
	boolean terminate();

	/**
	 * End the current Coordination.
	 * 
	 * Any participants will be called on their {@link Participant#ended()}
	 * method. This {@link #end()} method indicates that the Coordination has
	 * properly terminated and any participants should
	 * 
	 * The end method must terminate the current Coordination before any of the
	 * {@link Participant#ended()} methods is called. That is, the
	 * {@link Participant#ended()} methods must be running outside the current
	 * coordination, no participants can be added during the termination phase.
	 * 
	 * This method returns the outcome of the Coordination:
	 * <ol>
	 * <li>{@link #OK} - Correct outcome, no exceptions thrown</li>
	 * <li>{@link #PARTIALLY_ENDED} - One of the participants threw an exception
	 * </li>
	 * <li>{@link #FAILED} - The Coordination was set to always fail</li>
	 * </ol>
	 * 
	 * @return {@link #OK}, {@link #PARTIALLY_ENDED}, {@link #FAILED}
	 * @throws IllegalStateException when the Coordination is already
	 *         terminated.
	 */
	int end() throws IllegalStateException;

	/**
	 * Return the current list of participants that joined the Coordination.
	 * This list is only valid as long as the Coordination has not been
	 * terminated. That is, after {@link #end()} or {@link #fail(String)} is
	 * called this method will return an empty list.
	 * 
	 * @return list of participants.
	 * 
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission.ADMIN} action for the
	 *         {@link CoordinationPermission}.
	 */
	Collection<Participant> getParticipants();

	/**
	 * @return {@code true} if this Coordination has failed, {@code false}
	 *         otherwise.
	 * 
	 */
	boolean isFailed();

	/**
	 * Add a minimum timeout for this Coordination.
	 * 
	 * If this timeout expires, then the Coordination will fail and the
	 * initiating thread will be interrupted. This method must only be called on
	 * an active Coordination, that is, before {@link #end()} or
	 * {@link #fail(String)} is called.
	 * 
	 * If the current deadline is arriving later than the given timeout then the
	 * timeout is ignored.
	 * 
	 * @param timeOutInMs Number of ms to wait, zero means forever.
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission.ADMIN} or
	 *         {@link CoordinationPermission.INITIATE} action for the
	 *         {@link CoordinationPermission}.
	 */
	void addTimeout(long timeOutInMs);

	/**
	 * Add a Participant to this Coordination.
	 * 
	 * If this method returns {@code true} then there was a current Coordination
	 * and the participant has successfully joined it. If there was no current
	 * Coordination then {@code false} is returned.
	 * 
	 * Once a Participant is participating it is guaranteed to receive a call
	 * back on either the {@link Participant#ended()} or
	 * {@link Participant#failed()} method when the Coordination is terminated.
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
	 * @return {@code true} if the Coordination was active, otherwise {@code
	 *         false}.
	 * @throws CoordinationException This exception should normally not be
	 *         caught by the caller but allowed to bubble up to the initiator of
	 *         the coordination, it is therefore a {@link RuntimeException}. It
	 *         signals that this participant could not participate the current
	 *         coordination. This can be cause by the following reasons:
	 *         <ol>
	 *         <li>{@link CoordinationException#DEADLOCK_DETECTED}</li>
	 *         <li>{@link CoordinationException#TIMEOUT}</li>
	 *         <li>{@link CoordinationException#UNKNOWN}</li>
	 *         </ol>
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission.INITIATE} action for the current
	 *         Coordination, if any.
	 */
	boolean participate(Participant p);

	/**
	 * A utility map associated with the current Coordination.
	 * 
	 * Each coordination carries a map that can be used for communicating
	 * between different participants. To namespace of the map is a class,
	 * allowing for private date to be stored in the map by using implementation
	 * classes or shared data by interfaces.
	 * 
	 * @return The map
	 */
	Map<Class< ? >, ? > getVariables();
}
