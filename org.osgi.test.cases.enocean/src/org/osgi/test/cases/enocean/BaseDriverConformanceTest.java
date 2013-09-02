
package org.osgi.test.cases.enocean;

import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class BaseDriverConformanceTest extends DefaultTestBundleControl {

	protected void setUp() throws Exception {
	}

	protected void tearDown() throws Exception {
	}

	public void testMessageParsing() {
		// TODO: test how injecting a raw message from a mock serial port
		// gets translated into a proper interpretation
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