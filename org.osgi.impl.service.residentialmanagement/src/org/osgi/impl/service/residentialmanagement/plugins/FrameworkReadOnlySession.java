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
import java.util.Vector;
import java.util.Date;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;
import info.dmtree.spi.ReadableDataSession;

/**
 * 
 * @author Koya MORI NTT Corporation, Shigekuni KONDO
 */
class FrameworkReadOnlySession implements ReadableDataSession {

	protected static final String ID = "id";
	protected static final String STARTLEVEL = "StartLevel";
	protected static final String INSTALLBUNDLE = "InstallBundle";
	protected static final String FRAMEWORKLIFECYCLE = "FrameworkLifecycle";
	protected static final String BUNDLECONTROL = "BundleControl";
	protected static final String EXT = "Ext";
	protected static final String REQUESTEDSTARTLEVEL = "RequestedStartLevel";
	protected static final String ACTIVESTARTLEVEL = "ActiveStartLevel";
	protected static final String INITIALBUNDLESTARTLEVEL = "InitialBundleStartLevel";
	protected static final String LOCATION = "Location";
	protected static final String URL = "URL";
	protected static final String ERROR = "Error";
	protected static final String RESTART = "Restart";
	protected static final String SHUTDOWN = "Shutdown";
	protected static final String UPDATE = "Update";
	protected static final String BUNDLESTARTLEVEL = "BundleStartLevl";
	protected static final String BUNDLELIFECYCLE = "BundleLifecycle";
	protected static final String DESIREDSTATE = "DesiredState";
	protected static final String BUNDLEUPDATE = "BundleUpdate";
	protected static final String OPTION = "Option";
	protected static final String OPERATIONRESULT = "OperationResult";

	protected FrameworkPlugin plugin;
	protected BundleContext context;
	protected Hashtable bundlesTable = new Hashtable();
	protected int RequestedStartLevel = 0;
	protected Node installbundle;

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
			String[] children = new String[5];
			children[0] = STARTLEVEL;
			children[1] = INSTALLBUNDLE;
			children[2] = FRAMEWORKLIFECYCLE;
			children[3] = BUNDLECONTROL;
			children[4] = EXT;

			return children;
		}

		if (path.length == 2) {
			if (path[1].equals(STARTLEVEL)) {
				String[] children = new String[3];
				children[0] = REQUESTEDSTARTLEVEL;
				children[1] = ACTIVESTARTLEVEL;
				children[2] = INITIALBUNDLESTARTLEVEL;

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
				if (bundlesTable.size() == 0) {
					String[] children = new String[1];
					children[0] = "";
					return children;
				}
				String[] children = new String[bundlesTable.size()];
				int i = 0;
				for (Enumeration keys = bundlesTable.keys(); keys
						.hasMoreElements(); i++) {
					children[i] = (String) keys.nextElement();
				}
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
			if (bundlesTable.size() == 0) {
				String[] children = new String[1];
				children[0] = "";
				return children;
			}
			String[] children = new String[2];
			children[0] = BUNDLESTARTLEVEL;
			children[1] = BUNDLELIFECYCLE;
			return children;
		}

		if (path.length == 4) {
			String[] children = new String[4];
			children[0] = DESIREDSTATE;
			children[1] = BUNDLEUPDATE;
			children[2] = OPTION;
			children[3] = OPERATIONRESULT;
			return children;
		}

		// other case
		String[] children = new String[0];
		// children[0] = "";

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

			if (path[1].equals(EXT))
				return new FrameworkMetaNode("Extension subtree.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
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

			if (path[1].equals(BUNDLECONTROL))
				return new FrameworkMetaNode("<bundle_id> subtree.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);

			// ./OSGi/Framework/InstallBundle/<id>
			return new FrameworkMetaNode("Update command of the framework.",
					MetaNode.DYNAMIC, FrameworkMetaNode.CAN_ADD,
					FrameworkMetaNode.CAN_DELETE, FrameworkMetaNode.ALLOW_ZERO,
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

			if (path[3].equals(ERROR))
				return new FrameworkMetaNode(
						"Errors of the install operation.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(BUNDLESTARTLEVEL))
				return new FrameworkMetaNode("BundleStartLevel node.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[3].equals(BUNDLELIFECYCLE))
				return new FrameworkMetaNode("BundleLifecycle sub-tree.",
						MetaNode.AUTOMATIC, !FrameworkMetaNode.CAN_ADD,
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE);
		}
		if (path.length == 5) {
			if (path[4].equals(DESIREDSTATE))
				return new FrameworkMetaNode("DesiredState node.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[4].equals(BUNDLEUPDATE))
				return new FrameworkMetaNode("BundleUpdate node.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);

			if (path[4].equals(OPTION))
				return new FrameworkMetaNode("Option node.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[4].equals(OPERATIONRESULT))
				return new FrameworkMetaNode("BundleOperationResult node.",
						!FrameworkMetaNode.CAN_DELETE,
						!FrameworkMetaNode.CAN_REPLACE,
						FrameworkMetaNode.ALLOW_ZERO,
						!FrameworkMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_STRING, null);
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
		if (isLeafNode(nodePath))
			return FrameworkMetaNode.LEAF_MIME_TYPE;

		return FrameworkMetaNode.FRAMEWORK_MO_TYPE;
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);

		if (path.length == 1)
			return true;

		if (path.length == 2) {
			if (path[1].equals(STARTLEVEL) || path[1].equals(INSTALLBUNDLE)
					|| path[1].equals(FRAMEWORKLIFECYCLE)
					|| path[1].equals(BUNDLECONTROL) || path[1].equals(EXT))
				return true;
		}

		if (path.length == 3) {
			if (path[2].equals(REQUESTEDSTARTLEVEL)
					|| path[2].equals(ACTIVESTARTLEVEL)
					|| path[2].equals(INITIALBUNDLESTARTLEVEL)
					|| path[2].equals(RESTART) || path[2].equals(SHUTDOWN)
					|| path[2].equals(UPDATE))
				return true;

			if (installbundle.findNode(new String[] { path[2] }) != null)
				return true;

			if (bundlesTable.get(path[1]) != null)
				return true;
		}

		if (path.length == 4) {
			if (path[3].equals(LOCATION) || path[3].equals(URL)
					|| path[3].equals(ERROR)
					|| path[3].equals(BUNDLESTARTLEVEL)
					|| path[3].equals(BUNDLELIFECYCLE))
				return true;
		}

		if (path.length == 5) {
			if (path[4].equals(DESIREDSTATE) || path[4].equals(BUNDLEUPDATE)
					|| path[4].equals(OPTION)
					|| path[4].equals(OPERATIONRESULT))
				return true;
		}

		return false;
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length <= 2)
			return false;

		if (path.length == 3) {
			if (path[2].equals(REQUESTEDSTARTLEVEL)
					|| path[2].equals(ACTIVESTARTLEVEL)
					|| path[2].equals(INITIALBUNDLESTARTLEVEL)
					|| path[2].equals(RESTART) || path[2].equals(SHUTDOWN)
					|| path[2].equals(UPDATE))
				return true;
		}

		if (path.length == 4) {
			if (path[3].equals(LOCATION) || path[3].equals(URL)
					|| path[3].equals(BUNDLESTARTLEVEL)
					|| path[3].equals(ERROR))
				return true;
		}

		if (path.length == 5) {
			if (path[4].equals(DESIREDSTATE) || path[4].equals(BUNDLEUPDATE)
					|| path[4].equals(OPTION)
					|| path[4].equals(OPERATIONRESULT))
				return true;
		}

		return false;
	}


	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length <= 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 3) {
			if (path[2].equals(REQUESTEDSTARTLEVEL))
				return new DmtData(RequestedStartLevel);

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
			BundelControlValue bcv = (BundelControlValue)bundlesTable.get(path[2]);
			Bundle targetBundle = bcv.bundle;
			ServiceReference ref = context
			.getServiceReference(org.osgi.service.startlevel.StartLevel.class.getName());
			StartLevel sl = (StartLevel) context.getService(ref);
			int bundleStartLevel = sl.getBundleStartLevel(targetBundle);
			context.ungetService(ref);
			return new DmtData(bundleStartLevel);
		}
		
		if (path.length == 5) {
			BundelControlValue bcv = (BundelControlValue)bundlesTable.get(path[2]);
			if (path[4].equals(DESIREDSTATE))
				return new DmtData(bcv.desiredState);
			if (path[4].equals(BUNDLEUPDATE))
				return new DmtData(bcv.bundleUpdate);
			if (path[4].equals(OPTION))
				return new DmtData(bcv.option);
			if (path[4].equals(OPERATIONRESULT))
				return new DmtData(bcv.operationResult);
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified key does not exist in the framework object.");
	}

	// ----- Utilities -----//

	protected String[] shapedPath(String[] nodePath) {
		String[] newPath = new String[nodePath.length - 3];
		for (int i = 0; i < nodePath.length - 3; i++) {
			newPath[i] = nodePath[i + 3];
		}

		return newPath;
	}

	private Hashtable manageBundles() throws InvalidSyntaxException {
		Bundle[] bundles = context.getBundles();
		Hashtable bundlesTable = new Hashtable();
		for (int i = 0; i < bundles.length; i++) {
			long bundleIdLong = bundles[i].getBundleId();
			String bundleId = Long.toString(bundleIdLong);
			BundelControlValue bcv = new BundelControlValue(bundles[i],0,0,null,null,false);
			bundlesTable.put(bundleId, bcv);
		}
		return bundlesTable;
	}

	public void refreshBundlesTable() throws InvalidSyntaxException {
		if (bundlesTable.size() == 0) {
			bundlesTable = manageBundles();
		}
		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			long bundleIdLong = bundles[i].getBundleId();
			String bundleId = Long.toString(bundleIdLong);
			if (!bundlesTable.containsKey(bundleId)) {
				BundelControlValue bcv = new BundelControlValue(bundles[i],0,0,null,null,false);
				this.bundlesTable.put(bundleId, bcv);
			}
		}
		
	}

	// ----- InstallBundle subtree -----//

	protected class BundelControlValue{
		int desiredState;
		String bundleUpdate;
		String bundleUpdateTmp;
		int option;
		int optionTmp;
		String operationResult;
		Bundle bundle;
		boolean flag;
		
		BundelControlValue(Bundle bundle, int desiredState, int option, String bundleUpdate, String operationResult, boolean flag){
			this.bundleUpdate=bundleUpdate;
			this.desiredState=desiredState;
			this.operationResult=operationResult;
			this.option=option;
			this.bundle=bundle;
			this.flag = flag;
			this.bundleUpdateTmp = bundleUpdate;
			this.optionTmp=option;
		}
		void setDesiredState(int desiredState){
			this.desiredState = desiredState;	
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
