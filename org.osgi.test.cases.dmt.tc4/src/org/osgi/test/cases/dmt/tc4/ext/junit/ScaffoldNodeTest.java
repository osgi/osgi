package org.osgi.test.cases.dmt.tc4.ext.junit;

import java.util.Date;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.ArrayAssert;
import org.osgi.test.cases.dmt.tc4.ext.util.TestDataMountPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.TestDataPlugin;

public class ScaffoldNodeTest extends DmtAdminTestCase {

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

    public void testScaffoldNodeData() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A/B");

        long pluginRegistrationTime = System.currentTimeMillis();
        registerDataPlugin(dataPlugin, "./A/B");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertInteriorNode("./A/B");

        MetaNode metaNode = session.getMetaNode("./A");
        assertEquals(false, metaNode.can(MetaNode.CMD_ADD));
        assertEquals(false, metaNode.can(MetaNode.CMD_DELETE));
        assertEquals(false, metaNode.can(MetaNode.CMD_EXECUTE));
        assertEquals(true, metaNode.can(MetaNode.CMD_GET));
        assertEquals(false, metaNode.can(MetaNode.CMD_REPLACE));

        assertEquals(false, metaNode.isLeaf());
        assertEquals(MetaNode.PERMANENT, metaNode.getScope());
        assertEquals(null, metaNode.getDescription());
        assertEquals(1, metaNode.getMaxOccurrence());
        assertEquals(true, metaNode.isZeroOccurrenceAllowed());
        assertEquals(null, metaNode.getDefault());
        assertEquals(null, metaNode.getValidNames());
        assertEquals(null, metaNode.getValidValues());
        assertEquals(Double.MAX_VALUE, metaNode.getMax(), 0.0d);
        assertEquals(Double.MIN_VALUE, metaNode.getMin(), 0.0d);
        assertEquals(null, metaNode.getRawFormatNames());
        ArrayAssert.assertEquivalenceArrays(metaNode.getMimeTypes(), new String[]{ DmtConstants.DDF_SCAFFOLD });

        assertEquals(true, metaNode.isValidName(""));
        assertEquals(true, metaNode.isValidName("A"));
        assertEquals(true, metaNode.isValidName("."));
        assertEquals(true, metaNode.isValidName("/"));

        assertEquals(false, metaNode.isValidValue(new DmtData(true)));
        assertEquals(false, metaNode.isValidValue(new DmtData(new byte[] { (byte) 0, (byte) 1, (byte) 2 })));
        assertEquals(false, metaNode.isValidValue(new DmtData("ABC")));
        assertEquals(false, metaNode.isValidValue(new DmtData(new Object())));

        assertEquals(DmtData.FORMAT_NODE, metaNode.getFormat());

        ArrayAssert.assertEquivalenceArrays(new String[] { "B" }, session.getChildNodeNames("./A"));
        Acl acl = session.getNodeAcl(".");
        assertEquals(null, session.getNodeAcl("./A"));
        assertEquals(acl, session.getEffectiveNodeAcl("./A"));
        assertEquals(null, session.getNodeTitle("./A"));
        assertEquals(DmtConstants.DDF_SCAFFOLD, session.getNodeType("./A"));
        try {
            assertEquals(-1, session.getNodeSize("./A"));
        } catch (DmtException dmtException) {
            assertEquals(DmtException.COMMAND_NOT_ALLOWED, dmtException.getCode());
            assertEquals("./A", dmtException.getURI());
            assertEquals(null, dmtException.getCause());
            assertEquals(0, dmtException.getCauses().length);
        }
        try {
            assertEquals(null, session.getNodeValue("./A"));
            fail();
        } catch (DmtException dmtException) {
            assertEquals(DmtException.COMMAND_NOT_ALLOWED, dmtException.getCode());
            assertEquals("./A", dmtException.getURI());
            assertEquals(null, dmtException.getCause());
            assertEquals(0, dmtException.getCauses().length);
        }

        assertEquals(0, session.getNodeVersion("./A"));

        Date timeStamp = session.getNodeTimestamp("./A");
        assertTrue(pluginRegistrationTime <= timeStamp.getTime());
        assertTrue(timeStamp.getTime() <= System.currentTimeMillis());

        session.close();
    }

    public void testScaffoldNodePattern01() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A/B");
		ServiceRegistration<DataPlugin> registration = registerDataPlugin(
				dataPlugin, "./A/B");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A");
        assertInteriorNode("./A/B");
        session.close();

        unregister(registration);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertNodeNotFound("./A");
        assertNodeNotFound("./A/B");
        session.close();
    }

    public void testScaffoldNodePattern02() throws DmtException {
        TestDataPlugin dataPluginB = new TestDataPlugin();
        setInteriorNode(dataPluginB, "./A/B");
		ServiceRegistration<DataPlugin> registrationB = registerDataPlugin(
				dataPluginB, "./A/B");

        TestDataPlugin dataPluginC = new TestDataPlugin();
        setInteriorNode(dataPluginC, "./A/C");
		ServiceRegistration<DataPlugin> registrationC = registerDataPlugin(
				dataPluginC, "./A/C");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A");
        assertInteriorNode("./A/B");
        assertInteriorNode("./A/C");
        session.close();

        unregister(registrationB);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A");
        assertNodeNotFound("./A/B");
        assertInteriorNode("./A/C");
        session.close();

        unregister(registrationC);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertNodeNotFound("./A");
        assertNodeNotFound("./A/B");
        assertNodeNotFound("./A/C");
        session.close();
    }

    public void testScaffoldNodePattern03() throws DmtException {
        TestDataPlugin dataPluginB = new TestDataPlugin();
        setInteriorNode(dataPluginB, "./A/B");
		ServiceRegistration<DataPlugin> registrationB = registerDataPlugin(
				dataPluginB, "./A/B");

        TestDataPlugin dataPluginC = new TestDataPlugin();
        setInteriorNode(dataPluginC, "./A/C");
		ServiceRegistration<DataPlugin> registrationC = registerDataPlugin(
				dataPluginC, "./A/C");

        TestDataPlugin dataPluginY = new TestDataPlugin();
        setInteriorNode(dataPluginY, "./A/X/Y");
		ServiceRegistration<DataPlugin> registrationY = registerDataPlugin(
				dataPluginY, "./A/X/Y");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A");
        assertInteriorNode("./A/B");
        assertInteriorNode("./A/C");
        assertScaffoldNode("./A/X");
        assertInteriorNode("./A/X/Y");
        session.close();

        unregister(registrationB);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A");
        assertNodeNotFound("./A/B");
        assertInteriorNode("./A/C");
        assertScaffoldNode("./A/X");
        assertInteriorNode("./A/X/Y");
        session.close();

        unregister(registrationC);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertScaffoldNode("./A");
        assertNodeNotFound("./A/B");
        assertNodeNotFound("./A/C");
        assertScaffoldNode("./A/X");
        assertInteriorNode("./A/X/Y");
        session.close();

        unregister(registrationY);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        assertNodeNotFound("./A");
        assertNodeNotFound("./A/B");
        assertNodeNotFound("./A/C");
        assertNodeNotFound("./A/XX");
        assertNodeNotFound("./A/X/Y");
        session.close();
    }

    public void testWriteCommandToScaffoldNodeIsNotAllowedOnExclusiveLockType() throws DmtException {
        TestDataPlugin dataPluginB = new TestDataPlugin();
        setInteriorNode(dataPluginB, "./A/B");
		ServiceRegistration<DataPlugin> registrationB = registerDataPlugin(
				dataPluginB, "./A/B");

        TestDataPlugin dataPluginD = new TestDataPlugin();
        setInteriorNode(dataPluginD, "./C/D");
		ServiceRegistration<DataPlugin> registrationD = registerDataPlugin(
				dataPluginD, "./C/D");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        try {
            session.setNodeTitle("./A", "title");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        try {
            session.setNodeType("./A", "type");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        try {
            session.setDefaultNodeValue("./A");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        try {
            session.setNodeValue("./A", new DmtData("default"));
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        try {
            session.createInteriorNode("./A/X");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A/X");
        }
        try {
            session.createInteriorNode("./A/X", "type");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A/X");
        }
        try {
            session.createLeafNode("./A/X");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A/X");
        }
        try {
            session.createLeafNode("./A/X", new DmtData("default"));
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A/X");
        }
        try {
            session.createLeafNode("./A/X", new DmtData("default"), "type");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A/X");
        }
        try {
            session.deleteNode("./A");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        try {
            session.renameNode("./A", "X");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        try {
            session.copy("./A", "./B", false);
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        session.close();

        unregister(registrationB);
        unregister(registrationD);
    }

    public void testWriteCommandToScaffoldNodeIsNotAllowedOnAtomicLockType() throws DmtException {
        TestDataPlugin dataPluginB = new TestDataPlugin();
        setInteriorNode(dataPluginB, "./A/B");
		ServiceRegistration<DataPlugin> registrationB = registerDataPlugin(
				dataPluginB, "./A/B");

        TestDataPlugin dataPluginD = new TestDataPlugin();
        setInteriorNode(dataPluginD, "./C/D");
		ServiceRegistration<DataPlugin> registrationD = registerDataPlugin(
				dataPluginD, "./C/D");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_ATOMIC);
        try {
            session.setNodeTitle("./A", "title");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        try {
            session.setNodeType("./A", "type");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        try {
            session.setDefaultNodeValue("./A");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        try {
            session.setNodeValue("./A", new DmtData("default"));
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        try {
            session.createInteriorNode("./A/X");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A/X");
        }
        try {
            session.createInteriorNode("./A/X", "type");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A/X");
        }
        try {
            session.createLeafNode("./A/X");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A/X");
        }
        try {
            session.createLeafNode("./A/X", new DmtData("default"));
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A/X");
        }
        try {
            session.createLeafNode("./A/X", new DmtData("default"), "type");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A/X");
        }
        try {
            session.deleteNode("./A");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        try {
            session.renameNode("./A", "X");
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        try {
            session.copy("./A", "./B", false);
            fail("Expected to throw DmtException.");
        } catch (DmtException dmtException) {
            assertThrownDmtException(dmtException, DmtException.COMMAND_NOT_ALLOWED, "./A");
        }
        session.close();

        unregister(registrationB);
        unregister(registrationD);
    }

    public void testScaffoldNodeFromParentPluginToChildPlugin() throws Exception {
        TestDataMountPlugin plugin1 = new TestDataMountPlugin();
        setInteriorNode(plugin1, "./A");
		ServiceRegistration<DataPlugin> registration1 = registerMountDataPlugin(
				plugin1, "./A", new String[] {
						"B/C"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertInteriorNode("./A");
            assertNodeNotFound("./A/B");
            assertNodeNotFound("./A/B/C");
            assertNodeNotFound("./A/B/C/D");
            assertNodeNotFound("./A/B/C/D/E");
            assertNodeNotFound("./A/B/C/D/E/F");
        } finally {
            session.close();
        }

        TestDataMountPlugin plugin2 = new TestDataMountPlugin();
        setInteriorNode(plugin2, "./A/B/C");
		ServiceRegistration<DataPlugin> registration2 = registerMountDataPlugin(
				plugin2, "./A/B/C", new String[] {
						"D/E"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertInteriorNode("./A");
            assertScaffoldNode("./A/B");
            assertInteriorNode("./A/B/C");
            assertNodeNotFound("./A/B/C/D");
            assertNodeNotFound("./A/B/C/D/E");
            assertNodeNotFound("./A/B/C/D/E/F");
        } finally {
            session.close();
        }

        TestDataMountPlugin plugin3 = new TestDataMountPlugin();
        setInteriorNode(plugin3, "./A/B/C/D/E");
		ServiceRegistration<DataPlugin> registration3 = registerMountDataPlugin(
				plugin3, "./A/B/C/D/E");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertInteriorNode("./A");
            assertScaffoldNode("./A/B");
            assertInteriorNode("./A/B/C");
            assertScaffoldNode("./A/B/C/D");
            assertInteriorNode("./A/B/C/D/E");
            assertNodeNotFound("./A/B/C/D/E/F");
        } finally {
            session.close();
        }

        unregister(registration1);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertScaffoldNode("./A");
            assertScaffoldNode("./A/B");
            assertInteriorNode("./A/B/C");
            assertScaffoldNode("./A/B/C/D");
            assertInteriorNode("./A/B/C/D/E");
            assertNodeNotFound("./A/B/C/D/E/F");
        } finally {
            session.close();
        }

        unregister(registration2);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertScaffoldNode("./A");
            assertScaffoldNode("./A/B");
            assertScaffoldNode("./A/B/C");
            assertScaffoldNode("./A/B/C/D");
            assertInteriorNode("./A/B/C/D/E");
            assertNodeNotFound("./A/B/C/D/E/F");
        } finally {
            session.close();
        }

        unregister(registration3);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_SHARED);
        try {
            assertNodeNotFound("./A");
            assertNodeNotFound("./A/B");
            assertNodeNotFound("./A/B/C");
            assertNodeNotFound("./A/B/C/D");
            assertNodeNotFound("./A/B/C/D/E");
            assertNodeNotFound("./A/B/C/D/E/F");
        } finally {
            session.close();
        }
    }

    private void assertThrownDmtException(DmtException dmtException, int code, String uri) {
        assertEquals(code, dmtException.getCode());
        assertEquals(uri, dmtException.getURI());
        assertEquals(null, dmtException.getCause());
        assertEquals(0, dmtException.getCauses().length);
    }
}
