package org.osgi.service.dmt;

/**
 * An implementation of this interface takes the responsibility of handling
 * EXEC requests in a subtree of the DMT.  
 * <p>In an OSGi environment such implementations should be registered at 
 * the OSGi service registry specifying the list of root node URIs in a 
 * String array in the <code>execRootURIs</code> registration parameter.
 */
public interface DmtExecPlugIn {
    
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
     * (the node).
     * @throws DmtException
     */
    void execute(DmtSession session, String nodeUri, String data) 
         throws DmtException;
}
