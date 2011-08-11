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
package org.osgi.impl.service.residentialmanagement.plugins;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.Date;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.startlevel.StartLevel;

import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.ReadableDataSession;

/**
 * 
 * @author Koya MORI NTT Corporation, Shigekuni KONDO
 */
class FrameworkReadOnlySession implements ReadableDataSession, SynchronousBundleListener, FrameworkListener {
	
	protected static final String ID = "id";
	protected static final String STARTLEVEL = "StartLevel";
	protected static final String INSTALLBUNDLE = "InstallBundle";
	protected static final String FRAMEWORKLIFECYCLE = "FrameworkLifecycle";
	protected static final String BUNDLECONTROL = "BundleControl";
	protected static final String FRAMEWORKEVENT = "FrameworkEvent";
	protected static final String REFRESHPACKAGES = "RefreshPackages";
	
	protected static final String EVENT = "Event";
	protected static final String CATCHEVENTS = "CatchEvents";
	protected static final String THROWABLE = "Throwable";
	protected static final String TYPE = "Type";
	protected static final String BUNDLEID = "BundleId";

	protected static final String REQUESTEDSTARTLEVEL = "RequestedStartLevel";
	protected static final String ACTIVESTARTLEVEL = "ActiveStartLevel";
	protected static final String INITIALBUNDLESTARTLEVEL = "InitialBundleStartLevel";
	protected static final String BEGINNINGSTARTLEVEL = "BeginningStartLevel";
	
	protected static final String LOCATION = "Location";
	protected static final String URL = "URL";
	protected static final String INSTALLBUNDLEOPTION = "Option";
	protected static final String INSTALLBUNDLEOPERATIONRESULT = "OperationResult";
	
	protected static final String RESTART = "Restart";
	protected static final String SHUTDOWN = "Shutdown";
	protected static final String UPDATE = "Update";
	
	protected static final String LIFECYCLE = "Lifecycle";
	protected static final String BUNDLESTARTLEVEL = "BundleStartLevl";
	protected static final String BUNDLECONTROLREFRESHPACKAGES = "RefreshPackages";
	
	protected static final String OPERATION = "Operation";
	protected static final String BUNDLECONTROLOPTION = "Option";
	protected static final String BUNDLECONTROLOPERATIONRESULT = "OperationResult";
	
	protected static final String NODE_TYPE = "org.osgi/1.0/FrameworkManagementObject";

	protected FrameworkPlugin plugin;
	protected BundleContext context;
	protected Hashtable bundlesTable = new Hashtable();
	protected Hashtable eventTable = new Hashtable();
	protected int requestedStartLevel = 0;
	protected Node installbundle;
	protected boolean managedFlag = false;
	protected long eventId = 1;
	protected boolean eventFlag = false;
	protected boolean refreshPackagesFlag = false;

	FrameworkReadOnlySession(FrameworkPlugin plugin, BundleContext context) {
		this.plugin = plugin;
		this.context = context;
		installbundle = new Node("InstallBundle", null);
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and timestamp properties are not supported
	}

	public void close() throws DmtException {
		// no cleanup needs to be done when closing read-only session
	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) {
			String[] children = new String[6];
			children[0] = STARTLEVEL;
			children[1] = INSTALLBUNDLE;
			children[2] = FRAMEWORKLIFECYCLE;
			children[3] = BUNDLECONTROL;
			children[4] = REFRESHPACKAGES;
			children[5] = FRAMEWORKEVENT;
			return children;
		}

		if (path.length == 2) {
			if (path[1].equals(STARTLEVEL)) {
				String[] children = new String[4];
				children[0] = REQUESTEDSTARTLEVEL;
				children[1] = ACTIVESTARTLEVEL;
				children[2] = INITIALBUNDLESTARTLEVEL;
				children[3] = BEGINNINGSTARTLEVEL;
				return children;
			}

			if (path[1].equals(INSTALLBUNDLE)) {
				Node[] ids = installbundle.getChildren();
				String[] children = new String[ids.length];

				for (int i = 0; i < ids.length; i++) {
					children[i] = ids[i].getName();
				}
				return children;
			}

			if (path[1].equals(FRAMEWORKLIFECYCLE)) {
				String[] children = new String[3];
				children[0] = RESTART;
				children[1] = SHUTDOWN;
				children[2] = UPDATE;
				return children;
			}

			if (path[1].equals(BUNDLECONTROL)) {
				if (bundlesTable.size() == 0)
					return new String[0];
				String[] children = new String[bundlesTable.size()];
				int i = 0;
				for (Enumeration keys = bundlesTable.keys(); keys
						.hasMoreElements(); i++) {
					children[i] = (String) keys.nextElement();
				}
				return children;
			}
			
			if (path[1].equals(FRAMEWORKEVENT)) {
				String[] children = new String[2];
				children[0] = EVENT;
				children[1] = CATCHEVENTS;
				return children;
			}

		}

		if (path.length == 3 && path[1].equals(INSTALLBUNDLE)) {
			Node[] ids = installbundle.getChildren();

			for (int i = 0; i < ids.length; i++) {
				if (ids[i].getName().equals(path[2])) {
					Node[] gc = ids[i].getChildren();
					String[] children = new String[gc.length];
					for (int g = 0; g < gc.length; g++) {
						children[g] = gc[g].getName();
					}
					return children;
				}
			}
		}

		if (path.length == 3 && path[1].equals(BUNDLECONTROL)) {
			String[] children = new String[3];
			children[0] = BUNDLESTARTLEVEL;
			children[1] = LIFECYCLE;
			children[2] = BUNDLECONTROLREFRESHPACKAGES;
			return children;
		}
		
		if (path.length == 3 && path[1].equals(FRAMEWORKEVENT)) {
			if (eventTable.size() == 0)
				return new String[0];
			String[] children = new String[eventTable.size()];
			int i = 0;
			for (Enumeration keys = eventTable.keys(); keys
					.hasMoreElements(); i++) {
				children[i] = (String) keys.nextElement();
			}
			return children;
		}

		if (path.length == 4 && path[1].equals(BUNDLECONTROL)) {
			String[] children = new String[3];
			children[0] = OPERATION;
			children[1] = BUNDLECONTROLOPTION;
			children[2] = BUNDLECONTROLOPERATIONRESULT;
			return children;
		}
		
		if (path.length == 4 && path[1].equals(FRAMEWORKEVENT)) {
			String[] children = new String[3];
			children[0] = THROWABLE;
			children[1] = TYPE;
			children[2] = BUNDLEID;
			return children;
		}

		// other case
		String[] children = new String[0];
		
		return children;
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) // ./OSGi/Framework
			return new FrameworkMetaNode("Framework Root node.",
					MetaNode.PERMANENT, !FrameworkMetaNode.CAN_ADD,
					!FrameworkMetaNode.CAN_DELETE,
					!FrameworkMetaNode.ALLOW_ZERO,
					!FrameworkMetaNode.ALLOW_INFINITE);

		if (path.length == 2) { // ./OSGi/Framework/...
			if (path[1].equals(STARTLEVEL))
				return new FrameworkMetaNode("Start Level subtree.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[1].equals(INSTALLBUNDLE))
				return new FrameworkMetaNode("Install Bundle subtree.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[1].equals(BUNDLECONTROL))
				return new FrameworkMetaNode("BundleControl subtree.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			if (path[1].equals(FRAMEWORKLIFECYCLE))
				return new FrameworkMetaNode("Lifecycle subtree.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[1].equals(FRAMEWORKEVENT))
				return new FrameworkMetaNode("Event subtree.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[1].equals(REFRESHPACKAGES))
				return new FrameworkMetaNode(
						"A leaf node can be used to refresh packages.",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null);
		}

		if (path.length == 3) {
			if (path[2].equals(REQUESTEDSTARTLEVEL))
				return new FrameworkMetaNode(
						"The requested start level for the framework.",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[2].equals(ACTIVESTARTLEVEL))
				return new FrameworkMetaNode(
						"The active start level of the framework.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[2].equals(INITIALBUNDLESTARTLEVEL))
				return new FrameworkMetaNode(
						"The initial bundle start level of the framework.",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);
			
			if (path[2].equals(BEGINNINGSTARTLEVEL))
				return new FrameworkMetaNode(
						"The initial bundle start level of the framework.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[2].equals(RESTART))
				return new FrameworkMetaNode(
						"Restart command of the framework.",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null);

			if (path[2].equals(SHUTDOWN))
				return new FrameworkMetaNode(
						"Shutdown command of the framework.",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null);

			if (path[2].equals(UPDATE))
				return new FrameworkMetaNode(
						"Update command of the framework.",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null);
			
			if (path[2].equals(CATCHEVENTS))
				return new FrameworkMetaNode(
						"To use for activation of catch events of framework .",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null);

			if (path[1].equals(BUNDLECONTROL))
				return new FrameworkMetaNode("<bundle_id> subtree.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			// ./OSGi/Framework/InstallBundle/<id>
			if (path[1].equals(INSTALLBUNDLE))
				return new FrameworkMetaNode("The id of the instaled bundle.",
						MetaNode.DYNAMIC, FrameworkMetaNode.CAN_ADD,
						FrameworkMetaNode.CAN_DELETE, 
						FrameworkMetaNode.ALLOW_ZERO,
						FrameworkMetaNode.ALLOW_INFINITE);
		}

		if (path.length == 4) { // ./OSGi/Framework/InstallBundle/<id>...
			if (path[3].equals(LOCATION))
				return new FrameworkMetaNode(
						"The location of the installed bundle.",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(URL))
				return new FrameworkMetaNode(
						"The url of the installed bundle.",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[3].equals(INSTALLBUNDLEOPTION))
				return new FrameworkMetaNode(
						"Bundle start option.",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(INSTALLBUNDLEOPERATIONRESULT))
				return new FrameworkMetaNode(
						"The status of the last executed operation.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(BUNDLESTARTLEVEL))
				return new FrameworkMetaNode("BundleStartLevel node.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[3].equals(BUNDLECONTROLREFRESHPACKAGES))
				return new FrameworkMetaNode(
						"A leaf node can be used to refresh bundle packages.",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_BOOLEAN, null);
			
			if (path[3].equals(LIFECYCLE))
				return new FrameworkMetaNode("BundleLifecycle sub-tree.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
			
			if (path[2].equals(EVENT))
				return new FrameworkMetaNode("The id of the event.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
		}
		if (path.length == 5) {
			if (path[4].equals(OPERATION))
				return new FrameworkMetaNode("Operation node.",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[4].equals(BUNDLECONTROLOPTION))
				return new FrameworkMetaNode("Option node.",
						!FrameworkMetaNode.CAN_DELETE,
						FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[4].equals(BUNDLECONTROLOPERATIONRESULT))
				return new FrameworkMetaNode("BundleOperationResult node.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
			
			if (path[4].equals(THROWABLE))
				return new FrameworkMetaNode("Thrownable node.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[4].equals(TYPE))
				return new FrameworkMetaNode("Event type node.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[4].equals(BUNDLEID))
				return new FrameworkMetaNode("Bundle id associated whith the event node.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_LONG, null);
		}

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the framework tree.");
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
		return getNodeValue(nodePath).getSize();
	}

	public int getNodeVersion(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property is not supported.");
	}

	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property is not supported.");
	}

	public String getNodeTitle(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported.");
	}

	public String getNodeType(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length == 1)
			return NODE_TYPE;

// TODO no more transient
//		if (path.length == 3 && path[1].equals(INSTALLBUNDLE))
//			return TRANSIENT_NODE_TYPE;
//		if (path.length == 4 && path[2].equals(EVENT))
//			return TRANSIENT_NODE_TYPE;
		if (isLeafNode(nodePath))
			return FrameworkMetaNode.LEAF_MIME_TYPE;
		return FrameworkMetaNode.FRAMEWORK_MO_TYPE;
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);

		if (path.length == 1)
			return true;

		if (path.length == 2) {
			if (path[1].equals(STARTLEVEL) 
					|| path[1].equals(FRAMEWORKEVENT)
					|| path[1].equals(REFRESHPACKAGES)
					|| path[1].equals(INSTALLBUNDLE)
					|| path[1].equals(FRAMEWORKLIFECYCLE)
					|| path[1].equals(BUNDLECONTROL))
				return true;
		}

		if (path.length == 3) {
			if (path[2].equals(REQUESTEDSTARTLEVEL)
					|| path[2].equals(ACTIVESTARTLEVEL)
					|| path[2].equals(INITIALBUNDLESTARTLEVEL)
					|| path[2].equals(BEGINNINGSTARTLEVEL)
					|| path[2].equals(RESTART)
					|| path[2].equals(SHUTDOWN)
					|| path[2].equals(UPDATE)
					|| path[2].equals(EVENT)
					|| path[2].equals(CATCHEVENTS))
				return true;

			if (installbundle.findNode(new String[] { path[2] }) != null)
				return true;

			if (bundlesTable.get(path[2]) != null)
				return true;
		}

		if (path.length == 4 && path[1].equals(INSTALLBUNDLE))
			if (installbundle.findNode(new String[] { path[2] }) != null)
				if (path[3].equals(LOCATION) 
						|| path[3].equals(URL)
						|| path[3].equals(INSTALLBUNDLEOPTION)
						|| path[3].equals(INSTALLBUNDLEOPERATIONRESULT))
					return true;
		
		if (path.length == 4 && path[1].equals(BUNDLECONTROL))
			if (bundlesTable.get(path[2]) != null)
				if (path[3].equals(BUNDLESTARTLEVEL)
						|| path[3].equals(BUNDLECONTROLREFRESHPACKAGES)
						|| path[3].equals(LIFECYCLE))
					return true;
			
		if (path.length == 4 && path[1].equals(FRAMEWORKEVENT))
			if (eventTable.get(path[3]) != null)
				return true;
		
		if (path.length == 5 && path[1].equals(BUNDLECONTROL))
			if (bundlesTable.get(path[2]) != null)
				if (path[4].equals(OPERATION) 
						|| path[4].equals(BUNDLECONTROLOPTION)
						|| path[4].equals(BUNDLECONTROLOPTION)
						|| path[4].equals(BUNDLECONTROLOPERATIONRESULT))
					return true;
		
		if (path.length == 5 && path[1].equals(FRAMEWORKEVENT)) {
			if (path[4].equals(THROWABLE)
					|| path[4].equals(TYPE)
					|| path[4].equals(BUNDLEID))
				return true;
		}
		return false;
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if (path.length == 1)
			return false;
		
		if (path.length == 2){
			if (path[1].equals(REFRESHPACKAGES))
				return true;
		}			

		if (path.length == 3) {
			if (path[2].equals(REQUESTEDSTARTLEVEL)
					|| path[2].equals(ACTIVESTARTLEVEL)
					|| path[2].equals(INITIALBUNDLESTARTLEVEL)
					|| path[2].equals(BEGINNINGSTARTLEVEL)
					|| path[2].equals(RESTART) 
					|| path[2].equals(SHUTDOWN)
					|| path[2].equals(UPDATE)
					|| path[2].equals(CATCHEVENTS))
				return true;
		}

		if (path.length == 4) {
			if (path[3].equals(LOCATION) 
					|| path[3].equals(URL)
					|| path[3].equals(INSTALLBUNDLEOPTION)
					|| path[3].equals(INSTALLBUNDLEOPERATIONRESULT)
					|| path[3].equals(BUNDLESTARTLEVEL)
					|| path[3].equals(BUNDLECONTROLREFRESHPACKAGES))
				return true;
		}

		if (path.length == 5) {
			if (path[4].equals(OPERATION) 
					|| path[4].equals(BUNDLECONTROLOPTION)
					|| path[4].equals(BUNDLECONTROLOPTION)
					|| path[4].equals(BUNDLECONTROLOPERATIONRESULT)
					|| path[4].equals(THROWABLE)
					|| path[4].equals(TYPE)
					|| path[4].equals(BUNDLEID))
				return true;
		}
		return false;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");
		
		if (path.length == 2)
			if (path[1].equals(REFRESHPACKAGES))
				return new DmtData(false);

		if (path.length == 3) {
			if (path[2].equals(REQUESTEDSTARTLEVEL))
				return new DmtData(requestedStartLevel);

			if (path[2].equals(ACTIVESTARTLEVEL)) {
				try {
					ServiceReference ref = context
							.getServiceReference(org.osgi.service.startlevel.StartLevel.class
									.getName());
					StartLevel sl = (StartLevel) context.getService(ref);

					int activesl = sl.getStartLevel();
					context.ungetService(ref);

					return new DmtData(activesl);
				} catch (NullPointerException e) {
					throw new DmtException(nodePath,
							DmtException.DATA_STORE_FAILURE,
							"The StartLevel service is not available.");
				}
			}

			if (path[2].equals(INITIALBUNDLESTARTLEVEL)) {
				try {
					ServiceReference ref = context
							.getServiceReference(org.osgi.service.startlevel.StartLevel.class
									.getName());
					StartLevel sl = (StartLevel) context.getService(ref);
					int initialsl = sl.getInitialBundleStartLevel();
					context.ungetService(ref);
					return new DmtData(initialsl);
				} catch (NullPointerException e) {
					throw new DmtException(nodePath,
							DmtException.DATA_STORE_FAILURE,
							"The StartLevel service is not available.");
				}
			}

			if (path[2].equals(BEGINNINGSTARTLEVEL)) {
				ServiceReference ref = context
				.getServiceReference(org.osgi.service.startlevel.StartLevel.class
						.getName());
				if(ref==null)
					return new DmtData(-1);
				if(ref!=null){
					Properties prop = System.getProperties();
					String bsl = prop.getProperty(Constants.FRAMEWORK_BEGINNING_STARTLEVEL, "1");
					return new DmtData(Integer.parseInt(bsl));
				}
			}

			if (path[2].equals(CATCHEVENTS))
				return new DmtData(eventFlag);
			
			if (path[2].equals(RESTART))
				return new DmtData(false);
			if (path[2].equals(SHUTDOWN))
				return new DmtData(false);
			if (path[2].equals(UPDATE))
				return new DmtData(false);
		}
		
		if (path.length == 4 && path[1].equals(INSTALLBUNDLE)) {
			Node[] ids = installbundle.getChildren();
			for (int i = 0; i < ids.length; i++) {
				if (path[2].equals(ids[i].getName())) {
					Node[] leaf = ids[i].getChildren();
					for (int x = 0; x < leaf.length; x++) {
						if (path[3].equals(leaf[x].getName()))
							return leaf[x].getData();
					}
					break;
				}
			}
		}

		if (path.length == 4 && path[1].equals(BUNDLECONTROL)) {
			if(path[3].equals(BUNDLESTARTLEVEL)){
				BundelControlSubTree bcst = (BundelControlSubTree)bundlesTable.get(path[2]);
				Bundle targetBundle = bcst.getBundle();
				ServiceReference ref = context
				.getServiceReference(org.osgi.service.startlevel.StartLevel.class.getName());
				StartLevel sl = (StartLevel) context.getService(ref);
				int bundleStartLevel = sl.getBundleStartLevel(targetBundle);
				context.ungetService(ref);
				return new DmtData(bundleStartLevel);
			}
			if(path[3].equals(BUNDLECONTROLREFRESHPACKAGES)){
				BundelControlSubTree bcst = (BundelControlSubTree)bundlesTable.get(path[2]);
				return new DmtData(bcst.getRefreshPackages());
			}			
		}
		
		if (path.length == 5) {
			if(path[3].equals(LIFECYCLE)){
				BundelControlSubTree bcst = (BundelControlSubTree)bundlesTable.get(path[2]);
				if (path[4].equals(OPERATION))
					return new DmtData(bcst.getOperation());
				if (path[4].equals(BUNDLECONTROLOPTION))
					return new DmtData(bcst.getOption());
				if (path[4].equals(BUNDLECONTROLOPERATIONRESULT))
					return new DmtData(bcst.getOperationResult());
			}
			if(path[2].equals(EVENT)){
				EventSubTree est = (EventSubTree)eventTable.get(path[3]);
				if (path[4].equals(THROWABLE))
					return new DmtData(est.getThrowable());
				if (path[4].equals(BUNDLEID))
					return new DmtData(est.getBundleId());
				if (path[4].equals(TYPE))
					return new DmtData(est.getType());
			}
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified leaf node does not exist in the framework object.");
	}

	// ----- Utilities -----//

	protected String[] shapedPath(String[] nodePath) {
		String[] newPath = new String[nodePath.length - 3];
		for (int i = 0; i < nodePath.length - 3; i++) {
			newPath[i] = nodePath[i + 3];
		}
		return newPath;
	}
	
	public void bundleChanged(BundleEvent event) {
		if (!this.managedFlag) {
			Bundle[] bundles = context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				String id = Long.toString(bundles[i].getBundleId() + 1);
				BundelControlSubTree bcst = new BundelControlSubTree(bundles[i]);
				this.bundlesTable.put(id, bcst);
			}
			this.managedFlag = true;
			return;
		}
		Bundle bundle = event.getBundle();
		if (event.getType() == BundleEvent.INSTALLED
				|| event.getType() == BundleEvent.RESOLVED) {
			BundelControlSubTree bcst = new BundelControlSubTree(bundle);
			this.bundlesTable.put(Long.toString(bundle.getBundleId() + 1), bcst);
		} else if (event.getType() == BundleEvent.UNINSTALLED) {
			String id = Long.toString(bundle.getBundleId() + 1);
			this.bundlesTable.remove(id);
		}

	}

	public void frameworkEvent(FrameworkEvent event){
		if(eventFlag){
			String throwable=null;
			Throwable th = event.getThrowable();
			if(th==null){
				throwable = "";
			}else if(th!=null){
				throwable = th.getMessage();
			}
			String type;
			int iType = event.getType();
			if(iType==FrameworkEvent.STARTED)
				type="STARTED";
			else if(iType==FrameworkEvent.ERROR)
				type="ERROR";
			else if(iType==FrameworkEvent.PACKAGES_REFRESHED)
				type="PACKAGES_REFRESHED";
			else if(iType==FrameworkEvent.STARTLEVEL_CHANGED)
				type="STARTLEVEL_CHANGED";
			else if(iType==FrameworkEvent.WARNING)
				type="WARNING";
			else if(iType==FrameworkEvent.INFO)
				type="INFO";
			else if(iType==FrameworkEvent.STOPPED)
				type="STOPPED";
			else if(iType==FrameworkEvent.STOPPED_UPDATE)
				type="STOPPED_UPDATE";
			else if(iType==FrameworkEvent.STOPPED_BOOTCLASSPATH_MODIFIED)
				type="STOPPED_BOOTCLASSPATH_MODIFIED";
			else if(iType==FrameworkEvent.WAIT_TIMEDOUT)
				type="WAIT_TIMEDOUT";
			else
				type = "";
			long bundleId = event.getBundle().getBundleId();
			EventSubTree eventSubTree = new EventSubTree(throwable,type,bundleId);
			eventTable.put(Long.toString(eventId), eventSubTree);
			eventId++;
		}
	}
	
	// ----- InstallBundle subtree -----//
	protected class BundelControlSubTree{
		
		private Bundle bundle = null;
		private boolean refreshPackages = false;
		private int bundleStartLevel = 0;
		private String operation = "";
		private String option = "NO OPTION";
		private String operationResult = "";
		
		BundelControlSubTree(Bundle bundle){
			this.bundle = bundle;
		}
		protected Bundle getBundle(){
			return this.bundle;	
		}
		protected boolean getRefreshPackages(){
			return this.refreshPackages;	
		}
		protected int getBundleStartLevel(){
			return this.bundleStartLevel;
		}
		protected String getOperation(){
			return this.operation;	
		}
		protected String getOption(){
			return this.option;
		}
		protected String getOperationResult(){
			return this.operationResult;	
		}		
		protected void setRefreshPackages(boolean refreshPackages){
			this.refreshPackages = refreshPackages;	
		}
		protected void setBundleStartLevel(int bundleStartLevel){
			this.bundleStartLevel = bundleStartLevel;
		}
		protected void setOperation(String operation){
			this.operation = operation;	
		}
		protected void setOption(String option){
			this.option = option;
		}
		protected void setOperationResult(String operationResult){
			this.operationResult = operationResult;	
		}
	}
	
/*	protected class BundelControlValue{

		String operation;
		String bundleUpdate;
		String bundleUpdateTmp;
		int option;
		int optionTmp;
		String operationResult;
		Bundle bundle;
		boolean flag;
		
		BundelControlValue(Bundle bundle, String operation, int option, 
				String bundleUpdate, String operationResult, boolean flag){
			this.bundleUpdate=bundleUpdate;
			this.operation=operation;
			this.operationResult=operationResult;
			this.option=option;
			this.bundle=bundle;
			this.flag = flag;
			this.bundleUpdateTmp = bundleUpdate;
			this.optionTmp=option;
		}
		void setOperation(String operation){
			this.operation=operation;	
		}
		void setBundleUpdate(String bundleUpdate){
			this.bundleUpdate = bundleUpdate;	
		}
		void setOperationResult(String operationResult){
			this.operationResult = operationResult;	
		}
		void setOption(int option){
			this.option = option;
		}
		void setFlag(boolean flag){
			this.flag = flag;
		}
		void setBundleUpdateTmp(String bundleUpdateTmp){
			this.bundleUpdateTmp = bundleUpdateTmp;
		}
		void setOptionTmp(int optionTmp){
			this.optionTmp = optionTmp;
		}
	}
*/

	protected class EventSubTree{
		
		private String throwable = null;
		private String type = null;
		private long bundleId = 0;
		
		EventSubTree(String throwable, String type, long bundleId){
			this.throwable = throwable;
			this.type = type;
			this.bundleId = bundleId;
		}		
		protected String getThrowable(){
			return throwable;
		}		
		protected String getType(){
			return type;
		}		
		protected long getBundleId(){
			return bundleId;
		}		
	}
	
	protected class Node {
		static final String INTERIOR = "Interiror";
		static final String LEAF = "leaf";

		private String name;
		private String type;
		private Vector children = new Vector();
		private DmtData data = null;

		Node(String name, Node[] children) {
			this.name = name;
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					this.children.add(children[i]);
				}
			} else
				this.children = new Vector();

			type = INTERIOR;
		}

		Node(String name, Node[] children, DmtData dmtdata) {
			this.name = name;
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					this.children.add(children[i]);
				}
			} else
				this.children = new Vector();

			type = LEAF;
			data = dmtdata;
		}

		protected Node findNode(String[] path) {
			for (int i = 0; i < children.size(); i++) {
				if (((Node) children.get(i)).name.equals(path[0])) {
					if (path.length == 1) {
						return (Node) children.get(i);
					} else {
						String[] nextpath = new String[path.length - 1];
						for (int x = 1; x < path.length; x++) {
							nextpath[x - 1] = path[x];
						}
						return ((Node) children.get(i)).findNode(nextpath);
					}
				}
			}

			return null;
		}

		protected String getName() {
			return name;
		}

		protected Node addNode(Node add) {
			children.add(add);

			return null;
		}

		protected Node deleteNode(Node del) {
			children.remove(del);

			return null;
		}

		protected Node[] getChildren() {
			Node[] nodes = new Node[children.size()];

			for (int i = 0; i < children.size(); i++) {
				nodes[i] = ((Node) children.get(i));
			}
			return nodes;
		}

		protected void setData(DmtData d) {
			data = d;
		}

		protected DmtData getData() {
			return data;
		}

		protected String getType() {
			return type;
		}

		protected Node copy() {
			if (type.equals(INTERIOR)) {
				if (children.size() != 0) {
					Node[] subnode = new Node[children.size()];
					for (int i = 0; i < children.size(); i++) {
						subnode[i] = ((Node) children.get(i)).copy();
					}
					return new Node(name, subnode);
				} else {
					return new Node(name, null);
				}
			} else if (type.equals(LEAF)) {
				return new Node(name, null, data);
			}

			return null;
		}
	}

}
