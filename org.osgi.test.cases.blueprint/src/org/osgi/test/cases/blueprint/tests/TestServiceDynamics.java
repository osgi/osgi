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
package org.osgi.test.cases.blueprint.tests;

import org.osgi.test.cases.blueprint.framework.BlueprintEvent;
import org.osgi.test.cases.blueprint.framework.ClassLoadInitiator;
import org.osgi.test.cases.blueprint.framework.ComponentAssertion;
import org.osgi.test.cases.blueprint.framework.EventSet;
import org.osgi.test.cases.blueprint.framework.LazyActivationTestController;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.ServiceBlueprintEvent;
import org.osgi.test.cases.blueprint.framework.ServiceManagerRegister;
import org.osgi.test.cases.blueprint.framework.ServiceManagerUnregister;
import org.osgi.test.cases.blueprint.framework.ServiceRequestInitiator;
import org.osgi.test.cases.blueprint.framework.ServiceTestEvent;
import org.osgi.test.cases.blueprint.framework.StandardErrorTestController;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.ManagedService;
import org.osgi.test.cases.blueprint.services.ServiceManager;
import org.osgi.test.cases.blueprint.services.TestGoodService;
import org.osgi.test.cases.blueprint.services.TestServiceAllSubclass;
import org.osgi.test.cases.blueprint.services.TestServiceDynamicsInterface;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.TestServiceTwo;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class tests service registry dynamics.  In particular,
 * how service depencencies control registration/deregistration of
 * services that have director or indirect dependencies on registered
 * services.
 *
 * @version $Revision$
 */
public class TestServiceDynamics extends DefaultTestBundleControl {
	/**
	 * Test component dependency dynamics involved with a service export
     * that depends on an injected service.
	 */
	public void testComponentDirectDependency() throws Exception {
        // We're only going to load one jar for this test.  The unstatisfied
        // dependency will be handled by a service that's registered when
        // the WAITING blueprint event is received.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/injected_component.jar");

        // create a ServiceManager instance with two instancex of an injected service.
        // we will unregister one, and register the second as a replacement.
        ServiceManager serviceManager = new ServiceManager(getContext(),
            new ManagedService[] {
                // this one is registered from the start
                new ManagedService("ServiceOneA", new TestGoodService("ServiceOneA"), TestServiceOne.class, getContext(), null, true),
                // this one gets registered as a replacement
                new ManagedService("ServiceOneB", new TestGoodService("ServiceOneB"), TestServiceOne.class, getContext(), null, false)
            });

        // now we chain a few events to actions to allow us to track the dynamics.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // we need to add one of these at the head of the queue to catch the initial registration.
        // If we don't then the initial registration will trigger the the registration event below and
        // this will likely hang.
        importStartEvents.addServiceEvent("REGISTERED", TestServiceDynamicsInterface.class);

        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        importStartEvents.removeEvent(new BlueprintEvent("CREATED"));

        // Ok, when the CREATED event is triggered, we unregister the first service.
        importStartEvents.addEvent(new BlueprintEvent("CREATED", null, new ServiceManagerUnregister(serviceManager, "ServiceOneA")));

        // when our expect service unregisters, then add a replacement service
        importStartEvents.addEvent(new ServiceTestEvent("UNREGISTERING", new Class[] { TestServiceDynamicsInterface.class }, null,
            new ServiceManagerRegister(serviceManager, "ServiceOneB")));
        // then we should see this REGISTERED again at the end.
        importStartEvents.addServiceEvent("REGISTERED", TestServiceDynamicsInterface.class);

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // add a cleanup processor for the exported services.
        importStopEvents.addTerminator(new ServiceManagerUnregister(serviceManager));

        controller.run();
    }


	/**
	 * Essentially the same as the previous test, but the dependency relationship is
     * through a depends-on attribute of the exported component.
	 */
	public void testComponentDependsOnDependency() throws Exception {
        // We're only going to load one jar for this test.  The unstatisfied
        // dependency will be handled by a service that's registered when
        // the WAITING blueprint event is received.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/component_dependson.jar");

        // create a ServiceManager instance with two instancex of an injected service.
        // we will unregister one, and register the second as a replacement.
        ServiceManager serviceManager = new ServiceManager(getContext(),
            new ManagedService[] {
                // this one is registered from the start
                new ManagedService("ServiceOneA", new TestGoodService("ServiceOneA"), TestServiceOne.class, getContext(), null, true),
                // this one gets registered as a replacement
                new ManagedService("ServiceOneB", new TestGoodService("ServiceOneB"), TestServiceOne.class, getContext(), null, false)
            });

        // now we chain a few events to actions to allow us to track the dynamics.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // we need to add one of these at the head of the queue to catch the initial registration.
        // If we don't then the initial registration will trigger the the registration event below and
        // this will likely hang.
        importStartEvents.addServiceEvent("REGISTERED", TestServiceDynamicsInterface.class);

        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        importStartEvents.removeEvent(new BlueprintEvent("CREATED"));

        // Ok, when the CREATED event is triggered, we unregister the first service.
        importStartEvents.addEvent(new BlueprintEvent("CREATED", null, new ServiceManagerUnregister(serviceManager, "ServiceOneA")));

        // when our expect service unregisters, add a replacement service
        importStartEvents.addEvent(new ServiceTestEvent("UNREGISTERING", new Class[] { TestServiceDynamicsInterface.class }, null,
            new ServiceManagerRegister(serviceManager, "ServiceOneB")));
        // then we should see this REGISTERED again at the end.
        importStartEvents.addServiceEvent("REGISTERED", TestServiceDynamicsInterface.class);

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // add a cleanup processor for the exported services.
        importStopEvents.addTerminator(new ServiceManagerUnregister(serviceManager));

        controller.run();
    }


	/**
	 * Another test very similar to the previous two.  This one uses a depends-on relationship
     * on the service instance to gate the service registration.
	 */
	public void testServiceDependsOnDependency() throws Exception {
        // We're only going to load one jar for this test.  The unstatisfied
        // dependency will be handled by a service that's registered when
        // the WAITING blueprint event is received.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/service_dependson.jar");

        // create a ServiceManager instance with two instances of an injected service.
        // we will unregister one, and register the second as a replacement.
        ServiceManager serviceManager = new ServiceManager(getContext(),
            new ManagedService[] {
                // this one is registered from the start
                new ManagedService("ServiceOneA", new TestGoodService("ServiceOneA"), TestServiceOne.class, getContext(), null, true),
                // this one gets registered as a replacement
                new ManagedService("ServiceOneB", new TestGoodService("ServiceOneB"), TestServiceOne.class, getContext(), null, false)
            });

        // now we chain a few events to actions to allow us to track the dynamics.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // we need to add one of these at the head of the queue to catch the initial registration.
        // If we don't then the initial registration will trigger the the registration event below and
        // this will likely hang.
        importStartEvents.addServiceEvent("REGISTERED", TestServiceDynamicsInterface.class);

        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        importStartEvents.removeEvent(new BlueprintEvent("CREATED"));

        // Ok, when the CREATED event is triggered, we unregister the first service.
        importStartEvents.addEvent(new BlueprintEvent("CREATED", null, new ServiceManagerUnregister(serviceManager, "ServiceOneA")));

        // when our expect service unregisters, add a replacement service
        importStartEvents.addEvent(new ServiceTestEvent("UNREGISTERING", new Class[] { TestServiceDynamicsInterface.class }, null,
            new ServiceManagerRegister(serviceManager, "ServiceOneB")));
        // then we should see this REGISTERED again at the end.
        importStartEvents.addServiceEvent("REGISTERED", TestServiceDynamicsInterface.class);

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // add a cleanup processor for the exported services.
        importStopEvents.addTerminator(new ServiceManagerUnregister(serviceManager));

        controller.run();
    }


	/**
	 * Test component dependency dynamics involved with a service export
     * that depends on an injected service.
	 */
	public void testComponentWaitingDependency() throws Exception {
        // We're only going to load one jar for this test.  The unstatisfied
        // dependency will be handled by a service that's registered when
        // the WAITING blueprint event is received.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/injected_component.jar");

        // create a ServiceManager instance with one instance of an injected service.
        // We will register this when we receive then WAITING event from the blueprint service.
        ServiceManager serviceManager = new ServiceManager(getContext(),
            new ManagedService[] {
                // this one is registered from the start
                new ManagedService("ServiceOneA", new TestGoodService("ServiceOneA"), TestServiceOne.class, getContext(), null, false),
            });

        // now we chain a few events to actions to allow us to track the dynamics.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // Ok, when the WAITING event is triggered, we register the first service.
        importStartEvents.addEvent(new ServiceBlueprintEvent("WAITING", new Class[] { TestServiceOne.class },
            null, new ServiceManagerRegister(serviceManager, "ServiceOneA")));

        // then we should see this REGISTERED again at the end.
        importStartEvents.addServiceEvent("REGISTERED", TestServiceDynamicsInterface.class);

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // add a cleanup processor for the exported services.
        importStopEvents.addTerminator(new ServiceManagerUnregister(serviceManager));

        controller.run();
    }


	/**
	 * Test component dependency dynamics involved with blueprint.wait-for-dependencies:=false
	 */
	public void testComponentNowaitDependency() throws Exception {
        // We're only going to load one jar for this test.  The unstatisfied
        // dependency will be handled by a service that's registered when
        // the WAITING blueprint event is received.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/nowait_injected_component.jar");

        // create a ServiceManager instance with one instance of an injected service.
        // We will register this when we receive then CREATED event from the blueprint service.
        ServiceManager serviceManager = new ServiceManager(getContext(),
            new ManagedService[] {
                // this one is registered from the start
                new ManagedService("ServiceOneA", new TestGoodService("ServiceOneA"), TestServiceOne.class, getContext(), null, false),
            });

        // now we chain a few events to actions to allow us to track the dynamics.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // there should be no wait event with this
        importStartEvents.addFailureEvent(new BlueprintEvent("WAITING"));

        // Ok, when the CREATED event is triggered, we register the first service.
        importStartEvents.addEvent(new BlueprintEvent("CREATED", null, new ServiceManagerRegister(serviceManager, "ServiceOneA")));

        // then we should see this REGISTERED at the end.
        importStartEvents.addServiceEvent("REGISTERED", TestServiceDynamicsInterface.class);

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // add a cleanup processor for the exported services.
        importStopEvents.addTerminator(new ServiceManagerUnregister(serviceManager));

        controller.run();
    }


	/**
	 * Test component dependency dynamics involved with blueprint.wait-for-dependencies:=false
	 */
	public void testComponentTimeoutDependency() throws Exception {
        // this is testing a dependency wait time out.  This will be an error test, but
        // we also look for a WAITING event.
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/timeout_injected_component.jar");
        // use a little longer timeout here to give the extender time to time this out.
        controller.setTimeout(30000);

        // now we chain a few events to actions to allow us to track the dynamics.
        EventSet startEvents = controller.getTestEvents();
        // we should see one of these before the failures
        startEvents.addEvent(new ServiceBlueprintEvent("WAITING", new Class[] { TestServiceOne.class }));
        // remove the current failure event....we want a more specific one to test the properties getting set.
        startEvents.removeEvent(new BlueprintEvent("FAILURE"));
        // this failure event will verify the information about the failing dependency is set.
        startEvents.addEvent(new ServiceBlueprintEvent("FAILURE", new Class[] { TestServiceOne.class }));
        controller.run();
    }


	/**
	 * A lazy activation test.  The started bundle should register a single service, then
     * register two additional ones when triggered by a class load.
	 */
	public void testClassLoadingActivation() throws Exception {
        // This uses a special test controller.  The active portions of the test
        // are performed in the middle phase.
        LazyActivationTestController controller = new LazyActivationTestController(getContext(),
            getWebServer()+"www/lazy_activation.jar");

        // We're mostly going to tag things we don't expect to see happening, though there are a
        // few positive things we expect to see.
        MetadataEventSet startEvents = controller.getStartEvents(0);
        // we should see a single registration of TestServiceOne
        startEvents.addServiceEvent("REGISTERED", TestServiceOne.class);
        // either of these getting registered will be a failure
        startEvents.addFailureEvent(new ServiceTestEvent("REGISTERED", TestServiceTwo.class));
        startEvents.addFailureEvent(new ServiceTestEvent("REGISTERED", new Class[] { TestServiceOne.class, TestServiceTwo.class,
            TestServiceAllSubclass.class, ComponentTestInfo.class }));
        // none of these component should be instantiated
        startEvents.addFailureEvent(new ComponentAssertion("ServiceOne", AssertionService.COMPONENT_INIT_METHOD));
        startEvents.addFailureEvent(new ComponentAssertion("ServiceTwo", AssertionService.COMPONENT_INIT_METHOD));
        startEvents.addFailureEvent(new ComponentAssertion("ServiceThree", AssertionService.COMPONENT_INIT_METHOD));

        // now for the middle events.  We'll request a class to be loaded from the bundle, which
        // should trigger the ModuleContext to be created.
        MetadataEventSet middleEvents = controller.getMiddleEvents(0);

        // this will load a class from the started bundle, which will kick off bundle
        // activation processing.
        middleEvents.addInitializer(new ClassLoadInitiator());

        // all of the components should now be instantiated
        middleEvents.addAssertion("ServiceOne", AssertionService.COMPONENT_INIT_METHOD);
        middleEvents.addAssertion("ServiceTwo", AssertionService.COMPONENT_INIT_METHOD);
        middleEvents.addAssertion("ServiceThree", AssertionService.COMPONENT_INIT_METHOD);

        // we should see these registered now.
        middleEvents.addServiceEvent("REGISTERED", TestServiceTwo.class);
        middleEvents.addServiceEvent("REGISTERED", new Class[] { TestServiceOne.class, TestServiceTwo.class,
            TestServiceAllSubclass.class, ComponentTestInfo.class });

        // now some expected termination stuff
        EventSet stopEvents = controller.getStopEvents(0);
        // normal shutdown processing
        stopEvents.addFailureEvent(new ServiceTestEvent("UNREGISTERING", TestServiceOne.class));
        stopEvents.addFailureEvent(new ServiceTestEvent("UNREGISTERING", TestServiceTwo.class));
        stopEvents.addFailureEvent(new ServiceTestEvent("UNREGISTERING", new Class[] { TestServiceOne.class, TestServiceTwo.class,
            TestServiceAllSubclass.class, ComponentTestInfo.class }));

        controller.run();
    }


	/**
	 * A lazy activation test.  The started bundle should register a single service, then
     * register two additional ones when triggered by a service request for the first.
	 */
	public void testServiceRequestActivation() throws Exception {
        // This uses a special test controller.  The active portions of the test
        // are performed in the middle phase.
        LazyActivationTestController controller = new LazyActivationTestController(getContext(),
            getWebServer()+"www/lazy_activation.jar");

        // We're mostly going to tag things we don't expect to see happening, though there are a
        // few positive things we expect to see.
        MetadataEventSet startEvents = controller.getStartEvents(0);
        // we should see a single registration of TestServiceOne
        startEvents.addServiceEvent("REGISTERED", TestServiceOne.class);
        // either of these getting registered will be a failure
        startEvents.addFailureEvent(new ServiceTestEvent("REGISTERED", TestServiceTwo.class));
        startEvents.addFailureEvent(new ServiceTestEvent("REGISTERED", new Class[] { TestServiceOne.class, TestServiceTwo.class,
            TestServiceAllSubclass.class, ComponentTestInfo.class }));
        // none of these component should be instantiated
        startEvents.addFailureEvent(new ComponentAssertion("ServiceOne", AssertionService.COMPONENT_INIT_METHOD));
        startEvents.addFailureEvent(new ComponentAssertion("ServiceTwo", AssertionService.COMPONENT_INIT_METHOD));
        startEvents.addFailureEvent(new ComponentAssertion("ServiceThree", AssertionService.COMPONENT_INIT_METHOD));

        // now for the middle events.  We'll request a class to be loaded from the bundle, which
        // should trigger the ModuleContext to be created.
        MetadataEventSet middleEvents = controller.getMiddleEvents(0);

        // this will request the registered service, which should kick start the activation process.
        middleEvents.addInitializer(new ServiceRequestInitiator(getContext(), TestServiceOne.class, null));

        // all of the components should now be instantiated
        middleEvents.addAssertion("ServiceOne", AssertionService.COMPONENT_INIT_METHOD);
        middleEvents.addAssertion("ServiceTwo", AssertionService.COMPONENT_INIT_METHOD);
        middleEvents.addAssertion("ServiceThree", AssertionService.COMPONENT_INIT_METHOD);

        // we should see these registered now.
        middleEvents.addServiceEvent("REGISTERED", TestServiceTwo.class);
        middleEvents.addServiceEvent("REGISTERED", new Class[] { TestServiceOne.class, TestServiceTwo.class,
            TestServiceAllSubclass.class, ComponentTestInfo.class });

        // now some expected termination stuff
        EventSet stopEvents = controller.getStopEvents(0);
        // normal shutdown processing
        stopEvents.addFailureEvent(new ServiceTestEvent("UNREGISTERING", TestServiceOne.class));
        stopEvents.addFailureEvent(new ServiceTestEvent("UNREGISTERING", TestServiceTwo.class));
        stopEvents.addFailureEvent(new ServiceTestEvent("UNREGISTERING", new Class[] { TestServiceOne.class, TestServiceTwo.class,
            TestServiceAllSubclass.class, ComponentTestInfo.class }));

        controller.run();
    }


	/**
	 * Lazy activation processing will wait on dependencies before registering
     * any services.
	 */
	public void testLazyComponentWaitingDependency() throws Exception {
        // We're only going to load one jar for this test.  The unstatisfied
        // dependency will be handled by a service that's registered when
        // the WAITING blueprint event is received.
        LazyActivationTestController controller = new LazyActivationTestController(getContext(),
            getWebServer()+"www/lazy_injected_component.jar");

        // create a ServiceManager instance with one instance of an injected service.
        // We will register this when we receive then WAITING event from the blueprint service.
        ServiceManager serviceManager = new ServiceManager(getContext(),
            new ManagedService[] {
                // this one is registered from the start
                new ManagedService("ServiceOneA", new TestGoodService("ServiceOneA"), TestServiceOne.class, getContext(), null, true),
            });

        // now we chain a few events to actions to allow us to track the dynamics.
        MetadataEventSet startEvents = controller.getStartEvents(0);

        // Ok, when the WAITING event is triggered, we register the first service.
        startEvents.addEvent(new BlueprintEvent("WAITING", null, new ServiceManagerRegister(serviceManager, "ServiceOneA")));

        // then we should see this REGISTERED again at the end.
        startEvents.addServiceEvent("REGISTERED", TestServiceDynamicsInterface.class);

        // now for the middle events.  We'll request a class to be loaded from the bundle, which
        // should trigger the ModuleContext to be created.
        MetadataEventSet middleEvents = controller.getMiddleEvents(0);

        // this will request the registered service, which should kick start the activation process.
        middleEvents.addInitializer(new ServiceRequestInitiator(getContext(), TestServiceDynamicsInterface.class, null));

        // now some expected termination stuff
        EventSet stopEvents = controller.getStopEvents(0);
        stopEvents.addServiceEvent("UNREGISTERING", TestServiceDynamicsInterface.class);
        // add a cleanup processor for the exported services.
        stopEvents.addTerminator(new ServiceManagerUnregister(serviceManager));

        controller.run();
    }
}

