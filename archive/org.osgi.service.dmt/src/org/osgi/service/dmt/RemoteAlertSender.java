/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.dmt;


/**
 * The RemoteAlertSender service is provided by Protocol Adapters, and can be
 * used by the Dmt Admin to send notifications to remote servers.
 * Implementations of this interface have to be able to connect and send alerts
 * to a remote management server in a protocol specific way.
 * <p>
 * The properties of the service registration must specify a list of
 * destinations (servers) where the service is capable of sending alerts. This
 * must be done by providing a <code>String</code> array of server identifiers
 * in the <code>servers</code> registration property.
 * <p>
 * The <code>servers</code> registration property is used when the
 * {@link DmtAdmin#sendAlert DmtAdmin.sendAlert} method is called, to find the
 * proper <code>RemoteAlertSender</code> for the given destination server. If
 * the caller does not specify a destination server, the alert is only sent if
 * there is only one registered <code>RemoteAlertSender</code>.
 */
public interface RemoteAlertSender {
    /**
     * Sends an alert to a given server. In case the alert is sent in response
     * to a previous {@link DmtSession#execute(String, String, String) execute}
     * command, a correlation identifier can be specified to provide the
     * association between the exec and the alert.
     * <p>
     * The <code>serverId</code> parameter specifies which server the alert
     * should be sent to. This parameter can be <code>null</code> if the
     * client does not know the ID of the destination. The alert should still be
     * delivered if possible; for example if the alert sender is only connected
     * to one destination.
     * <p>
     * Any exception thrown on this method will be propagated to the original
     * sender of the event, wrapped in a <code>DmtException</code> with the
     * code <code>REMOTE_ERROR</code>.
     * 
     * @param serverId the ID of the server where the alert should be sent, can
     *        be <code>null</code>
     * @param code the alert code, can be 0 if not needed
     * @param correlator the correlation identifier of an associated EXEC
     *        command, or <code>null</code> if there is no associated EXEC
     * @param items the data of the alert items carried in this alert, can be
     *        empty or <code>null</code> if no alert items are needed
     * @throws Exception if the alert can not be sent to the server
     */
    void sendAlert(String serverId, int code, String correlator, 
            DmtAlertItem[] items) throws Exception;
}
