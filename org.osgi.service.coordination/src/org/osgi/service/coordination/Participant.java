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

/**
 * A Participant participates in a Coordination.
 * 
 * A Participant can participate in a Coordination by calling
 * {@link Coordinator#participate(Participant)} or
 * {@link Coordinator#participateOrBegin(Participant)}. After successfully
 * initiating the participation, the Participant is called back when the
 * Coordination is terminated.
 * 
 * If a Coordination ends with the {@link Coordination#end()} method, then all
 * the participants are called back on their {@link #ended()} method. If the
 * initiator decides to fail the Coordination (or another party has called
 * {@link Coordinator#alwaysFail(String)}) then the {@link #failed()} method is
 * called back.
 * 
 * Participants are not required to be thread safe for the {@link #ended()}
 * method though the {@link #failed()} can originate from another thread.
 * 
 * A Coordinator service must block a Participant when it tries to participate
 * in multiple Coordinations. Only one Coordination can be active on a thread so
 * a Participant can only be active on a single thread between the return of the
 * {@link Coordinator#participate(Participant)} call and the {@link #ended()} or
 * {@link #failed()} call. 
 * 
 */
public interface Participant {
	/**
	 * The Coordination has failed and the participant is informed.
	 * 
	 * A participant should properly discard any work it has done during the
	 * active coordination.
	 * 
	 * This method is in all most all cases called on the thread associated with
	 * the coordination. However, it is possible that this method is called on
	 * another thread when the coordination times out or is killed.
	 * 
	 * @throws Exception
	 *             Any exception thrown should be logged but is further ignored
	 *             and does not influence the outcome of the Coordination.
	 */
	void failed() throws Exception;

	/**
	 * The Coordination is being ended. This method must always be called on the
	 * coordinator thread.
	 * 
	 * throws Exception If an exception is thrown it should be logged and the
	 * return of the {@link Coordination#end()} method must be
	 * {@link Coordinator#PARTIALLY_ENDED}.
	 */
	void ended() throws Exception;
}
