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
 * A collection of DMT manipulation methods. The application programmers use
 * these methods when they are interacting with a <code>DmtSession</code>
 * which inherits from this interface. Data plugins also implement this
 * interface.
 */
public interface Dmt extends DmtReadOnly {

	/**
	 * Rolls back a series of DMT operations issued in the current atomic
	 * session since the last transaction boundary. Transaction boundaries are
	 * the creation of this object that starts the session, and all subsequent
	 * {@link Dmt#commit}and {@link Dmt#rollback}calls.
	 * 
	 * @throws DmtException with the following possible error codes
	 *         <li><code>ROLLBACK_FAILED</code> in case the rollback did not
	 *         succeed
	 *         <li><code>FEATURE_NOT_SUPPORTED</code> in case the session was
	 *         not created using the <code>LOCK_TYPE_ATOMIC</code> lock type
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 */
	void rollback() throws DmtException;

	/**
	 * Commits a series of DMT operations issued in the current atomic session
	 * since the last transaction boundary. Transaction boundaries are the
	 * creation of this object that starts the session, and all subsequent
	 * {@link Dmt#commit}and {@link Dmt#rollback}calls.
	 * <p>
	 * This method can fail even if all operations were successful. This can
	 * happen due to some multi-node semantic constraints defined by a specific
	 * implementation. For example, node A can be required to always have
	 * children A.B, A.C and A.D. If this condition is broken when
	 * <code>commit()</code> is executed, the method will fail, and throw a
	 * <code>COMMAND_FAILED</code> exception.
	 * <p>
	 * An error situation can arise due to the lack of a two phase commit
	 * mechanism in the underlying plugins. As an example, if plugin A has
	 * committed successfully but plugin B failed, the whole session must fail,
	 * but there is no way to undo the commit performed by A. To provide
	 * predictable behaviour, the commit operation should continue with the
	 * remaining plugins even after detecting a failure. All exceptions received
	 * from failed commits are aggregated into one
	 * <code>TRANSACTION_ERROR</code> exception thrown by this method.
	 * 
	 * @throws DmtException with the following possible error codes
	 *         <li><code>TRANSACTION_ERROR</code> if an error occured during
	 *         the commit of any of the underlying plugins
	 *         <li><code>FEATURE_NOT_SUPPORTED</code> in case the session was
	 *         not created using the <code>LOCK_TYPE_ATOMIC</code> lock type
	 *         <li><code>COMMAND_FAILED</code> if some multi-node semantic
	 *         constraint was violated during the course of the session
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>METADATA_MISMATCH</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 */
	void commit() throws DmtException;

	/**
	 * Set the title property of a node.
	 * 
	 * @param nodeUri The URI of the node
	 * @param title The title text of the node, can be <code>null</code>
	 * @throws DmtException with the following possible error codes
	 *         <li><code>NODE_NOT_FOUND</code>
	 *         <li><code>URI_TOO_LONG</code>
	 *         <li><code>INVALID_URI</code>
	 *         <li><code>PERMISSION_DENIED</code>
	 *         <li><code>FEATURE_NOT_SUPPORTED</code>
	 *         <li><code>COMMAND_FAILED</code> if the title string is too
	 *         long or contains not allowed characters
	 *         <li><code>OTHER_ERROR</code> if the URI is not within the
	 *         current session's subtree
	 *         <li><code>COMMAND_NOT_ALLOWED</code>
	 *         <li><code>METADATA_MISMATCH</code>
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>TRANSACTION_ERROR</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 */
	void setNodeTitle(String nodeUri, String title) throws DmtException;

	/**
	 * Set the value of a leaf node.
	 * 
	 * @param nodeUri The URI of the node
	 * @param data The data to be set. The format of the node is contained in
	 *        the <code>DmtData</code>. Nodes of <code>null</code> format
	 *        can be set by using {@link DmtData#NULL_VALUE}as second argument.
	 * @throws DmtException with the following possible error codes
	 *         <li><code>NODE_NOT_FOUND</code>
	 *         <li><code>URI_TOO_LONG</code>
	 *         <li><code>INVALID_URI</code>
	 *         <li><code>PERMISSION_DENIED</code>
	 *         <li><code>COMMAND_FAILED</code> if the data is
	 *         <code>null</code>
	 *         <li><code>OTHER_ERROR</code> if the URI is not within the
	 *         current session's subtree
	 *         <li><code>COMMAND_NOT_ALLOWED</code> if the specified node is
	 *         not a leaf node
	 *         <li><code>METADATA_MISMATCH</code>
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>FORMAT_NOT_SUPPORTED</code>
	 *         <li><code>TRANSACTION_ERROR</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 */
	void setNodeValue(String nodeUri, DmtData data) throws DmtException;

	/**
	 * Set the value of a leaf node to its default as defined by the node's meta
	 * data. The method throws a <code>METADATA_MISMATCH</code> exception if
	 * there is no default defined.
	 * 
	 * @param nodeUri The URI of the node
	 * @throws DmtException with the following possible error codes
	 *         <li><code>NODE_NOT_FOUND</code>
	 *         <li><code>URI_TOO_LONG</code>
	 *         <li><code>INVALID_URI</code>
	 *         <li><code>PERMISSION_DENIED</code>
	 *         <li><code>COMMAND_FAILED</code>
	 *         <li><code>OTHER_ERROR</code> if the URI is not within the
	 *         current session's subtree
	 *         <li><code>COMMAND_NOT_ALLOWED</code> if the specified node is
	 *         not a leaf node
	 *         <li><code>METADATA_MISMATCH</code> if there is no default
	 *         value defined for this node
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>TRANSACTION_ERROR</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 * @see #setNodeValue
	 */
	void setDefaultNodeValue(String nodeUri) throws DmtException;

	/**
	 * Set the type of a node. The type of leaf node is the MIME type of the
	 * data it contains. The type of interior node is an URL pointing to a DDF
	 * document.
	 * 
	 * @param nodeUri The URI of the node
	 * @param type The type of the node
	 * @throws DmtException with the following possible error codes
	 *         <li><code>NODE_NOT_FOUND</code>
	 *         <li><code>URI_TOO_LONG</code>
	 *         <li><code>INVALID_URI</code>
	 *         <li><code>PERMISSION_DENIED</code>
	 *         <li><code>OTHER_ERROR</code> if the URI is not within the
	 *         current session's subtree
	 *         <li><code>FEATURE_NOT_SUPPORTED</code>
	 *         <li><code>COMMAND_FAILED</code> if the type string is
	 *         <code>null</code> or invalid
	 *         <li><code>COMMAND_NOT_ALLOWED</code>
	 *         <li><code>METADATA_MISMATCH</code>
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>FORMAT_NOT_SUPPORTED</code>
	 *         <li><code>TRANSACTION_ERROR</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 */
	void setNodeType(String nodeUri, String type) throws DmtException;

	/**
	 * Delete the given node. Deleting interior nodes is recursive, the whole
	 * subtree under the given node is deleted.
	 * 
	 * @param nodeUri The URI of the node
	 * @throws DmtException with the following possible error codes
	 *         <li><code>NODE_NOT_FOUND</code>
	 *         <li><code>URI_TOO_LONG</code>
	 *         <li><code>INVALID_URI</code>
	 *         <li><code>PERMISSION_DENIED</code>
	 *         <li><code>OTHER_ERROR</code> if the URI is not within the
	 *         current session's subtree
	 *         <li><code>COMMAND_NOT_ALLOWED</code> if the node is permanent
	 *         or non-deletable
	 *         <li><code>METADATA_MISMATCH</code>
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>TRANSACTION_ERROR</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 */
	void deleteNode(String nodeUri) throws DmtException;

	/**
	 * Create an interior node.
	 * 
	 * @param nodeUri The URI of the node
	 * @throws DmtException with the following possible error codes
	 *         <li><code>URI_TOO_LONG</code>
	 *         <li><code>INVALID_URI</code>
	 *         <li><code>PERMISSION_DENIED</code>
	 *         <li><code>NODE_ALREADY_EXISTS</code>
	 *         <li><code>OTHER_ERROR</code> if the URI is not within the
	 *         current session's subtree
	 *         <li><code>METADATA_MISMATCH</code>
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>TRANSACTION_ERROR</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 */
	void createInteriorNode(String nodeUri) throws DmtException;

	/**
	 * Create an interior node with a given type. The type of interior node is a
	 * URL pointing to a DDF document.
	 * 
	 * @param nodeUri The URI of the node
	 * @param type The type URL of the interior node, must not be
	 *        <code>null</code>
	 * @throws DmtException with the following possible error codes
	 *         <li><code>URI_TOO_LONG</code>
	 *         <li><code>INVALID_URI</code>
	 *         <li><code>PERMISSION_DENIED</code>
	 *         <li><code>NODE_ALREADY_EXISTS</code>
	 *         <li><code>OTHER_ERROR</code> if the URI is not within the
	 *         current session's subtree
	 *         <li><code>COMMAND_FAILED</code> if the type string is
	 *         <code>null</code> or invalid
	 *         <li><code>METADATA_MISMATCH</code>
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>TRANSACTION_ERROR</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 * @see #createInteriorNode(String)
	 */
	void createInteriorNode(String nodeUri, String type) throws DmtException;

	/**
	 * Create a leaf node with default value. If a node does not have a default
	 * value defined by it's meta data, this method will throw a DmtException
	 * with error code <code>METADATA_MISMATCH</code>. The MIME type of the
	 * default node should also be specified by the meta data.
	 * 
	 * @param nodeUri The URI of the node
	 * @throws DmtException with the following possible error codes
	 *         <li><code>NODE_ALREADY_EXISTS</code>
	 *         <li><code>URI_TOO_LONG</code>
	 *         <li><code>INVALID_URI</code>
	 *         <li><code>PERMISSION_DENIED</code>
	 *         <li><code>OTHER_ERROR</code> if the URI is not within the
	 *         current session's subtree
	 *         <li><code>COMMAND_NOT_ALLOWED</code>
	 *         <li><code>METADATA_MISMATCH</code>
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>TRANSACTION_ERROR</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 * @see #createLeafNode(String, DmtData)
	 */
	void createLeafNode(String nodeUri) throws DmtException;

	/**
	 * Create a leaf node with a given value. The node's MIME type is not
	 * explicitely specified, it will be derived from the meta data associated
	 * with this node. The meta data defining the possible type (if any) should
	 * allow only one MIME type, otherwise this method will fail with
	 * <code>METADATA_MISMATCH</code>. Nodes of <code>null</code> format
	 * can be created by using {@link DmtData#NULL_VALUE}as second argument.
	 * 
	 * @param nodeUri The URI of the node
	 * @param value The value to be given to the new node, can not be
	 *        <code>null</code>
	 * @throws DmtException with the following possible error codes
	 *         <li><code>NODE_ALREADY_EXISTS</code>
	 *         <li><code>URI_TOO_LONG</code>
	 *         <li><code>INVALID_URI</code>
	 *         <li><code>PERMISSION_DENIED</code>
	 *         <li><code>OTHER_ERROR</code> if the URI is not within the
	 *         current session's subtree
	 *         <li><code>COMMAND_FAILED</code> if the data is
	 *         <code>null</code>
	 *         <li><code>COMMAND_NOT_ALLOWED</code>
	 *         <li><code>METADATA_MISMATCH</code>
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>FORMAT_NOT_SUPPORTED</code>
	 *         <li><code>TRANSACTION_ERROR</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 */
	void createLeafNode(String nodeUri, DmtData value) throws DmtException;

	/**
	 * Create a leaf node with a given value and MIME type. If the specified
	 * MIME type is <code>null</code>, the call is equivalent to
	 * <code>createLeafNode(nodeUri, value)</code>, i.e. the default MIME
	 * type is used as specified by the node meta-data.
	 * 
	 * @param nodeUri The URI of the node
	 * @param value The value to be given to the new node, can not be
	 *        <code>null</code>
	 * @param mimeType The MIME type to be given to the new node, can be
	 *        <code>null</code>
	 * @throws DmtException with the following possible error codes
	 *         <li><code>NODE_ALREADY_EXISTS</code>
	 *         <li><code>URI_TOO_LONG</code>
	 *         <li><code>INVALID_URI</code>
	 *         <li><code>PERMISSION_DENIED</code>
	 *         <li><code>OTHER_ERROR</code> if the URI is not within the
	 *         current session's subtree
	 *         <li><code>COMMAND_NOT_ALLOWED</code>
	 *         <li><code>COMMAND_FAILED</code> if the data is
	 *         <code>null</code>
	 *         <li><code>METADATA_MISMATCH</code>
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>FORMAT_NOT_SUPPORTED</code>
	 *         <li><code>TRANSACTION_ERROR</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 * @see #createLeafNode(String, DmtData)
	 */
	void createLeafNode(String nodeUri, DmtData value, String mimeType)
			throws DmtException;

	/**
	 * Create a deep copy of a node. All properties and values will be copied.
	 * The command works for single nodes and recursively for whole subtrees.
	 * 
	 * @param nodeUri The node or root of a subtree to be copied
	 * @param newNodeUri The URI of the new node or root of a subtree
	 * @param recursive <code>false</code> if only a single node is copied,
	 *        <code>true</code> if the whole subtree is copied.
	 * @throws DmtException with the following possible error codes
	 *         <li><code>NODE_ALREADY_EXISTS</code> if newNodeUri already
	 *         exists
	 *         <li><code>NODE_NOT_FOUND</code> if nodeUri does not exist
	 *         <li><code>URI_TOO_LONG</code>
	 *         <li><code>INVALID_URI</code>
	 *         <li><code>PERMISSION_DENIED</code>
	 *         <li><code>OTHER_ERROR</code> if either URI is not within the
	 *         current session's subtree
	 *         <li><code>COMMAND_NOT_ALLOWED</code> if any of the implied Get
	 *         or Add commands are not allowed, or if nodeUri is an ancestor of
	 *         newNodeUri
	 *         <li><code>METADATA_MISMATCH</code>
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>FORMAT_NOT_SUPPORTED</code>
	 *         <li><code>TRANSACTION_ERROR</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 */
	void copy(String nodeUri, String newNodeUri, boolean recursive)
			throws DmtException;

	/**
	 * Rename a node. The value and the other properties of the node does not
	 * change.
	 * 
	 * @param nodeUri The URI of the node to rename
	 * @param newName The new name property of the node. This is not the new URI
	 *        of the node, the new URI is constructed from the old URI and the
	 *        new name.
	 * @throws DmtException with the following possible error codes
	 *         <li><code>NODE_NOT_FOUND</code>
	 *         <li><code>URI_TOO_LONG</code>
	 *         <li><code>INVALID_URI</code>
	 *         <li><code>PERMISSION_DENIED</code>
	 *         <li><code>OTHER_ERROR</code> if the URI is not within the
	 *         current session's subtree
	 *         <li><code>COMMAND_FAILED</code> if the newName string is
	 *         invalid
	 *         <li><code>COMMAND_NOT_ALLOWED</code>
	 *         <li><code>METADATA_MISMATCH</code>
	 *         <li><code>DATA_STORE_FAILURE</code>
	 *         <li><code>TRANSACTION_ERROR</code>
	 * @throws IllegalStateException if the session is invalidated because of
	 *         timeout, or if the session is already closed
	 * @throws SecurityException if the caller does not have the necessary
	 *         permissions to execute the underlying management operation
	 */
	void renameNode(String nodeUri, String newName) throws DmtException;
}
