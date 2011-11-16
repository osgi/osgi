package org.osgi.test.cases.dmt.tc4.tb1.nodes;

import org.osgi.framework.BundleContext;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.MetaNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.BaseMetaNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.LeafNode;
import org.osgi.test.cases.dmt.tc4.tb1.intf.Node;
import org.osgi.test.support.tracker.Tracker;
import org.osgi.util.tracker.ServiceTracker;

public class ActiveStartLevel extends LeafNode {
	private ServiceTracker tracker;


	public ActiveStartLevel(BundleContext context) {
		tracker = new ServiceTracker(context,
				org.osgi.service.startlevel.StartLevel.class.getName(), null);
	}


	public void open() {
		tracker.open();

		try {
			Tracker.waitForService(tracker, Node.SERVICE_TIMER);
		} catch (InterruptedException e) {
			// Interrupted... do nothing
		}
	}

	public void close() {
		tracker.close();
	}

	public String getNodeName() {
		return "ActiveStartLevel";
	}

	public String getNodePath() {
		return "./OSGi/_Framework/StartLevel/ActiveStartLevel";
	}

	public String getNodeType() {
		return "application/vnd.osgi.tr-069;type=int";
	}

	public DmtData getNodeValue() {
		org.osgi.service.startlevel.StartLevel slSvc =
			(org.osgi.service.startlevel.StartLevel)tracker.getService();

		return new DmtData(slSvc.getStartLevel());
	}

	public void setNodeValue(DmtData value) throws DmtException {
		throw new DmtException(getNodePath(), DmtException.COMMAND_FAILED,
				"Operation is not allowed - read-only entity");
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
				}

				return false;
			}

			public DmtData getDefault() {
				return null;
			}

			public String getDescription() {
				return "A leaf node that contains the Framework's current StartLevel. This node is read-only to get the Framework's StartLevel.";
			}

			public int getFormat() {
				return DmtData.FORMAT_INTEGER;
			}

			public double getMax() {
				return Integer.MAX_VALUE;
			}

			public int getMaxOccurrence() {
				return 1;
			}

			public String[] getMimeTypes() {
				return new String[] { "x-osgi/tr069.unsigned_int" };
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
