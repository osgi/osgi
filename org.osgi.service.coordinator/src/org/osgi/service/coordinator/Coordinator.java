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
package org.osgi.service.coordinator;

import java.util.*;

/**
 * A Coordinator service coordinates activities between different parties.
 * 
 * The Coordinator can create Coordination objects. Once a Coordination object
 * is created, it can be pushed on a thread local stack
 * {@link #push(Coordination)} as an implicit parameter for calls to other
 * parties, or it can be passed as an argument. The current top of the thread
 * local stack can be obtained with {@link #getCurrentCoordination()}.
 * 
 * The {@link #addParticipant(Participant)} method on this service or the
 * {@link Coordination#addParticipant(Participant)} method can be used to
 * participate in a Coordination. Participants participate only in a single
 * Coordination, if a Participant object is added to a second Coordination the
 * {@link Coordination#addParticipant(Participant)} method is blocked until the
 * first Coordination is terminated.
 * 
 * A Coordination ends correctly when the {@link Coordination#end} method is
 * called before termination or when the Coordination fails due to a timeout or
 * a failure. If the Coordination ends correctly, all its participants are
 * called on the {@link Participant#ended(Coordination)} method, in all other
 * cases the {@link Participant#failed(Coordination)} is called.
 * 
 * The typical usage of the Coordinator service is as follows:
 * 
 * <pre>
 * Coordination coordination = coordinator.begin(&quot;mycoordination&quot;);
 * try {
 * 	doWork();
 * }
 * finally {
 * 	coordination.end();
 * }
 * </pre>
 * 
 * In the doWork() method, code can be called that requires a callback at the
 * end of the Coordination. The doWork method can then add a Participant to the
 * coordination. This code is for a Participant.
 * 
 * <pre>
 * 
 * void doWork() {
 * 	if (coordinator.addParticipant(this)) {
 * 		beginWork();
 * 	}
 * 	else {
 * 		beginWork();
 * 		finishWork();
 * 	}
 * }
 * 
 * void ended() {
 * 	finishWork();
 * }
 * 
 * void failed() {
 * 	undoWork();
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
	 * Create a new Coordination that is not associated with the current thread.
	 * 
	 * @param name The name of this coordination, a name does not have to be
	 *        unique. The name must follow the bundle symbolic name pattern,
	 *        e.g. com.example.coordination.
	 * @param timeout Timeout in milliseconds, less or equal than 0 means no
	 *        timeout
	 * @return The new Coordination object or {@code null}
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission#INITIATE} action, no bundle check
	 *         is done.
	 */
	Coordination create(String name, int timeout);

	/**
	 * Provide a immutable snapshot collection of all Coordination objects
	 * currently active.
	 * 
	 * Coordinations in this list can have terminated before this list is
	 * returned or any time thereafter.
	 * 
	 * The returned collection must only contain the Coordinations for which the
	 * caller has {@link CoordinationPermission#ADMIN}, without this permission
	 * an empty list must be returned.
	 * 
	 * @return a list of Coordination objects filter by
	 *         {@link CoordinationPermission#ADMIN}.
	 */
	Collection< ? extends Coordination> getCoordinations();

	/**
	 * Always fail the current Coordination, if it exists.
	 * 
	 * Must fail the current Coordination and return {@code true} or return
	 * {@code false} if there is no current Coordination.
	 * 
	 * @param reason The reason for failure, must not be {@code null}.
	 * @return {@code true} if there was a current Coordination and {@code
	 *         false} if not.
	 */
	boolean failed(Throwable reason);

	/**
	 * Return the current Coordination or {@code null}.
	 * 
	 * The current Coordination is the top of the thread local stack of
	 * Coordinations. If the stack is empty, there is no current Coordination.
	 * 
	 * @return {@code null} when the thread local stack is empty, otherwise the
	 *         top of the thread local stack of Coordinations.
	 */
	Coordination getCurrentCoordination();

	/**
	 * Begin a new Coordination and push it on the thread local stack with
	 * {@link #push(Coordination)}.
	 * 
	 * @param name The name of this coordination, a name does not have to be
	 *        unique.
	 * @param timeoutInMillis Timeout in milliseconds, less or equal than 0
	 *        means no timeout
	 * @return A new Coordination object
	 * 
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission#INITIATE} action, no bundle check
	 *         is done.
	 */
	Coordination begin(String name, int timeoutInMillis);

	/**
	 * Associate the given Coordination object with a thread local stack. The
	 * top of the thread local stack is returned with the
	 * {@link #getCurrentCoordination()} method. To remove the Coordination from
	 * the top call {@link #pop()}.
	 * 
	 * @param c The Coordination to push
	 * @return c (for the builder pattern purpose)
	 * @throws CoordinationException Can throw the
	 *         <ol>
	 *         <li>{@link CoordinationException#ALREADY_PUSHED}</li>
	 *         <li>{@link CoordinationException#UNKNOWN}</li>
	 *         </ol>
	 */
	Coordination push(Coordination c) throws CoordinationException;

	/**
	 * Pop the top of the thread local stack of Coordinations.
	 * 
	 * If no current Coordination is present, return {@code null}.
	 * 
	 * @return The top of the stack or {@code null}
	 */
	Coordination pop();

	/**
	 * Participate in the current Coordination and return {@code true} or return
	 * {@code false} if there is none.
	 * 
	 * This method calls {@link #getCurrentCoordination()}, if it is {@code
	 * null}, it will return false. Otherwise it will call
	 * {@link Coordination#addParticipant(Participant)}.
	 * 
	 * @param participant The participant of the Coordination
	 * @return {@code true} if there was a current Coordination that could be
	 *         successfully used to participate, otherwise return {@code false}.
	 * @throws CoordinationException This exception should normally not be
	 *         caught by the caller but allowed to bubble up to the initiator of
	 *         the coordination, it is therefore a {@link RuntimeException}. It
	 *         signals that this participant could not participate the current
	 *         coordination. This can be cause by the following reasons:
	 *         <ol>
	 *         <li>{@link CoordinationException#DEADLOCK_DETECTED}</li>
	 *         <li>{@link CoordinationException#ALREADY_ENDED}</li>
	 *         <li>{@link CoordinationException#LOCK_INTERRUPTED}</li>
	 *         <li>{@link CoordinationException#FAILED}</li>
	 *         <li>{@link CoordinationException#UNKNOWN}</li>
	 *         </ol>
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission#PARTICIPATE} action for the current
	 *         Coordination, if any.
	 */
	boolean addParticipant(Participant participant)
			throws CoordinationException;

	/**
	 * Answer the coordination associated with the given id.
	 * 
	 * @param id The id of the requested Coordination
	 * @return a Coordination with the given ID or {@code null} when
	 *         Coordination cannot be found.
	 */

	Coordination getCoordination(long id);

}
