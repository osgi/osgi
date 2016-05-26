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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;
import org.osgi.test.cases.zigbee.impl.ZCLFrameImpl;
import org.osgi.test.support.step.TestStepProxy;

public class TestStepLauncher {
	private static TestStepLauncher	instance;

	static public final String		CONF_FILE_PATH	= "file path";
	static public final String		LOAD_CONF		= "load conf";
	private String					confFilePath	= "template.xml";
	private ConfigurationFileReader	confReader;

	private TestStepLauncher(String pathFile, BundleContext bc) {
		TestStepProxy tproxy = new TestStepProxy(bc);
		String stringFile;
		try {
			stringFile = readFile(pathFile);
			String result = tproxy.execute(CONF_FILE_PATH + stringFile,
					"please type the configuration file path and be sure to fill it with your values: ");
			if (result != null && !"".equals(result)) {
				confFilePath = result;
			}

			tproxy.execute(LOAD_CONF,
					"please please plug and setup all the devices described in the configuration file: ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		confReader = ConfigurationFileReader.getInstance(pathFile);
		ZCLFrameImpl.minHeaderSize = confReader.getHeaderMinSize();
	}

	public static TestStepLauncher launch(String pathFile, BundleContext bc) {
		if (instance == null) {
			instance = new TestStepLauncher(pathFile, bc);
		}
		return instance;
	}

	public ConfigurationFileReader getConfReader() {
		return confReader;
	}

	private String readFile(String file) throws IOException {
		String result = "";
		try {
			String line;
			File myFile = new File(file);
			BufferedReader inFile = new BufferedReader(new FileReader(myFile));
			while ((line = inFile.readLine()) != null) {
				result += line;
			}
			inFile.close();
		} catch (IOException e) {
			System.out.println("problem with file");
		}

		return result;
	}
}
