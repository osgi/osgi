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

import org.osgi.service.dmt.*;

public class LogPluginMetanode implements DmtMetaNode {
    static final boolean CAN_EXECUTE    = true;
    static final boolean IS_PERMANENT   = true;
    static final boolean ALLOW_INFINITE = true; 

	static final String  LEAF_MIME_TYPE = "text/plain";
    
	// private fields
	private boolean	 canDelete;
	private boolean	 canAdd;
	private boolean	 canGet;
	private boolean	 canReplace;
	private boolean	 canExecute;
	private boolean	 isLeaf;
    
	private boolean  isPermanent;
    private String   description;
    private int      maxOccurrence;
    private boolean  isZeroOccurrenceAllowed;
    private int      format;
    private String[] validNames;
    private String[] mimeTypes;

    // Meta-node constructor for interior nodes
    protected LogPluginMetanode(boolean isPermanent, boolean canExecute, 
                                boolean allowInfinite, String description) {
        
        this.canDelete     = !isPermanent;
        this.canAdd        = true;
        this.canGet        = true;
        this.canReplace    = true;
        this.canExecute    = canExecute;
        this.isLeaf        = false;
        
        this.isPermanent   = isPermanent;
        this.description   = description;
        this.maxOccurrence = allowInfinite ? Integer.MAX_VALUE : 1;
        this.isZeroOccurrenceAllowed = allowInfinite;
        this.format        = DmtData.FORMAT_NODE;
        this.validNames    = null;
        this.mimeTypes     = null;
    }
    
    // Meta-node constructor for leaf nodes
	protected LogPluginMetanode(int format, String description) {
        this.canDelete     = true;
        this.canAdd        = false;
        this.canGet        = true;
        this.canReplace    = true;
        this.canExecute    = false;
        this.isLeaf        = true;
        
        this.isPermanent   = false;
        this.description   = description;
        this.maxOccurrence = 1;
        this.isZeroOccurrenceAllowed = false;
        this.format        = format;
        this.validNames    = new String[] { LogPlugin.FILTER, LogPlugin.EXCLUDE,
                                            LogPlugin.MAXR, LogPlugin.MAXS };
        this.mimeTypes     = new String[] { LEAF_MIME_TYPE };
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
		return isPermanent ? PERMANENT : DYNAMIC;
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
        // ENHANCE provide defaults for log search request parameters
		return null;
	}

	public int getMax() {
		return Integer.MAX_VALUE;
	}

	public int getMin() {
		return Integer.MIN_VALUE;
	}

    public String[] getValidNames() {
        return validNames;
    }
    
	public DmtData[] getValidValues() {
        // ENHANCE add valid value list for "exclude" node
		return null;
	}

	public int getFormat() {
		return format;
	}

    public String getNamePattern() {
        return null;
    }
    
	public String getPattern() {
		return null;
	}

	public String[] getMimeTypes() {
		return mimeTypes;
	}
}
