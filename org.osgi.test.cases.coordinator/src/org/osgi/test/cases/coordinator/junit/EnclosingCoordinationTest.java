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
import org.osgi.service.coordinator.CoordinationException;

/**
 * A coordination must provide the coordination within which it is nested when
 * applicable. A coordination's parent is the one immediately below it, if any,
 * on the thread local stack.
 */
public class EnclosingCoordinationTest extends CoordinatorTest {
	/**
	 * The basic test.
	 */
	public void testBasic() {
		Coordination c1 = coordinator.begin("c1", 0);
		// c1 is at the bottom of the stack.
		assertEnclosingCoordination(c1, null);
		Coordination c2 = coordinator.begin("c2", 0);
		// c2 is now on top of c1.
		assertEnclosingCoordination(c2, c1);
		Coordination c3 = coordinator.create("c3", 0);
		// c3 is not on the stack.
		assertEnclosingCoordination(c3, null);
		c3.push();
		// c3 is now on top of c2 on the stack.
		assertEnclosingCoordination(c3, c2);
		coordinator.pop();
		// c3 is now off the stack.
		assertEnclosingCoordination(c3, null);
		c2.fail(new Exception());
		// c2 is failed but still on the stack.
		assertEnclosingCoordination(c2, c1);
		assertEndFailed(c2, CoordinationException.FAILED);
		// c2 is ended and off the stack.
		assertEnclosingCoordination(c2, null);
		c1.end();
	}
}
