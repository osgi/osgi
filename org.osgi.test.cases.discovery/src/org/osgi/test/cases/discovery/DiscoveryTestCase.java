/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.discovery;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServiceListener;
import org.osgi.test.cases.discovery.internal.DiscoveryServiceListener;
import org.osgi.test.cases.discovery.internal.DiscoveryTestServiceInterface;

public class DiscoveryTestCase extends TestCase {
	BundleContext context;
	ServiceReference discoveryServiceRef;
	Discovery discovery;

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	protected void setUp() throws Exception {
		discoveryServiceRef = context.getServiceReference(Discovery.class.getName());
		discovery = (Discovery) context.getService(discoveryServiceRef);
	}

	protected void tearDown() throws Exception {
	}

	public void testDiscoveryPresence() {
		assertNotNull(discovery);
		
	}
	
	public void testPublish() {
		Map jInterfaces = new HashMap();
		Map epInterfaces = new HashMap();
		Map properties = new HashMap();
		
		jInterfaces.put(DiscoveryTestServiceInterface.class.getName(), "1.0.0");
		epInterfaces.put(DiscoveryTestServiceInterface.class.getName(), "{www.tibco.com/rfc119/tck}DiscoveryTestServiceInterface");
		properties.put("mytestkey", "mytestvalue");
		
		ServiceEndpointDescription sed = discovery.publishService(jInterfaces, epInterfaces, properties);
		assertNotNull(sed);
		assertNotNull(sed.getInterfaceNames());
		assertTrue(sed.getInterfaceNames().length == 1);
		assertEquals(DiscoveryTestServiceInterface.class.getName(), sed.getInterfaceNames()[0]);
		assertEquals("1.0.0", sed.getVersion(DiscoveryTestServiceInterface.class.getName()));
		assertEquals("mytestvalue", sed.getProperty("mytestkey"));
		
		discovery.unpublishService(sed);
	}
	
	public void testAutoPublish() {
		Map jInterfaces = new HashMap();
		Map epInterfaces = new HashMap();
		Map properties = new HashMap();
		
		jInterfaces.put(DiscoveryTestServiceInterface.class.getName(), "1.0.0");
		epInterfaces.put(DiscoveryTestServiceInterface.class.getName(), "{www.tibco.com/rfc119/tck}DiscoveryTestServiceInterface");
		properties.put("mytestkey", "mytestvalue");
		
		ServiceEndpointDescription sed = discovery.publishService(jInterfaces, epInterfaces, properties, true);
		assertNotNull(sed);
		assertNotNull(sed.getInterfaceNames());
		assertTrue(sed.getInterfaceNames().length == 1);
		assertEquals(DiscoveryTestServiceInterface.class.getName(), sed.getInterfaceNames()[0]);
		assertEquals("1.0.0", sed.getVersion(DiscoveryTestServiceInterface.class.getName()));
		assertEquals("mytestvalue", sed.getProperty("mytestkey"));
		
		discovery.unpublishService(sed);
	}
	
	public void testPublishFindListener() {
		Map jInterfaces = new HashMap();
		Map epInterfaces = new HashMap();
		Map properties = new HashMap();
		
		jInterfaces.put(DiscoveryTestServiceInterface.class.getName(), "1.0.0");
		epInterfaces.put(DiscoveryTestServiceInterface.class.getName(), "{www.tibco.com/rfc119/tck}DiscoveryTestServiceInterface");
		properties.put("mytestkey", "mytestvalue");
		
		DiscoveryServiceListener listener = new DiscoveryServiceListener();
		discovery.addServiceListener(listener);
		
		ServiceEndpointDescription sed = discovery.publishService(jInterfaces, epInterfaces, properties, true);
		assertNotNull(sed);
		assertNotNull(sed.getInterfaceNames());
		assertTrue(sed.getInterfaceNames().length == 1);
		assertEquals(DiscoveryTestServiceInterface.class.getName(), sed.getInterfaceNames()[0]);
		assertEquals("1.0.0", sed.getVersion(DiscoveryTestServiceInterface.class.getName()));
		assertEquals("mytestvalue", sed.getProperty("mytestkey"));
		
		assertEquals(sed, listener.getAvailableCalled());
		
		ServiceEndpointDescription[] descs = discovery.findService(DiscoveryTestServiceInterface.class.getName(), null);
		assertNotNull(descs);
		assertTrue(descs.length == 1);
		assertEquals(sed.getProperty("mytestkey"), descs[0].getProperty("mytestkey"));
		assertEquals(sed.getInterfaceNames()[0], descs[0].getInterfaceNames()[0]);
		assertEquals(sed.getVersion(DiscoveryTestServiceInterface.class.getName()), descs[0].getVersion(DiscoveryTestServiceInterface.class.getName()));
		
		discovery.unpublishService(sed);
		descs = discovery.findService(DiscoveryTestServiceInterface.class.getName(), "(mytestkey=mytestvalue)");
		assertNotNull(descs);
		assertTrue(descs.length == 0);
		
		assertEquals(sed, listener.getUnavailableCalled());

		// make sure the listener is removed again
		discovery.removeServiceListener(listener);
		listener.setAvailableCalled(null);
		sed = discovery.publishService(jInterfaces, epInterfaces, properties);
		assertNull(listener.getAvailableCalled());
		discovery.unpublishService(sed);
	}
	
	public void testPublishFindListenerFilter() {
		DiscoveryServiceListener listener = new DiscoveryServiceListener();
		discovery.addServiceListener(listener, "(mytestkey=mytestvalue)");
		
		Map jInterfaces = new HashMap();
		Map epInterfaces = new HashMap();
		Map properties = new HashMap();
		
		jInterfaces.put(DiscoveryTestServiceInterface.class.getName(), "1.0.0");
		epInterfaces.put(DiscoveryTestServiceInterface.class.getName(), "{www.tibco.com/rfc119/tck}DiscoveryTestServiceInterface");
		properties.put("mytestkey", "mytestvalue");
		
		ServiceEndpointDescription sed = discovery.publishService(jInterfaces, epInterfaces, properties, true);
		
		properties.put("mytestkey", "myothertestvalue");
		jInterfaces.put(DiscoveryTestServiceInterface.class.getName(), "1.2.3");
		ServiceEndpointDescription sed1 = discovery.publishService(jInterfaces, epInterfaces, properties, true);
		
		assertEquals(sed, listener.getAvailableCalled());
		
		ServiceEndpointDescription[] descs = discovery.findService(DiscoveryTestServiceInterface.class.getName(), "(mytestkey=mytestvalue)");
		assertNotNull(descs);
		assertTrue(descs.length == 1);
		assertEquals(sed1.getProperty("mytestkey"), descs[0].getProperty("mytestkey"));
		assertEquals(sed1.getInterfaceNames()[0], descs[0].getInterfaceNames()[0]);
		assertEquals(sed1.getVersion(DiscoveryTestServiceInterface.class.getName()), descs[0].getVersion(DiscoveryTestServiceInterface.class.getName()));
		
		discovery.unpublishService(sed);
		descs = discovery.findService(DiscoveryTestServiceInterface.class.getName(), "(mytestkey=mytestvalue)");
		assertNotNull(descs);
		assertTrue(descs.length == 1);
		assertEquals(sed1.getProperty("mytestkey"), descs[0].getProperty("mytestkey"));
		assertEquals(sed1.getInterfaceNames()[0], descs[0].getInterfaceNames()[0]);
		assertEquals(sed1.getVersion(DiscoveryTestServiceInterface.class.getName()), descs[0].getVersion(DiscoveryTestServiceInterface.class.getName()));
		
		discovery.unpublishService(sed1);
		assertEquals(sed1, listener.getUnavailableCalled());

		// make sure the listener is removed again
		discovery.removeServiceListener(listener);
	}
}
