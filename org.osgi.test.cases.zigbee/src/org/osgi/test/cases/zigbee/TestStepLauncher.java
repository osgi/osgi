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
	private static TestStepLauncher instance;

	static public final String CONF_FILE_PATH = "file path";
	static public final String LOAD_CONF = "load conf";
	private String confFilePath = "template.xml";
	private ConfigurationFileReader confReader;

	private TestStepLauncher(String pathFile, BundleContext bc) {
		TestStepProxy tproxy = new TestStepProxy(bc);
		String stringFile;
		try {
			stringFile = readFile(pathFile);
			String result = tproxy
					.execute(
							CONF_FILE_PATH + stringFile,
							"please type the configuration file path and be sure to fill it with your values: ");
			if (result != null && !"".equals(result)) {
				confFilePath = result;
			}

			tproxy.execute(
					LOAD_CONF,
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
