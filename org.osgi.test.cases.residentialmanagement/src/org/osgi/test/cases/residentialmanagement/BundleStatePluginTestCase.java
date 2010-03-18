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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable; //import java.util.Map;
import java.util.Iterator;
import java.util.Map;
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
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class BundleStatePluginTestCase extends DefaultTestBundleControl {

	static final String INSTANCE_ID = "1";
	static final String PLUGIN_ROOT_URI = "./OSGi/1/BundleState";

	protected static final String SYMBOLICNAME = "SymbolicName";
	protected static final String VERSION = "Version";
	protected static final String BUNDLETYPE = "BundleType";
	protected static final String MANIFEST = "Manifest";
	protected static final String STATUS = "Status";
	protected static final String HOST = "Host";
	protected static final String FRAGMENTS = "Fragments";
	protected static final String REQUIRED = "Required";
	protected static final String REQUIRING = "Requiring";
	protected static final String BUNDLESTATEEXT = "BundleStateExt";
	protected static final String LOCATION = "Location";
	protected static final String STARTLEVEL = "StartLevel";
	protected static final String STATE = "State";
	protected static final String PERSISTENTLYSTARTED = "PersistentlyStarted";
	protected static final String LASTMODIFIED = "LastModified";
	private static final String TRUSTEDSIGNERCERTIFICATIONS = "TrustedSignerCertifications";
	private static final String NONTRUSTEDSIGNERCERTIFICATIONS = "NonTrustedSignerCertifications";

	protected static final String TESTBUNDLELOCATION1 = "org.osgi.test.cases.residentialmanagement.tb1.jar";
	protected static final String TESTBUNDLELOCATION2 = "org.osgi.test.cases.residentialmanagement.tb7.jar";
	protected static final String TESTBUNDLELOCATION3 = "org.osgi.test.cases.residentialmanagement.tb8.jar";
	protected static final String TESTBUNDLELOCATION4 = "org.osgi.test.cases.residentialmanagement.tb9.jar";
	protected static final String TESTBUNDLESYMBOLICNAME1 = "org.osgi.test.cases.residentialmanagement.tb1.jar";

	private DmtAdmin dmtAdmin;
	private PackageAdmin packageAdmin;
	private StartLevel startLevel;
	private DmtSession session;
	private BundleContext context;
	private boolean checkFlag = false;

	private Bundle testBundle1 = null;
	private Bundle testBundle2 = null;
	private Bundle testBundle3 = null;
	private Bundle testBundle4 = null;

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	protected void setUp() throws DmtException {
		ServiceReference dmtServiceRef = context
				.getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) context.getService(dmtServiceRef);
		ServiceReference pkgServiceRef = context
				.getServiceReference(PackageAdmin.class.getName());
		packageAdmin = (PackageAdmin) context.getService(pkgServiceRef);
		ServiceReference slvServiceRef = context
				.getServiceReference(StartLevel.class.getName());
		startLevel = (StartLevel) context.getService(slvServiceRef);
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_SHARED);
	}

	protected void tearDown() throws DmtException, BundleException {
		if (this.testBundle1 != null
				&& testBundle1.getState() != Bundle.UNINSTALLED)
			this.testBundle1.uninstall();
		this.testBundle1 = null;
		if (this.testBundle2 != null
				&& testBundle2.getState() != Bundle.UNINSTALLED)
			this.testBundle2.uninstall();
		this.testBundle2 = null;
		if (this.testBundle3 != null
				&& testBundle3.getState() != Bundle.UNINSTALLED)
			this.testBundle3.uninstall();
		this.testBundle3 = null;
		if (this.testBundle4 != null
				&& testBundle4.getState() != Bundle.UNINSTALLED)
			this.testBundle4.uninstall();
		this.testBundle4 = null;
		session.close();
		dmtAdmin = null;
		checkFlag = false;

	}

	public void testBundleStateNodeArchitecture() throws DmtException {
		// 1st descendants
		String[] children;
		Hashtable bundleIdTable = new Hashtable();
		Bundle[] bundles = context.getBundles();
		assertNotNull("This object should not be null.", bundles);
		for (int i = 0; i < bundles.length; i++) {
			long bundleId = bundles[i].getBundleId();
			String bundleIdStr = Long.toString(bundleId);
			bundleIdTable.put(bundleIdStr, "");
		}
		String[] bundleIds = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertFalse("These objects must exist.", bundleIds.equals(""));
		for (int i = 0; i < bundleIds.length; i++) {
			bundleIdTable.remove(bundleIds[i]);
		}
		assertEquals("Lack of Node in the BundleState Plugin.", 0,
				bundleIdTable.size());
		bundleIdTable = null;

		// 2nd descendants
		Hashtable expectedSecond = new Hashtable();
		for (int i = 0; i < bundleIds.length; i++) {
			expectedSecond.put(SYMBOLICNAME, "");
			expectedSecond.put(VERSION, "");
			expectedSecond.put(BUNDLETYPE, "");
			expectedSecond.put(MANIFEST, "");
			expectedSecond.put(STATUS, "");
			expectedSecond.put(HOST, "");
			expectedSecond.put(FRAGMENTS, "");
			expectedSecond.put(REQUIRED, "");
			expectedSecond.put(REQUIRING, "");
			expectedSecond.put(BUNDLESTATEEXT, "");
			expectedSecond.put(TRUSTEDSIGNERCERTIFICATIONS, "");
			expectedSecond.put(NONTRUSTEDSIGNERCERTIFICATIONS, "");
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ bundleIds[i]);
			assertFalse("These objects must exist.", children.equals(""));
			for (int j = 0; j < children.length; j++) {
				expectedSecond.remove(children[j]);
			}
			assertEquals(
					"There are undefined nodes in the BundleState Plugin.", 0,
					expectedSecond.size());
		}
		expectedSecond = null;

		// 3rd descendants in case of STATUS
		Hashtable expectedState = new Hashtable();
		for (int i = 0; i < bundleIds.length; i++) {
			expectedState.put(LOCATION, "");
			expectedState.put(STATE, "");
			expectedState.put(STARTLEVEL, "");
			expectedState.put(PERSISTENTLYSTARTED, "");
			expectedState.put(LASTMODIFIED, "");
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ bundleIds[i] + "/" + STATUS);
			assertFalse("These objects must exist.", children.equals(""));
			for (int j = 0; j < children.length; j++) {
				expectedState.remove(children[j]);
			}
			assertEquals(
					"There are undefined nodes in the BundleState Plugin.", 0,
					expectedState.size());
		}
		expectedState = null;
	}

	public void testBundleStateNodePermanency() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		long bundleId = testBundle1.getBundleId();
		String symbolicName = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + bundleId + "/" + SYMBOLICNAME)
				.getString();
		assertEquals("This value must be " + TESTBUNDLESYMBOLICNAME1 + ".",
				symbolicName, TESTBUNDLESYMBOLICNAME1);
		testBundle1.uninstall();
		String symbolicNameAfter = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + bundleId + "/" + SYMBOLICNAME)
				.getString();
		assertEquals("This value must be " + TESTBUNDLESYMBOLICNAME1 + ".",
				symbolicNameAfter, TESTBUNDLESYMBOLICNAME1);
	}

	public void testBundleStateNodeAccess() throws DmtException,
			BundleException, IOException {
		// Check ReadOnly Node
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		DmtData symbolicName = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + SYMBOLICNAME);
		try {
			symbolicName.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		DmtData version = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + VERSION);
		try {
			version.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		DmtData bundleType = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + BUNDLETYPE);
		try {
			bundleType.getInt();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Integer.");
		}
		DmtData manifest = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + MANIFEST);
		try {
			manifest.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		DmtData location = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + STATUS + "/" + LOCATION);
		try {
			location.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		DmtData state = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + STATUS + "/" + STATE);
		try {
			state.getInt();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Integer.");
		}
		DmtData starteLevel = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + STATUS + "/" + STARTLEVEL);
		try {
			starteLevel.getInt();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Integer.");
		}
		DmtData persistentlyStarted = session.getNodeValue(PLUGIN_ROOT_URI
				+ "/" + testBundle1.getBundleId() + "/" + STATUS + "/"
				+ PERSISTENTLYSTARTED);
		try {
			persistentlyStarted.getBoolean();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be boolean.");
		}
		DmtData lastModified = session
				.getNodeValue(PLUGIN_ROOT_URI + "/" + testBundle1.getBundleId()
						+ "/" + STATUS + "/" + LASTMODIFIED);
		try {
			lastModified.getDate();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Date.");
		}

		DmtData host = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + HOST);
		try {
			host.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}

		DmtData fragments = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + FRAGMENTS);
		try {
			fragments.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}

		DmtData required = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + REQUIRED);
		try {
			required.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}

		DmtData requiring = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + REQUIRING);
		try {
			requiring.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}

		DmtData trustedSignerCertifications = session
				.getNodeValue(PLUGIN_ROOT_URI + "/" + testBundle1.getBundleId()
						+ "/" + TRUSTEDSIGNERCERTIFICATIONS);
		try {
			trustedSignerCertifications.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		DmtData nonTrustedSignerCertifications = session
				.getNodeValue(PLUGIN_ROOT_URI + "/" + testBundle1.getBundleId()
						+ "/" + NONTRUSTEDSIGNERCERTIFICATIONS);
		try {
			nonTrustedSignerCertifications.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}

		// Write operation must be fail.
		session.close();
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/" + SYMBOLICNAME,
					new DmtData("test"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/" + VERSION, new DmtData(
					"test"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			int i = 1;
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/" + BUNDLETYPE,
					new DmtData(i));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/" + MANIFEST, new DmtData(
					"test"));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(
					PLUGIN_ROOT_URI + "/" + testBundle1.getBundleId() + "/"
							+ STATUS + "/" + LOCATION, new DmtData("test"));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/" + STATUS + "/" + STATE,
					new DmtData(1));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/" + STATUS + "/"
					+ STARTLEVEL, new DmtData(1));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/" + STATUS + "/"
					+ PERSISTENTLYSTARTED, new DmtData(true));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/" + STATUS + "/"
					+ LASTMODIFIED, new DmtData(1));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/" + HOST, new DmtData(
					"test"));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/" + FRAGMENTS, new DmtData(
					"test"));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/" + REQUIRED, new DmtData(
					"test"));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/" + REQUIRING, new DmtData(
					"test"));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/"
					+ TRUSTEDSIGNERCERTIFICATIONS, new DmtData("test"));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ testBundle1.getBundleId() + "/"
					+ NONTRUSTEDSIGNERCERTIFICATIONS, new DmtData("test"));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		testBundle1.uninstall();
	}

	public void testBundleStateCheckSymbolicName() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		DmtData symbolicName = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + SYMBOLICNAME);
		try {
			symbolicName.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		assertEquals("This value must be " + TESTBUNDLESYMBOLICNAME1 + ".",
				symbolicName.getString(), TESTBUNDLESYMBOLICNAME1);
		testBundle1.uninstall();
	}

	public void testBundleStateCheckVersion() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		String versionFromFW = testBundle1.getVersion().toString();
		DmtData version = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + VERSION);
		try {
			version.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		assertEquals("This value must be " + versionFromFW + ".", version
				.getString(), versionFromFW);
		testBundle1.uninstall();
	}

	public void testBundleStateCheckBundleType() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		int bundleTypeFromFW = packageAdmin.getBundleType(testBundle1);
		DmtData bundleType = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + BUNDLETYPE);
		try {
			bundleType.getInt();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Integer.");
		}
		assertEquals("This value must be " + bundleTypeFromFW + ".", bundleType
				.getInt(), bundleTypeFromFW);
		testBundle1.uninstall();
	}

	public void testBundleStateCheckStatus() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		String locationFromFW = testBundle1.getLocation();
		DmtData location = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + STATUS + "/" + LOCATION);
		try {
			location.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		assertEquals("This value must be " + locationFromFW + ".", location
				.getString(), locationFromFW);

		int stateFromFW = testBundle1.getState();
		DmtData state = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + STATUS + "/" + STATE);
		try {
			state.getInt();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Integer.");
		}
		assertEquals("This value must be " + stateFromFW + ".", state.getInt(),
				stateFromFW);

		int startLevelFromFW = startLevel.getBundleStartLevel(testBundle1);
		DmtData starteLevel = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ testBundle1.getBundleId() + "/" + STATUS + "/" + STARTLEVEL);
		try {
			starteLevel.getInt();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Integer.");
		}
		assertEquals("This value must be " + startLevelFromFW + ".",
				starteLevel.getInt(), startLevelFromFW);

		boolean persistentlyStartedFromFW = startLevel
				.isBundlePersistentlyStarted(testBundle1);
		DmtData persistentlyStarted = session.getNodeValue(PLUGIN_ROOT_URI
				+ "/" + testBundle1.getBundleId() + "/" + STATUS + "/"
				+ PERSISTENTLYSTARTED);
		try {
			persistentlyStarted.getBoolean();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be boolean.");
		}
		assertEquals("This value must be " + persistentlyStartedFromFW + ".",
				persistentlyStarted.getBoolean(), persistentlyStartedFromFW);

		DmtData lastModified = session
				.getNodeValue(PLUGIN_ROOT_URI + "/" + testBundle1.getBundleId()
						+ "/" + STATUS + "/" + LASTMODIFIED);
		try {
			lastModified.getDate();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Date.");
		}
		Date d = new Date(testBundle1.getLastModified());
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
		int i = Integer.parseInt(sdf1.format(d).substring(0, 2)) + 1;
		String CC = Integer.toString(i);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyMMdd");
		String lastModifiedFromFW = CC + sdf2.format(d);

		assertEquals("This value must be " + lastModifiedFromFW + ".",
				lastModified.getDate(), lastModifiedFromFW);

		testBundle1.uninstall();
	}

	public void testBundleStateManifest() throws DmtException, BundleException,
			IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		Dictionary headers = testBundle1.getHeaders();
		StringBuffer sb = new StringBuffer();
		for (Enumeration keys = headers.elements(); keys.hasMoreElements();) {
			String header = (String) keys.nextElement();
			sb.append(header);
			sb.append("\n");
		}
		String manifestFormBundle = sb.toString();
		String manifestFromPrugin = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + testBundle1.getBundleId() + "/"
						+ MANIFEST).getString();

		assertEquals("Both of manifest must be equal.", manifestFormBundle,
				manifestFromPrugin);
	}

	public void testBundleStateCheckHostAndFragments() throws DmtException,
			BundleException, IOException {
		testBundle2 = bundleInstall(TESTBUNDLELOCATION2);
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		Hashtable checkTable = new Hashtable();
		// host
		Bundle[] hostBundle = packageAdmin.getHosts(testBundle2);
		for (int i = 0; i < hostBundle.length; i++) {
			String hostBundleId = Long.toString(hostBundle[i].getBundleId());
			checkTable.put(hostBundleId, "");
		}
		String hostFromDataPlugIn = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + testBundle2.getBundleId() + "/" + HOST)
				.getString();
		assertFalse("These objects must exist.", hostFromDataPlugIn.equals(""));
		String[] hostFromDataPlugInArray = processCommaSeparatedValue(hostFromDataPlugIn);
		for (int j = 0; j < hostFromDataPlugInArray.length; j++) {
			checkTable.remove(hostFromDataPlugInArray[j]);
		}
		assertEquals(0, checkTable.size());

		// fragment
		Bundle[] fragmentBundle = packageAdmin.getFragments(testBundle1);
		for (int i = 0; i < fragmentBundle.length; i++) {
			String fragmentBundleId = Long.toString(fragmentBundle[i]
					.getBundleId());
			checkTable.put(fragmentBundleId, "");
		}
		String fragmentsFromDataPlugIn = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + testBundle1.getBundleId() + "/"
						+ FRAGMENTS).getString();
		assertFalse("These objects must exist.", fragmentsFromDataPlugIn
				.equals(""));
		String[] fragmentsFromDataPlugInArray = processCommaSeparatedValue(fragmentsFromDataPlugIn);
		for (int j = 0; j < fragmentsFromDataPlugInArray.length; j++) {
			checkTable.remove(fragmentsFromDataPlugInArray[j]);
		}
		assertEquals(0, checkTable.size());
		checkTable = null;
	}

	public void testBundleStateCheckRequiredAndRequiring() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		Hashtable checkTable = new Hashtable();
		// required
		RequiredBundle[] requiredBundle = packageAdmin
				.getRequiredBundles(testBundle1.getSymbolicName());
		if (requiredBundle == null) {
			fail();
		}
		for (int i = 0; i < requiredBundle.length; i++) {
			Bundle[] requiringBundles = requiredBundle[i].getRequiringBundles();
			for (int j = 0; j < requiringBundles.length; j++) {
				String requiredBundleId = Long.toString(requiringBundles[j]
						.getBundleId());
				checkTable.put(requiredBundleId, "");
			}
		}
		String requiredFromDataPlugIn = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + testBundle1.getBundleId() + "/"
						+ REQUIRED).getString();
		assertFalse("These objects must exist.", requiredFromDataPlugIn
				.equals(""));
		String[] requiredFromDataPlugInArray = processCommaSeparatedValue(requiredFromDataPlugIn);
		for (int j = 0; j < requiredFromDataPlugInArray.length; j++) {
			checkTable.remove(requiredFromDataPlugInArray[j]);
		}

		assertEquals(0, checkTable.size());
		// requiring
		for (int i = 0; i < requiredBundle.length; i++) {
			String requiredBundleId = Long.toString(requiredBundle[i]
					.getBundle().getBundleId());
			checkTable.put(requiredBundleId, "");
		}
		String requiringFromDataPlugIn = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + testBundle3.getBundleId() + "/"
						+ REQUIRING).getString();
		assertFalse("These objects must exist.", requiringFromDataPlugIn
				.equals(""));
		String[] requiringFromDataPlugInArray = processCommaSeparatedValue(requiringFromDataPlugIn);
		for (int j = 0; j < requiringFromDataPlugInArray.length; j++) {
			if (checkTable.containsKey(requiringFromDataPlugInArray[j])) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		checkTable = null;
	}

	// NonTrustedSignerCertifications
	public void testBundleStateCheckSigner1() throws DmtException,
			BundleException, IOException {
		testBundle4 = installAndStartBundle(TESTBUNDLELOCATION4);
		Hashtable signersCheckTable = new Hashtable();
		Map signersTrusted = testBundle4
				.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		Map signersAll = testBundle4.getSignerCertificates(Bundle.SIGNERS_ALL);
		Iterator iTrust = signersTrusted.keySet().iterator();
		while (iTrust.hasNext()) {
			signersAll.remove(iTrust.next());
		}
		Iterator iAll = signersAll.keySet().iterator();
		while (iAll.hasNext()) {
			String signer = (String) iAll.next();
			signersCheckTable.put(signersAll.get(signer), "");
		}
		String nonTrustedSignerCertificatons = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + testBundle4.getBundleId() + "/"
						+ NONTRUSTEDSIGNERCERTIFICATIONS).getString();
		StringTokenizer st = new StringTokenizer(nonTrustedSignerCertificatons,
				">,<");
		String[] arrayValue = new String[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			String value = st.nextToken();
			if (value.startsWith("<")) {
				value = value.substring(1);
			}
			if (value.endsWith(">")) {
				value = value.substring(0, value.length() - 1);
			}
			arrayValue[i] = value;
		}
		for (int j = 0; j < arrayValue.length; j++) {
			signersCheckTable.remove(arrayValue[j]);
		}
		assertEquals(signersCheckTable.size(), 0);
	}

	// TrustedSignerCertifications
	public void testBundleStateCheckSigner2() throws DmtException,
			BundleException, IOException {
		testBundle4 = installAndStartBundle(TESTBUNDLELOCATION4);
		Hashtable signersCheckTable = new Hashtable();
		Map signersTrusted = testBundle4
				.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		Iterator iTrust = signersTrusted.keySet().iterator();
		while (iTrust.hasNext()) {
			String signer = (String) iTrust.next();
			signersCheckTable.put(signersTrusted.get(signer), "");
		}
		String trustedSignerCertificatons = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + testBundle4.getBundleId() + "/"
						+ TRUSTEDSIGNERCERTIFICATIONS).getString();
		StringTokenizer st = new StringTokenizer(trustedSignerCertificatons,
				">,<");
		String[] arrayValue = new String[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			String value = st.nextToken();
			if (value.startsWith("<")) {
				value = value.substring(1);
			}
			if (value.endsWith(">")) {
				value = value.substring(0, value.length() - 1);
			}
			arrayValue[i] = value;
		}
		for (int j = 0; j < arrayValue.length; j++) {
			signersCheckTable.remove(arrayValue[j]);
		}
		assertEquals(signersCheckTable.size(), 0);
	}

	// -----Utilities-----
	private Bundle installAndStartBundle(String location) throws IOException,
			BundleException {
		URL url = context.getBundle().getResource(location);
		InputStream is = url.openStream();
		Bundle bundle = context.installBundle(location, is);
		bundle.start();
		is.close();
		return bundle;
	}

	private Bundle bundleInstall(String location) throws IOException,
			BundleException {
		URL url = context.getBundle().getResource(location);
		InputStream is = url.openStream();
		Bundle bundle = context.installBundle(location, is);
		is.close();
		return bundle;
	}

	private String[] processCommaSeparatedValue(String value) {
		StringTokenizer st = new StringTokenizer(value, ",");
		String[] arrayValue = new String[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			arrayValue[i] = st.nextToken();
		}
		return arrayValue;
	}
}
