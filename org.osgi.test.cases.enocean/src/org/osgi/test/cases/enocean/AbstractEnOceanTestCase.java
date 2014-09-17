
package org.osgi.test.cases.enocean;

import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescriptionSet;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescriptionSet;
import org.osgi.service.event.EventAdmin;
import org.osgi.test.cases.enocean.utils.EventListener;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;
import org.osgi.test.cases.enocean.utils.Utils;
import org.osgi.test.cases.enoceansimulation.teststep.TestStep;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Abstract class that specify the setup(), and teardown() methods for the
 * EnOcean test cases.
 */
public abstract class AbstractEnOceanTestCase extends DefaultTestBundleControl {

	/** testStepServiceRef */
	protected ServiceReference					testStepServiceRef;
	/** testStepService */
	protected TestStep							testStepService;

	/** devices */
	protected ServiceListener					devices;

	/** events */
	protected EventListener						events;

	/** eventAdminRef */
	protected ServiceReference					eventAdminRef;
	/** eventAdmin */
	protected EventAdmin						eventAdmin;

	/** enOceanMessageDescriptionSets */
	protected ServiceListener					enOceanMessageDescriptionSets;

	/** enOceanChannelDescriptionSets */
	protected ServiceListener					enOceanChannelDescriptionSets;

	protected void setUp() throws Exception {
		/*
		 * Gets the currently registered EnOceanHost and access its streams. Get
		 * testStepService.
		 */
		testStepServiceRef = getContext().getServiceReference(TestStep.class.getName());
		testStepService = (TestStep) getContext().getService(testStepServiceRef);

		/* Tracks device creation */
		devices = new ServiceListener(getContext(), EnOceanDevice.class);

		/* Tracks device events */
		String[] topics = new String[] {Fixtures.SELF_TEST_EVENT_TOPIC};
		events = new EventListener(getContext(), topics, null);

		enOceanMessageDescriptionSets = new ServiceListener(getContext(), EnOceanMessageDescriptionSet.class);

		enOceanChannelDescriptionSets = new ServiceListener(getContext(), EnOceanChannelDescriptionSet.class);

		/* Get a global eventAdmin handle */
		eventAdminRef = getContext().getServiceReference(EventAdmin.class.getName());
		eventAdmin = (EventAdmin) getContext().getService(eventAdminRef);
	}

	protected void tearDown() throws Exception {
		getContext().ungetService(eventAdminRef);
		devices.close();
		events.close();
		enOceanMessageDescriptionSets.close();
		enOceanChannelDescriptionSets.close();
		ServiceReference[] deviceRefs = getContext().getServiceReferences(EnOceanDevice.class.getName(), null);
		if (deviceRefs != null) {
			for (int i = 0; i < deviceRefs.length; i++) {
				EnOceanDevice device = (EnOceanDevice) getContext().getService(deviceRefs[i]);
				log("EnOceanTestCase: unregistering device : '" + Utils.bytesToHex(Utils.intTo4Bytes(device.getChipId())) + "'");
				device.remove();
			}
		}
	}
}
