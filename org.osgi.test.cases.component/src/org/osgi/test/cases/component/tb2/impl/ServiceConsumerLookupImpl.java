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

package org.osgi.test.cases.component.tb2.impl;

import java.util.Dictionary;

import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.ServiceProvider;
import org.osgi.test.cases.component.service.TestObject;
import org.osgi.test.cases.component.tb2.ServiceConsumerLookup;

/**
 * @author $Id$
 */
public class ServiceConsumerLookupImpl implements ServiceConsumerLookup {

	private ComponentContext	context;

	public ServiceConsumerLookupImpl() {
		// empty
	}

	protected void activate(ComponentContext c) {
		this.context = c;
	}

	protected void deactivate(ComponentContext c) {
		this.context = null;
	}

	public Dictionary<String,Object> getProperties() {
		return context.getProperties();
	}

	public TestObject getTestObject() {
		ServiceProvider serviceProvider = (ServiceProvider) context
				.locateService("serviceProvider");
		return serviceProvider.getTestObject();
	}

	public void enableComponent(String name, boolean flag) {
		if (flag) {
			context.enableComponent(name);
		}
		else {
			context.disableComponent(name);
		}
	}
}
