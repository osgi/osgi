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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * OSGi Framework Service Publish Hook Service.
 * 
 * Bundles registering this service will be called during framework service
 * publish (register service) operations. Service hooks are not called for
 * service operations on other service hooks.
 * 
 * @ThreadSafe
 * @version $Revision$
 */

public interface PublishHook {
	/**
	 * Publish hook method. This method is called during the service publish ({@link BundleContext#registerService(String[], Object, Dictionary)})
	 * operation by the publishing bundle.
	 * 
	 * The implementation of this method may perform any pre operation actions
	 * such as modifying the operation parameters before calling the next hook
	 * in the chain. The implementation of this method can also perform any post
	 * operation actions such as modifying the operation result before
	 * returning.
	 * 
	 * The values supplied to the next hook in the chain and the values returned
	 * by this method must pass all the tests required of parameters supplied to
	 * and values returned by the
	 * {@link BundleContext#registerService(String[], Object, Dictionary)}
	 * method. If the values are invalid or this method throws an exception,
	 * then the service publish operation will fail and this hook will be not be
	 * called for future service publish operations.
	 * 
	 * @param next The next publish hook in the chain. The implementation of
	 *        this method MUST call
	 *        {@link PublishHookChain#publish(String[], Object, Dictionary)} to
	 *        complete the publish operation.
	 * @param context The bundle context of the publishing bundle.
	 * @param names The class names under which the service is to be published.
	 * @param service The service object or a <code>ServiceFactory</code>
	 *        object to be published.
	 * @param properties The properties of the service to be published or
	 *        <code>null</code> if the service has no properties.
	 * @return The ServiceRegistration to be returned to the publishing bundle.
	 *         The implementor can return the registration value returned by the
	 *         next hook in the chain or supply an alternative registration
	 *         object. An alternative registration object must wrap the
	 *         registration value returned by the next hook in the chain to
	 *         support modify and unregistration operations on the service.
	 */
	ServiceRegistration publish(PublishHookChain next, BundleContext context,
			String[] names, Object service, Dictionary properties);
}
