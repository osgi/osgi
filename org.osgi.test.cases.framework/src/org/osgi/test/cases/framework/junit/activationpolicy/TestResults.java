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
package org.osgi.test.cases.framework.junit.activationpolicy;

import java.util.ArrayList;
public class TestResults {
	ArrayList<Object> events = new ArrayList<>();
	
	synchronized public void addEvent(Object event) {
		events.add(event);
		notifyAll();
	}
	synchronized public Object[] getResults(int expectedResultsNumber) {
		while (events.size() < expectedResultsNumber || (expectedResultsNumber == 0 && !isSynchronous())) {
			int currentSize = events.size();
			try {
				wait(5000);
			} catch (InterruptedException e) {
				// do nothing
			}
			if (currentSize == events.size())
				break; // no new events occurred; break out
		}
		Object[] result = events.toArray();
		events.clear();
		return result;
	}

	protected boolean isSynchronous() {
		return false;
	}
}
