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
package org.osgi.test.cases.framework.secure.permissions.get;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.framework.secure.permissions.util.IService1;
import org.osgi.test.cases.framework.secure.permissions.util.PermissionsFilterException;
import org.osgi.test.cases.framework.secure.permissions.util.Util;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class GetActivator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {

		if (Util.debug)
			System.out.println("GETTER BUNDLE is going to start.");

		ServiceReference<IService1> ref = null;

		String clazz = IService1.class.getName();
		ref = context.getServiceReference(IService1.class);
		if (ref == null) {
			throw new PermissionsFilterException(
					"Fail to get ServiceReference of " + clazz);
		}
		else {
			if (Util.debug)
				System.out
						.println("# Get Test> Succeed in getting Service Reference of "
								+ clazz);
		}

		Object service = context.getService(ref);
		if (service == null) {
			throw new PermissionsFilterException("Fail to get service of "
					+ clazz);
		}
		else {
			if (Util.debug)
				System.out.println("# Get Test> Succeed in getting Service of "
						+ clazz);
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
