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

package org.osgi.test.cases.framework.div.tb24c;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.framework.div.tb24a.SomeService;
import org.osgi.test.cases.framework.div.tb24b.SomeServiceBuilder;

/**
 * Activator for service registration tests.
 * 
 * @author left@cesar.org.br
 * 
 * @author $Id$
 */
public class Activator implements BundleActivator {

	private ServiceRegistration< ? > sr;
	
	/**
	 * Starts Bundle.
	 */
	public void start(BundleContext bc) {
		SomeService service;
		
		service = new SomeServiceBuilder().createSomeService();
		sr = bc.registerService("org.osgi.test.cases.framework.div.tb24a.SomeService", 
				service, null);
	}

	/**
	 * Stops Bundle.
	 */
	public void stop(BundleContext bc) {
		sr.unregister();
	}

}
