package org.osgi.test.cases.device.tbc.locators;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Dictionary;

import org.osgi.service.device.DriverLocator;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * The locator that will find the redirecting driver and later will find the
 * driver pointed from the redirecting dirver.
 * 
 * @author ProSyst
 * @version 1.0
 */
public class RedirectionLocator1 implements DriverLocator {
	final TestBundleControl	master;

	public RedirectionLocator1(TestBundleControl master) {
		this.master = master;
	}

	/**
	 * Searches for drivers
	 * 
	 * @param props the properties of the registed Device service
	 * @returns a one element String array that contains the id of the
	 *          redirecting driver
	 */
	public String[] findDrivers(Dictionary props) {
		master.log("searching for " + props.get("deviceID"));
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
							master.log("loading for " + id);
							if ("redirecting_driver".equals(id)) {
								try {
									URL url = new URL(master.getWebServer()
											+ "drv6.jar");
									return url.openStream();
								}
								catch (Exception e) {
									e.printStackTrace();
									master
											.log("redirection locator 1",
													"error while reading the /drivers/basicdrv.jar resource");
									return null;
								}
							}
							else
								if ("Driver_Winner".equals(id)) {
									try {
										URL url = new URL(
												master.getWebServer()
														+ "drv4.jar");
										return url.openStream();
									}
									catch (Exception e) {
										e.printStackTrace();
										master
												.log("redirection locator 1",
														"error while reading the /drivers/basicdrv.jar resource");
										return null;
									}
								}
								else {
									master.log("redirection locator 1",
											"invalid dirver ID passed " + id);
									return null;
								}
						}
					});
		}
		catch (PrivilegedActionException ex) {
			throw ((IOException) ex.getException());
		}
	}

}