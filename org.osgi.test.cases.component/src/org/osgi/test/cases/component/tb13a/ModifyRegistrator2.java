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
package org.osgi.test.cases.component.tb13a;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.ComponentContextExposer;

public class ModifyRegistrator2 implements ComponentContextExposer {
	private Dictionary			properties		= new Properties();
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
		Dictionary props = ctxt.getProperties();
		Enumeration en = props.keys();
		while (en.hasMoreElements()) {
			Object key = en.nextElement();
			properties.put(key, props.get(key));
		}
		log(properties.get("component.name") + " activate");
		setDataBits(ACTIVATE);
	}

	protected void deactivate(ComponentContext ctxt) {
		log(properties.get("component.name") + " deactivate");
		setDataBits(DEACTIVATE);
	}

	protected void modified() {
		log(properties.get("component.name") + " modified");
		setDataBits(MODIFIED);
	}

	protected void mod() {
		log(properties.get("component.name") + " mod");
		setDataBits(MOD);
	}

	protected void modCc(ComponentContext ctxt) {
		log(properties.get("component.name") + " modCc");
		setDataBits(MOD_CC);
	}

	protected void modBc(BundleContext bc) {
		log(properties.get("component.name") + " modBc");
		setDataBits(MOD_BC);
	}

	protected void modMap(Map props) {
		log(properties.get("component.name") + " modMap");
		setDataBits(MOD_MAP);
	}

	protected void modCcBcMap(ComponentContext ctxt, BundleContext bc, Map props) {
		log(properties.get("component.name") + " modCcBcMap");
		setDataBits(MOD_CC_BC_MAP);
	}

	protected void throwException(ComponentContext ctxt) {
		log(properties.get("component.name") + " throwException");
    throw new RuntimeException("Test method throwException(ComponentContext) is called!");
  }

	public Dictionary getProperties() {
		return properties;
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

	private static void log(String message) {
		System.out.println(message);
	}

}
