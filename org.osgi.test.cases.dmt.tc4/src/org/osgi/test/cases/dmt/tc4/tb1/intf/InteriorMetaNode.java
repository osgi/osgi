package org.osgi.test.cases.dmt.tc4.tb1.intf;

import info.dmtree.DmtData;

public abstract class InteriorMetaNode extends BaseMetaNode {
	public DmtData getDefault() {
		return null;
	}

	public int getFormat() {
		return DmtData.FORMAT_NODE;
	}

	public double getMax() {
		return 0;
	}

	public String[] getMimeTypes() {
		return null;
	}

	public double getMin() {
		return 0;
	}

	public String[] getRawFormatNames() {
		return null;
	}

	public boolean isValidName(String name) {
		return true;
	}

	public boolean isValidValue(DmtData value) {
		return true;
	}
}
