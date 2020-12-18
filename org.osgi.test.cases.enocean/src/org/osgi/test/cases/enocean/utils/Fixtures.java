/*
 * Copyright (c) OSGi Alliance (2014, 2020). All Rights Reserved.
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

package org.osgi.test.cases.enocean.utils;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanEvent;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescription;
import org.osgi.test.cases.enocean.devices.Device01;

/**
 * @author $Id$
 */
public final class Fixtures {

	/** TEST_CASE_FILE_NAME */
	public static final String	TEST_CASE_FILE_NAME		= "test_case_file";

	/** HOST_ID */
	public static final int		HOST_ID					= 0x12345678;
	/** HOST_ID_2 */
	public static final int		HOST_ID_2				= 0x99887766;
	/** MANUFACTURER */
	public static final int		MANUFACTURER			= 0x6ea;
	/** RORG */
	public static final int		RORG					= 0xA5;
	/** RORG_RPS */
	public static final int		RORG_RPS				= 0xF6;
	/** FUNC */
	public static final int		FUNC					= 0x02;
	/** TYPE_1 */
	public static final int		TYPE_1					= 0x01;
	/** TYPE_2 */
	public static final int		TYPE_2					= 0x02;

	/** STR_HOST_ID */
	public static final String	STR_HOST_ID				= String.valueOf(HOST_ID);
	/** STR_HOST_ID_2 */
	public static final String	STR_HOST_ID_2			= String.valueOf(HOST_ID_2);
	/** STR_MANUFACTURER */
	public static final String	STR_MANUFACTURER		= String.valueOf(MANUFACTURER);
	/** STR_RORG */
	public static final String	STR_RORG				= String.valueOf(RORG);
	/** STR_FUNC */
	public static final String	STR_FUNC				= String.valueOf(FUNC);
	/** STR_TYPE_1 */
	public static final String	STR_TYPE_1				= String.valueOf(TYPE_1);
	/** STR_TYPE_2 */
	public static final String	STR_TYPE_2				= String.valueOf(TYPE_2);

	/** SELF_TEST_EVENT_TOPIC */
	public static final String	SELF_TEST_EVENT_TOPIC	= EnOceanEvent.TOPIC_MSG_RECEIVED;
	/** SELF_TEST_EVENT_KEY */
	public static final String	SELF_TEST_EVENT_KEY		= "degrees";
	/** SELF_TEST_EVENT_VALUE */
	public static final String	SELF_TEST_EVENT_VALUE	= "33.04°F";

	/** FLOATVALUE */
	public static final float	FLOATVALUE				= -20.0f;
	/** RAW_FLOATVALUE */
	public static final byte	RAW_FLOATVALUE			= 0x7f;
	/** TMP_CHANNEL_ID */
	public static final String	CHANNEL_ID				= "CID";
	/** CHANNEL_TYPE */
	public static final String	CHANNEL_TYPE			= EnOceanChannelDescription.TYPE_DATA;
	/** DEVICE_PID */
	public static final String	DEVICE_PID				= "my_exported_unique_device";
	/** STR_RORG_RPS */
	public static final Object	STR_RORG_RPS			= String.valueOf(RORG_RPS);

	/**
	 * @param bc
	 * @return device's service registration.
	 */
	public static ServiceRegistration<EnOceanDevice> registerDevice(
			BundleContext bc) {
		EnOceanDevice device = new Device01();
		Dictionary<String,Object> props = new Hashtable<>();
		props.put(EnOceanDevice.ENOCEAN_EXPORT, Boolean.TRUE);
		props.put(Constants.SERVICE_PID, Fixtures.DEVICE_PID);
		props.put(EnOceanDevice.RORG, Fixtures.STR_RORG);
		props.put(EnOceanDevice.FUNC, Fixtures.STR_FUNC);
		props.put(EnOceanDevice.TYPE, Fixtures.STR_TYPE_1);
		props.put(EnOceanDevice.MANUFACTURER, Fixtures.STR_MANUFACTURER);
		return bc.registerService(EnOceanDevice.class, device, props);
	}

}
