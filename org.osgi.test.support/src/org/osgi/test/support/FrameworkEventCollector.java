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

import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

public class FrameworkEventCollector extends EventCollector<FrameworkEvent>
		implements
		FrameworkListener {
	private final int	mask;

	public FrameworkEventCollector(int typeMask) {
		this.mask = typeMask;
	}

	@Override
	public void frameworkEvent(FrameworkEvent event) {
		if ((event.getType() & mask) != 0)
			addEvent(event);
	}

	@Override
	public Comparator<FrameworkEvent> getComparator() {
		return new Comparator<FrameworkEvent>() {
			@Override
			public int compare(FrameworkEvent event1, FrameworkEvent event2) {
				 
				long lresult = event1.getBundle().getBundleId() - event2.getBundle().getBundleId();
				if (lresult < 0) {
					return -1;
				}
				if (lresult > 0) {
					return 1;
				}
				int result = event1.getType() - event2.getType();
				if (result != 0) {
					return result;
				}
				Throwable t1 = event1.getThrowable();
				Throwable t2 = event2.getThrowable();
				
				if (t1 == t2) {
					return 0;
				}
				if (t1 == null) {
					return -1;
				}
				if (t2 == null) {
					return 1;
				}
				
				return t1.getClass().getName().compareTo(t2.getClass().getName());
			}
		};
	}
}
