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
import java.util.Map;

import org.osgi.service.event.Event;
import org.osgi.test.cases.blueprint.services.TestUtil;


/**
 * A wrapper for BlueprintContainer events broadcast via the EventAdmin
 * service.
 */
public class BlueprintContainerEvent extends AdminTestEvent {
    /**
     * Create the event wrapper.
     *
     * @param topic  The expected event topic.
     */
    public BlueprintContainerEvent(String topic) {
        this(topic, null, null);
    }

    /**
     * Create the event wrapper.
     *
     * @param topic  The expected event topic.
     * @param props  A set of properties we expect to receive with the event.
     */
    public BlueprintContainerEvent(String topic, Map props) {
        this(topic, props, null);
    }


    /**
     * Create the event wrapper.
     *
     * @param topic    The expected event topic.
     * @param props    A set of properties we expect to receive with the event.
     * @param listener An event listener we will poke when this event is received.
     */
    public BlueprintContainerEvent(String topic, Map props, TestEventListener listener) {
        super("org/osgi/test/cases/blueprint/BlueprintContainer/" + topic, props, listener);
    }


    /**
     * Create an event wrapper from a received EventAdmin
     * Event.  This is used for comparisons with expected
     * events and also for recording assertion failures.
     *
     * @param event  The received event.
     */
    public BlueprintContainerEvent(Event event) {
        super(event);
    }

    /**
     * Perform a comparison between two events
     *
     * @param other  The other event.
     *
     * @return True if these events match on the required properties.
     */
    public boolean matches(TestEvent o) {
        if (!(o instanceof BlueprintContainerEvent)) {
            return false;
        }

        BlueprintContainerEvent other = (BlueprintContainerEvent)o;

        // fail immediately on a mismatch on id or type
        if (bundle != bundle || !topic.equals(other.topic)) {
            return false;
        }

        // if this event is expecting some properties, then compare each of the properties.
        if (props != null) {
            return compareProperties(props, other.props);
        }
        // this matches on the necessary specifics
        return true;
    }

    /**
     * Format this event into a string value.
     *
     * @return The string value of the event.
     */
    public String toString() {
        if (props != null) {
            return "BlueprintContainerEvent " + topic + " for bundle " + bundle.getSymbolicName() + " with properties: " + TestUtil.formatProperties(props);
        }
        else {
            return "BlueprintContainerEvent " + topic + " for bundle " + bundle.getSymbolicName();
        }
    }

    /**
     * Test if this event is a failure event.
     *
     * @return true if this was a context failure event.  false for any other event type.
     */
    public boolean isError() {
        return topic.equals("org/osgi/test/cases/blueprint/BlueprintContainer/FAILED");
    }

    /**
     * Get an exception that might be attached with an event.
     *
     * @return A Throwable object that was attached with an admin event.
     */
    public Throwable getException() {
        if (props != null) {
            return (Throwable)props.get("exception");
        }
        return null;
    }
}

