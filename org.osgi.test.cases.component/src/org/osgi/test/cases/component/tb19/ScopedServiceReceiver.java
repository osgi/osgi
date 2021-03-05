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


package org.osgi.test.cases.component.tb19;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.ServiceReceiver;

public class ScopedServiceReceiver implements ServiceReceiver<BaseService> {
	private BaseService			service;
	private Map<String, Object>	properies;

	void bind(BaseService s, Map<String, Object> p) {
		service = s;
		properies = p;
	}

	public List<BaseService> getServices() {
		return Collections.singletonList(service);
	}

	public List<Map<String, Object>> getServicesProperies() {
		return Collections.singletonList(properies);
	}
}
