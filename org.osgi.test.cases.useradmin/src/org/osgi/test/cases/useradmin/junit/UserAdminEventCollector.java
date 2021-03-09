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
/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.useradmin.junit;

import java.util.Comparator;

import org.osgi.framework.ServiceReference;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.UserAdminEvent;
import org.osgi.service.useradmin.UserAdminListener;
import org.osgi.test.support.EventCollector;

public class UserAdminEventCollector extends EventCollector<UserAdminEvent>
		implements
		UserAdminListener {
	private final int	mask;

	public UserAdminEventCollector(int typeMask) {
		this.mask = typeMask;
	}

	public void roleChanged(UserAdminEvent event) {
		if ((event.getType() & mask) != 0)
			addEvent(event);
	}

	public Comparator<UserAdminEvent> getComparator() {
		return new Comparator<UserAdminEvent>() {
			public int compare(UserAdminEvent event1, UserAdminEvent event2) {

				ServiceReference< ? > ref1 = event1.getServiceReference();
				ServiceReference< ? > ref2 = event2.getServiceReference();
				int result = ref1.compareTo(ref2);
				if (result != 0) {
					return result;
				}
				Role role1 = event1.getRole();
				Role role2 = event2.getRole();

				result = role1.getName().compareTo(role2.getName());
				if (result != 0) {
					return result;
				}
				result = role1.getType() - role2.getType();
				if (result != 0) {
					return result;
				}

				result = event1.getType() - event2.getType();
				return result;
			}
		};
	}
}
