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

package org.osgi.impl.service.zigbee.basedriver.descriptors;

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
	public ZigBeePowerDescriptorImpl(short powerMode, short powerSource, short powerSourceLevel, boolean isconstant) {
		this.currentPowerMode = powerMode;
		this.currentPowerSource = powerSourceLevel;
		this.currentPowerSourceLevel = powerSourceLevel;
		this.isConstantMainsPowerAvailable = isconstant;
	}

	@Override
	public short getCurrentPowerMode() {
		return currentPowerMode;
	}

	@Override
	public short getCurrentPowerSource() {
		return currentPowerSource;
	}

	@Override
	public short getCurrentPowerSourceLevel() {
		return currentPowerSourceLevel;
	}

	@Override
	public boolean isConstantMainsPowerAvailable() {
		return isConstantMainsPowerAvailable;
	}

	@Override
	public boolean isDisposableBatteryAvailable() {
		return true;
	}

	@Override
	public boolean isRechargableBatteryAvailable() {
		return true;
	}

	@Override
	public boolean isMainsPower() {

		return false;
	}

	@Override
	public boolean isDisposableBattery() {

		return false;
	}

	@Override
	public boolean isRechargableBattery() {

		return false;
	}

	@Override
	public boolean isSyncronizedWithOnIdle() {

		return false;
	}

	@Override
	public boolean isPeriodicallyOn() {

		return false;
	}

	@Override
	public boolean isOnWhenStimulated() {

		return false;
	}
}
