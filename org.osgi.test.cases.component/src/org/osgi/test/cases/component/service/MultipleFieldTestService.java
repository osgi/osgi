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

package org.osgi.test.cases.component.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentServiceObjects;

public interface MultipleFieldTestService<T> {
	Collection<T> getCollectionService();

	List<T> getListService();

	Collection<T> getCollectionSubtypeService();

	Collection<ServiceReference<T>> getCollectionReference();

	List<ServiceReference<T>> getListReference();

	Collection<ServiceReference<T>> getCollectionSubtypeReference();

	Collection<ComponentServiceObjects<T>> getCollectionServiceObjects();

	List<ComponentServiceObjects<T>> getListServiceObjects();

	Collection<ComponentServiceObjects<T>> getCollectionSubtypeServiceObjects();

	Collection<Map<String, Object>> getCollectionProperties();

	List<Map<String, Object>> getListProperties();

	Collection<Map<String, Object>> getCollectionSubtypeProperties();

	Collection<Map.Entry<Map<String, Object>, T>> getCollectionTuple();

	List<Map.Entry<Map<String, Object>, T>> getListTuple();

	Collection<Map.Entry<Map<String, Object>, T>> getCollectionSubtypeTuple();

	int getActivationCount();

	int getModificationCount();

	int getDeactivationCount();
}
