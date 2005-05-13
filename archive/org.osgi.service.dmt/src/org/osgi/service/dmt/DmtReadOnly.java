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

import java.util.Date;

/**
 * This interface collects basic DMT operations. The application programmers use
 * these methods when they are interacting with a <code>DmtSession</code>
 * which inherits from this interface. The <code>DmtDataPlugin</code> and the
 * <code>DmtReadOnlyDataPlugin</code> interfaces also extend this interface.
 * <p>
 * If the admin or a data plugin does not support an optional DMT property (like
 * <code>timestamp</code>) then the corresponding getter method throws
 * <code>DmtException</code> with the error code
 * <code>FEATURE_NOT_SUPPORTED</code>. In case the <code>DmtAdmin</code>
 * receives a <code>null</code> from a plugin (or other value it can not
 * interpret as a proper result for a property getter method) it must also throw
 * the appropriate <code>DmtException</code>.
 */
public interface DmtReadOnly {

    /**
     * Closes a session. If the session was opened with atomic lock mode, the
     * <code>DmtSession</code> must first persist the changes made to the DMT
     * by calling <code>commit()</code> on all (transactional) plugins
     * participating in the session. See the documentation of the
     * {@link Dmt#commit} method for details and possible errors during this
     * operation.
     * <p>
     * Plugins implementing this method can assume that <code>commit()</code>
     * is explicitly called before <code>close()</code> if needed, i.e. this
     * method should not perform any data manipulation, only cleanup operations.
     * <p>
     * The <code>DmtSession</code> must guarantee that this method is always
     * called for all plugins that were opened. This is the only method that is
     * called on a plugin after it throws a fatal exception during a session, 
     * or after any exception thrown during a <code>commit</code> or
     * <code>rollback</code> operation.
     * <p>
     * The state of the session changes to <code>DmtSession.STATE_CLOSED</code> 
     * if the close operation completed successfully, otherwise it becomes
     * <code>DmtSession.STATE_INVALID</code>.
     * 
     * @throws DmtException with the following possible error codes
     *         <li><code>COMMAND_FAILED</code> if an underlying plugin failed
     *         to close
     *         <li><code>DATA_STORE_FAILURE</code>
     *         <li><code>METADATA_MISMATCH</code>
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    void close() throws DmtException;

    /**
     * Check whether the specified URI corresponds to a valid node in the DMT.
     * 
     * @param nodeUri the URI to check
     * @return true if the given node exists in the DMT
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed
     */
    boolean isNodeUri(String nodeUri);

    /**
     * Tells whether a node is a leaf or an interior node of the DMT.
     * 
     * @param nodeUri the URI of the node
     * @return true if the given node is a leaf node
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>URI_TOO_LONG</code>
     *         <li><code>INVALID_URI</code>
     *         <li><code>PERMISSION_DENIED</code>
     *         <li><code>OTHER_ERROR</code> if the URI is not within the
     *         current session's subtree
     *         <li><code>COMMAND_NOT_ALLOWED</code>
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed
     */
    boolean isLeafNode(String nodeUri) throws DmtException;

    /**
     * Get the data contained in a leaf node.
     * 
     * @param nodeUri the URI of the node to retrieve
     * @return the data of the leaf node
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>URI_TOO_LONG</code>
     *         <li><code>INVALID_URI</code>
     *         <li><code>PERMISSION_DENIED</code>
     *         <li><code>OTHER_ERROR</code> if the URI is not within the
     *         current session's subtree
     *         <li><code>COMMAND_FAILED</code>
     *         <li><code>COMMAND_NOT_ALLOWED</code> if the specified node is
     *         not a leaf node
     *         <li><code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    DmtData getNodeValue(String nodeUri) throws DmtException;

    /**
     * Get the title of a node.
     * 
     * @param nodeUri the URI of the node
     * @return the title of the node
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>URI_TOO_LONG</code>
     *         <li><code>INVALID_URI</code>
     *         <li><code>PERMISSION_DENIED</code>
     *         <li><code>OTHER_ERROR</code> if the URI is not within the
     *         current session's subtree
     *         <li><code>COMMAND_FAILED</code>
     *         <li><code>COMMAND_NOT_ALLOWED</code>
     *         <li><code>FEATURE_NOT_SUPPORTED</code>
     *         <li><code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    String getNodeTitle(String nodeUri) throws DmtException;

    /**
     * Get the type of a node. The type of leaf node is the MIME type of the
     * data it contains. The type of interior node is an URL pointing to a DDF
     * document.
     * 
     * @param nodeUri the URI of the node
     * @return the type of the node
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>URI_TOO_LONG</code>
     *         <li><code>INVALID_URI</code>
     *         <li><code>PERMISSION_DENIED</code>
     *         <li><code>OTHER_ERROR</code> if the URI is not within the
     *         current session's subtree
     *         <li><code>COMMAND_FAILED</code>
     *         <li><code>COMMAND_NOT_ALLOWED</code>
     *         <li><code>FEATURE_NOT_SUPPORTED</code>
     *         <li><code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    String getNodeType(String nodeUri) throws DmtException;

    /**
     * Get the version of a node. The version can not be set, it is calculated
     * automatically by the device. It is incremented each time the value of a
     * leaf node is changed. When a node is created the initial value is 0. The
     * version property is undefined for interior nodes.
     * 
     * @param nodeUri the URI of the node
     * @return the version of the node
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>URI_TOO_LONG</code>
     *         <li><code>INVALID_URI</code>
     *         <li><code>PERMISSION_DENIED</code>
     *         <li><code>OTHER_ERROR</code> if the URI is not within the
     *         current session's subtree
     *         <li><code>COMMAND_FAILED</code>
     *         <li><code>COMMAND_NOT_ALLOWED</code>
     *         <li><code>FEATURE_NOT_SUPPORTED</code>
     *         <li><code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    int getNodeVersion(String nodeUri) throws DmtException;

    /**
     * Get the timestamp when the node was last modified.
     * 
     * @param nodeUri the URI of the node
     * @return the timestamp of the last modification
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>URI_TOO_LONG</code>
     *         <li><code>INVALID_URI</code>
     *         <li><code>PERMISSION_DENIED</code>
     *         <li><code>OTHER_ERROR</code> if the URI is not within the
     *         current session's subtree
     *         <li><code>COMMAND_FAILED</code>
     *         <li><code>COMMAND_NOT_ALLOWED</code>
     *         <li><code>FEATURE_NOT_SUPPORTED</code>
     *         <li><code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    Date getNodeTimestamp(String nodeUri) throws DmtException;

    /**
     * Get the size of the data in the node. The value to return depends on the 
     * format of data in the node:
     * <ul>
     * <li>{@link DmtData#FORMAT_STRING STRING}, {@link DmtData#FORMAT_XML XML}
     * and {@link DmtData#FORMAT_BINARY BINARY}: the length of the stored data
     * <li>{@link DmtData#FORMAT_INTEGER INTEGER}: 4
     * <li>{@link DmtData#FORMAT_BOOLEAN BOOLEAN}: 1
     * <li>{@link DmtData#FORMAT_NULL NULL}: 0
     * </ul>
     * Throws <code>DmtException</code> with the error code
     * <code>COMMAND_NOT_ALLOWED</code> if issued on an interior node.
     * 
     * @param nodeUri the URI of the node
     * @return the size of the node
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>URI_TOO_LONG</code>
     *         <li><code>INVALID_URI</code>
     *         <li><code>PERMISSION_DENIED</code>
     *         <li><code>OTHER_ERROR</code> if the URI is not within the
     *         current session's subtree
     *         <li><code>COMMAND_FAILED</code>
     *         <li><code>COMMAND_NOT_ALLOWED</code>
     *         <li><code>FEATURE_NOT_SUPPORTED</code>
     *         <li><code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    int getNodeSize(String nodeUri) throws DmtException;

    /**
     * Get the list of children names of a node. The returned array contains the
     * names - not the URIs - of the immediate children nodes of the given node.
     * The returned array must not contain <code>null</code> entries. If a
     * plugin returns <code>null</code> as an array element, then the admin
     * must remove it from the array.
     * 
     * @param nodeUri the URI of the node
     * @return the list of child node names as a string array or an empty string
     *         array if the node has no children
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>URI_TOO_LONG</code>
     *         <li><code>INVALID_URI</code>
     *         <li><code>PERMISSION_DENIED</code>
     *         <li><code>OTHER_ERROR</code> if the URI is not within the
     *         current session's subtree
     *         <li><code>COMMAND_FAILED</code>
     *         <li><code>COMMAND_NOT_ALLOWED</code>
     *         <li><code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    String[] getChildNodeNames(String nodeUri) throws DmtException;

    /**
     * Get the meta data which describes a given node. Meta data can be only
     * inspected, it can not be changed.
     * <p>
     * This method is inherited in the <code>DmtSession</code> and also in the
     * Data Plugin interfaces, but the semantics of the method is slightly
     * different in these two cases. Meta data can be supported at the engine
     * level (i.e. in <code>DmtAdmin</code>), and its support by plugins is
     * an optional (and advanced) feature. It can be used, for example, when a
     * data plugin is implemented on top of a data store or another API that has
     * their own metadata, such as a relational database, in order to avoid
     * metadata duplication and inconsistency. The meta data specific to the
     * plugin returned by the plugin's <code>getMetaNode()</code> method is
     * complemented by the engine level meta data. The <code>DmtMetaNode</code>
     * the client receives on the <code>DmtSession.getMetaNode()</code> call
     * is the combination of the meta data returned by the data plugin plus the
     * meta data returned by the <code>DmtAdmin</code>. If there are
     * differences in the meta data elements known by the plugin and the
     * <code>DmtAdmin</code> then the plugin specific elements take
     * precedence.
     * 
     * @param nodeUri the URI of the node
     * @return a DmtMetaNode which describes meta data information
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>. Note that a node does not
     *         have to exist for having metadata associated to it. This error is
     *         thrown by the plugin or the <code>DmtAdmin</code> if the 
     *         specified node is not even defined, i.e. if it cannot exist.
     *         <li><code>URI_TOO_LONG</code>
     *         <li><code>INVALID_URI</code>
     *         <li><code>PERMISSION_DENIED</code>
     *         <li><code>OTHER_ERROR</code> if the URI is not within the
     *         current session's subtree
     *         <li><code>COMMAND_FAILED</code>
     *         <li><code>COMMAND_NOT_ALLOWED</code>
     *         <li><code>FEATURE_NOT_SUPPORTED</code>
     *         <li><code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    DmtMetaNode getMetaNode(String nodeUri) throws DmtException;

}
