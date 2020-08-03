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
public class ActiveStartLevel extends LeafNode {
	private ServiceTracker<org.osgi.service.startlevel.StartLevel,org.osgi.service.startlevel.StartLevel> tracker;


    public ActiveStartLevel(BundleContext context) {
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
		return "ActiveStartLevel";
	}

	@Override
	public String getNodePath() {
		return "./OSGi/_Framework/StartLevel/ActiveStartLevel";
	}

	@Override
	public String getNodeType() {
		return "application/vnd.osgi.tr-069;type=int";
	}

	@Override
	public DmtData getNodeValue() {
		org.osgi.service.startlevel.StartLevel slSvc =
			tracker.getService();

		return new DmtData(slSvc.getStartLevel());
	}

	@Override
	public void setNodeValue(DmtData value) throws DmtException {
		throw new DmtException(getNodePath(), DmtException.COMMAND_FAILED,
				"Operation is not allowed - read-only entity");
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
				}

				return false;
			}

			@Override
			public DmtData getDefault() {
				return null;
			}

			@Override
			public String getDescription() {
				return "A leaf node that contains the Framework's current StartLevel. This node is read-only to get the Framework's StartLevel.";
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
