/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.test.cases.remoteserviceadmin.junit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ImportReference;
import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.test.cases.remoteserviceadmin.common.A;

/**
 * Use RSA service to register a service in a child framework and then import
 * the same service on the parent framework. This test does not explicitly use
 * the discovery, but manually conveys the endpoint information.
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @version 1.0.0
 */
public class RemoteServiceAdminTest extends MultiFrameworkTestCase {
	private static final String JUNIT_FRAMEWORK = "junit.framework;version=\"3.8.2\"";

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.junit.MultiFrameworkTestCase#getConfiguration()
	 */
	public Map<String, String> getConfiguration() {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
		
		//make sure that the server framework System Bundle exports the interfaces
        String systemPackagesXtra = JUNIT_FRAMEWORK;
        configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPackagesXtra);
        configuration.put("osgi.console", "1112");
		return configuration;
	}

	/**
	 * Sets up a child framework in which a service is exported. In the parent framework the
	 * EndpointDescription is passed to the RSA service to import the service from the child
	 * framework. This manual step bypasses Discovery, which would normally do the transport
	 * between the two frameworks.
	 */
	public void testExportImportManually() throws Exception {
		verifyFramework();
		
		//
		// install test bundle in child framework
		//
		BundleContext childContext = getFramework().getBundleContext();
		
		Bundle tb2Bundle = installBundle(childContext, "/tb2.jar");
		assertNotNull(tb2Bundle);
		
		// start test bundle in child framework
		// this will run the test in the child framework and fail
		tb2Bundle.start();
		
		//
		// find the RSA in the parent framework and import the
		// service
		//
		ServiceReference rsaRef = getContext().getServiceReference(RemoteServiceAdmin.class.getName());
		Assert.assertNotNull(rsaRef);
		RemoteServiceAdmin rsa = (RemoteServiceAdmin) getContext().getService(rsaRef);
		Assert.assertNotNull(rsa);
		
		// reconstruct the endpoint description
		EndpointDescription endpoint = reconstructEndpoint();
		
		// import the service
		ImportRegistration importReg = rsa.importService(endpoint);
		assertNotNull(importReg);
		assertNull(importReg.getException());
		
		ImportReference importRef = importReg.getImportReference();
		assertNotNull(importRef);
		ServiceReference sref = importRef.getImportedService();
		assertNotNull(sref);
		assertEquals("has been overridden", sref.getProperty("mykey"));
		assertNotNull(sref.getProperty(RemoteConstants.SERVICE_IMPORTED));
		
		A serviceA = (A) getContext().getService(sref);
		assertNotNull(serviceA);
		assertEquals("this is A", serviceA.getA());
		
		getContext().ungetService(sref);
		
		//
		// test remove
		//
		tb2Bundle.stop();
		
		//Marc Schaaf: To really unimport the service the ImportRegistration needs 
		// to be closed. This is normally the Topology Manager which should decide 
		// if the service is not needed anymore. 
		importReg.close();
		
		assertFalse(rsa.getImportedEndpoints().contains(importRef));
		assertNull(getContext().getServiceReference(A.class.getName()));
	}

	/**
	 * @return
	 * @throws IOException 
	 */
	private EndpointDescription reconstructEndpoint() throws IOException {
		String propstr = System.getProperty("RSA_TCK.EndpointDescription_0");
		
		// see org.osgi.test.cases.remoteserviceadmin.tb2.Activator#exportEndpointDescription()
		// decode byte[] from hex
		byte[] ba = new byte[propstr.length()/2];
		
		for (int x=0; x < ba.length; ++x) {
            int sp = x*2;
            int a = Integer.parseInt(""+propstr.charAt(sp),16);
            int b = Integer.parseInt(""+propstr.charAt(sp+1),16);
            ba[x] = (byte)(a*16 + b);
		}
		
		ByteArrayInputStream bis = new ByteArrayInputStream(ba);
		ObjectInputStream ois = new ObjectInputStream(bis);
		
		Map<String,Object> props = null;
		try {
			props = (Map<String, Object>)ois.readObject();
		} catch (ClassNotFoundException e) {e.printStackTrace();}
		
		assert(props!=null);
		
		return new EndpointDescription(props);
	}
	


}
