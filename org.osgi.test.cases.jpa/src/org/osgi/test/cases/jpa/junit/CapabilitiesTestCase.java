/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.jpa.junit;

import static org.osgi.framework.Version.parseVersion;
import static org.osgi.namespace.contract.ContractNamespace.CONTRACT_NAMESPACE;
import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;
import static org.osgi.resource.Namespace.CAPABILITY_USES_DIRECTIVE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.namespace.contract.ContractNamespace;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.tracker.Tracker;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @version $Rev$ $Date$
 */
public class CapabilitiesTestCase extends DefaultTestBundleControl {

	public static final long SERVICE_WAIT_TIME = 5000;

	/**
	 * See JPA Service Spec 127.7.1
	 * 
	 * @throws Exception
	 */
	public void testExtenderCapability() throws Exception {

		boolean found = false;

		outer: for (Bundle b : getContext().getBundles()) {

			BundleWiring wiring = b.adapt(BundleWiring.class);

			if (wiring == null)
				continue;

			for (BundleCapability bc : wiring
					.getCapabilities(EXTENDER_NAMESPACE)) {
				Map<String,Object> attributes = bc.getAttributes();
				if ("osgi.jpa".equals(attributes.get(EXTENDER_NAMESPACE))
						&& parseVersion("1.1").equals(attributes.get(
								ExtenderNamespace.CAPABILITY_VERSION_ATTRIBUTE))) {

					String uses = bc.getDirectives()
							.get(CAPABILITY_USES_DIRECTIVE);

					if (uses != null) {
						Set<String> packages = new HashSet<>(
								Arrays.asList(uses.split("\\s,\\s")));
						if (packages.contains("javax.persistence")
								&& packages.contains("org.osgi.service.jpa")) {
							found = true;
							break outer;
						}
					}
				}
			}
		}

		assertTrue("No suitable extender capability found", found);
	}

	/**
	 * See JPA Service Spec 127.7.2
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testContractCapability() throws Exception {

		boolean found = false;

		outer: for (Bundle b : getContext().getBundles()) {

			BundleWiring wiring = b.adapt(BundleWiring.class);

			if (wiring == null)
				continue;

			for (BundleCapability bc : wiring
					.getCapabilities(CONTRACT_NAMESPACE)) {
				Map<String,Object> attributes = bc.getAttributes();
				if ("JavaJPA".equals(attributes.get(CONTRACT_NAMESPACE))) {
					Object o = attributes.get(
							ContractNamespace.CAPABILITY_VERSION_ATTRIBUTE);

					Version[] versions;
					if (o instanceof Version) {
						versions = new Version[] {
								(Version) o
						};
					} else if (o instanceof Version[]) {
						versions = (Version[]) o;
					} else if (o instanceof List) {
						versions = ((List<Version>) o).toArray(new Version[0]);
					} else {
						versions = new Version[0];
					}

					for (Version v : versions) {
						if (parseVersion("2.1").equals(v)) {
							String uses = bc.getDirectives()
									.get(CAPABILITY_USES_DIRECTIVE);

							if (uses != null) {
								Set<String> packages = new HashSet<>(
										Arrays.asList(uses.split("\\s,\\s")));
								if (packages.contains("javax.persistence")
										&& packages.contains(
												"javax.persistence.criteria")
										&& packages.contains(
												"javax.persistence.metamodel")
										&& packages.contains(
												"javax.persistence.spi")) {
									found = true;
									break outer;
								}
							}
						}
					}
				}
			}
		}

		assertTrue("No contract capability found", found);
	}

	/**
	 * See JPA Service Spec 127.7.1
	 * 
	 * @throws Exception
	 */
	public void testRequireContractCapability() throws Exception {
		Bundle ctExtenderCap = null;
		Bundle persistenceBundle = null;
		try {
			ctExtenderCap = installBundle(
					"ctExtenderCapProvidingBundle.jar");
			persistenceBundle = installBundle("extenderCapRequiringBundle.jar");
			waitForService(EntityManagerFactoryBuilder.class, true);
		} finally {
			if (persistenceBundle != null) {
				uninstallBundle(persistenceBundle);
			}
			if (ctExtenderCap != null) {
				uninstallBundle(ctExtenderCap);
			}
		}
	}

	/**
	 * See JPA Service Spec 127.7.1
	 * 
	 * @throws Exception
	 */
	public void testRequireDifferentContractCapability() throws Exception {
		Bundle ctExtenderCap = null;
		Bundle persistenceBundle = null;
		try {
			ctExtenderCap = installBundle(
					"ctExtenderCapProvidingBundle.jar");
			persistenceBundle = installBundle(
					"ctExtenderCapRequiringBundle.jar");
			waitForService(EntityManagerFactoryBuilder.class, false);
		} finally {
			if (persistenceBundle != null) {
				uninstallBundle(persistenceBundle);
			}
			if (ctExtenderCap != null) {
				uninstallBundle(ctExtenderCap);
			}
		}
	}

	public <T> void waitForService(Class<T> cls, boolean expected) {
		ServiceTracker<T,T> tracker = new ServiceTracker<>(getContext(),
				cls.getName(), null);
		tracker.open();
		Object service = null;
		try {
			service = Tracker.waitForService(tracker, SERVICE_WAIT_TIME);
		} catch (InterruptedException intEx) {
			// service will be null
		}
		tracker.close();

		if (expected) {
			assertNotNull("Service for " + cls.getName()
					+ " was not registered after waiting " + SERVICE_WAIT_TIME
					+ " milliseconds", service);
		} else {
			assertNull(
					"Service for " + cls.getName()
							+ " was registered despite not being expected",
					service);
		}
	}
}
