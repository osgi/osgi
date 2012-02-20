/*
 * Copyright (c) OSGi Alliance (2000, 2011). All Rights Reserved.
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

import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.spi.ReadableDataSession;

/**
 * 
 * @author Shigekuni KONDO NTT Corporation
 */
public class FiltersReadOnlySession implements ReadableDataSession {
	protected static final String FILTER = "Filter";
	protected static final String TARGET = "Target";
	protected static final String LIMIT = "Limit";
	protected static final String RESULTURILIST = "ResultUriList";
	protected static final String RESULT = "Result";
	protected static final String INSTANCEID = "InstanceId";
	protected static final String FILTER_NODE_TYPE = "org.osgi/1.0/FiltersManagementObject";
	protected static final String KEY_OF_RMT_ROOT_URI = "org.osgi.dmt.residential";
	protected static final String RMT_ROOT_URI = System.getProperty(KEY_OF_RMT_ROOT_URI,"./RMT");

	protected FiltersPlugin plugin;
	protected BundleContext context;
	protected Map searches;
	protected int instanceId;
	protected int rootLength = 1;

	FiltersReadOnlySession(FiltersPlugin plugin, BundleContext context) {
		this.plugin = plugin;
		this.context = context;
		searches = new HashMap();
		instanceId = 0;
		if (RMT_ROOT_URI != null) {
			String[] rootArray = pathToArrayUri(RMT_ROOT_URI + "/");
			rootLength = rootArray.length;
		}
	}

	public void close() throws DmtException {
	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		Util.log("getChildNodeNames is called. The path is : "
				+ arrayToPathUri(nodePath));
		String[] path = shapedPath(nodePath, rootLength);
		if (path.length == 1) {
			if (searches.size() == 0)
				return new String[0];
			String[] children = new String[searches.size()];
			Iterator it = searches.keySet().iterator();
			for (int i = 0; it.hasNext(); i++) {
				children[i] = (String) it.next();
			}
			return children;
		}

		if (path.length == 2) {
			if (searches.get(path[1]) != null) {
				String[] children = new String[6];
				children[0] = TARGET;
				children[1] = FILTER;
				children[2] = RESULT;
				children[3] = RESULTURILIST;
				children[4] = LIMIT;
				children[5] = INSTANCEID;
				return children;
			}
		}

		if (path.length == 3 && path[2].equals(RESULTURILIST)) {
			if (searches.get(path[1]) != null) {
				Filters fs = (Filters) searches.get(path[1]);
				if (!fs.isCreateResult())
					fs.serch(plugin.getSession());
				String[] children = new String[fs.getResultUriList().size()];
				Util.log("Size of ResultUriList : "+fs.getResultUriList().size());
				for (int i = 0; i<fs.getResultUriList().size(); i++) {
					children[i] = Integer.toString(i);
				}
				return children;
			}
		}
		
		if (path.length == 3 && path[2].equals(RESULT)) {
			if (searches.get(path[1]) != null) {
				Filters fs = (Filters) searches.get(path[1]);
				if (!fs.isCreateResult())
					fs.serch(plugin.getSession());
				Node resultNode = fs.getResultNode();
				if (resultNode != null)
					return resultNode.getChildNodeNames();
			}
		}

		if (path.length > 3 && path[2].equals(RESULT)) {
			Util.log("Path (> 3): "+arrayToPathUri(path));
			if (searches.get(path[1]) != null) {
				Util.log("Serches : "+path[1]);
				Filters fs = (Filters) searches.get(path[1]);
				if (!fs.isCreateResult())
					fs.serch(plugin.getSession());
				
				
				///Util.log("targetNode (> 3): "+fs.getResultNode().getName());
				Util.log("shapedPath (> 3): "+this.arrayToPathUri(shapedPath(path, 3)));
				
				
				Node targetNode = fs.getResultNode().findNode(shapedPath(path, 3));
				if (targetNode != null)
					return targetNode.getChildNodeNames();
			}
		}
		return new String[0];
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		// Util.log("[DEBUG] getMetaNode is called. The path is : " +
		// arrayToPathUri(nodePath));
		String[] path = shapedPath(nodePath, rootLength);
		if (path.length == 1)
			return new FiltersMetaNode("Filters Root node.",
					MetaNode.PERMANENT, !FiltersMetaNode.CAN_ADD,
					!FiltersMetaNode.CAN_DELETE, FiltersMetaNode.ALLOW_ZERO,
					!FiltersMetaNode.ALLOW_INFINITE);

		if (path.length == 2)
			return new FiltersMetaNode("Search ID of the filters.",
					MetaNode.DYNAMIC, FiltersMetaNode.CAN_ADD,
					FiltersMetaNode.CAN_DELETE, FiltersMetaNode.ALLOW_ZERO,
					FiltersMetaNode.ALLOW_INFINITE);

		if (path.length == 3) {
			if (path[2].equals(TARGET))
				return new FiltersMetaNode("The target for the filter search.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						FiltersMetaNode.CAN_REPLACE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE, DmtData.FORMAT_STRING,
						null);

			if (path[2].equals(FILTER))
				return new FiltersMetaNode("The filter for the filter search.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						FiltersMetaNode.CAN_REPLACE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE, DmtData.FORMAT_STRING,
						null);

			if (path[2].equals(LIMIT))
				return new FiltersMetaNode("Limits the number for results.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						FiltersMetaNode.CAN_REPLACE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[2].equals(INSTANCEID))
				return new FiltersMetaNode("The Instance Id.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						!FiltersMetaNode.CAN_REPLACE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[2].equals(RESULT))
				return new FiltersMetaNode("Result subtree.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE);

			if (path[2].equals(RESULTURILIST))
				return new FiltersMetaNode("The list of result uri.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE);
		}

		if (path.length == 4) {
			if (path[2].equals(RESULTURILIST))
				return new FiltersMetaNode("The Instance Id.",
						MetaNode.DYNAMIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						!FiltersMetaNode.CAN_REPLACE,
						FiltersMetaNode.ALLOW_ZERO,
						FiltersMetaNode.ALLOW_INFINITE, DmtData.FORMAT_STRING,
						null);
		}

		if (path.length >= 4) {
			if (path[2].equals(RESULT)) {
				Filters fs = (Filters) searches.get(path[1]);
				if (!fs.isCreateResult())
					fs.serch(plugin.getSession());
				Node targetNode = fs.getResultNode().findNode(
						shapedPath(path, 3));
				if (targetNode != null)
					return targetNode.getMetaNode();
			}
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the Filters MO.");
	}

	public int getNodeSize(String[] nodePath) throws DmtException {
		Util.log("getNodeSize is called. The path is : "
				+ arrayToPathUri(nodePath));
		return getNodeValue(nodePath).getSize();
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
		Util.log("getNodeType is called. The path is : "
				+ arrayToPathUri(nodePath));
		String[] path = shapedPath(nodePath, rootLength);
		if (path.length == 1)
			return DmtConstants.DDF_MAP;
		if (path.length == 3)
			if (path[2].equals(RESULTURILIST))
				return DmtConstants.DDF_LIST;
		if (path.length >= 4 && path[2].equals(RESULT)) {
			Filters fs = (Filters) searches.get(path[1]);
			if (!fs.isCreateResult())
				fs.serch(plugin.getSession());
			Node targetNode = fs.getResultNode().findNode(shapedPath(path, 3));
			if (targetNode != null)
				if (targetNode.getNodeType() != null) {
					if (targetNode.getNodeType().equals(DmtConstants.DDF_LIST))
						return DmtConstants.DDF_LIST;
					if (targetNode.getNodeType().equals(DmtConstants.DDF_MAP))
						return DmtConstants.DDF_MAP;
					if (targetNode.isLeafNode())
						return FiltersMetaNode.LEAF_MIME_TYPE;
				}
		}
		if (isLeafNode(nodePath))
			return FiltersMetaNode.LEAF_MIME_TYPE;
		return FiltersMetaNode.FILTERS_MO_TYPE;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		Util.log("getNodeValue is called. The path is : "
				+ arrayToPathUri(nodePath));
		String[] path = shapedPath(nodePath, rootLength);
		if (path.length <= 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 3) {
			if (path[2].equals(TARGET)) {
				Filters fs = (Filters) searches.get(path[1]);
				if (fs != null)
					return new DmtData(fs.getTarget());
			}
			if (path[2].equals(FILTER)) {
				Filters fs = (Filters) searches.get(path[1]);
				if (fs != null)
					return new DmtData(fs.getFilter());
			}
			if (path[2].equals(LIMIT)) {
				Filters fs = (Filters) searches.get(path[1]);
				if (fs != null)
					return new DmtData(fs.getLimit());
			}
			if (path[2].equals(INSTANCEID)) {
				Filters fs = (Filters) searches.get(path[1]);
				if (fs != null)
					return new DmtData(fs.getInstanceId());
			}
		}

		if (path.length == 4 && path[2].equals(RESULTURILIST)) {
			Filters fs = (Filters) searches.get(path[1]);
			if (!fs.isCreateResult())
				fs.serch(plugin.getSession());
			return new DmtData((String) fs.getResultUriList().get(
					Integer.parseInt(path[3])));
		}

		if (path.length >= 4 && path[2].equals(RESULT)) {
			Filters fs = (Filters) searches.get(path[1]);
			if (!fs.isCreateResult())
				fs.serch(plugin.getSession());
			Node targetNode = fs.getResultNode().findNode(shapedPath(path, 3));
			if (targetNode != null)
				if (targetNode.isLeafNode() && targetNode.getData() != null)
					return targetNode.getData();
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified key does not exist in the filters object.");
	}

	public int getNodeVersion(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property is not supported.");
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and timestamp properties are not supported

	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		Util.log("isLeafNode is called. The path is : "
				+ arrayToPathUri(nodePath));
		String[] path = shapedPath(nodePath, rootLength);

		if (path.length <= 2)
			return false;

		if (path.length == 3) {
			Filters fs = (Filters) searches.get(path[1]);
			if (fs != null)
				if (path[2].equals(TARGET) || path[2].equals(FILTER)
						|| path[2].equals(LIMIT) || path[2].equals(INSTANCEID))
					return true;
		}

		if (path.length == 4 && path[2].equals(RESULTURILIST))
			return true;

		if (path.length >= 4 && path[2].equals(RESULT)) {
			Filters fs = (Filters) searches.get(path[1]);
			Node targetNode = fs.getResultNode().findNode(shapedPath(path, 3));
			if (targetNode != null)
				return targetNode.isLeafNode();
		}
		return false;
	}

	public boolean isNodeUri(String[] nodePath) {
		Util.log("isNodeUri is called. The path is : "
				+ arrayToPathUri(nodePath));
		String[] path = shapedPath(nodePath, rootLength);

		if (path.length == 1) {
			if (path[0].equals(FILTER))
				return true;
		}

		if (path.length == 2) {
			Filters fs = (Filters) searches.get(path[1]);
			if (fs != null)
				return true;
		}

		if (path.length == 3) {
			Filters fs = (Filters) searches.get(path[1]);
			if (fs != null)
				if (path[2].equals(TARGET) || path[2].equals(FILTER)
						|| path[2].equals(LIMIT) || path[2].equals(INSTANCEID)
						|| path[2].equals(RESULT)
						|| path[2].equals(RESULTURILIST))
					return true;
		}

		if (path.length == 4 && path[2].equals(RESULTURILIST)) {
			Filters fs = (Filters) searches.get(path[1]);
			Vector vec = fs.getResultUriList();
			try {
				vec.get(Integer.parseInt(path[3]));
				return true;
			} catch (ArrayIndexOutOfBoundsException ae) {
				return false;
			}
		}

		if (path.length >= 4 && path[2].equals(RESULT)) {
			Filters fs = (Filters) searches.get(path[1]);
			Node targetNode = fs.getResultNode().findNode(shapedPath(path, 3));
			if (targetNode != null)
				return true;
		}
		return false;
	}

	// -----Utility----- //

	protected String[] shapedPath(String[] nodePath, int i) {
		int size = nodePath.length;
		int srcPos = i;
		int destPos = 0;
		int length = size - srcPos;
		String[] newPath = new String[length];
		System.arraycopy(nodePath, srcPos, newPath, destPos, length);
		return newPath;
	}

	protected String replaceAll(String value, String old_str, String new_str) {
		if (value == null || old_str == null || "".equals(old_str)) {
			return value;
		}
		StringBuffer ret = new StringBuffer();
		int old_len = old_str.length();
		int from_index = 0;
		int index = 0;
		boolean loop_flg = true;
		while (loop_flg) {
			index = value.indexOf(old_str, from_index);
			if (-1 < index) {
				ret.append(value.substring(from_index, index));
				ret.append(new_str);
				from_index = index + old_len;
			} else {
				ret.append(value.substring(from_index));
				loop_flg = false;
			}
		}
		return ret.toString();
	}

	protected String replaceFirst(String value, String old_str, String new_str) {
		if (value == null || old_str == null || "".equals(old_str)) {
			return value;
		}
		StringBuffer ret = new StringBuffer();
		int old_len = old_str.length();
		int from_index = 0;
		int index = value.indexOf(old_str, from_index);
		if (-1 < index) {
			ret.append(value.substring(from_index, index));
			ret.append(new_str);
			from_index = index + old_len;
			ret.append(value.substring(from_index));
		}
		return ret.toString();
	}

	protected String[] pathToArrayUri(String path) {
		Vector vector = new Vector();
		while (path.indexOf("/") != -1) {
			String start_path = path.substring(0, path.indexOf("/"));
			vector.add(start_path);
			path = path.substring(path.indexOf("/") + 1, path.length());
		}
		String[] arrayPath = new String[vector.size()];
		int i = 0;
		for (Iterator it = vector.iterator(); it.hasNext(); i++) {
			arrayPath[i] = (String) it.next();
		}
		return arrayPath;
	}

	protected String arrayToPathUri(String[] path) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < path.length; i++) {
			sb.append(path[i]);
			sb.append("/");
		}
		return sb.toString();
	}
	
	protected String arrayToPathUriWithoutSlash(String[] path) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < path.length; i++) {
			sb.append(path[i]);
			sb.append("/");
		}
		String str = sb.toString();
		if (str.lastIndexOf("/") == str.length() - 1) {
			str = str.substring(0,str.lastIndexOf("/"));
		}
		return str;
	}

	// --- Filters Class --- //

	protected class Filters {
		String target = "";
		String filter = "*";
		int limit = -1;
		int id;
		String name;
		boolean createResultFlag = false;
		DmtSession session;
		Vector targetList = new Vector();
		Vector resultUriList = new Vector();
		Node resultNodes = new Node(RESULT, null, true);
		int count = 0;

		Filters(String name) {
			this.name = name;
			this.id = ++instanceId;
		}

		protected void setTarget(String target) {
			this.target = target;
			if (this.createResultFlag)
				clearResult();
		}

		protected void setFilter(String filter) {
			this.filter = filter;
			if (this.createResultFlag)
				clearResult();
		}

		protected void setLimit(int limit) {
			this.limit = limit;
		}

		protected String getTarget() {
			return this.target;
		}

		protected String getFilter() {
			return this.filter;
		}

		protected int getLimit() {
			return this.limit;
		}

		protected int getInstanceId() {
			return this.id;
		}

		protected String getName() {
			return this.name;
		}

		protected Node getResultNode() {
			return this.resultNodes;
		}

		protected Vector getResultUriList() {
			return this.resultUriList;
		}

		protected boolean isCreateResult() {
			return this.createResultFlag;
		}

		protected void serch(DmtSession session) throws DmtException {
			Util.log("TARGET : " + this.target + "     FILTER : " + this.filter
					+ "     treeCreateFlag : " + this.createResultFlag);

			if (!(this.target.equals("")) && !(this.filter.equals(""))) {
				this.session = session;
				processTarget();
				createResultUri();
				createResultNodes();
				this.createResultFlag = true;
				this.session = null;
				
//				if(Util.DEBUG){
//					for (Iterator it = this.resultUriList.iterator(); it.hasNext();) {
//						Util.log("ResultUri : "+ it.hasNext());
//					}
//				}
			}
		}

		protected void clearResult() {
			Node[] nodes = this.resultNodes.getChildren();
			for (int i = 0; i < nodes.length; i++)
				this.resultNodes.deleteNode(nodes[i]);
			this.resultUriList.clear();
			this.createResultFlag = false;
		}

		private void processTarget() throws DmtException {
			if (!(this.target.startsWith("./")))
				throw new DmtException(this.target,
						DmtException.FEATURE_NOT_SUPPORTED,
						"The TARGET must be absolute URI.");

			if (this.target.endsWith("/-/") || this.target.endsWith("/-/*/"))
				throw new DmtException(this.target,
						DmtException.FEATURE_NOT_SUPPORTED,
						"The set target is not supported.");

			if (!(this.target.endsWith("/")))
				throw new DmtException(this.target,
						DmtException.FEATURE_NOT_SUPPORTED,
						"The TARGET must always end in a slash.");

			if (this.target.indexOf(RMT_ROOT_URI+"/Filter/") != -1)
				throw new DmtException(this.target,
						DmtException.FEATURE_NOT_SUPPORTED,
						"The set target is not supported.");
			String modifiedTarget = replaceAll(this.target, "/*/-/", "/-/");
			modifiedTarget = replaceAll(modifiedTarget, "/-/*/", "/-/");
			modifiedTarget = replaceAll(modifiedTarget, "/-/-/", "/-/");
			int star = modifiedTarget.indexOf("/*/");
			int bar = modifiedTarget.indexOf("/-/");
			if (star == -1 && bar == -1) {
				if (modifiedTarget.lastIndexOf("/") == modifiedTarget.length() - 1) {
					modifiedTarget = modifiedTarget.substring(0,
							modifiedTarget.lastIndexOf("/"));
				}
				if (!session.isLeafNode(modifiedTarget))
					this.targetList.add(modifiedTarget);
			} else {
				Vector tList = new Vector();
				tList.add(modifiedTarget);
				tList = tProcess(tList);
				Vector removeTargets = new Vector();
				for (Iterator it = tList.iterator(); it.hasNext();) {
					String target = (String) it.next();
					if (target.endsWith("/")) {
						String mTarget = target.substring(0,
								target.length() - 1);
						if (session.isLeafNode(mTarget)) {
							removeTargets.add(target);
						}
					} else if (session.isLeafNode(target)) {
						removeTargets.add(target);
					}
					if(target.startsWith(RMT_ROOT_URI+"/"+FILTER)){
						removeTargets.add(target);
					}
					
				}
				for (Iterator it = removeTargets.iterator(); it.hasNext();) {
					String removeTarget = (String) it.next();
					tList.remove(removeTarget);
				}
				this.targetList.addAll(tList);
			}
			if(Util.DEBUG){
				for (Iterator it = this.targetList.iterator(); it.hasNext();) {
					Util.log("Target : "+ it.hasNext());
				}
			}
			
		}

		private Vector tProcess(Vector tList) throws DmtException {

			if (tList.isEmpty())
				return tList;
			Vector removeList = new Vector();
			Vector addList = new Vector();
			for (Iterator it = tList.iterator(); it.hasNext();) {
				String target = (String) it.next();
				int star = target.indexOf("/*/");
				int bar = target.indexOf("/-/");
				if (bar == -1 && star == -1)
					continue;
				// '*' first
				if (bar == -1 || (star < bar && star != -1)) {
					String startPath = target.substring(0,
							target.indexOf("/*/"));
					
					String[] startPathArray = pathToArrayUri(startPath + "/");
					String[] sessionRootUriArray = pathToArrayUri(session.getRootUri() + "/");
					if(startPathArray.length>=sessionRootUriArray.length){
						if (!session.isNodeUri(startPath)) {
							removeList.add(target);
							continue;
						}
						String[] children = session.getChildNodeNames(startPath);
						Util.log("children length @ tProcess() : "+children.length);
						
						for (int i = 0; i < children.length; i++) {
							if (session.isLeafNode(startPath + "/" + children[i]))
								continue;
							String newTarget = replaceFirst(target, "*",
									children[i]);
							addList.add(newTarget);
						}
						removeList.add(target);
					}else{
						if (!session.isLeafNode(startPath + "/" + sessionRootUriArray[startPathArray.length])){
							String newTarget = replaceFirst(target, "*", sessionRootUriArray[startPathArray.length]);
							addList.add(newTarget);
							}
						removeList.add(target);
					}
					
					// '-' first
				} else if (star == -1 || (star > bar && bar != -1)) {
					String startPath = target.substring(0,
							target.indexOf("/-/"));
					String targetNodeName = target.substring(
							target.indexOf("/-/") + 3,
							target.indexOf("/", target.indexOf("/-/") + 3));
					if (!session.isNodeUri(startPath)) {
						removeList.add(target);
						continue;
					}
					String[] children = session.getChildNodeNames(startPath);
					for (int i = 0; i < children.length; i++) {
						if (session.isLeafNode(startPath + "/" + children[i]))
							continue;
						String basePath = startPath + "/" + children[i];
						Vector vec = findPath(basePath, targetNodeName, null);
						for (Iterator ite = vec.iterator(); ite.hasNext();) {
							String result = (String) ite.next();
							String newTarget = result
									+ "/"
									+ target.substring(
											target.indexOf("/",
													target.indexOf("/-/") + 3),
											target.length());
							addList.add(newTarget);
						}
					}
					removeList.add(target);
				}
			}
			for (Iterator it = removeList.iterator(); it.hasNext();) {
				String remove = (String) it.next();
				tList.remove(remove);
			}
			for (Iterator it = addList.iterator(); it.hasNext();) {
				String add = (String) it.next();
				tList.add(add);
			}

			boolean flag = false;
			for (Iterator it = tList.iterator(); it.hasNext();) {
				String path = (String) it.next();
				int star = path.indexOf("/*/");
				int bar = path.indexOf("/-/");
				if (bar != -1 || star != -1)
					flag = true;
			}
			if (flag)
				tList = tProcess(tList);
			return tList;
		}

		private Vector findPath(String basePath, String targetNode,
				Vector vector) throws DmtException {
			Vector vec;
			if (vector == null)
				vec = new Vector();
			else
				vec = vector;
			String[] children = session.getChildNodeNames(basePath);
			for (int i = 0; i < children.length; i++) {
				if (!session.isLeafNode(basePath + "/" + children[i]))
					if (children[i].equals(targetNode))
						vec.add(basePath + "/" + children[i]);
					else
						vec = findPath(basePath + "/" + children[i],
								targetNode, vec);
			}
			return vec;
		}

		private void createResultUri() {
			Util.log("createResultUri() is called.");
			//---------------
			String sessionRoot = session.getRootUri();
			String[] rootArray = pathToArrayUri(sessionRoot+"/");
			//---------------
			for (Iterator it = this.targetList.iterator(); it.hasNext();) {
				String target = (String) it.next();
				boolean flag;
				try {
					flag = checkFilter(this.filter, target);
					Util.log("checkFilter Result : " + flag);
					if (flag) {
						if (this.limit == -1){
							String[] tArray = pathToArrayUri(target);
							String[] path = shapedPath(tArray, rootArray.length);
							this.resultUriList.add(arrayToPathUriWithoutSlash(path));
						}
							
						if (this.limit != -1)
							if (++this.count <= this.limit){
								String[] tArray = pathToArrayUri(target);
								String[] path = shapedPath(tArray, rootArray.length);
								this.resultUriList.add(arrayToPathUriWithoutSlash(path));
							}
					}
				} catch (DmtException e) {
					e.printStackTrace();
				}
			}
		}

		private boolean checkFilter(String filterString, String targetSubtree)
				throws DmtException {
			Util.log("checkFilter() is called.   Filter : "+filter.toString() + "   TargetSubtree" + targetSubtree);

			Filter filter = null;
			try {
				if (filterString.equals("*")) {
					return true;
				} else {
					filter = context.createFilter(this.filter);
				}
			} catch (Exception e) {
				Util.log("printStackTrace");
				e.printStackTrace();
			}
			
			Dictionary prop = new Hashtable();
			if (targetSubtree.lastIndexOf("/") == targetSubtree.length() - 1) {
				targetSubtree = targetSubtree.substring(0,
						targetSubtree.lastIndexOf("/"));
			}
			//Util.log("targetSubtree for get propertyNodes : " + targetSubtree);
			String[] propertyNodes = session.getChildNodeNames(targetSubtree);
			for (int i = 0; i < propertyNodes.length; i++) {
				//Util.log("[DEBUG] propertyNodes in checkFilter : " + propertyNodes[i]);
				String propertyNodePath = targetSubtree + "/" + propertyNodes[i];
				String type = session.getNodeType(propertyNodePath);
				if (session.isLeafNode(propertyNodePath)) {
					DmtData data = session.getNodeValue(propertyNodePath);
					String nodeValue = null;

					switch (data.getFormat()) {
					case 1:
						int in = session.getNodeValue(propertyNodePath)
								.getInt();
						nodeValue = Integer.toString(in);
						prop.put(propertyNodes[i], nodeValue);
						break;
					case 4:
						nodeValue = session.getNodeValue(propertyNodePath)
								.getString();
						prop.put(propertyNodes[i], nodeValue);
						break;
					case 8:
						boolean bool = session.getNodeValue(propertyNodePath)
								.getBoolean();
						nodeValue = Boolean.toString(bool);
						prop.put(propertyNodes[i], nodeValue);
						break;
					case 16834:
						Date date = session.getNodeValue(propertyNodePath)
								.getDateTime();
						nodeValue = date.toString();
						prop.put(propertyNodes[i], nodeValue);
						break;
					case 8192:
						long lo = session.getNodeValue(propertyNodePath)
								.getLong();
						nodeValue = Long.toString(lo);
						prop.put(propertyNodes[i], nodeValue);
						break;
					case 4096:
						byte[] b = session.getNodeValue(propertyNodePath)
								.getBinary();
						nodeValue = b.toString();
						prop.put(propertyNodes[i], nodeValue);
						break;
					}
				} else if (type.equals(DmtConstants.DDF_LIST)) {
					String[] childrenNames = session
							.getChildNodeNames(propertyNodePath);
					String[] childrenValues = new String[childrenNames.length];
					for (int k = 0; k < childrenNames.length; k++) {
						String listNodePath = propertyNodePath + "/"
								+ childrenNames[k];
						DmtData data = session.getNodeValue(listNodePath);
						String nodeValue = null;
						switch (data.getFormat()) {
						case 1:
							int in = session.getNodeValue(propertyNodePath)
									.getInt();
							nodeValue = Integer.toString(in);
							childrenValues[k] = nodeValue;
							break;
						case 4:
							nodeValue = session.getNodeValue(propertyNodePath)
									.getString();
							childrenValues[k] = nodeValue;
							break;
						case 8:
							boolean bool = session.getNodeValue(
									propertyNodePath).getBoolean();
							nodeValue = Boolean.toString(bool);
							childrenValues[k] = nodeValue;
							break;
						case 16834:
							Date date = session.getNodeValue(propertyNodePath)
									.getDateTime();
							nodeValue = date.toString();
							childrenValues[k] = nodeValue;
							break;
						case 8192:
							long lo = session.getNodeValue(propertyNodePath)
									.getLong();
							nodeValue = Long.toString(lo);
							childrenValues[k] = nodeValue;
							break;
						case 4096:
							byte[] b = session.getNodeValue(propertyNodePath)
									.getBinary();
							nodeValue = b.toString();
							childrenValues[k] = nodeValue;
							break;
						}
					}
					prop.put(propertyNodes[i], childrenValues);
				}
			}
			
			Util.log("Property's size is " + prop.size());

			if (filter.match(prop))
				return true;
			return false;
		}

		private void createResultNodes() throws DmtException {
			createNodesInTargetPath();
			//String[] sessionUriRoot = pathToArrayUri(session.getRootUri()+"/");
			for (Iterator it = this.resultUriList.iterator(); it.hasNext();) {
				String target = (String) it.next();
				String[] tArray = pathToArrayUri(target+"/");

				Util.log("Target tArrayPath in createResultNodes() : " + arrayToPathUri(tArray));
				
				//String[] path = shapedPath(tArray, sessionUriRoot.length);
				
				Util.log("Target path in createResultNodes() : " + arrayToPathUri(tArray));
				
				Node endNode = this.resultNodes.findNode(tArray);
				createResultNode(target, endNode);
			}
		}

		private void createNodesInTargetPath() throws DmtException {
			Util.log("createNodesInTargetPath() is called.");
//			String sessionRoot = session.getRootUri();
//			String[] rootArray = pathToArrayUri(sessionRoot+"/");

			for (Iterator it = this.resultUriList.iterator(); it.hasNext();) {
				String target = (String) it.next();
				String[] tArray = pathToArrayUri(target+"/");
//				String[] path = shapedPath(tArray, rootArray.length);
//				Util.log("target path : " + arrayToPathUri(path));
				Node node = this.resultNodes;
				StringBuffer sb = new StringBuffer();
//				sb.append(sessionRoot);
//				sb.append("/");

				for (int i = 0; i < tArray.length; i++) {
					Node tmpNode = node.findNode(new String[] { tArray[i] });
					if (tmpNode == null) {
						sb.append(tArray[i]);
						String tmpPath = sb.toString();
						
						Util.log("node path : " + tmpPath);
						
						Node newNode = new Node(tArray[i], null, !session.isLeafNode(tmpPath));
						if(session.isLeafNode(tmpPath))
							newNode.setData(session.getNodeValue(tmpPath));
						newNode.setMetaNode(session.getMetaNode(tmpPath));
						newNode.setNodeType(session.getNodeType(tmpPath));
						node.addNode(newNode);
						node = newNode;
						sb.append("/");
					} else if (tmpNode != null) {
						sb.append(tArray[i]);
						sb.append("/");
						node = tmpNode;
					}
				}
			}
		}

		protected void createResultNode(String target, Node node)
				throws DmtException {
			Util.log("createResultNode is called. The Target is : "+target);
			
			if (target.lastIndexOf("/") == target.length() - 1) {
				target = target.substring(0, target.lastIndexOf("/"));
			}
			if(!target.startsWith(RMT_ROOT_URI+"/"+FILTER))
				return;
			String[] children = session.getChildNodeNames(target);
			for (int i = 0; i < children.length; i++) {
				Node tmpNode = node.findNode(new String[] { children[i] });
				if (tmpNode == null) {
					String newPath = target + "/" + children[i];
					boolean type = session.isLeafNode(newPath);
					if(type){
						Node newNode = new Node(children[i], null, !type);
						Util.log("Create new Node : "+children[i]);
						newNode.setData(session.getNodeValue(newPath));
						newNode.setMetaNode(session.getMetaNode(newPath));
						newNode.setNodeType(session.getNodeType(newPath));
						node.addNode(newNode);
					}else{
						Node newNode = new Node(children[i], null, type);
						Util.log("Create new Node : "+children[i]);
						createResultNode(newPath, newNode);
					}
				}
			}
		}
	}

	// --- Node Class --- //

	protected class Node {
		static final String INTERIOR = "Interiror";
		static final String LEAF = "leaf";
		private String name;
		private String type;
		private Vector children = new Vector();
		private MetaNode metanode = null;
		String nodetype = null;
		DmtData data = null;

		Node(String name, Node[] children, boolean nodeType) {
			this.name = name;
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					this.children.add(children[i]);
				}
			} else
				this.children = new Vector();
			if (nodeType)
				type = INTERIOR;
			else
				type = LEAF;
		}

		protected Node findNode(String[] path) {
			for (int i = 0; i < children.size(); i++) {
				if ((((Node) children.get(i)).getName()).equals(path[0])) {
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

		protected void addNode(Node add) {
			children.add(add);
		}

		protected void deleteNode(Node del) {
			children.remove(del);
		}

		protected Node[] getChildren() {
			Node[] nodes = new Node[children.size()];
			for (int i = 0; i < children.size(); i++) {
				nodes[i] = ((Node) children.get(i));
			}
			return nodes;
		}

		protected String[] getChildNodeNames() {
			String[] name = new String[children.size()];
			for (int i = 0; i < children.size(); i++) {
				Node node = ((Node) children.get(i));
				name[i] = node.getName();
			}
			return name;
		}

		protected String getType() {
			return type;
		}

		protected MetaNode getMetaNode() {
			return this.metanode;
		}

		protected void setMetaNode(MetaNode metaNode) {
			this.metanode = metaNode;
		}

		protected String getNodeType() {
			return this.nodetype;
		}

		protected void setNodeType(String nodeType) {
			this.nodetype = nodeType;
		}

		protected boolean isLeafNode() {
			if (this.type.equals(INTERIOR))
				return false;
			return true;
		}

		protected DmtData getData() {
			return this.data;
		}

		protected void setData(DmtData data) {
			this.data = data;
		}
	}
}
