package org.osgi.service.zigbee;

/**
 * This interface represents a listener to events from ZigBee Device nodes 
 *  
 * @version 1.0
 */
public interface ZigBeeEventListener {
	/**
	 * key for a service property having a value that is an object 
	 * of type org.osgi.framework.Filter and that is used  to limit 
	 * received events.
	 */
	public static final String ZIGBEE_FILTER = "zigbee.filter";
	
	/**
	 * Key of {@link String} containing the listener targeted network PAN ID
	 */
	public final String PAN_ID_TARGET = "zigbee.listener.target.pan.id";
	
	/**
	 *  Key of {@link String} containing the listener targeted network extended PAN ID
	 */
	public final String EXTENDED_PAN_ID_TARGET = "zigbee.listener.target.extended.pan.id";
	
	/**
	 * Callback method that is invoked for received events. 
	 * This method must be called asynchronously.
	 * 
	 * @param event a set of events
	 */
	public void notifyEvent(ZigBeeEvent event);
}
