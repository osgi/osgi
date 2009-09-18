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
package org.osgi.test.cases.component.tb11;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.ComponentContextExposer;


public class TargetProperties implements BaseService, ComponentContextExposer {
  private Dictionary properties;
  private ComponentContext ctxt;
  private ServiceRegistration sr;

  protected void activate(ComponentContext ctxt) {
    this.ctxt = ctxt;
    properties = ctxt.getProperties();

    Object prop = properties.get("serial.num");
    if (prop != null) {
      Properties serviceProps = new Properties();
      serviceProps.put("serial.num", prop);
      sr = ctxt.getBundleContext().registerService(getClass().getName(), this, serviceProps);
    }
  }

  protected void deactivate(ComponentContext ctxt) {
    if (sr != null) {
      sr.unregister();
      sr = null;
    }
  }

  public Dictionary getProperties() {
    return properties;
  }

  public ComponentContext getComponentContext() {
    return ctxt;
  }
}
