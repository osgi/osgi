package org.osgi.test.cases.dmt.tc4.tb1.intf;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;

public abstract class InteriorNode extends Node {
	public boolean isLeaf() {
		return false;
	}
	
	public int getNodeSize() {
		return 0;
	}
	
	public DmtData getNodeValue() {
		return null;
	}
	
	public void setNodeValue(DmtData value) throws DmtException {
		throw new DmtException(getNodePath(), DmtException.FEATURE_NOT_SUPPORTED, 
				"Operation is not allowed - setting the value of an Interior Node isn't allowed");
	}
	
	public String getNodeType() {
		return null;
	}
}
