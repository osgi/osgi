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
package org.osgi.test.cases.coordinator.secure;

/**
 * A basic implementation of TestClassResult.
 */
public class TestClassResultImpl implements TestClassResult {
	private final boolean succeeded;
	
	/**
	 * Create a new TestClassResultImpl with the specified succeeded flag.
	 * @param s true if the test succeeded, false otherwise.
	 */
	public TestClassResultImpl(boolean s) {
		succeeded = s;
	}
	
	public boolean succeeded() {
		return succeeded;
	}
}
