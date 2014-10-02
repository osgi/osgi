/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal;

/**
 * Contains for all test steps used by this test case.
 */
public final class DeviceTestSteps {

	private DeviceTestSteps() {
		/* prevent object instantiation */
	}

	/**
	 * Step identifier guarantees that at least one fully supported device will
	 * be available in the registry.
	 */
	public static final String	STEP_ID_AVAILABLE_DEVICE				= "dal.available.device";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_DEVICE} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_DEVICE			= "At least one fully supported device should be available in the registry.";

	/**
	 * Step identifier guarantees that at least one function will be available
	 * in the registry.
	 */
	public static final String	STEP_ID_AVAILABLE_FUNCTION				= "dal.available.function";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_FUNCTION} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_FUNCTION			= "At least one function should be available in the registry.";

	/**
	 * Step identifier guarantees that at least one function operation will be
	 * available.
	 */
	public static final String	STEP_ID_AVAILABLE_OPERATION				= "dal.available.operation";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_OPERATION} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_OPERATION		= "At least one function operation should be available.";

	/**
	 * Step identifier guarantees that at least one readable function property
	 * will be available.
	 */
	public static final String	STEP_ID_PROPERTY_READABLE				= "dal.property.readable";

	/**
	 * Step message for {@link #STEP_ID_PROPERTY_READABLE} step.
	 */
	public static final String	STEP_MESSAGE_PROPERTY_READABLE			= "At least one readable function property should be available.";

	/**
	 * Step identifier guarantees that at least one readable function property
	 * will be available.
	 */
	public static final String	STEP_ID_PROPERTY_WRITABLE				= "dal.property.writable";

	/**
	 * Step message for {@link #STEP_ID_PROPERTY_WRITABLE} step.
	 */
	public static final String	STEP_MESSAGE_PROPERTY_WRITABLE			= "At least one writable function property should be available.";

	/**
	 * Step identifier requires the availability of devices with a support of
	 * all available device properties.
	 */
	public static final String	STEP_ID_DEVICES_ALL_PROPS				= "dal.devices.all.props";

	/**
	 * Step message for {@link #STEP_ID_DEVICES_ALL_PROPS} step.
	 */
	public static final String	STEP_MESSAGE_DEVICES_ALL_PROPS			= "Devices with a support of all device properties like description, model, name etc. is required.";

	/**
	 * Step identifier requires the availability of functions with a support of
	 * all available function properties.
	 */
	public static final String	STEP_ID_FUNCTIONS_ALL_PROPS				= "dal.functions.all.props";

	/**
	 * Step message for {@link #STEP_ID_FUNCTIONS_ALL_PROPS} step.
	 */
	public static final String	STEP_MESSAGE_FUNCTIONS_ALL_PROPS		= "Functions with a support of all function properties like description, version etc. is required.";

	/**
	 * Step identifier for a new device registration. No result is expected from
	 * the execution, but a new device service should be registered.
	 */
	public static final String	STEP_ID_REGISTER_DEVICE					= "dal.register.device";

	/**
	 * Step message for {@link #STEP_ID_REGISTER_DEVICE} step.
	 */
	public static final String	STEP_MESSAGE_REGISTER_DEVICE			= "Register a new device to the OSGi service registy.";

	/**
	 * Step identifier for a device registration with at least one supported
	 * function. No result is expected form the execution, but a new device
	 * service should be registered with at least one function.
	 */
	public static final String	STEP_ID_REGISTER_DEVICE_FUNCTION		= "dal.register.device.function";

	/**
	 * Step message for {@link #STEP_ID_REGISTER_DEVICE_FUNCTION} step.
	 */
	public static final String	STEP_MESSAGE_REGISTER_DEVICE_FUNCTION	= "Register a new device with at least one function.";

}
