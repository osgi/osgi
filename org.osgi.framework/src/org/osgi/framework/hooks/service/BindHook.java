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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * OSGi Framework Service Bind Hook Service.
 * 
 * Bundles registering this service will be called during framework service bind
 * (get service object) operations. Service hooks are not called for service
 * operations on other service hooks.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface BindHook {
	/**
	 * Bind hook method. This method is called during the service bind ({@link BundleContext#getService(ServiceReference)})
	 * operation by the binding bundle.
	 * 
	 * The implementation of this method may perform any pre operation actions
	 * such as modifying the operation parameters before calling the next hook
	 * in the chain. The implementation of this method can also perform any post
	 * operation actions such as modifying the operation result before
	 * returning.
	 * 
	 * The values supplied to the next hook in the chain and the values returned
	 * by this method must pass all the tests required of parameters supplied to
	 * and values returned by the ({@link BundleContext#getService(ServiceReference)})
	 * method. If the values are invalid or this method throws an exception,
	 * then the service bind operation will fail and this hook will be not be
	 * called for future service bind operations.
	 * 
	 * @param next The next bind hook in the chain. The implementation of this
	 *        method MUST call {@link BindHookChain#bind(ServiceReference)} to
	 *        complete the bind operation.
	 * @param context The bundle context of the binding bundle.
	 * @param reference A reference to the service to which to bind.
	 * @return The service object to return to the binding bundle. The
	 *         implementor can return the service value returned by the next
	 *         hook in the chain or supply an alternative service object.
	 */
	Object bind(BindHookChain next, BundleContext context,
			ServiceReference reference);
}
