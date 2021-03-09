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
package org.osgi.test.cases.component.tb13a;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.ComponentContextExposer;

public class ModifyRegistrator2 implements ComponentContextExposer {
	private Dictionary<String,Object>	properties		= new Hashtable<>();
	private ComponentContext ctxt;
	public static final int		MODIFIED		= 1 << 0;
	public static final int		MOD				= 1 << 1;
	public static final int		MOD_CC			= 1 << 2;
	public static final int		MOD_BC			= 1 << 3;
	public static final int		MOD_MAP			= 1 << 4;
	public static final int		MOD_CC_BC_MAP	= 1 << 5;
	public static final int		ACTIVATE		= 1 << 6;
	public static final int		DEACTIVATE		= 1 << 7;

	protected void activate(ComponentContext ctxt) {
		this.ctxt = ctxt;
		Dictionary<String,Object> props = ctxt.getProperties();
		Enumeration<String> en = props.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			properties.put(key, props.get(key));
		}
		log(getComponentName() + " activate");
		setDataBits(ACTIVATE);
	}

	protected void deactivate(ComponentContext ctxt) {
		log(getComponentName() + " deactivate");
		setDataBits(DEACTIVATE);
	}

	protected void modified() {
		log(getComponentName() + " modified");
		setDataBits(MODIFIED);
	}

	protected void mod() {
		log(getComponentName() + " mod");
		setDataBits(MOD);
	}

	protected void modCc(ComponentContext ctxt) {
		log(getComponentName() + " modCc");
		setDataBits(MOD_CC);
	}

	protected void modBc(BundleContext bc) {
		log(getComponentName() + " modBc");
		setDataBits(MOD_BC);
	}

	protected void modMap(Map<String,Object> props) {
		log(getComponentName() + " modMap");
		setDataBits(MOD_MAP);
	}

	protected void modCcBcMap(ComponentContext ctxt, BundleContext bc,
			Map<String,Object> props) {
		log(getComponentName() + " modCcBcMap");
		setDataBits(MOD_CC_BC_MAP);
	}

	protected void throwException(ComponentContext ctxt) {
		log(getComponentName() + " throwException");
    throw new RuntimeException("Test method throwException(ComponentContext) is called!");
  }

	public Dictionary<String,Object> getProperties() {
		return properties;
	}

	private void setDataBits(int value) {
		if (properties == null) {
			return;
		}
		Object prop = properties.get("config.base.data");
		int data = (prop instanceof Integer) ? ((Integer) prop).intValue() : 0;
		properties.put("config.base.data", Integer.valueOf(data | value));
	}

	public ComponentContext getComponentContext() {
		return ctxt;
	}

	private String getComponentName() {
		return properties.get("component.name") + "@"
				+ Integer.toHexString(System.identityHashCode(this));
	}

	private static void log(String message) {
		System.out.println(message);
	}

}
