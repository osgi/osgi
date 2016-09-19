/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.zigbee.ZCLEventListener;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;
import org.osgi.test.cases.zigbee.mock.ZCLFrameImpl;
import org.osgi.test.support.step.TestStepProxy;

public class TestStepLauncher {
	private static TestStepLauncher	instance;

	/**
	 * The CT asks to the user to input the path of the configuration file that
	 * contains all the info about the ZigBee node that it is going to use in
	 * the test cases.
	 */
	static public final String		ASK_CONFIG_FILE_PATH	= "file_path";

	/**
	 * The CT asks to the user to pair with the RI all the devices that have
	 * been described in the configuration file configured at step
	 * {@link #ASK_CONFIG_FILE_PATH}.
	 */
	static public final String		ACTIVATE_ZIGBEE_DEVICES	= "activate_devices";

	/**
	 * The CT asks to the user to add the device that is described in the
	 * configuration file specified at step {@link #ASK_CONFIG_FILE_PATH} and
	 * that contains at least one reportable attribute. Once the user presses
	 * Enter, the CT tries to register a {@link ZCLEventListener} and check if
	 * the RI is sending the attribute reporting events accordingly.
	 */
	public static final String		EVENT_REPORTABLE		= "event_reportable";

	private static final String		riConfFilename			= "zigbee-template.xml";

	private ConfigurationFileReader	confReader;
	private TestStepProxy			tproxy;

	private TestStepLauncher(BundleContext bc) throws IOException {
		tproxy = new TestStepProxy(bc);

		String msg = "Please type the absolute path of the xml file that contains the\n"
				+ "information about the the zigbee network characteristics you are\n"
				+ "going to use during this CT. A template of this file is available in\n"
				+ "the OSGi A RI bundle (zigbee-template.xml). Please be careful to\n"
				+ "provide an absolute path and NOT GIVE to this file\n"
				+ "the name 'zigbee-template.xml' and that it is correctly filled\n"
				+ "with your devices info\n";

		String configFilePath = tproxy.execute(ASK_CONFIG_FILE_PATH, msg);

		if ((configFilePath == null) || ((configFilePath != null) && (configFilePath.equals("")))) {
			throw new IllegalArgumentException("invalid path");
		}

		tproxy.execute(ACTIVATE_ZIGBEE_DEVICES,
				"please please plug and setup all the devices described in the configuration file:");

		InputStream is = null;

		if (!configFilePath.equals(riConfFilename)) {
			// the file is not the RI one.
			is = new FileInputStream(configFilePath);
		} else {
			Bundle[] bundles = bc.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				if (bundles[i].getSymbolicName().equals("org.osgi.impl.service.zigbee")) {
					Enumeration e = bundles[i].getEntryPaths("");

					URL url = bundles[i].getEntry(configFilePath);
					is = url.openStream();
					break;
				}
			}
			if (is == null) {
				throw new FileNotFoundException("ZigBee RI not found.");
			}
		}

		confReader = ConfigurationFileReader.getInstance(is);
		is.close();
		ZCLFrameImpl.minHeaderSize = confReader.getHeaderMinSize();
	}

	public static TestStepLauncher launch(BundleContext bc) throws IOException {
		if (instance == null) {
			instance = new TestStepLauncher(bc);
		}
		return instance;
	}

	public ConfigurationFileReader getConfReader() {
		return confReader;
	}

	public TestStepProxy getTeststepProxy() {
		return tproxy;
	}
}
