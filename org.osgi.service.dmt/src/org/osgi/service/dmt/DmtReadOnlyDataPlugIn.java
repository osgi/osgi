package org.osgi.service.dmt;

/**
 * An implementation of this interface takes the responsibility over a 
 * non-modifiable subtree of the DMT. In an OSGi environment such 
 * implementations should be registered at the OSGi service registry 
 * specifying the list of root node URIs in a String array in the 
 * <code>dataRootURIs</code> registration parameter.
 */
public interface DmtReadOnlyDataPlugIn extends DmtReadOnly {
    
    /**
     * ??? Is this method needed?
     * This method is called to signal the start of a transaction when the
     * first reference is made within a DmtSession to a node which is 
     * handled by this plugin. Although a read only plugin need not be 
     * concerned about transactionality, knowing the session from which it
     * is accessed can be useful for example in the case of sending alerts.
     * @param session The session from which this plugin instance is accessed
     * @throws DmtException
     */
    void open(DmtSession session) throws DmtException;
    
    /**
     * This method is called to signal the start of a transaction when the
     * first reference is made within a DmtSession to a node which is 
     * handled by this plugin. Although a read only plugin need not be 
     * concerned about transactionality, knowing the session from which it
     * is accessed can be useful for example in the case of sending alerts.
     * @param subtreeUri The subtree which is locked in the current session
     * @param session The session from which this plugin instance is accessed
     * @throws DmtException
     */
    void open(String subtreeUri, DmtSession session) throws DmtException;
    
    /**
     * Get metadata information about a given node. The plugin implementation
     * may complement the metadata supplied by the Dmt Admin by its own, plugin
     * specific metadata. This mechanism can be used, for example, when a data
     * plugin is implemented on top of a data store or another API that have 
     * their own metadata, such as a relational database, in order to avoid 
     * metadata duplication and inconsistency.
     * @param nodeUri The URI of the node of which metadata information is
     * queried
     * @param generic Metadata information supplied by the Dmt Admin
     * @return The complete metadata information
     * @throws DmtException
     */
    DmtMetaNode getMetaNode(String nodeUri, DmtMetaNode generic) 
        throws DmtException;
}
