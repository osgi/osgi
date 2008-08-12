package org.osgi.test.cases.device.tbc.selectors;

import org.osgi.framework.*;
import org.osgi.service.device.Match;
import org.osgi.service.device.DriverSelector;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * A selector used in the driver loading test. It will select the winner form
 * three drivers.
 * 
 * @author ProSyst
 * @version 1.0
 */
public class DriverLoadingTestSelector1 implements DriverSelector {
	private TestBundleControl	master	= null;

	public DriverLoadingTestSelector1(TestBundleControl master) {
		this.master = master;
	}

	/**
	 * Logs the drivers from the passed matches list and returns the index of
	 * the winner driver.
	 * 
	 * @param reference the reference to the device service
	 * @param match an array of successfull matches
	 * @returns the index of the winner
	 */
	public int select(ServiceReference reference, Match[] matches) {
		logDriverList(matches);
		for (int i = 0; i < matches.length; i++) {
			if ("Driver_Winner".equals((String) matches[i].getDriver()
					.getProperty("DRIVER_ID"))) {
				log("winner selected");
				return i;
			}
		}
		// return incorrect value to use the default selection algorithm
		// this is just in case - the test will fail
		return -1;
	}

	/* logs a list of drivers after sorting them by their IDs */
	private void logDriverList(Match[] matches) {
		boolean moved = true;
		Match buffer = null;
		while (moved) {
			moved = false;
			for (int i = 0; i < matches.length - 1; i++) {
				String driver1 = (String) matches[i].getDriver().getProperty(
						"DRIVER_ID");
				String driver2 = (String) matches[i + 1].getDriver()
						.getProperty("DRIVER_ID");
				if (driver1.compareTo(driver2) > 0) {
					buffer = matches[i];
					matches[i] = matches[i + 1];
					matches[i + 1] = buffer;
					moved = true;
				}
			}
		}
		String list = "";
		for (int i = 0; i < matches.length; i++) {
			list += (String) matches[i].getDriver().getProperty("DRIVER_ID")
					+ " ";
		}
		list.trim();
		log("Loaded drivers: " + list);
	}

	private void log(String toLog) {
		//    master.logRemark("DriverLoadingTestSelector1", toLog);
	}
}