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

import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

public class BundleEventCollector extends EventCollector<BundleEvent> implements
		BundleListener {
	private final int	mask;

	public BundleEventCollector(int typeMask) {
		this.mask = typeMask;
	}

	@Override
	public void bundleChanged(BundleEvent event) {
		if ((event.getType() & mask) != 0)
			addEvent(event);
	}

	@Override
	public Comparator<BundleEvent> getComparator() {
		return new Comparator<BundleEvent>() {
			@Override
			public int compare(BundleEvent event1, BundleEvent event2) {

				long result = event1.getBundle().getBundleId()
						- event2.getBundle().getBundleId();
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
