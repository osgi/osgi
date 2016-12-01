/*
 * Copyright (c) 2012 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.serviceloader.client;

import java.util.Hashtable;
import java.util.ServiceLoader;

import junit.framework.TestCase;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.serviceloader.export.TestBridge;
import org.osgi.test.cases.serviceloader.spi.ColorProvider;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 
 */
public class ColorProviderClient2 implements BundleActivator, TestBridge {
    public ColorProviderClient2() {

    }

    /**
     * Use the ServiceLoader to obtain the provider.
     * 
     * @see org.osgi.test.cases.serviceloader.export.TestBridge#run(java.lang.String)
     */
	public void run(String result) throws Exception {
		System.out.println("client run - begin");
		
		ServiceLoader<ColorProvider> sl = ServiceLoader.load(ColorProvider.class);
		TestCase.assertNotNull(sl);
		TestCase.assertTrue("no ColorProvider found", sl.iterator().hasNext());

		int count = 0;
		for (ColorProvider cp : sl) {
			count++;
			String color = cp.getColor();
			System.out.println(color);
		}
		
		TestCase.assertEquals("expected exactly " + result + " provider(s)", result, ""+count);
		
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
