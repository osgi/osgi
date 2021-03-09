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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.MultipleFieldTestService;
import org.osgi.test.cases.component.service.ScalarFieldTestService;
import org.osgi.test.cases.component.service.TestObject;

public class BadFieldReceiver extends AbstractFieldReceiver
		implements ScalarFieldTestService<BaseService>, MultipleFieldTestService<BaseService> {
	private static final TestObject	SENTINEL	= new TestObject();
	private volatile TestObject		badScalarType;
	private volatile TestObject		badMultipleServiceType;
	private volatile TestObject		badMultipleReferenceType;
	private volatile TestObject		badMultipleServiceObjectsType;
	private volatile TestObject		badMultiplePropertiesType;
	private volatile TestObject		badMultipleTupleType;
	private volatile Set<BaseService>	badUpdate;

	public BadFieldReceiver() {
		badScalarType = SENTINEL;
		badMultipleServiceType = SENTINEL;
		badMultipleReferenceType = SENTINEL;
		badMultipleServiceObjectsType = SENTINEL;
		badMultiplePropertiesType = SENTINEL;
		badMultipleTupleType = SENTINEL;
		badUpdate = null;
	}

	public BaseService getService() {
		return null;
	}

	public Object getAssignable() {
		return (badScalarType == SENTINEL) ? null : new TestObject();
	}

	public ServiceReference<BaseService> getReference() {
		return null;
	}

	public ComponentServiceObjects<BaseService> getServiceObjects() {
		return null;
	}

	public Map<String, Object> getProperties() {
		return null;
	}

	public Entry<Map<String, Object>, BaseService> getTuple() {
		return null;
	}

	public Collection<BaseService> getCollectionService() {
		return (badMultipleServiceType == SENTINEL) ? null : Collections.<BaseService> emptyList();
	}

	public List<BaseService> getListService() {
		return null;
	}

	public Collection<BaseService> getCollectionSubtypeService() {
		return badUpdate;
	}

	public Collection<ServiceReference<BaseService>> getCollectionReference() {
		return (badMultipleReferenceType == SENTINEL) ? null : Collections.<ServiceReference<BaseService>> emptyList();
	}

	public List<ServiceReference<BaseService>> getListReference() {
		return null;
	}

	public Collection<ServiceReference<BaseService>> getCollectionSubtypeReference() {
		return null;
	}

	public Collection<ComponentServiceObjects<BaseService>> getCollectionServiceObjects() {
		return (badMultipleServiceObjectsType == SENTINEL) ? null
				: Collections.<ComponentServiceObjects<BaseService>> emptyList();
	}

	public List<ComponentServiceObjects<BaseService>> getListServiceObjects() {
		return null;
	}

	public Collection<ComponentServiceObjects<BaseService>> getCollectionSubtypeServiceObjects() {
		return null;
	}

	public Collection<Map<String, Object>> getCollectionProperties() {
		return (badMultiplePropertiesType == SENTINEL) ? null : Collections.<Map<String, Object>> emptyList();
	}

	public List<Map<String, Object>> getListProperties() {
		return null;
	}

	public Collection<Map<String, Object>> getCollectionSubtypeProperties() {
		return null;
	}

	public Collection<Entry<Map<String, Object>, BaseService>> getCollectionTuple() {
		return (badMultipleTupleType == SENTINEL) ? null
				: Collections.<Entry<Map<String, Object>, BaseService>> emptyList();
	}

	public List<Entry<Map<String, Object>, BaseService>> getListTuple() {
		return null;
	}

	public Collection<Entry<Map<String, Object>, BaseService>> getCollectionSubtypeTuple() {
		return null;
	}
}
