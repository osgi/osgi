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
package org.osgi.test.cases.framework.secure.lifecycle.tb5;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Bundle for the event test.
 * 
 * @author Ericsson Radio Systems AB
 */
public class EventTest implements BundleActivator {
	ServiceRegistration<EventTest> _sr;

	/**
	 * Starts the bundle. Registers a service.
	 */
	public void start(BundleContext bc) {
		Hashtable<String,Object> props;
		props = new Hashtable<>();
		props.put("Service-Name", "EventTest");
		_sr = bc.registerService(EventTest.class, this, props);
	}

	/**
	 * Stops the bundle. Unregisters the service.
	 */
	public void stop(BundleContext bc) {
		try {
			_sr.unregister();
		}
		catch (IllegalStateException e) { /* Ignore */
		}
	}
}
