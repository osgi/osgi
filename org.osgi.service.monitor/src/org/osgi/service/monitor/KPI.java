package org.osgi.service.monitor;

import java.util.Date;
import java.lang.UnsupportedOperationException;

/**
 * A KPI object represents the value of a status variable taken 
 * with a certain collection method at a certain
 * point of time. The type of the KPI can be integer, float or String. 
 * Additionally Object type is supported which can hold any type supported
 * by the OSGi Configuration Admin service. KPI objects are immutable.
 */
public class KPI {
	public static final int TYPE_INTEGER = 0;
	public static final int TYPE_FLOAT   = 1;
	public static final int TYPE_STRING  = 2;
	public static final int TYPE_OBJECT  = 3;
	
	public static final int CM_CC    = 0;
	public static final int CM_DER   = 1;
	public static final int CM_GAUGE = 2;
	public static final int CM_SI    = 3;


	/**
	 * Constructor for a KPI of integer type
	 * @param cm Collection method, should be one of the CM_ constants
	 * @param data The integer value of the KPI
	 */
	public KPI(int cm, int data) {}
	
	/**
	 * Constructor for a KPI of float type
	 * @param cm Collection method, should be one of the CM_ constants
	 * @param data The float value of the KPI
	 */
	public KPI(int cm, float data) {}
	
	/**
	 * Constructor for a KPI of String type
	 * @param cm Collection method, should be one of the CM_ constants
	 * @param data The string value of the KPI
	 */
	public KPI(int cm, String data) {}
	
	/**
	 * Constructor for a KPI of Object type
	 * @param cm Collection method, should be one of the CM_ constants
	 * @param data The value of the KPI. The type of the object should 
	 * be supported by the Configuration Admin
	 */
	public KPI(int cm, Object data) {}
	
	/**
	 * Returns the name of this KPI.
	 * A KPI name is unique within the scope of a Monitorable. 
	 * A KPI names must not contain a '/' character.
	 * @return the name of this KPI
	 */
	public String getID() {
		return null;
	}
	
	/**
	 * Returns a human readable description of this KPI.
	 * This can be used by management systems on their GUI. Null return
	 * value is allowed. 
	 * @return the human readable description of this KPI or null if it is
	 * not set
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * Returns information on the data type of this KPI
	 * @return one value of the set of type constants
	 */
	public int getType() {
		return 0;
	}
	
	/**
	 * Returns the time when the KPI value was queried.
	 * The KPI's value is set when the getKpi() or getKpis() methods are
	 * called on the Monitorable object.
	 * @return the time when the KPI value was queried
	 */
	public Date getTimeStamp() {
		return null;
	}
	
	/**
	 * Returns the KPI value if it's type is string.
	 * @return the KPI value as a string
	 * @throws java.lang.UnsupportedOperationException if the type of KPI is
	 * not string
	 */
	public String getString() throws UnsupportedOperationException {
		return null;
	}
	
	/**
	 * @return the KPI value as an integer
	 * @throws java.lang.UnsupportedOperationException
	 */
	public int getInteger() throws UnsupportedOperationException {
		return 0;
	}
	
	/**
	 * @return the KPI value as a float
	 * @throws java.lang.UnsupportedOperationException
	 */
	public float getFloat() throws UnsupportedOperationException {
		return 0;
	}
	
	/**
	 * Returns the KPI value if it's type is Object.
	 * All types supported by the OSGi Configuration Admin service can be used.
	 * Exact type information can be queried using getClass().getName() 
	 * @return the KPI value as an Object 
	 * @throws java.lang.UnsupportedOperationException if the type of KPI is 
	 * not Object 
	 */
	public Object getObject() throws UnsupportedOperationException {
		return null;
	}
	
	/**
	 * Returns the collection method of this KPI
	 * See section 3.3 b) in [ETSI TS 132 403]
	 * @return one of the CM_ constants
	 */ 
	public int getCollectionMethod() {
		return 0;
	}
}
