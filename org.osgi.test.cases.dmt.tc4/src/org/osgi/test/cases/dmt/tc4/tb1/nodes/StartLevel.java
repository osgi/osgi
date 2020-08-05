package org.osgi.test.cases.dmt.tc4.tb1.nodes;

import org.osgi.service.dmt.MetaNode;

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
	

	@Override
	public void open() {
		activeStartLevel.open();
		requestedStartLevel.open();
		initialBundleStartLevel.open();
	}
	
	@Override
	public void close() {
		activeStartLevel.close();
		requestedStartLevel.close();
		initialBundleStartLevel.close();
	}
	
	@Override
	public String getNodeName() {
		return "StartLevel";
	}
	
	@Override
	public String getNodePath() {
		return "./OSGi/_Framework/StartLevel";
	}
	
	@Override
	public Node[] getChildNodes() {
		return new Node[] {activeStartLevel, requestedStartLevel, initialBundleStartLevel};
	}
	
	@Override
	public String[] getChildNodeNames() {
		return new String[] {activeStartLevel.getNodeName(), 
				requestedStartLevel.getNodeName(), initialBundleStartLevel.getNodeName()};
	}

	@Override
	public MetaNode getMetaNode() {
		return new InteriorMetaNode() {
			@Override
			public boolean can(int operation) {
				if ( operation == MetaNode.CMD_GET ) {
					return true;
				}
				
				return false;
			}

			@Override
			public String getDescription() {
				return "A leaf node used to configure the Framework's StartLevel. When this node value is replaced or the  Bundles  sub-tree  starts, StartLevel#setStartLevel with the specified value must  be called.  This value must  be kept persistently.";
			}

			@Override
			public int getMaxOccurrence() {
				return 1;
			}

			@Override
			public int getScope() {
				return MetaNode.PERMANENT;
			}
		};
	}
}
