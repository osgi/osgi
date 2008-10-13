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
	
	public void testPublishFind() {
		ServiceEndpointDescription service = new ServiceEndpointDescription() {

			public String[] getInterfaceNames() {
				return new String[]{DiscoveryTestServiceInterface.class.getName()};
			}

			public URL getLocation() {
				// TODO Auto-generated method stub
				return null;
			}

			public Map getProperties() {
				HashMap props = new HashMap();
				props.put("mytestkey", "mytestvalue");
				return props;
			}

			public Object getProperty(String arg0) {
				return arg0 == "mytestkey" ? "mytestvalue" : null;
			}

			public Collection getPropertyKeys() {
				ArrayList keys = new ArrayList();
				keys.add("mytestkey");
				return keys;
			}

			public String getProtocolSpecificInterfaceName(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			public String getVersion(String arg0) {
				return "1.2.3";
			}
			
		};
		
		boolean b = discovery.publishService(service);
		assertTrue(b);
		
		ServiceEndpointDescription[] descs = discovery.findService(DiscoveryTestServiceInterface.class.getName(), "(mytestkey=mytestvalue)");
		assertNotNull(descs);
		assertTrue(descs.length == 1);
		assertEquals(service.getProperty("mytestkey"), descs[0].getProperty("mytestkey"));
		assertEquals(service.getInterfaceNames()[0], descs[0].getInterfaceNames()[0]);
		
		discovery.unpublishService(service);
		descs = discovery.findService(DiscoveryTestServiceInterface.class.getName(), "(mytestkey=mytestvalue)");
		assertNotNull(descs);
		assertTrue(descs.length == 0);
	}
}
