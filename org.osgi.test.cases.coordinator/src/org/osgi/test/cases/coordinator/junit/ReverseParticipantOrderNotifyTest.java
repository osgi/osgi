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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Participant;

/**
 * Participants must be notified in reverse participation order when ending or
 * failing a coordination.
 *
 * https://www.osgi.org/members/bugzilla/show_bug.cgi?id=2273
 */
public class ReverseParticipantOrderNotifyTest extends CoordinatorTest {
	private static class TestParticipant implements Participant {
		private final List<Participant> participants;

		public TestParticipant(List<Participant> participants) {
			this.participants = participants;
		}

		public void ended(Coordination coordination) throws Exception {
			participants.add(this);
		}

		public void failed(Coordination coordination) throws Exception {
			participants.add(this);
		}
	}

	/**
	 * Tests that participants are notified in reverse participation order when
	 * ending a coordination.
	 */
	public void testReverseParticipantOrderNotifyEnded() {
		List<Participant> before = new ArrayList<Participant>();
		List<Participant> after = Collections.synchronizedList(new ArrayList<Participant>());
		Coordination c = coordinator.create("c", 0);
		for (int i = 0; i < 10; i++) {
			Participant p = new TestParticipant(after);
			before.add(p);
			c.addParticipant(p);
		}
		c.end();
		assertReverseParticipationOrder(before, after);
	}

	/**
	 * Tests that participants are notified in reverse participation order when
	 * failing a coordination.
	 */
	public void testReverseParticipantOrderNotifyFailed() {
		List<Participant> before = new ArrayList<Participant>();
		List<Participant> after = Collections.synchronizedList(new ArrayList<Participant>());
		Coordination c = coordinator.begin("c", 0);
		for (int i = 0; i < 10; i++) {
			Participant p = new TestParticipant(after);
			before.add(p);
			c.addParticipant(p);
		}
		c.fail(new Exception());
		assertReverseParticipationOrder(before, after);
	}
	
	private void assertReverseParticipationOrder(List<Participant> before, List<Participant> after) {
		Collections.reverse(before);
		assertEquals("Not notified in reverse participation order", before, after);
	}
}
