package org.osgi.service.dmt;

import java.util.Arrays;

/**
 * A data structure representing a leaf node's value. Creating instances of 
 * DmtData can happen in two ways, either with specifying a MIME type or 
 * without. 
 * <p> When creating an instance without explicitly specifying a MIME type,
 * MIME type information will be derived from either the old value of the
 * node (if the DmtData is used to update an already existing node's value), 
 * or from the meta data associated with this node (in case the DmtData is 
 * used in a <code>Dmt.createLeafNode()</code> method). In the latter case the 
 * meta data should allow only one MIME type, otherwise 
 * <code>Dmt.createLeafNode()</code> will fail.
 * <p> Similarly, if a constructor does not specify the value of the new
 * DmtData instance, its setting is also deferred till the invocation of 
 * either <code>Dmt.setNode()</code> or <code>Dmt.createLeafNode()</code> method. 
 * If the corresponding meta node has a default value, it is set, otherwise 
 * a <code>DmtException</code> is thrown.
 */
public class DmtData {
    private String str;
    private int integer;
    private boolean bool;
    private byte[] bytes;

    private int    format;
    private String mimeType = null;

    /**
     * Create a DmtData instance of <code>null</code> format.
     */
    public DmtData() {
        format = DmtDataType.NULL;
    }

    /**
     * Create a DmtData instance of <code>chr</code> format and set its value.
     * @param str The string value to set
     */
    public DmtData(String str) {
        format = DmtDataType.STRING;
        this.str = str;
        // TODO set mime type
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
        if(xml) {
            format = DmtDataType.XML;
            // TODO set mime type
        }
    }
    
    /**
     * Create a DmtData instance of <code>int</code> format and set its
     * value.
     * @param integer The integer value to set
     */
    public DmtData(int integer) {
        format = DmtDataType.INTEGER;
        this.integer = integer;
    }

    /**
     * Create a DmtData instance of <code>bool</code> format and set its 
     * value.
     * @param The boolean value to set
     */
    public DmtData(boolean bool) {
        format = DmtDataType.BOOLEAN;
        this.bool = bool;
    }

    /**
     * Create a DmtData instance of <code>bin</code> format and set its
     * value.
     * @param The byte array to set
     */
    public DmtData(byte[] bytes) {
        format = DmtDataType.BINARY;
        this.bytes = bytes;
        // TODO set mime type?
    }

    /**
     * Create a DmtData instance of <code>chr</code> format and set its value 
     * while also setting its MIME type.
     * @param str The string value to set
     * @param mimeType The MIME type to set
     */
    public DmtData(String str, String mimeType) {
        this(str);
        this.mimeType = mimeType;
    }

    /**
     * Create a DmtData instance of <code>xml</code> format and set its value
     * and also its MIME type.
     * @param str The string or xml value to set
     * @param xml If <code>true</code> then a node of <code>xml</code> is 
     * created otherwise this constructor behaves the same as 
     * <code>DmtData(String,String)</code>.
     * @param mimeType The MIME type to set.
     */
    public DmtData(String str, boolean xml, String mimeType) {
        this(str, xml);
        this.mimeType = mimeType;
    }
    
    /**
     * Create a DmtData instance of <code>bool</code> format and set its
     * value and also its MIME type.
     * @param bytes The byte array to set
     * @param mimeType The MIME type to set
     */
    public DmtData(byte[] bytes, String mimeType) {
        this(bytes);
        this.mimeType = mimeType;
    }

    
    // TODO follow format when it is specified
    /**
     * Gets the string representation of the DmtNode. This method works for all
     * formats. [TODO specify for all formats. what does it mean if binary]
     * @return The string value of the DmtData
     */
    public String getString() {
        switch(format) {
        case DmtDataType.STRING:
        case DmtDataType.XML:     return str;
        case DmtDataType.INTEGER: return String.valueOf(integer);
        case DmtDataType.BOOLEAN: return String.valueOf(bool);
        case DmtDataType.BINARY:  return new String(bytes);
        case DmtDataType.NULL:    return null;
        }

        return null; // never reached
    }

    /**
     * Gets the value of a node with boolean format 
     * @return The boolean value
     * @throws DmtException if the format of the node is not boolean
     */
    public boolean getBoolean() throws DmtException {
        if(format == DmtDataType.BOOLEAN)
            return bool;

        throw new IllegalStateException("DmtData value is not boolean.");
    }

    /**
     * Gets the value of a node with integer format 
     * @return The integer value
     * @throws DmtException if the format of the node is not boolean
     */
    public int getInt() throws DmtException {
        if(format == DmtDataType.INTEGER)
            return integer;

        throw new IllegalStateException("DmtData value is not integer.");
    }

    /**
     * Gets the value of a node with binary format 
     * @return The binary value
     * @throws DmtException if the format of the node is not binary
     */
    public byte[] getBinary() {
        if(format != DmtDataType.BINARY)
            return null; // TODO throw exception / return binary representation?

        byte[] bytesCopy = new byte[bytes.length];
        for(int i = 0; i < bytes.length; i++)
            bytesCopy[i] = bytes[i];

        return bytesCopy;
       
    }

    /**
     * Get the node's format, expressed in terms of type 
     * constants defined in <code>DmtDataType</code>. Note that the 'format'
     * term is a legacy from OMA DM, it is more customary to think of this as
     * 'type'. 
     * @return The format of the node.
     */
    public int getFormat() {
        return format;
    }

    /**
     * Get the current MIME type of the node. 
     * @return The MIME type string
     */
    public String getMimeType() {
        return mimeType;
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof DmtData))
            return false;

        DmtData other = (DmtData) obj;
        
        if(format != other.format)
            return false;

        if(mimeType == null ? other.mimeType != null : 
                              !mimeType.equals(other.mimeType))
            return false;
        
        switch(format) {
        case DmtDataType.STRING:
        case DmtDataType.XML:     return str == null ? other.str == null : 
                                                       str.equals(other.str);
        case DmtDataType.INTEGER: return integer == other.integer;
        case DmtDataType.BOOLEAN: return bool == other.bool;
        case DmtDataType.BINARY:  return Arrays.equals(bytes, other.bytes);
        case DmtDataType.NULL:    return true;
        }

        return false;           // never reached
    }

    public int hashCode() {
        int hash = 0;

        if(mimeType != null)
            hash += mimeType.hashCode();

        switch(format) {
        case DmtDataType.STRING:
        case DmtDataType.XML:     return hash + str.hashCode();
        case DmtDataType.INTEGER: return hash + new Integer(integer).hashCode();
        case DmtDataType.BOOLEAN: return hash + new Boolean(bool).hashCode();
        case DmtDataType.BINARY:  return hash + new String(bytes).hashCode();
        case DmtDataType.NULL:    return hash;
        }

        return 0;               // never reached
    }
}
