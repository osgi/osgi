/*
 * Copyright (c) OSGi Alliance (2020). All Rights Reserved.
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

package org.osgi.service.typedevent;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Defines standard names for Type Safe Event properties.
 * 
 * @author $Id$
 */
@ProviderType
public final class TypedEventConstants {

	/**
	 * The name of the service property used to indicate the type of the event
	 * objects received by a {@link TypedEventHandler} service.
	 * <p>
	 * If this service property is not present then the reified type parameter
	 * from the TypedEventHandler implementation class will be used.
	 */
	public static final String	TYPED_EVENT_TYPE					= "event.type";

	/**
	 * The name of the service property used to indicate the topic(s) to which
	 * an a {@link TypedEventHandler} or {@link UntypedEventHandler} service is
	 * listening.
	 * <p>
	 * If this service property is not present then the reified type parameter
	 * from the TypedEventHandler implementation class will be used to determine
	 * the topic.
	 */
	public static final String	TYPED_EVENT_TOPICS					= "event.topics";

	/**
	 * The name of the implementation capability for the Type Safe Events
	 * specification
	 */
	public static final String	TYPED_EVENT_IMPLEMENTATION			= "osgi.typedevent";

	/**
	 * The version of the implementation capability for the Type Safe Events
	 * specification
	 */
	public static final String	TYPED_EVENT_SPECIFICATION_VERSION	= "1.0.0";

	/**
	 * Private constructor to prevent instantiation
	 */
	private TypedEventConstants() {
	}
}
