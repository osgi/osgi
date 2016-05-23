/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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
	 * @return the current power mode.
	 */
	public short getCurrentPowerMode();

	/**
	 * @return the current power source field of the Power Descriptor
	 */
	public short getCurrentPowerSource();

	/**
	 * @return {@code true} if the currently selected power source is the mains
	 *         power.
	 */
	public boolean isMainsPower();

	/**
	 * @return {@code true} if the currently selected power source is the
	 *         disposable battery.
	 */
	public boolean isDisposableBattery();

	/**
	 * @return {@code true} if the currently selected power source is the
	 *         rechargeable battery.
	 */
	public boolean isRechargableBattery();

	/**
	 * @return the current power source level. May be one of
	 *         {@link #CRITICAL_LEVEL}, {@link #LOW_LEVEL},
	 *         {@link #MIDDLE_LEVEL}, {@link #FULL_LEVEL}
	 */
	public short getCurrentPowerSourceLevel();

	/**
	 * @return true if constant (mains) power is available or false otherwise.
	 */
	public boolean isConstantMainsPowerAvailable();

	/**
	 * @return true if disposable battery is available or false otherwise.
	 */
	public boolean isDisposableBatteryAvailable();

	/**
	 * @return true if rechargeable battery is available or false otherwise.
	 */
	public boolean isRechargableBatteryAvailable();

	/**
	 * Returns true if synchronized with the receiver on-when-idle subfield of
	 * the node descriptor.
	 * 
	 * @return {@code true} if the Current Power Mode field is syncronized on
	 *         idle.
	 */
	public boolean isSyncronizedWithOnIdle();

	/**
	 * @return {@code true} if the Current Power Mode field is periodically on.
	 */
	public boolean isPeriodicallyOn();

	/**
	 * @return {@code true} if the Current Power Mode field tells that the
	 *         received is on when the device is stimulated by pressing a
	 *         button, for instance.
	 */
	public boolean isOnWhenStimulated();

}
