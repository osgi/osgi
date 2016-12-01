/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.osgi.test.cases.subsystem.junit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;

import junit.framework.TestCase;

public class LifecycleSubsystemTests_SL implements ServiceListener {
	/**
	 * 
	 */
	private final LifecycleSubsystemTests tests;
	final Map<Long, LifecycleSubsystemTests_SL> sls;
	final Map<Long, LifecycleSubsystemTests_BL> bls;
	final List<SubsystemEventInfo> events = new ArrayList<SubsystemEventInfo>();
	final int captureBundleEvents;

	public LifecycleSubsystemTests_SL(LifecycleSubsystemTests lifecycleSubsystemTests, Map<Long, LifecycleSubsystemTests_SL> sls, Map<Long, LifecycleSubsystemTests_BL> bls) {
		this(lifecycleSubsystemTests, sls, bls, 0xFFFFFFFF);
	}

	public LifecycleSubsystemTests_SL(LifecycleSubsystemTests lifecycleSubsystemTests, Map<Long, LifecycleSubsystemTests_SL> sls, Map<Long, LifecycleSubsystemTests_BL> bls, int captureBundleEvents) {
		tests = lifecycleSubsystemTests;
		this.sls = sls;
		this.bls = bls;
		this.captureBundleEvents = captureBundleEvents;
	}

	public void assertEvents(SubsystemEventInfo... expected) {
		List<SubsystemEventInfo> current;
		synchronized (events) {
			current = new ArrayList<SubsystemEventInfo>(events);
		}
		TestCase.assertEquals("Wrong number of service events.",
				expected.length, current.size());
		for (int i = 0; i < expected.length; i++) {
			assertEquals(i, expected[i], current.get(i));
		}
	}

	private void assertEquals(int index, SubsystemEventInfo expected, SubsystemEventInfo actual) {
		TestCase.assertEquals("Wrong subsystem state: " + index,
				expected.state, actual.state);
		TestCase.assertEquals("Wrong subsystem id: " + index,
				expected.subsystemID, actual.subsystemID);
		TestCase.assertEquals("Wrong service event type: " + index,
				expected.eventType, actual.eventType);
	}

	public void clear() {
		synchronized (events) {
			events.clear();	
		}
	}

	public void serviceChanged(ServiceEvent event) {
		if (event.getType() == ServiceEvent.REGISTERED) {
			if (Subsystem.State.INSTALLING.equals(event.getServiceReference().getProperty(SubsystemConstants.SUBSYSTEM_STATE_PROPERTY))){
				String type = (String) event.getServiceReference().getProperty(SubsystemConstants.SUBSYSTEM_TYPE_PROPERTY);
				if (SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION.equals(type) || SubsystemConstants.SUBSYSTEM_TYPE_COMPOSITE.equals(type)) {
					if (!sls.containsKey(event.getServiceReference().getProperty(SubsystemConstants.SUBSYSTEM_ID_PROPERTY))) {
						LifecycleSubsystemTests_SL sl = new LifecycleSubsystemTests_SL(tests, sls, bls, captureBundleEvents);
						LifecycleSubsystemTests_BL bl = new LifecycleSubsystemTests_BL(captureBundleEvents);
						Long id = (Long) event.getServiceReference().getProperty(SubsystemConstants.SUBSYSTEM_ID_PROPERTY);
						sls.put(id, sl);
						bls.put(id, bl);
						Subsystem subsystem = (Subsystem) tests.getContext()
								.getService(event.getServiceReference());
						tests.addServiceListener(subsystem.getBundleContext(),
								sl,
								LifecycleSubsystemTests.subsystemFilter);
						tests.addBundleListener(subsystem.getBundleContext(),
								bl);
						tests.getContext()
								.ungetService(event.getServiceReference());
					}
				}
			}
		}
		synchronized (events) {
			SubsystemEventInfo info = new SubsystemEventInfo(event);
			// we only want to record the event if it change the state from a 
			// previous event for the subsystem id.
			boolean found = false;
			for (int i = events.size() - 1; i >=0; i--) {
				SubsystemEventInfo previous = events.get(i);
				if (previous.subsystemID == info.subsystemID) {
					if (previous.eventType == info.eventType && previous.state.equals(info.state)) {
						found = true;
					}
					break;
				}
			}
			if (!found) {
				events.add(info);
			}
		}
	}
	
}