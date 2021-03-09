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

package org.osgi.test.cases.component.tb3.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.test.cases.component.service.TestObject;
import org.osgi.test.cases.component.tb3.ServiceConsumerEvent;

/**
 * @author $Id$
 */
public class ServiceConsumerEventImpl extends DefaultBindImpl implements
		ServiceConsumerEvent {

	private Hashtable<String,Object>	properties;
	int					count	= 0;

	public ServiceConsumerEventImpl() {
		properties = new Hashtable<>();
		properties.put("count", Integer.valueOf(count));
	}

	public synchronized void bindObject(Object o) {
		count++;
		properties.put("count", Integer.valueOf(count));
	}

	public synchronized void unbindObject(Object o) {
		count--;
		properties.put("count", Integer.valueOf(count));
	}

	public TestObject getTestObject() {
		return serviceProvider.getTestObject();
	}

	public Dictionary<String,Object> getProperties() {
		return properties;
	}
}
