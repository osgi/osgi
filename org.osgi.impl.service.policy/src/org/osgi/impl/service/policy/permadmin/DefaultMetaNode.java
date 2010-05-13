/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
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
package org.osgi.impl.service.policy.permadmin;

import info.dmtree.DmtData;
import info.dmtree.MetaNode;


/**
 *
 * Meta node for the /Default node
 * 
 * @version $Id$
 */
public final class DefaultMetaNode implements MetaNode {
	public static final String DEFAULT = "Default";
	public static final String[] DEFAULT_ARRAY = new String[] { DEFAULT };
	
	private static final DmtData defaultData = new DmtData("");
	public boolean can(int operation) { 
		return 
			(operation==CMD_DELETE)||
			(operation==CMD_GET)||
			(operation==CMD_ADD)||
			(operation==CMD_REPLACE);
	}
	public boolean isLeaf() { return true;	}
	public int getScope() { return DYNAMIC; }
	public String getDescription() { return "default permissions"; }
	public int getMaxOccurrence() {	return 1; }
	public boolean isZeroOccurrenceAllowed() { return true; }
	public DmtData getDefault() { return defaultData; }
	public double getMax() { return Double.MAX_VALUE; }
	public double getMin() { return Double.MIN_VALUE;	}
	public DmtData[] getValidValues() { return null; }
	public int getFormat() { return DmtData.FORMAT_STRING; }
	public String[] getMimeTypes() { return null; }
	public String[] getValidNames() { return null; }
	public boolean isValidValue(DmtData value) {
		// TODO
		return true;
	}
	public boolean isValidName(String name) { return DEFAULT.equals(name); }
	public String[] getRawFormatNames() { return null; }
	public String[] getExtensionPropertyKeys() { return null; }
	public Object getExtensionProperty(String key) { throw new IllegalArgumentException("extension property key "+key+" not supported"); }
}
