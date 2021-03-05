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
package org.osgi.test.cases.framework.secure.permissions.registerModify;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.framework.secure.permissions.util.IService1;
import org.osgi.test.cases.framework.secure.permissions.util.Util;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class RegisterModifyActivator implements BundleActivator {

	ServiceRegistration<IService1> sr;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {

		if (Util.debug)
			System.out.println("REGISTER BUNDLE is going to start.");
		final Hashtable<String,Object> props = new Hashtable<>();
		props.put("segment", "providerA");
		props.put("vendor", "NTT");
		String clazz = IService1.class.getName();
		try {
			sr = context.registerService(IService1.class,
					new IServiceImpl(context),
					props);
			if (Util.debug)
				System.out
						.println("# Properties Modify Test> Succeed in registering service: "
								+ clazz);
		}
		catch (Exception e) {
			if (Util.debug)
				System.out
						.println("# Properties Modify Test> Fail to register service: "
								+ clazz);
			throw e;
		}
		props.put("segment", "providerB");
		props.put("vendor", "ACME");
		try {
			sr.setProperties(props);
			if (Util.debug)
				System.out
						.println("# Properties Modify Test> Succeed in modifying properties: "
								+ clazz);
		}
		catch (Exception e) {
			if (Util.debug)
				System.out
						.println("# Properties Modify Test> Fail to modify properties: "
								+ clazz);
			// e.printStackTrace();
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		sr.unregister();
		sr = null;
	}

}
