/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal.secure.step;

/**
 * Contains all test steps used by this test case.
 */
public final class SecureDeviceTestSteps {

	private SecureDeviceTestSteps() {
		/* prevent object instantiation */
	}

	/**
	 * Step identifier guarantees that at least one device will be available in
	 * the registry.
	 */
	public static final String	STEP_ID_AVAILABLE_DEVICE		= "dal.secure.available.device";

	/**
	 * Step message for {@link #STEP_ID_AVAILABLE_DEVICE} step.
	 */
	public static final String	STEP_MESSAGE_AVAILABLE_DEVICE	= "At least one device should be available in the registry.";
}
