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

import java.util.Date;

/**
 * This interface collects basic DMT operations. The application programmers use
 * these methods when they are interacting with a <code>DmtSession</code>
 * which inherits from this interface.
 */
public interface DmtReadOnly {
	/**
	 * Closes a session and makes the changes made to the DMT persistent.
	 * Persisting the changes works differently for exclusive and atomic lock.
	 * For the former all changes that were accepted are persisted. For the
	 * latter once an error is encountered, all successful changes are rolled
	 * back.
	 * <p>
	 * This method can fail even if all operations were successful. This can
	 * happen due to some multi-node semantic constraints defined by a specific
	 * implementation. For example, node A can be required to always have
	 * children A.B, A.C and A.D. If this condition is broken when
	 * <code>close()</code> is executed, the method will fail, and throw an
	 * exception.
	 */
	void close() throws DmtException;

	/**
	 * Check whether the specified URI corresponds to a valid node in the DMT.
	 * 
	 * @param nodeUri the URI to check
	 * @return true if the given node exists in the DMT
	 */
	boolean isNodeUri(String nodeUri);

	/**
	 * Get the data contained in a leaf node. Throws <code>DmtException</code>
	 * with the error code <code>COMMAND_NOT_ALLOWED</code> if issued on an
	 * interior node.
	 * 
	 * @param nodeUri The URI of the node to retrieve
	 * @return The data of the leaf node
	 * @throws DmtException
	 */
	DmtData getNodeValue(String nodeUri) throws DmtException;

	/**
	 * Get the title of a node
	 * 
	 * @param nodeUri The URI of the node
	 * @return The title of the node
	 * @throws DmtException
	 */
	String getNodeTitle(String nodeUri) throws DmtException;

	/**
	 * Get the type of a node. The type of leaf node is the MIME type of the
	 * data it contains. The type of interior node is an URL pointing to a DDF
	 * document.
	 * 
	 * @param nodeUri The URI of the node
	 * @return The type of the node
	 * @throws DmtException
	 */
	String getNodeType(String nodeUri) throws DmtException;

	/**
	 * Get the version of a node. The version can not be set, it is calculated
	 * automatically by the device. It is incremented each time the value of the
	 * node is changed.
	 * 
	 * @param nodeUri The URI of the node
	 * @return The version of the node
	 * @throws DmtException
	 */
	int getNodeVersion(String nodeUri) throws DmtException;

	/**
	 * Get the timestamp when the node was last modified
	 * 
	 * @param nodeUri The URI of the node
	 * @return The timestamp of the last modification
	 * @throws DmtException
	 */
	Date getNodeTimestamp(String nodeUri) throws DmtException;

	/**
	 * Get the size of a node in bytes
	 * 
	 * @param nodeUri The URI of the node
	 * @return The size of the node
	 * @throws DmtException
	 */
	int getNodeSize(String nodeUri) throws DmtException;

	/**
	 * Get the list of children names of a node. The returned array contains the
	 * names - not the URIs - of the immediate children nodes of the given node.
	 * 
	 * @param nodeUri The URI of the node
	 * @return The list of children node names as a string array.
	 * @throws DmtException
	 */
	String[] getChildNodeNames(String nodeUri) throws DmtException;
}
