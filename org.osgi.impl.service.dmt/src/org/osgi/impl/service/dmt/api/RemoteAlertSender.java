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
package org.osgi.impl.service.dmt.api;

import org.osgi.service.dmt.DmtAlertItem;

/**
 * A remote protocol adapter provides the RemoteAlertSender service which can be
 * used by the DmtAdmin to send notifications to the server. This interface is
 * not standardised in OSGi. Here the adaptors have to be implemented as an OSGi
 * service which the Dmt Admin finds through the OSGi service registry.
 * Implementations of this interface have to be able to connect to and send an
 * alert to a remote management server in a protocol specific way.
 */
public interface RemoteAlertSender {
	/**
	 * Tells whether a protocol adapter can connect to and send alert to a
	 * remote server with a given ID. In case of more than one RemoteAlertServer
	 * services registered, the Dmt Admin uses this call to find out which
	 * service serves a given server.
	 * 
	 * @param serverId The ID of the server
	 * @return true if a subsequent sendAlert() call to the same server ID will
	 *         be likely to succeed
	 */
	boolean acceptServerId(String serverId);

	/**
     * Sends an alert to a given server. If the sender can supply session
     * related information it can do so, however it is protocol specific whether
     * it is needed or not, or whether it carries any meaningful information to
     * the adapter at all.
     * 
     * @param serverId The ID of the server where the alert should be sent
     * @param code Alert code. Can be 0 if not needed.
     * @param items The data of the alert items carried in this alert
     * @throws Exception Thrown when the alert can not be sent to the server. No
     *         separate Exception class is created, it will be wrapped to a
     *         DmtException in the Dmt Admin.
     */
    void sendAlert(String serverId, int code, DmtAlertItem[] items)
            throws Exception;
}
