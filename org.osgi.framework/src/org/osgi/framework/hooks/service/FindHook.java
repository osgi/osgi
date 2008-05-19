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

import java.util.Set;

import org.osgi.framework.BundleContext;

/**
 * OSGi Framework Service Find Hook Service.
 * 
 * <p>
 * Bundles registering this service will be called during framework service find
 * (get service references) operations. Service hooks are not called for service
 * operations on other service hooks.
 * 
 * @ThreadSafe
 * @version $Revision$
 */

public interface FindHook {
	/**
	 * Find hook method. This method is called during the service find ({@link
	 * BundleContext#getServiceReferences(String, String)}) operation by the
	 * finding bundle.
	 * 
	 * <p>
	 * The implementation of this method may perform any pre operation actions
	 * such as modifying the operation parameters before calling the next hook
	 * in the chain. The implementation of this method may also perform any post
	 * operation actions such as modifying the operation result before returning
	 * to its caller.
	 * 
	 * <p>
	 * The values supplied to the next hook in the chain and the values returned
	 * by this method must pass all the tests required of parameters supplied to
	 * and values returned by the {@link
	 * BundleContext#getServiceReferences(String, String)} method. If the values
	 * are invalid or this method throws an exception, then the service find
	 * operation will fail and this hook will be not be called for future
	 * service find operations.
	 * 
	 * <p>
	 * This hook will not be called for a given service find operation if the
	 * bundle registering this hook does not have
	 * <code>AdminPermission[finding bundle,SERVICE_HOOK]</code>, and the Java
	 * Runtime Environment supports permissions.
	 * 
	 * @param next The next find hook in the chain. The implementation of this
	 * 	method MUST call {@link FindHookChain#find(String, String, boolean)} to
	 * 	complete the find operation.
	 * @param context The bundle context of the finding bundle.
	 * @param name The class name of the services to find or <code>null</code>
	 * 	to find all services.
	 * @param filter The filter criteria of the services to find or
	 * 	<code>null</code> for no filter criteria.
	 * @param allServices <code>true</code> if the find operation is the result
	 * 	of a call to {@link BundleContext#getAllServiceReferences(String,
	 * 	String)}
	 * @return A <code>Set</code> of Service References to be returned to the
	 * 	finding bundle. The implementor can return the references value returned
	 * 	by the next hook in the chain or supply a modified or alternative set of
	 * 	references.
	 */
	Set find(FindHookChain next, BundleContext context, String name,
			String filter, boolean allServices);
}
