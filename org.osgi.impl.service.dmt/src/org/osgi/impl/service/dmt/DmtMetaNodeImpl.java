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

import java.util.Arrays;
import org.osgi.service.dmt.*;

// TODO update the meta-data according to the new tree definitions
public class DmtMetaNodeImpl implements DmtMetaNode {
	private boolean		deletable               = false;
	private boolean		addable                 = false;
	private boolean		retrievable             = true;
	private boolean		replaceable             = true;
	private boolean		executable              = false;
	private boolean		leaf; // there is no meaningful default
	private int         scope                   = PERMANENT;
	private String		description				= null;
	private int			maxOccurrence			= 1;
	private boolean		zeroOccurrenceAllowed	= false;
	private DmtData		defaultData				= null;
	private DmtData[]	validValues				= null;
	private int			format					= DmtData.FORMAT_NULL;
	private String[]	mimeTypes				= null;
    
	// Interior node with default properties
	public DmtMetaNodeImpl() {
		leaf = false;
		format = DmtData.FORMAT_NODE;
	}

	// Leaf node in ConfigurationPlugin
	public DmtMetaNodeImpl(String description, boolean allowInfinte,
			DmtData[] validValues, int format) {
		leaf = true;
        addable = true;
		scope = DYNAMIC;
		this.validValues = validValues;
		this.format = format;
		setCommon(description, allowInfinte);
	}

	// Interior node in ConfigurationPlugin
	public DmtMetaNodeImpl(String description, boolean allowInfinte,
			boolean isPermanent) {
		leaf = false;
		addable = !isPermanent;
		format = DmtData.FORMAT_NODE;
		scope = isPermanent ? PERMANENT : DYNAMIC;
		setCommon(description, allowInfinte);
	}

    
    
    public boolean can(int operation) {
        switch(operation) {
        case CMD_DELETE:  return deletable;
        case CMD_ADD:     return addable;
        case CMD_GET:     return retrievable;
        case CMD_REPLACE: return replaceable;
        case CMD_EXECUTE: return executable;
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

	public int getMax() {
		return Integer.MAX_VALUE;
	}

	public int getMin() {
		return Integer.MIN_VALUE;
	}

    public String[] getValidNames() {
        return null;
    }
	public DmtData[] getValidValues() {
		return validValues;
	}

	public int getFormat() {
		return format;
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
        
        return validValues == null ? true : 
            Arrays.asList(validValues).contains(value);
    }

	private void setCommon(String description, boolean allowInfinte) {
		this.description = description;
		if (allowInfinte) {
			maxOccurrence = Integer.MAX_VALUE; // infinite
			zeroOccurrenceAllowed = true;
			deletable = true;
		}
	}
}
