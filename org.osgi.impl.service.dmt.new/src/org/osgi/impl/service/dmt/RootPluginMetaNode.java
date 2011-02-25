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

import info.dmtree.DmtData;
import info.dmtree.MetaNode;

public class RootPluginMetaNode implements MetaNode {
    private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = 
        "org.osgi.impl.service.dmt.interior-node-value-support";

    public boolean can(int operation) {
        return operation == CMD_GET || operation == CMD_REPLACE;
    }
    
    public boolean isLeaf() {
        return false;
    }

    public int getScope() {
        return PERMANENT;        
    }

    public String getDescription() {
        return null;
    }

    public int getMaxOccurrence() {
        return 1;
    }

    public boolean isZeroOccurrenceAllowed() {
        return false;
    }

    public DmtData getDefault() {
        return null;
    }

    public double getMax() {
        return Double.MAX_VALUE;
    }

    public double getMin() {
        return Double.MIN_VALUE;
    }

    public String[] getValidNames() {
        return null;
    }
    
    public DmtData[] getValidValues() {
        return null;
    }

    public int getFormat() {
        return DmtData.FORMAT_NODE;
    }

    public String[] getRawFormatNames() {
        return null;
    }

    public String[] getMimeTypes() {
        return null;
    }
    
    public boolean isValidName(String name) {
        return true;
    }
    
    public boolean isValidValue(DmtData value) {
        return false;
    }

    public String[] getExtensionPropertyKeys() {
        return new String[] { INTERIOR_NODE_VALUE_SUPPORT_PROPERTY };
    }

    public Object getExtensionProperty(String key) {
        if(key.equals(INTERIOR_NODE_VALUE_SUPPORT_PROPERTY))
            return new Boolean(false);
        
        throw new IllegalArgumentException("Only the '" + 
                INTERIOR_NODE_VALUE_SUPPORT_PROPERTY + 
                "' extension property is supported by this plugin");
    }
}
