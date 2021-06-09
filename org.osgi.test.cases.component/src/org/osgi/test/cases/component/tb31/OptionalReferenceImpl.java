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

package org.osgi.test.cases.component.tb31;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.TestObject;

public class OptionalReferenceImpl implements BaseService {

	private final Optional<TestObject>									serviceParam;
	private final Optional<ServiceReference<TestObject>>				srParam;
	private final Optional<ComponentServiceObjects<TestObject>>			soParam;
	private final Optional<Map<String,Object>>							propsParam;
	private final Optional<Map.Entry<Map<String,Object>,TestObject>>	tupleParam;

	private volatile Optional<TestObject>								serviceField;
	private volatile Optional<ServiceReference<TestObject>>				srField;
	private volatile Optional<ComponentServiceObjects<TestObject>>		soField;
	private volatile Optional<Map<String,Object>>						propsField;
	private volatile Optional<Map.Entry<Map<String,Object>,TestObject>>	tupleField;

	public OptionalReferenceImpl(Optional<TestObject> serviceParam,
			Optional<ServiceReference<TestObject>> srParam,
			Optional<ComponentServiceObjects<TestObject>> soParam,
			Optional<Map<String,Object>> propsParam,
			Optional<Map.Entry<Map<String,Object>,TestObject>> tupleParam) {
		this.serviceParam = serviceParam;
		this.srParam = srParam;
		this.soParam = soParam;
		this.propsParam = propsParam;
		this.tupleParam = tupleParam;
	}

	public Dictionary<String,Object> getProperties() {
		final Dictionary<String,Object> props = new Hashtable<>();
		Optional.ofNullable(serviceParam)
				.ifPresent(local -> props.put("serviceParam", local));
		Optional.ofNullable(srParam)
				.ifPresent(local -> props.put("srParam", local));
		Optional.ofNullable(soParam)
				.ifPresent(local -> props.put("soParam", local));
		Optional.ofNullable(propsParam)
				.ifPresent(local -> props.put("propsParam", local));
		Optional.ofNullable(tupleParam)
				.ifPresent(local -> props.put("tupleParam", local));

		Optional.ofNullable(serviceField)
				.ifPresent(local -> props.put("serviceField", local));
		Optional.ofNullable(srField)
				.ifPresent(local -> props.put("srField", local));
		Optional.ofNullable(soField)
				.ifPresent(local -> props.put("soField", local));
		Optional.ofNullable(propsField)
				.ifPresent(local -> props.put("propsField", local));
		Optional.ofNullable(tupleField)
				.ifPresent(local -> props.put("tupleField", local));

		return props;
	}

}
