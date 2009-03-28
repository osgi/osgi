/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.discovery;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.discovery.DiscoveredServiceNotification;
import org.osgi.service.discovery.DiscoveredServiceTracker;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;
import org.osgi.test.cases.discovery.internal.DiscoveredServiceTrackerImpl;
import org.osgi.test.cases.discovery.internal.DiscoveryTestServiceImpl;
import org.osgi.test.cases.discovery.internal.DiscoveryTestServiceInterface;
import org.osgi.test.cases.discovery.internal.DoNotPublishInterface;

public class DiscoveryTestCase extends TestCase {
	BundleContext context;
	Properties tckProperties;

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	protected void setUp() throws Exception {
		tckProperties = new Properties();
		tckProperties.put("discovery.tck.discover.timeout", "10000");
		tckProperties.put("discovery.tck.undiscover.timeout", "10000");
		
		try {
			tckProperties.load(new FileInputStream("tck.properties"));
		} catch (Exception e) {
			System.err.println("Failed to read TCK properties from tck.properties file");
		}
	}

	protected void tearDown() throws Exception {
	}

	public void testDiscoveryPresence() {
		ServiceReference discoveryServiceRef = context.getServiceReference(Discovery.class.getName());
		Discovery discovery = (Discovery) context.getService(discoveryServiceRef);
		
		assertNotNull(discovery);
		
		System.out.println("Discovery:");
		System.out.println("vendor name:         " + discoveryServiceRef.getProperty(Discovery.PROP_KEY_VENDOR_NAME));
		System.out.println("product name:        " + discoveryServiceRef.getProperty(Discovery.PROP_KEY_PRODUCT_NAME));
		System.out.println("product version:     " + discoveryServiceRef.getProperty(Discovery.PROP_KEY_PRODUCT_VERSION));
		System.out.println("supported protocols: " + discoveryServiceRef.getProperty(Discovery.PROP_KEY_SUPPORTED_PROTOCOLS));
	}
	
	/**
	 * Create a DiscoveryTestService instance and register it as ServicePublication. The discovery
	 * implementation will pick it up
	 */
	public void testPublish() throws Exception {
		// first setup the notification listener
		Collection trackerinterfaces = new ArrayList();
		trackerinterfaces.add(DiscoveryTestServiceInterface.class.getName());
		
		String filter = "(&(service.interface=" +
		DiscoveryTestServiceInterface.class.getName() + ")(service.interface.version=" +
		DiscoveryTestServiceInterface.class.getName() + ServicePublication.SEPARATOR + "1.0.0))";
		
		Collection trackerfilters = new ArrayList();
		trackerfilters.add(filter);
		
		Hashtable trackerprops = new Hashtable();
		trackerprops.put(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_INTERFACES, trackerinterfaces);
		trackerprops.put(DiscoveredServiceTracker.PROP_KEY_MATCH_CRITERIA_FILTERS, trackerfilters);
		
		DiscoveredServiceTrackerImpl tracker = new DiscoveredServiceTrackerImpl();
		ServiceRegistration tsr = context.registerService(DiscoveredServiceTracker.class.getName(), tracker, trackerprops);
		assertNotNull(tsr);
		
		// then register a service
		Hashtable properties = new Hashtable();
		properties.put("myprop", "myvalue");

		DiscoveryTestServiceImpl serviceimpl = new DiscoveryTestServiceImpl();
		
		ServiceRegistration sr = context.registerService(DiscoveryTestServiceInterface.class.getName(), serviceimpl, properties);
		assertNotNull(sr);
		
		// used in ServicePublication
		final ServiceReference reference = sr.getReference();
		
		// fill out the properties for Discovery
		Collection ifnames = new ArrayList();
		ifnames.add(DiscoveryTestServiceInterface.class.getName());
		Collection ifversions = new ArrayList();
		ifversions.add(DiscoveryTestServiceInterface.class.getName() + ServicePublication.SEPARATOR + "1.0.0"); // TODO test case with bad version number
		
		Hashtable spprops = new Hashtable();
		spprops.put(ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME, ifnames);
		spprops.put(ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION, ifversions);
		Map propertiesMap = new HashMap();
		propertiesMap.put("myprop", properties.get("myprop"));
		spprops.put(ServicePublication.PROP_KEY_SERVICE_PROPERTIES, propertiesMap);
		// TODO add test case with non existent interface
		
		// some optional properties
		spprops.put(ServicePublication.PROP_KEY_ENDPOINT_ID, "myid");
		
		Collection epifnames = new ArrayList();
		epifnames.add(DiscoveryTestServiceInterface.class.getName()  + ServicePublication.SEPARATOR
				+ "osgi:service:" + DiscoveryTestServiceInterface.class.getName() 
				+ "/version=1.0.0");
		spprops.put(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME, epifnames);
		
		// TODO add some bad metadata test cases
		
		ServicePublication sp = new ServicePublication() {
			public ServiceReference getReference() {
				return reference;
			}};
			
		ServiceRegistration spsr = context.registerService(ServicePublication.class.getName(), sp, spprops);
		assertNotNull(spsr);
		
		ServiceReference srt = context.getServiceReference(ServicePublication.class.getName());
		assertNotNull(srt);
		ServicePublication ssp = (ServicePublication) context.getService(srt);
		assertNotNull(ssp);
		assertSame(reference, ssp.getReference());
		
		int dtimeout = Integer.parseInt(tckProperties.getProperty("discovery.tck.discover.timeout"));
			
		// give the discovery some time to discover the service and then check for the callback
		System.out.println("wait " + (dtimeout / 1000) + " seconds for Discovery to discover service");
		
		DiscoveredServiceNotification dsn = tracker.waitForEvent(dtimeout);
		assertNotNull(dsn);
		System.out.println("found service");
		
		assertEquals(DiscoveredServiceNotification.AVAILABLE, dsn.getType());
		assertNotNull(dsn.getFilters());
		assertEquals(filter, dsn.getFilters().toArray()[0]);
		assertNotNull(dsn.getInterfaces());
		assertTrue(dsn.getInterfaces().size() > 0);
		assertEquals(DiscoveryTestServiceInterface.class.getName(), dsn.getInterfaces().toArray()[0]);
		assertFalse(dsn.getInterfaces().contains(DoNotPublishInterface.class.getName()));
		
		ServiceEndpointDescription sed = dsn.getServiceEndpointDescription();
		assertNotNull(sed);
		
		assertEquals("myid", sed.getEndpointID());
		assertEquals("myvalue", sed.getProperty("myprop"));
		assertNotNull(sed.getProvidedInterfaces());
		assertTrue(sed.getProvidedInterfaces().contains(DiscoveryTestServiceInterface.class.getName()));
		assertEquals("osgi:service:" + DiscoveryTestServiceInterface.class.getName() + "/version=1.0.0",
				sed.getEndpointInterfaceName(DiscoveryTestServiceInterface.class.getName()));
		assertEquals("1.0.0", sed.getVersion(DiscoveryTestServiceInterface.class.getName()));
		
		// modify registration and check for event update
		spprops.put("mynewprop", "another value");
		spsr.setProperties(spprops);

		dsn = tracker.waitForEvent(dtimeout);
		assertNotNull(dsn);
		System.out.println("service modified");
		
		assertEquals(DiscoveredServiceNotification.MODIFIED, dsn.getType());
		assertNotNull(dsn.getFilters());
		assertEquals(filter, dsn.getFilters().toArray()[0]);
		assertNotNull(dsn.getInterfaces());
		assertTrue(dsn.getInterfaces().size() > 0);
		assertEquals(DiscoveryTestServiceInterface.class.getName(), dsn.getInterfaces().toArray()[0]);
		assertFalse(dsn.getInterfaces().contains(DoNotPublishInterface.class.getName()));

		sed = dsn.getServiceEndpointDescription();
		assertNotNull(sed);
		
		assertEquals("myid", sed.getEndpointID());
		assertEquals("another value", sed.getProperty("mynewprop"));
		
		// check for event notification when service unregisters
		spsr.unregister();
		
		int udtimeout = Integer.parseInt(tckProperties.getProperty("discovery.tck.undiscover.timeout"));
		
		// give the discovery some time to discover the unregistration of the service and then check for the callback
		System.out.println("wait " + (udtimeout / 1000) + " seconds for Discovery to recognize unregistration of the service");
		
		dsn = tracker.waitForEvent(udtimeout);
		assertNotNull("service unregistration was not discovered in time", dsn);
		
		assertEquals(DiscoveredServiceNotification.UNAVAILABLE, dsn.getType());
		assertNotNull(dsn.getFilters());
		assertEquals(filter, dsn.getFilters().toArray()[0]);
		assertNotNull(dsn.getInterfaces());
		assertTrue(dsn.getInterfaces().size() > 0);
		assertEquals(DiscoveryTestServiceInterface.class.getName(), dsn.getInterfaces().toArray()[0]);
		assertFalse(dsn.getInterfaces().contains(DoNotPublishInterface.class.getName()));
		
		sed = dsn.getServiceEndpointDescription();
		assertNotNull(sed);
		
		assertEquals("myid", sed.getEndpointID());
		assertEquals("myvalue", sed.getProperty("myprop"));
		assertNotNull(sed.getProvidedInterfaces());
		assertTrue(sed.getProvidedInterfaces().contains(DiscoveryTestServiceInterface.class.getName()));
		assertEquals("osgi:service:" + DiscoveryTestServiceInterface.class.getName() + "/version=1.0.0",
				sed.getEndpointInterfaceName(DiscoveryTestServiceInterface.class.getName()));
		assertEquals("1.0.0", sed.getVersion(DiscoveryTestServiceInterface.class.getName()));
	}
	
}
