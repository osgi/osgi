package org.osgi.impl.service.dmt.api;

import org.osgi.service.dmt.DmtAlertItem;

/**
 * A remote protocol adapter provides the RemoteAlertSender service which can 
 * be used by the DmtAdmin to send notifications to the server. This interface 
 * is not standardised in OSGi.  Here the adaptors have to be implemented as an
 * OSGi service which the Dmt Admin finds through the OSGi service registry. 
 * Implementations of this interface have to be able to connect to and send an
 * alert to a remote management server in a protocol specific way.
 */
public interface RemoteAlertSender {
	
    /**
     * Tells whether a protocol adapter can connect to and send alert to a 
     * remote server with a given ID. In case of more than one RemoteAlertServer
     * services registered, the Dmt Admin uses this call to find out which 
     * service serves a given server.
     * @param serverId The ID of the server 
     * @return true if a subsequent sendAlert() call to the same server ID will
     * be likely to succeed
     */
    boolean acceptServerId(String serverId);
	
    /**
     * Sends an alert to a given server. If the sender can supply session 
     * related information it can do so, however it is protocol specific whether
     * it is needed or not, or whether it carries any meaningful information to 
     * the adapter at all.
     * @param serverId The ID of the server where the alert should be sent
     * @param sessionId The session identification. Might be null.
     * @param code Alert code. Can be 0 if not needed.
     * @param items The data of the alert items carried in this alert
     * @throws Exception Thrown when the alert can not be sent to the 
     * server. No separate Exception class is created, it will be wrapped to a
     * DmtException in the Dmt Admin.
     */
    void sendAlert(String serverId, String sessionId, 
                   int code, DmtAlertItem[] items)
        throws Exception;

}
