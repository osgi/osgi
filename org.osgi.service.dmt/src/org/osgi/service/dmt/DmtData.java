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

import java.util.Arrays;

// TODO add date and time constructors/getters/etc.
/**
 * A data structure representing a leaf node. This structure represents
 * only the value and the format property of the node, all other properties of 
 * the node (like MIME type) can be set and read using the <code>Dmt</code> 
 * and <code>DmtReadOnly</code> interfaces.
 * <p> 
 * Different constructors are available to create nodes with different 
 * formats. Nodes of <code>null</code> format can be created using the static
 * {@link #NULL_VALUE} constant instance of this class.
 */
public class DmtData {

    /**
     * The node holds an OMA DM <code>int</code> value.
     */
    public static final int FORMAT_INTEGER = 0x0001;

    /**
     * The node holds an OMA DM <code>float</code> value.
     */
    public static final int FORMAT_FLOAT   = 0x0002;

    /**
     * The node holds an OMA DM <code>chr</code> value.
     */
    public static final int FORMAT_STRING  = 0x0004;

    /**
     * The node holds an OMA DM <code>bool</code> value.
     */
    public static final int FORMAT_BOOLEAN = 0x0008;

    /**
     * The node holds an OMA DM <code>date</code> value. 
     * <p>
     * ### not implemented yet
     */
    public static final int FORMAT_DATE    = 0x0010;
    
    /**
     * The node holds an OMA DM <code>time</code> value. 
     * <p>
     * ### not implemented yet
     */
    public static final int FORMAT_TIME    = 0x0020;
    
    /**
     * The node holds an OMA DM <code>bin</code> value. The value of the
     * node corresponds to the Java <code>byte[]</code> type.
     */
    public static final int FORMAT_BINARY  = 0x0040;

    /**
     * The node holds an OMA DM <code>xml</code> value.
     */
    public static final int FORMAT_XML     = 0x0080;

    /**
     * The node holds an OMA DM <code>null</code> value. This corresponds to
     * the Java <code>null</code> type.
     */
    public static final int FORMAT_NULL    = 0x0100;

    /**
     * Format specifier of an internal node. A <code>DmtData</code> instance
     * can not have this value. This is used only as a return value of the
     * {@link DmtMetaNode#getFormat} method.
     */
    public static final int FORMAT_NODE    = 0x0200;

    /**
     * Constant instance representing a leaf node of <code>null</code> format.
     */
    public static DmtData NULL_VALUE = new DmtData(); 

    //private static long MILLIS_IN_DAY = 86400000;
    
    private String  str;
    private int     integer;
    private float   flt;
    private boolean bool;
    private byte[]  bytes;

    private int     format;

    /**
     * Create a <code>DmtData</code> instance of <code>null</code> format.
     * This constructor is private and used only to create the public
     * {@link #NULL_VALUE} constant.
     */
    private DmtData() {
        format = DmtData.FORMAT_NULL;
    }

    /**
     * Create a <code>DmtData</code> instance of <code>chr</code> format
     * with the given string value. The <code>null</code> string argument is
     * valid.
     * 
     * @param str The string value to set
     */
    public DmtData(String str) {
        format = DmtData.FORMAT_STRING;
        this.str = str;
    }

    /**
     * Create a <code>DmtData</code> instance of <code>date</code> format
     * with the given date. This format must not be used for storing complete
     * date and time information, only for storing a calendar date.  Therefore
     * the given date si 
     * 
     * @param date The date value to set, must not be <code>null</code>.
    public DmtData(Date date) {
        format = FORMAT_DATE;
        long millis = date.getTime();
        this.date = new Date(millis - millis % MILLIS_IN_DAY);
    }
     */
    
    /**
     * Create a <code>DmtData</code> instance of <code>xml</code> format and
     * set its value. The <code>null</code> string argument is valid.
     * 
     * @param str The string or XML value to set
     * @param xml If <code>true</code> then a node of <code>xml</code> is
     *        created otherwise this constructor behaves the same as
     *        {@link #DmtData(String)}
     */
    public DmtData(String str, boolean xml) {
        this(str);
        if(xml)
            format = DmtData.FORMAT_XML;
    }

    /**
     * Create a <code>DmtData</code> instance of <code>int</code> format and
     * set its value.
     * 
     * @param integer The integer value to set
     */
    public DmtData(int integer) {
        format = DmtData.FORMAT_INTEGER;
        this.integer = integer;
    }

    /**
     * Create a <code>DmtData</code> instance of <code>float</code> format and
     * set its value.
     * 
     * @param flt The float value to set
     */
    public DmtData(float flt) {
        format = DmtData.FORMAT_FLOAT;
        this.flt = flt;
    }

    /**
     * Create a <code>DmtData</code> instance of <code>bool</code> format
     * and set its value.
     * 
     * @param bool The boolean value to set
     */
    public DmtData(boolean bool) {
        format = DmtData.FORMAT_BOOLEAN;
        this.bool = bool;
    }

    /**
     * Create a <code>DmtData</code> instance of <code>bin</code> format and
     * set its value.
     * 
     * @param bytes The byte array to set
     */
    public DmtData(byte[] bytes) {
        format = DmtData.FORMAT_BINARY;
        this.bytes = bytes;
    }

    
    /**
     * Gets the value of a node with string (<code>chr</code>) format.
     * 
     * @return The string value
     * @throws DmtException with the error code <code>OTHER_ERROR</code> if
     *         the format of the node is not string
     */
    public String getString() throws DmtException {
        if(format == DmtData.FORMAT_STRING)
            return str;

        throw new DmtException(null, DmtException.OTHER_ERROR,
                               "DmtData value is not string.");
    }

    /**
     * Gets the value of a node with <code>xml</code> format.
     * 
     * @return The XML value
     * @throws DmtException with the error code <code>OTHER_ERROR</code> if
     *         the format of the node is not <code>xml</code>
     */
    public String getXml() throws DmtException {
        if(format == DmtData.FORMAT_XML)
            return str;

        throw new DmtException(null, DmtException.OTHER_ERROR,
                               "DmtData value is not XML.");
    }
    
    /**
     * Gets the value of a node with integer (<code>int</code>) format.
     * 
     * @return The integer value
     * @throws DmtException with the error code <code>OTHER_ERROR</code> if
     *         the format of the node is not integer
     */
    public int getInt() throws DmtException {
        if(format == DmtData.FORMAT_INTEGER)
            return integer;

        throw new DmtException(null, DmtException.OTHER_ERROR,
                               "DmtData value is not integer.");
    }

    /**
     * Gets the value of a node with <code>float</code> format.
     * 
     * @return The float value
     * @throws DmtException with the error code <code>OTHER_ERROR</code> if
     *         the format of the node is not <code>float</code>
     */
    public float getFloat() throws DmtException {
        if(format == DmtData.FORMAT_FLOAT)
            return flt;

        throw new DmtException(null, DmtException.OTHER_ERROR,
                               "DmtData value is not float.");
    }

    /**
     * Gets the value of a node with boolean (<code>bool</code>) format.
     * 
     * @return The boolean value
     * @throws DmtException with the error code <code>OTHER_ERROR</code> if
     *         the format of the node is not boolean
     */
    public boolean getBoolean() throws DmtException {
        if(format == DmtData.FORMAT_BOOLEAN)
            return bool;

        throw new DmtException(null, DmtException.OTHER_ERROR,
                               "DmtData value is not boolean.");
    }

    /**
     * Gets the value of a node with binary (<code>bin</code>) format.
     * 
     * @return The binary value
     * @throws DmtException with the error code <code>OTHER_ERROR</code> if
     *         the format of the node is not binary
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
     * Get the node's format, expressed in terms of type constants defined in
     * this class. Note that the 'format' term is a legacy from OMA DM, it is
     * more customary to think of this as 'type'.
     * 
     * @return The format of the node
     */
    public int getFormat() {
        return format;
    }
    
    /**
     * Get the size of the data. The returned value depends on the format of
     * data in the node:
     * <ul>
     * <li>{@link #FORMAT_STRING}, {@link #FORMAT_XML} and
     * {@link #FORMAT_BINARY}: the length of the stored data
     * <li>{@link #FORMAT_INTEGER} and {@link #FORMAT_FLOAT}: 4
     * <li>{@link #FORMAT_BOOLEAN}: 1
     * <li>{@link #FORMAT_NULL}: 0
     * </ul>
     */
    public int getSize() {
        switch(format) {
        case FORMAT_STRING:
        case FORMAT_XML:     return str.length();
        case FORMAT_BINARY:  return bytes.length;
        case FORMAT_INTEGER:
        case FORMAT_FLOAT:   return 4;
        case FORMAT_BOOLEAN: return 1;
        case FORMAT_NULL:    return 0;
        }
        
        return 0; // never reached
    }

    /**
     * Gets the string representation of the <code>DmtData</code>. This
     * method works for all formats.
     * <p>
     * For string format data, the string value itself is returned, while for
     * XML, integer, float and boolean formats the string form of the value is
     * returned. Binary data is represented by two-digit hexadecimal numbers for
     * each byte separated by spaces. The {@link #NULL_VALUE} data has the
     * string form of "<code>null</code>".
     * 
     * @return The string representation of this <code>DmtData</code> instance
     */
    public String toString() {
        switch(format) {
        case DmtData.FORMAT_STRING:
        case DmtData.FORMAT_XML:     return str;
        case DmtData.FORMAT_INTEGER: return String.valueOf(integer);
        case DmtData.FORMAT_FLOAT:   return String.valueOf(flt);
        case DmtData.FORMAT_BOOLEAN: return String.valueOf(bool);
        case DmtData.FORMAT_BINARY:  return getHexDump(bytes);
        case DmtData.FORMAT_NULL:    return "null";
        }

        return null; // never reached
    }

    /**
     * Compares the specified object with this <code>DmtData</code> instance.
     * Two <code>DmtData</code> objects are considered equal if their format
     * is the same, and their data (selected by the format) is equal.
     * 
     * @param obj the object to compare with this <code>DmtData</code>
     * @return true if the argument represents the same <code>DmtData</code>
     *         as this object
     */
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
        case DmtData.FORMAT_FLOAT:   return flt == other.flt;
        case DmtData.FORMAT_BOOLEAN: return bool == other.bool;
        case DmtData.FORMAT_BINARY:  return Arrays.equals(bytes, other.bytes);
        case DmtData.FORMAT_NULL:    return true;
        }

        return false;           // never reached
    }

    /**
     * Returns the hash code value for this <code>DmtData</code> instance. The
     * hash code is calculated based on the data (selected by the format) of
     * this object.
     * 
     * @return the hash code value for this object
     */
    public int hashCode() {
        switch(format) {
        case DmtData.FORMAT_STRING:
        case DmtData.FORMAT_XML:     return str == null ? 0 : str.hashCode();
        case DmtData.FORMAT_INTEGER: return new Integer(integer).hashCode();
        case DmtData.FORMAT_FLOAT:   return new Float(flt).hashCode();
        case DmtData.FORMAT_BOOLEAN: return new Boolean(bool).hashCode();
        case DmtData.FORMAT_BINARY:  return new String(bytes).hashCode();
        case DmtData.FORMAT_NULL:    return 0;
        }

        return 0;               // never reached
    }
    
    // character array of hexadecimal digits, used for printing binary data
    private static char[] hex = "0123456789ABCDEF".toCharArray();
        
    // generates a hexadecimal dump of the given binary data
    private static String getHexDump(byte[] bytes) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            buf.append(hex[(b & 0xF0) >> 4]).append(hex[b & 0x0F]).append(' ');
        }
        
        return buf.toString();
    }
}
