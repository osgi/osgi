package org.osgi.test.cases.jmx.junit;

import java.io.*;

import org.osgi.framework.*;
import org.osgi.jmx.framework.*;

public class ServiceStateMBeanTestCase extends MBeanGeneralTestCase {

	private Bundle testBundle1;
	private Bundle testBundle2;
	private ServiceStateMBean ssMBean;
	
	private static String expectedInterface = "org.osgi.test.cases.jmx.tb2.api.HelloSayer";
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		testBundle2 = super.install("tb2.jar");
		testBundle2.start();
		
		testBundle1 = super.install("tb1.jar");
		testBundle1.start();
	
		super.waitForRegistering(createObjectName(ServiceStateMBean.OBJECTNAME));
		ssMBean = getMBeanFromServer(ServiceStateMBean.OBJECTNAME,
				ServiceStateMBean.class);	
		
	}
	
	
	public void testGetServiceInterfaces() throws IOException {
		assertNotNull(ssMBean);
		
		Long serviceId = getServiceId(expectedInterface); 
		boolean found = false;
		for(String serviceInterface : ssMBean.getObjectClass(serviceId)) {
			if(serviceInterface.equals(expectedInterface)) {
				found = true;
				break;
			}
		}
		
		assertTrue("the service with interface " + expectedInterface + " and serviceID " + serviceId + " could not be found.", found);
	}


	private Long getServiceId(String expectedInterface) {
		ServiceReference ref = getContext().getServiceReference(expectedInterface);
		Long serviceId = (Long)ref.getProperty("service.id");
		return serviceId;
	}

	/**
	 * Answer the bundle identifier of the bundle which registered the service
	 * 
	 * @param serviceId
	 *            - the identifier of the service
	 * @return the identifier for the bundle
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the service indicated does not exist
	 */
	public void testGetBundle() throws IOException {
		assertNotNull(ssMBean);
		
		long expectedBundleId = testBundle2.getBundleId();
		long bundleID = ssMBean.getBundleIdentifier(getServiceId(expectedInterface));
		assertTrue("getBUndle for serviceId " + expectedInterface
				+ " returned the wrong bundleId: " + bundleID,
				expectedBundleId == bundleID);
	}

	/**
	 * Answer the map of credentials associated with this service
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiProperties for the details of the TabularType
	 *      <p>
	 *      For each propery entry, the following row is returned
	 *      <ul>
	 *      <li>Property Key - the string key</li>
	 *      <li>Property Value - the stringified version of the property value</li>
	 *      <li>Property Value Type - the type of the property value</li>
	 *      </ul>
	 * 
	 * @param serviceId
	 *            - the identifier of the service
	 * @return the table of credentials. These include the standard mandatory
	 *         service.id and objectClass credentials as defined in the
	 *         <code>org.osgi.framework.Constants</code> interface
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the service indicated does not exist
	 */
	public void testGetProperties() throws IOException {
		assertNotNull(ssMBean);
		long serviceId = getServiceId(expectedInterface);
		
		assertTrue("did not get any properties for service "
				+ expectedInterface,
				ssMBean.getProperties(serviceId).size() > 0);
	}

	/**
	 * Answer the service state of the system in tabular form
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiService for the details of the TabularType
	 *      <p>
	 *      Each row of the returned table represents a single service. For each
	 *      service, the following row is returned
	 *      <ul>
	 *      <li>identifier - long</li>
	 *      <li>interfaces - String[]</li>
	 *      <li>bundle - long</li>
	 *      <li>using bundles - long[]</li>
	 *      </ul>
	 *      <p>
	 *      See <link>OSGiService</link> for the precise definition of the
	 *      CompositeType that defines each row of the table.
	 * 
	 * @return the tabular respresentation of the service state
	 * @throws IOException 
	 */
	public void testGetServices() throws IOException {
		assertNotNull(ssMBean);
		assertTrue("did not get any properties for service the " +
				"service-state of the system ",
				ssMBean.listServices().size() > 0);
	}

	/**
	 * Answer the list of identifers of the bundles that use the service
	 * 
	 * @param serviceId
	 *            - the identifier of the service
	 * @return the list of bundle identifiers
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the service indicated does not exist
	 */
	public void testGetUsingBundles() throws IOException {
		assertNotNull(ssMBean);
		long expectedBundleId = testBundle1.getBundleId();
		boolean found = false;
		for(long bundleId : ssMBean.getUsingBundles(getServiceId(expectedInterface))) {
			if(bundleId == expectedBundleId) {
				found = true;
				break;
			}
		}
		
		assertTrue(
				"tb1 uses interface " + expectedInterface
				+ " from tb2. however, the getUsingBundles() " +
						"call did not return the bundleId of tb1.",
				found);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(ServiceStateMBean.OBJECTNAME));
	}
}
