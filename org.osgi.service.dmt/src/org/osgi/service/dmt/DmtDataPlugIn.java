package org.osgi.service.dmt;

/**
 * An implementation of this interface takes the responsibility over a 
 * modifiable subtree of the DMT. If the subtree is non modifiable then
 * the <code>DmtReadOnlyDataPlugin</code> interface should be used. 
 * <p>The plugin might support transactionality, in this case it has to 
 * implemenent commit and rollback functionality. 
 * <p>In an OSGi environment such implementations should be registered 
 * at the OSGi service registry specifying the list of root node URIs 
 * in a String array in the <code>dataRootURIs</code> registration 
 * parameter.  
 */
public interface DmtDataPlugIn extends Dmt {
    
    /**
     * ??? Is this method needed?
     * This method is called to signal the start of a transaction 
     * when the first reference is made within a DmtSession to a 
     * node which is handled by this plugin.
     * @param lockMode One of the lock type constants specified in
     * <code>DmtSession</code>
     * @param session The session from which this plugin instance is accessed
     * @throws DmtException
     */
    void open(int lockMode, DmtSession session) 
        throws DmtException;
    
    /**
     * This method is called to signal the start of a transaction 
     * when the first reference is made within a DmtSession to a 
     * node which is handled by this plugin.
     * @param subtreeUri The subtree which is locked in the current session
     * @param lockMode One of the lock type constants specified in
     * <code>DmtSession</code>
     * @param session The session from which this plugin instance is accessed
     * @throws DmtException
     */
    void open(String subtreeUri, int lockMode, DmtSession session) 
        throws DmtException;
    
    /**
     * Get metadata information about a given node. The plugin implementation
     * may complement the metadata supplied by the Dmt Admin by its own, plugin
     * specific metadata. This mechanism can be used, for 
     * example, when a data plugin is implemented on top of a data store or 
     * another API that have their own metadata, such as a relational 
     * database, in order to avoid metadata duplication and inconsistency. 
     * @param nodeUri The URI of the node of which metadata information is
     * queried
     * @param generic Metadata information supplied by the Dmt Admin
     * @return The complete metadata information
     * @throws DmtException
     */
    DmtMetaNode getMetaNode(String nodeUri, DmtMetaNode generic) 
        throws DmtException;
    
    /**
     * Tells whether the plugin can handle atomic transactions. If a session is
     * created using <code>DmtSession.LOCK_TYPE_ATOMIC</code> locking and the 
     * plugin supports it then it is possible to roll back operations in the 
     * session.
     * @return <code>true</code> if the plugin can handle atomic transactions
     */
    boolean supportsAtomic();
}
