/*
 * Copyright (c) 1997-2009 ProSyst Software GmbH. All Rights Reserved.
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
package org.osgi.test.cases.component.tb7;

import static org.osgi.test.cases.component.service.DSTestConstants.*;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.ComponentEnabler;

public class BindUnbind implements BaseService {
	private Dictionary<String,Object>	properties	= new Hashtable<>();
  private ComponentContext ctxt;

  protected void activate(ComponentContext ctxt) {
    this.ctxt = ctxt;
		Dictionary<String,Object> props = ctxt.getProperties();
		Enumeration<String> en = props.keys();
    while (en.hasMoreElements()) {
			String key = en.nextElement();
      properties.put(key, props.get(key));
    }
  }

  protected void deactivate(ComponentContext ctxt) {

  }

	protected void bindSr(ServiceReference<ComponentEnabler> sr) {
		setDataBits(BIND_1);
  }

	protected void unbindSr(ServiceReference<ComponentEnabler> sr) {
		setDataBits(UNBIND_1);
  }

  protected void bindCe(ComponentEnabler ce) {
		setDataBits(BIND_2);
  }

  protected void unbindCe(ComponentEnabler ce) {
		setDataBits(UNBIND_2);
  }

	protected void bindCeMap(ComponentEnabler ce, Map<String,Object> props) {
		setDataBits(BIND_3);
  }

	protected void unbindCeMap(ComponentEnabler ce, Map<String,Object> props) {
		setDataBits(UNBIND_3);
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
