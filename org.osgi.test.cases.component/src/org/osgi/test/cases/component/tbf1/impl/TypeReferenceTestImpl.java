/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.TestObject;

public class TypeReferenceTestImpl implements BaseService {

	private TestObject service;

	private ServiceReference ref;

	// private ServiceObjects objects;

	private Map properties;

	private Map.Entry tuple;

	public Dictionary getProperties() {
		final Dictionary props = new Hashtable();
		props.put("service", service);
		props.put("ref", ref);
		props.put("map", properties);
		props.put("tuple", tuple);

		return props;
	}

}
