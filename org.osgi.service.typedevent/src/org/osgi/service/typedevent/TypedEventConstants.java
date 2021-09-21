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

import org.osgi.annotation.versioning.ProviderType;

/**
 * Defines standard names for Typed Event properties.
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
	 * The name of the service property used to indicate a filter that should be
	 * applied to events from the {@link #TYPED_EVENT_TOPICS}. Only events which
	 * match the filter will be delivered to the Event Handler service.
	 * <p>
	 * If this service property is not present then all events from the topic(s)
	 * will be delivered to the Event Handler service.
	 */
	public static final String	TYPED_EVENT_FILTER					= "event.filter";

	/**
	 * The name of the implementation capability for the Typed Event
	 * specification
	 */
	public static final String	TYPED_EVENT_IMPLEMENTATION			= "osgi.typedevent";

	/**
	 * The version of the implementation capability for the Typed Event
	 * specification
	 */
	public static final String	TYPED_EVENT_SPECIFICATION_VERSION	= "1.0";

	/**
	 * The package version of the Typed Event specification
	 */
	public static final String	TYPED_EVENT_PACKAGE_VERSION			= TYPED_EVENT_SPECIFICATION_VERSION
			+ ".0";

	/**
	 * Private constructor to prevent instantiation
	 */
	private TypedEventConstants() {
	}
}
