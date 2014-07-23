
package org.osgi.test.cases.enocean;

import java.io.InputStream;
import java.io.OutputStream;
import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.event.EventAdmin;
import org.osgi.test.cases.enocean.descriptions.EnOceanChannelDescription_TMP_00;
import org.osgi.test.cases.enocean.descriptions.EnOceanMessageDescription_A5_02_01;
import org.osgi.test.cases.enocean.sets.EnOceanChannelDescriptionSetImpl;
import org.osgi.test.cases.enocean.sets.EnOceanMessageDescriptionSetImpl;
import org.osgi.test.cases.enocean.utils.EventListener;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;
import org.osgi.test.cases.enocean.utils.Utils;
import org.osgi.test.cases.enoceansimulation.EnOceanInOut;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 *
 */
public abstract class EnOceanTestCase extends DefaultTestBundleControl {

	/** devices */
	protected ServiceListener					devices;
	/** events */
	protected EventListener						events;

	/** msgDescriptionSet */
	protected EnOceanMessageDescriptionSetImpl	msgDescriptionSet;
	/** channelDescriptionSet */
	protected EnOceanChannelDescriptionSetImpl	channelDescriptionSet;
	/** eventAdminRef */
	protected ServiceReference					eventAdminRef;
	/** enOceanInOutRef */
	protected ServiceReference					enOceanInOutRef;
	/** inputStream */
	protected InputStream						inputStream;
	/** outputStream */
	protected OutputStream						outputStream;
	/** enOceanInOut */
	protected EnOceanInOut						enOceanInOut;
	/** eventAdmin */
	protected EventAdmin						eventAdmin;

	protected void setUp() throws Exception {

		/* Gets the currently registered EnOceanHost and access its streams */
		enOceanInOutRef = getContext().getServiceReference(EnOceanInOut.class.getName());
		enOceanInOut = (EnOceanInOut) getContext().getService(enOceanInOutRef);
		inputStream = enOceanInOut.getInputStream();
		outputStream = enOceanInOut.getOutputStream();

		/* Tracks device creation */
		devices = new ServiceListener(getContext(), EnOceanDevice.class);

		/* Tracks device events */
		String[] topics = new String[] {Fixtures.SELF_TEST_EVENT_TOPIC};
		events = new EventListener(getContext(), topics, null);

		/* Get a global eventAdmin handle */
		eventAdminRef = getContext().getServiceReference(EventAdmin.class.getName());
		eventAdmin = (EventAdmin) getContext().getService(eventAdminRef);

		/* Inserts some message documentation classes */
		msgDescriptionSet = new EnOceanMessageDescriptionSetImpl();
		msgDescriptionSet.putMessage(Fixtures.RORG, Fixtures.FUNC, Fixtures.TYPE_1, -1, new EnOceanMessageDescription_A5_02_01());

		channelDescriptionSet = new EnOceanChannelDescriptionSetImpl();
		channelDescriptionSet.putChannelDescription(Fixtures.TMP_CHANNEL_ID, new EnOceanChannelDescription_TMP_00());
	}

	protected void tearDown() throws Exception {
		getContext().ungetService(eventAdminRef);
		getContext().ungetService(enOceanInOutRef);
		devices.close();
		events.close();
		enOceanInOut.resetBuffers();
		ServiceReference[] deviceRefs = getContext().getServiceReferences(EnOceanDevice.class.getName(), null);
		if (deviceRefs != null) {
			for (int i = 0; i < deviceRefs.length; i++) {
				EnOceanDevice device = (EnOceanDevice) getContext().getService(deviceRefs[i]);
				log("unregistering device : '" + Utils.bytesToHex(Utils.intTo4Bytes(device.getChipId())) + "'");
				device.remove();
			}
		}
	}
}
