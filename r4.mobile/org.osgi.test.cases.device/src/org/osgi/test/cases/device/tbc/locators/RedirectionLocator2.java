package org.osgi.test.cases.device.tbc.locators;

import java.io.*;
import java.util.*;
import org.osgi.service.device.*;

/**
 * When using redirection all registred locators should be called. This locator
 * is used to check this. Does nothing else at all
 * 
 * @author ProSyst
 * @version 1.0
 */
public class RedirectionLocator2 implements DriverLocator {
	public static boolean	called	= false;

	public String[] findDrivers(Dictionary props) {
		return null;
	}

	public InputStream loadDriver(String id) throws IOException {
		called = true;
		return null;
	}
}