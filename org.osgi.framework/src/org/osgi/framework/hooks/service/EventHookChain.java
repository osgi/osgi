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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;

/**
 * Next hook in the OSGi Framework Service Event Hook chain.
 * 
 * This interface is implemented by the OSGi Framework and passed to each hook
 * when called so that the hook implementation can call the next hook in the
 * chain to complete the operation.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface EventHookChain {
	/**
	 * Call the next hook in the chain.
	 * 
	 * Implementations of
	 * {@link EventHook#event(EventHookChain, BundleContext, ServiceEvent, Bundle[])}
	 * MUST call this method to continue processing of the event delivery.
	 * 
	 * @param event The Service Event to be delivered.
	 * @param bundles The bundles to which the event will be delivered.
	 */
	void event(ServiceEvent event, Bundle[] bundles);
}
