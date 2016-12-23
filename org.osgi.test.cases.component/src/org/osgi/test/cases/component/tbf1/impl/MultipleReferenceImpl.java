/*
 * Copyright (c) OSGi Alliance (2012, 2016). All Rights Reserved.
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

package org.osgi.test.cases.component.tbf1.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.TestObject;

public class MultipleReferenceImpl implements BaseService {

	private volatile List<TestObject> services;

	public Dictionary<String,Object> getProperties() {
		final Dictionary<String,Object> props = new Hashtable<>();
		final List<TestObject> local = services;
		if (local != null) {
			props.put("services", local);
		}

		return props;
	}

}
