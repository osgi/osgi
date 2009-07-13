/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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

package org.osgi.test.cases.blueprint.framework;
import java.util.Dictionary;
import java.util.Map;

import org.osgi.framework.Bundle;

import org.osgi.service.blueprint.container.BlueprintEvent;
import org.osgi.service.event.Event;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.TestUtil;

import junit.framework.AssertionFailedError;

/**
 * A component assertion event broadcast by our AssertionService
 * via the EventAdmin service.
 */
public class ReplayAssertion extends ComponentAssertion {
    protected BlueprintContainerEvent replayedEvent;

    /**
     * Create an assertion with must an assertion type
     *
     * @param topic
     *               The assertion type.
     */
    public ReplayAssertion(String componentId, BlueprintContainerEvent event) {
        super(componentId, AssertionService.LISTENER_REPLAY);
        this.replayedEvent = event;
        // make sure this is turned on for matching/validation
        replayedEvent.setReplay(true);
    }

    /**
     * Method used for bundle injection when attached to an EventSet.
     *
     * @param bundle The bundle we're associated with.
     */
    public void setBundle(Bundle bundle) {
        super.setBundle(bundle);
        // set the the bundle for the target event also
        replayedEvent.setBundle(bundle);
    }

    /**
     * Perform a comparison between two assertions
     *
     * @param other  The other assertion.
     *
     * @return True if these assertions match on the required properties.
     */
    public boolean matches(TestEvent o) {
        if (!super.matches(o)) {
            return false;
        }

        BlueprintEvent e = (BlueprintEvent)((ComponentAssertion)o).getProperty(AssertionService.REPLAY_EVENT);

        if (e == null) {
            return false;
        }

        // transform into an internal event for processing
        BlueprintContainerEvent ce = BlueprintContainerEvent.createContainerEvent(e);

        // compare the two contained events for a match.
        return replayedEvent.matches(ce);
    }


    /**
     * Do any event-specific validation of a received event.  If the
     * event is not good, then a TestEvent instance that can be added
     * to the failure list should be returned.  A validation failure will
     * terminate processing of this event.
     *
     * @param received The received event.
     *
     * @return A TestEvent instance used to raise a deferred failure or null if
     *         this was ok.
     */
    public TestEvent validate(TestEvent received) {
        // this should be true, since we matched, but don't assume
        if (!(received instanceof ComponentAssertion)) {
            return null;
        }

        BlueprintEvent e = (BlueprintEvent)((ComponentAssertion)received).getProperty(AssertionService.REPLAY_EVENT);

        if (e != null) {
            // transform into an internal event for processing
            BlueprintContainerEvent ce = BlueprintContainerEvent.createContainerEvent(e);
            // compare the two contained events for a match.
            replayedEvent.validate(ce);
        }
        // allow the superclass to validate this (which includes calling potential
        // listeners
        return super.validate(received);
    }
}
