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
 * OSGi Framework Service Event Hook Service.
 * 
 * Bundles registering this service will be called during framework service
 * event delivery. Service hooks are not called for service operations on other
 * service hooks.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface EventHook {
	/**
	 * Event hook method. This method is called during service event delivery.
	 * 
	 * The implementation of this method may perform any pre operation actions
	 * such as modifying the operation parameters before calling the next hook
	 * in the chain. The implementation of this method can also perform any post
	 * operation actions.
	 * 
	 * The values supplied to the next hook in the chain must be legitimate
	 * objects that can be properly used by the receiver. If the values are
	 * invalid or this method throws an exception, then the service event
	 * delivery will fail and this hook will be not be called for future service
	 * event deliveries.
	 * 
	 * @param next The next event hook in the chain. The implementation of this
	 *        method MUST call
	 *        {@link EventHookChain#event(ServiceEvent, Bundle[])} to complete
	 *        the event delivery.
	 * @param context The bundle context of the bundle which registered the
	 *        service.
	 * @param event The service event to be delivered.
	 * @param bundles The bundles to which the event will be delivered.
	 */
	void event(EventHookChain next, BundleContext context, ServiceEvent event,
			Bundle[] bundles);
}
