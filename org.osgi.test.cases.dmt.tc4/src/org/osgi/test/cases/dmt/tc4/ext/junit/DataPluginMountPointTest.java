package org.osgi.test.cases.dmt.tc4.ext.junit;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.MountPoint;
import org.osgi.test.cases.dmt.tc4.ext.util.ArrayAssert;
import org.osgi.test.cases.dmt.tc4.ext.util.MountPointEvent;
import org.osgi.test.cases.dmt.tc4.ext.util.TestDataMountPlugin;

public class DataPluginMountPointTest extends DmtAdminTestCase {

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

    public void test1MountPointCallback() throws Exception {
        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1");

        MountPointEvent addedEvent = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEvent.getType());
        assertMountPath("./A1", addedEvent.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        session.close();

        unregister(registrationA1);
        MountPointEvent removedEvent = pluginA1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEvent.getType());
        assertEquals(addedEvent.getMountPoint(), removedEvent.getMountPoint());
    }

    public void test2MountPointsCallbackPattern01() throws Exception {
        TestDataMountPlugin pluginA = new TestDataMountPlugin();
        setLeafNode(pluginA, "./A1");
        setLeafNode(pluginA, "./A2");
		ServiceRegistration<DataPlugin> registrationA = registerMountDataPlugin(
				pluginA, new String[] {
						"./A1", "./A2"
				});

        MountPointEvent addedEvent0 = pluginA.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEvent0.getType());
        assertMountPath("./A1", addedEvent0.getMountPoint());

        MountPointEvent addedEvent1 = pluginA.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEvent1.getType());
        assertMountPath("./A1", addedEvent0.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertLeafNode("./A1");
        assertLeafNode("./A2");
        session.close();

        unregister(registrationA);
        MountPointEvent removedEvent0 = pluginA.getMountPointEvent(2);
        MountPointEvent removedEvent1 = pluginA.getMountPointEvent(3);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEvent0.getType());
        assertEquals(addedEvent0.getMountPoint(), removedEvent0.getMountPoint());
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEvent1.getType());
        assertEquals(addedEvent1.getMountPoint(), removedEvent1.getMountPoint());
    }

    public void test2MountPluginsCallbackPattern02() throws Exception {
        TestDataMountPlugin pluginA = new TestDataMountPlugin();
        setLeafNode(pluginA, "./A1");
        setLeafNode(pluginA, "./A2/B1");
		ServiceRegistration<DataPlugin> registrationA = registerMountDataPlugin(
				pluginA, new String[] {
						"./A1", "./A2/B1"
				});

        MountPointEvent addedEvent1 = pluginA.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEvent1.getType());
        assertMountPath("./A1", addedEvent1.getMountPoint());
        MountPointEvent addedEvent2 = pluginA.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEvent2.getType());
        assertMountPath("./A2/B1", addedEvent2.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertLeafNode("./A1");
        assertLeafNode("./A2/B1");
        session.close();

        unregister(registrationA);
        MountPointEvent removedEvent1 = pluginA.getMountPointEvent(2);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEvent1.getType());
        assertEquals(addedEvent1.getMountPoint(), removedEvent1.getMountPoint());
        MountPointEvent removedEvent2 = pluginA.getMountPointEvent(3);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEvent2.getType());
        assertEquals(addedEvent2.getMountPoint(), removedEvent2.getMountPoint());
    }

    public void test2MountPluginsCallbackPattern01() throws Exception {
        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});

        MountPointEvent addedEventA1 = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertMountPath("./A1", addedEventA1.getMountPoint());

        TestDataMountPlugin pluginA2B2 = new TestDataMountPlugin();
        setLeafNode(pluginA2B2, "./A1/B2");
        setLeafNode(pluginA2B2, "./A2");
		ServiceRegistration<DataPlugin> registrationA2B2 = registerMountDataPlugin(
				pluginA2B2, new String[] {
						"./A1/B2", "./A2"
				});

        MountPointEvent addedEventA2B21 = pluginA2B2.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA2B21.getType());
        assertMountPath("./A2", addedEventA2B21.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertNodeNotFound("./A1/B1");
        assertNodeNotFound("./A1/B2");
        assertLeafNode("./A2");
        session.close();

        unregister(registrationA1);
        MountPointEvent removedEventA1 = pluginA1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA1.getType());
        assertEquals(addedEventA1.getMountPoint(), removedEventA1.getMountPoint());

        MountPointEvent addedEventA2B22 = pluginA2B2.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA2B22.getType());
        assertMountPath("./A1/B2", addedEventA2B22.getMountPoint());

        unregister(registrationA2B2);
        MountPoint[] removedMountPoints = new MountPoint[2];
        MountPointEvent removedEventA2B21 = pluginA2B2.getMountPointEvent(2);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA2B21.getType());
        removedMountPoints[0] = removedEventA2B21.getMountPoint();
        MountPointEvent removedEventA2B22 = pluginA2B2.getMountPointEvent(3);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA2B21.getType());
        removedMountPoints[1] = removedEventA2B22.getMountPoint();
        ArrayAssert.assertEquivalenceArrays(new MountPoint[]{
        		addedEventA2B22.getMountPoint(), addedEventA2B21.getMountPoint()}, removedMountPoints);
    }

    public void test2MountPluginsCallbackPattern03() throws Exception {
        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setLeafNode(pluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1");
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        TestDataMountPlugin pluginB2C2 = new TestDataMountPlugin();
        setLeafNode(pluginB2C2, "./A1/B2/C2");
		ServiceRegistration<DataPlugin> registrationB2C2 = registerMountDataPlugin(
				pluginB2C2, "./A1/B2/C2");
        MountPointEvent addedEventB2C2 = pluginB2C2.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB2C2.getType());
        assertMountPath("./A1/B2/C2", addedEventB2C2.getMountPoint());

        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A1");
        assertLeafNode("./A1/B1");
        assertScaffoldNode("./A1/B2");
        assertLeafNode("./A1/B2/C2");
        session.close();

        unregister(registrationB1);
        MountPointEvent removedEventB1 = pluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        assertEquals(addedEventB1.getMountPoint(), removedEventB1.getMountPoint());

        unregister(registrationB2C2);
        MountPointEvent removedEventA2B2 = pluginB2C2.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA2B2.getType());
        assertEquals(addedEventB2C2.getMountPoint(), removedEventA2B2.getMountPoint());

        unregister(registrationA1);
    }

    public void test3MountPluginsCallbackPattern01() throws Exception {
        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setInteriorNode(pluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});
        MountPointEvent addedEventA1 = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertMountPath("./A1", addedEventA1.getMountPoint());

        TestDataMountPlugin pluginC1 = new TestDataMountPlugin();
        setLeafNode(pluginC1, "./A1/B1/C1");
		ServiceRegistration<DataPlugin> registrationC1 = registerMountDataPlugin(
				pluginC1, "./A1/B1/C1");
        MountPointEvent addedEventC1 = pluginC1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertInteriorNode("./A1/B1");
        assertLeafNode("./A1/B1/C1");
        session.close();

        unregister(registrationA1);
        unregister(registrationB1);
        unregister(registrationC1);
    }

    public void test3MountPluginsCallbackPattern02() throws Exception {
        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setInteriorNode(pluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        TestDataMountPlugin pluginC1 = new TestDataMountPlugin();
        setLeafNode(pluginC1, "./A1/B1/C1");
		ServiceRegistration<DataPlugin> registrationC1 = registerMountDataPlugin(
				pluginC1, "./A1/B1/C1");
        MountPointEvent addedEventC1 = pluginC1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());

        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});
        MountPointEvent addedEventA1 = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertMountPath("./A1", addedEventA1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertInteriorNode("./A1/B1");
        assertLeafNode("./A1/B1/C1");
        session.close();

        unregister(registrationA1);
        unregister(registrationB1);
        unregister(registrationC1);
    }

    public void test3MountPluginsCallbackPattern03() throws Exception {
        TestDataMountPlugin pluginC1 = new TestDataMountPlugin();
        setLeafNode(pluginC1, "./A1/B1/C1");
		ServiceRegistration<DataPlugin> registrationC1 = registerMountDataPlugin(
				pluginC1, "./A1/B1/C1");
        MountPointEvent addedEventC1 = pluginC1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());

        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});

        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setInteriorNode(pluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertInteriorNode("./A1/B1");
        assertLeafNode("./A1/B1/C1");
        session.close();

        unregister(registrationA1);
        unregister(registrationB1);
        unregister(registrationC1);
    }

    public void test3MountPluginsCallbackPattern04() throws Exception {
        TestDataMountPlugin pluginC1 = new TestDataMountPlugin();
        setLeafNode(pluginC1, "./A1/B1/C1");
		ServiceRegistration<DataPlugin> registrationC1 = registerMountDataPlugin(
				pluginC1, "./A1/B1/C1");
        MountPointEvent addedEventC1 = pluginC1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());

        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setInteriorNode(pluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});
        MountPointEvent addedEventA1 = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertMountPath("./A1", addedEventA1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertInteriorNode("./A1/B1");
        assertLeafNode("./A1/B1/C1");
        session.close();

        unregister(registrationA1);
        unregister(registrationB1);
        unregister(registrationC1);
    }

    public void test3MountPluginsCallbackPattern05() throws Exception {
        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});
        MountPointEvent addedEventA1 = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertMountPath("./A1", addedEventA1.getMountPoint());

        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setInteriorNode(pluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        TestDataMountPlugin pluginC1 = new TestDataMountPlugin();
        setLeafNode(pluginC1, "./A1/B1/C1");
		ServiceRegistration<DataPlugin> registrationC1 = registerMountDataPlugin(
				pluginC1, "./A1/B1/C1");
        MountPointEvent addedEventC1 = pluginC1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertInteriorNode("./A1/B1");
        assertLeafNode("./A1/B1/C1");
        session.close();

        unregister(registrationA1);
        MountPointEvent removedEventA1 = pluginA1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA1.getType());
        assertEquals(addedEventA1.getMountPoint(), removedEventA1.getMountPoint());

        unregister(registrationB1);
        MountPointEvent removedEventB1 = pluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        assertEquals(addedEventB1.getMountPoint(), removedEventB1.getMountPoint());

        unregister(registrationC1);
        MountPointEvent removedEventC1 = pluginC1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventC1.getType());
        assertEquals(addedEventC1.getMountPoint(), removedEventC1.getMountPoint());
    }

    public void test3MountPluginsCallbackPattern06() throws Exception {
        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});
        MountPointEvent addedEventA1 = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertMountPath("./A1", addedEventA1.getMountPoint());

        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setInteriorNode(pluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        TestDataMountPlugin pluginC1 = new TestDataMountPlugin();
        setLeafNode(pluginC1, "./A1/B1/C1");
		ServiceRegistration<DataPlugin> registrationC1 = registerMountDataPlugin(
				pluginC1, "./A1/B1/C1");
        MountPointEvent addedEventC1 = pluginC1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertInteriorNode("./A1/B1");
        assertLeafNode("./A1/B1/C1");
        session.close();

        unregister(registrationB1);
        MountPointEvent removedEventB1 = pluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        assertEquals(addedEventB1.getMountPoint(), removedEventB1.getMountPoint());
        MountPointEvent removedEventC1 = pluginC1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventC1.getType());
        assertEquals(addedEventC1.getMountPoint(), removedEventC1.getMountPoint());

        unregister(registrationA1);
        MountPointEvent removedEventA1 = pluginA1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA1.getType());
        assertEquals(addedEventA1.getMountPoint(), removedEventA1.getMountPoint());
        MountPointEvent reAddedEventC1 = pluginC1.getMountPointEvent(2);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, reAddedEventC1.getType());
        assertMountPath("./A1/B1/C1", reAddedEventC1.getMountPoint());

        unregister(registrationC1);
        MountPointEvent reRemovedEventC1 = pluginC1.getMountPointEvent(3);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, reRemovedEventC1.getType());
        assertMountPath("./A1/B1/C1", reRemovedEventC1.getMountPoint());
    }

    public void testMountPluginCallbackPatten08() throws Exception {
        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});
        MountPointEvent addedEventA1 = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertMountPath("./A1", addedEventA1.getMountPoint());

        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setInteriorNode(pluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        TestDataMountPlugin pluginC1 = new TestDataMountPlugin();
        setLeafNode(pluginC1, "./A1/B1/C1");
		ServiceRegistration<DataPlugin> registrationC1 = registerMountDataPlugin(
				pluginC1, "./A1/B1/C1");
        MountPointEvent addedEventC1 = pluginC1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertInteriorNode("./A1/B1");
        assertLeafNode("./A1/B1/C1");
        session.close();

        unregister(registrationC1);
        MountPointEvent removedEventC1 = pluginC1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventC1.getType());
        assertEquals(addedEventC1.getMountPoint(), removedEventC1.getMountPoint());

        unregister(registrationB1);
        MountPointEvent removedEventB1 = pluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        assertEquals(addedEventB1.getMountPoint(), removedEventB1.getMountPoint());

        unregister(registrationA1);
        MountPointEvent removedEventA1 = pluginA1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA1.getType());
        assertEquals(addedEventA1.getMountPoint(), removedEventA1.getMountPoint());
    }

    public void testMountPluginCallbackPattern10() throws Exception {
        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});
        MountPointEvent addedEventA1 = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertMountPath("./A1", addedEventA1.getMountPoint());

        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setInteriorNode(pluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        TestDataMountPlugin pluginC1 = new TestDataMountPlugin();
        setInteriorNode(pluginC1, "./A1/B1/C1");
		ServiceRegistration<DataPlugin> registrationC1 = registerMountDataPlugin(
				pluginC1, "./A1/B1/C1", new String[] {
						"D1"
				});
        MountPointEvent addedEventC1 = pluginC1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());

        TestDataMountPlugin pluginD1 = new TestDataMountPlugin();
        setLeafNode(pluginD1, "./A1/B1/C1/D1");
		ServiceRegistration<DataPlugin> registrationD1 = registerMountDataPlugin(
				pluginD1, "./A1/B1/C1/D1");
        MountPointEvent addedEventD1 = pluginD1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventD1.getType());
        assertMountPath("./A1/B1/C1/D1", addedEventD1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertInteriorNode("./A1/B1");
        assertInteriorNode("./A1/B1/C1");
        assertLeafNode("./A1/B1/C1/D1");
        session.close();

        unregister(registrationB1);
        MountPointEvent removedEventB1 = pluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        assertEquals(addedEventB1.getMountPoint(), removedEventB1.getMountPoint());
        MountPointEvent removedEventC1 = pluginC1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventC1.getType());
        assertEquals(addedEventC1.getMountPoint(), removedEventC1.getMountPoint());
        MountPointEvent removedEventD1 = pluginD1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventD1.getType());
        assertEquals(addedEventD1.getMountPoint(), removedEventD1.getMountPoint());

        unregister(registrationC1);
        unregister(registrationD1);

        unregister(registrationA1);
        MountPointEvent removedEventA1 = pluginA1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA1.getType());
        assertEquals(addedEventA1.getMountPoint(), removedEventA1.getMountPoint());
    }

    public void testMountPluginCallbackPattern11() throws Exception {
        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});
        MountPointEvent addedEventA1 = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertMountPath("./A1", addedEventA1.getMountPoint());

        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setInteriorNode(pluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        TestDataMountPlugin pluginC1 = new TestDataMountPlugin();
        setInteriorNode(pluginC1, "./A1/B1/C1");
		ServiceRegistration<DataPlugin> registrationC1 = registerMountDataPlugin(
				pluginC1, "./A1/B1/C1", new String[] {
						"D1"
				});
        MountPointEvent addedEventC1 = pluginC1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());

        TestDataMountPlugin pluginD1 = new TestDataMountPlugin();
        setLeafNode(pluginD1, "./A1/B1/C1/D1");
		ServiceRegistration<DataPlugin> registrationD1 = registerMountDataPlugin(
				pluginD1, "./A1/B1/C1/D1");
        MountPointEvent addedEventD1 = pluginD1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventD1.getType());
        assertMountPath("./A1/B1/C1/D1", addedEventD1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertInteriorNode("./A1/B1");
        assertInteriorNode("./A1/B1/C1");
        assertLeafNode("./A1/B1/C1/D1");
        session.close();

        unregister(registrationB1);
        MountPointEvent removedEventB1 = pluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        assertEquals(addedEventB1.getMountPoint(), removedEventB1.getMountPoint());
        MountPointEvent removedEventC1 = pluginC1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventC1.getType());
        assertEquals(addedEventC1.getMountPoint(), removedEventC1.getMountPoint());
        MountPointEvent removedEventD1 = pluginD1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventD1.getType());
        assertEquals(addedEventD1.getMountPoint(), removedEventD1.getMountPoint());

        unregister(registrationA1);
        MountPointEvent removedEventA1 = pluginA1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA1.getType());
        assertEquals(addedEventA1.getMountPoint(), removedEventA1.getMountPoint());
        addedEventC1 = pluginC1.getMountPointEvent(2);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());
        addedEventD1 = pluginD1.getMountPointEvent(2);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventD1.getType());
        assertMountPath("./A1/B1/C1/D1", addedEventD1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A1");
        assertScaffoldNode("./A1/B1");
        assertInteriorNode("./A1/B1/C1");
        assertLeafNode("./A1/B1/C1/D1");
        session.close();

        unregister(registrationC1);
        removedEventC1 = pluginC1.getMountPointEvent(3);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventC1.getType());
        assertEquals(addedEventC1.getMountPoint(), removedEventC1.getMountPoint());

        unregister(registrationD1);
        removedEventD1 = pluginD1.getMountPointEvent(3);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventD1.getType());
        assertEquals(addedEventD1.getMountPoint(), removedEventD1.getMountPoint());
    }

    public void testMountPluginCallbackPattern12() throws Exception {
        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});
        MountPointEvent addedEventA1 = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertMountPath("./A1", addedEventA1.getMountPoint());

        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setInteriorNode(pluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        TestDataMountPlugin pluginC1 = new TestDataMountPlugin();
        setInteriorNode(pluginC1, "./A1/B1/C1");
		ServiceRegistration<DataPlugin> registrationC1 = registerMountDataPlugin(
				pluginC1, "./A1/B1/C1", new String[] {
						"D1"
				});
        MountPointEvent addedEventC1 = pluginC1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());

        TestDataMountPlugin pluginD1 = new TestDataMountPlugin();
        setLeafNode(pluginD1, "./A1/B1/C1/D1");
		ServiceRegistration<DataPlugin> registrationD1 = registerMountDataPlugin(
				pluginD1, "./A1/B1/C1/D1");
        MountPointEvent addedEventD1 = pluginD1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventD1.getType());
        assertMountPath("./A1/B1/C1/D1", addedEventD1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertInteriorNode("./A1/B1");
        assertInteriorNode("./A1/B1/C1");
        assertLeafNode("./A1/B1/C1/D1");
        session.close();

        unregister(registrationB1);
        MountPointEvent removedEventB1 = pluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        assertEquals(addedEventB1.getMountPoint(), removedEventB1.getMountPoint());
        MountPointEvent removedEventC1 = pluginC1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventC1.getType());
        assertEquals(addedEventC1.getMountPoint(), removedEventC1.getMountPoint());
        MountPointEvent removedEventD1 = pluginD1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventD1.getType());
        assertEquals(addedEventD1.getMountPoint(), removedEventD1.getMountPoint());

        unregister(registrationA1);
        MountPointEvent removedEventA1 = pluginA1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA1.getType());
        assertEquals(addedEventA1.getMountPoint(), removedEventA1.getMountPoint());
        addedEventC1 = pluginC1.getMountPointEvent(2);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());
        addedEventD1 = pluginD1.getMountPointEvent(2);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventD1.getType());
        assertMountPath("./A1/B1/C1/D1", addedEventD1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A1");
        assertScaffoldNode("./A1/B1");
        assertInteriorNode("./A1/B1/C1");
        assertLeafNode("./A1/B1/C1/D1");
        session.close();

        unregister(registrationD1);
        removedEventD1 = pluginD1.getMountPointEvent(3);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventD1.getType());
        assertEquals(addedEventD1.getMountPoint(), removedEventD1.getMountPoint());

        unregister(registrationC1);
        removedEventC1 = pluginC1.getMountPointEvent(3);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventC1.getType());
        assertEquals(addedEventC1.getMountPoint(), removedEventC1.getMountPoint());
    }

    public void testMountPluginCallbackPattern13() throws Exception {
        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});
        MountPointEvent addedEventA1 = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertMountPath("./A1", addedEventA1.getMountPoint());

        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setInteriorNode(pluginB1, "./A1/B1");
        setInteriorNode(pluginB1, "./A1/B1/C1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1/D1"
				});
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        TestDataMountPlugin pluginD1 = new TestDataMountPlugin();
        setInteriorNode(pluginD1, "./A1/B1/C1/D1");
		ServiceRegistration<DataPlugin> registrationD1 = registerMountDataPlugin(
				pluginD1, "./A1/B1/C1/D1", new String[] {
						"E1"
				});
        MountPointEvent addedEventD1 = pluginD1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventD1.getType());
        assertMountPath("./A1/B1/C1/D1", addedEventD1.getMountPoint());

        TestDataMountPlugin pluginE1 = new TestDataMountPlugin();
        setLeafNode(pluginE1, "./A1/B1/C1/D1/E1");
		ServiceRegistration<DataPlugin> registrationE1 = registerMountDataPlugin(
				pluginE1, "./A1/B1/C1/D1/E1");
        MountPointEvent addedEventE1 = pluginE1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventE1.getType());
        assertMountPath("./A1/B1/C1/D1/E1", addedEventE1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertInteriorNode("./A1/B1");
        assertInteriorNode("./A1/B1/C1");
        assertInteriorNode("./A1/B1/C1/D1");
        assertLeafNode("./A1/B1/C1/D1/E1");
        session.close();

        unregister(registrationB1);
        MountPointEvent removedEventB1 = pluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        assertEquals(addedEventB1.getMountPoint(), removedEventB1.getMountPoint());
        MountPointEvent removedEventD1 = pluginD1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventD1.getType());
        assertEquals(addedEventD1.getMountPoint(), removedEventD1.getMountPoint());
        MountPointEvent removedEventE1 = pluginE1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventE1.getType());
        assertEquals(addedEventE1.getMountPoint(), removedEventE1.getMountPoint());

        unregister(registrationA1);
        MountPointEvent removedEventA1 = pluginA1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA1.getType());
        assertEquals(addedEventA1.getMountPoint(), removedEventA1.getMountPoint());
        addedEventD1 = pluginD1.getMountPointEvent(2);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventD1.getType());
        assertMountPath("./A1/B1/C1/D1", addedEventD1.getMountPoint());
        addedEventE1 = pluginE1.getMountPointEvent(2);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventE1.getType());
        assertMountPath("./A1/B1/C1/D1/E1", addedEventE1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A1");
        assertScaffoldNode("./A1/B1");
        assertScaffoldNode("./A1/B1/C1");
        assertInteriorNode("./A1/B1/C1/D1");
        assertLeafNode("./A1/B1/C1/D1/E1");
        session.close();

        unregister(registrationD1);
        removedEventD1 = pluginD1.getMountPointEvent(3);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventD1.getType());
        assertEquals(addedEventD1.getMountPoint(), removedEventD1.getMountPoint());

        unregister(registrationE1);
        removedEventE1 = pluginE1.getMountPointEvent(3);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventE1.getType());
        assertEquals(addedEventE1.getMountPoint(), removedEventE1.getMountPoint());
    }

    public void testMountPluginCallbackPattern14() throws Exception {
        TestDataMountPlugin pluginA1 = new TestDataMountPlugin();
        setInteriorNode(pluginA1, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginA1, "./A1", new String[] {
						"B1"
				});
        MountPointEvent addedEventA1 = pluginA1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertMountPath("./A1", addedEventA1.getMountPoint());

        TestDataMountPlugin pluginB1 = new TestDataMountPlugin();
        setInteriorNode(pluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				pluginB1, "./A1/B1", new String[] {
						"C1"
				});
        MountPointEvent addedEventB1 = pluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        assertMountPath("./A1/B1", addedEventB1.getMountPoint());

        TestDataMountPlugin pluginC1 = new TestDataMountPlugin();
        setInteriorNode(pluginC1, "./A1/B1/C1");
		ServiceRegistration<DataPlugin> registrationC1 = registerMountDataPlugin(
				pluginC1, "./A1/B1/C1", new String[] {
						"D1"
				});
        MountPointEvent addedEventC1 = pluginC1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventC1.getType());
        assertMountPath("./A1/B1/C1", addedEventC1.getMountPoint());

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertInteriorNode("./A1/B1");
        assertInteriorNode("./A1/B1/C1");
        session.close();

        unregister(registrationB1);
        MountPointEvent removedEventB1 = pluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        assertEquals(addedEventB1.getMountPoint(), removedEventB1.getMountPoint());
        MountPointEvent removedEventC1 = pluginC1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventC1.getType());
        assertEquals(addedEventC1.getMountPoint(), removedEventC1.getMountPoint());

        TestDataMountPlugin pluginD1 = new TestDataMountPlugin();
        setLeafNode(pluginD1, "./A1/B1/C1/D1");
		ServiceRegistration<DataPlugin> registrationD1 = registerMountDataPlugin(
				pluginD1, "./A1/B1/C1/D1");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertNodeNotFound("./A1/B1");
        assertNodeNotFound("./A1/B1/C1");
        assertNodeNotFound("./A1/B1/C1/D1");
        session.close();

        unregister(registrationA1);
        MountPointEvent removedEventA1 = pluginA1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA1.getType());
        assertEquals(addedEventA1.getMountPoint(), removedEventA1.getMountPoint());

        unregister(registrationC1);
        unregister(registrationD1);
    }
}
