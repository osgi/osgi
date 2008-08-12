package org.osgi.test.cases.device.tbc.selectors;

import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Match;
import org.osgi.service.device.DriverSelector;
import org.osgi.test.cases.device.tbc.TestBundleControl;

/**
 * Second selector. As stated in the specification this selector should be
 * ignored
 * 
 * @author ProSyst
 * @version 1.0
 */
public class DriverLoadingTestSelector2 implements DriverSelector {
	private TestBundleControl	master	= null;

	public DriverLoadingTestSelector2(TestBundleControl master) {
		this.master = master;
	}

	public int select(ServiceReference reference, Match[] matches) {
		master.log("[Driver loading test selector 2]",
				"second selector called! Error!");
		throw new RuntimeException(
				"[Driver loading test selector 2] second selector called! Error!");
	}
}