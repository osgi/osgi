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


package org.osgi.test.cases.component.tb24;

import java.util.Map;
import java.util.Map.Entry;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.ScalarFieldTestService;
import org.osgi.test.support.MockFactory;

public class StaticScalarFieldReceiver extends AbstractFieldReceiver implements ScalarFieldTestService<BaseService> {
	private BaseService									service;
	private Object										assignable;
	private ServiceReference<BaseService>				reference;
	private ComponentServiceObjects<BaseService>		serviceobjects;
	private Map<String, Object>							properties;
	private Map.Entry<Map<String, Object>, BaseService>	tuple;
	
	@SuppressWarnings("unchecked")
	public StaticScalarFieldReceiver() {
		service = MockFactory.newMock(BaseService.class, null);
		assignable = new Object();
		reference = MockFactory.newMock(ServiceReference.class, null);
		serviceobjects = MockFactory.newMock(ComponentServiceObjects.class, null);
		properties = MockFactory.newMock(Map.class, null);
		tuple = MockFactory.newMock(Map.Entry.class, null);
	}

	public BaseService getService() {
		return service;
	}

	public Object getAssignable() {
		return assignable;
	}

	public ServiceReference<BaseService> getReference() {
		return reference;
	}

	public ComponentServiceObjects<BaseService> getServiceObjects() {
		return serviceobjects;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public Entry<Map<String, Object>, BaseService> getTuple() {
		return tuple;
	}
}
