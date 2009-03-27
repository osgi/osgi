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
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.service.blueprint.context.ModuleContextEventConstants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.test.cases.blueprint.services.TestUtil;

/**
 * A Blueprint event that involves either a service wait or
 * or service timeout failure.  We expect to see additional
 * properties set for this event.
 */
public class ServiceBlueprintEvent extends BlueprintEvent {
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
        super("org/osgi/service/blueprint/context/" + topic, props, listener);
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

        BlueprintEvent other = (BlueprintEvent)received;

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

