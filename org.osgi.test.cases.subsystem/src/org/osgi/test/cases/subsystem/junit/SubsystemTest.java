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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.resource.Capability;
import org.osgi.resource.Resource;
import org.osgi.service.repository.Repository;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.Subsystem.State;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.service.subsystem.SubsystemException;
import org.osgi.test.cases.subsystem.resource.SubsystemInfo;
import org.osgi.test.cases.subsystem.resource.TestRepository;
import org.osgi.test.cases.subsystem.resource.TestResource;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.wiring.Wiring;
import org.osgi.util.tracker.ServiceTracker;

/**
 *	Contains utilities used by other tests.
 */
public abstract class SubsystemTest extends OSGiTestCase {
	/**
	 * subsystem manifest file.
	 */
	public static final String SUBSYSTEM_MANIFEST = "OSGI-INF/SUBSYSTEM.MF";

	/**
	 * deployment manifest file.
	 */
	public static final String DEPLOYMENT_MANIFEST = "OSGI-INF/DEPLOYMENT.MF";
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

	/**
	 * The registered repositories
	 */
	protected Map<String, ServiceRegistration<Repository>> registeredRepositories;

	/**
	 * Registered bundle listeners
	 */
	protected Map<BundleListener, BundleContext> bundleListeners;

	/**
	 * Registered service listeners
	 */
	protected Map<ServiceListener, BundleContext> serviceListeners;

	private static Map<String, SubsystemInfo> testSubsystems;
	private static Map<String, Repository> testRepositories;

	public static String SUBSYSTEM_EMPTY = "empty@1.0.0.esa";
	public static String SUBSYSTEM_EMPTY_A = "empty.a@1.0.0.esa";
	public static String SUBSYSTEM_EMPTY_COMPOSITE_A = "empty.composite.a@1.0.0.esa";
	public static String SUBSYSTEM_EMPTY_B = "empty.b@1.0.0.esa";
	public static String SUBSYSTEM_INVALID_SMV = "invalid.smv@1.0.0.esa";
	public static String SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_A = "no.content.header.scoped.a@1.0.0.esa";
	public static String SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_B = "no.content.header.unscoped.b@1.0.0.esa";
	public static String SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_C = "no.content.header.scoped.c@1.0.0.esa";
	public static String SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_D = "no.content.header.unscoped.d@1.0.0.esa";
	public static String SUBSYSTEM_EMPTY_COMPOSITE_E = "empty.composite.e@1.0.0.esa";
	public static String SUBSYSTEM_EMPTY_FEATURE_F = "empty.composite.f@1.0.0.esa";
	public static String SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_G = "no.content.header.scoped.g@1.0.0.esa";
	public static String SUBSYSTEM_CONTENT_HEADER_SCOPED_H = "content.header.scoped.h@1.0.0.esa";
	public static String SUBSYSTEM_CONTENT_HEADER_SCOPED_I = "content.header.scoped.i@1.0.0.esa";
	public static String SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J = "content.header.unscoped.j@1.0.0.esa";
	public static String SUBSYSTEM_CONTENT_HEADER_UNSCOPED_K = "content.header.unscoped.k@1.0.0.esa";
	public static String SUBSYSTEM_CONTENT_HEADER_SCOPED_L = "content.header.scoped.l@1.0.0.esa";
	public static String SUBSYSTEM_CONTENT_HEADER_SCOPED_M = "content.header.scoped.m@1.0.0.esa";
	public static String SUBSYSTEM_INVALID_COMPOSITE_N = "invalid.composite.n@1.0.0.esa";
	public static String SUBSYSTEM_CYCLE_UNSCOPED_A = "cycle.unscoped.a@1.0.0.esa";
	public static String SUBSYSTEM_CYCLE_UNSCOPED_B = "cycle.unscoped.b@1.0.0.esa";
	public static String SUBSYSTEM_CYCLE_SCOPED_C = "cycle.scoped.c@1.0.0.esa";
	public static String SUBSYSTEM_CYCLE_UNSCOPED_D = "cycle.unscoped.d@1.0.0.esa";
	public static String SUBSYSTEM_ISOLATE_APPLICATION_A = "isolate.application.a@1.0.0.esa";
	public static String SUBSYSTEM_ISOLATE_COMPOSITE_B = "isolate.composite.b@1.0.0.esa";
	public static String SUBSYSTEM_ISOLATE_FEATURE_C = "isolate.feature.c@1.0.0.esa";
	public static String SUBSYSTEM_ISOLATE_PACKAGE_FEATURE_A = "isolate.package.feature.a@1.0.0.esa";
	public static String SUBSYSTEM_ISOLATE_REQUIRE_BUNDLE_FEATURE_B = "isolate.require.bundle.feature.b@1.0.0.esa";
	public static String SUBSYSTEM_ISOLATE_CAPABILITY_FEATURE_C = "isolate.capability.feature.c@1.0.0.esa";
	public static String SUBSYSTEM_IMPORT_SERVICE_COMPOSITE_A ="import.service.composite.a@1.0.0.esa";
	public static String SUBSYSTEM_IMPORT_PACKAGE_COMPOSITE_B ="import.package.composite.b@1.0.0.esa";
	public static String SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_C ="require.package.composite.c@1.0.0.esa";
	public static String SUBSYSTEM_REQUIRE_CAPABILITY_COMPOSITE_D ="require.capability.composite.d@1.0.0.esa";
	public static String SUBSYSTEM_EXPORT_PACKAGE_COMPOSITE_A = "export.package.composite.a@1.0.0.esa";
	public static String SUBSYSTEM_PROVIDE_CAPABILITY_COMPOSITE_B = "provide.capability.composite.b@1.0.0.esa";

	public static String BUNDLE_NO_DEPS_A_V1 = "no.deps.a@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_B_V1 = "no.deps.b@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_C_V1 = "no.deps.c@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_D_V1 = "no.deps.d@1.0.0.jar";
	public static String BUNDLE_SHARE_A = "share.a@1.0.0.jar";
	public static String BUNDLE_SHARE_B = "share.b@1.0.0.jar";
	public static String BUNDLE_SHARE_C = "share.c@1.0.0.jar";
	public static String BUNDLE_SHARE_D = "share.d@1.0.0.jar";
	public static String BUNDLE_SHARE_E = "share.e@1.0.0.jar";
	public static String BUNDLE_SHARE_F = "share.f@1.0.0.jar";
	public static String BUNDLE_SHARE_G = "share.g@1.0.0.jar";
	public static String BUNDLE_SHARE_H = "share.h@1.0.0.jar";

	public static String REPOSITORY_EMPTY = "repository.empty";
	public static String REPOSITORY_1 = "repository.1";
	public static String REPOSITORY_2 = "repository.2";
	public static String REPOSITORY_NODEPS = "repository.nodeps";
	public static String REPOSITORY_CYCLE = "repository.cycle";
	
	protected void setUp() throws Exception {
		Filter rootFilter = getContext().createFilter("(&(objectClass=" + Subsystem.class.getName() + ")(" + SubsystemConstants.SUBSYSTEM_ID_PROPERTY + "=0))");
		rootSubsystem = new ServiceTracker<Subsystem, Subsystem>(getContext(), rootFilter, null);
		rootSubsystem.open();

		explicitlyInstalledBundles = new ArrayList<Bundle>();
		explicitlyInstalledSubsystems = new ArrayList<Subsystem>();
		initialRootConstituents = Arrays.asList(getContext().getBundles());
		bundleListeners = new HashMap<BundleListener, BundleContext>();
		serviceListeners = new HashMap<ServiceListener, BundleContext>();

		createTestSubsystems();
		createTestRepositories();
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
		explicitlyInstalledBundles = null;
		explicitlyInstalledSubsystems = null;;
		initialRootConstituents = null;
		unregisterRepositories();
		removeListeners();
	}

	protected ServiceRegistration<Repository> registerRepository(String repositoryName) {
		if (registeredRepositories == null) {
			registeredRepositories = new HashMap<String, ServiceRegistration<Repository>>();
		}
		if (registeredRepositories.containsKey(repositoryName)) {
			return registeredRepositories.get(repositoryName);
		}
		Repository repo = testRepositories.get(repositoryName);
		assertNotNull("Could not find repository: " + repositoryName, repo);
		ServiceRegistration<Repository> reg = getContext().registerService(Repository.class, repo, null);
		registeredRepositories.put(repositoryName, reg);
		return reg;
	}

	protected void unregisterRepository(String repositoryName) {
		if (registeredRepositories == null)
			fail("Failed to find registered repository: " + repositoryName);
		ServiceRegistration<Repository> repository = registeredRepositories.remove(repositoryName);
		assertNotNull("Failed to find registered repository: " + repositoryName, repository);
		repository.unregister();
	}

	protected void addBundleListener(BundleContext context, BundleListener listener) {
		context.addBundleListener(listener);
		bundleListeners.put(listener, context);
	}

	protected void addServiceListener(BundleContext context, ServiceListener listener, String filter) {
		try {
			context.addServiceListener(listener, filter);
			serviceListeners.put(listener, context);
		} catch (InvalidSyntaxException e) {
			fail("Failed to add listener.", e);
		}
	}

	private void removeListeners() {
		for (BundleListener listener : bundleListeners.keySet()) {
			try {
				bundleListeners.get(listener).removeBundleListener(listener);
			} catch (IllegalStateException e) {
				// ignore; context is not valid anymore
			}
		}
		for (ServiceListener listener : serviceListeners.keySet()) {
			try {
				serviceListeners.get(listener).removeServiceListener(listener);
			} catch (IllegalStateException e) {
				// ignore; context is not valid anymore
			}
		}
	}

	private void unregisterRepositories() {
		if (registeredRepositories == null) {
			return;
		}
		for (ServiceRegistration<Repository> repositoryReg : registeredRepositories.values()) {
			try {
				repositoryReg.unregister();
			} catch (IllegalStateException e) {
				// happens if already unregistered
			}
		}
		registeredRepositories = null;
	}
	protected Subsystem getRootSubsystem() {
		Subsystem root = rootSubsystem.getService();
		assertNotNull("Can not locate the root subsystem.", root);
		return root;
	}

	/**
	 * Verifies the Subsystem attributes for the given subsystem
	 * @param subsystem the subsystem check
	 * @param tag a tag to use in failure messages
	 * @param symbolicName the expected symbolic name
	 * @param version the expected version
	 * @param type the expected type
	 * @param id the expected id
	 * @param location the expected location
	 * @param state the expected state
	 */
	protected void checkSubsystemProperties(Subsystem subsystem, String tag, String symbolicName, Version version, String type, Long id, String location, State state) {
		assertNotNull("Can not locate the subsystem: " + tag, subsystem);
		assertEquals("Wrong subsystem symbolic name: " + tag, symbolicName, subsystem.getSymbolicName());
		assertEquals("Wrong subsystem version: " + tag, version, subsystem.getVersion());
		assertEquals("Wrong subsystem type: " + tag, type, subsystem.getType());
		assertEquals("Wrong subsystem id: " + tag, id, new Long(subsystem.getSubsystemId()));
		assertEquals("Wrong subsystem location: " + tag, location, subsystem.getLocation());
		assertEquals("Wrong subsystem state: " + tag, state, subsystem.getState());
	}

	/**
	 * Verifies the service properties for the given subsystem service reference
	 * @param subsystemRef the subsystem service reference to check
	 * @param tag a tag to use in failure messages
	 * @param symbolicName the expected symbolic name
	 * @param version the expected version
	 * @param type the expected type
	 * @param id the expected id
	 * @param state the expected state
	 */
	protected void checkSubsystemProperties(ServiceReference<Subsystem> subsystemRef, String tag, String symbolicName, Version version, String type, Long id, State state) {
		assertNotNull("Can not locate the subsystem: " + tag, subsystemRef);
		assertEquals("Wrong subsystem symbolic name: " + tag, symbolicName, subsystemRef.getProperty(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME_PROPERTY));
		assertEquals("Wrong subsystem version: " + tag, version, subsystemRef.getProperty(SubsystemConstants.SUBSYSTEM_VERSION_PROPERTY));
		assertEquals("Wrong subsystem type: " + tag, type, subsystemRef.getProperty(SubsystemConstants.SUBSYSTEM_TYPE_PROPERTY));
		assertEquals("Wrong subsystem id: " + tag, id, subsystemRef.getProperty(SubsystemConstants.SUBSYSTEM_ID_PROPERTY));
		assertEquals("Wrong subsystem state: " + tag, state, subsystemRef.getProperty(SubsystemConstants.SUBSYSTEM_STATE_PROPERTY));
	}

	/**
	 * Verifies that each of the bundles in the specified bundle collection exist in the specified resources collection
	 * @param tag a tag to use in failure messages
	 * @param bundles the bundles to check
	 * @param constituents the resources to check against
	 */
	protected void checkBundleConstituents(String tag, Collection<Bundle> bundles, Collection<Resource> constituents) {
		for (Bundle bundle : bundles) {
			BundleRevision revision = bundle.adapt(BundleRevision.class);
			Capability bundleIdentity = revision
					.getCapabilities(IdentityNamespace.IDENTITY_NAMESPACE)
					.iterator().next();
			Map<String, Object> bundleIdentityAttrs = bundleIdentity.getAttributes();
			boolean found = false;
			for (Resource resource : constituents) {
				Capability resourceIdentity = resource
						.getCapabilities(IdentityNamespace.IDENTITY_NAMESPACE)
						.iterator().next();
				Map<String, Object> resourceIdentityAttrs = resourceIdentity.getAttributes();
				// Just doing a check for SN, Version, and Type here
				found = checkMapValues(tag, false, bundleIdentityAttrs,
						resourceIdentityAttrs,
						IdentityNamespace.IDENTITY_NAMESPACE,
						IdentityNamespace.CAPABILITY_VERSION_ATTRIBUTE,
						IdentityNamespace.CAPABILITY_TYPE_ATTRIBUTE);
				if (found) {
					break;
				}
			}
			assertTrue("Could not find bundle: " + revision.toString() + " : " + tag, found);
		}
	}

	/**
	 * Verifies that each of the subsystems in the specified subsystem collection exist in the specified resources collection
	 * @param tag a tag to use in failure messages
	 * @param subsystems the subsystems to check
	 * @param constituents the resources to check against
	 */
	protected void checkSubsystemConstituents(String tag, Collection<Subsystem> subsystems, Collection<Resource> constituents) {
		for (Subsystem subsystem : subsystems) {
			Map<String, Object> subsystemIdentityAttrs = new HashMap<String, Object>();
			subsystemIdentityAttrs.put(IdentityNamespace.IDENTITY_NAMESPACE,
					subsystem.getSymbolicName());
			subsystemIdentityAttrs.put(
					IdentityNamespace.CAPABILITY_VERSION_ATTRIBUTE,
					subsystem.getVersion());
			subsystemIdentityAttrs.put(
					IdentityNamespace.CAPABILITY_TYPE_ATTRIBUTE,
					subsystem.getType());

			boolean found = false;
			for (Resource resource : constituents) {
				Capability resourceIdentity = resource
						.getCapabilities(IdentityNamespace.IDENTITY_NAMESPACE)
						.iterator().next();
				Map<String, Object> resourceIdentityAttrs = resourceIdentity.getAttributes();
				// Just doing a check for SN, Version, and Type here
				found = checkMapValues(tag, false, subsystemIdentityAttrs,
						resourceIdentityAttrs,
						IdentityNamespace.IDENTITY_NAMESPACE,
						IdentityNamespace.CAPABILITY_VERSION_ATTRIBUTE,
						IdentityNamespace.CAPABILITY_TYPE_ATTRIBUTE);
				if (found) {
					break;
				}
			}
			assertTrue("Could not find subsystem: " + subsystem.getSymbolicName() + " : " + tag, found);
		}
	}
	/**
	 * Verifies the context bundle for the given subsystem.
	 * @param tag a tag to use in failure messages
	 * @param subsystem the subsystem to check the context bundle for
	 */
	protected void checkContextBundle(String tag, Subsystem subsystem) {
		BundleContext context = subsystem.getBundleContext();
		assertNotNull("Subsystem context is null: " + tag, context);
		Bundle contextBundle = context.getBundle();
		// get the first scoped parent subsystem of this subsystem (including this subsystem)
		Subsystem scoped = getScope(tag, subsystem);
		// make sure the context bundle is a constituent of the scoped subsystem
		checkBundleConstituents(tag + ": context bundle constituent", Arrays.asList(contextBundle), scoped.getConstituents());

		// check the properties of the context bundle
		assertEquals("Wrong context bundle symbolic name: " + tag, "org.osgi.service.subsystem.region.context." + scoped.getSubsystemId(), contextBundle.getSymbolicName());
		assertEquals("Wrong context bundle version: " + tag, new Version(1, 0, 0), contextBundle.getVersion());
		assertEquals("Wrong context bundle location: " + tag, scoped.getLocation() + "/" + scoped.getSubsystemId(), contextBundle.getLocation());
		assertEquals("Wrong context bundle state: " + tag, Bundle.ACTIVE, contextBundle.getState());
		BundleStartLevel bsl = contextBundle.adapt(BundleStartLevel.class);
		assertTrue("Context bundle is not perstently started: " + tag, bsl.isPersistentlyStarted());
		assertEquals("Wrong start level for context bundle: " + tag, 1, bsl.getStartLevel());
	}

	/**
	 * Returns the first scoped subsystem in the parent chain of the given subsystem,
	 * including the given subsystem.
	 * @param tag a tag to use in failure messages
	 * @param subsystem the subsystem to get the scoped subsystem for.
	 * @return the scoped subsystem.
	 */
	protected Subsystem getScope(String tag, Subsystem subsystem) {
		return getScope0(tag, subsystem, new HashSet<Long>());
	}

	private Subsystem getScope0(String tag, Subsystem subsystem, Set<Long> visited) {
		assertTrue("Should not have cycles: " + tag, visited.add(subsystem.getSubsystemId()));
		String type = subsystem.getType();
		if (SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION.equals(type) || SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE.equals(type)) {
			return subsystem;
		}

		Collection<Subsystem> parents = subsystem.getParents();
		assertNotNull("Parents is null: " + tag);
		assertFalse("No parents found: " + tag, parents.isEmpty());
		return getScope0(tag, parents.iterator().next(), visited);
	}

	/**
	 * Verifies the actual map contains the same values as the expected map for the given keys.  
	 * This method will fail if it detects differences and failIfDifferent is true
	 * @param tag a tag to use in failure messages
	 * @param failIfDifferent indicates if the method should fail on differences
	 * @param expected the expected values
	 * @param actual the actual values
	 * @param keys the keys to check
	 * @return true if there are no differences
	 */
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

	public static enum Operation {
		START,
		STOP,
		UNINSTALL
	}

	/**
	 * Performs the specified operation on the specified subsystem.  This method will fail depending on
	 * the shoulFail flag.
	 * @param tag a tag to use in failure messages
	 * @param subsystem the subsystem to perform the operation on
	 * @param op the operation to perform
	 * @param shouldFail true if expecting the operation to fail
	 */
	protected void doSubsystemOperation(String tag, Subsystem subsystem, Operation op, boolean shouldFail) {
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
			if (!shouldFail) {
				fail("Subsystem operation '" + op + "' should have succeeded: " + tag, e);
			}
		}
		if (shouldFail) {
			return;
		}
		Subsystem.State expectedState = null;
		switch (op) {
		case START:
			expectedState = Subsystem.State.ACTIVE;
			break;
		case STOP:
			expectedState = Subsystem.State.RESOLVED;
			break;
		case UNINSTALL:
			expectedState = Subsystem.State.UNINSTALLED;
			break;
		default:
			fail("Invalid operation: " + tag + " : " + op);
			break;
		}
		assertEquals("Wrong state after operation: " + op, expectedState, subsystem.getState());
	}

	/**
	 * Performs the specified operation on the specified bundle.  This method will fail depending on
	 * the shoulFail flag.
	 * @param tag a tag to use in failure messages
	 * @param bundle the bundle to perform the operation on
	 * @param op the operation to perform
	 * @param shouldFail true if expecting the operation to fail
	 */
	protected void doBundleOperation(String tag, Bundle bundle, Operation op, boolean shouldFail) {
		try {
			switch (op) {
			case START:
				bundle.start();
				break;
			case STOP:
				bundle.stop();
				break;
			case UNINSTALL:
				bundle.uninstall();
				break;
			default:
				fail("Invalid operation: " + tag + " : " + op);
				break;
			}
			if (shouldFail) {
				fail("Bundle operation '" + op + "' should have failed: " + tag);
			}
		} catch (BundleException e) {
			if (!shouldFail) {
				fail("Bundle operation '" + op + "' should have succeeded: " + tag, e);
			}
		}
		if (shouldFail) {
			return;
		}
		int expectedState = 0;
		switch (op) {
		case START:
			expectedState = Bundle.ACTIVE;
			break;
		case STOP:
			expectedState = Bundle.RESOLVED;
			break;
		case UNINSTALL:
			expectedState = Bundle.UNINSTALLED;
			break;
		default:
			fail("Invalid operation: " + tag + " : " + op);
			break;
		}
		assertEquals("Wrong state after operation: " + op, expectedState, bundle.getState());
	}

	protected Subsystem doSubsystemInstall(String tag, Subsystem target, String location, String namedSubsystem, boolean shouldFail) {
		try {
			Subsystem result = namedSubsystem == null ? target.install(location) : target.install(location, getSubsystemContent(tag, namedSubsystem));
			explicitlyInstalledSubsystems.add(result);
			if (shouldFail) {
				fail("Expecting to fail subsystem install: " + tag);
			}
			Collection<Subsystem> parents = result.getParents();
			assertNotNull("The parent subsystems is null: " + tag, parents);
			assertTrue("The parent subsystems does not include '" + target.getSymbolicName() + "': " + tag, parents.contains(target));
			for (Subsystem parent : parents) {
				Collection<Subsystem> children = parent.getChildren();
				assertTrue("The children subsystem does not include '" + result.getSymbolicName() + "': " + tag, children.contains(result));
				Collection<Resource> constituents = parent.getConstituents();
				checkSubsystemConstituents("Child is not a constituent of parent.", Arrays.asList(result), constituents);
			}
			return result;
		} catch (SubsystemException e) {
			if (!shouldFail) {
				fail("Unexpected failure installing a subsystem: " + tag, e);
			}
		}
		return null;
	}

	protected Bundle doBundleInstall(String tag, BundleContext context, String location, String namedBundle, boolean shouldFail) {
		if (location == null)
			location = namedBundle;
		assertNotNull("Location is null.", location);
		try {
			Bundle result = namedBundle == null ? context.installBundle(location) : context.installBundle(location, getBundleContent(tag, namedBundle));
			if (shouldFail) {
				fail("Expecting to fail bundle install: " + tag + ": " + namedBundle == null ? location : namedBundle);
			}
			explicitlyInstalledBundles.add(result);
			return result;
		} catch (BundleException e) {
			if (!shouldFail) {
				fail("Unexpected failure installing a subsystem: " + tag, e);
			}
		}
		return null;
	}

	/**
	 * Returns an input stream to the named subsystem
	 * @param content
	 * @return
	 */
	private InputStream getSubsystemContent(String tag, String namedSubsystem) {
		SubsystemInfo subsystem = testSubsystems.get(namedSubsystem);
		if (subsystem == null)
			fail("Could not locate test subsystem '" + namedSubsystem + "': " + tag);
		try {
			return new FileInputStream(subsystem.getSubsystemArchive());
		} catch (IOException e) {
			fail("Failed to open test subsystem '" + namedSubsystem + "': " + tag);
		}
		return null;
	}

	private InputStream getBundleContent(String tag, String namedBundle) {
		URL url = getContext().getBundle().getEntry(namedBundle);
		if (url == null)
			fail("Could not locate test bundle '" + namedBundle + "': " + tag);
		try {
			return url.openStream();
		} catch (IOException e) {
			fail("Failed to open test subsystem '" + namedBundle + "': " + tag);
		}
		return null;
	}

	/**
	 * Returns an encoded URL which can be used as an embedded URL in a subsystem URI to point
	 * to an SSA file.
	 * @param namedSubsystem the subsystem name to get the embedded URL fo
	 * @return the embedded URL
	 */
	protected String getEmbeddedURL(String namedSubsystem) {
		SubsystemInfo subsystem = testSubsystems.get(namedSubsystem);
		assertNotNull("Could not locate test subsystem '" + namedSubsystem, subsystem);
		String uri = subsystem.getSubsystemArchive().toURI().toString();
		try {
			uri = URLEncoder.encode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			fail("Failed to get embedded URL: " + namedSubsystem, e);
		}
		return uri;
	}

	/**
	 * Creates a subsystem with the given subsystem manifest, deployment manifest, and content.
	 * The subsystem is created using the given target file.
	 * @param sm the subsystem manifest
	 * @param dm the deployment manifest
	 * @param content the content
	 * @param target the target to write the subsystem archive.
	 * @return The target file containing the ssa
	 */
	protected File createSubsystem(Map<String, String> sm, Map<String, String> dm, Map<String, URL> content, File target) {
		target.getParentFile().mkdirs();
		Set<String> directories = new HashSet<String>();
		assertTrue("Parent folder does not exist.", target.getParentFile().exists());
		try {
			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(target));
			putManifest(SUBSYSTEM_MANIFEST, sm, zip, directories);
			putManifest(DEPLOYMENT_MANIFEST, dm, zip, directories);
			if (content != null) {
				for (Map.Entry<String, URL> entry : content.entrySet()) {
					putNextEntry(zip, entry.getKey(), entry.getValue().openStream(), directories);
				}
			}
			// make sure we have at least one entry
			Map<String, String> testInfo = new HashMap<String, String>();
			testInfo.put("subsystem.file.name", target.getName());
			putManifest("OSGI-INF/test", testInfo, zip, directories);
			zip.close();
		} catch (IOException e) {
			fail("Failed to create subsystem archive: " + target.getName(), e);
		}
		return target;
	}

	private void putManifest(String manifestName, Map<String, String> manifest, ZipOutputStream zip, Set<String> directories) throws IOException {
		if (manifest == null)
			return;
		ByteArrayOutputStream manifestContent = new ByteArrayOutputStream();
		PrintStream manifestPrinter = new PrintStream(manifestContent);
		for (Map.Entry<String, String> entry : manifest.entrySet()) {
			manifestPrinter.println(entry.getKey() + ": " + entry.getValue());
		}
		manifestPrinter.close();
		ByteArrayInputStream manifestInput = new ByteArrayInputStream(manifestContent.toByteArray());
		putNextEntry(zip, manifestName, manifestInput, directories);
	}

	private void putNextEntry(ZipOutputStream zip, String entryName, InputStream in, Set<String> directories) throws IOException {
		ZipEntry entry = new ZipEntry(entryName);
		// It is questionable if we should test with or without directories entries
		// this bit of code ensures directory entries exist before the content entries
		if (!entry.isDirectory()) {
			int idxLastSlash = entryName.lastIndexOf('/');
			if (idxLastSlash != -1) {
				ZipEntry dirEntry = new ZipEntry(entryName.substring(0, idxLastSlash + 1));
				if (!directories.contains(dirEntry.getName())) {
					zip.putNextEntry(dirEntry);
					zip.closeEntry();
					directories.add(dirEntry.getName());
				}
			}
		} else {
			if (directories.contains(entry.getName())) {
				return;
			} else {
				directories.add(entry.getName());
			}
		}
		zip.putNextEntry(new ZipEntry(entryName));
		int len;
		byte[] buf = new byte[1024];
		while ((len = in.read(buf)) > 0) {
            zip.write(buf, 0, len);
        }
		zip.closeEntry();
		in.close();
	}

	private void createTestSubsystems() {
		if (testSubsystems != null)
			return;

		File testSubsystemRoots = getContext().getDataFile("testSubsystems");
		testSubsystemRoots.mkdirs();

		Map<String, SubsystemInfo> result = new HashMap<String, SubsystemInfo>();
		Map<String, String> extraHeaders = new HashMap<String, String>();

		result.put(SUBSYSTEM_EMPTY, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_EMPTY), false, null, null, false, null, null, null));

		result.put(SUBSYSTEM_EMPTY_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_EMPTY_A), true, "1.0.0", null, false, null, null, null));
		// Create another instance of empty.a subsystem but different type (composite)
		extraHeaders.clear();
		extraHeaders.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getSymbolicName(SUBSYSTEM_EMPTY_A));
		result.put(SUBSYSTEM_EMPTY_COMPOSITE_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_EMPTY_COMPOSITE_A), false, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, null, null, extraHeaders));

		result.put(SUBSYSTEM_EMPTY_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_EMPTY_B), true, "1.0.0", null, false, null, null, null));

		extraHeaders.clear();
		extraHeaders.put(SubsystemConstants.SUBSYSTEM_MANIFESTVERSION, "1000");
		result.put(SUBSYSTEM_INVALID_SMV, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_INVALID_SMV), true, null, null, false, null, null, extraHeaders));
		
		Map<String, URL> content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1, BUNDLE_NO_DEPS_C_V1);
		result.put(SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_A), true, "1.0.0", null, false, null, content, null));
		
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1, BUNDLE_NO_DEPS_C_V1);
		result.put(SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, null, content, null));

		content = getBundleContents(null, BUNDLE_NO_DEPS_C_V1, BUNDLE_NO_DEPS_D_V1);
		result.put(SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_D, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_D), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, null, content, null));

		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1);
		content = getSubsystemContents(content, result, SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_B, SUBSYSTEM_NO_CONTENT_HEADER_UNSCOPED_D);
		result.put(SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_C, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_C), true, "1.0.0", null, false, null, content, null));

		result.put(SUBSYSTEM_EMPTY_COMPOSITE_E, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_EMPTY_COMPOSITE_E), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, null, null, null));

		result.put(SUBSYSTEM_EMPTY_FEATURE_F, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_EMPTY_FEATURE_F), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, null, null, null));

		content = getSubsystemContents(null, result, SUBSYSTEM_EMPTY);
		URL url = content.remove(SUBSYSTEM_EMPTY);
		content.put("foo@3.0.0.esa", url);
		content = getSubsystemContents(content, result, SUBSYSTEM_EMPTY_A);
		url = content.remove(SUBSYSTEM_EMPTY_A);
		content.put("bar@3.0.0.esa", url);
		result.put(SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_G, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_NO_CONTENT_HEADER_SCOPED_G), true, "1.0.0", null, false, null, content, null));

		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1, BUNDLE_NO_DEPS_C_V1, BUNDLE_NO_DEPS_D_V1);
		result.put(SUBSYSTEM_CONTENT_HEADER_SCOPED_H, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_CONTENT_HEADER_SCOPED_H), true, "1.0.0", null, false, "no.deps.a, no.deps.b, no.deps.c", content, null));

		result.put(SUBSYSTEM_CONTENT_HEADER_SCOPED_I, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_CONTENT_HEADER_SCOPED_I), true, "1.0.0", null, false, "no.deps.a, no.deps.b, no.deps.c", null, null));

		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1, BUNDLE_NO_DEPS_C_V1);
		result.put(SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, "no.deps.a, no.deps.b, no.deps.c", content, null));

		content = getBundleContents(null, BUNDLE_NO_DEPS_C_V1, BUNDLE_NO_DEPS_D_V1);
		result.put(SUBSYSTEM_CONTENT_HEADER_UNSCOPED_K, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_CONTENT_HEADER_UNSCOPED_K), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, "no.deps.c, no.deps.d", content, null));

		String contentHeader = "no.deps.a, " + 
				getSymbolicName(SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J) + "; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE + ", " +
				getSymbolicName(SUBSYSTEM_CONTENT_HEADER_UNSCOPED_K) + "; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE;
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1);
		content = getSubsystemContents(content, result, SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J, SUBSYSTEM_CONTENT_HEADER_UNSCOPED_K);
		result.put(SUBSYSTEM_CONTENT_HEADER_SCOPED_L, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_CONTENT_HEADER_SCOPED_L), true, "1.0.0", null, false, contentHeader, content, null));

		contentHeader = "no.deps.a, " +
				"bundle.does.not.exist; resolution:=optional, " +
				getSymbolicName(SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J) + "; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE + ", " +
				"subsystem.does.not.exist; resolution:=optional; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE;
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1);
		content = getSubsystemContents(content, result, SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J);
		result.put(SUBSYSTEM_CONTENT_HEADER_SCOPED_M, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_CONTENT_HEADER_SCOPED_M), true, "1.0.0", null, false, contentHeader, content, null));

		contentHeader = "no.deps.a, " + 
				getSymbolicName(SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J) + "; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE + ", " +
				getSymbolicName(SUBSYSTEM_CONTENT_HEADER_UNSCOPED_K) + "; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE;
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1);
		content = getSubsystemContents(content, result, SUBSYSTEM_CONTENT_HEADER_UNSCOPED_J, SUBSYSTEM_CONTENT_HEADER_UNSCOPED_K);
		result.put(SUBSYSTEM_INVALID_COMPOSITE_N, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_INVALID_COMPOSITE_N), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_CYCLE_UNSCOPED_B) + "; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE;
		result.put(SUBSYSTEM_CYCLE_UNSCOPED_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_CYCLE_UNSCOPED_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, null, null));

		contentHeader = getSymbolicName(SUBSYSTEM_CYCLE_UNSCOPED_A) + "; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE;
		result.put(SUBSYSTEM_CYCLE_UNSCOPED_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_CYCLE_UNSCOPED_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, null, null));

		contentHeader = getSymbolicName(SUBSYSTEM_CYCLE_UNSCOPED_D) + "; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE + "; version=\"[1.0,1.0]\"";
		result.put(SUBSYSTEM_CYCLE_SCOPED_C, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_CYCLE_SCOPED_C), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, null, null));

		contentHeader = getSymbolicName(SUBSYSTEM_CYCLE_SCOPED_C) + "; type=" + SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE;
		result.put(SUBSYSTEM_CYCLE_UNSCOPED_D, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_CYCLE_UNSCOPED_D), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, null, null));

		contentHeader = getSymbolicName(BUNDLE_NO_DEPS_A_V1) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1);
		result.put(SUBSYSTEM_ISOLATE_APPLICATION_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_ISOLATE_APPLICATION_A), true, "1.0.0", null, false, contentHeader, content, null));
		result.put(SUBSYSTEM_ISOLATE_COMPOSITE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_ISOLATE_COMPOSITE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));
		result.put(SUBSYSTEM_ISOLATE_FEATURE_C, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_ISOLATE_FEATURE_C), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		contentHeader = getSymbolicName(BUNDLE_SHARE_F);
		content = getBundleContents(null, BUNDLE_SHARE_F);
		result.put(SUBSYSTEM_ISOLATE_PACKAGE_FEATURE_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_ISOLATE_PACKAGE_FEATURE_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		contentHeader = getSymbolicName(BUNDLE_SHARE_A);
		content = getBundleContents(null, BUNDLE_SHARE_A);
		result.put(SUBSYSTEM_ISOLATE_REQUIRE_BUNDLE_FEATURE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_ISOLATE_REQUIRE_BUNDLE_FEATURE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		contentHeader = getSymbolicName(BUNDLE_SHARE_G);
		content = getBundleContents(null, BUNDLE_SHARE_G);
		result.put(SUBSYSTEM_ISOLATE_CAPABILITY_FEATURE_C, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_ISOLATE_CAPABILITY_FEATURE_C), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		Map<String, String> importPolicy = new HashMap<String, String>();
		contentHeader = getSymbolicName(BUNDLE_NO_DEPS_A_V1) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1);

		importPolicy.clear();
		importPolicy.put(SubsystemConstants.SUBSYSTEM_IMPORTSERVICE, "java.lang.Object; filter:=\"(test=value)\", does.not.exist; filter:=\"(a=b)\"");
		result.put(SUBSYSTEM_IMPORT_SERVICE_COMPOSITE_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_IMPORT_SERVICE_COMPOSITE_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));

		importPolicy.clear();
		importPolicy.put(Constants.IMPORT_PACKAGE, "x, does.not.exist; a=b");
		result.put(SUBSYSTEM_IMPORT_PACKAGE_COMPOSITE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_IMPORT_PACKAGE_COMPOSITE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));

		importPolicy.clear();
		importPolicy.put(Constants.REQUIRE_BUNDLE, getSymbolicName(BUNDLE_SHARE_A) + "; bundle-version=\"[1.0, 2.0)\", does.not.exist; bundle-version=\"[1.0, 2.0)\"");
		result.put(SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_C, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_C), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));

		importPolicy.clear();
		importPolicy.put(Constants.REQUIRE_CAPABILITY, "y; filter:=\"(y=test)\", does.not.exist; filter:=\"(a=b)\"");
		result.put(SUBSYSTEM_REQUIRE_CAPABILITY_COMPOSITE_D, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_REQUIRE_CAPABILITY_COMPOSITE_D), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));

		Map<String, String> exportPolicy = new HashMap<String, String>();

		exportPolicy.clear();
		exportPolicy.put(Constants.EXPORT_PACKAGE, "x; version=1.0, does.not.exist; a=b");
		result.put(SUBSYSTEM_EXPORT_PACKAGE_COMPOSITE_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_EXPORT_PACKAGE_COMPOSITE_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, exportPolicy));

		exportPolicy.clear();
		exportPolicy.put(Constants.PROVIDE_CAPABILITY, "y; y=test; does.not.exist; a=b");
		result.put(SUBSYSTEM_PROVIDE_CAPABILITY_COMPOSITE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_PROVIDE_CAPABILITY_COMPOSITE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, exportPolicy));

		testSubsystems = result;
	}


	Map<String, URL> getBundleContents(Map<String, URL> result, String... bundles) {
		if (result == null)
			result = new HashMap<String, URL>();
		for (String bundleName : bundles) {
			URL url = getContext().getBundle().getEntry(bundleName);
			assertNotNull("Could not find bundle: " + bundleName, url);
			result.put(bundleName, url);
		}
		return result;
	}

	Map<String, URL> getSubsystemContents(Map<String, URL> result, Map<String, SubsystemInfo> subsystemInfos, String... subsystems) {
		if (result == null)
			result = new HashMap<String, URL>();
		for (String subsystemName : subsystems) {
			SubsystemInfo info = subsystemInfos.get(subsystemName);
			assertNotNull("Could not find subsystem: " + subsystemName, info);
			try {
				result.put(subsystemName, info.getSubsystemArchive().toURI().toURL());
			} catch (MalformedURLException e) {
				fail("Could not find subsystem: " + subsystemName, e);
			}
		}
		return result;
	}

	private void createTestRepositories() {
		Map<String, Repository> result = new HashMap<String, Repository>();
		if (testRepositories != null)
			return;

		List<TestResource> empty = Collections.emptyList();
		result.put(REPOSITORY_EMPTY, new TestRepository(empty));

		Map<String, URL> bundles = getBundleContents(null, BUNDLE_SHARE_A, BUNDLE_SHARE_B, BUNDLE_SHARE_C, BUNDLE_SHARE_D, BUNDLE_SHARE_E, BUNDLE_SHARE_F, BUNDLE_SHARE_G);
		Map<String, TestResource> resources = createTestResources(bundles);

		Repository r1 = new TestRepository(resources.values());
		result.put(REPOSITORY_1, r1);
		resources.remove(BUNDLE_SHARE_F);
		resources.remove(BUNDLE_SHARE_G);
		Repository r2 = new TestRepository(resources.values());
		result.put(REPOSITORY_2, r2);

		bundles = getBundleContents(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1, BUNDLE_NO_DEPS_C_V1, BUNDLE_NO_DEPS_D_V1);
		resources = createTestResources(bundles);
		Repository rNoDeps = new TestRepository(resources.values());
		result.put(REPOSITORY_NODEPS, rNoDeps);

		Collection<TestResource> subsystemResources = 
				Arrays.asList(
						testSubsystems.get(SUBSYSTEM_CYCLE_UNSCOPED_A).getSubsystemResource(), 
						testSubsystems.get(SUBSYSTEM_CYCLE_UNSCOPED_B).getSubsystemResource(),
						testSubsystems.get(SUBSYSTEM_CYCLE_SCOPED_C).getSubsystemResource(),
						testSubsystems.get(SUBSYSTEM_CYCLE_UNSCOPED_D).getSubsystemResource());
		Repository rCycles = new TestRepository(subsystemResources);
		result.put(REPOSITORY_CYCLE, rCycles);

		testRepositories = result;		
	}

	private Map<String, TestResource> createTestResources(Map<String, URL> bundles) {
		Map<String, TestResource> resources = new HashMap<String, TestResource>();
		for (Map.Entry<String, URL> entry : bundles.entrySet()) {
			Bundle b = null;
			try {
				b = getContext().installBundle(entry.getValue().toExternalForm(), entry.getValue().openStream());
				resources.put(entry.getKey(), new TestResource(b, entry.getValue()));
			} catch (Exception e) {
				fail("Failed to create resource: " + entry.getKey(), e);
			} finally {
				if (b != null)
					try {
						b.uninstall();
					} catch (BundleException e) {
						// do nothing;
					}
			}
		}
		return resources;
	}

	public static String getSymbolicName(String namedResource) {
		int atIndex = namedResource.indexOf('@');
		assertFalse("No @ in named resource: " + namedResource, atIndex == -1);
		return namedResource.substring(0, atIndex);
	}
}
