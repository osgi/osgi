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
package org.osgi.impl.service.dmt.plugins;

import info.dmtree.*;

import java.util.List;

class LogMetaNode implements MetaNode {
    static final boolean MODIFIABLE = true; 
    static final boolean SEARCH_PARAMETER = true; 
    static final boolean ALLOW_INFINITE = true; 

	static final String  LEAF_MIME_TYPE = "text/plain";
	static final String  LOG_MO_TYPE = "org.osgi/1.0/LogManagementObject";
    
	// private fields
    private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = 
        "org.osgi.impl.service.dmt.interior-node-value-support";

	private boolean	  canDelete;
	private boolean	  canAdd;
	private boolean	  canGet;
	private boolean	  canReplace;
	private boolean	  canExecute;
	private boolean	  isLeaf;
    
	private int       scope;
    private String    description;
    private int       maxOccurrence;
    private boolean   isZeroOccurrenceAllowed;
    private int       format;
    private String[]  mimeTypes;
    private DmtData   defaultValue;

    // used for checking the components of a comma-separated list
    private List validComponents;

    // Meta-node constructor for interior nodes
    protected LogMetaNode(int scope, boolean modifiable, 
            boolean allowInfinite, String description) {
        
        this.canDelete       = modifiable;
        this.canAdd          = modifiable;
        this.canGet          = true;
        this.canReplace      = true;
        this.canExecute      = false;
        this.isLeaf          = false;
        
        this.scope           = scope;
        this.description     = description;
        this.maxOccurrence   = allowInfinite ? Integer.MAX_VALUE : 1;
        this.isZeroOccurrenceAllowed = allowInfinite;
        this.format          = DmtData.FORMAT_NODE;
        this.validComponents = null;
        this.mimeTypes       = null;
        this.defaultValue    = null;
    }
    
    // Meta-node constructor for leaf nodes
	protected LogMetaNode(boolean isSearchParameter, int format, 
            DmtData defaultValue, List validComponents, 
            String description) {
        this.canDelete       = false;
        this.canAdd          = false;
        this.canGet          = true;
        this.canReplace      = isSearchParameter;
        this.canExecute      = false;
        this.isLeaf          = true;
        
        this.scope           = MetaNode.AUTOMATIC;
        this.description     = description;
        this.maxOccurrence   = 1;
        this.isZeroOccurrenceAllowed = !isSearchParameter;
        this.format          = format;
        this.validComponents = validComponents;
        this.mimeTypes       = new String[] { LEAF_MIME_TYPE };
        this.defaultValue    = defaultValue;
	}

	/* ---------------------------------------------------------------------- */

    public boolean can(int operation) {
        switch(operation) {
        case CMD_DELETE:  return canDelete;
        case CMD_ADD:     return canAdd;
        case CMD_GET:     return canGet;
        case CMD_REPLACE: return canReplace;
        case CMD_EXECUTE: return canExecute;
        }
        return false;
    }       

    public boolean isLeaf() {
		return isLeaf;
	}

	public int getScope() {
		return scope;
	}

	public String getDescription() {
		return description;
	}

	public int getMaxOccurrence() {
		return maxOccurrence;
	}

	public boolean isZeroOccurrenceAllowed() {
		return isZeroOccurrenceAllowed;
	}

	public DmtData getDefault() {
		return defaultValue;
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
		return format;
	}

	public String[] getRawFormatNames() {
        return null;
    }

    public String[] getMimeTypes() {
		return mimeTypes;
	}
    
    public boolean isValidName(String name) {
        return true;
    }
    
    public boolean isValidValue(DmtData value) {
        if((format & value.getFormat()) == 0)
            return false;
        
        if(validComponents != null) { // format assumed to be FORMAT_STRING
            try {
                String excludes = value.getString();
                String[] components = Splitter.split(excludes, ',', 0);
                for (int i = 0; i < components.length; i++)
                    if(!validComponents.contains(components[i].trim()))
                        return false;
            } catch(DmtIllegalStateException e) {
                // not checking components if format is not string
            }
        }
        
        return true;
    }
    
    public String[] getExtensionPropertyKeys() {
        return new String[] { INTERIOR_NODE_VALUE_SUPPORT_PROPERTY };
    }

    public Object getExtensionProperty(String key) {
        if(key.equals(INTERIOR_NODE_VALUE_SUPPORT_PROPERTY))
            return new Boolean(false);
        
        throw new IllegalArgumentException("Only the '" + 
                INTERIOR_NODE_VALUE_SUPPORT_PROPERTY + 
                "' extension property is supported by this plugin.");
    }
}
