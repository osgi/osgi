/*
 * Copyright (c) 2012 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.serviceloader.client;

import java.util.Collection;
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
public class ColorProviderClientOSGi implements BundleActivator, TestBridge {
	private BundleContext context;
	
    public ColorProviderClientOSGi() {

    }

    /**
     * Use the ServiceLoader to obtain the provider.
     * 
     * @see org.osgi.test.cases.serviceloader.export.TestBridge#run(java.lang.String)
     */
	public void run(String result) throws Exception {
		System.out.println("client run - begin");

		String filter = "(type=two)";
		Collection<ServiceReference<ColorProvider>> refs = context.getServiceReferences(ColorProvider.class, filter);
		TestCase.assertNotNull(refs);
		TestCase.assertEquals("expected 1 provider", 1, refs.size());

		ServiceReference<ColorProvider> ref = refs.iterator().next();
		ColorProvider provider = context.getService(ref);
		
		try {
			TestCase.assertNotNull(provider);

			TestCase.assertEquals(result, provider.getColor());
		} finally {
			context.ungetService(ref);
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
