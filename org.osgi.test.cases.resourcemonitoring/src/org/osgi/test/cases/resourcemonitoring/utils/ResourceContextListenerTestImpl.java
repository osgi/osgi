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

package org.osgi.test.cases.resourcemonitoring.utils;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.resourcemonitoring.ResourceContextEvent;
import org.osgi.service.resourcemonitoring.ResourceContextListener;

/**
 * A resource context listener implementation for test purpose.
 * 
 * @author $Id$
 */
public class ResourceContextListenerTestImpl implements ResourceContextListener {

	/**
	 * service registration ServiceRegistration<ResourceContextListener>
	 */
	private ServiceRegistration<ResourceContextListener>	serviceRegistration;

	/**
	 * list of received events
	 */
	private final List<ResourceContextEvent>	events;

	/**
	 * 
	 */
	public ResourceContextListenerTestImpl() {
		events = new ArrayList<>();
	}

	/**
	 * Register this instance as a {@link ResourceContextListener} service. Once registered, this instance will receive
	 * {@link ResourceContextEvent}.
	 * 
	 * @param bundleContext
	 */
	public void start(BundleContext bundleContext) {
		serviceRegistration = bundleContext
				.registerService(ResourceContextListener.class, this, null);
	}

	/**
	 * Unregister this instance as a {@link ResourceContextListener} service. Once unregistered, this instance won't receive anymore
	 * {@link ResourceContextEvent}.
	 */
	public void stop() {
		serviceRegistration.unregister();
		events.clear();
	}

	/**
	 * this method is called when a {@link ResourceContextEvent} is sent.
	 */
	public void notify(ResourceContextEvent event) {
		// store received event
		events.add(event);
	}

	/**
	 * Returns the last received event got through {@link #notify(ResourceContextEvent)} method.
	 * 
	 * @return last received resource context event.
	 */
	public ResourceContextEvent getLastEvent() {
		int index = events.size() - 1;
		if (index < 0) {
			return null;
		} else {
			return events.get(index);
		}
	}

	/**
	 * Returns the last last event got through {@link #notify(ResourceContextEvent)} method.
	 * 
	 * @return last last received event
	 */
	public ResourceContextEvent getLastLastEvent() {
		int index = events.size() - 2;
		if (index < 0) {
			return null;
		} else {
			return events.get(index);
		}
	}

	/**
	 * Retrieves the received events
	 * 
	 * @return received events, or an empty list if there is no event.
	 */
	public List<ResourceContextEvent> getReceivedEvents() {
		return events;
	}

}
