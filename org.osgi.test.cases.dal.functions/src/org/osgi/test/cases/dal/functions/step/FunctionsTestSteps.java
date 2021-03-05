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

package org.osgi.test.cases.dal.functions.step;

/**
 * Contains for all test steps used by this test case.
 */
public final class FunctionsTestSteps {

	private FunctionsTestSteps() {
		// prevent object instantiation
	}

	/**
	 * Step identifier guarantees that at least one fully supported Alarm
	 * function will be available in the registry.
	 */
	public static final String	STEP_ID_AVAILABLE_ALARM				= "dal.available.alarm";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_ALARM} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_ALARM		= "At least one fully supported Alarm function should be available in the registry.";

	/**
	 * Step identifier guarantees that at least one fully supported Boolean
	 * Control function will be available in the registry.
	 */
	public static final String	STEP_ID_AVAILABLE_BC				= "dal.available.bc";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_BC} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_BC			= "At least one fully supported Boolean Control function should be available in the registry.";

	/**
	 * Step identifier guarantees that at least one fully supported Boolean
	 * Sensor function will be available in the registry.
	 */
	public static final String	STEP_ID_AVAILABLE_BS				= "dal.available.bs";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_BS} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_BS			= "At least one fully supported Boolean Sensor function should be available in the registry.";

	/**
	 * Step identifier guarantees that at least one fully supported Keypad
	 * function will be available in the registry.
	 */
	public static final String	STEP_ID_AVAILABLE_KEYPAD			= "dal.available.keypad";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_KEYPAD} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_KEYPAD		= "At least one fully supported Keypad function should be available in the registry.";

	/**
	 * Step identifier guarantees that at least one fully supported Meter
	 * function will be available in the registry.
	 */
	public static final String	STEP_ID_AVAILABLE_METER				= "dal.available.meter";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_METER} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_METER		= "At least one fully supported Meter function should be available in the registry.";

	/**
	 * Step identifier guarantees that at least one fully supported Multi-Level
	 * Control function will be available in the registry.
	 */
	public static final String	STEP_ID_AVAILABLE_MLC				= "dal.available.mlc";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_MLC} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_MLC			= "At least one fully supported Multi-Level Control function should be available in the registry.";

	/**
	 * Step identifier guarantees that at least one fully supported Multi-Level
	 * Sensor function will be available in the registry.
	 */
	public static final String	STEP_ID_AVAILABLE_MLS				= "dal.available.mls";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_MLS} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_MLS			= "At least one fully supported Multi-Level Sensor function should be available in the registry.";

	/**
	 * Step identifier guarantees that at least one fully supported Wake Up
	 * function will be available in the registry.
	 */
	public static final String	STEP_ID_AVAILABLE_WAKE_UP			= "dal.available.wake.up";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_WAKE_UP} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_WAKE_UP		= "At least one fully supported Wake Up function should be available in the registry.";

	/**
	 * Step identifier for a publishing of a new property event for the 'alarm'
	 * property of the 'Alarm' function. No result is expected from the
	 * execution.
	 */
	public static final String	STEP_ID_EVENT_ALARM					= "dal.functions.event.alarm";

	/**
	 * Step message for {@link #STEP_ID_EVENT_ALARM} step.
	 */
	public static final String	STEP_MESSAGE_EVENT_ALARM			= "Publish a new property event for the 'alarm' property of the 'Alarm' function.";

	/**
	 * Step identifier for a publishing of a new property event for the 'data'
	 * property of the 'BooleanSensor' function. No result is expected from the
	 * execution.
	 */
	public static final String	STEP_ID_EVENT_BS					= "dal.functions.event.bs";

	/**
	 * Step message for {@link #STEP_ID_EVENT_BS} step.
	 */
	public static final String	STEP_MESSAGE_EVENT_BS				= "Publish a new property event for the 'data' property of the 'BooleanSensor' function.";

	/**
	 * Step identifier for a publishing of a new property event for the 'key'
	 * property of the 'Keypad' function. No result is expected from the
	 * execution.
	 */
	public static final String	STEP_ID_EVENT_KEYPAD				= "dal.functions.event.keypad";

	/**
	 * Step message for {@link #STEP_ID_EVENT_KEYPAD} step.
	 */
	public static final String	STEP_MESSAGE_EVENT_KEYPAD			= "Publish a new property event for the 'key' property of the 'Keypad' function.";

	/**
	 * Step identifier for a publishing of a new property event for the
	 * 'current' property of the 'Meter' function. No result is expected from
	 * the execution.
	 */
	public static final String	STEP_ID_EVENT_METER_CURRENT			= "dal.functions.event.meter.current";

	/**
	 * Step message for {@link #STEP_ID_EVENT_METER_CURRENT} step.
	 */
	public static final String	STEP_MESSAGE_EVENT_METER_CURRENT	= "Publish a new property event for the 'current' property of the 'Meter' function.";

	/**
	 * Step identifier for a publishing of a new property event for the 'total'
	 * property of the 'Meter' function. No result is expected from the
	 * execution.
	 */
	public static final String	STEP_ID_EVENT_METER_TOTAL			= "dal.functions.event.meter.total";

	/**
	 * Step message for {@link #STEP_ID_EVENT_METER_TOTAL} step.
	 */
	public static final String	STEP_MESSAGE_EVENT_METER_TOTAL		= "Publish a new property event for the 'total' property of the 'Meter' function.";

	/**
	 * Step identifier for a publishing of a new property event for the 'data'
	 * property of the 'MultiLevelControl' function. No result is expected from
	 * the execution.
	 */
	public static final String	STEP_ID_EVENT_MLC					= "dal.functions.event.mlc";

	/**
	 * Step message for {@link #STEP_ID_EVENT_MLC} step.
	 */
	public static final String	STEP_MESSAGE_EVENT_MLC				= "Publish a new property event for the 'data' property of the 'MultiLevelControl' function.";

	/**
	 * Step identifier for a publishing of a new property event for the 'data'
	 * property of the 'MultiLevelSensor' function.. No result is expected from
	 * the execution.
	 */
	public static final String	STEP_ID_EVENT_MLS					= "dal.functions.event.mls";

	/**
	 * Step message for {@link #STEP_ID_EVENT_MLS} step.
	 */
	public static final String	STEP_MESSAGE_EVENT_MLS				= "Publish a new property event for the 'data' property of the 'MultiLevelSensor' function.";
}
