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
package org.osgi.impl.service.residentialmanagement.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.TransactionalDataSession;

/**
 * 
 * @author Shigekuni KONDO NTT Corporation
 */
class FrameworkReadWriteSession extends FrameworkReadOnlySession implements
		TransactionalDataSession {

	private Hashtable<String,BundleSubTree>	bundlesTableTmp				= null;
	private Hashtable<String,BundleSubTree>	bundlesTableSnap			= null;
	private Vector<Operation>				operations					= null;

	private Vector<BundleSubTree>			uninstallBundles			= null;
	private Vector<BundleSubTree>			updateBundles				= null;
	private Vector<BundleSubTree>			startBundles				= null;
	private Vector<BundleSubTree>			stopBundles					= null;
	private Vector<BundleSubTree>			resolveBundles				= null;
	private Vector<BundleSubTree>			stopAndRefreshBundles		= null;
	private Vector<BundleSubTree>			bundleStartLevelCue			= null;
	private BundleSubTree frameworkBs = null;
	private Hashtable<BundleSubTree,String>	restoreBundlesForUninstall	= null;
	private Hashtable<String,String>		restoreBundlesForUpdate		= null;
	private Hashtable<String,String>		restoreBundles				= null;
	private long			timeOut						= Long.parseLong(RMTConstants
																.getProperty(
			RMTConstants.TIMEOUT_FOR_SETSTARTLEVEL, "10000"));
	private long			waitTime					= Long.parseLong(RMTConstants
																.getProperty(
			RMTConstants.WAIT_TIME_FOR_SETSTARTLEVEL, "500"));

	FrameworkReadWriteSession(FrameworkPlugin plugin, BundleContext context,
			FrameworkReadOnlySession session) {
		super(plugin, context);
		this.bundlesTable = session.bundlesTable;
		operations = new Vector<>();
		bundlesTableTmp = new Hashtable<>();
		bundlesTableSnap = new Hashtable<>();
		operations = new Vector<>();
		uninstallBundles = new Vector<>();
		updateBundles = new Vector<>();
		startBundles = new Vector<>();
		stopBundles = new Vector<>();
		resolveBundles = new Vector<>();
		stopAndRefreshBundles = new Vector<>();
		bundleStartLevelCue = new Vector<>();
		restoreBundlesForUninstall = new Hashtable<>();
		restoreBundlesForUpdate = new Hashtable<>();
		restoreBundles = new Hashtable<>();
		frameworkBs = null;
	}

	@Override
	public void commit() throws DmtException {
		this.bundlesTableSnap = new Hashtable<>(this.bundlesTable);
		Iterator<Operation> i = operations.iterator();
		while (i.hasNext()) {
			Operation operation = i.next();
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
						BundleSubTree bs = this.bundlesTable
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
						BundleSubTree bs = this.bundlesTable
								.get(nodepath[nodepath.length - 2]);
						bs.setAutoStart(operation.getData().getBoolean());
					} else if (nodepath[nodepath.length - 1]
							.equals(RMTConstants.REQUESTEDSTATE)) {
						BundleSubTree bs = this.bundlesTable
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
						BundleSubTree bs = this.bundlesTable
								.get(nodepath[nodepath.length - 2]);
						int bundleStartLevel = operation.getData().getInt();
						bs.setStartLevel(bundleStartLevel);
						this.bundleStartLevelCue.add(bs);
					} else if (nodepath[nodepath.length - 1]
							.equals(RMTConstants.FRAMEWORKSTARTLEVEL)) {
						int flameworkStartLevel = operation.getData().getInt();
						Bundle sysBundle = context.getBundle(0);
						FrameworkStartLevel fs = sysBundle
								.adapt(FrameworkStartLevel.class);
						FrameworkListenerForStartLevel fls = new FrameworkListenerForStartLevel();
						fs.setStartLevel(flameworkStartLevel, new FrameworkListener[]{fls});
						long s = System.currentTimeMillis();
						while(!fls.flag){
							long n = System.currentTimeMillis();
							if((n-s)>=timeOut)				
								break;
							Thread.sleep(waitTime);
						}
					} else if (nodepath[nodepath.length - 1]
							.equals(RMTConstants.INITIALBUNDLESTARTLEVEL)) {
						int initialBundleStartlevel = operation.getData()
								.getInt();
						Bundle sysBundle = context.getBundle(0);
						FrameworkStartLevel fs = sysBundle
								.adapt(FrameworkStartLevel.class);
						fs.setInitialBundleStartLevel(initialBundleStartlevel);
					}
				}
			} catch (Exception e) {
				bundlesTable = new Hashtable<>(this.bundlesTableSnap);
				rollback();
				throw new DmtException(operation.getObjectname(),
						DmtException.COMMAND_FAILED,
						"The operation encountered problems.");
			}
		}

		// Operation of uninstall and update
		Iterator<BundleSubTree> stopunit = this.uninstallBundles.iterator();
		while (stopunit.hasNext()) {
			BundleSubTree bs = stopunit.next();
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
		Iterator<BundleSubTree> stopupit = this.updateBundles.iterator();
		while (stopupit.hasNext()) {
			BundleSubTree bs = stopupit.next();
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
		Iterator<BundleSubTree> uninstallit = this.uninstallBundles.iterator();
		while (uninstallit.hasNext()) {
			BundleSubTree bs = uninstallit.next();
			String location = null;
			try {
				location = bs.getLocation();
				String state = this.restoreBundles.get(location);
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
		Iterator<BundleSubTree> updateit = this.updateBundles.iterator();
		while (updateit.hasNext()) {
			BundleSubTree bs = updateit.next();
			String urlStr = bs.getURL();
			URL url;
			String location = null;
			try {
				location = bs.getLocation();
				String state = this.restoreBundles.get(location);
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
		for (Enumeration<String> keys = this.bundlesTableTmp.keys(); keys
				.hasMoreElements();) {
			String key = keys.nextElement();
			BundleSubTree bs = this.bundlesTable.get(key);
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
		Vector<Bundle> bundles = new Vector<>();
		Iterator<BundleSubTree> refreshupi = this.updateBundles.iterator();
		while (refreshupi.hasNext()) {
			BundleSubTree bs = refreshupi.next();
			bundles.add(bs.getBundleObj());
		}
		for (Enumeration<String> keys = this.bundlesTableTmp.keys(); keys
				.hasMoreElements();) {
			String key = keys.nextElement();
			BundleSubTree bs = this.bundlesTable.get(key);
			bundles.add(bs.getBundleObj());
		}
		Iterator<BundleSubTree> refreshuni = this.uninstallBundles.iterator();
		while (refreshuni.hasNext()) {
			BundleSubTree bs = refreshuni.next();
			bundles.add(bs.getBundleObj());
		}
		Bundle sysBundle = context.getBundle(0);
		FrameworkWiring fw = sysBundle
				.adapt(FrameworkWiring.class);
        fw.refreshBundles(bundles);

		// setting bundle's StartLevel
		Iterator<BundleSubTree> startLevelit = this.bundleStartLevelCue
				.iterator();
		while (startLevelit.hasNext()) {
			BundleSubTree bs = startLevelit.next();
			int startLevel = bs.getStartLevelTmp();
			BundleStartLevel sl = bs.getBundleObj().adapt(
					BundleStartLevel.class);
			sl.setStartLevel(startLevel);
			long s = System.currentTimeMillis();
			while(!(sl.getStartLevel()==startLevel)){
				long n = System.currentTimeMillis();
				if((n-s)>=timeOut)				
					break;
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					restore();
					throw new DmtException("Set bundle start level operation failure",
							DmtException.COMMAND_FAILED,
							"The operation of install encountered problems.");
				}
			}
		}

		// starting bundles
		Iterator<BundleSubTree> startit = this.startBundles.iterator();
		while (startit.hasNext()) {
			BundleSubTree bs = startit.next();
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
		Iterator<BundleSubTree> stopit = this.stopBundles.iterator();
		while (stopit.hasNext()) {
			BundleSubTree bs = stopit.next();
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
		Iterator<BundleSubTree> resolveit = this.resolveBundles.iterator();
		@SuppressWarnings("hiding")
		Vector<Bundle> resolveBundles = new Vector<>();
		while (resolveit.hasNext()) {
			BundleSubTree bs = resolveit.next();
			String location = bs.getLocation();
			String state = bs.getState();
			resolveBundles.add(bs.getBundleObj());
			if (this.restoreBundles.get(location) == null)
				this.restoreBundles.put(location, state);
		}
		fw.resolveBundles(resolveBundles);

		// stopping and refreshing bundles
		Iterator<BundleSubTree> stopandrefit = this.stopAndRefreshBundles
				.iterator();
		Vector<Bundle> refreshBundles = new Vector<>();
		while (stopandrefit.hasNext()) {
			BundleSubTree bs = stopandrefit.next();
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
        fw.refreshBundles(refreshBundles);
		rollback();

		// framework --> on thread
		if (frameworkBs != null) {
			FrameworkUpdateThread fut = new FrameworkUpdateThread();
			fut.start();
		}
	}

	class FrameworkUpdateThread extends Thread {
		@SuppressWarnings("hiding")
		private int	waitTime	= Integer
										.parseInt(RMTConstants
												.getProperty(
				RMTConstants.WAIT_TIME_FOR_FRAMEWORK_UPDATE, "5000"));

		FrameworkUpdateThread() {
		}

		@Override
		public void run() {
			try {
				wait(waitTime);
			} catch (InterruptedException e1) {
				// ignore
			}
			Framework framework = (Framework) context.getBundle(0);
			try {
				framework.update();
			} catch (BundleException e) {
				BundleSubTree bs = bundlesTable
						.get(Constants.SYSTEM_BUNDLE_LOCATION);
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
			}
		}
	}

	class FrameworkListenerForStartLevel implements FrameworkListener{
		public boolean flag = false;
		@Override
		public void frameworkEvent(FrameworkEvent event) {
			if(event.getType()==FrameworkEvent.STARTLEVEL_CHANGED)
				flag = true;
		}
	}
	
	protected void restore() {
		Vector<Bundle> refreshBundles = new Vector<>();
		Bundle sysBundle = context.getBundle(0);
		FrameworkWiring fw = sysBundle
				.adapt(FrameworkWiring.class);
		for (Enumeration<BundleSubTree> keys = this.restoreBundlesForUninstall
				.keys(); keys
				.hasMoreElements();) {
			BundleSubTree bs = keys.nextElement();
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
					// ignore
				}
			} else if (urlStr == null) {
				bs = null;
			}
		}
		for (Enumeration<String> keys = this.restoreBundlesForUpdate
				.keys(); keys
				.hasMoreElements();) {
			String location = keys.nextElement();
			BundleSubTree bs = this.bundlesTableSnap
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
					// ignore
				}
			}
		}
        fw.refreshBundles(refreshBundles);
		refreshBundles = new Vector<>();

		for (Enumeration<String> keys = this.restoreBundles.keys(); keys
				.hasMoreElements();) {
			try {
				String location = keys.nextElement();
				BundleSubTree bs = this.bundlesTable
						.get(location);
				String afterState = bs.getState();
				String beforeState = this.restoreBundles.get(location);
				if (afterState.equals(beforeState))
					continue;
				if (beforeState.equals(RMTConstants.UNINSTALLED)) {
					bs.getBundleObj().uninstall();
				} else if (beforeState.equals(RMTConstants.INSTALLED)) {
					bs.getBundleObj().stop();
					refreshBundles.add(bs.getBundleObj());
                        fw.refreshBundles(refreshBundles);
				} else if (beforeState.equals(RMTConstants.RESOLVED)) {
					if (bs.getState().equals(RMTConstants.INSTALLED)) {
						@SuppressWarnings("hiding")
						Vector<Bundle> resolveBundles = new Vector<>();
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
				// ignore
			}
		}
	}

	@Override
	public void rollback() throws DmtException {
		operations = new Vector<>();
		bundlesTableTmp = new Hashtable<>();
		bundlesTableSnap = new Hashtable<>();
		operations = new Vector<>();
		uninstallBundles = new Vector<>();
		updateBundles = new Vector<>();
		startBundles = new Vector<>();
		stopBundles = new Vector<>();
		resolveBundles = new Vector<>();
		stopAndRefreshBundles = new Vector<>();
		bundleStartLevelCue = new Vector<>();
		restoreBundlesForUninstall = new Hashtable<>();
		restoreBundlesForUpdate = new Hashtable<>();
		restoreBundles = new Hashtable<>();
		frameworkBs = null;
	}

	@Override
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
			BundleSubTree bs = new BundleSubTree(path[2]);
			this.bundlesTableTmp.put(Uri.encode(path[2]), bs);
			return;
		}

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"There is no appropriate node for the given path.");
	}

	@Override
	public void createLeafNode(String[] nodePath, DmtData data, String mimeType)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
				"There is no leaf node to be created in the framework subtree.");

	}

	@Override
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

	@Override
	public void deleteNode(String[] nodePath) throws DmtException {
		// NOT supported operation in Framework MO.
		throw new DmtException(nodePath, DmtException.METADATA_MISMATCH,
				"The specified node can not be deleted.");
	}

	@Override
	public void setNodeType(String[] nodePath, String type) throws DmtException {
		// NOT supported operation in Framework MO.
		// do nothing, meta-data guarantees that type is "text/plain"
		if (type == null)
			return;
		if (!isLeafNode(nodePath))
			throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
					"Cannot set type property of interior nodes.");
	}

	@Override
	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		// NOT supported operation in Framework MO.
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported.");
	}

	@Override
	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		// NOT supported operation in Framework MO.
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
				"Cannot rename any node in the framework MO.");
	}

	@Override
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
