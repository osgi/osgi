package org.osgi.service.dmt;


/**
 * The Dmt Admin provides the DmtAlertSender service which can be used
 * by clients to send notifications to the server. The clients find the
 * service through the OSGi service registry. The typical client of this 
 * service is a Dmt Plugin which send an alert asynchronously after an
 * exec operation. 
 * It is the Dmt Admin's responsibility to route the alert to the appropriate 
 * protocol adaptor. Routing is possible based on the session or server 
 * information provided as a parameter in the sendAlert() methods.
 */
public interface DmtAlertSender {
    
    /**
     * Sends an alert, where routing is based on session information. The 
     * plugin receives a session reference in its open() method so it can 
     * provide this information to the AlertSender. The DmtAdmin uses the 
     * session information to find out the server ID where the notification
     * must be sent. If the session was initiated locally the alert must not
     * be sent. The session might be closed already at the time this method
     * is called, however the DmtAdmin must still be able to deduce the
     * server ID.
     * @param session The session what the plugin received in its open() method
     * @param code Alert code. Can be 0 if not needed.
     * @param items The data of the alert items carried in this alert
     * @throws DmtException Thrown when the alert can not be routed to the 
     * server.
     */
    void sendAlert(DmtSession session, int code, DmtAlertItem[] items) 
        throws DmtException;
    
    /**
     * Sends an alert, where routing is based on a server ID. The client might 
     * know the ID of a server which it wants to notify, in this case this 
     * form of the method must be used. 
     * @param serverid The ID of the remote server
     * @param code Alert code. Can be 0 if not needed.
     * @param items The data of the alert items carried in this alert
     * @throws DmtException
     */
    void sendAlert(String serverid, int code, DmtAlertItem[] items) 
        throws DmtException;
    
    /**
     * Sends an alert, when the client does not have any routing 
     * hints to provide. Even in this case the
     * routing might be possible if the DmtAdmin is connected to only one 
     * protocol adapter which is connected to only one remote server.
     * @param code Alert code. Can be 0 if not needed.
     * @param items The data of the alert items carried in this alert
     * @throws DmtException
     */
    void sendAlert(int code, DmtAlertItem[] items) 
        throws DmtException;
    
}

