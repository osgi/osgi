/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.dmt;

// TODO add REMOTE_ERROR type exceptions to javadoc
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
     * @param items The data of the alert items carried in this alert.
     * Can be <code>null</code> if not needed.
     * @throws DmtException with the following possible error codes
     * <li> <code>ALERT_NOT_ROUTED</code> when the alert can not be routed to
     * the server
     * <li> <code>FEATURE_NOT_SUPPORTED</code> in case of locally initiated
     * sessions
     * <li> <code>REMOTE_ERROR</code> in case of communication problems between
     * the device and the server
     */
    void sendAlert(DmtSession session, int code, DmtAlertItem[] items)
        throws DmtException;

    /**
     * Sends an alert, where routing is based on a server ID. The client might
     * know the ID of a server which it wants to notify, in this case this
     * form of the method must be used. If OMA DM is used as a management
     * protocol the server ID corresponds to a DMT node value in
     * <code>./SyncML/DMAcc/x/ServerId</code>.
     * @param serverid The ID of the remote server
     * @param code Alert code. Can be 0 if not needed.
     * @param items The data of the alert items carried in this alert.
     * Can be <code>null</code> if not needed.
     * @throws DmtException with the following possible error codes
     * <li> <code>ALERT_NOT_ROUTED</code> when the alert can not be routed to
     * the server
     * <li> <code>FEATURE_NOT_SUPPORTED</code> in case of locally initiated
     * sessions
     * <li> <code>REMOTE_ERROR</code> in case of communication problems between
     * the device and the server
     */
    void sendAlert(String serverid, int code, DmtAlertItem[] items)
        throws DmtException;

    /**
     * Sends an alert, when the client does not have any routing
     * hints to provide. Even in this case the
     * routing might be possible if the DmtAdmin is connected to only one
     * protocol adapter which is connected to only one remote server.
     * @param code Alert code. Can be 0 if not needed.
     * @param items The data of the alert items carried in this alert.
     * Can be <code>null</code> if not needed.
     * @throws DmtException with the following possible error codes
     * <li> <code>ALERT_NOT_ROUTED</code> when the alert can not be routed to
     * the server
     * <li> <code>FEATURE_NOT_SUPPORTED</code> in case of locally initiated
     * sessions
     * <li> <code>REMOTE_ERROR</code> in case of communication problems between
     * the device and the server
     */
    void sendAlert(int code, DmtAlertItem[] items)
        throws DmtException;

}

