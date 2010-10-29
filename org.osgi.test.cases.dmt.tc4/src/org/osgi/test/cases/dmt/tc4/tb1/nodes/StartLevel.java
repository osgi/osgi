package org.osgi.test.cases.dmt.tc4.tb1.nodes;

import info.dmtree.MetaNode;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.dmt.tc4.tb1.intf.InteriorMetaNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.InteriorNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.LeafNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;

public class StartLevel extends InteriorNode {
	private LeafNode activeStartLevel;
	private LeafNode requestedStartLevel;
	private LeafNode initialBundleStartLevel;
	
	
	public StartLevel(BundleContext context) {
		activeStartLevel = new ActiveStartLevel(context);
		requestedStartLevel = new RequestedStartLevel(context);
		initialBundleStartLevel = new InitialBundleStartLevel(context);
	}
	

	public void open() {
		activeStartLevel.open();
		requestedStartLevel.open();
		initialBundleStartLevel.open();
	}
	
	public void close() {
		activeStartLevel.close();
		requestedStartLevel.close();
		initialBundleStartLevel.close();
	}
	
	public String getNodeName() {
		return "StartLevel";
	}
	
	public String getNodePath() {
		return "./OSGi/_Framework/StartLevel";
	}
	
	public Node[] getChildNodes() {
		return new Node[] {activeStartLevel, requestedStartLevel, initialBundleStartLevel};
	}
	
	public String[] getChildNodeNames() {
		return new String[] {activeStartLevel.getNodeName(), 
				requestedStartLevel.getNodeName(), initialBundleStartLevel.getNodeName()};
	}

	public MetaNode getMetaNode() {
		return new InteriorMetaNode() {
			public boolean can(int operation) {
				if ( operation == MetaNode.CMD_GET ) {
					return true;
				}
				
				return false;
			}

			public String getDescription() {
				return "A leaf node used to configure the Framework's StartLevel. When this node value is replaced or the  Bundles  sub-tree  starts, StartLevel#setStartLevel with the specified value must  be called.  This value must  be kept persistently.";
			}

			public int getMaxOccurrence() {
				return 1;
			}

			public int getScope() {
				return MetaNode.PERMANENT;
			}
		};
	}
}
