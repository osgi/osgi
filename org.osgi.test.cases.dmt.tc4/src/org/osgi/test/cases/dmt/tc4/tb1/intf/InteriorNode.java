package org.osgi.test.cases.dmt.tc4.tb1.intf;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;

public abstract class InteriorNode extends Node {
	@Override
	public boolean isLeaf() {
		return false;
	}
	
	@Override
	public int getNodeSize() {
		return 0;
	}
	
	@Override
	public DmtData getNodeValue() {
		return null;
	}
	
	@Override
	public void setNodeValue(DmtData value) throws DmtException {
		throw new DmtException(getNodePath(), DmtException.FEATURE_NOT_SUPPORTED, 
				"Operation is not allowed - setting the value of an Interior Node isn't allowed");
	}
	
	@Override
	public String getNodeType() {
		return null;
	}
}
