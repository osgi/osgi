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
package org.osgi.test.cases.framework.secure.permissions.registerPlural;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.framework.secure.permissions.util.IService1;
import org.osgi.test.cases.framework.secure.permissions.util.IService2;
import org.osgi.test.cases.framework.secure.permissions.util.Util;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class registerPluralActivator implements BundleActivator {

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
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("segment", "providerA");
		props.put("vendor", "NTT");

		String[] clazzes = new String[] {IService1.class.getName(),
				IService2.class.getName()};

		try {
			context.registerService(clazzes, new IServicePluralImpl(context),
					props);
			if (Util.debug)
				System.out
						.println("# Register Plural Test> Succeed in registering service: "
								+ clazzes);

		}
		catch (Exception e) {
			if (Util.debug)
				System.out
						.println("# Register Plural Test> Fail to register service: "
								+ clazzes);
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
		// empty
	}

}
