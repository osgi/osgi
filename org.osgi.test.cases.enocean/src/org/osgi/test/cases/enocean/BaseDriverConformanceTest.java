
package org.osgi.test.cases.enocean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class BaseDriverConformanceTest extends DefaultTestBundleControl {

	private FileInputStream		inStream;
	private FileOutputStream	outStream;

	protected void setUp() throws Exception {
		File file = new File("/tmp/testdriver");
		if (!file.exists()) {
			file.createNewFile();
		}
		inStream = new FileInputStream(file);
		outStream = new FileOutputStream(file);
	}

	protected void tearDown() throws Exception {
	}

	public void testMessageParsing() throws EnOceanException, IOException {
		outStream.write("Uhelloworld".getBytes());
	}

	public void testDeviceRegistration() {
		// TODO: test how a teach-in message actually ends up creating a proper
		// EnOceanDevice service object, which gets registered.
	}

	public void testMessageDescriptionUse() {
		// TODO: test how a MessageDescriptionSet, once registered, is used
		// by the BaseDriver to build complex EnOceanMessage representations.
	}
}