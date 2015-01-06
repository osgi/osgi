/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.impl.service.dal;

import org.osgi.service.dal.Device;

final class DeviceUtil {

	private DeviceUtil() {/* prevent object instantiation */
	}

	public static void silentDeviceRemove(final Device device) {
		try {
			device.remove();
		} catch (Exception e) {
			// nothing to do
		}
	}
}
