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

import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * <p>
 * An interface that a vendor has to implement to manage the overall power state
 * changes. The Power Manager has to maintain a consistency between the current
 * power state of the host (known as system power state) and power state of
 * devices (known as device power state) connected to it.
 * <p>
 * There will only be a single instance of this service registered with the
 * Framework.
 * 
 * @version $Revision$
 */
public interface PowerManager {

	/**
	 * S0 (Working or G0) system power state as defined in <a
	 * href="http://www.acpi.info">ACPISpecifications</a>. A computer
	 * state where the system dispatches user mode (application) threads and
	 * they execute. In this state, peripheral devices (peripherals) are having
	 * their power state changed dynamically. The user can select, through some
	 * UI, various performance/power characteristics of the system to have the
	 * software optimize for performance or battery life. The system responds to
	 * external events in real time. It is not safe to disassemble the machine
	 * in this state.
	 */
	public static final int	S0	= 0;

	/**
	 * S1 Sleeping system power state as defined in <a
	 * href="http://www.acpi.info">ACPISpecifications</a>. The S1
	 * sleeping state is a low wake latency sleeping state. In this state, no
	 * system context is lost (CPU or chip set) and hardware maintains all
	 * system context.
	 */
	public static final int	S1	= 1;

	/**
	 * S2 Sleeping system power state as defined in <a
	 * href="http://www.acpi.info">ACPISpecifications</a>. The S2
	 * sleeping state is a low wake latency sleeping state. This state is
	 * similar to the S1 sleeping state except that the CPU and system cache
	 * context is lost (the OS is responsible for maintaining the caches and CPU
	 * context). Control starts from the processor's reset vector after the wake
	 * event.
	 */
	public static final int	S2	= 2;

	/**
	 * S3 Sleeping system power state as defined in <a
	 * href="http://www.acpi.info">ACPISpecifications</a>. The S3
	 * sleeping state is a low wake latency sleeping state where all system
	 * context is lost except system memory. CPU, cache, and chip set context
	 * are lost in this state. Hardware maintains memory context and restores
	 * some CPU and L2 configuration context. Control starts from the
	 * processor's reset vector after the wake event.
	 */
	public static final int	S3	= 3;

	/**
	 * S4 Sleeping system power state as defined in <a
	 * href="http://www.acpi.info">ACPISpecifications</a>. The S4
	 * sleeping state is the lowest power, longest wake latency sleeping state
	 * supported by ACPI. In order to reduce power to a minimum, it is assumed
	 * that the hardware platform has powered off all devices. Platform context
	 * is maintained.
	 */
	public static final int	S4	= 4;

	/**
	 * S5 system power state (Soft Off or G2) as defined in <a
	 * href="http://www.acpi.info">ACPISpecifications</a>. The S5 state
	 * is similar to the S4 state except that the OS does not save any context.
	 * The system is in the "soft" off state and requires a complete boot when
	 * it wakes. Software uses a different state value to distinguish between
	 * the S5 state and the S4 state to allow for initial boot operations within
	 * the BIOS to distinguish whether or not the boot is going to wake from a
	 * saved memory image.
	 */
	public static final int	S5	= 5;

	/**
	 * Returns the current system power state.
	 * 
	 * @return the current system power state.
	 */
	public int getPowerState();

	/**
	 * <p>
	 * Changes the system power state with the given value.
	 * 
	 * <p>
	 * The state must be one of the system power state values.
	 * 
	 * <p>
	 * if <code>urgent</code> is set to <code>true</code>, all interested
	 * {@link PowerHandler} services are asked for request permission by calling
	 * {@link PowerHandler#handleQuery(int)} method. In case of veto from a
	 * {@link PowerHandler}, all previouly notified {@link PowerHandler}s must
	 * be called on {@link PowerHandler#handleQueryFailed(int)} to inform that
	 * the transition has been rejected. Then the method ends and
	 * <code>false</code> is returned. If not, the process continues.
	 * 
	 * Then, Power Manager sents the appropriate event to
	 * {@link EventAdmin#sendEvent(Event)} for publishing the corresponding
	 * power state change.
	 * 
	 * @param state the state into which the system is going to transit.
	 * @param urgent <code>true</code> to force the system to change state
	 *        without requesting any permission from {@link PowerHandler}s.
	 *        <code>true</code> to query all {@link PowerHandler}s for
	 *        permission. In case of at least one of the {@link PowerHandler}s
	 *        denies the query, the Power Manager informs in reverse order all
	 *        {@link PowerHandler}s that the query failed and an array of
	 *        {@link ServiceReference}s which rejected the request is returned.
	 *        In circumstances like a low power or emergency shutdown,
	 *        <code>urgent</code> should be set to <code>true</code>.
	 * @return array of {@link PowerHandler}s {@link ServiceReference}s which
	 *         have denied the request; <code>null</code> otherwize.
	 * @throws IllegalArgumentException if the given state value is not one of
	 *         the system power states or the transition is not allowed.
	 * @throws java.lang.SecurityException If the caller does not have the
	 *         appropriate <code>PowerPermission[system,setSystemPower]</code>,
	 *         and the Java Runtime Environment supports permissions.
	 */
	public ServiceReference[] setPowerState(int state, boolean urgent)
			throws java.lang.SecurityException, IllegalArgumentException;

}
