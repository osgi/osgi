/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Abr 14, 2005  Luiz Felipe Guimaraes
 * 1			 Implement Meg TCK
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc3.tbc.MetaNode.MetaData;

import info.dmtree.DmtData;
import info.dmtree.MetaNode;

public class TestPluginMetaDataMetaNode implements MetaNode {

	private boolean	 canAdd;
	private boolean	 canDelete;
	private boolean	 canGet;
	private boolean	 canReplace;
	private boolean	 canExecute;
	private DmtData  defaultValue;
	private String   description;
    private int      format;
	private boolean	 isLeaf;	
    private boolean  isZeroOccurrenceAllowed;
    private int      max;
    private int      maxOccurrence;
    private int      min;
	private String[] mimeTypes;
    private int      scope;
	private DmtData[] validValues;
	private String[] validNames;

	

    public TestPluginMetaDataMetaNode() {
        this.isLeaf        = false;
        this.format        = DmtData.FORMAT_NODE;
        this.canAdd        = true;
        this.canDelete     = true;
        this.canGet        = true;
        this.canReplace    = true;
        this.canExecute    = true;
        this.defaultValue  = null;
        this.description   = "";
        this.isZeroOccurrenceAllowed = false;
        this.max 		   = Integer.MAX_VALUE;
        this.maxOccurrence = Integer.MAX_VALUE;
        this.min 		   = Integer.MIN_VALUE; 
        this.mimeTypes 	   = null;
        this.scope = MetaNode.DYNAMIC;
        this.validValues   = null;
        this.validNames    = null;
        
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
		return max;
	}

	public double getMin() {
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
        return null;
    }
    
	public String getPattern() {
		return null;
	}

	public String[] getMimeTypes() {
		return mimeTypes;
	}
	/**
	 * @param canAdd The canAdd to set.
	 */
	public void setCanAdd(boolean canAdd) {
		this.canAdd = canAdd;
	}
	/**
	 * @param canDelete The canDelete to set.
	 */
	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
	/**
	 * @param canGet The canGet to set.
	 */
	public void setCanGet(boolean canGet) {
		this.canGet = canGet;
	}
	/**
	 * @param canReplace The canReplace to set.
	 */
	public void setCanReplace(boolean canReplace) {
		this.canReplace = canReplace;
	}
    /**
     * @param canExecute The canExecute to set.
     */
    public void setCanExecute(boolean canExecute) {
        this.canExecute = canExecute;
    }
	/**
	 * @param defaultValue The defaultValue to set.
	 */
	public void setDefaultValue(DmtData defaultValue) {
		this.defaultValue = defaultValue;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @param format The format to set.
	 */
	public void setFormat(int format) {
		this.format = format;
	}
	/**
	 * @param isLeaf The isLeaf to set.
	 */
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	/**
	 * @param isZeroOccurrenceAllowed The isZeroOccurrenceAllowed to set.
	 */
	public void setZeroOccurrenceAllowed(boolean isZeroOccurrenceAllowed) {
		this.isZeroOccurrenceAllowed = isZeroOccurrenceAllowed;
	}
	/**
	 * @param max The max to set.
	 */
	public void setMax(int max) {
		this.max = max;
	}
	/**
	 * @param maxOccurrence The maxOccurrence to set.
	 */
	public void setMaxOccurrence(int maxOccurrence) {
		this.maxOccurrence = maxOccurrence;
	}
	/**
	 * @param mimeTypes The mimeTypes to set.
	 */
	public void setMimeTypes(String[] mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	/**
	 * @param min The min to set.
	 */
	public void setMin(int min) {
		this.min = min;
	}
	/**
	 * @param scope The scope to set.
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}
	/**
	 * @param validNames The validNames to set.
	 */
	public void setValidNames(String[] validNames) {
		this.validNames = validNames;
	}
	/**
	 * @param validValues The validValues to set.
	 */
	public void setValidValues(DmtData[] validValues) {
		this.validValues = validValues;
	}


	public boolean isValidValue(DmtData value) {
	    if (validValues==null)
	        return true;
	    
		for (int i=0;i<validValues.length;i++) {
			if (value.equals(validValues[i])) {
				return true;
			}
		}
		return false;
	}

	public boolean isValidName(String name) {
	    if (validNames==null)
	        return true;
	    
		for (int i=0;i<validNames.length;i++) {
			if (name.equals(validNames[i])) {
				return true;
			}
		}
		return false;
	}


	public String[] getExtensionPropertyKeys() {
		return null;
	}


	public Object getExtensionProperty(String key) {
		return null;
	}


	public String[] getRawFormatNames() {
		return null;
	}

}
