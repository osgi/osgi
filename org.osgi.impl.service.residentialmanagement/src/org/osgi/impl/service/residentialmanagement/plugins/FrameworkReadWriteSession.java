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
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
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

	private Hashtable bundlesTableCopy = null;
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
	
	FrameworkReadWriteSession(FrameworkPlugin plugin, BundleContext context, FrameworkReadOnlySession session){
		super(plugin, context);
		this.bundlesTable = session.bundlesTable;
		operations = new Vector();
		bundlesTableCopy = new Hashtable();
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
			try{
				if (operation.getOperation() == Operation.ADD_OBJECT) {
					String[] path = operation.getObjectname();
					if (path.length == 3 && path[1].equals(BUNDLE)) {
						BundleSubTree bs = new BundleSubTree(path[2]);
						this.bundlesTable.put(Uri.encode(path[2]), bs);
					}
				} else if (operation.getOperation() == Operation.SET_VALUE) {
					String[] nodepath = operation.getObjectname();					
					if (nodepath[nodepath.length - 1].equals(URL)) {
						BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(nodepath[nodepath.length - 2]);
						bs.setURL(operation.getData().getString());
						if(nodepath[nodepath.length - 2].equals(Constants.SYSTEM_BUNDLE_LOCATION)){
							this.frameworkBs = bs;
						}else if(bs.getCreateFlag()){
							this.updateBundles.add(bs);
						}
					} else if (nodepath[nodepath.length - 1].equals(AUTOSTART)) {
						BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(nodepath[nodepath.length - 2]);
						bs.setAutoStart(operation.getData().getBoolean());
					} else if (nodepath[nodepath.length - 1].equals(REQUESTEDSTATE)) {
						BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(nodepath[nodepath.length - 2]);
						String requestedState = operation.getData().getString();
						bs.setRequestedState(requestedState);
						if(requestedState.equals(ACTIVE)){
							this.startBundles.add(bs);
						} else if(requestedState.equals(UNINSTALLED)){
							this.uninstallBundles.add(bs);
						} else if(requestedState.equals(RESOLVED)&&bs.getBundleObj().getState()==2){
							this.resolveBundles.add(bs);
						} else if(requestedState.equals(RESOLVED)&&bs.getBundleObj().getState()!=2){
							this.stopBundles.add(bs);
						} else if(requestedState.equals(INSTALLED)){
							this.stopAndRefreshBundles.add(bs);
						}
					} else if (nodepath[nodepath.length - 1].equals(BUNDLESTARTLEVEL)
							&& nodepath.length==4) {
						BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(nodepath[nodepath.length - 2]);
						int bundleStartLevel = operation.getData().getInt();
						bs.setStartLevel(bundleStartLevel);
						this.bundleStartLevelCue.add(bs);
					} else if (nodepath[nodepath.length - 1].equals(FRAMEWORKSTARTLEVEL)) {
						int flameworkStartLevel = operation.getData().getInt();
						Bundle sysBundle = context.getBundle(0);
						FrameworkStartLevel fs = (FrameworkStartLevel)sysBundle.adapt(FrameworkStartLevel.class);
						fs.setStartLevel(flameworkStartLevel, null);
					} else if (nodepath[nodepath.length - 1].equals(INITIALBUNDLESTARTLEVEL)) {
						int initialBundleStartlevel = operation.getData().getInt();
						Bundle sysBundle = context.getBundle(0);
						FrameworkStartLevel fs = (FrameworkStartLevel)sysBundle.adapt(FrameworkStartLevel.class);
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
		
		//Operation of uninstall and update
		Iterator stopunit = this.uninstallBundles.iterator();
		while(stopunit.hasNext()){
			BundleSubTree bs = (BundleSubTree)stopunit.next();
			try {
				String location = bs.getLocation();
				String state = bs.getState();
				bs.getBundleObj().stop();
				this.restoreBundles.put(location, state);
				//this.restoreBundlesForUninstall.put(bs, state);
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
			}
		}
		Iterator stopupit = this.updateBundles.iterator();
		while(stopupit.hasNext()){
			BundleSubTree bs = (BundleSubTree)stopupit.next();
			try {
				String location = bs.getLocation();
				String state = bs.getState();
				bs.getBundleObj().stop();
				this.restoreBundles.put(location, state);
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
			}
		}
		Iterator uninstallit = this.uninstallBundles.iterator();
		while(uninstallit.hasNext()){
			BundleSubTree bs = (BundleSubTree)uninstallit.next();
			try {
				String location = bs.getLocation();
				String state = (String)this.restoreBundles.get(location);
				bs.getBundleObj().uninstall();
				this.restoreBundlesForUninstall.put(bs, state);
				this.restoreBundles.remove(location);
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
			}
		}
		Iterator updateit = this.updateBundles.iterator();
		while(updateit.hasNext()){
			BundleSubTree bs = (BundleSubTree)updateit.next();
			String urlStr = bs.getURL();
			URL url;
			try {
				String location = bs.getLocation();
				String state = (String)this.restoreBundles.get(location);
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
			}
		}
		
		//Operation of bundle installation
		for(Enumeration keys = this.bundlesTableTmp.keys(); keys.hasMoreElements();) {
			String key = (String)keys.nextElement();
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(key);
			String urlStr = bs.getURL();
			try{
				String location = bs.getLocation();
				String state = UNINSTALLED;
				URL url = new URL(urlStr);
				InputStream is = url.openStream();
				context.installBundle(key, is);
				this.restoreBundles.put(location, state);
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
			}
		}
		
		//refreshing installed bundles and updated bundles
		Vector bundles = new Vector();
		Iterator refreshupi = this.updateBundles.iterator();
		int refreshBundlesNumber = 0;
		while(refreshupi.hasNext()){
			BundleSubTree bs = (BundleSubTree)refreshupi.next();
			bundles.add(bs.getBundleObj());
			refreshBundlesNumber++;
		}
		for(Enumeration keys = this.bundlesTableTmp.keys(); keys.hasMoreElements();) {
			String key = (String)keys.nextElement();
			BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(key);
			bundles.add(bs.getBundleObj());
			refreshBundlesNumber++;
		}
		Bundle sysBundle = context.getBundle(0);
		FrameworkWiring fw = (FrameworkWiring)sysBundle.adapt(FrameworkWiring.class);
		fw.refreshBundles(bundles, null);
		
		//setting bundle's StartLevel
		Iterator startLevelit = this.bundleStartLevelCue.iterator();
		while(startLevelit.hasNext()){
			BundleSubTree bs = (BundleSubTree)startLevelit.next();
			int startLevel = bs.getStartLevelTmp();
			BundleStartLevel sl = (BundleStartLevel)bs.getBundleObj().adapt(BundleStartLevel.class);
			sl.setStartLevel(startLevel);
		}
		
		//starting bundles
		Iterator startit = this.startBundles.iterator();
		while(startit.hasNext()){
			BundleSubTree bs = (BundleSubTree)startit.next();
			boolean auto = bs.getAutoStart();
			try{
				String location = bs.getLocation();
				String state = bs.getState();
				if(auto)
					bs.getBundleObj().start();
				else
					bs.getBundleObj().start(Bundle.START_TRANSIENT);
				if(this.restoreBundles.get(location)==null)
					this.restoreBundles.put(location, state);
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
			}
		}
		
		//stopping bundles
		Iterator stopit = this.stopBundles.iterator();
		while(stopit.hasNext()){
			BundleSubTree bs = (BundleSubTree)stopit.next();
			try {
				String location = bs.getLocation();
				String state = bs.getState();
				bs.getBundleObj().stop();
				if(this.restoreBundles.get(location)==null)
					this.restoreBundles.put(location, state);
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
			}
		}
		
		//resolving bundles
		Iterator resolveit = this.resolveBundles.iterator();
		Vector resolveBundles = new Vector();
		while(resolveit.hasNext()){
			BundleSubTree bs = (BundleSubTree)resolveit.next();
			String location = bs.getLocation();
			String state = bs.getState();
			resolveBundles.add(bs.getBundleObj());
			if(this.restoreBundles.get(location)==null)
				this.restoreBundles.put(location, state);
		}
		fw.resolveBundles(resolveBundles);
		
		//stopping and refreshing bundles
		Iterator stopandrefit = this.stopAndRefreshBundles.iterator();
		Vector refreshBundles = new Vector();
		while(stopandrefit.hasNext()){
			BundleSubTree bs = (BundleSubTree)stopandrefit.next();
			try {
				String location = bs.getLocation();
				String state = bs.getState();
				bs.getBundleObj().stop();
				if(this.restoreBundles.get(location)==null)
					this.restoreBundles.put(location, state);
			} catch (BundleException e) {
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
				restore();
			}
			refreshBundles.add(bs.getBundleObj());
		}
		fw.refreshBundles(refreshBundles, null);
		rollback();
		
		//framework --> on thread
		if(frameworkBs!=null){
			FrameworkUpdateThread fut = new FrameworkUpdateThread();
			fut.start();
		}
	}
	
	class FrameworkUpdateThread extends Thread{
		FrameworkUpdateThread(){
		}
		public void run(){
			try {
				wait(5000);
			} catch (InterruptedException e1) {
			}
			Framework framework = (Framework)context.getBundle(0);
			try {
				framework.update();
			} catch (BundleException e) {
				BundleSubTree bs = (BundleSubTree)bundlesTable.get(Constants.SYSTEM_BUNDLE_LOCATION);
				bs.setFaultMassage(e.getMessage());
				bs.setFaultType(e.getType());
			}
		}
	}
	
	protected void restore(){
		Vector refreshBundles = new Vector();
		Bundle sysBundle = context.getBundle(0);
		FrameworkWiring fw = (FrameworkWiring)sysBundle.adapt(FrameworkWiring.class);
		for(Enumeration keys = this.restoreBundlesForUninstall.keys(); keys.hasMoreElements();) {
			BundleSubTree bs = (BundleSubTree)keys.nextElement();
			String urlStr = bs.getURL();
			if(urlStr!=null){
				try{
					URL url = new URL(urlStr);
					InputStream is = url.openStream();
					Bundle installedBundle = context.installBundle(bs.getLocation(), is);
					refreshBundles.add(installedBundle);
					this.restoreBundles.put(bs.getLocation(), this.restoreBundlesForUninstall.get(bs));
					bs = null;
				} catch (Exception e){
				}
			} else if(urlStr==null){
				bs = null;
			}
		}
		for(Enumeration keys = this.restoreBundlesForUpdate.keys(); keys.hasMoreElements();) {
			String location = (String)keys.nextElement();
			BundleSubTree bs = (BundleSubTree)this.bundlesTableSnap.get(location);
			String urlStr = bs.getURL();
			if(urlStr!=null){
				try{
					URL url = new URL(urlStr);
					InputStream is = url.openStream();
					bs.getBundleObj().update(is);
					refreshBundles.add(bs.getBundleObj());
					this.restoreBundles.put(location, this.restoreBundlesForUpdate.get(location));
					bs = null;
				} catch (Exception e){
				}
			}
		}
		fw.refreshBundles(refreshBundles, null);
		refreshBundles = new Vector();
		
		for(Enumeration keys = this.restoreBundles.keys(); keys.hasMoreElements();) {
			try{
				String location = (String)keys.nextElement();
				BundleSubTree bs = (BundleSubTree)this.bundlesTable.get(location);
				String afterState = bs.getState();
				String beforeState = (String)this.restoreBundles.get(location);
				if(afterState.equals(beforeState))
					continue;
				if(beforeState.equals(UNINSTALLED)){
					bs.getBundleObj().uninstall();
				} else if(beforeState.equals(INSTALLED)){
					bs.getBundleObj().stop();
					refreshBundles.add(bs.getBundleObj());
					fw.refreshBundles(refreshBundles, null);
				} else if(beforeState.equals(RESOLVED)){
					if(bs.getState().equals(INSTALLED)){
						Vector resolveBundles = new Vector();
						resolveBundles.add(bs.getBundleObj());
						fw.resolveBundles(resolveBundles);
					} else {
						bs.getBundleObj().stop();
					}
				} else if(beforeState.equals(ACTIVE)){
					boolean auto = bs.getAutoStart();
					if(auto)
						bs.getBundleObj().start();
					else
						bs.getBundleObj().start(Bundle.START_TRANSIENT);
				}
			} catch (Exception e){
			}
		}
	}

	public void rollback() throws DmtException {
		operations = new Vector();
		bundlesTableCopy = new Hashtable();
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
		if (path.length == 3 && path[1].equals(BUNDLE)) {
			if(bundlesTable.get(path[2])!=null)
				throw new DmtException(nodePath,
						DmtException.NODE_ALREADY_EXISTS,
						"A given node already exists in the framework MO.");
			operations.add(new Operation(Operation.ADD_OBJECT, path));
			if(bundlesTableCopy.size()==0)
				bundlesTableCopy = (Hashtable) bundlesTable.clone();
			BundleSubTree bs = new BundleSubTree(path[2]);
			this.bundlesTableCopy.put(Uri.encode(path[2]), bs);
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
			if (path[1].equals(FRAMEWORKSTARTLEVEL)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
			if (path[1].equals(INITIALBUNDLESTARTLEVEL)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
		}
		
		if (path.length == 4) {
			if (path[3].equals(URL)
					|| path[3].equals(AUTOSTART)
					|| path[3].equals(REQUESTEDSTATE)
					|| path[3].equals(BUNDLESTARTLEVEL)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
			throw new DmtException(nodePath, DmtException.METADATA_MISMATCH,
					"The specified node can not be set the value.");
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified node does not exist in the framework object.");
	}

	public void deleteNode(String[] nodePath) throws DmtException {
		//NOT supported operation in Framework MO.
		throw new DmtException(nodePath, DmtException.METADATA_MISMATCH,
				"The specified node can not be deleted.");
	}

	public void setNodeType(String[] nodePath, String type) throws DmtException {
		//NOT supported operation in Framework MO.
		// do nothing, meta-data guarantees that type is "text/plain"
		if (type == null)
			return;
		if (!isLeafNode(nodePath))
			throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
					"Cannot set type property of interior nodes.");
	}

	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		//NOT supported operation in Framework MO.
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported.");
	}

	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		//NOT supported operation in Framework MO.
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
				"Cannot rename any node in the framework MO.");
	}

	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
		//NOT supported operation in Framework MO.
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Cannot copy nodes of Framework MO.");
	}

	// ----- Overridden methods to provide updated information -----//

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);		
		if(bundlesTableCopy.size()==0)
			bundlesTableCopy = (Hashtable) bundlesTable.clone();

		if (path.length == 1) {
			String[] children = new String[4];
			children[0] = FRAMEWORKSTARTLEVEL;
			children[1] = INITIALBUNDLESTARTLEVEL;
			children[2] = BUNDLE;
			children[3] = PROPERTY;
			return children;
		}

		if (path.length == 2) {
			if (path[1].equals(PROPERTY)) {
				if (properties.size() == 0)
					return new String[0];
				String[] children = new String[properties.size()];
				int i = 0;
				for (Enumeration keys = properties.keys(); keys.hasMoreElements(); i++) {
					children[i] = (String) keys.nextElement();
				}
				return children;
			}

			if (path[1].equals(BUNDLE)) {
				if (bundlesTableCopy.size() == 0)
					return new String[0];
				String[] children = new String[bundlesTableCopy.size()];
				int i = 0;
				for (Enumeration keys = bundlesTableCopy.keys(); keys.hasMoreElements(); i++) {
					//children[i] = Uri.decode((String) keys.nextElement());
					children[i] = (String) keys.nextElement();
				}
				return children;
			}
		}
		
		if (path.length == 3 && path[1].equals(BUNDLE)) {
			if(this.bundlesTableCopy.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
				Node node = bs.getLocatonNode();
				return node.getChildNodeNames();
			}
		}

		if(path.length == 4){
			if (path[3].equals(ENTRIES)) {
				if(this.bundlesTableCopy.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
					Vector entries = bs.getEntries();
					String[] children = new String[entries.size()];
					for(int i=0;entries.size()<i;i++){
						children[i] = Integer.toString(i);
					}
					return children;
				}
			}	
			if (path[3].equals(SIGNERS)) {
				if(this.bundlesTableCopy.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
					Vector entries = bs.getSigners();
					String[] children = new String[entries.size()];
					for(int i=0;entries.size()<i;i++){
						children[i] = Integer.toString(i);
					}
					return children;
				}
			}			
			if (path[3].equals(WIRES)) {
				if(this.bundlesTableCopy.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
					Map wires = bs.getWires();
					String[] children = new String[wires.size()];
					Iterator it = wires.keySet().iterator();
					for(int i=0;it.hasNext();i++){
						children[i] = (String)it.next();
					}
					return children;
				}
			}
		}
			
		if(path.length == 5){
			if (path[3].equals(ENTRIES)) {
				if(this.bundlesTableCopy.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
					Vector entries = bs.getEntries();
					try{
						entries.get(Integer.parseInt(path[4]));
						String[] children = new String[3];
						children[0] = PATH;
						children[1] = CONTENT;
						children[2] = ENTRIESINSTANCEID;
						return children;
					}catch(ArrayIndexOutOfBoundsException ae){
						String[] children = new String[0];
						return children;
					}
				}
			}			
			if (path[3].equals(SIGNERS)) {
				if(this.bundlesTableCopy.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
					Vector signers = bs.getSigners();
					try{
						signers.get(Integer.parseInt(path[4]));
						String[] children = new String[3];
						children[0] = CERTIFICATECHAIN;
						children[1] = ISTRUSTED;
						children[2] = SIGNERSINSTANCEID;
						return children;
					}catch(ArrayIndexOutOfBoundsException ae){
						String[] children = new String[0];
						return children;
					}
				}
			}			
			if (path[3].equals(WIRES)) {
				if(this.bundlesTableCopy.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
					Map wires = bs.getWires();
					Vector list = (Vector)wires.get(path[4]);
					String[] children = new String[list.size()];
					for(int i=0;i<list.size();i++){
						children[i] = Integer.toString(i);
					}
					return children;
				}
			}
		}
		
		if(path.length == 6){
			if (path[3].equals(SIGNERS)) {
				if(this.bundlesTableCopy.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
					Vector signers = bs.getSigners();
					try{
						SignersSubtree ss = (SignersSubtree)signers.get(Integer.parseInt(path[4]));
						Vector chainList = ss.getCertifitateChainList();
						String[] children = new String[chainList.size()];
						for(int i=0;chainList.size()<i;i++){
							children[i] = Integer.toString(i);
						}
						return children;
					}catch(ArrayIndexOutOfBoundsException ae){
						String[] children = new String[0];
						return children;
					}
				}
			}			
			if (path[3].equals(WIRES)) {
				if(this.bundlesTableCopy.get(path[2])!=null){
					BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
					if(bs.getWires()!=null){
						Vector vec =(Vector)((Map)bs.getWires()).get(path[4]);
						if(vec!=null){
							try{
								vec.get(Integer.parseInt(path[5]));
								String[] children = new String[6];
								children[0] = NAMESPACE;
								children[1] = PROVIDER;
								children[2] = REQUIRER;
								children[3] = WIRESINSTANCEID;
								children[4] = REQUIREMENT;
								children[5] = CAPABILITY;
								return children;
							}catch(ArrayIndexOutOfBoundsException ae){
								String[] children = new String[0];
								return children;
							}
						}
					}
				}
			}
		}
		
		if(path.length == 7 && path[3].equals(WIRES)) {
			if(this.bundlesTableCopy.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
				if(bs.getWires()!=null){
					Vector vec =(Vector)((Map)bs.getWires()).get(path[4]);
					if(vec!=null){
						try{
							vec.get(Integer.parseInt(path[5]));
							if(path[6].equals(REQUIREMENT)){
								String[] children = new String[3];
								children[0] = FILTER;
								children[1] = REQUIREMENTDIRECTIVE;
								children[2] = REQUIREMENTATTRIBUTE;
								return children;
							}
							if(path[6].equals(CAPABILITY)){
								String[] children = new String[2];
								children[0] = CAPABILITYDIRECTIVE;
								children[1] = CAPABILITYATTRIBUTE;
								return children;
							}
						}catch(ArrayIndexOutOfBoundsException ae){
							String[] children = new String[0];
							return children;
						}
					}
				}
			}
		}

		if(path.length == 8 && path[3].equals(WIRES)) {
			if(this.bundlesTableCopy.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
				if(bs.getWires()!=null){
					Vector vec =(Vector)((Map)bs.getWires()).get(path[4]);
					if(vec!=null){
						try{
							WiresSubtree ws = (WiresSubtree)vec.get(Integer.parseInt(path[5]));
							if(path[6].equals(REQUIREMENT)&&path[7].equals(REQUIREMENTDIRECTIVE)){
								Map requirementDerective = ws.getRequirementDirective();
								String[] children = new String[requirementDerective.size()];
								Iterator it = requirementDerective.keySet().iterator();
								for(int i=0;it.hasNext();i++){
									children[i] = (String)it.next();
								}
								return children;
							}
							if(path[6].equals(REQUIREMENT)&&path[7].equals(REQUIREMENTATTRIBUTE)){
								Map requirementAttribute = ws.getRequirementAttribute();
								String[] children = new String[requirementAttribute.size()];
								Iterator it = requirementAttribute.keySet().iterator();
								for(int i=0;it.hasNext();i++){
									children[i] = (String)it.next();
								}
								return children;
							}
							if(path[6].equals(CAPABILITY)&&path[7].equals(CAPABILITYDIRECTIVE)){
								Map capabilityDerective = ws.getCapabilityDirective();
								String[] children = new String[capabilityDerective.size()];
								Iterator it = capabilityDerective.keySet().iterator();
								for(int i=0;it.hasNext();i++){
									children[i] = (String)it.next();
								}
								return children;
							}
							if(path[6].equals(CAPABILITY)&&path[7].equals(CAPABILITYATTRIBUTE)){
								Map capabilityAttribute = ws.getCapabilityAttribute();
								String[] children = new String[capabilityAttribute.size()];
								Iterator it = capabilityAttribute.keySet().iterator();
								for(int i=0;it.hasNext();i++){
									children[i] = (String)it.next();
								}
								return children;
							}
						}catch(ArrayIndexOutOfBoundsException ae){
							String[] children = new String[0];
							return children;
						}
					}
				}
			}
		}

		// other case
		String[] children = new String[0];
		return children;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		if(bundlesTableCopy.size()==0)
			bundlesTableCopy = (Hashtable) bundlesTable.clone();

		if (path.length == 1)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");
		
		if (path.length == 2){
			Bundle sysBundle = context.getBundle(0);
			FrameworkStartLevel fs = (FrameworkStartLevel)sysBundle.adapt(FrameworkStartLevel.class);
			if (path[1].equals(FRAMEWORKSTARTLEVEL)){
				int st = fs.getStartLevel();
				return new DmtData(st);
			}
			if (path[1].equals(INITIALBUNDLESTARTLEVEL)){
				int ist = fs.getInitialBundleStartLevel();
				return new DmtData(ist);
			}
		}

		if (path.length == 3) {
			if (path[1].equals(PROPERTY)){
				String value = (String)properties.get(path[2]);
				return new DmtData(value);
			}
		}
		
		if (path.length == 4) {
			BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
			if(bs!=null&&bs.getLocatonNode().findNode(new String[]{path[3]})!=null){
				if (path[3].equals(URL))
					return new DmtData(bs.getURL());
				if (path[3].equals(AUTOSTART))
					return new DmtData(bs.getAutoStart());
				if (path[3].equals(FAULTTYPE))
					return new DmtData(bs.getFaultType());
				if (path[3].equals(FAULTMESSAGE))
					return new DmtData(bs.getFaultMassage());
				if (path[3].equals(BUNDLEID))
					return new DmtData(bs.getBundleId());
				if (path[3].equals(SYMBOLICNAME))
					return new DmtData(bs.getSymbolicNmae());
				if (path[3].equals(VERSION))
					return new DmtData(bs.getVersion());
				if (path[3].equals(LOCATION))
					return new DmtData(bs.getLocation());
				if (path[3].equals(STATE))
					return new DmtData(bs.getState());
				if (path[3].equals(REQUESTEDSTATE))
					return new DmtData(bs.getRequestedState());
				if (path[3].equals(LASTMODIFIED))
					return new DmtData(bs.getLastModified());
				if (path[3].equals(BUNDLESTARTLEVEL))
					return new DmtData(bs.getStartLevel());
				if (path[3].equals(BUNDLEINSTANCEID))
					return new DmtData(bs.getInstanceId());
			}
		}
		
		if (path.length == 5){
			BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
			if(bs!=null&&bs.getLocatonNode().findNode(new String[]{path[3]})!=null){
				if (path[3].equals(BUNDLETYPE)&&path[4].equals("0")){
					return new DmtData(bs.getBundleType());
				}
				if (path[3].equals(HEADERS)){
					Dictionary dic = bs.getHeaders();
					String value = (String)dic.get(path[4]);
					if(value!=null)
						return new DmtData(value);
				}
			}
		}
		
		if (path.length == 6){
			BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
			if(bs!=null&&bs.getLocatonNode().findNode(new String[]{path[3]})!=null){
				if (path[3].equals(ENTRIES)){
					Vector vec = (Vector)bs.getEntries();
					EntrySubtree es = (EntrySubtree)vec.get(Integer.parseInt(path[4]));
					if(path[5].equals(PATH))
						return new DmtData(es.getPath());
					if(path[5].equals(CONTENT))
						return new DmtData(es.getContent());
					if(path[5].equals(ENTRIESINSTANCEID))
						return new DmtData(es.getInstanceId());
				}
				if (path[3].equals(SIGNERS)){
					Vector vec = (Vector)bs.getSigners();
					SignersSubtree ss = (SignersSubtree)vec.get(Integer.parseInt(path[4]));
					if(path[5].equals(ISTRUSTED))
						return new DmtData(ss.isTrusted());
					if(path[5].equals(SIGNERSINSTANCEID))
						return new DmtData(ss.getInstanceId());
				}
			}
		}
		
		if (path.length == 7){
			BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
			if(bs!=null&&bs.getLocatonNode().findNode(new String[]{path[3]})!=null){
				if (path[3].equals(SIGNERS)){
					Vector vec = (Vector)bs.getSigners();
					SignersSubtree ss = (SignersSubtree)vec.get(Integer.parseInt(path[4]));
					if(path[5].equals(CERTIFICATECHAIN)){
						Vector cvec = (Vector)ss.getCertifitateChainList();
						String name = (String)cvec.get(Integer.parseInt(path[6]));
						return new DmtData(name);
					}
				}
				if (path[3].equals(WIRES)){
					Map wires = bs.getWires();
					Vector vec= (Vector)wires.get(path[4]);
					WiresSubtree ws;
					if(vec!=null){
						try{
							ws = (WiresSubtree)vec.get(Integer.parseInt(path[5]));
						}catch(ArrayIndexOutOfBoundsException ae){
							throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
							"The specified leaf node does not exist in the framework object.");
						}
						if(path[6].equals(NAMESPACE))
							return new DmtData(ws.getNameSpace());
						if(path[6].equals(PROVIDER))
							return new DmtData(ws.getProvider());
						if(path[6].equals(REQUIRER))
							return new DmtData(ws.getRequirer());
						if(path[6].equals(WIRESINSTANCEID))
							return new DmtData(ws.getInstanceId());
					}
				}
			}
		}
		
		if (path.length == 8){
			BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
			if(bs!=null&&bs.getLocatonNode().findNode(new String[]{path[3]})!=null){
				if (path[3].equals(WIRES)){
					Map wires = bs.getWires();
					Vector vec= (Vector)wires.get(path[4]);
					WiresSubtree ws;
					if(vec!=null){
						try{
							ws = (WiresSubtree)vec.get(Integer.parseInt(path[5]));
						}catch(ArrayIndexOutOfBoundsException ae){
							throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
							"The specified leaf node does not exist in the framework object.");
						}
						if(path[6].equals(REQUIREMENT)&&path[7].equals(FILTER))
							return new DmtData(ws.getFilter());
					}
				}
			}
		}
		
		if (path.length == 9){
			BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
			if(bs!=null&&bs.getLocatonNode().findNode(new String[]{path[3]})!=null){
				if (path[3].equals(WIRES)){
					Map wires = bs.getWires();
					Vector vec= (Vector)wires.get(path[4]);
					WiresSubtree ws;
					if(vec!=null){
						try{
							ws = (WiresSubtree)vec.get(Integer.parseInt(path[5]));
						}catch(ArrayIndexOutOfBoundsException ae){
							throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
							"The specified leaf node does not exist in the framework object.");
						}
						if(path[6].equals(REQUIREMENT)){
							if(path[7].equals(REQUIREMENTDIRECTIVE)){
								Map rd = ws.getRequirementDirective();
								if(!rd.isEmpty())
									return new DmtData(rd.get(path[8]).toString()); 
							}
							if(path[7].equals(REQUIREMENTATTRIBUTE)){
								Map ra = ws.getRequirementAttribute();
								if(!ra.isEmpty())
									return new DmtData(ra.get(path[8]).toString()); 
							}
						}
						if(path[6].equals(CAPABILITY)){
							if(path[7].equals(CAPABILITYDIRECTIVE)){
								Map cd = ws.getCapabilityDirective();
								if(!cd.isEmpty())
									return new DmtData(cd.get(path[8]).toString()); 
							}
							if(path[7].equals(CAPABILITYATTRIBUTE)){
								Map ca = ws.getCapabilityAttribute();
								if(!ca.isEmpty())
									return new DmtData(ca.get(path[8]).toString()); 
							}
						}
					}
				}
			}
		}
		
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified leaf node does not exist in the framework object.");
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);
		if(bundlesTableCopy.size()==0)
			bundlesTableCopy = (Hashtable) bundlesTable.clone();

		if (path.length == 1)
			return true;

		if (path.length == 2) {
			if (path[1].equals(FRAMEWORKSTARTLEVEL) 
					|| path[1].equals(INITIALBUNDLESTARTLEVEL)
					|| path[1].equals(PROPERTY)
					|| path[1].equals(BUNDLE))
				return true;
		}

		if (path.length == 3) {
			if(path[1].equals(PROPERTY)){
				if (properties.get(path[2]) != null)
					return true;
			}
			if(path[1].equals(BUNDLE)){
				if (bundlesTableCopy.get(path[2]) != null)
					return true;
			}
		}
		
		if (path.length == 4 && path[1].equals(BUNDLE)){
			if(this.bundlesTableCopy.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
				Node node = bs.getLocatonNode();
				if(node.findNode(new String[] {path[3]})!=null){
					return true;
				}
			}
		}
		
		if (path.length == 5 && path[1].equals(BUNDLE)){
			if(this.bundlesTableCopy.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
				if(path[3].equals(BUNDLETYPE)){
					if(bs.getBundleType()!=null)
						return true;
				}
				if(path[3].equals(HEADERS)){
					Dictionary headers = bs.getHeaders();
					if(headers.get(path[4])!=null)
						return true;
				}
				if(path[3].equals(ENTRIES)){
					Vector entries = bs.getEntries();
					try{
						entries.get(Integer.parseInt(path[4]));
						return true;
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
				}
				if(path[3].equals(SIGNERS)){
					Vector entries = bs.getSigners();
					try{
						entries.get(Integer.parseInt(path[4]));
						return true;
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
				}
				if(path[3].equals(WIRES)){
					Map wires = bs.getWires();
					if(wires.get(path[4])!=null)
						return true;
				}
			}
		}
		
		if (path.length == 6 && path[1].equals(BUNDLE)){
			if(this.bundlesTableCopy.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
				if(path[3].equals(ENTRIES)){
					Vector entries = bs.getEntries();
					try{
						entries.get(Integer.parseInt(path[4]));
						if(path[5].equals(PATH)
								|| path[5].equals(CONTENT)
								|| path[5].equals(ENTRIESINSTANCEID))
							return true;
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
				}
				if(path[3].equals(SIGNERS)){
					Vector entries = bs.getSigners();
					try{
						entries.get(Integer.parseInt(path[4]));
						if(path[5].equals(ISTRUSTED)
								|| path[5].equals(SIGNERSINSTANCEID)
								|| path[5].equals(CERTIFICATECHAIN))
						return true;
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
				}
				if(path[3].equals(WIRES)){
					Map wires = bs.getWires();
					Vector vec= (Vector)wires.get(path[4]);
					if(vec!=null){
						try{
							vec.get(Integer.parseInt(path[5]));
						}catch(ArrayIndexOutOfBoundsException ae){
							return false;
						}						
						return true;
					}
				}
			}
		}
		
		if (path.length == 7 && path[1].equals(BUNDLE)){
			if(this.bundlesTableCopy.get(path[2])!=null){
				BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
				if(path[3].equals(SIGNERS)){
					Vector entries = bs.getSigners();
					try{
						SignersSubtree ss = (SignersSubtree)entries.get(Integer.parseInt(path[4]));
						Vector list = ss.getCertifitateChainList();
						list.get(Integer.parseInt(path[6]));
						return true;
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
				}
				if(path[3].equals(WIRES)){
					Map wires = bs.getWires();
					Vector vec= (Vector)wires.get(path[4]);
					if(vec!=null){
						try{
							vec.get(Integer.parseInt(path[5]));
						}catch(ArrayIndexOutOfBoundsException ae){
							return false;
						}
						if(path[6].equals(NAMESPACE)
								|| path[6].equals(REQUIREMENT)
								|| path[6].equals(PROVIDER)
								|| path[6].equals(REQUIRER)
								|| path[6].equals(WIRESINSTANCEID)
								|| path[6].equals(CAPABILITY))
						return true;
					}
				}
			}
		}
		
		if (path.length == 8 && path[1].equals(BUNDLE)){
			BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
			if(path[3].equals(WIRES)){
				Map wires = bs.getWires();
				Vector vec= (Vector)wires.get(path[4]);
				if(vec!=null){
					try{
						vec.get(Integer.parseInt(path[5]));
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
					if(path[7].equals(FILTER)
							|| path[7].equals(REQUIREMENTDIRECTIVE)
							|| path[7].equals(REQUIREMENTATTRIBUTE)
							|| path[7].equals(CAPABILITYDIRECTIVE)
							|| path[7].equals(CAPABILITYDIRECTIVE))
					return true;
				}
			}
		}
		
		if (path.length == 9 && path[1].equals(BUNDLE)){
			BundleSubTree bs = (BundleSubTree)this.bundlesTableCopy.get(path[2]);
			if(path[3].equals(WIRES)){
				Map wires = bs.getWires();
				Vector vec= (Vector)wires.get(path[4]);
				WiresSubtree ws;
				if(vec!=null){
					try{
						ws = (WiresSubtree)vec.get(Integer.parseInt(path[5]));
					}catch(ArrayIndexOutOfBoundsException ae){
						return false;
					}
					if(path[6].equals(REQUIREMENT)&&path[7].equals(REQUIREMENTDIRECTIVE)){
						Map rd = ws.getRequirementDirective();
						return !rd.isEmpty();							
					}
					if(path[6].equals(REQUIREMENT)&&path[7].equals(REQUIREMENTATTRIBUTE)){
						Map ra = ws.getRequirementAttribute();
						return !ra.isEmpty();
					}
					if(path[6].equals(CAPABILITY)&&path[7].equals(CAPABILITYDIRECTIVE)){
						Map cd = ws.getCapabilityDirective();
						return !cd.isEmpty();
					}
					if(path[6].equals(CAPABILITY)&&path[7].equals(CAPABILITYDIRECTIVE)){
						Map ca = ws.getCapabilityAttribute();
						return !ca.isEmpty();
					}
				}
			}
		}

		return false;
	}

	// ----- Utilities -----//

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
