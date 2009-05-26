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

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.test.cases.blueprint.services.TestUtil;

/**
 * A Blueprint event that involves either a service wait or
 * or service timeout failure.  We expect to see additional
 * properties set for this event.
 */
public class ServiceBlueprintEvent extends BlueprintAdminEvent {
    // the set of interfaces we expect to see as a property
    protected Class[] interfaces;

    /**
     * Create an event for a given topic type.
     *
     * @param topic
     *               The assertion type.
     */
    public ServiceBlueprintEvent(String topic, Class[] interfaces) {
        this(topic, interfaces, null, null);
    }

    /**
     * Create an event for a given topic type.
     *
     * @param topic
     *               The assertion type.
     * @param props  An expected set of properties that will be associated with the
     *               event.
     */
    public ServiceBlueprintEvent(String topic, Class[] interfaces, Map props) {
        this(topic, interfaces, props, null);
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
    public ServiceBlueprintEvent(String topic, Class[] interfaces, Map props, TestEventListener listener) {
        super("org/osgi/service/blueprint/container/" + topic, props, listener);
        this.interfaces = interfaces;
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
        // do the superclass tests first
        TestEvent error = super.validate(received);
        if (error != null) {
            return error;
        }

        BlueprintAdminEvent other = (BlueprintAdminEvent)received;

        Object interfacesProp = other.getProperty(EventConstants.SERVICE_OBJECTCLASS);
        if (interfacesProp == null || !(interfacesProp instanceof String[]) ) {
            return new AssertionFailure("Missing service.objectClass property on blueprint event: " + other.toString());
        }

        String[] otherInterfaces = (String[])interfacesProp;

        for (int i = 0; i < interfaces.length; i++) {
            if (!TestUtil.contains(interfaces[i].getName(), otherInterfaces)) {
                return new AssertionFailure("Missing service.objectClass interface " + interfaces[i].getName() + " on blueprint event: " + other.toString());
            }
        }

        // just validate that a filter property is given
        if (other.getProperty("service.Filter") == null) {
            return new AssertionFailure("Missing service.Filter property on blueprint event: " + other.toString());
        }

        return null;
    }
}

