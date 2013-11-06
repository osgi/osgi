
package org.osgi.service.resourcemanagement;

/**
 * Resource Monitor Exception reports an invalid usage of a monitor.
 * 
 */
public class ResourceMonitorException extends Exception {

	/**
	 * Create a new ResourceMonitorException
	 * 
	 * @param msg message
	 */
	public ResourceMonitorException(String msg) {
		super(msg);
	}

}
