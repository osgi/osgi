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
package org.osgi.test.support;

import java.util.Comparator;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

public class ServiceEventCollector extends EventCollector<ServiceEvent>
		implements
		ServiceListener {
	private final int	mask;

	public ServiceEventCollector(int typeMask) {
		this.mask = typeMask;
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		if ((event.getType() & mask) != 0)
			addEvent(event);
	}

	@Override
	public Comparator<ServiceEvent> getComparator() {
		return new Comparator<ServiceEvent>() {
			@Override
			public int compare(ServiceEvent event1, ServiceEvent event2) {

				Long id1 = (Long) event1.getServiceReference().getProperty(Constants.SERVICE_ID);
				Long id2 = (Long) event2.getServiceReference().getProperty(Constants.SERVICE_ID);
				long result = id1.longValue() - id2.longValue();
				if (result < 0) {
					return -1;
				}
				if (result > 0) {
					return 1;
				}
				return event1.getType() - event2.getType();
			}
		};
	}
}
