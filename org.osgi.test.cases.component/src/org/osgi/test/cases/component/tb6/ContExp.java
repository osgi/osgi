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
package org.osgi.test.cases.component.tb6;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.ComponentContextExposer;

public class ContExp implements ComponentContextExposer {
  private ComponentContext ctxt;
	Dictionary<String,Object>	properties;
	public static final int		ACTIVATE	= 1 << 0;
	public static final int		DEACTIVATE	= 1 << 1;

  protected void activate(ComponentContext ctxt) {
    this.ctxt = ctxt;
		properties = new Hashtable<>();
		Dictionary<String,Object> props = ctxt.getProperties();
		Enumeration<String> en = props.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			properties.put(key, props.get(key));
		}
		log(getComponentName() + " activate");
		setDataBits(ACTIVATE);
  }

	protected void deactivate(int reason) {
		log(getComponentName() + " deactivate");
		setDataBits(DEACTIVATE | reason << 16);
  }
  
	private void setDataBits(int value) {
		if (properties == null) {
			return;
		}
		Object prop = properties.get("config.base.data");
		int data = (prop instanceof Integer) ? ((Integer) prop).intValue() : 0;
		properties.put("config.base.data", new Integer(data | value));
	}

  public ComponentContext getComponentContext() {
    return ctxt;
  }

	public Dictionary<String,Object> getProperties() {
    return properties;
  }

	private String getComponentName() {
		return properties.get("component.name") + "@"
				+ System.identityHashCode(this);
	}

	private static void log(String message) {
		System.out.println(message);
	}
}
