/*
 * Copyright (c) 2011 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.serviceloader.junit;

import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.OSGiTestCase;

/**
 * TODO Test cases to write:
 * <ul>
 * <li> 134.3.2 provide API in two different versions and have client and server wire to different versions. Ensure that the mediator does not wire them
 * <li> 134.3.3 stop the provider bundle and ensure that the client does not find the provider
 * <li> 134.3.4 client must not see a provider it is not wired to
 * <li> 134.4.2 create service provider w/ advertisement but w/o publishing, ensure client does not find it
 * <li> 134.4.2 different publishing scenarios with custom properties for the OSGi service, wild cards in capability, etc., uses constraints
 * <li> 134.4.2 write a client that uses the service provider from the OSGi registry directly
 * <li> 134.5.1 verify registered provider service
 * </ul>
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */
public class ServiceLoaderServiceClientTest extends OSGiTestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Bundle spiBundle = install("spi.jar");
		spiBundle.start();
	}

    /**
     * 134.5.1 client using provider as OSGi service
     * 
     * @throws Exception
     */
    public void testServiceClient() throws Exception {
        Bundle implBundle = install("impl.jar");
        implBundle.start();

    	Bundle client = install("serviceclient.jar");
    	client.start();
    	
    	Collection<ServiceReference<TestBridge>> refs = getContext().getServiceReferences(TestBridge.class, "(test=serviceclient)");
    	assertNotNull(refs);
    	assertEquals(1, refs.size());
    	
        TestBridge service = getContext().getService(refs.iterator().next());
        assertNotNull("client bundle did not register its service", service);

        service.run();
    }
}
