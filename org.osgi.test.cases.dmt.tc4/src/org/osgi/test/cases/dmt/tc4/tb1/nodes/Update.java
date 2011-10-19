package org.osgi.test.cases.dmt.tc4.tb1.nodes;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.test.cases.dmt.tc4.tb1.intf.BaseMetaNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.LeafNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;

public class Update extends LeafNode {
	private BundleContext ctx;
	
	
	public Update(BundleContext context) {
		ctx = context;
	}
	
	
	public void open() {
		// Nothing to do
	}
	
	public void close() {
		// Nothing to do
	}
	
	public String getNodeName() {
		return "Update";
	}
	
	public String getNodeType() {
		return "application/vnd.osgi.tr-069;type=boolean";
	}
	
	public String getNodePath() {
		return "./OSGi/_Framework/FrameworkLifecycle/Update";
	}
	
	public DmtData getNodeValue() {
		// Always false when read
		return new DmtData(false);
	}
	
	public void setNodeValue(DmtData value) throws DmtException {
		if (value.getBoolean() == true) {
			Bundle b = ctx.getBundle(0);
			try {
				b.update();  // NOTE: Same Bundle call as Restart
				nodeChanged();
			} catch (BundleException e) {
				throw new DmtException(getNodePath(), DmtException.COMMAND_FAILED, e.getMessage());
			}
		}
	}

	public Node[] getChildNodes() {
		return new Node[0];
	}

	public String[] getChildNodeNames() {
		return new String[0];
	}
	
	public MetaNode getMetaNode() {
		return new BaseMetaNode() {
			public boolean can(int operation) {
				if ( operation == MetaNode.CMD_GET ) {
					return true;
				} else if ( operation == MetaNode.CMD_REPLACE ) {
					return true;
				}
				
				return false;
			}

			public DmtData getDefault() {
				return new DmtData(false);
			}

			public String getDescription() {
				return "A leaf node used to update the OSGi Framework. This node is writable to set the update command. If this node value is replaced with 'TRUE', the Framework sub-tree must  update the OSGi Framework.";
			}

			public int getFormat() {
				return DmtData.FORMAT_BOOLEAN;
			}

			public double getMax() {
				return 1;
			}

			public int getMaxOccurrence() {
				return 1;
			}

			public String[] getMimeTypes() {
				return new String[] { "x-osgi/tr069.boolean" };
			}

			public double getMin() {
				return 0;
			}

			public String[] getRawFormatNames() {
				return null;
			}

			public int getScope() {
				return MetaNode.PERMANENT;
			}

			public boolean isValidName(String name) {
				return true;
			}

			public boolean isValidValue(DmtData value) {
				return true;
			}
		};
	}
}
