
package org.osgi.test.cases.dmt.tc4.ext.junit;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtEventListener;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.dmt.spi.MountPlugin;
import org.osgi.service.dmt.spi.MountPoint;
import org.osgi.test.cases.dmt.tc4.ext.util.TestDataMountPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.TestDataPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.TestExecPlugin;
import org.osgi.test.cases.dmt.tc4.ext.util.TestNode;
import org.osgi.test.support.OSGiTestCase;

public abstract class DmtAdminTestCase extends OSGiTestCase {

	protected ServiceReference<DmtAdmin>	dmtAdminRef;

	protected DmtAdmin				dmtAdmin;

	protected DmtSession			session;

	protected LinkedList<ServiceRegistration< ? >>	registrationList		= new LinkedList<>();

	protected static BundleContext	context;

	private final Map<DmtEventListener,ServiceRegistration< ? >>	listenerRegistrationMap	= new HashMap<>();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getContext();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void getDmtAdmin() {
		dmtAdminRef = getContext().getServiceReference(DmtAdmin.class);
		dmtAdmin = getContext().getService(dmtAdminRef);
	}

	protected void closeDmtSession() throws DmtException {
		if ((session != null) && (session.getState() == DmtSession.STATE_OPEN)) {
			try {
				session.close();
			} catch (DmtIllegalStateException exception) {
				exception.printStackTrace();
			}
		}
	}

	protected void unregisterPlugins() {
		while (!registrationList.isEmpty()) {
			ServiceRegistration< ? > registration = registrationList
					.removeLast();
			registration.unregister();
		}
	}

	protected void assertNodeNotFound(String nodeUri) {
		try {
			session.getNodeTitle(nodeUri);
			fail();
		} catch (DmtException exception) {
			assertNodeNotFoundDmtException(nodeUri, exception);
		}
		try {
			session.getNodeType(nodeUri);
			fail();
		} catch (DmtException exception) {
			assertNodeNotFoundDmtException(nodeUri, exception);
		}
		try {
			session.getNodeValue(nodeUri);
			fail();
		} catch (DmtException exception) {
			assertNodeNotFoundDmtException(nodeUri, exception);
		}
	}

	protected void setInteriorNode(TestDataPlugin plugin, String uri) {
		plugin.setNode(uri, TestNode.newInteriorNode(uri + ":Title", uri + ":Type"));
	}

	protected void setInteriorNode(TestDataMountPlugin plugin, String uri) {
		plugin.setNode(uri, TestNode.newInteriorNode(uri + ":Title", uri + ":Type"));
	}

	protected void setInteriorNode(TestDataMountPlugin plugin, String[] path) {
		setInteriorNode(plugin, Uri.toUri(path));
	}

	protected void setLeafNode(TestDataPlugin plugin, String uri) {
		plugin.setNode(uri, TestNode.newLeafNode(uri + ":Title", uri + ":Type", new DmtData(uri + ":Value")));
	}

	protected void setLeafNode(TestDataMountPlugin plugin, String uri) {
		plugin.setNode(uri, TestNode.newLeafNode(uri + ":Title", uri + ":Type", new DmtData(uri + ":Value")));
	}

	protected void setLeafNode(TestDataMountPlugin plugin, String[] path) {
		setLeafNode(plugin, Uri.toUri(path));
	}

	protected void assertScaffoldNode(String uri) throws DmtException {
		assertEquals(null, session.getNodeTitle(uri));
		assertEquals(DmtConstants.DDF_SCAFFOLD, session.getNodeType(uri));

		try {
			assertEquals(null, session.getNodeValue(uri));
			fail();
		} catch (DmtException exception) {
			assertEquals(DmtException.COMMAND_NOT_ALLOWED, exception.getCode());
			assertEquals(uri, exception.getURI());
		}
	}

	protected void assertInteriorNode(String[] path) throws DmtException {
		assertInteriorNode(Uri.toUri(path));
	}

	protected void assertInteriorNode(String uri) throws DmtException {
		assertFalse(session.isLeafNode(uri));
		assertEquals(uri + ":Title", session.getNodeTitle(uri));
		assertEquals(uri + ":Type", session.getNodeType(uri));
		assertInteriorNodeNoValue(uri);
	}

	protected void assertInteriorNodeNoValue(String uri) {
		try {
			session.getNodeValue(uri);
			fail("There is an unexpected node value for: " + uri);
		} catch (DmtException dmtE) {
			assertEquals("FEATURE_NOT_SUPPORTED is expected.",
					DmtException.FEATURE_NOT_SUPPORTED, dmtE.getCode());
		}
	}

	protected void assertInteriorNode(String fromUri, String toUri) throws DmtException {
		assertFalse(session.isLeafNode(toUri));
		assertEquals(fromUri + ":Title", session.getNodeTitle(toUri));
		assertEquals(fromUri + ":Type", session.getNodeType(toUri));
		assertEquals(null, session.getNodeValue(toUri));
	}

	protected void assertLeafNode(String uri) throws DmtException {
		assertTrue(session.isLeafNode(uri));
		assertEquals(uri + ":Title", session.getNodeTitle(uri));
		assertEquals(uri + ":Type", session.getNodeType(uri));
		assertEquals(new DmtData(uri + ":Value"), session.getNodeValue(uri));
	}

	protected void assertLeafNode(String fromUri, String toUri) throws DmtException {
		assertTrue(session.isLeafNode(toUri));
		assertEquals(fromUri + ":Title", session.getNodeTitle(toUri));
		assertEquals(fromUri + ":Type", session.getNodeType(toUri));
		assertEquals(new DmtData(fromUri + ":Value"), session.getNodeValue(toUri));
	}

	protected void assertChildNodeNames(String uri, String[] expectedChildNodeNames) throws DmtException {
		assertTrue(Arrays.equals(expectedChildNodeNames, session.getChildNodeNames(uri)));
	}

	protected void assertNodeNotFound(String[] path) {
		assertNodeNotFound(Uri.toUri(path));
	}

	protected void assertNodeNotFoundDmtException(String nodeUri, DmtException exception) {
		assertEquals(DmtException.NODE_NOT_FOUND, exception.getCode());
		assertEquals(nodeUri, exception.getURI());
		assertEquals(null, exception.getCause());
		assertEquals(0, exception.getCauses().length);
	}

	protected static void assertMountPath(String expectedMountURI, MountPoint mountPoint) {
		assertEquals(expectedMountURI, Uri.toUri(mountPoint.getMountPath()));
	}

	protected static void assertMountPaths(String[] expectedMountURIs, MountPoint[] mountPoints) {
		String[] actualMountURIs = new String[mountPoints.length];
		for (int i = 0; i < mountPoints.length; i++) {
			actualMountURIs[i] = Uri.toUri(mountPoints[i].getMountPath());
		}
		assertTrue(Arrays.equals(expectedMountURIs, actualMountURIs));
	}

	protected int assertEnumratedPluginMountPoint(String[] mountPath, String parentNodeUri) {
		assertTrue(Uri.toUri(mountPath).startsWith(parentNodeUri));
		assertEquals(Uri.toPath(parentNodeUri).length + 1, mountPath.length);
		int lastIndex = mountPath.length - 1;
		return Integer.parseInt(mountPath[lastIndex]);
	}

	protected void assertExecute(TestExecPlugin plugin, String uri, String corrleator, String data) throws DmtException {
		session.execute(uri, corrleator, data);
		assertTrue(Arrays.equals(Uri.toPath(uri), plugin.getNodePath()));
		assertEquals(corrleator, plugin.getCorrelator());
		assertEquals(data, plugin.getData());
		assertTrue(plugin.wasExecuteCalled());
	}

	protected void assertExecuteFailed(TestExecPlugin plugin, String uri) throws DmtException {
		assertExecuteFailed(plugin, uri, DmtException.COMMAND_FAILED);
	}

	protected void assertExecuteFailed(TestExecPlugin plugin, String uri, int code) throws DmtException {
		try {
			session.execute(uri, "to be failed.");
			fail();
		} catch (DmtException dmtException) {
			assertEquals(code, dmtException.getCode());
			assertEquals(uri, dmtException.getURI());
			assertEquals(null, dmtException.getCause());
			assertEquals(0, dmtException.getCauses().length);
			assertFalse(plugin.wasExecuteCalled());
		}
	}

	protected ServiceRegistration<DataPlugin> registerDataPlugin(
			DataPlugin dataPlugin, String dataRootUri) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, dataRootUri);
		return registerDataPlugin(dataPlugin, serviceProps);
	}

	protected ServiceRegistration<DataPlugin> registerDataPlugin(
			DataPlugin dataPlugin, String[] dataRootUris) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, dataRootUris);
		return registerDataPlugin(dataPlugin, serviceProps);
	}

	protected ServiceRegistration<DataPlugin> registerDataPlugin(
			DataPlugin dataPlugin,
			Dictionary<String,Object> serviceProps) {
		ServiceRegistration<DataPlugin> registration = getContext()
				.registerService(DataPlugin.class, dataPlugin, serviceProps);
		registrationList.add(registration);
		return registration;
	}

	protected ServiceRegistration<DataPlugin> registerMountingDataPlugin(
			DataPlugin dataPlugin, String dataRootUri, String[] mountPoints) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, dataRootUri);
		serviceProps.put(DataPlugin.MOUNT_POINTS, mountPoints);
		return registerDataPlugin(dataPlugin, serviceProps);
	}

	protected ServiceRegistration<DataPlugin> registerMountDataPlugin(
			DataPlugin dataPlugin, String dataRootUri) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, dataRootUri);
		return registerMountDataPlugin(dataPlugin, serviceProps);
	}

	protected ServiceRegistration<DataPlugin> registerMountDataPlugin(
			DataPlugin dataPlugin, String[] dataRootUris) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, dataRootUris);
		return registerMountDataPlugin(dataPlugin, serviceProps);
	}

	protected ServiceRegistration<DataPlugin> registerMountDataPlugin(
			DataPlugin dataPlugin, String dataRootUri, String[] mountPoints) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, dataRootUri);
		serviceProps.put(DataPlugin.MOUNT_POINTS, mountPoints);
		return registerMountDataPlugin(dataPlugin, serviceProps);
	}

	protected ServiceRegistration<DataPlugin> registerMountDataPluginWithServicePIDs(
			DataPlugin dataPlugin, String dataRootUri, String[] servicePIDs) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(Constants.SERVICE_PID, servicePIDs);
		serviceProps.put(DataPlugin.DATA_ROOT_URIS, dataRootUri);
		return registerMountDataPlugin(dataPlugin, serviceProps);
	}

	protected ServiceRegistration<DataPlugin> registerMountDataPlugin(
			DataPlugin dataPlugin,
			Dictionary<String,Object> serviceProps) {
		ServiceRegistration<DataPlugin> registration = getContext()
				.registerService(DataPlugin.class, dataPlugin, serviceProps);
		registrationList.add(registration);
		return registration;
	}

	protected ServiceRegistration< ? > registerExecPlugin(ExecPlugin execPlugin,
			String dataRootUri) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, dataRootUri);
		return registerExecPlugin(execPlugin, serviceProps);
	}

	protected ServiceRegistration< ? > registerExecPlugin(ExecPlugin execPlugin,
			String[] execRootUris) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, execRootUris);
		return registerExecPlugin(execPlugin, execRootUris);
	}

	protected ServiceRegistration<ExecPlugin> registerExecPlugin(
			ExecPlugin execPlugin,
			Dictionary<String,Object> serviceProps) {
		ServiceRegistration<ExecPlugin> registration = getContext()
				.registerService(ExecPlugin.class, execPlugin, serviceProps);
		registrationList.add(registration);
		return registration;
	}

	protected ServiceRegistration<ExecPlugin> registerMountingExecPlugin(
			ExecPlugin execPlugin, String dataRootUri, String[] mountPoints) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, dataRootUri);
		serviceProps.put(ExecPlugin.MOUNT_POINTS, mountPoints);
		return registerExecPlugin(execPlugin, serviceProps);
	}

	protected ServiceRegistration< ? > registerMountExecPlugin(
			ExecPlugin execPlugin, String dataRootUri) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, dataRootUri);
		return registerMountExecPlugin(execPlugin, serviceProps);
	}

	protected ServiceRegistration< ? > registerMountExecPlugin(
			ExecPlugin execPlugin, String[] execRootUris) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, execRootUris);
		return registerMountExecPlugin(execPlugin, serviceProps);
	}

	protected ServiceRegistration< ? > registerMountExecPlugin(
			ExecPlugin execPlugin, String dataRootUri, String[] mountPoints) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, dataRootUri);
		serviceProps.put(ExecPlugin.MOUNT_POINTS, mountPoints);
		return registerMountExecPlugin(execPlugin, serviceProps);
	}

	protected ServiceRegistration< ? > registerMountExecPluginWithServicePIDs(
			ExecPlugin execPlugin, String execRootUris, String[] servicePIDs) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(Constants.SERVICE_PID, servicePIDs);
		serviceProps.put(ExecPlugin.EXEC_ROOT_URIS, execRootUris);
		return registerMountExecPlugin(execPlugin, serviceProps);
	}

	protected ServiceRegistration< ? > registerMountExecPlugin(
			ExecPlugin execPlugin,
			Dictionary<String,Object> serviceProps) {
		String[] objectClasses = new String[] {ExecPlugin.class.getName(), MountPlugin.class.getName()};
		ServiceRegistration< ? > registration = getContext()
				.registerService(objectClasses, execPlugin, serviceProps);
		registrationList.add(registration);
		return registration;
	}

	protected void unregister(ServiceRegistration< ? > registration) {
		registrationList.remove(registration);
		registration.unregister();
	}

	protected String getLastSegment(String[] path) {
		return path[path.length - 1];
	}

	protected void addDmtEventListener(String principal, int type, String uri, DmtEventListener listener) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DmtEventListener.FILTER_PRINCIPAL, new String[] {principal});
		serviceProps.put(DmtEventListener.FILTER_EVENT, Integer.valueOf(type));
		serviceProps.put(DmtEventListener.FILTER_SUBTREE, new String[] {uri});
		ServiceRegistration<DmtEventListener> registration = getContext()
				.registerService(DmtEventListener.class, listener,
						serviceProps);
		listenerRegistrationMap.put(listener, registration);
	}

	protected void addDmtEventListener(int type, String uri, DmtEventListener listener) {
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(DmtEventListener.FILTER_EVENT, Integer.valueOf(type));
		serviceProps.put(DmtEventListener.FILTER_SUBTREE, new String[] {uri});
		ServiceRegistration<DmtEventListener> registration = getContext()
				.registerService(DmtEventListener.class, listener,
						serviceProps);
		listenerRegistrationMap.put(listener, registration);
	}

	protected void removeDmtEventListener(DmtEventListener listener) {
		ServiceRegistration< ? > registration = listenerRegistrationMap
				.get(listener);
		if (registration != null) {
			registration.unregister();
		}
	}
}
