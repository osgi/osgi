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
package org.osgi.test.cases.residentialmanagement.old;

import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

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
	
	static final String SERVICE_STATE_NODE_TYPE = "org.osgi/1.0/ServiceStateManagementObject";

	protected static final String PROPERTY = "Property";
	protected static final String REGISTERINGBUNDLE = "RegisteringBundle";
	protected static final String USINGBUNDLES = "UsingBundles";
	protected static final String VALUES = "Values";
	protected static final String TYPE = "Type";
	protected static final String CARDINALITY = "Cardinality";
	protected static final String KEY = "Key";
	protected static final String SCALAR = "SCALAR";
	protected static final String ARRAY = "ARRAY";
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
	private String[] ids;
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
		ids = null;
	}
	
	/**
	 * Test of checking ServiceState MO node structure.
	 * 
	 * @throws DmtException
	 * @throws InvalidSyntaxException
	 */
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
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			serviceIdTable.remove(ids[i]);
		}
		assertEquals("Lack of Node in the ServiceState Plugin.", 0,
				serviceIdTable.size());
		// 2nd descendants
		Hashtable expected = new Hashtable();
		for (int i = 0; i < ids.length; i++) {
			expected.put(PROPERTY, "");
			expected.put(REGISTERINGBUNDLE, "");
			expected.put(USINGBUNDLES, "");
			children = session
					.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]);
			assertNotNull("This object should not be null.", children);
			for (int j = 0; j < children.length; j++) {
				expected.remove(children[j]);
			}
			assertEquals(
					"There are undefined nodes in the ServiceState Plugin.", 0,
					expected.size());
		}

		// 3rd descendants
		// properties
		for (int i = 0; i < ids.length; i++) {
			String[] propertyIds = session.getChildNodeNames(PLUGIN_ROOT_URI
					+ "/" + ids[i] + "/" + PROPERTY);
			for (int j = 0; j < propertyIds.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY + "/"
								+ propertyIds[j] + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String[] values = session.getChildNodeNames(PLUGIN_ROOT_URI
							+ "/" + ids[i] + "/" + PROPERTY + "/"
							+ propertyIds[j] + "/" + VALUES);
					for (int k = 0; k < values.length; k++) {
						String className = session.getNodeValue(
								PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY
										+ "/" + propertyIds[j] + "/" + VALUES
										+ "/" + values[k]).getString();
						String serviceIdFilter = "(" + Constants.SERVICE_ID
								+ "=" + ids[i] + ")";
						ServiceReference[] serviceRef = context
								.getServiceReferences(className,
										serviceIdFilter);
						for (int l = 0; l < serviceRef.length; l++) {
							String[] keys = serviceRef[l].getPropertyKeys();
							if (keys.length != propertyIds.length)
								fail("The keys number of this node is incorrect.");
						}
					}
				}
			}
		}

		// UsingBundles
		for (int i = 0; i < ids.length; i++) {
			String[] usingBundles = session.getChildNodeNames(PLUGIN_ROOT_URI
					+ "/" + ids[i] + "/" + USINGBUNDLES);
			String[] propertyIds = session.getChildNodeNames(PLUGIN_ROOT_URI
					+ "/" + ids[i] + "/" + PROPERTY);
			for (int j = 0; j < propertyIds.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY + "/"
								+ propertyIds[j] + "/" + KEY).getString();
				if (key.equals(Constants.SERVICE_ID)) {
					String[] values = session.getChildNodeNames(PLUGIN_ROOT_URI
							+ "/" + ids[i] + "/" + PROPERTY + "/"
							+ propertyIds[j] + "/" + VALUES);
					long serviceId = session.getNodeValue(
							PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY
									+ "/" + propertyIds[j] + "/" + VALUES + "/"
									+ values[0]).getLong();
					String serviceIdFilter = "(" + Constants.SERVICE_ID + "="
							+ serviceId + ")";
					ServiceReference[] serviceRef = context
							.getServiceReferences(null, serviceIdFilter);
					Bundle[] bundles = serviceRef[0].getUsingBundles();
					if (bundles == null) {
						assertEquals(
								"The number of using bundles is not correct.",
								usingBundles.length, 0);
						break;
					} else {
						assertEquals(
								"The number of using bundles is not correct.",
								usingBundles.length, bundles.length);
						break;
					}
				}
			}
		}

		// 4th descendants
		Hashtable expectedProp = new Hashtable();
		for (int i = 0; i < ids.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
					+ "/" + PROPERTY);
			assertNotNull("This object should not be null.", children);
			for (int j = 0; j < children.length; j++) {
				String[] childrenOfThisId = session
						.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i] + "/"
								+ PROPERTY + "/" + children[j]);
				expectedProp.put(KEY, "");
				expectedProp.put(TYPE, "");
				expectedProp.put(CARDINALITY, "");
				expectedProp.put(VALUES, "");
				for (int k = 0; k < childrenOfThisId.length; k++) {
					expectedProp.remove(childrenOfThisId[k]);
				}
				assertEquals(
						"There are undefined nodes in the ServiceState Plugin.",
						0, expectedProp.size());
			}
		}
	}

	/**
	 * Tests the specified node types.
	 *  
	 */
	public void testServiceStateNodeTypes() throws BundleException, IOException {
		try {
			assertEquals("The PackageState node must be of type: " + SERVICE_STATE_NODE_TYPE,
					SERVICE_STATE_NODE_TYPE,
					session.getNodeType(PLUGIN_ROOT_URI));

			ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
			assertNotNull(ids);
			assertTrue(ids.length > 0 );

			for (int i = 0; i < ids.length; i++) {
				assertEquals("The ID node must be of type DDF_LIST_SUBTREE",
						DmtConstants.DDF_LIST,
						session.getNodeType(PLUGIN_ROOT_URI + "/" + ids[i] + "/" + USINGBUNDLES));

				String uri = PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY;
				System.out.println( "Property node type = " + session.getNodeType(uri));
				String[] propIDs = session.getChildNodeNames(uri);
				boolean hasServicePID = false;
				for (int j = 0; propIDs != null && j < propIDs.length; j++) {
					
					assertEquals("The Property Values node must be of type DDF_LIST_SUBTREE",
							DmtConstants.DDF_LIST,
							session.getNodeType(uri + "/" + propIDs[j] + "/" + VALUES ));

					if ( Constants.SERVICE_PID.equals( session.getNodeValue(uri + "/" + propIDs[j] + "/" + KEY )))
						hasServicePID = true;
				}
				// the type of the ID can either be node or transient_node, depending 
				// on the existence of the service property SERVICE_PID 
				// TODO commented out
//			if ( ! hasServicePID )
//					assertEquals("The ID node must be of type DDF_TRANSIENT for services without a SERVICE_PID property",
//							DmtConstants.DDF_TRANSIENT,
//							session.getNodeType(PLUGIN_ROOT_URI + "/" + ids[i]));

			}
		} catch (DmtException de) {
			de.printStackTrace();
			fail();
		}
	}

	/**
	 * Test that the service ids are greater than 0;
	 * 
	 */
	public void testServiceStateIDsNotZero() {
		try {
			ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
			for (int i = 0; i < ids.length; i++)
				assertTrue("The service-id must be greater than 0",
						Long.parseLong(ids[i]) > 0);
		} catch (DmtException de) {
			de.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Tests the metadata of the ServiceState nodes.
	 * This ensures that only read-operations are allowed on the nodes and that the scope and format is correct.   
	 * @throws BundleException 
	 * @throws IOException 
	 * 
	 */
	public void testServiceStateMetaData() throws IOException, BundleException {
		try {
			// install and start a bundle that registers a service with properties
			testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
			ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
			String uri = PLUGIN_ROOT_URI;
			assertMetaData( uri, MetaNode.PERMANENT);
			for (int i = 0; i < ids.length; i++) {
				uri = PLUGIN_ROOT_URI + "/" + ids[i];
				assertMetaData( uri, MetaNode.AUTOMATIC);
				assertMetaData( uri + "/" + REGISTERINGBUNDLE, MetaNode.AUTOMATIC, DmtData.FORMAT_LONG);

				String uriUsingBundles = uri + "/" + USINGBUNDLES; 
				assertMetaData( uriUsingBundles, MetaNode.AUTOMATIC);
				String[] usingBundles = session.getChildNodeNames(uriUsingBundles);
				for (int j = 0; j < usingBundles.length; j++)
					assertMetaData( uriUsingBundles + "/" + usingBundles[j], MetaNode.AUTOMATIC, DmtData.FORMAT_LONG);

				if ( session.isNodeUri(uri + "/Ext")) 
					assertMetaData( uri + "/Ext", MetaNode.AUTOMATIC);
				
				assertMetaData( uri += "/" + PROPERTY, MetaNode.AUTOMATIC);
				String[] props = session.getChildNodeNames(uri);
				for (int j = 0; j < props.length; j++) {
					String uriProp = uri + "/" + props[j];
					assertMetaData( uriProp , MetaNode.AUTOMATIC);
					assertMetaData( uriProp +  "/" + KEY, MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
					assertMetaData( uriProp +  "/" + TYPE, MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
					assertMetaData( uriProp +  "/" + CARDINALITY, MetaNode.AUTOMATIC, DmtData.FORMAT_STRING);
					assertMetaData( uriProp +  "/" + VALUES, MetaNode.AUTOMATIC);
					String[] values = session.getChildNodeNames(uriProp + "/" + VALUES);
					for (int k = 0; k < values.length; k++) {
						assertMetaData(uriProp + "/" + VALUES + "/" + values[k], MetaNode.AUTOMATIC );
						// TODO: check the node format against the property TYPE 
					}
					if ( session.isNodeUri(uriProp + "/Ext")) 
						assertMetaData( uriProp + "/Ext", MetaNode.AUTOMATIC);
				}
			}
			
		} catch (DmtException de) {
			de.printStackTrace();
			fail("unexpeced DmtException: " + de.getMessage());
		}
	}

	private void assertMetaData( String uri, int scope ) throws DmtException {
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
	 * 
	 * [testBundle3]
	 * This bundle registers OSGi service whose objectName are
	 * "org.osgi.test.cases.residentialmanagement.util.Service1" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service2" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service3".
	 * The OSGi service has properties which are ("testKey1"="testValue1") 
	 * and ("testKey2"="testValue3").
	 * 
	 * Test of node creation.
	 * precondition  : 
	 * "testBundle3" is not installed. Therefore, "Service1" is not registered yet.
	 * postcondition : 
	 * "testBundle3" is installed. Then, "Service1" is registered and this MO must 
	 * create subtree about the registered "Service1".
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testServiceStateNodeCreation() throws DmtException,
			BundleException, IOException {
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
					+ "/" + PROPERTY);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY + "/"
								+ children[j] + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String[] classNames = session
							.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
									+ "/" + PROPERTY + "/" + children[j] + "/"
									+ VALUES);
					for (int k = 0; k < classNames.length; k++) {
						String className = session.getNodeValue(
								PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY
										+ "/" + children[j] + "/" + VALUES
										+ "/" + classNames[k]).getString();
						if (className.equals(TEST_INTERFACE_NAME1))
							fail("Test service1 must not exist.");
					}
				}
			}
		}
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
					+ "/" + PROPERTY);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY + "/"
								+ children[j] + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String[] classNames = session
							.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
									+ "/" + PROPERTY + "/" + children[j] + "/"
									+ VALUES);
					for (int k = 0; k < classNames.length; k++) {
						String className = session.getNodeValue(
								PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY
										+ "/" + children[j] + "/" + VALUES
										+ "/" + classNames[k]).getString();
						if (className.equals(TEST_INTERFACE_NAME1)) {
							checkFlag = true;
							break;
						}
					}
				}
			}
		}
		assertTrue(checkFlag);
	}
	
	/**
	 * 
	 * [testBundle3]
	 * This bundle registers OSGi service whose objectName are
	 * "org.osgi.test.cases.residentialmanagement.util.Service1" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service2" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service3".
	 * The OSGi service has properties which are ("testKey1"="testValue1") 
	 * and ("testKey2"="testValue3").
	 * 
	 * Test of node deletion.
	 * precondition  : 
	 * "testBundle3" is installed. Therefore, "Service1" is registered and this MO has
	 * the subtree about the registered "Service1".
	 * postcondition : 
	 * "testBundle3" is uninstalled. After that, "Service1" must be unregistered from
	 * framework, then this MO must be delete the subtree about the unregistered "Service1".
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testServiceStateNodeDelete() throws DmtException,
			BundleException, IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);

		for (int i = 0; i < ids.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
					+ "/" + PROPERTY);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY + "/"
								+ children[j] + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String[] classNames = session
							.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
									+ "/" + PROPERTY + "/" + children[j] + "/"
									+ VALUES);
					for (int k = 0; k < classNames.length; k++) {
						String className = session.getNodeValue(
								PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY
										+ "/" + children[j] + "/" + VALUES
										+ "/" + classNames[k]).getString();
						if (className.equals(TEST_INTERFACE_NAME1)) {
							checkFlag = true;
							break;
						}
					}
				}
			}
		}
		assertTrue(checkFlag);
		testBundle3.uninstall();
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
					+ "/" + PROPERTY);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY + "/"
								+ children[j] + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String[] classNames = session
							.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
									+ "/" + PROPERTY + "/" + children[j] + "/"
									+ VALUES);
					for (int k = 0; k < classNames.length; k++) {
						String className = session.getNodeValue(
								PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY
										+ "/" + children[j] + "/" + VALUES
										+ "/" + classNames[k]).getString();
						if (className.equals(TEST_INTERFACE_NAME1))
							fail("Test service1 must not exist.");
					}
				}
			}
		}
	}
	
	/**
	 * Test of access to node.
	 * 
	 * @throws DmtException
	 */
	public void testServiceStateNodeAccess() throws DmtException {
		// ReadOnlyNode
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		DmtData data = null;
		try {
			// <interface_name>
			for (int i = 0; i < ids.length; i++) {
				children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
						+ ids[i] + "/" + PROPERTY);
				for (int j = 0; j < children.length; j++) {
					String key = session.getNodeValue(
							PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY
									+ "/" + children[j] + "/" + KEY)
							.getString();
					if (key.equals(Constants.OBJECTCLASS)) {
						String[] classNames = session
								.getChildNodeNames(PLUGIN_ROOT_URI + "/"
										+ ids[i] + "/" + PROPERTY + "/"
										+ children[j] + "/" + VALUES);
						for (int k = 0; k < classNames.length; k++) {
							data = session.getNodeValue(PLUGIN_ROOT_URI + "/"
									+ ids[i] + "/" + PROPERTY + "/"
									+ children[j] + "/" + VALUES + "/"
									+ classNames[k]);
							data.getString();
						}
					}
				}
			}

			// RegisteringBundle/<bundle_id>
			for (int i = 0; i < ids.length; i++) {
				data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + ids[i]
						+ "/" + REGISTERINGBUNDLE);
				data.getLong();
			}

			// UsingBundles/<bundle_id>
			for (int i = 0; i < ids.length; i++) {
				String[] bundleId = session.getChildNodeNames(PLUGIN_ROOT_URI
						+ "/" + ids[i] + "/" + USINGBUNDLES);
				for (int k = 0; k < bundleId.length; k++) {
					data = session.getNodeValue(PLUGIN_ROOT_URI + "/" + ids[i]
							+ "/" + USINGBUNDLES + "/" + bundleId[k]);
					data.getLong();
				}

			}

			// Properties
			for (int s = 0; s < ids.length; s++) {
				String[] propertyIds = session
						.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[s] + "/"
								+ PROPERTY);
				assertNotNull("This object should not be null.", propertyIds);
				for (int i = 0; i < propertyIds.length; i++) {
					DmtData key = session.getNodeValue(PLUGIN_ROOT_URI + "/"
							+ ids[s] + "/" + PROPERTY + "/" + propertyIds[i]
							+ "/" + KEY);
					key.getString();
					DmtData type = session.getNodeValue(PLUGIN_ROOT_URI + "/"
							+ ids[s] + "/" + PROPERTY + "/" + propertyIds[i]
							+ "/" + TYPE);
					type.getString();
					DmtData cardinality = session.getNodeValue(PLUGIN_ROOT_URI
							+ "/" + ids[s] + "/" + PROPERTY + "/"
							+ propertyIds[i] + "/" + CARDINALITY);
					cardinality.getString();
					String[] values = session.getChildNodeNames(PLUGIN_ROOT_URI
							+ "/" + ids[s] + "/" + PROPERTY + "/"
							+ propertyIds[i] + "/" + VALUES);
					for (int k = 0; k < values.length; k++) {
						// DmtData value =
						session.getNodeValue(PLUGIN_ROOT_URI + "/" + ids[s]
								+ "/" + PROPERTY + "/" + propertyIds[i] + "/"
								+ VALUES + "/" + values[k]);
						// value.getString();
					}
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
			String[] propertyIds = session.getChildNodeNames(PLUGIN_ROOT_URI
					+ "/" + ids[0] + "/" + PROPERTY);
			assertNotNull("This object should not be null.", propertyIds);
			for (int i = 0; i < propertyIds.length; i++) {
				session.setNodeValue(PLUGIN_ROOT_URI + "/" + ids[0] + "/"
						+ PROPERTY + "/" + propertyIds[i] + "/" + KEY,
						new DmtData("test"));
				session.setNodeValue(PLUGIN_ROOT_URI + "/" + ids[0] + "/"
						+ PROPERTY + "/" + propertyIds[i] + "/" + TYPE,
						new DmtData("test"));
				session.setNodeValue(PLUGIN_ROOT_URI + "/" + ids[0] + "/"
						+ PROPERTY + "/" + propertyIds[i] + "/" + CARDINALITY,
						new DmtData("test"));
				String[] values = session.getChildNodeNames(PLUGIN_ROOT_URI
						+ "/" + ids[0] + "/" + PROPERTY + "/" + propertyIds[i]
						+ "/" + VALUES);
				for (int k = 0; k < values.length; k++) {
					session.setNodeValue(PLUGIN_ROOT_URI + "/" + ids[0] + "/"
							+ PROPERTY + "/" + propertyIds[i] + "/" + VALUES
							+ "/" + values[k], new DmtData("test"));
				}
			}
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		// RegisteringBundle/<bundle_id>
		try {
			session.setNodeValue(PLUGIN_ROOT_URI + "/" + ids[0] + "/"
					+ REGISTERINGBUNDLE, new DmtData("test"));
		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
		// UsingBundles/<bundle_id>
		try {
			String[] bundleId = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
					+ ids[0] + "/" + USINGBUNDLES);
			for (int k = 0; k < bundleId.length; k++) {
				session.setNodeValue(PLUGIN_ROOT_URI + "/" + ids[0] + "/"
						+ USINGBUNDLES + "/" + bundleId[k], new DmtData("test"));
			}

		} catch (DmtException e) {
			checkFlag = true;
		}
		assertTrue("This leaf node must be read-only:", checkFlag);
		checkFlag = false;
	}

	/**
	 * 
	 * [testBundle3]
	 * This bundle registers OSGi service whose objectName are
	 * "org.osgi.test.cases.residentialmanagement.util.Service1" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service2" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service3".
	 * The OSGi service has properties which are ("testKey1"="testValue1") 
	 * and ("testKey2"="testValue3").
	 * 
	 * Test deletion and creation of ./ServiceState/<id> node.
	 * precondition  : 
	 * "testBundle3" is installed. Therefore, "Service1" is registered and this MO has
	 * the <id> node about the registered "Service1".
	 * postcondition : 
	 * "testBundle3" is uninstalled and installed again. 
	 * "Service1" must be registered with new <id>.
	 *  
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testCheckServiceId() throws DmtException, BundleException,
			IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		String beforeServiceId = null;
		String afterServiceId = null;
		String currentServiceId = getServiceId(TEST_INTERFACE_NAME1);
		for (int i = 0; i < ids.length; i++) {
			if (ids[i].equals(currentServiceId)) {
				beforeServiceId = ids[i];
				break;
			}
		}
		testBundle3.uninstall();
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		currentServiceId = getServiceId(TEST_INTERFACE_NAME1);
		for (int i = 0; i < ids.length; i++) {
			if (ids[i].equals(currentServiceId)) {
				afterServiceId = ids[i];
				break;
			}
		}
		assertTrue(beforeServiceId != null && afterServiceId != null
				&& !afterServiceId.equals(beforeServiceId));
	}

	/**
	 * 
	 * [testBundle3]
	 * This bundle registers OSGi service whose objectName are
	 * "org.osgi.test.cases.residentialmanagement.util.Service1" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service2" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service3".
	 * The OSGi service has properties which are ("testKey1"="testValue1") 
	 * and ("testKey2"="testValue3").
	 * 
	 * Interfaces node check 
	 * "testBundle3" is installed. Therefore, "Service1", "Service2" and "Service3" 
	 * are registered. To check whether these subtree are created.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testCheckInterfaces() throws DmtException, BundleException,
			IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		String currentServiceId = getServiceId(TEST_INTERFACE_NAME1);
		String[] classNames = null;
		Hashtable currentClassNmae = new Hashtable();
		currentClassNmae.put(TEST_INTERFACE_NAME1, "");
		currentClassNmae.put(TEST_INTERFACE_NAME2, "");
		currentClassNmae.put(TEST_INTERFACE_NAME3, "");
		String[] propertyId = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ currentServiceId + "/" + PROPERTY);
		for (int i = 0; i < propertyId.length; i++) {
			String key = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + currentServiceId + "/" + PROPERTY
							+ "/" + propertyId[i] + "/" + KEY).getString();
			if (key.equals(Constants.OBJECTCLASS)) {
				classNames = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
						+ currentServiceId + "/" + PROPERTY + "/"
						+ propertyId[i] + "/" + VALUES);
				for (int k = 0; k < classNames.length; k++) {
					String className = session.getNodeValue(
							PLUGIN_ROOT_URI + "/" + currentServiceId + "/"
									+ PROPERTY + "/" + propertyId[i] + "/"
									+ VALUES + "/" + classNames[k]).getString();
					currentClassNmae.remove(className);
				}

			}
		}
		assertTrue("The interfaces number of node is incorrect.",
				(0 == currentClassNmae.size()) && (3 == classNames.length));
	}
	
	/**
	 * 
	 * [testBundle3]
	 * This bundle registers OSGi service whose objectName are
	 * "org.osgi.test.cases.residentialmanagement.util.Service1" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service2" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service3".
	 * The OSGi service has properties which are ("testKey1"="testValue1") 
	 * and ("testKey2"="testValue3").
	 * 
	 * RegisteringBundle node check
	 * "testBundle3" is installed. 
	 * To check whether bundleId of testBundle3 is included in registeringBundle node.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testCheckRegisteringBundle() throws DmtException,
			BundleException, IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		String currentServiceId = getServiceId(TEST_INTERFACE_NAME1);
		String currentRegisteringBundleId = getRegisteringBundleId(TEST_INTERFACE_NAME1);
		long registeringBundleId = session.getNodeValue(
				PLUGIN_ROOT_URI + "/" + currentServiceId + "/"
						+ REGISTERINGBUNDLE).getLong();
		// assertNotNull("This object should not be null.",
		// registeringBundleId);
		if (currentRegisteringBundleId.equals(String
				.valueOf(registeringBundleId))) {
			checkFlag = true;
		}
		assertTrue("This bundle id is incorrect", checkFlag);
	}

	/**
	 * 
	 * [testBundle3]
	 * This bundle registers OSGi service whose objectName are
	 * "org.osgi.test.cases.residentialmanagement.util.Service1" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service2" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service3".
	 * The OSGi service has properties which are ("testKey1"="testValue1") 
	 * and ("testKey2"="testValue3").
	 * [testBundle4]
	 * This bundle gets OSGi service "Service1".
	 * [testBundle5]
	 * This bundle gets OSGi service "Service2".
	 * 
	 * UsingBundles node check
	 * To check whether bundleId of testBundle4 and testBundle5 
	 * are included in children of usingBundles node.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testCheckUsingBundle() throws DmtException, BundleException,
			IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		testBundle4 = installAndStartBundle(TESTBUNDLELOCATION4);
		testBundle5 = installAndStartBundle(TESTBUNDLELOCATION5);
		String currentServiceId = getServiceId(TEST_INTERFACE_NAME1);
		Hashtable currentUsingBundleIds = getUsingBundleIds(TEST_INTERFACE_NAME1);
		String[] usingBundleIds = session.getChildNodeNames(PLUGIN_ROOT_URI
				+ "/" + currentServiceId + "/" + USINGBUNDLES);
		assertFalse("These nodes must exist in this condition.",
				usingBundleIds.equals(new String[0]));
		for (int i = 0; i < usingBundleIds.length; i++) {
			long usingBundleId = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + currentServiceId + "/"
							+ USINGBUNDLES + "/" + usingBundleIds[i]).getLong();
			currentUsingBundleIds.remove(String.valueOf(usingBundleId));
		}

		assertEquals("The usingBundleId number of node is incorrect.", 0,
				currentUsingBundleIds.size());
	}


	/**
	 * 
	 * This bundle registers OSGi service whose objectName are
	 * "org.osgi.test.cases.residentialmanagement.util.Service1" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service2" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service3".
	 * The OSGi service has properties which are ("testKey1"="testValue1") 
	 * and ("testKey2"="testValue3").
	 * 
	 * To check of value in Property subtree.
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testCheckPropertiesNode() throws DmtException, BundleException,
			IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		String testId = getServiceId(TEST_INTERFACE_NAME1);
		// objectClass
		String[] propertyIds = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ testId + "/" + PROPERTY);
		for (int i = 0; i < propertyIds.length; i++) {
			String keyValue = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + testId + "/" + PROPERTY + "/"
							+ propertyIds[i] + "/" + KEY).getString();
			if (keyValue.equals(Constants.OBJECTCLASS)) {
				DmtData objectClassDataOfType = session
						.getNodeValue(PLUGIN_ROOT_URI + "/" + testId + "/"
								+ PROPERTY + "/" + propertyIds[i] + "/" + TYPE);
				String type = objectClassDataOfType.getString();
				assertTrue(type.equals(String.class.getName()));
				DmtData objectClassDataOfCardinality = session
						.getNodeValue(PLUGIN_ROOT_URI + "/" + testId + "/"
								+ PROPERTY + "/" + propertyIds[i] + "/"
								+ CARDINALITY);
				String cardinality = objectClassDataOfCardinality.getString();
				assertTrue(cardinality.equals(ARRAY));
				String[] values = session.getChildNodeNames(PLUGIN_ROOT_URI
						+ "/" + testId + "/" + PROPERTY + "/" + propertyIds[i]
						+ "/" + VALUES);
				assertFalse(values.equals(""));
				for (int j = 0; j < values.length; j++) {
					DmtData valueOfObjectClass = session
							.getNodeValue(PLUGIN_ROOT_URI + "/" + testId + "/"
									+ PROPERTY + "/" + propertyIds[i] + "/"
									+ VALUES + "/" + values[j]);
					String value = valueOfObjectClass.getString();
					if (value.equals(TEST_INTERFACE_NAME1)) {
						checkFlag = true;
						break;
					}
				}
			}
		}
		assertTrue("This value is incorrect", checkFlag);
		checkFlag = false;

		// service.id
		for (int i = 0; i < propertyIds.length; i++) {
			String keyValue = session.getNodeValue(
					PLUGIN_ROOT_URI + "/" + testId + "/" + PROPERTY + "/"
							+ propertyIds[i] + "/" + KEY).getString();
			if (keyValue.equals(Constants.SERVICE_ID)) {
				DmtData serviceIdDataOfType = session
						.getNodeValue(PLUGIN_ROOT_URI + "/" + testId + "/"
								+ PROPERTY + "/" + propertyIds[i] + "/" + TYPE);
				String type = serviceIdDataOfType.getString();
				assertTrue(type.equals(Long.TYPE.getName()));
				DmtData objectClassDataOfCardinality = session
						.getNodeValue(PLUGIN_ROOT_URI + "/" + testId + "/"
								+ PROPERTY + "/" + propertyIds[i] + "/"
								+ CARDINALITY);
				String cardinality = objectClassDataOfCardinality.getString();
				assertTrue(cardinality.equals(SCALAR));

				String[] values = session.getChildNodeNames(PLUGIN_ROOT_URI
						+ "/" + testId + "/" + PROPERTY + "/" + propertyIds[i]
						+ "/" + VALUES);
				assertFalse(values.equals(""));
				for (int j = 0; j < values.length; j++) {
					DmtData valueOfServiceIdData = session
							.getNodeValue(PLUGIN_ROOT_URI + "/" + testId + "/"
									+ PROPERTY + "/" + propertyIds[i] + "/"
									+ VALUES + "/" + values[j]);
					long value = valueOfServiceIdData.getLong();
					if (testId.equals(String.valueOf(value))) {
						checkFlag = true;
						break;
					}
				}
			}
		}
		assertTrue("This value is incorrect", checkFlag);
	}

	/**
	 * 
	 * This bundle registers OSGi service whose objectName are
	 * "org.osgi.test.cases.residentialmanagement.util.Service1" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service2" and
	 * "org.osgi.test.cases.residentialmanagement.util.Service3".
	 * The OSGi service has properties which are ("testKey1"="testValue1") 
	 * and ("testKey2"="testValue3").
	 * 
	 * To check of modified value in Property subtree .
	 * 
	 * @throws DmtException
	 * @throws BundleException
	 * @throws IOException
	 */
	public void testServicePropertiesModify() throws DmtException,
			BundleException, IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		String serviceId = this.getServiceId(TEST_INTERFACE_NAME3);
		String[] propertyId = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ serviceId + "/" + PROPERTY);
		int numberOfProperty = propertyId.length;
		ServiceReference serviceRef = context
				.getServiceReference(Service3.class.getName());
		Service3 service = (Service3) context.getService(serviceRef);
		service.modifytServiceProperty();
		propertyId = session.getChildNodeNames(PLUGIN_ROOT_URI + "/"
				+ serviceId + "/" + PROPERTY);
		int numberOfPropertyAfterModified = propertyId.length;
		assertTrue("The property must be changed.",
				numberOfProperty != numberOfPropertyAfterModified);
	}

	public void testServiceStateNodeUpdate() throws DmtException,
			BundleException, IOException {
		testBundle3 = installAndStartBundle(TESTBUNDLELOCATION3);
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		String serviceId = null;
		for (int i = 0; i < ids.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
					+ "/" + PROPERTY);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY + "/"
								+ children[j] + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String[] classNames = session
							.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
									+ "/" + PROPERTY + "/" + children[j] + "/"
									+ VALUES);
					for (int k = 0; k < classNames.length; k++) {
						String className = session.getNodeValue(
								PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY
										+ "/" + children[j] + "/" + VALUES
										+ "/" + classNames[k]).getString();
						if (className.equals(TEST_INTERFACE_NAME1)) {
							checkFlag = true;
							serviceId = ids[i];
							break;
						}
					}
				}
			}
		}
		assertTrue(checkFlag);
		checkFlag = false;
		this.updateBundle(TESTBUNDLELOCATION3, testBundle3);
		ids = session.getChildNodeNames(PLUGIN_ROOT_URI);
		assertNotNull("This object should not be null.", ids);
		for (int i = 0; i < ids.length; i++) {
			children = session.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
					+ "/" + PROPERTY);
			for (int j = 0; j < children.length; j++) {
				String key = session.getNodeValue(
						PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY + "/"
								+ children[j] + "/" + KEY).getString();
				if (key.equals(Constants.OBJECTCLASS)) {
					String[] classNames = session
							.getChildNodeNames(PLUGIN_ROOT_URI + "/" + ids[i]
									+ "/" + PROPERTY + "/" + children[j] + "/"
									+ VALUES);
					for (int k = 0; k < classNames.length; k++) {
						String className = session.getNodeValue(
								PLUGIN_ROOT_URI + "/" + ids[i] + "/" + PROPERTY
										+ "/" + children[j] + "/" + VALUES
										+ "/" + classNames[k]).getString();
						if (className.equals(TEST_INTERFACE_NAME1)
								&& !serviceId.equals(ids[i])) {
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
}
