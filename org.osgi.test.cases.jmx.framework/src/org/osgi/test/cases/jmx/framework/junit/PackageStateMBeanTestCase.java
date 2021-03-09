/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.jmx.framework.junit;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.jmx.framework.PackageStateMBean;
import org.osgi.test.support.wiring.Wiring;

public class PackageStateMBeanTestCase extends MBeanGeneralTestCase {

	private PackageStateMBean pMBean;
	private static final String EXPORTED_PACKAGE = "org.osgi.test.cases.jmx.framework.tb2.api";
	private Bundle testBundle1;
	private Bundle testBundle2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testBundle2 = super.install("tb2.jar");
		testBundle2.start();

		testBundle1 = super.install("tb1.jar");
		testBundle1.start();

		super
				.waitForRegistering(createObjectName(PackageStateMBean.OBJECTNAME));
		pMBean = getMBeanFromServer(PackageStateMBean.OBJECTNAME,
				PackageStateMBean.class);
		assertNotNull(pMBean);
	}

	public void testGetExportingBundle() throws IOException {
		String bundleVersion = testBundle1.getHeaders().get(
				"Bundle-Version");

		long[] mBeanBundleIds = pMBean.getExportingBundles(EXPORTED_PACKAGE,
				bundleVersion);
		List<BundleCapability> exportedPackages = Wiring.getExportedPackages(
				getContext(), EXPORTED_PACKAGE);
		boolean found = false;
		for (BundleCapability exportPack : exportedPackages) {
			for (long id : mBeanBundleIds) {
				if (exportPack.getRevision().getBundle().getBundleId() == id) {
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		assertTrue(
				"was not able to find the right bundle that exports package "
						+ EXPORTED_PACKAGE + "; exptected bundle "
						+ testBundle2.getSymbolicName(), found);
	}

	public void testGetImportingBundles() throws IOException {
		String bundleVersion = testBundle2.getHeaders().get(
				"Bundle-Version");
		long[] bundleIds = pMBean.getImportingBundles(EXPORTED_PACKAGE,
				bundleVersion, testBundle2.getBundleId());
		boolean found = false;
		for (long bundleId : bundleIds) {
			if (bundleId == testBundle1.getBundleId()) {
				found = true;
				break;
			}
		}
		assertTrue(
				"was not able to find the right bundle that imports package "
						+ EXPORTED_PACKAGE + ": expected bundle "
						+ testBundle1.getSymbolicName(), found);
	}

	public void testGetPackages() throws IOException {
		TabularData data = pMBean.listPackages();
		assertTabularDataStructure(data, "PACKAGES_TYPE", new String [] {"Name", "Version", "ExportingBundles"},
											 new String[] {"Name", "Version", "ExportingBundles", "ImportingBundles", "RemovalPending"});
		Collection< ? > values = data.values();
		Iterator< ? > iter = values.iterator();
		boolean found = false;
		while (iter.hasNext()) {
			CompositeData item = (CompositeData) iter.next();
			String name = (String) item.get("Name");
			if (name.equals(EXPORTED_PACKAGE)) {
				Long[] exportingBundles = (Long[]) item.get("ExportingBundles");
				Long[] importingBundles = (Long[]) item.get("ImportingBundles");
				assertTrue("package exporting bundles info is wrong", (exportingBundles != null) && (exportingBundles.length == 1) &&
																		(exportingBundles[0] == testBundle2.getBundleId()));
				assertTrue("package importing bundles info is wrong", (importingBundles != null) && (importingBundles.length == 1) &&
						(importingBundles[0] == testBundle1.getBundleId()));
				found = true;
			}
		}
		assertTrue("package not found in returned packages", found);
	}

	public void testIsRemovalPending() throws IOException {
		String bundleVersion = testBundle2.getHeaders().get(
				"Bundle-Version");

		boolean mBeanRemovalPending = pMBean.isRemovalPending(EXPORTED_PACKAGE,
				bundleVersion, testBundle2.getBundleId());
		boolean bundleStateRemovalPending = testBundle2.getState() == Bundle.STOPPING;

		assertTrue(
				"received inconsistent state information for bundle exporting package "
						+ EXPORTED_PACKAGE + "."
						+ testBundle2.getSymbolicName(),
				mBeanRemovalPending == bundleStateRemovalPending);

	}

	public void testExceptions() {
		//test listPackages method
		try {
			pMBean.listPackages();
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}

		//test getExportingBundles method
		try {
			pMBean.getExportingBundles(STRING_NULL, STRING_NULL);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			pMBean.getExportingBundles(STRING_EMPTY, STRING_EMPTY);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			pMBean.getExportingBundles(STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS);
		}
		catch (IOException e) {
			fail("unexpected exception", e);
		}
		catch (IllegalArgumentException e) {
			fail("unexpected exception", e);
		}

		//test getImportingBundles method
		try {
			pMBean.getImportingBundles(STRING_NULL, STRING_NULL, LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			pMBean.getImportingBundles(STRING_EMPTY, STRING_SPECIAL_SYMBOLS, LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}

		//test isRemovalPending method
		try {
			pMBean.isRemovalPending(STRING_NULL, STRING_NULL, LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
		try {
			pMBean.isRemovalPending(STRING_EMPTY, STRING_SPECIAL_SYMBOLS, LONG_NEGATIVE);
			fail("expected exception");
		}
		catch (IOException e) {
			// expected
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(PackageStateMBean.OBJECTNAME));
		if (testBundle1 != null) {
			try {
				super.uninstallBundle(testBundle1);
			} catch (Exception io) {}
		}
		if (testBundle2 != null) {
			try {
				super.uninstallBundle(testBundle2);
			} catch (Exception io) {}
		}
	}
}
