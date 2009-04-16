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
 * A wrapper for bundle-related events broadcast via the EventAdmin
 * service.
 */
public class BundleTestEvent extends AdminTestEvent {
    /**
     * Create an event for a given topic.
     *
     * @param topic  The EventAdmin topic.
     */
    public BundleTestEvent(String topic) {
        this(topic, null);
    }

    /**
     * Create an event for a given topic.
     *
     * @param topic  The EventAdmin topic.
     * @param props  A potential list of properties we expect to find.
     */
    public BundleTestEvent(String topic, Map props) {
        super("org/osgi/framework/BundleEvent/" + topic, props);
    }


    /**
     * Create an event from a received EventAdmin
     * Event.  This is used for comparisons with expected
     * events and also for recording assertion failures.
     *
     * @param event  The received event.
     */
    public BundleTestEvent(Event event) {
        super(event);
    }

    /**
     * Perform a comparison between two events
     *
     * @param other  The other event.
     *
     * @return True if these assertions match on the required properties.
     */
    public boolean matches(TestEvent o) {
        if (!(o instanceof BundleTestEvent)) {
            return false;
        }

        BundleTestEvent other = (BundleTestEvent)o;

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
            return "BundleTestEvent " + topic + " for bundle " + bundle.getSymbolicName() + " with properties: " + TestUtil.formatProperties(props);
        }
        else {
            return "BundleTestEvent " + topic + " for bundle " + bundle.getSymbolicName();
        }
    }

    /**
     * Test if this event is a failure event.
     *
     * @return true if this was a context failure event.  false for any other event type.
     */
    public boolean isError() {
        // bundle events don't have any real failure events.
        return false;
    }
}


