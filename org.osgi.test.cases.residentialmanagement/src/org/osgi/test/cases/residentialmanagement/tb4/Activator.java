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
package org.osgi.test.cases.residentialmanagement.tb4;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.residentialmanagement.util.Service1;
/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Bundle using Service1 is going to start.");
		ServiceReference<Service1> serviceRef = context
				.getServiceReference(Service1.class);
		Service1 service = context.getService(serviceRef);
		service.testMessage1();
	}
	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Bundle using Service1 is going to stop.");
	}
}
