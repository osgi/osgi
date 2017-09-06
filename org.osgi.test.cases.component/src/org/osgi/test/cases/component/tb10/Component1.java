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
package org.osgi.test.cases.component.tb10;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.ComponentContextExposer;


public class Component1 implements BaseService, ComponentContextExposer {
  protected static int deactCount = 0;
  private int deactPos = -1;
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
    deactPos = deactCount++;
    properties.put("config.base.data", Integer.valueOf(deactPos));
  }

	public Dictionary<String,Object> getProperties() {
    return properties;
  }

  public int getDeactivationPos() {
    return deactPos;
  }

  public ComponentContext getComponentContext() {
    return ctxt;
  }
}
