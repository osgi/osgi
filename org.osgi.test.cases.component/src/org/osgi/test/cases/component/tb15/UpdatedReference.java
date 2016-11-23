/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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

package org.osgi.test.cases.component.tb15;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.TestObject;

@SuppressWarnings("unused")
public class UpdatedReference implements BaseService {
	private static final String	KEY			= "org.osgi.test.cases.component.tb15.serviceproperty";
	private Dictionary<String,Object>	properties	= new Hashtable<>();

	private void activate(ComponentContext ctxt) {
		Dictionary<String,Object> props = ctxt.getProperties();
		Enumeration<String> en = props.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			properties.put(key, props.get(key));
		}
	}

	private void deactivate(ComponentContext ctxt) {
		// empty
	}

	public Dictionary<String,Object> getProperties() {
		return properties;
	}

	private void bind(TestObject svc, Map<String,Object> props) {
		properties.put(KEY, "bind" + props.get(KEY));
	}

	private void updatedSr(ServiceReference<TestObject> ref) {
		properties.put(KEY, "updatedSr" + ref.getProperty(KEY));
	}

	private void updatedSvc(TestObject svc) {
		properties.put(KEY, "updatedSvc");
	}

	private void updated(TestObject svc, Map<String,Object> props) {
		properties.put(KEY, "updatedSvcMap" + props.get(KEY));
	}

	private void updatedBadSig(Map<String,Object> props) {
		properties.put(KEY, "updatedBadSig" + props.get(KEY));
	}

	private void updatedSvcMap(TestObject svc, Map<String,Object> props) {
		properties.put(KEY, "updatedSvcMap" + props.get(KEY));
	}

	private void updatedOverloaded1(ServiceReference<TestObject> ref) {
		properties.put(KEY, "updatedOverloaded1Sr" + ref.getProperty(KEY));
	}

	private void updatedOverloaded1(TestObject svc) {
		properties.put(KEY, "updatedOverloaded1Svc");
	}

	private void updatedOverloaded1(TestObject svc, Map<String,Object> props) {
		properties.put(KEY, "updatedOverloaded1SvcMap" + props.get(KEY));
	}

	private void updatedOverloaded2(TestObject svc) {
		properties.put(KEY, "updatedOverloaded2Svc");
	}

	private void updatedOverloaded2(TestObject svc, Map<String,Object> props) {
		properties.put(KEY, "updatedOverloaded2SvcMap" + props.get(KEY));
	}

	private void unbind(TestObject svc, Map<String,Object> props) {
		properties.put(KEY, "unbind" + props.get(KEY));
	}
}
