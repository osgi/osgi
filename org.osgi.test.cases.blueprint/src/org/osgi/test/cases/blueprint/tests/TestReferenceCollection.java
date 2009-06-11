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

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.osgi.service.blueprint.reflect.RefListMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;
import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.ServiceManager;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.TestServiceTwo;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class tests import of services via reference collections.
 *
 * @version $Revision$
 */
public class TestReferenceCollection extends DefaultTestBundleControl {
    /**
     * Import a list of services and validate against an expected set.
     */
    public void testStaticListCollectionImport() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/static_reference_list_import.jar");

        // this installs and starts the bundle containing the reference services first to
        // ensure the timing of the initial service listener calls.
        controller.addSetupBundle(getWebServer()+"www/managed_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // The collection should be initialized with two services.  There should
        // be a BIND event broadcast for both at startup
        Hashtable propsA = new Hashtable();
        propsA.put("service.interface.name", TestServiceOne.class.getName());
        propsA.put("service.listener.type", "interface");
        propsA.put("test.service.name", "ServiceOneA");

        Hashtable propsC = new Hashtable();
        propsC.put("service.interface.name", TestServiceOne.class.getName());
        propsC.put("service.listener.type", "interface");
        propsC.put("test.service.name", "ServiceOneC");

        // We should see both of these initially
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // validate the metadata for the imported service manager, since it is directly used
        importStartEvents.addValidator(new PropertyMetadataValidator("ReferenceChecker",
                new TestProperty(new TestComponentValue(
                new ReferencedService(null, ServiceManager.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.INITIALIZATION_LAZY,
                null, null, null, ReferencedService.DEFAULT_TIMEOUT)), "serviceManager")));

        // and one for the reference to the service, which should just be a component reference
        importStartEvents.addValidator(new PropertyMetadataValidator("ReferenceChecker",
                new TestProperty(new TestRefValue("TestCollection"), "list")));

        // and the collection metadata
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferenceCollection("TestCollection",
                TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL,
                ServiceReferenceMetadata.INITIALIZATION_EAGER, null, null,
                new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind")},
                RefListMetadata.USE_SERVICE_OBJECT)));

        // this validates the ModuleContext.getComponent() result
        importStartEvents.addValidator(new ReferenceListValidator("TestCollection"));
        importStartEvents.addValidator(new ComponentNamePresenceValidator("TestCollection"));
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("TestCollection"));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // We should get these at the end
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

        controller.run();
    }


    /**
     * Import a list of services and validate against an expected set.  Also has a depends-on relationship
     * to some lazy beans.
     */
    public void testListCollectionDependson() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ref_list_dependson.jar");

        // this installs and starts the bundle containing the reference services first to
        // ensure the timing of the initial service listener calls.
        controller.addSetupBundle(getWebServer()+"www/managed_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // The collection should be initialized with two services.  There should
        // be a BIND event broadcast for both at startup
        Hashtable propsA = new Hashtable();
        propsA.put("service.interface.name", TestServiceOne.class.getName());
        propsA.put("service.listener.type", "interface");
        propsA.put("test.service.name", "ServiceOneA");

        Hashtable propsC = new Hashtable();
        propsC.put("service.interface.name", TestServiceOne.class.getName());
        propsC.put("service.listener.type", "interface");
        propsC.put("test.service.name", "ServiceOneC");

        // we should see both the construction and INIT of this.  It's difficult to test the ordering,
        // but we should see the events from this lazy bean.
        importStartEvents.addAssertion("dependsleaf2", AssertionService.COMPONENT_CREATED);
        importStartEvents.addAssertion("dependsleaf2", AssertionService.COMPONENT_INIT_METHOD);
        importStartEvents.addAssertion("dependsleaf1", AssertionService.COMPONENT_CREATED);
        importStartEvents.addAssertion("dependsleaf1", AssertionService.COMPONENT_INIT_METHOD);

        // We should see both of these initially
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // validate the metadata for the imported service manager, since it is directly used
        importStartEvents.addValidator(new PropertyMetadataValidator("ReferenceChecker",
                new TestProperty(new TestComponentValue(
                new ReferencedService(null, ServiceManager.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.INITIALIZATION_LAZY,
                null, null, null, ReferencedService.DEFAULT_TIMEOUT)), "serviceManager")));

        // and one for the reference to the service, which should just be a component reference
        importStartEvents.addValidator(new PropertyMetadataValidator("ReferenceChecker",
                new TestProperty(new TestRefValue("TestCollection"), "list")));

        // and the collection metadata
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferenceCollection("TestCollection",
                TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL,
                ServiceReferenceMetadata.INITIALIZATION_EAGER, null,
                new String[] { "dependsleaf1", "dependsleaf2" },
                new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind")},
                RefListMetadata.USE_SERVICE_OBJECT)));

        // this validates the ModuleContext.getComponent() result
        importStartEvents.addValidator(new ReferenceListValidator("TestCollection"));
        importStartEvents.addValidator(new ComponentNamePresenceValidator("TestCollection"));
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("TestCollection"));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // We should get these at the end
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

        importStopEvents.addAssertion("dependsleaf1", AssertionService.COMPONENT_DESTROY_METHOD);
        importStopEvents.addAssertion("dependsleaf2", AssertionService.COMPONENT_DESTROY_METHOD);

        controller.run();
    }


    /**
     * Test lazy initialization of a reference list.  The listeners and dependencies should all have delayed
     * initialization until the collection is requested
     */
    public void testLazyReferenceList() throws Exception {
        ThreePhaseTestController controller = new ThreePhaseTestController(getContext(),
                getWebServer()+"www/lazy_ref_list.jar");

        // this installs and starts the bundle containing the reference services first to
        // ensure the timing of the initial service listener calls.
        controller.addSetupBundle(getWebServer()+"www/managed_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // There will all sorts of events that will signal a failure if we see them
        importStartEvents.addFailureEvent(new ComponentAssertion("ReferenceChecker", AssertionService.COMPONENT_CREATED));
        importStartEvents.addFailureEvent(new ComponentAssertion("dependsleaf1", AssertionService.COMPONENT_CREATED));
        importStartEvents.addFailureEvent(new ComponentAssertion("dependsleaf2", AssertionService.COMPONENT_CREATED));
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.COMPONENT_CREATED));

        MetadataEventSet importMiddleEvents = controller.getMiddleEvents(0);
        // this will start the initialization cascade
        importMiddleEvents.addInitializer(new LazyComponentStarter("ReferenceChecker"));

        // The collection should be initialized with two services.  There should
        // be a BIND event broadcast for both at startup
        Hashtable propsA = new Hashtable();
        propsA.put("service.interface.name", TestServiceOne.class.getName());
        propsA.put("service.listener.type", "interface");
        propsA.put("test.service.name", "ServiceOneA");

        Hashtable propsC = new Hashtable();
        propsC.put("service.interface.name", TestServiceOne.class.getName());
        propsC.put("service.listener.type", "interface");
        propsC.put("test.service.name", "ServiceOneC");

        // we should see both the construction and INIT of this.  It's difficult to test the ordering,
        // but we should see the events from this lazy bean.
        importMiddleEvents.addAssertion("dependsleaf2", AssertionService.COMPONENT_CREATED);
        importMiddleEvents.addAssertion("dependsleaf2", AssertionService.COMPONENT_INIT_METHOD);
        importMiddleEvents.addAssertion("dependsleaf1", AssertionService.COMPONENT_CREATED);
        importMiddleEvents.addAssertion("dependsleaf1", AssertionService.COMPONENT_INIT_METHOD);
        importMiddleEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.COMPONENT_CREATED));
        importMiddleEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.COMPONENT_INIT_METHOD));
        importMiddleEvents.addEvent(new ComponentAssertion("ReferenceChecker", AssertionService.COMPONENT_CREATED));

        // We should see both of these initially
        importMiddleEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importMiddleEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // validate the metadata for the imported service manager, since it is directly used
        importMiddleEvents.addValidator(new PropertyMetadataValidator("ReferenceChecker",
                new TestProperty(new TestComponentValue(
                new ReferencedService(null, ServiceManager.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.INITIALIZATION_LAZY,
                null, null, null, ReferencedService.DEFAULT_TIMEOUT)), "serviceManager")));

        // and one for the reference to the service, which should just be a component reference
        importMiddleEvents.addValidator(new PropertyMetadataValidator("ReferenceChecker",
                new TestProperty(new TestRefValue("TestCollection"), "list")));

        // and the collection metadata
        importMiddleEvents.addValidator(new ComponentMetadataValidator(new ReferenceCollection("TestCollection",
                TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL,
                ServiceReferenceMetadata.INITIALIZATION_LAZY, null,
                new String[] { "dependsleaf1", "dependsleaf2" },
                new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind")},
                RefListMetadata.USE_SERVICE_OBJECT)));

        // this validates the ModuleContext.getComponent() result
        importMiddleEvents.addValidator(new ReferenceListValidator("TestCollection"));
        importMiddleEvents.addValidator(new ComponentNamePresenceValidator("TestCollection"));
        importMiddleEvents.addValidator(new GetReferencedServicesMetadataValidator("TestCollection"));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importMiddleEvents.addAssertion("ReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // We should get these at the end
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

        importStopEvents.addAssertion("dependsleaf1", AssertionService.COMPONENT_DESTROY_METHOD);
        importStopEvents.addAssertion("dependsleaf2", AssertionService.COMPONENT_DESTROY_METHOD);
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.COMPONENT_DESTROY_METHOD));
        importStopEvents.addEvent(new ComponentAssertion("ReferenceChecker", AssertionService.COMPONENT_DESTROY_METHOD));

        controller.run();
    }


    /**
     * Import a list of ServiceReferences and validate against an expected set.
     */
    public void testStaticListCollectionReferenceImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/static_reference_list_ref_import.jar",
                getWebServer()+"www/managed_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // The collection should be initialized with two services.  There should not
        // be any bind or unbind events broadcast for these.
        Hashtable propsA = new Hashtable();
        propsA.put("service.interface.name", TestServiceOne.class.getName());
        propsA.put("service.listener.type", "interface");
        propsA.put("test.service.name", "ServiceOneA");

        Hashtable propsC = new Hashtable();
        propsC.put("service.interface.name", TestServiceOne.class.getName());
        propsC.put("service.listener.type", "interface");
        propsC.put("test.service.name", "ServiceOneC");

        // We should get one of these for each event.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // and the collection metadata...this is a service reference member type
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferenceCollection("TestCollection",
                TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL,
                ServiceReferenceMetadata.INITIALIZATION_EAGER, null, null,
                new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind")},
                RefListMetadata.USE_SERVICE_REFERENCE)));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // and the listener calls on shutdown
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

        controller.run();
    }

    /**
     * Import a list of services and validate against an expected set.
     * This will also unregister the services, validate the collection
     * has been emptied, then register and check again.
     */
    public void testListCollectionImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/null_reference_list_import.jar",
                getWebServer()+"www/managed_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // create a property bundle set for each of the services we expect to see
        // bind and unbind
        Hashtable propsA = new Hashtable();
        propsA.put("service.interface.name", TestServiceOne.class.getName());
        propsA.put("service.listener.type", "interface");
        propsA.put("test.service.name", "ServiceOneA");

        Hashtable propsB = new Hashtable();
        propsB.put("service.interface.name", TestServiceOne.class.getName());
        propsB.put("service.listener.type", "interface");
        propsB.put("test.service.name", "ServiceOneB");

        Hashtable propsC = new Hashtable();
        propsC.put("service.interface.name", TestServiceOne.class.getName());
        propsC.put("service.listener.type", "interface");
        propsC.put("test.service.name", "ServiceOneC");

        // we get an initial set of bind events for the starting services
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // the first step taken in the test is complete the registration of the
        // rest of the managed services.  This will result in an extra bind event
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsB));

        // next it unregisters everything.  We should see an unbind for each service that
        // goes away
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsB));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

        // validate the reference list when used as a property.
        importStartEvents.addValidator(new PropertyMetadataValidator("NullReferenceChecker",
                new TestProperty(new TestComponentValue(
                new ReferenceCollection(null,
                TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL,
                ServiceReferenceMetadata.INITIALIZATION_LAZY, null, null,
                new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind")},
                RefListMetadata.USE_SERVICE_OBJECT)), "list")));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // all services are unregistered at this point, so we should not see any listener calls
        // at shutdown.  We can get away with a generic event here, since an UNBIND for any of the services
        // is an error.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND));

        controller.run();
    }

    /**
     * Import a list of ServiceReferences and validate against an expected set.
     * This will also unregister the services, validate the collection
     * has been emptied, then register and check again.
     */
    public void testListCollectionReferenceImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/null_reference_list_ref_import.jar",
                getWebServer()+"www/managed_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // create a property bundle set for each of the services we expect to see
        // bind and unbind
        Hashtable propsA = new Hashtable();
        propsA.put("service.interface.name", TestServiceOne.class.getName());
        propsA.put("service.listener.type", "interface");
        propsA.put("test.service.name", "ServiceOneA");

        Hashtable propsB = new Hashtable();
        propsB.put("service.interface.name", TestServiceOne.class.getName());
        propsB.put("service.listener.type", "interface");
        propsB.put("test.service.name", "ServiceOneB");

        Hashtable propsC = new Hashtable();
        propsC.put("service.interface.name", TestServiceOne.class.getName());
        propsC.put("service.listener.type", "interface");
        propsC.put("test.service.name", "ServiceOneC");

        // we get an initial set of bind events for the starting services
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // the first step taken in the test is complete the registration of the
        // rest of the managed services.  This will result in an extra bind event
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsB));

        // next it unregisters everything.  We should see an unbind for each service that
        // goes away
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsB));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

        // validate the reference list when used as a property.
        importStartEvents.addValidator(new PropertyMetadataValidator("NullReferenceChecker",
                new TestProperty(new TestComponentValue(
                new ReferenceCollection(null,
                TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL,
                ServiceReferenceMetadata.INITIALIZATION_LAZY, null, null,
                new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind")},
                RefListMetadata.USE_SERVICE_REFERENCE)), "list")));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // all services are unregistered at this point, so we should not see any listener calls
        // at shutdown.  We can get away with a generic event here, since an UNBIND for any of the services
        // is an error.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND));

        controller.run();
    }

    /**
     * Similar to the basic list import test, but the listener is the same
     * component the reference list is injected into, thus creating a
     * circular reference situation.
     */
    public void testCircularListCollectionImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/circular_reference_list_import.jar",
                getWebServer()+"www/managed_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // create a property bundle set for each of the services we expect to see
        // bind and unbind
        Hashtable propsA = new Hashtable();
        propsA.put("service.interface.name", TestServiceOne.class.getName());
        propsA.put("service.listener.type", "interface");
        propsA.put("test.service.name", "ServiceOneA");

        Hashtable propsB = new Hashtable();
        propsB.put("service.interface.name", TestServiceOne.class.getName());
        propsB.put("service.listener.type", "interface");
        propsB.put("test.service.name", "ServiceOneB");

        Hashtable propsC = new Hashtable();
        propsC.put("service.interface.name", TestServiceOne.class.getName());
        propsC.put("service.listener.type", "interface");
        propsC.put("test.service.name", "ServiceOneC");

        // we get an initial set of bind events for the starting services
        importStartEvents.addEvent(new ComponentAssertion("NullReferenceChecker", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("NullReferenceChecker", AssertionService.SERVICE_BIND, propsC));

        // the first step taken in the test is complete the registration of the
        // rest of the managed services.  This will result in an extra bind event
        importStartEvents.addEvent(new ComponentAssertion("NullReferenceChecker", AssertionService.SERVICE_BIND, propsB));

        // next it unregisters everything.  We should see an unbind for each service that
        // goes away
        importStartEvents.addEvent(new ComponentAssertion("NullReferenceChecker", AssertionService.SERVICE_UNBIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("NullReferenceChecker", AssertionService.SERVICE_UNBIND, propsB));
        importStartEvents.addEvent(new ComponentAssertion("NullReferenceChecker", AssertionService.SERVICE_UNBIND, propsC));

        // validate the reference list when used as a property.  This one is similar to previous
        // ones, but is special because of the circular reference relationship with the listener
        importStartEvents.addValidator(new PropertyMetadataValidator("NullReferenceChecker",
                new TestProperty(new TestComponentValue(
                new ReferenceCollection(null,
                TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL,
                ServiceReferenceMetadata.INITIALIZATION_LAZY, null, null,
                new BindingListener[] { new BindingListener("NullReferenceChecker", "bind", "unbind")},
                RefListMetadata.USE_SERVICE_REFERENCE)), "list")));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // all services are unregistered at this point, so we should not see any listener calls
        // at shutdown.  We can get away with a generic event here, since an UNBIND for any of the services
        // is an error.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND));

        controller.run();
    }


    /**
     * Import a list of services and validate against an expected set.
     * The initial list will be empty, with optional availability.  Then it
     * will register all of the services and validate the result against the expected
     * service set.
     */
    public void testEmptyListCollectionImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/null_reference_list_import.jar",
                getWebServer()+"www/managed_null_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // create a property bundle set for each of the services we expect to see
        // bind and unbind
        Hashtable propsA = new Hashtable();
        propsA.put("service.interface.name", TestServiceOne.class.getName());
        propsA.put("service.listener.type", "interface");
        propsA.put("test.service.name", "ServiceOneA");

        Hashtable propsB = new Hashtable();
        propsB.put("service.interface.name", TestServiceOne.class.getName());
        propsB.put("service.listener.type", "interface");
        propsB.put("test.service.name", "ServiceOneB");

        Hashtable propsC = new Hashtable();
        propsC.put("service.interface.name", TestServiceOne.class.getName());
        propsC.put("service.listener.type", "interface");
        propsC.put("test.service.name", "ServiceOneC");

        // we start out with an empty list, register everything, then unregister everything.
        // we should see a pair of events for each service.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsB));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // next it unregisters everything.  We should see an unbind for each service that
        // goes away
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsB));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // all services are unregistered at this point, so we should not see any listener calls
        // at shutdown.  We can get away with a generic event here, since an UNBIND for any of the services
        // is an error.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND));

        controller.run();
    }


    /**
     * Import a list of services and validate against an expected set.
     * The initial list will be empty, with optional availability.  Then it
     * will register all of the services and validate the result against the expected
     * service set.  This is the same as testEmptyListCollectionImport(), but the
     * availability is specified using default-availability.
     */
    public void testEmptyListCollectionDefaultImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/null_reference_list_default_import.jar",
                getWebServer()+"www/managed_null_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // create a property bundle set for each of the services we expect to see
        // bind and unbind
        Hashtable propsA = new Hashtable();
        propsA.put("service.interface.name", TestServiceOne.class.getName());
        propsA.put("service.listener.type", "interface");
        propsA.put("test.service.name", "ServiceOneA");

        Hashtable propsB = new Hashtable();
        propsB.put("service.interface.name", TestServiceOne.class.getName());
        propsB.put("service.listener.type", "interface");
        propsB.put("test.service.name", "ServiceOneB");

        Hashtable propsC = new Hashtable();
        propsC.put("service.interface.name", TestServiceOne.class.getName());
        propsC.put("service.listener.type", "interface");
        propsC.put("test.service.name", "ServiceOneC");

        // we start out with an empty list, register everything, then unregister everything.
        // we should see a pair of events for each service.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsB));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // next it unregisters everything.  We should see an unbind for each service that
        // goes away
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsB));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // all services are unregistered at this point, so we should not see any listener calls
        // at shutdown.  We can get away with a generic event here, since an UNBIND for any of the services
        // is an error.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND));

        controller.run();
    }


    /**
     * Import a list of services and validate against an expected set.
     * The initial list will be empty, with optional availability.  Then it
     * will register all of the services and validate the result against the expected
     * service set.  This is the same as testEmptyListCollectionImport(), but the
     * availability is specified using default-availability.
     *
     * This is identical to the test above, but the attached listener is provided
     * by an imported service.
     */
    public void testEmptyListCollectionServiceListener() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/list_reference_listener_import.jar",
                getWebServer()+"www/managed_null_service_export.jar",
                getWebServer()+"www/reference_listener_export.jar");


        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // create a property bundle set for each of the services we expect to see
        // bind and unbind
        Hashtable propsA = new Hashtable();
        propsA.put("service.interface.name", TestServiceOne.class.getName());
        propsA.put("service.listener.type", "interface");
        propsA.put("test.service.name", "ServiceOneA");

        Hashtable propsB = new Hashtable();
        propsB.put("service.interface.name", TestServiceOne.class.getName());
        propsB.put("service.listener.type", "interface");
        propsB.put("test.service.name", "ServiceOneB");

        Hashtable propsC = new Hashtable();
        propsC.put("service.interface.name", TestServiceOne.class.getName());
        propsC.put("service.listener.type", "interface");
        propsC.put("test.service.name", "ServiceOneC");

        // we start out with an empty list, register everything, then unregister everything.
        // we should see a pair of events for each service.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsB));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // next it unregisters everything.  We should see an unbind for each service that
        // goes away
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsB));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // all services are unregistered at this point, so we should not see any listener calls
        // at shutdown.  We can get away with a generic event here, since an UNBIND for any of the services
        // is an error.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND));

        controller.run();
    }


    /**
     * Import a list of ServiceReferences and validate against an expected set.
     * The initial list will be empty, with optional availability.  Then it
     * will register all of the services and validate the result against the expected
     * service set.
     */
    public void testEmptyListCollectionReferenceImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/null_reference_list_ref_import.jar",
                getWebServer()+"www/managed_null_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // create a property bundle set for each of the services we expect to see
        // bind and unbind
        Hashtable propsA = new Hashtable();
        propsA.put("service.interface.name", TestServiceOne.class.getName());
        propsA.put("service.listener.type", "interface");
        propsA.put("test.service.name", "ServiceOneA");

        Hashtable propsB = new Hashtable();
        propsB.put("service.interface.name", TestServiceOne.class.getName());
        propsB.put("service.listener.type", "interface");
        propsB.put("test.service.name", "ServiceOneB");

        Hashtable propsC = new Hashtable();
        propsC.put("service.interface.name", TestServiceOne.class.getName());
        propsC.put("service.listener.type", "interface");
        propsC.put("test.service.name", "ServiceOneC");

        // we start out with an empty list, register everything, then unregister everything.
        // we should see a pair of events for each service.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsB));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // next it unregisters everything.  We should see an unbind for each service that
        // goes away
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsB));
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // all services are unregistered at this point, so we should not see any listener calls
        // at shutdown.  We can get away with a generic event here, since an UNBIND for any of the services
        // is an error.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND));

        controller.run();
    }


    /**
     * This tests the state of references in the reference collections during
     * bind/unbind listener calls for <ref-lists>
     */
    public void testBindUnbindListImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/collection_bind_unbind_list_state.jar",
                getWebServer()+"www/managed_one_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // We're only seeing one service, so we don't need the property bundle.
        // this is the initial bind event
        importStartEvents.addAssertion("BindUnbindSetChecker", AssertionService.SERVICE_BIND);

        // ok, we'll unbind, then bind, then unbind again.  We should see two events, one from
        // each checker component
        // rest of the managed services.  This will result in an extra bind event
        importStartEvents.addAssertion("BindUnbindListChecker", AssertionService.SERVICE_UNBIND);

        importStartEvents.addAssertion("BindUnbindListChecker", AssertionService.SERVICE_BIND);

        importStartEvents.addAssertion("BindUnbindListChecker", AssertionService.SERVICE_UNBIND);

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("BindUnbindListChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // all services are unregistered at this point, so we should not see any listener calls
        // at shutdown.  We can get away with a generic event here, since an UNBIND for any of the services
        // is an error.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND));

        controller.run();
    }


    /*
     * This tests the behavior of unregistered manadatory services and
     */
    public void testUnregisteredListServiceDependency() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/unregistered_list_dependency.jar");
        // this is added as a setup bundle because we want to ensure we don't see any
        // waiting events.  This gets the first bundle established before we initialize.
        controller.addSetupBundle(getWebServer()+"www/managed_one_service_export.jar");

        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // metadata issues have been well tested elsewhere.  We're going to focus on the service dynamics.

        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        Hashtable props1 = new Hashtable();
        props1.put("service.interface.name", TestServiceOne.class.getName());
        props1.put("service.listener.type", "interface");
        props1.put("test.service.name", "ServiceOneA");

        // this is the initial bind operation at BlueprintContainer creation
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // this is followed by an UNBIND operation when the service goes away
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        // And then rebound again by the driver code.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("UnregisteredDependencyChecker", AssertionService.COMPONENT_INIT_METHOD);

        // there should be no wait event with this
        importStartEvents.addFailureEvent(new BlueprintAdminEvent("WAITING"));
        importStartEvents.addFailureEvent(new BlueprintContainerEvent("WAITING"));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // the final UNBIND operation
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        controller.run();
    }


    /**
     * Test the iterator and proxy semantics for a <ref-list>
     */
    public void testRefListIterator() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ref_list_iterator.jar");

        // this installs and starts the bundle containing the reference services first to
        // ensure the timing of the initial service listener calls.
        controller.addSetupBundle(getWebServer()+"www/managed_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        controller.run();
    }
}
