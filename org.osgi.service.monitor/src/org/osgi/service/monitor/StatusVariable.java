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
package org.osgi.service.monitor;

import java.util.Date;

/**
 * A StatusVariable object represents the value of a status variable taken with
 * a certain collection method at a certain point of time. The type of the
 * StatusVariable can be integer, float or string.
 */
public final class StatusVariable {
    //----- Public constants -----//
    /**
     * StatusVariable type identifying integer data.
     */
    public static final int    TYPE_INTEGER = 0;

    /**
     * StatusVariable type identifying float data.
     */
    public static final int    TYPE_FLOAT   = 1;

    /**
     * StatusVariable type identifying string data.
     */
    public static final int    TYPE_STRING  = 2;
    
    
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
    
    //----- Private fields -----//
    private String  id;
    private String  path;
    private Date    timeStamp;
    private int     cm;
    private int     type;
    
    private int     intData;
    private float   floatData;
    private String  stringData;
    
    //----- Constructors -----//
    /**
     * Constructor for a StatusVariable of integer type.
     * 
     * @param monitorableId The identifier of the monitorable service that this
     *        StatusVariable belongs to
     * @param id The identifier of the StatusVariable
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The integer value of the StatusVariable
     * @throws IllegalArgumentException if any of the ID parameters contains
     *         invalid characters or if <code>cm</code> is not one of the
     *         collection method constants
     * @throws NullPointerException if any of the ID parameters is
     *         <code>null</code>
     */
    public StatusVariable(String monitorableId, String id, int cm,
            int data) {
        setCommon(monitorableId, id, cm);
        type = TYPE_INTEGER;
        intData = data;
    }

    /**
     * Constructor for a StatusVariable of float type.
     * 
     * @param monitorableId The identifier of the monitorable service that this
     *        StatusVariable belongs to
     * @param id The identifier of the StatusVariable
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The float value of the StatusVariable
     * @throws IllegalArgumentException if any of the ID parameters contains
     *         invalid characters or if <code>cm</code> is not one of the
     *         collection method constants
     * @throws NullPointerException if any of the ID parameters is
     *         <code>null</code>
     */
    public StatusVariable(String monitorableId, String id, int cm, float data) {
        setCommon(monitorableId, id, cm);
        type = TYPE_FLOAT;
        floatData = data;
    }

    /**
     * Constructor for a StatusVariable of String type.
     * 
     * @param monitorableId The identifier of the monitorable service that this
     *        StatusVariable belongs to
     * @param id The identifier of the StatusVariable
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The string value of the StatusVariable
     * @throws IllegalArgumentException if any of the ID parameters contains
     *         invalid characters or if <code>cm</code> is not one of the
     *         collection method constants
     * @throws NullPointerException if any of the ID parameters is
     *         <code>null</code>
     */
    public StatusVariable(String monitorableId, String id, int cm,
            String data) {
        setCommon(monitorableId, id, cm);
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
     * Returns the StatusVariable value if its type is integer.
     * 
     * @return the StatusVariable value as an integer
     * @throws java.lang.IllegalStateException if the type of this
     *         StatusVariable is not integer
     */
    public int getInt() throws IllegalStateException {
        if (type != TYPE_INTEGER)
            throw new IllegalStateException(
                    "This StatusVariable does not contain an integer value.");
        return intData;
    }

    /**
     * Returns the StatusVariable value if its type is float.
     * 
     * @return the StatusVariable value as a float
     * @throws java.lang.IllegalStateException if the type of this
     *         StatusVariable is not float
     */
    public float getFloat() throws IllegalStateException {
        if (type != TYPE_FLOAT)
            throw new IllegalStateException(
                    "This StatusVariable does not contain a float value.");
        return floatData;
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
        
        if (!equals(path, other.path)
                || cm != other.cm || type != other.type)
            return false;
        
        switch (type) {
        case TYPE_INTEGER :
            return intData == other.intData;
        case TYPE_FLOAT :
            return floatData == other.floatData;
        case TYPE_STRING :
            return equals(stringData, other.stringData);
        }
        
        return false; // never reached
    }

    /**
     * Returns the hash code value for this StatusVariable. The hash code is
     * calculated based on the full path, collection method and value of the
     * StatusVariable.
     */
    public int hashCode() {
        int hash = hashCode(path) ^ cm;

        switch (type) {
        case TYPE_INTEGER :
            return hash ^ hashCode(new Integer(intData));
        case TYPE_FLOAT :
            return hash ^ hashCode(new Float(floatData));
        case TYPE_STRING :
            return hash ^ hashCode(stringData);
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
            case CM_CC :
                cmName = "CC";
                break;
            case CM_DER :
                cmName = "DER";
                break;
            case CM_GAUGE :
                cmName = "GAUGE";
                break;
            case CM_SI :
                cmName = "SI";
                break;
        }
        
        String beg = "StatusVariable(" + path + ", " + cmName + ", "
                + timeStamp + ", ";
        switch (type) {
            case TYPE_INTEGER :
                return beg + "INTEGER, " + intData + ")";
            case TYPE_FLOAT :
                return beg + "FLOAT, " + floatData + ")";
            case TYPE_STRING :
                return beg + "STRING, " + stringData + ")";
        }
        
        return null; // never reached
    }

    //----- Private methods -----//
    
    private void setCommon(String monitorableId, String id, int cm)
            throws IllegalArgumentException, NullPointerException {
        checkId(id, "StatusVariable ID");
        checkId(monitorableId, "Monitorable ID");
        
        if (cm != CM_CC && cm != CM_DER && cm != CM_GAUGE && cm != CM_SI)
            throw new IllegalArgumentException(
                    "Unknown data collection method constant '" + cm + "'.");
        
        this.id = id;
        this.path = monitorableId + '/' + id;
        this.cm = cm;
        timeStamp = new Date();
    }

    private void checkId(String id, String idName)
            throws IllegalArgumentException, NullPointerException {
        if (id == null)
            throw new NullPointerException(idName + " is null.");
        
        // TODO check that ID is a valid URI element (that it does not cause problems in the DM tree)
        if (id.indexOf('/') != -1)
            throw new IllegalArgumentException("Invalid character '/' in "
                    + idName + ".");
    }

    private boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    private int hashCode(Object o) {
        return o == null ? 0 : o.hashCode();
    }
}
