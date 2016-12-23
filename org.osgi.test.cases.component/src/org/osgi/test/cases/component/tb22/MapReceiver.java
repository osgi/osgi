/*
 * Copyright (c) OSGi Alliance (2015, 2016). All Rights Reserved.
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


package org.osgi.test.cases.component.tb22;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.ServiceReceiver;

public class MapReceiver implements ServiceReceiver<BaseService> {
	private Map<Map<String, Object>, BaseService> refs = new TreeMap<Map<String, Object>, BaseService>(
			new Comparator<Map<String, Object>>() {
				@SuppressWarnings("unchecked")
				public int compare(Map<String, Object> var0, Map<String, Object> var1) {
					return ((Comparable<Map<String, Object>>) var1).compareTo(var0);
				}
			});

	void bind(BaseService s, Map<String, Object> p) {
		synchronized (refs) {
			refs.put(p, s);
		}
	}

	public List<BaseService> getServices() {
		synchronized (refs) {
			return new ArrayList<BaseService>(refs.values());
		}
	}

	public List<Map<String, Object>> getServicesProperies() {
		synchronized (refs) {
			return new ArrayList<Map<String, Object>>(refs.keySet());
		}
	}
}
