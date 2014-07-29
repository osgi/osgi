package org.osgi.test.cases.remoteserviceadmin.junit;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.remoteserviceadmin.EndpointEventListener;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class CapabilityTest extends DefaultTestBundleControl {

	/**
	 * 122.9.2 Local Discovery Provider
	 */
	public void testLocalDiscoveryProviderCapability() throws Exception {
		boolean foundDiscovery = false;

		Bundle[] bundles = getContext().getBundles();
		for (int i = 0; i < bundles.length; i++) {
			Bundle eplBundle = bundles[i];
			BundleWiring bw = eplBundle.adapt(BundleWiring.class);

			List<BundleCapability> capabilities = bw
					.getCapabilities("osgi.extender");

			if (capabilities.size() >= 1) {
				for (BundleCapability bc : capabilities) {
					Map<String, Object> attributes = bc.getAttributes();
					if ("osgi.remoteserviceadmin".equals(attributes
							.get("osgi.extender"))) {
						// this bundle is the discovery provider

						Version version = (Version) attributes.get("version");
						Assert.assertEquals(0,
								version.compareTo(new Version(1, 1, 0)));

						String uses = bc.getDirectives().get("uses");
						Assert.assertNotNull(uses);
						Assert.assertTrue("org.osgi.service.remoteserviceadmin"
								.equals(uses));

						foundDiscovery = true;
					}
				}
			}

		}

		Assert.assertTrue(foundDiscovery);
	}

	/**
	 * 122.9.2 Discovery Provider Capability
	 */
	public void testDiscoveryProviderCapability() throws Exception {
		Collection<ServiceReference<EndpointEventListener>> listenerRefs = getContext()
				.getServiceReferences(EndpointEventListener.class, null);
		
		boolean foundDiscovery = false;
		
		for (ServiceReference<EndpointEventListener> eplRef : listenerRefs) {
			
			Bundle eplBundle = eplRef.getBundle();
			BundleWiring bw = eplBundle.adapt(BundleWiring.class);
			

			List<BundleCapability> capabilities = bw
					.getCapabilities("osgi.remoteserviceadmin.discovery");

			if (capabilities.size() == 1) {
				// this bundle is the discovery provider
				Map<String, Object> attributes = capabilities.get(0)
						.getAttributes();
				Version version = (Version) attributes.get("version");
				Assert.assertEquals(0, version.compareTo(new Version(1, 1, 0)));
				List<String> configs = (List<String>) attributes
						.get("protocols");
				Assert.assertNotNull(configs);
				Assert.assertTrue(configs.size() > 0);
				foundDiscovery = true;
			}
			
		}
		
		Assert.assertTrue(foundDiscovery);
	}

	/**
	 * 122.9.3 Distribution Provider Capability
	 */
	public void testDistributionProviderCapability() throws Exception {
		ServiceReference<RemoteServiceAdmin> rsaRef = getContext()
				.getServiceReference(
				RemoteServiceAdmin.class);

		Bundle rsaBundle = rsaRef.getBundle();
		BundleWiring bw = rsaBundle.adapt(BundleWiring.class);

		List<BundleCapability> capabilities = bw
				.getCapabilities("osgi.remoteserviceadmin.distribution");

		Assert.assertEquals(1, capabilities.size());
		Map<String, Object> attributes = capabilities.get(0).getAttributes();
		Version version = (Version) attributes.get("version");
		Assert.assertEquals(0, version.compareTo(new Version(1, 1, 0)));
		List<String> configs = (List<String>) attributes.get("configs");
		Assert.assertNotNull(configs);
		Assert.assertTrue(configs.size() > 0);
	}

	/**
	 * 122.9.4 Topology Manager Capability
	 */
	public void testTopologyCapability() throws Exception {
		Collection<ServiceReference<EndpointEventListener>> listenerRefs = getContext()
				.getServiceReferences(EndpointEventListener.class, null);

		boolean foundTopologyManager = false;

		for (ServiceReference<EndpointEventListener> eplRef : listenerRefs) {

			Bundle eplBundle = eplRef.getBundle();
			BundleWiring bw = eplBundle.adapt(BundleWiring.class);

			List<BundleCapability> capabilities = bw
					.getCapabilities("osgi.remoteserviceadmin.topology");
			if (capabilities.size() == 1) {
				// this bundle is the Topology Manager
				Map<String, Object> attributes = capabilities.get(0)
						.getAttributes();
				Version version = (Version) attributes.get("version");
				Assert.assertEquals(0, version.compareTo(new Version(1, 1, 0)));
				List<String> configs = (List<String>) attributes.get("policy");
				Assert.assertNotNull(configs);
				Assert.assertTrue(configs.size() > 0);
				foundTopologyManager = true;
			}

		}

		Assert.assertTrue(foundTopologyManager);
	}

}
