/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.service.blueprint.container;

/**
 * Listener for <code>BlueprintEvent</code>s. Implementers should register
 * this a Blueprint Event Listener service. The Blueprint extender must inform
 * the Blueprint Event Listener synchronously with the registration the last
 * non-DESTROYED event of all managed bundles with the replay flag set before
 * any of the other events are delivered. The delivery must maintain the time
 * ordering.
 * 
 * @see BlueprintEvent
 * 
 * @ThreadSafe
 */
public interface BlueprintListener {

	/**
	 * Receives synchronous notifications of a Blueprint Event.
	 * 
	 * Implementers should quickly process the events and return.
	 * 
	 * @param event
	 *            The <code>BlueprintEvent</code>.
	 */
	void blueprintEvent(BlueprintEvent event);

}
