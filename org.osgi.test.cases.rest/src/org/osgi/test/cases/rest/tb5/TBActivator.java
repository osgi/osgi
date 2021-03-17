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
package org.osgi.test.cases.rest.tb5;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.rest.RestApiExtension;
import org.restlet.resource.ServerResource;

/**
 * Test bundle registering REST API Extension with relative URI.
 *
 * @author Petia Sotirova
 */
public class TBActivator extends ServerResource implements BundleActivator, RestApiExtension {

	private ServiceRegistration<RestApiExtension> sReg;

  public void start(BundleContext context) throws Exception {
    Dictionary<String, Object> properties = new Hashtable<String, Object>();
    properties.put(RestApiExtension.URI_PATH, "contributions/extension");
    properties.put(RestApiExtension.NAME, "REST TCK Extension");
    properties.put("restlet", TBActivator.class);

		sReg = context.registerService(RestApiExtension.class, this,
				properties);
  }

  public void stop(BundleContext context) throws Exception {
    if (sReg != null) {
      sReg.unregister();
      sReg = null;
    }
  }

}
