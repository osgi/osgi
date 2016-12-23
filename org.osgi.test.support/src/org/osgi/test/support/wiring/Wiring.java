/*
 * Copyright (c) OSGi Alliance (2010, 2016). All Rights Reserved.
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


package org.osgi.test.support.wiring;

import static junit.framework.TestCase.*;
import static org.osgi.test.support.OSGiTestCase.fail;
import static org.osgi.test.support.OSGiTestCaseProperties.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.framework.wiring.FrameworkWiring;

public class Wiring {
	/**
	 * Refreshes the collection of bundle synchronously.
	 * 
	 * @param context A bundle context to use to obtain the system bundle.
	 * @param bundles The bundles to be refreshed, or {@code null} to refresh
	 *        the removal pending bundles.
	 */
	public static void synchronousRefreshBundles(BundleContext context,
			Bundle... bundles) {
		synchronousRefreshBundles(context, asCollection(bundles));
	}

	public static void synchronousRefreshBundles(BundleContext context,
			Collection<Bundle> bundles) {
		FrameworkWiring fwkWiring = context.getBundle(0).adapt(
				FrameworkWiring.class);
		assertNotNull("Framework wiring is null.", fwkWiring);
		final boolean[] done = new boolean[1];
		synchronized (done) {
			done[0] = false;
		}
		FrameworkListener listener = new FrameworkListener() {
			@Override
			public void frameworkEvent(FrameworkEvent event) {
				if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
					synchronized (done) {
						done[0] = true;
						done.notify();
					}
				}
			}
		};
		fwkWiring.refreshBundles(bundles, listener);
		long waitTime = getTimeout() * getScaling();
		final long endTime = System.currentTimeMillis() + waitTime;
		synchronized (done) {
			while (!done[0]) {
				if (waitTime <= 0) {
					fail("Timed out waiting for refresh bundles to finish.");
				}
				try {
					done.wait(waitTime);
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					fail("Unexpected interruption.", e);
				}
				waitTime = endTime - System.currentTimeMillis();
			}
		}
	}

	/**
	 * Resolves the collection of bundle synchronously.
	 * 
	 * @param context A bundle context to use to obtain the system bundle.
	 * @param bundles The bundles to resolve or {@code null} to resolve all
	 *        unresolved bundles installed in the Framework.
	 */
	public static boolean resolveBundles(BundleContext context,
			Bundle... bundles) {
		return resolveBundles(context, asCollection(bundles));
	}

	public static boolean resolveBundles(BundleContext context,
			Collection<Bundle> bundles) {
		FrameworkWiring fwkWiring = context.getBundle(0).adapt(
				FrameworkWiring.class);
		assertNotNull("Framework wiring is null.", fwkWiring);
		return fwkWiring.resolveBundles(bundles);
	}

	@SafeVarargs
	private static <T> Collection<T> asCollection(T... items) {
		if ((items == null) || (items.length == 0)) {
			return null;
		}
		return Arrays.asList(items);
	}

	public static List<BundleCapability> getExportedPackages(
			BundleContext context, String name) {
		List<BundleCapability> result = new ArrayList<BundleCapability>();
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			BundleWiring wiring = bundle.adapt(BundleWiring.class);
			List<BundleCapability> exportedPackages = wiring
					.getCapabilities(BundleRevision.PACKAGE_NAMESPACE);
			for (BundleCapability exportedPackage : exportedPackages) {
				if (name.equals(exportedPackage.getAttributes().get(
						BundleRevision.PACKAGE_NAMESPACE))) {
					result.add(exportedPackage);
				}
			}
		}
		return result;
	}

	public static BundleCapability getExportedPackage(
			BundleContext context, String name) {
		BundleCapability result = null;
		Version resultVersion = null;
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			BundleWiring wiring = bundle.adapt(BundleWiring.class);
			List<BundleCapability> exportedPackages = wiring
			.getCapabilities(BundleRevision.PACKAGE_NAMESPACE);
			for (BundleCapability exportedPackage : exportedPackages) {
				if (name.equals(exportedPackage.getAttributes().get(
						BundleRevision.PACKAGE_NAMESPACE))) {
					if (result == null) {
						result = exportedPackage;
						resultVersion = getVersion(
								exportedPackage.getAttributes(),
								Constants.VERSION_ATTRIBUTE);
					} else {
						Version version = getVersion(
								exportedPackage.getAttributes(),
								Constants.VERSION_ATTRIBUTE);
						if (version.compareTo(resultVersion) > 1) {
							result = exportedPackage;
							resultVersion = version;
						}
					}
				}
			}
		}
		return result;
	}

	private static Version getVersion(Map<String, Object> attributes, String key) {
		return (Version) attributes.get(key);
	}

	public static List<BundleRevision> getHosts(BundleContext context,
			BundleRevision fragment) {
		List<BundleRevision> result = new ArrayList<BundleRevision>();
		if ((fragment.getTypes() & BundleRevision.TYPE_FRAGMENT) == 0) {
			return result;
		}
		BundleWiring wiring = fragment.getWiring();
		if (wiring == null) {
			return result;
		}
		List<BundleWire> wires = wiring
				.getRequiredWires(BundleRevision.HOST_NAMESPACE);
		for (BundleWire wire : wires) {
			result.add(wire.getProviderWiring().getRevision());
		}
		return result;
	}
}
