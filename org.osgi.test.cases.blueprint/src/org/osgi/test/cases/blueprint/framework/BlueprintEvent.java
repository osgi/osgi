/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.blueprint.framework;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

import org.osgi.service.blueprint.context.ModuleContextEventConstants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;

import org.osgi.test.cases.blueprint.services.TestUtil;

/**
 * An event that is broadcast by the blueprint extender using the
 * EventAdmin service.
 */
public class BlueprintEvent extends AdminTestEvent {
    // TODO:  This is a temporary patch to get us past some RI errors
    private boolean bypassValidation = false;

    /**
     * Create an event for a given topic type.
     *
     * @param topic
     *               The assertion type.
     */
    public BlueprintEvent(String topic) {
        this(topic, null, null);
    }

    /**
     * Create an event for a given topic type.
     *
     * @param topic
     *               The assertion type.
     * @param props  An expected set of properties that will be associated with the
     *               event.
     */
    public BlueprintEvent(String topic, Map props) {
        this(topic, props, null);
    }


    /**
     * Create an event for a given topic type.
     *
     * @param topic
     *                 The assertion type.
     * @param props    An expected set of properties that will be associated with the
     *                 event.
     * @param listener A listener that will be triggered with this event is received.
     */
    public BlueprintEvent(String topic, Map props, TestEventListener listener) {
        super("org/osgi/service/blueprint/context/" + topic, props, listener);
    }


    /**
     * Create an event from a received EventAdmin
     * Event.  This is used for comparisons with expected
     * events and also for recording assertion failures.
     *
     * @param event  The received event.
     */
    public BlueprintEvent(Event event) {
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
        if (!(o instanceof BlueprintEvent)) {
            return false;
        }

        BlueprintEvent other = (BlueprintEvent)o;

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
            return "BlueprintEvent " + topic + " for bundle " + bundle.getSymbolicName() + " with properties: " + TestUtil.formatProperties(props);
        }
        else {
            return "BlueprintEvent " + topic + " for bundle " + bundle.getSymbolicName();
        }
    }

    /**
     * Test if this event is a failure event.
     *
     * @return true if this was a context failure event.  false for any other event type.
     */
    public boolean isError() {
        return topic.equals("org/osgi/service/blueprint/context/FAILURE");
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
        if (!(received instanceof BlueprintEvent)) {
            return null;
        }
        // TODO:  This needs to be removed eventually
        if (bypassValidation) {
            return null;
        }

        BlueprintEvent other = (BlueprintEvent)received;

        // this is a little circular, but it's probably the best solution for getting
        // a reference to the extender bundle.  The first time we process an event from
        // the extender, we'll snag the bundle from the event properties and that bundle
        // will be used to validate all subsequent events.
        Bundle extenderBundle = BaseTestController.getExtenderBundle(other.props);

        // if we have exception information, then keep this even if we replace it with another error.
        Throwable cause = (Throwable)other.props.get(EventConstants.EXCEPTION);

        if (!TestUtil.validateBundleVersion(bundle, (Version)other.getProperty(ModuleContextEventConstants.BUNDLE_VERSION))) {
            return new AssertionFailure("Mismatched bundle version on blueprint event expected=" + (String)bundle.getHeaders().get(Constants.BUNDLE_VERSION) + " received=" + other.getProperty("bundle.version"), cause);
        }
        if (!TestUtil.validateBundleId(bundle, (Long)other.getProperty(BUNDLE_ID))) {
            return new AssertionFailure("Mismatched bundle id on blueprint event other=" + other.getProperty(BUNDLE_ID), cause);
        }
        if (!TestUtil.validateBundleSymbolicName(bundle, (String)other.getProperty(BUNDLE_SYMBOLICNAME))) {
            return new AssertionFailure("Mismatched bundle symbolic name on blueprint event other=" + other.getProperty(BUNDLE_SYMBOLICNAME), cause);
        }

        if (bundle != (Bundle)other.getProperty(BUNDLE)) {
            return new AssertionFailure("Mismatched bundle on blueprint event other=" + other.getProperty(BUNDLE), cause);
        }
        // now validate for the extender bundle

        if (!extenderBundle.equals(other.getProperty(ModuleContextEventConstants.EXTENDER_BUNDLE))) {
            return new AssertionFailure("Mismatched extender bundle id blueprint event other=" +
                other.getProperty(ModuleContextEventConstants.EXTENDER_BUNDLE), cause);
        }

        if (!TestUtil.validateBundleId(extenderBundle, (Long)other.getProperty(ModuleContextEventConstants.EXTENDER_ID))) {
            return new AssertionFailure("Mismatched extender bundle id on blueprint event other=" +
                other.getProperty(ModuleContextEventConstants.EXTENDER_ID), cause);
        }
        if (!TestUtil.validateBundleSymbolicName(extenderBundle, (String)other.getProperty(ModuleContextEventConstants.EXTENDER_SYMBOLICNAME))) {
            return new AssertionFailure("Mismatched extender bundle symbolic name on blueprint event other=" +
                other.getProperty(ModuleContextEventConstants.EXTENDER_SYMBOLICNAME), cause);
        }

        // allow the superclass to validate this (which includes calling potential
        // listeners
        return super.validate(received);
    }
}
