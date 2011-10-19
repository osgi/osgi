package org.osgi.test.cases.dmt.tc4.tb1.nodes;

import org.osgi.service.dmt.MetaNode;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.dmt.tc4.tb1.intf.InteriorMetaNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.InteriorNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.LeafNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;

public class FrameworkLifecycle extends InteriorNode {
	private LeafNode update;
	private LeafNode restart;
	private LeafNode shutdown;
	
	
	public FrameworkLifecycle(BundleContext context) {
		update = new Update(context);
		restart = new Restart(context);
		shutdown = new Shutdown(context);
	}

	
	public void open() {
		update.open();
		restart.open();
		shutdown.open();
	}
	
	public void close() {
		update.close();
		restart.close();
		shutdown.close();
	}
	
	public String getNodeName() {
		return "FrameworkLifecycle";
	}
	
	public String getNodePath() {
		return "./OSGi/_Framework/FrameworkLifecycle";
	}
	
	public Node[] getChildNodes() {
		return new Node[] {update, restart, shutdown};
	}
	
	public String[] getChildNodeNames() {
		return new String[] {update.getNodeName(), restart.getNodeName(), shutdown.getNodeName()};
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
				return "Interior node that contains the values for Lifecycle control of the OSGi Framework.";
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
