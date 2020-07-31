package org.osgi.test.cases.jmx.framework.junit;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.jmx.JmxConstants;
import org.osgi.jmx.framework.ServiceStateMBean;
import org.osgi.service.cm.ManagedServiceFactory;

public class ServiceStateMBeanTestCase extends MBeanGeneralTestCase {

	private Bundle testBundle1;
	private Bundle testBundle2;
	private ServiceStateMBean ssMBean;

	private static String expectedInterface = "org.osgi.test.cases.jmx.framework.tb2.api.HelloSayer";

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

    public void testObjectNameStructure() throws Exception {
        ObjectName queryName = new ObjectName(ServiceStateMBean.OBJECTNAME + ",*");
        Set<ObjectName> names = getMBeanServer().queryNames(queryName, null);
        assertEquals(1, names.size());

        ObjectName name = names.iterator().next();
        Hashtable<String, String> props = name.getKeyPropertyList();

        String type = props.get("type");
        assertEquals("serviceState", type);
        String version = props.get("version");
        assertEquals("1.7", version);
        String framework = props.get("framework");
        assertEquals(getContext().getBundle(0).getSymbolicName(), framework);
        String uuid = props.get("uuid");
        assertEquals(getContext().getProperty(Constants.FRAMEWORK_UUID), uuid);

        assertTrue(name.getKeyPropertyListString().startsWith(
                "type=" + type + ",version=" + version + ",framework=" + framework + ",uuid=" + uuid));
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
		ServiceReference< ? > ref = getContext()
				.getServiceReference(expectedInterface);
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
		TabularData list = ssMBean.getProperties(serviceId);
		assertTrue("did not get any properties for service "
				+ expectedInterface,
				list.size() > 0);
		assertTabularDataStructure(list, "PROPERTIES_TYPE", "Key", new String[] {"Key", "Value", "Type"});
		Collection< ? > values = list.values();
		Iterator< ? > iter = values.iterator();
		boolean found = false;
		while (iter.hasNext()) {
			CompositeData item = (CompositeData) iter.next();
			String key = (String) item.get("Key");
			if (key.equals("service.id")) {
				long foundServiceId = Long.parseLong((String)item.get("Value"));
				assertTrue("service id returned in properties is wrong",serviceId == foundServiceId);
				found = true;
				break;
			}
		}
		assertTrue("service id not found in returned properties", found);
	}

	public void testGetProperty() throws IOException {
	    Long id = getServiceId(ManagedServiceFactory.class.getName());
	    CompositeData data = ssMBean.getProperty(id, "test_key");
	    assertEquals("test_key", data.get(JmxConstants.KEY));
	    assertEquals("test_value", data.get(JmxConstants.VALUE));
	    assertEquals("String", data.get(JmxConstants.TYPE));
	}

	public void testGetServiceIDs() throws Exception {
	    ServiceReference<?>[] refs = getContext().getAllServiceReferences((String) null, null);

	    SortedSet<Long> expectedIDs = new TreeSet<Long>();
	    for (ServiceReference<?> ref : refs) {
	        expectedIDs.add((Long) ref.getProperty(Constants.SERVICE_ID));
	    }

        long[] ids = ssMBean.getServiceIds();
        SortedSet<Long> actualIDs = new TreeSet<Long>();
        for (long id : ids) {
            actualIDs.add(id);
        }
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
	public void testListServices() throws IOException {
		assertNotNull(ssMBean);
		long serviceId = getServiceId(expectedInterface);

		TabularData list = ssMBean.listServices();
		assertTrue("did not get any properties for service the " +
				"service-state of the system ",
				list.size() > 0);
		assertTabularDataStructure(list, "SERVICE_TYPE", "Identifier",
		        new String[] {"Identifier", "BundleIdentifier", "Properties", "objectClass", "UsingBundles"});

		Collection< ? > values = list.values();
		Iterator< ? > iter = values.iterator();
		boolean found = false;
		while (iter.hasNext()) {
			CompositeData item = (CompositeData) iter.next();
			long tempServiceId = ((Long) item.get("Identifier")).longValue();
			if(tempServiceId == serviceId) {
				found = true;
		        assertTestService(item);
			}
		}
		assertTrue("service id not found in returned properties", found);
	}

    private void assertTestService(CompositeData item) {
        assertEquals(Long.valueOf(getServiceId(expectedInterface)),item.get("Identifier"));

        long bundleId = ((Long) item.get("BundleIdentifier")).longValue();
        assertTrue("wrong bundle id is returned", bundleId == testBundle2.getBundleId());
        Long[] usingBundles = (Long[]) item.get("UsingBundles");
        assertTrue("wrong list with using bundles", ((usingBundles != null) && (usingBundles.length == 1) &&
        											 (usingBundles[0].longValue() == testBundle1.getBundleId())));

        TabularData properties = (TabularData) item.get("Properties");
        assertTabularDataStructure(properties, "PROPERTY", JmxConstants.KEY,
                new String[] {JmxConstants.KEY, JmxConstants.VALUE, JmxConstants.TYPE});
        CompositeData oc0 = properties.get(new Object[] {"objectClass"});
        assertEquals("objectClass", oc0.get(JmxConstants.KEY));
        assertEquals("Array of String", oc0.get(JmxConstants.TYPE));
        assertEquals(expectedInterface, oc0.get(JmxConstants.VALUE));

        String[] objectClass = (String[]) item.get("objectClass");
        assertEquals(1, objectClass.length);
        assertEquals(expectedInterface, objectClass[0]);
    }

	public void testListServicesNoFilter() throws Exception {
	    ServiceReference<?>[] refs = getContext().getServiceReferences((String) null, null);
	    TabularData list1 = ssMBean.listServices();
	    TabularData list2 = ssMBean.listServices(null, null);

	    assertEquals(refs.length, list1.size());
	    assertEquals(refs.length, list2.size());
	}

	public void testListServicesClassName() throws IOException {
	    TabularData serviceList = ssMBean.listServices(expectedInterface, null);
	    assertEquals(1, serviceList.size());

	    assertTabularDataStructure(serviceList, "SERVICE_TYPE", "Identifier",
                new String[] {"Identifier", "BundleIdentifier", "Properties", "objectClass", "UsingBundles"});
	    assertTestService((CompositeData) serviceList.values().iterator().next());
	}

	public void testListServicesFilter() throws IOException {
        TabularData serviceList = ssMBean.listServices(null, "(objectClass=" + expectedInterface + ")");
        assertEquals(1, serviceList.size());

        assertTabularDataStructure(serviceList, "SERVICE_TYPE", "Identifier",
                new String[] {"Identifier", "BundleIdentifier", "Properties", "objectClass", "UsingBundles"});
        assertTestService((CompositeData) serviceList.values().iterator().next());
    }

	public void testListServicesLimited() throws IOException {
	    TabularData serviceList = ssMBean.listServices(ManagedServiceFactory.class.getName(), null,
	            ServiceStateMBean.PROPERTIES);
	    assertEquals(1, serviceList.size());

	    Collection<String> expectedKeys = Arrays.asList(ServiceStateMBean.IDENTIFIER, ServiceStateMBean.PROPERTIES);
        CompositeData data = (CompositeData) serviceList.values().iterator().next();

        for (String expected : expectedKeys) {
            assertNotNull(data.get(expected));
        }

        TabularType tabularType = serviceList.getTabularType();
        Set<String> actualKeys = tabularType.getRowType().keySet();
        Set<String> nullKeys = new HashSet<String>(actualKeys);
        nullKeys.removeAll(expectedKeys);
        for (String nullKey : nullKeys) {
            assertNull("Keys not request should not have their value set in the response", data.get(nullKey));
        }

	    Long expectedServiceID = getServiceId(ManagedServiceFactory.class.getName());
        assertEquals(expectedServiceID, data.get(ServiceStateMBean.IDENTIFIER));

        TabularData properties = (TabularData) data.get(ServiceStateMBean.PROPERTIES);
        ServiceReference<?> sref = getContext().getServiceReference(ManagedServiceFactory.class.getName());
        assertEquals(sref.getPropertyKeys().length, properties.size());
        for(String key : sref.getPropertyKeys()) {
            CompositeData propData = properties.get(new Object [] {key});

            Object val = sref.getProperty(key);
            if (val instanceof String) {
                assertEquals(val, propData.get(JmxConstants.VALUE));
                assertEquals("String", propData.get(JmxConstants.TYPE));
            } else if (val instanceof String[] ) {
                String jmxList = (String) propData.get(JmxConstants.VALUE);
                String [] strList = jmxList.split(",");
                assertTrue(Arrays.equals((String []) val, strList));
                assertEquals("Array of String", propData.get(JmxConstants.TYPE));
            } else if (val instanceof Long) {
                assertEquals(val, Long.valueOf((String) propData.get(JmxConstants.VALUE)));
                assertEquals("Long", propData.get(JmxConstants.TYPE));
            } else {
                fail("Unrecognized property: " + key);
            }
        }
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

	public void testExceptions() {
		assertNotNull(ssMBean);

		//test listServices method
		try {
			ssMBean.listServices();
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		catch (IllegalArgumentException e) {
			fail("unexpected exception", e);
		}

		//test getBundleIdentifier method
		try {
			ssMBean.getBundleIdentifier(LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			ssMBean.getBundleIdentifier(LONG_BIG);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getObjectClass method
		try {
			ssMBean.getObjectClass(LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			ssMBean.getObjectClass(LONG_BIG);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getProperties method
		try {
			ssMBean.getProperties(LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			ssMBean.getProperties(LONG_BIG);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test getUsingBundles method
		try {
			ssMBean.getUsingBundles(LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			ssMBean.getUsingBundles(LONG_BIG);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(ServiceStateMBean.OBJECTNAME));
		if (testBundle1 != null) {
			try {
				super.uninstallBundle(testBundle1);
			} catch (Exception io) {}
		}
		if (testBundle2 != null) {
			try {
				super.uninstallBundle(testBundle2);
			} catch (Exception io) {}
		}
	}
}
