/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

package org.osgi.framework.hooks.service;

import java.util.Dictionary;

import org.osgi.framework.ServiceRegistration;

/**
 * Next hook in the OSGi Framework Service Publish Hook chain.
 * 
 * This interface is implemented by the OSGi Framework and passed to each hook
 * when called so that the hook implementation can call the next hook in the
 * chain to complete the operation.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface PublishHookChain {
	/**
	 * Call the next hook in the chain.
	 * 
	 * Implementations of
	 * {@link PublishHook#publish(PublishHookChain, org.osgi.framework.BundleContext, String[], Object, Dictionary)}
	 * MUST call this method to continue processing of the publish operation.
	 * 
	 * @param names The class names under which the service is to be published.
	 * @param service The service object or a <code>ServiceFactory</code>
	 *        object to be published.
	 * @param properties The properties of the service to be published or
	 *        <code>null</code> if the service has no properties.
	 * @return The ServiceRegistration to be returned to the publishing bundle.
	 */
	ServiceRegistration publish(String[] names, Object service,
			Dictionary properties);
}
