package org.osgi.test.cases.dmt.tc4.tb1.intf;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

public abstract class BaseMetaNode implements MetaNode {
	private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = 
        "org.osgi.impl.service.dmt.interior-node-value-support"; 
	
	 public String[] getExtensionPropertyKeys() {
	        return new String[] { INTERIOR_NODE_VALUE_SUPPORT_PROPERTY };
	    }

	    public Object getExtensionProperty(String key) {
	        if(key.equals(INTERIOR_NODE_VALUE_SUPPORT_PROPERTY))
	            return Boolean.valueOf(false);
	        
	        throw new IllegalArgumentException("Only the '" + 
	                INTERIOR_NODE_VALUE_SUPPORT_PROPERTY + 
	                "' extension property is supported by this plugin.");
	    } 

	public String[] getValidNames() {
		return null;
	}

	public DmtData[] getValidValues() {
		return null;
	}

	public boolean isLeaf() {
		if ( getFormat() == DmtData.FORMAT_NODE ) {
			return false;
		}
		
		return true;
	}

	public boolean isZeroOccurrenceAllowed() {
		return false;
	}
}
