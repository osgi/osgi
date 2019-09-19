/*
 * Copyright (c) OSGi Alliance (2019). All Rights Reserved.
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

package org.osgi.service.event.typed;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Defines standard names for Type Safe Event properties.
 * 
 * @author $Id$
 */
@ProviderType
public interface EventConstants {

	/**
	 * The name of the implementation capability for the Event Admin
	 * specification
	 */
	public static final String	TYPE_SAFE_EVENT_TYPE					= "event.type";

	/**
	 * The name of the implementation capability for the Event Admin
	 * specification
	 */
	public static final String	TYPE_SAFE_EVENT_TOPICS					= "event.topics";

	/**
	 * The name of the implementation capability for the Event Admin
	 * specification
	 */
	public static final String	TYPE_SAFE_EVENT_IMPLEMENTATION			= "osgi.event.typed";

	/**
	 * The version of the implementation capability for the Event Admin
	 * specification
	 */
	public static final String	TYPE_SAFE_EVENT_SPECIFICATION_VERSION	= "1.0.0";

}
