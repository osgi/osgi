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
package org.osgi.service.blueprint.context;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * Event property names used in EventAdmin events published for a
 * module context.
 *
 */
public interface EventConstants {
	/**
	 * The extender bundle property defining the extender bundle processing the
	 * module context for which an event has been issued.
	 *
	 * @see Bundle
	 */
	public static final String EXTENDER_BUNDLE = "extender.bundle";

	/**
	 * The extender bundle id property defining the id of the extender bundle
	 * processing the module context for which an event has been issued.
	 */
	public static final String EXTENDER_ID = "extender.bundle.id";

	/**
	 * The extender bundle symbolic name property defining the symbolic name of
	 * the extender bundle processing the module context for which an event
	 * has been issued.
	 */
	public static final String EXTENDER_SYMBOLICNAME = "extender.bundle.symbolicName";

	/**
	 * Topic prefix for all events issued by the Blueprint Service
	 */
	public static final String TOPIC_BLUEPRINT_EVENTS = "org/osgi/service/blueprint";

	/**
	 * Topic for Blueprint Service CREATING events
	 */
	public static final String TOPIC_CREATING = TOPIC_BLUEPRINT_EVENTS + "/context/CREATING";

	/**
 	 * Topic for Blueprint Service CREATED events
 	 */
	public static final String TOPIC_CREATED = TOPIC_BLUEPRINT_EVENTS + "/context/CREATED";

	/**
 	 * Topic for Blueprint Service DESTROYING events
 	 */
	public static final String TOPIC_DESTROYING = TOPIC_BLUEPRINT_EVENTS + "/context/DESTROYING";

	/**
	 * Topic for Blueprint Service DESTROYED events
	 */
	public static final String TOPIC_DESTROYED = TOPIC_BLUEPRINT_EVENTS + "/context/DESTROYED";

	/**
	 * Topic for Blueprint Service WAITING events
	 */
	public static final String TOPIC_WAITING = TOPIC_BLUEPRINT_EVENTS + "/context/WAITING";

	/**
	 * Topic for Blueprint Service FAILURE events
	 */
	public static final String TOPIC_FAILURE = TOPIC_BLUEPRINT_EVENTS + "/context/FAILURE";

}
