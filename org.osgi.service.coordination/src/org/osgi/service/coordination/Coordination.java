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

import java.util.Collection;

/**
 * A Coordination represents an ongoing coordination associated with a Thread.
 * It is created by a {@link Coordinator} service. A Coordination is
 * <em>not</em> a Thread safe object, it is only valid on the thread it was
 * created on, it is an error to call a method on this object outside the
 * initiating thread.
 * 
 * The Coordination object is a capability. This means that most methods are not
 * checked. Coordination initiators should normally not share the Coordination
 * object.
 * 
 * @ThreadSafe
 */

public interface Coordination {
	/**
	 * Return the name of this Coordination.
	 * 
	 * The name is given in the {@link Coordinator#begin(String)} method.
	 * 
	 * @return the name of this modification
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
	 * failed methods is called. That is, the {@link Participant#failed()}
	 * methods must be running outside the current coordination, no participants
	 * can be added during the termination phase.
	 * 
	 * A fail method must return silently when the coordination is already
	 * finished. This enables the following pattern.
	 * 
	 * This method can be called on another thread than the coordination thread
	 * in case that something went wrong and the participants need to be
	 * recovered.
	 */
	boolean fail(String reason);

	/**
	 * Terminate this coordination of not already terminated. If this
	 * Coordination is already terminated then ignore this method. If not, the
	 * Coordination is set to fail. This method enables the following fail-safe
	 * pattern to ensure Coordinations are properly terminated.
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
	 * The terminate method must be called on the initiating thread, from
	 * another thread {@link #fail(String)} must be called.
	 * 
	 * @return <code>true</code> if this method actually terminated the
	 *         coordination (that is, it was not properly ended).
	 *         <code>false</code> if the Coordination was already properly
	 *         terminate by an {@link #end()} or {@link #fail(String)} method.
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
	 * <li>{@link Coordinator#OK} - Correct outcome, no exceptions thrown</li>
	 * <li>{@link Coordinator#PARTIALLY_ENDED} - One of the participants threw
	 * an exception</li>
	 * <li>{@link Coordinator#FAILED} - The Coordination was set to always fail</li>
	 * </ol>
	 * 
	 * The end() method must be called on the initiating thread, it is not
	 * possible to end a Coordination from another thread, it is only possible
	 * to fail it.
	 * 
	 * @return {@link Coordinator#OK}, {@link Coordinator#PARTIALLY_ENDED},
	 *         {@link Coordinator#FAILED}
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
	 * (Re)set the timeout for this Coordination.
	 * 
	 * If this timeout expires, then the Coordination will fail and the
	 * initiating thread will be interrupted. This method must only be called on
	 * an active Coordination, that is, before {@link #end()} or
	 * {@link #fail(String)} is called.
	 * 
	 * @param timeOutInMs Number of ms to wait, zero means forever.
	 * @throws SecurityException This method requires the
	 *         {@link CoordinationPermission.ADMIN} or
	 *         {@link CoordinationPermission.INITIATE} action for the
	 *         {@link CoordinationPermission}.
	 */
	void setTimeout(long timeOutInMs);

	/**
	 * Return <code>true</code> if this Coordination has failed,
	 * <code>false</code> otherwise.
	 */
	boolean isFailed();

}
