/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
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
package org.osgi.test.cases.component.tb27.impl;

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

			Collection<Map<String,Object>> fieldServiceM) {
		/**/
		System.out.println("constructed");
		name = configNames.prop();
		this.bc = bc;
		this.cc = cc;
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
