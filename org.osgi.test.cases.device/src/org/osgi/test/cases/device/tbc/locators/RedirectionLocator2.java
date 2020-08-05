package org.osgi.test.cases.device.tbc.locators;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;

import org.osgi.service.device.DriverLocator;

/**
 * When using redirection all registred locators should be called. This locator
 * is used to check this. Does nothing else at all
 * 
 * @author ProSyst
 * @version 1.0
 */
public class RedirectionLocator2 implements DriverLocator {
	public static boolean	called	= false;

	public String[] findDrivers(Dictionary<String, ? > props) {
		return null;
	}

	public InputStream loadDriver(String id) throws IOException {
		called = true;
		return null;
	}
}