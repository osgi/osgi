/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.monitor;

import java.util.Date;

/**
 * A <code>StatusVariable</code> object represents the value of a status
 * variable taken with a certain collection method at a certain point of time.
 * The type of the <code>StatusVariable</code> can be <code>long</code>,
 * <code>double</code>, <code>boolean</code> or <code>String</code>.
 * <p>
 * A <code>StatusVariable</code> is identified by an ID string that is unique
 * within the scope of a <code>Monitorable</code>. The ID must be a non-
 * <code>null</code>, non-empty string that does not contain the Reserved
 * characters described in 2.2 of RFC-2396 (URI Generic Syntax). As the ID is
 * used as a node name in the DMT, the restrictions on node names must also be
 * observed.
 */
public final class StatusVariable {
    //----- Public constants -----//
    /**
     * Constant for identifying <code>long</code> data type.
     */
    public static final int    TYPE_LONG   = 0;

    /**
     * Constant for identifying <code>double</code> data type.
     */
    public static final int    TYPE_DOUBLE = 1;

    /**
     * Constant for identifying <code>string</code> data type.
     */
    public static final int    TYPE_STRING = 2;

    /**
     * Constant for identifying <code>boolean</code> data type.
     */
   public static final int    TYPE_BOOLEAN = 3;

    /**
     * Constant for identifying 'Cumulative Counter' data collection method. 
     */
    public static final int    CM_CC        = 0;

    /**
     * Constant for identifying 'Discrete Event Registration' data collection
     * method.
     */
    public static final int    CM_DER       = 1;

    /**
     * Constant for identifying 'Gauge' data collection method. 
     */
    public static final int    CM_GAUGE     = 2;

    /**
     * Constant for identifying 'Status Inspection' data collection method.
     */
    public static final int    CM_SI        = 3;

    //----- Private constants -----//

    private static final String URI_CHARACTERS =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" +
        "-_.!~*'()";   // ";:@&=+$," not allowed by Monitoring RFC 

    //----- Private fields -----//
    private String  id;
    private Date    timeStamp;
    private int     cm;
    private int     type;

    private long    longData;
    private double  doubleData;
    private String  stringData;
    private boolean booleanData;

    //----- Constructors -----//
    /**
     * Constructor for a <code>StatusVariable</code> of <code>long</code>
     * type.
     * 
     * @param id the identifier of the <code>StatusVariable</code>
     * @param cm the collection method, one of the <code>CM_</code> constants
     * @param data the <code>long</code> value of the
     *        <code>StatusVariable</code>
     * @throws java.lang.IllegalArgumentException if the given <code>id</code>
     *         is not a valid <code>StatusVariable</code> name, or if 
     *         <code>cm</code> is not one of the collection method constants
     * @throws java.lang.NullPointerException if the <code>id</code>
     *         parameter is <code>null</code>
     */
    public StatusVariable(String id, int cm, long data) {
        setCommon(id, cm);
        type = TYPE_LONG;
        longData = data;
    }

    /**
     * Constructor for a <code>StatusVariable</code> of <code>double</code>
     * type.
     * 
     * @param id the identifier of the <code>StatusVariable</code>
     * @param cm the collection method, one of the <code>CM_</code> constants
     * @param data The <code>double</code> value of the
     *        <code>StatusVariable</code>
     * @throws java.lang.IllegalArgumentException if the given <code>id</code>
     *         is not a valid <code>StatusVariable</code> name, or if 
     *         <code>cm</code> is not one of the collection method constants
     * @throws java.lang.NullPointerException if the <code>id</code> parameter
     *         is <code>null</code>
     */
    public StatusVariable(String id, int cm, double data) {
        setCommon(id, cm);
        type = TYPE_DOUBLE;
        doubleData = data;
    }

    /**
     * Constructor for a <code>StatusVariable</code> of <code>boolean</code>
     * type.
     * 
     * @param id the identifier of the <code>StatusVariable</code>
     * @param cm the collection method, one of the <code>CM_</code> constants
     * @param data the <code>boolean</code> value of the
     *        <code>StatusVariable</code>
     * @throws java.lang.IllegalArgumentException if the given <code>id</code>
     *         is not a valid <code>StatusVariable</code> name, or if 
     *         <code>cm</code> is not one of the collection method constants
     * @throws java.lang.NullPointerException if the <code>id</code> parameter
     *         is <code>null</code>
     */
    public StatusVariable(String id, int cm, boolean data) {
        setCommon(id, cm);
        type = TYPE_BOOLEAN;
        booleanData = data;
    }

    /**
     * Constructor for a <code>StatusVariable</code> of <code>String</code>
     * type.
     * 
     * @param id the identifier of the <code>StatusVariable</code>
     * @param cm the collection method, one of the <code>CM_</code> constants
     * @param data the <code>String</code> value of the
     *        <code>StatusVariable</code>, can be <code>null</code>
     * @throws java.lang.IllegalArgumentException if the given <code>id</code>
     *         is not a valid <code>StatusVariable</code> name, or if 
     *         <code>cm</code> is not one of the collection method constants
     * @throws java.lang.NullPointerException if the <code>id</code> parameter
     *         is <code>null</code>
     */
    public StatusVariable(String id, int cm, String data) {
        setCommon(id, cm);
        type = TYPE_STRING;
        stringData = data;
    }

    
    // ----- Public methods -----//
    /**
     * Returns the ID of this <code>StatusVariable</code>. The ID is unique 
     * within the scope of a <code>Monitorable</code>.
     * 
     * @return the ID of this <code>StatusVariable</code>
     */
    public String getID() {
        return id;
    }

    /**
     * Returns information on the data type of this <code>StatusVariable</code>.
     * 
     * @return one of the <code>TYPE_</code> constants indicating the type of
     *         this <code>StatusVariable</code>
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the time when the <code>StatusVariable</code> value was
     * queried. The <code>StatusVariable</code>'s timestamp is set when the
     * {@link Monitorable#getStatusVariable Monitorable.getStatusVariable()}
     * method is called.
     * 
     * @return the time when the <code>StatusVariable</code> value was
     *         queried, cannot be <code>null</code>
     * 
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * Returns the <code>StatusVariable</code> value if its type is
     * <code>String</code>.
     * 
     * @return the <code>StatusVariable</code> value as a <code>String</code>
     * @throws java.lang.IllegalStateException if the type of the 
     * <code>StatusVariable</code> is not <code>String</code>
     */
    public String getString() throws IllegalStateException {
        if (type != TYPE_STRING)
            throw new IllegalStateException(
                    "This StatusVariable does not contain a String value.");
        return stringData;
    }

    /**
     * Returns the <code>StatusVariable</code> value if its type is
     * <code>long</code>.
     * 
     * @return the <code>StatusVariable</code> value as a <code>long</code>
     * @throws java.lang.IllegalStateException if the type of this
     *         <code>StatusVariable</code> is not <code>long</code>
     */
    public long getLong() throws IllegalStateException {
        if (type != TYPE_LONG)
            throw new IllegalStateException(
                    "This StatusVariable does not contain a long value.");
        return longData;
    }

    /**
     * Returns the <code>StatusVariable</code> value if its type is
     * <code>double</code>.
     * 
     * @return the <code>StatusVariable</code> value as a <code>double</code>
     * @throws java.lang.IllegalStateException if the type of this
     *         <code>StatusVariable</code> is not <code>double</code>
     */
    public double getDouble() throws IllegalStateException {
        if (type != TYPE_DOUBLE)
            throw new IllegalStateException(
                    "This StatusVariable does not contain a double value.");
        return doubleData;
    }

    /**
     * Returns the <code>StatusVariable</code> value if its type is
     * <code>boolean</code>.
     * 
     * @return the <code>StatusVariable</code> value as a <code>boolean</code>
     * @throws java.lang.IllegalStateException if the type of this
     *         <code>StatusVariable</code> is not <code>boolean</code>
     */
    public boolean getBoolean() throws IllegalStateException {
        if (type != TYPE_BOOLEAN)
            throw new IllegalStateException(
                    "This StatusVariable does not contain a boolean value.");
        return booleanData;
    }
    
    /**
     * Returns the collection method of this <code>StatusVariable</code>. See
     * section 3.3 b) in [ETSI TS 132 403]
     * 
     * @return one of the <code>CM_</code> constants
     */
    public int getCollectionMethod() {
        return cm;
    }

    /**
     * Compares the specified object with this <code>StatusVariable</code>.
     * Two <code>StatusVariable</code> objects are considered equal if their
     * full path, collection method and type are identical, and the data
     * (selected by their type) is equal.
     * 
     * @param obj the object to compare with this <code>StatusVariable</code>
     * @return <code>true</code> if the argument represents the same
     *         <code>StatusVariable</code> as this object
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof StatusVariable))
            return false;
        
        StatusVariable other = (StatusVariable) obj;
        
        if (!equals(id, other.id) || cm != other.cm || type != other.type)
            return false;
        
        switch (type) {
        case TYPE_LONG:    return longData == other.longData;
        case TYPE_DOUBLE:  return doubleData == other.doubleData;
        case TYPE_STRING:  return equals(stringData, other.stringData);
        case TYPE_BOOLEAN: return booleanData == other.booleanData;
        }
        
        return false; // never reached
    }

    /**
     * Returns the hash code value for this <code>StatusVariable</code>. The
     * hash code is calculated based on the full path, collection method and
     * value of the <code>StatusVariable</code>.
     * 
     * @return the hash code of this object
     */
    public int hashCode() {
        int hash = hashCode(id) ^ cm;

        switch (type) {
        case TYPE_LONG:    return hash ^ hashCode(new Long(longData));
        case TYPE_DOUBLE:  return hash ^ hashCode(new Double(doubleData));
        case TYPE_BOOLEAN: return hash ^ hashCode(new Boolean(booleanData));
        case TYPE_STRING:  return hash ^ hashCode(stringData);
        }
        
        return 0; // never reached
    }

    //  String representation: StatusVariable(path, cm, time, type, value)
    /**
     * Returns a <code>String</code> representation of this
     * <code>StatusVariable</code>. The returned <code>String</code>
     * contains the full path, collection method, timestamp, type and value 
     * parameters of the <code>StatusVariable</code> in the following format:
     * <pre>StatusVariable(&lt;path&gt;, &lt;cm&gt;, &lt;timestamp&gt;, &lt;type&gt;, &lt;value&gt;)</pre>
     * 
     * @return the <code>String</code> representation of this
     *         <code>StatusVariable</code>
     */
    public String toString() {
        String cmName = null;
        switch (cm) {
        case CM_CC:    cmName = "CC";    break;
        case CM_DER:   cmName = "DER";   break;
        case CM_GAUGE: cmName = "GAUGE"; break;
        case CM_SI:    cmName = "SI";    break;
        }
        
        String beg = "StatusVariable(" + id + ", " + cmName + ", "
                + timeStamp + ", ";
        
        switch (type) {
        case TYPE_LONG:    return beg + "LONG, " + longData + ")";
        case TYPE_DOUBLE:  return beg + "DOUBLE, " + doubleData + ")";
        case TYPE_STRING:  return beg + "STRING, " + stringData + ")";
        case TYPE_BOOLEAN: return beg + "BOOLEAN, " + booleanData + ")";
        }
        
        return null; // never reached
    }

    //----- Private methods -----//
    
    private void setCommon(String id, int cm)
            throws IllegalArgumentException, NullPointerException {
        checkId(id, "StatusVariable ID");
        
        if (cm != CM_CC && cm != CM_DER && cm != CM_GAUGE && cm != CM_SI)
            throw new IllegalArgumentException(
                    "Unknown data collection method constant '" + cm + "'.");
        
        this.id = id;
        this.cm = cm;
        timeStamp = new Date();
    }

    private void checkId(String id, String idName)
            throws IllegalArgumentException, NullPointerException {
        if (id == null)
            throw new NullPointerException(idName + " is null.");
        if(id.length() == 0)
            throw new IllegalArgumentException(idName + " is empty.");
        if(id.equals(".."))
            throw new IllegalArgumentException(idName + " is invalid.");
        
        if(!containsValidChars(id))
            throw new IllegalArgumentException(idName + 
                    " contains invalid characters.");
    }
    
    private boolean containsValidChars(String name) {
        char[] chars = name.toCharArray();
        int i = 0;
        while(i < chars.length) {
            if(URI_CHARACTERS.indexOf(chars[i]) == -1)
                return false;
            i++;
        }
        
        return true;        
    }
    
    private boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    private int hashCode(Object o) {
        return o == null ? 0 : o.hashCode();
    }
}
