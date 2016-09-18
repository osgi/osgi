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

package org.osgi.service.zigbee.descriptors;

/**
 * This interface represents a power descriptor as described in the ZigBee
 * Specification The Power Descriptor gives a dynamic indication of the power
 * status of the node.
 * 
 * @noimplement
 * 
 * @author $Id$
 */
public interface ZigBeePowerDescriptor {

	/**
	 * Current power source level: critical.
	 */
	public static final short	CRITICAL_LEVEL	= 0;

	/**
	 * Current power source level: 33%.
	 */
	public static final short	LOW_LEVEL		= 1;

	/**
	 * Current power source level: 66%.
	 */
	public static final short	MIDDLE_LEVEL	= 2;

	/**
	 * Current power source level: 100%.
	 */
	public static final short	FULL_LEVEL		= 3;

	/**
	 * Returns the current power mode.
	 * 
	 * @return the current power mode.
	 */
	public short getCurrentPowerMode();

	/**
	 * Returns the current power source field of the Power Descriptor.
	 * 
	 * @return the current power source field of the Power Descriptor.
	 */
	public short getCurrentPowerSource();

	/**
	 * Checks if the currently selected power source is the mains power.
	 * 
	 * @return {@code true} if the currently selected power source is the mains
	 *         power.
	 */
	public boolean isMainsPower();

	/**
	 * Checks if the currently selected power source is the disposable battery.
	 * 
	 * @return {@code true} if the currently selected power source is the
	 *         disposable battery.
	 */
	public boolean isDisposableBattery();

	/**
	 * Checks if the currently selected power source is the rechargeable
	 * battery.
	 * 
	 * @return {@code true} if the currently selected power source is the
	 *         rechargeable battery.
	 */
	public boolean isRechargableBattery();

	/**
	 * Returns the current power source level.
	 * 
	 * @return the current power source level. May be one of
	 *         {@link #CRITICAL_LEVEL}, {@link #LOW_LEVEL},
	 *         {@link #MIDDLE_LEVEL}, {@link #FULL_LEVEL}.
	 */
	public short getCurrentPowerSourceLevel();

	/**
	 * Checks if constant (mains) power is available.
	 * 
	 * @return true if constant (mains) power is available or false otherwise.
	 */
	public boolean isConstantMainsPowerAvailable();

	/**
	 * Checks if a disposable battery is available.
	 * 
	 * @return true if a disposable battery is available or false otherwise.
	 */
	public boolean isDisposableBatteryAvailable();

	/**
	 * Checks if a rechargeable battery is available.
	 * 
	 * @return true if a rechargeable battery is available or false otherwise.
	 */
	public boolean isRechargableBatteryAvailable();

	/**
	 * Checks if synchronized with the receiver on-when-idle subfield of the
	 * node descriptor.
	 * 
	 * @return {@code true} if the Current Power Mode field is synchronized on
	 *         idle.
	 */
	public boolean isSyncronizedWithOnIdle();

	/**
	 * Checks if the Current Power Mode field is periodically on.
	 * 
	 * @return {@code true} if the Current Power Mode field is periodically on.
	 */
	public boolean isPeriodicallyOn();

	/**
	 * Checks if the receiver is on when the device is simulated.
	 * 
	 * @return {@code true} if the Current Power Mode field tells that the
	 *         receiver is on when the device is stimulated by pressing a
	 *         button, for instance.
	 */
	public boolean isOnWhenStimulated();

}
