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
 * The DmtMetaNode contains meta data both standard for SyncML DM and
 * defined by OSGi MEG (without breaking the compatibility) to provide for
 * better DMT data quality in an environment where many software components
 * manipulate this data.
 * <p> The interface has two types of functions to describe type of nodes
 * in the DMT. One is used to retrieve standard OMA DM metadata, such as
 * access mode, cardinality, default etc. Another is used for meta data
 * extensions defined by OSGi MEG, such as valid values and regular expressions.
 * <p> Most of the methods of this class return <code>null</code> if a certain
 * piece of meta information is not defined for the node or providing this
 * information is not supported. Methods of this class do not throw exceptions.
 */
public interface DmtMetaNode {

    public int CMD_ADD     = 0;
    public int CMD_DELETE  = 1;
    public int CMD_EXECUTE = 2;
    public int CMD_REPLACE = 3;
    public int CMD_GET     = 4;

    public int PERMANENT   = 0;
    public int DYNAMIC     = 1;
    
    /**
     * Check whether the given operation is valid for this node.
     * @param operation One of the <code>DmtMetaNode.CMD_...</code> constants.
     * @return <code>false</code> if the operation is not valid for this node 
     * or the operation code is not one of the allowed constants.
     */
    boolean   can(int operation);
    
    /**
     * Check whether the node is a leaf node or an internal one
     * @return <code>true</code> if the node is a leaf node
     */
    boolean   isLeaf();

    /**
     * Return the scope of the node. Valid values are 
     * <code>DmtMetaNode.PERMANENT</code> and <code>DmtMetaNode.DYNAMIC</code>.
     * Note that a permanent node
     * is not the same as a node where the DELETE operation is not allowed.
     * Permanent nodes never can be deleted, whereas a non-deletable node can
     * disappear in a recursive DELETE operation issued on one of its parents.
     * @return <code>DmtMetaNode.PERMANENT</code> or 
     * <code>DmtMetaNode.DYNAMIC</code>
     */
    int     getScope();

    /**
     * Get the explanation string associated with this node
     * @return Node description string
     */
    String    getDescription();

    /**
     * Get the number of maximum occurrences of this type of nodes on the same
     * level in the DMT. Returns <code>Integer.MAX_VALUE</code> if there is no
     * upper limit. Note that if the occurrence is greater than 1 then
     * this node can not have siblings with different metadata. That is, if 
     * different type of nodes coexist on the same level, their occurrence can 
     * not be greater than 1. 
     * @return The maximum allowed occurrence of this node type
     */
    int       getMaxOccurrence();

    /**
     * Check whether zero occurrence of this node is valid
     * @return <code>true</code> if zero occurrence of this node is valid
     */
    boolean   isZeroOccurrenceAllowed();

    /**
     * Get the default value of this node if any.
     * @return The default value or <code>null</code> if not defined.
     */
    DmtData   getDefault();

     /**
     * Get the maximum allowed value associated with an integer node.
     * @return The allowed maximum. If there is no upper limit defined or the
     * node's format is not integer then
     * <code>Integer.MAX_VALUE</code> is returned.
     */
    int       getMax();

    /**
     * Get the minimum allowed value associated with an integer node.
     * @return The allowed minimum. If there is no lower limit defined or the
     * node's format is not integer then
     * <code>Integer.MIN_VALUE</code> is returned.
     */
    int       getMin();

    /**
     * Return an array of DmtData objects if valid values are defined for
     * the node, or <code>null</code> otherwise
     * @return the valid values for this node, or <code>null</code> if
     * not defined
     */
    DmtData[] getValidValues();
    
    /**
     * Return an array of Strings if valid names are defined for
     * the node, or <code>null</code> if no valid name list is defined 
     * or if this piece of meta info is not supported
     * @return the valid values for this node name, or <code>null</code> if
     * not defined
     */
    String[] getValidNames();

    /**
     * Get the node's format, expressed in terms of type
     * constants defined in <code>DmtData</code>. If there are multiple formats
     * allowed for the node then the format constants ar OR-ed.
     * Note that the 'format'
     * term is a legacy from OMA DM, it is more customary to think of this as
     * 'type'.
     * @return The format of the node.
     */
    int       getFormat();

    /**
     * Get the regular expression associated with the value of this node if any. 
     * This method makes sense only in the case of <code>chr</code> nodes.
     * @return The regular expression associated with this node or
     * <code>null</code> if not defined, or if the node is not of type
     * <code>chr</code>.
     */
    String    getPattern();
    
    /**
     * Get the regular expression associated with the name of this node if any. 
     * @return The regular expression associated with the name of this node or
     * <code>null</code> if not defined.
     */
    String    getNamePattern();

    /**
     * Get the list of MIME types this node can hold.
     * @return The list of allowed MIME types for this node or
     * <code>null</code> if not defined. If there is a default value defined 
     * for this node then the associated MIME type (if any) must be the first
     * element of the list.
     */
    String[]  getMimeTypes();
}
