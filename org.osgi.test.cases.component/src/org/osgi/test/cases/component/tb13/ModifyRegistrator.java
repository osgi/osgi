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
package org.osgi.test.cases.component.tb13;

import java.util.Dictionary;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.ComponentContextExposer;

public class ModifyRegistrator implements ComponentContextExposer {
	private Dictionary properties;
	private ComponentContext ctxt;
	private static final int MODIFIED = 1 << 0;
	private static final int MOD = 1 << 1;
	private static final int MOD_CC = 1 << 2;
	private static final int MOD_BC = 1 << 3;
	private static final int MOD_MAP = 1 << 4;
	private static final int MOD_CC_BC_MAP = 1 << 5;
	private static final int ACTIVATE = 1 << 6;
	private static final int DEACTIVATE = 1 << 7;

	protected void activate(ComponentContext ctxt) {
		this.ctxt = ctxt;
		properties = ctxt.getProperties();
		setDataBits(ACTIVATE);
	}

	protected void deactivate(ComponentContext ctxt) {
		setDataBits(DEACTIVATE);
	}

	protected void modified() {
		setDataBits(MODIFIED);
	}

	protected void mod() {
		setDataBits(MOD);
	}

	protected void modCc(ComponentContext ctxt) {
		setDataBits(MOD_CC);
	}

	protected void modBc(BundleContext bc) {
		setDataBits(MOD_BC);
	}

	protected void modMap(Map props) {
		setDataBits(MOD_MAP);
	}

	protected void modCcBcMap(ComponentContext ctxt, BundleContext bc, Map props) {
		setDataBits(MOD_CC_BC_MAP);
	}

	protected void throwException(ComponentContext ctxt) {
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
}
