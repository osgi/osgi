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

package org.osgi.test.cases.blueprint.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.service.blueprint.container.EventConstants;
import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;
import org.osgi.test.cases.blueprint.services.ManagedService;
import org.osgi.test.cases.blueprint.services.ServiceManager;
import org.osgi.test.cases.blueprint.services.ServiceManagerImpl;
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
        // We're only going to load one jar for this test.  The services
        // we're dependent upon are handled by a driver created service manager.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/injected_component.jar");

        // create a ServiceManager instance with two instance of an injected service.
        // we will unregister one, and register the second as a replacement.
        ServiceManager serviceManager = new ServiceManagerImpl(getContext(),
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
        importStartEvents.removeEvent(new BlueprintAdminEvent("CREATED"));

        // Ok, when the CREATED event is triggered, we unregister the first service.
        importStartEvents.addEvent(new BlueprintAdminEvent("CREATED", null, null, new ServiceManagerUnregister(serviceManager, "ServiceOneA")));

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
        // dependency will be handled by a ServiceManager instance.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/component_dependson.jar");

        // create a ServiceManager instance with two instancex of an injected service.
        // we will unregister one, and register the second as a replacement.
        ServiceManager serviceManager = new ServiceManagerImpl(getContext(),
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
        importStartEvents.removeEvent(new BlueprintAdminEvent("CREATED"));

        // Ok, when the CREATED event is triggered, we unregister the first service.
        importStartEvents.addEvent(new BlueprintAdminEvent("CREATED", null, null, new ServiceManagerUnregister(serviceManager, "ServiceOneA")));

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
        // dependency will be handled by a ServiceManager instance that we create here.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/service_dependson.jar");

        // create a ServiceManager instance with two instances of an injected service.
        // we will unregister one, and register the second as a replacement.
        ServiceManager serviceManager = new ServiceManagerImpl(getContext(),
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
        importStartEvents.removeEvent(new BlueprintAdminEvent("CREATED"));

        // Ok, when the CREATED event is triggered, we unregister the first service.
        importStartEvents.addEvent(new BlueprintAdminEvent("CREATED", null, null, new ServiceManagerUnregister(serviceManager, "ServiceOneA")));

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
        // the GRACE_PERIOD blueprint event is received.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/injected_component.jar");

        // create a ServiceManager instance with one instance of an injected service.
        // We will register this when we receive then GRACE_PERIOD event from the blueprint service.
        ServiceManager serviceManager = new ServiceManagerImpl(getContext(),
            new ManagedService[] {
                // this one is registered from the start
                new ManagedService("ServiceOneA", new TestGoodService("ServiceOneA"), TestServiceOne.class, getContext(), null, false),
            });

        // now we chain a few events to actions to allow us to track the dynamics.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // we need a property bundle to validate the filter used to request this
        Properties filterProps = new Properties();
        filterProps.put(org.osgi.framework.Constants.OBJECTCLASS, new String[] { TestServiceOne.class.getName() });

        // Ok, when the GRACE_PERIOD event is triggered, we register the first service.
        importStartEvents.addEvent(new BlueprintAdminEvent("GRACE_PERIOD",
            null, new Properties[] { filterProps },
            new ServiceManagerRegister(serviceManager, "ServiceOneA")));
         // and also process the original container event, but without the listener trigger
        importStartEvents.addEvent(new BlueprintContainerEvent("GRACE_PERIOD",
            null, new Properties[] { filterProps }, null));

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
        // dependency will be handled by a locally created ServiceManager.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/nowait_injected_component.jar");

        // create a ServiceManager instance with one instance of an injected service.
        // We will register this when we receive then CREATED event from the blueprint service.
        ServiceManager serviceManager = new ServiceManagerImpl(getContext(),
            new ManagedService[] {
                // this one is registered from the start
                new ManagedService("ServiceOneA", new TestGoodService("ServiceOneA"), TestServiceOne.class, getContext(), null, false),
            });

        // now we chain a few events to actions to allow us to track the dynamics.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // there should be no wait event with this
        importStartEvents.addFailureEvent(new BlueprintAdminEvent("GRACE_PERIOD"));
        importStartEvents.addFailureEvent(new BlueprintContainerEvent("GRACE_PERIOD"));

        importStartEvents.removeEvent(new BlueprintAdminEvent("CREATED"));
        // Ok, when the CREATED event is triggered, we register the first service.
        importStartEvents.addEvent(new BlueprintAdminEvent("CREATED", null, null, new ServiceManagerRegister(serviceManager, "ServiceOneA")));

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
        // we also look for a GRACE_PERIOD event.
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/timeout_injected_component.jar");
        // use a little longer timeout here to give the extender time to time this out.
        controller.setTimeout(30000);

        // now we chain a few events to actions to allow us to track the dynamics.
        EventSet startEvents = controller.getTestEvents();
        // we should see one of these before the failures

        // we need a property bundle to validate the filter used to request this
        Properties filterProps = new Properties();
        filterProps.put(org.osgi.framework.Constants.OBJECTCLASS, new String[] { TestServiceOne.class.getName() });
        filterProps.put("osgi.service.blueprint.compname", "XYZ");
        filterProps.put("someproperty", "abc");

        startEvents.addEvent(new BlueprintContainerEvent("GRACE_PERIOD", null, new Properties[] { filterProps }, null));
        startEvents.addEvent(new BlueprintAdminEvent("GRACE_PERIOD", null, new Properties[] { filterProps }, null));

        // remove the current failure event....we want a more specific one to test the properties getting set.
        startEvents.removeEvent(new BlueprintAdminEvent("FAILURE"));
        // this failure event will verify the information about the failing dependency is set.
        startEvents.addEvent(new BlueprintContainerEvent("FAILURE", null, new Properties[] { filterProps }, null));
        startEvents.addEvent(new BlueprintAdminEvent("FAILURE", null, new Properties[] { filterProps }, null));
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
        // none of these component should be instantiated
        startEvents.addFailureEvent(new ComponentAssertion("ServiceOne", AssertionService.BEAN_INIT_METHOD));

        // now for the middle events.  We'll request a class to be loaded from the bundle, which
        // should trigger the ModuleContext to be created.
        MetadataEventSet middleEvents = controller.getMiddleEvents(0);

        // this will request the registered service, which should kick start the activation process.
        middleEvents.addInitializer(new ServiceRequestInitiator(getContext(), TestServiceOne.class, null));

        // all of the components should now be instantiated
        middleEvents.addAssertion("ServiceOne", AssertionService.BEAN_INIT_METHOD);

        // now some expected termination stuff
        EventSet stopEvents = controller.getStopEvents(0);
        // normal shutdown processing
        stopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        controller.run();
    }


	/**
	 * A lazy initialization test.  This is the same as the lazy activation test, and should
     * display essentially the same behavior.  The service will be registered, but will not
     * create any components until the first service request.
	 */
	public void testServiceRequestInitialization() throws Exception {
        // This uses a special test controller.  The active portions of the test
        // are performed in the middle phase.
        ThreePhaseTestController controller = new ThreePhaseTestController(getContext(),
            getWebServer()+"www/lazy_activation.jar");

        // We're mostly going to tag things we don't expect to see happening, though there are a
        // few positive things we expect to see.
        MetadataEventSet startEvents = controller.getStartEvents(0);
        // we should see a single registration of TestServiceOne
        startEvents.addServiceEvent("REGISTERED", TestServiceOne.class);
        // none of these component should be instantiated
        startEvents.addFailureEvent(new ComponentAssertion("ServiceOne", AssertionService.BEAN_INIT_METHOD));

        // now for the middle events.  We'll request a class to be loaded from the bundle, which
        // should trigger the ModuleContext to be created.
        MetadataEventSet middleEvents = controller.getMiddleEvents(0);

        // this will request the registered service, which should kick start the activation process.
        middleEvents.addInitializer(new ServiceRequestInitiator(getContext(), TestServiceOne.class, null));

        // all of the components should now be instantiated
        middleEvents.addAssertion("ServiceOne", AssertionService.BEAN_INIT_METHOD);

        // now some expected termination stuff
        EventSet stopEvents = controller.getStopEvents(0);
        // normal shutdown processing
        stopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        controller.run();
    }


	/**
	 * Lazy activation processing will wait on dependencies before registering
     * any services.
	 */
	public void testLazyComponentWaitingDependency() throws Exception {
        // We're only going to load one jar for this test.  The unstatisfied
        // dependency will be handled by a service that's registered when
        // the GRACE_PERIOD blueprint event is received.
        LazyActivationTestController controller = new LazyActivationTestController(getContext(),
            getWebServer()+"www/lazy_injected_component.jar");

        // create a ServiceManager instance with one instance of an injected service.
        // We will register this when we receive then GRACE_PERIOD event from the blueprint service.
        ServiceManager serviceManager = new ServiceManagerImpl(getContext(),
            new ManagedService[] {
                // this one is registered from the start
                new ManagedService("ServiceOneA", new TestGoodService("ServiceOneA"), TestServiceOne.class, getContext(), null, true),
            });

        // now we chain a few events to actions to allow us to track the dynamics.
        MetadataEventSet startEvents = controller.getStartEvents(0);

        // Ok, when the GRACE_PERIOD event is triggered, we register the first service.
        // we expect one of these to both handlers, but only one triggers the action

        // we need a property bundle to validate the filter used to request this
        Properties filterProps = new Properties();
        filterProps.put(org.osgi.framework.Constants.OBJECTCLASS, new String[] { TestServiceOne.class.getName() });

        startEvents.addEvent(new BlueprintContainerEvent("GRACE_PERIOD", null, new Properties[] { filterProps }, null));
        startEvents.addEvent(new BlueprintAdminEvent("GRACE_PERIOD", null, new Properties[] { filterProps }, new ServiceManagerRegister(serviceManager, "ServiceOneA")));

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


	/**
	 * A lazy initialization test with eager beans.  Even though the bundle is activated with
     * lazy activation, this should proceed to fully activated state because it contains an
     * eager bean definition.
	 */
	public void testEagerServiceRequestInitialization() throws Exception {
        // This uses a special test controller.  The active portions of the test
        // are performed in the middle phase.
        StandardTestController controller = new StandardTestController(getContext());
        controller.addBundle(getWebServer()+"www/eager_lazy_activation.jar", Bundle.START_ACTIVATION_POLICY | Bundle.START_TRANSIENT);

        // We're mostly going to tag things we don't expect to see happening, though there are a
        // few positive things we expect to see.
        MetadataEventSet startEvents = controller.getStartEvents(0);
        // we should see a single registration of TestServiceOne
        startEvents.addServiceEvent("REGISTERED", TestServiceOne.class);
        // all of the components should now be instantiated
        startEvents.addAssertion("ServiceOne", AssertionService.BEAN_INIT_METHOD);

        // now some expected termination stuff
        EventSet stopEvents = controller.getStopEvents(0);
        // normal shutdown processing
        stopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        controller.run();
    }


	/**
	 * Somewhat complicated test.  The source for the service being exported
     * is a <reference> element.  This will essentially republish a service
     * instance using a different set of properties.  We'll then register and
     * unregister the dependent service watching for the the equivalent deregistration
     * of the service instance
	 */
	public void testServiceReferenceExport() throws Exception {
        // We're only going to load one jar for this test.  The services
        // we're dependent upon are handled by a driver created service manager.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ServiceOne_reference_export.jar");

        // create a ServiceManager instance with one instance of the sevice.  We'll flip
        // this on and off
        ServiceManager serviceManager = new ServiceManagerImpl(getContext(),
            new ManagedService[] {
                // this one is registered from the start
                new ManagedService("ServiceOneA", new TestGoodService("ServiceOneA"), TestServiceOne.class, getContext(), null, true),
            });

        // now we chain a few events to actions to allow us to track the dynamics.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        Map props = new HashMap();
        props.put("serviceType", "Good");
        props.put("autoExport", "Disabled");

        // we need to add one of these at the head of the queue to catch the initial registration.
        // We need to consume this event so that additional triggered events don't interfere.
        importStartEvents.addEvent(new ServiceTestEvent("REGISTERED", TestServiceOne.class, props));

        // a CREATED event is part of our standard set.  We need to remove
        // that one and attach a new one with a listener
        importStartEvents.removeEvent(new BlueprintAdminEvent("CREATED"));

        // Ok, when the CREATED event is triggered, we unregister the first service.
        importStartEvents.addEvent(new BlueprintAdminEvent("CREATED", null, null, new ServiceManagerUnregister(serviceManager, "ServiceOneA")));

        // when our expect service unregisters, then reregister the original
        importStartEvents.addEvent(new ServiceTestEvent("UNREGISTERING", new Class[] { TestServiceOne.class }, props,
            new ServiceManagerRegister(serviceManager, "ServiceOneA")));
        // then we should see this REGISTERED again at the end.
        importStartEvents.addEvent(new ServiceTestEvent("REGISTERED", TestServiceOne.class, props));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // add a cleanup processor for the exported services.
        importStopEvents.addTerminator(new ServiceManagerUnregister(serviceManager));

        controller.run();
    }
}

