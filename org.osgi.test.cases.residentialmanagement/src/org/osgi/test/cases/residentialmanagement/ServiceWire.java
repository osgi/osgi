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
package org.osgi.test.cases.residentialmanagement;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.ServiceReference;

public class ServiceWire implements RMTConstants{

	String provider;
	String requirer;
	ServiceReference< ? >	ref;

	public ServiceWire(ServiceReference< ? > ref, String provider,
			String requirer) {
		this.ref = ref;
		this.provider = provider;
		this.requirer = requirer;
	}
	
	String getFilter() {
		return ("(service.id=" + ref.getProperty("service.id") + ")");
	}
	
	Map<String, Object> getCapabilityAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();
		for (String key : ref.getPropertyKeys())
			attributes.put( key, ref.getProperty(key));
		attributes.put("osgi.wiring.rmt.service", ref.getProperty("service.id"));
		return attributes;
	}
	
	Map<String, String> getRequirementAttributes() {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(FILTER, getFilter());
		return attributes;
	}
}
