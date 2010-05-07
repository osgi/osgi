/*
 * Copyright (c) OSGi Alliance (2000-2009).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.residentialmanagement;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;

import info.dmtree.DmtAdmin;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtIllegalStateException;
import info.dmtree.DmtSession;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.*;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class PackageStatePluginTestCase extends DefaultTestBundleControl {

	static final String INSTANCE_ID = "1";
	static final String PLUGIN_ROOT_URI = "./OSGi/1/PackageState";

	protected static final String NAME = "Name";
	protected static final String VERSION = "Version";
	protected static final String REMOVALPENDING = "RemovalPending";
	protected static final String EXPORTINGBUNDLE = "ExportingBundle";
	protected static final String IMPORTINGBUNDLES = "ImportingBundles";

	protected static final String TESTBUNDLELOCATION1 = "org.osgi.test.cases.residentialmanagement.tb1.jar";
	protected static final String TESTBUNDLELOCATION2 = "org.osgi.test.cases.residentialmanagement.tb2.jar";
	protected static final String TESTBUNDLELOCATION6 = "org.osgi.test.cases.residentialmanagement.tb6.jar";
	protected static final String TESTPACKAGENAME = "org.osgi.test.cases.residentialmanagement.sharedpackage";
	protected static final String TESTPACKAGE1_VERSION = "1.0.0";
	protected static final String TESTPACKAGE6_VERSION = "1.1.0";
	protected static final String FRAMEWORKPACKAGE = "org.osgi.framework";
	protected static final String SYSTEMPACKAGE = "org.osgi.test.cases.residentialmanagement.syspkg";
	protected static final int SLEEP_TIME = 3000;

	private BundleContext context;
	private DmtAdmin dmtAdmin;
	private PackageAdmin pkgAdmin;
	private Bundle testBundle1 = null;
	private Bundle testBundle2 = null;
	private String[] children;
	private String[] ids;
	private String childName;
	private String childVer;
	private String targetIdNumber = null;
	private boolean checkFlag = false;
	private boolean packageRP = false;
	private DmtSession session;

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	protected void setUp() throws DmtException {
		ServiceReference dmtServiceRef = context
				.getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) context.getService(dmtServiceRef);
		ServiceReference pkgServiceRef = context
				.getServiceReference(PackageAdmin.class.getName());
		pkgAdmin = (PackageAdmin) context.getService(pkgServiceRef);
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_SHARED);
	}

	protected void tearDown() throws BundleException, InterruptedException {
		checkFlag = false;
		packageRP = false;
		childName = null;
		childVer = null;
		if (this.testBundle1 != null
				&& testBundle1.getState() != Bundle.UNINSTALLED)
			this.testBundle1.uninstall();
		this.testBundle1 = null;
		if (this.testBundle2 != null
				&& testBundle2.getState() != Bundle.UNINSTALLED)
			this.testBundle2.uninstall();
		this.testBundle2 = null;
		pkgAdmin.refreshPackages(null);
		Thread.sleep(SLEEP_TIME);
	}

	public void testPackageStateNodeArchitecuture() throws DmtException {
		// 1st descendants
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		Bundle bundle = null;
		ExportedPackage[] exportedPackages = pkgAdmin
				.getExportedPackages(bundle);
		assertEquals("Lack of Node in the PackageState Plugin.",
				exportedPackages.length, ids.length);

		// 2nd descendants
		Hashtable expected = new Hashtable();
		for (int i = 0; i < ids.length; i++) {
			expected.put(NAME, "");
			expected.put(VERSION, "");
			expected.put(REMOVALPENDING, "");
			expected.put(EXPORTINGBUNDLE, "");
			expected.put(IMPORTINGBUNDLES, "");
			children = session
					.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]);
			assertNotNull("This object should not be null.", children);
			for (int j = 0; j < children.length; j++) {
				expected.remove(children[j]);
			}
			assertEquals(
					"There are undefined nodes in the PackageState Plugin.", 0,
					expected.size());

		}
		session.close();
	}

	public void testPackageStateNodeCreation() throws DmtException,
			BundleException, IOException, InterruptedException {
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			childName = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + i + "/" + NAME).toString();
			childVer = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + i + "/" + VERSION).toString();
			if (childName.equals(TESTPACKAGENAME)
					&& childVer.equals(TESTPACKAGE1_VERSION)) {
				fail("Test package1 must not exist.");
			}
		}
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			childName = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + i + "/" + NAME).toString();
			childVer = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + i + "/" + VERSION).toString();
			if (childName.equals(TESTPACKAGENAME)
					&& childVer.equals(TESTPACKAGE1_VERSION)) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		session.close();
	}

	public void testPackageStateNodeDelete1() throws DmtException,
			BundleException, IOException, InterruptedException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		testBundle1.uninstall();
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			childName = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).toString();
			childVer = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + VERSION).toString();
			if (childName.equals(TESTPACKAGENAME)
					&& childVer.equals(TESTPACKAGE1_VERSION)) {
				assertTrue(false);
			}
		}
		session.close();
	}

	public void testPackageStateNodeDelete2() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		testBundle2 = installAndStartBundle(TESTBUNDLELOCATION2);
		testBundle1.uninstall();
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			childName = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).toString();
			childVer = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + VERSION).toString();
			if (childName.equals(TESTPACKAGENAME)
					&& childVer.equals(TESTPACKAGE1_VERSION)) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		session.close();
	}

	public void testPackageStateNodeDelete3() throws DmtException,
			BundleException, IOException, InterruptedException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		testBundle2 = installAndStartBundle(TESTBUNDLELOCATION2);
		testBundle1.uninstall();
		pkgAdmin.refreshPackages(null);
		Thread.sleep(SLEEP_TIME);
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			childName = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).getString();
			childVer = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + VERSION).getString();
			if (childName.equals(TESTPACKAGENAME)
					&& childVer.equals(TESTPACKAGE1_VERSION)) {
				assertTrue(false);
			}
		}
		session.close();
	}

	public void testPackageStateNodeAccess() throws DmtException {
		// ReadOnlyNode
		try {
			DmtData data = session.getNodeValue(PLUGIN_ROOT_URI + "/0/" + NAME);
			data.getString();

			data = session.getNodeValue(PLUGIN_ROOT_URI + "/0/" + VERSION);
			data.getString();

			data = session.getNodeValue(PLUGIN_ROOT_URI + "/0/"
					+ REMOVALPENDING);
			data.getBoolean();

			data = session.getNodeValue(PLUGIN_ROOT_URI + "/0/"
					+ EXPORTINGBUNDLE);
			data.getString();

			data = session.getNodeValue(PLUGIN_ROOT_URI + "/0/"
					+ IMPORTINGBUNDLES);
			data.getString();

		} catch (DmtIllegalStateException e) {
			fail("Leaf node contains illegal format values.");
		} catch (DmtException e) {
			fail("Can not get a leaf node's value.");
		}
		session.close();

		// Write operation must be fail.
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);

		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/0/" + NAME, new DmtData(
					"test"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;

		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/0/" + VERSION,
					new DmtData("1.1.1"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;

		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/0/" + REMOVALPENDING,
					new DmtData(1));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;

		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/0/" + EXPORTINGBUNDLE,
					new DmtData("1"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;

		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/0/" + IMPORTINGBUNDLES,
					new DmtData("2,3,4"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;

		session.close();
	}

	public void testPackageStateExportedByFrameworkNode() throws DmtException,
			BundleException, IOException {
		// Id and Name node check
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			String packageName = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).getString();
			if (packageName.equals(FRAMEWORKPACKAGE)) {
				targetIdNumber = ids[i];
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		checkFlag = false;
		// Version node check
		String packageVer = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + targetIdNumber + "/" + VERSION)
				.getString();
		ExportedPackage[] pkgVerComp = pkgAdmin
				.getExportedPackages(FRAMEWORKPACKAGE);
		assertNotNull("This object should not be null.", pkgVerComp);
		for (int i = 0; i < pkgVerComp.length; i++) {
			if (packageVer.equals(pkgVerComp[i].getVersion().toString())) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		checkFlag = false;

		// RemovalPending node check
		packageRP = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + targetIdNumber + "/" + REMOVALPENDING)
				.getBoolean();
		if (packageRP) {
			fail("This value of RemovalPending must be false.");
		}

		// ExportingBundle node check
		String exBundleId = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + targetIdNumber + "/" + EXPORTINGBUNDLE)
				.getString();
		if (!exBundleId.equals("0")) {
			fail("This exporting bundle is system bundle(id=0).");
		}

		// ImportingBundles node check
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		String imBundleIds = session
				.getNodeValue(
						PLUGIN_ROOT_URI + "/" + targetIdNumber + "/"
								+ IMPORTINGBUNDLES).getString();
		String[] imBundleIdsArray = processCommaSeparatedValue(imBundleIds);
		assertNotNull("This object should not be null.", imBundleIdsArray);
		for (int i = 0; i < imBundleIdsArray.length; i++) {
			if (imBundleIdsArray[i].equals(Long.toString(testBundle1
					.getBundleId()))) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		session.close();
	}

	public void testPackageStateExportedBySystemPackagekNode()
			throws DmtException, BundleException, IOException {
		// Id and Name node check
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			String packageName = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).getString();
			if (packageName.equals(SYSTEMPACKAGE)) {
				targetIdNumber = ids[i];
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		checkFlag = false;
		// Version node check
		String packageVer = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + targetIdNumber + "/" + VERSION)
				.getString();
		ExportedPackage[] pkgVerComp = pkgAdmin
				.getExportedPackages(SYSTEMPACKAGE);
		assertNotNull("This object should not be null.", pkgVerComp);
		for (int i = 0; i < pkgVerComp.length; i++) {
			if (packageVer.equals(pkgVerComp[i].getVersion().toString())) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		checkFlag = false;

		// RemovalPending node check
		packageRP = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + targetIdNumber + "/" + REMOVALPENDING)
				.getBoolean();
		if (packageRP) {
			fail("This value of RemovalPending must be false.");
		}

		// ExportingBundle node check
		String exBundleId = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + targetIdNumber + "/" + EXPORTINGBUNDLE)
				.getString();
		if (!exBundleId.equals("0")) {
			fail("This exporting bundle is system bundle(id=0).");
		}

		// ImportingBundles node check
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		testBundle2 = installAndStartBundle(TESTBUNDLELOCATION2);
		String imBundleIds = session
				.getNodeValue(
						PLUGIN_ROOT_URI + "/" + targetIdNumber + "/"
								+ IMPORTINGBUNDLES).getString();
		String[] imBundleIdsArray = processCommaSeparatedValue(imBundleIds);
		assertNotNull("This object should not be null.", imBundleIdsArray);
		for (int i = 0; i < imBundleIdsArray.length; i++) {
			if (imBundleIdsArray[i].equals(Long.toString(testBundle2
					.getBundleId()))) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		session.close();

	}

	public void testPackageStateExportedByBundleNode() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		testBundle2 = installAndStartBundle(TESTBUNDLELOCATION2);
		// Id and Name node check
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			String packageName = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).getString();
			if (packageName.equals(TESTPACKAGENAME)) {
				targetIdNumber = ids[i];
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		checkFlag = false;
		// Version node check
		String packageVer = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + targetIdNumber + "/" + VERSION)
				.getString();
		ExportedPackage[] pkgVerComp = pkgAdmin
				.getExportedPackages(TESTPACKAGENAME);
		assertNotNull("This object should not be null.", pkgVerComp);
		for (int i = 0; i < pkgVerComp.length; i++) {
			if (packageVer.equals(pkgVerComp[i].getVersion().toString())) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		checkFlag = false;

		// RemovalPending node check
		packageRP = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + targetIdNumber + "/" + REMOVALPENDING)
				.getBoolean();
		assertFalse(packageRP);

		// ExportingBundle node check
		String exBundleId = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + targetIdNumber + "/" + EXPORTINGBUNDLE)
				.getString();
		assertTrue(exBundleId.equals(Long.toString(testBundle1.getBundleId())));

		// ImportingBundles node check
		String currentImBundleId = Long.toString(testBundle2.getBundleId());

		String imBundleIds = session
				.getNodeValue(
						PLUGIN_ROOT_URI + "/" + targetIdNumber + "/"
								+ IMPORTINGBUNDLES).getString();
		String[] imBundleIdsArray = processCommaSeparatedValue(imBundleIds);
		assertNotNull("This object should not be null.", imBundleIdsArray);
		for (int i = 0; i < imBundleIdsArray.length; i++) {
			if (imBundleIdsArray[i].equals(currentImBundleId)) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		session.close();

	}

	public void testPackageStateNodeUpdate1() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		testBundle2 = installAndStartBundle(TESTBUNDLELOCATION2);
		this.updateBundle(TESTBUNDLELOCATION6, testBundle1);
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			childName = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).toString();
			childVer = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + VERSION).toString();
			if (childName.equals(TESTPACKAGENAME)
					&& childVer.equals(TESTPACKAGE1_VERSION)) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		session.close();
	}

	//FW�������ĂȂ��̂ŁCTC����O���D
	public void suspendedTestPackageStateNodeUpdate2() throws DmtException,
			BundleException, IOException, InterruptedException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		testBundle2 = installAndStartBundle(TESTBUNDLELOCATION2);
		
		System.out.println("First----------------------------------------------------------------");
		
		ExportedPackage[] exportedPackage1 = pkgAdmin.getExportedPackages(testBundle1);
		for(int i=0 ;i<exportedPackage1.length;i++){
			System.out.println("###Bundle1 : "+exportedPackage1[i].getVersion().toString());
		}
		this.updateBundle(TESTBUNDLELOCATION6, testBundle1);
		
		System.out.println("Second----------------------------------------------------------------");
		
		exportedPackage1 = pkgAdmin.getExportedPackages(testBundle1);
		for(int i=0 ;i<exportedPackage1.length;i++){
			System.out.println("###Bundle1 : "+exportedPackage1[i].getVersion().toString());
		}
		
		System.out.println("Third----------------------------------------------------------------");
		
		pkgAdmin.refreshPackages(null);
		Thread.sleep(SLEEP_TIME);
		
		System.out.println("###Bundle1SymbolicName : "+testBundle1.getSymbolicName());

		exportedPackage1 = pkgAdmin.getExportedPackages(testBundle1);
		for(int i=0 ;i<exportedPackage1.length;i++){
			System.out.println("###Bundle1 : "+exportedPackage1[i].getVersion().toString());
		}
		System.out.println("End of checking----------------------------------------------------------------");
		
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			childName = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).toString();
			childVer = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + VERSION).toString();
			System.out.println("##Name##"+childName);
			System.out.println("##Ver##"+childVer);
			
			if (childName.equals(TESTPACKAGENAME)
					&& childVer.equals(TESTPACKAGE1_VERSION)) {
				assertTrue(false);
			} else if (childName.equals(TESTPACKAGENAME)
					&& childVer.equals(TESTPACKAGE6_VERSION)) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		session.close();
	}

	private Bundle installAndStartBundle(String location) throws IOException,
			BundleException {
		URL url = context.getBundle().getResource(location);
		InputStream is = url.openStream();
		Bundle bundle = context.installBundle(location, is);
		bundle.start();
		is.close();
		return bundle;
	}

	private void updateBundle(String location, Bundle bundle)
			throws IOException, BundleException {
		URL url = context.getBundle().getResource(location);
		InputStream is = url.openStream();
		bundle.update(is);
		is.close();
	}

	private String[] processCommaSeparatedValue(String value) {
		StringTokenizer st = new StringTokenizer(value, ",");
		String[] arrayValue = new String[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			arrayValue[i] = st.nextToken();
		}
		if (arrayValue.length == 0)
			return null;
		return arrayValue;
	}

}
