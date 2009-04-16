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
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.service.event.Event;

import junit.framework.AssertionFailedError;

/**
 * The base event type handled by the test EventSet class.
 */
public class TestEvent implements BundleAware {
    // a potential event listener for received/not received handling
    protected TestEventListener listener;
    // a potential bundle (which is injected when we're attached to an event set)
    protected Bundle bundle;

    TestEvent() {
        this(null);
    }

    /**
     * Construct a test event with an attached listener.
     *
     * @param listener The attached event listener that will handle events related
     *                 to this expected event.
     */
    TestEvent(TestEventListener listener) {
        this.listener = listener;
    }

    /**
     * Method used for bundle injection when attached to an EventSet.
     *
     * @param bundle The bundle we're associated with.
     */
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Raise an assertion event because an expected event was not
     * received.
     */
    public void failExpected()
    {
        throw new AssertionFailedError("Missing event: " + toString());
    }

    /**
     * Throw an AssertionFailure for an unexpected event.
     */
    public void failUnexpected() {
        throw new AssertionFailedError("Unexpected event received: " + toString());
    }

    /**
     * Raise a deferred failure.
     */
    public void fail() {
        throw new AssertionFailedError("Explicitly prohibited event received: " + toString());
    }

    /**
     * Matching method that the implementing subclasses must override. Note
     * that this relationship is not commutative.  The matching is intended
     * to be executed using an expected event instanced matching against
     * a received event instance.
     *
     * @param other  The other event for comparison purposes.
     *
     * @return true if the events are matches, false otherwise.
     */
    public boolean matches(TestEvent other) {
        // match by identity normally
        return other == this;
    }


    /**
     * Flag an event as being an error situation that will terminate
     * the test if received when not expected.
     *
     * @return Always returns false for the base.
     */
    public boolean isError() {
        return false;
    }


    /**
     * Test if an event is related to a specific bundle.
     *
     * @return Always returns false for the base.
     */
    public boolean isForBundle(Bundle bundle) {
        // we return true by default, since events that are not filtered by bundle
        // need to be processed by all event sets.
        return true;
    }


    /**
     * Broadcast an event received event to any attached listener.
     *
     * @param results  The expected results holding this event.
     * @param received The actual received event.
     */
    public void eventReceived(TestEvent received) {
        // send this along
        if (listener != null) {
            listener.eventReceived(this, received);
        }
    }


    /**
     * Broadcast that an expected event was not received to any listener
     * attached to a specific event.
     *
     * @param results  The expected results holding this event.
     */
    public void eventNotReceived() throws Exception {
        // send this along
        if (listener != null) {
            listener.eventNotReceived(this);
        }
    }


    /**
     * Copy an event's property set.
     *
     * @param event  The event source for the properties.
     *
     * @return A Map containing all of the event properties.
     */
    static protected Map copyProperties(Event event) {
        Map props = new HashMap();
        String[] names = event.getPropertyNames();
        for (int i = 0; i < names.length; i++) {
            props.put(names[i], event.getProperty(names[i]));
        }
        return props;
    }


    /**
     * Set an event listener on this event.
     *
     * @param listener The new listener.  If null, this clears any existing listener.
     */
    public void setEventListener(TestEventListener listener) {
        this.listener = listener;
    }


    /**
     * Get the current event listener.
     *
     * @return The current listener attached to this event.  Returns null if there
     *         is no listener currently attached.
     */
    public TestEventListener getEventListener() {
        return listener;
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
        // if we have an attached listener, give it an opportunity to validate
        // this event information.  Otherwise, we give it a pass.
        if (listener != null) {
            return listener.validateEvent(this, received);
        }
        return null;
    }
}
