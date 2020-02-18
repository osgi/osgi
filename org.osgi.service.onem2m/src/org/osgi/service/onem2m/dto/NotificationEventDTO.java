package org.osgi.service.onem2m.dto;

import java.util.HashMap;

/**
 * 
 * NotificationEventDTO
 * 
 * This data structure is held in NotificationDTO.
 * 
 * @see oneM2M TS-0004 6.3.5.13
 *
 */
public class NotificationEventDTO {
	/**
	 * 
	 * m2m:representation
	 * 
	 * @see oneM2M TS-0004 6.3.5.62
	 */
	public Object representation;
	
	/**
	 * operationMonitor
	 * 
	 * @see oneM2M TS-0004 6.3.5.57
	 */
	public HashMap<String, Object> operationMonitor;
	
	/**
	 * notificationEventType
	 * 
	 * @see oneM2M TS-0004 6.3.4.2.19 
	 */
	public NotificationEventType notificationEventType;
	
	
	/**
	 * NotificationEventType
	 * 
	 * @see oneM2M TS-0004 6.3.4.2.19 
	 * 
	 */
	public static enum NotificationEventType {
		/**
		 * updagte_of_resouce. 
		 * 
		 * This is the default value.
		 */
		updagte_of_resource(1), 
		/**
		 * delete_of_resource
		 */
		delete_of_resource(2),
		/**
		 * create_of_direct_child_resource
		 */
		create_of_direct_child_resource(3),
		/**
		 * create_of_direct_child_resouce
		 */
		delete_of_direct_child_resouce(4),
		/**
		 * retrieve_of_container_resource_with_no_child_resource
		 */
		retrieve_of_container_resource_with_no_child_resource(5);

		int value;

		private NotificationEventType(int i) {
			value = i;
		}

		public int getValue() {
			return value;
		}
	}
}
