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

import static org.osgi.test.support.OSGiTestCase.fail;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class EventCollector<T> {
	private final List<T>	events	= new ArrayList<T>();

	synchronized protected void addEvent(T event) {
		events.add(event);
		notifyAll();
	}
	
	synchronized public void clear() {
		events.clear();
	}

	synchronized public List<T> getList(int expectedCount, long waitTime) {
		final long endTime = System.currentTimeMillis() + waitTime;
		while (events.size() < expectedCount) {
			if (waitTime <= 0) {
				break; // timeout has elapsed
			}
			try {
				wait(waitTime);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				fail("Unexpected interruption.", e);
			}
			waitTime = endTime - System.currentTimeMillis();
		}
		List<T> result = new ArrayList<T>(events);
		clear();
		return result;
	}

	public List<T> getListSorted(int expectedCount, long waitTime) {
		List<T> result = getList(expectedCount, waitTime);
		Collections.sort(result, getComparator());
		return result;
	}

	public abstract Comparator<T> getComparator();
}
