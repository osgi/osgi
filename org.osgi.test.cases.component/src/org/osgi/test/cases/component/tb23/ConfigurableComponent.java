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


package org.osgi.test.cases.component.tb23;

import java.util.Dictionary;

import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.BaseService;

public class ConfigurableComponent implements BaseService {
	private Dictionary<String, Object> properties;

	void activate(ComponentContext context) {
		properties = context.getProperties();
		System.out.printf("activate: %s[%X]\n", properties.get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
	}

	void modified(ComponentContext context) {
		properties = context.getProperties();
		System.out.printf("modified: %s[%X]\n", properties.get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
	}

	void deactivate(ComponentContext context) {
		System.out.printf("deactivate: %s[%X]\n", properties.get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
		properties = null;
	}

	public Dictionary<String, Object> getProperties() {
		return properties;
	}
}
