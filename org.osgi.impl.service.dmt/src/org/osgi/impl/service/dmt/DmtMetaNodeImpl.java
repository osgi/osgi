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

// TODO fill in the validNames attribute properly
// TODO consider using the nameRegExp attribute
public class DmtMetaNodeImpl implements DmtMetaNode {
	// TODO make variables private, add constructors, add setters
	boolean		deletable				= false;
	boolean		extendable				= false;
	boolean		retrievable				= true;
	boolean		replaceable				= true;
	boolean		executable				= false;
	boolean		leaf; // there is no meaningful default
	int         scope                   = PERMANENT;
	String		description				= null;
	int			maxOccurrence			= 1;
	boolean		zeroOccurrenceAllowed	= false;
	DmtData		defaultData				= null;
	int			max						= Integer.MAX_VALUE;
	int			min						= Integer.MIN_VALUE;
	String[]	validNames				= null;
	DmtData[]	validValues				= null;
	int			format					= DmtData.FORMAT_NULL;
	String		namePattern             = null;
	String		valuePattern			= null;
	String[]	mimeTypes				= null;
    
	// Interior node with default properties
	public DmtMetaNodeImpl() {
		leaf = false;
		format = DmtData.FORMAT_NODE;
	}

	// Leaf node in ConfigurationPlugin
	public DmtMetaNodeImpl(String description, boolean allowInfinte,
			DmtData[] validValues, int format) {
		leaf = true;
		scope = DYNAMIC;
		this.validValues = validValues;
		this.format = format;
		setCommon(description, allowInfinte);
	}

	// Interior node in ConfigurationPlugin
	public DmtMetaNodeImpl(String description, boolean allowInfinte,
			boolean isPermanent) {
		leaf = false;
		extendable = true;
		format = DmtData.FORMAT_NODE;
		scope = isPermanent ? PERMANENT : DYNAMIC;
		setCommon(description, allowInfinte);
	}

    
    
    public boolean can(int operation) {
        switch(operation) {
        case CMD_DELETE:  return deletable;
        case CMD_ADD:     return extendable;
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
		return max;
	}

	public int getMin() {
		return min;
	}

    public String[] getValidNames() {
        return validNames;
    }
	public DmtData[] getValidValues() {
		return validValues;
	}

	public int getFormat() {
		return format;
	}

    public String getNamePattern() {
        return namePattern;
    }
    
	public String getPattern() {
		return valuePattern;
	}

	public String[] getMimeTypes() {
		return mimeTypes;
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
