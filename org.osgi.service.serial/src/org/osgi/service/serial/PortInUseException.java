package org.osgi.service.serial;

/**
 * Thrown when the specified port is in use.
 */
public class PortInUseException extends Exception{

	/**
	 * The current owner of the communications port.
	 */
	private String currentOwner;

	/**
	 * Constructor.
	 */
	public PortInUseException(String currentOwner) {
		this.currentOwner = currentOwner;
	}

	/**
	 * Describes the current owner of the communications port.<br>
	 * <br>
	 * @return current owner
	 */
	public String currentOwner() {
		return currentOwner;
	}
}
