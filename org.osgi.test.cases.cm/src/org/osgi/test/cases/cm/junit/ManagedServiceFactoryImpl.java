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
package org.osgi.test.cases.cm.junit;

import static org.osgi.test.support.compatibility.DefaultTestBundleControl.log;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

public class ManagedServiceFactoryImpl implements ManagedServiceFactory {
	private final String	name;
	@SuppressWarnings("unused")
	private final String	propertyName;
	private final Map<String,SomeService>	services	= new HashMap<>();
	private final Semaphore	semaphore;

	public ManagedServiceFactoryImpl(String name, String propertyName,
			Semaphore semaphore) {
		this.name = name;
		this.propertyName = propertyName;
		this.semaphore = semaphore;
	}

	public String getName() {
		return name;
	}

	public synchronized void updated(String pid,
			Dictionary<String, ? > properties)
			throws ConfigurationException {
		try {
			log("+++ updating " + pid);
			String data = "somedata";
			/*
			 * String data = (String) properties.get(propertyName);
			 * 
			 * if(data == null) { throw new ConfigurationException(propertyName,
			 * "not found in the properties"); }
			 */
			/* Try to get the service */
			SomeService theService = services.get(pid);
			/* If the service did not exist... */
			if (theService == null) {
				theService = new SomeService();
				services.put(pid, theService);
			}
			theService.setConfigData(data);
			semaphore.release();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		log("--- done updating " + pid);
	}

	public synchronized void deleted(java.lang.String pid) {
		services.remove(pid);
	}

	public synchronized int getNumberOfServices() {
		return services.size();
	}

	class SomeService {
		@SuppressWarnings("unused")
		private String	configData;

		public void setConfigData(String configData) {
			this.configData = configData;
		}
	}
}
