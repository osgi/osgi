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
package org.osgi.impl.service.policy;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtMetaNode;


abstract class HashNode {
	abstract DmtMetaNode getMetaNode(String[] subUri);

	public boolean isNodeUri(String[] subNodeUri) {
		if (subNodeUri.length==0) return true;
		if ((subNodeUri.length==1)&&(subNodeUri[0].equals("Type"))) return true;
		return false;
	}

	public abstract DmtData getNodeValue(String[] subNodeUri);
}