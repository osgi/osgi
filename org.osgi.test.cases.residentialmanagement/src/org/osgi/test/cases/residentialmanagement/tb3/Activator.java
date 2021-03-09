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
package org.osgi.test.cases.residentialmanagement.tb3;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.residentialmanagement.util.Service1;
import org.osgi.test.cases.residentialmanagement.util.Service2;
import org.osgi.test.cases.residentialmanagement.util.Service3;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class Activator implements BundleActivator {
	public ServiceRegistration< ? >	serviceReg;
	Dictionary<String,Object>		props	= new Hashtable<>();

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Bundle Registering Service1 and Service3 is going to start.");
		props.put("testKey1", "testValue1");
		props.put("testKey2", "testValue3");
		String[] clazzes = new String[] { Service1.class.getName(),
				Service2.class.getName(), Service3.class.getName() };
		try {
			TestServiceImpl tsi = new TestServiceImpl();
			serviceReg = context.registerService(clazzes, tsi, props);
			tsi.setServiceRegistration(serviceReg);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Bundle Registering Service1 and Service3 is going to stop.");
	}
}
