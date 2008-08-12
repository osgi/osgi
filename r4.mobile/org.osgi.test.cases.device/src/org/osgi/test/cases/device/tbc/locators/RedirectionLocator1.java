package org.osgi.test.cases.device.tbc.locators;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import org.osgi.service.device.*;
import org.osgi.test.cases.device.tbc.*;

/**
 * The locator that will find the redirecting driver and later will find the
 * driver pointed from the redirecting dirver.
 * 
 * @author ProSyst
 * @version 1.0
 */
public class RedirectionLocator1 implements DriverLocator {
	private TestBundleControl	master	= null;

	public RedirectionLocator1(TestBundleControl master) {
		this.master = master;
	}

	/**
	 * Searches for drivers
	 * 
	 * @param props the properties of the registed Device service
	 * @retunrs a one element String array that contains the id of the
	 *          redirecting driver
	 */
	public String[] findDrivers(Dictionary props) {
		logRemark("searching for " + props.get("deviceID"));
		String[] toReturn = new String[1];
		toReturn[0] = "redirecting_driver";
		return toReturn;
	}

	/**
	 * Finds an InputStream to a driver with the passed id.
	 * 
	 * @param id the id of the driver to be loaded
	 * @return an InputStream to the driver bundle corresponding to the passed
	 *         id or null if unkown id passed
	 */
	public InputStream loadDriver(final String id) throws IOException {
		try {
			return (InputStream) AccessController
					.doPrivileged(new PrivilegedExceptionAction() {
						public Object run() throws Exception {
							logRemark("loading for " + id);
							if ("redirecting_driver".equals(id)) {
								try {
									URL url = new URL(TestBundleControl.tcHome
											+ "drv6.jar");
									return url.openStream();
								}
								catch (Exception e) {
									e.printStackTrace();
									log("error while reading the /drivers/basicdrv.jar resource");
									return null;
								}
							}
							else
								if ("Driver_Winner".equals(id)) {
									try {
										URL url = new URL(
												TestBundleControl.tcHome
														+ "drv4.jar");
										return url.openStream();
									}
									catch (Exception e) {
										e.printStackTrace();
										log("error while reading the /drivers/basicdrv.jar resource");
										return null;
									}
								}
								else {
									log("invalid dirver ID passed " + id);
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
		master.log("redirection locator 1", toLog);
	}

	private void logRemark(String toLog) {
		System.out.println();
	}
}