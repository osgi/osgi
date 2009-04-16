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

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.blueprint.services.ServiceManager;

/**
 * A class to hook ServiceManager unregistration events in to
 * the different test phases.  This is used for ServiceManager
 * instances that are managed on the test controller side.
 */
public class ServiceManagerUnregister implements TestCleanup, TestEventListener, TestInitializer {
    // Our list of named services to unregister.  If null, we unregister
    // everything
    protected String[] services;
    // The ServiceManager instance we're interfacing with
    protected ServiceManager manager;

    public ServiceManagerUnregister(ServiceManager manager) {
        this(manager, (String[])null);
    }

    public ServiceManagerUnregister(ServiceManager manager, String service) {
        this(manager, new String[] { service });
    }

    public ServiceManagerUnregister(ServiceManager manager, String[] services) {
        this.manager = manager;
        this.services = services;
    }


    /**
     * Unregister the defined services when triggered by a phase
     * stage or event.
     */
    public void unregister() {
        if (services == null) {
            manager.unregisterServices();
        }
        else {
            for (int i = 0; i < services.length; i++) {
                manager.unregisterService(services[i]);
            }
        }
    }


    /**
     * Perform any additional test phase setup actions.
     *
     * @param testContext
     *               The BundleContext for the test (used for inspecting the test
     *               environment).
     *
     * @exception Exception
     */
    public void start(BundleContext testContext) throws Exception {
        // go unregister the events
        unregister();
    }

    /**
     * Perform any additional test phase cleanup actions.
     *
     * @param testContext
     *               The BundleContext for the test (used for inspecting the test
     *               environment).
     *
     * @exception Exception
     */
    public void cleanup(BundleContext testContext) throws Exception {
        // go unregister the events
        unregister();
    }


    /**
     * Give the listener an opportunity to respond to receiving
     * an expected event. This is intended for triggering actions resulting
     * from receiving an event.  For example, a waiting event can register a
     * service upon notification that a WAITING event for that service has been
     * received.
     *
     * @param results  The test case expected result set.
     * @param expected The expected event.
     * @param received The actual received event, with all attached event properties.
     */
    public void eventReceived(TestEvent expected, TestEvent received) {
        // go unregister the events
        unregister();
    }

    /**
     * Give the listener an opportunity to respond to not receiving
     * an expected event.
     *
     * @param results  The test case expected result set.
     * @param expected The expected event.
     */
    public void eventNotReceived(TestEvent expected) throws Exception {
        // this is a NOP event
    }

    /**
     * Give the listener to perform test validation at the point where
     * an event is returned.  If the listener returns null, then
     * the validation has passed.  If the listener returns a TestEvent
     * instance, the returned event will be added to the failures list
     * and will be used to raise a deferred assertion in the main thread.
     *
     * @param expected The expected exception (the one the listener is attached to).
     * @param received The received exception requiring validation.
     *
     * @return null, or a TestEvent instance used to raise a deferred assertion failure.
     */
    public TestEvent validateEvent(TestEvent expected, TestEvent received) {
        // this is a NOP event
        return null;
    }
}

