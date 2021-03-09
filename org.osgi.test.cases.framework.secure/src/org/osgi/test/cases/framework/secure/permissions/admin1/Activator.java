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
package org.osgi.test.cases.framework.secure.permissions.admin1;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		Bundle[] bundles = context.getBundles();
		for (int i = 0, l = bundles.length; i < l; i++) {
			Bundle bundle = bundles[i];
			if ("org.osgi.test.cases.framework.secure.permissions.admin2"
					.equals(bundle.getSymbolicName())) {
				String requestedClassName = "org.osgi.test.cases.framework.secure.permissions.admin2.TestClass";
				Class< ? > testClass = bundle.loadClass(requestedClassName);
				if (requestedClassName.equals(testClass.getName())) {
					return;
				}
				throw new RuntimeException(
						"unable to load TestClass from bundle org.osgi.test.cases.framework.secure.permissions.admin2");
			}
		}
		throw new RuntimeException(
				"unable to find bundle org.osgi.test.cases.framework.secure.permissions.admin2");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// empty
	}

}
