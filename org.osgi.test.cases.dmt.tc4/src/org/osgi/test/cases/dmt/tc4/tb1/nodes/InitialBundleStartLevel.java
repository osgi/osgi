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

@SuppressWarnings("deprecation")
public class InitialBundleStartLevel extends LeafNode {
	private ServiceTracker<org.osgi.service.startlevel.StartLevel,org.osgi.service.startlevel.StartLevel> tracker;


	public InitialBundleStartLevel(BundleContext context) {
		tracker = new ServiceTracker<>(context,
				org.osgi.service.startlevel.StartLevel.class, null);
	}


	@Override
	public void open() {
		tracker.open();

		try {
			Tracker.waitForService(tracker, Node.SERVICE_TIMER);
		} catch (InterruptedException e) {
			// Interrupted... do nothing
		}
	}

	@Override
	public void close() {
		tracker.close();
	}

	@Override
	public String getNodeName() {
		return "InitialBundleStartLevel";
	}

	@Override
	public String getNodePath() {
		return "./OSGi/_Framework/StartLevel/InitialBundleStartLevel";
	}

	@Override
	public String getNodeType() {
		return "application/vnd.osgi.tr-069;type=int";
	}

	@Override
	public DmtData getNodeValue() {
		org.osgi.service.startlevel.StartLevel slSvc =
			tracker.getService();

		return new DmtData(slSvc.getInitialBundleStartLevel());
	}

	@Override
	public void setNodeValue(DmtData value) throws DmtException {
		org.osgi.service.startlevel.StartLevel slSvc =
			tracker.getService();

		slSvc.setInitialBundleStartLevel(value.getInt());
		nodeChanged();
	}

	@Override
	public Node[] getChildNodes() {
		return new Node[0];
	}

	@Override
	public String[] getChildNodeNames() {
		return new String[0];
	}

	@Override
	public MetaNode getMetaNode() {
		return new BaseMetaNode() {
			@Override
			public boolean can(int operation) {
				if ( operation == MetaNode.CMD_GET ) {
					return true;
				} else if ( operation == MetaNode.CMD_REPLACE ) {
					return true;
				}

				return false;
			}

			@Override
			public DmtData getDefault() {
				return null;
			}

			@Override
			public String getDescription() {
				return "A leaf node used to configure the initial bundle StartLevel. When this node value is replaced or the  Bundles  sub-tree  starts, StartLevel#setInitialStartLevel with the specified value must be called.";
			}

			@Override
			public int getFormat() {
				return DmtData.FORMAT_INTEGER;
			}

			@Override
			public double getMax() {
				return Integer.MAX_VALUE;
			}

			@Override
			public int getMaxOccurrence() {
				return 1;
			}

			@Override
			public String[] getMimeTypes() {
				return new String[] { "x-osgi/tr069.unsigned_int" };
			}

			@Override
			public double getMin() {
				return 0;
			}

			@Override
			public String[] getRawFormatNames() {
				return null;
			}

			@Override
			public int getScope() {
				return MetaNode.PERMANENT;
			}

			@Override
			public boolean isValidName(String name) {
				return true;
			}

			@Override
			public boolean isValidValue(DmtData value) {
				return true;
			}
		};
	}
}
