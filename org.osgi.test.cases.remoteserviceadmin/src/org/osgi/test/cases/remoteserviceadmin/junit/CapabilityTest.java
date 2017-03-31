package org.osgi.test.cases.remoteserviceadmin.junit;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class CapabilityTest extends DefaultTestBundleControl {

	/**
	 * 122.9.2 Local Discovery Provider
	 */
	public void testLocalDiscoveryProviderCapability() throws Exception {
		boolean foundDiscovery = false;

		// find at least one bundle that provides the required capability
		List<BundleCapability> capabilities = findCapabilities(getContext(),
				"osgi.extender");
		if (capabilities.size() >= 1) {
			for (BundleCapability bc : capabilities) {
				Map<String, Object> attributes = bc.getAttributes();
				if ("osgi.remoteserviceadmin".equals(attributes
						.get("osgi.extender"))) {
					// this bundle is the discovery provider

					Version version = (Version) attributes.get("version");
					assertEquals(0,
							version.compareTo(new Version(1, 1, 0)));

					String uses = bc.getDirectives().get("uses");
					assertNotNull(uses);
					assertTrue("org.osgi.service.remoteserviceadmin"
							.equals(uses));

					foundDiscovery = true;
				}
			}
		}
		assertTrue(foundDiscovery);
	}

	/**
	 * 122.9.2 Discovery Provider Capability
	 */
	public void testDiscoveryProviderCapability() throws Exception {
		boolean foundDiscovery = false;

		List<BundleCapability> capabilities = findCapabilities(getContext(),
				"osgi.remoteserviceadmin.discovery");

		if (capabilities.size() >= 1) {
			for (BundleCapability bc : capabilities) {
				// this bundle is a discovery provider, check capability spec
				Map<String, Object> attributes = bc.getAttributes();
				Version version = (Version) attributes.get("version");
				assertEquals(0, version.compareTo(new Version(1, 1, 0)));
				List<String> configs = (List<String>) attributes
						.get("protocols");
				assertNotNull(configs);
				assertTrue(configs.size() > 0);
				foundDiscovery = true;
			}
		}

		assertTrue(foundDiscovery);
	}

	/**
	 * 122.9.3 Distribution Provider Capability
	 */
	public void testDistributionProviderCapability() throws Exception {
		boolean foundDistributionProvider = false;
		List<BundleCapability> capabilities = findCapabilities(getContext(),
				"osgi.remoteserviceadmin.distribution");
		if (capabilities.size() >= 1) {
			for (BundleCapability bc : capabilities) {
				Map<String, Object> attributes = bc.getAttributes();
				Version version = (Version) attributes.get("version");
				assertEquals(0, version.compareTo(new Version(1, 1, 0)));
				List<String> configs = (List<String>) attributes.get("configs");
				assertNotNull(configs);
				assertTrue(configs.size() > 0);
				foundDistributionProvider = true;
			}
		}
		assertTrue(foundDistributionProvider);
	}

	/**
	 * 122.9.4 Topology Manager Capability
	 */
	public void testTopologyCapability() throws Exception {
		boolean foundTopologyManager = false;

		List<BundleCapability> capabilities = findCapabilities(getContext(),
				"osgi.remoteserviceadmin.topology");
		if (capabilities.size() >= 1) {
			for (BundleCapability bc : capabilities) {
				Map<String, Object> attributes = bc.getAttributes();
				Version version = (Version) attributes.get("version");
				assertEquals(0, version.compareTo(new Version(1, 1, 0)));
				List<String> configs = (List<String>) attributes.get("policy");
				assertNotNull(configs);
				assertTrue(configs.size() > 0);
				foundTopologyManager = true;
			}
		}

		assertTrue(foundTopologyManager);
	}

	/**
	 * Helper to find all provided capabilities of a certain namespace
	 * */
	private static List<BundleCapability> findCapabilities(
			BundleContext bundleContext, String namespace) {
		List<BundleCapability> capabilitiesRes = new LinkedList<BundleCapability>();

		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			Bundle bundle = bundles[i];
			BundleWiring bw = bundle.adapt(BundleWiring.class);

			List<BundleCapability> capabilities = bw.getCapabilities(namespace);
			capabilitiesRes.addAll(capabilities);
		}

		return capabilitiesRes;
	}

}
