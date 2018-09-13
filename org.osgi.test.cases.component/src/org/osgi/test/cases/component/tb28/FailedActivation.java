/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.osgi.test.cases.component.tb28;

import java.util.Map;

import org.osgi.service.component.ComponentConstants;
import org.osgi.test.cases.component.service.ObjectProvider1;

public class FailedActivation implements ObjectProvider1<Object> {
	private volatile Map<String, Object>	properties;

	public FailedActivation() {}

	void activate(Map<String,Object> p) {
		properties = p;
		System.out.printf("activate: %s[%X]\n",
				properties.get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
		throw new RuntimeException("failed activation test");
	}

	void deactivate() {}

	public Object get1() {
		return this;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

}
