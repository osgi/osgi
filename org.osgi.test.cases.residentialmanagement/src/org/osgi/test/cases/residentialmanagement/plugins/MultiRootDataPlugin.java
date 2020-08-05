package org.osgi.test.cases.residentialmanagement.plugins;

import java.util.Date;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.dmt.spi.MountPlugin;
import org.osgi.service.dmt.spi.MountPoint;
import org.osgi.service.dmt.spi.ReadWriteDataSession;
import org.osgi.service.dmt.spi.ReadableDataSession;
import org.osgi.service.dmt.spi.TransactionalDataSession;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * this is a simplified plugin that also implements all 3 types of Sessions. It
 * is simplified because not all methods are fully implemented.
 */
public class MultiRootDataPlugin implements DataPlugin, TransactionalDataSession, MountPlugin, EventHandler, ExecPlugin {

	public static final int NO_ACTION = -1;
	public static final int ACTION_SET_NODE_VALUE = 1;
	public static final int ACTION_IS_NODE_URI = 2;
	
	public static final String FORCED_FATAL_EXCEPTION_MESSAGE = "forced fatal exception";

	// some debugging variables keep the state of the last invoked actions
	int lastAction = NO_ACTION;
	String lastOpenedSession;
	String lastUri;
	Object lastValue;
	Set<MountPoint> addedMountPoints;
	Set<MountPoint> removedMountPoints;
	Event lastReceivedEvent;
	String[] lastExecPath;
	String lastExecData;
	
	boolean forceFatalExceptionOnNextGetNodeSize; 

	@SuppressWarnings("unused")
	private String pluginID;
	private Node pluginRootNode;
	
	private Set<MountPoint>		mountPoints;
	

	/**
	 * a simple Plugin that knows
	 * 
	 * @param pluginID
	 *            an identifier that can be used for identity checks
	 * @param rootUri
	 *            the intended uri that the plugin should be mounted on
	 * @param root
	 *            the root <code>Node</code>
	 */
	public MultiRootDataPlugin(String pluginID, Node root) {
		this.pluginID = pluginID;
		this.pluginRootNode = root;
	}

	/**
	 * traverses the plugins subtree to find a suitable node
	 */
	private Node findNode(Node start, String[] absPath, final int level) {
		if ( start.getName().equals(absPath[level]) ) {
			// full match ?
			if ( absPath.length == level+1 )
				return start;
			
			Iterator<Node> iterator = start.getChildren().iterator();
			while (iterator.hasNext()) {
				// check next segment of the path with the nodes children
				Node found = findNode(iterator.next(), absPath, level + 1);
				if ( found != null )
					return found;
			}
		}
		return null;
	}
	
	private synchronized Node findNode( String[] absPath ) {
		// we are potentially "multihomed", i.e. the absolute path contains one of our root-uris
		// need to figure out, which rootUri fits in order to get the correct relative path
		Node node = null;
		for ( MountPoint mp : getMountPoints() ) {
			String[] rootUri = mp.getMountPath();
			if ( arrayStartsWith(absPath, rootUri)) {
				// start search for matching dataRootUri
				pluginRootNode.setName( rootUri[rootUri.length -1 ]);
				node = findNode(pluginRootNode, absPath, rootUri.length -1 );
				if ( node != null )
					return node;
			}
		}
		return node;
	}
	
	private boolean arrayStartsWith( String[] full, String[] part ) {
		if ( part == null || part.length == 0 ) return false;
		if ( full.length < part.length ) return false;
		for (int i = 0; i < part.length; i++) {
			if ( ! part[i].equals( full[i] ))
				return false;
		}
		return true;
	}

	@Override
	public ReadableDataSession openReadOnlySession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		lastOpenedSession = Uri.toUri(sessionRoot);
		return this;
	}

	@Override
	public ReadWriteDataSession openReadWriteSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		lastOpenedSession = Uri.toUri(sessionRoot);
		return this;
	}

	@Override
	public TransactionalDataSession openAtomicSession(String[] sessionRoot,
			DmtSession session) throws DmtException {
		lastOpenedSession = Uri.toUri(sessionRoot);
		return this;
	}

	@Override
	public void copy(String[] nodePath, String[] newNodePath, boolean recursive)
			throws DmtException {
		throw new DmtException(Uri.toUri(nodePath), DmtException.FEATURE_NOT_SUPPORTED, "This plugin does not support copy operations.");
	}

	@Override
	public void createInteriorNode(String[] nodePath, String type)
			throws DmtException {
		// find parent node
		String[] parentPath = new String[ nodePath.length - 1 ];
		for (int i = 0; i < parentPath.length; i++)
			parentPath[i] = nodePath[i];
		Node parent = findNode(parentPath);
		if ( parent != null )
			new Node(parent, nodePath[nodePath.length - 1], null );
	}

	@Override
	public void createLeafNode(String[] nodePath, DmtData value, String mimeType)
			throws DmtException {
		// find parent node
		String[] parentPath = new String[ nodePath.length - 1 ];
		for (int i = 0; i < parentPath.length; i++)
			parentPath[i] = nodePath[i];
		Node parent = findNode(parentPath);
		if ( parent != null )
			new Node(parent, nodePath[nodePath.length - 1], "" + value );
	}

	@Override
	public void deleteNode(String[] nodePath) throws DmtException {
		Node n = findNode(nodePath );
		n.getParent().removeChild(n);
	}

	@Override
	public void renameNode(String[] nodePath, String newName)
			throws DmtException {
		Node n = findNode(nodePath );
		n.setName(newName);
	}

	@Override
	public void setNodeTitle(String[] nodePath, String title)
			throws DmtException {
		Node n = findNode(nodePath );
		n.setTitle(title);
	}

	@Override
	public void setNodeType(String[] nodePath, String type) throws DmtException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNodeValue(String[] nodePath, DmtData data)
			throws DmtException {
		this.lastAction = ACTION_SET_NODE_VALUE;
		this.lastUri = Uri.toUri(nodePath);
		this.lastValue = data;
	}

	@Override
	public void nodeChanged(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws DmtException {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] getChildNodeNames(String[] nodePath) throws DmtException {
		Node n = findNode(nodePath);
		Vector<String> children = new Vector<String>();
		if ( n != null ) {
			for ( Node child : n.getChildren() )
				children.add( child.getName());
		}
		return children.toArray( new String[n.getChildren().size()] );
	}

	@Override
	public MetaNode getMetaNode(String[] nodePath) throws DmtException {
		Node n = findNode(nodePath);
		return n != null ? n.getMetaNode() : null;
	}

	@Override
	public int getNodeSize(String[] nodePath) throws DmtException {
		if ( forceFatalExceptionOnNextGetNodeSize ) {
			forceFatalExceptionOnNextGetNodeSize = false;
			throw new DmtException(nodePath, DmtException.FEATURE_NOT_SUPPORTED, FORCED_FATAL_EXCEPTION_MESSAGE, null, true );
		}
		return 0;
	}

	@Override
	public Date getNodeTimestamp(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNodeTitle(String[] nodePath) throws DmtException {
		Node n = findNode(nodePath);
		return n != null ? n.getTitle() : null;
	}

	@Override
	public String getNodeType(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNodeUri(String[] nodePath) {
		this.lastAction = ACTION_IS_NODE_URI;
		this.lastUri = Uri.toUri(nodePath);
		return findNode(nodePath) != null;
	}

	// it is a leaf node, if it has a value and no children
	@Override
	public boolean isLeafNode(String[] nodePath) throws DmtException {
		Node n = findNode(nodePath);
		return n != null && n.getValue() != null && n.getChildren().size() == 0;
	}

	@Override
	public DmtData getNodeValue(String[] nodePath) throws DmtException {
		Node n = findNode(nodePath);
		return (n != null ? new DmtData(n.getValue()) : null);
	}

	@Override
	public int getNodeVersion(String[] nodePath) throws DmtException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void commit() throws DmtException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback() throws DmtException {
		// TODO Auto-generated method stub

	}
	
	public void resetStatus() {
		this.lastAction = NO_ACTION;
		this.lastUri = null;
		this.lastOpenedSession = null;
		this.lastValue = null;
		this.lastReceivedEvent = null;
		this.addedMountPoints = null;
		this.removedMountPoints = null;
		this.lastExecPath = null;
		this.lastExecData = null;
		this.forceFatalExceptionOnNextGetNodeSize = false;
	}

	public static void main(String[] args) {
//		Node n2 = new Node(null, "A", "node A");
//		Node n3 = new Node(n2, "B", "node B");
//		MultiRootDataPlugin gdp = new MultiRootDataPlugin("P1", "./A", n2);
//		System.out.println(gdp.findNode(n2, "./A/B"));
	}

	
	
	@Override
	public void mountPointAdded(MountPoint mountPoint) {
		// add to volatile debugging-list
		getAddedMountPoints().add(mountPoint);
		// add to permanent list
		getMountPoints().add(mountPoint);
	}

	@Override
	public void mountPointRemoved(MountPoint mountPoint) {
		getRemovedMountPoints().add(mountPoint);
		// remove from permanent list
		getMountPoints().remove(mountPoint);
	}

	@Override
	public void handleEvent(Event event) {
		this.lastReceivedEvent = event;
		
	}

	@Override
	public void execute(DmtSession session, String[] nodePath,
			String correlator, String data) throws DmtException {
		this.lastExecPath = nodePath;
	}
	
	/**
	 * posts internal events with the given arguments to all mountPoints
	 * @param topic
	 * @param nodes
	 * @param newNodes
	 * @param props
	 */
	void postInternalEvent(String topic, String[] nodes, String[] newNodes,
			Dictionary<String,Object> props) {
		for ( MountPoint mp : getMountPoints() )
			mp.postEvent(topic, nodes, newNodes, props);
	}
	
	// lazy getters
	
	private Set<MountPoint> getAddedMountPoints() {
		if (addedMountPoints == null)
			addedMountPoints = new HashSet<MountPoint>();
		return addedMountPoints;
	}
	
	private Set<MountPoint> getRemovedMountPoints() {
		if (removedMountPoints == null)
			removedMountPoints = new HashSet<MountPoint>();
		return removedMountPoints;
	}
	
	private Set<MountPoint> getMountPoints() {
		if (mountPoints == null)
			mountPoints = new HashSet<MountPoint>();
		return mountPoints;
	}

}
