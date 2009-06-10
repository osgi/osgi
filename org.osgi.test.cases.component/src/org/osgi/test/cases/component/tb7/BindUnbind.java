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

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.ComponentEnabler;

public class BindUnbind implements BaseService {
  private Dictionary properties = new Properties();
  private ComponentContext ctxt;
  private static final int BIND_SR = 1 << 0;
  private static final int UNBIND_SR = 1 << 1;
  private static final int BIND_CE = 1 << 2;
  private static final int UNBIND_CE = 1 << 3;
  private static final int BIND_CE_MAP = 1 << 4;
  private static final int UNBIND_CE_MAP = 1 << 5;

  protected void activate(ComponentContext ctxt) {
    this.ctxt = ctxt;
    Dictionary props = ctxt.getProperties();
    Enumeration en = props.keys();
    while (en.hasMoreElements()) {
      Object key = en.nextElement();
      properties.put(key, props.get(key));
    }
  }

  protected void deactivate(ComponentContext ctxt) {

  }

  protected void bindSr(ServiceReference sr) {
    setDataBits(BIND_SR);
  }

  protected void unbindSr(ServiceReference sr) {
    setDataBits(UNBIND_SR);
  }

  protected void bindCe(ComponentEnabler ce) {
    setDataBits(BIND_CE);
  }

  protected void unbindCe(ComponentEnabler ce) {
    setDataBits(UNBIND_CE);
  }

  protected void bindCeMap(ComponentEnabler ce, Map props) {
    setDataBits(BIND_CE_MAP);
  }

  protected void unbindCeMap(ComponentEnabler ce, Map props) {
    setDataBits(UNBIND_CE_MAP);
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
