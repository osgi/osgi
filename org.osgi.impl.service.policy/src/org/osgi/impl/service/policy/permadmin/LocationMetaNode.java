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
package org.osgi.impl.service.policy.permadmin;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtDataType;

/**
 *
 * Meta node for Location.
 * 
 * @version $Revision$
 */
public final class LocationMetaNode implements DmtMetaNode {
	public boolean canDelete() { return false; }
	public boolean canAdd() { return false; }
	public boolean canGet() { return true; }
	public boolean canReplace() { return false; }
	public boolean canExecute() { return false;	}
	public boolean isLeaf() { return true;	}
	public boolean isPermanent() { return false; }
	public String getDescription() { return "location of the bundle"; }
	public int getMaxOccurrence() {	return 1; }
	public boolean isZeroOccurrenceAllowed() { return false; }
	public DmtData getDefault() { return null; }
	public boolean hasMax() { return false; }
	public boolean hasMin() { return false; }
	public int getMax() { return 0; }
	public int getMin() { return 0;	}
	public DmtData[] getValidValues() { return null; }
	public int getFormat() { return DmtDataType.STRING; }
	public String getRegExp() {	return null; }
	public String[] getMimeTypes() { return null; }
	public String getReferredURI() { return null; }
	public String[] getDependentURIs() { return null; }
	public String[] getChildURIs() { return null; }
}
