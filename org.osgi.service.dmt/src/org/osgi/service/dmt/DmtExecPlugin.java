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
 * An implementation of this interface takes the responsibility of handling EXEC
 * requests in a subtree of the DMT.
 * <p>
 * In an OSGi environment such implementations should be registered at the OSGi
 * service registry specifying the list of root node URIs in a
 * <code>String</code> array in the <code>execRootURIs</code> registration
 * parameter.
 */
public interface DmtExecPlugin {

    // TODO can we specify OTHER_ERROR more precisely?
    /**
     * Execute the given node with the given data. The <code>execute()</code>
     * method of the <code>DmtSession</code> is forwarded to the appropriate
     * <code>DmtExecPlugin</code> which handles the request. This operation
     * corresponds to the EXEC command in OMA DM. The semantics of an EXEC
     * operation and the data parameters it takes depend on the definition of
     * the managed object on which the command is issued. Session information is
     * given as it is needed for sending alerts back from the plugin.
     * 
     * @param session a reference to the session in which the operation was
     *        issued, must not be <code>null</code>
     * @param nodeUri the node to be executed, must not be <code>null</code>
     * @param data the data of the EXEC operation, can be <code>null</code>
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code> if the plugin does not allow
     *         executing unexisting nodes
     *         <li><code>OTHER_ERROR</code> 
     *         <li><code>COMMAND_NOT_ALLOWED</code> if the node is
     *         non-executable
     *         <li><code>DATA_STORE_FAILURE</code>
     * @throws IllegalStateException if the session is invalidated because of
     *         timeout, or if the session is already closed.
     */
    void execute(DmtSession session, String nodeUri, String data) 
         throws DmtException;
}
