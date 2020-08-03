package org.osgi.test.cases.dmt.tc4.ext.junit;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.TestDataPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.TestExecPlugin;

public class ExecMountPluginTest extends DmtAdminTestCase {

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

    public void testExecutePluginIsNotOnDataPlugin1() throws DmtException {
        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        // see Bug 2266: isNodeUri() must return false for ExecPlugins scaffolds, i.e. their scaffolds are non-visible 
        assertExecuteFailed(pluginA, "./A1", DmtException.NODE_NOT_FOUND);
        assertExecuteFailed(pluginB, "./A1/B1", DmtException.NODE_NOT_FOUND);
        assertExecuteFailed(pluginB, "./A1/B1/C1", DmtException.NODE_NOT_FOUND);
        assertExecuteFailed(pluginB, "./A1/B1/C1/D1", DmtException.NODE_NOT_FOUND);
        session.close();

        unregister(registrationA);
        unregister(registrationB);
    }

    public void testExecutePluginIsNotOnDataPlugin2() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        registerMountDataPlugin(dataPlugin, "./A1", new String[] { "B1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        // see Bug 2266: isNodeUri() must return false for ExecPlugins scaffolds, i.e. their scaffolds are non-visible 
        assertExecuteFailed(pluginA, "./A1", DmtException.NODE_NOT_FOUND);
        assertExecuteFailed(pluginB, "./A1/B1", DmtException.NODE_NOT_FOUND);
        assertExecuteFailed(pluginB, "./A1/B1/C1", DmtException.NODE_NOT_FOUND);
        assertExecuteFailed(pluginB, "./A1/B1/C1/D1", DmtException.NODE_NOT_FOUND);
        session.close();

        unregister(registrationA);
        unregister(registrationB);
    }

    /**
     * Plugin should fail to be registered when execRootURI's value type is invalid.
     * 
     * @throws DmtException
     */
    public void testPluginIsNotMappedIfExecRootUrisContainsInvalidTypeObject() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setLeafNode(dataPlugin, "./A1");
        setLeafNode(dataPlugin, "./A2");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin execPlugin = new TestExecPlugin();
		Dictionary<String,Object> serviceProps = new Hashtable<>();
        serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, Integer.valueOf(0));

		ServiceRegistration<ExecPlugin> registration = null;
        try {
			registration = context.registerService(ExecPlugin.class, execPlugin,
					serviceProps);
            session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
            assertExecuteFailed(execPlugin, "./A1");
            assertExecuteFailed(execPlugin, "./A2", DmtException.NODE_NOT_FOUND);
            session.close();
        } finally {
            registration.unregister();
        }
    }

    /**
     * Plugin is correctly registered when mountPoints contains empty array.
     * 
     * @throws DmtException
     */
    public void testPluginIsMappedWhen1DataRootURIAnd0MountPointsAreSpecified() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setLeafNode(dataPlugin, "./A1");
        registerDataPlugin(dataPlugin, "./A1");

        TestExecPlugin execPlugin = new TestExecPlugin();
		Dictionary<String,Object> serviceProps = new Hashtable<>();
        serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1" });
        serviceProps.put(ExecPlugin.MOUNT_POINTS, new String[] {});

		ServiceRegistration<ExecPlugin> registration = null;
        try {
			registration = context.registerService(ExecPlugin.class, execPlugin,
					serviceProps);
            session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
            assertExecute(execPlugin, "./A1", "./A1:correlator", "./A1:data");
            session.close();
        } finally {
            registration.unregister();
        }
    }

    public void testPluginIsIgnoredWhen2ExecRootURIsAnd0MountPointsAreSpecified() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        registerDataPlugin(dataPlugin, new String[] { "./A1", "./A2" });
        setLeafNode(dataPlugin, "./A1");
        setLeafNode(dataPlugin, "./A2");

        TestExecPlugin execPlugin = new TestExecPlugin();
		Dictionary<String,Object> serviceProps = new Hashtable<>();
        serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1", "./A2" });
        serviceProps.put(ExecPlugin.MOUNT_POINTS, new String[] {});

		ServiceRegistration<ExecPlugin> registration = null;
        try {
			registration = context.registerService(ExecPlugin.class, execPlugin,
					serviceProps);
            session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
            assertExecute(execPlugin, "./A1", "./A1:correlator", "./A1:data");
            assertExecute(execPlugin, "./A2", "./A2:correlator", "./A2:data");
            session.close();
        } finally {
            registration.unregister();
        }
    }

    public void testPluginIsIgnoredWhen2ExecRootURIsAnd1MountPointsAreSpecified() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setLeafNode(dataPlugin, "./A1");
        setLeafNode(dataPlugin, "./A2");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin execPlugin = new TestExecPlugin();
		Dictionary<String,Object> serviceProps = new Hashtable<>();
        serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1", "./A2" });
        serviceProps.put(ExecPlugin.MOUNT_POINTS, new String[] { "B1" });

		ServiceRegistration<ExecPlugin> registration = null;
        try {
			registration = context.registerService(ExecPlugin.class, execPlugin,
					serviceProps);
            session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
            assertExecuteFailed(execPlugin, "./A1");
            assertExecuteFailed(execPlugin, "./A2", DmtException.NODE_NOT_FOUND);
            session.close();
        } finally {
            registration.unregister();
        }
    }

    public void testPluginIsIgnoredWhenMountPointsContainsNull() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setLeafNode(dataPlugin, "./A1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin execPlugin = new TestExecPlugin();
		Dictionary<String,Object> serviceProps = new Hashtable<>();
        serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1" });
        serviceProps.put(ExecPlugin.MOUNT_POINTS, new String[] { "B1", null });

		ServiceRegistration<ExecPlugin> registration = null;
        try {
			registration = context.registerService(ExecPlugin.class, execPlugin,
					serviceProps);
            session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
            assertExecuteFailed(execPlugin, "./A1");
            session.close();
        } finally {
            registration.unregister();
        }
    }

    public void testPluginIsIgnoredWhenMountPointsContainsInvalidURI() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setLeafNode(dataPlugin, "./A1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin execPlugin = new TestExecPlugin();

		Dictionary<String,Object> serviceProps = new Hashtable<>();
        serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1" });
        serviceProps.put(ExecPlugin.MOUNT_POINTS, new String[] { "B1", "/" });
		ServiceRegistration<ExecPlugin> registration = null;
        try {
			registration = context.registerService(ExecPlugin.class, execPlugin,
					serviceProps);
            session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
            assertExecuteFailed(execPlugin, "./A1");
            assertExecuteFailed(execPlugin, "./A1/B1", DmtException.NODE_NOT_FOUND);
            session.close();
        } finally {
            registration.unregister();
        }
    }

    public void testPluginIsIgnoredWhenMountPointsContainsAbsoluteURI() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setLeafNode(dataPlugin, "./A1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin execPlugin = new TestExecPlugin();
		Dictionary<String,Object> serviceProps = new Hashtable<>();
        serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1" });
        serviceProps.put(ExecPlugin.MOUNT_POINTS, new String[] { "B1", "./B2" });
		ServiceRegistration<ExecPlugin> registration = null;
        try {
			registration = context.registerService(ExecPlugin.class, execPlugin,
					serviceProps);
            session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
            assertExecuteFailed(execPlugin, "./A1");
            assertExecuteFailed(execPlugin, "./A1/B1", DmtException.NODE_NOT_FOUND);
            assertExecuteFailed(execPlugin, "./A1/B2", DmtException.NODE_NOT_FOUND);
            session.close();
        } finally {
            registration.unregister();
        }
    }

    public void testPluginIsIgnoredWhenMountPointsContainsOverlappedRelativeURIs() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setLeafNode(dataPlugin, "./A1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin execPlugin = new TestExecPlugin();
		Dictionary<String,Object> serviceProps = new Hashtable<>();
        serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1" });
        serviceProps.put(ExecPlugin.MOUNT_POINTS, new String[] { "B1", "B2", "B1/C1" });
		ServiceRegistration<ExecPlugin> registration = null;
        try {
			registration = context.registerService(ExecPlugin.class, execPlugin,
					serviceProps);
            session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
            assertExecuteFailed(execPlugin, "./A1/B1", DmtException.NODE_NOT_FOUND);
            assertExecuteFailed(execPlugin, "./A1/B2", DmtException.NODE_NOT_FOUND);
            assertExecuteFailed(execPlugin, "./A1/B1/C1", DmtException.NODE_NOT_FOUND);
            session.close();
        } finally {
            registration.unregister();
        }
    }

    public void test1MountPoint0MatchedPlugin0UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        session.close();

        unregister(registrationA);
    }

    public void testMountingPlugin0MountPoints() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration< ? > registrationA = registerExecPlugin(
				pluginA, "./A1");
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration< ? > registrationB = registerExecPlugin(
				pluginB, "./A1/B1");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginA, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB);
    }

    public void testMountingPlugin1MountPointsMathced() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration< ? > registrationB = registerExecPlugin(
				pluginB, "./A1/B1");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB);
    }

    public void testMountingPlugin1MountPointsUnMathced() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration< ? > registrationB = registerExecPlugin(
				pluginB, "./A1/B2");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecute(pluginA, "./A1/B2", "./A1/B2:corrleator", "./A1/B2:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB);
    }

    public void testMountingPlugin1MountPointsMathcedDeepPath() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1/C1"
				});
        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        unregister(registrationC);
    }

    public void testMountingPlugin2MountPoints1stMathced() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration< ? > registrationB = registerExecPlugin(
				pluginB, "./A1/B1");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecuteFailed(pluginA, "./A1/B2", DmtException.NODE_NOT_FOUND);
        session.close();

        unregister(registrationA);
        unregister(registrationB);
    }

    public void testMountingPlugin2MountPoints2ndMathced() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration< ? > registrationB = registerExecPlugin(
				pluginB, "./A1/B2");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecuteFailed(pluginA, "./A1/B1");
        assertExecute(pluginB, "./A1/B2", "./A1/B2:corrleator", "./A1/B2:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB);
    }

    public void testMountingPlugin2MountPoints2Mathced() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});
        TestExecPlugin pluginB1 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB1 = registerExecPlugin(
				pluginB1, "./A1/B1");
        TestExecPlugin pluginB2 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB2 = registerExecPlugin(
				pluginB2, "./A1/B2");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB1, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginB2, "./A1/B2", "./A1/B2:corrleator", "./A1/B2:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB1);
        unregister(registrationB2);
    }

    public void testMountingPlugin2MountPointsUnmatched() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        setInteriorNode(dataPlugin, "./A1/B3");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});
        TestExecPlugin pluginB3 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB3 = registerExecPlugin(
				pluginB3, "./A1/B3");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecuteFailed(pluginA, "./A1/B1");
        assertExecuteFailed(pluginA, "./A1/B2");
        assertExecute(pluginA, "./A1/B3", "./A1/B3:corrleator", "./A1/B3:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB3);
    }

    public void testRemountToUnmounedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        setInteriorNode(dataPlugin, "./A1/B1/C1/D1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});
        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationC = registerMountingExecPlugin(
				pluginC, "./A1/B1/C1", new String[] {
						"D1"
				});

        unregister(registrationB);

        TestExecPlugin pluginD = new TestExecPlugin();
		ServiceRegistration< ? > registrationD = registerExecPlugin(
				pluginD, "./A1/B1/C1/D1");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecuteFailed(pluginC, "./A1/B1/C1");
        assertExecuteFailed(pluginC, "./A1/B1/C1/D1");
        session.close();

        unregister(registrationA);
        unregister(registrationC);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecuteFailed(pluginC, "./A1/B1/C1");
        assertExecute(pluginD, "./A1/B1/C1/D1", "./A1/B1/C1/D1:corrleator", "./A1/B1/C1/D1:data");
        session.close();

        unregister(registrationD);
    }

    public void test1MountPoint0MatchedPlugin1UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginB2 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB2 = registerExecPlugin(
				pluginB2, "./A1/B2");
        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        session.close();

        unregister(registrationA);
        unregister(registrationB2);
    }

    public void test1MountPoint1MatchedPlugin0UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginB1 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB1 = registerExecPlugin(
				pluginB1, "./A1/B1");
        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB1, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB1);
    }

    public void test1MountPoint1MatchedPlugin1UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginB1 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB1 = registerExecPlugin(
				pluginB1, "./A1/B1");
        TestExecPlugin pluginB2 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB2 = registerExecPlugin(
				pluginB2, "./A1/B2");
        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecute(pluginB1, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginB2, "./A1/B2", "./A1/B2:corrleator", "./A1/B2:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB1);
        unregister(registrationB2);
    }

    public void test2MountPoints0MatchedPlugin0UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        setInteriorNode(dataPlugin, "./A1/B3");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        session.close();

        unregister(registrationA);
    }

    public void test2MountPoints0MatchedPlugin1UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        setInteriorNode(dataPlugin, "./A1/B3");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginB3 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB3 = registerExecPlugin(
				pluginB3, "./A1/B3");
        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecute(pluginB3, "./A1/B3", "./A1/B3:corrleator", "./A1/B3:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB3);
    }

    public void test2MountPoints1MatchedPlugin0UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        setInteriorNode(dataPlugin, "./A1/B3");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginB1 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB1 = registerExecPlugin(
				pluginB1, "./A1/B1");
        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB1, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB1);
    }

    public void test2MountPoints1MatchedPlugin1UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        setInteriorNode(dataPlugin, "./A1/B3");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginB1 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB1 = registerExecPlugin(
				pluginB1, "./A1/B1");
        TestExecPlugin pluginB3 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB3 = registerExecPlugin(
				pluginB3, "./A1/B3");
        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecute(pluginB1, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginB3, "./A1/B3", "./A1/B3:corrleator", "./A1/B3:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB1);
        unregister(registrationB3);
    }

    public void test2MountPoints2MatchedPlugin0UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        setInteriorNode(dataPlugin, "./A1/B3");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginB1 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB1 = registerExecPlugin(
				pluginB1, "./A1/B1");
        TestExecPlugin pluginB2 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB2 = registerExecPlugin(
				pluginB2, "./A1/B2");
        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB1, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginB2, "./A1/B2", "./A1/B2:corrleator", "./A1/B2:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB1);
        unregister(registrationB2);
    }

    public void test2MountPoints2MatchedPlugin1UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        setInteriorNode(dataPlugin, "./A1/B3");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginB1 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB1 = registerExecPlugin(
				pluginB1, "./A1/B1");
        TestExecPlugin pluginB2 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB2 = registerExecPlugin(
				pluginB2, "./A1/B2");
        TestExecPlugin pluginB3 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB3 = registerExecPlugin(
				pluginB3, "./A1/B3");
        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecute(pluginB1, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginB2, "./A1/B2", "./A1/B2:corrleator", "./A1/B2:data");
        assertExecute(pluginB3, "./A1/B3", "./A1/B3:corrleator", "./A1/B3:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB1);
        unregister(registrationB2);
        unregister(registrationB3);
    }

    public void test2MountPoints2MatchedPlugin1UnmachedPluginDepthPath() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B2");
        setInteriorNode(dataPlugin, "./A1/B3/C3");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginB1 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB1 = registerExecPlugin(
				pluginB1, "./A1/B1");
        TestExecPlugin pluginB2 = new TestExecPlugin();
		ServiceRegistration< ? > registrationB2 = registerExecPlugin(
				pluginB2, "./A1/B2");
        TestExecPlugin pluginC3 = new TestExecPlugin();
		ServiceRegistration< ? > registrationC3 = registerExecPlugin(
				pluginC3, "./A1/B3/C3");
        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1", "B2"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecute(pluginB1, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginB2, "./A1/B2", "./A1/B2:corrleator", "./A1/B2:data");
        assertExecute(pluginC3, "./A1/B3/C3", "./A1/B3/C3:corrleator", "./A1/B3/C3:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB1);
        unregister(registrationB2);
        unregister(registrationC3);
    }

    public void testRemount0MountPoints0MatchedPlugin1UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin oldPluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> oldRegistrationB = registerMountingExecPlugin(
				oldPluginB, "./A1/B1", new String[] {
						"C1"
				});
        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");

        unregister(oldRegistrationB);

        TestExecPlugin newPluginB = new TestExecPlugin();
		ServiceRegistration< ? > newRegistrationB = registerExecPlugin(
				newPluginB, "./A1/B1");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(newPluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(newPluginB, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        unregister(newRegistrationB);
        unregister(registrationC);
    }

    public void testRemount1MountPoints1MatchedPlugin0UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin oldPluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> oldRegistrationB = registerMountingExecPlugin(
				oldPluginB, "./A1/B1", new String[] {
						"C1"
				});
        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");

        unregister(oldRegistrationB);

        TestExecPlugin newPluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> newRegistrationB = registerMountingExecPlugin(
				newPluginB, "./A1/B1", new String[] {
						"C1"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(newPluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        unregister(newRegistrationB);
        unregister(registrationC);
    }

    public void testRemount1MountPoints1MatchedPlugin0UnmachedPluginDeepPath() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        setInteriorNode(dataPlugin, "./A1/B1/C1/D1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin oldPluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> oldRegistrationB = registerMountingExecPlugin(
				oldPluginB, "./A1/B1", new String[] {
						"C1/D1"
				});
        TestExecPlugin pluginD = new TestExecPlugin();
		ServiceRegistration< ? > registrationD = registerExecPlugin(
				pluginD, "./A1/B1/C1/D1");

        unregister(oldRegistrationB);

        TestExecPlugin newPluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> newRegistrationB = registerMountingExecPlugin(
				newPluginB, "./A1/B1", new String[] {
						"C1/D1"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(newPluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(newPluginB, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        assertExecute(pluginD, "./A1/B1/C1/D1", "./A1/B1/C1/D1:corrleator", "./A1/B1/C1/D1:data");
        session.close();

        unregister(registrationA);
        unregister(newRegistrationB);
        unregister(registrationD);
    }

    public void testRemount1MountPoints0MatchedPlugin1UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin oldPluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> oldRegistrationB = registerMountingExecPlugin(
				oldPluginB, "./A1/B1", new String[] {
						"C1"
				});
        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");

        unregister(oldRegistrationB);

        TestExecPlugin newPluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> newRegistrationB = registerMountingExecPlugin(
				newPluginB, "./A1/B1", new String[] {
						"C2"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(newPluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(newPluginB, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        unregister(newRegistrationB);
        unregister(registrationC);
    }

    public void testRemount2MountPoints1MatchedPlugin0UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        setInteriorNode(dataPlugin, "./A1/B1/C2");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin oldPluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> oldRegistrationB = registerMountingExecPlugin(
				oldPluginB, "./A1/B1", new String[] {
						"C1"
				});
        TestExecPlugin pluginC1 = new TestExecPlugin();
		ServiceRegistration< ? > registrationC1 = registerExecPlugin(
				pluginC1, "./A1/B1/C1");

        unregister(oldRegistrationB);

        TestExecPlugin newPluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> newRegistrationB = registerMountingExecPlugin(
				newPluginB, "./A1/B1", new String[] {
						"C1", "C2"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(newPluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC1, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        TestExecPlugin pluginC2 = new TestExecPlugin();
		ServiceRegistration< ? > registrationC2 = registerExecPlugin(
				pluginC2, "./A1/B1/C2");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(newPluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC1, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        assertExecute(pluginC2, "./A1/B1/C2", "./A1/B1/C2:corrleator", "./A1/B1/C2:data");
        session.close();

        unregister(registrationA);
        unregister(newRegistrationB);
        unregister(registrationC1);
        unregister(registrationC2);
    }

    public void testRemount2MountPoints0MatchedPlugin2UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin oldPluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> oldRegistrationB = registerMountingExecPlugin(
				oldPluginB, "./A1/B1", new String[] {
						"C1"
				});
        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");

        unregister(oldRegistrationB);

        TestExecPlugin newPluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> newRegistrationB = registerMountingExecPlugin(
				newPluginB, "./A1/B1", new String[] {
						"C2", "C3"
				});

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(newPluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(newPluginB, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        unregister(newRegistrationB);
        unregister(registrationC);
    }

    public void test2PluginsRegisterUnregisterOrderPattern01() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        registerDataPlugin(dataPlugin, new String[] { "./A1" });
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");

        TestExecPlugin mountingPlugin = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> mountingPluginRegistration = registerMountingExecPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(mountingPlugin, "./A1", "./A1:corrleator", "./A1:data");
        session.close();

        TestExecPlugin mountedPlugin = new TestExecPlugin();
		ServiceRegistration< ? > mountedPluginRegistration = registerExecPlugin(
				mountedPlugin, "./A1/B1");
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(mountingPlugin, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(mountedPlugin, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        unregister(mountingPluginRegistration);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(mountingPlugin, "./A1");
        assertExecute(mountedPlugin, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        unregister(mountedPluginRegistration);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(mountingPlugin, "./A1");
        assertExecuteFailed(mountedPlugin, "./A1/B1");
        session.close();
    }

    public void test2PluginsRegisterUnregisterOrderPattern02() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        registerDataPlugin(dataPlugin, new String[] { "./A1" });
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");

        TestExecPlugin mountingPlugin = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> mountingPluginRegistration = registerMountingExecPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(mountingPlugin, "./A1", "./A1:corrleator", "./A1:data");
        assertExecuteFailed(mountingPlugin, "./A1/B1");
        session.close();

        TestExecPlugin mountedPlugin = new TestExecPlugin();
		ServiceRegistration< ? > mountedPluginRegistration = registerExecPlugin(
				mountedPlugin, "./A1/B1");
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(mountingPlugin, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(mountedPlugin, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        unregister(mountedPluginRegistration);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(mountingPlugin, "./A1", "./A1:corrleator", "./A1:data");
        assertExecuteFailed(mountedPlugin, "./A1/B1");
        session.close();

        unregister(mountingPluginRegistration);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(mountingPlugin, "./A1");
        assertExecuteFailed(mountedPlugin, "./A1/B1");
        session.close();
    }

    public void test2PluginsRegisterUnregisterOrderPattern03() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        registerDataPlugin(dataPlugin, new String[] { "./A1" });
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");

        TestExecPlugin mountedPlugin = new TestExecPlugin();
		ServiceRegistration< ? > mountedPluginRegistration = registerExecPlugin(
				mountedPlugin, "./A1/B1");
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(mountedPlugin, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        TestExecPlugin mountingPlugin = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> mountingPluginRegistration = registerMountingExecPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(mountingPlugin, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(mountedPlugin, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        unregister(mountingPluginRegistration);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(mountingPlugin, "./A1");
        assertExecute(mountedPlugin, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        unregister(mountedPluginRegistration);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(mountingPlugin, "./A1");
        assertExecuteFailed(mountedPlugin, "./A1/B1");
        session.close();
    }

    public void test2PluginsRegisterUnregisterOrderPattern04() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        registerDataPlugin(dataPlugin, new String[] { "./A1" });
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");

        TestExecPlugin mountedPlugin = new TestExecPlugin();
		ServiceRegistration< ? > mountedPluginRegistration = registerExecPlugin(
				mountedPlugin, "./A1/B1");
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(mountedPlugin, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        TestExecPlugin mountingPlugin = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> mountingPluginRegistration = registerMountingExecPlugin(
				mountingPlugin, "./A1", new String[] {
						"B1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(mountingPlugin, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(mountedPlugin, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        unregister(mountedPluginRegistration);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(mountingPlugin, "./A1", "./A1:corrleator", "./A1:data");
        assertExecuteFailed(mountedPlugin, "./A1/B1");
        session.close();

        unregister(mountingPluginRegistration);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(mountingPlugin, "./A1");
        assertExecuteFailed(mountedPlugin, "./A1/B1");
        session.close();
    }

    public void test3PluginsRegisterUnregisterOrderPattern01() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        registerDataPlugin(dataPlugin, new String[] { "./A1" });
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        session.close();

        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationB);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationC);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecuteFailed(pluginC, "./A1/B1/C1");
        session.close();
    }

    public void test3PluginsRegisterUnregisterOrderPattern02() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        registerDataPlugin(dataPlugin, new String[] { "./A1" });
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        session.close();

        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecuteFailed(pluginC, "./A1/B1/C1");
        session.close();

        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationB);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationC);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecuteFailed(pluginC, "./A1/B1/C1");
        session.close();
    }

    public void test3PluginsRegisterUnregisterOrderPattern03() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        registerDataPlugin(dataPlugin, new String[] { "./A1" });
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");

        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationB);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationC);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecuteFailed(pluginC, "./A1/B1/C1");
        session.close();
    }

    public void test3PluginsRegisterUnregisterOrderPattern04() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        registerDataPlugin(dataPlugin, new String[] { "./A1" });
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");

        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        session.close();

        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationB);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationC);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecuteFailed(pluginC, "./A1/B1/C1");
        session.close();
    }

    public void test3PluginsRegisterUnregisterOrderPattern05() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        registerDataPlugin(dataPlugin, new String[] { "./A1" });
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");

        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationB);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationC);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecuteFailed(pluginC, "./A1/B1/C1");
        session.close();
    }

    public void test3PluginsRegisterUnregisterOrderPattern06() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        registerDataPlugin(dataPlugin, new String[] { "./A1" });
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");

        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationB);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationC);
        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecuteFailed(pluginA, "./A1");
        assertExecuteFailed(pluginB, "./A1/B1");
        assertExecuteFailed(pluginC, "./A1/B1/C1");
        session.close();
    }

    public void testModified0MountPoints0MatchedPlugin1UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});
        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");

		Dictionary<String,Object> modifiedServiceProps = new Hashtable<>();
        modifiedServiceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1/B1" });
        modifiedServiceProps.put(ExecPlugin.MOUNT_POINTS, new String[] { "C1", "C2" });
        registrationB.setProperties(modifiedServiceProps);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB);
        unregister(registrationC);
    }

    public void testModified1MountPoints1MatchedPlugin0UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});
        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");

		Dictionary<String,Object> modifiedServiceProps = new Hashtable<>();
        modifiedServiceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1/B1" });
        modifiedServiceProps.put(ExecPlugin.MOUNT_POINTS, new String[] { "C1" });
        registrationB.setProperties(modifiedServiceProps);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB);
        unregister(registrationC);
    }

    public void testModified1MountPoints1MatchedPlugin0UnmachedPluginDeepPath() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        setInteriorNode(dataPlugin, "./A1/B1/C1/D1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1/D1"
				});
        TestExecPlugin pluginD = new TestExecPlugin();
		ServiceRegistration< ? > registrationD = registerExecPlugin(
				pluginD, "./A1/B1/C1/D1");

		Dictionary<String,Object> modifiedServiceProps = new Hashtable<>();
        modifiedServiceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1/B1" });
        modifiedServiceProps.put(ExecPlugin.MOUNT_POINTS, new String[] { "C1/D1" });
        registrationB.setProperties(modifiedServiceProps);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginB, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        assertExecute(pluginD, "./A1/B1/C1/D1", "./A1/B1/C1/D1:corrleator", "./A1/B1/C1/D1:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB);
        unregister(registrationD);
    }

    public void testModified1MountPoints0MatchedPlugin1UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});
        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");

		Dictionary<String,Object> modifiedServiceProps = new Hashtable<>();
        modifiedServiceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1/B1" });
        modifiedServiceProps.put(ExecPlugin.MOUNT_POINTS, new String[] { "C2" });
        registrationB.setProperties(modifiedServiceProps);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginB, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB);
        unregister(registrationC);
    }

    public void testModified2MountPoints1MatchedPlugin0UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        setInteriorNode(dataPlugin, "./A1/B1/C2");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});
        TestExecPlugin pluginC1 = new TestExecPlugin();
		ServiceRegistration< ? > registrationC1 = registerExecPlugin(
				pluginC1, "./A1/B1/C1");

		Dictionary<String,Object> modifiedServiceProps = new Hashtable<>();
        modifiedServiceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1/B1" });
        modifiedServiceProps.put(ExecPlugin.MOUNT_POINTS, new String[] { "C1", "C2" });
        registrationB.setProperties(modifiedServiceProps);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC1, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        TestExecPlugin pluginC2 = new TestExecPlugin();
		ServiceRegistration< ? > registrationC2 = registerExecPlugin(
				pluginC2, "./A1/B1/C2");

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginC1, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        assertExecute(pluginC2, "./A1/B1/C2", "./A1/B1/C2:corrleator", "./A1/B1/C2:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB);
        unregister(registrationC1);
        unregister(registrationC2);
    }

    public void testModified2MountPoints0MatchedPlugin2UnmachedPlugin() throws DmtException {
        TestDataPlugin dataPlugin = new TestDataPlugin();
        setInteriorNode(dataPlugin, "./A1");
        setInteriorNode(dataPlugin, "./A1/B1");
        setInteriorNode(dataPlugin, "./A1/B1/C1");
        registerDataPlugin(dataPlugin, new String[] { "./A1" });

        TestExecPlugin pluginA = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationA = registerMountingExecPlugin(
				pluginA, "./A1", new String[] {
						"B1"
				});
        TestExecPlugin pluginB = new TestExecPlugin();
		ServiceRegistration<ExecPlugin> registrationB = registerMountingExecPlugin(
				pluginB, "./A1/B1", new String[] {
						"C1"
				});
        TestExecPlugin pluginC = new TestExecPlugin();
		ServiceRegistration< ? > registrationC = registerExecPlugin(
				pluginC, "./A1/B1/C1");

		Dictionary<String,Object> modifiedServiceProps = new Hashtable<>();
        modifiedServiceProps.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { "./A1/B1" });
        modifiedServiceProps.put(ExecPlugin.MOUNT_POINTS, new String[] { "C2", "C3" });
        registrationB.setProperties(modifiedServiceProps);

        session = dmtAdmin.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
        assertExecute(pluginA, "./A1", "./A1:corrleator", "./A1:data");
        assertExecute(pluginB, "./A1/B1", "./A1/B1:corrleator", "./A1/B1:data");
        assertExecute(pluginB, "./A1/B1/C1", "./A1/B1/C1:corrleator", "./A1/B1/C1:data");
        session.close();

        unregister(registrationA);
        unregister(registrationB);
        unregister(registrationC);
    }
}
