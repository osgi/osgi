/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.zigbee.mock;

import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;

/**
 * Mocked impl of ZigBeePowerDescriptor.
 * 
 * @author $Id: 66088806ce415346a4f2c7df80649c509a5ad73d $
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
	public ZigBeePowerDescriptorImpl(short powerMode, short powerSource, short powerSourceLevel, boolean isconstant) {
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
