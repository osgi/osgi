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

import java.util.Date;

/**
 * This interface collects basic DMT operations. The application programmers
 * use these methods when they are interacting with a <code>DmtSession</code>
 * which inherits from this interface. The <code>DmtDataPlugin</code> and
 * the </code>DmtReadOnlyDataPlugin</code> interfaces also extend this
 * interface.
 * <p> If the admin or a data plugin does not support an optional DMT property
 * (like <code>timestamp</code>) then the corresponding getter method throws
 * <code>DmtException</code> with the error code
 * </code>FEATURE_NOT_SUPPORTED</code>. In case the Dmt Admin receives a
 * <code>null</code> from a plugin (or other value it can not interpret as a
 * proper result for a property getter method) it must also throw the
 * appropriate <code>DmtException</code>.
 */
public interface DmtReadOnly {

    /**
     * Closes a session and makes the changes made to the DMT persistent.
     * Persisting the changes works differently for exclusive and atomic lock.
     * For the former all changes that were accepted are persisted. For the
     * latter once an error is encountered, all successful changes are rolled
     * back.
     * <p> This method can fail even if all operations were successful. This
     * can happen due to some multi-node semantic constraints defined by a
     * specific implementation. For example, node A can be required to always
     * have children A.B, A.C and A.D. If this condition is broken when
     * <code>close()</code> is executed, the method will fail, and throw an
     * exception.
     * <p> After this method is called the session's state is
     * <code>DmtSession.STATE_CLOSED</code>.
     * @throws DmtException with the following possible error codes
     * <li> <code>COMMAND_FAILED</code> if an underlying plugin failed to close
     * <li> <code>DATA_STORE_FAILURE</code>
     * <li> <code>METADATA_MISMATCH</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    void close() throws DmtException;

    /**
     * Check whether the specified URI corresponds to a valid node in the DMT.
     * @param nodeUri the URI to check
     * @return true if the given node exists in the DMT
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     */
    boolean isNodeUri(String nodeUri);

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
     * timeout, or if the session is already closed.
     */
    boolean isLeafNode(String nodeUri) throws DmtException;

    /**
     * Get the data contained in a leaf node.
     * @param nodeUri The URI of the node to retrieve
     * @return The data of the leaf node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_FAILED</code>
     * <li> <code>COMMAND_NOT_ALLOWED</code> if the specified node is not a leaf
     * node
     * <li> <code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    DmtData getNodeValue(String nodeUri) throws DmtException;

    /**
     * Get the title of a node
     * @param nodeUri The URI of the node
     * @return The title of the node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_FAILED</code>
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>FEATURE_NOT_SUPPORTED</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    String getNodeTitle(String nodeUri) throws DmtException;

    /**
     * Get the type of a node. The type of leaf node is the MIME type of the
     * data it contains. The type of interior node is an URL pointing to a DDF
     * document.
     * @param nodeUri The URI of the node
     * @return The type of the node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_FAILED</code>
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>FEATURE_NOT_SUPPORTED</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    String getNodeType(String nodeUri) throws DmtException;

    /**
     * Get the version of a node. The version can not be set, it is calculated
     * automatically by the device. It is incremented each time the value of
     * a leaf node is changed. When a node is created the initial value is 0. 
     * The version property is undefined for interior nodes.
     * @param nodeUri The URI of the node
     * @return The version of the node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_FAILED</code>
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>FEATURE_NOT_SUPPORTED</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    int getNodeVersion(String nodeUri) throws DmtException;

    /**
     * Get the timestamp when the node was last modified
     * @param nodeUri The URI of the node
     * @return The timestamp of the last modification
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_FAILED</code>
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>FEATURE_NOT_SUPPORTED</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    Date getNodeTimestamp(String nodeUri) throws DmtException;

    /**
     * Get the size of the data in the node in bytes. Throws
     * <code>DmtException</code>
     * with the error code <code>COMMAND_NOT_ALLOWED</code> if issued on an
     * interior node.
     * @param nodeUri The URI of the node
     * @return The size of the node
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_FAILED</code>
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>FEATURE_NOT_SUPPORTED</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    int     getNodeSize(String nodeUri) throws DmtException;

    /**
     * Get the list of children names of a node. The returned array contains the
     * names - not the URIs - of the immediate children nodes of the given
     * node. The returned array must not contain <code>null</code> entries.
     * If a plugin
     * returns <code>null</code> as an array element, then the admin must remove
     * it from the array.
     * @param nodeUri The URI of the node
     * @return The list of children node names as a string array or
     * an empty string array if the node has no children.
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_FAILED</code>
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    String[] getChildNodeNames(String nodeUri) throws DmtException;

    /**
     * Get the meta data which describes a given node. Meta data can be only
     * inspected, it can not be changed. <p>
     * This method is inherited in the <code>DmtSession</code> and
     * also in the Data Plugin interfaces, but the semantics of the
     * method is slightly different in these two cases.  Meta data can
     * be supported at the engine level (i.e. in Dmt Admin), and its
     * support by plugins is an optional (and advanced) feature. It
     * can be used, for example, when a data plugin is implemented on
     * top of a data store or another API that has their own metadata,
     * such as a relational database, in order to avoid metadata
     * duplication and inconsistency. The meta data specific to the
     * plugin returned by the plugin's <code>getMetaNode()</code>
     * method is complemented by the engine level meta data. The
     * <code>DmtMetaNode</code> the client receives on the
     * <code>DmtSession.getMetaNode()</code> call is the combination
     * of the meta data returned by the data plugin plus the meta data
     * returned by the Dmt Admin. If there are differences in the meta
     * data elements known by the plugin and the Dmt Admin then the
     * plugin specific elements take precedence.
     * @param nodeUri the URI of the node
     * @return a DmtMetaNode which describes meta data information
     * @throws DmtException with the following possible error codes
     * <li> <code>NODE_NOT_FOUND</code>. Note that a node does not have to exist 
     * for having metadata associated to it. This error is thrown by the plugin
     * or the Dmt Admin if they can not provide metadata for the given node 
     * even if it existed.
     * <li> <code>URI_TOO_LONG</code>
     * <li> <code>INVALID_URI</code>
     * <li> <code>PERMISSION_DENIED</code>
     * <li> <code>OTHER_ERROR</code> if the URI is not within the current
     * session's subtree
     * <li> <code>COMMAND_FAILED</code>
     * <li> <code>COMMAND_NOT_ALLOWED</code>
     * <li> <code>FEATURE_NOT_SUPPORTED</code>
     * <li> <code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of 
     * timeout, or if the session is already closed.
     * @throws SecurityException if the caller does not have the necessary
     * permissions to execute the underlying management operation
     */
    DmtMetaNode getMetaNode(String nodeUri) throws DmtException;

}
