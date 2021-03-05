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
package org.osgi.service.typedevent.monitor;

import java.time.Instant;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * A monitoring event.
 */
@ProviderType
public class MonitorEvent {

	/**
	 * The Event Topic
	 */
	public String				topic;
	
	/**
	 * The Data from the Event in Map form
	 */
	public Map<String, Object> eventData;

	/**
	 * The time at which the event was published
	 */
	public Instant publicationTime;
}
