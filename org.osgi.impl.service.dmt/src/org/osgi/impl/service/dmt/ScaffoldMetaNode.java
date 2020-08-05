/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.dmt;

import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

public class ScaffoldMetaNode implements MetaNode {
    private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = 
        "org.osgi.impl.service.dmt.interior-node-value-support";

    @Override
	public boolean can(int operation) {
        return operation == CMD_GET;
    }
    
    @Override
	public boolean isLeaf() {
        return false;
    }

    @Override
	public int getScope() {
        return PERMANENT;        
    }

    @Override
	public String getDescription() {
        return null;
    }

    @Override
	public int getMaxOccurrence() {
        return 1;
    }

    @Override
	public boolean isZeroOccurrenceAllowed() {
        return true;
    }

    @Override
	public DmtData getDefault() {
        return null;
    }

    @Override
	public double getMax() {
        return Double.MAX_VALUE;
    }

    @Override
	public double getMin() {
        return Double.MIN_VALUE;
    }

    @Override
	public String[] getValidNames() {
        return null;
    }
    
    @Override
	public DmtData[] getValidValues() {
        return null;
    }

    @Override
	public int getFormat() {
        return DmtData.FORMAT_NODE;
    }

    @Override
	public String[] getRawFormatNames() {
        return null;
    }

    @Override
	public String[] getMimeTypes() {
        return new String[] { DmtConstants.DDF_SCAFFOLD };
    }
    
    @Override
	public boolean isValidName(String name) {
        return true;
    }
    
    @Override
	public boolean isValidValue(DmtData value) {
        return false;
    }

    @Override
	public String[] getExtensionPropertyKeys() {
        return new String[] { INTERIOR_NODE_VALUE_SUPPORT_PROPERTY };
    }

    @Override
	public Object getExtensionProperty(String key) {
        if(key.equals(INTERIOR_NODE_VALUE_SUPPORT_PROPERTY))
            return Boolean.valueOf(false);
        
        throw new IllegalArgumentException("Only the '" + 
                INTERIOR_NODE_VALUE_SUPPORT_PROPERTY + 
                "' extension property is supported by this plugin");
    }
}
