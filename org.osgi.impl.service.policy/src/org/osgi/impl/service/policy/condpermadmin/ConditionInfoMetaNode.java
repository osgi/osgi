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
package org.osgi.impl.service.policy.condpermadmin;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtMetaNode;

/**
 *
 * Meta node for PermissionInfo.
 * 
 * @version $Revision$
 */
public final class ConditionInfoMetaNode implements DmtMetaNode {
	public boolean can(int operation) { return operation==CMD_GET; }
	public boolean isLeaf() { return true;	}
	public int getScope() { return DYNAMIC; }
	public String getDescription() { return "Condition Info array"; }
	public int getMaxOccurrence() {	return 1; }
	public boolean isZeroOccurrenceAllowed() { return false; }
	public DmtData getDefault() { return null; }
	public int getMax() { return Integer.MAX_VALUE; }
	public int getMin() { return Integer.MIN_VALUE;	}
	public DmtData[] getValidValues() { return null; }
	public int getFormat() { return DmtData.FORMAT_STRING; }
	public String getPattern() {	return null; }
	public String[] getMimeTypes() { return null; }
	public String getReferredURI() { return null; }
	public String[] getDependentURIs() { return null; }
	public String[] getChildURIs() { return null; }
	public String[] getValidNames() { return null; }
	public String getNamePattern() { return null; }
}
