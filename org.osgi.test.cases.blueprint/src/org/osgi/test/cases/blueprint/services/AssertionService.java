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

package org.osgi.test.cases.blueprint.services;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

public class AssertionService
{
    public static final String ASSERTION_BASE = "org/osgi/test/cases/blueprint/";
    public static final String ASSERTION_FAILURE = "ASSERTION_FAILURE";
    public static final String BEAN_CREATED = "BEAN_CREATED";
    public static final String BEAN_INIT_METHOD = "BEAN_INIT_METHOD";
    public static final String BEAN_DESTROY_METHOD = "BEAN_DESTROY_METHOD";
    public static final String BEAN_PROPERTY_SET = "BEAN_PROPERTY_SET";
    public static final String FACTORY_CALLED = "FACTORY_CALLED";
    public static final String STATIC_FACTORY_CALLED = "STATIC_FACTORY_CALLED";
    public static final String METHOD_CALLED = "METHOD_CALLED";
    public static final String BLUEPRINT_CONTAINER_INJECTED = "BLUEPRINT_CONTAINER_INJECTED";
    public static final String GENERAL_EVENT = "GENERAL_EVENT";
    public static final String SERVICE_REQUEST = "SERVICE_REQUEST";
    public static final String SERVICE_SUCCESS = "SERVICE_SUCCESS";
    public static final String SERVICE_FAILURE = "SERVICE_FAILURE";
    public static final String SERVICE_FACTORY_GET = "SERVICE_FACTORY_GET";
    public static final String SERVICE_FACTORY_UNGET = "SERVICE_FACTORY_UNGET";
    public static final String SERVICE_BIND = "SERVICE_BIND";
    public static final String SERVICE_UNBIND = "SERVICE_UNBIND";
    public static final String SERVICE_REGISTERED = "SERVICE_REGISTERED";
    public static final String SERVICE_UNREGISTERED = "SERVICE_UNREGISTERED";
    public static final String LISTENER_REPLAY = "LISTENER_REPLAY";

    public static final String ASSERTION_MESSAGE = "ASSERTION_MESSAGE";
    public static final String BEAN = "BEAN";
    public static final String BEAN_PROPERTIES = "BEAN_PROPERTIES";
    public static final String PROPERTY_NAME = "PROPERTY_NAME";
    public static final String TRACEBACK = "TRACEBACK";
    public static final String EVENT_TYPE = "EVENT_TYPE";
    public static final String REPLAY_EVENT = "REPLAY_EVENT";

    // our current hosting context
    static BundleContext testContext;
    // the event admin service instance
    static EventAdmin adminService;
    // the service control instance
    static ServiceReference serviceReference;

    /**
     * Private constructor since it is a static only class
     */
    protected AssertionService() {
    }

    /**
     * Initialize this service class for a test bundle.
     *
     * @param testContext The bundle context we're servicing.
     */
    public static void initService(BundleContext context) {
        testContext = context;
        serviceReference = context.getServiceReference("org.osgi.service.event.EventAdmin");
        adminService = (EventAdmin)context.getService(serviceReference);
    }

    /**
     * Perform end of test cleanup on this service.
     */
    public static void cleanupService() {
        adminService = null;
        testContext.ungetService(serviceReference);
        serviceReference = null;
        testContext = null;
    }

    /**
     * Asserts that a condition is true. If it isn't it publishes an assertion event
     * with the given message.
     *
     * @param component
     *                  The component object making the assertion
     * @param message
     *                  the identifying message for the event (<code>null</code> okay)
     * @param condition
     *                  condition to be checked
     */
    static public void assertTrue(Object component, String message, boolean condition) {
        if (!condition) {
            fail(component, message);
        }
    }

    /**
     * Asserts that a condition is false. If it isn't it publishes an
     * assertion event with the given message.
     *
     * @param component
     *                  The component object making the assertion
     * @param message
     *            the identifying message for the {@link AssertionError} (<code>null</code>
     *            okay)
     * @param condition
     *            condition to be checked
     */
    static public void assertFalse(Object component, String message, boolean condition) {
        assertTrue(component, message, !condition);
    }

    /**
     * Fails a test with the given message.
     *
     * @param component
     *                  The component object making the assertion
     * @param message
     *            the identifying message for the assertion event (<code>null</code>
     *            okay)
     */
    static public void fail(Object component, String message) {
        Hashtable props = new Hashtable();
        props.put(ASSERTION_MESSAGE, message);
        props.put(TRACEBACK, new Throwable());
        sendEvent(component, ASSERTION_FAILURE, props);
    }

    /**
     * Fails a test with the given message message and cause.
     *
     * @param component
     *                  The component object making the assertion
     * @param message
     *            the identifying message for the assertion event (<code>null</code>
     *            okay)
     */
    static public void fail(Object component, String message, Throwable cause) {
        Hashtable props = new Hashtable();
        props.put(ASSERTION_MESSAGE, message);
        props.put(TRACEBACK, cause);
        sendEvent(component, ASSERTION_FAILURE, props);
    }

    /**
     * Send an event for a component.
     *
     * @param component
     *                  The component object making the assertion
     * @param topic  The topic of the event.
     */
    static public void sendEvent(Object component, String topic) {
        sendEvent(component, topic, null);
    }

    /**
     * Send a general event alert for a component.
     *
     * @param component
     *                  The component object making the assertion
     * @param topic  The topic of the event.
     */
    static public void sendGeneralEvent(Object component, String topic, String message) {
        Hashtable props = new Hashtable();
        props.put(EVENT_TYPE, topic);
        props.put(ASSERTION_MESSAGE, message);
        sendEvent(component, GENERAL_EVENT, props);
    }

    /**
     * Send a event alert with a name.
     *
     * @param component
     *                  The component object making the assertion
     * @param topic  The topic of the event.
     */
    static public void sendPropertyNameEvent(Object component, String topic, String specialName) {
        Hashtable props = new Hashtable();
        props.put(PROPERTY_NAME, specialName);
        sendEvent(component, topic, props);
    }

    /**
     * Send an event for a component with additional property
     * information attached.
     *
     * @param component
     *                  The component object making the assertion
     * @param topic  The event topic.
     * @param props  The additional property information.
     */
    static public void sendEvent(Object component, String topic, Dictionary props) {
        if (props == null) {
            props = new Hashtable();
        }
        props.put(BEAN, component);
        // if this is a ComponentTestInfo object, then send along the component properties
        // in the event.  Note that the attached properties will be a snapshot of the
        // component state at the time the event is posted.  This will allow the test validation
        // code to see the full state of the information.
        if (component instanceof ComponentTestInfo) {
            props.put(BEAN_PROPERTIES, ((ComponentTestInfo)component).getComponentProperties());
        }
        sendEvent(topic, props);
    }


    /**
     * Broadcast a test event to the admin service.
     *
     * @param topic      The specific topic type.
     */
    static public void sendEvent(String topic) {
        // just send with an empty properties bundle
        sendEvent(topic, new Hashtable());
    }


    /**
     * Broadcast a test event to the admin service.
     *
     * @param topic      The specific topic type.
     * @param properties Any property information associated with the event.
     */
    static public void sendEvent(String topic, Dictionary properties) {
        // we reuse some components outside the normal test context.  In
        // that role, we don't care if they send events or not.  It's possible
        // that the admin service will not be initialized yet, so if the
        // service is not here, don't send the event.
        if (adminService != null) {
            adminService.sendEvent(new Event(ASSERTION_BASE + topic, properties));
        }
    }


    /**
     * Asserts that two objects are equal. If they are not, an
     * assertion event is published is thrown with the given message. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param component
     *                  The component object making the assertion
     * @param message
     *            the identifying message for the event (<code>null</code>
     *            okay)
     * @param expected
     *            expected value
     * @param actual
     *            actual value
     */
    static public void assertEquals(Object component, String message, Object expected, Object actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        } else {
            failNotEquals(component, message, expected, actual);
        }
    }


    /**
     * Asserts that two int[] objects are equal. If they are not, an
     * assertion event is published is thrown with the given message. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param component
     *                  The component object making the assertion
     * @param message
     *            the identifying message for the event (<code>null</code>
     *            okay)
     * @param expected
     *            expected value
     * @param actual
     *            actual value
     */
    static public void assertArrayEquals(Object component, String message, int[] expected, int[] actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        } else {
            assertTrue(component, message, Arrays.equals(expected, actual));
        }
    }


    /**
     * Asserts that two Object[] objects are equal. If they are not, an
     * assertion event is published is thrown with the given message. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param component
     *                  The component object making the assertion
     * @param message
     *            the identifying message for the event (<code>null</code>
     *            okay)
     * @param expected
     *            expected value
     * @param actual
     *            actual value
     */
    static public void assertArrayEquals(Object component, String message, Object[] expected, Object[] actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        } else {
            assertTrue(component, message, Arrays.equals(expected, actual));
        }
    }


    /**
     * Asserts that two longs are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param component
     *                  The component object making the assertion
     * @param message
     *            the identifying message for the {@link AssertionError} (<code>null</code>
     *            okay)
     * @param expected
     *            long expected value.
     * @param actual
     *            long actual value
     */
    static public void assertEquals(Object component, String message, long expected, long actual) {
        assertEquals(component, message, new Long(expected), new Long(actual));
    }



    /**
     * Asserts that an object isn't null. If it is an {@link AssertionError} is
     * published with the given message.
     *
     * @param component
     *                  The component object making the assertion
     * @param message
     *            the identifying message for the {@link AssertionError} (<code>null</code>
     *            okay)
     * @param object
     *            Object to check or <code>null</code>
     */
    static public void assertNotNull(Object component, String message, Object object) {
        assertTrue(component, message, object != null);
    }

    /**
     * Asserts that an object is null. If it is not, an assertion event
     * is published with the given message.
     *
     * @param component
     *                  The component object making the assertion
     * @param message
     *            the identifying message for the {@link AssertionError} (<code>null</code>
     *            okay)
     * @param object
     *            Object to check or <code>null</code>
     */
    static public void assertNull(Object component, String message, Object object) {
        assertTrue(component, message, object == null);
    }


    /**
     * Asserts that two objects refer to the same object. If they are not, an
     * assertion event is published with the given message.
     *
     * @param component
     *                  The component object making the assertion
     * @param message
     *            the identifying message for the {@link AssertionError} (<code>null</code>
     *            okay)
     * @param expected
     *            the expected object
     * @param actual
     *            the object to compare to <code>expected</code>
     */
    static public void assertSame(Object component, String message, Object expected, Object actual) {
        if (expected == actual)
        {
            return;
        }
        failNotSame(component, message, expected, actual);
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object, an {@link AssertionError} is thrown with the
     * given message.
     *
     * @param component
     *                  The component object making the assertion
     * @param message
     *            the identifying message for the {@link AssertionError} (<code>null</code>
     *            okay)
     * @param unexpected
     *            the object you don't expect
     * @param actual
     *            the object to compare to <code>unexpected</code>
     */
    static public void assertNotSame(Object component, String message, Object unexpected, Object actual) {
        if (unexpected == actual)
        {
            failSame(component, message);
        }
    }

    static private void failSame(Object component, String message) {
        String formatted= "";
        if (message != null) {
            formatted= message + " ";
        }
        fail(component, formatted + "expected not same");
    }

    static private void failNotSame(Object component, String message, Object expected, Object actual) {
        String formatted= "";
        if (message != null) {
            formatted= message + " ";
        }
        fail(component, formatted + "expected same:<" + expected + "> was not:<" + actual + ">");
    }

    static private void failNotEquals(Object component, String message, Object expected, Object actual) {
        fail(component, format(message, expected, actual));
    }

    static String format(String message, Object expected, Object actual) {
        String formatted= "";
        if (message != null && !message.equals(""))
        {
            formatted = message + " ";
        }
        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        if (expectedString.equals(actualString))
        {
            return formatted + "expected: " + formatClassAndValue(expected, expectedString) + " but was: " + formatClassAndValue(actual, actualString);
        }
        else
        {
            return formatted + "expected:<" + expectedString + "> but was:<" + actualString + ">";
        }
    }

    private static String formatClassAndValue(Object value, String valueString) {
        String className= value == null ? "null" : value.getClass().getName();
        return className + "<" + valueString + ">";
    }

    /**
     * Assert that a component is in the initialized state
     *
     * @param source The source component.
     */
    public static void assertComponentInitialized(Object source) {
        if (!(source instanceof ComponentTestInfo)) {
            fail(source, "Object " + source + " is not a valid test component");
        }
        Object initialized = ((ComponentTestInfo)source).getProperty(ComponentTestInfo.INIT_CALLED);
        assertEquals(source, "Object " + source + " is not initialiazed", Boolean.TRUE, initialized);
    }

    /**
     * Assert that a component is NOT in the destroyed state
     *
     * @param source The source component.
     */
    public static void assertComponentNotDestroyed(Object source) {
        if (!(source instanceof ComponentTestInfo)) {
            fail(source, "Object " + source + " is not a valid test component");
        }
        Object destroyed = ((ComponentTestInfo)source).getProperty(ComponentTestInfo.DESTROY_CALLED);
        assertEquals(source, "Object " + source + " has been destroyed", Boolean.FALSE, destroyed);
    }

    /**
     * Assert that a component property is equal to a value
     *
     * @param source The source component.
     * @param name   The name of the property.
     * @param value  The required value
     */
    public static void assertComponentPropertyEquals(Object source, String name, Object value) {
        if (!(source instanceof ComponentTestInfo)) {
            fail(source, "Object " + source + " is not a valid test component");
        }
        assertEquals(source, "Property " + name + " of " + source, ((ComponentTestInfo)source).getProperty(name), value);
    }

    /**
     * Assert that a component property is equal to a value
     *
     * @param source The source component.
     * @param name   The name of the property.
     * @param value  The required value
     */
    public static void assertComponentPropertySame(Object source, String name, Object value) {
        if (!(source instanceof ComponentTestInfo)) {
            fail(source, "Object " + source + " is not a valid test component");
        }
        assertSame(source, "Property " + name + " of " + source, ((ComponentTestInfo)source).getProperty(name), value);
    }

    /**
     * Assert that a component property is not the same as a value
     *
     * @param source The source component.
     * @param name   The name of the property.
     * @param value  The required value
     */
    public static void assertComponentPropertyNotSame(Object source, String name, Object value) {
        if (!(source instanceof ComponentTestInfo)) {
            fail(source, "Object " + source + " is not a valid test component");
        }
        assertNotSame(source, "Property " + name + " of " + source, ((ComponentTestInfo)source).getProperty(name), value);
    }
}

