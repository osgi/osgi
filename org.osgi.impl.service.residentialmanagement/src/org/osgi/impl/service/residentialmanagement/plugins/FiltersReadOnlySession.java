package org.osgi.impl.service.residentialmanagement.plugins;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Dictionary;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Filter;
import info.dmtree.DmtAdmin;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import info.dmtree.DmtIllegalStateException;
import info.dmtree.spi.ReadableDataSession;

public class FiltersReadOnlySession implements ReadableDataSession {
	protected static final String FILTERS = "Filters";
	protected static final String TARGETSUBTREE = "TargetSubtree";
	protected static final String FILTER = "Filter";
	protected static final String RESULT = "Result";

	protected FiltersPlugin plugin;
	protected BundleContext context;

	protected Hashtable searches;
	protected int searchid;

	FiltersReadOnlySession(FiltersPlugin plugin, BundleContext context) {
		this.plugin = plugin;
		this.context = context;

		searches = new Hashtable();
		searchid = 1;
	}

	public void close() throws DmtException {
		// TODO Auto-generated method stub

	}

	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

		if (path.length == 1) {
			if (searches.size() == 0) {
				return new String[0];
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

		if (path.length >= 3) {
			if (searches.get(path[1]) != null & path[2].equals(RESULT)) {
				Vector subtrees = getFilteredSubtrees(path);

				String cpath = ((Search) searches.get(path[1])).target;
				if(cpath.indexOf("/*") != -1) cpath = cpath.substring(0, cpath.indexOf("/*")) + "/";
				if (path.length >= 4) {
					for (int i = 3; i < path.length; i++) {
						cpath = cpath + path[i] + "/";
					}
				} else {
					cpath = ".";
				}

				Vector results = new Vector();
				Iterator i = subtrees.iterator();
				while (i.hasNext()) {
					Hashtable tree = (Hashtable) i.next();
					if (tree.get(cpath) != null){
						String[] res = (String[]) tree.get(cpath);
						for(int t = 0; t < res.length; t++){
							results.add(res[t]);
						}
					}
				}
				
				String[] result = new String[results.size()];
				results.toArray(result);
				
				return result;
			}
		}

		return new String[0];
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
		// TODO Auto-generated method stub
		return null;
	}

	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		String[] path = shapedPath(nodePath);

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
		}

		if (path.length >= 4 & path[2].equals(RESULT)) {
			Search search = (Search) searches.get(path[1]);
			if (search != null) {
				Vector subtrees = getFilteredSubtrees(path);

				String cpath = "";
				for (int i = 0; i < path.length; i++) {
					cpath = cpath + path[i] + "/";
				}

				Iterator i = subtrees.iterator();
				while (i.hasNext()) {
					Hashtable tree = (Hashtable) i.next();
					try {
						if (tree.get(cpath).getClass().getName().equals(
								DmtData.class.getName()))
							return (DmtData) tree.get(cpath);
					} catch (Exception e) {

					}
				}
			}
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

		if (path.length >= 4 & path[2].equals(RESULT)) {
			Search search = (Search) searches.get(path[1]);
			if (search != null) {
				Vector subtrees;
				try{
					subtrees = getFilteredSubtrees(path);
				}catch(Exception e){
					return false;
				}
				
				String cpath = ((Search) searches.get(path[1])).target;
				if(cpath.indexOf("/*") != -1) cpath = cpath.substring(0, cpath.indexOf("/*")) + "/";
				if (path.length >= 4) {
					for (int i = 3; i < path.length; i++) {
						cpath = cpath + path[i] + "/";
					}
				} else {
					cpath = ".";
				}

				Iterator i = subtrees.iterator();
				while (i.hasNext()) {
					Hashtable tree = (Hashtable) i.next();
					try {
						if (tree.get(cpath).getClass().getName().equals(
								DmtData.class.getName()))
							return true;

						return false;
					} catch (Exception e) {

					}
				}

				/*				Vector subtrees = getFilteredSubtrees(path);

				String cpath = "";
				for (int i = 0; i < path.length; i++) {
					cpath = cpath + path[i] + "/";
				}

				Iterator i = subtrees.iterator();
				while (i.hasNext()) {
					Hashtable tree = (Hashtable) i.next();
					try {
						if (tree.get(cpath).getClass().getName().equals(
								DmtData.class.getName()))
							return true;

						return false;
					} catch (Exception e) {

					}
				}*/
			}
		}

		throw new DmtException(nodePath, DmtException.NODE_NOT_FOUND,
				"The specified key does not exist in the filters object.");
	}

	public boolean isNodeUri(String[] nodePath) {
		String[] path = shapedPath(nodePath);

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

		if (path.length >= 4 && path[2].equals(RESULT)) {
			Search search = (Search) searches.get(path[1]);
			if (search != null) {
				Vector subtrees;
				try{
					subtrees = getFilteredSubtrees(path);
				}catch(Exception e){
					return false;
				}
				
				String cpath = ((Search) searches.get(path[1])).target;
				if(cpath.indexOf("/*") != -1) cpath = cpath.substring(0, cpath.indexOf("/*")) + "/";
				if (path.length >= 4) {
					for (int i = 3; i < path.length; i++) {
						cpath = cpath + path[i] + "/";
					}
				} else {
					cpath = ".";
				}

				Iterator i = subtrees.iterator();
				while (i.hasNext()) {
					Hashtable tree = (Hashtable) i.next();
					if (tree.get(cpath) != null){
						return true;
					}
				}
				
/*				Vector subtrees;
				try {
					subtrees = getFilteredSubtrees(path);
				} catch (Exception e) {
					return false;
				}

				String cpath = "";
				for (int i = 0; i < path.length; i++) {
					cpath = cpath + path[i] + "/";
				}

				Iterator i = subtrees.iterator();
				while (i.hasNext()) {
					Hashtable tree = (Hashtable) i.next();
					try {
						if (tree.get(cpath) != null)
							return true;
					} catch (Exception e) {

					}
				}*/
			}
		}

		return false;
	}

	public void nodeChanged(String[] nodePath) throws DmtException {
		// do nothing - the version and timestamp properties are not supported

	}

	protected String[] shapedPath(String[] nodePath) {
		String[] newPath = new String[nodePath.length - 3];
		for (int i = 0; i < nodePath.length - 3; i++) {
			newPath[i] = nodePath[i + 3];
		}

		return newPath;
	}

	private Vector getFilteredSubtrees(String[] path) throws DmtException {
		Search search = (Search) searches.get(path[1]);
		Vector subtrees = new Vector();

		ServiceReference ref = context.getServiceReference(DmtAdmin.class
				.getName());
		DmtAdmin dmtadmin = (DmtAdmin) context.getService(ref);

		/*
		 * String targets = search.target.substring(0,
		 * search.target.lastIndexOf("/")); String[] candidates; if
		 * (search.target.indexOf("*") != -1) { targets = search.target
		 * .substring(0, search.target.indexOf("*") - 1);
		 * 
		 * DmtSession session = dmtadmin.getSession(targets,
		 * DmtSession.LOCK_TYPE_SHARED);
		 * 
		 * candidates = session.getChildNodeNames(targets);
		 * 
		 * session.close(); } else { candidates = new String[] {
		 * search.target.substring(search.target .lastIndexOf("/") + 1,
		 * search.target.length()) }; }
		 */

		String targets = search.target;
		if (targets.indexOf("/*") != -1) {
			targets = targets.substring(0, targets.indexOf("/*"));
		} else if (targets.endsWith("/")) {
			targets = targets.substring(0, targets.lastIndexOf("/"));
		}

		// for (int c = 0; c < candidates.length; c++) {
		DmtSession session = dmtadmin.getSession(targets,
				DmtSession.LOCK_TYPE_SHARED);

		String[] children = session.getChildNodeNames(targets);

		for (int i = 0; i < children.length; i++) {
			Hashtable values = new Hashtable();
			getChildNodeNamesAndValues(session, values, targets + "/"
					+ children[i]);

			if (filterSubtree(values, search.target, search.filter)) {
				/*
				 * String[] tmpchildren = (String[]) values.get("."); if
				 * (tmpchildren != null) { String[] filteredChildren = new
				 * String[tmpchildren.length + 1]; for (int x = 0; x <
				 * tmpchildren.length; x++) { filteredChildren[x] =
				 * tmpchildren[x]; } filteredChildren[tmpchildren.length] =
				 * children[i]; values.put(".", filteredChildren); } else {
				 */
				values.put(".", new String[] { children[i] });
				// }

				subtrees.add(values);
			}
		}

		session.close();
		// }

		return subtrees;
	}

	private void getChildNodeNamesAndValues(DmtSession session,
			Hashtable values, String path) {
		try {
			if (!path.endsWith("/") && session.isLeafNode(path)) {
				DmtData data = session.getNodeValue(path);
				values.put(path, data);
			} else {
				if (path.endsWith("/")) {
					String[] children = session.getChildNodeNames(path
							.substring(0, path.length() - 1));
					values.put(path, children);

					for (int i = 0; i < children.length; i++) {
						getChildNodeNamesAndValues(session, values, path
								+ children[i]);
					}
				} else {
					String[] children = session.getChildNodeNames(path);
					values.put(path + "/", children);

					for (int i = 0; i < children.length; i++) {
						getChildNodeNamesAndValues(session, values, path + "/"
								+ children[i]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean filterSubtree(Hashtable subtree, String target, String query) {
		Filter filter;

		try {
			filter = FrameworkUtil.createFilter(query);
		} catch (InvalidSyntaxException ise) {
			System.out.println("Create filter failed");
			return false;
		}

		Dictionary nodes = new Hashtable();

		Enumeration e = subtree.keys();
		while (e.hasMoreElements()) {
			String fullpath = (String) e.nextElement();

			String path = fullpath;
			for (int position = 0; position < target.length();) {
				if (target.indexOf("/", position) != -1) {
					position = target.indexOf("/", position) + 1;
					path = path.substring(path.indexOf("/") + 1);
				}
			}

			if (subtree.get(fullpath).getClass().getName().equals(
					DmtData.class.getName())) {
				try {
					int format = ((DmtData) subtree.get(fullpath)).getFormat();

					if (format == 1)
						nodes.put(path, String.valueOf(((DmtData) subtree
								.get(fullpath)).getInt()));
					else if (format == 2)
						nodes.put(path, String.valueOf(((DmtData) subtree
								.get(fullpath)).getFloat()));
					else if (format == 4)
						nodes.put(path, ((DmtData) subtree.get(fullpath))
								.getString());
					else if (format == 8)
						nodes.put(path, String.valueOf(((DmtData) subtree
								.get(fullpath)).getBoolean()));
					else if (format == 16)
						nodes.put(path, ((DmtData) subtree.get(fullpath))
								.getDate());
					else if (format == 32)
						nodes.put(path, ((DmtData) subtree.get(fullpath))
								.getTime());
					else if (format == 64)
						nodes.put(path, String.valueOf(((DmtData) subtree
								.get(fullpath)).getBinary()));
					else if (format == 128)
						nodes.put(path, String.valueOf(((DmtData) subtree
								.get(fullpath)).getBase64()));
				} catch (DmtIllegalStateException x) {
					x.printStackTrace();
				}
			} else if (subtree.get(fullpath).getClass().getName().equals(
					String[].class.getName())) {
				nodes.put(path, (String[]) subtree.get(fullpath));
			}
		}

		return filter.match(nodes);
	}

	protected class Search {
		String target;
		String filter;

		Search(String target, String filter) {
			this.target = target;
			this.filter = filter;
		}
	}

}
