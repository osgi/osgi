/*
 * Copyright (c) OSGi Alliance (2010, 2013). All Rights Reserved.
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

import java.util.Collection;

/**
 * A Coordinator service coordinates activities between different parties.
 * 
 * <p>
 * A bundle can use the Coordinator service to create {@link Coordination}
 * objects. Once a Coordination object is created, it can be
 * {@link Coordination#push() pushed} on the thread local Coordination stack to
 * be an implicit parameter as the current Coordination for calls to other
 * parties, or it can be passed directly to other parties as an argument. The
 * current Coordination, which is on the top of the current thread's thread
 * local Coordination stack, can be obtained with {@link #peek()}.
 * 
 * <p>
 * Any active Coordinations created by a bundle must be terminated when the
 * bundle releases the Coordinator service. The Coordinator service must
 * {@link Coordination#fail(Throwable) fail} these Coordinations with the
 * {@link Coordination#RELEASED RELEASED} exception.
 * 
 * <p>
 * A {@link Participant} can {@link Coordination#addParticipant(Participant)
 * register} to participate in a Coordination and receive notification of the
 * termination of the Coordination.
 * 
 * <p>
 * The following example code shows a example usage of the Coordinator service.
 * 
 * <pre>
 * void foo() {
 *   Coordination c = coordinator.begin(&quot;work&quot;, 0);
 *   try {
 *     doWork();
 *   } catch (Exception e) {
 *     c.fail(e);
 *   } finally {
 *     c.end();
 *   }
 * }
 * </pre>
 * 
 * In the {@code doWork} method, code can be called that requires notification
 * of the termination of the Coordination. The {@code doWork} method can then
 * register a Participant with the Coordination.
 * 
 * <pre>
 * void doWork() {
 *   if (coordinator.addParticipant(this)) {
 *     beginWork();
 *   } else {
 *     beginWork();
 *     finishWork();
 *   }
 * }
 * 
 * void ended(Coordination c) {
 *   finishWork();
 * }
 * 
 * void failed(Coordination c) {
 *   undoWork();
 * }
 * </pre>
 * 
 * @ThreadSafe
 * @noimplement
 * @author $Id$
 */
public interface Coordinator {

	/**
	 * Create a new Coordination.
	 * 
	 * @param name The name of this coordination. The name does not have to be
	 *        unique but must follow the {@code symbolic-name} syntax from the
	 *        Core specification.
	 * @param timeMillis Timeout in milliseconds. A value of 0 means no timeout
	 *        is required. If the Coordination is not terminated within the
	 *        timeout, the Coordinator service will
	 *        {@link Coordination#fail(Throwable) fail} the Coordination with a
	 *        {@link Coordination#TIMEOUT TIMEOUT} exception.
	 * @return The new Coordination object.
	 * @throws IllegalArgumentException If the specified name does not follow
	 *         the {@code symbolic-name} syntax or the specified time is
	 *         negative.
	 * @throws SecurityException If the caller does not have
	 *         {@code CoordinationPermission[INITIATE]} for the specified name
	 *         and creating bundle.
	 */
	Coordination create(String name, long timeMillis);

	/**
	 * Create a new Coordination and make it the {@link #peek() current
	 * Coordination}.
	 * 
	 * <p>
	 * This method does that same thing as calling {@link #create(String, long)
	 * create(name, timeMillis)}.{@link Coordination#push() push()}
	 * 
	 * @param name The name of this coordination. The name does not have to be
	 *        unique but must follow the {@code symbolic-name} syntax from the
	 *        Core specification.
	 * @param timeMillis Timeout in milliseconds. A value of 0 means no timeout
	 *        is required. If the Coordination is not terminated within the
	 *        timeout, the Coordinator service will
	 *        {@link Coordination#fail(Throwable) fail} the Coordination with a
	 *        {@link Coordination#TIMEOUT TIMEOUT} exception.
	 * @return A new Coordination object
	 * @throws IllegalArgumentException If the specified name does not follow
	 *         the {@code symbolic-name} syntax or the specified time is
	 *         negative.
	 * @throws SecurityException If the caller does not have
	 *         {@code CoordinationPermission[INITIATE]} for the specified name
	 *         and creating bundle.
	 */
	Coordination begin(String name, long timeMillis);

	/**
	 * Returns the current Coordination.
	 * 
	 * <p>
	 * The current Coordination is the Coordination at the top of the thread
	 * local Coordination stack. If the thread local Coordination stack is
	 * empty, there is no current Coordination. Each Coordinator service
	 * maintains thread local Coordination stacks.
	 * 
	 * <p>
	 * This method does not alter the thread local Coordination stack.
	 * 
	 * @return The current Coordination or {@code null} if the thread local
	 *         Coordination stack is empty.
	 */
	Coordination peek();

	/**
	 * Remove the current Coordination from the thread local Coordination stack.
	 * 
	 * <p>
	 * The current Coordination is the Coordination at the top of the thread
	 * local Coordination stack. If the thread local Coordination stack is
	 * empty, there is no current Coordination. Each Coordinator service
	 * maintains its own thread local Coordination stacks.
	 * 
	 * <p>
	 * This method alters the thread local Coordination stack, if it is not
	 * empty, by removing the Coordination at the top of the thread local
	 * Coordination stack.
	 * 
	 * @return The Coordination that was the current Coordination or
	 *         {@code null} if the thread local Coordination stack is empty.
	 * @throws SecurityException If the caller does not have
	 *         {@code CoordinationPermission[INITIATE]} for the current
	 *         Coordination.
	 */
	Coordination pop();

	/**
	 * Terminate the {@link #peek() current Coordination} as a failure with the
	 * specified failure cause.
	 * 
	 * <p>
	 * If there is no current Coordination, this method does nothing and returns
	 * {@code false}.
	 * 
	 * <p>
	 * Otherwise, this method returns the result from calling
	 * {@link Coordination#fail(Throwable)} with the specified failure cause on
	 * the current Coordination.
	 * 
	 * @param cause The failure cause. The failure cause must not be
	 *        {@code null} .
	 * @return {@code false} if there was no current Coordination, otherwise
	 *         returns the result from calling
	 *         {@link Coordination#fail(Throwable)} on the current Coordination.
	 * @throws SecurityException If the caller does not have
	 *         {@code CoordinationPermission[PARTICIPATE]} for the current
	 *         Coordination.
	 * @see Coordination#fail(Throwable)
	 */
	boolean fail(Throwable cause);

	/**
	 * Register a Participant with the {@link #peek() current Coordination}.
	 * 
	 * <p>
	 * If there is no current Coordination, this method does nothing and returns
	 * {@code false}.
	 * 
	 * <p>
	 * Otherwise, this method calls
	 * {@link Coordination#addParticipant(Participant)} with the specified
	 * Participant on the current Coordination and returns {@code true}.
	 * 
	 * @param participant The Participant to register with the current
	 *        Coordination. The participant must not be {@code null}.
	 * @return {@code false} if there was no current Coordination, otherwise
	 *         returns {@code true}.
	 * @throws CoordinationException If the Participant could not be registered
	 *         with the current Coordination. This exception should normally not
	 *         be caught by the caller but allowed to be caught by the initiator
	 *         of this Coordination.
	 * @throws SecurityException If the caller does not have
	 *         {@code CoordinationPermission[PARTICIPATE]} for the current
	 *         Coordination.
	 * @see Coordination#addParticipant(Participant)
	 */
	boolean addParticipant(Participant participant);

	/**
	 * Returns a snapshot of all active Coordinations.
	 * 
	 * <p>
	 * Since Coordinations can be terminated at any time, Coordinations in the
	 * returned collection can be terminated before the caller examines the
	 * returned collection.
	 * 
	 * <p>
	 * The returned collection must only contain the Coordinations for which the
	 * caller has {@code CoordinationPermission[ADMIN]}.
	 * 
	 * @return A snapshot of all active Coordinations. If there are no active
	 *         Coordinations, the returned list will be empty. The returned
	 *         collection is the property of the caller and can be modified by
	 *         the caller.
	 */
	Collection<Coordination> getCoordinations();

	/**
	 * Returns the Coordination with the specified id.
	 * 
	 * @param id The id of the requested Coordination.
	 * @return A Coordination having with specified id or {@code null} if no
	 *         Coordination with the specified id exists, the Coordination with
	 *         the specified id is {@link Coordination#isTerminated()
	 *         terminated} or the caller does not have
	 *         {@code CoordinationPermission[ADMIN]} for the Coordination with
	 *         the specified id.
	 */
	Coordination getCoordination(long id);
}
