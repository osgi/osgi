
package org.osgi.test.cases.enocean;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanHost;
import org.osgi.test.cases.enocean.descriptions.EnOceanChannelDescription_TMP_00;
import org.osgi.test.cases.enocean.descriptions.EnOceanMessageDescription_A5_02_01;
import org.osgi.test.cases.enocean.sets.EnOceanChannelDescriptionSetImpl;
import org.osgi.test.cases.enocean.sets.EnOceanMessageDescriptionSetImpl;
import org.osgi.test.cases.enocean.utils.EventListener;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

public class PersistencyTests extends DefaultTestBundleControl {

	private ServiceListener		devices;
	private EventListener		events;
	private EnOceanMessageDescriptionSetImpl	msgDescriptionSet;
	private EnOceanChannelDescriptionSetImpl	channelDescriptionSet;
	private ServiceReference					eventAdminRef;

	protected void setUp() throws Exception {
		/* Tracks device creation */
		devices = new ServiceListener(getContext(), EnOceanDevice.class);

		/* Tracks device events */
		String[] topics = new String[] {Fixtures.SELF_TEST_EVENT_TOPIC};
		events = new EventListener(getContext(), topics, null);

		/* Inserts some message documentation classes */
		msgDescriptionSet = new EnOceanMessageDescriptionSetImpl();
		msgDescriptionSet.putMessage(Fixtures.RORG, Fixtures.FUNC, Fixtures.TYPE_1, -1, new EnOceanMessageDescription_A5_02_01());

		channelDescriptionSet = new EnOceanChannelDescriptionSetImpl();
		channelDescriptionSet.putChannelDescription(Fixtures.TMP_CHANNEL_ID, new EnOceanChannelDescription_TMP_00());
	}

	protected void tearDown() throws Exception {
		devices.close();
		events.close();
	}

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