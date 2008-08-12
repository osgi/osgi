package org.osgi.test.cases.device.tbc.locators;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import org.osgi.service.device.*;
import org.osgi.test.cases.device.tbc.*;

/**
 * Locator used in the default selection test. Finds 3 drivers
 * 
 * @author ProSyst
 * @version 1.0
 */
public class DefaultSelectionLocator implements DriverLocator {
	private TestBundleControl	master	= null;

	/**
	 * @param master the master of test case - used for logging
	 */
	public DefaultSelectionLocator(TestBundleControl master) {
		this.master = master;
	}

	/**
	 * Searches for drivers
	 * 
	 * @param props the properties of the registed Device service
	 * @retunrs a String array of 3 elements - the IDs of the drivers that will
	 *          be filtered from the default selection algorythm
	 */
	public String[] findDrivers(Dictionary props) {
		log("searching for " + props.get("deviceID"));
		String[] toReturn = new String[3];
		toReturn[0] = "Driver_Winner";
		toReturn[1] = "Driver_Common";
		toReturn[2] = "Driver_Concurent";
		return toReturn;
	}

	/**
	 * Finds an InputStream to a driver with the passed id. This implementation
	 * recognizes the IDs of the drivers that are loaded in the default
	 * selection test.
	 * 
	 * @param id the id of the driver to be loaded
	 * @return an InputStream to the driver bundle corresponding to the passed
	 *         id.
	 */
	public InputStream loadDriver(final String id) throws IOException {
		log("loading for " + id);
		try {
			return (InputStream) AccessController
					.doPrivileged(new PrivilegedExceptionAction() {
						public Object run() throws Exception {
							if ("Driver_Winner".equals(id)) {
								URL url = new URL(TestBundleControl.tcHome
										+ "drv4.jar");
								return url.openStream();
							}
							else
								if ("Driver_Common".equals(id)) {
									URL url = new URL(TestBundleControl.tcHome
											+ "drv2.jar");
									return url.openStream();
								}
								else
									if ("Driver_Concurent".equals(id)) {
										URL url = new URL(
												TestBundleControl.tcHome
														+ "drv3.jar");
										return url.openStream();
									}
									else {
										log("unknown driver ID passed " + id);
										return null;
									}
						}
					});
		}
		catch (PrivilegedActionException ex) {
			throw ((IOException) ex.getException());
		}
	}

	private void log(String toLog) {
		System.out.println(toLog);
	}
}