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

import junit.framework.Assert;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * Validate that a target service is registered at the requested
 * test phase boundary.
 */
public class ServiceRequestInitiator extends Assert implements TestCleanup, TestEventListener, TestInitializer, BundleAware {
    // the bundle we're validating for
    protected Bundle bundle;
    // our test bundle context
    protected BundleContext testContext;
    // the name of the interface to validate
    protected String interfaceName;
    // any filter string to use
    protected String filter;


    /**
     * Create a registration validator.
     *
     * @param interfaceName
     *               The interface name to request.
     * @param filter A lookup filter for the service.
     */
    public ServiceRequestInitiator(BundleContext testContext, Class interfaceClass, String filter) {
        this.testContext = testContext;
        this.interfaceName = interfaceClass.getName();
        this.filter = filter;
    }

    /**
     * Inject the event set's bundler into this validator instance.
     *
     * @param bundle The bundle associated with the hosting event set.
     */
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Perform the service request.
     *
     * @exception Exception
     */
    protected void requestService() {
        try {
            ServiceReference[] refs = testContext.getServiceReferences(interfaceName, filter);
            if (refs != null) {
                for (int i = 0; i < refs.length; i++) {
                    if (refs[i].getBundle() == bundle) {
                        // request the service instance
                        testContext.getService(refs[i]);
                        // that should be all we need
                        testContext.ungetService(refs[i]);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception" + e);
            e.printStackTrace();
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
        // go trigger a service request.
        requestService();
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
        // go trigger a service request.
        requestService();
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
        // go trigger a service request.
        requestService();
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

