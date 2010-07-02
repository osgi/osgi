/*
 * Copyright (c) OSGi Alliance (2005, 2009). All Rights Reserved.
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

package org.osgi.application;

import java.util.EventListener;

import org.osgi.framework.*;

/**
 * An {@code ApplicationServiceEvent} listener. When a 
 * {@code ServiceEvent} is
 * fired, it is converted to an {@code ApplictionServiceEvent}
 * and it is synchronously delivered to an {@code ApplicationServiceListener}.
 * 
 * <p>
 * {@code ApplicationServiceListener} is a listener interface that may be
 * implemented by an application developer.
 * <p>
 * An {@code ApplicationServiceListener} object is registered with the Framework
 * using the {@code ApplicationContext.addServiceListener} method.
 * {@code ApplicationServiceListener} objects are called with an
 * {@code ApplicationServiceEvent} object when a service is registered, modified, or
 * is in the process of unregistering.
 * 
 * <p>
 * {@code ApplicationServiceEvent} object delivery to 
 * {@code ApplicationServiceListener}
 * objects is filtered by the filter specified when the listener was registered.
 * If the Java Runtime Environment supports permissions, then additional
 * filtering is done. {@code ApplicationServiceEvent} objects are only delivered to
 * the listener if the application which defines the listener object's class has the
 * appropriate {@code ServicePermission} to get the service using at
 * least one of the named classes the service was registered under, and the application
 * specified its dependence on the corresponding service in the application metadata.
 * 
 * <p>
 * {@code ApplicationServiceEvent} object delivery to {@code ApplicationServiceListener}
 * objects is further filtered according to package sources as defined in
 * {@link ServiceReference#isAssignableTo(Bundle, String)}.
 * 
 * @version $Id$
 * @see ApplicationServiceEvent
 * @see ServicePermission
 */
public interface ApplicationServiceListener extends EventListener {
	/**
	 * Receives notification that a service has had a lifecycle change.
	 * 
	 * @param event The {@code ApplicationServiceEvent} object.
	 */
	public void serviceChanged(ApplicationServiceEvent event);
}
