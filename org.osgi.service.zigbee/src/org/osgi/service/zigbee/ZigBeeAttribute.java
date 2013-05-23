package org.osgi.service.zigbee;

import org.osgi.service.zigbee.datatype.ZigBeeDataTypeDescription;
import org.osgi.service.zigbee.description.ZigBeeAttributeDescription;
import org.osgi.service.zigbee.handler.ZigBeeHandler;

/**
 * This interface represents a ZigBee Attribute
 * 
 * @version 1.0
 */
public interface ZigBeeAttribute {
	/**
	 * Property key for the optional attribute id.
	 * A ZigBee Event Listener service can announce for what ZigBee attributes 
	 * it wants notifications.
	 */
	public final static String ID = "zigbee.listener.attribute.id";
	
	/**
	 * Property key for the optional minimum interval, in seconds
	 * between issuing reports of the attribute
	 * A ZigBee Event Listener service can declare the minimum frequency 
	 * at which events it wants notifications.
	 */
	public final static String MIN_REPORT_INTERVAL = "zigbee.listener.attribute.min.report.interval";
	
	/**
	 * Property key for the optional maximum interval, in seconds
	 * between issuing reports of the attribute
	 * A ZigBee Event Listener service can declare the maximum frequency 
	 * at which events it wants notifications.
	 */
	public final static String MAX_REPORT_INTERVAL = "zigbee.listener.attribute.max.report.interval";
	
	/**
	 * Property key for the optional maximum change to the attribute that
	 * will result in a report being issued.
	 * A ZigBee Event Listener service can declare the maximum frequency 
	 * at which events it wants notifications.
	 */
	public final static String REPORTABLE_CHANGE = "zigbee.listener.attribute.reportable.change";
	
	/**
	 * Property key for the optional maximum expected time, in seconds, between 
	 * received reports for the attribute.
	 * A ZigBee Event Listener service can declare the maximum frequency 
	 * at which events it wants notifications.
	 */
	public final static String TIMEOUT_PERIOD = "zigbee.listener.attribute.timeout.period";
	
	
	/**
	 * @return the attribute identifier
	 */
	public int getId();
	
	/**
	 * Gets the current value of the attribute.
	 * This method is used when the attribute data type is not provided
	 * 
	 * @param handler the handler
	 * @return the attribute current value
	 */
	public void getValue(ZigBeeHandler handler) throws ZigBeeException;
	
	/**
	 * Gets the current value of the attribute.
	 * This method is used when the attribute data type is provided
	 * 
	 * @param outputType output data type expected.
	 * @param handler the handler
	 * @return the attribute current value
	 */
	public void getValue(ZigBeeDataTypeDescription outputType, ZigBeeHandler handler) throws ZigBeeException;
	
	/**
	 * Sets the current value of the attribute.
	 * 
	 * @param value the value to set
	 * @param handler the handler
	 */
	public void setValue(Object value, ZigBeeHandler handler) throws ZigBeeException;
	
	/**
	 * Sets the current value of the attribute.
	 * 
	 * @param value the value to set
	 * @param handler the handler
	 */
	public void setValue(byte[] value, ZigBeeHandler handler) throws ZigBeeException;
	
	/**
	 * @return the Attribute data type
	 */
	public ZigBeeDataTypeDescription getDataType();
	
	/**
	 * @return the Attribute description if provided or null if not provided
	 */
	public ZigBeeAttributeDescription getDescription();
}