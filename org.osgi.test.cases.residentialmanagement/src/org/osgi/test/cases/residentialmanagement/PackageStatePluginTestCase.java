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

import info.dmtree.DmtAdmin;
import info.dmtree.DmtConstants;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtIllegalStateException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation, Steffen Druesedow (Deutsche Telekom AG)
 * 
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
	protected static final int SLEEP_TIME = 1000;

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

	protected void tearDown() throws BundleException, InterruptedException,
			DmtException {
		if (session != null)
			session.close();
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

	/**
	 * Test of checking PackageState MO node structure.
	 * 
	 */
	public void testPackageStateNodeArchitecuture() {
		try {
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
				children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
						+ ids[i]);
				assertNotNull("This object should not be null.", children);
				for (int j = 0; j < children.length; j++) {
					expected.remove(children[j]);
				}
				assertEquals(
						"There are undefined nodes in the PackageState Plugin.",
						0, expected.size());

			}
			// 3rd descendants
			for (int i = 0; i < ids.length; i++) {
				children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
						+ ids[i] + "/" + IMPORTINGBUNDLES);
				String pkgName = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).toString();
				String pkgVer = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + VERSION)
						.toString();
				ExportedPackage[] exportedPackage = pkgAdmin
						.getExportedPackages(pkgName);
				long expBundleId = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + EXPORTINGBUNDLE)
						.getLong();
				String expBundleIdStr = Long.toString(expBundleId);
				Bundle[] importingBundles = null;
				for (int j = 0; j < exportedPackage.length; j++) {
					if (exportedPackage[j].getVersion().toString()
							.equals(pkgVer)
							&& Long.toString(
									exportedPackage[j].getExportingBundle()
											.getBundleId()).equals(
									expBundleIdStr)) {
						importingBundles = exportedPackage[j]
								.getImportingBundles();
					}
				}
				assertEquals("Node number is incorrect.",
						importingBundles.length, children.length);
			}
			session.close();
			session = null;
		} catch (DmtException de) {
			de.printStackTrace();
			fail();
		}
	}

	/**
	 * Tests the specified node types.
	 *  
	 */
	public void testPackageStateNodeTypes() {
		try {
			assertEquals("The PackageState node must be of type: org.osgi/1.0/PackageStateManagementObject",
					"org.osgi/1.0/PackageStateManagementObject",
					session.getNodeType(PLUGIN_ROOT_URI));

			ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
			assertNotNull(ids);
			assertTrue(ids.length > 0 );
			
			assertEquals("The ID node must be of type DDF_TRANSIENT",
					DmtConstants.DDF_TRANSIENT,
					session.getNodeType(PLUGIN_ROOT_URI + "/" + ids[0]));
			
			assertEquals("The ImportingBundles node must be of type DDF_LIST_SUBTREE",
					DmtConstants.DDF_LIST_SUBTREE,
					session.getNodeType(PLUGIN_ROOT_URI + "/" + ids[0] + "/" + IMPORTINGBUNDLES));
			
		} catch (DmtException de) {
			de.printStackTrace();
			fail();
		}
	}

	/**
	 * Test that the package ids do not start with 0.
	 * 
	 */
	public void testPackageStateIDNotZero() {
		try {
			ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
			for (int i = 0; i < ids.length; i++)
				assertTrue("The package-id must be greater than 0",
						Long.parseLong(ids[i]) > 0);
			session.close();
			session = null;
		} catch (DmtException de) {
			de.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Test that the package ids do not start with 0.
	 * 
	 */
	public void testImportingBundlesIDNotZero() {
		try {
			ids = session.getChildNodeNames(PLUGIN_ROOT_URI);

			for (int i = 0; i < ids.length; i++) {
				String[] importingBundlesIds = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i] + "/" + IMPORTINGBUNDLES );
				if ( importingBundlesIds != null && importingBundlesIds.length > 0 )
					assertTrue("The importing bundles id must be greater than 0", Long.parseLong(importingBundlesIds[0]) > 0);
			}
			session.close();
			session = null;
		} catch (DmtException de) {
			de.printStackTrace();
			fail();
		}
	}

	/**
	 * Tests the metadata of the PackageState nodes.
	 * This ensures that only read-operations are allowed on the nodes and that the scope and format is correct.   
	 * @throws BundleException 
	 * @throws IOException 
	 * 
	 */
	public void testPackageStateMetaData() throws IOException, BundleException {
		try {
			// install and start a bundle that imports the shared package
			testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
			assertNotNull(testBundle1);

			ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
			String uri = PLUGIN_ROOT_URI;
			assertMetaData( uri, MetaNode.PERMANENT);

			for (int i = 0; i < ids.length; i++) {
				uri = PLUGIN_ROOT_URI + "/" + ids[i];
				assertMetaData( uri, MetaNode.AUTOMATIC);
				assertMetaData( uri +  "/" + NAME, MetaNode.AUTOMATIC, DmtData.FORMAT_STRING );
				assertMetaData( uri +  "/" + VERSION, MetaNode.AUTOMATIC, DmtData.FORMAT_STRING );
				assertMetaData( uri +  "/" + REMOVALPENDING, MetaNode.AUTOMATIC, DmtData.FORMAT_BOOLEAN );
				assertMetaData( uri +  "/" + EXPORTINGBUNDLE, MetaNode.AUTOMATIC, DmtData.FORMAT_LONG );
				if ( session.isNodeUri(uri + "/Ext")) 
					assertMetaData( uri + "/Ext", MetaNode.AUTOMATIC);

				assertMetaData( uri +=  "/" + IMPORTINGBUNDLES, MetaNode.AUTOMATIC );
				String[] bundles = session.getChildNodeNames(uri);
				for (int j = 0; j < bundles.length; j++)
					assertMetaData( uri + "/" + bundles[j], MetaNode.AUTOMATIC, DmtData.FORMAT_LONG );
			}

			session.close();
			session = null;
		} catch (DmtException de) {
			de.printStackTrace();
			fail("unexpeced DmtException: " + de.getMessage());
		}
	}
	
	private void assertMetaData( String uri , int scope ) throws DmtException {
		assertMetaData(uri, scope, -1);
	}

	private void assertMetaData( String uri, int scope, int format ) throws DmtException {
		MetaNode metaNode = session.getMetaNode(uri);
		assertNotNull(metaNode);
		
		assertEquals( "This node must support the GET operation: " + uri, true, metaNode.can( MetaNode.CMD_GET ) );
		assertEquals( "This node must not support the ADD operation: " + uri, false, metaNode.can( MetaNode.CMD_ADD ) );
		assertEquals( "This node must not support the DELETE operation: " + uri, false, metaNode.can( MetaNode.CMD_DELETE ) );
		assertEquals( "This node must not support the EXECUTE operation: " + uri, false, metaNode.can( MetaNode.CMD_EXECUTE ) );
		assertEquals( "This node must not support the REPLACE operation: " + uri, false, metaNode.can( MetaNode.CMD_REPLACE ) );
		assertEquals( "This node has a wrong scope : " + uri, scope, metaNode.getScope() );
		if ( format != -1 ) 
			assertEquals( "This node has a wrong format: ", format, metaNode.getFormat() );
	}
	

	/**
	 * Test of node creation. precondition : "testBundle1" is not installed.
	 * postcondition : This MO must create subtree which is described the
	 * exported package by testBundle1 after installation of testBundle1 .
	 * 
	 */
	public void testPackageStateNodeCreation() {
		try {
			ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
			assertNotNull("This object should not be null.", ids);
			for (int i = 0; i < ids.length; i++) {
				childName = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).toString();
				childVer = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + VERSION)
						.toString();
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
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).toString();
				childVer = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + VERSION)
						.toString();
				if (childName.equals(TESTPACKAGENAME)
						&& childVer.equals(TESTPACKAGE1_VERSION)) {
					checkFlag = true;
					break;
				}
			}
			assertTrue(checkFlag);
			session.close();
			session = null;
		} catch (DmtException de) {
			de.printStackTrace();
			fail();
		} catch (IOException ie) {
			ie.printStackTrace();
			fail();
		} catch (BundleException be) {
			be.printStackTrace();
			fail();
		}
	}

	/**
	 * 
	 * Test of node deletion. precondition : "testBundle1" is installed at
	 * first. postcondition : This MO must delete subtree which is described the
	 * exported package by testBundle1 after testBundle1 is uninstalled.
	 */
	public void testPackageStateNodeDelete1() {
		try {
			testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
			testBundle1.uninstall();
			ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
			assertNotNull("This object should not be null.", ids);
			for (int i = 0; i < ids.length; i++) {
				childName = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).toString();
				childVer = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + VERSION)
						.toString();
				if (childName.equals(TESTPACKAGENAME)
						&& childVer.equals(TESTPACKAGE1_VERSION)) {
					assertTrue(false);
				}
			}
			session.close();
			session = null;
		} catch (DmtException de) {
			de.printStackTrace();
			fail();
		} catch (IOException ie) {
			ie.printStackTrace();
			fail();
		} catch (BundleException be) {
			be.printStackTrace();
			fail();
		}
	}

	/**
	 * 
	 * Test of node deletion. precondition : "testBundle1" and "testBundle2" are
	 * installed at first. postcondition : After testBundle1 is uninstalled this
	 * MO must NOT delete subtree which is described the exported package by
	 * testBundle1 without calling refreshPackages().
	 * 
	 */
	public void testPackageStateNodeDelete2() {
		try {
			testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
			testBundle2 = installAndStartBundle(TESTBUNDLELOCATION2);
			testBundle1.uninstall();
			ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
			assertNotNull("This object should not be null.", ids);
			for (int i = 0; i < ids.length; i++) {
				childName = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).toString();
				childVer = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + VERSION)
						.toString();
				if (childName.equals(TESTPACKAGENAME)
						&& childVer.equals(TESTPACKAGE1_VERSION)) {
					checkFlag = true;
					break;
				}
			}
			assertTrue(checkFlag);
			session.close();
			session = null;
		} catch (DmtException de) {
			de.printStackTrace();
			fail();
		} catch (IOException ie) {
			ie.printStackTrace();
			fail();
		} catch (BundleException be) {
			be.printStackTrace();
			fail();
		}
	}

	/**
	 * 
	 * Test of node deletion. precondition : "testBundle1" and "testBundle2" are
	 * installed at first. postcondition : After testBundle1 is uninstalled,
	 * this MO must delete subtree which is described the exported package by
	 * testBundle1.
	 * 
	 */
	public void testPackageStateNodeDelete3() {
		try {
			testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
			testBundle2 = installAndStartBundle(TESTBUNDLELOCATION2);
			testBundle1.uninstall();
			pkgAdmin.refreshPackages(null);
			Thread.sleep(SLEEP_TIME);
			ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
			assertNotNull("This object should not be null.", ids);
			for (int i = 0; i < ids.length; i++) {
				childName = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME)
						.getString();
				childVer = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + VERSION)
						.getString();
				if (childName.equals(TESTPACKAGENAME)
						&& childVer.equals(TESTPACKAGE1_VERSION)) {
					assertTrue(false);
				}
			}
			session.close();
			session = null;
		} catch (DmtException de) {
			de.printStackTrace();
			fail();
		} catch (IOException ie) {
			ie.printStackTrace();
			fail();
		} catch (BundleException be) {
			be.printStackTrace();
			fail();
		} catch (InterruptedException ine) {
			ine.printStackTrace();
			fail();
		}
	}

	/**
	 * Test of access to node.
	 * 
	 * 
	 */
	public void testPackageStateNodeAccess() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		testBundle2 = installAndStartBundle(TESTBUNDLELOCATION2);
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			String packageName = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + ids[i] + "/" + NAME).getString();
			if (packageName.equals(TESTPACKAGENAME)) {
				targetIdNumber = ids[i];
				break;
			}
		}

		// ReadOnlyNode
		try {
			DmtData data = session.getNodeValue(PLUGIN_ROOT_URI + "/"
					+ targetIdNumber + "/" + NAME);
			data.getString();

			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + targetIdNumber
					+ "/" + VERSION);
			data.getString();

			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + targetIdNumber
					+ "/" + REMOVALPENDING);
			data.getBoolean();

			data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + targetIdNumber
					+ "/" + EXPORTINGBUNDLE);
			data.getLong();

			String[] importBundlesChildren = session
					.getChildNodeNames(PLUGIN_ROOT_URI + "/" + targetIdNumber
							+ "/" + IMPORTINGBUNDLES);
			for (int j = 0; j < importBundlesChildren.length; j++) {
				data = session.getNodeValue(PLUGIN_ROOT_URI + "/"
						+ targetIdNumber + "/" + IMPORTINGBUNDLES + "/"
						+ importBundlesChildren[j]);
				data.getLong();
			}

		} catch (DmtIllegalStateException e) {
			fail("Leaf node contains illegal format values.");
		} catch (DmtException e) {
			fail("Can not get a leaf node's value.");
		}
		session.close();
		session = null;

		// Write operation must be fail.
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);

		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/" + targetIdNumber + "/"
					+ NAME, new DmtData("test"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;

		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/" + targetIdNumber + "/"
					+ VERSION, new DmtData("1.1.1"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;

		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/" + targetIdNumber + "/"
					+ REMOVALPENDING, new DmtData(1));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;

		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/" + targetIdNumber + "/"
					+ EXPORTINGBUNDLE, new DmtData(1));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;

		try {
			String[] importBundlesChildren = session
					.getChildNodeNames(PLUGIN_ROOT_URI + "/" + targetIdNumber
							+ "/" + IMPORTINGBUNDLES);
			if (importBundlesChildren.length != 0)
				for (int j = 0; j < importBundlesChildren.length; j++) {
					session.setNodeValue(PLUGIN_ROOT_URI + "/" + targetIdNumber
							+ "/" + IMPORTINGBUNDLES + "/"
							+ importBundlesChildren[j], new DmtData(5));
				}

		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		session.close();
		session = null;
	}

	/**
	 * Check of node creation of exported package by framework.
	 * 
	 */
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
		long exBundleId = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + targetIdNumber + "/" + EXPORTINGBUNDLE)
				.getLong();
		if (exBundleId != 0) {
			fail("This exporting bundle is system bundle(id=0).");
		}

		// ImportingBundles node check
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ targetIdNumber + "/" + IMPORTINGBUNDLES);
		assertFalse("This object should not be zero.", children.length == 0);
		for (int i = 0; i < children.length; i++) {
			long importingBundleId = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + targetIdNumber + "/"
							+ IMPORTINGBUNDLES + "/" + children[i]).getLong();
			if (importingBundleId == testBundle1.getBundleId()) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		session.close();
		session = null;
	}

	/**
	 * Check of node creation of SystemPackage.
	 * 
	 */
	public void testPackageStateExportedBySystemPackageNode()
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
		long exBundleId = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + targetIdNumber + "/" + EXPORTINGBUNDLE)
				.getLong();
		if (exBundleId != 0) {
			fail("This exporting bundle is system bundle(id=0).");
		}

		// ImportingBundles node check
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		testBundle2 = installAndStartBundle(TESTBUNDLELOCATION2);
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ targetIdNumber + "/" + IMPORTINGBUNDLES);
		assertFalse("This object should not be zero.", children.length == 0);
		for (int i = 0; i < children.length; i++) {
			long importingBundleId = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + targetIdNumber + "/"
							+ IMPORTINGBUNDLES + "/" + children[i]).getLong();
			if (importingBundleId == testBundle2.getBundleId()) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		session.close();
		session = null;
	}

	/**
	 * Check of node creation of exported package by bundles.
	 * 
	 */
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
		long exBundleId = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + targetIdNumber + "/" + EXPORTINGBUNDLE)
				.getLong();
		assertTrue(exBundleId == testBundle1.getBundleId());

		// ImportingBundles node check
		long currentImBundleId = testBundle2.getBundleId();
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ targetIdNumber + "/" + IMPORTINGBUNDLES);
		assertFalse("This object should not be zero.", children.length == 0);
		for (int i = 0; i < children.length; i++) {
			long importingBundleId = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + targetIdNumber + "/"
							+ IMPORTINGBUNDLES + "/" + children[i]).getLong();
			if (importingBundleId == currentImBundleId) {
				checkFlag = true;
				break;
			}
		}
		assertTrue(checkFlag);
		session.close();
		session = null;
	}

	/**
	 * Test of bundle update. precondition : "testBundle1" and "testBundle2" are
	 * installed at first. postcondition : After testBundle1 is updated to
	 * testBundle6 which exports higher version package, this MO must keep
	 * subtree which is described the exported package by previous testBundle1
	 * without calling refreshPackages().
	 * 
	 */
	public void testPackageStateNodeUpdate() throws DmtException,
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
		session = null;
	}

	// ----- Utilities -----//

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
}
