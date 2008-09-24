/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.discovery;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.*;

import junit.framework.TestCase;

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
}
