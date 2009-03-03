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
import org.osgi.service.event.Event;

import org.osgi.test.cases.blueprint.services.TestUtil;


/**
 * A wrapper for ModuleContext events broadcast via the EventAdmin
 * service.
 */
public class ModuleContextEvent extends AdminTestEvent {
    /**
     * Create the event wrapper.
     *
     * @param topic  The expected event topic.
     */
    public ModuleContextEvent(String topic) {
        this(topic, null, null);
    }

    /**
     * Create the event wrapper.
     *
     * @param topic  The expected event topic.
     * @param props  A set of properties we expect to receive with the event.
     */
    public ModuleContextEvent(String topic, Map props) {
        this(topic, props, null);
    }


    /**
     * Create the event wrapper.
     *
     * @param topic    The expected event topic.
     * @param props    A set of properties we expect to receive with the event.
     * @param listener An event listener we will poke when this event is received.
     */
    public ModuleContextEvent(String topic, Map props, TestEventListener listener) {
        super("org/osgi/test/cases/blueprint/ModuleContext/" + topic, props, listener);
    }


    /**
     * Create an event wrapper from a received EventAdmin
     * Event.  This is used for comparisons with expected
     * events and also for recording assertion failures.
     *
     * @param event  The received event.
     */
    public ModuleContextEvent(Event event) {
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
        if (!(o instanceof ModuleContextEvent)) {
            return false;
        }

        ModuleContextEvent other = (ModuleContextEvent)o;

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
            return "ModuleContextEvent " + topic + " for bundle " + bundle.getSymbolicName() + " with properties: " + TestUtil.formatProperties(props);
        }
        else {
            return "ModuleContextEvent " + topic + " for bundle " + bundle.getSymbolicName();
        }
    }

    /**
     * Test if this event is a failure event.
     *
     * @return true if this was a context failure event.  false for any other event type.
     */
    public boolean isError() {
        return topic.equals("org/osgi/test/cases/blueprint/ModuleContext/FAILED");
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

