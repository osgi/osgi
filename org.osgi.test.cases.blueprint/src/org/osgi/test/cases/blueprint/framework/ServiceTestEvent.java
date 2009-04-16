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

import org.osgi.framework.Constants;
import org.osgi.service.event.Event;
import org.osgi.test.cases.blueprint.services.TestUtil;

/**
 * Wrapper around a service event broadcast via the EventAdmin
 * service.
 */
public class ServiceTestEvent extends AdminTestEvent {
    // the interfaces this service is registered under
    protected String[] interfaces;

    /**
     * Create a service event of the given topic type and interface name.
     *
     * @param topic  The topic.
     * @param interfaceName
     *               The target interface name.
     */
    public ServiceTestEvent(String topic, String interfaceName) {
        this(topic, new String[] { interfaceName }, null, null);
    }


    /**
     * Create a service event of the given topic type and interface name.
     *
     * @param topic  The topic.
     * @param interfaceName
     *               The target interface name.
     * @param props  A set of properties we expect to receive on the actual event.
     */
    public ServiceTestEvent(String topic, String interfaceName, Map props) {
        this(topic, new String[] { interfaceName }, props, null);
    }


    /**
     * Create a service event of the given topic type and interface name.
     *
     * @param topic      The topic.
     * @param interfaces The set of interfaces we expect to receive on the event.
     */
    public ServiceTestEvent(String topic, String[] interfaces) {
        this(topic, interfaces, null, null);
    }


    /**
     * Create a service event of the given topic type and interface name.
     *
     * @param topic      The topic.
     * @param interfaces The set of interfaces we expect to receive on the event.
     * @param props      A set of properties we expect on the event broadcast.
     */
    public ServiceTestEvent(String topic, String[] interfaces, Map props) {
        this(topic, interfaces, props, null);
    }


    /**
     * Create a service event of the given topic type and interface name.
     *
     * @param topic      The topic.
     * @param interfaces The set of interfaces we expect to receive on the event.
     * @param props      A set of properties we expect on the event broadcast.
     * @param listener   A potential event listener to poke when this event is received.
     */
    public ServiceTestEvent(String topic, String[] interfaces, Map props, TestEventListener listener) {
        // add on the prefix as a convenience
        super("org/osgi/framework/ServiceEvent/" + topic, props, listener);
        this.interfaces = interfaces;
    }


    /**
     * Create a service event of the given topic type and interface name.
     *
     * @param topic  The topic.
     * @param interfaceClass
     *               The target interface class.
     */
    public ServiceTestEvent(String topic, Class interfaceClass) {
        this(topic, new String[] { interfaceClass.getName() }, null, null);
    }


    /**
     * Create a service event of the given topic type and interface name.
     *
     * @param topic  The topic.
     * @param interfaceClass
     *               The target interface class.
     * @param props  A set of properties we expect to receive on the actual event.
     */
    public ServiceTestEvent(String topic, Class interfaceClass, Map props) {
        this(topic, new String[] { interfaceClass.getName() }, props, null);
    }


    /**
     * Create a service event of the given topic type and interface name.
     *
     * @param topic      The topic.
     * @param interfaces The set of interfaces we expect to receive on the event.
     */
    public ServiceTestEvent(String topic, Class[] interfaces) {
        this(topic, interfaces, null, null);
    }


    /**
     * Create a service event of the given topic type and interface name.
     *
     * @param topic      The topic.
     * @param interfaces The set of interfaces we expect to receive on the event.
     * @param props      A set of properties we expect on the event broadcast.
     */
    public ServiceTestEvent(String topic, Class[] interfaces, Map props) {
        this(topic, interfaces, props, null);
    }


    /**
     * Create a service event of the given topic type and interface name.
     *
     * @param topic      The topic.
     * @param interfaces The set of interfaces we expect to receive on the event.
     * @param props      A set of properties we expect on the event broadcast.
     * @param listener   A potential event listener to poke when this event is received.
     */
    public ServiceTestEvent(String topic, Class[] interfaceClasses, Map props, TestEventListener listener) {
        // add on the prefix as a convenience
        super("org/osgi/framework/ServiceEvent/" + topic, props, listener);
        this.interfaces = new String[interfaceClasses.length];
        for (int i = 0; i < interfaceClasses.length; i++) {
            interfaces[i] = interfaceClasses[i].getName();
        }
    }


    /**
     * Create an event wrapper from a received EventAdmin
     * Event.  This is used for comparisons with expected
     * events and also for recording failures.
     *
     * @param event  The received event.
     */
    public ServiceTestEvent(Event event) {
        // this is a self-translated service event, so all of the
        // bundle information has been set up for us already. We
        super(event);
        // but we do need to extract the interface information
        interfaces = (String[])event.getProperty(Constants.OBJECTCLASS);
    }

    /**
     * Perform a comparison between two assertions
     *
     * @param other  The other assertion.
     *
     * @return True if these assertions match on the required properties.
     */
    public boolean matches(TestEvent o) {
        if (!(o instanceof ServiceTestEvent)) {
            return false;
        }

        ServiceTestEvent other = (ServiceTestEvent)o;

        // fail immediately on a mismatch on id or type
        if (!bundle.equals(other.bundle) || !topic.equals(other.topic)) {
            return false;
        }

        // we don't validate that the length is the same, since the
        // registration event might contain more interfaces than
        // we expect to see.

        // make sure this has all of the interfaces of interest.
        for (int i = 0; i < interfaces.length; i++) {
            if (!arrayContains(other.interfaces, interfaces[i])) {
                return false;
            }
        }

        // if this event is expecting some properties, then compare each of the properties.
        if (props != null) {
            return compareProperties(props, other.props);
        }

        // this matches on the necessary specifics
        return true;
    }

    /**
     * Small utility for checking if one string value is contained
     * within a String array.
     *
     * @param array  The array to search.
     * @param target The target string.
     *
     * @return true if the string was found, false if it is not located.
     */
    private boolean arrayContains(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
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
        String bundleStr = bundle == null ? "<null>" : bundle.getSymbolicName();
        if (props != null) {
            return "ServiceTestEvent " + topic + " for bundle " + bundleStr + " for interfaces " + formatInterfaces() + " with properties: " + TestUtil.formatProperties(props);
        }
        else {
            return "ServiceTestEvent " + topic + " for bundle " + bundleStr  + " for interfaces " + formatInterfaces();
        }
    }

    /**
     * Format the interfaces list into a string form.
     *
     * @return A formatted interfaces list.
     */
    private String formatInterfaces() {
        StringBuffer buffer = new StringBuffer("{");
        for (int i = 0; i < interfaces.length; i++)
        {
            buffer.append(interfaces[i]);
            buffer.append(",");
        }
        // remove the trailing comma
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append("}");
        return buffer.toString();
    }
}
