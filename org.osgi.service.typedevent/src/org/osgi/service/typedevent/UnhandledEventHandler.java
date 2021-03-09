/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.typedevent;

import java.util.Map;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Listener for Unhandled Events.
 * <p>
 * {@code UnhandledEventHandler} objects are registered with the Framework
 * service registry and are notified with an event object when an event is sent,
 * but no other handler is found to receive the event
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ConsumerType
public interface UnhandledEventHandler {
	/**
	 * Called by the {@link TypedEventBus} service to notify the listener of
	 * an unhandled event.
	 * 
	 * @param topic The topic to which the event was sent
	 * @param event The event that occurred.
	 */
	void notifyUnhandled(String topic, Map<String,Object> event);
}
