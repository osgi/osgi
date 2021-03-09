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
package org.osgi.test.cases.component.tb4a.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.osgi.test.cases.component.tb4a.NamedService;

/**
 */
public class NamedServiceFactory implements NamedService {

	@interface Config {
		String name();
	}

	private BundleContext	context;
	@SuppressWarnings("unused")
	private ComponentContext				cc;
	@SuppressWarnings("unused")
	private Map<String,Object>				props;
	private Config							config;

	private final Collection<LogService>	logs;

	public NamedServiceFactory(ComponentContext componentContext,
			BundleContext bundleContext, Map<String,Object> properties,
			Config someProperties,
			Collection<LogService> logs) {
		if (componentContext != null && bundleContext != null
				&& properties != null && someProperties != null
				&& componentContext.getBundleContext().equals(bundleContext)
				&& Objects.equals(componentContext.getProperties().get("name"),
						someProperties.name())) {
			this.logs = logs;
		} else {
			this.logs = Collections.emptyList();
		}
	}

	@Override
	public String getName() {
		return config.name();
	}

	@Override
	public String toString() {
		return getName() + " " + !getLogServices().isEmpty();
	}

	@Override
	public BundleContext getBundleContext() {
		return context;
	}

	private Collection<LogService> getLogServices() {
		return logs;
	}

}
