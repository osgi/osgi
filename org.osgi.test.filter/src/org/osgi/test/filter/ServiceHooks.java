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

package org.osgi.test.filter;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.hooks.service.EventListenerHook;
import org.osgi.framework.hooks.service.FindHook;
import org.osgi.framework.hooks.service.ListenerHook.ListenerInfo;

/**
 * Service Hooks to exclude services from use by the CT.
 */
class ServiceHooks implements FindHook, EventListenerHook {
	private final Filter	serviceExcludeFilter;
	private final boolean	debug;

	/**
	 * Constructor.
	 * 
	 * @param serviceExcludeFilter All service matching the specified filter are
	 *        to be filtered out.
	 * @param debug Display debug messages if true.
	 */
	ServiceHooks(Filter serviceExcludeFilter, boolean debug) {
		this.serviceExcludeFilter = serviceExcludeFilter;
		this.debug = debug;
	}

	/**
	 * Event listener hook method. This method is called prior to service event
	 * delivery when a publishing bundle registers, modifies or unregisters a
	 * service. This method can filter the listeners which receive the event.
	 * 
	 * @param event The service event to be delivered.
	 * @param listeners A map of Bundle Contexts to a collection of Listener
	 *        Infos for the bundle's listeners to which the specified event will
	 *        be delivered. The implementation of this method may remove bundle
	 *        contexts from the map and listener infos from the collection
	 *        values to prevent the event from being delivered to the associated
	 *        listeners. The map supports all the optional {@code Map}
	 *        operations except {@code put} and {@code putAll}. Attempting to
	 *        add to the map will result in an
	 *        {@code UnsupportedOperationException}. The collection values in
	 *        the map supports all the optional {@code Collection} operations
	 *        except {@code add} and {@code addAll}. Attempting to add to a
	 *        collection will result in an {@code UnsupportedOperationException}
	 *        . The map and the collections are not synchronized.
	 */
	@Override
	public void event(ServiceEvent event,
			Map<BundleContext,Collection<ListenerInfo>> listeners) {
		if (debug) {
			System.out.println(">> EventListenerHook[" + serviceExcludeFilter + "].event(" + event.getServiceReference() + ")");
		}
		if (serviceExcludeFilter.match(event.getServiceReference())) {
			if (debug) {
				System.out.println("== EventListenerHook[" + serviceExcludeFilter + "].event() filtered out " + event.getServiceReference());
			}
			listeners.clear();
		}
	}

	/**
	 * Find hook method. This method is called during the service find operation
	 * (for example, {@link BundleContext#getServiceReferences(String, String)}
	 * ). This method can filter the result of the find operation.
	 * 
	 * @param context The bundle context of the bundle performing the find
	 *        operation.
	 * @param name The class name of the services to find or {@code null} to
	 *        find all services.
	 * @param filter The filter criteria of the services to find or {@code null}
	 *        for no filter criteria.
	 * @param allServices {@code true} if the find operation is the result of a
	 *        call to
	 *        {@link BundleContext#getAllServiceReferences(String, String)}
	 * @param references A collection of Service References to be returned as a
	 *        result of the find operation. The implementation of this method
	 *        may remove service references from the collection to prevent the
	 *        references from being returned to the bundle performing the find
	 *        operation. The collection supports all the optional
	 *        {@code Collection} operations except {@code add} and
	 *        {@code addAll}. Attempting to add to the collection will result in
	 *        an {@code UnsupportedOperationException}. The collection is not
	 *        synchronized.
	 */
	@Override
	public void find(BundleContext context, String name, String filter,
			boolean allServices, Collection<ServiceReference< ? >> references) {
		if (debug) {
			System.out.println(">> FindHook[" + serviceExcludeFilter + "].find(" + references + ")");
		}
		for (Iterator<ServiceReference< ? >> iter = references.iterator(); iter
				.hasNext();) {
			ServiceReference< ? > ref = iter.next();
			if (serviceExcludeFilter.match(ref)) {
				if (debug) {
					System.out.println("== FindHook[" + serviceExcludeFilter + "].find() filtered out " + ref);
				}
				iter.remove();
			}
		}
	}
}
