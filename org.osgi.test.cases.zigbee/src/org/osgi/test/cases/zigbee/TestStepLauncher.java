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
import java.io.InputStream;
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

	private ConfigurationFileReader	confReader;
	private TestStepProxy			tproxy;

	private TestStepLauncher(BundleContext bc) {
		tproxy = new TestStepProxy(bc);

		String configFilePath = tproxy.execute(ASK_CONFIG_FILE_PATH,
				"please type the configuration file path and be sure to fill it with your values:");

		if ((configFilePath == null) || ((configFilePath != null) && (configFilePath.equals("")))) {
			throw new IllegalArgumentException("invalid path");
		}

		tproxy.execute(ACTIVATE_ZIGBEE_DEVICES,
				"please please plug and setup all the devices described in the configuration file:");

		InputStream is;
		try {
			is = new FileInputStream(configFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		confReader = ConfigurationFileReader.getInstance(is);
		ZCLFrameImpl.minHeaderSize = confReader.getHeaderMinSize();
	}

	public static TestStepLauncher launch(BundleContext bc) {
		if (instance == null) {
			instance = new TestStepLauncher(bc);
		}
		return instance;
	}

	public ConfigurationFileReader getConfReader() {
		return confReader;
	}

	// private String readFile(String file) throws IOException {
	// String result = "";
	// try {
	// String line;
	// File myFile = new File(file);
	// BufferedReader inFile = new BufferedReader(new FileReader(myFile));
	// while ((line = inFile.readLine()) != null) {
	// result += line;
	// }
	// inFile.close();
	// } catch (IOException e) {
	// System.out.println("problem with file");
	// }
	//
	// return result;
	// }

	public TestStepProxy getTeststepProxy() {
		return tproxy;
	}
}
