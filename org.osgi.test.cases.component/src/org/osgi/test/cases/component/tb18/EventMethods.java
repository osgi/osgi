/*
 * Copyright (c) 2015 OSGi Alliance. All Rights Reserved.
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
package org.osgi.test.cases.component.tb18;

import static org.osgi.test.cases.component.service.DSTestConstants.*;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.ComponentEnabler;
import org.osgi.test.cases.component.service.TBCService;

public class EventMethods implements BaseService {
	private Dictionary<String,Object>	properties	= new Hashtable<>();
	private ComponentContext	ctxt;

	void activate(ComponentContext ctxt) {
		this.ctxt = ctxt;
		Dictionary<String,Object> props = ctxt.getProperties();
		Enumeration<String> en = props.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			properties.put(key, props.get(key));
		}
	}

	void deactivate(ComponentContext ctxt) {
	}

	void bind1(ComponentServiceObjects<ComponentEnabler> cso) {
		setDataBits(ERROR_1);
	}

	void unbind1(ComponentServiceObjects<ComponentEnabler> cso) {
		setDataBits(ERROR_1);
	}

	void bind1(ServiceReference<ComponentEnabler> sr) {
		check(sr);
		setDataBits(BIND_1);
	}

	void unbind1(ServiceReference<ComponentEnabler> sr) {
		check(sr);
		setDataBits(UNBIND_1);
	}

	void bind2(TBCService ce) {
		setDataBits(ERROR_2);
	}

	void unbind2(TBCService ce) {
		setDataBits(ERROR_2);
	}

	void bind2(ComponentEnabler ce) {
		check(ce);
		setDataBits(BIND_2);
	}

	void unbind2(ComponentEnabler ce) {
		check(ce);
		setDataBits(UNBIND_2);
	}

	void bind3(ComponentEnabler ce, Map<String,Object> props) {
		setDataBits(BIND_3);
	}

	void unbind3(ComponentEnabler ce, Map<String,Object> props) {
		check(ce);
		check(props);
		setDataBits(UNBIND_3);
	}

	void bind4(ServiceReference<ComponentEnabler> sr, ComponentEnabler ce) {
		check(sr);
		check(ce);
		setDataBits(BIND_4);
	}

	void unbind4(ServiceReference<ComponentEnabler> sr, ComponentEnabler ce) {
		check(sr);
		check(ce);
		setDataBits(UNBIND_4);
	}

	void bind5(ComponentEnabler ce, ServiceReference<ComponentEnabler> sr) {
		setDataBits(ERROR_5);
	}

	void unbind5(ComponentEnabler ce, ServiceReference<ComponentEnabler> sr) {
		setDataBits(ERROR_5);
	}

	void bind5(Map<String,Object> props) {
		check(props);
		setDataBits(BIND_5);
	}

	void unbind5(Map<String,Object> props) {
		check(props);
		setDataBits(UNBIND_5);
	}

	void bind6(Map<String,Object> props, ComponentEnabler ce,
			ServiceReference<ComponentEnabler> sr,
			ComponentServiceObjects< ? > cso, TBCService ce2) {
		check(ce);
		check(props);
		check(sr);
		check(cso);
		if (!sr.equals(cso.getServiceReference()))
			throw new AssertionError("service reference mismatch");
		if (ce2 != ce)
			throw new AssertionError("service mismatch");
		if (cso.getService() != ce)
			throw new AssertionError("service object mismatch");
		setDataBits(BIND_6);
	}

	void unbind6(Map<String,Object> props, ComponentEnabler ce,
			ServiceReference<ComponentEnabler> sr,
			ComponentServiceObjects< ? > cso, TBCService ce2) {
		check(ce);
		check(props);
		check(sr);
		check(cso);
		if (!sr.equals(cso.getServiceReference()))
			throw new AssertionError("service reference mismatch");
		if (ce2 != ce)
			throw new AssertionError("service mismatch");
		setDataBits(UNBIND_6);
	}

	void bind7(Map<String,Object> props) {
		setDataBits(ERROR_7);
	}

	void unbind7(Map<String,Object> props) {
		setDataBits(ERROR_7);
	}

	void bind7(TBCService ce) {
		check(ce);
		setDataBits(BIND_7);
	}

	void unbind7(TBCService ce) {
		check(ce);
		setDataBits(UNBIND_7);
	}

	void bind8(ComponentEnabler ce) {
		setDataBits(ERROR_8);
	}

	void unbind8(ComponentEnabler ce) {
		setDataBits(ERROR_8);
	}

	void bind8(ComponentServiceObjects<ComponentEnabler> cso) {
		check(cso);
		setDataBits(BIND_8);
	}

	void unbind8(ComponentServiceObjects<ComponentEnabler> cso) {
		check(cso);
		setDataBits(UNBIND_8);
	}

	private void check(ServiceReference< ? > sr) {
		if (sr == null)
			throw new AssertionError("null");
	}

	private void check(TBCService ce) {
		if (ce == null)
			throw new AssertionError("null");
	}

	private void check(ComponentServiceObjects< ? > cso) {
		if (cso == null)
			throw new AssertionError("null");
	}

	private void check(Map<String,Object> props) {
		if (props == null)
			throw new AssertionError("null");
		if (!(props instanceof Comparable))
			throw new AssertionError("not comparable");
		try {
			props.put("dummyKey", "dummyValue");
			throw new AssertionError("not unmodifiable");
		}
		catch (UnsupportedOperationException e) {
		}
	}

	public Dictionary<String,Object> getProperties() {
		return properties;
	}

	private void setDataBits(long value) {
		if (properties == null) {
			return;
		}
		Object prop = properties.get("config.base.data");
		long data = (prop instanceof Long) ? ((Long) prop).longValue() : 0;
		properties.put("config.base.data", Long.valueOf(data | value));
	}

	public ComponentContext getComponentContext() {
		return ctxt;
	}
}
