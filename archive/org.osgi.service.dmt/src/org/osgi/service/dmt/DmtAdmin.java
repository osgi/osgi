/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.dmt;

/**
 * The DmtAdmin interface is used to create DmtSession objects. The
 * implementation of DmtAdmin should register itself in the OSGi service
 * registry as a service. DmtAdmin is the entry point for applications to
 * use the Dmt API. The <code>getSession</code> methods are used to open a
 * session on a specified subtree of the DMT. A typical way of usage:
 * <blockquote><pre>
 *     serviceRef = context.getServiceReference(DmtAdmin.class.getName());
 *     DmtAdmin factory = (DmtAdmin) context.getService(serviceRef);
 *     DmtSession session = factory.getSession(&quot;./OSGi/cfg&quot;);
 *     session.createInteriorNode(&quot;./OSGi/cfg/mycfg&quot;);
 * </pre></blockquote>
 */
public interface DmtAdmin {
    /**
     * Opens a DmtSession for local usage on a given subtree of the DMT with
     * non transactional write lock.
     * This call is equivalent to the following:
     * <code>getSession(null, subtreeUri, DmtSession.LOCK_TYPE_EXCLUSIVE)</code>
     * @param subtreeUri The subtree on which DMT manipulations can be
     * performed within the returned session. If you want to use the whole DMT
     * then use &quot;.&quot; as subtree URI.
     * @return a DmtSession object on which DMT manipulations can be performed
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>TIMEOUT</code>
     */
    DmtSession getSession(String subtreeUri) throws DmtException;

    /**
     * Opens a DmtSession for local usage on a specific DMT subtree with a
     * given locking mode. This call is equivalent to the following:
     * <code>getSession(null, subtreeUri, lockMode)</code>
     * @param subtreeUri The subtree on which DMT manipulations can be
     * performed within the returned session. If you want to use the whole DMT
     * then use &quot;.&quot; as subtree URI.
     * @param lockMode One of the locking modes specified in DmtSession
     * @return a DmtSession object on which DMT manipulations can be performed
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>OTHER_ERROR</code> if the lockMode is unknown
     * <li> <code>TIMEOUT</code>
     */
    DmtSession getSession(String subtreeUri, int lockMode)
        throws DmtException;

    /**
     * Opens a DmtSession on a specific DMT subtree using a specific locking
     * mode on behalf of a remote principal. If local management applications
     * are using this method then they should provide <code>null</code> as the 
     * first parameter. Alternatively they can use other forms of this method 
     * without providing a principal string. This method is guarded by
     * DmtPrincipalPermission.
     * @param principal the identifier of the remote server on whose
     * behalf the data manipulation is performed, or <code>null</code>
     * for local sessions
     * @param subtreeUri The subtree on which DMT manipulations can be
     * performed within the returned session. If you want to use the whole DMT
     * then use &quot;.&quot; as subtree URI.
     * @param lockMode One of the locking modes specified in DmtSession
     * @return a DmtSession object on which DMT manipulations can be performed
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>OTHER_ERROR</code> if the lockMode is unknown
     * <li> <code>TIMEOUT</code>
     * @throws SecurityException if the caller does not have the required
     * DmtPrincipalPermission
     */
    DmtSession getSession(String principal, String subtreeUri, int lockMode)
        throws DmtException;
    
    /**
     * Sends an alert to a named principal. If OMA DM is used as a management
     * protocol the principal name is server ID that corresponds to a DMT node 
     * value in <code>./SyncML/DMAcc/x/ServerId</code>. It is the DmtAdmin's
     * responsibility to route the alert to the given principal.
     * @param principal The principal name which is the recepient of this alert.
     * In remotely initiated session it corresponds to a remote server ID, which
     * can be obtained using the session's <code>getPrincipal</code> call. 
     * <p>It can be
     * <code>null</code> when the client does not know the principal name. Even 
     * in this case the
     * routing might be possible if the DmtAdmin is connected to only one
     * protocol adapter which is connected to only one remote server.
     * @param code Alert code. Can be 0 if not needed.
     * @param items The data of the alert items carried in this alert.
     * Can be <code>null</code> if not needed.
     * @throws DmtException with the following possible error codes
     * <li> <code>ALERT_NOT_ROUTED</code> when the alert can not be routed to
     * the server
     * <li> <code>REMOTE_ERROR</code> in case of communication problems between
     * the device and the server
     */
    void sendAlert(String principal, int code, DmtAlertItem[] items)
        throws DmtException;
}
