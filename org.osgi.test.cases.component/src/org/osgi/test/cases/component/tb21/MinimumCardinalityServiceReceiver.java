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


package org.osgi.test.cases.component.tb21;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.ServiceReceiver;

public class MinimumCardinalityServiceReceiver implements ServiceReceiver<BaseService> {
	private List<BaseService>			services	= new ArrayList<BaseService>();
	private List<Map<String, Object>>	properies	= new ArrayList<Map<String, Object>>();

	void bind(BaseService s, Map<String, Object> p) {
		synchronized (services) {
			services.add(s);
			properies.add(p);
		}
	}

	void unbind(BaseService s, Map<String, Object> p) {
		synchronized (services) {
			services.remove(s);
			properies.remove(p);
		}
	}

	public List<BaseService> getServices() {
		synchronized (services) {
			return new ArrayList<BaseService>(services);
		}
	}

	public List<Map<String, Object>> getServicesProperies() {
		synchronized (services) {
			return new ArrayList<Map<String, Object>>(properies);
		}
	}
}
