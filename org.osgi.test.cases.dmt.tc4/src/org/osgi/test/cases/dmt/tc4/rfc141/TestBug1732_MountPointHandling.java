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
package org.osgi.test.cases.dmt.tc4.rfc141;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.dmt.spi.MountPlugin;
import org.osgi.service.dmt.spi.MountPoint;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.GenericDataPlugin;
import org.osgi.test.cases.dmt.tc4.rfc141.plugins.Node;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

public class TestBug1732_MountPointHandling extends
		DefaultTestBundleControl {

	DmtAdmin dmtAdmin;
	DmtSession session;


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");
		dmtAdmin = getService(DmtAdmin.class);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("tearing down");
		closeSession();
		unregisterAllServices();
		ungetAllServices();
	}


	/**
	 * registers first a mounting and then a mounted plugin, opens a session and
	 * checks that the correct plugin was invoked
	 */
	public void testMountingPluginFirst() throws Exception {

		// register plugin for ./A with mountpoint ./A/B
		GenericDataPlugin mountingPlugin = prepareMountingPlugin(new String[] { "B" }, false);
		// register plugin for ./A/B with no mountPoints
		GenericDataPlugin mountedPlugin = prepareMountedPlugin1(false);


		// ensure that the mounted plugin has been mapped and serves the right
		// session
		assertPluginSessions(mountedPlugin.getRootUri(), mountedPlugin,
				mountingPlugin);

		// ensure that also the mounting plugin has been mapped and serves his
		// session
		assertPluginSessions(mountingPlugin.getRootUri(), mountingPlugin,
				mountedPlugin);
	}

	/**
	 * registers first a mounted and then a mounting plugin, opens a session and
	 * checks that the correct plugin was invoked
	 */
	public void testMountedPluginFirst() throws Exception {

		// register plugin for ./A/B with no mountPoints
		// should work, because there is no other plugin mounted above at this time
		GenericDataPlugin mountedPlugin = prepareMountedPlugin1(false);
		// register plugin for ./A with mountpoint ./A/B
		GenericDataPlugin mountingPlugin = prepareMountingPlugin(new String[] { "B" }, false);

		// ensure that the mounted plugin has been mapped and serves the right
		// session
		assertPluginSessions(mountedPlugin.getRootUri(), mountedPlugin,
				mountingPlugin);

		// ensure that also the mounting plugin has been mapped and serves his
		// session
		assertPluginSessions(mountingPlugin.getRootUri(), mountingPlugin,
				mountedPlugin);
	}

	/**
	 * checks that the callbacks are correctly invoked if a DataPlugin is registered also as MountPlugin
	 */
	public void testDataPluginCallback() throws Exception {

		assertCallbackInvocation( false );
	}

	/**
	 * checks that the callbacks are correctly invoked if an ExecPlugin is registered also as MountPlugin
	 */
	public void testExecPluginCallback() throws Exception {

		assertCallbackInvocation( true );
	}


	private void assertCallbackInvocation( boolean asExecPlugin ) {
		String mountRoot = "./A";
		Node n = new Node(null, "A", "node A");
		GenericDataPlugin plugin = new GenericDataPlugin("MountingPlugin", mountRoot, n);

		Dictionary<String,Object> props = new Hashtable<>();
		String[] uris = new String[] {mountRoot, "./XY" };
		String[] classes = null;
		if ( asExecPlugin ) {
			props.put(ExecPlugin.EXEC_ROOT_URIS, uris);
			classes = new String[] {ExecPlugin.class.getName(), MountPlugin.class.getName()};
		}
		else {
			props.put(DataPlugin.DATA_ROOT_URIS, uris);
			classes = new String[] {DataPlugin.class.getName(), MountPlugin.class.getName()};
		}

		ServiceRegistration< ? > reg = getContext().registerService(classes,
				plugin, props);

		try {
			assertNotNull(plugin.lastAddedMountPoints);
			assertEquals(2, plugin.lastAddedMountPoints.size());
			checkMountPointInList(mountRoot, plugin.lastAddedMountPoints );
			checkMountPointInList("./XY", plugin.lastAddedMountPoints );
		} catch (Exception e) {	}
		finally {
			reg.unregister();
		}

		assertNotNull(plugin.lastRemovedMountPoints);
		assertEquals(2, plugin.lastRemovedMountPoints.size());
		checkMountPointInList(mountRoot, plugin.lastRemovedMountPoints );
		checkMountPointInList("./XY", plugin.lastRemovedMountPoints );
	}


	private void checkMountPointInList(String uri,
			List<MountPoint> mountPoints) {
		boolean found = false;
		for (int i = 0; i < mountPoints.size(); i++) {
			if ( uri.equals(Uri.toUri(mountPoints.get(i).getMountPath()) ))
				found = true;
		}
		assertEquals( "Expected path: '" + uri + "' not found in provided mountPoints", true, found );
	}


	/**
	 * checks that a plugin is ignored, if it provides MountPoints but has more
	 * than one DataRootURI registered
	 *
	 * @throws Exception
	 */
	public void testPluginWithMoreDataRootURIs() throws Exception {
		// register the mounting plugin with an additional dataRootURI
		String additionalDataRootURI = "./X";
		GenericDataPlugin mountingPlugin = prepareMountingPlugin( new String[]{ "B" }, false, additionalDataRootURI );

		// check that for none of the dataRootURIs a session can be opened
		String uri = mountingPlugin.getRootUri();
		try {
			session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_SHARED);
			fail( "A plugin with MountPoints and more than one DataRootURI must be ignored.");
		} catch (DmtException e) {
			pass( "The plugin with MountPoints and more than one DataRootURI is correctly ignored.");
		}

		uri = additionalDataRootURI;
		try {
			session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_SHARED);
			fail( "A plugin with MountPoints and more than one DataRootURI must be ignored.");
		} catch (DmtException e) {
			pass( "The plugin with MountPoints and more than one DataRootURI is correctly ignored.");
		}
	}

// THIS testcase has become obsolete with bug-decision 1898!
// THERE must be no re-mapping of plugins, if conditions change.
// Mapping must only at registration time. If it fails, the plugin must be ignored.

//	/**
//	 * tests how plugins are mapped/un-mapped dynamically, when their mapping conditions appear/disappear
//	 */
//	public void testDataPluginDependencies() throws Exception {
//
//		GenericDataPlugin mountingPlugin = prepareMountingPlugin(new String[] { "B", "C/D/E" }, false);
//		GenericDataPlugin mountedPlugin1 = prepareMountedPlugin1(false);
//		GenericDataPlugin mountedPlugin2 = prepareMountedPlugin2(false);
//
//
//		assertPluginSessions(mountedPlugin1.getRootUri(), mountedPlugin1, mountingPlugin);
//		assertPluginSessions(mountingPlugin.getRootUri(), mountingPlugin, mountedPlugin1);
//		assertPluginSessions(mountedPlugin2.getRootUri(), mountedPlugin2, mountedPlugin1 );
//
//		// test that the mounted plugins "fall down", when the mounting plugin disappears
//		unregisterService(mountingPlugin);
//
//		assertMountedPluginUnmapped(mountedPlugin1.getRootUri());
//		assertMountedPluginUnmapped(mountedPlugin2.getRootUri());
//
//		// re-register the MountingPlugin --> both mountedPlugins should be re-mapped
//		mountingPlugin = prepareMountingPlugin(new String[] { "B", "C/D/E" }, false);
//
//		assertMountedPluginRemapped(mountedPlugin1.getRootUri());
//		assertMountedPluginRemapped(mountedPlugin2.getRootUri());
//	}

//	private void assertMountedPluginRemapped(String uri) throws DmtException {
//		try {
//			session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_SHARED);
//			pass( "The unmapped MountedPlugin was correctly mapped when it's MountingPlugin appeared");
//		} catch (DmtException e) {
//			fail( "The unmapped MountedPlugin was not mapped when it's mountPoint appeared");
//		}
//		finally {
//			closeSession();
//		}
//	}
//
//	private void assertMountedPluginUnmapped(String uri) throws DmtException {
//		try {
//			session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_SHARED);
//			fail( "The MountedPlugin must be unmapped when it's mountPoint disappears");
//		} catch (DmtException e) {
//			pass( "The MountedPlugin was correctly unmapped when it's MountingPlugin has disappeared");
//		}
//		finally {
//			closeSession();
//		}
//	}

//	/**
//	 * tests how plugins are mapped/un-mapped dynamically, when their mapping conditions appear/disappear
//	 */
//	public void testExecPluginDependencies() throws Exception {
//
//		GenericDataPlugin dataPlugin = prepareDataPluginForExecTests();
//		GenericDataPlugin mountedPlugin1 = prepareMountedPlugin1(true);
//		GenericDataPlugin mountedPlugin2 = prepareMountedPlugin2(true);
//
//		// open session on root of DataPlugin
//		session = dmtAdmin.getSession(dataPlugin.getRootUri(), DmtSession.LOCK_TYPE_EXCLUSIVE);
//		assertExecPluginIsMapped(session, mountedPlugin1.getRootUri() );
//		assertExecPluginIsMapped(session, mountedPlugin2.getRootUri() );
//
//		closeSession();
//		// test that the mounted plugins "fall down", when the mounting plugin disappears
//		unregisterService(dataPlugin);
//
//		assertExecPluginIsNotMapped(session, mountedPlugin1.getRootUri());
//		assertExecPluginIsNotMapped(session, mountedPlugin2.getRootUri());
//
//		// re-register the MountingPlugin --> both mountedPlugins should be re-mapped
//		dataPlugin = prepareDataPluginForExecTests();
//		// open session on root of DataPlugin
//		session = dmtAdmin.getSession(dataPlugin.getRootUri(), DmtSession.LOCK_TYPE_EXCLUSIVE);
//
//		assertExecPluginIsMapped(session, mountedPlugin1.getRootUri());
//		assertExecPluginIsMapped(session, mountedPlugin2.getRootUri());
//	}
//
//	private void assertExecPluginIsMapped(DmtSession session, String uri) throws DmtException {
//		try {
//			session.execute(uri, null);
//			pass( "The ExecPlugin is correctly mapped to: " + uri );
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail( "The ExecPlugin was expected to be mounted at: " + uri );
//		}
//	}
//
//	private void assertExecPluginIsNotMapped(DmtSession session, String uri) throws DmtException {
//		try {
//			session.execute(uri, null);
//			fail( "The ExecPlugin was expected to be unmapped.");
//		} catch (Exception e) {
//			pass( "The ExecPlugin is correctly not mapped");
//		}
//	}

	private void closeSession() throws DmtException {
		if ( session != null && DmtSession.STATE_OPEN == session.getState())
			session.close();
	}


	/**
	 */
	public void testExecPluginWithMountingPluginFirst() throws Exception {

		GenericDataPlugin dataPlugin = prepareDataPluginForExecTests();

		// now register a mounting and a mounted ExecPlugin
		GenericDataPlugin mountingPlugin = prepareMountingPlugin(new String[] { "B" }, true);
		GenericDataPlugin mountedPlugin = prepareMountedPlugin1(true);

		// open session on root of DataPlugin
		session = dmtAdmin.getSession(dataPlugin.getRootUri(), DmtSession.LOCK_TYPE_EXCLUSIVE);

		assertCorrectExecPluginInvoked(dataPlugin.getRootUri(), mountedPlugin, mountingPlugin);
	}

	/**
	 */
	public void testExecPluginWithMountedPluginFirst() throws Exception {

		GenericDataPlugin dataPlugin = prepareDataPluginForExecTests();

		// now register a mounting and a mounted ExecPlugin
		GenericDataPlugin mountedPlugin = prepareMountedPlugin1(true);
		GenericDataPlugin mountingPlugin = prepareMountingPlugin(new String[] { "B" }, true);

		// open session on root of DataPlugin
		session = dmtAdmin.getSession(dataPlugin.getRootUri(), DmtSession.LOCK_TYPE_EXCLUSIVE);

		assertCorrectExecPluginInvoked(dataPlugin.getRootUri(), mountedPlugin, mountingPlugin);
	}

	private void assertCorrectExecPluginInvoked(String dataPluginRoot,
			GenericDataPlugin mountedPlugin, GenericDataPlugin mountingPlugin)
			throws DmtException {

		assertNull(mountingPlugin.lastExecPath);
		assertNull(mountedPlugin.lastExecPath);

		// invoke execute on root of mounting ExecPlugin
		session.execute("./A", "should be invoked on mounting ExecPlugin" );
		assertNotNull( mountingPlugin.lastExecPath );
		assertNull( mountedPlugin.lastExecPath );
		assertEquals( Arrays.asList(Uri.toPath("./A")), Arrays.asList(mountingPlugin.lastExecPath) );

		mountedPlugin.resetStatus();
		mountingPlugin.resetStatus();

		session.execute("./A/B", "should be invoked on mounted ExecPlugin" );
		assertNotNull( mountedPlugin.lastExecPath );
		assertNull( mountingPlugin.lastExecPath );
		assertEquals( Arrays.asList(Uri.toPath("./A/B")), Arrays.asList(mountedPlugin.lastExecPath) );
	}


	private GenericDataPlugin prepareDataPluginForExecTests() throws Exception {
		// first preparing a DataPlugin that covers all nodes that are used by the execPlugins
		String dataPluginRoot = "./A";
		Node n = new Node(null, "A", "node A");
		new Node(n, "B", "node B");
		Node n3 = new Node(n, "C", "node C");
		Node n4 = new Node(n3, "D", "node D");
		new Node(n4, "E", "node E");
		GenericDataPlugin dataPlugin = new GenericDataPlugin("DataPluginForExecTests", dataPluginRoot, n);
		Dictionary<String,Object> props = new Hashtable<>();
		props.put(DataPlugin.DATA_ROOT_URIS, new String[] { dataPluginRoot });
		// If MOUNT_POINTS is specified, ./A/B and ./A/C/D/E will be out of scope of this plugin
		// so those nodes will not exist for the execPlugins. 
		//props.put(DataPlugin.MOUNT_POINTS, new String[] {"B", "C/D/E"});
		registerService(DataPlugin.class.getName(), dataPlugin, props);

		return dataPlugin;
	}


	/**
	 * asserts that a session has been opened on the "sessionPlugin", but not on
	 * the "noSessionPlugin"
	 *
	 * @param sessionPlugin
	 *            the plugin that should have served the session
	 * @param noSessionPlugin
	 *            the plugin that should not have served a session
	 * @throws DmtException
	 */
	private void assertPluginSessions(String uri,
			GenericDataPlugin sessionPlugin, GenericDataPlugin noSessionPlugin)
			throws DmtException {

		// reset status vars in both plugins
		sessionPlugin.resetStatus();
		noSessionPlugin.resetStatus();

		session = dmtAdmin.getSession(uri, DmtSession.LOCK_TYPE_SHARED);

		// check now that the session has been opened on the correct plugin
		assertEquals(null, noSessionPlugin.lastOpenedSession);
		assertEquals(uri, sessionPlugin.lastOpenedSession);

		session.isNodeUri(uri);

		assertEquals(GenericDataPlugin.ACTION_IS_NODE_URI,
				sessionPlugin.lastAction);
		assertEquals(sessionPlugin.lastUri, uri);

		closeSession();
	}



	/**
	 * a Plugin that mounts at "./A" and defines the given array of mount points,
	 * so that it should accept mounted plugins at these points
	 *
	 * @throws Exception
	 */
	private GenericDataPlugin prepareMountingPlugin(String[] mountPoints, boolean asExecPlugin, String additionalUri)
			throws Exception {
		String mountRoot = "./A";
		Node n = new Node(null, "A", "node A");
		GenericDataPlugin mountingPlugin = new GenericDataPlugin(
				"MountingPlugin", mountRoot, n);

		Dictionary<String,Object> props = new Hashtable<>();
		String[] uris = null;
		if ( additionalUri != null )
			uris = new String[] {mountRoot, additionalUri};
		else
			uris = new String[] {mountRoot};

		if ( asExecPlugin )
			props.put(ExecPlugin.EXEC_ROOT_URIS, uris);
		else
			props.put(DataPlugin.DATA_ROOT_URIS, uris);
		props.put(DataPlugin.MOUNT_POINTS, mountPoints);

		if ( asExecPlugin )
			registerService(ExecPlugin.class.getName(), mountingPlugin, props);
		else
			registerService(DataPlugin.class.getName(), mountingPlugin, props);
		Sleep.sleep(500);
		return mountingPlugin;
	}

	private GenericDataPlugin prepareMountingPlugin(String[] mountPoints, boolean asExecPlugin)
	throws Exception {
		return prepareMountingPlugin(mountPoints, asExecPlugin, null );
	}


	/**
	 * a Plugin that mounts at "./A/B" and defines no mount points
	 *
	 * @throws Exception
	 */
	private GenericDataPlugin prepareMountedPlugin1( boolean asExecPlugin ) throws Exception {
		String mountRoot = "./A/B";
		Node n = new Node(null, "B", "node B");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin(
				"MountedPlugin1", mountRoot, n);

		Dictionary<String,Object> props = new Hashtable<>();
		if ( asExecPlugin ) {
			props.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { mountRoot });
			registerService(ExecPlugin.class.getName(), mountedPlugin, props);			
		} else {
			props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
			registerService(DataPlugin.class.getName(), mountedPlugin, props);			
		}
		return mountedPlugin;
	}

	/**
	 * a Plugin that mounts at "./A/C/D/E" and defines no mount points
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private GenericDataPlugin prepareMountedPlugin2( boolean asExecPlugin ) throws Exception {
		String mountRoot = "./A/C/D/E";
		Node n = new Node(null, "E", "node E");
		GenericDataPlugin mountedPlugin = new GenericDataPlugin(
				"MountedPlugin2", mountRoot, n);

		Dictionary<String,Object> props = new Hashtable<>();
		if (asExecPlugin) {
			props.put(ExecPlugin.EXEC_ROOT_URIS, new String[] { mountRoot });
			registerService(ExecPlugin.class.getName(), mountedPlugin, props);
		} else {
			props.put(DataPlugin.DATA_ROOT_URIS, new String[] { mountRoot });
			registerService(DataPlugin.class.getName(), mountedPlugin, props);
		}

		return mountedPlugin;
	}
}
