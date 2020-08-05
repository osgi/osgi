package org.osgi.test.cases.dmt.tc4.ext.junit;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.ArrayAssert;
import org.osgi.test.cases.dmt.tc4.ext.util.MountPointEvent;
import org.osgi.test.cases.dmt.tc4.ext.util.TestDataMountPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.TestMetaNode;

public class SharedMountPointTest extends DmtAdminTestCase {

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

    public void testMountPointNotIndicatePluginTableOrder01() throws Exception {
        TestDataMountPlugin pluginTable = new TestDataMountPlugin();
        setInteriorNode(pluginTable, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginTable, "./A1", new String[] {
						"B1"
				});

        MountPointEvent addedEvent = pluginTable.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEvent.getType());
        assertEquals("./A1", Uri.toUri(addedEvent.getMountPoint().getMountPath()));

        TestDataMountPlugin sharedPluginB1 = new TestDataMountPlugin();
        setLeafNode(sharedPluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				sharedPluginB1, "./A1/#");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertEquals(0, session.getChildNodeNames("./A1").length);
        assertNodeNotFound("./A1/B1");
        session.close();

        unregister(registrationA1);
        MountPointEvent removedEvent = pluginTable.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEvent.getType());
        assertEquals("./A1", Uri.toUri(removedEvent.getMountPoint().getMountPath()));

        unregister(registrationB1);
    }

    public void testMountPointNotIndicatePluginTableOrder02() throws Exception {
        TestDataMountPlugin sharedPluginB1 = new TestDataMountPlugin();
        setLeafNode(sharedPluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				sharedPluginB1, "./A1/#");

        MountPointEvent addedEvent = sharedPluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEvent.getType());
        String mountUri = Uri.toUri(addedEvent.getMountPoint().getMountPath());
        assertTrue(mountUri.startsWith("./A1"));
        setInteriorNode(sharedPluginB1, mountUri);

        TestDataMountPlugin pluginTable = new TestDataMountPlugin();
        setInteriorNode(pluginTable, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginTable, "./A1", new String[] {
						"B1"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A1");
        assertEquals(1, session.getChildNodeNames("./A1").length);
        assertInteriorNode(mountUri);
        assertNodeNotFound("./A1/B1");
        session.close();

        unregister(registrationA1);

        unregister(registrationB1);
        MountPointEvent removedEvent = sharedPluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEvent.getType());
        assertEquals(mountUri, Uri.toUri(removedEvent.getMountPoint().getMountPath()));
    }

    public void testRootURINotIndicatePluginTableOrder01() throws Exception {
        TestDataMountPlugin pluginTable = new TestDataMountPlugin();
        setInteriorNode(pluginTable, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginTable, "./A1", new String[] {
						"#"
				});

        MountPointEvent addedEvent = pluginTable.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEvent.getType());
        assertEquals("./A1", Uri.toUri(addedEvent.getMountPoint().getMountPath()));

        TestDataMountPlugin sharedPluginB1 = new TestDataMountPlugin();
        setLeafNode(sharedPluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				sharedPluginB1, "./A1/B1");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertEquals(0, session.getChildNodeNames("./A1").length);
        assertNodeNotFound("./A1/B1");
        session.close();

        unregister(registrationA1);
        MountPointEvent removedEvent = pluginTable.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEvent.getType());
        assertEquals("./A1", Uri.toUri(removedEvent.getMountPoint().getMountPath()));

        unregister(registrationB1);
    }

    public void testRootURINotIndicatePluginTablOrder02() throws Exception {
        TestDataMountPlugin sharedPluginB1 = new TestDataMountPlugin();
        setLeafNode(sharedPluginB1, "./A1/B1");
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				sharedPluginB1, "./A1/B1");

        MountPointEvent addedEvent = sharedPluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEvent.getType());
        assertEquals("./A1/B1", Uri.toUri(addedEvent.getMountPoint().getMountPath()));

        TestDataMountPlugin pluginTable = new TestDataMountPlugin();
        setInteriorNode(pluginTable, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginTable, "./A1", new String[] {
						"#"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A1");
        assertLeafNode("./A1/B1");
        session.close();

        unregister(registrationA1);

        unregister(registrationB1);
        MountPointEvent removedEvent = sharedPluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEvent.getType());
        assertEquals("./A1/B1", Uri.toUri(removedEvent.getMountPoint().getMountPath()));
    }

    public void testRemountEnumeratedPlugins01() throws Exception {
        TestDataMountPlugin pluginTable = new TestDataMountPlugin();
        setInteriorNode(pluginTable, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginTable, "./A1", new String[] {
						"#"
				});
        MountPointEvent addedEventA1 = pluginTable.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertEquals("./A1", Uri.toUri(addedEventA1.getMountPoint().getMountPath()));

        TestDataMountPlugin sharedPluginB1 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				sharedPluginB1, "./A1/#");
        MountPointEvent addedEventB1 = sharedPluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        String[] mountPathB1 = addedEventB1.getMountPoint().getMountPath();
        String rootNodeNameB1 = getLastSegment(mountPathB1);
        assertEnumratedPluginMountPoint(mountPathB1, "./A1");
        setInteriorNode(sharedPluginB1, mountPathB1);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertChildNodeNames("./A1", new String[] { rootNodeNameB1 });
        assertInteriorNode(mountPathB1);
        session.close();

        unregister(registrationA1);
        MountPointEvent removedEventA1 = pluginTable.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA1.getType());
        assertEquals("./A1", Uri.toUri(removedEventA1.getMountPoint().getMountPath()));

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A1");
        assertChildNodeNames("./A1", new String[] { rootNodeNameB1 });
        assertInteriorNode(mountPathB1);
        session.close();

        TestDataMountPlugin newPluginTable = new TestDataMountPlugin();
        setInteriorNode(newPluginTable, "./A1");
		ServiceRegistration<DataPlugin> newRegistrationA1 = registerMountDataPlugin(
				newPluginTable, "./A1", new String[] {
						"#"
				});
        MountPointEvent addedEventNewA1 = newPluginTable.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventNewA1.getType());
        assertEquals("./A1", Uri.toUri(addedEventNewA1.getMountPoint().getMountPath()));

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertChildNodeNames("./A1", new String[] { rootNodeNameB1 });
        assertInteriorNode(mountPathB1);
        session.close();

        unregister(newRegistrationA1);
        MountPointEvent removedEventNewA1 = newPluginTable.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventNewA1.getType());
        assertEquals("./A1", Uri.toUri(removedEventNewA1.getMountPoint().getMountPath()));

        unregister(registrationB1);
        MountPointEvent removedEventB1 = sharedPluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        assertEquals(Uri.toUri(mountPathB1), Uri.toUri(removedEventB1.getMountPoint().getMountPath()));
    }

    public void testRemountEnumeratedPlugins02() throws Exception {
        TestDataMountPlugin pluginTable = new TestDataMountPlugin();
        setInteriorNode(pluginTable, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginTable, "./A1", new String[] {
						"#"
				});
        MountPointEvent addedEventA1 = pluginTable.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventA1.getType());
        assertEquals("./A1", Uri.toUri(addedEventA1.getMountPoint().getMountPath()));

        TestDataMountPlugin sharedPluginB1 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				sharedPluginB1, "./A1/#");
        MountPointEvent addedEventB1 = sharedPluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        String[] mountPathB1 = addedEventB1.getMountPoint().getMountPath();
        String rootNodeNameB1 = getLastSegment(mountPathB1);
        assertEnumratedPluginMountPoint(mountPathB1, "./A1");
        setInteriorNode(sharedPluginB1, mountPathB1);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A1");
        assertChildNodeNames("./A1", new String[] { rootNodeNameB1 });
        assertInteriorNode(mountPathB1);
        session.close();

        unregister(registrationA1);
        MountPointEvent removedEventA1 = pluginTable.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventA1.getType());
        assertEquals("./A1", Uri.toUri(removedEventA1.getMountPoint().getMountPath()));

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A1");
        assertChildNodeNames("./A1", new String[] { rootNodeNameB1 });
        assertInteriorNode(mountPathB1);
        session.close();

        TestDataMountPlugin sharedPluginB2 = new TestDataMountPlugin();
        setInteriorNode(sharedPluginB2, "./A1/B2");
		ServiceRegistration<DataPlugin> registrationB2 = registerMountDataPlugin(
				sharedPluginB2, "./A1/B2");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A1");
        assertInteriorNode(mountPathB1);
        assertNodeNotFound("./A1/B2");
        session.close();

        unregister(registrationB1);
        MountPointEvent removedEventB1 = sharedPluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        assertEquals(Uri.toUri(mountPathB1), Uri.toUri(removedEventB1.getMountPoint().getMountPath()));

        MountPointEvent addedEventB2 = sharedPluginB2.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB2.getType());
        assertEquals("./A1/B2", Uri.toUri(addedEventB2.getMountPoint().getMountPath()));

        unregister(registrationB2);
        MountPointEvent removedEventB2 = sharedPluginB2.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB2.getType());
        assertEquals("./A1/B2", Uri.toUri(removedEventB2.getMountPoint().getMountPath()));
    }

    public void testMountUnknownEnumeratedPlugins() throws Exception {
        TestDataMountPlugin pluginTable = new TestDataMountPlugin();
        setInteriorNode(pluginTable, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginTable, "./A1", new String[] {
						"#"
				});

        TestDataMountPlugin sharedPluginB1 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPlugin(
				sharedPluginB1, "./A1/#");

        MountPointEvent addedEventB1 = sharedPluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        String[] mountPathB1 = addedEventB1.getMountPoint().getMountPath();
        int mountPonintNumberB1 = assertEnumratedPluginMountPoint(mountPathB1, "./A1");

        TestDataMountPlugin sharedPluginB2 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> registrationB2 = registerMountDataPlugin(
				sharedPluginB2, "./A1/#");

        MountPointEvent addedEventB2 = sharedPluginB2.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB2.getType());
        String[] mountPathB2 = addedEventB2.getMountPoint().getMountPath();
        int mountPonintNumberB2 = assertEnumratedPluginMountPoint(mountPathB2, "./A1");

        assertEquals(mountPonintNumberB1 + 1, mountPonintNumberB2);

        unregister(registrationB1);
        MountPointEvent removedEventB1 = sharedPluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        ArrayAssert.assertEquivalenceArrays(mountPathB1, removedEventB1.getMountPoint().getMountPath());

        unregister(registrationB2);
        MountPointEvent removedEventB2 = sharedPluginB2.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB2.getType());
        ArrayAssert.assertEquivalenceArrays(mountPathB2, removedEventB2.getMountPoint().getMountPath());

        unregister(registrationA1);
    }

    public void testMountKnownEnumeratedPlugins() throws Exception {
        TestDataMountPlugin pluginTable = new TestDataMountPlugin();
        setInteriorNode(pluginTable, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginTable, "./A1", new String[] {
						"#"
				});

        TestDataMountPlugin sharedPluginB1 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPluginWithServicePIDs(
				sharedPluginB1, "./A1/#", new String[] {
						"B1"
				});
        MountPointEvent addedEventB1 = sharedPluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB1.getType());
        String[] mountPathB1 = addedEventB1.getMountPoint().getMountPath();
        int mountPonintNumberB1 = assertEnumratedPluginMountPoint(mountPathB1, "./A1");

        TestDataMountPlugin sharedPluginB2 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> registrationB2 = registerMountDataPluginWithServicePIDs(
				sharedPluginB2, "./A1/#", new String[] {
						"B2"
				});
        MountPointEvent addedEventB2 = sharedPluginB2.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, addedEventB2.getType());
        String[] mountPathB2 = addedEventB2.getMountPoint().getMountPath();
        int mountPonintNumberB2 = assertEnumratedPluginMountPoint(mountPathB2, "./A1");

        assertTrue(mountPonintNumberB1 != mountPonintNumberB2);

        unregister(registrationB1);
        MountPointEvent removedEventB1 = sharedPluginB1.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB1.getType());
        ArrayAssert.assertEquivalenceArrays(mountPathB1, removedEventB1.getMountPoint().getMountPath());

        TestDataMountPlugin newSharedPluginB1 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> newRegistrationB1 = registerMountDataPluginWithServicePIDs(
				newSharedPluginB1, "./A1/#", new String[] {
						"B1"
				});
        MountPointEvent newAddedEventB1 = newSharedPluginB1.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, newAddedEventB1.getType());
        String[] newMountPathB1 = newAddedEventB1.getMountPoint().getMountPath();
        int newMountPonintNumberB1 = assertEnumratedPluginMountPoint(newMountPathB1, "./A1");

        assertEquals(mountPonintNumberB1, newMountPonintNumberB1);

        unregister(registrationB2);
        MountPointEvent removedEventB2 = sharedPluginB2.getMountPointEvent(1);
        assertEquals(MountPointEvent.MOUNT_POINTS_REMOVED, removedEventB2.getType());
        ArrayAssert.assertEquivalenceArrays(mountPathB2, removedEventB2.getMountPoint().getMountPath());

        TestDataMountPlugin newSharedPluginB2 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> newRegistrationB2 = registerMountDataPluginWithServicePIDs(
				newSharedPluginB2, "./A1/#", new String[] {
						"B2"
				});
        MountPointEvent newAddedEventB2 = newSharedPluginB2.getMountPointEvent(0);
        assertEquals(MountPointEvent.MOUNT_POINTS_ADDED, newAddedEventB2.getType());
        String[] newMountPathB2 = newAddedEventB2.getMountPoint().getMountPath();
        int newMountPonintNumberB2 = assertEnumratedPluginMountPoint(newMountPathB2, "./A1");

        assertEquals(mountPonintNumberB2, newMountPonintNumberB2);

        unregister(registrationA1);
        unregister(newRegistrationB1);
        unregister(newRegistrationB2);
    }

    public void testGetMetaNodeNotMountSharedPlugins() throws Exception {
        TestDataMountPlugin pluginTable = new TestDataMountPlugin();
        setInteriorNode(pluginTable, "./A1");
		ServiceRegistration<DataPlugin> registrationA1 = registerMountDataPlugin(
				pluginTable, "./A1", new String[] {
						"#"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertSharedMountPointMetaNode(session.getMetaNode("./A1/1"), MetaNode.DYNAMIC);
        } finally {
            session.close();
        }

        TestDataMountPlugin sharedPluginB1 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> registrationB1 = registerMountDataPluginWithServicePIDs(
				sharedPluginB1, "./A1/#", new String[] {
						"B1"
				});
        MountPointEvent addedEventB1 = sharedPluginB1.getMountPointEvent(0);
        String[] mountPathB1 = addedEventB1.getMountPoint().getMountPath();
        setInteriorNode(sharedPluginB1, mountPathB1);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertEquals(TestMetaNode.class, session.getMetaNode(Uri.toUri(mountPathB1)).getClass());
        } finally {
            session.close();
        }

        TestDataMountPlugin sharedPluginB2 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> registrationB2 = registerMountDataPluginWithServicePIDs(
				sharedPluginB2, "./A1/#", new String[] {
						"B2"
				});
        MountPointEvent addedEventB2 = sharedPluginB2.getMountPointEvent(0);
        String[] mountPathB2 = addedEventB2.getMountPoint().getMountPath();
        setInteriorNode(sharedPluginB2, mountPathB2);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertEquals(TestMetaNode.class, session.getMetaNode(Uri.toUri(mountPathB1)).getClass());
            assertEquals(TestMetaNode.class, session.getMetaNode(Uri.toUri(mountPathB2)).getClass());
        } finally {
            session.close();
        }

        unregister(registrationB1);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertSharedMountPointMetaNode(session.getMetaNode(Uri.toUri(mountPathB1)), MetaNode.DYNAMIC);
            assertEquals(TestMetaNode.class, session.getMetaNode(Uri.toUri(mountPathB2)).getClass());
        } finally {
            session.close();
        }

        TestDataMountPlugin newSharedPluginB1 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> newRegistrationB1 = registerMountDataPluginWithServicePIDs(
				newSharedPluginB1, "./A1/#", new String[] {
						"B1"
				});
        setInteriorNode(newSharedPluginB1, mountPathB1);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertEquals(TestMetaNode.class, session.getMetaNode(Uri.toUri(mountPathB1)).getClass());
            assertEquals(TestMetaNode.class, session.getMetaNode(Uri.toUri(mountPathB2)).getClass());
        } finally {
            session.close();
        }

        unregister(registrationB2);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertEquals(TestMetaNode.class, session.getMetaNode(Uri.toUri(mountPathB1)).getClass());
            assertSharedMountPointMetaNode(session.getMetaNode(Uri.toUri(mountPathB2)), MetaNode.DYNAMIC);
        } finally {
            session.close();
        }

        TestDataMountPlugin newSharedPluginB2 = new TestDataMountPlugin();
		ServiceRegistration<DataPlugin> newRegistrationB2 = registerMountDataPluginWithServicePIDs(
				newSharedPluginB2, "./A1/#", new String[] {
						"B2"
				});
        setInteriorNode(newSharedPluginB2, mountPathB2);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertEquals(TestMetaNode.class, session.getMetaNode(Uri.toUri(mountPathB1)).getClass());
            assertEquals(TestMetaNode.class, session.getMetaNode(Uri.toUri(mountPathB2)).getClass());
        } finally {
            session.close();
        }

        unregister(registrationA1);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertEquals(TestMetaNode.class, session.getMetaNode(Uri.toUri(mountPathB1)).getClass());
            assertEquals(TestMetaNode.class, session.getMetaNode(Uri.toUri(mountPathB2)).getClass());
        } finally {
            session.close();
        }

        unregister(newRegistrationB1);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertEquals(TestMetaNode.class, session.getMetaNode(Uri.toUri(mountPathB2)).getClass());
        } finally {
            session.close();
        }

        unregister(newRegistrationB2);
    }

    private void assertSharedMountPointMetaNode(MetaNode sharedMountPointMetaNode, int scope) {
        assertTrue(sharedMountPointMetaNode.can(MetaNode.CMD_GET));
        assertNull(sharedMountPointMetaNode.getDefault());
        assertNull(sharedMountPointMetaNode.getDescription());
        assertEquals(DmtData.FORMAT_NODE, sharedMountPointMetaNode.getFormat());
        assertEquals(Double.MAX_VALUE, sharedMountPointMetaNode.getMax(), 0.0d);
        assertEquals(Integer.MAX_VALUE, sharedMountPointMetaNode.getMaxOccurrence());
        assertNull(null, sharedMountPointMetaNode.getMimeTypes());
        assertEquals(Double.MIN_VALUE, sharedMountPointMetaNode.getMin(), 0.0d);
        assertNull(sharedMountPointMetaNode.getRawFormatNames());
        assertEquals(scope, sharedMountPointMetaNode.getScope());
        assertNull(sharedMountPointMetaNode.getValidNames());
        assertNull(sharedMountPointMetaNode.getValidValues());
        assertFalse(sharedMountPointMetaNode.isLeaf());

        assertFalse(sharedMountPointMetaNode.isValidName("foo"));
        assertFalse(sharedMountPointMetaNode.isValidName(Integer.toString(Integer.MIN_VALUE)));
        assertFalse(sharedMountPointMetaNode.isValidName("-1"));
        assertFalse(sharedMountPointMetaNode.isValidName("0"));
        assertTrue(sharedMountPointMetaNode.isValidName("1"));
        assertTrue(sharedMountPointMetaNode.isValidName("2"));
        assertTrue(sharedMountPointMetaNode.isValidName(Integer.toString(Integer.MAX_VALUE - 1)));
        assertFalse(sharedMountPointMetaNode.isValidName(Integer.toString(Integer.MAX_VALUE)));
        assertFalse(sharedMountPointMetaNode.isValidName(Integer.toString(Integer.MAX_VALUE) + "0"));

        assertFalse(sharedMountPointMetaNode.isValidValue(new DmtData(1)));
        assertFalse(sharedMountPointMetaNode.isValidValue(new DmtData("foo")));
        assertTrue(sharedMountPointMetaNode.isZeroOccurrenceAllowed());
    }
}
