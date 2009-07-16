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
import java.util.Properties;

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
 * An event that is broadcast by the blueprint extender using the
 * EventAdmin service.
 */
public class BlueprintAdminEvent extends AdminTestEvent {
    // an optional set of dependency filters we match on
    protected Properties[] dependencies = null;

    /**
     * Create an event for a given topic type.
     *
     * @param topic
     *               The assertion type.
     */
    public BlueprintAdminEvent(String topic) {
        this(topic, null, null, null);
    }

    /**
     * Create an event for a given topic type.
     *
     * @param topic
     *               The assertion type.
     * @param props  An expected set of properties that will be associated with the
     *               event.
     */
    public BlueprintAdminEvent(String topic, Map props) {
        this(topic, props, null, null);
    }


    /**
     * Create an event for a given topic type.
     *
     * @param topic
     *                 The assertion type.
     * @param props    An expected set of properties that will be associated with the
     *                 event.
     * @param dependencies
     *                 A set of service filter strings we expect on the event.
     * @param listener A listener that will be triggered with this event is received.
     */
    public BlueprintAdminEvent(String topic, Map props, Properties[] dependencies, TestEventListener listener) {
        super("org/osgi/service/blueprint/container/" + topic, props, listener);
        this.dependencies = dependencies;
    }


    /**
     * Create an event from a received EventAdmin
     * Event.  This is used for comparisons with expected
     * events and also for recording assertion failures.
     *
     * @param event  The received event.
     */
    public BlueprintAdminEvent(Event event) {
        super(event);
    }

    /**
     * Perform a comparison between two events
     *
     * @param other  The other assertion.
     *
     * @return True if these assertions match on the required properties.
     */
    public boolean matches(TestEvent o) {
        if (!(o instanceof BlueprintAdminEvent)) {
            return false;
        }

        BlueprintAdminEvent other = (BlueprintAdminEvent)o;

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
            return "BlueprintAdminEvent " + topic + " for bundle " + bundle.getSymbolicName() + " with properties: " + TestUtil.formatProperties(props);
        } else {
            return "BlueprintAdminEvent " + topic + " for bundle " + bundle.getSymbolicName();
        }
    }

    /**
     * Test if this event is a failure event.
     *
     * @return true if this was a context failure event.  false for any other event type.
     */
    public boolean isError() {
        return topic.equals("org/osgi/service/blueprint/container/FAILURE");
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
        if (!(received instanceof BlueprintAdminEvent)) {
            return null;
        }

        BlueprintAdminEvent other = (BlueprintAdminEvent)received;

        // this is a little circular, but it's probably the best solution for getting
        // a reference to the extender bundle.  The first time we process an event from
        // the extender, we snag the bundle from the BlueprintEvent  and that bundle
        // will be used to validate all subsequent events.
        Bundle extenderBundle = BlueprintContainerEvent.getExtenderBundle();

        // if we have exception information, then keep this even if we replace it with another error.
        Throwable cause = (Throwable) other.props.get(EventConstants.CAUSE);
        // if this is a failure event, then the exception property is required
        if (isError() && cause == null) {
            return new AssertionFailure("Missing exception cause on a blueprint admin failure event: " + other.toString());
        }

        // this must have an attached blueprint event
        Object event = other.getProperty(EventConstants.EVENT);
        if (event == null || !(event instanceof BlueprintEvent)) {
            return new AssertionFailure("Invalid or missing EVENT property on blueprint admin event: " + other.toString(), cause);
        }

        // replay events should never be broadcast using EventAdmin, so the replay flag must be false
        if (((BlueprintEvent)event).isReplay()) {
            return new AssertionFailure("Replay event broadcast using EventAdmin services: " + other.toString(), cause);
        }

        Object timestamp = other.getProperty(EventConstants.TIMESTAMP);
        if (timestamp == null || !(timestamp instanceof Long)) {
            return new AssertionFailure("Invalid or missing timestamp property on blueprint admin event: " + other.toString(), cause);
        }

        if (!TestUtil.validateBundleVersion(bundle, (Version)other.getProperty(EventConstants.BUNDLE_VERSION))) {
            return new AssertionFailure("Mismatched bundle version on blueprint admin event expected=" +
                (String)bundle.getHeaders().get(Constants.BUNDLE_VERSION) + " received=" + other.getProperty("bundle.version"), cause);
        }
        if (!TestUtil.validateBundleId(bundle, (Long) other.getProperty(EventConstants.BUNDLE_ID))) {
            return new AssertionFailure("Mismatched bundle id on blueprint admin event other="
                 + other.getProperty(EventConstants.BUNDLE_ID), cause);
        }
        if (!TestUtil.validateBundleSymbolicName(bundle, (String) other.getProperty(EventConstants.BUNDLE_SYMBOLICNAME))) {
            return new AssertionFailure("Mismatched bundle symbolic name on blueprint admin event other="
                + other.getProperty(EventConstants.BUNDLE_SYMBOLICNAME), cause);
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

        if (bundle != (Bundle) other.getProperty(EventConstants.BUNDLE)) {
            return new AssertionFailure("Mismatched bundle on blueprint admin event other="
                    + other.getProperty(EventConstants.BUNDLE), cause);
        }
        // now validate for the extender bundle

        if (!extenderBundle.equals(other.getProperty(EventConstants.EXTENDER_BUNDLE))) {
            return new AssertionFailure("Mismatched extender bundle id blueprint admin event other=" +
                    other.getProperty(EventConstants.EXTENDER_BUNDLE),
                    cause);
        }

        if (!TestUtil.validateBundleId(extenderBundle, (Long) other
                .getProperty(EventConstants.EXTENDER_BUNDLE_ID))) {
            return new AssertionFailure("Mismatched extender bundle id on blueprint admin event other=" +
                    other
                    .getProperty(EventConstants.EXTENDER_BUNDLE_ID),
                    cause);
        }
        if (!TestUtil.validateBundleSymbolicName(extenderBundle, (String) other
                .getProperty(EventConstants.EXTENDER_BUNDLE_SYMBOLICNAME))) {
            return new AssertionFailure("Mismatched extender bundle symbolic name on blueprint admin event other=" +
                    other
                    .getProperty(EventConstants.EXTENDER_BUNDLE_SYMBOLICNAME),
                    cause);
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
}
