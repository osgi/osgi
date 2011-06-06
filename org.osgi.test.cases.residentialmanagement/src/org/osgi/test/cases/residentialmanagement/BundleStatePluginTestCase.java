/*
 * Copyright (c) OSGi Alliance (2000, 2010). All Rights Reserved.
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
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable; //import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

	protected static final String ID = "ID";
	protected static final String SYMBOLICNAME = "SymbolicName";
	protected static final String VERSION = "Version";
	protected static final String BUNDLETYPE = "BundleType";
	protected static final String MANIFEST = "Manifest";
	protected static final String LOCATION = "Location";
	protected static final String STATUS = "Status";
	protected static final String HOSTS = "Hosts";
	protected static final String FRAGMENTS = "Fragments";
	protected static final String REQUIRED = "Required";
	protected static final String REQUIRING = "Requiring";
	protected static final String TRUSTEDSIGNERCERTIFICATION = "TrustedSignerCertificate";
	protected static final String NONTRUSTEDSIGNERCERTIFICATION = "NonTrustedSignerCertificate";
	protected static final String STATE = "State";
	protected static final String STARTLEVEL = "StartLevel";
	protected static final String PERSISTENTLYSTARTED = "PersistentlyStarted";
	protected static final String ACTIVATIONPOLICYUSED = "ActivationPolicyUsed";
	protected static final String LASTMODIFIED = "LastModified";
	protected static final String CERTIFICATECHAIN = "CertificateChain";

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

	/**
	 * 
	 * Test of checking BundleState MO node structure.
	 * 
	 * @throws DmtException
	 */
	public void testBundleStateNodeArchitecture() throws DmtException {
		// 1st descendants
		String[] children;
		Hashtable bundleIdTable = new Hashtable();
		Bundle[] bundles = context.getBundles();
		assertNotNull("This object should not be null.", bundles);
		for (int i = 0; i < bundles.length; i++) {
			long bundleId = bundles[i].getBundleId() + 1;
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
			expectedSecond.put(ID, "");
			expectedSecond.put(SYMBOLICNAME, "");
			expectedSecond.put(VERSION, "");
			expectedSecond.put(BUNDLETYPE, "");
			expectedSecond.put(MANIFEST, "");
			expectedSecond.put(LOCATION, "");
			expectedSecond.put(STATUS, "");
			expectedSecond.put(HOSTS, "");
			expectedSecond.put(FRAGMENTS, "");
			expectedSecond.put(REQUIRED, "");
			expectedSecond.put(REQUIRING, "");
			expectedSecond.put(TRUSTEDSIGNERCERTIFICATION, "");
			expectedSecond.put(NONTRUSTEDSIGNERCERTIFICATION, "");
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
			expectedState.put(STATE, "");
			expectedState.put(STARTLEVEL, "");
			expectedState.put(PERSISTENTLYSTARTED, "");
			expectedState.put(ACTIVATIONPOLICYUSED, "");
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

		// 3rd descendants in case of children of Hosts
	}

	/**
	 * 
	 * To check deletion of ../BundleState/<id> subtree after the Bundle is
	 * uninstalled.
	 * 
	 * precondition : "testBundle1" is installed. Therefore, ../BundleState/<id>
	 * subtree must exist. postcondition : "testBundle1" is uninstalled. Then,
	 * the ../BundleState/<id> subtree must be deleted.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testBundleStateNodePermanency() throws DmtException,
			BundleException, IOException {
		boolean flag = false;
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		String symbolicName = session.getNodeValue(
				PLUGIN_ROOT_URI + "/"
						+ Long.toString(testBundle1.getBundleId() + 1) + "/"
						+ SYMBOLICNAME).getString();
		assertEquals("This value must be " + TESTBUNDLESYMBOLICNAME1 + ".",
				symbolicName, TESTBUNDLESYMBOLICNAME1);
		testBundle1.uninstall();
		try {
			session.getNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + SYMBOLICNAME).getString();
		} catch (DmtException de) {
			flag = true;
		}
		assertTrue(flag);
	}

	/**
	 * 
	 * Test of access to node.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testBundleStateNodeAccess() throws DmtException,
			BundleException, IOException {
		// Check ReadOnly Node
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		DmtData id = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/" + ID);
		try {
			id.getLong();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Long.");
		}
		DmtData symbolicName = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/"
				+ SYMBOLICNAME);
		try {
			symbolicName.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		DmtData version = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/" + VERSION);
		try {
			version.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		DmtData bundleType = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/"
				+ BUNDLETYPE);
		try {
			bundleType.getInt();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Integer.");
		}
		DmtData manifest = session
				.getNodeValue(PLUGIN_ROOT_URI + "/"
						+ Long.toString(testBundle1.getBundleId() + 1) + "/"
						+ MANIFEST);
		try {
			manifest.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		DmtData location = session
				.getNodeValue(PLUGIN_ROOT_URI + "/"
						+ Long.toString(testBundle1.getBundleId() + 1) + "/"
						+ LOCATION);
		try {
			location.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		DmtData state = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/" + STATUS
				+ "/" + STATE);
		try {
			state.getInt();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Integer.");
		}
		DmtData starteLevel = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/" + STATUS
				+ "/" + STARTLEVEL);
		try {
			starteLevel.getInt();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Integer.");
		}
		DmtData persistentlyStarted = session.getNodeValue(PLUGIN_ROOT_URI
				+ "/" + Long.toString(testBundle1.getBundleId() + 1) + "/"
				+ STATUS + "/" + PERSISTENTLYSTARTED);
		try {
			persistentlyStarted.getBoolean();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be boolean.");
		}
		DmtData lastModified = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/" + STATUS
				+ "/" + LASTMODIFIED);
		try {
			lastModified.getDateTime();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Date.");
		}
		DmtData activationPolicyUsed = session.getNodeValue(PLUGIN_ROOT_URI
				+ "/" + Long.toString(testBundle1.getBundleId() + 1) + "/"
				+ STATUS + "/" + ACTIVATIONPOLICYUSED);
		try {
			activationPolicyUsed.getBoolean();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be boolean.");
		}

		// Write operation must be fail.
		session.close();
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);
		try {
			session.setNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + ID, new DmtData(1));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + SYMBOLICNAME, new DmtData("test"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + VERSION, new DmtData("test"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			int i = 1;
			session.setNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + BUNDLETYPE, new DmtData(i));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + MANIFEST, new DmtData("test"));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + LOCATION, new DmtData("test"));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + STATUS + "/" + STATE, new DmtData(1));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + STATUS + "/" + STARTLEVEL, new DmtData(1));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + STATUS + "/" + PERSISTENTLYSTARTED,
					new DmtData(true));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + STATUS + "/" + LASTMODIFIED, new DmtData(1));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		try {
			session.setNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + STATUS + "/" + ACTIVATIONPOLICYUSED,
					new DmtData(true));
		} catch (DmtIllegalStateException dise) {
			checkFlag = true;
		} catch (DmtException de) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		testBundle1.uninstall();
	}

	/**
	 * 
	 * To check the SymbolicName LeafNode value.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testBundleStateCheckSymbolicName() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		DmtData symbolicName = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/"
				+ SYMBOLICNAME);
		try {
			symbolicName.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		assertEquals("This value must be " + TESTBUNDLESYMBOLICNAME1 + ".",
				symbolicName.getString(), TESTBUNDLESYMBOLICNAME1);
		testBundle1.uninstall();
	}

	/**
	 * 
	 * To check the Version LeafNode value.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testBundleStateCheckVersion() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		String versionFromFW = testBundle1.getVersion().toString();
		DmtData version = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/" + VERSION);
		try {
			version.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		assertEquals("This value must be " + versionFromFW + ".",
				version.getString(), versionFromFW);
		testBundle1.uninstall();
	}

	/**
	 * 
	 * To check the BundleType LeafNode value.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testBundleStateCheckBundleType() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		int bundleTypeFromFW = packageAdmin.getBundleType(testBundle1);
		DmtData bundleType = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/"
				+ BUNDLETYPE);
		try {
			bundleType.getInt();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Integer.");
		}
		assertEquals("This value must be " + bundleTypeFromFW + ".",
				bundleType.getInt(), bundleTypeFromFW);
		testBundle1.uninstall();
	}

	/**
	 * 
	 * To check the Location LeafNode value.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testBundleStateCheckLocation() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		String locationFromFW = testBundle1.getLocation();
		DmtData location = session
				.getNodeValue(PLUGIN_ROOT_URI + "/"
						+ Long.toString(testBundle1.getBundleId() + 1) + "/"
						+ LOCATION);
		try {
			location.getString();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be String.");
		}
		assertEquals("This value must be " + locationFromFW + ".",
				location.getString(), locationFromFW);
		testBundle1.uninstall();
	}

	/**
	 * 
	 * To check LeafNodes value in State subtree.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testBundleStateCheckStatus() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		int stateFromFW = testBundle1.getState();
		DmtData state = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/" + STATUS
				+ "/" + STATE);
		try {
			state.getInt();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Integer.");
		}
		assertEquals("This value must be " + stateFromFW + ".", state.getInt(),
				stateFromFW);

		int startLevelFromFW = startLevel.getBundleStartLevel(testBundle1);
		DmtData starteLevel = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/" + STATUS
				+ "/" + STARTLEVEL);
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
				+ "/" + Long.toString(testBundle1.getBundleId() + 1) + "/"
				+ STATUS + "/" + PERSISTENTLYSTARTED);
		try {
			persistentlyStarted.getBoolean();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be boolean.");
		}
		assertEquals("This value must be " + persistentlyStartedFromFW + ".",
				persistentlyStarted.getBoolean(), persistentlyStartedFromFW);

		DmtData lastModified = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId() + 1) + "/" + STATUS
				+ "/" + LASTMODIFIED);
		try {
			lastModified.getDateTime();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Date.");
		}
		Date d = new Date(testBundle1.getLastModified());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'hhmmss");
		String lastModifiedFromFW = sdf.format(d);
		assertEquals("This value must be " + lastModifiedFromFW + ".",
				lastModified.getDateTime(), lastModifiedFromFW);

		testBundle1.uninstall();
	}

	/**
	 * 
	 * To check the Manifest LeafNode value.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
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
				PLUGIN_ROOT_URI + "/"
						+ Long.toString(testBundle1.getBundleId() + 1) + "/"
						+ MANIFEST).getString();

		assertEquals("Both of manifest must be equal.", manifestFormBundle,
				manifestFromPrugin);
	}

	/**
	 * 
	 * To check the Hosts and Fragments.
	 * 
	 * [testBundle1] This bundle hosts testBundle2.
	 * 
	 * [testBundle2] This bundle is fragment bundle of testBundle1.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
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
		String[] hostLeafNode = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle2.getBundleId() + 1) + "/" + HOSTS);
		assertFalse("These objects must exist.", hostLeafNode.length == 0);
		for (int i = 0; i < hostLeafNode.length; i++) {
			long hostFromDataPlugIn = session.getNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle2.getBundleId() + 1)
							+ "/" + HOSTS + "/" + hostLeafNode[i]).getLong();
			checkTable.remove(Long.toString(hostFromDataPlugIn));
		}
		assertEquals(0, checkTable.size());

		// fragment
		Bundle[] fragmentBundle = packageAdmin.getFragments(testBundle1);
		for (int i = 0; i < fragmentBundle.length; i++) {
			String fragmentBundleId = Long.toString(fragmentBundle[i]
					.getBundleId());
			checkTable.put(fragmentBundleId, "");
		}
		String[] fragmentLeafNode = session.getChildNodeNames(PLUGIN_ROOT_URI
				+ "/" + Long.toString(testBundle1.getBundleId() + 1) + "/"
				+ FRAGMENTS);
		assertFalse("These objects must exist.", fragmentLeafNode.length == 0);
		for (int i = 0; i < fragmentLeafNode.length; i++) {
			long fragmentFromDataPlugIn = session.getNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + FRAGMENTS + "/" + fragmentLeafNode[i])
					.getLong();
			checkTable.remove(Long.toString(fragmentFromDataPlugIn));
		}
		assertEquals(0, checkTable.size());
		checkTable = null;
	}

	/**
	 * 
	 * To check the Hosts and Fragments.
	 * 
	 * [testBundle1] This bundle is required by testBundle3.
	 * 
	 * [testBundle3] This bundle is requiring testBundle1.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
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
		String[] requiredLeafNode = session.getChildNodeNames(PLUGIN_ROOT_URI
				+ "/" + Long.toString(testBundle1.getBundleId() + 1) + "/"
				+ REQUIRED);
		assertFalse("These objects must exist.", requiredLeafNode.length == 0);
		for (int i = 0; i < requiredLeafNode.length; i++) {
			long requiredFromDataPlugIn = session.getNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle1.getBundleId() + 1)
							+ "/" + REQUIRED + "/" + requiredLeafNode[i])
					.getLong();
			checkTable.remove(Long.toString(requiredFromDataPlugIn));
		}
		assertEquals(0, checkTable.size());
		// requiring
		for (int i = 0; i < requiredBundle.length; i++) {
			String requiredBundleId = Long.toString(requiredBundle[i]
					.getBundle().getBundleId());
			checkTable.put(requiredBundleId, "");
		}
		String[] requiringLeafNode = session.getChildNodeNames(PLUGIN_ROOT_URI
				+ "/" + Long.toString(testBundle3.getBundleId() + 1) + "/"
				+ REQUIRING);
		assertFalse("These objects must exist.", requiringLeafNode.length == 0);
		for (int i = 0; i < requiringLeafNode.length; i++) {
			long requiringFromDataPlugIn = session.getNodeValue(
					PLUGIN_ROOT_URI + "/"
							+ Long.toString(testBundle3.getBundleId() + 1)
							+ "/" + REQUIRING + "/" + requiringLeafNode[i])
					.getLong();
			if (checkTable.containsKey(Long.toString(requiringFromDataPlugIn))) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		checkTable = null;
	}

	/**
	 * 
	 * Test of NonTrustedSignerCertifications subtree
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testBundleStateCheckSigner1() throws DmtException,
			BundleException, IOException {
		testBundle4 = installAndStartBundle(TESTBUNDLELOCATION4);
		Map signersTrusted = testBundle4
				.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		Map signersNonTrusted = testBundle4
				.getSignerCertificates(Bundle.SIGNERS_ALL);
		Iterator itPre = signersTrusted.keySet().iterator();
		for (int i = 0; itPre.hasNext(); i++) {
			signersNonTrusted.remove(itPre.next());
		}
		List certList = new ArrayList();
		List nList = new ArrayList();
		Iterator it = signersNonTrusted.keySet().iterator();
		for (int i = 0; it.hasNext(); i++) {
			X509Certificate cert = (X509Certificate) it.next();
			List certificateChane = (List) signersNonTrusted.get(cert);
			Iterator itCert = certificateChane.iterator();
			for (int j = 0; itCert.hasNext(); j++) {
				X509Certificate certs = (X509Certificate) itCert.next();
				Principal pri = certs.getIssuerDN();
				String name = pri.getName();
				nList.add(name);
			}
			certList.add(Integer.toString(i));
		}
		String[] id = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle4.getBundleId() + 1) + "/"
				+ NONTRUSTEDSIGNERCERTIFICATION);
		assertEquals(certList.size(), id.length);
		for (int i = 0; i < id.length; i++) {
			String[] certs = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ Long.toString(testBundle4.getBundleId() + 1) + "/"
					+ NONTRUSTEDSIGNERCERTIFICATION + "/" + id[i] + "/"
					+ CERTIFICATECHAIN);
			for (int j = 0; j < certs.length; j++) {
				String cert = session.getNodeValue(
						PLUGIN_ROOT_URI + "/"
								+ Long.toString(testBundle4.getBundleId() + 1)
								+ "/" + NONTRUSTEDSIGNERCERTIFICATION + "/"
								+ id[i] + "/" + CERTIFICATECHAIN + "/"
								+ certs[j]).getString();
				nList.remove(cert);
			}
		}
		assertEquals(nList.size(), 0);
	}

	/**
	 * 
	 * Test of TrustedSignerCertificate subtree
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testBundleStateCheckSigner2() throws DmtException,
			BundleException, IOException {
		testBundle4 = installAndStartBundle(TESTBUNDLELOCATION4);
		Map signersTrusted = testBundle4
				.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		List certList = new ArrayList();
		List nList = new ArrayList();
		Iterator it = signersTrusted.keySet().iterator();
		for (int i = 0; it.hasNext(); i++) {
			X509Certificate cert = (X509Certificate) it.next();
			List certificateChane = (List) signersTrusted.get(cert);
			Iterator itCert = certificateChane.iterator();
			for (int j = 0; itCert.hasNext(); j++) {
				X509Certificate certs = (X509Certificate) itCert.next();
				Principal pri = certs.getIssuerDN();
				String name = pri.getName();
				nList.add(name);
			}
			certList.add(Integer.toString(i));
		}
		String[] id = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle4.getBundleId() + 1) + "/"
				+ TRUSTEDSIGNERCERTIFICATION);
		assertEquals(certList.size(), id.length);
		for (int i = 0; i < id.length; i++) {
			String[] certs = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ Long.toString(testBundle4.getBundleId() + 1) + "/"
					+ TRUSTEDSIGNERCERTIFICATION + "/" + id[i] + "/"
					+ CERTIFICATECHAIN);
			for (int j = 0; j < certs.length; j++) {
				String cert = session.getNodeValue(
						PLUGIN_ROOT_URI + "/"
								+ Long.toString(testBundle4.getBundleId() + 1)
								+ "/" + TRUSTEDSIGNERCERTIFICATION + "/"
								+ id[i] + "/" + CERTIFICATECHAIN + "/"
								+ certs[j]).getString();
				nList.remove(cert);
			}
		}
		assertEquals(nList.size(), 0);
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
}
