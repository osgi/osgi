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

import info.dmtree.*;
import info.dmtree.spi.TransactionalDataSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.net.*;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleException;
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
		// Value Setting
		Iterator i = operations.iterator();
		while (i.hasNext()) {
			Operation operation = (Operation) i.next();

			try {
				if (operation.getOperation() == Operation.ADD_OBJECT) {
					String[] nodepath = operation.getObjectname();
					Node newbundle = new Node(nodepath[nodepath.length - 1],
							new Node[] {
									new Node(LOCATION, null, new DmtData("")),
									new Node(URL, null, new DmtData("")) });
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
							.equals(REQUESTEDSTARTLEVEL)) {
						RequestedStartLevel = operation.getData().getInt();

						try {
							ServiceReference ref = context
									.getServiceReference(org.osgi.service.startlevel.StartLevel.class
											.getName());
							StartLevel sl = (StartLevel) context
									.getService(ref);

							sl.setStartLevel(RequestedStartLevel);
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
						System.exit(0);
					} else if (nodepath[nodepath.length - 1].equals(SHUTDOWN)) {
						System.exit(0);
					} else if (nodepath[nodepath.length - 1].equals(UPDATE)) {
						System.exit(0);
					} else if (nodepath[nodepath.length - 1].equals(LOCATION)
							|| nodepath[nodepath.length - 1].equals(URL)) {
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
						BundelControlValue bcv = (BundelControlValue)bundlesTable.get(nodepath[2]);
						try {
							ServiceReference ref = context
									.getServiceReference(org.osgi.service.startlevel.StartLevel.class
											.getName());
							StartLevel sl = (StartLevel) context
									.getService(ref);

							sl.setBundleStartLevel(bcv.bundle, operation.getData().getInt());
							context.ungetService(ref);
						} catch (NullPointerException e) {
							throw new DmtException(operation.getObjectname(),
									DmtException.DATA_STORE_FAILURE,
									"The StartLevel service is not available.");
						}
					} else if (nodepath[nodepath.length - 1].equals(BUNDLEUPDATE)) {
						BundelControlValue bcv = (BundelControlValue)bundlesTable.get(nodepath[2]);
						String bundleUpdate = operation.getData().getString();
						try{
							if(bcv.flag==true){
								URL url = context.getBundle().getResource(bundleUpdate);
								InputStream is = url.openStream();
								bcv.bundle.update(is);
								is.close();
								bcv.setFlag(false);
								bcv.setOperationResult("Success");
							}
						}catch(IOException ie){
							bcv.setOperationResult("Fail");
							throw new DmtException(operation.getObjectname(),
									DmtException.DATA_STORE_FAILURE,
									"Budnle Update failed.");
						}catch(BundleException be){
							bcv.setOperationResult("Fail");
							throw new DmtException(operation.getObjectname(),
									DmtException.DATA_STORE_FAILURE,
									"Budnle Update failed.");
						}
						bcv.setBundleUpdate(bundleUpdate);
						
					} else if (nodepath[nodepath.length - 1].equals(OPTION)) {
						BundelControlValue bcv = (BundelControlValue)bundlesTable.get(nodepath[2]);
						bcv.setOption(operation.getData().getInt());
						
					} else if (nodepath[nodepath.length - 1].equals(DESIREDSTATE)) {
						BundelControlValue bcv = (BundelControlValue)bundlesTable.get(nodepath[2]);
						int desiredState = operation.getData().getInt();
						if(bcv.flag==true){
							try{
								URL url = context.getBundle().getResource(bcv.bundleUpdateTmp);
								InputStream is = url.openStream();
								bcv.bundle.update(is);
								is.close();
								bcv.setFlag(false);
								bcv.setOperationResult("Success");
							}catch(IOException ie){
								bcv.setOperationResult("Fail");
								throw new DmtException(operation.getObjectname(),
										DmtException.DATA_STORE_FAILURE,
										"Budnle Update failed.");
							}catch(BundleException be){
								bcv.setOperationResult("Fail");
								throw new DmtException(operation.getObjectname(),
										DmtException.DATA_STORE_FAILURE,
										"Budnle Update failed.");
							}
						}
						try{
							if(desiredState==Bundle.UNINSTALLED){
								bcv.bundle.uninstall();
								bcv.setOperationResult("Success");
								bcv.setDesiredState(desiredState);
							}else if(desiredState==Bundle.RESOLVED){
								if(bcv.optionTmp==1){
									bcv.bundle.stop(1);
									bcv.setOperationResult("Success");
									bcv.setDesiredState(desiredState);
								}else{
									bcv.bundle.stop(0);
									bcv.setOperationResult("Success");
									bcv.setDesiredState(desiredState);
								}
							}else if(desiredState==Bundle.ACTIVE){
								bcv.bundle.start(bcv.optionTmp);
								bcv.setOperationResult("Success");
								bcv.setDesiredState(desiredState);							
							}else{
								throw new DmtException(operation.getObjectname(),
										DmtException.FEATURE_NOT_SUPPORTED,
										"The value is illegal value.");
							}							
						}catch(BundleException be){
							bcv.setOperationResult("Fail");
							throw new DmtException(operation.getObjectname(),
									DmtException.DATA_STORE_FAILURE,
									"The required operation failed.");
						}
					} 
				}
			} catch (RuntimeException e) {
				throw new DmtException(operation.getObjectname(),
						DmtException.COMMAND_FAILED,
						"The operation encountered problems.");
			}
		}

		// Bundle Install
		Node[] children = installbundle.getChildren();
		for (int x = 0; x < children.length; x++) {
			if(children[x].findNode(new String[] { ERROR }) != null){
				
			} else if (!children[x].findNode(new String[] { URL }).getData().getString().equals("")
					& !children[x].findNode(new String[] { LOCATION }).getData().getString().equals("")) {
				String url = children[x].findNode(new String[] { URL })
						.getData().getString();
				String loc = children[x].findNode(new String[] { LOCATION })
						.getData().getString();

				try {
					context.installBundle(loc, new URL(url).openConnection()
							.getInputStream());
					installbundle.deleteNode(children[x]);
				} catch (BundleException be) {
					Node error = new Node(ERROR, null, new DmtData(be.toString()));
					children[x].addNode(error);
				} catch (IOException ie) {
					Node error = new Node(ERROR, null, new DmtData(ie.toString()));
					children[x].addNode(error);
				}
			} else if (children[x].findNode(new String[] { URL }).getData().getString().equals("")
					& !children[x].findNode(new String[] { LOCATION }).getData().getString().equals("")) {
				String loc = children[x].findNode(new String[] { LOCATION })
						.getData().getString();

				try {
					context.installBundle(loc);
					installbundle.deleteNode(children[x]);
				} catch (BundleException be) {
					Node error = new Node(ERROR, null, new DmtData(be.toString()));
					children[x].addNode(error);
				}
			} else {
				Node error = new Node(ERROR, null, new DmtData(
						"There is no available bundle location."));
				children[x].addNode(error);
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
				if (path[1].equals(ids[i].getName()))
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

		if (path.length <= 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 3) {
			if (path[2].equals(REQUESTEDSTARTLEVEL)
					|| path[2].equals(INITIALBUNDLESTARTLEVEL)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}

			if (path[2].equals(RESTART) || path[2].equals(SHUTDOWN)
					|| path[2].equals(UPDATE)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}

			throw new DmtException(nodePath, DmtException.METADATA_MISMATCH,
					"The specified node can not be set the value.");
		}

		if (path.length == 4 && path[1].equals(INSTALLBUNDLE)) {
			if (path[3].equals(ERROR))
				throw new DmtException(nodePath,
						DmtException.METADATA_MISMATCH,
						"The specified node can not be set the value.");

			if (path[3].equals(LOCATION) || path[3].equals(URL)) {
				operations.add(new Operation(Operation.SET_VALUE, path, data));
				return;
			}
		}
		if (path.length == 4 && path[1].equals(BUNDLECONTROL)) {
			operations.add(new Operation(Operation.SET_VALUE, path, data));
		}
		
		if (path.length == 5) {
			if (path[4].equals(DESIREDSTATE)){
				operations.add(new Operation(Operation.SET_VALUE, path, data));
			}
			if (path[4].equals(BUNDLEUPDATE)){
				BundelControlValue bcv = (BundelControlValue)bundlesTable.get(path[2]);
				bcv.setFlag(true);
				bcv.setBundleUpdateTmp(data.getString());
				operations.add(new Operation(Operation.SET_VALUE, path, data));
			}
			if (path[4].equals(OPTION)){
				BundelControlValue bcv = (BundelControlValue)bundlesTable.get(path[2]);
				bcv.setOptionTmp(data.getInt());
				operations.add(new Operation(Operation.SET_VALUE, path, data));
			}
			if (path[4].equals(OPERATIONRESULT))
				throw new DmtException(nodePath,
						DmtException.METADATA_MISMATCH,
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
											new Node(LOCATION, null,
													new DmtData("")),
											new Node(URL, null, new DmtData("")) });
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
			String[] children = new String[2];
			children[0] = LOCATION;
			children[1] = URL;

			return children;
		}
		
		if (path.length == 3 && path[1].equals(BUNDLECONTROL)) {
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
		String[] children = new String[1];
		children[0] = "";

		return children;

	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length <= 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

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
				return new DmtData(RequestedStartLevel);
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
			Node tmptree = installbundle.copy();

			Iterator i = operations.iterator();
			while (i.hasNext()) {
				Operation operation = (Operation) i.next();

				try {
					if (operation.getOperation() == Operation.ADD_OBJECT) {
						String[] nodepath = operation.getObjectname();
						Node newbundle = new Node(
								nodepath[nodepath.length - 1], new Node[] {
										new Node(LOCATION, null,
												new DmtData("")),
										new Node(URL, null, new DmtData("")) });
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
								|| nodepath[nodepath.length - 1].equals(URL)) {
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
			
			if (path.length == 4 && path[1].equals(BUNDLECONTROL)) {
				Bundle targetBundle = (Bundle) bundlesTable.get(path[2]);
				ServiceReference ref = context
				.getServiceReference(org.osgi.service.startlevel.StartLevel.class.getName());
				StartLevel sl = (StartLevel) context.getService(ref);
				int bundleStartLevel = sl.getBundleStartLevel(targetBundle);
				context.ungetService(ref);
				return new DmtData(bundleStartLevel);
			}
			
			if (path.length == 5) {
				BundelControlValue bcv = (BundelControlValue)bundlesTable.get(path[2]);
				if (path[4].equals(DESIREDSTATE)) {
					Iterator iDesire = operations.iterator();
					while (iDesire.hasNext()) {
						Operation operation = (Operation) iDesire.next();
						if (operation.getOperation() == Operation.SET_VALUE) {
							String[] nodepath = operation.getObjectname();
							if (nodepath[nodepath.length - 1]
									.equals(DESIREDSTATE)) {
								return operation.getData();
							}
						}
					}
					return new DmtData(bcv.desiredState);
				}
				if (path[4].equals(BUNDLEUPDATE)) {
					Iterator iBundleUpdate = operations.iterator();
					while (iBundleUpdate.hasNext()) {
						Operation operation = (Operation) iBundleUpdate.next();
						if (operation.getOperation() == Operation.SET_VALUE) {
							String[] nodepath = operation.getObjectname();
							if (nodepath[nodepath.length - 1]
									.equals(BUNDLEUPDATE)) {
								return operation.getData();
							}
						}
					}
					
					return new DmtData(bcv.bundleUpdate);
				}
				if (path[4].equals(OPTION)) {
					Iterator iOption = operations.iterator();
					while (iOption.hasNext()) {
						Operation operation = (Operation) iOption.next();
						if (operation.getOperation() == Operation.SET_VALUE) {
							String[] nodepath = operation.getObjectname();
							if (nodepath[nodepath.length - 1]
									.equals(OPTION)) {
								return operation.getData();
							}
						}
					}
					return new DmtData(bcv.option);
				}
				if (path[4].equals(OPERATIONRESULT)) {
					Iterator iOperationResult = operations.iterator();
					while (iOperationResult.hasNext()) {
						Operation operation = (Operation) iOperationResult.next();
						if (operation.getOperation() == Operation.SET_VALUE) {
							String[] nodepath = operation.getObjectname();
							if (nodepath[nodepath.length - 1]
									.equals(OPERATIONRESULT)) {
								return operation.getData();
							}
						}
					}
					return new DmtData(bcv.operationResult);
				}
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
			if (bundlesTable.get(path[1]) != null)
				return true;

			Node tmptree = installbundle.copy();

			Iterator i = operations.iterator();
			while (i.hasNext()) {
				Operation operation = (Operation) i.next();

				try {
					if (operation.getOperation() == Operation.ADD_OBJECT) {
						String[] nodepath = operation.getObjectname();
						Node newbundle = new Node(
								nodepath[nodepath.length - 1], new Node[] {
										new Node(LOCATION, null,
												new DmtData("")),
										new Node(URL, null, new DmtData("")) });
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

			tmptree.findNode(new String[] { path[2] });
			if (tmptree.findNode(new String[] { path[2] }) != null)
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
