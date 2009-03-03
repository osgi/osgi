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
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;

import junit.framework.AssertionFailedError;

/**
 * The base class for all events that are received using the
 * EventAdmin service.
 */
public abstract class AdminTestEvent extends TestEvent implements EventConstants {
    // the type of the assertion (also the EventAdmin topic used broadcast the event)
    protected String topic;
    // Any additional properties associated with the event
    protected Map props;

    /**
     * Constructor for an Admin type event.
     *
     * @param topic  The event type.
     */
    public AdminTestEvent(String topic) {
        this(topic, null, null);
    }

    /**
     * Constructor for an Admin type event.
     *
     * @param topic  The event type.
     * @param props  A set of properties we expect on the broadcast event.
     */
    public AdminTestEvent(String topic, Map props) {
        this(topic, props, null);
    }

    /**
     * Constructor for an Admin type event.
     *
     * @param topic  The event type.
     * @param props  A set of properties we expect on the broadcast event.
     * @param listener An attached listener that will handle conditions related to this
     *                 specific event.
     */
    public AdminTestEvent(String topic, Map props, TestEventListener listener) {
        super(listener);
        this.topic = topic;
        this.props = props;
    }


    /**
     * Create an event from a received EventAdmin
     * Event.  This is used for comparisons with expected
     * events and also for recording assertion failures.
     *
     * @param event  The received event.
     */
    public AdminTestEvent(Event event) {
        topic = event.getTopic();
        props = copyProperties(event);
        // get the bundle from the event
        bundle = (Bundle)event.getProperty(BUNDLE);
    }

    /**
     * Raise an assertion failure for a missing expected event
     */
    public void failExpected() {
        throw new AssertionFailedError("Expected event " + toString() + " was not received");
    }


    /**
     * Raise an assertion failure for an unexpected admin event
     */
    public void failUnexpected() {
        // the unexpected events are generally FAILURES, which will have an exception property attached.
        // include that as the initCause() if it's there.
        AssertionFailedError e = new AssertionFailedError("Unexpected event " + toString() + " was received:");
        Throwable t = getException();
        if (t != null) {
            e.initCause(t);
        }
        throw e;
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
     * Compare two property bundles.  The first bundle is our expected
     * property set.  This returns true if all of the properties in the
     * expected bundle are in the received set with equivalent values.
     * Extra properties in the received set are ignored.
     *
     * @param expected The expected property set.
     * @param received The set received from an event.
     *
     * @return true if all of the expected values are found.  false for any
     *         failures.
     */
    static boolean compareProperties(Map expected, Map received) {
        Iterator i = expected.keySet().iterator();
        while (i.hasNext()) {
            String name = (String)i.next();
            // each property must be in the event and be equivalent
            if (!received.containsKey(name)) {
                return false;
            }
            if (!expected.get(name).equals(received.get(name))) {
                return false;
            }
        }
        return true;  // full match
    }

    /**
     * Test if this event is generated by a target bundle.
     *
     * @param bundle The bundle in question.
     *
     * @return True if this is a bundle match, false for any mismatch.
     */
    public boolean isForBundle(Bundle bundle) {
        return this.bundle == null || this.bundle.equals(bundle);
    }


    /**
     * Retrieve the event properties.
     *
     * @return The property bundle, if any.
     */
    public Map getProperties() {
        return props;
    }


    /**
     * Retrieve a given property from the event.
     *
     * @param name   The target event name.
     *
     * @return The property value, or null if the property doesn't exist.
     */
    public Object getProperty(String name) {
        // generally, if the caller is calling this, there should be properties, but
        // why take a chance.
        if (props == null) {
            return null;
        }
        return props.get(name);
    }

    /**
     * Retrieve the type of the event
     */
    public String getTopic() {
        return topic;
    }
}

