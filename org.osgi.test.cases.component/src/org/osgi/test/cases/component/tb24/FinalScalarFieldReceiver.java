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


package org.osgi.test.cases.component.tb24;

import java.util.Map;
import java.util.Map.Entry;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.ScalarFieldTestService;

public class FinalScalarFieldReceiver implements ScalarFieldTestService<BaseService> {
	private final BaseService									service			= null;
	private final Object										assignable		= null;
	private final ServiceReference<BaseService>					reference		= null;
	private final ComponentServiceObjects<BaseService>			serviceobjects	= null;
	private final Map<String, Object>							properties		= null;
	private final Map.Entry<Map<String, Object>, BaseService>	tuple			= null;
	
	public FinalScalarFieldReceiver() {
	}

	void activate(ComponentContext context) {
		System.out.printf("activate: %s[%X]\n", context.getProperties().get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
	}

	void modified(ComponentContext context) {
		System.out.printf("modified: %s[%X]\n", context.getProperties().get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
	}

	void deactivate(ComponentContext context) {
		System.out.printf("deactivate: %s[%X]\n", context.getProperties().get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
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
