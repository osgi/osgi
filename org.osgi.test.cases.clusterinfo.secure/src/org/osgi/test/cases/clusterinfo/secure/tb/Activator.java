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
package org.osgi.test.cases.clusterinfo.secure.tb;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
	private ServiceRegistration<String> reg;

	@Override
	public void start(BundleContext context) throws Exception {
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("org.osgi.service.clusterinfo.tags", new String[] {
				"foo", "bar"
		});
		reg = context.registerService(String.class, "something", props);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		reg.unregister();
	}
}
