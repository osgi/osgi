/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.enocean;

import java.util.Collection;

import org.osgi.framework.ServiceReference;
import org.osgi.service.enocean.EnOceanDevice;
import org.osgi.service.enocean.EnOceanHost;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescriptionSet;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescriptionSet;
import org.osgi.service.event.EventAdmin;
import org.osgi.test.cases.enocean.utils.EventListener;
import org.osgi.test.cases.enocean.utils.Fixtures;
import org.osgi.test.cases.enocean.utils.ServiceListener;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.step.TestStepProxy;

/**
 * Abstract class that specify the setup(), and teardown() methods for the
 * EnOcean test cases.
 * 
 * @author $Id$
 */
public abstract class AbstractEnOceanTestCase extends DefaultTestBundleControl {

    static public final String PLUG_DONGLE = "Plug the EnOcean USB dongle";
    static public final String MSG_EXAMPLE_1A = "MessageExample1_A";
    static public final String MSG_EXAMPLE_1B = "MessageExample1_B";
    static public final String MSG_EXAMPLE_2 = "MessageExample2_";
    static public final String MDSET_WITH_MD = "EnOceanMessageDescriptionSet_with_an_EnOceanMessageDescription";
    static public final String CDSET_WITH_CD = "EnOceanChannelDescriptionSet_with_an_EnOceanChannelDescription_CID";
    static public final String GET_EVENT = "Get_the_event_that_the_base_driver_should_have_received";

    /**
     * The manual test steps are sent to the test step proxy.
     */
    protected TestStepProxy testStepProxy;

    /** devices */
	protected ServiceListener<EnOceanDevice>	devices;

    /** events */
    protected EventListener events;

    /** eventAdminRef */
	protected ServiceReference<EventAdmin>					eventAdminRef;
    /** eventAdmin */
    protected EventAdmin eventAdmin;

    /** enOceanMessageDescriptionSets */
	protected ServiceListener<EnOceanMessageDescriptionSet>	enOceanMessageDescriptionSets;

    /** enOceanChannelDescriptionSets */
	protected ServiceListener<EnOceanChannelDescriptionSet>	enOceanChannelDescriptionSets;

    /** enOceanHost */
	protected ServiceListener<EnOceanHost>					enOceanHostServiceListener;

    protected void setUp() throws Exception {
	sleep(1000);
	tlog("enter setUp");
	this.testStepProxy = new TestStepProxy(super.getContext());

	/* Tracks device creation */
	devices = new ServiceListener<>(getContext(), EnOceanDevice.class);

	/* Tracks device events */
	String[] topics = new String[] { Fixtures.SELF_TEST_EVENT_TOPIC };
	events = new EventListener(getContext(), topics, null);

	enOceanMessageDescriptionSets = new ServiceListener<>(getContext(),
		EnOceanMessageDescriptionSet.class);

	enOceanChannelDescriptionSets = new ServiceListener<>(getContext(),
		EnOceanChannelDescriptionSet.class);

	enOceanHostServiceListener = new ServiceListener<>(getContext(),
			EnOceanHost.class);

	/* Get a global eventAdmin handle */
	eventAdminRef = getContext().getServiceReference(EventAdmin.class);
	eventAdmin = getContext().getService(eventAdminRef);
	tlog("exit setUp");
    }

    protected void tearDown() throws Exception {
	tlog("enter tearDown");
	this.testStepProxy.close();
	getContext().ungetService(eventAdminRef);
	devices.close();
	enOceanHostServiceListener.close();
	events.close();
	enOceanMessageDescriptionSets.close();
	enOceanChannelDescriptionSets.close();
	Collection<ServiceReference<EnOceanDevice>> deviceRefs = getContext().getServiceReferences(
		EnOceanDevice.class, null);
	for (ServiceReference<EnOceanDevice> deviceRef : deviceRefs) {
		EnOceanDevice device = getContext().getService(deviceRef);
		tlog("unregistering device : " + device);
		device.remove();
	}
	tlog("exit tearDown");
    }

    /**
     * Logs a message with the current class name and current thread
     * 
     * @param message
     */
    protected void tlog(String message) {
	String cls = getClass().getName();
	cls = cls.substring(cls.lastIndexOf('.') + 1);
	DefaultTestBundleControl.log("[" + cls + "," + Thread.currentThread().getName() + "] "
		+ message);
    }

}
