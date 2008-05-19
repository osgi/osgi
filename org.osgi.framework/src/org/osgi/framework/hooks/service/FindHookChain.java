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
 * Next hook in the OSGi Framework Service Find Hook chain.
 * 
 * <p>
 * This interface is implemented by the OSGi Framework and passed to each hook
 * when called so that the hook implementation can call the next hook in the
 * chain to complete the operation.
 * 
 * @NotThreadSafe
 * @version $Revision$
 */

public interface FindHookChain {
	/**
	 * Call the next hook in the chain.
	 * 
	 * <p>
	 * Implementations of {@link FindHook#find(FindHookChain, BundleContext,
	 * String, String, boolean)} MUST call this method to continue processing of
	 * the find operation.
	 * 
	 * @param name The class name of the services to find or <code>null</code>
	 * 	to find all services.
	 * @param filter The filter criteria of the services to find or
	 * 	<code>null</code> for no filter criteria.
	 * @param allServices <code>true</code> if the find operation is the result
	 * 	of a call to {@link BundleContext#getAllServiceReferences(String,
	 * 	String)}
	 * @return A <code>Set</code> of Service References to be returned to the
	 * 	finding bundle.
	 */
	Set find(String name, String filter, boolean allServices);
}
