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
 * An implementation of this interface takes the responsibility over a
 * modifiable subtree of the DMT. If the subtree is non modifiable then
 * the <code>DmtReadOnlyDataPlugin</code> interface should be used instead.
 * <p>The plugin might support transactionality, in this case it has to
 * implemenent commit and rollback functionality.
 * <p>In an OSGi environment such implementations should be registered
 * at the OSGi service registry specifying the list of root node URIs
 * in a String array in the <code>dataRootURIs</code> registration
 * parameter.
 */
public interface DmtDataPlugin extends Dmt {

    /**
     * This method is called to signal the start of a transaction
     * when the first reference is made within a DmtSession to a
     * node which is handled by this plugin.
     * @param subtreeUri The subtree which is locked in the current session
     * @param lockMode One of the lock type constants specified in
     * <code>DmtSession</code>
     * @param session The session from which this plugin instance is accessed
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>OTHER_ERROR</code> [TODO can we specify this more precisely?]
     * @throws SecurityException if some underlying operation failed because of
     * lack of permissions
     */
    void open(String subtreeUri, int lockMode, DmtSession session)
        throws DmtException;

    /**
     * Tells whether the plugin can handle atomic transactions. If a session is
     * created using <code>DmtSession.LOCK_TYPE_ATOMIC</code> locking and the
     * plugin supports it then it is possible to roll back operations in the
     * session.
     * @return <code>true</code> if the plugin can handle atomic transactions
     */
    boolean supportsAtomic();
}
