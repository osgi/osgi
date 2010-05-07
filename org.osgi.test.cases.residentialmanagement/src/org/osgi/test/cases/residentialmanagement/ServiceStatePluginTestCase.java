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
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.residentialmanagement.util.Service3;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class ServiceStatePluginTestCase extends DefaultTestBundleControl {

	static final String INSTANCE_ID = "1";
	static final String PLUGIN_ROOT_URI = "./OSGi/1/ServiceState";

	protected static final String PROPERTIES = "Properties";
	protected static final String REGISTERINGBUNDLE = "RegisteringBundle";
	protected static final String USINGBUNDLES = "UsingBundle";
	protected static final String VALUES = "Values";
	protected static final String TYPE = "Type";
	protected static final String CARDINALITY = "Cardinality";
	protected static final String KEY = "Key";
	protected static final String SCALAR = "scalar";
	protected static final String ARRAY = "array";
	protected static final String TESTBUNDLELOCATION3 = "org.osgi.test.cases.residentialmanagement.tb3.jar";
	protected static final String TESTBUNDLELOCATION4 = "org.osgi.test.cases.residentialmanagement.tb4.jar";
	protected static final String TESTBUNDLELOCATION5 = "org.osgi.test.cases.residentialmanagement.tb5.jar";
	protected static final String TEST_INTERFACE_NAME1 = "org.osgi.test.cases.residentialmanagement.util.Service1";
	protected static final String TEST_INTERFACE_NAME2 = "org.osgi.test.cases.residentialmanagement.util.Service2";
	protected static final String TEST_INTERFACE_NAME3 = "org.osgi.test.cases.residentialmanagement.util.Service3";
	protected static final String TEST_PROPERTY_KEY1 = "testKey1";
	protected static final String TEST_PROPERTY_VALUE1 = "testValue1";

	private BundleContext context;
	private DmtAdmin dmtAdmin;
	private DmtSession session;

	private ServiceReference dmtServiceRef;
	private Bundle testBundle3 = null;
	private Bundle testBundle4 = null;
	private Bundle testBundle5 = null;
	private String[] children;
	private String[] serviceIds;
	private boolean checkFlag = false;

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	protected void setUp() throws DmtException {
		dmtServiceRef = context.getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) context.getService(dmtServiceRef);
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_SHARED);
	}

	protected void tearDown() throws Exception {
		if (this.testBundle3 != null
				&& testBundle3.getState() != Bundle.UNINSTALLED)
			this.testBundle3.uninstall();
		this.testBundle3 = null;
		if (this.testBundle4 != null
				&& testBundle4.getState() != Bundle.UNINSTALLED)
			this.testBundle4.uninstall();
		this.testBundle5 = null;
		if (this.testBundle5 != null
				&& testBundle4.getState() != Bundle.UNINSTALLED)
			this.testBundle5.uninstall();
		this.testBundle5 = null;
		session.close();
		checkFlag = false;
		serviceIds = null;
	}

	public void testServiceStateNodeArchitecuture() throws DmtException,
			InvalidSyntaxException {
		// 1st descendants
		Hashtable serviceIdTable = new Hashtable();
		ServiceReference[] serviceAllRef = context.getAllServiceReferences(
				null, null);
		assertNotNull("This object should not be null.", serviceAllRef);
		for (int i = 0; i < serviceAllRef.length; i++) {
			Long currentServiceIdsOfInt = (Long) serviceAllRef[i]
					.getProperty(Constants.SERVICE_ID);
			String currentServiceIds = currentServiceIdsOfInt.toString();
			serviceIdTable.put(currentServiceIds, "");
		}
		serviceIds = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", serviceIds);
		for (int i = 0; i < serviceIds.length; i++) {
			serviceIdTable.remove(serviceIds[i]);
		}
		assertEquals("Lack of Node in the ServiceState Plugin.", 0,
				serviceIdTable.size());
		// 2nd descendants
		Hashtable expected = new Hashtable();
		for (int i = 0; i < serviceIds.length; i++) {
			expected.put(PROPERTIES, "");
			expected.put(REGISTERINGBUNDLE, "");
			expected.put(USINGBUNDLES, "");
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ serviceIds[i]);
			assertNotNull("This object should not be null.", children);
			for (int j = 0; j < children.length; j++) {
				expected.remove(children[j]);
			}
			assertEquals(
					"There are undefined nodes in the PackageState Plugin.", 0,
					expected.size());
		}

		// 3rd descendants
		// properties
		for (int i = 0; i < serviceIds.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ serviceIds[i] + "/" + PROPERTIES);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
								+ PROPERTIES + "/" + j + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String className = session.getNodeValue(
							PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
									+ PROPERTIES + "/" + j + "/" + VALUES)
							.getString();
					String[] classNames = processCommaSeparatedValue(className);
					String serviceIdFilter = "(" + Constants.SERVICE_ID + "="
							+ serviceIds[i] + ")";
					for (int k = 0; k < classNames.length; k++) {
						ServiceReference[] serviceRef = context
								.getServiceReferences(classNames[k],
										serviceIdFilter);
						for (int l = 0; l < serviceRef.length; l++) {
							String[] keys = serviceRef[l].getPropertyKeys();
							if (keys.length != children.length)
								fail("The keys number of this node is incorrect.");
						}
					}
				}
			}
		}

		// 4th descendants
		Hashtable expectedProp = new Hashtable();
		for (int i = 0; i < serviceIds.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ serviceIds[i] + "/" + PROPERTIES);
			assertNotNull("This object should not be null.", children);
			for (int j = 0; j < children.length; j++) {
				String[] childrenOfThisId = session
						.getChildNodeNames(PLUGIN_ROOT_URI + "/"
								+ serviceIds[i] + "/" + PROPERTIES + "/"
								+ children[j]);
				expectedProp.put(KEY, "");
				expectedProp.put(TYPE, "");
				expectedProp.put(CARDINALITY, "");
				expectedProp.put(VALUES, "");
				for (int k = 0; k < childrenOfThisId.length; k++) {
					expectedProp.remove(childrenOfThisId[k]);
				}
				assertEquals(
						"There are undefined nodes in the PackageState Plugin.",
						0, expectedProp.size());
			}
		}
	}

	public void testServiceStateNodeCreation() throws DmtException,
			BundleException, IOException {
		serviceIds = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", serviceIds);
		for (int i = 0; i < serviceIds.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ serviceIds[i] + "/" + PROPERTIES);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
								+ PROPERTIES + "/" + j + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String className = session.getNodeValue(
							PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
									+ PROPERTIES + "/" + j + "/" + VALUES)
							.getString();
					String[] classNames = processCommaSeparatedValue(className);
					for (int k = 0; k < classNames.length; k++) {
						if (classNames[k].equals(TEST_INTERFACE_NAME1))
							fail("Test service1 must not exist.");
					}
				}
			}
		}
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		serviceIds = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", serviceIds);
		for (int i = 0; i < serviceIds.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ serviceIds[i] + "/" + PROPERTIES);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
								+ PROPERTIES + "/" + j + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String className = session.getNodeValue(
							PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
									+ PROPERTIES + "/" + j + "/" + VALUES)
							.getString();
					String[] classNames = processCommaSeparatedValue(className);
					for (int k = 0; k < classNames.length; k++) {
						if (classNames[k].equals(TEST_INTERFACE_NAME1)) {
							checkFlag = true;
							break;
						}
					}
				}
			}
		}
		assertTrue(checkFlag);
	}

	public void testServiceStateNodeDelete() throws DmtException,
			BundleException, IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		serviceIds = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", serviceIds);

		for (int i = 0; i < serviceIds.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ serviceIds[i] + "/" + PROPERTIES);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
								+ PROPERTIES + "/" + j + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String className = session.getNodeValue(
							PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
									+ PROPERTIES + "/" + j + "/" + VALUES)
							.getString();
					String[] classNames = processCommaSeparatedValue(className);
					for (int k = 0; k < classNames.length; k++) {
						if (classNames[k].equals(TEST_INTERFACE_NAME1)) {
							checkFlag = true;
							break;
						}
					}
				}
			}
		}
		assertTrue(checkFlag);
		testBundle3.uninstall();
		serviceIds = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", serviceIds);
		for (int i = 0; i < serviceIds.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ serviceIds[i] + "/" + PROPERTIES);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
								+ PROPERTIES + "/" + j + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String className = session.getNodeValue(
							PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
									+ PROPERTIES + "/" + j + "/" + VALUES)
							.getString();
					String[] classNames = processCommaSeparatedValue(className);
					for (int k = 0; k < classNames.length; k++) {
						if (classNames[k].equals(TEST_INTERFACE_NAME1))
							fail("Test service1 must not exist.");
					}
				}
			}
		}
	}

	public void testServiceStateNodeAccess() throws DmtException {
		// ReadOnlyNode
		serviceIds = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", serviceIds);
		DmtData data = null;
		try {
			// <interface_name>
			for (int i = 0; i < serviceIds.length; i++) {
				children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
						+ serviceIds[i] + "/" + PROPERTIES);
				for (int j = 0; j < children.length; j++) {
					String key = session.getNodeValue(
							PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
									+ PROPERTIES + "/" + j + "/" + KEY)
							.getString();
					if (key.equals(Constants.OBJECTCLASS)) {
						data = session.getNodeValue(PLUGIN_ROOT_URI + "/"
								+ serviceIds[i] + "/" + PROPERTIES + "/" + j
								+ "/" + VALUES);
						data.getString();
					}
				}
			}

			// RegisteringBundle/<bundle_id>
			for (int i = 0; i < serviceIds.length; i++) {
				data = session.getNodeValue(PLUGIN_ROOT_URI + "/"
						+ serviceIds[i] + "/" + REGISTERINGBUNDLE);
				data.getString();
			}

			// UsingBundles/<bundle_id>
			for (int i = 0; i < serviceIds.length; i++) {
				data = session.getNodeValue(PLUGIN_ROOT_URI + "/"
						+ serviceIds[i] + "/" + USINGBUNDLES);
				data.getString();
			}
			
			// Properties
			for (int s = 0; s < serviceIds.length; s++) {
				String[] keyIds = session.getChildNodeNames(PLUGIN_ROOT_URI
						+ "/" + serviceIds[s] + "/" + PROPERTIES);
				assertNotNull("This object should not be null.", keyIds);
				for (int i = 0; i < keyIds.length; i++) {
					DmtData key = session.getNodeValue(PLUGIN_ROOT_URI + "/"
							+ serviceIds[s] + "/" + PROPERTIES + "/"
							+ keyIds[i] + "/" + KEY);
					key.getString();
					DmtData type = session.getNodeValue(PLUGIN_ROOT_URI + "/"
							+ serviceIds[s] + "/" + PROPERTIES + "/"
							+ keyIds[i] + "/" + TYPE);
					type.getString();
					DmtData cardinality = session.getNodeValue(PLUGIN_ROOT_URI
							+ "/" + serviceIds[s] + "/" + PROPERTIES + "/"
							+ keyIds[i] + "/" + CARDINALITY);
					cardinality.getString();
					DmtData value = session.getNodeValue(PLUGIN_ROOT_URI + "/"
							+ serviceIds[s] + "/" + PROPERTIES + "/"
							+ keyIds[i] + "/" + VALUES);
					value.getString();
				}
			}
		} catch (DmtIllegalStateException e) {
			e.printStackTrace();
			fail("Leaf node contains illegal format values.");
		} catch (DmtException e) {
			e.printStackTrace();
			fail("Can not get a leaf node's value.");
		}
		session.close();

		// Write operation must be fail.
		session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
				DmtSession.LOCK_TYPE_ATOMIC);
		// Properties
		try {
			String[] keyIds = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ serviceIds[0] + "/" + PROPERTIES);
			assertNotNull("This object should not be null.", keyIds);
			for (int i = 0; i < keyIds.length; i++) {
				session.setNodeValue(PLUGIN_ROOT_URI + "/" + serviceIds[0]
						+ "/" + PROPERTIES + "/" + keyIds[i] + "/" + KEY,
						new DmtData("test"));
				session.setNodeValue(PLUGIN_ROOT_URI + "/" + serviceIds[0]
						+ "/" + PROPERTIES + "/" + keyIds[i] + "/" + TYPE,
						new DmtData("test"));
				session.setNodeValue(PLUGIN_ROOT_URI + "/" + serviceIds[0]
						+ "/" + PROPERTIES + "/" + keyIds[i] + "/"
						+ CARDINALITY, new DmtData("test"));
				session.setNodeValue(PLUGIN_ROOT_URI + "/" + serviceIds[0]
						+ "/" + PROPERTIES + "/" + keyIds[i] + "/" + VALUES,
						new DmtData("test"));
			}
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		// RegisteringBundle/<bundle_id>
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/" + serviceIds[0] + "/"
					+ REGISTERINGBUNDLE, new DmtData("test"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		// UsingBundles/<bundle_id>
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/" + serviceIds[0] + "/"
					+ USINGBUNDLES, new DmtData("test"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
	}

	// ServiceId node
	public void testCheckServiceId() throws DmtException, BundleException,
			IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		serviceIds = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", serviceIds);
		String beforeServiceId = null;
		String afterServiceId = null;
		String currentServiceId = getServiceId(TEST_INTERFACE_NAME1);
		for (int i = 0; i < serviceIds.length; i++) {
			if (serviceIds[i].equals(currentServiceId)) {
				beforeServiceId = serviceIds[i];
				break;
			}
		}
		testBundle3.uninstall();
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		serviceIds = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", serviceIds);
		currentServiceId = getServiceId(TEST_INTERFACE_NAME1);
		for (int i = 0; i < serviceIds.length; i++) {
			if (serviceIds[i].equals(currentServiceId)) {
				afterServiceId = serviceIds[i];
				break;
			}
		}
		assertTrue(beforeServiceId != null && afterServiceId != null
				&& !afterServiceId.equals(beforeServiceId));
	}

	// Interfaces node check
	public void testCheckInterfaces() throws DmtException, BundleException,
			IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		String currentServiceId = getServiceId(TEST_INTERFACE_NAME1);
		String[] classNames = null;
		Hashtable currentClassNmae = new Hashtable();
		currentClassNmae.put(TEST_INTERFACE_NAME1, "");
		currentClassNmae.put(TEST_INTERFACE_NAME2, "");
		currentClassNmae.put(TEST_INTERFACE_NAME3, "");
		String[] numberOfProperties = session.getChildNodeNames(PLUGIN_ROOT_URI
				+ "/" + currentServiceId + "/" + PROPERTIES);
		for (int i = 0; i < numberOfProperties.length; i++) {
			String key = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + currentServiceId + "/" + PROPERTIES
							+ "/" + i + "/" + KEY).getString();
			if (key.equals(Constants.OBJECTCLASS)) {
				String className = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + currentServiceId + "/"
								+ PROPERTIES + "/" + i + "/" + VALUES)
						.getString();
				classNames = processCommaSeparatedValue(className);
				for (int k = 0; k < classNames.length; k++) {
					currentClassNmae.remove(classNames[k]);
				}
			}
		}
		assertTrue("The interfaces number of node is incorrect.",
				(0 == currentClassNmae.size()) && (3 == classNames.length));
	}

	// RegisteringBundle node check
	public void testCheckRegisteringBundle() throws DmtException,
			BundleException, IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		String currentServiceId = getServiceId(TEST_INTERFACE_NAME1);
		String currentRegisteringBundleId = getRegisteringBundleId(TEST_INTERFACE_NAME1);
		String registeringBundleId = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + currentServiceId + "/"
						+ REGISTERINGBUNDLE).getString();
		assertNotNull("This object should not be null.", registeringBundleId);
		if (currentRegisteringBundleId.equals(registeringBundleId)) {
			checkFlag = true;
		}
		assertTrue("This bundle id is incorrect", checkFlag);
	}

	// UsingBundles node check
	public void testCheckUsingBundle() throws DmtException, BundleException,
			IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		testBundle4 = installAndStartBundle(TESTBUNDLELOCATION4);
		testBundle5 = installAndStartBundle(TESTBUNDLELOCATION5);
		String currentServiceId = getServiceId(TEST_INTERFACE_NAME1);
		Hashtable currentUsingBundleIds = getUsingBundleIds(TEST_INTERFACE_NAME1);
		String usingBundleIds = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + currentServiceId + "/" + USINGBUNDLES)
				.getString();
		assertNotNull("This object should not be null.", usingBundleIds);
		String[] usingBundleIdsArray = processCommaSeparatedValue(usingBundleIds);
		for (int i = 0; i < usingBundleIdsArray.length; i++) {
			currentUsingBundleIds.remove(usingBundleIdsArray[i]);
		}
		assertEquals("The usingBundleId number of node is incorrect.", 0,
				currentUsingBundleIds.size());
	}

	public void testCheckPropertiesNode() throws DmtException, BundleException,
			IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		String testServiceId = getServiceId(TEST_INTERFACE_NAME1);
		// objectClass
		String[] keyIds = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ testServiceId + "/" + PROPERTIES);
		for (int i = 0; i < keyIds.length; i++) {
			String keyValue = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + testServiceId + "/" + PROPERTIES
							+ "/" + keyIds[i] + "/" + KEY).getString();
			if (keyValue.equals(Constants.OBJECTCLASS)) {
				DmtData objectClassDataOfType = session
						.getNodeValue(PLUGIN_ROOT_URI + "/" + testServiceId
								+ "/" + PROPERTIES + "/" + keyIds[i] + "/"
								+ TYPE);
				String type = objectClassDataOfType.getString();
				assertTrue(type.equals(String.class.getName()));
				DmtData objectClassDataOfCardinality = session
						.getNodeValue(PLUGIN_ROOT_URI + "/" + testServiceId
								+ "/" + PROPERTIES + "/" + keyIds[i] + "/"
								+ CARDINALITY);
				String cardinality = objectClassDataOfCardinality.getString();
				assertTrue(cardinality.equals(ARRAY));
				DmtData objectClassDataOfValue = session
						.getNodeValue(PLUGIN_ROOT_URI + "/" + testServiceId
								+ "/" + PROPERTIES + "/" + keyIds[i] + "/"
								+ VALUES);
				String values = objectClassDataOfValue.getString();
				assertFalse(values.equals(""));
				String[] valuesArray = processCommaSeparatedValue(values);
				for (int j = 0; j < valuesArray.length; j++) {
					if (valuesArray[j].equals(TEST_INTERFACE_NAME1)) {
						checkFlag = true;
						break;
					}
				}
			}
		}
		assertTrue("This value is incorrect", checkFlag);
		checkFlag = false;

		// service.id

		for (int i = 0; i < keyIds.length; i++) {
			String keyValue = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + testServiceId + "/" + PROPERTIES
							+ "/" + keyIds[i] + "/" + KEY).getString();
			if (keyValue.equals(Constants.SERVICE_ID)) {
				DmtData serviceIdDataOfType = session
						.getNodeValue(PLUGIN_ROOT_URI + "/" + testServiceId
								+ "/" + PROPERTIES + "/" + keyIds[i] + "/"
								+ TYPE);
				String type = serviceIdDataOfType.getString();
				assertTrue(type.equals(Integer.TYPE.getName()));
				DmtData objectClassDataOfCardinality = session
						.getNodeValue(PLUGIN_ROOT_URI + "/" + testServiceId
								+ "/" + PROPERTIES + "/" + keyIds[i] + "/"
								+ CARDINALITY);
				String cardinality = objectClassDataOfCardinality.getString();
				assertTrue(cardinality.equals(SCALAR));

				DmtData serviceIdDataOfValue = session
						.getNodeValue(PLUGIN_ROOT_URI + "/" + testServiceId
								+ "/" + PROPERTIES + "/" + keyIds[i] + "/"
								+ VALUES);
				String values = serviceIdDataOfValue.getString();
				assertFalse(values.equals(""));
				if (values.equals(testServiceId)) {
					checkFlag = true;
					break;
				}
			}
		}
		assertTrue("This value is incorrect", checkFlag);
	}

	public void testServicePropertiesModify() throws DmtException,
			BundleException, IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		String serviceId = this.getServiceId(TEST_INTERFACE_NAME3);
		String[] servicePropertiesNumber = session
				.getChildNodeNames(PLUGIN_ROOT_URI + "/" + serviceId + "/"
						+ PROPERTIES);
		int valueNumber = servicePropertiesNumber.length;
		System.out.println("###valueNumber: " + valueNumber);
		ServiceReference serviceRef = context
				.getServiceReference(Service3.class.getName());
		Service3 service = (Service3) context.getService(serviceRef);
		service.modifytServiceProperty();
		servicePropertiesNumber = session.getChildNodeNames(PLUGIN_ROOT_URI
				+ "/" + serviceId + "/" + PROPERTIES);
		int valueNumberMod = servicePropertiesNumber.length;
		System.out.println("###valueNumberMod: " + valueNumberMod);
		assertTrue("The property must be changed.",
				valueNumber != valueNumberMod);
	}

	public void testServiceStateNodeUpdate() throws DmtException,
			BundleException, IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		serviceIds = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", serviceIds);
		String serviceId = null;
		for (int i = 0; i < serviceIds.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ serviceIds[i] + "/" + PROPERTIES);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
								+ PROPERTIES + "/" + j + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String className = session.getNodeValue(
							PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
									+ PROPERTIES + "/" + j + "/" + VALUES)
							.getString();
					String[] classNames = processCommaSeparatedValue(className);
					for (int k = 0; k < classNames.length; k++) {
						if (classNames[k].equals(TEST_INTERFACE_NAME1)) {
							checkFlag = true;
							serviceId = serviceIds[i];
							break;
						}
					}
				}
			}
		}
		assertTrue(checkFlag);
		checkFlag = false;
		this.updateBundle(TESTBUNDLELOCATION3, testBundle3);
		serviceIds = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", serviceIds);
		for (int i = 0; i < serviceIds.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ serviceIds[i] + "/" + PROPERTIES);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
								+ PROPERTIES + "/" + j + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String className = session.getNodeValue(
							PLUGIN_ROOT_URI + "/" + serviceIds[i] + "/"
									+ PROPERTIES + "/" + j + "/" + VALUES)
							.getString();
					String[] classNames = processCommaSeparatedValue(className);
					for (int k = 0; k < classNames.length; k++) {
						if (classNames[k].equals(TEST_INTERFACE_NAME1)
								&& !serviceId.equals(serviceIds[i])) {
							checkFlag = true;
							break;
						}
					}
				}
			}
		}
		assertTrue(checkFlag);
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

	private String getServiceId(String objectClass) {
		ServiceReference serviceRefForTest = context
				.getServiceReference(objectClass);
		Long serviceIdOfLong = (Long) serviceRefForTest
				.getProperty(Constants.SERVICE_ID);
		String serviceId = serviceIdOfLong.toString();
		return serviceId;
	}

	private String getRegisteringBundleId(String objectClass) {
		ServiceReference serviceRefForTest = context
				.getServiceReference(objectClass);
		Bundle bundle = serviceRefForTest.getBundle();
		long bundleIdOfLong = bundle.getBundleId();
		String bundleId = Long.toString(bundleIdOfLong);
		return bundleId;
	}

	private Hashtable getUsingBundleIds(String objectClass) {
		Hashtable table = new Hashtable();
		ServiceReference serviceRefForTest = context
				.getServiceReference(objectClass);
		Bundle[] bundles = serviceRefForTest.getUsingBundles();
		for (int i = 0; i < bundles.length; i++) {
			long bundleIdOfLong = bundles[i].getBundleId();
			String bundleId = Long.toString(bundleIdOfLong);
			table.put(bundleId, "");
		}
		return table;
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
