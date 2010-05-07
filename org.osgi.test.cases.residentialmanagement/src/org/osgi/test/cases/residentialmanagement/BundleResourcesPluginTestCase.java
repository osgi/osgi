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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;

import info.dmtree.DmtAdmin;
import info.dmtree.DmtException;
import info.dmtree.DmtIllegalStateException;
import info.dmtree.DmtSession;
import info.dmtree.DmtData;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.cases.residentialmanagement.util.*;

public class BundleResourcesPluginTestCase extends DefaultTestBundleControl {

	static final String INSTANCE_ID = "1";
	static final String PLUGIN_ROOT_URI = "./OSGi/" + INSTANCE_ID
			+ "/BundleResources";
	static final String TESTBUNDLELOCATION1 = "org.osgi.test.cases.residentialmanagement.tb9.jar";
	static final String TESTBUNDLEDIR1 = "TestDir";
	static final String TESTBUNDLEFILE1 = "TestFile1.txt";
	static final String TESTBUNDLEFILE2 = "TestFile2.txt";
	static final String TESTBUNDLEFILE3 = "TestFile3.txt";
	static final String TESTBUNDLEFILERATH1 = "/" + TESTBUNDLEDIR1 + "/"
			+ TESTBUNDLEFILE1;

	private DmtAdmin dmtAdmin;
	private DmtSession session;
	private BundleContext context;

	private Bundle testBundle1 = null;

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	protected void setUp() throws DmtException {
		ServiceReference dmtServiceRef = context
				.getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) context.getService(dmtServiceRef);
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_SHARED);
	}

	protected void tearDown() throws DmtException, BundleException {
		if (this.testBundle1 != null
				&& testBundle1.getState() != Bundle.UNINSTALLED)
			this.testBundle1.uninstall();
		this.testBundle1 = null;
		session.close();
		dmtAdmin = null;
	}

	public void testBundleResourcesNodeArchitecuture() throws DmtException,
			BundleException, IOException {
		// 1st descendants
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
			System.out.println("@testBundleResourcesNodeArchitecuture()1st descendants: "+bundleIds[i]);
			bundleIdTable.remove(bundleIds[i]);
		}
		assertEquals("Lack of Node in the BundleState Plugin.", 0,
				bundleIdTable.size());
		bundleIdTable = null;

		// 2nd descendants
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		checkNodeArc();
	}

/*	public void testBundleResourcesNodePermanency() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		checkNodeArc();
		testBundle1.uninstall();
		checkNodeArc();
	}
*/
	public void testBundleResourcesNodeAccess() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		DmtData file = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId()) + "/"
				+ TESTBUNDLEDIR1 + "/"+ TESTBUNDLEFILE1);
		try {
			file.getBase64();
		} catch (DmtIllegalStateException dise) {
			fail("This value must be Base64.");
		}
		session.close();
		boolean checkFlag = false;
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);
		try {
			// TODO:Base64
			String test = "";
			session.setNodeValue(PLUGIN_ROOT_URI + "/"
					+ Long.toString(testBundle1.getBundleId()) + "/"
					+ TESTBUNDLEDIR1 + TESTBUNDLEFILE1, new DmtData(test));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
	}

	public void testBundleResourcesCheckFileNode() throws DmtException,
			BundleException, IOException {
		testBundle1 = installAndStartBundle(TESTBUNDLELOCATION1);
		DmtData file = session.getNodeValue(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId()) + "/"
				+ TESTBUNDLEDIR1 + "/" + TESTBUNDLEFILE1);
		String fileSourceStrFromDataPlugin = Base64.byteArrayToBase64(file
				.getBase64());

		String fileSourceStr = null;
		URL url = testBundle1.getResource(TESTBUNDLEFILERATH1);
		try {
			InputStream in = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in)); 
			String line;
			String fileResource = "";
			while((line = br.readLine()) != null){
				fileResource = fileResource + line;
			}
			fileSourceStr = fileResource;
			
			
			/*InputStream in = url.openStream();
			byte[] b = {};
			int i = in.read(b);
			if (i == -1)
				fail();
			fileSourceStr = new String(b);*/
		} catch (IOException e) {
			fail();
		}
		System.out.println("##############################"+fileSourceStr);
		assertTrue(fileSourceStrFromDataPlugin.equals(fileSourceStr));
	}

	// -----Utilities-----
	private void checkNodeArc() throws DmtException {
		System.out.println("@ checkNodeArc(): ");
		Hashtable expectedFiles = new Hashtable();
		expectedFiles.put(TESTBUNDLEFILE1, "");
		expectedFiles.put(TESTBUNDLEFILE2, "");
		expectedFiles.put(TESTBUNDLEFILE3, "");
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ Long.toString(testBundle1.getBundleId()) + "/"
				+ TESTBUNDLEDIR1);
		if (children.equals(""))
			fail("Tere must be nodes");
		for (int i = 0; i < children.length; i++) {
			System.out.println("@ checkNodeArc(): "+children[i]);
			expectedFiles.remove(children[i]);
		}
		assertEquals(
				"There are undefined nodes in the BundleResources Plugin.", 0,
				expectedFiles.size());
		expectedFiles = null;
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
}
