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
 * @version 1.0
 */

public interface ZigBeePowerDescriptor {
	/**
	 * Receiver synchronized with the receiver on when idle subfield of the node
	 * descriptor.
	 */
	public static final short	POWER_MODE_SYNC			= 0;

	/**
	 * The current power mode.
	 */
	public static final short	POWER_MODE_PERIODIC		= 1;

	/**
	 * The current power mode.
	 */
	public static final short	POWER_MODE_STIMULATED	= 2;

	/**
	 * Current power source level: critical.
	 */
	public static final short	CRITICAL_LEVEL			= 0;

	/**
	 * Current power source level: 33%.
	 */
	public static final short	LOW_LEVEL				= 4;

	/**
	 * Current power source level: 66%.
	 */
	public static final short	MIDDLE_LEVEL			= 8;

	/**
	 * Current power source level: 100%.
	 */
	public static final short	FULL_LEVEL				= 12;

	/**
	 * Power source: Constant (mains) power.
	 */
	public static final short	CONSTANT_POWER			= 1;

	/**
	 * Power source: Rechargeable battery.
	 */
	public static final short	RECHARGEABLE_BATTERY	= 2;

	/**
	 * Power source: Disposable battery.
	 */
	public static final short	DISPOSABLE_BATTERY		= 4;

	/**
	 * @return the current power mode.
	 */
	public short getCurrentPowerMode();

	/**
	 * @return the current power source.
	 */
	public short getCurrentPowerSource();

	/**
	 * @return the current power source level.
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
	 * @return true if rechargable battery is available or false otherwise.
	 */
	public boolean isRechargableBatteryAvailable();
}
