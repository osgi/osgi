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

import java.lang.reflect.Array;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

/**
 * A KPI object represents the value of a status variable taken with a certain
 * collection method at a certain point of time. The type of the KPI can be
 * integer, float or String. Additionally Object type is supported which can
 * hold any type supported by the OSGi Configuration Admin service. KPI objects
 * are immutable.
 */
public class KPI {
    //----- Public constants -----//
    /**
     * KPI type identifying integer data.
     */
    public static final int    TYPE_INTEGER = 0;

    /**
     * KPI type identifying float data.
     */
    public static final int    TYPE_FLOAT   = 1;

    /**
     * KPI type identifying string data.
     */
    public static final int    TYPE_STRING  = 2;
    
    /**
     * KPI type identifying Object data.
     */
    public static final int    TYPE_OBJECT  = 3;
    
    
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
    private String  description;
    private Date    timeStamp;
    private int     cm;
    private int     type;
    
    private int     intData;
    private float   floatData;
    private String  stringData;
    private Object  objectData;

    //----- Constructors -----//
    /**
     * Constructor for a KPI of integer type.
     * 
     * @param monitorableId The identifier of the monitorable service that this
     *        KPI belongs to
     * @param id The identifier of the KPI
     * @param description The human-readable description of the KPI
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The integer value of the KPI
     * @throws IllegalArgumentException if any of the ID parameters contains
     *         invalid characters or if <code>cm</code> is not one of the
     *         collection method constants
     * @throws NullPointerException if any of the ID parameters is
     *         <code>null</code>
     */
    public KPI(String monitorableId, String id, String description, int cm,
            int data) {
        setCommon(monitorableId, id, description, cm);
        type = TYPE_INTEGER;
        intData = data;
    }

    /**
     * Constructor for a KPI of float type.
     * 
     * @param monitorableId The identifier of the monitorable service that this
     *        KPI belongs to
     * @param id The identifier of the KPI
     * @param description The human-readable description of the KPI
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The float value of the KPI
     * @throws IllegalArgumentException if any of the ID parameters contains
     *         invalid characters or if <code>cm</code> is not one of the
     *         collection method constants
     * @throws NullPointerException if any of the ID parameters is
     *         <code>null</code>
     */
    public KPI(String monitorableId, String id, String description, int cm,
            float data) {
        setCommon(monitorableId, id, description, cm);
        type = TYPE_FLOAT;
        floatData = data;
    }

    /**
     * Constructor for a KPI of String type.
     * 
     * @param monitorableId The identifier of the monitorable service that this
     *        KPI belongs to
     * @param id The identifier of the KPI
     * @param description The human-readable description of the KPI
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The string value of the KPI
     * @throws IllegalArgumentException if any of the ID parameters contains
     *         invalid characters or if <code>cm</code> is not one of the
     *         collection method constants
     * @throws NullPointerException if any of the ID parameters is
     *         <code>null</code>
     */
    public KPI(String monitorableId, String id, String description, int cm,
            String data) {
        setCommon(monitorableId, id, description, cm);
        type = TYPE_STRING;
        stringData = data;
    }

    /**
	 * Constructor for a KPI of Object type. The type of the object should be
	 * supported by the Configuration Admin. In case of arrays and vectors all
	 * elements of the collection must be of the same dynamic type. For arrays,
	 * this must also be the same as the component type of the array itself.
	 * E.g. an array of <code>Object</code>s is not allowed (even if it only
	 * contains <code>Integer</code> instances), but an array of 
	 * <code>Integer</code>s is.
	 * 
	 * @param monitorableId The identifier of the monitorable service that this
	 *        KPI belongs to
	 * @param id The identifier of the KPI
	 * @param description The human-readable description of the KPI
	 * @param cm Collection method, should be one of the CM_ constants
	 * @param data The value of the KPI
	 * @throws IllegalArgumentException if any of the ID parameters contains
	 *         invalid characters or if <code>cm</code> is not one of the
	 *         collection method constants
	 * @throws NullPointerException if any of the ID parameters is
	 *         <code>null</code>
	 */
    public KPI(String monitorableId, String id, String description, int cm,
            Object data) {
        checkObjectData(data);
        setCommon(monitorableId, id, description, cm);
        type = TYPE_OBJECT;
        objectData = data;
    }

    // ----- Public methods -----//
    /**
     * Returns the name of this KPI. A KPI name is unique within the scope of a
     * Monitorable. A KPI name must not contain the Reserved characters
     * described in 2.2 of RFC-2396 (URI Generic Syntax).
     * 
     * @return the name of this KPI
     */
    public String getID() {
        return id;
    }

    /**
     * Returns the path (long name) of this KPI. The path of the KPI is created
     * from the ID of the Monitorable service it belongs to and the ID of the
     * KPI in the following form: <code>[Monitorable_id]/[kpi_id]</code>.
     * 
     * @return the name of this KPI
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns a human readable description of this KPI. This can be used by
     * management systems on their GUI. Null return value is allowed.
     * 
     * @return the human readable description of this KPI or null if it is not
     *         set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns information on the data type of this KPI.
     * 
     * @return one value of the set of type constants
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the time when the KPI value was queried. The KPI's value is set
     * when the getKpi() or getKpis() methods are called on the Monitorable
     * object.
     * 
     * @return the time when the KPI value was queried
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * Returns the KPI value if its type is String.
     * 
     * @return the KPI value as a string
     * @throws java.lang.IllegalStateException if the type of the KPI is not
     *         String
     */
    public String getString() throws IllegalStateException {
        if (type != TYPE_STRING)
            throw new IllegalStateException(
                    "This KPI does not contain a String value.");
        return stringData;
    }

    /**
     * Returns the KPI value if its type is integer.
     * 
     * @return the KPI value as an integer
     * @throws java.lang.IllegalStateException if the type of this KPI is not
     *         integer
     */
    public int getInteger() throws IllegalStateException {
        if (type != TYPE_INTEGER)
            throw new IllegalStateException(
                    "This KPI does not contain an integer value.");
        return intData;
    }

    /**
     * Returns the KPI value if its type is float.
     * 
     * @return the KPI value as a float
     * @throws java.lang.IllegalStateException if the type of this KPI is not
     *         float
     */
    public float getFloat() throws IllegalStateException {
        if (type != TYPE_FLOAT)
            throw new IllegalStateException(
                    "This KPI does not contain a float value.");
        return floatData;
    }

    /**
     * Returns the KPI value if its type is Object. All types supported by the
     * OSGi Configuration Admin service can be used. Exact type information can
     * be queried using <code>getClass().getName()</code>.
     * 
     * @return the KPI value as an Object
     * @throws java.lang.IllegalStateException if the type of this KPI is not
     *         Object
     */
    public Object getObject() throws IllegalStateException {
        if (type != TYPE_OBJECT)
            throw new IllegalStateException(
                    "This KPI does not contain an Object type value.");
        return objectData;
    }

    /**
     * Returns the collection method of this KPI. See section 3.3 b) in [ETSI TS
     * 132 403]
     * 
     * @return one of the CM_ constants
     */
    public int getCollectionMethod() {
        return cm;
    }

    /**
     * Compares the specified object with this KPI. Two KPI objects are
     * considered equal if their full path, description (if any), collection
     * method and type are identical, and the data (selected by their type) is
     * equal.
     * 
     * @param obj the object to compare with this KPI
     * @return <code>true</code> if the argument represents the same KPI as
     *         this object
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof KPI))
            return false;
        
        KPI other = (KPI) obj;
        
        if (!equals(path, other.path)
                || !equals(description, other.description) || cm != other.cm
                || type != other.type)
            return false;
        
        switch (type) {
            case TYPE_INTEGER :
                return intData == other.intData;
            case TYPE_FLOAT :
                return floatData == other.floatData;
            case TYPE_STRING :
                return equals(stringData, other.stringData);
            case TYPE_OBJECT :
                return equals(objectData, other.objectData);
        }
        
        return false; // never reached
    }

    /**
     * Returns the hash code value for this KPI. The hash code is calculated
     * based on the full path, description (if any), collection method and value
     * data of the KPI.
     */
    public int hashCode() {
        int hash = hashCode(path) ^ hashCode(description) ^ cm;

        switch (type) {
            case TYPE_INTEGER :
                return hash ^ hashCode(new Integer(intData));
            case TYPE_FLOAT :
                return hash ^ hashCode(new Float(floatData));
            case TYPE_STRING :
                return hash ^ hashCode(stringData);
            case TYPE_OBJECT :
                return hash ^ hashCode(objectData);
        }
        
        return 0; // never reached
    }

    //  String representation: KPI(path, cm, [description, ]time, type, value)
    /**
     * Returns a string representation of this KPI. The returned string contains
     * the full path, the collection method, the exact time of creation, the
     * description (if any), the type and the value of the KPI in the following
     * format:
     * <code>KPI(&lt;path&gt;, &lt;cm&gt;, [&lt;description&gt;, ]&lt;timestamp&gt;, &lt;type&gt;, &lt;value&gt;)</code>
     * 
     * @return the string representation of this KPI
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
        
        String beg = "KPI(" + path + ", " + cmName + ", "
                + (description == null ? "" : "\"" + description + "\", ")
                + timeStamp + ", ";
        switch (type) {
            case TYPE_INTEGER :
                return beg + "INTEGER, " + intData + ")";
            case TYPE_FLOAT :
                return beg + "FLOAT, " + floatData + ")";
            case TYPE_STRING :
                return beg + "STRING, " + stringData + ")";
            case TYPE_OBJECT :
                return beg + "OBJECT, " + objectData + ")";
        }
        
        return null; // never reached
    }

    //----- Private methods -----//
    
    private void setCommon(String monitorableId, String id, String description,
            int cm) throws IllegalArgumentException, NullPointerException {
        checkId(id, "KPI ID");
        checkId(monitorableId, "Monitorable ID");
        if (cm != CM_CC && cm != CM_DER && cm != CM_GAUGE && cm != CM_SI)
            throw new IllegalArgumentException(
                    "Unknown data collection method constant '" + cm + "'.");
        this.id = id;
        this.path = monitorableId + '/' + id;
        this.description = description;
        this.cm = cm;
        timeStamp = new Date();
    }

    // Nested arrays/vectors, mixed type vectors and null elements not allowed,
	// these will not be supported by the Configuration Admin in Rel. 4
	private void checkObjectData(Object data) throws IllegalArgumentException {
		if (data == null)
			throw new NullPointerException(
					"'null' data given in Object KPI constructor.");
        
		if (data.getClass().isArray()) {
			checkScalarType(data.getClass().getComponentType());
			for (int i = 0; i < Array.getLength(data); i++)
				if (Array.get(data, i) == null)
					throw new IllegalArgumentException(
							"Null elements not allowed in KPI arrays.");
		}
		else if (data instanceof Vector) {
			Class last = null;
			for (Iterator i = ((Vector) data).iterator(); i.hasNext();) {
				Object o = i.next();
				if (o == null)
					throw new IllegalArgumentException(
							"Null elements not allowed in KPI vectors.");
				Class type = o.getClass();
				checkScalarType(type);
				if (last != null && !type.equals(last))
					throw new IllegalArgumentException(
							"Mixed types not allowed in KPI vectors.");
				last = type;
			}
		}
		else
			checkScalarType(data.getClass());
	}

    private void checkId(String id, String idName)
            throws IllegalArgumentException, NullPointerException {
        if (id == null)
            throw new NullPointerException(idName + " is null.");
        
        // TODO check that ID is a valid URI element (that it does not cause
        // problems in the DM tree)
        if (id.indexOf('/') != -1)
            throw new IllegalArgumentException("Invalid character '/' in "
                    + idName + ".");
    }

    private void checkScalarType(Class c) throws IllegalArgumentException {
        if (!c.equals(String.class) && !c.equals(Long.class)
                && !c.equals(Integer.class) && !c.equals(Short.class)
                && !c.equals(Character.class) && !c.equals(Byte.class)
                && !c.equals(Double.class) && !c.equals(Float.class)
                && !c.equals(Boolean.class))
            throw new IllegalArgumentException(
                    "Unsupported data type in Object KPI constructor.");
    }
    
    private boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    private int hashCode(Object o) {
        return o == null ? 0 : o.hashCode();
    }
}
