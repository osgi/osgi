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

package org.osgi.test.cases.serviceloader.secure.client;

import java.util.Hashtable;
import java.util.ServiceLoader;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.serviceloader.secure.export.TestBridge;
import org.osgi.test.cases.serviceloader.secure.spi.ColorProvider;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */
public class ColorProviderClient implements BundleActivator, TestBridge {
	
    public ColorProviderClient() {

    }

    /**
     * Use the ServiceLoader to obtain the provider.
     * 
     * @see org.osgi.test.cases.serviceloader.secure.export.TestBridge#run(java.lang.String)
     */
	public void run(String result) throws Exception {
		System.out.println("client run - begin");

		ServiceLoader<ColorProvider> sl = ServiceLoader.load(ColorProvider.class);
		TestCase.assertNotNull(sl);
		
		if (result != null) {
			TestCase.assertTrue("no ColorProvider found", sl.iterator().hasNext());

			ColorProvider provider = sl.iterator().next();
			TestCase.assertNotNull(provider);

			TestCase.assertEquals(result, provider.getColor());
		} else {
			// expecting no service provider to be found
			TestCase.assertFalse("expected no provider to be found", sl.iterator().hasNext());
		}
		
		System.out.println("client run - end");
	}

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
    	System.out.println("client bundle started");
    	
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put("test", "client");

        context.registerService(TestBridge.class, this, properties);
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        System.out.println("client bundle stopped");
    }

}
