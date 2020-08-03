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

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

public class SharedMountPointMetaNode implements MetaNode {
    private int scope;
    
    public SharedMountPointMetaNode( int scope ) {
    	this.scope = scope;
    }
    
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
        return scope;        
    }

    @Override
	public String getDescription() {
        return null;
    }

    @Override
	public int getMaxOccurrence() {
        return Integer.MAX_VALUE;
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
        return null;
    }
    
    @Override
	public boolean isValidName(String name) {
    	int index = -1;
    	try {
        	index = Integer.parseInt(name);
		} catch (NumberFormatException e) {
			return false;
		}
        return index >= 1 && index < Integer.MAX_VALUE;
    }
    
    @Override
	public boolean isValidValue(DmtData value) {
        return false;
    }

    @Override
	public String[] getExtensionPropertyKeys() {
    	return null;
    }

    @Override
	public Object getExtensionProperty(String key) {
    	return null;
    }
}
