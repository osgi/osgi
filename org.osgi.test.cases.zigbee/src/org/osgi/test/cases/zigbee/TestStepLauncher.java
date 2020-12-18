/*
 * Copyright (c) OSGi Alliance (2016, 2020). All Rights Reserved.
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

package org.osgi.test.cases.zigbee;

import java.io.FileInputStream;
import java.io.InputStream;
import org.osgi.framework.BundleContext;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;
import org.osgi.test.cases.zigbee.mock.ZCLFrameImpl;
import org.osgi.test.support.step.TestStepProxy;

public class TestStepLauncher {

	/**
	 * The singleton instance.
	 */
	private static TestStepLauncher	instance;

	/**
	 * The CT asks to the user to pair with the RI all the devices that have
	 * been described in the configuration file (see configFilename constant)
	 */
	static public final String		ACTIVATE_ZIGBEE_DEVICES	= "activate_devices";

	/**
	 * The CT asks to the user to add the device that is described in the
	 * configuration file that contains at least one reportable attribute. Once
	 * the user presses Enter, the CT tries to register a
	 * {@link ZCLEventListener} and check if the RI is sending the attribute
	 * reporting events accordingly.
	 */
	public static final String		EVENT_REPORTABLE		= "event_reportable";

	private static final String		configFilename			= System.getProperty("org.osgi.test.cases.zigbee.template", "zigbee-ct-template.xml");

	private ConfigurationFileReader	confReader;
	private static TestStepProxy	tproxy;

	private static Object			lock					= new Object();

	/*
	 * Constructor
	 */
	private TestStepLauncher(BundleContext bc) throws Exception {
		tproxy = new TestStepProxy(bc);

		/*
		 * Loads the configuration file from the filesystem. This file is
		 * located in the CT project but it is not a bnd resource and so it is
		 * not stored in the bundle but in the filesystem.
		 */
		InputStream is = new FileInputStream(configFilename);
		confReader = ConfigurationFileReader.getInstance(is);
		is.close();

		ZCLFrameImpl.minHeaderSize = confReader.getHeaderMinSize();
		ZCLFrameImpl.maxHeaderSize = confReader.getHeaderMaxSize();

		tproxy.execute(ACTIVATE_ZIGBEE_DEVICES,
				"Please ensure that the ZigBee Device Service and the ZigBee devices reported on the " + configFilename + " file have been properly paired with the coordinator");

	}

	/*
	 * Returns the TestStepLauncher singleton.
	 */

	public static TestStepLauncher getInstance(BundleContext bc) throws Exception {
		synchronized (lock) {
			if (instance == null) {
				instance = new TestStepLauncher(bc);
			}
			return instance;
		}
	}

	public ConfigurationFileReader getConfiguration() {
		return confReader;
	}
}
