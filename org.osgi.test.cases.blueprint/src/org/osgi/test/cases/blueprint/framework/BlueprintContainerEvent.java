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
import java.util.Dictionary;
import java.util.Properties;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.Version;
import org.osgi.service.blueprint.container.BlueprintEvent;
import org.osgi.service.blueprint.container.EventConstants;
import org.osgi.service.event.Event;
import org.osgi.test.cases.blueprint.services.TestUtil;


/**
 * A wrapper for BlueprintContainer events broadcast via the EventAdmin
 * service.
 */
public class BlueprintContainerEvent extends AdminTestEvent {
    // our extender bundle
    static protected Bundle extenderBundle;
    // an optional set of dependency filters we match on
    protected Properties[] dependencies = null;
    // a replay indicator (used for capturing state replays)
    protected boolean replay = false;

    /**
     * Create the event wrapper.
     *
     * @param topic  The expected event topic.
     */
    public BlueprintContainerEvent(String topic) {
        this(topic, null, null, null);
    }

    /**
     * Create the event wrapper.
     *
     * @param topic  The expected event topic.
     * @param props  A set of properties we expect to receive with the event.
     */
    public BlueprintContainerEvent(String topic, Map props) {
        this(topic, props, null, null);
    }


    /**
     * Create the event wrapper.
     *
     * @param topic    The expected event topic.
     * @param props    A set of properties we expect to receive with the event.
     * @param dependencies
     *                 A set of service filter strings we expect on the event.
     * @param listener An event listener we will poke when this event is received.
     */
    public BlueprintContainerEvent(String topic, Map props, Properties[] dependencies, TestEventListener listener) {
        super("org/osgi/test/cases/blueprint/BlueprintContainer/" + topic, props, listener);
        this.dependencies = dependencies;
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
     * Set the expected replay value.
     *
     * @param replay The new replay setting.
     */
    public void setReplay(boolean replay) {
        this.replay = replay;
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
        if (!(received instanceof BlueprintContainerEvent)) {
            return null;
        }
        // just a few things we validate here...
        BlueprintContainerEvent other = (BlueprintContainerEvent)received;
        // evaluate a few things directly from the event.
        BlueprintEvent event = (BlueprintEvent)other.getProperty(EventConstants.EVENT);

        if (topic.equals("org/osgi/test/cases/blueprint/BlueprintContainer/UNKNOWN")) {
            return new AssertionFailure("Unknown event BlueprintEvent type received");
        }

        // if we have exception information, then keep this even if we replace it with another error.
		Throwable cause = (Throwable) other.props.get(EventConstants.CAUSE);
        // if this is a failure event, then the exception property is required
        if (isError() && cause == null) {
            return new AssertionFailure("Missing exception cause on a failure event: " + other.toString());
        }

        // do we expect dependencies to be included on this event?  If so, we need to do some filter
        // matching
        if (dependencies != null) {
            String[] filterStrings = (String[])other.getProperty(org.osgi.service.blueprint.container.EventConstants.DEPENDENCIES);
            if (filterStrings == null) {
                return new AssertionFailure("Missing dependencies on a Blueprint event: " + other.toString());
            }

            if (filterStrings.length != dependencies.length) {
                return new AssertionFailure("Incorrect number of dependency strings from Blueprint event: " + other.toString());
            }

            // now convert the string filters into ones we can match against
            Filter[] filters = new Filter[filterStrings.length];
            for (int i = 0; i < filterStrings.length; i++) {
                try {
                    filters[i] = bundle.getBundleContext().createFilter(filterStrings[i]);
                } catch (InvalidSyntaxException e) {
                    return new AssertionFailure("Invalid filter string for Blueprint event: " + filterStrings[i], e);
                }
            }

            for (int i = 0; i < dependencies.length; i++) {
                if (!matchFilter(filters, dependencies[i])) {
                    return new AssertionFailure("Unmatched filter string for Blueprint event: " + filterStrings[i]);
                }
            }
        }

        if (!getExtenderBundle().equals(other.getProperty(EventConstants.EXTENDER_BUNDLE))) {
            return new AssertionFailure("Mismatched extender bundle id blueprint event other=" +
                    other.getProperty(EventConstants.EXTENDER_BUNDLE),
                    cause);
        }

        // validate the replay status (normally false)
        if (event.isReplay() != replay) {
            return new AssertionFailure("Incorrect replay status for Blueprint event: " + other.toString());
        }

        // allow the superclass to validate this (which includes calling potential
        // listeners
        return super.validate(received);
    }

    /**
     * Look for a match between a filter and a set of properties.
     *
     * @param filters The received list of filters.
     * @param props   The matching property set we're expecting to find.
     *
     * @return true if this is a match, false for non matches.
     */
    protected boolean matchFilter(Filter[] filters, Properties props) {
        for (int i = 0; i < filters.length; i++) {
            if (filters[i].match(props)) {
                return true;
            }
        }
        return false;
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
        return topic.equals("org/osgi/test/cases/blueprint/BlueprintContainer/FAILURE") ||
            topic.equals("org/osgi/test/cases/blueprint/BlueprintContainer/UNKNOWN");
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


    /**
     * Transform a BlueprintEvent into an emulated EventAdmin
     * Event for processing.
     *
     * @param e      The source BlueprintEvent.
     *
     * @return A generic Event object that can be passed along for further
     *         processing.
     */
    public static Event createEvent(BlueprintEvent e) {
        // transform the type into a string topic name
        String topic = "UNKNOWN";
        switch (e.getType()) {
            case BlueprintEvent.CREATING:
                topic = "CREATING";
                break;
            case BlueprintEvent.CREATED:
                topic = "CREATED";
                break;
            case BlueprintEvent.DESTROYING:
                topic = "DESTROYING";
                break;
            case BlueprintEvent.DESTROYED:
                topic = "DESTROYED";
                break;
            case BlueprintEvent.FAILURE:
                topic = "FAILURE";
                break;
            case BlueprintEvent.GRACE_PERIOD:
                topic = "GRACE_PERIOD";
                break;
            case BlueprintEvent.WAITING:
                topic = "WAITING";
                break;
        }

        // strip off the bundle information
        Bundle bundle = e.getBundle();
        // just turn this into a special event typed.
        Dictionary props = new Hashtable();
        props.put(EventConstants.BUNDLE_SYMBOLICNAME, bundle.getSymbolicName());
        props.put("bundle.version", Version.parseVersion((String)bundle.getHeaders().get(Constants.BUNDLE_VERSION)));
        props.put(EventConstants.BUNDLE, bundle);
        props.put(EventConstants.BUNDLE_ID, new Long(bundle.getBundleId()));

        // get the extender as well
        Bundle extender = e.getExtenderBundle();
        // save this information the first time through...all events after this should be consistent
        if (extenderBundle == null) {
            extenderBundle = extender;
        }

        // store the extender bundle information
        props.put(EventConstants.EXTENDER_BUNDLE, extender);

        // also attach the event information
        props.put(EventConstants.EVENT, e);
		if (e.getCause() != null) {
			props.put(org.osgi.service.blueprint.container.EventConstants.CAUSE, e.getCause());
        }
        if (e.getDependencies() != null) {
            props.put(org.osgi.service.blueprint.container.EventConstants.DEPENDENCIES, e.getDependencies());
        }
        return new Event("org/osgi/test/cases/blueprint/BlueprintContainer/" + topic, props);
    }


    /**
     * Transform a BlueprintEvent into a BlueprintContainerEvent
     * that can be used for mapping/validation purposes.
     *
     * @param e      The source BlueprintEvent.
     */
    public static BlueprintContainerEvent createContainerEvent(BlueprintEvent e) {
        return new BlueprintContainerEvent(createEvent(e));
    }


    public static Bundle getExtenderBundle() {
        return extenderBundle;
    }
}

