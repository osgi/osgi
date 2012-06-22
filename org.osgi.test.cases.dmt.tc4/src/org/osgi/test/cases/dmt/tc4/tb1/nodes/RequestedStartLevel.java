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

public class RequestedStartLevel extends LeafNode {
	private ServiceTracker tracker;
	private int lastRequestedValue;


	public RequestedStartLevel(BundleContext context) {
		lastRequestedValue = 1;

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
		return "RequestedStartLevel";
	}

	public String getNodePath() {
		return "./OSGi/_Framework/StartLevel/RequestedStartLevel";
	}

	public String getNodeType() {
		return "application/vnd.osgi.tr-069;type=int";
	}

	public DmtData getNodeValue() {
		return new DmtData(lastRequestedValue);
	}

	public void setNodeValue(DmtData value) throws DmtException {
		org.osgi.service.startlevel.StartLevel slSvc =
			(org.osgi.service.startlevel.StartLevel)tracker.getService();

		slSvc.setStartLevel(value.getInt());
		lastRequestedValue = value.getInt();
		nodeChanged();
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
				return null;
			}

			public String getDescription() {
				return "A leaf node used to configure the Framework's StartLevel. When this node value is replaced or the  Bundles  sub-tree  starts, StartLevel#setStartLevel with the specified value must  be called.  This value must  be kept persistently.";
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
