package org.osgi.impl.service.residentialmanagement.plugins;

import java.util.Vector;
import java.util.Date;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;
import info.dmtree.spi.ReadableDataSession;

class FrameworkReadOnlySession implements ReadableDataSession {

	protected static final String ID = "id";
	protected static final String STARTLEVEL = "StartLevel";
	protected static final String INSTALLBUNDLE = "InstallBundle";
	protected static final String LIFECYCLE = "Lifecycle";
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

	protected FrameworkPlugin plugin;
	protected BundleContext context;
	// protected int InstanceNumber = 1;
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

		/*
		 * if (path.length == 1) { String[] children = new
		 * String[InstanceNumber]; for (int i = 0; i < InstanceNumber; i++) {
		 * children[i] = String.valueOf(InstanceNumber); } return children; }
		 */

		if (path.length == 1) {
			String[] children = new String[4];
			children[0] = STARTLEVEL;
			children[1] = INSTALLBUNDLE;
			children[2] = LIFECYCLE;
			children[3] = EXT;

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

			if (path[1].equals(LIFECYCLE)) {
				String[] children = new String[3];
				children[0] = RESTART;
				children[1] = SHUTDOWN;
				children[2] = UPDATE;

				return children;
			}
		}

		if (path.length == 3 && path[1].equals(INSTALLBUNDLE)) {
			Node[] ids = installbundle.getChildren();
			
			for (int i = 0; i < ids.length; i++) {
				if(ids[i].getName().equals(path[2])){
					Node[] gc = ids[i].getChildren();
					String[] children = new String[gc.length];
					for(int g = 0; g < gc.length; g++){
						children[g] = gc[g].getName();
					}
					return children;
				}
			}
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

		/*
		 * if (path.length == 1) // ./OSGi/Framework/<id> return new
		 * FrameworkMetaNode( "Instance number of Framework object.",
		 * MetaNode.DYNAMIC, FrameworkMetaNode.CAN_ADD,
		 * FrameworkMetaNode.CAN_DELETE, !FrameworkMetaNode.ALLOW_ZERO,
		 * FrameworkMetaNode.ALLOW_INFINITE);
		 */

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

			if (path[1].equals(LIFECYCLE))
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

		/*
		 * if (path.length == 1) { try { Integer.parseInt(path[0]); return true;
		 * } catch (NumberFormatException e) { return false; } }
		 */

		if (path.length == 2) {
			if (path[1].equals(STARTLEVEL) || path[1].equals(INSTALLBUNDLE)
					|| path[1].equals(LIFECYCLE) || path[1].equals(EXT))
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
		}

		if (path.length == 4) {
			if (path[3].equals(LOCATION) || path[3].equals(URL)
					|| path[3].equals(ERROR))
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
					|| path[3].equals(ERROR))
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

		if (path.length == 4) {
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

	// ----- InstallBundle subtree -----//

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
