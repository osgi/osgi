package org.osgi.service.dmt;

import java.util.Date;

/**
 * This interface collects basic DMT operations. The application programmers
 * use these methods when they are interacting with a <code>DmtSession</code> 
 * which inherits from this interface.
 */
public interface DmtReadOnly {
    
    /**
     * Closes a session and makes the changes made to the DMT persistent. 
     * Persisting the changes works differently for exclusive and atomic lock. 
     * For the former all changes that were accepted are persisted. For the 
     * latter once an error is encountered, all successful changes are rolled 
     * back.
     * <p> This method can fail even if all operations were successful. This 
     * can happen due to some multi-node semantic constraints defined by a
     * specific implementation. For example, node A can be required to always 
     * have children A.B, A.C and A.D. If this condition is broken when 
     * <code>close()</code> is executed, the method will fail, and throw an 
     * exception. 
     */
    void close() throws DmtException;

    /**
     * Check whether the specified URI corresponds to a valid node in the DMT.
     * @param nodeUri the URI to check
     * @return true if the given node exists in the DMT
     */
    boolean isNodeUri(String nodeUri);

    /**
     * Get the data contained in a leaf node. Throws <code>DmtException</code>
     * with the error code <code>COMMAND_NOT_ALLOWED</code> if issued on an 
     * interior node.
     * @param nodeUri The URI of the node to retrieve
     * @return The data of the leaf node
     * @throws DmtException
     */
    DmtData getNodeValue(String nodeUri) throws DmtException;
    
    /**
     * Get the title of a node
     * @param nodeUri The URI of the node
     * @return The title of the node
     * @throws DmtException
     */
    String  getNodeTitle(String nodeUri) throws DmtException;
    
    /**
     * Get the type of a node. The type of leaf node is the MIME type of the
     * data it contains. The type of interior node is an URL pointing to a DDF
     * document.
     * @param nodeUri The URI of the node
     * @return The type of the node 
     * @throws DmtException
     */
    String  getNodeType(String nodeUri) throws DmtException;
    
    /**
     * Get the version of a node. The version can not be set, it is calculated
     * automatically by the device. It is incremented each time the value of 
     * the node is changed. 
     * @param nodeUri The URI of the node
     * @return The version of the node
     * @throws DmtException
     */
    int     getNodeVersion(String nodeUri) throws DmtException;
    
    /**
     * Get the timestamp when the node was last modified
     * @param nodeUri The URI of the node
     * @return The timestamp of the last modification
     * @throws DmtException
     */
    Date    getNodeTimestamp(String nodeUri) throws DmtException;
    
    /**
     * Get the size of a node in bytes
     * @param nodeUri The URI of the node
     * @return The size of the node
     * @throws DmtException
     */
    int     getNodeSize(String nodeUri) throws DmtException;

    /**
     * Get the list of children names of a node. The returned array contains the
     * names - not the URIs - of the immediate children nodes of the given
     * node. 
     * @param nodeUri The URI of the node
     * @return The list of children node names as a string array.
     * @throws DmtException
     */
    String[] getChildNodeNames(String nodeUri) throws DmtException;
}
