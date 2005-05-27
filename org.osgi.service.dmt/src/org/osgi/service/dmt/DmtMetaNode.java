/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.dmt;

/**
 * The DmtMetaNode contains meta data both standard for SyncML DM and defined by
 * OSGi MEG (without breaking the compatibility) to provide for better DMT data
 * quality in an environment where many software components manipulate this
 * data.
 * <p>
 * The interface has two types of functions to describe type of nodes in the
 * DMT. One is used to retrieve standard OMA DM metadata, such as access mode,
 * cardinality, default etc. Another is used for meta data extensions defined by
 * OSGi MEG, such as valid values and regular expressions.
 * <p>
 * Most of the methods of this class return <code>null</code> if a certain
 * piece of meta information is not defined for the node or providing this
 * information is not supported. Methods of this class do not throw exceptions.
 */
public interface DmtMetaNode {

    /**
     * Constant for the ADD access type. If {@link #can(int)} returns
     * <code>true</code> for this operation, new nodes can potentially be
     * added as children under this node.
     */
    public int CMD_ADD     = 0;

    /**
     * Constant for the DELETE access type. If {@link #can(int)} returns
     * <code>true</code> for this operation, the node can potentially be 
     * deleted.
     */
    public int CMD_DELETE  = 1;
    
    /**
     * Constant for the EXECUTE access type. If {@link #can(int)} returns
     * <code>true</code> for this operation, the node can potentially be 
     * executed.
     */
    public int CMD_EXECUTE = 2;
    
    /**
     * Constant for the REPLACE access type. If {@link #can(int)} returns
     * <code>true</code> for this operation, the value (in case of leaf nodes)
     * and properties of the node can can potentially be modified.  
     */
    public int CMD_REPLACE = 3;

    /**
     * Constant for the GET access type. If {@link #can(int)} returns
     * <code>true</code> for this operation, the value (in case of leaf nodes),
     * the list of child nodes (in case of interior nodes) and the properties
     * of the node can potentially be retrieved.  
     */
    public int CMD_GET     = 4;

    /**
     * Constant for representing a permanent node in the tree.  This must be
     * returned by {@link #getScope} if the node cannot be added, deleted or  
     * modified in any way through tree operations.  Permanent nodes cannot
     * have dynamic nodes as parents.
     */
    public int PERMANENT   = 0;
    
    /**
     * Constant for representing a dynamic node in the tree.  This must be
     * returned by {@link #getScope} for all nodes that are not permanent.
     */
    public int DYNAMIC     = 1;
    
    /**
     * Check whether the given operation is valid for this node. If no meta-data
     * is provided for a node, all operations are valid. 
     * 
     * @param operation One of the <code>DmtMetaNode.CMD_...</code> constants.
     * @return <code>false</code> if the operation is not valid for this node
     *         or the operation code is not one of the allowed constants
     */
    boolean can(int operation);
    
    /**
     * Check whether the node is a leaf node or an internal one.
     * 
     * @return <code>true</code> if the node is a leaf node
     */
    boolean isLeaf();

    /**
     * Return the scope of the node. Valid values are
     * {@link #PERMANENT DmtMetaNode.PERMANENT} and
     * {@link #DYNAMIC DmtMetaNode.DYNAMIC}. Note that a permanent node is not
     * the same as a node where the DELETE operation is not allowed. Permanent
     * nodes never can be deleted, whereas a non-deletable node can disappear in
     * a recursive DELETE operation issued on one of its parents. If no
     * meta-data is provided for a node, it can be assumed to be a dynamic node.
     * 
     * @return {@link #PERMANENT} for permanent nodes and {@link #DYNAMIC}
     *         otherwise
     */
    int getScope();

    /**
     * Get the explanation string associated with this node.  Can be
     * <code>null</code> if no description is provided for this node.
     * 
     * @return node description string or <code>null</code> for no description
     */
    String getDescription();

    /**
     * Get the number of maximum occurrences of this type of nodes on the same
     * level in the DMT. Returns <code>Integer.MAX_VALUE</code> if there is no
     * upper limit. Note that if the occurrence is greater than 1 then this node
     * can not have siblings with different metadata. In other words, if
     * different types of nodes coexist on the same level, their occurrence can 
     * not be greater than 1. If no meta-data is provided for a node, there is 
     * no upper limit on the number of occurrences.
     * 
     * @return The maximum allowed occurrence of this node type
     */
    int getMaxOccurrence();

    /**
     * Check whether zero occurrence of this node is valid.  If no meta-data is
     * returned for a node, zero occurrences are allowed.
     * 
     * @return <code>true</code> if zero occurrence of this node is valid
     */
    boolean isZeroOccurrenceAllowed();

    /**
     * Get the default value of this node if any.
     * 
     * @return The default value or <code>null</code> if not defined
     */
    DmtData getDefault();

    /**
     * Get the maximum allowed value associated with an integer node. If no
     * meta-data is provided for a node, there is no upper limit to its value.
     * 
     * @return the allowed maximum, or <code>Integer.MAX_VALUE</code> if there 
     *         is no upper limit defined or the node's format is not integer
     */
    int getMax();

    /**
     * Get the minimum allowed value associated with an integer node. If no
     * meta-data is provided for a node, there is no lower limit to its value.
     * 
     * @return the allowed minimum, or <code>Integer.MIN_VALUE</code> if there
     *         is no lower limit defined or the node's format is not integer
     */
    int getMin();

    /**
     * Return an array of DmtData objects if valid values are defined for the
     * node, or <code>null</code> otherwise. If no meta-data is provided for a
     * node, all values are considered valid.
     * 
     * @return the valid values for this node, or <code>null</code> if not
     *         defined
     */
    DmtData[] getValidValues();
    
    /**
     * Return an array of Strings if valid names are defined for the node, or
     * <code>null</code> if no valid name list is defined or if this piece of
     * meta info is not supported.  If no meta-data is provided for a node, all
     * names are considered valid.
     * 
     * @return the valid values for this node name, or <code>null</code> if
     *         not defined
     */
    String[] getValidNames();

    /**
     * Get the node's format, expressed in terms of type constants defined in
     * {@link DmtData}. If there are multiple formats allowed for the node then
     * the format constants are OR-ed. Interior nodes must have
     * {@link DmtData#FORMAT_NODE} format, and this code must not be returned
     * for leaf nodes. If no meta-data is provided for a node, all applicable
     * formats are considered valid (with the above constraints regarding
     * interior and leaf nodes).
     * <p>
     * Note that the 'format' term is a legacy from OMA DM, it is more customary
     * to think of this as 'type'.
     * 
     * @return The allowed format(s) of the node
     */
    int getFormat();

    /**
     * Get the regular expression associated with the value of this node, if
     * any. This method makes sense only in the case of <code>chr</code>
     * nodes.
     * 
     * @return The regular expression associated with this node or
     *         <code>null</code> if not defined, or if the node is not of type
     *         <code>chr</code>
     */
    String getPattern();
    
    /**
     * Get the regular expression associated with the name of this node, if any.
     * 
     * @return The regular expression associated with the name of this node or
     *         <code>null</code> if not defined.
     *         ### which regex can be used?
     */
    String getNamePattern();

    /**
     * Get the list of MIME types this node can hold. If there is a default
     * value defined for this node then the associated MIME type (if any) must
     * be the first element of the list. If no meta-data is provided for a node,
     * all MIME types are considered valid.
     * 
     * @return The list of allowed MIME types for this node or <code>null</code>
     *         if not defined
     */
    String[] getMimeTypes();
}
