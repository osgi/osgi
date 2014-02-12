
package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZCLHeader;

/**
 * Mocked impl.
 */
public class ZCLHeaderImpl implements ZCLHeader {

	private int		commandId					= -1;
	private int		manufacturerCode			= -1;
	private boolean	isClusterSpecificCommand	= false;
	private boolean	isManufacturerSpecific		= false;
	private boolean	isClientServerDirection		= false;
	private boolean	isDefaultResponseEnabled	= false;

	/**
	 * @param commandId
	 * @param manufacturerCode
	 * @param isClusterSpecificCommand
	 * @param isManufacturerSpecific
	 * @param isClientServerDirection
	 * @param isDefaultResponseEnabled
	 */
	public ZCLHeaderImpl(int commandId, int manufacturerCode, boolean isClusterSpecificCommand,
			boolean isManufacturerSpecific, boolean isClientServerDirection, boolean isDefaultResponseEnabled) {
		this.commandId = commandId;
		this.manufacturerCode = manufacturerCode;
		this.isClusterSpecificCommand = isClusterSpecificCommand;
		this.isManufacturerSpecific = isManufacturerSpecific;
		this.isClientServerDirection = isClientServerDirection;
		this.isDefaultResponseEnabled = isDefaultResponseEnabled;
	}

	public int getCommandId() {
		return commandId;
	}

	public int getManufacturerCode() {
		return manufacturerCode;
	}

	public boolean isClusterSpecificCommand() {
		return isClusterSpecificCommand;
	}

	public boolean isManufacturerSpecific() {
		return isManufacturerSpecific;
	}

	public boolean isClientServerDirection() {
		return isClientServerDirection;
	}

	public boolean isDefaultResponseEnabled() {
		return isDefaultResponseEnabled;
	}

}
