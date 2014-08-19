/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal;

final class Commands {

	private Commands() {
		/* prevent object instantiation */
	}

	/**
	 * As a result, a new device must be registered with remove support. The
	 * command arguments are optional and can specify the device supported
	 * functions. The command result is the device unique identifier.
	 */
	public static final String	REGISTER_DEVICE					= "Register Device";

	/**
	 * As a result, a new device must be registered with remove support and
	 * single device function. The command result is the device unique
	 * identifier.
	 */
	public static final String	REGISTER_DEVICE_SINGLE_FUNCTION	= "Register Device with Single Function";

}
