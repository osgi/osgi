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
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.ComponentContextExposer;

public class ActDeactComponent implements ComponentContextExposer {
	private Dictionary			properties;
  private ComponentContext ctxt;
	public static final int		ACTIVATE_CC			= 1 << 0;
	public static final int		DEACTIVATE_CC		= 1 << 1;
	public static final int		ACT					= 1 << 2;
	public static final int		DEACT				= 1 << 3;
	public static final int		ACT_CC				= 1 << 4;
	public static final int		DEACT_CC			= 1 << 5;
	public static final int		ACT_BC				= 1 << 6;
	public static final int		DEACT_BC			= 1 << 7;
	public static final int		ACT_MAP				= 1 << 8;
	public static final int		DEACT_MAP			= 1 << 9;
	public static final int		ACT_CC_BC_MAP		= 1 << 10;
	public static final int		DEACT_CC_BC_MAP		= 1 << 11;
	public static final int		DEACT_INT			= 1 << 12;
	public static final int		DEACT_CC_BC_MAP_INT	= 1 << 13;

  protected void activate(ComponentContext ctxt) {
    this.ctxt = ctxt;
		properties = new Properties();
		Dictionary props = ctxt.getProperties();
		Enumeration en = props.keys();
		while (en.hasMoreElements()) {
			Object key = en.nextElement();
			properties.put(key, props.get(key));
		}
		log(getComponentName() + " activate");
    setDataBits(ACTIVATE_CC);
  }

  protected void deactivate(ComponentContext ctxt) {
		log(getComponentName() + " deactivate");
    setDataBits(DEACTIVATE_CC);
  }

  protected void act() {
    properties = new Properties();
    properties.put(ComponentConstants.COMPONENT_NAME, getName());
		log(getComponentName() + " act");
    setDataBits(ACT);
  }

  protected void deact() {
		log(getComponentName() + " deact");
    setDataBits(DEACT);
  }

  protected void actCc(ComponentContext ctxt) {
    this.ctxt = ctxt;
		properties = new Properties();
		Dictionary props = ctxt.getProperties();
		Enumeration en = props.keys();
		while (en.hasMoreElements()) {
			Object key = en.nextElement();
			properties.put(key, props.get(key));
		}
		log(getComponentName() + " actCc");
    setDataBits(ACT_CC);
  }

  protected void deactCc(ComponentContext ctxt) {
		log(getComponentName() + " deactCc");
    setDataBits(DEACT_CC);
  }

  protected void actBc(BundleContext bc) {
    properties = new Properties();
    properties.put(ComponentConstants.COMPONENT_NAME, getName());
		log(getComponentName() + " actBc");
    setDataBits(ACT_BC);
  }

  protected void deactBc(BundleContext bc) {
		log(getComponentName() + " deactBc");
    setDataBits(DEACT_BC);
  }

  protected void actMap(Map props) {
    properties = new Properties();
    Iterator it = props.keySet().iterator();
    while (it.hasNext()) {
      Object key = it.next();
      properties.put(key, props.get(key));
    }
		log(getComponentName() + " actMap");
    setDataBits(ACT_MAP);
  }

  protected void deactMap(Map props) {
		log(getComponentName() + " deactMap");
    setDataBits(DEACT_MAP);
  }

	protected void actCcBcMap(ComponentContext ctxt, BundleContext bc, Map map) {
    this.ctxt = ctxt;
		properties = new Properties();
		Dictionary props = ctxt.getProperties();
		Enumeration en = props.keys();
		while (en.hasMoreElements()) {
			Object key = en.nextElement();
			properties.put(key, props.get(key));
		}
		log(getComponentName() + " actCcBcMap");
    setDataBits(ACT_CC_BC_MAP);
  }

  protected void deactCcBcMap(ComponentContext ctxt, BundleContext bc, Map props) {
		log(getComponentName() + " deactCcBcMap");
    setDataBits(DEACT_CC_BC_MAP);
  }

  protected void deactInt(int reason) {
		log(getComponentName() + " deactInt");
    setDataBits(DEACT_INT | reason << 16);
  }

  protected void deactCcBcMapInt(ComponentContext ctxt, BundleContext bc,
      Map props, int reason) {
		log(getComponentName() + " deactCcBcMapInt");
    setDataBits(DEACT_CC_BC_MAP_INT | reason << 16);
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

  // Successors should override
  public String getName() {
    return "name.unknown";
  }

	private String getComponentName() {
		return properties.get("component.name") + "@"
				+ Integer.toHexString(System.identityHashCode(this));
	}

  public ComponentContext getComponentContext() {
    return ctxt;
  }

	private static void log(String message) {
		System.out.println(message);
	}
}
