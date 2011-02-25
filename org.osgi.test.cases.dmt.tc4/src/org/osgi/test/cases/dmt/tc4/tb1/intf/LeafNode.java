package org.osgi.test.cases.dmt.tc4.tb1.intf;

public abstract class LeafNode extends Node {
	public boolean isLeaf() {
		return true;
	}
	
	public int getNodeSize() {
		return getNodeValue().getSize();
	}
}
