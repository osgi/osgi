/*
 * Copyright (c) OSGi Alliance (2000, 2010). All Rights Reserved.
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

import info.dmtree.*;
import info.dmtree.spi.TransactionalDataSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.net.*;

import org.osgi.framework.launch.Framework;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleException;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
/**
 * 
 * @author Koya MORI NTT Corporation, Shigekuni KONDO
 */
class FrameworkReadWriteSession extends FrameworkReadOnlySession implements
		TransactionalDataSession {

	private Vector operations;

	FrameworkReadWriteSession(FrameworkPlugin plugin, BundleContext context, FrameworkReadOnlySession session){
		super(plugin, context);
		this.installbundle = session.installbundle;
		operations = new Vector();
	}

	public void commit() throws DmtException {
		
		Iterator i = operations.iterator();
		while (i.hasNext()) {
			Operation operation = (Operation) i.next();
			try {
				if (operation.getOperation() == Operation.ADD_OBJECT) {
					String[] nodepath = operation.getObjectname();
					Node newbundle = new Node(nodepath[nodepath.length - 1],
							new Node[] {
									new Node(LOCATION, null, new DmtData("")),
									new Node(URL, null, new DmtData("")),
									new Node(INSTALLBUNDLEOPTION, null, new DmtData("NO START")),
									new Node(INSTALLBUNDLEOPERATIONRESULT, null, new DmtData(""))});
					installbundle.addNode(newbundle);
				} else if (operation.getOperation() == Operation.DELETE_OBJECT) {
					Node[] children = installbundle.getChildren();
					String[] nodepath = operation.getObjectname();
					for (int x = 0; x < children.length; x++) {
						if (nodepath[2].equals(children[x].getName())) {
							installbundle.deleteNode(children[x]);
							break;
						}
					}
				} else if (operation.getOperation() == Operation.SET_VALUE) {
					String[] nodepath = operation.getObjectname();
					
					if (nodepath[nodepath.length - 1]
									.equals(REFRESHPACKAGES)) {
						refreshPackagesFlag = operation.getData().getBoolean();
						if(refreshPackagesFlag){
							ServiceReference ref = context
							.getServiceReference(PackageAdmin.class.getName());
							PackageAdmin pa = (PackageAdmin)context.getService(ref);
							pa.refreshPackages(null);
						}
					} else if (nodepath[nodepath.length - 1]
											.equals(CATCHEVENTS)) {
						eventFlag = operation.getData().getBoolean();
					} else if (nodepath[nodepath.length - 1]
							.equals(REQUESTEDSTARTLEVEL)) {
						requestedStartLevel = operation.getData().getInt();
						try {
							ServiceReference ref = context
									.getServiceReference(org.osgi.service.startlevel.StartLevel.class
											.getName());
							StartLevel sl = (StartLevel) context
									.getService(ref);
							sl.setStartLevel(requestedStartLevel);
							context.ungetService(ref);
						} catch (NullPointerException e) {
							throw new DmtException(operation.getObjectname(),
									DmtException.DATA_STORE_FAILURE,
									"The StartLevel service is not available.");
						}
					} else if (nodepath[nodepath.length - 1]
							.equals(INITIALBUNDLESTARTLEVEL)) {
						try {
							ServiceReference ref = context
									.getServiceReference(org.osgi.service.startlevel.StartLevel.class
											.getName());
							StartLevel sl = (StartLevel) context
									.getService(ref);

							sl.setInitialBundleStartLevel(operation.getData()
									.getInt());
							context.ungetService(ref);
						} catch (NullPointerException e) {
							throw new DmtException(operation.getObjectname(),
									DmtException.DATA_STORE_FAILURE,
									"The StartLevel service is not available.");
						}
					} else if (nodepath[nodepath.length - 1].equals(RESTART)) {
						Framework framework = (Framework)context.getBundle(0);

						framework.update();
					} else if (nodepath[nodepath.length - 1].equals(SHUTDOWN)) {
						System.exit(0);
					} else if (nodepath[nodepath.length - 1].equals(UPDATE)) {
						Framework framework = (Framework)context.getBundle(0);
						framework.update();
					} else if (nodepath[nodepath.length - 1].equals(LOCATION)
							|| nodepath[nodepath.length - 1].equals(URL)
							|| nodepath[nodepath.length - 1].equals(INSTALLBUNDLEOPTION)) {
						try {
							installbundle.findNode(
									new String[] {
											nodepath[nodepath.length - 2],
											nodepath[nodepath.length - 1] })
									.setData(operation.getData());
						} catch (NullPointerException e) {
							throw new DmtException(operation.getObjectname(),
									DmtException.DATA_STORE_FAILURE,
									"The given node is not available now.");
						}
						
					} else if (nodepath[nodepath.length - 1].equals(BUNDLESTARTLEVEL)) {
						BundelControlSubTree bcst = (BundelControlSubTree)bundlesTable.get(nodepath[2]);
						try {
							ServiceReference ref = context
									.getServiceReference(org.osgi.service.startlevel.StartLevel.class
											.getName());
							StartLevel sl = (StartLevel) context
									.getService(ref);
							sl.setBundleStartLevel(bcst.getBundle(), operation.getData().getInt());
							context.ungetService(ref);
						} catch (NullPointerException e) {
							throw new DmtException(operation.getObjectname(),
									DmtException.DATA_STORE_FAILURE,
									"The StartLevel service is not available.");
						}
					} else if (nodepath[nodepath.length - 1].equals(BUNDLECONTROLREFRESHPACKAGES)) {
						BundelControlSubTree bcst = (BundelControlSubTree)bundlesTable.get(nodepath[2]);
						boolean refresh = operation.getData().getBoolean();
						if(refresh){
							ServiceReference ref = context
							.getServiceReference(PackageAdmin.class.getName());
							PackageAdmin pa = (PackageAdmin)context.getService(ref);
							Bundle[] bundle = new Bundle[]{bcst.getBundle()};
							pa.refreshPackages(bundle);
						}
						bcst.setRefreshPackages(refresh);
					} else if (nodepath[nodepath.length - 1].equals(OPERATION)) {
						BundelControlSubTree bcst = (BundelControlSubTree)bundlesTable.get(nodepath[2]);
						String bclOperation = operation.getData().getString();
						String bclOption = null;
						Iterator ibc = operations.iterator();
						while (ibc.hasNext()) {
							Operation operationBcl = (Operation) ibc.next();
							String[] nodepathbcl = operationBcl.getObjectname();
							if (nodepathbcl[nodepathbcl.length - 1].equals(BUNDLECONTROLOPTION)){
								bclOption = operationBcl.getData().getString();
								break;
							}
						}
						if(bclOperation.equals("")){
						}else if(bclOperation.equals("START")){
							try{
								if(bclOption==null){
									bcst.getBundle().start();
								}else if(bclOption!=null){
									if(bclOption.equals("NO OPTION"))
										bcst.getBundle().start();
									else if(bclOption.equals("TRANSIENT"))
										bcst.getBundle().start(Bundle.START_TRANSIENT);
									else if(bclOption.equals("START ACTIVATION POLICY"))
										bcst.getBundle().start(Bundle.START_ACTIVATION_POLICY);
									else if(bclOption.equals("START ACTIVATION POLICY AND TRANSIENT"))
										bcst.getBundle().start(Bundle.START_ACTIVATION_POLICY|Bundle.START_TRANSIENT);
								}
							}catch(BundleException be){
								bcst.setOperationResult(be.getMessage());
								throw new DmtException(operation.getObjectname(),
										DmtException.COMMAND_FAILED,
										"The required operation failed.");
							}
						}else if(bclOperation.equals("STOP")){
							try{
								if(bclOption==null){
									bcst.getBundle().stop();
								}else if(bclOption!=null){
									if(bclOption.equals("NO OPTION"))
										bcst.getBundle().stop();
									else if(bclOption.equals("TRANSIENT"))
										bcst.getBundle().stop(Bundle.STOP_TRANSIENT);
								}
							}catch(BundleException be){
								bcst.setOperationResult(be.getMessage());
								throw new DmtException(operation.getObjectname(),
										DmtException.COMMAND_FAILED,
										"The required operation failed.");
							}
						}else if(bclOperation.equals("UNINSTALL")){
							try{
								bcst.getBundle().uninstall();
								bcst.setOperationResult("SUCCESS");
							}catch(BundleException be){
								bcst.setOperationResult(be.getMessage());
								throw new DmtException(operation.getObjectname(),
										DmtException.COMMAND_FAILED,
										"The required operation failed.");
							}
						}else if(bclOperation.equals("RESOLVE")){
							try{
								ServiceReference ref = context
								.getServiceReference(PackageAdmin.class.getName());
								PackageAdmin pa = (PackageAdmin)context.getService(ref);
								Bundle[] bundle = new Bundle[]{bcst.getBundle()};
								pa.resolveBundles(bundle);
							}catch (Exception e){
								bcst.setOperationResult(e.getMessage());
								throw new DmtException(operation.getObjectname(),
										DmtException.COMMAND_FAILED,
										"The required operation failed.");
							}
						}else if(bclOperation.equals("UPDATE")){
							try{
								if(bclOption==null){
									bcst.getBundle().update();
								}else if(bclOption!=null){
									if(bclOption.equals("NO OPTION"))
										bcst.getBundle().update();
									else{
										try{
											URL url = new URL(bclOption);
											InputStream is = url.openStream();
											bcst.getBundle().update(is);
											is.close();
										}catch (MalformedURLException mue){
											bcst.getBundle().update();
										}
									}
								}
							}catch (Exception e){
								bcst.setOperationResult(e.getMessage());
								throw new DmtException(operation.getObjectname(),
										DmtException.COMMAND_FAILED,
										"The required operation failed.");
							}
						}
						bcst.setOperation(bclOperation);
						if(bclOption!=null)
							bcst.setOption(bclOption);
					} else if (nodepath[nodepath.length - 1].equals(BUNDLECONTROLOPTION)) {
						//Nothing to do.
					}
				}
			} catch (RuntimeException e) {
				throw new DmtException(operation.getObjectname(),
						DmtException.COMMAND_FAILED,
						"The operation encountered problems.");
			} catch (BundleException e) {
				throw new DmtException(operation.getObjectname(),
						DmtException.COMMAND_FAILED,
						"The operation encountered problems.");
			}
		}
		
		// Bundle Install
		Node[] children = installbundle.getChildren();
		for (int x = 0; x < children.length; x++) {
			if (!children[x].findNode(new String[] { URL }).getData().getString().equals("")
					& !children[x].findNode(new String[] { LOCATION }).getData().getString().equals("")) {
				String url = children[x].findNode(new String[] { URL }).getData().getString();
				String loc = children[x].findNode(new String[] { LOCATION }).getData().getString();
				String opt = children[x].findNode(new String[] { INSTALLBUNDLEOPTION }).getData().getString();
				try {
					Bundle bundle = context.installBundle(loc, new URL(url).openConnection().getInputStream());
					if(opt.equals("NO START OPTION"))
						bundle.start();
					else if(opt.equals("START TRANSIENT"))
						bundle.start(Bundle.START_TRANSIENT);
					else if(opt.equals("START ACTIVATION POLICY"))
						bundle.start(Bundle.START_ACTIVATION_POLICY);
					else if(opt.equals("START ACTIVATION POLICY AND TRANSIENT"))
						bundle.start(Bundle.START_ACTIVATION_POLICY|Bundle.START_TRANSIENT);
					else if(opt.equals("RESOLVE")){
						ServiceReference ref = context.getServiceReference(PackageAdmin.class.getName());
						PackageAdmin pa = (PackageAdmin)context.getService(ref);
						Bundle[] bundles = new Bundle[]{bundle};
						pa.resolveBundles(bundles);
					}
					Node operationResult = new Node(INSTALLBUNDLEOPERATIONRESULT, null, new DmtData("SUCCESS"));
					children[x].addNode(operationResult);
					installbundle.deleteNode(children[x]);
				} catch (BundleException be) {
					Node operationResult = new Node(INSTALLBUNDLEOPERATIONRESULT, null, new DmtData(be.toString()));
					children[x].addNode(operationResult);
				} catch (IOException ie) {
					Node operationResult = new Node(INSTALLBUNDLEOPERATIONRESULT, null, new DmtData(ie.toString()));
					children[x].addNode(operationResult);
				}
			} else if (children[x].findNode(new String[] { URL }).getData().getString().equals("")
					& !children[x].findNode(new String[] { LOCATION }).getData().getString().equals("")) {
				String loc = children[x].findNode(new String[] { LOCATION }).getData().getString();
				String opt = children[x].findNode(new String[] { INSTALLBUNDLEOPTION }).getData().getString();
				try {
					Bundle bundle = context.installBundle(loc);
					if(opt.equals("NO START OPTION"))
						bundle.start();
					else if(opt.equals("START TRANSIENT"))
						bundle.start(Bundle.START_TRANSIENT);
					else if(opt.equals("START ACTIVATION POLICY"))
						bundle.start(Bundle.START_ACTIVATION_POLICY);
					else if(opt.equals("START ACTIVATION POLICY AND TRANSIENT"))
						bundle.start(Bundle.START_ACTIVATION_POLICY|Bundle.START_TRANSIENT);
					else if(opt.equals("RESOLVE")){
						ServiceReference ref = context.getServiceReference(PackageAdmin.class.getName());
						PackageAdmin pa = (PackageAdmin)context.getService(ref);
						Bundle[] bundles = new Bundle[]{bundle};
						pa.resolveBundles(bundles);
					}
					Node operationResult = new Node(INSTALLBUNDLEOPERATIONRESULT, null, new DmtData("SUCCESS"));
					children[x].addNode(operationResult);
					installbundle.deleteNode(children[x]);
				} catch (BundleException be) {
					Node operationResult = new Node(INSTALLBUNDLEOPERATIONRESULT, null, new DmtData(be.toString()));
					children[x].addNode(operationResult);
				}
			} else {
				Node operationResult = new Node(INSTALLBUNDLEOPERATIONRESULT, null, new DmtData(
						"There is no available bundle location."));
				children[x].addNode(operationResult);
			}
		}
		operations = new Vector();
	}

	public void rollback() throws DmtException {
		operations = new Vector();
	}

	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		if (type != null)
			throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
					"Cannot set type property of interior nodes.");

		String[] path = shapedPath(nodePath);
		if (path.length == 3) {
			Node[] ids = installbundle.getChildren();
			for (int i = 0; i < ids.length; i++) {
				if (path[2].equals(ids[i].getName()))
					throw new DmtException(nodePath,
							DmtException.NODE_ALREADY_EXISTS,
							"A given node already exists in the framework subtree.");
			}
			operations.add(new Operation(Operation.ADD_OBJECT, path));
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
			if (path[1].equals(REFRESHPACKAGES)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
		}
		
		if (path.length == 3) {
			if (path[2].equals(REQUESTEDSTARTLEVEL)
					|| path[2].equals(INITIALBUNDLESTARTLEVEL)
					|| path[2].equals(CATCHEVENTS)
					|| path[2].equals(RESTART) 
					|| path[2].equals(SHUTDOWN)
					|| path[2].equals(UPDATE)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
			throw new DmtException(nodePath, DmtException.METADATA_MISMATCH,
					"The specified node can not be set the value.");
		}

		if (path.length == 4 && path[1].equals(INSTALLBUNDLE)) {
			if (path[3].equals(INSTALLBUNDLEOPERATIONRESULT))
				throw new DmtException(nodePath,
						DmtException.METADATA_MISMATCH,
						"The specified node can not be set the value.");

			if (path[3].equals(LOCATION) 
					|| path[3].equals(URL)
					|| path[3].equals(INSTALLBUNDLEOPTION)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
		}
		if (path.length == 4 && path[1].equals(BUNDLECONTROL)) {
			if (path[3].equals(BUNDLESTARTLEVEL)
					|| path[3].equals(BUNDLECONTROLREFRESHPACKAGES))
				operations.add(new Operation(Operation.SET_VALUE, path, data));
		}
		
		if (path.length == 5) {
			if (path[4].equals(BUNDLECONTROLOPERATIONRESULT))
				throw new DmtException(nodePath,
						DmtException.METADATA_MISMATCH,
						"The specified node can not be set the value.");
			if (path[4].equals(OPERATION)
					|| path[4].equals(BUNDLECONTROLOPTION)){
				operations.add(new Operation(Operation.SET_VALUE, path, data));
			}
			throw new DmtException(nodePath, DmtException.METADATA_MISMATCH,
			"The specified node can not be set the value.");
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified node does not exist in the framework object.");
	}

	public void deleteNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 3) {
			if (path[1].equals(INSTALLBUNDLE)) {
				operations.add(new Operation(Operation.DELETE_OBJECT, path));
				return;
			}
		}

		if (path.length > 3)
			throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
					"The specified node does not exist in the framework object.");

		throw new DmtException(nodePath, DmtException.METADATA_MISMATCH,
				"The specified node can not be deleted.");

	}

	public void setNodeType(String[] nodePath, String type) throws DmtException {
		if (type == null)
			return;

		if (!isLeafNode(nodePath))
			throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
					"Cannot set type property of interior nodes.");

		// do nothing, meta-data guarantees that type is "text/plain"
	}

	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported.");
	}

	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.COMMAND_FAILED,
				"Cannot rename any node in the framework subtree.");
	}

	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Cannot copy framework's nodes.");
	}

	// ----- Overridden methods to provide updated information -----//

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
				Node tmptree = installbundle.copy();
				Iterator i = operations.iterator();
				while (i.hasNext()) {
					Operation operation = (Operation) i.next();
					try {
						if (operation.getOperation() == Operation.ADD_OBJECT) {
							String[] nodepath = operation.getObjectname();
							Node newbundle = new Node(
									nodepath[nodepath.length - 1],
									new Node[] {
											new Node(LOCATION, null, new DmtData("")),
											new Node(URL, null, new DmtData("")),
											new Node(INSTALLBUNDLEOPTION, null, new DmtData("NO START")),
											new Node(INSTALLBUNDLEOPERATIONRESULT, null, new DmtData(""))});
							tmptree.addNode(newbundle);
						} else if (operation.getOperation() == Operation.DELETE_OBJECT) {
							Node[] children = tmptree.getChildren();
							String[] nodepath = operation.getObjectname();
							for (int x = 0; x < children.length; x++) {
								if (nodepath[1].equals(children[x].getName())) {
									tmptree.deleteNode(children[x]);
									break;
								}
							}
						}
					} catch (Exception e) {

					}
				}
				Node[] ids = tmptree.getChildren();
				String[] children = new String[ids.length];
				for (int x = 0; x < ids.length; x++) {
					children[x] = ids[x].getName();
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
					return new String[0];
				}
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
			String[] children = new String[4];
			children[0] = LOCATION;
			children[1] = URL;
			children[2] = INSTALLBUNDLEOPTION;
			children[3] = INSTALLBUNDLEOPERATIONRESULT;

			return children;
		}
		
		if (path.length == 3 && path[1].equals(BUNDLECONTROL)) {
			String[] children = new String[3];
			children[0] = BUNDLESTARTLEVEL;
			children[1] = LIFECYCLE;
			children[2] = BUNDLECONTROLREFRESHPACKAGES;
			return children;
		}
		
		if (path.length == 3 && path[1].equals(FRAMEWORKEVENT)) {
			if (eventTable.size() == 0) {
				return new String[0];
			}
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
		return new String[0];

	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length < 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");
		
		if (path.length == 2) {
			if (path[1].equals(REFRESHPACKAGES)) {
				Iterator i = operations.iterator();
				while (i.hasNext()) {
					Operation operation = (Operation) i.next();
					if (operation.getOperation() == Operation.SET_VALUE) {
						String[] nodepath = operation.getObjectname();
						if (nodepath[nodepath.length - 1]
								.equals(REFRESHPACKAGES)) {
							return operation.getData();
						}
					}
				}
				return new DmtData(refreshPackagesFlag);
			}
		}

		if (path.length == 3) {
			if (path[2].equals(REQUESTEDSTARTLEVEL)) {
				Iterator i = operations.iterator();
				while (i.hasNext()) {
					Operation operation = (Operation) i.next();

					if (operation.getOperation() == Operation.SET_VALUE) {
						String[] nodepath = operation.getObjectname();

						if (nodepath[nodepath.length - 1]
								.equals(REQUESTEDSTARTLEVEL)) {
							return operation.getData();
						}
					}
				}
				return new DmtData(requestedStartLevel);
			}
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
				Iterator i = operations.iterator();
				while (i.hasNext()) {
					Operation operation = (Operation) i.next();
					if (operation.getOperation() == Operation.SET_VALUE) {
						String[] nodepath = operation.getObjectname();
						if (nodepath[nodepath.length - 1]
								.equals(INITIALBUNDLESTARTLEVEL)) {
							return operation.getData();
						}
					}
				}				
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
			if (path[2].equals(CATCHEVENTS)) {
				Iterator i = operations.iterator();
				while (i.hasNext()) {
					Operation operation = (Operation) i.next();
					if (operation.getOperation() == Operation.SET_VALUE) {
						String[] nodepath = operation.getObjectname();

						if (nodepath[nodepath.length - 1]
								.equals(CATCHEVENTS)) {
							return operation.getData();
						}
					}
				}
				return new DmtData(eventFlag);
			}
			if (path[2].equals(RESTART))
				return new DmtData(false);
			if (path[2].equals(SHUTDOWN))
				return new DmtData(false);
			if (path[2].equals(UPDATE))
				return new DmtData(false);
		}
		
		if (path.length == 4 && path[1].equals(INSTALLBUNDLE)) {
			Node tmptree = installbundle.copy();
			Iterator i = operations.iterator();
			while (i.hasNext()) {
				Operation operation = (Operation) i.next();
				try {
					if (operation.getOperation() == Operation.ADD_OBJECT) {
						String[] nodepath = operation.getObjectname();
						Node newbundle = new Node(
								nodepath[nodepath.length - 1], new Node[] {
										new Node(LOCATION, null, new DmtData("")),
										new Node(URL, null, new DmtData("")),
										new Node(INSTALLBUNDLEOPTION, null, new DmtData("NO START")),
										new Node(INSTALLBUNDLEOPERATIONRESULT, null, new DmtData(""))});
						tmptree.addNode(newbundle);
					} else if (operation.getOperation() == Operation.DELETE_OBJECT) {
						Node[] children = tmptree.getChildren();
						String[] nodepath = operation.getObjectname();
						for (int x = 0; x < children.length; x++) {
							if (nodepath[1].equals(children[x].getName())) {
								tmptree.deleteNode(children[x]);
								break;
							}
						}
					} else if (operation.getOperation() == Operation.SET_VALUE) {
						String[] nodepath = operation.getObjectname();

						if (nodepath[nodepath.length - 1].equals(LOCATION)
								|| nodepath[nodepath.length - 1].equals(URL)
								|| nodepath[nodepath.length - 1].equals(INSTALLBUNDLEOPTION)) {
							try {
								tmptree
										.findNode(
												new String[] {
														nodepath[nodepath.length - 2],
														nodepath[nodepath.length - 1] })
										.setData(operation.getData());
							} catch (NullPointerException e) {
								e.printStackTrace();
								throw new DmtException(operation
										.getObjectname(),
										DmtException.DATA_STORE_FAILURE,
										"The given node is not available now.");
							}
						}
					}
				} catch (Exception e) {
				}
			}
			Node[] ids = tmptree.getChildren();
			for (int z = 0; z < ids.length; z++) {
				if (path[2].equals(ids[z].getName())) {
					Node[] leaf = ids[z].getChildren();
					for (int x = 0; x < leaf.length; x++) {
						if (path[3].equals(leaf[x].getName()))
							return leaf[x].getData();
					}
					break;
				}
			}
		}
		
		if (path.length == 4 && path[1].equals(BUNDLECONTROL)) {
			if (path[3].equals(BUNDLESTARTLEVEL)) {
				Iterator i = operations.iterator();
				while (i.hasNext()) {
					Operation operation = (Operation) i.next();
					if (operation.getOperation() == Operation.SET_VALUE) {
						String[] nodepath = operation.getObjectname();
						if (nodepath[nodepath.length - 1]
								.equals(BUNDLESTARTLEVEL)) {
							return operation.getData();
						}
					}
				}
				BundelControlSubTree bcst = (BundelControlSubTree)bundlesTable.get(path[2]);
				return new DmtData(bcst.getBundleStartLevel());
			}
			if (path[3].equals(BUNDLECONTROLREFRESHPACKAGES)) {
				Iterator i = operations.iterator();
				while (i.hasNext()) {
					Operation operation = (Operation) i.next();
					if (operation.getOperation() == Operation.SET_VALUE) {
						String[] nodepath = operation.getObjectname();
						if (nodepath[nodepath.length - 1]
								.equals(BUNDLECONTROLREFRESHPACKAGES)) {
							return operation.getData();
						}
					}
				}
				BundelControlSubTree bcst = (BundelControlSubTree)bundlesTable.get(path[2]);
				return new DmtData(bcst.getRefreshPackages());
			}
		}
		
		if (path.length == 5) {
			if (path[4].equals(OPERATION)) {
				Iterator i = operations.iterator();
				while (i.hasNext()) {
					Operation operation = (Operation) i.next();
					if (operation.getOperation() == Operation.SET_VALUE) {
						String[] nodepath = operation.getObjectname();
						if (nodepath[nodepath.length - 1]
								.equals(OPERATION)) {
							return operation.getData();
						}
					}
				}
				BundelControlSubTree bcst = (BundelControlSubTree)bundlesTable.get(path[2]);
				return new DmtData(bcst.getOperation());
			}
			if (path[4].equals(BUNDLECONTROLOPTION)) {
				Iterator i = operations.iterator();
				while (i.hasNext()) {
					Operation operation = (Operation) i.next();
					if (operation.getOperation() == Operation.SET_VALUE) {
						String[] nodepath = operation.getObjectname();
						if (nodepath[nodepath.length - 1]
								.equals(BUNDLECONTROLOPTION)) {
							return operation.getData();
						}
					}
				}
				BundelControlSubTree bcst = (BundelControlSubTree)bundlesTable.get(path[2]);
				return new DmtData(bcst.getOption());
			}
			if (path[4].equals(BUNDLECONTROLOPERATIONRESULT)) {
				BundelControlSubTree bcst = (BundelControlSubTree)bundlesTable.get(path[2]);
				return new DmtData(bcst.getOperationResult());
			}
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified key does not exist in the framework object.");
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
			
			if (path[1].equals(BUNDLECONTROL))
				if (bundlesTable.get(path[1]) != null)
					return true;

			if (path[1].equals(INSTALLBUNDLE)){
				Node tmptree = installbundle.copy();
				Iterator i = operations.iterator();
				while (i.hasNext()) {
					Operation operation = (Operation) i.next();

					try {
						if (operation.getOperation() == Operation.ADD_OBJECT) {
							String[] nodepath = operation.getObjectname();
							Node newbundle = new Node(
									nodepath[nodepath.length - 1], new Node[] {
											new Node(LOCATION, null, new DmtData("")),
											new Node(URL, null, new DmtData("")),
											new Node(INSTALLBUNDLEOPTION, null, new DmtData("NO START")),
											new Node(INSTALLBUNDLEOPERATIONRESULT, null, new DmtData(""))});
							tmptree.addNode(newbundle);
						} else if (operation.getOperation() == Operation.DELETE_OBJECT) {
							Node[] children = tmptree.getChildren();
							String[] nodepath = operation.getObjectname();
							for (int x = 0; x < children.length; x++) {
								if (nodepath[1].equals(children[x].getName())) {
									tmptree.deleteNode(children[x]);
									break;
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (tmptree.findNode(new String[] { path[2] }) != null)
					return true;
			}
		}

		if (path.length == 4) {
			if (path[1].equals(BUNDLECONTROL))
				if (bundlesTable.get(path[2]) != null)
					if (path[3].equals(BUNDLESTARTLEVEL)
							|| path[3].equals(BUNDLECONTROLREFRESHPACKAGES)
							|| path[3].equals(LIFECYCLE))
						return true;
			
			if (path[1].equals(INSTALLBUNDLE))
					if (path[3].equals(LOCATION) 
							|| path[3].equals(URL)
							|| path[3].equals(INSTALLBUNDLEOPTION)
							|| path[3].equals(INSTALLBUNDLEOPERATIONRESULT))
						return true;

			if (path[1].equals(FRAMEWORKEVENT))
				if (eventTable.get(path[3]) != null)
					return true;
		}

		if (path.length == 5) {
			if (path[1].equals(BUNDLECONTROL))
				if (bundlesTable.get(path[2]) != null)
					if (path[4].equals(OPERATION) 
							|| path[4].equals(BUNDLECONTROLOPTION)
							|| path[4].equals(BUNDLECONTROLOPERATIONRESULT))
						return true;
			
			if (path[1].equals(FRAMEWORKEVENT))
				if (eventTable.get(path[3]) != null)
					if (path[4].equals(THROWABLE)
							|| path[4].equals(TYPE)
							|| path[4].equals(BUNDLEID))
						return true;
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
