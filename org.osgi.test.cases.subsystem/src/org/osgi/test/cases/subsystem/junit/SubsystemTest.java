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
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.resource.Capability;
import org.osgi.framework.resource.Resource;
import org.osgi.framework.resource.ResourceConstants;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.Subsystem.State;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.service.subsystem.SubsystemException;
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

	public static String SUBSYSTEM_EMPTY = "empty@1.0.0.ssa";
	public static String SUBSYSTEM_A_EMPTY = "a.empty@1.0.0.ssa";
	public static String SUBSYSTEM_B_EMPTY = "b.empty@1.0.0.ssa";
	public static String SUBSYSTEM_INVALID_SMV = "invalid.smv@1.0.0.ssa";
	public static String SUBSYSTEM_A_SCOPED_NO_CONTENT_HEADER = "a.scoped.no.content.header@1.0.0.ssa";
	public static String SUBSYSTEM_B_UNSCOPED_NO_CONTENT_HEADER = "b.unscoped.no.content.header@1.0.0.ssa";
	public static String SUBSYSTEM_C_SCOPED_NO_CONTENT_HEADER = "c.scoped.no.content.header@1.0.0.ssa";
	public static String SUBSYSTEM_D_UNSCOPED_NO_CONTENT_HEADER = "d.unscoped.no.content.header@1.0.0.ssa";
	public static String SUBSYSTEM_E_COMPOSITE_EMPTY = "e.composite.empty@1.0.0.ssa";
	public static String SUBSYSTEM_F_FEATURE_EMPTY = "f.composite.empty@1.0.0.ssa";
	public static String SUBSYSTEM_G_SCOPED_NO_CONTENT_HEADER = "g.scoped.no.content.header@1.0.0.ssa";

	public static String BUNDLE_NO_DEPS_A_V1 = "no.deps.a@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_B_V1 = "no.deps.b@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_C_V1 = "no.deps.c@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_D_V1 = "no.deps.d@1.0.0.jar";

	private static Map<String, File> testSubsystems;

	private ServiceRegistration<Subsystem> dummyRoot;
	
	protected void setUp() throws Exception {
		// uncomment the following to help test the CT without an implementation
		// registerDummyRoot();
		Filter rootFilter = getContext().createFilter("(&(objectClass=" + Subsystem.class.getName() + ")(" + SubsystemConstants.SUBSYSTEM_ID_PROPERTY + "=0))");
		rootSubsystem = new ServiceTracker<Subsystem, Subsystem>(getContext(), rootFilter, null);
		rootSubsystem.open();

		explicitlyInstalledBundles = new ArrayList<Bundle>();
		explicitlyInstalledSubsystems = new ArrayList<Subsystem>();
		initialRootConstituents = Arrays.asList(getContext().getBundles());

		createTestSubsystems();
	}
	
	private void registerDummyRoot() {
		Dictionary<String, Object> rootProperties = new Hashtable<String, Object>();
		rootProperties.put(SubsystemConstants.SUBSYSTEM_ID_PROPERTY, new Long(RootSubsystemTests.ROOT_ID));
		rootProperties.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME_PROPERTY, RootSubsystemTests.ROOT_SYMBOLIC_NAME);
		rootProperties.put(SubsystemConstants.SUBSYSTEM_TYPE_PROPERTY, SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION);
		rootProperties.put(SubsystemConstants.SUBSYSTEM_STATE_PROPERTY, Subsystem.State.ACTIVE);
		rootProperties.put(SubsystemConstants.SUBSYSTEM_VERSION_PROPERTY, RootSubsystemTests.ROOT_VERSION);
		rootProperties.put(Constants.SERVICE_RANKING, Integer.MIN_VALUE);
		dummyRoot = getContext().registerService(Subsystem.class, new Subsystem() {
			public BundleContext getBundleContext() {
				return null;
			}
			public Collection<Subsystem> getChildren() {
				return Collections.emptyList();
			}

			public Map<String, String> getSubsystemHeaders(Locale locale) {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, RootSubsystemTests.ROOT_SYMBOLIC_NAME);
				headers.put(SubsystemConstants.SUBSYSTEM_VERSION, RootSubsystemTests.ROOT_VERSION.toString());
				return headers;
			}

			public String getLocation() {
				return RootSubsystemTests.ROOT_LOCATION;
			}

			public Collection<Subsystem> getParents() {
				return Collections.emptyList();
			}

			public Collection<Resource> getConstituents() {
				Collection<Resource> constituents = new ArrayList<Resource>();
				for (Bundle bundle : initialRootConstituents) {
					constituents.add(bundle.adapt(BundleRevision.class));
				}
				return constituents;
			}

			public State getState() {
				return Subsystem.State.ACTIVE;
			}

			public long getSubsystemId() {
				return RootSubsystemTests.ROOT_ID;
			}

			public String getSymbolicName() {
				return RootSubsystemTests.ROOT_SYMBOLIC_NAME;
			}

			public String getType() {
				return SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION;
			}

			public Version getVersion() {
				return RootSubsystemTests.ROOT_VERSION;
			}

			public Subsystem install(String location) throws SubsystemException {
				throw new SubsystemException();
			}

			public Subsystem install(String location, InputStream content)
					throws SubsystemException {
				throw new SubsystemException();
			}

			public void start() throws SubsystemException {
			}

			public void stop() throws SubsystemException {
				throw new SubsystemException();
			}

			public void uninstall() throws SubsystemException {
				throw new SubsystemException();
			}
			
		}, rootProperties);
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
		if (dummyRoot != null)
			dummyRoot.unregister();
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

	/**
	 * Verifies that each of the subsystems in the specified subsystem collection exist in the specified resources collection
	 * @param tag a tag to use in failure messages
	 * @param subsystems the subsystems to check
	 * @param constituents the resources to check against
	 */
	protected void checkSubsystemConstituents(String tag, Collection<Subsystem> subsystems, Collection<Resource> constituents) {
		for (Subsystem subsystem : subsystems) {
			Map<String, Object> subsystemIdentityAttrs = new HashMap<String, Object>();
			subsystemIdentityAttrs.put(ResourceConstants.IDENTITY_NAMESPACE, subsystem.getSymbolicName());
			subsystemIdentityAttrs.put(ResourceConstants.IDENTITY_VERSION_ATTRIBUTE, subsystem.getVersion());
			subsystemIdentityAttrs.put(ResourceConstants.IDENTITY_TYPE_ATTRIBUTE, subsystem.getType());

			boolean found = false;
			for (Resource resource : constituents) {
				Capability resourceIdentity = resource.getCapabilities(ResourceConstants.IDENTITY_NAMESPACE).iterator().next();
				Map<String, Object> resourceIdentityAttrs = resourceIdentity.getAttributes();
				// Just doing a check for SN, Version, and Type here
				found = checkMapValues(tag, false, subsystemIdentityAttrs, resourceIdentityAttrs, ResourceConstants.IDENTITY_NAMESPACE, ResourceConstants.IDENTITY_VERSION_ATTRIBUTE, ResourceConstants.IDENTITY_TYPE_ATTRIBUTE);
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

	public static enum SubsystemOperation {
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
			if (!shouldFail) {
				fail("Subsystem operation '" + op + "' should have succeeded: " + tag, e);
			}
		}
	}

	protected Subsystem doSubsystemInstall(String tag, Subsystem target, String location, String namedSubsystem, boolean shouldFail) {
		try {
			Subsystem result = namedSubsystem == null ? target.install(location) : target.install(location, getSubsystemContent(tag, namedSubsystem));
			if (shouldFail) {
				fail("Expecting to fail subsystem install: " + tag);
			}
			explicitlyInstalledSubsystems.add(result);
			Collection<Subsystem> parents = result.getParents();
			assertNotNull("The parent subsystems is null: " + tag, parents);
			assertTrue("The parent subsystems does not include '" + target.getSymbolicName() + "': " + tag, parents.contains(target));
			for (Subsystem parent : parents) {
				Collection<Subsystem> children = parent.getChildren();
				assertTrue("The children subsystem does not include '" + result.getSymbolicName() + "': " + tag, children.contains(result));
				Collection<Resource> constituents = parent.getConstituents();
				checkSubsystemConstituents(tag, Arrays.asList(result), constituents);
			}
			return result;
		} catch (SubsystemException e) {
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
		File ssa = testSubsystems.get(namedSubsystem);
		if (ssa == null)
			fail("Could not locate test subsystem '" + namedSubsystem + "': " + tag);
		try {
			return new FileInputStream(ssa);
		} catch (IOException e) {
			fail("Failed to open test subsystem '" + namedSubsystem + "': " + tag);
		}
		return null;
	}

	/**
	 * Returns an ecoded URL which can be used as an embedded URL in a subsystem URI to point
	 * to an SSA file.
	 * @param namedSubsystem the subsystem name to get the embedded URL fo
	 * @return the embedded URL
	 */
	protected String getEmbeddedURL(String namedSubsystem) {
		File ssa = testSubsystems.get(namedSubsystem);
		assertNotNull("Could not locate test subsystem '" + namedSubsystem, ssa);
		String uri = ssa.toURI().toString();
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

		Map<String, File> result = new HashMap<String, File>();
		result.put(SUBSYSTEM_EMPTY, createSubsystem(null, null, null, new File(testSubsystemRoots, SUBSYSTEM_EMPTY)));

		Map<String, String> sm = new HashMap<String, String>();
		sm.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, "a.empty");
		sm.put(SubsystemConstants.SUBSYSTEM_VERSION, "1.0.0");
		result.put(SUBSYSTEM_A_EMPTY, createSubsystem(sm, null, null, new File(testSubsystemRoots, SUBSYSTEM_A_EMPTY)));

		sm.clear();
		sm.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, "b.empty");
		sm.put(SubsystemConstants.SUBSYSTEM_VERSION, "1.0.0");
		result.put(SUBSYSTEM_B_EMPTY, createSubsystem(sm, null, null, new File(testSubsystemRoots, SUBSYSTEM_B_EMPTY)));

		sm.clear();
		sm.put(SubsystemConstants.SUBSYSTEM_MANIFESTVERSION, "1000");
		sm.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, "invalid.smv");
		result.put(SUBSYSTEM_INVALID_SMV, createSubsystem(sm, null, null, new File(testSubsystemRoots, SUBSYSTEM_INVALID_SMV)));

		sm.clear();
		sm.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, "a.scoped.no.content.header");
		sm.put(SubsystemConstants.SUBSYSTEM_VERSION, "1.0.0");
		Map<String, URL> content = getBundleContent(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1, BUNDLE_NO_DEPS_C_V1);
		result.put(SUBSYSTEM_A_SCOPED_NO_CONTENT_HEADER, createSubsystem(sm, null, content, new File(testSubsystemRoots, SUBSYSTEM_A_SCOPED_NO_CONTENT_HEADER)));

		sm.clear();
		sm.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, "b.unscoped.no.content.header");
		sm.put(SubsystemConstants.SUBSYSTEM_VERSION, "1.0.0");
		sm.put(SubsystemConstants.SUBSYSTEM_TYPE, SubsystemConstants.SUBSYSTEM_TYPE_FEATURE);
		content = getBundleContent(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1, BUNDLE_NO_DEPS_C_V1);
		result.put(SUBSYSTEM_B_UNSCOPED_NO_CONTENT_HEADER, createSubsystem(sm, null, content, new File(testSubsystemRoots, SUBSYSTEM_B_UNSCOPED_NO_CONTENT_HEADER)));

		sm.clear();
		sm.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, "d.unscoped.no.content.header");
		sm.put(SubsystemConstants.SUBSYSTEM_VERSION, "1.0.0");
		sm.put(SubsystemConstants.SUBSYSTEM_TYPE, SubsystemConstants.SUBSYSTEM_TYPE_FEATURE);
		content = getBundleContent(null, BUNDLE_NO_DEPS_C_V1, BUNDLE_NO_DEPS_D_V1);
		result.put(SUBSYSTEM_D_UNSCOPED_NO_CONTENT_HEADER, createSubsystem(sm, null, content, new File(testSubsystemRoots, SUBSYSTEM_D_UNSCOPED_NO_CONTENT_HEADER)));

		sm.clear();
		sm.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, "c.scoped.no.content.header");
		sm.put(SubsystemConstants.SUBSYSTEM_VERSION, "1.0.0");
		content = getBundleContent(null, BUNDLE_NO_DEPS_A_V1);
		content = getSubsystemContent(content, result, SUBSYSTEM_B_UNSCOPED_NO_CONTENT_HEADER, SUBSYSTEM_D_UNSCOPED_NO_CONTENT_HEADER);
		result.put(SUBSYSTEM_C_SCOPED_NO_CONTENT_HEADER, createSubsystem(sm, null, content, new File(testSubsystemRoots, SUBSYSTEM_C_SCOPED_NO_CONTENT_HEADER)));

		sm.clear();
		sm.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, "e.composite.empty");
		sm.put(SubsystemConstants.SUBSYSTEM_VERSION, "1.0.0");
		sm.put(SubsystemConstants.SUBSYSTEM_TYPE, SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE);
		result.put(SUBSYSTEM_E_COMPOSITE_EMPTY, createSubsystem(sm, null, null, new File(testSubsystemRoots, SUBSYSTEM_E_COMPOSITE_EMPTY)));

		sm.clear();
		sm.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, "f.feature.empty");
		sm.put(SubsystemConstants.SUBSYSTEM_VERSION, "1.0.0");
		sm.put(SubsystemConstants.SUBSYSTEM_TYPE, SubsystemConstants.SUBSYSTEM_TYPE_FEATURE);
		result.put(SUBSYSTEM_F_FEATURE_EMPTY, createSubsystem(sm, null, null, new File(testSubsystemRoots, SUBSYSTEM_F_FEATURE_EMPTY)));

		sm.clear();
		sm.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, "g.scoped.no.content.header");
		sm.put(SubsystemConstants.SUBSYSTEM_VERSION, "1.0.0");
		content = getSubsystemContent(null, result, SUBSYSTEM_EMPTY);
		URL url = content.remove(SUBSYSTEM_EMPTY);
		content.put("foo@3.0.0.ssa", url);
		content = getSubsystemContent(content, result, SUBSYSTEM_A_EMPTY);
		url = content.remove(SUBSYSTEM_A_EMPTY);
		content.put("bar@3.0.0.ssa", url);
		result.put(SUBSYSTEM_G_SCOPED_NO_CONTENT_HEADER, createSubsystem(sm, null, content, new File(testSubsystemRoots, SUBSYSTEM_G_SCOPED_NO_CONTENT_HEADER)));

		testSubsystems = result;
	}

	Map<String, URL> getBundleContent(Map<String, URL> result, String... bundles) {
		if (result == null)
			result = new HashMap<String, URL>();
		for (String bundleName : bundles) {
			URL url = getContext().getBundle().getEntry(bundleName);
			assertNotNull("Could not find bundle: " + bundleName, url);
			result.put(bundleName, url);
		}
		return result;
	}

	Map<String, URL> getSubsystemContent(Map<String, URL> result, Map<String, File> subsystemFiles, String... subsystems) {
		if (result == null)
			result = new HashMap<String, URL>();
		for (String subsystemName : subsystems) {
			File ssa = subsystemFiles.get(subsystemName);
			assertNotNull("Could not find subsystem: " + subsystemName, ssa);
			try {
				result.put(subsystemName, ssa.toURI().toURL());
			} catch (MalformedURLException e) {
				fail("Could not find subsystem: " + subsystemName, e);
			}
		}
		return result;
	}
}
