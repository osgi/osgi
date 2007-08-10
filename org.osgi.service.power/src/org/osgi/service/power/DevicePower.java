/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2005, 2006). All Rights Reserved.
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

package org.osgi.service.power;

/**
 * <p>
 * Interface for identifying Power Device as defined in <a
 * href="http://www.acpi.info">ACPISpecifications</a>.
 * 
 * <p>
 * A service must implement this interface to indicate that it is a device power
 * service.
 */
public interface DevicePower {

	/**
	 * <p>
	 * 'D0' Device Power state as defined in <a
	 * href="http://www.acpi.info">ACPISpecifications</a>.
	 * 
	 * <p>
	 * This state is assumed to be the highest level of power consumption. The
	 * device is completely active and responsive, and is expected to remember
	 * all relevant context continuously.
	 * 
	 */
	public static final int		D0							= 0x00000001;

	/**
	 * <p>
	 * 'D1' Device Power State as defined in <a
	 * href="http://www.acpi.info">ACPISpecifications</a>.
	 * 
	 * <p>
	 * The meaning of the D1 Device State is defined by each device class. Many
	 * device classes may not define D1. In general, D1 is expected to save less
	 * power and preserve more device context than D2.
	 */
	public static final int		D1							= 0x00000002;

	/**
	 * <p>
	 * 'D2' Device Power State as defined in <a
	 * href="http://www.acpi.info">ACPISpecifications</a>.
	 * 
	 * <p>
	 * The meaning of the D2 Device State is defined by each device class. Many
	 * device classes may not define D2. In general, D2 is expected to save more
	 * power and preserve less device context than D1 or D0. Buses in D2 may
	 * cause the device to lose some context (for example, by reducing power on
	 * the bus, thus forcing the device to turn off some of its functions).
	 */
	public static final int		D2							= 0x00000004;

	/**
	 * <p>
	 * 'D3' Device Power State as defined in <a
	 * href="http://www.acpi.info">ACPISpecifications</a>.
	 * 
	 * <p>
	 * Power has been fully removed from the device. The device context is lost
	 * when this state is entered, so the OS software will reinitialize the
	 * device when powering it back on. Since device context and power are lost,
	 * devices in this state do not decode their address lines. Devices in this
	 * state have the longest restore times. All classes of devices define this
	 * state.
	 */
	public static final int		D3							= 0x00000008;

	/**
	 * <p>
	 * Unspecified device power state. This state can be used in the
	 * {@link #DEVICE_POWER_MAPPING} property registration to express that the
	 * transition is not defined.
	 */
	public static final int		UNSPECIFIED_STATE			= 0x00000000;

	/**
	 * Service registration property key (named
	 * <code>power.device.capabilities</code>) for DevicePower indicating the
	 * list of supported device power states, one or several of the following
	 * values: {@link #D0}, {@link #D1}, {@link #D2} or {@link #D3}. The type
	 * of this property must be an {@link Integer}. It represents a mask of all
	 * supported device power states. If this property is not used then
	 * {@link PowerManager} assumes that this DevicePower only supports
	 * {@link #D0} and {@link #D3} states.
	 */
	public static final String	DEVICE_POWER_CAPABILITIES	= "power.device.capabilities";

	/**
	 * Service registration property key (named
	 * <code>power.device.mapping</code> for DevicePower indicating the
	 * mapping (System/Device Power States) that the device wants to propose to
	 * the Power Manager. The Power Manager is allowed to override it. The type
	 * of the property must be int[]. Each index position represents a system
	 * power state from S0 to S5. Each value of the array represents the
	 * DevicePower state value for a given system power state position. For
	 * example, the following mapping
	 * 
	 * <pre>
	 *           S0 -&gt; D0
	 *           S1 -&gt; D1
	 *           S2 -&gt; unspecified
	 *           S3 -&gt; D3
	 *           S4 -&gt; D3
	 *           S5 -&gt; D3
	 * </pre>
	 * Is represented as follows: <pre>
	 * int[] mappings = new int[]{
	 *   D0,D1, UNSPECIFIED_STATE, D3, D3, D3
	 * };
	 * </pre>
	 * If this property is not used or invalid then the default mapping (defined
	 * in Power Manager) is used.
	 * 
	 */
	public static final String	DEVICE_POWER_MAPPING		= "power.device.mapping";

	/**
	 * Returns the current power state of this device power.
	 * 
	 * @return the current device power state.
	 */
	public int getPowerState();

	/**
	 * Sets the device power state with the given value. This method is
	 * generally called by {@link PowerManager} for a transition to another
	 * system power state.
	 * 
	 * @param state the device power state that the device must transit to.
	 * 
	 * @throws IllegalArgumentException if the given state value is not one of
	 *         the device power states or the transition is not allowed.
	 * @throws java.lang.SecurityException If the caller does not have the
	 *         appropriate <code>PowerPermission[this,setDevicePower]</code>,
	 *         and the Java Runtime Environment supports permissions.
	 * @see PowerPermission
	 */
	void setPowerState(int state) throws java.lang.SecurityException,
			IllegalArgumentException;
}
