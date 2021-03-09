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

package org.osgi.test.cases.dmt.tc4.ext.junit;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.ArrayAssert;
import org.osgi.test.cases.dmt.tc4.ext.util.TestDataPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.TestNode;

public class DataMountPluginTest extends DmtAdminTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getDmtAdmin();
	}

	@Override
	protected void tearDown() throws Exception {
		unregisterPlugins();
		closeDmtSession();
		super.tearDown();
	}

	public void testPluginIsNotMappedIfDataRootUrisContainsInvalidTypeObject() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		setInteriorNode(mountingPlugin, "./A1");

		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, Integer.valueOf(0));

		ServiceRegistration<DataPlugin> registration = null;
		try {
			registration = context.registerService(DataPlugin.class,
					mountingPlugin, serviceProps);
			session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
			assertNodeNotFound("./A1");
			session.close();
		} finally {
			registration.unregister();
		}
	}

	public void testPluginIsMappedWhen1DataRootURIAnd0MountPointsAreSpecified() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		setInteriorNode(mountingPlugin, "./A1");

		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, new String[] {"./A1"});
		serviceProps.put(DataPlugin.MOUNT_POINTS, new String[] {});

		ServiceRegistration<DataPlugin> registration = null;
		try {
			registration = context.registerService(DataPlugin.class,
					mountingPlugin, serviceProps);
			session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
			assertInteriorNode("./A1");
			session.close();
		} finally {
			registration.unregister();
		}
	}

	/*
	 * Multiple dataRootURIs and zero length mountPoints should succeeds to be
	 * registered.
	 */
	public void testPluginIsIgnoredWhen2DataRootURIsAnd0MountPointsAreSpecified() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		setInteriorNode(mountingPlugin, "./A1");
		setInteriorNode(mountingPlugin, "./A2");

		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, new String[] {"./A1", "./A2"});
		serviceProps.put(DataPlugin.MOUNT_POINTS, new String[] {});

		ServiceRegistration<DataPlugin> registration = null;
		try {
			registration = context.registerService(DataPlugin.class,
					mountingPlugin, serviceProps);
			session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
			assertInteriorNode("./A1");
			assertInteriorNode("./A2");
			session.close();
		} finally {
			registration.unregister();
		}
	}

	/*
	 * Multiple dataRootURIs and more than one mountPoints should not succeeds
	 * to be registered.
	 */
	public void testPluginIsIgnoredWhen2DataRootURIsAnd1MountPointsAreSpecified() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		setInteriorNode(mountingPlugin, "./A1");
		setInteriorNode(mountingPlugin, "./A2");

		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, new String[] {"./A1", "./A2"});
		serviceProps.put(DataPlugin.MOUNT_POINTS, new String[] {"B1"});

		ServiceRegistration<DataPlugin> registration = null;
		try {
			registration = context.registerService(DataPlugin.class,
					mountingPlugin, serviceProps);
			session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
			assertNodeNotFound("./A1");
			assertNodeNotFound("./A2");
			assertNodeNotFound("./A1/B1");
			assertNodeNotFound("./A2/B1");
			session.close();
		} finally {
			registration.unregister();
		}
	}

	/*
	 * If mountPoints contains an invalid URI value such as null, the plugin
	 * should be ignored.
	 */
	public void testPluginIsIgnoredWhenMountPointsContainsNull() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		setInteriorNode(mountingPlugin, "./A1");
		setLeafNode(mountingPlugin, "./A1/B1");

		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, new String[] {"./A1"});
		serviceProps.put(DataPlugin.MOUNT_POINTS, new String[] {"B1", null});

		ServiceRegistration<DataPlugin> registration = null;
		try {
			registration = context.registerService(DataPlugin.class,
					mountingPlugin, serviceProps);
			session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
			assertNodeNotFound("./A1");
			assertNodeNotFound("./A1/B1");
			session.close();
		} finally {
			registration.unregister();
		}
	}

	/*
	 * If mountPoints contains an invalid URI, the plugin should be ignored.
	 */
	public void testPluginIsIgnoredWhenMountPointsContainsInvalidURI() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		setInteriorNode(mountingPlugin, "./A1");
		setLeafNode(mountingPlugin, "./A1/B1");

		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, new String[] {"./A1"});
		serviceProps.put(DataPlugin.MOUNT_POINTS, new String[] {"B1", "/"});

		ServiceRegistration<DataPlugin> registration = null;
		try {
			registration = context.registerService(DataPlugin.class,
					mountingPlugin, serviceProps);
			session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
			assertNodeNotFound("./A1");
			assertNodeNotFound("./A1/B1");
			session.close();
		} finally {
			registration.unregister();
		}
	}

	/*
	 * If mountPoints contains an invalid URI such as absolute URI, the plugin
	 * should be ignored.
	 */
	public void testPluginIsIgnoredWhenMountPointsContainsAbsoluteURI() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		setInteriorNode(mountingPlugin, "./A1");
		setLeafNode(mountingPlugin, "./A1/B1");

		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, new String[] {"./A1"});
		serviceProps.put(DataPlugin.MOUNT_POINTS, new String[] {"B1", "./B2"});

		ServiceRegistration<DataPlugin> registration = null;
		try {
			registration = context.registerService(DataPlugin.class,
					mountingPlugin, serviceProps);
			session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
			assertNodeNotFound("./A1");
			assertNodeNotFound("./A1/B1");
			session.close();
		} finally {
			registration.unregister();
		}
	}

	/*
	 * If mountPoints contains overlapping URIs, the plugin should be ignored.
	 */
	public void testPluginIsIgnoredWhenMountPointsContainsOverlappedRelativeURIs() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		setInteriorNode(mountingPlugin, "./A1");
		setInteriorNode(mountingPlugin, "./A1/B1");
		setLeafNode(mountingPlugin, "./A1/B1/C1");

		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, new String[] {"./A1"});
		serviceProps.put(DataPlugin.MOUNT_POINTS, new String[] {"B1", "B2", "B1/C1"});

		ServiceRegistration<DataPlugin> registration = null;
		try {
			registration = context.registerService(DataPlugin.class,
					mountingPlugin, serviceProps);
			session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
			assertNodeNotFound("./A1");
			assertNodeNotFound("./A1/B1");
			assertNodeNotFound("./A1/B2");
			assertNodeNotFound("./A1/B1/C1");
			session.close();
		} finally {
			registration.unregister();
		}
	}

	public void testTheNodeUnderMountPointNodeIsNotFound() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertNodeNotFound("./A1/B1");
		session.close();

		unregister(mountingPluginRegistration);
	}

	public void testTheNodeUnderMountedPluginsMountPointNodeIsNotFound() throws DmtException {
		TestDataPlugin mountingPluginA = new TestDataPlugin();
		mountingPluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistrationA = registerMountingDataPlugin(
				mountingPluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin mountedPluginB = new TestDataPlugin();
		mountedPluginB.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> mountedPluginRegistrationB = registerDataPlugin(
				mountedPluginB, "./A1/B1");

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertNodeNotFound("./A1/B1/C1");
		session.close();

		unregister(mountingPluginRegistrationA);
		unregister(mountedPluginRegistrationB);
	}

	public void testGetChildNodeNamesToParentOfMountPointWithNoMountedPlugin0() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals(0, session.getChildNodeNames("./A1").length);
		session.close();

		unregister(mountingPluginRegistration);
	}

	public void testGetChildNodeNamesToParentOfMountPointWithNoMountedPlugin1() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		mountingPlugin.setNode("./A1/B0", TestNode.newInteriorNode("./A1/B0:Title", "./A1/B0:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		String[] childNodeNames = session.getChildNodeNames("./A1");
		assertEquals(1, childNodeNames.length);
		ArrayAssert.assertEquivalenceArrays(new String[] {"B0",}, childNodeNames);
		session.close();

		unregister(mountingPluginRegistration);
	}

	public void testGetChildNodeNamesToParentOfMountPointWithMountedPlugin0() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		String[] childNodeNames = session.getChildNodeNames("./A1");
		assertEquals(1, childNodeNames.length);
		ArrayAssert.assertEquivalenceArrays(new String[] {"B1"}, childNodeNames);
		session.close();

		unregister(mountingPluginRegistration);
		unregister(mountedPluginRegistration);
	}

	public void testGetChildNodeNamesToParentOfMountPointWithMountedPlugin1() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		mountingPlugin.setNode("./A1/B0", TestNode.newInteriorNode("./A1/B0:Title", "./A1/B0:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		String[] childNodeNames = session.getChildNodeNames("./A1");
		assertEquals(2, childNodeNames.length);
		ArrayAssert.assertEquivalenceArrays(new String[] {"B0", "B1"}, childNodeNames);
		session.close();

		unregister(mountingPluginRegistration);
		unregister(mountedPluginRegistration);
	}

	public void testGetChildNodeNamesToParentOfMountPointWithMountedPlugin2() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		mountingPlugin.setNode("./A1/B0", TestNode.newInteriorNode("./A1/B0:Title", "./A1/B0:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1", "B2"
				});

		TestDataPlugin mountedPlugin1 = new TestDataPlugin();
		mountedPlugin1.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration1 = registerDataPlugin(
				mountedPlugin1, "./A1/B1");

		TestDataPlugin mountedPlugin2 = new TestDataPlugin();
		mountedPlugin2.setNode("./A1/B2", TestNode.newLeafNode("./A1/B2:Title", "./A1/B2:Type", new DmtData("./A1/B2:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration2 = registerDataPlugin(
				mountedPlugin2, "./A1/B2");

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		String[] childNodeNames = session.getChildNodeNames("./A1");
		assertEquals(3, childNodeNames.length);
		ArrayAssert.assertEquivalenceArrays(new String[] {"B0", "B1", "B2"}, childNodeNames);
		session.close();

		unregister(mountingPluginRegistration);
		unregister(mountedPluginRegistration1);
		unregister(mountedPluginRegistration2);
	}

	public void test2PluginsRegisterUnregisterOrderPattern01() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals(new DmtData("./A1/B1:Value"), session.getNodeValue("./A1/B1"));
		session.close();

		unregister(mountingPluginRegistration);
		unregister(mountedPluginRegistration);
	}

	public void test2PluginsRegisterUnregisterOrderPattern02() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals(new DmtData("./A1/B1:Value"), session.getNodeValue("./A1/B1"));
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		session.close();

		unregister(mountingPluginRegistration);
		unregister(mountedPluginRegistration);
	}

	public void test2PluginsRegisterUnregisterOrderPattern03() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals(new DmtData("./A1/B1:Value"), session.getNodeValue("./A1/B1"));
		session.close();

		unregister(mountedPluginRegistration);
		unregister(mountingPluginRegistration);
	}

	public void test2PluginsRegisterUnregisterOrderPattern04() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals(new DmtData("./A1/B1:Value"), session.getNodeValue("./A1/B1"));
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		session.close();

		unregister(mountedPluginRegistration);
		unregister(mountingPluginRegistration);
	}

	public void test2PluginsRegisterUnregisterOrderPattern05() throws DmtException {
		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals(new DmtData("./A1/B1:Value"), session.getNodeValue("./A1/B1"));
		session.close();

		unregister(mountingPluginRegistration);
		unregister(mountedPluginRegistration);
	}

	public void test2PluginsRegisterUnregisterOrderPattern06() throws DmtException {
		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals(new DmtData("./A1/B1:Value"), session.getNodeValue("./A1/B1"));
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		session.close();

		unregister(mountingPluginRegistration);
		unregister(mountedPluginRegistration);
	}

	public void test2PluginsRegisterUnregisterOrderPattern07() throws DmtException {
		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals(new DmtData("./A1/B1:Value"), session.getNodeValue("./A1/B1"));
		session.close();

		unregister(mountingPluginRegistration);
		unregister(mountedPluginRegistration);
	}

	public void test2PluginsRegisterUnregisterOrderPattern08() throws DmtException {
		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals(new DmtData("./A1/B1:Value"), session.getNodeValue("./A1/B1"));
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		session.close();

		unregister(mountingPluginRegistration);
		unregister(mountedPluginRegistration);
	}

	public void test2PluginsRegisterUnregisterOrderPattern09() throws DmtException {
		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);

		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		try {
			assertEquals("./A1:Title", session.getNodeTitle("./A1"));
			fail();
		} catch (DmtIllegalStateException exception) {
		} finally {
			unregister(mountingPluginRegistration);
		}
	}

	public void test2PluginsRegisterUnregisterOrderPattern10() throws DmtException {
		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);

		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));

		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		try {
			assertEquals("./A1:Title", session.getNodeTitle("./A1"));
			fail();
		} catch (DmtIllegalStateException exception) {
		} finally {
			unregister(mountingPluginRegistration);
			unregister(mountedPluginRegistration);
		}
	}

	public void test2PluginsRegisterUnregisterOrderPattern11() throws DmtException {
		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);

		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		try {
			assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
			fail();
		} catch (DmtIllegalStateException exception) {
		} finally {
			unregister(mountedPluginRegistration);
		}
	}

	public void test2PluginsRegisterUnregisterOrderPattern12() throws DmtException {
		TestDataPlugin mountedPlugin = new TestDataPlugin();
		mountedPlugin.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> mountedPluginRegistration = registerDataPlugin(
				mountedPlugin, "./A1/B1");

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);

		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals(new DmtData("./A1/B1:Value"), session.getNodeValue("./A1/B1"));

		TestDataPlugin mountingPlugin = new TestDataPlugin();
		mountingPlugin.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> mountingPluginRegistration = registerMountingDataPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});

		try {
			assertEquals("./A1:Title", session.getNodeTitle("./A1"));
			fail();
		} catch (DmtIllegalStateException exception) {
		} finally {
			unregister(mountingPluginRegistration);
			unregister(mountedPluginRegistration);
		}
	}

	public void test3PluginsRegisterUnregisterOrderPattern01() throws DmtException {
		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin pluginB = new TestDataPlugin();
		pluginB.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB = registerMountingDataPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});

		TestDataPlugin pluginC = new TestDataPlugin();
		pluginC.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC = registerDataPlugin(
				pluginC, "./A1/B1/C1");

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);

		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals("./A1/B1/C1:Title", session.getNodeTitle("./A1/B1/C1"));
		assertEquals("./A1/B1/C1:Type", session.getNodeType("./A1/B1/C1"));
		assertEquals(new DmtData("./A1/B1/C1:Value"), session.getNodeValue("./A1/B1/C1"));

		session.close();

		unregister(registrationA);
		unregister(registrationB);
		unregister(registrationC);
	}

	public void test3PluginsRegisterUnregisterOrderPattern02() throws DmtException {
		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin pluginC = new TestDataPlugin();
		pluginC.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC = registerDataPlugin(
				pluginC, "./A1/B1/C1");

		TestDataPlugin pluginB = new TestDataPlugin();
		pluginB.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB = registerMountingDataPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);

		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals("./A1/B1/C1:Title", session.getNodeTitle("./A1/B1/C1"));
		assertEquals("./A1/B1/C1:Type", session.getNodeType("./A1/B1/C1"));

		session.close();

		unregister(registrationA);
		unregister(registrationB);
		unregister(registrationC);
	}

	public void test3PluginsRegisterUnregisterOrderPattern03() throws DmtException {
		TestDataPlugin pluginB = new TestDataPlugin();
		pluginB.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB = registerMountingDataPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});

		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin pluginC = new TestDataPlugin();
		pluginC.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC = registerDataPlugin(
				pluginC, "./A1/B1/C1");

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);

		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals("./A1/B1/C1:Title", session.getNodeTitle("./A1/B1/C1"));
		assertEquals("./A1/B1/C1:Type", session.getNodeType("./A1/B1/C1"));
		assertEquals(new DmtData("./A1/B1/C1:Value"), session.getNodeValue("./A1/B1/C1"));

		session.close();

		unregister(registrationA);
		unregister(registrationB);
		unregister(registrationC);
	}

	public void test3PluginsRegisterUnregisterOrderPattern04() throws DmtException {
		TestDataPlugin pluginB = new TestDataPlugin();
		pluginB.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB = registerMountingDataPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});

		TestDataPlugin pluginC = new TestDataPlugin();
		pluginC.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC = registerDataPlugin(
				pluginC, "./A1/B1/C1");

		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);

		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals("./A1/B1/C1:Title", session.getNodeTitle("./A1/B1/C1"));
		assertEquals("./A1/B1/C1:Type", session.getNodeType("./A1/B1/C1"));
		assertEquals(new DmtData("./A1/B1/C1:Value"), session.getNodeValue("./A1/B1/C1"));

		session.close();

		unregister(registrationA);
		unregister(registrationB);
		unregister(registrationC);
	}

	public void test3PluginsRegisterUnregisterOrderPattern05() throws DmtException {
		TestDataPlugin pluginC = new TestDataPlugin();
		pluginC.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC = registerDataPlugin(
				pluginC, "./A1/B1/C1");

		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin pluginB = new TestDataPlugin();
		pluginB.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB = registerMountingDataPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);

		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals("./A1/B1/C1:Title", session.getNodeTitle("./A1/B1/C1"));
		assertEquals("./A1/B1/C1:Type", session.getNodeType("./A1/B1/C1"));
		assertEquals(new DmtData("./A1/B1/C1:Value"), session.getNodeValue("./A1/B1/C1"));

		session.close();

		unregister(registrationA);
		unregister(registrationB);
		unregister(registrationC);
	}

	public void test3PluginsRegisterUnregisterOrderPattern06() throws DmtException {
		TestDataPlugin pluginC = new TestDataPlugin();
		pluginC.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC = registerDataPlugin(
				pluginC, "./A1/B1/C1");

		TestDataPlugin pluginB = new TestDataPlugin();
		pluginB.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB = registerMountingDataPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});

		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);

		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals("./A1/B1/C1:Title", session.getNodeTitle("./A1/B1/C1"));
		assertEquals("./A1/B1/C1:Type", session.getNodeType("./A1/B1/C1"));
		assertEquals(new DmtData("./A1/B1/C1:Value"), session.getNodeValue("./A1/B1/C1"));

		session.close();

		unregister(registrationA);
		unregister(registrationB);
		unregister(registrationC);
	}

	public void test2MountPoints1MountedPluginsRegisterUnregisterOrderPattrn01() throws DmtException {
		TestDataPlugin pluginB1 = new TestDataPlugin();
		pluginB1.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> registrationB1 = registerDataPlugin(
				pluginB1, "./A1/B1");

		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertInteriorNodeNoValue("./A1");
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals(new DmtData("./A1/B1:Value"), session.getNodeValue("./A1/B1"));
		assertNodeNotFound("./A1/B2");

		session.close();

		unregister(registrationA);
		unregister(registrationB1);
	}

	public void test2MountPoints1MountedPluginsRegisterUnregisterOrderPattrn02() throws DmtException {
		TestDataPlugin pluginB2 = new TestDataPlugin();
		pluginB2.setNode("./A1/B2", TestNode.newLeafNode("./A1/B2:Title", "./A1/B2:Type", new DmtData("./A1/B2:Value")));
		ServiceRegistration<DataPlugin> registrationB2 = registerDataPlugin(
				pluginB2, "./A1/B2");

		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertInteriorNodeNoValue("./A1");
		assertNodeNotFound("./A1/B1");
		assertEquals("./A1/B2:Title", session.getNodeTitle("./A1/B2"));
		assertEquals("./A1/B2:Type", session.getNodeType("./A1/B2"));
		assertEquals(new DmtData("./A1/B2:Value"), session.getNodeValue("./A1/B2"));

		session.close();

		unregister(registrationA);
		unregister(registrationB2);
	}

	public void test2MountPoints2MountedPlugins() throws DmtException {
		TestDataPlugin pluginB1 = new TestDataPlugin();
		pluginB1.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> registrationB1 = registerDataPlugin(
				pluginB1, "./A1/B1");

		TestDataPlugin pluginB2 = new TestDataPlugin();
		pluginB2.setNode("./A1/B2", TestNode.newLeafNode("./A1/B2:Title", "./A1/B2:Type", new DmtData("./A1/B2:Value")));
		ServiceRegistration<DataPlugin> registrationB2 = registerDataPlugin(
				pluginB2, "./A1/B2");

		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertInteriorNodeNoValue("./A1");
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals(new DmtData("./A1/B1:Value"), session.getNodeValue("./A1/B1"));
		assertEquals("./A1/B2:Title", session.getNodeTitle("./A1/B2"));
		assertEquals("./A1/B2:Type", session.getNodeType("./A1/B2"));
		assertEquals(new DmtData("./A1/B2:Value"), session.getNodeValue("./A1/B2"));

		session.close();

		unregister(registrationA);
		unregister(registrationB1);
		unregister(registrationB2);
	}

	public void testMountAlgorithmPattern01() throws DmtException {
		TestDataPlugin pluginB1 = new TestDataPlugin();
		pluginB1.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> registrationB1 = registerDataPlugin(
				pluginB1, "./A1/B1");

		TestDataPlugin pluginB2 = new TestDataPlugin();
		pluginB2.setNode("./A1/B2", TestNode.newLeafNode("./A1/B2:Title", "./A1/B2:Type", new DmtData("./A1/B2:Value")));
		ServiceRegistration<DataPlugin> registrationB2 = registerDataPlugin(
				pluginB2, "./A1/B2");

		TestDataPlugin pluginB3 = new TestDataPlugin();
		pluginB3.setNode("./A1/B3", TestNode.newLeafNode("./A1/B3:Title", "./A1/B3:Type", new DmtData("./A1/B3:Value")));
		ServiceRegistration<DataPlugin> registrationB3 = registerDataPlugin(
				pluginB3, "./A1/B3");

		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals(null, session.getNodeTitle("./A1"));
		assertEquals(DmtConstants.DDF_SCAFFOLD, session.getNodeType("./A1"));
		try {
			assertEquals(null, session.getNodeValue("./A1"));
		} catch (DmtException dmtException) {
			assertEquals(DmtException.COMMAND_NOT_ALLOWED, dmtException.getCode());
			assertEquals("./A1", dmtException.getURI());
		}
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertEquals(new DmtData("./A1/B1:Value"), session.getNodeValue("./A1/B1"));
		assertEquals("./A1/B2:Title", session.getNodeTitle("./A1/B2"));
		assertEquals("./A1/B2:Type", session.getNodeType("./A1/B2"));
		assertEquals(new DmtData("./A1/B2:Value"), session.getNodeValue("./A1/B2"));
		assertEquals("./A1/B3:Title", session.getNodeTitle("./A1/B3"));
		assertEquals("./A1/B3:Type", session.getNodeType("./A1/B3"));
		assertEquals(new DmtData("./A1/B3:Value"), session.getNodeValue("./A1/B3"));

		session.close();

		unregister(registrationA);
		unregister(registrationB1);
		unregister(registrationB2);
		unregister(registrationB3);
	}

	public void testMountAlgorithmPattern02() throws DmtException {
		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin pluginB1 = new TestDataPlugin();
		pluginB1.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB1 = registerMountingDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});

		TestDataPlugin pluginC1 = new TestDataPlugin();
		pluginB1.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC1 = registerDataPlugin(
				pluginC1, "./A1/B1/C1");

		unregister(registrationB1);

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertInteriorNodeNoValue("./A1");
		assertNodeNotFound("./A1/B1");
		assertNodeNotFound("./A1/B1/C1");
		session.close();

		unregister(registrationA);
		unregister(registrationC1);
	}

	public void testMountAlgorithmPattern03() throws DmtException {
		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin pluginB1 = new TestDataPlugin();
		pluginB1.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB1 = registerMountingDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});

		TestDataPlugin pluginC1 = new TestDataPlugin();
		pluginC1.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC1 = registerDataPlugin(
				pluginC1, "./A1/B1/C1");

		unregister(registrationB1);

		TestDataPlugin newPluginB1 = new TestDataPlugin();
		newPluginB1.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:NewTitle", "./A1/B1:NewType"));
		ServiceRegistration<DataPlugin> newRegistrationB1 = registerMountingDataPlugin(
				newPluginB1, "./A1/B1", new String[] {
						"C1"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertInteriorNodeNoValue("./A1");
		assertEquals("./A1/B1:NewTitle", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:NewType", session.getNodeType("./A1/B1"));
		assertInteriorNodeNoValue("./A1/B1");
		assertEquals("./A1/B1/C1:Title", session.getNodeTitle("./A1/B1/C1"));
		assertEquals("./A1/B1/C1:Type", session.getNodeType("./A1/B1/C1"));
		assertEquals(new DmtData("./A1/B1/C1:Value"), session.getNodeValue("./A1/B1/C1"));
		session.close();

		unregister(registrationA);
		unregister(newRegistrationB1);
		unregister(registrationC1);
	}

	public void testMountAlgorithmPattern04() throws DmtException {
		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin pluginB1 = new TestDataPlugin();
		pluginB1.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB1 = registerMountingDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});

		TestDataPlugin pluginC1 = new TestDataPlugin();
		pluginC1.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC1 = registerDataPlugin(
				pluginC1, "./A1/B1/C1");

		unregister(registrationB1);

		TestDataPlugin newPluginB1 = new TestDataPlugin();
		newPluginB1.setNode("./A1/B1", TestNode.newLeafNode("./A1/B1:Title", "./A1/B1:Type", new DmtData("./A1/B1:Value")));
		ServiceRegistration<DataPlugin> newRegistrationB1 = registerMountingDataPlugin(
				newPluginB1, "./A1/B1", new String[] {
						"C2"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertNodeNotFound("./A1/B1/C1");
		assertNodeNotFound("./A1/B1/C2");
		session.close();

		unregister(registrationA);
		unregister(newRegistrationB1);
		unregister(registrationC1);
	}

	public void testMountAlgorithmPattern05() throws DmtException {
		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin pluginB1 = new TestDataPlugin();
		pluginB1.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB1 = registerMountingDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});

		TestDataPlugin pluginC1 = new TestDataPlugin();
		pluginC1.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC1 = registerDataPlugin(
				pluginC1, "./A1/B1/C1");

		unregister(registrationB1);

		TestDataPlugin newPluginB1 = new TestDataPlugin();
		newPluginB1.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:NewTitle", "./A1/B1:NewType"));
		ServiceRegistration<DataPlugin> newRegistrationB1 = registerMountingDataPlugin(
				newPluginB1, "./A1/B1", new String[] {
						"C1", "C2"
				});

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertInteriorNodeNoValue("./A1");
		assertEquals("./A1/B1:NewTitle", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:NewType", session.getNodeType("./A1/B1"));
		assertInteriorNodeNoValue("./A1/B1");
		assertEquals("./A1/B1/C1:Title", session.getNodeTitle("./A1/B1/C1"));
		assertEquals("./A1/B1/C1:Type", session.getNodeType("./A1/B1/C1"));
		assertEquals(new DmtData("./A1/B1/C1:Value"), session.getNodeValue("./A1/B1/C1"));
		assertNodeNotFound("./A1/B1/C2");
		session.close();

		unregister(registrationA);
		unregister(newRegistrationB1);
		unregister(registrationC1);
	}

	public void testMountAlgorithmPattern06() throws DmtException {
		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin pluginB1 = new TestDataPlugin();
		pluginB1.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB1 = registerMountingDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});

		TestDataPlugin newPluginB1 = new TestDataPlugin();
		newPluginB1.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:NewTitle", "./A1/B1:NewType"));
		ServiceRegistration<DataPlugin> newRegistrationB1 = registerMountingDataPlugin(
				newPluginB1, "./A1/B1", new String[] {
						"C1", "C2"
				});

		TestDataPlugin pluginC1 = new TestDataPlugin();
		pluginC1.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC1 = registerDataPlugin(
				pluginC1, "./A1/B1/C1");

		unregister(registrationB1);

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertInteriorNodeNoValue("./A1");
		assertEquals("./A1/B1:NewTitle", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:NewType", session.getNodeType("./A1/B1"));
		assertInteriorNodeNoValue("./A1/B1");
		assertEquals("./A1/B1/C1:Title", session.getNodeTitle("./A1/B1/C1"));
		assertEquals("./A1/B1/C1:Type", session.getNodeType("./A1/B1/C1"));
		assertEquals(new DmtData("./A1/B1/C1:Value"), session.getNodeValue("./A1/B1/C1"));
		assertNodeNotFound("./A1/B1/C2");
		session.close();

		unregister(registrationA);
		unregister(newRegistrationB1);
		unregister(registrationC1);
	}

	public void testDataPluginModified01() throws DmtException {
		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin pluginB1 = new TestDataPlugin();
		pluginB1.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB1 = registerMountingDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});

		TestDataPlugin pluginC1 = new TestDataPlugin();
		pluginC1.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC1 = registerDataPlugin(
				pluginC1, "./A1/B1/C1");

		Dictionary<String,Object> modifiedServiceProps = new Hashtable<>();
		modifiedServiceProps.put(DataPlugin.DATA_ROOT_URIS, new String[] {"./A1/B1"});
		modifiedServiceProps.put(DataPlugin.MOUNT_POINTS, new String[] {"C1"});
		registrationB1.setProperties(modifiedServiceProps);

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertInteriorNodeNoValue("./A1");
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertInteriorNodeNoValue("./A1/B1");
		assertEquals("./A1/B1/C1:Title", session.getNodeTitle("./A1/B1/C1"));
		assertEquals("./A1/B1/C1:Type", session.getNodeType("./A1/B1/C1"));
		assertEquals(new DmtData("./A1/B1/C1:Value"), session.getNodeValue("./A1/B1/C1"));
		session.close();

		unregister(registrationA);
		unregister(registrationB1);
		unregister(registrationC1);
	}

	public void testDataPluginModified02() throws DmtException {
		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin pluginB1 = new TestDataPlugin();
		pluginB1.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB1 = registerMountingDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});

		TestDataPlugin pluginC1 = new TestDataPlugin();
		pluginC1.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC1 = registerDataPlugin(
				pluginC1, "./A1/B1/C1");

		Dictionary<String,Object> modifiedServiceProps = new Hashtable<>();
		modifiedServiceProps.put(DataPlugin.DATA_ROOT_URIS, new String[] {"./A1/B1"});
		modifiedServiceProps.put(DataPlugin.MOUNT_POINTS, new String[] {"C2"});
		registrationB1.setProperties(modifiedServiceProps);

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertNodeNotFound("./A1/B1/C1");
		assertNodeNotFound("./A1/B1/C2");
		session.close();

		unregister(registrationA);
		unregister(registrationB1);
		unregister(registrationC1);
	}

	public void testDataPluginModified03() throws DmtException {
		TestDataPlugin pluginA = new TestDataPlugin();
		pluginA.setNode("./A1", TestNode.newInteriorNode("./A1:Title", "./A1:Type"));
		ServiceRegistration<DataPlugin> registrationA = registerMountingDataPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

		TestDataPlugin pluginB1 = new TestDataPlugin();
		pluginB1.setNode("./A1/B1", TestNode.newInteriorNode("./A1/B1:Title", "./A1/B1:Type"));
		ServiceRegistration<DataPlugin> registrationB1 = registerMountingDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});

		TestDataPlugin pluginC1 = new TestDataPlugin();
		pluginC1.setNode("./A1/B1/C1", TestNode.newLeafNode("./A1/B1/C1:Title", "./A1/B1/C1:Type", new DmtData("./A1/B1/C1:Value")));
		ServiceRegistration<DataPlugin> registrationC1 = registerDataPlugin(
				pluginC1, "./A1/B1/C1");

		Dictionary<String,Object> modifiedServiceProps = new Hashtable<>();
		modifiedServiceProps.put(DataPlugin.DATA_ROOT_URIS, new String[] {"./A1/B1"});
		modifiedServiceProps.put(DataPlugin.MOUNT_POINTS, new String[] {"C1", "C2"});
		registrationB1.setProperties(modifiedServiceProps);

		session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
		assertEquals("./A1:Title", session.getNodeTitle("./A1"));
		assertEquals("./A1:Type", session.getNodeType("./A1"));
		assertInteriorNodeNoValue("./A1");
		assertEquals("./A1/B1:Title", session.getNodeTitle("./A1/B1"));
		assertEquals("./A1/B1:Type", session.getNodeType("./A1/B1"));
		assertInteriorNodeNoValue("./A1/B1");
		assertEquals("./A1/B1/C1:Title", session.getNodeTitle("./A1/B1/C1"));
		assertEquals("./A1/B1/C1:Type", session.getNodeType("./A1/B1/C1"));
		assertEquals(new DmtData("./A1/B1/C1:Value"), session.getNodeValue("./A1/B1/C1"));
		assertNodeNotFound("./A1/B1/C2");
		session.close();

		unregister(registrationA);
		unregister(registrationB1);
		unregister(registrationC1);
	}
}
