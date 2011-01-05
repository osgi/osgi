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

/**
 * A Participant participates in a Coordination.
 * 
 * <p>
 * A Participant can participate in a Coordination by
 * {@link Coordination#addParticipant(Participant) registering} itself with the
 * Coordination. After successfully registering itself, the Participant is
 * notified when the Coordination is terminated.
 * 
 * <p>
 * If a Coordination terminates {@link Coordination#end() normally}, then all
 * registered Participants are notified on their {@link #ended(Coordination)}
 * method. If the Coordination terminates as a
 * {@link Coordination#fail(Throwable) failure}, then all registered
 * Participants are notified on their {@link #failed(Coordination)} method.
 * 
 * <p>
 * Participants are required to be thread safe as notification can be made on
 * any thread.
 * 
 * <p>
 * A Participant can only be registered with a single active Coordination at a
 * time. If a Participant is already registered with an active Coordination,
 * attempts to register the Participation with another active Coordination will
 * block until the Coordination the Participant is registered with terminates.
 * Notice that in edge cases the notification to the Participant that the
 * Coordination has terminated can happen before the registration method
 * returns.
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface Participant {
	/**
	 * Notification that a Coordination has terminated
	 * {@link Coordination#end() normally}.
	 * 
	 * <p>
	 * This Participant should finalize any work associated with the specified
	 * Coordination.
	 * 
	 * @param coordination The Coordination that has terminated normally.
	 * @throws Exception If this Participant throws an exception, the
	 *         {@link Coordinator} service should log the exception. The
	 *         {@link Coordination#end()} method which is notifying this
	 *         Participant must continue notification of other registered
	 *         Participants. When this is completed, the
	 *         {@link Coordination#end()} method must throw a
	 *         CoordinationException of type
	 *         {@link CoordinationException#PARTIALLY_ENDED}.
	 */
	void ended(Coordination coordination) throws Exception;

	/**
	 * Notification that a Coordination has terminated as a
	 * {@link Coordination#fail(Throwable) failure}.
	 * 
	 * <p>
	 * This Participant should discard any work associated with the specified
	 * Coordination.
	 * 
	 * @param coordination The Coordination that has terminated as a failure.
	 * @throws Exception If this Participant throws an exception, the
	 *         {@link Coordinator} service should log the exception. The
	 *         {@link Coordination#fail(Throwable)} method which is notifying
	 *         this Participant must continue notification of other registered
	 *         Participants.
	 */
	void failed(Coordination coordination) throws Exception;
}
