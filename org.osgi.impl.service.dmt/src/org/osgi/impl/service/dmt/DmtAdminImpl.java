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
import org.osgi.service.event.EventAdmin;

public class DmtAdminImpl implements DmtAdmin {
	private DmtPluginDispatcher	dispatcher;
	private EventAdmin eventChannel;

	public DmtAdminImpl(DmtPluginDispatcher dispatcher,
			              EventAdmin eventChannel) {
		this.dispatcher = dispatcher;
        this.eventChannel = eventChannel;
	}

	public DmtSession getSession(String subtreeUri) throws DmtException {
		return getSession(null, subtreeUri, DmtSession.LOCK_TYPE_EXCLUSIVE);
	}

	public DmtSession getSession(String subtreeUri, int lockMode)
			throws DmtException {
		return getSession(null, subtreeUri, lockMode);
	}

	public DmtSession getSession(String principal, String subtreeUri, int lockMode)
			throws DmtException {
		return new DmtSessionImpl(principal, subtreeUri, lockMode, 
                                  eventChannel, dispatcher);
	}
}
