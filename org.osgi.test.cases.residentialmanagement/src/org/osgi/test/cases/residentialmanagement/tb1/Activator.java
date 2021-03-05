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
package org.osgi.test.cases.residentialmanagement.tb1;
/**
 * 
 * @author Koya MORI, Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.residentialmanagement.sharedpackage.SharedPackage;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Bundle registering SharedPackage is going to start.");
		Object service = new SPImpl();
		String clazz = SharedPackage.class.getName();
		context.registerService(clazz , service, null);
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Bundle registering SharedPackage is going to stop.");

	}

}
