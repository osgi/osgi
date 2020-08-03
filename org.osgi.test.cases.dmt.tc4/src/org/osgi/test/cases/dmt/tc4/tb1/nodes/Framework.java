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
	

	@Override
	public void open() {
		startLevel.open();
		frameworkLifecycle.open();
	}
	
	@Override
	public void close() {
		startLevel.close();
		frameworkLifecycle.close();
	}
	
	@Override
	public String getNodeName() {
		return "_Framework";
	}
	
	@Override
	public String getNodePath() {
		return "./OSGi/_Framework";
	}
	
	@Override
	public Node[] getChildNodes() {
//		return new Node[] {startLevel, frameworkLifecycle };
		return new Node[] {frameworkLifecycle };
	}
	
	@Override
	public String[] getChildNodeNames() {
//		return new String[] { startLevel.getNodeName(), frameworkLifecycle.getNodeName() };
		return new String[] { frameworkLifecycle.getNodeName() };
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
				return "Framework Root node.";
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
