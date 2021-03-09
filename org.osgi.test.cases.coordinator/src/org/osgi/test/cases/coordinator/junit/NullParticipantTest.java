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
package org.osgi.test.cases.coordinator.junit;

import org.osgi.service.coordinator.Coordination;

/**
 * A coordination must throw a NullPointerException if the participant to be
 * added is null.
 */
public class NullParticipantTest extends CoordinatorTest {
	/**
	 * The basic test.
	 */
	public void testBasic() {
		Coordination c = coordinator.create("c", 0);
		try {
			c.addParticipant(null);
			fail("A runtime exception was not thrown in response to a null participant.");
		}
		catch (RuntimeException e) {
			// Okay.
		}
	}
}
