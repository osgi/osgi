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
import java.util.Map;

import org.osgi.service.event.Event;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.TestUtil;

import junit.framework.AssertionFailedError;

/**
 * A component assertion event broadcast by our AssertionService
 * via the EventAdmin service.
 */
public class ComponentAssertion extends AdminTestEvent {
    // the component id of the component broadcasting the event.
    protected String componentId;
    // the component properties at the time of the event
    protected Dictionary componentProps;
    // the message associated with the event
    protected String message;
    // a potential associated property name
    protected String propertyName;
    // a traceback returned from the component
    protected Throwable traceback;

    /**
     * Create an assertion from a component identifier and
     * a cause.  This is generally used for created expected
     * assertions.
     *
     * @param componentId
     *               The component id of the expected assertion.
     * @param topic
     *               The assertion type.
     */
    public ComponentAssertion(String componentId, String topic) {
        this(componentId, topic, null, null);
    }

    /**
     * Create an assertion from a component identifier and
     * a cause.  This is generally used for created expected
     * assertions.
     *
     * @param componentId
     *               The component id of the expected assertion.
     * @param topic
     *               The assertion type.
     * @param props  A set of properties we expect for a match.
     */
    public ComponentAssertion(String componentId, String topic, Map props) {
        this(componentId, topic, null, null);
        this.props = props;
    }

    /**
     * Create an assertion from a component identifier and
     * a cause.  This is generally used for created expected
     * assertions.
     *
     * @param componentId
     *               The component id of the expected assertion.
     * @param topic
     *               The assertion type.
     * @param propertyName
     *               The property name associated with the event.
     */
    public ComponentAssertion(String componentId, String topic, String propertyName) {
        this(componentId, topic, propertyName, null);
    }

    /**
     * Create an assertion from a component identifier and
     * a cause.  This is generally used for created expected
     * assertions.
     *
     * @param componentId
     *                 The component id of the expected assertion.
     * @param topic
     *                 The assertion type.
     * @param propertyName
     *                 The property name associated with the event.
     * @param listener An event listener that will be poked when this expected event
     *                 is received.
     */
    public ComponentAssertion(String componentId, String topic, String propertyName, TestEventListener listener) {
        super(AssertionService.ASSERTION_BASE + topic, null, listener);
        this.componentId = componentId;
        this.propertyName = propertyName;
        this.message = null;
        this.traceback = null;
    }

    /**
     * Create a component assertion from a received EventAdmin
     * Event.  This is used for comparisons with expected
     * events and also for recording assertion failures.
     *
     * @param event  The received event.
     */
    public ComponentAssertion(Event event) {
        super(event);
        ComponentTestInfo component = (ComponentTestInfo)event.getProperty(AssertionService.COMPONENT);
        componentProps = component.getComponentProperties();
        componentId = (String)componentProps.get(ComponentTestInfo.COMPONENT_ID);
        message = (String)event.getProperty(AssertionService.ASSERTION_MESSAGE);
        propertyName = (String)event.getProperty(AssertionService.PROPERTY_NAME);
        traceback = (Throwable)event.getProperty(AssertionService.TRACEBACK);
    }

    /**
     * Perform a comparison between two assertions
     *
     * @param other  The other assertion.
     *
     * @return True if these assertions match on the required properties.
     */
    public boolean matches(TestEvent o) {
        if (!(o instanceof ComponentAssertion)) {
            return false;
        }

        ComponentAssertion other = (ComponentAssertion)o;

        // fail immediately on a mismatch on id or type
        if (!(componentId.equals(other.componentId) && topic.equals(other.topic))) {
            return false;
        }

        // if this assertion has a property name, then the type expects on too.  Compare on that property.
        if (propertyName != null) {
            return propertyName.equals(other.propertyName);
        }

        // if we have properties we need to match on those too
        if (props != null) {
            return TestUtil.containsAll(props, other.getProperties());
        }
        // this matches on the necessary specifics
        return true;
    }

    /**
     * Raise a deferred assertion failure.
     */
    public void fail() {
        AssertionFailedError e = new AssertionFailedError("Assertion failure for component " + componentId + ": " + message);
        if (traceback != null) {
            e.initCause(traceback);
        }
        throw e;
    }

    /**
     * Raise an assertion failure for a missing expected event
     */
    public void failExpected() {
        // we could be expecting a property set event.  Give a different message for that.
        if (propertyName != null) {
            throw new AssertionFailedError("Property " + propertyName + " for " + componentId + " was not set: " + message);
        }
        else {
            throw new AssertionFailedError("Expected event " + topic + " for " + componentId + " was not received: " + message);
        }
    }

    /**
     * Raise an assertion failure for an extra assertion event
     */
    public void failUnexpected() {
        AssertionFailedError error;
        // we could be expecting a property set event.  Give a different message for that.
        if (propertyName != null) {
            error = new AssertionFailedError("Property " + propertyName + " for " + componentId + " was set when not expected: " + message);
        }
        else {
            error = new AssertionFailedError("Unexpected event " + topic + " for " + componentId + " was received: " + message);
        }
        if (traceback != null) {
            error.initCause(traceback);
        }
        throw error;
    }


    /**
     * Flag an event as being an error situation that will terminate
     * the test if received when not expected.
     *
     * @return Only returns true for assertion failure messages.  Our components
     *         broadcast lots of "this happened" events that we're not always
     *         set up to receive.  Real failures we always flag.
     */
    public boolean isError() {
        // only flag the failure messages.
        return topic.equals(AssertionService.ASSERTION_BASE+AssertionService.ASSERTION_FAILURE);
    }

    // retrieve the component properties of the component.
    public Dictionary getComponentProperties() {
        return componentProps;
    }

    /**
     * Retrieve a specific property from an event.
     *
     * @param name   The name of the property.
     *
     * @return the return value.
     */
    public Object getComponentProperty(String name) {
        return componentProps.get(name);
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
        if (!(received instanceof ComponentAssertion)) {
            return null;
        }

        ComponentAssertion other = (ComponentAssertion)received;
        // if we have properties we should validate on
        if (props != null) {
            try {
                TestUtil.validateProperties(props, other.getProperties());
            } catch (Throwable e) {
                return new AssertionFailure("Mismatched assertion properties for component " + componentId + " event " + topic, e);
            }
        }
        // allow the superclass to validate this (which includes calling potential
        // listeners
        return super.validate(received);
    }

    /**
     * Format this event into a string value.
     *
     * @return The string value of the event.
     */
    public String toString() {
        if (props != null) {
            return "ComponentAssertion " + topic + " for component " + componentId + " with properties: " + TestUtil.formatProperties(props);
        }
        else {
            return "ComponentAssertion " + topic + " for component " + componentId;
        }
    }
}
