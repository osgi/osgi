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

public class DmtMetaNodeImpl implements DmtMetaNode {
	// TODO make variables private, add constructors, add setters
	boolean		deletable				= false;
	boolean		extendable				= false;
	boolean		retrievable				= true;
	boolean		replaceable				= true;
	boolean		executable				= false;
	boolean		leaf;											// there is
																	// no
																	// meaningful
																	// default
	boolean		permanent				= true;
	String		description				= null;
	int			maxOccurrence			= 1;
	boolean		zeroOccurrenceAllowed	= false;
	DmtData		defaultData				= null;
	boolean		hasMaximum				= false;
	boolean		hasMinimum				= false;
	int			max						= Integer.MAX_VALUE;
	int			min						= Integer.MIN_VALUE;
	DmtData[]	validValues				= null;
	int			format					= DmtDataType.NULL;
	String		regExp					= null;
	String[]	mimeTypes				= null;
	String		referredURI				= null;
	String[]	dependentURIs			= null;
	String[]	childURIs				= null;

	// Interior node with default properties
	public DmtMetaNodeImpl() {
		leaf = false;
		format = DmtDataType.NODE;
	}

	// Leaf node in ConfigurationPlugin
	public DmtMetaNodeImpl(String description, boolean allowInfinte,
			DmtData[] validValues, int format) {
		leaf = true;
		permanent = false;
		this.validValues = validValues;
		this.format = format;
		setCommon(description, allowInfinte);
	}

	// Interior node in ConfigurationPlugin
	public DmtMetaNodeImpl(String description, boolean allowInfinte,
			boolean isPermanent) {
		leaf = false;
		extendable = true;
		format = DmtDataType.NODE;
		permanent = isPermanent;
		setCommon(description, allowInfinte);
	}

	public boolean canDelete() {
		return deletable;
	}

	public boolean canAdd() {
		return extendable;
	}

	public boolean canGet() {
		return retrievable;
	}

	public boolean canReplace() {
		return replaceable;
	}

	public boolean canExecute() {
		return executable;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public boolean isPermanent() {
		return permanent;
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

	public boolean hasMax() {
		return hasMaximum;
	}

	public boolean hasMin() {
		return hasMinimum;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public DmtData[] getValidValues() {
		return validValues;
	}

	public int getFormat() {
		return format;
	}

	public String getRegExp() {
		return regExp;
	}

	public String[] getMimeTypes() {
		return mimeTypes;
	}

	public String getReferredURI() {
		return referredURI;
	}

	public String[] getDependentURIs() {
		return dependentURIs;
	}

	public String[] getChildURIs() {
		return childURIs;
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
