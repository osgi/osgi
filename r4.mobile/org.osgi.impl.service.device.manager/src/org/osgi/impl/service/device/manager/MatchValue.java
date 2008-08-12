/*
 * ============================================================================
 * Copyright (c) 2005, Gatespace Telematics
 * All rights reserved.
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

package org.osgi.impl.service.device.manager;

import org.osgi.framework.ServiceReference;

public class MatchValue {
    MatchValue next;

    Integer key;

    String drvid;

    String pid;

    ServiceReference dev;

    int match;
}
