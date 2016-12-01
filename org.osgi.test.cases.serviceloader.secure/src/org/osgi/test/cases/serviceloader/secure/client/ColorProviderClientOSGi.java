/*
 * Copyright (c) 2012 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.serviceloader.secure.client;

import java.util.Hashtable;

import junit.framework.TestCase;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.serviceloader.secure.export.TestBridge;
import org.osgi.test.cases.serviceloader.secure.spi.ColorProvider;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 
 */
public class ColorProviderClientOSGi implements BundleActivator, TestBridge {
	private BundleContext context;
	
    public ColorProviderClientOSGi() {

    }

    /**
     * Use the ServiceLoader to obtain the provider.
     * 
     * @see org.osgi.test.cases.serviceloader.secure.export.TestBridge#run(java.lang.String)
     */
	public void run(String result) throws Exception {
		System.out.println("client run - begin");

		ServiceReference<ColorProvider> ref = context.getServiceReference(ColorProvider.class);
		if (result == null) {
			TestCase.assertNull("expected not to find a service provider", ref);
		} else {
			TestCase.assertNotNull("expected to find one service provider", ref);

			ColorProvider provider = context.getService(ref);

			try {
				TestCase.assertNotNull(provider);

				TestCase.assertEquals(result, provider.getColor());
			} finally {
				context.ungetService(ref);
			}
		}
		
		System.out.println("client run - end");
	}

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
    	this.context = context;
    	
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
