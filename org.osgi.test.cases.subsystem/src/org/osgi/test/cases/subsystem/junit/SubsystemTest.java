/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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
package org.osgi.test.cases.subsystem.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.resource.Capability;
import org.osgi.framework.resource.Resource;
import org.osgi.framework.resource.ResourceConstants;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemException;
import org.osgi.service.subsystem.Subsystem.State;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.wiring.Wiring;
import org.osgi.util.tracker.ServiceTracker;

/**
 *	Contains utilities used by other tests.
 */
public abstract class SubsystemTest extends OSGiTestCase {
	/**
	 * The Root Subsystem
	 */
	protected ServiceTracker<Subsystem, Subsystem> rootSubsystem;
	
	/**
	 * Subsystems that have been explicitly installed
	 */
	protected Collection<Subsystem> explicitlyInstalledSubsystems;

	/**
	 * Bundles that have been explicitly installed
	 */
	protected Collection<Bundle> explicitlyInstalledBundles;

	/**
	 * The bundles that should be the initial root constituents
	 */
	protected Collection<Bundle> initialRootConstituents;
	
	protected void setUp() throws Exception {
		Filter rootFilter = getContext().createFilter("(&(objectClass=" + Subsystem.class + ")(" + SubsystemConstants.SUBSYSTEM_ID_PROPERTY + "=0))");
		rootSubsystem = new ServiceTracker<Subsystem, Subsystem>(getContext(), rootFilter, null);
		rootSubsystem.open();

		explicitlyInstalledBundles = new ArrayList<Bundle>();
		explicitlyInstalledSubsystems = new ArrayList<Subsystem>();
		initialRootConstituents = Arrays.asList(getContext().getBundles());
	}
	
	protected void tearDown() throws Exception {

		for (Subsystem subsystem : explicitlyInstalledSubsystems) {
			if (!subsystem.getState().equals(State.UNINSTALLED)) {
				try {
					subsystem.uninstall();
				} catch (Throwable t) {
					// ignore; just trying to clean up
				}
			}
		}

		for (Bundle bundle : explicitlyInstalledBundles) {
			try {
				bundle.uninstall();
			} catch (Throwable t) {
				// ignore; just trying to clean up
			}
		}

		// One final clean up in case other bundles are hanging out in root
		// that were not here before the test began.
		Bundle[] currentBundles = getContext().getBundles();
		for (Bundle bundle : currentBundles) {
			if (!initialRootConstituents.contains(bundle)) {
				try {
					bundle.uninstall();
				} catch (Throwable t) {
					// ignore; just trying to clean up
				}
			}
		}

		Wiring.synchronousRefreshBundles(getContext());

		rootSubsystem.close();
		rootSubsystem = null;
		explicitlyInstalledBundles.clear();
		explicitlyInstalledSubsystems.clear();
		initialRootConstituents.clear();
	}

	protected Subsystem getRootSubsystem() {
		Subsystem root = rootSubsystem.getService();
		assertNotNull("Can not locate the root subsystem.", root);
		return root;
	}

	protected void checkSubsystemProperties(Subsystem subsystem, String tag, String symbolicName, Version version, String type, Long id, String location, State state) {
		assertNotNull("Can not locate the subsystem: " + tag, subsystem);
		assertEquals("Wrong subsystem symbolic name: " + tag, symbolicName, subsystem.getSymbolicName());
		assertEquals("Wrong subsystem version: " + tag, version, subsystem.getVersion());
		assertEquals("Wrong subsystem type: " + tag, id, new Long(subsystem.getType()));
		assertEquals("Wrong subsystem id: " + tag, id, new Long(subsystem.getSubsystemId()));
		assertEquals("Wrong subsystem location: " + tag, location, subsystem.getLocation());
		assertEquals("Wrong subsystem state: " + tag, state, subsystem.getState());
	}

	protected void checkSubsystemProperties(ServiceReference<Subsystem> subsystemRef, String tag, String symbolicName, Version version, String type, Long id, State state) {
		assertNotNull("Can not locate the subsystem: " + tag, subsystemRef);
		assertEquals("Wrong subsystem symbolic name: " + tag, symbolicName, subsystemRef.getProperty(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME_PROPERTY));
		assertEquals("Wrong subsystem version: " + tag, version, subsystemRef.getProperty(SubsystemConstants.SUBSYSTEM_VERSION_PROPERTY));
		assertEquals("Wrong subsystem type: " + tag, id, subsystemRef.getProperty(SubsystemConstants.SUBSYSTEM_TYPE_PROPERTY));
		assertEquals("Wrong subsystem id: " + tag, id, subsystemRef.getProperty(SubsystemConstants.SUBSYSTEM_ID_PROPERTY));
		assertEquals("Wrong subsystem state: " + tag, state, subsystemRef.getProperty(SubsystemConstants.SUBSYSTEM_STATE_PROPERTY));
	}

	protected void checkBundleConstituents(String tag, Collection<Bundle> bundles, Collection<Resource> constituents) {
		for (Bundle bundle : bundles) {
			BundleRevision revision = bundle.adapt(BundleRevision.class);
			Capability bundleIdentity = revision.getCapabilities(ResourceConstants.IDENTITY_NAMESPACE).iterator().next();
			Map<String, Object> bundleIdentityAttrs = bundleIdentity.getAttributes();
			boolean found = false;
			for (Resource resource : constituents) {
				Capability resourceIdentity = resource.getCapabilities(ResourceConstants.IDENTITY_NAMESPACE).iterator().next();
				Map<String, Object> resourceIdentityAttrs = resourceIdentity.getAttributes();
				// Just doing a check for SN, Version, and Type here
				found = checkMapValues(tag, false, bundleIdentityAttrs, resourceIdentityAttrs, ResourceConstants.IDENTITY_NAMESPACE, ResourceConstants.IDENTITY_VERSION_ATTRIBUTE, ResourceConstants.IDENTITY_TYPE_ATTRIBUTE);
				if (found) {
					break;
				}
			}
			assertTrue("Could not find bundle: " + revision.toString() + " : " + tag, found);
		}
	}

	protected void checkContextBundle(String tag, Subsystem subsystem) {
		BundleContext context = subsystem.getBundleContext();
		assertNotNull("Subsystem context is null: " + tag, context);
		Bundle contextBundle = context.getBundle();
		// get the first scoped parent subsystem of this subsystem (including this subsystem)
		Subsystem scoped = getParentScope(tag, subsystem);
		// make sure the context bundle is a constituent of the scoped subsystem
		checkBundleConstituents(tag + ": context bundle constituent", Arrays.asList(contextBundle), scoped.getConstituents());

		// check the properties of the context bundle
		assertEquals("Wrong context bundle symbolic name: " + tag, "org.osgi.service.subsystem.region.context." + scoped.getSubsystemId(), contextBundle.getSymbolicName());
		assertEquals("Wrong context bundle version: " + tag, new Version(1, 0, 0), contextBundle.getVersion());
		assertEquals("Wrong context bundle location: " + tag, scoped.getLocation() + "/" + scoped.getSubsystemId(), contextBundle.getLocation());
		assertEquals("Wrong context bundle state: " + tag, Bundle.ACTIVE, contextBundle.getState());
	}

	protected Subsystem getParentScope(String tag, Subsystem subsystem) {
		return getParentScope0(tag, subsystem, new HashSet<Long>());
	}

	private Subsystem getParentScope0(String tag, Subsystem subsystem, Set<Long> visited) {
		assertTrue("Should not have cycles: " + tag, visited.add(subsystem.getSubsystemId()));
		String type = subsystem.getType();
		if (SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION.equals(type) || SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE.equals(type)) {
			return subsystem;
		}

		Collection<Subsystem> parents = subsystem.getParents();
		assertNotNull("Parents is null: " + tag);
		assertFalse("No parents found: " + tag, parents.isEmpty());
		return getParentScope0(tag, parents.iterator().next(), visited);
	}

	protected void checkIdentity(String tag, Bundle bundle, Resource resource) {
		checkIdentity(tag, 
				bundle.adapt(BundleRevision.class).getCapabilities(ResourceConstants.IDENTITY_NAMESPACE).iterator().next(), 
				resource.getCapabilities(ResourceConstants.IDENTITY_NAMESPACE).iterator().next());
	}

	protected void checkIdentity(String tag, Capability expected, Capability actual) {
		assertEquals("Wrong namespace used for identity: " + tag, ResourceConstants.IDENTITY_NAMESPACE, actual.getNamespace());
		assertEquals("Namespaces do not match: " + tag, expected.getNamespace(), actual.getNamespace());
		checkMapValues(tag, true, expected.getAttributes(), actual.getAttributes(), ResourceConstants.IDENTITY_NAMESPACE, ResourceConstants.IDENTITY_VERSION_ATTRIBUTE, ResourceConstants.IDENTITY_TYPE_ATTRIBUTE);
	}

	protected boolean checkMapValues(String tag, boolean failIfDifferent, Map<String, ?> expected, Map<String, ?> actual, String... keys) {
		for (String key : keys) {
			Object expectedValue = expected.get(key);
			Object actualValue = actual.get(key);
			if (failIfDifferent) {
				assertEquals("The key '" + key + "' does not match: " + tag, expectedValue, actualValue);
			} else {
				if ((actualValue == null || expectedValue == null) && actualValue != expectedValue) {
					return false;
				}
				if (!expectedValue.equals(actualValue)) {
					return false;
				}
			}
		}
		return true;
	}

	public static enum SubsystemOperation {
		START,
		STOP,
		UNINSTALL
	}

	protected void doSubsystemOperation(String tag, Subsystem subsystem, SubsystemOperation op, boolean shouldFail) {
		try {
			switch (op) {
			case START:
				subsystem.start();
				break;
			case STOP:
				subsystem.stop();
				break;
			case UNINSTALL:
				subsystem.uninstall();
				break;
			default:
				fail("Invalid operation: " + tag + " : " + op);
				break;
			}
			if (shouldFail) {
				fail("Subsystem operation '" + op + "' should have failed: " + tag);
			}
		} catch (SubsystemException e) {
			fail("Subsystem operation '" + op + "' should have succeeded: " + tag, e);
		}
	}
}
