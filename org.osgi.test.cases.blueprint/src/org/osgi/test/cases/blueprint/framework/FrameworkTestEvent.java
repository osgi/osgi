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

import junit.framework.AssertionFailedError;

import org.osgi.framework.Bundle;
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

