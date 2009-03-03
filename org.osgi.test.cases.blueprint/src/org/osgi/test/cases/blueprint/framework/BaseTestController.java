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

import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.service.blueprint.context.ModuleContextEventConstants;
import org.osgi.service.blueprint.context.ModuleContextListener;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.blueprint.services.AssertionService;

import junit.framework.AssertionFailedError;

/**
 * A base class for the different types of test controller.
 */
public class BaseTestController implements EventHandler, ModuleContextListener, ServiceListener {
    // default timeout for the test.  For most tests, we're not dealing with
    // expected timeout situations, so a short timeout is acceptable.  This will
    // need to be set higher if we're testing actual timeout situations.
    public static final long DEFAULT_TIMEOUT = 10000;

    protected long timeout = DEFAULT_TIMEOUT;

    static final String[] topics = new String[] {
        "org/osgi/test/cases/blueprint/*",
        "org/osgi/service/blueprint/context/*",
        "org/osgi/framework/FrameworkEvent/*",
        "org/osgi/framework/BundleEvent/*",
    };

    // the test context we're running in
    protected BundleContext testContext;
    // our list of test phases to be executed.
    protected List testPhases = new LinkedList();
    // the current testphase being executed.
    protected TestPhase activeTestPhase = null;

    // Our listener service registrations
    protected ServiceRegistration eventAdminListener;
    protected ServiceRegistration moduleContextListener;

    // this is static so we can retrieve it without a controller reference.
    static protected Bundle extenderBundle;

    /**
     * Constructor for a testcase base.
     *
     * @param testContext
     *               The test context bundle.
     */
    public BaseTestController(BundleContext testContext, long timeout) {
        this.testContext = testContext;
        this.timeout = timeout;
    }

    /**
     * Return the extender bundle that we believe we're running
     * against.
     *
     * @return
     */
    static public Bundle getExtenderBundle(Map props) {
        // if we've not been asked for this yet, extract it from the properties
        if (extenderBundle == null) {
            extenderBundle = (Bundle)props.get(ModuleContextEventConstants.EXTENDER_BUNDLE);
        }
        return extenderBundle;
    }


    /**
     * Add a test phase to the test controller.  This is added
     * to the end of the phase list.
     *
     * @param phase
     */
    public void addTestPhase(TestPhase phase) {
        testPhases.add(phase);
    }


    /**
     * Add a test phase to the test controller.  This is added
     * to the end of the phase list.
     *
     * @param index  The index position to add the phase.  This allows phases to be
     *               inserted between phases.
     * @param phase
     */
    public void addTestPhase(int index, TestPhase phase) {
        testPhases.add(index, phase);
    }

    /**
     * Perform any controller-related testcase setup steps.
     */
    public void setup() throws Exception {
        // this is largely for subclasses to override.  The testphases
        // manage their own startup, so they don't need to be nudged.
    }

    /**
     * Perform any controller-related testcase cleanup steps.
     *
     * @param runner The test case running controlling the tests.
     */
    public void cleanup() throws Exception {
        // have all of the phases perform any needed cleanup (bundle uninsallt, etc.)
        for (int i = 0; i < testPhases.size(); i++) {
            TestPhase phase = (TestPhase)testPhases.get(i);
            phase.cleanup(testContext);
        }
    }

    /**
     * Run the actual test.
     *
     * @param runner The runner instance.
     *
     * @exception Exception
     */
    protected void runTest() throws Exception {
        Iterator i = testPhases.iterator();
        // run each of the phases in the prescribed order.
        while (i.hasNext()) {
            // this also sets the active phase used for event dispatch.
            activeTestPhase = (TestPhase)i.next();
            activeTestPhase.runTest();
        }
        // no more event processing
        activeTestPhase = null;
    }


    /**
     * Handle an event dispatched from the test runner.  This is
     * pre-processed by event type, and then passed on to the active
     * test phase.
     *
     * @param event  The event to process.
     */
	public void handleEvent(Event event) {
        // use a local copy of this to ensure the active phase is not
        // swiped from underneath us.
        TestPhase targetPhase = activeTestPhase;
        if (targetPhase == null) {
            return;
        }

        String topic = event.getTopic();
        // one of our assertions
        if (topic.startsWith("org/osgi/test/cases/blueprint/ModuleContext")) {
            processEvent(targetPhase, new ModuleContextEvent(event));
        }
        else if (topic.startsWith("org/osgi/test/cases/blueprint/")) {
            processEvent(targetPhase, new ComponentAssertion(event));
        }
        // one of our assertions
        else if (topic.startsWith("org/osgi/service/blueprint/context")) {
            processEvent(targetPhase, new BlueprintEvent(event));
        }
        else if (topic.startsWith("org/osgi/framework/FrameworkEvent/")) {
            processEvent(targetPhase, new FrameworkTestEvent(event));
        }
        else if (topic.startsWith("org/osgi/framework/BundleEvent/")) {
            processEvent(targetPhase, new BundleTestEvent(event));
        }
        else if (topic.startsWith("org/osgi/framework/ServiceEvent/")) {
            processEvent(targetPhase, new ServiceTestEvent(event));
        }
    }


    /**
     * Process a received event type by checking it against our
     * expected results and determining if we need to raise this as
     * a failing condition.
     *
     * @param event  The received test event type.
     */
    private void processEvent(TestPhase targetPhase, TestEvent event) {
        try {
            System.out.println(">>>>>>>>>> Processing event " + event);
            // pass this along to the current phase.
            targetPhase.handleEvent(event);
        } catch (Throwable e) {
            // something bad happened with the event.  Make sure we at least
            // see this
            System.out.println(">>>> Exception processing event: " + event);
            e.printStackTrace();
        }
    }


    /**
     * Perform test startup operations.
     */
    private void startup() throws Exception {
        // this initializes the assertion service so our created components can see them
        AssertionService.initService(testContext);

        // register a listener
        Hashtable handlerProps = new Hashtable();
        handlerProps.put(EventConstants.EVENT_TOPIC, topics);
        this.eventAdminListener = testContext.registerService(EventHandler.class.getName(), this, handlerProps);
        this.moduleContextListener = testContext.registerService(ModuleContextListener.class.getName(), this, null);
        // there are some race conditions involved with service listeners that
        // preclude us using the EventAdmin service for snagging those events.  We'll need to listen for them directly.
        testContext.addServiceListener(this);
        // give our class implementation an opportunity to do additional setup.
        setup();
    }

    /**
     * Perform cleanup on this result set.  In particular, this
     * removes the event listener and stops the bundle.  Called
     * at the end of processing.
     */
    private void terminate() throws Exception {
        // give our subclass an opportunity to do additional cleanup.
        cleanup();
        // remove our listeners
        eventAdminListener.unregister();
        moduleContextListener.unregister();
        testContext.removeServiceListener(this);
        // have the assertion service clean things up
        AssertionService.cleanupService();
    }

    /**
     * Run the tests using specific timeout intervals.
     *
     * @param timeout The timeout value (in milliseconds).
     */
    public void run() throws Exception {
        try {
            // perform any startup operations
            startup();
            // have the controller run the tests
            // specialized subclasses might do things differently
            runTest();
        } finally {
            // cleanup test resources, remove listeners, etc.
            terminate();
        }
    }


    /**
     * Method implemented for the ModuleContextListener interface. This
     * transforms the ModuleContextEvent information into a dummy Event
     * for processing.
     *
     * @param bundle
     *                The bundle creating the context
     * @param version The bundle version information.
     */
    public void contextCreated(Bundle bundle) {
        // just turn this into a special event typed.
        Dictionary props = new Hashtable();
        props.put(EventConstants.BUNDLE_SYMBOLICNAME, bundle.getSymbolicName());
        props.put("bundle.version", Version.parseVersion((String)bundle.getHeaders().get(Constants.BUNDLE_VERSION)));
        props.put(EventConstants.BUNDLE, bundle);
        props.put(EventConstants.BUNDLE_ID, new Long(bundle.getBundleId()));
        handleEvent(new Event("org/osgi/test/cases/blueprint/ModuleContext/CREATED", props));
    }


    /**
     * Method implemented for the ModuleContextListener interface. This
     * transforms the ModuleContextEvent information into a dummy Event
     * for processing.
     *
     * @param bundleSymbolicName
     *                The symbolic name for the context bundle.
     * @param version The bundle version information.
     */
    public void contextCreationFailed(Bundle bundle, Throwable rootCause) {
        // just turn this into a special event typed.
        Dictionary props = new Hashtable();
        props.put(EventConstants.BUNDLE_SYMBOLICNAME, bundle.getSymbolicName());
        props.put("bundle.version", Version.parseVersion((String)bundle.getHeaders().get(Constants.BUNDLE_VERSION)));
        props.put(EventConstants.BUNDLE, bundle);
        props.put(EventConstants.BUNDLE_ID, new Long(bundle.getBundleId()));
        if (rootCause != null) {
            props.put(EventConstants.EXCEPTION, rootCause);
        }
        handleEvent(new Event("org/osgi/test/cases/blueprint/ModuleContext/FAILED", props));
    }

    /**
     * Event handler for a ServiceEvent listener.  This
     * transforms the service event into one of our
     * internal events for processing.
     *
     * @param event  The source event.
     */
    public void serviceChanged(ServiceEvent event) {
        int type = event.getType();
        String topic = null;
        if (type == ServiceEvent.REGISTERED) {
            topic = "org/osgi/framework/ServiceEvent/REGISTERED";
        }
        else if (type == ServiceEvent.UNREGISTERING) {
            topic = "org/osgi/framework/ServiceEvent/UNREGISTERING";
        }
        else if (type == ServiceEvent.MODIFIED) {
            topic = "org/osgi/framework/ServiceEvent/MODIFIED";
        }

        ServiceReference ref = event.getServiceReference();
        Bundle bundle = ref.getBundle();

        // just turn this into a special event typed.
        Dictionary props = new Hashtable();
        props.put(EventConstants.BUNDLE_SYMBOLICNAME, bundle.getSymbolicName());
        props.put("bundle.version", Version.parseVersion((String)bundle.getHeaders().get(Constants.BUNDLE_VERSION)));
        props.put(EventConstants.BUNDLE, bundle);
        props.put(EventConstants.BUNDLE_ID, new Long(bundle.getBundleId()));
        props.put(Constants.OBJECTCLASS, ref.getProperty(Constants.OBJECTCLASS));
        handleEvent(new Event(topic, props));
    }


    /**
     * Install a bundle for this test context.
     *
     * @param bundleName The fully qualified bundle name.
     *
     * @return The installed bundle.
     * @exception Exception
     */
    public Bundle installBundle(String bundleName) throws Exception {
        URL url = new URL(bundleName);
        InputStream in = url.openStream();

        return testContext.installBundle(bundleName, in);
    }

    /**
     * Override the default timeout value.
     *
     * @param timeout The new timeout length.
     */
    public void setTimeout(long timeout) {
        // the TestPhases are already created, so we need to
        // explicitly set each of these.
        this.timeout = timeout;
        for (int i = 0; i < testPhases.size(); i++) {
            TestPhase phase = (TestPhase)testPhases.get(i);
            phase.setTimeout(timeout);
        }
    }


    /**
     * Retrieve the timeout length.
     *
     * @return The current timeout value.
     */
    public long getTimeout() {
        return timeout;
    }
}

