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

import org.osgi.framework.ServiceReference;

/**
 * Next hook in the OSGi Framework Service Bind Hook chain.
 * 
 * This interface is implemented by the OSGi Framework and passed to each hook
 * when called so that the hook implementation can call the next hook in the
 * chain to complete the operation.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface BindHookChain {
	/**
	 * Call the next hook in the chain.
	 * 
	 * Implementations of
	 * {@link BindHook#bind(BindHookChain, org.osgi.framework.BundleContext, ServiceReference)}
	 * MUST call this method to continue processing of the bind operation.
	 * 
	 * @param reference A reference to the service to which to bind.
	 * @return The service object to return to the binding bundle.
	 */
	Object bind(ServiceReference reference);
}
