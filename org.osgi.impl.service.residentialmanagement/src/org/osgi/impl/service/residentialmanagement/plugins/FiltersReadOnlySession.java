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

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;

import info.dmtree.DmtAdmin;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import info.dmtree.spi.ReadableDataSession;

/**
 * 
 * @author Koya MORI NTT Corporation, Shigekuni KONDO
 */
public class FiltersReadOnlySession implements ReadableDataSession {
	protected static final String FILTERS = "Filters";
	protected static final String TARGETSUBTREE = "TargetSubtree";
	protected static final String FILTER = "Filter";
	protected static final String RESULT = "Result";
	protected static final String DEVICE = "Device";
	protected static final String SERVICES = "Services";
	protected static final String OSGI = "OSGi";
	protected static final String INSTANCE_ID = "1";
	protected static final String PLUGIN_ROOT_URI = "./OSGi";

	protected FiltersPlugin plugin;
	protected BundleContext context;
	protected Hashtable searches;
	protected int searchid;
	private DmtAdmin dmtAdmin;
	private DmtSession session;

	FiltersReadOnlySession(FiltersPlugin plugin, BundleContext context) {
		this.plugin = plugin;
		this.context = context;
		searches = new Hashtable();
		searchid = 1;
	}

	public void close() throws DmtException {

	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		ServiceReference dmtServiceRef = context
				.getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) context.getService(dmtServiceRef);

		if (path.length == 1) {
			if (searches.size() == 0) {
				String[] children = new String[1];
				children[0] = "";
				return children;
			}
			String[] children = new String[searches.size() + 1];
			Enumeration e = searches.keys();
			int n = 0;
			while (e.hasMoreElements()) {
				children[n] = (String) e.nextElement();
				n++;
			}
			return children;
		}

		if (path.length == 2) {
			if (searches.get(path[1]) != null) {
				String[] children = new String[3];
				children[0] = TARGETSUBTREE;
				children[1] = FILTER;
				children[2] = RESULT;
				return children;
			}
		}

		if (path.length == 3) {
			if (searches.get(path[1]) != null) {
				String[] children = new String[1];
				children[0] = DEVICE;
				return children;
			}
		}

		if (path.length == 4) {
			if (searches.get(path[1]) != null) {
				String[] children = new String[1];
				children[0] = SERVICES;
				return children;
			}
		}

		if (path.length == 5) {
			if (searches.get(path[1]) != null) {
				String[] children = new String[1];
				children[0] = OSGI;
				return children;
			}
		}

		if (path.length == 6) {
			if (searches.get(path[1]) != null) {
				String[] children = new String[1];
				children[0] = INSTANCE_ID;
				return children;
			}
		}

		if (path.length >= 7) {
			Search search = (Search) searches.get(path[1]);
			if (search == null)
				return new String[0];
			session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
					DmtSession.LOCK_TYPE_SHARED);
			searchTree(search);
			String reqPath = processNodepath(nodePath);
			Enumeration enu = search.targetSubtreeList.keys();
			while (enu.hasMoreElements()) {
				String ts = (String) enu.nextElement();
				if (ts.equals(reqPath)) {
					String[] children = (String[]) search.targetSubtreeList
							.get(ts);
					session.close();
					return children;
				} else if (ts.startsWith(reqPath)) {
					String[] children = new String[1];
					String partialTs = this.replaceAll(ts, reqPath, "");
					if (partialTs.indexOf("/", 1) == -1) {
						children[0] = partialTs.substring(1);
						session.close();
						return children;
					}
					children[0] = partialTs.substring(1, partialTs.indexOf("/",
							1));
					session.close();
					return children;
				} else if (reqPath.startsWith(ts)) {
					String[] filterdChildren = (String[]) search.targetSubtreeList
							.get(ts);
					for (int i = 0; i < filterdChildren.length; i++) {
						String tsWithChildren = ts + "/" + filterdChildren[i];
						if (tsWithChildren.startsWith(reqPath)) {
							String[] children = session
									.getChildNodeNames(reqPath);
							session.close();
							return children;
						}
					}
				}
			}
		}
		// other case
		String[] children = new String[0];
		session.close();
		return children;
	}

	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) // ./OSGi/Filters
			return new FiltersMetaNode("Filters Root node.",
					MetaNode.PERMANENT, !FiltersMetaNode.CAN_ADD,
					!FiltersMetaNode.CAN_DELETE, !FiltersMetaNode.ALLOW_ZERO,
					!FiltersMetaNode.ALLOW_INFINITE);

		if (path.length == 2) // ./OSGi/Filters/<search_id>
			return new FiltersMetaNode("Search ID of the filters.",
					MetaNode.DYNAMIC, FiltersMetaNode.CAN_ADD,
					FiltersMetaNode.CAN_DELETE, FiltersMetaNode.ALLOW_ZERO,
					FiltersMetaNode.ALLOW_INFINITE);

		if (path.length == 3) {
			if (path[2].equals(TARGETSUBTREE))
				return new FiltersMetaNode(
						"The target subtree for the filter search.",
						!FiltersMetaNode.CAN_DELETE,
						FiltersMetaNode.CAN_REPLACE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE, DmtData.FORMAT_STRING,
						null);

			if (path[2].equals(FILTER))
				return new FiltersMetaNode(
						"The filter string for the filter search.",
						!FiltersMetaNode.CAN_DELETE,
						FiltersMetaNode.CAN_REPLACE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE, DmtData.FORMAT_STRING,
						null);

			if (path[2].equals(RESULT))
				return new FiltersMetaNode("Result subtree.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE);
		}

		if (path.length >= 4) {
			if (path[2].equals(RESULT))
				return new FiltersMetaNode("Result subtree.",
						MetaNode.AUTOMATIC, !FiltersMetaNode.CAN_ADD,
						!FiltersMetaNode.CAN_DELETE,
						!FiltersMetaNode.ALLOW_ZERO,
						!FiltersMetaNode.ALLOW_INFINITE);
		}

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"No such node defined in the filters tree.");

	}

	public int getNodeSize(String[] nodePath) throws DmtException {
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
		return null;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		ServiceReference dmtServiceRef = context
				.getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) context.getService(dmtServiceRef);

		if (path.length <= 2)
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");

		if (path.length == 3) {
			if (path[2].equals(TARGETSUBTREE)) {
				Search search = (Search) searches.get(path[1]);
				if (search != null)
					return new DmtData(search.target);
			} else if (path[2].equals(FILTER)) {
				Search search = (Search) searches.get(path[1]);
				if (search != null)
					return new DmtData(search.filter);
			}
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");
		}

		if (path.length == 4 || path.length == 5 || path.length == 6
				|| path.length == 7) {
			throw new DmtException(nodePath,
					DmtException.FEATURE_NOT_SUPPORTED,
					"The given path indicates an interior node.");
		}

		if (path.length >= 8 && path[2].equals(RESULT)) {
			Search search = (Search) searches.get(path[1]);
			session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
					DmtSession.LOCK_TYPE_SHARED);
			String reqPath = processNodepath(nodePath);
			if (!session.isLeafNode(reqPath))
				throw new DmtException(nodePath,
						DmtException.FEATURE_NOT_SUPPORTED,
						"The given path indicates an interior node.");
			searchTree(search);
			Enumeration enu = search.targetSubtreeList.keys();
			while (enu.hasMoreElements()) {
				String ts = (String) enu.nextElement();
				if (ts.equals(reqPath) || ts.startsWith(reqPath)) {
					DmtData value = session.getNodeValue(reqPath);
					session.close();
					return value;
				} else if (reqPath.startsWith(ts)) {
					String[] filterdChildren = (String[]) search.targetSubtreeList
							.get(ts);
					for (int i = 0; i < filterdChildren.length; i++) {
						String tsWithChildren = ts + "/" + filterdChildren[i];
						if (tsWithChildren.startsWith(reqPath)) {
							DmtData value = session.getNodeValue(reqPath);
							session.close();
							return value;
						}
					}
				}
				session.close();
				throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
						"The node indicated the given path is not found.");
			}
			session.close();
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified key does not exist in the filters object.");
	}

	public int getNodeVersion(String[] nodePath) throws DmtException {
		throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED,
				"Version property is not supported.");
	}

	public boolean isLeafNode(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);
		ServiceReference dmtServiceRef = context
				.getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) context.getService(dmtServiceRef);

		if (path.length <= 2)
			return false;

		if (path.length == 3) {
			if (path[2].equals(TARGETSUBTREE)) {
				Search search = (Search) searches.get(path[1]);
				if (search != null)
					return true;
			} else if (path[2].equals(FILTER)) {
				Search search = (Search) searches.get(path[1]);
				if (search != null)
					return true;
			}
			return false;
		}

		if (path.length == 4 || path.length == 5 || path.length == 6
				|| path.length == 7) {
			return false;
		}

		if (path.length >= 8 & path[2].equals(RESULT)) {
			Search search = (Search) searches.get(path[1]);
			session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
					DmtSession.LOCK_TYPE_SHARED);
			String reqPath = processNodepath(nodePath);
			searchTree(search);
			Enumeration enu = search.targetSubtreeList.keys();
			while (enu.hasMoreElements()) {
				String ts = (String) enu.nextElement();
				if (ts.equals(reqPath) || ts.startsWith(reqPath)) {
					boolean flag = session.isLeafNode(reqPath);
					session.close();
					return flag;
				} else if (reqPath.startsWith(ts)) {
					String[] filterdChildren = (String[]) search.targetSubtreeList
							.get(ts);
					for (int i = 0; i < filterdChildren.length; i++) {
						String tsWithChildren = ts + "/" + filterdChildren[i];
						if (tsWithChildren.startsWith(reqPath)) {
							boolean flag = session.isLeafNode(reqPath);
							session.close();
							return flag;
						}
					}
				}
			}
			session.close();
		}
		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified node does not exist in the filters object.");
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);
		ServiceReference dmtServiceRef = context
				.getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) context.getService(dmtServiceRef);

		if (path.length == 1 && path[0].equals(FILTERS)) {
			return true;
		}

		if (path.length == 2) {
			Search search = (Search) searches.get(path[1]);
			if (search != null)
				return true;
		}

		if (path.length == 3) {
			if (path[2].equals(TARGETSUBTREE)) {
				Search search = (Search) searches.get(path[1]);
				if (search != null)
					return true;
			} else if (path[2].equals(FILTER)) {
				Search search = (Search) searches.get(path[1]);
				if (search != null)
					return true;
			} else if (path[2].equals(RESULT)) {
				Search search = (Search) searches.get(path[1]);
				if (search != null)
					return true;
			}
		}

		if (path.length == 4 || path.length == 5 || path.length == 6
				|| path.length == 7) {
			return true;
		}

		if (path.length >= 8 && path[2].equals(RESULT)) {
			try {
				Search search = (Search) searches.get(path[1]);
				session = dmtAdmin.getSession(PLUGIN_ROOT_URI,
						DmtSession.LOCK_TYPE_SHARED);
				String reqPath = processNodepath(nodePath);
				searchTree(search);
				Enumeration enu = search.targetSubtreeList.keys();
				while (enu.hasMoreElements()) {
					String ts = (String) enu.nextElement();
					if (ts.equals(reqPath) || ts.startsWith(reqPath)) {
						boolean flag = session.isNodeUri(reqPath);
						session.close();
						return flag;
					} else if (reqPath.startsWith(ts)) {
						String[] filterdChildren = (String[]) search.targetSubtreeList
								.get(ts);
						for (int i = 0; i < filterdChildren.length; i++) {
							String tsWithChildren = ts + "/"
									+ filterdChildren[i];
							if (tsWithChildren.startsWith(reqPath)) {
								boolean flag = session.isNodeUri(reqPath);
								session.close();
								return flag;
							}
						}
					}
				}
				session.close();
			} catch (Exception e) {
				try {
					session.close();
				} catch (DmtException e1) {
					return false;
				}
				return false;
			}
		}

		return false;
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and timestamp properties are not supported

	}

	// ---------------Utility--------------------------------------------------------------------

	private String replaceAll(String value, String old_str, String new_str) {
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

	protected String[] shapedPath(String[] nodePath) {
		int size = nodePath.length;
		int srcPos = 3;
		int destPos = 0;
		int length = size - srcPos;
		String[] newPath = new String[length];
		System.arraycopy(nodePath, srcPos, newPath, destPos, length);
		return newPath;
	}

	private String processNodepath(String[] nodePath) {
		int size = nodePath.length;
		int srcPos = 8;
		int destPos = 0;
		int length = size - srcPos;
		String[] newPath = new String[length];
		System.arraycopy(nodePath, srcPos, newPath, destPos, length);
		StringBuffer sb = new StringBuffer();
		sb.append("./");
		for (int i = 0; i < newPath.length; i++) {
			sb.append(newPath[i]);
			sb.append("/");
		}
		StringBuffer path = sb.deleteCharAt(sb.length() - 1);
		return path.toString();
	}

	private String[] processCommaSeparatedValue(String value) {
		StringTokenizer st = new StringTokenizer(value, ",");
		String[] arrayValue = new String[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			arrayValue[i] = st.nextToken();
		}
		if (arrayValue.length == 0)
			return null;
		return arrayValue;
	}

	protected class Search {
		String target;
		String filter;
		Hashtable targetSubtreeList = new Hashtable();

		Search(String target, String filter) {
			this.target = target;
			this.filter = filter;
		}
	}

	// ------------------------------------------------------------------------------------------

	private Hashtable findTergetSubtree(String path, String targetPath,
			Hashtable targetSubtreeList) throws DmtException {
		String[] children = session.getChildNodeNames(path);
		try {
			for (int i = 0; i < children.length; i++) {
				if (children[i].equals("BundleResources")
						|| children[i].equals("Log")
						|| children[i].equals("Configuration")
						|| children[i].equals("Policy")
						|| children[i].equals("Filters")
						|| children[i].equals("Application")
						|| children[i].equals("Deployment")
						|| children[i].equals("Monitor")
						|| children[i].equals(""))
					continue;
				if (children[i].equals(targetPath)) {
					targetSubtreeList.put(path + "/" + children[i], "");
				} else if (!(children[i].equals(targetPath))
						&& session.isLeafNode(path + "/" + children[i])) {
				} else {
					targetSubtreeList = findTergetSubtree(path + "/"
							+ children[i], targetPath, targetSubtreeList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return targetSubtreeList;
	}

	private Hashtable processFilter(String filter) {
		Hashtable filterTable = new Hashtable();
		ArrayList array = new ArrayList();
		// Add ArrayList
		while (filter.indexOf("=") != -1) {
			String partOfFilter = filter.substring(filter.indexOf("(") + 1,
					filter.indexOf("="));
			if (partOfFilter.indexOf("(") == -1) {
				filterTable.put(partOfFilter, array);

			} else {
				while (partOfFilter.indexOf("(") != -1) {
					partOfFilter = partOfFilter.substring(partOfFilter
							.indexOf("(") + 1);
				}
				filterTable.put(partOfFilter, array);
			}
			filter = filter.substring((filter.indexOf("=") + 1));
		}
		return filterTable;
	}

	// ------------------------------------------------------------------------------------------

	private void searchTree(Search search) throws DmtException {
		// create TS list
		String targetSubtree = replaceAll(search.target, "/Device/Services/",
				"./");

		int star = targetSubtree.indexOf("/*/");
		int bar = targetSubtree.indexOf("/-/");
		if (star == -1 && bar == -1) {
			if (targetSubtree.lastIndexOf("/") == targetSubtree.length() - 1) {
				targetSubtree = targetSubtree.substring(0, targetSubtree
						.lastIndexOf("/"));
			}
			search.targetSubtreeList.put(targetSubtree, "");
		} else if (star != -1 || bar != -1) {
			String startPath = targetSubtree.substring(0, targetSubtree
					.indexOf("/*/"));
			String targetPath = targetSubtree.substring(targetSubtree
					.indexOf("/*/") + 3, targetSubtree.indexOf("/",
					targetSubtree.indexOf("/*/") + 3));
			Hashtable tsList = new Hashtable();
			search.targetSubtreeList = findTergetSubtree(startPath, targetPath,
					tsList);
		}

		// Filtering of TS (list)
		boolean frag = false;
		String sFilter = search.filter;
		Filter filter = null;
		try {
			filter = context.createFilter(sFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Hashtable filtersTable = processFilter(sFilter);
		Enumeration e = search.targetSubtreeList.keys();
		while (e.hasMoreElements()) {
			Vector childrenVector = new Vector();
			String ts = (String) e.nextElement();
			String[] children = session.getChildNodeNames(ts);
			for (int i = 0; i < children.length; i++) {
				filtersTable = cheackFilter(ts + "/" + children[i],
						filtersTable);
				for (Enumeration keys = filtersTable.keys(); keys
						.hasMoreElements();) {
					String targetNodeName = (String) keys.nextElement();
					Object obj = filtersTable.get(targetNodeName);
					if (obj instanceof ArrayList) {
						ArrayList array = (ArrayList) obj;
						String[] values = new String[array.size()];
						for (int j = 0; j < array.size(); j++) {
							values[j] = (String) array.get(j);
						}
						filtersTable.put(targetNodeName, values);
						frag = true;
					}
				}
				if (frag) {
					if (filter.match(filtersTable)) {
						childrenVector.add(children[i]);
					}
				}
				frag = false;
			}
			String[] childrenArray = new String[childrenVector.size()];
			for (int v = 0; v < childrenVector.size(); v++) {
				childrenArray[v] = (String) childrenVector.get(v);
			}
			search.targetSubtreeList.put(ts, childrenArray);
		}
	}

	private Hashtable cheackFilter(String targetSubtree, Hashtable filtersTable)
			throws DmtException {
		String[] children = session.getChildNodeNames(targetSubtree);
		for (int i = 0; i < children.length; i++) {
			if (children[i].equals(""))
				continue;
			if (session.isLeafNode(targetSubtree + "/" + children[i])) {
				for (Enumeration keys = filtersTable.keys(); keys
						.hasMoreElements();) {
					String targetNodeName = (String) keys.nextElement();
					if (targetNodeName.startsWith("@")) {
						String key = "Key";
						if (children[i].equals(key)) {
							String keyValue = session.getNodeValue(
									targetSubtree + "/" + children[i])
									.getString();
							if (keyValue.equals(targetNodeName.substring(1))) {
								String value = session.getNodeValue(
										targetSubtree + "/Values").getString();
								String[] valueOfArray = processCommaSeparatedValue(value);
								Object obj = filtersTable.get(targetNodeName);
								if (obj instanceof ArrayList) {
									ArrayList array = (ArrayList) filtersTable
											.get(targetNodeName);
									for (int a = 0; a < valueOfArray.length; a++) {
										array.add(valueOfArray[a]);
									}
									filtersTable.put(targetNodeName, array);
								} else {
									ArrayList array = new ArrayList();
									for (int a = 0; a < valueOfArray.length; a++) {
										array.add(valueOfArray[a]);
									}
									filtersTable.put(targetNodeName, array);
								}
							}
						}
					} else {
						if (children[i].equals(targetNodeName)) {
							String value = session.getNodeValue(
									targetSubtree + "/" + children[i])
									.getString();
							String[] valueOfArray = processCommaSeparatedValue(value);
							Object obj = filtersTable.get(targetNodeName);
							if (obj instanceof ArrayList
									&& valueOfArray != null) {
								ArrayList array = (ArrayList) filtersTable
										.get(targetNodeName);
								for (int a = 0; a < valueOfArray.length; a++) {
									array.add(valueOfArray[a]);
								}
								filtersTable.put(targetNodeName, array);
							} else if (valueOfArray != null) {
								ArrayList array = new ArrayList();
								for (int a = 0; a < valueOfArray.length; a++) {
									array.add(valueOfArray[a]);
								}
								filtersTable.put(targetNodeName, array);
							}
						}
					}
				}
			} else {
				filtersTable = cheackFilter(targetSubtree + "/" + children[i],
						filtersTable);
			}
		}
		return filtersTable;
	}
}