package org.osgi.service.dmt;

/**
 * A collection of DMT manipulation methods. The application programmers
 * use these methods when they are interacting with a <code>DmtSession</code> 
 * which inherits from this interface. Data plugins also implement this 
 * interface.
 */
public interface Dmt extends DmtReadOnly {
    
    /**
     * Rolls back a series of DMT operations issued in the current session since
     * it was opened. 
     * @throws DmtException The error code is <code>ROLLBACK_FAILED</code> in 
     * case the rollback
     * did not succedd. <code>FEATURE_NOT_SUPPORTED</code> in case the session
     * was not created using the <code>LOCK_TYPE_ATOMIC</code> lock type.
     */
    void rollback() throws DmtException;

    /**
     * Set the title property of a node.
     * @param nodeUri The URI of the node
     * @param title The title text of the node
     * @throws DmtException
     */
    void setNodeTitle(String nodeUri, String title) throws DmtException;
    
    /**
     * Set the value of a leaf node. 
     * @param nodeUri The URI of the node
     * @param data The data to be set. The format of the node is contained in
     * the DmtData. If <code>null</code> is given, the node will have the OMA 
     * <code>null</code> format. 
     * @throws DmtException
     */
    void setNode(String nodeUri, DmtData data) throws DmtException;
    
    /**
     * Get the type of a node. The type of leaf node is the MIME type of the
     * data it contains. The type of interior node is an URL pointing to a DDF
     * document.
     * @param nodeUri The URI of the node
     * @param type The type of the node
     * @throws DmtException
     */
    void setNodeType(String nodeUri, String type) throws DmtException;

    /**
     * Delete the given node. Deleting interior nodes is recursive, the whole
     * subtree under the given node is deleted.
     * @param nodeUri The URI of the node
     * @throws DmtException
     */
    void deleteNode(String nodeUri) throws DmtException;

    /**
     * Create an interior node 
     * @param nodeUri The URI of the node
     * @throws DmtException
     */
    void createInteriorNode(String nodeUri) throws DmtException;
    
    /**
     * Create an interior node with a given type. The type of interior node is 
     * an URL pointing to a DDF document.
     * @param nodeUri The URI of the node
     * @param type The type URL of the interior node
     * @throws DmtException
     */
    void createInteriorNode(String nodeUri, String type) throws DmtException;

    /**
     * Create a leaf node with a given value. The default constructor for 
     * DmtData is used to create nodes with default values. If a node does 
     * not have a default value, this method using such a DmtData object will
     * throw a DmtException with error code <code>METADATA_MISMATCH</code>.
     * If null is passed as the second parameter of the method, a node with 
     * <code>null</code> format will be created.
     * @param nodeUri The URI of the node
     * @param value The value to be given to the new node or <code>null</code>. 
     * @throws DmtException
     */
    void createLeafNode(String nodeUri, DmtData value) throws DmtException;

    /**
     * Create a deep copy of a node. All properties and values will be copied. 
     * The command works for single nodes and recursively for whole subtrees.
     * @param nodeUri The node or root of a subtree to be copied 
     * @param newNodeUri The URI of the new node or root of a subtree
     * @param recursive <code>false</code> if only a single node is copied,
     * <code>true</code> if the whole subtree is copied.
     * @throws DmtException
     */
    void clone(String nodeUri, String newNodeUri, boolean recursive)
        throws DmtException;

    /**
     * Rename a node. The value and the other properties of the node does not
     * change. 
     * @param nodeUri The URI of the node to rename
     * @param newName The new name property of the node. This is not the new 
     * URI of the node, the new URI is constructed from the old URI and the 
     * new name.
     * @throws DmtException
     */
    void renameNode(String nodeUri, String newName) throws DmtException;
}
