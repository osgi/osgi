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
     * @throws SecurityException if the caller does not have the required
     * DmtPrincipalPermission
     */
    DmtSession getSession(String principal, String subtreeUri, int lockMode)
        throws DmtException;
}
