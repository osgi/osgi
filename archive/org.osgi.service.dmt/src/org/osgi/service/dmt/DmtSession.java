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
 * DmtSession provides concurrent access to the DMT. All DMT manipulation
 * commands for management applications are available on the DmtSession
 * interface. The session is associated with a root node which limits the
 * subtree in which the operations can be executed within this session. Most of
 * the operations take a node URI as parameter, it can be either an absolute
 * URI (starting with &quot;./&quot;) or a URI relative to the root node of the 
 * session.
 * If the URI specified does not correspond to a legitimate node in the tree
 * an exception is thrown. The only exception is the isNodeUri() method which
 * returns false in case of an invalid URI.
 * <p> Each method of the DmtSession can throw InvalidStateException in case the
 * session is invalidated because of timeout. 
 */
public interface DmtSession extends Dmt {
    /**
     * Sessions created with <code>LOCK_TYPE_SHARED</code> lock allows
     * read-only access to the tree, but can be shared between multiple readers.
     */
    static final int LOCK_TYPE_SHARED    = 0;
    /**
     * <code>LOCK_TYPE_EXCLUSIVE</code> lock guarantees full access to the
     * tree, but can not be shared with any other locks.
     */
    static final int LOCK_TYPE_EXCLUSIVE = 1;
    /**
     * <code>LOCK_TYPE_ATOMIC</code> is an exclusive lock with transactional
     * functionality. Commands of an atomic session will either fail or
     * succeed together, if a single command fails then the whole session will
     * be rolled back.
     */
    static final int LOCK_TYPE_ATOMIC    = 2;

    /**
     * Gives the type of lock the session currently has.
     * @return One of the <code>LOCK_TYPE_...</code> constants.
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed or rolled back.
     */
    int getLockType();

    /**
     * Gives the name of the principal on whose behalf the session was
     * created.  Local sessions do not have an associated principal,
     * in this case <code>null</code> is returned.
     * @return the identifier of the remote server that initiated the
     * session, or <code>null</code> for local sessions
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed or rolled back.
     */
    String getPrincipal();

    /**
     * The unique identifier of the session. The ID is generated automatically,
     * and it is guaranteed to be unique on a machine.
     * @return the session identification number
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed or rolled back.
     */
    int getSessionId();

    /**
     * Executes a node. This corresponds to the EXEC operation in OMA DM. The
     * semantics of an EXEC operation depend on the definition of the managed
     * object on which it is issued.
     * @param nodeUri the node on which the execute operation is issued
     * @param data the parameters to the execute operation. The format of the
     * data string is described by the managed object definition.
     * Can be <code>null</code>.
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_NOT_ALLOWED</code> if the node is non-executable
     * <li> <code>COMMAND_FAILED</code> if no DmtExecPlugin is associated with
     * the node
     * <li> <code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed or rolled back.
     */
    void execute(String nodeUri, String data) throws DmtException;

    /**
     * Tells whether a node is a leaf or an interior node of the DMT.
     * @param nodeUri the URI of the node
     * @return true if the given node is a leaf node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed or rolled back.
     */
    boolean isLeafNode(String nodeUri) throws DmtException;

    /**
     * Gives the Access Control List associated with a given node.
     * @param nodeUri the URI of the node
     * @return the Access Control List belonging to the node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed or rolled back.
     */
    DmtAcl getNodeAcl(String nodeUri) throws DmtException;

    /**
     * Set the Access Control List associated with a given node.
     * @param nodeUri the URI of the node
     * @param acl the Access Control List to be set on the node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed or rolled back.
     */
    void setNodeAcl(String nodeUri, DmtAcl acl) throws DmtException;

    /**
     * Get the root URI associated with this session. Gives "<code>.</code>"
     * if the session was created without specifying a root, which means that
     * the target of this session is the whole DMT.
     * @return the root URI
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed or rolled back.
     */
    String getRootUri();
}
