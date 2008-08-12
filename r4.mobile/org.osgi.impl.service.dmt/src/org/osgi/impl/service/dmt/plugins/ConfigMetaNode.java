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

import java.util.Arrays;
import info.dmtree.DmtData;
import info.dmtree.MetaNode;

class ConfigMetaNode implements MetaNode {
    static final boolean CAN_ADD        = true;
    static final boolean CAN_DELETE     = true;
    static final boolean CAN_REPLACE    = true;
    static final boolean ALLOW_ZERO     = true;
    static final boolean ALLOW_INFINITE = true;
    static final boolean IS_INDEX       = true;
    
    static final String  LEAF_MIME_TYPE = "text/plain";
    static final String  CONFIGURATION_MO_TYPE = 
        "org.osgi/1.0/ConfigurationManagementObject";

    
    private static final String INTERIOR_NODE_VALUE_SUPPORT_PROPERTY = 
        "org.osgi.impl.service.dmt.interior-node-value-support";

    private boolean		canAdd                  = false;
	private boolean		canDelete               = false;
	private boolean		canGet                  = true;
	private boolean		canReplace              = true;
	private boolean		canExecute              = false;
	
	private boolean		leaf; // there is no meaningful default
	private int         scope                   = PERMANENT;
	private String		description				= null;
	private boolean		zeroOccurrenceAllowed	= false;
	private int			maxOccurrence			= 1;
	private DmtData		defaultData				= null;
	private DmtData[]	validValues				= null;
	private int			formats					= DmtData.FORMAT_NULL;
	private String[]	mimeTypes				= null;
	
	// If true, the Configuration/<pid>/Keys/<key>/Values/<index> node is
	// checked to be a number in isValidName.
	private boolean     isIndex                 = false;
    
	// Leaf node in ConfigurationPlugin
	// First element in validValues (if any) is the default value.
	ConfigMetaNode(String description, boolean canDelete, 
			boolean canReplace, boolean allowZero, boolean allowInfinite,
			boolean isIndex, int formats, DmtData[] validValues) {
		leaf = true;
		
        this.canAdd = true;
		this.canDelete = canDelete;
		this.canReplace = canReplace;
		this.scope = DYNAMIC;
		this.mimeTypes = new String[] { LEAF_MIME_TYPE };
		this.zeroOccurrenceAllowed = allowZero;
		this.maxOccurrence = allowInfinite ? Integer.MAX_VALUE /* inf */ : 1;
		this.formats = formats;
		this.validValues = validValues;
		this.defaultData = validValues == null ? null : validValues[0];
		
		this.isIndex = isIndex;
	}

	// Interior node in ConfigurationPlugin
	ConfigMetaNode(String description, int scope, boolean canAdd, 
			boolean canDelete, boolean allowZero, boolean allowInfinite) {
		leaf = false;

		this.canAdd        = canAdd;
		this.canDelete     = canDelete;
		this.scope         = scope;
		this.description   = description;
		this.zeroOccurrenceAllowed = allowZero;
		this.maxOccurrence = allowInfinite ? Integer.MAX_VALUE /* inf */ : 1;
		this.formats       = DmtData.FORMAT_NODE;
	}

    
    
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
		return leaf;
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
		return zeroOccurrenceAllowed;
	}

	public DmtData getDefault() {
		return defaultData;
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
		return validValues;
	}

	public int getFormat() {
		return formats;
	}

	public String[] getRawFormatNames() {
        return null;
    }

    public String[] getMimeTypes() {
		return mimeTypes;
	}
    
    public boolean isValidName(String name) {
		if(isIndex) {
			try {
				int i = Integer.parseInt(name);
                if(i < 0)
                    return false;
			} catch(NumberFormatException e) {
				return false;
			}
		}
        return true;
    }
    
    public boolean isValidValue(DmtData value) {
        if((formats & value.getFormat()) == 0)
            return false;
        
        return validValues == null ? true : 
            Arrays.asList(validValues).contains(value);
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
