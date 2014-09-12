/*
 * Copyright (c) OSGi Alliance (2010, 2013). All Rights Reserved.
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

package org.osgi.service.resourcemonitoring;

/**
 * <p>
 * A <code>ResourceContextListener</code> is notified whenever:
 * <ul>
 * <li>a {@link ResourceContext} is created or deleted.</li>
 * <li>a bundle is added or removed from a {@link ResourceContext}.
 * </ul>
 * </p>
 * <p>
 * A <code>ResourceContextListener</code> is registered as an OSGi service. At
 * registration time, the following properties may be provided:
 * <ul>
 * <li>the {@link #RESOURCE_CONTEXT} property which limits the Resource Context
 * for which notifications will be received. This property can be either a
 * String value or an array of String. If this property is not set, the Resource
 * Context Listener receives events from all the Resource Context.</li>
 * <li>the {@link #EVENT_TYPE} property. If set, this property filters the type
 * of event this listener will receive. The value of this property can either a
 * String (then, the Listener receives notifications about a single type) or an
 * array of String (several types). The expected values for this property are
 * the types defined by a {@link ResourceContextEvent}:
 * <ul>
 * <li>{@link ResourceContextEvent#RESOURCE_CONTEXT_CREATED}</li>
 * <li>{@link ResourceContextEvent#RESOURCE_CONTEXT_REMOVED}</li>
 * <li>{@link ResourceContextEvent#BUNDLE_ADDED}</li>
 * <li>{@link ResourceContextEvent#BUNDLE_REMOVED}</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * 
 * @author $Id$
 */
public interface ResourceContextListener {

	/**
	 * <p>
	 * Property specifying the {@link ResourceContext}(s) for which a
	 * notification will be received by this listener.
	 * </p>
	 * <p>
	 * The property value is either a string (i.e the name of the
	 * {@link ResourceContext}) and an array of string (several
	 * {@link ResourceContext}).
	 * </p>
	 */
	public static final String	RESOURCE_CONTEXT	= "resource.context";

	/**
	 * Property specifying the type of events this listener will receive. The
	 * expected values are the {@link ResourceContextEvent} types. It can be a
	 * String or an array of String.
	 */
	public static final String	EVENT_TYPE			= "event.type";

	/**
	 * Notify this listener about a {@link ResourceContext} events.
	 * 
	 * @param event event.
	 */
	public void notify(ResourceContextEvent event);

}
