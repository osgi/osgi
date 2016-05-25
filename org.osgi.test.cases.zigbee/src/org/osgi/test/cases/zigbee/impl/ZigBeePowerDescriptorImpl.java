
package org.osgi.test.cases.zigbee.impl;

import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;

/**
 * Mocked impl of ZigBeePowerDescriptor.
 * 
 * @author $Id$
 */
public class ZigBeePowerDescriptorImpl implements ZigBeePowerDescriptor {

	private short	currentPowerMode;
	private short	currentPowerSource;
	private short	currentPowerSourceLevel;
	private boolean	isConstantMainsPowerAvailable;

	/**
	 * @param powerMode
	 * @param powerSource
	 * @param powerSourceLevel
	 * @param isconstant
	 */
	public ZigBeePowerDescriptorImpl(short powerMode, short powerSource,
			short powerSourceLevel, boolean isconstant) {
		this.currentPowerMode = powerMode;
		this.currentPowerSource = powerSourceLevel;
		this.currentPowerSourceLevel = powerSourceLevel;
		this.isConstantMainsPowerAvailable = isconstant;
	}

	public short getCurrentPowerMode() {
		return currentPowerMode;
	}

	public short getCurrentPowerSource() {
		return currentPowerSource;
	}

	public short getCurrentPowerSourceLevel() {
		return currentPowerSourceLevel;
	}

	public boolean isConstantMainsPowerAvailable() {
		return isConstantMainsPowerAvailable;
	}

	public boolean isDisposableBatteryAvailable() {
		return true;
	}

	public boolean isRechargableBatteryAvailable() {
		return true;
	}

	public boolean isMainsPower() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDisposableBattery() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRechargableBattery() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSyncronizedWithOnIdle() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPeriodicallyOn() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOnWhenStimulated() {
		// TODO Auto-generated method stub
		return false;
	}
}
