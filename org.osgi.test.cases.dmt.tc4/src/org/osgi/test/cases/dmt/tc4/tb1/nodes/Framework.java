package org.osgi.test.cases.dmt.tc4.tb1.nodes;

import org.osgi.service.dmt.MetaNode;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.dmt.tc4.tb1.intf.InteriorMetaNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.InteriorNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;

public class Framework extends InteriorNode {
	private InteriorNode startLevel;
	private InteriorNode frameworkLifecycle;
	
	
	public Framework(BundleContext context) {
		startLevel = new StartLevel(context);
		frameworkLifecycle = new FrameworkLifecycle(context);
	}
	

	public void open() {
		startLevel.open();
		frameworkLifecycle.open();
	}
	
	public void close() {
		startLevel.close();
		frameworkLifecycle.close();
	}
	
	public String getNodeName() {
		return "_Framework";
	}
	
	public String getNodePath() {
		return "./OSGi/_Framework";
	}
	
	public Node[] getChildNodes() {
//		return new Node[] {startLevel, frameworkLifecycle };
		return new Node[] {frameworkLifecycle };
	}
	
	public String[] getChildNodeNames() {
//		return new String[] { startLevel.getNodeName(), frameworkLifecycle.getNodeName() };
		return new String[] { frameworkLifecycle.getNodeName() };
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
				return "Framework Root node.";
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
