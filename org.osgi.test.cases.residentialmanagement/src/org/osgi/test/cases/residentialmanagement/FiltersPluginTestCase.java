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

import info.dmtree.DmtAdmin;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class FiltersPluginTestCase extends DefaultTestBundleControl {

	static final String INSTANCE_ID = "1";
	static final String PLUGIN_ROOT_URI = "./OSGi/1/Filters";

	protected static final String TARGETSUBTREE = "TargetSubtree";
	protected static final String FILTER = "Filter";
	protected static final String RESULT = "Result";
	protected static final String TEST_URI = "";
	protected static final String TEST_TARGETSUBTREE1 = "/Device/Services/OSGi/1/BundleState/";
	protected static final String TEST_TARGETSUBTREE2 = "/Device/Services/OSGi/*/PackageState/";
	protected static final String TEST_TARGETSUBTREE3 = "/Device/Services/OSGi/1/ServiceState/";
	protected static final String TESR_FILTER1 = "(SymbolicName=org.osgi.test.cases.residentialmanagement.tb3.*)";
	protected static final String TESR_FILTER2 = "(&(ExportingBundle=0)(ImportingBundles=*))";
	protected static final String TESR_FILTER3 = "(@" + Constants.OBJECTCLASS
			+ "=org.osgi.test.cases.residentialmanagement.util.*)";
	protected static final String TESTBUNDLELOCATION1 = "org.osgi.test.cases.residentialmanagement.tb3.jar";
	protected static final String TESTBUNDLELOCATION2 = "org.osgi.test.cases.residentialmanagement.tb1.jar";
	
	private BundleContext context;
	private DmtAdmin dmtAdmin;
	private DmtSession session;

	private Bundle testBundle = null;

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	protected void setUp() throws DmtException {
		ServiceReference dmtServiceRef = context
				.getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) context.getService(dmtServiceRef);
	}

	protected void tearDown() throws Exception {
		if (this.testBundle != null
				&& testBundle.getState() != Bundle.UNINSTALLED)
			this.testBundle.uninstall();
		this.testBundle = null;
		session = null;
	}

	public void testFiltersObjectNodeCreation() throws DmtException {
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_EXCLUSIVE);
		int searchIdNumber = 10;
		for (int i = 0; i < searchIdNumber; i++) {
			String search_id = Integer.toString(i);
			session.createInteriorNode(PLUGIN_ROOT_URI + "/" + search_id, null);
			session.setNodeValue(PLUGIN_ROOT_URI + "/" + search_id + "/"
					+ TARGETSUBTREE, new DmtData(TEST_TARGETSUBTREE1));
			session.setNodeValue(PLUGIN_ROOT_URI + "/" + search_id + "/"
					+ FILTER, new DmtData(TESR_FILTER1));
		}
		String[] searchIds = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertEquals(searchIdNumber, searchIds.length);
		Hashtable table = new Hashtable();
		table.put(TARGETSUBTREE, "");
		table.put(FILTER, "");
		table.put(RESULT, "");
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/1");
		for (int j = 0; j < children.length; j++)
			table.remove(children[j]);
		assertTrue(table.size() == 0 && children.length == 3);
		String[] childrenDel = session.getChildNodeNames(PLUGIN_ROOT_URI);
		for (int i = 0; i < childrenDel.length; i++) {
			session.deleteNode(PLUGIN_ROOT_URI + "/" + childrenDel[i]);
		}
		session.close();
	}

	public void testFiltersObjectNodeDelete() throws DmtException {
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_EXCLUSIVE);
		int searchIdNumber = 10;
		int searchIdDeleteNumber = 5;
		for (int i = 0; i < searchIdNumber; i++) {
			String search_id = Integer.toString(i);
			session.createInteriorNode(PLUGIN_ROOT_URI + "/" + search_id, null);
		}
		String[] searchIdsBefore = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertEquals(searchIdNumber, searchIdsBefore.length);
		for (int j = 0; j < searchIdDeleteNumber; j++) {
			String search_id = Integer.toString(j);
			session.deleteNode(PLUGIN_ROOT_URI + "/" + search_id);
		}
		String[] searchIdsAfter = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertEquals(searchIdNumber - searchIdDeleteNumber,
				searchIdsAfter.length);
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI);
		for (int i = 0; i < children.length; i++) {
			session.deleteNode(PLUGIN_ROOT_URI + "/" + children[i]);
		}
		session.close();
	}

	public void testCheckTargetSubtreeNode() throws DmtException {
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_EXCLUSIVE);
		String search_id = "1";
		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + search_id, null);
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + search_id + "/"
				+ TARGETSUBTREE, new DmtData(TEST_TARGETSUBTREE1));
		String valueOfTargetSubtree = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + search_id + "/" + TARGETSUBTREE)
				.getString();
		assertEquals(TEST_TARGETSUBTREE1, valueOfTargetSubtree);
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + search_id + "/"
				+ TARGETSUBTREE, new DmtData(TEST_TARGETSUBTREE2));
		String valueOfTargetSubtreeAfter = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + search_id + "/" + TARGETSUBTREE)
				.getString();
		assertEquals(TEST_TARGETSUBTREE2, valueOfTargetSubtreeAfter);
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI);
		for (int i = 0; i < children.length; i++) {
			session.deleteNode(PLUGIN_ROOT_URI + "/" + children[i]);
		}
		session.close();
	}

	public void testCheckFilterNode() throws DmtException {
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_EXCLUSIVE);
		String search_id = "1";
		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + search_id, null);
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + search_id + "/" + FILTER,
				new DmtData(TESR_FILTER1));
		String valueOfFilter = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + search_id + "/" + FILTER).getString();
		assertEquals(TESR_FILTER1, valueOfFilter);
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + search_id + "/" + FILTER,
				new DmtData(TESR_FILTER2));
		String valueOfFilterAfter = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + search_id + "/" + FILTER).getString();
		assertEquals(TESR_FILTER2, valueOfFilterAfter);
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI);
		for (int i = 0; i < children.length; i++) {
			session.deleteNode(PLUGIN_ROOT_URI + "/" + children[i]);
		}
		session.close();
	}

	public void testResultSubtree1() throws DmtException,
	BundleException, IOException{
		testBundle = installAndStartBundle(TESTBUNDLELOCATION1);
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_EXCLUSIVE);
		String search_id = "1";
		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + search_id, null);
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + search_id + "/"
				+ TARGETSUBTREE, new DmtData(TEST_TARGETSUBTREE1));
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + search_id + "/" + FILTER,
				new DmtData(TESR_FILTER1));
		session.close();
		session = null;
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_SHARED);
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ search_id + "/" + RESULT
				+ "/Device/Services/OSGi/1/BundleState");
		Hashtable table = new Hashtable();
		for (int i = 0; i < children.length; i++) {
			table.put(children[i], "");
		}
		table.remove(Long.toString(testBundle.getBundleId()));
		assertEquals(table.size(),0);
		
		session.close();
	}

	public void testResultSubtree2() throws DmtException,
	BundleException, IOException{
		testBundle = installAndStartBundle(TESTBUNDLELOCATION1);
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_EXCLUSIVE);
		String search_id = "2";
		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + search_id, null);
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + search_id + "/"
				+ TARGETSUBTREE, new DmtData(TEST_TARGETSUBTREE3));
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + search_id + "/" + FILTER,
				new DmtData(TESR_FILTER3));
		session.close();
		session = null;
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_SHARED);
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ search_id + "/" + RESULT
				+ "/Device/Services/OSGi/1/ServiceState");
		Hashtable table = new Hashtable();
		for (int i = 0; i < children.length; i++) {
			table.put(children[i], "");
		}
		ServiceReference[] sr = testBundle.getRegisteredServices();
		for(int j=0;j<sr.length;j++){
			table.remove(sr[j].getProperty(Constants.SERVICE_ID).toString());
		}
		assertEquals(table.size(),0);
		session.close();
	}

	public void testResultSubtree3() throws DmtException,
	BundleException, IOException{
		testBundle = installAndStartBundle(TESTBUNDLELOCATION2);
		String bundleId = Long.toString(testBundle.getBundleId());
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_EXCLUSIVE);
		String search_id = "3";
		session.createInteriorNode(PLUGIN_ROOT_URI + "/" + search_id, null);
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + search_id + "/"
				+ TARGETSUBTREE, new DmtData(TEST_TARGETSUBTREE2));
		session.setNodeValue(PLUGIN_ROOT_URI + "/" + search_id + "/" + FILTER,
				new DmtData("(&(ExportingBundle="+bundleId+")(ImportingBundles=*))"));
		session.close();
		session = null;
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_SHARED);
		String[] children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ search_id + "/" + RESULT
				+ "/Device/Services/OSGi/1/PackageState");
		assertEquals(children.length,1);
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
}
