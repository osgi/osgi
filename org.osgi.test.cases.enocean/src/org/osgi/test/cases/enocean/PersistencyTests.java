
package org.osgi.test.cases.enocean;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.enocean.EnOceanHost;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

public class PersistencyTests extends DefaultTestBundleControl {

	/**
	 * Tests device export persistency.
	 * 
	 * @throws Exception
	 */
	public void testDeviceExportPersistency() throws Exception {
		ServiceRegistration sReg = Fixtures.registerDevice(getContext());

		/* Get CHIP_ID attributed by the driver from the given service PID. */
		ServiceReference hostRef = getContext().getServiceReference(EnOceanHost.class.getName());
		EnOceanHost defaultHost = (EnOceanHost) getContext().getService(hostRef);
		int originalChipId = defaultHost.getChipId(Fixtures.DEVICE_PID);

		Bundle baseDriver = getBaseDriverBundle();
		assertNotNull(baseDriver);

		baseDriver.stop();
		Sleep.sleep(1000 * OSGiTestCaseProperties.getScaling());

		hostRef = getContext().getServiceReference(EnOceanHost.class.getName());
		assertNull(hostRef);

		baseDriver.start();
		Sleep.sleep(1000 * OSGiTestCaseProperties.getScaling());

		hostRef = getContext().getServiceReference(EnOceanHost.class.getName());
		defaultHost = (EnOceanHost) getContext().getService(hostRef);
		int newChipId = defaultHost.getChipId(Fixtures.DEVICE_PID);

		assertEquals(originalChipId, newChipId);

		sReg.unregister();
		getContext().ungetService(hostRef);
	}

	private Bundle getBaseDriverBundle() {
		Bundle[] bundles = getContext().getBundles();
		for (int i = 0; i < bundles.length; i++) {
			Bundle b = bundles[i];
			if (b.getSymbolicName().equals("org.osgi.impl.service.enocean")) {
				return b;
			}
		}
		return null;
	}

}