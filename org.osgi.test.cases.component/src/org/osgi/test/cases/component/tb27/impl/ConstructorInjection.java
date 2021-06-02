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
package org.osgi.test.cases.component.tb27.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.log.LogService;
import org.osgi.test.cases.component.tb27.NamedService;

/**
 *
 *
 */
public class ConstructorInjection implements NamedService {
	@interface Config {
		String prop() default "default.prop";
	}

	private final String			name;
	private final BundleContext		bc;
	private final ComponentContext	cc;

	public ConstructorInjection(ComponentContext cc,

			BundleContext bc,

			Map<String,Object> props,

			Config configNames,

			LogService fieldStatic,

			LogService fieldMandatory,

			LogService fieldOptional,

			List<LogService> fieldMultiple,

			List<LogService> fieldAtLeastOne,

			LogService fieldGreedy,

			LogService fieldReluctant,

			LogService fieldReplace,

			ComponentContext cc2,

			LogService fieldBundle,

			LogService fieldPrototype,

			BundleContext bc2,

			ServiceReference<LogService> fieldReference,

			ComponentServiceObjects<LogService> fieldServiceObjects,

			Map<String,Object> fieldProperties,

			Map.Entry<Map<String,Object>,LogService> fieldTuple,

			Collection<ComponentServiceObjects<LogService>> fieldServiceObjectsM,

			Collection<Map<String,Object>> fieldPropertiesM,

			Collection<Map.Entry<Map<String,Object>,LogService>> fieldTupleM,

			Collection<Map<String,Object>> fieldServiceM,

			Collection<ServiceReference<LogService>> fieldServiceReferencesM,

			LogService fieldOptionalNull) {
		/**/
		System.out.println("constructed");
		name = configNames.prop();
		this.bc = bc;
		this.cc = cc;
		assertThat(cc).isNotNull();
		assertThat(bc).isNotNull();
		assertThat(props).isNotNull();
		assertThat(configNames).isNotNull();
		assertThat(fieldStatic).isNotNull();
		assertThat(fieldMandatory).isNotNull();
		assertThat(fieldOptional).isNotNull();
		assertThat(fieldMultiple).doesNotContainNull();
		assertThat(fieldAtLeastOne).isNotEmpty().doesNotContainNull();
		assertThat(fieldGreedy).isNotNull();
		assertThat(fieldReluctant).isNotNull();
		assertThat(fieldReplace).isNotNull();
		assertThat(cc2).isNotNull();
		assertThat(fieldBundle).isNotNull();
		assertThat(fieldPrototype).isNotNull();
		assertThat(bc2).isNotNull();
		assertThat(fieldReference).isNotNull();
		assertThat(fieldServiceObjects).isNotNull();
		assertThat(fieldProperties).isNotEmpty();
		assertThat(fieldTuple).isNotNull();
		assertThat(fieldTuple.getKey()).isNotNull();
		assertThat(fieldTuple.getValue()).isNotNull();
		assertThat(fieldServiceObjectsM).doesNotContainNull();
		assertThat(fieldPropertiesM).doesNotContainNull();
		assertThat(fieldTupleM).doesNotContainNull();
		assertThat(fieldServiceM).doesNotContainNull();
		assertThat(fieldServiceReferencesM).doesNotContainNull();
		assertThat(fieldOptionalNull).isNull();

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName() + " " + (bc == cc.getBundleContext());
	}

	@Override
	public BundleContext getBundleContext() {
		return bc;
	}

	public ComponentContext getComponentContext() {
		return cc;
	}
}
