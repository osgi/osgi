package org.osgi.test.cases.device.tbc.locators;

import org.osgi.test.cases.device.tbc.TestBundleControl;
import java.io.InputStream;
import java.io.IOException;
import java.util.Dictionary;

/**
 * Simulates crash in findDrivers method
 * 
 * @author ProSyst
 * @version 1.0
 */
public class DriverLoadingLocator3 implements
		org.osgi.service.device.DriverLocator {
	private TestBundleControl	master	= null;

	public DriverLoadingLocator3(TestBundleControl master) {
		this.master = master;
	}

	/**
	 * Crashes to test the exception handling for locators
	 * 
	 * @throws RuntimeException every time
	 */
	public String[] findDrivers(Dictionary props) {
		throw new RuntimeException(
				"[findDrivers] Testing the device manager exception handling");
	}

	/**
	 * Should not be called so the call will cause test case failure
	 * 
	 * @returns null
	 */
	public InputStream loadDriver(String id) throws IOException {
		master.log("[Exception throwing locator]",
				"loaddDriver called with ID " + id + " error!!!!");
		return null;
	}
}