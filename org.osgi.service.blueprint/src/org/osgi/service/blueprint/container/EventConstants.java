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

import org.osgi.framework.*;

/**
 * Event property names used in Event Admin events published by a Blueprint
 * container.
 * 
 * Each type of event is sent to a different topic:<br/>
 * 
 * <pre>
 *   org/osgi/service/blueprint/container/&lt;event-type&gt;
 * </pre>
 * 
 * where <code>&lt;event-type&gt;</code> can have the values
 * <code>CREATING</code>, <code>CREATED</code>, <code>DESTROYING</code>,
 * <code>DESTROYED</code>, <code>FAILURE</code>, <code>GRACE_PERIOD</code>
 * or <code>WAITING</code>. <br/> Such events have the following properties:
 * <ul>
 * <li><code>type<code>
 *   <li><code>event<code>
 *   <li><code>timestamp<code>
 *   <li><code>bundle<code>
 *   <li><code>bundle.symbolicName<code>
 *   <li><code>bundle.id<code>
 *   <li><code>bundle.version<code>
 *   <li><code>extender.bundle<code>
 *   <li><code>extender.bundle.symbolicName<code>
 *   <li><code>extender.bundle.id<code>
 *   <li><code>extender.bundle.version<code>
 *   <li><code>dependencies<code>
 *   <li><code>cause<code>
 * </ul>
 */
public interface EventConstants {

	/**
	 * The type of the event that has been issued. This property is of type
	 * <code>Integer</code> and can take one of the values defined in
	 * {@link BlueprintEvent}.
	 */
	public static final String TYPE = "type";

	/**
	 * The <code>BlueprintEvent</code> object that caused this event. This
	 * property is of type {@link BlueprintEvent}.
	 */
	public static final String EVENT = "event";

	/**
	 * The type of the event that has been issued. This property is of type
	 * <code>Long</code>.
	 */
	public static final String TIMESTAMP = "timestamp";

	/**
	 * The bundle property defining the blueprint bundle for which an event has
	 * been issued. This property is of type {@link org.osgi.framework.Bundle}.
	 * 
	 * @see Bundle
	 */
	public static final String BUNDLE = "bundle";

	/**
	 * The bundle id property defining the id of the blueprint bundle for which
	 * an event has been issued. This property is of type <code>Long</code>.
	 */
	public static final String BUNDLE_ID = "bundle.id";

	/**
	 * The bundle symbolic name property defining the symbolic name of the
	 * blueprint bundle for which an event has been issued. This property is of
	 * type <code>String</code>.
	 */
	public static final String BUNDLE_SYMBOLICNAME = "bundle.symbolicName";

	/**
	 * The bundle id property defining the id of the blueprint bundle for which
	 * an event has been issued. This property is of type <code>Version</code>.
	 */
	public static final String BUNDLE_VERSION = "bundle.version";

	/**
	 * The extender bundle property defining the extender bundle processing the
	 * Blueprint Container for which an event has been issued. This property is
	 * of type {@link org.osgi.framework.Bundle}.
	 * 
	 * @see Bundle
	 */
	public static final String EXTENDER_BUNDLE = "extender.bundle";

	/**
	 * The Blueprint extender bundle id property defining the id of the extender bundle
	 * processing the Blueprint Container for which an event has been issued.
	 * This property is of type <code>Long</code>.
	 */
	public static final String EXTENDER_BUNDLE_ID = "extender.bundle.id";

	/**
	 * The extender bundle symbolic name property defining the symbolic name of
	 * the extender bundle processing the Blueprint Container for which an event
	 * has been issued. This property is of type <code>String</code>.
	 */
	public static final String EXTENDER_BUNDLE_SYMBOLICNAME = "extender.bundle.symbolicName";

	/**
	 * The Blueprint extender bundle version property defining the version of the Blueprint extender
	 * bundle processing the Blueprint Container for which an event has been
	 * issued. This property is of type <code>Version</code>.
	 */
	public static final String EXTENDER_BUNDLE_VERSION = "extender.bundle.version";

	/**
	 * The dependencies property containing an array of filters describing the
	 * missing mandatory dependencies for a FAILED, GRACE_PERIOD or WAITING
	 * event. This property is an array of <code>String</code>.
	 */
	public static final String DEPENDENCIES = "dependencies";

	/**
	 * The exception property containing the cause for a FAILED event. This
	 * property is of type <code>Throwable</code>.
	 */
	public static final String EXCEPTION = "exception";

	/**
	 * Topic prefix for all events issued by the Blueprint Container
	 */
	public static final String TOPIC_BLUEPRINT_EVENTS = "org/osgi/service/blueprint";

	/**
	 * Topic for Blueprint Container CREATING events
	 */
	public static final String TOPIC_CREATING = TOPIC_BLUEPRINT_EVENTS
			+ "/container/CREATING";

	/**
	 * Topic for Blueprint Container CREATED events
	 */
	public static final String TOPIC_CREATED = TOPIC_BLUEPRINT_EVENTS
			+ "/container/CREATED";

	/**
	 * Topic for Blueprint Container DESTROYING events
	 */
	public static final String TOPIC_DESTROYING = TOPIC_BLUEPRINT_EVENTS
			+ "/container/DESTROYING";

	/**
	 * Topic for Blueprint Container DESTROYED events
	 */
	public static final String TOPIC_DESTROYED = TOPIC_BLUEPRINT_EVENTS
			+ "/container/DESTROYED";

	/**
	 * Topic for Blueprint Container FAILURE events
	 */
	public static final String TOPIC_FAILURE = TOPIC_BLUEPRINT_EVENTS
			+ "/container/FAILURE";

	/**
	 * Topic for Blueprint Container GRACE_PERIOD events
	 */
	public static final String TOPIC_GRACE_PERIOD = TOPIC_BLUEPRINT_EVENTS
			+ "/container/GRACE_PERIOD";

	/**
	 * Topic for Blueprint Container WAITING events
	 */
	public static final String TOPIC_WAITING = TOPIC_BLUEPRINT_EVENTS
			+ "/container/WAITING";

}
