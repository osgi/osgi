package org.osgi.service.monitor;

import java.util.Date;

/**
 * A KPI object represents the value of a status variable taken with a
 * certain collection method at a certain point of time. The type of
 * the KPI can be integer, float or String.  Additionally Object type
 * is supported which can hold any type supported by the OSGi
 * Configuration Admin service. KPI objects are immutable.
 */
public class KPI {
    //----- Public constants -----//

    public static final int TYPE_INTEGER = 0;
    public static final int TYPE_FLOAT   = 1;
    public static final int TYPE_STRING  = 2;
    public static final int TYPE_OBJECT  = 3;
        
    public static final int CM_CC    = 0;
    public static final int CM_DER   = 1;
    public static final int CM_GAUGE = 2;
    public static final int CM_SI    = 3;


    //----- Private fields -----//

    private String id;
    private String description;
    private Date timeStamp;
    private int cm;
    private int type;

    private int intData;
    private float floatData;
    private String stringData;
    private Object objectData;


    //----- Constructors -----//

    /**
     * Constructor for a KPI of integer type.
     * @param id The identifier of the KPI
     * @param description The human-readable description of the KPI
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The integer value of the KPI
     * @throws IllegalArgumentException if <code>id</code> contains
     * invalid characters or if <code>cm</code> is not one of the
     * collection method constants
     * @throws NullPointerException if <code>id</code> is <code>null</code>
     */
    public KPI(String id, String description, int cm, int data) {
        setCommon(id, description, cm);
        type = TYPE_INTEGER;
        intData = data;
    }
        
    /**
     * Constructor for a KPI of float type.
     * @param id The identifier of the KPI
     * @param description The human-readable description of the KPI
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The float value of the KPI
     * @throws IllegalArgumentException if <code>id</code> contains
     * invalid characters or if <code>cm</code> is not one of the
     * collection method constants
     * @throws NullPointerException if <code>id</code> is <code>null</code>
     */
    public KPI(String id, String description, int cm, float data) {
        setCommon(id, description, cm);
        type = TYPE_FLOAT;
        floatData = data;
    }
        
    /**
     * Constructor for a KPI of String type.
     * @param id The identifier of the KPI
     * @param description The human-readable description of the KPI
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The string value of the KPI
     * @throws IllegalArgumentException if <code>id</code> contains
     * invalid characters or if <code>cm</code> is not one of the
     * collection method constants
     * @throws NullPointerException if <code>id</code> is <code>null</code>
     */
    public KPI(String id, String description, int cm, String data) {
        setCommon(id, description, cm);
        type = TYPE_STRING;
        stringData = data;
    }
        
    /**
     * Constructor for a KPI of Object type.
     * @param id The identifier of the KPI
     * @param description The human-readable description of the KPI
     * @param cm Collection method, should be one of the CM_ constants
     * @param data The value of the KPI. The type of the object should
     * be supported by the Configuration Admin
     * @throws IllegalArgumentException if <code>id</code> contains
     * invalid characters or if <code>cm</code> is not one of the
     * collection method constants
     * @throws NullPointerException if <code>id</code> is <code>null</code>
     */
    public KPI(String id, String description, int cm, Object data) {
        setCommon(id, description, cm);
        type = TYPE_OBJECT;
        objectData = data;

        // TODO check that 'data' is really of a supported type
    }


    //----- Public methods -----//

    /**
     * Returns the name of this KPI.  A KPI name is unique within the
     * scope of a Monitorable.  A KPI names must not contain a '/'
     * character.
     * @return the name of this KPI
     */
    public String getID() {
        return id;
    }
        
    /**
     * Returns a human readable description of this KPI.  This can be
     * used by management systems on their GUI. Null return value is
     * allowed.
     * @return the human readable description of this KPI or null if
     * it is not set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns information on the data type of this KPI.
     * @return one value of the set of type constants
     */
    public int getType() {
        return type;
    }
        
    /**
     * Returns the time when the KPI value was queried.  The KPI's
     * value is set when the getKpi() or getKpis() methods are called
     * on the Monitorable object.
     * @return the time when the KPI value was queried
     */
    public Date getTimeStamp() {
        return timeStamp;
    }
        
    /**
     * Returns the KPI value if its type is String.
     * @return the KPI value as a string
     * @throws java.lang.IllegalStateException if the type of the KPI
     * is not String
     */
    public String getString() throws IllegalStateException {
        if(type != TYPE_STRING)
            throw new IllegalStateException(
                "This KPI does not contain a String value.");

        return stringData;
    }
        
    /**
     * Returns the KPI value if its type is integer.
     * @return the KPI value as an integer
     * @throws java.lang.IllegalStateException if the type of this KPI
     * is not integer
     */
    public int getInteger() throws IllegalStateException {
        if(type != TYPE_INTEGER)
            throw new IllegalStateException(
                "This KPI does not contain an integer value.");

        return intData;
    }
        
    /**
     * Returns the KPI value if its type is float.
     * @return the KPI value as a float
     * @throws java.lang.IllegalStateException if the type of this KPI
     * is not float
     */
    public float getFloat() throws IllegalStateException {
        if(type != TYPE_FLOAT)
            throw new IllegalStateException(
                "This KPI does not contain a float value.");

        return floatData;
    }
        
    /**
     * Returns the KPI value if its type is Object.  All types
     * supported by the OSGi Configuration Admin service can be used.
     * Exact type information can be queried using
     * <code>getClass().getName()</code>.
     * @return the KPI value as an Object
     * @throws java.lang.IllegalStateException if the type of this KPI
     * is not Object
     */
    public Object getObject() throws IllegalStateException {
        if(type != TYPE_OBJECT)
            throw new IllegalStateException(
                "This KPI does not contain an Object type value.");

        return objectData;
    }
        
    /**
     * Returns the collection method of this KPI.  See section 3.3 b)
     * in [ETSI TS 132 403]
     * @return one of the CM_ constants
     */ 
    public int getCollectionMethod() {
        return cm;
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof KPI))
            return false;

        KPI other = (KPI) obj;

        if(!equals(id, other.id) || !equals(description, other.description) ||
           !equals(timeStamp, other.timeStamp) || cm != other.cm || 
           type != other.type)
            return false;

        switch(type) {
        case TYPE_INTEGER: return intData == other.intData;
        case TYPE_FLOAT:   return floatData == other.floatData;
        case TYPE_STRING:  return equals(stringData, other.stringData);
        case TYPE_OBJECT:  return equals(objectData, other.objectData);
        }

        return false;           // never reached
    }

    public int hashCode() {
        int hash = 
            hashCode(id) + hashCode(description) + hashCode(timeStamp) + cm;

        switch(type) {
        case TYPE_INTEGER: return hash + hashCode(new Integer(intData));
        case TYPE_FLOAT:   return hash + hashCode(new Float(floatData));
        case TYPE_STRING:  return hash + hashCode(stringData);
        case TYPE_OBJECT:  return hash + hashCode(objectData);
        }
        
        return 0;               // never reached
    }

    //----- Private methods -----//

    private void setCommon(String id, String description, int cm) {
        // TODO check that ID is a valid URI element (that it does not cause problems in the DM tree)
        if(id.indexOf('/') != -1)
            throw new IllegalArgumentException("Invalid character '/' in KPI ID.");
        
        if(cm != CM_CC && cm != CM_DER && cm != CM_GAUGE && cm != CM_SI)
            throw new IllegalArgumentException(
                "Unknown data collection method constant '" + cm + "'.");

        this.id = id;
        this.description = description;
        this.cm = cm;
        
        timeStamp = new Date();
    }

    private boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    private int hashCode(Object o) {
        return o == null ? 0 : o.hashCode();
    }
}
