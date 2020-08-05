/*
 * Copyright (c) OSGi Alliance (2000, 2017). All Rights Reserved.
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
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.ReadableDataSession;

/**
 * 
 * @author Shigekuni KONDO NTT Corporation
 */
public class FiltersReadOnlySession implements ReadableDataSession {

	protected FiltersPlugin plugin;
	protected BundleContext context;
	protected Map<String,Filters>	searches;
	protected int instanceId;
	protected int rootLength = 1;

	FiltersReadOnlySession(FiltersPlugin plugin, BundleContext context) {
		this.plugin = plugin;
		this.context = context;
		searches = new HashMap<>();
		instanceId = 0;
		if (RMTConstants.RMT_ROOT != null) {
			String[] rootArray = RMTUtil.pathToArrayUri(RMTConstants.RMT_ROOT + Uri.PATH_SEPARATOR_CHAR);
			rootLength = rootArray.length;
		}
	}

	@Override
	public void close() throws DmtException {
		// empty
	}

	@Override
	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = RMTUtil.shapedPath(nodePath, rootLength);
		if (path.length == 1) {
			if (searches.size() == 0)
				return new String[0];
			String[] children = new String[searches.size()];
			Iterator<String> it = searches.keySet().iterator();
			for (int i = 0; it.hasNext(); i++) {
				children[i] = it.next();
			}
			return children;
		}

		if (path.length == 2) {
			if (searches.get(path[1]) != null) {
				String[] children = new String[6];
				children[0] = RMTConstants.TARGET;
				children[1] = RMTConstants.FILTER;
				children[2] = RMTConstants.RESULT;
				children[3] = RMTConstants.RESULTURILIST;
				children[4] = RMTConstants.LIMIT;
				children[5] = RMTConstants.INSTANCEID;
				return children;
			}
		}

		if (path.length == 3 && path[2].equals(RMTConstants.RESULTURILIST)) {
			if (searches.get(path[1]) != null) {
				Filters fs = searches.get(path[1]);
				if (!fs.isCreateResult())
					fs.search(plugin.getSession());
				String[] children = new String[fs.getResultUriList().size()];
				for (int i = 0; i<fs.getResultUriList().size(); i++) {
					children[i] = Integer.toString(i);
				}
				return children;
			}
		}
		
		if (path.length == 3 && path[2].equals(RMTConstants.RESULT)) {
			if (searches.get(path[1]) != null) {
				Filters fs = searches.get(path[1]);
				if (!fs.isCreateResult())
					fs.search(plugin.getSession());
				Node resultNode = fs.getResultNode();
				if (resultNode != null)
					return resultNode.getChildNodeNames();
			}
		}

		if (path.length > 3 && path[2].equals(RMTConstants.RESULT)) {
			if (searches.get(path[1]) != null) {
				Filters fs = searches.get(path[1]);
				if (!fs.isCreateResult())
					fs.search(plugin.getSession());
				Node targetNode = fs.getResultNode().findNode(RMTUtil.shapedPath(path, 3));
				if (targetNode != null)
					return targetNode.getChildNodeNames();
			}
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
		"The specified node does not exist in the filter object.");
	}

	@Override
	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		String[] path = RMTUtil.shapedPath(nodePath, rootLength);
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
			if (path[2].equals(RMTConstants.TARGET))
				return new FiltersMetaNode("The target for the filter search.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						FiltersMetaNode.CAN_REPLACE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE, DmtData.FORMAT_STRING,
						null);

			if (path[2].equals(RMTConstants.FILTER))
				return new FiltersMetaNode("The filter for the filter search.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						FiltersMetaNode.CAN_REPLACE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE, DmtData.FORMAT_STRING,
						null);

			if (path[2].equals(RMTConstants.LIMIT))
				return new FiltersMetaNode("Limits the number for results.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						FiltersMetaNode.CAN_REPLACE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[2].equals(RMTConstants.INSTANCEID))
				return new FiltersMetaNode("The Instance Id.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						!FiltersMetaNode.CAN_REPLACE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE,
						DmtData.FORMAT_INTEGER, null);

			if (path[2].equals(RMTConstants.RESULT))
				return new FiltersMetaNode("Result subtree.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE);

			if (path[2].equals(RMTConstants.RESULTURILIST))
				return new FiltersMetaNode("The list of result uri.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE);
		}

		if (path.length == 4) {
			if (path[2].equals(RMTConstants.RESULTURILIST))
				return new FiltersMetaNode("The Instance Id.",
						MetaNode.DYNAMIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						!FiltersMetaNode.CAN_REPLACE,
						FiltersMetaNode.ALLOW_ZERO,
						FiltersMetaNode.ALLOW_INFINITE, DmtData.FORMAT_STRING,
						null);
		}

		if (path.length >= 4) {
			if (path[2].equals(RMTConstants.RESULT)) {
				Filters fs = searches.get(path[1]);
				if (!fs.isCreateResult())
					fs.search(plugin.getSession());
				Node targetNode = fs.getResultNode().findNode(
						RMTUtil.shapedPath(path, 3));
				if (targetNode != null)
					return targetNode.getMetaNode();
			}
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the Filters MO.");
	}

	@Override
	public int getNodeSize(String[] nodePath) throws DmtException {
		return getNodeValue(nodePath).getSize();
	}

	@Override
	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Timestamp property is not supported.");
	}

	@Override
	public String getNodeTitle(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Title property is not supported.");
	}

	@Override
	public String getNodeType(String[] nodePath) throws DmtException {
		String[] path = RMTUtil.shapedPath(nodePath, rootLength);
		if (path.length == 1)
			return DmtConstants.DDF_MAP;
		if (path.length == 3)
			if (path[2].equals(RMTConstants.RESULTURILIST))
				return DmtConstants.DDF_LIST;
		if (path.length >= 4 && path[2].equals(RMTConstants.RESULT)) {
			Filters fs = searches.get(path[1]);
			if (!fs.isCreateResult())
				fs.search(plugin.getSession());
			Node targetNode = fs.getResultNode().findNode(RMTUtil.shapedPath(path, 3));
			if (targetNode != null)
				if (targetNode.getNodeType() != null) {
					if (targetNode.isLeafNode() && 
							(path[2].equals(RMTConstants.LIMIT)
							||path[2].equals(RMTConstants.INSTANCEID)))
						return null;
					else if(targetNode.isLeafNode())
						return FiltersMetaNode.LEAF_MIME_TYPE;
					else
						return targetNode.getNodeType();
				}
		}
		if (isLeafNode(nodePath))
			return FiltersMetaNode.LEAF_MIME_TYPE;
		return FiltersMetaNode.FILTERS_MO_TYPE;
	}

	@Override
	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = RMTUtil.shapedPath(nodePath, rootLength);
		if (path.length <= 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 3) {
			if (path[2].equals(RMTConstants.TARGET)) {
				Filters fs = searches.get(path[1]);
				if (fs != null)
					return new DmtData(fs.getTarget());
			}
			if (path[2].equals(RMTConstants.FILTER)) {
				Filters fs = searches.get(path[1]);
				if (fs != null)
					return new DmtData(fs.getFilter());
			}
			if (path[2].equals(RMTConstants.LIMIT)) {
				Filters fs = searches.get(path[1]);
				if (fs != null)
					return new DmtData(fs.getLimit());
			}
			if (path[2].equals(RMTConstants.INSTANCEID)) {
				Filters fs = searches.get(path[1]);
				if (fs != null)
					return new DmtData(fs.getInstanceId());
			}
		}

		if (path.length == 4 && path[2].equals(RMTConstants.RESULTURILIST)) {
			Filters fs = searches.get(path[1]);
			if (!fs.isCreateResult())
				fs.search(plugin.getSession());
			return new DmtData(fs.getResultUriList().get(
					Integer.parseInt(path[3])));
		}

		if (path.length >= 4 && path[2].equals(RMTConstants.RESULT)) {
			Filters fs = searches.get(path[1]);
			if (!fs.isCreateResult())
				fs.search(plugin.getSession());
			Node targetNode = fs.getResultNode().findNode(RMTUtil.shapedPath(path, 3));
			if (targetNode != null)
				if (targetNode.isLeafNode() && targetNode.getData() != null)
					return targetNode.getData();
		}
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"The specified key does not exist in the filters object.");
	}

	@Override
	public int getNodeVersion(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property is not supported.");
	}

	@Override
	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and timestamp properties are not supported

	}

	@Override
	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = RMTUtil.shapedPath(nodePath, rootLength);

		if (path.length <= 2)
			return false;

		if (path.length == 3) {
			Filters fs = searches.get(path[1]);
			if (fs != null)
				if (path[2].equals(RMTConstants.TARGET) || path[2].equals(RMTConstants.FILTER)
						|| path[2].equals(RMTConstants.LIMIT) || path[2].equals(RMTConstants.INSTANCEID))
					return true;
			if (path[2].equals(RMTConstants.RESULTURILIST) || path[2].equals(RMTConstants.RESULT))
					return false;
		}

		if (path.length == 4 && path[2].equals(RMTConstants.RESULTURILIST))
			if (searches.get(path[1]) != null) {
				Filters fs = searches.get(path[1]);
				if (fs.isCreateResult())
					return true;
			}

		if (path.length >= 4 && path[2].equals(RMTConstants.RESULT)) {
			Filters fs = searches.get(path[1]);
			Node targetNode = fs.getResultNode().findNode(RMTUtil.shapedPath(path, 3));
			if (targetNode != null)
				return targetNode.isLeafNode();
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
		"The specified node does not exist in the filter object.");
	}

	@Override
	public boolean isNodeUri(String[] nodePath) {
		String[] path = RMTUtil.shapedPath(nodePath, rootLength);

		if (path.length == 1) {
			if (path[0].equals(RMTConstants.FILTER))
				return true;
		}

		if (path.length == 2) {
			Filters fs = searches.get(path[1]);
			if (fs != null)
				return true;
		}

		if (path.length == 3) {
			Filters fs = searches.get(path[1]);
			if (fs != null)
				if (path[2].equals(RMTConstants.TARGET) || path[2].equals(RMTConstants.FILTER)
						|| path[2].equals(RMTConstants.LIMIT) || path[2].equals(RMTConstants.INSTANCEID)
						|| path[2].equals(RMTConstants.RESULT)
						|| path[2].equals(RMTConstants.RESULTURILIST))
					return true;
		}

		if (path.length == 4 && path[2].equals(RMTConstants.RESULTURILIST)) {
			Filters fs = searches.get(path[1]);
			Vector<String> vec = fs.getResultUriList();
			try {
				vec.get(Integer.parseInt(path[3]));
				return true;
			} catch (ArrayIndexOutOfBoundsException ae) {
				return false;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		if (path.length >= 4 && path[2].equals(RMTConstants.RESULT)) {
			Filters fs = searches.get(path[1]);
			Node targetNode = fs.getResultNode().findNode(RMTUtil.shapedPath(path, 3));
			if (targetNode != null)
				return true;
		}
		return false;
	}

	// --- Filters Class --- //

	protected class Filters {
		private String target = "";
		private String filter = "*";
		private int limit = -1;
		private int id;
		private String name;
		private boolean createResultFlag = false;
		private DmtSession session;
		private Vector<String>	targetList			= new Vector<>();
		private Vector<String>	resultUriList		= new Vector<>();
		private Node resultNodes = new Node(RMTConstants.RESULT, null, true);
		private int count = 0;

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

		protected Vector<String> getResultUriList() {
			return this.resultUriList;
		}

		protected boolean isCreateResult() {
			return this.createResultFlag;
		}

		protected void search(@SuppressWarnings("hiding") DmtSession session)
				throws DmtException {
			if (!(this.target.equals("")) && !(this.filter.equals(""))) {
				this.session = session;
				processTarget();
				createResultUri();
				createResultNodes();
				this.createResultFlag = true;
				this.session = null;
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

			if (this.target.indexOf(RMTConstants.RMT_ROOT+"/Filter/") != -1)
				throw new DmtException(this.target,
						DmtException.FEATURE_NOT_SUPPORTED,
						"The set target is not supported.");
			String modifiedTarget = replaceAll(this.target, "/*/-/", "/-/");
			modifiedTarget = replaceAll(modifiedTarget, "/-/*/", "/-/");
			modifiedTarget = replaceAll(modifiedTarget, "/-/-/-/", "/-/");
			modifiedTarget = replaceAll(modifiedTarget, "/-/-/", "/-/");
			int star = modifiedTarget.indexOf("/*/");
			int bar = modifiedTarget.indexOf("/-/");
			if (star == -1 && bar == -1) {
				this.targetList.add(modifiedTarget);
			} else {
				Vector<String> tList = new Vector<>();
				tList.add(modifiedTarget);
				tList = tProcess(tList);
				Vector<String> removeTargets = new Vector<>();
				for (Iterator<String> it = tList.iterator(); it.hasNext();) {
					@SuppressWarnings("hiding")
					String target = it.next();
					if (target.endsWith("/")) {
						String mTarget = target.substring(0,
								target.length() - 1);
						if (isLeafNode(mTarget)==1||isLeafNode(mTarget)==3) {
							removeTargets.add(target);
						}
					} else if (isLeafNode(target)==1||isLeafNode(target)==3) {
						removeTargets.add(target);
					}
					if(target.startsWith(RMTConstants.RMT_ROOT+Uri.PATH_SEPARATOR_CHAR+RMTConstants.FILTER)){
						removeTargets.add(target);
					}
					
				}
				for (Iterator<String> it = removeTargets.iterator(); it
						.hasNext();) {
					String removeTarget = it.next();
					tList.remove(removeTarget);
				}
				this.targetList.addAll(tList);
			}
		}
		
		private int isLeafNode(String path){
			boolean flag;
			try{
				flag = session.isLeafNode(path);
			}catch(DmtException e){
				return 3;
			}
			if(flag)
				return 1;
			else
				return 2;
		}
		

		private Vector<String> tProcess(Vector<String> tList)
				throws DmtException {

			if (tList.isEmpty())
				return tList;
			Vector<String> removeList = new Vector<>();
			Vector<String> addList = new Vector<>();
			for (Iterator<String> it = tList.iterator(); it.hasNext();) {
				@SuppressWarnings("hiding")
				String target = it.next();
				int star = target.indexOf("/*/");
				int bar = target.indexOf("/-/");
				if (bar == -1 && star == -1)
					continue;
				// '*' first
				if (bar == -1 || (star < bar && star != -1)) {
					String startPath = target.substring(0,
							target.indexOf("/*/"));
					
					String[] startPathArray = RMTUtil.pathToArrayUri(startPath + Uri.PATH_SEPARATOR_CHAR);
					String[] sessionRootUriArray = RMTUtil.pathToArrayUri(session.getRootUri() + Uri.PATH_SEPARATOR_CHAR);
					if(startPathArray.length>=sessionRootUriArray.length){
						if (!session.isNodeUri(startPath)) {
							throw new DmtException(target, DmtException.NODE_NOT_FOUND,
							"The specified node is outside of the session.");
						}
						String[] children = session.getChildNodeNames(startPath);
						for (int i = 0; i < children.length; i++) {
							if (isLeafNode(startPath + Uri.PATH_SEPARATOR_CHAR + children[i])==1||isLeafNode(startPath + Uri.PATH_SEPARATOR_CHAR + children[i])==3)
								continue;
							String newTarget = replaceFirst(target, "*",
									children[i]);
							addList.add(newTarget);
						}
						removeList.add(target);
					}else{
						if (isLeafNode(startPath + Uri.PATH_SEPARATOR_CHAR + sessionRootUriArray[startPathArray.length])==2){
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
						if (isLeafNode(startPath + Uri.PATH_SEPARATOR_CHAR + children[i])==1||isLeafNode(startPath + Uri.PATH_SEPARATOR_CHAR + children[i])==3)
							continue;
						String basePath = startPath + Uri.PATH_SEPARATOR_CHAR + children[i];
						Vector<String> vec = findPath(basePath, targetNodeName,
								null);
						for (Iterator<String> ite = vec.iterator(); ite
								.hasNext();) {
							String result = ite.next();
							String newTarget = result
									+ Uri.PATH_SEPARATOR_CHAR
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
			for (Iterator<String> it = removeList.iterator(); it.hasNext();) {
				String remove = it.next();
				tList.remove(remove);
			}
			for (Iterator<String> it = addList.iterator(); it.hasNext();) {
				String add = it.next();
				tList.add(add);
			}

			boolean flag = false;
			for (Iterator<String> it = tList.iterator(); it.hasNext();) {
				String path = it.next();
				int star = path.indexOf("/*/");
				int bar = path.indexOf("/-/");
				if (bar != -1 || star != -1)
					flag = true;
			}
			if (flag)
				tList = tProcess(tList);
			return tList;
		}

		private Vector<String> findPath(String basePath, String targetNode,
				Vector<String> vector) throws DmtException {
			Vector<String> vec;
			if (vector == null)
				vec = new Vector<>();
			else
				vec = vector;
			String[] children = session.getChildNodeNames(basePath);
			for (int i = 0; i < children.length; i++) {
				if (isLeafNode(basePath + Uri.PATH_SEPARATOR_CHAR + children[i])==2)
					if (children[i].equals(targetNode))
						vec.add(basePath + Uri.PATH_SEPARATOR_CHAR + children[i]);
					else
						vec = findPath(basePath + Uri.PATH_SEPARATOR_CHAR + children[i],
								targetNode, vec);
			}
			return vec;
		}

		private void createResultUri() throws DmtException {
			String sessionRoot = session.getRootUri();
			String[] rootArray = RMTUtil.pathToArrayUri(sessionRoot+Uri.PATH_SEPARATOR_CHAR);
			for (Iterator<String> it = this.targetList.iterator(); it
					.hasNext();) {
				@SuppressWarnings("hiding")
				String target = it.next();
				boolean flag;
					flag = checkFilter(this.filter, target);
					if (flag) {
						if (this.limit == -1){
							String[] tArray = RMTUtil.pathToArrayUri(target);
							String[] path = RMTUtil.shapedPath(tArray, rootArray.length);
							this.resultUriList.add(RMTUtil.arrayToPathUriWithoutSlash(path));
						}
							
						if (this.limit != -1)
							if (++this.count <= this.limit){
								String[] tArray = RMTUtil.pathToArrayUri(target);
								String[] path = RMTUtil.shapedPath(tArray, rootArray.length);
								this.resultUriList.add(RMTUtil.arrayToPathUriWithoutSlash(path));
							}
					}
			}
		}

		private boolean checkFilter(String filterString, String targetSubtree)
				throws DmtException {
			@SuppressWarnings("hiding")
			Filter filter = null;
			try {
				if (filterString.equals("*")) {
					return true;
				} else {
					filter = context.createFilter(this.filter);
				}
			} catch (Exception e) {
				throw new DmtException(this.target,
						DmtException.FEATURE_NOT_SUPPORTED,
						"The specified filer is NOT correct.");
			}
			
			Dictionary<String,Object> prop = new Hashtable<>();
			if (targetSubtree.lastIndexOf("/") == targetSubtree.length() - 1) {
				targetSubtree = targetSubtree.substring(0,
						targetSubtree.lastIndexOf("/"));
			}
			String[] propertyNodes = session.getChildNodeNames(targetSubtree);
			for (int i = 0; i < propertyNodes.length; i++) {
				String propertyNodePath = targetSubtree + Uri.PATH_SEPARATOR_CHAR + propertyNodes[i];
				String type = session.getNodeType(propertyNodePath);
				if (isLeafNode(propertyNodePath)==1) {
					DmtData data = session.getNodeValue(propertyNodePath);
					String nodeValue = null;

					switch (data.getFormat()) {
					case DmtData.FORMAT_INTEGER:
						int in = session.getNodeValue(propertyNodePath)
								.getInt();
						prop.put(propertyNodes[i], Integer.valueOf(in));
						break;
					case DmtData.FORMAT_STRING:
						nodeValue = session.getNodeValue(propertyNodePath)
								.getString();
						prop.put(propertyNodes[i], nodeValue);
						break;
					case DmtData.FORMAT_BOOLEAN:
						boolean bool = session.getNodeValue(propertyNodePath)
								.getBoolean();
						prop.put(propertyNodes[i], Boolean.valueOf(bool));
						break;
					case DmtData.FORMAT_DATE_TIME:
						Date date = session.getNodeValue(propertyNodePath)
								.getDateTime();
						prop.put(propertyNodes[i], date);
						break;
					case DmtData.FORMAT_LONG:
						long lo = session.getNodeValue(propertyNodePath)
								.getLong();
						prop.put(propertyNodes[i], Long.valueOf(lo));
						break;
					case DmtData.FORMAT_RAW_BINARY:
						byte[] b = session.getNodeValue(propertyNodePath)
								.getBinary();
						prop.put(propertyNodes[i], b);
						break;
					}
				} else if (type.equals(DmtConstants.DDF_LIST)) {
					String[] childrenNames = session
							.getChildNodeNames(propertyNodePath);
					String[] childrenValues = new String[childrenNames.length];
					for (int k = 0; k < childrenNames.length; k++) {
						String listNodePath = propertyNodePath + Uri.PATH_SEPARATOR_CHAR
								+ childrenNames[k];
						DmtData data = session.getNodeValue(listNodePath);
						String nodeValue = null;
						switch (data.getFormat()) {
						case DmtData.FORMAT_INTEGER:
							int in = session.getNodeValue(propertyNodePath)
									.getInt();
							nodeValue = Integer.toString(in);
							childrenValues[k] = nodeValue;
							break;
						case DmtData.FORMAT_STRING:
							nodeValue = session.getNodeValue(propertyNodePath)
									.getString();
							childrenValues[k] = nodeValue;
							break;
						case DmtData.FORMAT_BOOLEAN:
							boolean bool = session.getNodeValue(
									propertyNodePath).getBoolean();
							nodeValue = Boolean.toString(bool);
							childrenValues[k] = nodeValue;
							break;
						case DmtData.FORMAT_DATE_TIME:
							Date date = session.getNodeValue(propertyNodePath)
									.getDateTime();
							nodeValue = date.toString();
							childrenValues[k] = nodeValue;
							break;
						case DmtData.FORMAT_LONG:
							long lo = session.getNodeValue(propertyNodePath)
									.getLong();
							nodeValue = Long.toString(lo);
							childrenValues[k] = nodeValue;
							break;
						case DmtData.FORMAT_RAW_BINARY:
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
			if (filter.match(prop))
				return true;
			return false;
		}

		private void createResultNodes() throws DmtException {
			createNodesInTargetPath();
			for (Iterator<String> it = this.resultUriList.iterator(); it
					.hasNext();) {
				@SuppressWarnings("hiding")
				String target = it.next();
				String[] tArray = RMTUtil.pathToArrayUri(target+Uri.PATH_SEPARATOR_CHAR);
				Node endNode = this.resultNodes.findNode(tArray);
				createResultNode(target, endNode);
			}
		}

		private void createNodesInTargetPath() throws DmtException {
			for (Iterator<String> it = this.resultUriList.iterator(); it
					.hasNext();) {
				@SuppressWarnings("hiding")
				String target = it.next();
				String[] tArray = RMTUtil.pathToArrayUri(target+Uri.PATH_SEPARATOR_CHAR);
				Node node = this.resultNodes;
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < tArray.length; i++) {
					Node tmpNode = node.findNode(new String[] { tArray[i] });
					if (tmpNode == null) {
						sb.append(tArray[i]);
						String tmpPath = sb.toString();						
						Node newNode = null;
						if(isLeafNode(tmpPath)==1)
							newNode = new Node(tArray[i], null, false);
						if(isLeafNode(tmpPath)==2)
							newNode = new Node(tArray[i], null, true);
						if(isLeafNode(tmpPath)==1)
							newNode.setData(session.getNodeValue(tmpPath));
						if(isLeafNode(tmpPath)==1||isLeafNode(tmpPath)==2){
							newNode.setMetaNode(session.getMetaNode(tmpPath));
							newNode.setNodeType(session.getNodeType(tmpPath));
							node.addNode(newNode);
							node = newNode;
						}
						sb.append("/");
					} else if (tmpNode != null) {
						sb.append(tArray[i]);
						sb.append("/");
						node = tmpNode;
					}
				}
			}
		}

		protected void createResultNode(
				@SuppressWarnings("hiding") String target, Node node)
				throws DmtException {
			if (target.lastIndexOf("/") == target.length() - 1) {
				target = target.substring(0, target.lastIndexOf("/"));
			}
			if(!target.startsWith(RMTConstants.RMT_ROOT+Uri.PATH_SEPARATOR_CHAR+RMTConstants.FILTER))
				return;
			String[] children = session.getChildNodeNames(target);
			for (int i = 0; i < children.length; i++) {
				Node tmpNode = node.findNode(new String[] { children[i] });
				if (tmpNode == null) {
					String newPath = target + Uri.PATH_SEPARATOR_CHAR + children[i];
					boolean type = session.isLeafNode(newPath);
					if(type){
						Node newNode = new Node(children[i], null, !type);
						newNode.setData(session.getNodeValue(newPath));
						newNode.setMetaNode(session.getMetaNode(newPath));
						newNode.setNodeType(session.getNodeType(newPath));
						node.addNode(newNode);
					}else{
						Node newNode = new Node(children[i], null, type);
						createResultNode(newPath, newNode);
					}
				}
			}
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

		
	}

	// --- Node Class --- //

	protected class Node {
		static final String INTERIOR = "Interiror";
		static final String LEAF = "leaf";
		private String name;
		private String type;
		private Vector<Node>	children	= new Vector<>();
		private MetaNode metanode = null;
		String nodetype = null;
		DmtData data = null;

		Node(String name, Node[] children, boolean isInterior) {
			this.name = name;
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					this.children.add(children[i]);
				}
			} else
				this.children = new Vector<>();
			if (isInterior)
				type = INTERIOR;
			else
				type = LEAF;
		}

		protected Node findNode(String[] path) {
			for (int i = 0; i < children.size(); i++) {
				if ((children.get(i).getName()).equals(path[0])) {
					if (path.length == 1) {
						return children.get(i);
					} else {
						String[] nextpath = new String[path.length - 1];
						for (int x = 1; x < path.length; x++) {
							nextpath[x - 1] = path[x];
						}
						return children.get(i).findNode(nextpath);
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
				nodes[i] = (children.get(i));
			}
			return nodes;
		}

		protected String[] getChildNodeNames() {
			@SuppressWarnings("hiding")
			String[] name = new String[children.size()];
			for (int i = 0; i < children.size(); i++) {
				Node node = (children.get(i));
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
