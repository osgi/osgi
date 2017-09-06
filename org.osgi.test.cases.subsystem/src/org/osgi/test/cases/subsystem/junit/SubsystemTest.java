/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	/**
	 * Random service registrations
	 */
	private Collection<ServiceRegistration<?>> serviceRegistrations;

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
	public static String SUBSYSTEM_INVALID_COMPOSITE_CONTENT_TYPE = "invalid.composite.content.type@1.0.0.esa";
	public static String SUBSYSTEM_INVALID_APPLICATION_PREFER_TYPE = "invalid.application.prefer.type@1.0.0.esa";
	public static String SUBSYSTEM_INVALID_FEATURE_PREFER = "invalid.feature.prefer@1.0.0.esa";
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
	public static String SUBSYSTEM_IMPORT_SERVICE_COMPOSITE_B ="import.service.composite.b@1.0.0.esa";
	public static String SUBSYSTEM_IMPORT_PACKAGE_COMPOSITE_A ="import.package.composite.a@1.0.0.esa";
	public static String SUBSYSTEM_IMPORT_PACKAGE_COMPOSITE_B ="import.package.composite.b@1.0.0.esa";
	public static String SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_A ="require.package.composite.a@1.0.0.esa";
	public static String SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_B ="require.package.composite.b@1.0.0.esa";
	public static String SUBSYSTEM_REQUIRE_CAPABILITY_COMPOSITE_A ="require.capability.composite.a@1.0.0.esa";
	public static String SUBSYSTEM_REQUIRE_CAPABILITY_COMPOSITE_B ="require.capability.composite.b@1.0.0.esa";
	public static String SUBSYSTEM_EXPORT_SERVICE_COMPOSITE_A = "export.service.composite.a@1.0.0.esa";
	public static String SUBSYSTEM_EXPORT_SERVICE_COMPOSITE_B = "export.service.composite.b@1.0.0.esa";
	public static String SUBSYSTEM_EXPORT_PACKAGE_COMPOSITE_A = "export.package.composite.a@1.0.0.esa";
	public static String SUBSYSTEM_EXPORT_PACKAGE_COMPOSITE_B = "export.package.composite.b@1.0.0.esa";
	public static String SUBSYSTEM_PROVIDE_CAPABILITY_COMPOSITE_A = "provide.capability.composite.a@1.0.0.esa";
	public static String SUBSYSTEM_PROVIDE_CAPABILITY_COMPOSITE_B = "provide.capability.composite.b@1.0.0.esa";
	public static String SUBSYSTEM_2B2g_S1_APPLICATION = "2B2g.s1.application@1.0.0.esa";
	public static String SUBSYSTEM_2B2g_S2_APPLICATION = "2B2g.s2.application@1.0.0.esa";
	public static String SUBSYSTEM_2B2g_S3_APPLICATION = "2B2g.s3.application@1.0.0.esa";
	public static String SUBSYSTEM_2E11_APPLICATION = "2E11.application@1.0.0.esa";
	public static String SUBSYSTEM_2E12_APPLICATION = "2E12.application@1.0.0.esa";
	public static String SUBSYSTEM_2F3a_APPLICATION = "2F3a.application@1.0.0.esa";
	public static String SUBSYSTEM_2F3b_APPLICATION = "2F3b.application@1.0.0.esa";
	public static String SUBSYSTEM_2F4_APPLICATION = "2F4.application@1.0.0.esa";
	public static String SUBSYSTEM_4A_APPLICATION = "4A.application@1.0.0.esa";
	public static String SUBSYSTEM_4A_COMPOSITE = "4A.composite@1.0.0.esa";
	public static String SUBSYSTEM_4A_FEATURE = "4A.feature@1.0.0.esa";
	public static String SUBSYSTEM_4B_APPLICATION = "4B.application@1.0.0.esa";
	public static String SUBSYSTEM_4B_COMPOSITE = "4B.composite@1.0.0.esa";
	public static String SUBSYSTEM_4B_FEATURE = "4B.feature@1.0.0.esa";
	public static String SUBSYSTEM_4C_APPLICATION = "4C.application@1.0.0.esa";
	public static String SUBSYSTEM_4C_COMPOSITE = "4C.composite@1.0.0.esa";
	public static String SUBSYSTEM_4D_APPLICATION = "4D.application@1.0.0.esa";
	public static String SUBSYSTEM_4D_COMPOSITE = "4D.composite@1.0.0.esa";
	public static String SUBSYSTEM_4D_FEATURE = "4D.feature@1.0.0.esa";
	public static String SUBSYSTEM_4E1A_COMPOSITE_1 = "4E1a.composite.1@1.0.0.esa";
	public static String SUBSYSTEM_4E_COMPOSITE_2 = "4E.composite.2@1.0.0.esa";
	public static String SUBSYSTEM_4E_APPLICATION_2 = "4E.application.2@1.0.0.esa";
	public static String SUBSYSTEM_4E_FEATURE_2 = "4E.feature.2@1.0.0.esa";
	public static String SUBSYSTEM_4E1B_COMPOSITE_1A = "4E1b.composite.1a@1.0.0.esa";
	public static String SUBSYSTEM_4E1B_COMPOSITE_1C = "4E1b.composite.1c@1.0.0.esa";
	public static String SUBSYSTEM_4E1B_COMPOSITE_1F = "4E1b.composite.1f@1.0.0.esa";
	public static String SUBSYSTEM_4E1B_APPLICATION_1A = "4E1b.application.1a@1.0.0.esa";
	public static String SUBSYSTEM_4E1B_APPLICATION_1C = "4E1b.application.1c@1.0.0.esa";
	public static String SUBSYSTEM_4E1B_APPLICATION_1F = "4E1b.application.1f@1.0.0.esa";
	public static String SUBSYSTEM_4E2A_COMPOSITE_1 = "4E2a.composite.1@1.0.0.esa";
	public static String SUBSYSTEM_4E2B_COMPOSITE_1A = "4E2b.composite.1a@1.0.0.esa";
	public static String SUBSYSTEM_4E2B_COMPOSITE_1C = "4E2b.composite.1c@1.0.0.esa";
	public static String SUBSYSTEM_4E2B_COMPOSITE_1F = "4E2b.composite.1f@1.0.0.esa";
	public static String SUBSYSTEM_4E2B_APPLICATION_1A = "4E2b.application.1a@1.0.0.esa";
	public static String SUBSYSTEM_4E2B_APPLICATION_1C = "4E2b.application.1c@1.0.0.esa";
	public static String SUBSYSTEM_4E2B_APPLICATION_1F = "4E2b.application.1f@1.0.0.esa";
	public static String SUBSYSTEM_4E3B_COMPOSITE_1A = "4E3b.composite.1a@1.0.0.esa";
	public static String SUBSYSTEM_4E3B_COMPOSITE_1C = "4E3b.composite.1c@1.0.0.esa";
	public static String SUBSYSTEM_4E3B_COMPOSITE_1F = "4E3b.composite.1f@1.0.0.esa";
	public static String SUBSYSTEM_4F1_APPLICATION = "4F1.application@1.0.0.esa";
	public static String SUBSYSTEM_4F1_COMPOSITE = "4F1.composite@1.0.0.esa";
	public static String SUBSYSTEM_4F2_COMPOSITE_EXPORTER = "4F2.composite.exporter@1.0.0.esa";
	public static String SUBSYSTEM_4F2_FEATURE_EXPORTER = "4F2.feature.exporter@1.0.0.esa";
	public static String SUBSYSTEM_4F2_PREFER_COMP_APPLICATION = "4F2.prefer.comp.application@1.0.0.esa";
	public static String SUBSYSTEM_4F2_PREFER_COMP_COMPOSITE = "4F2.prefer.comp.composite@1.0.0.esa";
	public static String SUBSYSTEM_4F2_PREFER_FEAT_APPLICATION = "4F2.prefer.feat.application@1.0.0.esa";
	public static String SUBSYSTEM_4F2_PREFER_FEAT_COMPOSITE = "4F2.prefer.feat.composite@1.0.0.esa";
	public static String SUBSYSTEM_4G1A_COMPOSITE = "4G1a.composite@1.0.0.esa";
	public static String SUBSYSTEM_4H_APPLICATION = "4H.application@1.0.0.esa";
	public static String SUBSYSTEM_5A_APPLICATION_S1 = "5A.application.s1@1.0.0.esa";
	public static String SUBSYSTEM_5A_COMPOSITE_S1 = "5A.composite.s1@1.0.0.esa";
	public static String SUBSYSTEM_5A_FEATURE_S1 = "5A.feature.s1@1.0.0.esa";
	public static String SUBSYSTEM_5B_APPLICATION_S1 = "5B.application.s1@1.0.0.esa";
	public static String SUBSYSTEM_5B_COMPOSITE_S1 = "5B.composite.s1@1.0.0.esa";
	public static String SUBSYSTEM_5B_FEATURE_S1 = "5B.feature.s1@1.0.0.esa";
	public static String SUBSYSTEM_6_EMPTY_COMPOSITE_A = "6A.empty.composite.a@1.0.0.esa";
	public static String SUBSYSTEM_6_EMPTY_COMPOSITE_B = "6A.empty.composite.b@1.0.0.esa";
	public static String SUBSYSTEM_6_EMPTY_APPLICATION_A = "6A.empty.application.a@1.0.0.esa";
	public static String SUBSYSTEM_6_EMPTY_APPLICATION_B = "6A.empty.application.b@1.0.0.esa";
	public static String SUBSYSTEM_6_EMPTY_FEATURE_A = "6A.empty.feature.a@1.0.0.esa";
	public static String SUBSYSTEM_6_EMPTY_FEATURE_B = "6A.empty.feature.b@1.0.0.esa";
	public static String SUBSYSTEM_6_EMPTY_FEATURE_C = "6A.empty.feature.c@1.0.0.esa";
	public static String SUBSYSTEM_6A3_APPLICATION = "6A3.application@1.0.0.esa";
	public static String SUBSYSTEM_6A3_COMPOSITE = "6A3.composite@1.0.0.esa";
	public static String SUBSYSTEM_6A_FEATURE1 = "6A.feature1@1.0.0.esa";
	public static String SUBSYSTEM_6A_FEATURE2 = "6A.feature2@1.0.0.esa";
	public static String SUBSYSTEM_6A4_COMPOSITE1 = "6A4.composite@1.0.0.esa";
	public static String SUBSYSTEM_6A4_APPLICATION1 = "6A4.application1@1.0.0.esa";
	public static String SUBSYSTEM_6A4_COMPOSITE2 = "6A4.composite2@1.0.0.esa";
	public static String SUBSYSTEM_6A4_APPLICATION2 = "6A4.application2@1.0.0.esa";
	public static String SUBSYSTEM_7_APPLICATION_A_S1 = "7.application.a.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_COMPOSITE_A_S1 = "7.composite.a.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_FEATURE_A_S1 = "7.feature.a.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_APPLICATION_C_S1 = "7.application.c.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_COMPOSITE_C_S1 = "7.composite.c.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_FEATURE_C_S1 = "7.feature.c.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_APPLICATION_F_S1 = "7.application.f.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_COMPOSITE_F_S1 = "7.composite.f.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_FEATURE_F_S1 = "7.feature.f.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_APPLICATION_S2 = "7.application.s2@1.0.0.esa";
	public static String SUBSYSTEM_7_COMPOSITE_S2 = "7.composite.s2@1.0.0.esa";
	public static String SUBSYSTEM_7_FEATURE_S2 = "7.feature.s2@1.0.0.esa";
	public static String SUBSYSTEM_7_ORDERED_APPLICATION_A_S1 = "7.ordered.application.a.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_ORDERED_COMPOSITE_A_S1 = "7.ordered.composite.a.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_ORDERED_FEATURE_A_S1 = "7.ordered.feature.a.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_ORDERED_APPLICATION_C_S1 = "7.ordered.application.c.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_ORDERED_COMPOSITE_C_S1 = "7.ordered.composite.c.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_ORDERED_FEATURE_C_S1 = "7.ordered.feature.c.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_ORDERED_APPLICATION_F_S1 = "7.ordered.application.f.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_ORDERED_COMPOSITE_F_S1 = "7.ordered.composite.f.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_ORDERED_FEATURE_F_S1 = "7.ordered.feature.f.s1@1.0.0.esa";
	public static String SUBSYSTEM_7_ORDERED_APPLICATION_S2 = "7.ordered.application.s2@1.0.0.esa";
	public static String SUBSYSTEM_7_ORDERED_COMPOSITE_S2 = "7.ordered.composite.s2@1.0.0.esa";
	public static String SUBSYSTEM_7_ORDERED_FEATURE_S2 = "7.ordered.feature.s2@1.0.0.esa";
	public static String SUBSYSTEM_7G_APPLICATION_S1 = "7G.appication.s1@1.0.0.esa";
	public static String SUBSYSTEM_7G_COMPOSITE_S1 = "7G.composite.s1@1.0.0.esa";
	public static String SUBSYSTEM_7G_FEATURE_S1 = "7G.feature.s1@1.0.0.esa";
	public static String SUBSYSTEM_7G_APPLICATION_S2 = "7G.appication.s2@1.0.0.esa";
	public static String SUBSYSTEM_7G_COMPOSITE_S2 = "7G.composite.s2@1.0.0.esa";
	public static String SUBSYSTEM_7G_FEATURE_S2 = "7G.feature.s2@1.0.0.esa";

	public static String BUNDLE_NO_DEPS_A_V1 = "no.deps.a@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_A_V2 = "no.deps.a@2.0.0.jar";
	public static String BUNDLE_NO_DEPS_B_V1 = "no.deps.b@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_B_V2 = "no.deps.b@2.0.0.jar";
	public static String BUNDLE_NO_DEPS_C_V1 = "no.deps.c@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_C_V2 = "no.deps.c@2.0.0.jar";
	public static String BUNDLE_NO_DEPS_D_V1 = "no.deps.d@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_E_V1 = "no.deps.e@1.0.0.jar";
	public static String BUNDLE_NO_DEPS_F_V1 = "no.deps.f@1.0.0.jar";
	public static String BUNDLE_SHARE_A = "share.a@1.0.0.jar";
	public static String BUNDLE_SHARE_B = "share.b@1.0.0.jar";
	public static String BUNDLE_SHARE_C = "share.c@1.0.0.jar";
	public static String BUNDLE_SHARE_D = "share.d@1.0.0.jar";
	public static String BUNDLE_SHARE_E = "share.e@1.0.0.jar";
	public static String BUNDLE_SHARE_F = "share.f@1.0.0.jar";
	public static String BUNDLE_SHARE_G = "share.g@1.0.0.jar";
	public static String BUNDLE_SHARE_H = "share.h@1.0.0.jar";
	public static String BUNDLE_SHARE_I = "share.i@1.0.0.jar";
	public static String BUNDLE_SHARE_J = "share.j@1.0.0.jar";
	public static String BUNDLE_SHARE_K = "share.k@1.0.0.jar";
	public static String BUNDLE_REQUIRE_EE_NATIVE = "require.ee.native.jar";

	public static String INVALID_TYPE = "invalid.type@1.0.0.abc";

	public static String REPOSITORY_EMPTY = "repository.empty";
	public static String REPOSITORY_1 = "repository.1";
	public static String REPOSITORY_2 = "repository.2";
	public static String REPOSITORY_2B2g = "repository.2B2g";
	public static String REPOSITORY_NODEPS = "repository.nodeps";
	public static String REPOSITORY_NODEPS_V2 = "repository.nodeps.v2";
	public static String REPOSITORY_CYCLE = "repository.cycle";
	public static String REPOSITORY_INVALID_TYPE = "repository.invalid.type";
	
	protected void setUp() throws Exception {
		Filter rootFilter = getContext().createFilter(
				"(&(objectClass=" + Subsystem.class.getName() + ")("
						+ SubsystemConstants.SUBSYSTEM_ID_PROPERTY + "=0)("
						+ SubsystemConstants.SUBSYSTEM_STATE_PROPERTY + "="
						+ Subsystem.State.ACTIVE + "))");
		rootSubsystem = new ServiceTracker<Subsystem, Subsystem>(getContext(), rootFilter, null);
		rootSubsystem.open();
		// wait for the subsytems implementation to register the root subsystem
		rootSubsystem.waitForService(10000);
		// only get the initial bundles after we know the root subsystem impl is ready
		initialRootConstituents = Arrays.asList(getContext().getBundles());
		explicitlyInstalledBundles = new ArrayList<Bundle>();
		explicitlyInstalledSubsystems = new ArrayList<Subsystem>();

		bundleListeners = new HashMap<BundleListener, BundleContext>();
		serviceListeners = new HashMap<ServiceListener, BundleContext>();

		createTestSubsystems();
		createTestRepositories();

		// better make sure the implementation is active
		startImplementation();
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

		Wiring.synchronousRefreshBundles(getContext(),(Collection<Bundle>)null);

		rootSubsystem.close();
		rootSubsystem = null;
		explicitlyInstalledBundles = null;
		explicitlyInstalledSubsystems = null;;
		initialRootConstituents = null;
		unregisterRepositories();
		unregisterServices();
		removeListeners();
	}

	protected void startImplementation() {
		Bundle[] implementation = getSubsystemsImplementationBundles();
		for (int i = 0; i < implementation.length; i++) {
			Bundle b = implementation[i];
			if (b.getBundleId() == 0 || b.equals(getContext().getBundle())) {
				// skip the system bundle and the test bundle
				continue;
			}
			try {
				if ((b.adapt(BundleRevision.class).getTypes() & BundleRevision.TYPE_FRAGMENT) == 0) {
					// only start non-fragments
					b.start();
				}
			} catch (BundleException e) {
				fail("Failed to restart implementation.", e);
			}
		}
	}
	
	protected void stopImplementation() {
		Bundle[] implementation = getSubsystemsImplementationBundles();
		for (int i = implementation.length - 1; i >= 0; i--) {
			Bundle b = implementation[i];
			if (b.getBundleId() == 0 || b.equals(getContext().getBundle())) {
				// skip the system bundle and the test bundle
				continue;
			}
			try {
				b.stop();
			} catch (BundleException e) {
				// Just ignore
			}
		}
	}
	
	private Bundle[] getSubsystemsImplementationBundles() {
		String property = getContext().getProperty("org.osgi.test.cases.subsystem.impl.bundles");
		if (property == null || property.length() == 0) {
			return initialRootConstituents.toArray(new Bundle[initialRootConstituents.size()]);
		}
		property = property.trim();
		Collection<String> symbolicNames = Arrays.asList(property.split("\\s*,\\s*"));
		Collection<Bundle> result = new ArrayList<Bundle>(symbolicNames.size());
		for (Bundle bundle : initialRootConstituents) {
			if (symbolicNames.contains(bundle.getSymbolicName())) {
				result.add(bundle);
			}
		}
		return result.toArray(new Bundle[result.size()]);
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

	protected <T> ServiceRegistration<T> registerService(Class<T> clazz, T service, Dictionary<String, ?> properties) {
		ServiceRegistration<T> result = getContext().registerService(clazz, service, properties);
		if (serviceRegistrations == null) {
			serviceRegistrations = new ArrayList<ServiceRegistration<?>>();
		}
		serviceRegistrations.add(result);
		return result;
	}

	private void unregisterServices() {
		if (serviceRegistrations == null) {
			return;
		}
		for (ServiceRegistration<?> registration : serviceRegistrations) {
			try {
				registration.unregister();
			} catch (IllegalStateException e) {
				// happens if already unregistered
			}
		}
		serviceRegistrations = null;
	}

	protected Subsystem getRootSubsystem() {
		Subsystem root = null;
		try {
			root = rootSubsystem.waitForService(10000);
		}
		catch (InterruptedException e) {
			// Ignore. Test will fail below if root is null.
		}
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
		assertEquals("Wrong subsystem id: " + tag, id, Long.valueOf(subsystem.getSubsystemId()));
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
				fail("Expecting to fail subsystem install: " + tag + " - " + location);
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
				fail("Unexpected failure installing a subsystem: " + tag + " - " + location, e);
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
			explicitlyInstalledBundles.add(result);
			if (shouldFail) {
				fail("Expecting to fail bundle install: " + tag + ": " + (namedBundle == null ? location : namedBundle));
			}
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

	protected InputStream getBundleContent(String tag, String namedBundle) {
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
		String uri = getSubsystemArchive(namedSubsystem).toURI().toString();
		try {
			uri = URLEncoder.encode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			fail("Failed to get embedded URL: " + namedSubsystem, e);
		}
		return uri;
	}

	protected File getSubsystemArchive(String namedSubsystem) {
		SubsystemInfo subsystem = testSubsystems.get(namedSubsystem);
		assertNotNull("Could not locate test subsystem '" + namedSubsystem, subsystem);
		return subsystem.getSubsystemArchive();
	}

	protected Bundle getBundle(Subsystem s, String bundleName) {
		Bundle[] bundles = s.getBundleContext().getBundles();
		Bundle b = null;
		for (Bundle bundle : bundles) {
			if (getSymbolicName(bundleName).equals(bundle.getSymbolicName())) {
				b = bundle;
				break;
			}
		}
		assertNotNull("Could not find bundle: " + bundleName, b);
		return b;
	}

	protected void assertNoBundle(Subsystem s, String bundleName) {
		Bundle[] bundles = s.getBundleContext().getBundles();
		for (Bundle bundle : bundles) {
			if (getSymbolicName(bundleName).equals(bundle.getSymbolicName())) {
				fail("Found bundle: " + bundleName);
			}
		}
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

		Map<String, String> preferredProvider = new HashMap<String, String>();
		preferredProvider.put(SubsystemConstants.PREFERRED_PROVIDER, 
				getSymbolicName(SUBSYSTEM_ISOLATE_APPLICATION_A) + "; type=" + SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION);
		result.put(SUBSYSTEM_INVALID_APPLICATION_PREFER_TYPE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_INVALID_APPLICATION_PREFER_TYPE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, preferredProvider));

		preferredProvider.clear();
		preferredProvider.put(SubsystemConstants.PREFERRED_PROVIDER, 
				getSymbolicName(BUNDLE_SHARE_A) + "; type=" + IdentityNamespace.TYPE_BUNDLE);
		result.put(SUBSYSTEM_INVALID_FEATURE_PREFER, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_INVALID_FEATURE_PREFER), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, preferredProvider));

		contentHeader = getSymbolicName(INVALID_TYPE) + "; type=" + getSymbolicName(INVALID_TYPE) + "; version=\"[1.0,1.0]\"";
		result.put(SUBSYSTEM_INVALID_COMPOSITE_CONTENT_TYPE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_INVALID_COMPOSITE_CONTENT_TYPE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, null, null));


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
		result.put(SUBSYSTEM_IMPORT_SERVICE_COMPOSITE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_IMPORT_SERVICE_COMPOSITE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));

		importPolicy.clear();
		importPolicy.put(Constants.IMPORT_PACKAGE, "x, does.not.exist; a=b");
		result.put(SUBSYSTEM_IMPORT_PACKAGE_COMPOSITE_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_IMPORT_PACKAGE_COMPOSITE_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));
		result.put(SUBSYSTEM_IMPORT_PACKAGE_COMPOSITE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_IMPORT_PACKAGE_COMPOSITE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));

		importPolicy.clear();
		importPolicy.put(Constants.REQUIRE_BUNDLE, getSymbolicName(BUNDLE_SHARE_A) + "; bundle-version=\"[1.0, 2.0)\", does.not.exist; bundle-version=\"[1.0, 2.0)\"");
		result.put(SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));
		result.put(SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_REQUIRE_BUNDLE_COMPOSITE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));

		importPolicy.clear();
		importPolicy.put(Constants.REQUIRE_CAPABILITY, "y; filter:=\"(y=test)\", does.not.exist; filter:=\"(a=b)\"");
		result.put(SUBSYSTEM_REQUIRE_CAPABILITY_COMPOSITE_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_REQUIRE_CAPABILITY_COMPOSITE_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));
		result.put(SUBSYSTEM_REQUIRE_CAPABILITY_COMPOSITE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_REQUIRE_CAPABILITY_COMPOSITE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));

		Map<String, String> exportPolicy = new HashMap<String, String>();

		exportPolicy.clear();
		exportPolicy.put(SubsystemConstants.SUBSYSTEM_EXPORTSERVICE, "java.lang.Object; filter:=\"(test=value)\", does.not.exist; filter:=\"(a=b)\"");
		result.put(SUBSYSTEM_EXPORT_SERVICE_COMPOSITE_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_EXPORT_SERVICE_COMPOSITE_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, exportPolicy));
		result.put(SUBSYSTEM_EXPORT_SERVICE_COMPOSITE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_EXPORT_SERVICE_COMPOSITE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, exportPolicy));

		exportPolicy.clear();
		exportPolicy.put(Constants.EXPORT_PACKAGE, "x; version=1.0, does.not.exist; a=b");
		result.put(SUBSYSTEM_EXPORT_PACKAGE_COMPOSITE_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_EXPORT_PACKAGE_COMPOSITE_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, exportPolicy));
		result.put(SUBSYSTEM_EXPORT_PACKAGE_COMPOSITE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_EXPORT_PACKAGE_COMPOSITE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, exportPolicy));

		exportPolicy.clear();
		exportPolicy.put(Constants.PROVIDE_CAPABILITY, "y; y=test, does.not.exist; a=b");
		result.put(SUBSYSTEM_PROVIDE_CAPABILITY_COMPOSITE_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_PROVIDE_CAPABILITY_COMPOSITE_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, exportPolicy));
		result.put(SUBSYSTEM_PROVIDE_CAPABILITY_COMPOSITE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_PROVIDE_CAPABILITY_COMPOSITE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, exportPolicy));

		extraHeaders.clear();
		extraHeaders.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getSymbolicName(SUBSYSTEM_2B2g_S1_APPLICATION) + 
				"; a1=\"v1\"; a2=\"v2\"");
		Map<String, Object> extraIdentityAttrs = new HashMap<String, Object>();
		extraIdentityAttrs.put("a1", "v1");
		extraIdentityAttrs.put("a2", "v2");
		extraIdentityAttrs.put(IdentityNamespace.IDENTITY_NAMESPACE, getSymbolicName(SUBSYSTEM_2B2g_S1_APPLICATION));
		SubsystemInfo info2B2g = new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_2B2g_S1_APPLICATION), false, "1.0.0", null, false, null, null, extraHeaders, null, extraIdentityAttrs);
		result.put(SUBSYSTEM_2B2g_S1_APPLICATION, info2B2g);

		contentHeader = getSymbolicName(SUBSYSTEM_2B2g_S1_APPLICATION) + "; a1=\"v1\"; a2=\"v2\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION;
		result.put(SUBSYSTEM_2B2g_S2_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_2B2g_S2_APPLICATION), true, "1.0.0", null, false, contentHeader, null, null));

		contentHeader = getSymbolicName(SUBSYSTEM_2B2g_S1_APPLICATION) + "; a1=\"nomatch\"; a2=\"nomatch\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION;
		result.put(SUBSYSTEM_2B2g_S3_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_2B2g_S3_APPLICATION), true, "1.0.0", null, false, contentHeader, null, null));


		result.put(SUBSYSTEM_2F3a_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_2F3a_APPLICATION), true, "1", null, false, null, null, null));
		result.put(SUBSYSTEM_2F3b_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_2F3b_APPLICATION), true, "1.0.0.qualifier", null, false, null, null, null));

		extraHeaders.clear();
		extraHeaders.put("Unknown-Header", "test");
		extraHeaders.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getSymbolicName(SUBSYSTEM_2F4_APPLICATION) + 
				"; unknown-directive:=\"test\"");
		extraHeaders.put(Constants.IMPORT_PACKAGE, "unresolved;" +
				Constants.RESOLUTION_DIRECTIVE + ":=" + Constants.RESOLUTION_OPTIONAL +
				"; unknown-directive:=\"test\"");
		result.put(SUBSYSTEM_2F4_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_2F4_APPLICATION), false, "1.0.0", null, false, null, null, extraHeaders));

		extraHeaders.clear();
		extraHeaders.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, "test$invalid@characters");
		result.put(SUBSYSTEM_2E11_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_2E11_APPLICATION), false, "1.0.0", null, false, null, null, extraHeaders));

		result.put(SUBSYSTEM_2E12_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_2E12_APPLICATION), true, "1.invlidversion.0", null, false, null, null, null));

		contentHeader = 
				getSymbolicName(BUNDLE_SHARE_A) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_B) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_C) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_D) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_E) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_SHARE_A, BUNDLE_SHARE_B, BUNDLE_SHARE_C, BUNDLE_SHARE_D, BUNDLE_SHARE_E);
		result.put(SUBSYSTEM_4A_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4A_APPLICATION), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_4A_COMPOSITE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4A_COMPOSITE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));
		result.put(SUBSYSTEM_4A_FEATURE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4A_FEATURE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));


		contentHeader = 
				getSymbolicName(BUNDLE_SHARE_C) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_D) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_E) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_SHARE_C, BUNDLE_SHARE_D, BUNDLE_SHARE_E);
		result.put(SUBSYSTEM_4B_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4B_APPLICATION), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));

		importPolicy.clear();
		importPolicy.put(Constants.IMPORT_PACKAGE, "x");
		importPolicy.put(Constants.REQUIRE_BUNDLE, getSymbolicName(BUNDLE_SHARE_A));
		importPolicy.put(Constants.REQUIRE_CAPABILITY, "y; filter:=\"(y=test)\"");
		result.put(SUBSYSTEM_4B_COMPOSITE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4B_COMPOSITE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));
		importPolicy.clear();

		result.put(SUBSYSTEM_4B_FEATURE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4B_FEATURE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		contentHeader = 
				getSymbolicName(BUNDLE_SHARE_A) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_B) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_SHARE_A, BUNDLE_SHARE_B);
		result.put(SUBSYSTEM_4C_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4C_APPLICATION), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_4C_COMPOSITE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4C_COMPOSITE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));

		contentHeader = 
				getSymbolicName(BUNDLE_SHARE_C) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_E) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_SHARE_C, BUNDLE_SHARE_E, BUNDLE_SHARE_F, BUNDLE_SHARE_G);
		result.put(SUBSYSTEM_4D_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4D_APPLICATION), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));

		importPolicy.clear();
		importPolicy.put(Constants.IMPORT_PACKAGE, "x");
		importPolicy.put(Constants.REQUIRE_CAPABILITY, "y; filter:=\"(y=test)\"");
		result.put(SUBSYSTEM_4D_COMPOSITE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4D_COMPOSITE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));
		importPolicy.clear();

		result.put(SUBSYSTEM_4D_FEATURE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4D_FEATURE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		importPolicy.clear();
		importPolicy.put(Constants.IMPORT_PACKAGE, "x");
		importPolicy.put(Constants.REQUIRE_BUNDLE, getSymbolicName(BUNDLE_SHARE_A));
		importPolicy.put(Constants.REQUIRE_CAPABILITY, "y; filter:=\"(y=test)\"");
		result.put(SUBSYSTEM_4E1A_COMPOSITE_1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E1A_COMPOSITE_1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, null, null, importPolicy));

		contentHeader = 
				getSymbolicName(BUNDLE_SHARE_C) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_D) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_E) + "; version=\"[1.0,1.0]\"";
		result.put(SUBSYSTEM_4E_COMPOSITE_2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E_COMPOSITE_2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, null, importPolicy));
		result.put(SUBSYSTEM_4E_APPLICATION_2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E_APPLICATION_2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, null, null));
		result.put(SUBSYSTEM_4E_FEATURE_2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E_FEATURE_2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, null, null));

		contentHeader = getSymbolicName(SUBSYSTEM_4E_APPLICATION_2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION;
		content = getSubsystemContents(null, result, SUBSYSTEM_4E_APPLICATION_2);
		result.put(SUBSYSTEM_4E1B_COMPOSITE_1A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E1B_COMPOSITE_1A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));
		result.put(SUBSYSTEM_4E1B_APPLICATION_1A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E1B_APPLICATION_1A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_4E_COMPOSITE_2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE;
		content = getSubsystemContents(null, result, SUBSYSTEM_4E_COMPOSITE_2);
		result.put(SUBSYSTEM_4E1B_COMPOSITE_1C, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E1B_COMPOSITE_1C), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));
		result.put(SUBSYSTEM_4E1B_APPLICATION_1C, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E1B_APPLICATION_1C), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_4E_FEATURE_2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE;
		content = getSubsystemContents(null, result, SUBSYSTEM_4E_FEATURE_2);
		result.put(SUBSYSTEM_4E1B_COMPOSITE_1F, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E1B_COMPOSITE_1F), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, importPolicy));
		result.put(SUBSYSTEM_4E1B_APPLICATION_1F, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E1B_APPLICATION_1F), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));

		importPolicy.clear();
		importPolicy.put(Constants.IMPORT_PACKAGE, "x");
		importPolicy.put(Constants.REQUIRE_BUNDLE, getSymbolicName(BUNDLE_SHARE_A));
		importPolicy.put(Constants.REQUIRE_CAPABILITY, "y; filter:=\"(y=test)\"");
		result.put(SUBSYSTEM_4E2A_COMPOSITE_1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E2A_COMPOSITE_1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, true, null, null, importPolicy));

		contentHeader = getSymbolicName(SUBSYSTEM_4E_APPLICATION_2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION;
		content = getSubsystemContents(null, result, SUBSYSTEM_4E_APPLICATION_2);
		result.put(SUBSYSTEM_4E2B_COMPOSITE_1A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E2B_COMPOSITE_1A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, true, contentHeader, content, importPolicy));
		result.put(SUBSYSTEM_4E2B_APPLICATION_1A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E2B_APPLICATION_1A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, true, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_4E_COMPOSITE_2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE;
		content = getSubsystemContents(null, result, SUBSYSTEM_4E_COMPOSITE_2);
		result.put(SUBSYSTEM_4E2B_COMPOSITE_1C, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E2B_COMPOSITE_1C), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, true, contentHeader, content, importPolicy));
		result.put(SUBSYSTEM_4E2B_APPLICATION_1C, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E2B_APPLICATION_1C), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, true, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_4E_FEATURE_2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE;
		content = getSubsystemContents(null, result, SUBSYSTEM_4E_FEATURE_2);
		result.put(SUBSYSTEM_4E2B_COMPOSITE_1F, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E2B_COMPOSITE_1F), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, true, contentHeader, content, importPolicy));
		result.put(SUBSYSTEM_4E2B_APPLICATION_1F, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E2B_APPLICATION_1F), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, true, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_4E_APPLICATION_2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION;
		content = getSubsystemContents(null, result, SUBSYSTEM_4E_APPLICATION_2);
		result.put(SUBSYSTEM_4E3B_COMPOSITE_1A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E3B_COMPOSITE_1A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_4E_COMPOSITE_2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE;
		content = getSubsystemContents(null, result, SUBSYSTEM_4E_COMPOSITE_2);
		result.put(SUBSYSTEM_4E3B_COMPOSITE_1C, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E3B_COMPOSITE_1C), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_4E_FEATURE_2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE;
		content = getSubsystemContents(null, result, SUBSYSTEM_4E_FEATURE_2);
		result.put(SUBSYSTEM_4E3B_COMPOSITE_1F, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4E3B_COMPOSITE_1F), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));

		preferredProvider.clear();
		preferredProvider.put(SubsystemConstants.PREFERRED_PROVIDER, 
				getSymbolicName(BUNDLE_SHARE_F) + "; type=" + IdentityNamespace.TYPE_BUNDLE + "," +
				getSymbolicName(BUNDLE_SHARE_G) + "; type=" + IdentityNamespace.TYPE_BUNDLE);
		contentHeader = 
				getSymbolicName(BUNDLE_SHARE_C) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_E) + "; version=\"[1.0,1.0]\"";
		result.put(SUBSYSTEM_4F1_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4F1_APPLICATION), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, null, preferredProvider));
		preferredProvider.put(Constants.IMPORT_PACKAGE, "x");
		preferredProvider.put(Constants.REQUIRE_CAPABILITY, "y; filter:=\"(y=test)\"");
		result.put(SUBSYSTEM_4F1_COMPOSITE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4F1_COMPOSITE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, null, preferredProvider));

		contentHeader = 
				getSymbolicName(BUNDLE_SHARE_F) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_G) + "; version=\"[1.0,1.0]\"";
		exportPolicy.clear();
		exportPolicy.put(Constants.EXPORT_PACKAGE, "x; version=1.0, does.not.exist; a=b");
		exportPolicy.put(Constants.PROVIDE_CAPABILITY, "y; y=test, does.not.exist; a=b");
		result.put(SUBSYSTEM_4F2_COMPOSITE_EXPORTER, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4F2_COMPOSITE_EXPORTER), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, null, exportPolicy));
		result.put(SUBSYSTEM_4F2_FEATURE_EXPORTER, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4F2_FEATURE_EXPORTER), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, null, null));

		preferredProvider.clear();
		preferredProvider.put(SubsystemConstants.PREFERRED_PROVIDER, getSymbolicName(SUBSYSTEM_4F2_COMPOSITE_EXPORTER));
		contentHeader = 
				getSymbolicName(BUNDLE_SHARE_C) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_E) + "; version=\"[1.0,1.0]\"";
		result.put(SUBSYSTEM_4F2_PREFER_COMP_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4F2_PREFER_COMP_APPLICATION), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, null, preferredProvider));
		preferredProvider.put(Constants.IMPORT_PACKAGE, "x");
		preferredProvider.put(Constants.REQUIRE_CAPABILITY, "y; filter:=\"(y=test)\"");
		result.put(SUBSYSTEM_4F2_PREFER_COMP_COMPOSITE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4F2_PREFER_COMP_COMPOSITE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, null, preferredProvider));

		preferredProvider.clear();
		preferredProvider.put(SubsystemConstants.PREFERRED_PROVIDER, 
				getSymbolicName(SUBSYSTEM_4F2_FEATURE_EXPORTER) + "; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE);
		contentHeader = 
				getSymbolicName(BUNDLE_SHARE_C) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_SHARE_E) + "; version=\"[1.0,1.0]\"";
		result.put(SUBSYSTEM_4F2_PREFER_FEAT_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4F2_PREFER_FEAT_APPLICATION), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, null, preferredProvider));
		preferredProvider.put(Constants.IMPORT_PACKAGE, "x");
		preferredProvider.put(Constants.REQUIRE_CAPABILITY, "y; filter:=\"(y=test)\"");
		result.put(SUBSYSTEM_4F2_PREFER_FEAT_COMPOSITE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4F2_PREFER_FEAT_COMPOSITE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, null, preferredProvider));

		contentHeader = getSymbolicName(BUNDLE_SHARE_C) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_SHARE_C);
		importPolicy.clear();
		importPolicy.put(Constants.IMPORT_PACKAGE, "x");
		result.put(SUBSYSTEM_4G1A_COMPOSITE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4G1A_COMPOSITE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));


		Map<String, String> dm = new HashMap<String, String>();

		dm.put(SubsystemConstants.DEPLOYED_CONTENT, 
				getSymbolicName(BUNDLE_NO_DEPS_A_V1) + "; " + SubsystemConstants.DEPLOYED_VERSION_ATTRIBUTE + "=\"1.0\", " + 
				getSymbolicName(BUNDLE_NO_DEPS_B_V1) + "; " + SubsystemConstants.DEPLOYED_VERSION_ATTRIBUTE + "=\"1.0\"");

		contentHeader = getSymbolicName(BUNDLE_NO_DEPS_A_V1) + ", " + getSymbolicName(BUNDLE_NO_DEPS_B_V1);

		result.put(SUBSYSTEM_5A_APPLICATION_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_5A_APPLICATION_S1), true, "1.0.0", null, false, contentHeader, null, null, dm));
		result.put(SUBSYSTEM_5A_FEATURE_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_5A_FEATURE_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, null, null, dm));

		contentHeader = getSymbolicName(BUNDLE_NO_DEPS_A_V1) + "; version=\"[1.0,1.0]\"," +
				getSymbolicName(BUNDLE_NO_DEPS_B_V1) + "; version=\"[1.0,1.0]\"";
		dm.put(SubsystemConstants.SUBSYSTEM_CONTENT, contentHeader);
		result.put(SUBSYSTEM_5A_COMPOSITE_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_5A_COMPOSITE_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, null, null, dm));

		dm.clear();
		dm.put(SubsystemConstants.DEPLOYED_CONTENT,
				getSymbolicName(BUNDLE_SHARE_C) + "; " + SubsystemConstants.DEPLOYED_VERSION_ATTRIBUTE + "=\"1.0\", " + 
				getSymbolicName(BUNDLE_SHARE_E) + "; " + SubsystemConstants.DEPLOYED_VERSION_ATTRIBUTE + "=\"1.0\"");
		dm.put(SubsystemConstants.PROVISION_RESOURCE,
				getSymbolicName(BUNDLE_SHARE_F) + "; " + SubsystemConstants.DEPLOYED_VERSION_ATTRIBUTE + "=\"1.0\", " + 
				getSymbolicName(BUNDLE_SHARE_G) + "; " + SubsystemConstants.DEPLOYED_VERSION_ATTRIBUTE + "=\"1.0\"");
		dm.put(Constants.IMPORT_PACKAGE, "x");
		dm.put(Constants.REQUIRE_CAPABILITY, "y; filter:=\"(y=test)\"");

		extraHeaders.clear();
		extraHeaders.put(Constants.IMPORT_PACKAGE, dm.get(Constants.IMPORT_PACKAGE));
		extraHeaders.put(Constants.REQUIRE_CAPABILITY, dm.get(Constants.REQUIRE_CAPABILITY));

		contentHeader = getSymbolicName(BUNDLE_SHARE_C) + "; version=\"[1.0,1.0]\", " + 
				getSymbolicName(BUNDLE_SHARE_E) + "; version=\"[1.0,1.0]\"";

		result.put(SUBSYSTEM_5B_APPLICATION_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_5B_APPLICATION_S1), true, "1.0.0", null, false, contentHeader, null, null, dm));
		result.put(SUBSYSTEM_5B_COMPOSITE_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_5B_COMPOSITE_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, null, extraHeaders, dm));

		dm.remove(Constants.IMPORT_PACKAGE);
		dm.remove(Constants.REQUIRE_BUNDLE);
		result.put(SUBSYSTEM_5B_FEATURE_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_5B_FEATURE_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, null, null, dm));


		result.put(SUBSYSTEM_6_EMPTY_APPLICATION_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6_EMPTY_APPLICATION_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, null, null, null));
		result.put(SUBSYSTEM_6_EMPTY_APPLICATION_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6_EMPTY_APPLICATION_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, null, null, null));
		result.put(SUBSYSTEM_6_EMPTY_COMPOSITE_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6_EMPTY_COMPOSITE_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, null, null, null));
		result.put(SUBSYSTEM_6_EMPTY_COMPOSITE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6_EMPTY_COMPOSITE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, null, null, null));
		result.put(SUBSYSTEM_6_EMPTY_FEATURE_A, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6_EMPTY_FEATURE_A), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, null, null, null));
		result.put(SUBSYSTEM_6_EMPTY_FEATURE_B, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6_EMPTY_FEATURE_B), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, null, null, null));
		result.put(SUBSYSTEM_6_EMPTY_FEATURE_C, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6_EMPTY_FEATURE_C), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, null, null, null));

		contentHeader = getSymbolicName(BUNDLE_NO_DEPS_A_V1) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1);
		result.put(SUBSYSTEM_6A3_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6A3_APPLICATION), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_6A3_COMPOSITE, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6A3_COMPOSITE), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));
		result.put(SUBSYSTEM_6A_FEATURE1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6A_FEATURE1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));
		result.put(SUBSYSTEM_6A_FEATURE2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6A_FEATURE2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_6A_FEATURE1) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE + "," +
				getSymbolicName(SUBSYSTEM_6A_FEATURE2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE;
		content = getSubsystemContents(null, result, SUBSYSTEM_6A_FEATURE1, SUBSYSTEM_6A_FEATURE2);
		result.put(SUBSYSTEM_6A4_APPLICATION1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6A4_APPLICATION1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_6A4_COMPOSITE1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6A4_COMPOSITE1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_6A_FEATURE1) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE + "," +
				getSymbolicName(BUNDLE_NO_DEPS_A_V1) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1);
		content = getSubsystemContents(content, result, SUBSYSTEM_6A_FEATURE1, SUBSYSTEM_6A_FEATURE2);
		result.put(SUBSYSTEM_6A4_APPLICATION2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6A4_APPLICATION2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_6A4_COMPOSITE2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_6A4_COMPOSITE2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));

		contentHeader = getSymbolicName(BUNDLE_NO_DEPS_C_V1) + "; version=\"[1.0,1.0]\", " + getSymbolicName(BUNDLE_NO_DEPS_D_V1) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_NO_DEPS_C_V1, BUNDLE_NO_DEPS_D_V1);
		result.put(SUBSYSTEM_7_APPLICATION_S2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_APPLICATION_S2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_COMPOSITE_S2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_COMPOSITE_S2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_FEATURE_S2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_FEATURE_S2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_7_APPLICATION_S2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION + "," +
				getSymbolicName(BUNDLE_NO_DEPS_A_V1) + "; version=\"[1.0,1.0]\", " + getSymbolicName(BUNDLE_NO_DEPS_B_V1) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1);
		content = getSubsystemContents(content, result, SUBSYSTEM_7_APPLICATION_S2);
		result.put(SUBSYSTEM_7_APPLICATION_A_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_APPLICATION_A_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_COMPOSITE_A_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_COMPOSITE_A_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_FEATURE_A_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_FEATURE_A_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_7_COMPOSITE_S2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE + "," +
				getSymbolicName(BUNDLE_NO_DEPS_A_V1) + "; version=\"[1.0,1.0]\", " + getSymbolicName(BUNDLE_NO_DEPS_B_V1) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1);
		content = getSubsystemContents(content, result, SUBSYSTEM_7_COMPOSITE_S2);
		result.put(SUBSYSTEM_7_APPLICATION_C_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_APPLICATION_C_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_COMPOSITE_C_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_COMPOSITE_C_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_FEATURE_C_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_FEATURE_C_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		contentHeader = getSymbolicName(SUBSYSTEM_7_FEATURE_S2) + "; version=\"[1.0,1.0]\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE + "," +
				getSymbolicName(BUNDLE_NO_DEPS_A_V1) + "; version=\"[1.0,1.0]\", " + getSymbolicName(BUNDLE_NO_DEPS_B_V1) + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1);
		content = getSubsystemContents(content, result, SUBSYSTEM_7_FEATURE_S2);
		result.put(SUBSYSTEM_7_APPLICATION_F_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_APPLICATION_F_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_COMPOSITE_F_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_COMPOSITE_F_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_FEATURE_F_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_FEATURE_F_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		contentHeader = 
				getSymbolicName(BUNDLE_NO_DEPS_D_V1) + "; version=\"[1.0,1.0]\"; start-order:=\"20\", " + 
				getSymbolicName(BUNDLE_NO_DEPS_E_V1) + "; version=\"[1.0,1.0]\"; start-order:=\"10\"," + 
				getSymbolicName(BUNDLE_NO_DEPS_F_V1) + "; version=\"[1.0,1.0]\"; start-order:=\"30\"";
		content = getBundleContents(null, BUNDLE_NO_DEPS_D_V1, BUNDLE_NO_DEPS_E_V1, BUNDLE_NO_DEPS_F_V1);
		result.put(SUBSYSTEM_7_ORDERED_APPLICATION_S2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_ORDERED_APPLICATION_S2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_ORDERED_COMPOSITE_S2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_ORDERED_COMPOSITE_S2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_ORDERED_FEATURE_S2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_ORDERED_FEATURE_S2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		contentHeader = 
				getSymbolicName(SUBSYSTEM_7_ORDERED_APPLICATION_S2) + "; version=\"[1.0,1.0]\"; start-order:=\"20\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION + ", " +
				getSymbolicName(BUNDLE_NO_DEPS_A_V1) + "; version=\"[1.0,1.0]\"; start-order:=\"40\", " + 
				getSymbolicName(BUNDLE_NO_DEPS_B_V1) + "; version=\"[1.0,1.0]\"; start-order:=\"10\", " + 
				getSymbolicName(BUNDLE_NO_DEPS_C_V1) + "; version=\"[1.0,1.0]\"; start-order:=\"30\"";
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1, BUNDLE_NO_DEPS_C_V1);
		content = getSubsystemContents(content, result, SUBSYSTEM_7_ORDERED_APPLICATION_S2);
		result.put(SUBSYSTEM_7_ORDERED_APPLICATION_A_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_ORDERED_APPLICATION_A_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_ORDERED_COMPOSITE_A_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_ORDERED_COMPOSITE_A_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_ORDERED_FEATURE_A_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_ORDERED_FEATURE_A_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		contentHeader = 
				getSymbolicName(SUBSYSTEM_7_ORDERED_COMPOSITE_S2) + "; version=\"[1.0,1.0]\"; start-order:=\"20\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE + ", " +
				getSymbolicName(BUNDLE_NO_DEPS_A_V1) + "; version=\"[1.0,1.0]\"; start-order:=\"40\", " + 
				getSymbolicName(BUNDLE_NO_DEPS_B_V1) + "; version=\"[1.0,1.0]\"; start-order:=\"10\", " + 
				getSymbolicName(BUNDLE_NO_DEPS_C_V1) + "; version=\"[1.0,1.0]\"; start-order:=\"30\"";
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1, BUNDLE_NO_DEPS_C_V1);
		content = getSubsystemContents(content, result, SUBSYSTEM_7_ORDERED_COMPOSITE_S2);
		result.put(SUBSYSTEM_7_ORDERED_APPLICATION_C_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_ORDERED_APPLICATION_C_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_ORDERED_COMPOSITE_C_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_ORDERED_COMPOSITE_C_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_ORDERED_FEATURE_C_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_ORDERED_FEATURE_C_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		contentHeader = 
				getSymbolicName(SUBSYSTEM_7_ORDERED_FEATURE_S2) + "; version=\"[1.0,1.0]\"; start-order:=\"20\"; type=" + SubsystemConstants.SUBSYSTEM_TYPE_FEATURE + ", " +
				getSymbolicName(BUNDLE_NO_DEPS_A_V1) + "; version=\"[1.0,1.0]\"; start-order:=\"40\", " + 
				getSymbolicName(BUNDLE_NO_DEPS_B_V1) + "; version=\"[1.0,1.0]\"; start-order:=\"10\", " + 
				getSymbolicName(BUNDLE_NO_DEPS_C_V1) + "; version=\"[1.0,1.0]\"; start-order:=\"30\"";
		content = getBundleContents(null, BUNDLE_NO_DEPS_A_V1, BUNDLE_NO_DEPS_B_V1, BUNDLE_NO_DEPS_C_V1);
		content = getSubsystemContents(content, result, SUBSYSTEM_7_ORDERED_FEATURE_S2);
		result.put(SUBSYSTEM_7_ORDERED_APPLICATION_F_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_ORDERED_APPLICATION_F_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_ORDERED_COMPOSITE_F_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_ORDERED_COMPOSITE_F_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, content, null));
		result.put(SUBSYSTEM_7_ORDERED_FEATURE_F_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7_ORDERED_FEATURE_F_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, content, null));

		importPolicy.clear();
		importPolicy.put(Constants.IMPORT_PACKAGE, "x");

		contentHeader = getSymbolicName(BUNDLE_SHARE_C) + "; version=\"[1.0,1.0]\"";
		result.put(SUBSYSTEM_7G_APPLICATION_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7G_APPLICATION_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, null, null));
		result.put(SUBSYSTEM_7G_COMPOSITE_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7G_COMPOSITE_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, null, importPolicy));
		result.put(SUBSYSTEM_7G_FEATURE_S1, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7G_FEATURE_S1), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, null, null));

		importPolicy.clear();
		importPolicy.put(Constants.REQUIRE_BUNDLE, getSymbolicName(BUNDLE_SHARE_A));
		importPolicy.put(Constants.REQUIRE_CAPABILITY, "y; filter:=\"(y=test)\"");

		contentHeader = getSymbolicName(BUNDLE_SHARE_D) + "; version=\"[1.0,1.0]\", " +
				getSymbolicName(BUNDLE_SHARE_E) + "; version=\"[1.0,1.0]\", ";
		result.put(SUBSYSTEM_7G_APPLICATION_S2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7G_APPLICATION_S2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, null, null));
		result.put(SUBSYSTEM_7G_COMPOSITE_S2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7G_COMPOSITE_S2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE, false, contentHeader, null, importPolicy));
		result.put(SUBSYSTEM_7G_FEATURE_S2, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_7G_FEATURE_S2), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, false, contentHeader, null, null));

		contentHeader = getSymbolicName(BUNDLE_SHARE_I) + "; version=\"[1.0,1.0]\", " +
				getSymbolicName(BUNDLE_SHARE_J)  + "; version=\"[1.0,1.0]\", " +
				getSymbolicName(BUNDLE_SHARE_K)  + "; version=\"[1.0,1.0]\"";
		content = getBundleContents(null, BUNDLE_SHARE_I, BUNDLE_SHARE_J, BUNDLE_SHARE_K); 
		result.put(SUBSYSTEM_4H_APPLICATION, new SubsystemInfo(new File(testSubsystemRoots, SUBSYSTEM_4H_APPLICATION), true, "1.0.0", SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION, false, contentHeader, content, null));
		
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

		subsystemResources = 
				Arrays.asList(
						testSubsystems.get(SUBSYSTEM_2B2g_S1_APPLICATION).getSubsystemResource());
		Repository repo2B2g = new TestRepository(subsystemResources);
		result.put(REPOSITORY_2B2g, repo2B2g);

		Map<String, URL> v2bundles = getBundleContents(null, BUNDLE_NO_DEPS_A_V2, BUNDLE_NO_DEPS_B_V2, BUNDLE_NO_DEPS_C_V2);
		resources = createTestResources(v2bundles);
		Repository rNoDepsV2 = new TestRepository(resources.values());
		result.put(REPOSITORY_NODEPS_V2, rNoDepsV2);

		// Creating a silly invalid content resource; just using a bundle as the URL content
		Map<String, Object> identityAttrs = new HashMap<String, Object>();
		identityAttrs.put(IdentityNamespace.IDENTITY_NAMESPACE, getSymbolicName(INVALID_TYPE));
		identityAttrs.put(IdentityNamespace.CAPABILITY_VERSION_ATTRIBUTE, new Version(1,0,0));
		identityAttrs.put(IdentityNamespace.CAPABILITY_TYPE_ATTRIBUTE, getSymbolicName(INVALID_TYPE));
		TestResource invalidType = new TestResource(identityAttrs, null, getContext().getBundle().getEntry(BUNDLE_NO_DEPS_A_V1));
		Repository invalidTypeRepo = new TestRepository(Arrays.asList(invalidType));
		result.put(REPOSITORY_INVALID_TYPE, invalidTypeRepo);

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
		return SubsystemInfo.getSymbolicName(namedResource);
	}
}
