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
 * An implementation of this interface takes the responsibility of handling
 * EXEC requests in a subtree of the DMT.
 * <p>In an OSGi environment such implementations should be registered at
 * the OSGi service registry specifying the list of root node URIs in a
 * String array in the <code>execRootURIs</code> registration parameter.
 */
public interface DmtExecPlugin {

    /**
     * Execute the given node with the given data. The <code>execute()</code>
     * method of the DmtSession is forwarded to the appropriate DmtExecPlugin
     * which handles the request. This operation corresponds to the EXEC
     * command in OMA DM. The semantics of an EXEC operation and the data
     * parameters it takes depend on the definition of the managed object
     * on which the command is issued.
     * @param session A reference to the session in which the operation was
     * issued. Session information is needed in case an alert should be sent
     * back from the plugin.
     * @param nodeUri The node to be executed.
     * @param data The data of the EXEC operation. The format of the data 
     * is not specified, it depends on the definition of the managed object 
     * (the node). Can be <code>null</code>.
     * @throws DmtException
     */
    void execute(DmtSession session, String nodeUri, String data) 
         throws DmtException; //TODO specify exceptions
}
