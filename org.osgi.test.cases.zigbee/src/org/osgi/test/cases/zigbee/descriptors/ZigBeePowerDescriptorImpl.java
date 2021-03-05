/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.zigbee.descriptors;

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
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	public boolean isRechargableBatteryAvailable() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	public boolean isMainsPower() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	public boolean isDisposableBattery() {
		throw new UnsupportedOperationException("this field is not checked by the CT.");
	}

	public boolean isRechargableBattery() {
		return false;
	}

	public boolean isSyncronizedWithOnIdle() {
		return false;
	}

	public boolean isPeriodicallyOn() {
		return false;
	}

	public boolean isOnWhenStimulated() {
		return false;
	}
}
