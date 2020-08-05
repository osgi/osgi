package org.osgi.test.cases.dmt.tc4.tb1.intf;

import org.osgi.service.dmt.DmtData;

public abstract class InteriorMetaNode extends BaseMetaNode {
	@Override
	public DmtData getDefault() {
		return null;
	}

	@Override
	public int getFormat() {
		return DmtData.FORMAT_NODE;
	}

	@Override
	public double getMax() {
		return 0;
	}

	@Override
	public String[] getMimeTypes() {
		return null;
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
	public boolean isValidName(String name) {
		return true;
	}

	@Override
	public boolean isValidValue(DmtData value) {
		return true;
	}
}
