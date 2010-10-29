package org.osgi.test.cases.dmt.tc4.tb1.intf;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.MetaNode;

import java.util.Date;

public abstract class Node {
	private int version;
	private Date timestamp;
	private String title;
	
	protected static final long SERVICE_TIMER = 2000;
	
	
	public Node() {
		version = 0;
		timestamp = new Date();
		title = "";
	}
	
	
	public int getVersion() {
		return version;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void nodeChanged() {
		version++;
		timestamp = new Date();
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String data) {
		title = data;
	}

	
	public abstract void open();
	
	public abstract void close();
	
	public abstract boolean isLeaf();
	
	public abstract int getNodeSize();
	
	public abstract String getNodeName();

	public abstract String getNodePath();
	
	public abstract String getNodeType();
	
	public abstract DmtData getNodeValue();
	
	public abstract void setNodeValue(DmtData value) throws DmtException;
	
	public abstract Node[] getChildNodes();
	
	public abstract String[] getChildNodeNames();
	
	public abstract MetaNode getMetaNode();
}
