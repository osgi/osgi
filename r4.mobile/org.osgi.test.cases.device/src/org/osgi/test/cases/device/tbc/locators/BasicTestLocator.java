package org.osgi.test.cases.device.tbc.locators;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import org.osgi.service.device.*;
import org.osgi.test.cases.device.tbc.*;

/**
 * The locator used in the device detection test. For all IDs it retunrs an
 * InputStream to the basic driver (in fact this is the only driver bundle in
 * the whole test case)
 */
public class BasicTestLocator implements DriverLocator {
	private TestBundleControl	master	= null;

	/**
	 * @param master the master of test case - used for logging
	 */
	public BasicTestLocator(TestBundleControl master) {
		this.master = master;
	}

	/**
	 * Searches for drivers
	 * 
	 * @param props the properties of the registed Device service
	 * @retunrs a single element String array containing the property with key
	 *          "deviceID" from the props Dictionary
	 */
	public String[] findDrivers(Dictionary props) {
		log("searching for " + props.get("deviceID"));
		String[] toReturn = new String[1];
		toReturn[0] = (String) props.get("deviceID");
		return toReturn;
	}

	/**
	 * Finds an InputStream to a driver with the passed id. This implementation
	 * returns always InputStream to the same bundle.
	 * 
	 * @param id the id of the driver to be loaded
	 * @return an InputStream to the basicdrv.jar bundle
	 */
	public InputStream loadDriver(String id) {
		log("loading for " + id);
		try {
			return (InputStream) AccessController
					.doPrivileged(new PrivilegedExceptionAction() {
						public Object run() throws Exception {
							URL url = new URL(TestBundleControl.tcHome
									+ "drv1.jar");
							InputStream is = url.openStream();
							return url.openStream();
						}
					});
		}
		catch (PrivilegedActionException e) {
			e.getException().printStackTrace();
			log("error while reading the /drivers/basicdrv.jar resource");
			return null;
		}
	}

	private void log(String toLog) {
		System.out.println(toLog);
	}
}
