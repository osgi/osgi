package org.osgi.service.dmt;

/**
 * DmtSession provides concurrent access to the DMT. All DMT manipulation 
 * commands for management applications are available on the DmtSession 
 * interface. The session is associated with a root node which limits the
 * subtree in which the operations can be executed within this session. Most of
 * the operations take a node URI as parameter, it can be either an absolute
 * URI (starting with "./") or a URI relative to the root node of the session.
 * If the URI specified does not correspond to a legitimate node in the tree 
 * an exception is thrown. The only exception is the isNodeUri() method which 
 * returns false in case of an invalid URI.
 */
public interface DmtSession extends Dmt {
    /**
     * Sessions created with <code>LOCK_TYPE_SHARED</code> lock allows 
     * read-only access to the tree, but can be shared between multiple readers. 
     */
    static final int LOCK_TYPE_SHARED    = 0;
    /**
     * <code>LOCK_TYPE_EXCLUSIVE</code> lock guarantees full access to the 
     * tree, but can not be shared with any other locks.
     */
    static final int LOCK_TYPE_EXCLUSIVE = 1;
    /**
     * <code>LOCK_TYPE_AUTOMATIC</code> starts as a shared lock, and is 
     * escalated to an exclusive one when an update operation is performed
     * on the tree for the first time. This is the default lock type.
     */
    static final int LOCK_TYPE_AUTOMATIC = 2;
    /**
     * <code>LOCK_TYPE_ATOMIC</code> is an exclusive lock with transactional 
     * functionality.
     */
    static final int LOCK_TYPE_ATOMIC    = 3;
   
    /**
     * Gives the type of lock the session currently has, which might be
     * different from the type it was created with. 
     * @return One of the <code>LOCK_TYPE_...</code> constants.
     */
    int getLockType();

    /**
     * Gives the entity on whose behalf the session was created.
     * @return a DmtPrincipal object who created this session
     */
    DmtPrincipal getPrincipal();

    /**
     * The unique identifier of the session. The ID is generated automatically,
     * and it is guaranteed to be unique on a machine.
     * @return the session identification number
     */
    int getSessionId();

    /**
     * Executes a node. This corresponds to the EXEC operation in OMA DM. The 
     * semantics of an EXEC operation depend on the definition of the managed
     * object on which it is issued.
     * @param nodeUri the node on which the execute operation is issued
     * @param data the parameters to the execute operation. The format of the
     * data string is described by the managed object definition.
     * @throws DmtException 
     */
    void execute(String nodeUri, String data) throws DmtException;

    /**
     * Tells whether a node is a leaf or an interior node of the DMT.
     * @param nodeUri the URI of the node
     * @return true if the given node is a leaf node
     * @throws DmtException
     */
    boolean isLeafNode(String nodeUri) throws DmtException;

    /**
     * Gives the Access Control List associated with a given node.
     * @param nodeUri the URI of the node
     * @return the Access Control List belonging to the node
     * @throws DmtException
     */
    DmtAcl getNodeAcl(String nodeUri) throws DmtException;
    
    /**
     * Set the Access Control List associated with a given node.
     * @param nodeUri the URI of the node
     * @param acl the Access Control List to be set on the node
     * @throws DmtException
     */
    void setNodeAcl(String nodeUri, DmtAcl acl) throws DmtException;

    /**
     * Get the meta data which describes a given node. Meta data can be only
     * inspected, it can not be changed. 
     * @param nodeUri the URI of the node
     * @return a DmtMetaNode which describes meta data information
     * @throws DmtException
     */
    DmtMetaNode getMetaNode(String nodeUri) throws DmtException;

    /**
     * Get the root URI associated with this session. Gives "<code>.</code>" 
     * if the session was created without specifying a root.
     * @return the root URI
     */
    String getRootUri();
}
