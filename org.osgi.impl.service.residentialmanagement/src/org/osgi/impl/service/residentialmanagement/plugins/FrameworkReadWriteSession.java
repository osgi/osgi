/*
 * Copyright (c) OSGi Alliance (2000-2011). All Rights Reserved.
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

import org.osgi.service.dmt.*;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.osgi.framework.launch.Framework;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

/**
 * 
 * @author Shigekuni KONDO NTT Corporation
 */
class FrameworkReadWriteSession extends FrameworkReadOnlySession implements
		TransactionalDataSession {

	private Hashtable bundlesTableTmp = null;
	private Hashtable bundlesTableSnap = null;
	private Vector operations = null;

	private Vector uninstallBundles = null;
	private Vector updateBundles = null;
	private Vector startBundles = null;
	private Vector stopBundles = null;
	private Vector resolveBundles = null;
	private Vector stopAndRefreshBundles = null;
	private Vector bundleStartLevelCue = null;
	private BundleSubTree frameworkBs = null;
	private Hashtable restoreBundlesForUninstall = null;
	private Hashtable restoreBundlesForUpdate = null;
	private Hashtable restoreBundles = null;
	private long waitTime = Long.parseLong(System.getProperty(
			RMTConstants.WAIT_TIME_FOR_SETSTARTLEVEL, "10000"));

	FrameworkReadWriteSession(FrameworkPlugin plugin, BundleContext context,
			FrameworkReadOnlySession session) {
		super(plugin, context);
		this.bundlesTable = session.bundlesTable;
		operations = new Vector();
		bundlesTableTmp = new Hashtable();
		bundlesTableSnap = new Hashtable();
		operations = new Vector();
		uninstallBundles = new Vector();
		updateBundles = new Vector();
		startBundles = new Vector();
		stopBundles = new Vector();
		resolveBundles = new Vector();
		stopAndRefreshBundles = new Vector();
		bundleStartLevelCue = new Vector();
		restoreBundlesForUninstall = new Hashtable();
		restoreBundlesForUpdate = new Hashtable();
		restoreBundles = new Hashtable();
		frameworkBs = null;
	}

	public void commit() throws DmtException {
		this.bundlesTableSnap = (Hashtable) this.bundlesTable.clone();
		Iterator i = operations.iterator();
		while (i.hasNext()) {
			Operation operation = (Operation) i.next();
			try {
				if (operation.getOperation() == Operation.ADD_OBJECT) {
					String[] path = operation.getObjectname();
					if (path.length == 3 && path[1].equals(RMTConstants.BUNDLE)) {
						BundleSubTree bs = new BundleSubTree(path[2]);
						this.bundlesTable.put(Uri.encode(path[2]), bs);
					}
				} else if (operation.getOperation() == Operation.SET_VALUE) {
					String[] nodepath = operation.getObjectname();
					if (nodepath[nodepath.length - 1].equals(RMTConstants.URL)) {
						BundleSubTree bs = (BundleSubTree) this.bundlesTable
								.get(nodepath[nodepath.length - 2]);
						bs.setURL(operation.getData().getString());
						if (nodepath[nodepath.length - 2]
								.equals(Constants.SYSTEM_BUNDLE_LOCATION)) {
							this.frameworkBs = bs;
						} else if (bs.getCreateFlag()) {
							this.updateBundles.add(bs);
						}
					} else if (nodepath[nodepath.length - 1]
							.equals(RMTConstants.AUTOSTART)) {
						BundleSubTree bs = (BundleSubTree) this.bundlesTable
								.get(nodepath[nodepath.length - 2]);
						bs.setAutoStart(operation.getData().getBoolean());
					} else if (nodepath[nodepath.length - 1]
							.equals(RMTConstants.REQUESTEDSTATE)) {
						BundleSubTree bs = (BundleSubTree) this.bundlesTable
								.get(nodepath[nodepath.length - 2]);
						String requestedState = operation.getData().getString();
						bs.setRequestedState(requestedState);
						if (requestedState.equals(RMTConstants.ACTIVE)) {
							this.startBundles.add(bs);
						} else if (requestedState
								.equals(RMTConstants.UNINSTALLED)) {
							this.uninstallBundles.add(bs);
						} else if (requestedState.equals(RMTConstants.RESOLVED)
								&& bs.getBundleObj().getState() == 2) {
							this.resolveBundles.add(bs);
						} else if (requestedState.equals(RMTConstants.RESOLVED)
								&& bs.getBundleObj().getState() != 2) {
							this.stopBundles.add(bs);
						} else if (requestedState
								.equals(RMTConstants.INSTALLED)) {
							this.stopAndRefreshBundles.add(bs);
						}
					} else if (nodepath[nodepath.length - 1]
							.equals(RMTConstants.BUNDLESTARTLEVEL)
							&& nodepath.length == 4) {
						BundleSubTree bs = (BundleSubTree) this.bundlesTable
								.get(nodepath[nodepath.length - 2]);
						int bundleStartLevel = operation.getData().getInt();
						bs.setStartLevel(bundleStartLevel);
						this.bundleStartLevelCue.add(bs);
					} else if (nodepath[nodepath.length - 1]
							.equals(RMTConstants.FRAMEWORKSTARTLEVEL)) {
						int flameworkStartLevel = operation.getData().getInt();
						Bundle sysBundle = context.getBundle(0);
						FrameworkStartLevel fs = (FrameworkStartLevel) sysBundle
								.adapt(FrameworkStartLevel.class);
						fs.setStartLevel(flameworkStartLevel, null);
						long s = System.currentTimeMillis();
						while(!(fs.getStartLevel()==flameworkStartLevel)){
							long n = System.currentTimeMillis();
							if((n-s)>=waitTime)				
								break;
						}
					} else if (nodepath[nodepath.length - 1]
							.equals(RMTConstants.INITIALBUNDLESTARTLEVEL)) {
						int initialBundleStartlevel = operation.getData()
								.getInt();
						Bundle sysBundle = context.getBundle(0);
						FrameworkStartLevel fs = (FrameworkStartLevel) sysBundle
								.adapt(FrameworkStartLevel.class);
						fs.setInitialBundleStartLevel(initialBundleStartlevel);
					}
				}
			} catch (Exception e) {
				bundlesTable = (Hashtable) this.bundlesTableSnap.clone();
				rollback();
				throw new DmtException(operation.getObjectname(),
						DmtException.COMMAND_FAILED,
						"The operation encountered problems.");
			}
		}

		// Operation of uninstall and update
		Iterator stopunit = this.uninstallBundles.iterator();
		while (stopunit.hasNext()) {
			BundleSubTree bs = (BundleSubTree) stopunit.next();
			String location = null;
			try {
				location = bs.getLocation();
				String state = bs.getState();
				bs.getBundleObj().stop();
				this.restoreBundles.put(location, state);
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
				throw new DmtException(location,
						DmtException.COMMAND_FAILED,
						"The operation of stop encountered problems.");
			}
		}
		Iterator stopupit = this.updateBundles.iterator();
		while (stopupit.hasNext()) {
			BundleSubTree bs = (BundleSubTree) stopupit.next();
			String location = null;
			try {
				location = bs.getLocation();
				String state = bs.getState();
				bs.getBundleObj().stop();
				this.restoreBundles.put(location, state);
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
				throw new DmtException(location,
						DmtException.COMMAND_FAILED,
						"The operation of stop encountered problems.");
			}
		}
		Iterator uninstallit = this.uninstallBundles.iterator();
		while (uninstallit.hasNext()) {
			BundleSubTree bs = (BundleSubTree) uninstallit.next();
			String location = null;
			try {
				location = bs.getLocation();
				String state = (String) this.restoreBundles.get(location);
				bs.getBundleObj().uninstall();
				this.restoreBundlesForUninstall.put(bs, state);
				this.restoreBundles.remove(location);
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
				throw new DmtException(location,
						DmtException.COMMAND_FAILED,
						"The operation of uninstall encountered problems.");
			}
		}
		Iterator updateit = this.updateBundles.iterator();
		while (updateit.hasNext()) {
			BundleSubTree bs = (BundleSubTree) updateit.next();
			String urlStr = bs.getURL();
			URL url;
			String location = null;
			try {
				location = bs.getLocation();
				String state = (String) this.restoreBundles.get(location);
				url = new URL(urlStr);
				InputStream is;
				is = url.openStream();
				bs.getBundleObj().update(is);
				this.restoreBundlesForUpdate.put(location, state);
				this.restoreBundles.remove(location);
			} catch (MalformedURLException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(0);
				restore();
			} catch (IOException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(0);
				restore();
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
				throw new DmtException(location,
						DmtException.COMMAND_FAILED,
						"The operation of update encountered problems.");
			}
		}

		// Operation of bundle installation
		for (Enumeration keys = this.bundlesTableTmp.keys(); keys
				.hasMoreElements();) {
			String key = (String) keys.nextElement();
			BundleSubTree bs = (BundleSubTree) this.bundlesTable.get(key);
			String urlStr = bs.getURL();
			if (urlStr.equals("")) {
				throw new DmtException(key, DmtException.COMMAND_FAILED,
						"Creating a new Bundle node must be set URL of the bundle.");
			}
			String location = null;
			try {
				location = bs.getLocation();
				String state = RMTConstants.UNINSTALLED;
				URL url = new URL(urlStr);
				InputStream is = url.openStream();
				context.installBundle(key, is);
				this.restoreBundles.put(location, state);
			} catch (MalformedURLException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(0);
				restore();
				throw new DmtException(location,
						DmtException.COMMAND_FAILED,
						"The operation of install encountered problems.");
			} catch (IOException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(0);
				restore();
				throw new DmtException(location,
						DmtException.COMMAND_FAILED,
						"The operation of install encountered problems.");
				
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
				throw new DmtException(location,
						DmtException.COMMAND_FAILED,
						"The operation of install encountered problems.");
			}
		}

		// refreshing installed bundles, uninstalled bundles and updated bundles
		Vector bundles = new Vector();
		Iterator refreshupi = this.updateBundles.iterator();
		while (refreshupi.hasNext()) {
			BundleSubTree bs = (BundleSubTree) refreshupi.next();
			bundles.add(bs.getBundleObj());
		}
		for (Enumeration keys = this.bundlesTableTmp.keys(); keys
				.hasMoreElements();) {
			String key = (String) keys.nextElement();
			BundleSubTree bs = (BundleSubTree) this.bundlesTable.get(key);
			bundles.add(bs.getBundleObj());
		}
		Iterator refreshuni = this.uninstallBundles.iterator();
		while (refreshuni.hasNext()) {
			BundleSubTree bs = (BundleSubTree) refreshuni.next();
			bundles.add(bs.getBundleObj());
		}
		Bundle sysBundle = context.getBundle(0);
		FrameworkWiring fw = (FrameworkWiring) sysBundle
				.adapt(FrameworkWiring.class);
		fw.refreshBundles(bundles, null);

		// setting bundle's StartLevel
		Iterator startLevelit = this.bundleStartLevelCue.iterator();
		while (startLevelit.hasNext()) {
			BundleSubTree bs = (BundleSubTree) startLevelit.next();
			int startLevel = bs.getStartLevelTmp();
			BundleStartLevel sl = (BundleStartLevel) bs.getBundleObj().adapt(
					BundleStartLevel.class);
			sl.setStartLevel(startLevel);
			long s = System.currentTimeMillis();
			while(!(sl.getStartLevel()==startLevel)){
				long n = System.currentTimeMillis();
				if((n-s)>=waitTime)				
					break;
			}
		}

		// starting bundles
		Iterator startit = this.startBundles.iterator();
		while (startit.hasNext()) {
			BundleSubTree bs = (BundleSubTree) startit.next();
			boolean auto = bs.getAutoStart();
			String location = null;
			try {
				location = bs.getLocation();
				String state = bs.getState();
				if (auto)
					bs.getBundleObj().start();
				else
					bs.getBundleObj().start(Bundle.START_TRANSIENT);
				if (this.restoreBundles.get(location) == null)
					this.restoreBundles.put(location, state);
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
				throw new DmtException(location,
						DmtException.COMMAND_FAILED,
						"The operation of start encountered problems.");
			}
		}

		// stopping bundles
		Iterator stopit = this.stopBundles.iterator();
		while (stopit.hasNext()) {
			BundleSubTree bs = (BundleSubTree) stopit.next();
			String location = null;
			try {
				location = bs.getLocation();
				String state = bs.getState();
				bs.getBundleObj().stop();
				if (this.restoreBundles.get(location) == null)
					this.restoreBundles.put(location, state);
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
				throw new DmtException(location,
						DmtException.COMMAND_FAILED,
						"The operation of stop encountered problems.");
			}
		}

		// resolving bundles
		Iterator resolveit = this.resolveBundles.iterator();
		Vector resolveBundles = new Vector();
		while (resolveit.hasNext()) {
			BundleSubTree bs = (BundleSubTree) resolveit.next();
			String location = bs.getLocation();
			String state = bs.getState();
			resolveBundles.add(bs.getBundleObj());
			if (this.restoreBundles.get(location) == null)
				this.restoreBundles.put(location, state);
		}
		fw.resolveBundles(resolveBundles);

		// stopping and refreshing bundles
		Iterator stopandrefit = this.stopAndRefreshBundles.iterator();
		Vector refreshBundles = new Vector();
		while (stopandrefit.hasNext()) {
			BundleSubTree bs = (BundleSubTree) stopandrefit.next();
			String location = null;
			try {
				location = bs.getLocation();
				String state = bs.getState();
				bs.getBundleObj().stop();
				if (this.restoreBundles.get(location) == null)
					this.restoreBundles.put(location, state);
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
				throw new DmtException(location,
						DmtException.COMMAND_FAILED,
						"The operation of stop encountered problems.");
			}
			refreshBundles.add(bs.getBundleObj());
		}
		fw.refreshBundles(refreshBundles, null);
		rollback();

		// framework --> on thread
		if (frameworkBs != null) {
			FrameworkUpdateThread fut = new FrameworkUpdateThread();
			fut.start();
		}
	}

	class FrameworkUpdateThread extends Thread {
		private int waitTime = Integer.parseInt(System.getProperty(
				RMTConstants.WAIT_TIME_FOR_FRAMEWORK_UPDATE, "5000"));

		FrameworkUpdateThread() {
		}

		public void run() {
			try {
				wait(waitTime);
			} catch (InterruptedException e1) {
			}
			Framework framework = (Framework) context.getBundle(0);
			try {
				framework.update();
			} catch (BundleException e) {
				BundleSubTree bs = (BundleSubTree) bundlesTable
						.get(Constants.SYSTEM_BUNDLE_LOCATION);
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
			}
		}
	}

	protected void restore() {
		Vector refreshBundles = new Vector();
		Bundle sysBundle = context.getBundle(0);
		FrameworkWiring fw = (FrameworkWiring) sysBundle
				.adapt(FrameworkWiring.class);
		for (Enumeration keys = this.restoreBundlesForUninstall.keys(); keys
				.hasMoreElements();) {
			BundleSubTree bs = (BundleSubTree) keys.nextElement();
			String urlStr = bs.getURL();
			if (urlStr != null) {
				try {
					URL url = new URL(urlStr);
					InputStream is = url.openStream();
					Bundle installedBundle = context.installBundle(
							bs.getLocation(), is);
					refreshBundles.add(installedBundle);
					this.restoreBundles.put(bs.getLocation(),
							this.restoreBundlesForUninstall.get(bs));
					bs = null;
				} catch (Exception e) {
				}
			} else if (urlStr == null) {
				bs = null;
			}
		}
		for (Enumeration keys = this.restoreBundlesForUpdate.keys(); keys
				.hasMoreElements();) {
			String location = (String) keys.nextElement();
			BundleSubTree bs = (BundleSubTree) this.bundlesTableSnap
					.get(location);
			String urlStr = bs.getURL();
			if (urlStr != null) {
				try {
					URL url = new URL(urlStr);
					InputStream is = url.openStream();
					bs.getBundleObj().update(is);
					refreshBundles.add(bs.getBundleObj());
					this.restoreBundles.put(location,
							this.restoreBundlesForUpdate.get(location));
					bs = null;
				} catch (Exception e) {
				}
			}
		}
		fw.refreshBundles(refreshBundles, null);
		refreshBundles = new Vector();

		for (Enumeration keys = this.restoreBundles.keys(); keys
				.hasMoreElements();) {
			try {
				String location = (String) keys.nextElement();
				BundleSubTree bs = (BundleSubTree) this.bundlesTable
						.get(location);
				String afterState = bs.getState();
				String beforeState = (String) this.restoreBundles.get(location);
				if (afterState.equals(beforeState))
					continue;
				if (beforeState.equals(RMTConstants.UNINSTALLED)) {
					bs.getBundleObj().uninstall();
				} else if (beforeState.equals(RMTConstants.INSTALLED)) {
					bs.getBundleObj().stop();
					refreshBundles.add(bs.getBundleObj());
					fw.refreshBundles(refreshBundles, null);
				} else if (beforeState.equals(RMTConstants.RESOLVED)) {
					if (bs.getState().equals(RMTConstants.INSTALLED)) {
						Vector resolveBundles = new Vector();
						resolveBundles.add(bs.getBundleObj());
						fw.resolveBundles(resolveBundles);
					} else {
						bs.getBundleObj().stop();
					}
				} else if (beforeState.equals(RMTConstants.ACTIVE)) {
					boolean auto = bs.getAutoStart();
					if (auto)
						bs.getBundleObj().start();
					else
						bs.getBundleObj().start(Bundle.START_TRANSIENT);
				}
			} catch (Exception e) {
			}
		}
	}

	public void rollback() throws DmtException {
		operations = new Vector();
		bundlesTableTmp = new Hashtable();
		bundlesTableSnap = new Hashtable();
		operations = new Vector();
		uninstallBundles = new Vector();
		updateBundles = new Vector();
		startBundles = new Vector();
		stopBundles = new Vector();
		resolveBundles = new Vector();
		stopAndRefreshBundles = new Vector();
		bundleStartLevelCue = new Vector();
		restoreBundlesForUninstall = new Hashtable();
		restoreBundlesForUpdate = new Hashtable();
		restoreBundles = new Hashtable();
		frameworkBs = null;
	}

	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		if (type != null)
			throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
					"Cannot set type property of interior nodes.");
		String[] path = shapedPath(nodePath);
		if (path.length == 3 && path[1].equals(RMTConstants.BUNDLE)) {
			if (bundlesTable.get(path[2]) != null)
				throw new DmtException(nodePath,
						DmtException.NODE_ALREADY_EXISTS,
						"A given node already exists in the framework MO.");
			operations.add(new Operation(Operation.ADD_OBJECT, path));
//			if (bundlesTableCopy.size() == 0)
//				bundlesTableCopy = (Hashtable) bundlesTable.clone();
			BundleSubTree bs = new BundleSubTree(path[2]);
//			this.bundlesTableCopy.put(Uri.encode(path[2]), bs);
			this.bundlesTableTmp.put(Uri.encode(path[2]), bs);
			return;
		}

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"There is no appropriate node for the given path.");
	}

	public void createLeafNode(String[] nodePath, DmtData data, String mimeType)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
				"There is no leaf node to be created in the framework subtree.");

	}

	public void setNodeValue(String[] nodePath, DmtData data)
			throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length < 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 2) {
			if (path[1].equals(RMTConstants.FRAMEWORKSTARTLEVEL)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
			if (path[1].equals(RMTConstants.INITIALBUNDLESTARTLEVEL)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
		}

		if (path.length == 4) {
			if (path[3].equals(RMTConstants.URL)
					|| path[3].equals(RMTConstants.AUTOSTART)
					|| path[3].equals(RMTConstants.REQUESTEDSTATE)
					|| path[3].equals(RMTConstants.BUNDLESTARTLEVEL)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
			throw new DmtException(nodePath, DmtException.METADATA_MISMATCH,
					"The specified node can not be set the value.");
		}
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"The given path indicates an interior node or a node which does not exist.");
	}

	public void deleteNode(String[] nodePath) throws DmtException {
		// NOT supported operation in Framework MO.
		throw new DmtException(nodePath, DmtException.METADATA_MISMATCH,
				"The specified node can not be deleted.");
	}

	public void setNodeType(String[] nodePath, String type) throws DmtException {
		// NOT supported operation in Framework MO.
		// do nothing, meta-data guarantees that type is "text/plain"
		if (type == null)
			return;
		if (!isLeafNode(nodePath))
			throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
					"Cannot set type property of interior nodes.");
	}

	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		// NOT supported operation in Framework MO.
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported.");
	}

	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		// NOT supported operation in Framework MO.
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
				"Cannot rename any node in the framework MO.");
	}

	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
		// NOT supported operation in Framework MO.
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Cannot copy nodes of Framework MO.");
	}


	protected class Operation {
		static final int ADD_OBJECT = 0;
		static final int DELETE_OBJECT = 1;
		static final int SET_VALUE = 2;

		private int operation;
		private String[] objectname;
		private DmtData data;

		protected Operation(int operation, String[] node) {
			this.operation = operation;
			this.objectname = node;
		}

		protected Operation(int operation, String[] node, DmtData data) {
			this.operation = operation;
			this.objectname = node;
			this.data = data;
		}

		protected int getOperation() {
			return operation;
		}

		protected String[] getObjectname() {
			return objectname;
		}

		protected DmtData getData() {
			return data;
		}
	}
}
