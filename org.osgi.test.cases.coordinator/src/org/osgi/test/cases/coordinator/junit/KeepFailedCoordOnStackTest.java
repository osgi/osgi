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
 * Coordinations can be failed from any thread. This is necessary to allow for
 * asynchronous failures such as timeouts or bundle stoppage. However, failed
 * coordinations must not be removed from the stack because this could hide the
 * failure in the case of a thread local coordination. Moreover, in the case of
 * nested coordinations, participants could be added to the wrong coordination.
 */
public class KeepFailedCoordOnStackTest extends CoordinatorTest {
	/**
	 * The basic test.
	 * @throws InterruptedException
	 */
	public void testBasic() throws InterruptedException {
		final Coordination c = coordinator.begin("c", 0);
		assertAtTopOfStack(c);
		Thread thread = new Thread() {
			public void run() {
				c.fail(Coordination.TIMEOUT);
			}
		};
		thread.start();
		thread.join();
		assertTerminated(c);
		assertFailure(c, Coordination.TIMEOUT);
		assertAtTopOfStack(c);
		assertEndFailed(c, CoordinationException.FAILED, Coordination.TIMEOUT);
		assertEmptyStack();
	}
}
