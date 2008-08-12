/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.service.dmt;

import java.util.Arrays;

// TODO discuss proposal for refactoring this class:
// The current DmtData representation (where the format is identified by an int) 
// should be replaced with a more OO approach of having a DmtData base class and
// different descendent classes for the different data types.  This would allow
// nicer construction (no problem with conflicting signatures).

// TODO add constructors and get/set methods for b64, to access the encoded value
// TODO add constructors and get/set methods for date/time, for more convenient Java access
// TODO define proper toString, hashCode, equals and getSize behaviour for date/time/binary/base64 formats
/**
 * A data structure representing the contents of a leaf or interior node. This
 * structure represents only the value and the format property of the node, all
 * other properties (like MIME type) can be set and read using the
 * <code>DmtSession</code> interface.
 * <p>
 * Different constructors are available to create nodes with different formats.
 * Nodes of <code>null</code> format can be created using the static
 * {@link #NULL_VALUE} constant instance of this class.
 */
public final class DmtData {

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
     */
    public static final int FORMAT_DATE    = 0x0010;
    
    /**
     * The node holds an OMA DM <code>time</code> value. 
     */
    public static final int FORMAT_TIME    = 0x0020;
    
    /**
     * The node holds an OMA DM <code>bin</code> value. The value of the
     * node corresponds to the Java <code>byte[]</code> type.
     */
    public static final int FORMAT_BINARY  = 0x0040;

    /**
     * The node holds an OMA DM <code>b64</code> value. Like
     * {@link #FORMAT_BINARY}, this format is also represented by the Java
     * <code>byte[]</code> type, the difference is only in the correspoding
     * OMA DM format.
     */
    public static final int FORMAT_BASE64  = 0x0080;

    /**
     * The node holds an OMA DM <code>xml</code> value.
     */
    public static final int FORMAT_XML     = 0x0100;

    /**
     * The node holds an OMA DM <code>null</code> value. This corresponds to
     * the Java <code>null</code> type.
     */
    public static final int FORMAT_NULL    = 0x0200;

    /**
     * Format specifier of an internal node. An interior node can hold a Java
     * object as value (see {@link DmtData#DmtData(Object)} and
     * {@link DmtData#getNode()}). This value can be used by Java programs that
     * know a specific URI understands the associated Java type. This type is
     * further used as a return value of the {@link MetaNode#getFormat} method
     * for interior nodes.
     */
    public static final int FORMAT_NODE    = 0x0400;

    /**
     * Constant instance representing a leaf node of <code>null</code> format.
     */
    public static DmtData NULL_VALUE = new DmtData(); 

    private final String  str;
    private final int     integer;
    private final float   flt;
    private final boolean bool;
    private final byte[]  bytes;
    private final Object  complex;

    private final int     format;

    /**
     * Create a <code>DmtData</code> instance of <code>null</code> format.
     * This constructor is private and used only to create the public
     * {@link #NULL_VALUE} constant.
     */
    private DmtData() {
        format = FORMAT_NULL;

        this.str     = null;
        this.integer = 0;
        this.flt     = 0;
        this.bool    = false;
        this.bytes   = null;
        this.complex = null;
    }

    /**
     * Create a <code>DmtData</code> instance of <code>chr</code> format
     * with the given string value. The <code>null</code> string argument is
     * valid.
     * 
     * @param str the string value to set
     */
    public DmtData(String str) {
        format = FORMAT_STRING;
        this.str = str;

        this.integer = 0;
        this.flt     = 0;
        this.bool    = false;
        this.bytes   = null;
        this.complex = null;
    }

    /**
     * Create a <code>DmtData</code> instance of <code>node</code> format
     * with the given object value. The value represents complex data associated
     * with an interior node.
     * <p>
     * Certain interior nodes can support access to their subtrees through such
     * complex values, making it simpler to retrieve or update all leaf nodes in
     * a subtree.
     * <p>
     * The given value must be a non-<code>null</code> immutable object.
     * 
     * @param complex the complex data object to set
     */
    public DmtData(Object complex) {
        if(complex == null)
            throw new NullPointerException("Complex data argument is null.");

        format = FORMAT_NODE;
        this.complex = complex;

        this.str     = null;
        this.integer = 0;
        this.flt     = 0;
        this.bool    = false;
        this.bytes   = null;
    }

    /**
     * Create a <code>DmtData</code> instance of the specified format and
     * set its value based on the given string.  Only the following string-based
     * formats can be created using this constructor:
     * <ul>
     * <li>{@link #FORMAT_STRING} - value can be any string
     * <li>{@link #FORMAT_XML} - value must contain an XML fragment (the 
     *     validity is not checked by this constructor)
     * <li>{@link #FORMAT_DATE} - value must be parseable to an ISO 8601 
     *     calendar date in complete representation, basic format (pattern
     *     <tt>CCYYMMDD</tt>)
     * <li>{@link #FORMAT_TIME} - value must be parseable to an ISO 8601 time of
     *     day in either local time, complete representation, basic format
     *     (pattern <tt>hhmmss</tt>) or Coordinated Universal Time, basic format 
     *     (pattern <tt>hhmmssZ</tt>)
     * </ul> 
     * The <code>null</code> string argument is only valid if the format is
     * string or XML.
     * 
     * @param value the string, XML, date or time value to set
     * @param format the format of the <code>DmtData</code> instance to be 
     *        created, must be one of the formats specified above
     * @throws IllegalArgumentException if <code>format</code> is not one of
     *         the allowed formats, or <code>value</code> is not a valid string
     *         for the given format
     * @throws NullPointerException if a date or time is constructed and
     *         <code>value</code> is <code>null</code>
     */
    public DmtData(String value, int format) {
        switch(format) {
        case FORMAT_DATE:
            checkDateFormat(value);
            break;
        case FORMAT_TIME:
            checkTimeFormat(value);
            break;
        case FORMAT_STRING:
        case FORMAT_XML:
            break; // nothing to do, all string values are accepted
        default:
            throw new IllegalArgumentException(
                    "Invalid format in string constructor: " + format);
        }
        this.format  = format;
        this.str     = value;
        this.integer = 0;
        this.flt     = 0;
        this.bool    = false;
        this.bytes   = null;
        this.complex = null;
    }
    
    /**
     * Create a <code>DmtData</code> instance of <code>int</code> format and
     * set its value.
     * 
     * @param integer the integer value to set
     */
    public DmtData(int integer) {
        format = FORMAT_INTEGER;
        this.integer = integer;
        
        this.str     = null;
        this.flt     = 0;
        this.bool    = false;
        this.bytes   = null;
        this.complex = null;
    }

    /**
     * Create a <code>DmtData</code> instance of <code>float</code> format and
     * set its value.
     * 
     * @param flt the float value to set
     */
    public DmtData(float flt) {
        format = FORMAT_FLOAT;
        this.flt = flt;
        
        this.str     = null;
        this.integer = 0;
        this.bool    = false;
        this.bytes   = null;
        this.complex = null;
    }

    /**
     * Create a <code>DmtData</code> instance of <code>bool</code> format
     * and set its value.
     * 
     * @param bool the boolean value to set
     */
    public DmtData(boolean bool) {
        format = FORMAT_BOOLEAN;
        this.bool = bool;
        
        this.str     = null;
        this.integer = 0;
        this.flt     = 0;
        this.bytes   = null;
        this.complex = null;
    }

    /**
     * Create a <code>DmtData</code> instance of <code>bin</code> format and
     * set its value.
     * 
     * @param bytes the byte array to set, must not be <code>null</code>
     * @throws NullPointerException if <code>bytes</code> is <code>null</code>
     */
    public DmtData(byte[] bytes) {
        if(bytes == null)
            throw new NullPointerException("Binary data argument is null.");
        
        format = FORMAT_BINARY;
        this.bytes = bytes;
        
        this.str     = null;
        this.integer = 0;
        this.flt     = 0;
        this.bool    = false;
        this.complex = null;
    }

    /**
     * Create a <code>DmtData</code> instance of <code>bin</code> or
     * <code>b64</code> format and set its value.  The chosen format is
     * specified by the <code>base64</code> parameter.
     * 
     * @param bytes the byte array to set, must not be <code>null</code>
     * @param base64 if <code>true</code>, the new instance will have
     * <code>b64</code> format, if <code>false</code>, it will have
     * <code>bin</code> format
     * @throws NullPointerException if <code>bytes</code> is <code>null</code>
     */
    public DmtData(byte[] bytes, boolean base64) {
        if(bytes == null)
            throw new NullPointerException("Binary data argument is null.");
        
        format = base64 ? FORMAT_BASE64 : FORMAT_BINARY;
        this.bytes = bytes;
        
        this.str     = null;
        this.integer = 0;
        this.flt     = 0;
        this.bool    = false;
        this.complex = null;
    }

    
    /**
     * Gets the value of a node with string (<code>chr</code>) format.
     * 
     * @return the string value
     * @throws IllegalStateException if the format of the node is not string
     */
    public String getString() {
        if(format == FORMAT_STRING)
            return str;

        throw new IllegalStateException("DmtData value is not string.");
    }
    
    /**
     * Gets the value of a node with date format. The returned date string is
     * formatted according to the ISO 8601 definition of a calendar date in
     * complete representation, basic format (pattern <tt>CCYYMMDD</tt>).
     * 
     * @return the date value
     * @throws IllegalStateException if the format of the node is not date
     */
    public String getDate() {
        if(format == FORMAT_DATE)
            return str;

        throw new IllegalStateException("DmtData value is not date.");
    }

    /**
     * Gets the value of a node with time format. The returned time string is
     * formatted according to the ISO 8601 definition of the time of day. The
     * exact format depends on the value the object was initialized with: 
     * either local time, complete representation, basic format
     * (pattern <tt>hhmmss</tt>) or Coordinated Universal Time, basic format
     * (pattern <tt>hhmmssZ</tt>).
     * 
     * @return the time value
     * @throws IllegalStateException if the format of the node is not time
     */
    public String getTime() {
        if(format == FORMAT_TIME)
            return str;

        throw new IllegalStateException("DmtData value is not time.");
    }

    /**
     * Gets the value of a node with <code>xml</code> format.
     * 
     * @return the XML value
     * @throws IllegalStateException if the format of the node is not
     *         <code>xml</code>
     */
    public String getXml() {
        if(format == FORMAT_XML)
            return str;

        throw new IllegalStateException("DmtData value is not XML.");
    }
    
    /**
     * Gets the value of a node with integer (<code>int</code>) format.
     * 
     * @return the integer value
     * @throws IllegalStateException if the format of the node is not integer
     */
    public int getInt() {
        if(format == FORMAT_INTEGER)
            return integer;

        throw new IllegalStateException("DmtData value is not integer.");
    }

    /**
     * Gets the value of a node with <code>float</code> format.
     * 
     * @return the float value
     * @throws IllegalStateException if the format of the node is not 
     *         <code>float</code>
     */
    public float getFloat() {
        if(format == FORMAT_FLOAT)
            return flt;

        throw new IllegalStateException("DmtData value is not float.");
    }

    /**
     * Gets the value of a node with boolean (<code>bool</code>) format.
     * 
     * @return the boolean value
     * @throws IllegalStateException if the format of the node is not boolean
     */
    public boolean getBoolean() {
        if(format == FORMAT_BOOLEAN)
            return bool;

        throw new IllegalStateException("DmtData value is not boolean.");
    }

    /**
     * Gets the value of a node with binary (<code>bin</code>) format.
     * 
     * @return the binary value
     * @throws IllegalStateException if the format of the node is not binary
     */
    public byte[] getBinary() {
        if(format == FORMAT_BINARY) {
            byte[] bytesCopy = new byte[bytes.length];
            for(int i = 0; i < bytes.length; i++)
                bytesCopy[i] = bytes[i];

            return bytesCopy;
        }

        throw new IllegalStateException("DmtData value is not a byte array.");
    }

    /**
     * Gets the value of a node with base 64 (<code>b64</code>) format.
     * 
     * @return the binary value
     * @throws IllegalStateException if the format of the node is not base 64.
     */
    public byte[] getBase64() {
        if(format == FORMAT_BASE64) {
            byte[] bytesCopy = new byte[bytes.length];
            for(int i = 0; i < bytes.length; i++)
                bytesCopy[i] = bytes[i];

            return bytesCopy;
        }

        throw new 
            IllegalStateException("DmtData value is not in base 64 format.");
    }

    /**
     * Gets the complex data associated with an interior node (<code>node</code>
     * format).
     * <p>
     * Certain interior nodes can support access to their subtrees through
     * complex values, making it simpler to retrieve or update all leaf nodes in
     * the subtree.
     * 
     * @return the data object associated with an interior node
     * @throws IllegalStateException if the format of the data is not 
     *         <code>node</code>
     */
    public Object getNode() {
        if(format == FORMAT_NODE)
            return complex;

        throw new IllegalStateException(
                "DmtData does not contain interior node data.");
    }

    /**
     * Get the node's format, expressed in terms of type constants defined in
     * this class. Note that the 'format' term is a legacy from OMA DM, it is
     * more customary to think of this as 'type'.
     * 
     * @return the format of the node
     */
    public int getFormat() {
        return format;
    }
    
    /**
     * Get the size of the data. The returned value depends on the format of
     * data in the node:
     * <ul>
     * <li>{@link #FORMAT_STRING}, {@link #FORMAT_XML}, {@link #FORMAT_BINARY}
     * and {@link #FORMAT_BASE64}: the length of the stored data, or 0 if the
     * data is <code>null</code>
     * <li>{@link #FORMAT_INTEGER} and {@link #FORMAT_FLOAT}: 4
     * <li>{@link #FORMAT_DATE} and {@link #FORMAT_TIME}: the length of the
     * date or time in its string representation
     * <li>{@link #FORMAT_BOOLEAN}: 1
     * <li>{@link #FORMAT_NODE}: -1 (unknown)
     * <li>{@link #FORMAT_NULL}: 0
     * </ul>
     * 
     * @return the size of the data stored by this object
     */
    public int getSize() {
        switch(format) {
        case FORMAT_STRING:
        case FORMAT_XML:     
        case FORMAT_DATE:    
        case FORMAT_TIME:    return str == null ? 0 : str.length();
        case FORMAT_BINARY:
        case FORMAT_BASE64:  return bytes.length;
        case FORMAT_INTEGER:
        case FORMAT_FLOAT:   return 4;
        case FORMAT_BOOLEAN: return 1;
        case FORMAT_NODE:    return -1;
        case FORMAT_NULL:    return 0;
        }
        
        return 0; // never reached
    }

    /**
     * Gets the string representation of the <code>DmtData</code>. This
     * method works for all formats.
     * <p>
     * For string format data, the string value itself is returned, while for
     * XML, date, time, integer, float, boolean and node formats the string form 
     * of the value is returned. Binary and base64 data is represented by 
     * two-digit hexadecimal numbers for each byte separated by spaces. The 
     * {@link #NULL_VALUE} data has the string form of "<code>null</code>".
     * Data of string or XML format containing the Java <code>null</code> value
     * is represented by an empty string.  
     * 
     * @return the string representation of this <code>DmtData</code> instance
     */
    public String toString() {
        switch(format) {
        case FORMAT_STRING:
        case FORMAT_XML:
        case FORMAT_DATE:
        case FORMAT_TIME:    return str == null ? "" : str;
        case FORMAT_INTEGER: return String.valueOf(integer);
        case FORMAT_FLOAT:   return String.valueOf(flt);
        case FORMAT_BOOLEAN: return String.valueOf(bool);
        case FORMAT_BINARY:  
        case FORMAT_BASE64:  return getHexDump(bytes);
        case FORMAT_NODE:    return complex.toString();
        case FORMAT_NULL:    return "null";
        }

        return "<unknown>"; // never reached
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
        case FORMAT_STRING:
        case FORMAT_XML:
        case FORMAT_DATE:
        case FORMAT_TIME:    return str == null ? other.str == null :
                                                  str.equals(other.str);
        case FORMAT_INTEGER: return integer == other.integer;
        case FORMAT_FLOAT:   return flt == other.flt;
        case FORMAT_BOOLEAN: return bool == other.bool;
        case FORMAT_BINARY:
        case FORMAT_BASE64:  return Arrays.equals(bytes, other.bytes);
        case FORMAT_NODE:    return complex.equals(other.complex);
        case FORMAT_NULL:    return true;
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
        case FORMAT_STRING:
        case FORMAT_XML:
        case FORMAT_DATE:
        case FORMAT_TIME:    return str == null ? 0 : str.hashCode();
        case FORMAT_INTEGER: return new Integer(integer).hashCode();
        case FORMAT_FLOAT:   return new Float(flt).hashCode();
        case FORMAT_BOOLEAN: return new Boolean(bool).hashCode();
        case FORMAT_BINARY:
        case FORMAT_BASE64:  return new String(bytes).hashCode();
        case FORMAT_NODE:    return complex.hashCode();
        case FORMAT_NULL:    return 0;
        }

        return 0;               // never reached
    }
    
    private static void checkDateFormat(String value) {
        if(value.length() != 8)
            throw new IllegalArgumentException("Date string '" + value + 
                    "' does not follow the format 'CCYYMMDD'.");
        
        int year = checkNumber(value, "Date", 0, 4, 0, 9999);
        int month = checkNumber(value, "Date", 4, 2, 1, 12);
        int day = checkNumber(value, "Date", 6, 2, 1, 31);
        
        // Date checking is not prepared for all special rules (for example
        // historical leap years), production code could contain a full check.
        
        // Day 31 is invalid for April, June, September and November
        if((month == 4 || month == 6 || month == 9 || month == 11) && day == 31)
        	throw new IllegalArgumentException("Date string '" + value + 
        			"' contains an invalid date.");
        
        // February 29 is invalid except for leap years, Feb. 30-31 are invalid 
        if(month == 2 && day > 28 &&
        	!(day == 29 && year%4 == 0 && (year%100 != 0 || year%400 == 0)))
        	throw new IllegalArgumentException("Date string '" + value + 
        			"' contains an invalid date.");
    }
    
    private static void checkTimeFormat(String value) {
        if(value.length() > 0 && value.charAt(value.length()-1) == 'Z')
            value = value.substring(0, value.length()-1);
        
        if(value.length() != 6)
            throw new IllegalArgumentException("Time string '" + value + 
                    "' does not follow the format 'hhmmss' or 'hhmmssZ'.");
            
        // Time checking is not prepared for all special rules (for example
        // leap seconds), production code could contain a full check.
        
        // if hour is 24, only 240000 should be allowed
        checkNumber(value, "Time", 0, 2, 0, 24);
        checkNumber(value, "Time", 2, 2, 0, 59);
        checkNumber(value, "Time", 4, 2, 0, 59);
        
        if(value.startsWith("24") && !value.startsWith("240000"))
        	throw new IllegalArgumentException("Time string is out of range.");
    }
    
    private static int checkNumber(String value, String name, int from, 
            int length, int min, int max) {
        String part = value.substring(from, from+length);
        int number;
        try {
            number = Integer.parseInt(part);
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException(name + " string '" + value +
                    "' contains a non-numeric part.");
        }
        if(number < min || number > max) 
            throw new IllegalArgumentException("A segment of the " + name +
                    " string '" + value + "' is out of range.");
        
        return number;
    }


    // character array of hexadecimal digits, used for printing binary data
    private static char[] hex = "0123456789ABCDEF".toCharArray();
        
    // generates a hexadecimal dump of the given binary data
    private static String getHexDump(byte[] bytes) {
        if(bytes.length == 0)
            return "";
        
        StringBuffer buf = new StringBuffer();
        appendHexByte(buf, bytes[0]);
        for (int i = 1; i < bytes.length; i++)
            appendHexByte(buf.append(' '), bytes[i]);
        
        return buf.toString();
    }
    
    private static void appendHexByte(StringBuffer buf, byte b) {
        buf.append(hex[(b & 0xF0) >> 4]).append(hex[b & 0x0F]);
    }
}
