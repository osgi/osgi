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

import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;
import org.osgi.service.blueprint.reflect.RefCollectionMetadata;

import org.osgi.test.cases.blueprint.framework.BindingListener;
import org.osgi.test.cases.blueprint.framework.BlueprintEvent;
import org.osgi.test.cases.blueprint.framework.ComponentAssertion;
import org.osgi.test.cases.blueprint.framework.ComponentMetadataValidator;
import org.osgi.test.cases.blueprint.framework.ComponentNamePresenceValidator;
import org.osgi.test.cases.blueprint.framework.EventSet;
import org.osgi.test.cases.blueprint.framework.GetReferencedServicesMetadataValidator;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.PropertyMetadataValidator;
import org.osgi.test.cases.blueprint.framework.ReferenceCollection;
import org.osgi.test.cases.blueprint.framework.ReferenceListValidator;
import org.osgi.test.cases.blueprint.framework.ReferenceSetValidator;
import org.osgi.test.cases.blueprint.framework.ReferencedService;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.cases.blueprint.framework.TestComponentValue;
import org.osgi.test.cases.blueprint.framework.TestProperty;
import org.osgi.test.cases.blueprint.framework.TestRefValue;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.ServiceManager;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
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
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/static_reference_list_import.jar");

        // this installs and starts the bundle containing the reference services first to
        // ensure the timing of the initial service listener calls.
        controller.addSetupBundle(getWebServer()+"www/managed_service_export.jar");

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

        // We should not get these, so consider them failures.
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // validate the metadata for the imported service manager, since it is directly used
        importStartEvents.addValidator(new PropertyMetadataValidator("ReferenceChecker",
            new TestProperty(new TestComponentValue(
            new ReferencedService(null, ServiceManager.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY,
            null, null, ReferencedService.DEFAULT_TIMEOUT)), "serviceManager")));

        // and one for the reference to the service, which should just be a component reference
        importStartEvents.addValidator(new PropertyMetadataValidator("ReferenceChecker",
            new TestProperty(new TestRefValue("TestCollection"), "list")));

        // and the collection metadata
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferenceCollection("TestCollection",
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL, null,
            new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind") }, List.class, null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)));

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

        // We should not get these, so consider them failures.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

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

        // We should not get these, so consider them failures.
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // and the collection metadata...this is a service reference member type
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferenceCollection("TestCollection",
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL, null,
            new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind") }, List.class, null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // We should not get these, so consider them failures.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

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
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL, null,
            new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind") }, List.class, null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "list")));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

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
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL, null,
            new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind") }, List.class, null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "list")));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

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
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL, null,
            new BindingListener[] { new BindingListener("NullReferenceChecker", "bind", "unbind") }, List.class, null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "list")));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

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

        controller.run();
    }


	/**
	 * Import a set of services and validate against an expected set.
	 */
	public void testStaticSetCollectionImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/static_reference_set_import.jar",
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

        // We should not get these, so consider them failures.
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // and one for the reference to the service, which should just be a component reference
        importStartEvents.addValidator(new PropertyMetadataValidator("ReferenceChecker",
            new TestProperty(new TestRefValue("TestCollection"), "set")));

        // and the collection metadata
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferenceCollection("TestCollection",
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL, null,
            new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind") }, Set.class, null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)));

        // this validates the ModuleContext.getComponent() result
        importStartEvents.addValidator(new ReferenceSetValidator("TestCollection"));
        importStartEvents.addValidator(new ComponentNamePresenceValidator("TestCollection"));
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("TestCollection"));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // We should not get these, so consider them failures.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

        controller.run();
    }


	/**
	 * Import a set of ServiceReferences and validate against an expected set.
	 */
	public void testStaticSetCollectionReferenceImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/static_reference_set_ref_import.jar",
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

        // We should not get these, so consider them failures.
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsA));
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsC));

        // and the collection metadata...this is a service reference member type
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferenceCollection("TestCollection",
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL, null,
            new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind") }, Set.class, null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("ReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        // all of our validation here is on the importing side
        EventSet importStopEvents = controller.getStopEvents(0);

        // We should not get these, so consider them failures.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsA));
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsC));

        controller.run();
    }


	/**
	 * Import a Set of services and validate against an expected set.
     * This will also unregister the services, validate the collection
     * has been emptied, then register and check again.
	 */
	public void testSetCollectionImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/null_reference_set_import.jar",
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
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL, null,
            new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind") }, Set.class, null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "set")));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        controller.run();
    }


	/**
	 * Import a Set of ServiceReferences and validate against an expected set.
     * This will also unregister the services, validate the collection
     * has been emptied, then register and check again.
	 */
	public void testSetCollectionReferenceImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/null_reference_set_ref_import.jar",
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
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL, null,
            new BindingListener[] { new BindingListener("ServiceOneListener", "bind", "unbind") }, Set.class, null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "set")));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        controller.run();
    }


	/**
	 * Import a Set of services with a circular reference involved with the collection listener.
	 */
	public void testCircularSetCollectionImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/circular_reference_set_import.jar",
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

        // the first step taken in the test is to complete the registration of the
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
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_OPTIONAL, null,
            new BindingListener[] { new BindingListener("NullReferenceChecker", "bind", "unbind") }, Set.class, null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "set")));

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("NullReferenceChecker", AssertionService.COMPONENT_INIT_METHOD);

        controller.run();
    }


	/**
	 * Import a Set of services and validate against an expected set.
     * The initial list will be empty, with optional availability.  Then it
     * will register all of the services and validate the result against the expected
     * service set.
	 */
	public void testEmptySetCollectionImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/null_reference_set_import.jar",
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

        controller.run();
    }


	/**
	 * Import a Set of services and validate against an expected set.
     * The initial list will be empty, with optional availability.  Then it
     * will register all of the services and validate the result against the expected
     * service set.  This is the same as testEmptySetCollectionImport(), but the
     * availabilty option is specified using default-availability.
	 */
	public void testEmptySetCollectionDefaultImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/null_reference_set_default_import.jar",
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

        controller.run();
    }


	/**
	 * Import a Set of ServiceReferences and validate against an expected set.
     * The initial list will be empty, with optional availability.  Then it
     * will register all of the services and validate the result against the expected
     * service set.
	 */
	public void testEmptySetCollectionReferenceImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/null_reference_set_ref_import.jar",
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

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefSetNameSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_set_name_sort.jar");

        // this installs and starts the bundle containing the reference services first to
        // ensure the timing of the initial service listener calls.
        controller.addSetupBundle(getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        // validate the metadata for the different collection types/comparator combinations
        importStartEvents.addValidator(new PropertyMetadataValidator("ComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, Set.class,
            new TestRefValue("NameComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "set")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefSetInvertedNameSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_set_inverted_name_sort.jar");

        // this installs and starts the bundle containing the reference services first to
        // ensure the timing of the initial service listener calls.
        controller.addSetupBundle(getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("InvertedComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        importStartEvents.addValidator(new PropertyMetadataValidator("InvertedComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, Set.class,
            new TestRefValue("InvertedNameComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "set")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefSetReferenceComparatorSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_set_reference_comparator.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ServiceReferenceComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceReferenceComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, Set.class,
            new TestRefValue("ServiceReferenceComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE_REFERENCE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "set")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefSetServiceOrderSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_set_service_order.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ServiceOrderChecker", AssertionService.COMPONENT_INIT_METHOD);


        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceOrderChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, Set.class,
            null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "set")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefSetServiceReferenceOrderSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_set_servicereference_order.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ServiceReferenceOrderChecker", AssertionService.COMPONENT_INIT_METHOD);

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceReferenceOrderChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, Set.class,
            null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE_REFERENCE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "set")));
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefSetNameReferenceSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_set_name_sort_reference.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        // validate the metadata for the different collection types/comparator combinations
        importStartEvents.addValidator(new PropertyMetadataValidator("ComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, Set.class,
            new TestRefValue("NameComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "set")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefSetInvertedNameReferenceSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_set_inverted_name_sort_reference.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("InvertedComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        importStartEvents.addValidator(new PropertyMetadataValidator("InvertedComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, Set.class,
            new TestRefValue("InvertedNameComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "set")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefSetReferenceComparatorReferenceSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_set_reference_comparator_reference.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ServiceReferenceComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceReferenceComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, Set.class,
            new TestRefValue("ServiceReferenceComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE_REFERENCE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "set")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefSetServiceOrderReferenceSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_set_service_order_reference.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ServiceOrderChecker", AssertionService.COMPONENT_INIT_METHOD);


        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceOrderChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, Set.class,
            null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "set")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefSetServiceReferenceOrderReferenceSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_set_servicereference_order_reference.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ServiceReferenceOrderChecker", AssertionService.COMPONENT_INIT_METHOD);

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceReferenceOrderChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, Set.class,
            null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE_REFERENCE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "set")));
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefListNameSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_list_name_sort.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        // validate the metadata for the different collection types/comparator combinations
        importStartEvents.addValidator(new PropertyMetadataValidator("ComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, List.class,
            new TestRefValue("NameComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "list")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.  The
     * sorting comparator is imported as a service.
	 */
	public void testRefListNameSortService() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_list_name_sort.jar",
            getWebServer()+"www/sorting_service_export.jar",
            getWebServer()+"www/name_comparator_service.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        // validate the metadata for the different collection types/comparator combinations
        importStartEvents.addValidator(new PropertyMetadataValidator("ComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, List.class,
            new TestRefValue("NameComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "list")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefListInvertedNameSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_list_inverted_name_sort.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("InvertedComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        importStartEvents.addValidator(new PropertyMetadataValidator("InvertedComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, List.class,
            new TestRefValue("InvertedNameComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "list")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefListReferenceComparatorSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_list_reference_comparator.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ServiceReferenceComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceReferenceComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, List.class,
            new TestRefValue("ServiceReferenceComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE_REFERENCE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "list")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefListServiceOrderSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_list_service_order.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ServiceOrderChecker", AssertionService.COMPONENT_INIT_METHOD);


        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceOrderChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, List.class,
            null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "list")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefListServiceReferenceOrderSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_list_servicereference_order.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ServiceReferenceOrderChecker", AssertionService.COMPONENT_INIT_METHOD);

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceReferenceOrderChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, List.class,
            null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE_REFERENCE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)), "list")));
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefListNameReferenceSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_list_name_sort_reference.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        // validate the metadata for the different collection types/comparator combinations
        importStartEvents.addValidator(new PropertyMetadataValidator("ComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, List.class,
            new TestRefValue("NameComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "list")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefListInvertedNameReferenceSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_list_inverted_name_sort_reference.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("InvertedComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        importStartEvents.addValidator(new PropertyMetadataValidator("InvertedComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, List.class,
            new TestRefValue("InvertedNameComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "list")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefListReferenceComparatorReferenceSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_list_reference_comparator_reference.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ServiceReferenceComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceReferenceComparatorChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, List.class,
            new TestRefValue("ServiceReferenceComparator"),
            RefCollectionMetadata.ORDERING_BASIS_SERVICE_REFERENCE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "list")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefListServiceOrderReferenceSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_list_service_order_reference.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ServiceOrderChecker", AssertionService.COMPONENT_INIT_METHOD);


        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceOrderChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, List.class,
            null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "list")));

        controller.run();
    }


	/**
	 * A number of sort/Iterator tests, including some service dynamics elements.
	 */
	public void testRefListServiceReferenceOrderReferenceSort() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ref_list_servicereference_order_reference.jar",
            getWebServer()+"www/sorting_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ServiceReferenceOrderChecker", AssertionService.COMPONENT_INIT_METHOD);

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceReferenceOrderChecker",
            new TestProperty(new TestComponentValue(
            new ReferenceCollection(null,
            TestServiceOne.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, List.class,
            null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE_REFERENCE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_REFERENCE)), "list")));
    }


	/**
	 * Tests set semantics of ref-set
	 */
	public void testSetSemantics() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/set_semantics_import.jar",
            getWebServer()+"www/set_semantics_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("SetChecker", AssertionService.COMPONENT_INIT_METHOD);

        controller.run();
    }


	/**
	 * A test for ref collection import using multple specified interfaces.
	 */
	public void testReferenceCollectionMultipleInterface() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/reference_collection_multiple_interface_import.jar",
            getWebServer()+"www/sorting_multiple_interface_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        controller.run();
    }


	/**
	 * A test for ServiceReference ref collection import using multple specified interfaces.
	 */
	public void testServiceReferenceCollectionMultipleInterface() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/reference_collection_multiple_interface_ref_import.jar",
            getWebServer()+"www/sorting_multiple_interface_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // all of the real tests are performed in the instantiated components, so
        // we're just looking for the completion methods.  Any assertion failures
        // will terminate the test.
        importStartEvents.addAssertion("ComparatorChecker", AssertionService.COMPONENT_INIT_METHOD);

        controller.run();
    }


	/**
	 * This tests the state of references in the reference collections during
     * bind/unbind listener calls for both Set and List.
	 */
	public void testBindUnbindSetImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/collection_bind_unbind_set_state.jar",
            getWebServer()+"www/managed_one_service_export.jar");

        // all of our validation here is on the importing side
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // We're only seeing one service, so we don't need the property bundle.

        // ok, we'll unbind, then bind, then unbind again.  We should see two events, one from
        // each checker component
        // rest of the managed services.  This will result in an extra bind event
        importStartEvents.addAssertion("BindUnbindSetChecker", AssertionService.SERVICE_UNBIND);

        importStartEvents.addAssertion("BindUnbindSetChecker", AssertionService.SERVICE_BIND);

        importStartEvents.addAssertion("BindUnbindSetChecker", AssertionService.SERVICE_UNBIND);

        // this event signals completion of all of the checking work.  If there
        // have been any errors, these get signalled as assertion failures and will
        // fail the test.
        importStartEvents.addAssertion("BindUnbindSetChecker", AssertionService.COMPONENT_INIT_METHOD);
        controller.run();
    }


	/**
	 * This tests the state of references in the reference collections during
     * bind/unbind listener calls for both Set and List.
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
        // this is the initial bind operation at ModuleContext creation
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // this is followed by an UNBIND operation when the service goes away
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        // And then rebound again by the driver code.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("UnregisteredDependencyChecker", AssertionService.COMPONENT_INIT_METHOD);


        // there should be no wait event with this
        importStartEvents.addFailureEvent(new BlueprintEvent("WAITING"));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // the final UNBIND operation
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        controller.run();
    }


	/*
	 * This tests the behavior of unregistered manadatory services and
	 */
	public void testUnregisteredSetServiceDependency() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/unregistered_set_dependency.jar");
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
        // this is the initial bind operation at ModuleContext creation
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // this is followed by an UNBIND operation when the service goes away
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        // And then rebound again by the driver code.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("UnregisteredDependencyChecker", AssertionService.COMPONENT_INIT_METHOD);


        // there should be no wait event with this
        importStartEvents.addFailureEvent(new BlueprintEvent("WAITING"));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // the final UNBIND operation
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        controller.run();
    }
}
