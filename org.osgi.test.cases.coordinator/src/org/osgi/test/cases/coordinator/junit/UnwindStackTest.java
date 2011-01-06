/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.coordinator.junit;

import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Participant;

/**
 * When ending, if this Coordination has been pushed on the thread local 
 * Coordination stack of this thread but is not the current Coordination, then 
 * the Coordinations on the thread local Coordination stack above this 
 * Coordination must be terminated and removed from the thread local 
 * Coordination stack before this Coordination is terminated. Each of these 
 * Coordinations, starting with the current Coordination, will be terminated 
 * normally . If the termination throws a CoordinationException, then the next 
 * Coordination on the thread local Coordination stack will be terminated as a 
 * failure with a failure cause of the thrown CoordinationException. At the end
 * of this process, this Coordination will be the current Coordination and will
 * have been terminated as a failure if any of the terminated Coordinations 
 * threw a CoordinationException
 */
public class UnwindStackTest extends CoordinatorTest {
	private static class Participator implements Participant {
		static void reset(Coordination[] c) {
			i = 0;
			coords = c;
		}
		
		private static int i;
		private static Coordination[] coords;
		
		public Participator() {
		}

		public void ended(Coordination coordination) throws Exception {
			assertEquals("The coordinations were not terminated in stack order", coords[i++], coordination);
		}

		public void failed(Coordination coordination) throws Exception {
			fail("The coordination should have terminated normally.");
		}
		
		
	}
	/**
	 * Scenario: Three coordinations are pushed onto the thread local stack. 
	 * The coordination at the bottom of the stack (c1) is then ended.
	 * 
	 * c3
	 * c2
	 * c1
	 * 
	 * Expected Result: All coordinations are terminated normally in stack
	 * order. The thread local stack is empty.
	 */
	public void testUnwindFromBottom() {
		Coordination c1 = coordinator.begin("c1", 0);
		assertAtTopOfStack(c1);
		Coordination c2 = coordinator.begin("c2", 0);
		assertAtTopOfStack(c2);
		Coordination c3 = coordinator.begin("c3", 0);
		assertAtTopOfStack(c3);
		Participator.reset(new Coordination[]{c3, c2, c1});
		c1.addParticipant(new Participator());
		c2.addParticipant(new Participator());
		c3.addParticipant(new Participator());
		assertEnd(c1);
		assertTerminatedNormally(c1);
		assertTerminatedNormally(c2);
		assertTerminatedNormally(c3);
		assertEmptyStack();
	}
	
	/**
	 * Scenario: Three coordinations are pushed onto the thread local stack. 
	 * The coordination in the middle of the stack (c2) is then ended.
	 * 
	 * c3
	 * c2
	 * c1
	 * 
	 * Expected Result: c3 and c2 are terminated normally in stack order. c1 is
	 * not terminated. c1 is at the top of the thread local stack.
	 */
	public void testUnwindFromMiddle() {
		Coordination c1 = coordinator.begin("c1", 0);
		assertAtTopOfStack(c1);
		Coordination c2 = coordinator.begin("c2", 0);
		assertAtTopOfStack(c2);
		Coordination c3 = coordinator.begin("c3", 0);
		assertAtTopOfStack(c3);
		Participator.reset(new Coordination[]{c3, c2});
		c2.addParticipant(new Participator());
		c3.addParticipant(new Participator());
		assertEnd(c2);
		assertTerminatedNormally(c3);
		assertTerminatedNormally(c2);
		assertNotTerminated(c1);
		assertAtTopOfStack(c1);
	}
	
	/**
	 * Scenario: Three coordinations are pushed onto the thread local stack. 
	 * The coordination at the top of the stack (c3) is then ended.
	 * 
	 * c3
	 * c2
	 * c1
	 * 
	 * Expected Result: c3 is terminated normally. c2 and c1 are not
	 * terminated. c2 is at the top of the thread local stack.
	 */
	public void testUnwindFromTop() {
		Coordination c1 = coordinator.begin("c1", 0);
		assertAtTopOfStack(c1);
		Coordination c2 = coordinator.begin("c2", 0);
		assertAtTopOfStack(c2);
		Coordination c3 = coordinator.begin("c3", 0);
		assertAtTopOfStack(c3);
		Participator.reset(new Coordination[]{c3});
		c1.addParticipant(new Participator());
		assertEnd(c3);
		assertTerminatedNormally(c3);
		assertNotTerminated(c2);
		assertNotTerminated(c1);
		assertAtTopOfStack(c2);
	}
}
