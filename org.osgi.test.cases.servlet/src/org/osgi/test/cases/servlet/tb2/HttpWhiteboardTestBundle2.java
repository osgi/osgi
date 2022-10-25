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
package org.osgi.test.cases.servlet.tb2;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants;
import org.osgi.test.cases.servlet.junit.mock.MockSCL;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextListener;

public class HttpWhiteboardTestBundle2 implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=sc1)");
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
		properties.put("test", "true");
		serviceRegistrations.add(
				context.registerService(
						new String[] {ServletContextListener.class.getName(), MockSCL.class.getName()},
						new MockSCL(new AtomicReference<ServletContext>()), properties));
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		for (ServiceRegistration<?> serviceRegistration : serviceRegistrations) {
			serviceRegistration.unregister();
		}

		serviceRegistrations.clear();
	}

	private List<ServiceRegistration<?>>	serviceRegistrations	= new CopyOnWriteArrayList<ServiceRegistration<?>>();

}
