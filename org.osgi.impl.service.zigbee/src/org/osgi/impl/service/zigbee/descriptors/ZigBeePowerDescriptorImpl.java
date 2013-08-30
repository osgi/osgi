
package org.osgi.impl.service.zigbee.descriptors;

import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;

public class ZigBeePowerDescriptorImpl implements ZigBeePowerDescriptor {

	private short	currentPowerMode;
	private short	currentPowerSource;
	private short	currentPowerSourceLevel;
	private boolean	isConstantMainsPowerAvailable;

	public ZigBeePowerDescriptorImpl(short powerMode, short powerSource, short powerSourceLevel, boolean isconstant) {
		this.currentPowerMode = powerMode;
		this.currentPowerSource = powerSourceLevel;
		this.currentPowerSourceLevel = powerSourceLevel;
		this.isConstantMainsPowerAvailable = isconstant;

	}

	/**
	 * @return the current power mode.
	 */
	public short getCurrentPowerMode() {
		return currentPowerMode;
	}

	/**
	 * @return the current power source.
	 */
	public short getCurrentPowerSource() {
		return currentPowerSource;
	}

	/**
	 * @return the current power source level.
	 */
	public short getCurrentPowerSourceLevel() {
		return currentPowerSourceLevel;
	}

	/**
	 * @return true if constant (mains) power is available or false otherwise.
	 */
	public boolean isConstantMainsPowerAvailable() {
		return isConstantMainsPowerAvailable;
	}

	/**
	 * @return true if disposable battery is available or false otherwise.
	 */
	public boolean isDisposableBatteryAvailable() {
		return true;
	}

	/**
	 * @return true if rechargeable battery is available or false otherwise.
	 */
	public boolean isRechargableBatteryAvailable() {
		return true;
	}
}
