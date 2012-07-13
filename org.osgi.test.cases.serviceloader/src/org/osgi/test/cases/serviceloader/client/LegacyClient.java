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
import org.osgi.test.cases.serviceloader.junit.TestBridge;
import org.osgi.test.cases.serviceloader.spi.ColorProvider;
import org.osgi.test.cases.serviceloader.spi.ShapeProvider;

/**
 * A client using the {@link ServiceLoader} API directly to gain access to multiple service provider types.
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */
public class LegacyClient implements BundleActivator, TestBridge {
	private BundleContext context;

	@Override
	public void run(String expectedResult) throws Exception {
		System.out.println("client run - begin");

		ServiceLoader<ColorProvider> slColorProvider = ServiceLoader.load(ColorProvider.class);
		TestCase.assertNotNull(slColorProvider);
		TestCase.assertTrue("no ColorProvider found", slColorProvider.iterator().hasNext());
		
		ColorProvider cp = slColorProvider.iterator().next();
		System.out.println("  color = " + cp.getColor());

		ServiceLoader<ShapeProvider> slShapeProvider = ServiceLoader.load(ShapeProvider.class);
		TestCase.assertNotNull(slShapeProvider);
		TestCase.assertTrue("no ShapeProvider found", slShapeProvider.iterator().hasNext());
		
		ShapeProvider sp = slShapeProvider.iterator().next();
		System.out.println("  shape = " + sp.getShape());
		
		System.out.println("client run - end");
	}

	@Override
	public void start(BundleContext context) throws Exception {
    	this.context = context;
    	
    	System.out.println("client bundle started");
    	
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put("test", "client");

        context.registerService(TestBridge.class, this, properties);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
        System.out.println("client bundle stopped");
	}

}
