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
 * A collection of DMT manipulation methods. The application programmers
 * use these methods when they are interacting with a <code>DmtSession</code>
 * which inherits from this interface. Data plugins also implement this
 * interface.
 */
public interface Dmt extends DmtReadOnly {

    /**
     * Rolls back a series of DMT operations issued in the current atomic
     * session since the last transaction boundary. Transaction boundaries are
     * the {@link DmtReadOnly#open open} call that starts the session, and all
     * subsequent {@link #commit commit} and {@link #rollback rollback} calls.
     * 
     * @throws DmtException with the following possible error codes
     *         <li><code>ROLLBACK_FAILED</code> in case the rollback did not
     *         succeed
     *         <li><code>FEATURE_NOT_SUPPORTED</code> in case the session was
     *         not created using the <code>LOCK_TYPE_ATOMIC</code> lock type.
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    void rollback() throws DmtException;
    
    /**
     * Commits a series of DMT operations issued in the current atomic session
     * since the last transaction boundary. Transaction boundaries are the
     * {@link DmtReadOnly#open open} call that starts the session, and all
     * subsequent {@link #commit commit} and {@link #rollback rollback} calls.
     * <p>
     * This method can fail even if all operations were successful. This can
     * happen due to some multi-node semantic constraints defined by a specific
     * implementation. For example, node A can be required to always have
     * children A.B, A.C and A.D. If this condition is broken when
     * <code>commit()</code> is executed, the method will fail, and throw an
     * exception.
     * 
     * @throws DmtException with the following possible error codes
     *         <li><code>TRANSACTION_ERROR</code> if error occured because of
     *         lack of two phase commit in the underlying plugins. An example:
     *         plugin A has committed successfully but plugin B failed, so the
     *         whole session must fail, but A can not undo the commit
     *         <li><code>FEATURE_NOT_SUPPORTED</code> in case the session was
     *         not created using the <code>LOCK_TYPE_ATOMIC</code> lock type.
     *         <li><code>COMMAND_FAILED</code> if an underlying plugin failed
     *         to commit
     *         <li><code>DATA_STORE_FAILURE</code>
     *         <li><code>METADATA_MISMATCH</code>
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    void commit() throws DmtException;

    /**
     * Set the title property of a node.
     * @param nodeUri The URI of the node
     * @param title The title text of the node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>FEATURE_NOT_SUPPORTED</code>
     * <li> <code>COMMAND_FAILED</code> if the title string is too long or
     * contains not allowed characters
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>METADATA_MISMATCH</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>TRANSACTION_ERROR</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void setNodeTitle(String nodeUri, String title) throws DmtException;

    /**
     * Set the value of a leaf node.
     * @param nodeUri The URI of the node
     * @param data The data to be set. The format of the node is contained in
     * the DmtData. Nodes of <code>null</code> format can be set by using
     * <code>DmtData.NULL_VALUE</code> as second argument.
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>COMMAND_FAILED</code> if the data is null
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_NOT_ALLOWED</code> if the specified node is not a leaf
     * node
     * <li> <code>METADATA_MISMATCH</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>FORMAT_NOT_SUPPORTED</code>
     * <li> <code>TRANSACTION_ERROR</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void setNodeValue(String nodeUri, DmtData data) throws DmtException;

    /**
     * Set the value of a leaf node to it's default as defined by the node's
     * meta data. The method throws exception if there is no default defined.
     * @param nodeUri The URI of the node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>COMMAND_FAILED</code> 
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_NOT_ALLOWED</code> if the specified node is not a leaf
     * node
     * <li> <code>METADATA_MISMATCH</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>TRANSACTION_ERROR</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void setDefaultNodeValue(String nodeUri) throws DmtException;
    
    /**
     * Set the type of a node. The type of leaf node is the MIME type of the
     * data it contains. The type of interior node is an URL pointing to a DDF
     * document.
     * @param nodeUri The URI of the node
     * @param type The type of the node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>FEATURE_NOT_SUPPORTED</code>
     * <li> <code>COMMAND_FAILED</code> if the type string is null or invalid
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>METADATA_MISMATCH</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>FORMAT_NOT_SUPPORTED</code>
     * <li> <code>TRANSACTION_ERROR</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void setNodeType(String nodeUri, String type) throws DmtException;

    /**
     * Delete the given node. Deleting interior nodes is recursive, the whole
     * subtree under the given node is deleted.
     * @param nodeUri The URI of the node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_NOT_ALLOWED</code> if the node is permanent or
     * non-deletable
     * <li> <code>METADATA_MISMATCH</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>TRANSACTION_ERROR</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void deleteNode(String nodeUri) throws DmtException;

    /**
     * Create an interior node
     * @param nodeUri The URI of the node
     * @throws DmtException with the following possible error codes
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>NODE_ALREADY_EXISTS</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>METADATA_MISMATCH</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>TRANSACTION_ERROR</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void createInteriorNode(String nodeUri) throws DmtException;

    /**
     * Create an interior node with a given type. The type of interior node is
     * an URL pointing to a DDF document.
     * @param nodeUri The URI of the node
     * @param type The type URL of the interior node
     * @throws DmtException with the following possible error codes
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>NODE_ALREADY_EXISTS</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_FAILED</code> if the type string is invalid
     * <li> <code>METADATA_MISMATCH</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>TRANSACTION_ERROR</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void createInteriorNode(String nodeUri, String type) throws DmtException;

    /**
     * Create a leaf node with default value. If a node does
     * not have a default value defined by it's meta data, this method will
     * throw a DmtException with error code <code>METADATA_MISMATCH</code>.
     * The MIME type of the default node should also be specified by the meta
     * data.
     * @param nodeUri The URI of the node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_ALREADY_EXISTS</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>METADATA_MISMATCH</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>TRANSACTION_ERROR</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void createLeafNode(String nodeUri) throws DmtException;

    /**
     * Create a leaf node with a given value. The node's MIME type is not 
     * explicitely specified, it will be derived from the meta data associated 
     * with this node. The meta data defining the possible type (if any) 
     * should allow only one MIME type, otherwise
     * this method will fail with <code>METADATA_MISMATCH</code>.
     * Nodes of <code>null</code> format can be created by using
     * <code>DmtData.NULL_VALUE</code> as second argument.  
     * @param nodeUri The URI of the node
     * @param value The value to be given to the new node, can not be null
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_ALREADY_EXISTS</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_FAILED</code> if the data is null
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>METADATA_MISMATCH</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>FORMAT_NOT_SUPPORTED</code>
     * <li> <code>TRANSACTION_ERROR</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void createLeafNode(String nodeUri, DmtData value) throws DmtException;

    /**
     * Create a leaf node with a given value and MIME type. 
     * @param nodeUri The URI of the node
     * @param value The value to be given to the new node, can not be null
     * @param mimeType The MIME type to be given to the new node. It can be 
     * null. 
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_ALREADY_EXISTS</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>COMMAND_FAILED</code> if the data is null
     * <li> <code>METADATA_MISMATCH</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>FORMAT_NOT_SUPPORTED</code>
     * <li> <code>TRANSACTION_ERROR</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void createLeafNode(String nodeUri, DmtData value, String mimeType)
                    throws DmtException;

    /**
     * Create a deep copy of a node. All properties and values will be copied.
     * The command works for single nodes and recursively for whole subtrees.
     * @param nodeUri The node or root of a subtree to be copied
     * @param newNodeUri The URI of the new node or root of a subtree
     * @param recursive <code>false</code> if only a single node is copied,
     * <code>true</code> if the whole subtree is copied.
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_ALREADY_EXISTS</code> if newNodeUri already exists
     * <li> <code>NODE_NOT_FOUND</code> if nodeUri does not exist
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if either URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_NOT_ALLOWED</code> if any of the implied Get or Add
     * commands are not allowed, or if nodeUri is an ancestor of newNodeUri
     * <li> <code>METADATA_MISMATCH</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>FORMAT_NOT_SUPPORTED</code>
     * <li> <code>TRANSACTION_ERROR</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void copy(String nodeUri, String newNodeUri, boolean recursive)
        throws DmtException;

    /**
     * Rename a node. The value and the other properties of the node does not
     * change.
     * @param nodeUri The URI of the node to rename
     * @param newName The new name property of the node. This is not the new
     * URI of the node, the new URI is constructed from the old URI and the
     * new name.
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_FAILED</code> if the newName string is invalid
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>METADATA_MISMATCH</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>TRANSACTION_ERROR</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void renameNode(String nodeUri, String newName) throws DmtException;
}
