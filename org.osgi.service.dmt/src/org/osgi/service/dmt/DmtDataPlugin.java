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
 * An implementation of this interface takes the responsibility over a
 * modifiable subtree of the DMT. If the subtree is non-modifiable then the
 * <code>DmtReadOnlyDataPlugin</code> interface should be used instead.
 * <p>
 * The plugin might support transactionality, in this case it has to implemenent
 * commit and rollback functionality.
 * <p>
 * In an OSGi environment such implementations should be registered at the OSGi
 * service registry specifying the list of root node URIs in a
 * <code>String</code> array in the <code>dataRootURIs</code> registration
 * parameter.
 */
public interface DmtDataPlugin extends Dmt {
    /**
     * The registration parameter that specifies the subtrees handled by this
     * plugin. The parameter with this name must contain the array of root URIs
     * of all subtrees in which this plugin handles the data manipulation
     * operations.
     */
    String DATA_ROOT_URIS = "dataRootURIs";

    // TODO can we specify OTHER_ERROR more precisely?
    /**
     * This method is called to signal the start of a transaction when the first
     * reference is made within a <code>DmtSession</code> to a node which is
     * handled by this plugin. Session information is given as it is needed for
     * sending alerts back from the plugin.
     * 
     * @param subtreeUri the subtree which is accessed in the current session,
     *        must not be <code>null</code>
     * @param lockMode one of the lock type constants specified in
     *        <code>DmtSession</code>
     * @param session the session from which this plugin instance is accessed,
     *        must not be <code>null</code>
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>OTHER_ERROR</code>
     * @throws SecurityException if some underlying operation failed because of
     *         lack of permissions
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
    

    /**
     * Notifies the plugin that the given node has changed outside the scope of
     * the plugin, therefore the version and timestamp properties must be
     * updated (if supported).  This method is needed because the ACL property
     * of a node is managed by the Dmt Admin instead of the plugin.  The Dmt 
     * Admin must call this method whenever the ACL property of a node changes.
     * 
     * @param nodeUri the URI of the node that has changed
     */
    void nodeChanged(String nodeUri);
}
