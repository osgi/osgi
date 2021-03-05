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

package org.osgi.service.resourcemonitoring;

/**
 * <p>
 * A <code>ResourceContextListener</code> is notified whenever:
 * <ul>
 * <li>a {@link ResourceContext} is created or deleted.</li>
 * <li>a bundle is added or removed from a {@link ResourceContext}.</li>
 * </ul>
 * 
 * <p>
 * A <code>ResourceContextListener</code> is registered as an OSGi service. At
 * registration time, the following property may be provided:
 * <ul>
 * <li>the {@link #RESOURCE_CONTEXT} property which limits the Resource Context
 * for which notifications will be received. This property can be either a
 * String value or an array of String. If this property is not set, the Resource
 * Context Listener receives events from all the Resource Context.</li>
 * </ul>
 * 
 * @version 1.0
 * @author $Id$
 */
public interface ResourceContextListener {

	/**
	 * <p>
	 * Property specifying the {@link ResourceContext}(s) for which a
	 * notification will be received by this listener.
	 * 
	 * <p>
	 * The property value is either a string (i.e the name of the
	 * {@link ResourceContext}) and an array of string (several
	 * {@link ResourceContext}).
	 */
	public static final String	RESOURCE_CONTEXT	= "resource.context";

	/**
	 * Notify this listener about a {@link ResourceContext} events.
	 * 
	 * @param event event.
	 */
	public void notify(ResourceContextEvent event);

}
