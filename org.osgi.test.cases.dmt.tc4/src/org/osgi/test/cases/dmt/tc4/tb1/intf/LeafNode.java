package org.osgi.test.cases.dmt.tc4.tb1.intf;

public abstract class LeafNode extends Node {
	@Override
	public boolean isLeaf() {
		return true;
	}
	
	@Override
	public int getNodeSize() {
		return getNodeValue().getSize();
	}
}
