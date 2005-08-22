/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.dmt.spi;

import org.osgi.service.dmt.DmtException;

/**
 * Provides atomic read-write access to the part of the tree handled by this
 * plugin.
 */
public interface TransactionalDataSession extends ReadWriteDataSession {

    /**
     * Commits a series of DMT operations issued in the current atomic session
     * since the last transaction boundary. Transaction boundaries are the
     * creation of this object that starts the session, and all
     * subsequent {@link #commit} and {@link #rollback} calls.
     * <p>
     * This method can fail even if all operations were successful. This can
     * happen due to some multi-node semantic constraints defined by a specific
     * implementation. For example, node A can be required to always have
     * children A.B, A.C and A.D. If this condition is broken when
     * <code>commit()</code> is executed, the method will fail, and throw a
     * <code>COMMAND_FAILED</code> exception.
     * <p>
     * In many cases the tree is not the only way to manage a given part of the
     * system.  It may happen that while modifying some nodes in an atomic
     * session, the underlying settings are modified parallelly outside the 
     * scope of the DMT. If this is detected during commit, an exception with 
     * the code <code>CONCURRENT_ACCESS</code> is thrown.
     * 
     * @throws DmtException with the following possible error codes
     *         <ul>
     *         <li><code>METADATA_MISMATCH</code> if the operation failed
     *         because of meta-data restrictions
     *         <li><code>CONCURRENT_ACCESS</code> if it is detected that some
     *         modification has been made outside the scope of the DMT to the
     *         nodes affected in the session's operations
     *         <li><code>DATA_STORE_FAILURE</code> if an error occurred while
     *         accessing the data store
     *         <li><code>COMMAND_FAILED</code> if some multi-node semantic
     *         constraint was violated during the course of the session, or if 
     *         some unspecified error is encountered while attempting to 
     *         complete the command
     *         </ul>
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    void commit() throws DmtException;

    /**
     * Rolls back a series of DMT operations issued in the current atomic
     * session since the last transaction boundary. Transaction boundaries are
     * the creation of this object that starts the session, and all
     * subsequent {@link #commit} and {@link #rollback} calls.
     * 
     * @throws DmtException with the error code <code>ROLLBACK_FAILED</code> 
     *         in case the rollback did not succeed
     * @throws SecurityException if the caller does not have the necessary
     *         permissions to execute the underlying management operation
     */
    void rollback() throws DmtException;
}
