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
package org.osgi.test.cases.jmx.framework.tb2.impl;

import java.util.Dictionary;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;

public class ConfiguratorImpl implements ManagedServiceFactory, ManagedService {
	
	public ConfiguratorImpl() {
	}
	
	//coming from ManagedService
	public void updated(Dictionary<String, ? > props)
			throws ConfigurationException {
		System.out.println("Update called with pid " + props.get(Constants.SERVICE_PID));		
	}
	
	//coming from ManagedServiceFactory	
	public String getName() {
		System.out.println("Get name called");		
		return "JMX test managed service factory";
	}

	//coming from ManagedServiceFactory	
	public void updated(String pid, Dictionary<String, ? > properties)
			throws ConfigurationException {
		System.out.println("Factory Configuration update with " + pid + " and value " + properties + " is called");
	}
	
	//coming from ManagedServiceFactory	
	public void deleted(String pid) {
		System.out.println("Factory Configuration delete with " + pid + " called");		
	}
}
