package org.osgi.test.cases.dmt.tc4.rfc141.plugins;

import org.osgi.service.dmt.DmtData;

public class MetaNode implements org.osgi.service.dmt.MetaNode {

	// according to bug-fix 1948, this extension-property must not be neccessary anymore
//    private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = 
//        "org.osgi.impl.service.dmt.interior-node-value-support";

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

	// this stuff seems necessary (for any obscure reasons) to make the OSGi-ref-impl-DMTAdmin avoid NullPointerExceptions
    public String[] getExtensionPropertyKeys() {
    	return null;
//        return new String[] { INTERIOR_NODE_VALUE_SUPPORT_PROPERTY };
    }

    public Object getExtensionProperty(String key) {
//        if(key.equals(INTERIOR_NODE_VALUE_SUPPORT_PROPERTY))
//            return Boolean.valueOf(true);
//        
//        throw new IllegalArgumentException("Only the '" + 
//                INTERIOR_NODE_VALUE_SUPPORT_PROPERTY + 
//                "' extension property is supported by this plugin.");
    	return null;
    }

}
