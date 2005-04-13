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
package org.osgi.service.monitor;

import java.util.Date;

/**
 * A StatusVariable object represents the value of a status variable taken with
 * a certain collection method at a certain point of time. The type of the
 * StatusVariable can be long, double, boolean or string.
 */
public final class StatusVariable {
    //----- Public constants -----//
    /**
     * StatusVariable type identifying long data.
     */
    public static final int    TYPE_LONG   = 0;

    /**
     * StatusVariable type identifying double data.
     */
    public static final int    TYPE_DOUBLE = 1;

    /**
     * StatusVariable type identifying string data.
     */
    public static final int    TYPE_STRING = 2;
    
    /**
     * StatusVariable type identifying string data.
     */
   public static final int    TYPE_BOOLEAN = 3;
    
    /**
     * Collection method type identifying 'Cumulative Counter' data collection. 
     */
    public static final int    CM_CC        = 0;

    /**
	 * Collection method type identifying 'Discrete Event Registration' data
	 * collection.
	 */
    public static final int    CM_DER       = 1;
    
    /**
     * Collection method type identifying 'Gauge' data collection. 
     */
    public static final int    CM_GAUGE     = 2;
    
    /**
	 * Collection method type identifying 'Status Inspection' data collection.
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
    
    private long     longData;
    private double   doubleData;
    private String  stringData;
    private boolean booleanData;
    
    //----- Constructors -----//
    /**
     * Constructor for a StatusVariable of long type.
     * 
     * @param id The identifier of the StatusVariable
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The long value of the StatusVariable
     * @throws IllegalArgumentException if the id parameter is empty or contains
     *         invalid characters, or if <code>cm</code> is not one of the
     *         collection method constants
     * @throws NullPointerException if the id parameter is <code>null</code>
     */
    public StatusVariable(String id, int cm, long data) {
        setCommon(id, cm);
        type = TYPE_LONG;
        longData = data;
    }

    /**
     * Constructor for a StatusVariable of double type.
     * 
     * @param id The identifier of the StatusVariable
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The double value of the StatusVariable
     * @throws IllegalArgumentException if the id parameter is empty or contains
     *         invalid characters, or if <code>cm</code> is not one of the
     *         collection method constants
     * @throws NullPointerException if the id parameter is <code>null</code>
     */
    public StatusVariable(String id, int cm, double data) {
        setCommon(id, cm);
        type = TYPE_DOUBLE;
        doubleData = data;
    }

    /**
     * Constructor for a StatusVariable of boolean type.
     * 
     * @param id The identifier of the StatusVariable
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The boolean value of the StatusVariable
     * @throws IllegalArgumentException if the id parameter is empty or contains
     *         invalid characters, or if <code>cm</code> is not one of the
     *         collection method constants
     * @throws NullPointerException if the id parameter is <code>null</code>
     */
    public StatusVariable(String id, int cm, boolean data) {
        setCommon(id, cm);
        type = TYPE_BOOLEAN;
        booleanData = data;
    }

    /**
     * Constructor for a StatusVariable of String type.
     * 
     * @param id The identifier of the StatusVariable
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The string value of the StatusVariable
     * @throws IllegalArgumentException if the id parameter is empty or contains
     *         invalid characters, or if <code>cm</code> is not one of the
     *         collection method constants
     * @throws NullPointerException if the id parameter is <code>null</code>
     */
    public StatusVariable(String id, int cm, String data) {
        setCommon(id, cm);
        type = TYPE_STRING;
        stringData = data;
    }

    
    // ----- Public methods -----//
    /**
     * Returns the name of this StatusVariable. A StatusVariable name is unique
     * within the scope of a Monitorable. A StatusVariable name must not contain
     * the Reserved characters described in 2.2 of RFC-2396 (URI Generic
     * Syntax).
     * 
     * @return the name of this StatusVariable
     */
    public String getID() {
        return id;
    }

    /**
     * Returns information on the data type of this StatusVariable.
     * 
     * @return one value of the set of type constants
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the time when the StatusVariable value was queried. The
     * StatusVariable's value is set when the getStatusVariable() or
     * getStatusVariables() methods are called on the Monitorable object.
     * 
     * @return the time when the StatusVariable value was queried
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * Returns the StatusVariable value if its type is String.
     * 
     * @return the StatusVariable value as a string
     * @throws java.lang.IllegalStateException if the type of the StatusVariable
     *         is not String
     */
    public String getString() throws IllegalStateException {
        if (type != TYPE_STRING)
            throw new IllegalStateException(
                    "This StatusVariable does not contain a String value.");
        return stringData;
    }

    /**
     * Returns the StatusVariable value if its type is long.
     * 
     * @return the StatusVariable value as an long
     * @throws java.lang.IllegalStateException if the type of this
     *         StatusVariable is not long
     */
    public long getLong() throws IllegalStateException {
        if (type != TYPE_LONG)
            throw new IllegalStateException(
                    "This StatusVariable does not contain a long value.");
        return longData;
    }

    /**
     * Returns the StatusVariable value if its type is double.
     * 
     * @return the StatusVariable value as a double
     * @throws java.lang.IllegalStateException if the type of this
     *         StatusVariable is not double
     */
    public double getDouble() throws IllegalStateException {
        if (type != TYPE_DOUBLE)
            throw new IllegalStateException(
                    "This StatusVariable does not contain a double value.");
        return doubleData;
    }

    /**
     * Returns the StatusVariable value if its type is boolean.
     * 
     * @return the StatusVariable value as a boolean
     * @throws java.lang.IllegalStateException if the type of this
     *         StatusVariable is not double
     */
    public boolean getBoolean() throws IllegalStateException {
        if (type != TYPE_BOOLEAN)
            throw new IllegalStateException(
                    "This StatusVariable does not contain a boolean value.");
        return booleanData;
    }
    
    /**
     * Returns the collection method of this StatusVariable. See section 3.3 b)
     * in [ETSI TS 132 403]
     * 
     * @return one of the <code>CM_</code> constants
     */
    public int getCollectionMethod() {
        return cm;
    }

    /**
     * Compares the specified object with this StatusVariable. Two
     * StatusVariable objects are considered equal if their full path,
     * collection method and type are identical, and the data (selected by their
     * type) is equal.
     * 
     * @param obj the object to compare with this StatusVariable
     * @return <code>true</code> if the argument represents the same
     *         StatusVariable as this object
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
     * Returns the hash code value for this StatusVariable. The hash code is
     * calculated based on the full path, collection method and value of the
     * StatusVariable.
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
     * Returns a string representation of this StatusVariable. The returned
     * string contains the full path, the collection method, the exact time of
     * creation, the type and the value of the StatusVariable in the following
     * format:
     * <code>StatusVariable(&lt;path&gt;, &lt;cm&gt;, &lt;timestamp&gt;, &lt;type&gt;, &lt;value&gt;)</code>
     * 
     * @return the string representation of this StatusVariable
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
            if(URI_CHARACTERS.indexOf(chars[i]) != -1)
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
