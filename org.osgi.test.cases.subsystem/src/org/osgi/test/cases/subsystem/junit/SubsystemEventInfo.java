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

import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.Subsystem.State;
import org.osgi.service.subsystem.SubsystemConstants;

public class SubsystemEventInfo {
	final Subsystem.State state;
	final Long subsystemID;
	final int eventType;

	public SubsystemEventInfo(ServiceEvent event) {
		this.state = (State) event.getServiceReference().getProperty(SubsystemConstants.SUBSYSTEM_STATE_PROPERTY);
		this.eventType = event.getType();
		this.subsystemID = (Long) extractRefernce(event).getProperty(SubsystemConstants.SUBSYSTEM_ID_PROPERTY);
	}

	public SubsystemEventInfo(State state, Long id, int eventType) {
		this.state = state;
		this.subsystemID = id;
		this.eventType = eventType;
	}

	@SuppressWarnings("unchecked")
	private ServiceReference<Subsystem> extractRefernce(ServiceEvent event) {
		return (ServiceReference<Subsystem>) event.getServiceReference();
	}

	public String toString() {
		return "subsystemID:" + subsystemID + " state:" + state + " eventType:"+ eventType;
	}
}
