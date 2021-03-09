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

package org.osgi.test.cases.serviceloader.serviceclient;

import java.util.Hashtable;

import junit.framework.TestCase;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.serviceloader.export.TestBridge;
import org.osgi.test.cases.serviceloader.spi.ColorProvider;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 
 */
public class ColorProviderClient implements BundleActivator, TestBridge {
    private BundleContext context;

    public ColorProviderClient() {

    }

    /**
     * 134.4.2 look up the Provider from the service registry.
     * 
     * @see org.osgi.test.cases.serviceloader.export.TestBridge#run()
     */
    public void run(String result) throws Exception {
        ServiceReference<ColorProvider> reference = this.context.getServiceReference(ColorProvider.class);
        TestCase.assertNotNull(reference);
        TestCase.assertEquals("134.5.1 service missing sp.provider.url property", "META-INF/services/org.osgi.test.cases.serviceloader.spi.ColorProvider",
                reference.getProperty("spi.provider.url"));
        TestCase.assertEquals("134.5.1 service missing implementation property", "TIBCO",
                reference.getProperty("implementation"));

        ColorProvider service = this.context.getService(reference);
        TestCase.assertNotNull(service);

        TestCase.assertEquals("green", service.getColor());

    }

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        this.context = context;

        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put("test", "serviceclient");

        context.registerService(TestBridge.class, this, properties);
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        // TODO Auto-generated method stub

    }

}
