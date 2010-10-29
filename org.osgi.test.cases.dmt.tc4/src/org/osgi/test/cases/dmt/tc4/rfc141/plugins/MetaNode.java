package org.osgi.test.cases.dmt.tc4.rfc141.plugins;

import info.dmtree.DmtData;

public class MetaNode implements info.dmtree.MetaNode {

	private boolean leaf;
	private int scope;
	private int format;
	private int[] operations = {};
	
	
	public MetaNode( boolean leaf, int scope, int format, int[] operations ) {
		this.leaf = leaf;
		this.scope = scope;
		this.format = format;
		this.operations = operations;
	}
	
	
	public boolean can(int operation) {
		for (int i = 0; i < operations.length; i++)
			if ( operation == operations [i])
				return true;
		return false;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public int getScope() {
		return scope;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMaxOccurrence() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isZeroOccurrenceAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	public DmtData getDefault() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getMimeTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getMax() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getMin() {
		// TODO Auto-generated method stub
		return 0;
	}

	public DmtData[] getValidValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getFormat() {
		return format;
	}

	public String[] getRawFormatNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isValidValue(DmtData value) {
		return true;
	}

	public String[] getValidNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isValidName(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	public String[] getExtensionPropertyKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getExtensionProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
