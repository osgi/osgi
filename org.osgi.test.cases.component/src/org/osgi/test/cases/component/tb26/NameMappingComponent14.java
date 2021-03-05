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


package org.osgi.test.cases.component.tb26;

import java.util.Map;

import org.osgi.service.component.ComponentConstants;
import org.osgi.test.cases.component.service.ObjectProvider1;
import org.osgi.test.cases.component.types.NameMapping14;

public class NameMappingComponent14 implements ObjectProvider1<NameMapping14> {
	private volatile NameMapping14		config;
	private volatile Map<String, Object>	properties;

	public NameMappingComponent14() {
	}

	void activate(NameMapping14 c, Map<String,Object> p) {
		config = c;
		properties = p;
		System.out.printf("activate: %s[%X]\n",
				properties.get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
	}

	void modified(NameMapping14 c, Map<String,Object> p) {
		config = c;
		properties = p;
		System.out.printf("modified: %s[%X]\n",
				properties.get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
	}

	void deactivate(NameMapping14 c, Map<String,Object> p) {
		System.out.printf("deactivate: %s[%X]\n",
				properties.get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
	}

	public NameMapping14 get1() {
		return config;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

}
