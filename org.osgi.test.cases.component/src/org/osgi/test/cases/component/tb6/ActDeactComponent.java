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
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.ComponentContextExposer;

public class ActDeactComponent implements ComponentContextExposer {
  private Dictionary properties;
  private ComponentContext ctxt;
  private static final int ACTIVATE_CC = 1 << 0;
  private static final int DEACTIVATE_CC = 1 << 1;
  private static final int ACT = 1 << 2;
  private static final int DEACT = 1 << 3;
  private static final int ACT_CC = 1 << 4;
  private static final int DEACT_CC = 1 << 5;
  private static final int ACT_BC = 1 << 6;
  private static final int DEACT_BC = 1 << 7;
  private static final int ACT_MAP = 1 << 8;
  private static final int DEACT_MAP = 1 << 9;
  private static final int ACT_CC_BC_MAP = 1 << 10;
  private static final int DEACT_CC_BC_MAP = 1 << 11;
  private static final int DEACT_INT = 1 << 12;
  private static final int DEACT_CC_BC_MAP_INT = 1 << 13;

  protected void activate(ComponentContext ctxt) {
    this.ctxt = ctxt;
    properties = ctxt.getProperties();
    setDataBits(ACTIVATE_CC);
  }

  protected void deactivate(ComponentContext ctxt) {
    setDataBits(DEACTIVATE_CC);
  }

  protected void act() {
    properties = new Properties();
    properties.put(ComponentConstants.COMPONENT_NAME, getName());
    setDataBits(ACT);
  }

  protected void deact() {
    setDataBits(DEACT);
  }

  protected void actCc(ComponentContext ctxt) {
    this.ctxt = ctxt;
    properties = ctxt.getProperties();
    setDataBits(ACT_CC);
  }

  protected void deactCc(ComponentContext ctxt) {
    setDataBits(DEACT_CC);
  }

  protected void actBc(BundleContext bc) {
    properties = new Properties();
    properties.put(ComponentConstants.COMPONENT_NAME, getName());
    setDataBits(ACT_BC);
  }

  protected void deactBc(BundleContext bc) {
    setDataBits(DEACT_BC);
  }

  protected void actMap(Map props) {
    properties = new Properties();
    Iterator it = props.keySet().iterator();
    while (it.hasNext()) {
      Object key = it.next();
      properties.put(key, props.get(key));
    }
    setDataBits(ACT_MAP);
  }

  protected void deactMap(Map props) {
    setDataBits(DEACT_MAP);
  }

  protected void actCcBcMap(ComponentContext ctxt, BundleContext bc, Map props) {
    this.ctxt = ctxt;
    properties = ctxt.getProperties();
    setDataBits(ACT_CC_BC_MAP);
  }

  protected void deactCcBcMap(ComponentContext ctxt, BundleContext bc, Map props) {
    setDataBits(DEACT_CC_BC_MAP);
  }

  protected void deactInt(int reason) {
    setDataBits(DEACT_INT | reason << 16);
  }

  protected void deactCcBcMapInt(ComponentContext ctxt, BundleContext bc,
      Map props, int reason) {
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

  public ComponentContext getComponentContext() {
    return ctxt;
  }
}
