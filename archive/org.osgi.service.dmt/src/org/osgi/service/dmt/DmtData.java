/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.dmt;

import java.util.Arrays;

/**
 * A data structure representing a leaf node. This structure represents
 * only the value and the format property of the node, all other properties of 
 * the node (like MIME type) can be set and read using the <code>Dmt</code> 
 * and <code>DmtReadOnly</code> interfaces.
 * <p> Different constructors are available to create nodes with different 
 * formats. Nodes of <code>null</code> format can be created using the static
 * <code>DmtData.NULL_VALUE</code> constant instance of this class.
 */
public class DmtData {

    /**
     * The node holds an integer value. Note that this does not correspond to
     * the Java <code>int</code> type, OMA DM integers are unsigned.
     */
    public static final int FORMAT_INTEGER = 0x01;

    /**
     * The node holds an OMA DM <code>chr</code> value.
     */
    public static final int FORMAT_STRING  = 0x02;

    /**
     * The node holds an OMA DM <code>bool</code> value.
     */
    public static final int FORMAT_BOOLEAN = 0x04;

    /**
     * The node holds an OMA DM <code>binary</code> value. The value of the
     * node corresponds to the Java <code>byte[]</code> type.
     */
    public static final int FORMAT_BINARY  = 0x08;

    /**
     * The node holds an OMA DM <code>xml</code> value.
     */
    public static final int FORMAT_XML     = 0x10;

    /**
     * The node holds an OMA DM <code>null</code> value. This corresponds to
     * the Java <code>null</code> type.
     */
    public static final int FORMAT_NULL    = 0x20;

    /**
     * Format specifier of an internal node. A DmtData instance can not have 
     * this value. This is used only as a return value of the 
     * <code>DmtMetaNode.getFormat()</code> method.
     */
    public static final int FORMAT_NODE    = 0x40;

    /**
     * Constant instance representing a leaf node of <code>null</code> format.
     */
    public static DmtData NULL_VALUE = new DmtData(); 

    private String  str;
    private int     integer;
    private boolean bool;
    private byte[]  bytes;

    private int     format;

    /**
     * Create a DmtData instance of <code>null</code> format. This 
     * constructor is private and used only to create the public
     * NULL DmtData constant class.
     */
    private DmtData() {
        format = DmtData.FORMAT_NULL;
    }

    /**
     * Create a DmtData instance of <code>chr</code> format with the given 
     * string value.
     * @param str The string value to set
     */
    public DmtData(String str) {
        format = DmtData.FORMAT_STRING;
        this.str = str;
    }

    /**
     * Create a DmtData instance of <code>xml</code> format and set its value.
     * @param str The string or xml value to set
     * @param xml If <code>true</code> then a node of <code>xml</code> is
     * created otherwise this constructor behaves the same as
     * <code>DmtData(String)</code>.
     */
    public DmtData(String str, boolean xml) {
        this(str);
        if(xml)
            format = DmtData.FORMAT_XML;
    }

    /**
     * Create a DmtData instance of <code>int</code> format and set its
     * value.
     * @param integer The integer value to set
     */
    public DmtData(int integer) {
        format = DmtData.FORMAT_INTEGER;
        this.integer = integer;
    }

    /**
     * Create a DmtData instance of <code>bool</code> format and set its
     * value.
     * @param bool The boolean value to set
     */
    public DmtData(boolean bool) {
        format = DmtData.FORMAT_BOOLEAN;
        this.bool = bool;
    }

    /**
     * Create a DmtData instance of <code>bin</code> format and set its
     * value.
     * @param bytes The byte array to set
     */
    public DmtData(byte[] bytes) {
        format = DmtData.FORMAT_BINARY;
        this.bytes = bytes;
    }

    
    /**
     * Gets the value of a node with chr format
     * @return The string value
     * @throws DmtException with the error code 
     * <code>OTHER_ERROR</code> if the format of the node is not chr
     */
    public String getString() throws DmtException {
        if(format == DmtData.FORMAT_STRING)
            return str;

        throw new DmtException(null, DmtException.OTHER_ERROR,
                               "DmtData value is not string.");
    }

    /**
     * Gets the value of a node with xml format
     * @return The xml value
     * @throws DmtException with the error code 
     * <code>OTHER_ERROR</code> if the format of the node is not xml
     */
    public String getXml() throws DmtException {
        if(format == DmtData.FORMAT_XML)
            return str;

        throw new DmtException(null, DmtException.OTHER_ERROR,
                               "DmtData value is not xml.");
    }
    
    /**
     * Gets the value of a node with integer format
     * @return The integer value
     * @throws DmtException with the error code 
     * <code>OTHER_ERROR</code> if the format of the node is not integer
     */
    public int getInt() throws DmtException {
        if(format == DmtData.FORMAT_INTEGER)
            return integer;

        throw new DmtException(null, DmtException.OTHER_ERROR,
                               "DmtData value is not integer.");
    }

    /**
     * Gets the value of a node with boolean format
     * @return The boolean value
     * @throws DmtException with the error code 
     * <code>OTHER_ERROR</code> if the format of the node is not boolean
     */
    public boolean getBoolean() throws DmtException {
        if(format == DmtData.FORMAT_BOOLEAN)
            return bool;

        throw new DmtException(null, DmtException.OTHER_ERROR,
                               "DmtData value is not boolean.");
    }

    /**
     * Gets the value of a node with binary format
     * @return The binary value
     * @throws DmtException with the error code 
     * <code>OTHER_ERROR</code> if the format of the node is not binary
     */
    public byte[] getBinary() throws DmtException {
        if(format == DmtData.FORMAT_BINARY) {

            byte[] bytesCopy = new byte[bytes.length];
            for(int i = 0; i < bytes.length; i++)
                bytesCopy[i] = bytes[i];

            return bytesCopy;
        }

        throw new DmtException(null, DmtException.OTHER_ERROR,
                               "DmtData value is not a byte array.");
    }

    /**
     * Get the node's format, expressed in terms of type constants
     * defined in this class. Note that the 'format' term is a legacy
     * from OMA DM, it is more customary to think of this as 'type'.
     * @return The format of the node.
     */
    public int getFormat() {
        return format;
    }

    // TODO follow format when it is specified
    /**
     * Gets the string representation of the DmtNode. This method works for all
     * formats. [TODO specify for all formats. what does it mean if binary]
     * @return The string value of the DmtData
     */
    public String toString() {
        switch(format) {
        case DmtData.FORMAT_STRING:
        case DmtData.FORMAT_XML:     return str;
        case DmtData.FORMAT_INTEGER: return String.valueOf(integer);
        case DmtData.FORMAT_BOOLEAN: return String.valueOf(bool);
        case DmtData.FORMAT_BINARY:  return new String(bytes);
        case DmtData.FORMAT_NULL:    return null;
        }

        return null; // never reached
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof DmtData))
            return false;

        DmtData other = (DmtData) obj;

        if(format != other.format)
            return false;

        switch(format) {
        case DmtData.FORMAT_STRING:
        case DmtData.FORMAT_XML:     return str == null ? other.str == null :
                                                       str.equals(other.str);
        case DmtData.FORMAT_INTEGER: return integer == other.integer;
        case DmtData.FORMAT_BOOLEAN: return bool == other.bool;
        case DmtData.FORMAT_BINARY:  return Arrays.equals(bytes, other.bytes);
        case DmtData.FORMAT_NULL:    return true;
        }

        return false;           // never reached
    }

    public int hashCode() {
        switch(format) {
        case DmtData.FORMAT_STRING:
        case DmtData.FORMAT_XML:     return str.hashCode();
        case DmtData.FORMAT_INTEGER: return new Integer(integer).hashCode();
        case DmtData.FORMAT_BOOLEAN: return new Boolean(bool).hashCode();
        case DmtData.FORMAT_BINARY:  return new String(bytes).hashCode();
        case DmtData.FORMAT_NULL:    return 0; // TODO is this OK?
        }

        return 0;               // never reached
    }
}
