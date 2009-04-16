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
import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * A combination object that is used to manage service instances.
 * This object will toggle the registered state of a service any
 * time one of its methods is called. This can be extremely useful
 * for dependency tests.  For example, it might be installed as
 * an initializer for two test phases.  This will cause the service
 * to be registered at the start of the first phase, and deregistered
 * at the start of the second phase.  This will trigger some transitions
 * in the test enviroment.
 *
 * Another useful activity is to attach this as a listener to a specific
 * test event.  This allows the toggling activity to be triggered
 * when an event is received (such as a WAITING event from a
 * ModuleContext).
 */
public class TestService implements TestInitializer, TestCleanup, TestValidator, TestEventListener {
    // our test bundle context.
    protected BundleContext testBundle;
    // The service implementation
    protected Object serviceInstance;
    // The registered service interfaces
    protected String[] interfaces;
    // any special registered service properties
    protected Dictionary props;
    // our registered service reference
    protected ServiceRegistration serviceReg;

    /**
     * Create a Service tracker instance.
     *
     * @param testBundle The testbundle context.
     * @param serviceInstance
     *                   The service object we're going to register.
     * @param interfaceName
     *                   The interface this service will be registered under.
     * @param props      Additional properties used for the registration.
     */
    public TestService(BundleContext testBundle, Object serviceInstance, String interfaceName, Dictionary props) {
        this(testBundle, serviceInstance, new String[] { interfaceName }, props);
    }


    /**
     * Create a Service tracker instance.
     *
     * @param testBundle The testbundle context.
     * @param serviceInstance
     *                   The service object we're going to register.
     * @param interfaceName
     *                   The interface this service will be registered under.
     * @param props      Additional properties used for the registration.
     */
    public TestService(BundleContext testBundle, Object serviceInstance, String[] interfaces, Dictionary props) {
        this.testBundle = testBundle;
        this.serviceInstance = serviceInstance;
        this.interfaces = interfaces;
        this.props = props;
    }

    /**
     * Toggle the service between registered and unregistered states.
     */
    protected void toggleService() {
        if (serviceReg == null) {
            // register this now
            serviceReg = testBundle.registerService(interfaces, serviceInstance, props);
        }
        else {
            // remove this and delete our reference.
            serviceReg.unregister();
            serviceReg = null;
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
        // called as an initializer.  Most requently, this is a registration
        // activity.
        toggleService();
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
        // called at a phase end.  Normally, this is just a cleanup operation,
        // but some tests might require the opposite behavior.
        toggleService();
    }


    /**
     * Perform any additional validation checks at the end of a test phase.
     * This can perform any validation action needed beyond just
     * event verification.  One good use is to ensure that specific
     * services are actually in the services registry or validating
     * the service properties.
     *
     * @param testContext
     *               The BundleContext for the test (used for inspecting the test
     *               environment).
     *
     * @exception Exception
     */
    public void validate(BundleContext testContext) throws Exception {
        // less frequent, but we can toggle at the validate phase too.
        toggleService();
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
        // we've been asked to respond to an event.
        toggleService();
    }


    /**
     * Give the listener an opportunity to respond to not receiving
     * an expected event.
     *
     * @param results  The test case expected result set.
     * @param expected The expected event.
     */

    public void eventNotReceived(TestEvent expected) throws Exception {
        // this one is a nop.
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
        // also a nop
        return null;
    }
}

