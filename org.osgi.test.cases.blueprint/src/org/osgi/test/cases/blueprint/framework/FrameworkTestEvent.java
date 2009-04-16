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

import junit.framework.AssertionFailedError;

import org.osgi.service.event.Event;
import org.osgi.test.cases.blueprint.services.TestUtil;

/**
 * A wrapper for framework events received for a test context.
 */
public class FrameworkTestEvent extends AdminTestEvent {
    /**
     * Create framework event.
     *
     * @param topic
     *               The assertion type.
     */
    public FrameworkTestEvent(String topic) {
        this(topic, null, null);
    }

    /**
     * Create framework event.
     *
     * @param topic
     *               The assertion type.
     * @param props  An expected set of properties we expect on the event.
     */
    public FrameworkTestEvent(String topic, Map props) {
        this(topic, props, null);
    }


    /**
     * Create framework event.
     *
     * @param topic
     *                 The assertion type.
     * @param props    An expected set of properties we expect on the event.
     * @param listener An event listener to be trigged upon event receipt.
     */
    public FrameworkTestEvent(String topic, Map props, TestEventListener listener) {
        super("org/osgi/framework/FrameworkEvent/" + topic, props, listener);
    }


    /**
     * Create an event wrapper from a received EventAdmin
     * Event.  This is used for comparisons with expected
     * events and also for recording assertion failures.
     *
     * @param event  The received event.
     */
    public FrameworkTestEvent(Event event) {
        super(event);
    }

    /**
     * Perform a comparison between two assertions
     *
     * @param other  The other assertion.
     *
     * @return True if these assertions match on the required properties.
     */
    public boolean matches(TestEvent o) {
        if (!(o instanceof FrameworkTestEvent)) {
            return false;
        }

        FrameworkTestEvent other = (FrameworkTestEvent)o;

        // fail immediately on a mismatch on id or type
        if (bundle != other.bundle || !topic.equals(other.topic)) {
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
            return "FrameworkTestEvent " + topic + " for bundle " + bundle.getSymbolicName() + " with properties: " + TestUtil.formatProperties(props);
        }
        else {
            return "FrameworkTestEvent " + topic + " for bundle " + bundle.getSymbolicName();
        }
    }


    /**
     * Raise an assertion failure for a missing expected event
     */
    public void failExpected() {
        throw new AssertionFailedError("Expected event " + toString() + " was not received");
    }


    /**
     * Raise an assertion failure for an unexpected blueprint event
     */
    public void failUnexpected() {
        // the unexpected events are generally FAILURES, which will have an exception property attached.
        // include that as the initCause() if it's there.
        AssertionFailedError e = new AssertionFailedError("Unexpected event " + toString() + " was received:");
        Throwable t = (Throwable)props.get("exception");
        if (t != null) {
            e.initCause(t);
        }
        throw e;
    }

    /**
     * Test if this event is a failure event.
     *
     * @return true if this was a context failure event.  false for any other event type.
     */
    public boolean isError() {
        return topic.equals("org/osgi/framework/Framework/ERROR");
    }
}

