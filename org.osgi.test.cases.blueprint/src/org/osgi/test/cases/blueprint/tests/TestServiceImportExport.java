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
import java.util.Properties;

import org.osgi.framework.ServiceFactory;

import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.ServiceMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;
import org.osgi.service.blueprint.reflect.ReferenceListMetadata;
import org.osgi.test.cases.blueprint.components.serviceimport.ServiceOneListener;
import org.osgi.test.cases.blueprint.components.serviceimport.ServiceTwoListener;
import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.*;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class tests interactions between service import and export.
 * Since these operations are intertwined, most of the tests
 * involve loading multiple bundles.  One bundle will export services,
 * the other will import them.
 *
 * @version $Revision$
 */
public class TestServiceImportExport extends DefaultTestBundleControl {
    /**
     * Just a simple export/import test.  This will perform fairly full
     * metadata validation on both ends.
     */
    public void testSingleInterfaceExport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_import.jar",
                getWebServer()+"www/ServiceOne_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // the is the component creation
        exportStartEvents.addAssertion("ServiceOne", AssertionService.BEAN_CREATED);
        // validate that the service has been registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne"));
        // this will validate the getComponent() result and check this is also in getComponentNames();
        exportStartEvents.addValidator(new ServiceComponentValidator("ServiceOneService"));
        exportStartEvents.addValidator(new ComponentNamePresenceValidator("ServiceOneService"));
        exportStartEvents.addValidator(new GetExportedServicesMetadataValidator("ServiceOneService"));

        // also validate the metadata for the exported service (also verify the empty case for service properties
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, new MapValueEntry[0], null, null)));
        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // also validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, null, ReferencedService.DEFAULT_TIMEOUT)));
        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);
        // validate that the component is the correct type
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceOne", TestServiceOne.class));
        importStartEvents.addValidator(new ComponentNamePresenceValidator("ServiceOne"));
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceOne"));

        // do some validation of the import metadata
        importStartEvents.addValidator(new ArgumentMetadataValidator("ServiceOneConstructor", new TestArgument[] {
            new StringArgument("ServiceOneConstructor"),
                    new ReferenceArgument("ServiceOne", TestServiceOne.class)
        }));

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceOneProperty", new TestProperty[] {
            new ReferenceProperty("one", "ServiceOne")
        }));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // the is the component creation
        exportStopEvents.addAssertion("ServiceOne", AssertionService.BEAN_DESTROY_METHOD);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, null));

        controller.run();
    }


    /**
     * this is the same imports/exports as above, but this test only checks that there
     * is no GRACE_PERIOD event when the dependencies are satisfied.
     */
    public void testNoGracePeriod() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_import.jar");
        // we add this as a setup bundle to ensure everything is available before we
        // start the importing bundle.
        controller.addSetupBundle(getWebServer()+"www/ServiceOne_export.jar");

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // a grace period event is a failure...that's all we're really testing here.
        importStartEvents.addFailureEvent(new BlueprintContainerEvent("GRACE_PERIOD"));
        controller.run();
    }

    /**
     * Just a simple export/import test, but with the added wrinkle of a depends-on
     * relationship with the <reference> element
     */
    public void testReferenceDependsOn() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_dependson_import.jar",
                getWebServer()+"www/ServiceOne_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // the is the component creation
        exportStartEvents.addAssertion("ServiceOne", AssertionService.BEAN_CREATED);
        // validate that the service has been registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne"));
        // this will validate the getComponent() result and check this is also in getComponentNames();
        exportStartEvents.addValidator(new ServiceComponentValidator("ServiceOneService"));
        exportStartEvents.addValidator(new ComponentNamePresenceValidator("ServiceOneService"));
        exportStartEvents.addValidator(new GetExportedServicesMetadataValidator("ServiceOneService"));

        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, null)));
        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we should see both the construction and INIT of this.  It's difficult to test the ordering,
        // but we should see the events from this lazy bean.
        importStartEvents.addAssertion("dependsleaf2", AssertionService.BEAN_CREATED);
        importStartEvents.addAssertion("dependsleaf2", AssertionService.BEAN_INIT_METHOD);
        importStartEvents.addAssertion("dependsleaf1", AssertionService.BEAN_CREATED);
        importStartEvents.addAssertion("dependsleaf1", AssertionService.BEAN_INIT_METHOD);
        // also validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, new String[] { "dependsleaf2", "dependsleaf1" }, null,
                ReferencedService.DEFAULT_TIMEOUT)));
        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);
        // validate that the component is the correct type
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceOne", TestServiceOne.class));
        importStartEvents.addValidator(new ComponentNamePresenceValidator("ServiceOne"));
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceOne"));

        // do some validation of the import metadata
        importStartEvents.addValidator(new ArgumentMetadataValidator("ServiceOneConstructor", new TestArgument[] {
            new StringArgument("ServiceOneConstructor"),
                    new ReferenceArgument("ServiceOne", TestServiceOne.class)
        }));

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceOneProperty", new TestProperty[] {
            new ReferenceProperty("one", "ServiceOne")
        }));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // the is the component creation
        exportStopEvents.addAssertion("ServiceOne", AssertionService.BEAN_DESTROY_METHOD);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, null));

        EventSet importStopEvents = controller.getStopEvents(0);
        importStopEvents.addAssertion("dependsleaf1", AssertionService.BEAN_DESTROY_METHOD);
        importStopEvents.addAssertion("dependsleaf2", AssertionService.BEAN_DESTROY_METHOD);

        controller.run();
    }

    /**
     * Just a simple export/import test, using prototype scope for the export.
     * This will also test the prototype lifecycle
     */
    public void testSingleInterfacePrototypeExport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_import.jar",
                getWebServer()+"www/ServiceOne_prototype_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // the is the component creation...we should see two of those.  One from the importing
        // bundle, and a second when we request the service from this bundle.
        exportStartEvents.addAssertion("ServiceOne", AssertionService.BEAN_CREATED);
        // validate that the service has been registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne"));
        // this will validate the getComponent() result and check this is also in getComponentNames();
        exportStartEvents.addValidator(new ServiceComponentValidator("ServiceOneService"));
        exportStartEvents.addValidator(new ComponentNamePresenceValidator("ServiceOneService"));
        exportStartEvents.addValidator(new GetExportedServicesMetadataValidator("ServiceOneService"));

        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, null)));
        // we should see a service event here indicating this was registered.  When this occurs, we'll
        // make a request from this bundle context.  This will trigger a second prototype to be created, and
        // also force the prototype bean to be immediately destroyed.
        exportStartEvents.addEvent(new ServiceTestEvent("REGISTERED", new Class[] {TestServiceOne.class}, null,
                new ServiceRequestInitiator(getContext(), TestServiceOne.class, null)));

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // also validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, null, ReferencedService.DEFAULT_TIMEOUT)));
        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);
        // validate that the component is the correct type
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceOne", TestServiceOne.class));
        importStartEvents.addValidator(new ComponentNamePresenceValidator("ServiceOne"));
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceOne"));

        // do some validation of the import metadata
        importStartEvents.addValidator(new ArgumentMetadataValidator("ServiceOneConstructor", new TestArgument[] {
            new StringArgument("ServiceOneConstructor"),
                    new ReferenceArgument("ServiceOne", TestServiceOne.class)
        }));

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceOneProperty", new TestProperty[] {
            new ReferenceProperty("one", "ServiceOne")
        }));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, null));

        controller.run();
    }


    /**
     * This is referencing a single service that is qualified by component name.
     * There will be a second service registred for the same interface, but using a
     * different component.  We should pick up the correct service.
     */
    public void testComponentQualifier() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.  The export jar will export both the target "good" service
        // and secondary "bad" service.  We should resolve to the good service.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_compid_import.jar",
                getWebServer()+"www/ServiceOneBad_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // the is the component creation
        exportStartEvents.addAssertion("ServiceOne", AssertionService.BEAN_CREATED);
        // We should see both of these registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne"));
        // this should be registered under a lot of different interface names, and also has service properties
        Hashtable serviceProps = new Hashtable();
        serviceProps.put("serviceType", "Bad");
        serviceProps.put("autoExport", "All");
        exportStartEvents.addValidator(new ServiceRegistrationValidator(
                new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceTwoSubclass.class, TestServiceAllSubclass.class,
                    TestGoodService.class, TestGoodServiceSubclass.class, TestBadService.class, BaseTestComponent.class,
                    ComponentTestInfo.class, Comparable.class},
                    null, null, serviceProps));
        // also validate the metadata for the exported service
        MapValueEntry []  metaServiceProps = new MapValueEntry[] {
            new MapValueEntry("serviceType", "Bad"),
            new MapValueEntry("autoExport", "All")
        };

        TestComponentValue badServiceBean = new TestComponentValue(
                new BeanComponent(
                        (String)null,
                        TestBadService.class,
                        null,
                        "init",
                        null,
                        new TestArgument[] { new StringArgument("ServiceOne") },
                        null,
 						null,
 						BeanMetadata.ACTIVATION_LAZY,
 						null
                )
        );

        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService[] {
			new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, null),

            new ExportedService("BadServiceService",
                ServiceMetadata.ACTIVATION_EAGER, badServiceBean,
                new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceTwoSubclass.class, TestServiceAllSubclass.class,
                    TestGoodService.class, TestGoodServiceSubclass.class, TestBadService.class},
                ServiceMetadata.AUTO_EXPORT_ALL_CLASSES, 2, metaServiceProps, null, null)}));
        // we should see a service event here indicating this was registered
        Hashtable props2 = new Hashtable();
        props2.put("osgi.service.blueprint.compname", "ServiceOne");
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class, props2);
        // a very complex service event
        exportStartEvents.addServiceEvent("REGISTERED", new Class[] { TestServiceOne.class, TestServiceTwo.class,
                    TestServiceTwoSubclass.class, TestServiceAllSubclass.class, TestGoodService.class,
                    TestGoodServiceSubclass.class, TestBadService.class, BaseTestComponent.class, ComponentTestInfo.class,
                    Comparable.class}, serviceProps);

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // this is used to validate two inline references.
        ReferencedService service = new ReferencedService(null, TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_LAZY,
                "ServiceOne", null, null, null, ReferencedService.DEFAULT_TIMEOUT);

        // also validate the metadata for the imported service.  We're importing the same service twice using
        // an inline <reference>, so this should show up twice in the metadata
        importStartEvents.addValidator(new ReferencedServiceValidator(new ReferencedService[] { service, service }));

        // do some validation of the import metadata of the injected parameter.  Since these are
        // embedded elements, they're reuse the definition above
        importStartEvents.addValidator(new ArgumentMetadataValidator("ServiceOneConstructor", new TestArgument[] {
            new StringArgument("ServiceOneConstructor"),
                    new TestArgument(new TestComponentValue(service), TestServiceOne.class)
        }));

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceOneProperty", new TestProperty[] {
            new TestProperty(new TestComponentValue(service), "one")
        }));

        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // the is the component creation
        exportStopEvents.addAssertion("ServiceOne", AssertionService.BEAN_DESTROY_METHOD);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class, props2);
        exportStopEvents.addServiceEvent("UNREGISTERING", new Class[] { TestServiceOne.class, TestServiceTwo.class,
                    TestServiceTwoSubclass.class, TestServiceAllSubclass.class, TestGoodService.class,
                    TestGoodServiceSubclass.class, TestBadService.class, BaseTestComponent.class, ComponentTestInfo.class,
                    Comparable.class}, serviceProps);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, "(osgi.service.blueprint.compname=ServiceOne)"));
        // and make sure all of the bad ones are unregistered also
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(
                new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceTwoSubclass.class, TestServiceAllSubclass.class,
                    TestGoodService.class, TestGoodServiceSubclass.class, TestBadService.class, BaseTestComponent.class, ComponentTestInfo.class}, null));

        controller.run();
    }


    /**
     * This is testing the using of the ranking qualifier for service matching.  Our good
     * service will have a higher ranking than the "bad" service, so it should be resolved.
     * This is a lot like the component qualifier test, so we'll scale back the amount of
     * validity checking on this one (particuarly the processing of the "bad" service).
     */
    public void testRankingQualifier() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.  The export jar will export both the target "good" service
        // and secondary "bad" service.  We should resolve to the good service.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_ranked_import.jar",
                getWebServer()+"www/ServiceOne_ranked_export.jar");

        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // the service ranking is stored in the service properties, so validate it is what we expect to see
        Hashtable serviceProps = new Hashtable();
        serviceProps.put("service.ranking", new Integer(3));
        // We should see both of these registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne", null, serviceProps));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService[] {
            new ExportedService("GoodServiceService", ServiceMetadata.ACTIVATION_EAGER, "ServiceOne",
                    TestServiceOne.class, ServiceMetadata.AUTO_EXPORT_DISABLED, 3, null, null, null),
            new ExportedService("BadServiceService", ServiceMetadata.ACTIVATION_EAGER, null,
                    TestServiceOne.class, ServiceMetadata.AUTO_EXPORT_ALL_CLASSES, 2, null, null, null)
        }));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class, serviceProps);

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, "(osgi.service.blueprint.compname=ServiceOne)"));

        controller.run();
    }


    /**
     * This is testing the use of a service filter to resolve service references.  Both our
     * bad service and good service export similarly named properties with different values.
     * The service imports use a filter to narrow the service resolution to the good filter.
     */
    public void testServicePropertyQualifier() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.  The export jar will export both the target "good" service
        // and secondary "bad" service.  We should resolve to the good service.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_filtered_import.jar",
                getWebServer()+"www/ServiceOne_property_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // the service ranking is stored in the service properties, so validate it is what we expect to see
        Hashtable serviceProps = new Hashtable();
        serviceProps.put("serviceType", "Good");
        serviceProps.put("autoExport", "Disabled");
        // We should see both of these registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne", null, serviceProps));
        MapValueEntry[] metaServiceProps = new MapValueEntry[] {
            new MapValueEntry("serviceType", "Good"),
            new MapValueEntry("autoExport", "Disabled")
        };
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService [] {
            new ExportedService("GoodServiceService", ServiceMetadata.ACTIVATION_EAGER, "ServiceOne",
                TestServiceOne.class, ServiceMetadata.AUTO_EXPORT_DISABLED, 0, metaServiceProps, null, null),
            new ExportedService("BadServiceService", ServiceMetadata.ACTIVATION_EAGER, null,
                TestServiceOne.class, ServiceMetadata.AUTO_EXPORT_ALL_CLASSES, 2, null, null, null)
        }));


        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class, serviceProps);

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);

        ReferencedService service = new ReferencedService(null, TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_LAZY,
                null, "(serviceType=Good)", null, null, ReferencedService.DEFAULT_TIMEOUT);

        // do some validation of the import metadata of the injected parameter.  Since these are
        // embedded elements, they're reuse the definition above
        importStartEvents.addValidator(new ArgumentMetadataValidator("ServiceOneConstructor", new TestArgument[] {
            new StringArgument("ServiceOneConstructor"),
                    new TestArgument(new TestComponentValue(service), TestServiceOne.class)
        }));


        service = new ReferencedService(null, TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_LAZY,
                null, "(!(serviceType=Bad))", null, null, ReferencedService.DEFAULT_TIMEOUT);

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceOneProperty",
                new TestProperty(new TestComponentValue(service), "one")));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, "(osgi.service.blueprint.compname=ServiceOne)"));

        controller.run();
    }


    /**
     * This tests the ability to export the different
     * non-string property types.
     *
     * @exception Exception
     */
    public void testComplexServiceProperty() throws Exception {
        // we're only interested in the export here and validating the types
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_complex_property_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        // This will validate all of the service properties
        exportStartEvents.addValidator(new ComplexServicePropertyValidator());

        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("GoodServiceService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0,
                new MapValueEntry[] {
                    new MapValueEntry("osgi.service.blueprint.compname", "BAD"),
                    new MapValueEntry("service.ranking", "666", Integer.class),
                    new MapValueEntry("test.case.property.int", "1", Integer.TYPE),
                    new MapValueEntry("test.case.property.short", "2", Short.TYPE),
                    new MapValueEntry("test.case.property.byte", "3", Byte.TYPE),
                    new MapValueEntry("test.case.property.char", "4", Character.TYPE),
                    new MapValueEntry("test.case.property.long", "5", Long.TYPE),
                    new MapValueEntry("test.case.property.boolean", "true", Boolean.TYPE),
                    new MapValueEntry("test.case.property.float", "6.0", Float.TYPE),
                    new MapValueEntry("test.case.property.double", "7.0", Double.TYPE),

                    new MapValueEntry("test.case.property.java.lang.Integer", "11", Integer.class),
                    new MapValueEntry("test.case.property.java.lang.Short", "12", Short.class),
                    new MapValueEntry("test.case.property.java.lang.Byte", "13", Byte.class),
                    new MapValueEntry("test.case.property.java.lang.Character", "a", Character.class),
                    new MapValueEntry("test.case.property.java.lang.Long", "15", Long.class),
                    new MapValueEntry("test.case.property.java.lang.Boolean", "false", Boolean.class),
                    new MapValueEntry("test.case.property.java.lang.Float", "16.0", Float.class),
                    new MapValueEntry("test.case.property.java.lang.Double", "17.0", Double.class),

                    new MapValueEntry("test.case.property.int.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Integer.TYPE, "1") }, Integer.TYPE)),
                    new MapValueEntry("test.case.property.short.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Short.TYPE, "2") }, Short.TYPE)),
                    new MapValueEntry("test.case.property.byte.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Byte.TYPE, "3") }, Byte.TYPE)),
                    new MapValueEntry("test.case.property.char.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Character.TYPE, "4") }, Character.TYPE)),
                    new MapValueEntry("test.case.property.long.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Long.TYPE, "5") }, Long.TYPE)),
                    new MapValueEntry("test.case.property.boolean.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Boolean.TYPE, "true") }, Boolean.TYPE)),
                    new MapValueEntry("test.case.property.float.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Float.TYPE, "6.0") }, Float.TYPE)),
                    new MapValueEntry("test.case.property.double.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Double.TYPE, "7.0") }, Double.TYPE)),

                    new MapValueEntry("test.case.property.java.lang.String.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(String.class, "abc") }, String.class)),
                    new MapValueEntry("test.case.property.java.lang.Integer.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Integer.class, "11") }, Integer.class)),
                    new MapValueEntry("test.case.property.java.lang.Short.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Short.class, "12") }, Short.class)),
                    new MapValueEntry("test.case.property.java.lang.Byte.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Byte.class, "13") }, Byte.class)),
                    new MapValueEntry("test.case.property.java.lang.Character.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Character.class, "a") }, Character.class)),
                    new MapValueEntry("test.case.property.java.lang.Long.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Long.class, "15") }, Long.class)),
                    new MapValueEntry("test.case.property.java.lang.Boolean.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Boolean.class, "false") }, Boolean.class)),
                    new MapValueEntry("test.case.property.java.lang.Float.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Float.class, "16.0") }, Float.class)),
                    new MapValueEntry("test.case.property.java.lang.Double.array",
                        new TestArrayValue(new TestValue[] { new TestStringValue(Double.class, "17.0") }, Double.class)),

                    new MapValueEntry("test.case.property.java.lang.String.Set",
                        new TestSetValue(new TestValue[] { new TestStringValue(String.class, "abc") }, String.class)),
                    new MapValueEntry("test.case.property.java.lang.Integer.Set",
                        new TestSetValue(new TestValue[] { new TestStringValue(Integer.class, "11") }, Integer.class)),
                    new MapValueEntry("test.case.property.java.lang.Short.Set",
                        new TestSetValue(new TestValue[] { new TestStringValue(Short.class, "12") }, Short.class)),
                    new MapValueEntry("test.case.property.java.lang.Byte.Set",
                        new TestSetValue(new TestValue[] { new TestStringValue(Byte.class, "13") }, Byte.class)),
                    new MapValueEntry("test.case.property.java.lang.Character.Set",
                        new TestSetValue(new TestValue[] { new TestStringValue(Character.class, "a") }, Character.class)),
                    new MapValueEntry("test.case.property.java.lang.Long.Set",
                        new TestSetValue(new TestValue[] { new TestStringValue(Long.class, "15") }, Long.class)),
                    new MapValueEntry("test.case.property.java.lang.Boolean.Set",
                        new TestSetValue(new TestValue[] { new TestStringValue(Boolean.class, "false") }, Boolean.class)),
                    new MapValueEntry("test.case.property.java.lang.Float.Set",
                        new TestSetValue(new TestValue[] { new TestStringValue(Float.class, "16.0") }, Float.class)),
                    new MapValueEntry("test.case.property.java.lang.Double.Set",
                        new TestSetValue(new TestValue[] { new TestStringValue(Double.class, "17.0") }, Double.class)),

                    new MapValueEntry("test.case.property.java.lang.String.List",
                        new TestListValue(new TestValue[] { new TestStringValue(String.class, "abc") }, String.class)),
                    new MapValueEntry("test.case.property.java.lang.Integer.List",
                        new TestListValue(new TestValue[] { new TestStringValue(Integer.class, "11") }, Integer.class)),
                    new MapValueEntry("test.case.property.java.lang.Short.List",
                        new TestListValue(new TestValue[] { new TestStringValue(Short.class, "12") }, Short.class)),
                    new MapValueEntry("test.case.property.java.lang.Byte.List",
                        new TestListValue(new TestValue[] { new TestStringValue(Byte.class, "13") }, Byte.class)),
                    new MapValueEntry("test.case.property.java.lang.Character.List",
                        new TestListValue(new TestValue[] { new TestStringValue(Character.class, "a") }, Character.class)),
                    new MapValueEntry("test.case.property.java.lang.Long.List",
                        new TestListValue(new TestValue[] { new TestStringValue(Long.class, "15") }, Long.class)),
                    new MapValueEntry("test.case.property.java.lang.Boolean.List",
                        new TestListValue(new TestValue[] { new TestStringValue(Boolean.class, "false") }, Boolean.class)),
                    new MapValueEntry("test.case.property.java.lang.Float.List",
                        new TestListValue(new TestValue[] { new TestStringValue(Float.class, "16.0") }, Float.class)),
                    new MapValueEntry("test.case.property.java.lang.Double.List",
                        new TestListValue(new TestValue[] { new TestStringValue(Double.class, "17.0") }, Double.class)),
                },
                null, null)));
        // we should see a service event here indicating this was registered

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);

        controller.run();
    }


    /**
     * This tests the use of the "depends-on" attribute to ensure that dependent components are
     * available.
     */
    public void testDependsOnQualifier() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.  The export jar will export both the target "good" service
        // and secondary "bad" service.  We should resolve to the good service.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_import.jar",
                getWebServer()+"www/ServiceOne_dependson_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // We should see both of these registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("GoodServiceService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, new String[] { "Depends1", "Depends2"}, null)));

        // these are lazy-inited components, but we should see them get created
        // because of the depends-on attribute
        exportStartEvents.addAssertion("Depends1", AssertionService.BEAN_CREATED);
        exportStartEvents.addAssertion("Depends2", AssertionService.BEAN_CREATED);

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, "(osgi.service.blueprint.compname=ServiceOne)"));

        controller.run();
    }


    /**
     * This tests the use of multiple interfaces on both the import and export.
     */
    public void testMultipleInterface() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.  The export jar will export both the target "good" service
        // and secondary "bad" service.  We should resolve to the good service.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_multiple_import.jar",
                getWebServer()+"www/ServiceOne_multiple_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // Check the registration of both of these classes.  Note, the validation
        // requests each of these interface individually, but the properties should be the same for all.
        exportStartEvents.addValidator(new ServiceRegistrationValidator(new Class[] { TestServiceOne.class, TestServiceTwo.class}, "ServiceOne"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne",
                new Class[] { TestServiceOne.class, TestServiceTwo.class},
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, null)));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", new Class[] { TestServiceOne.class, TestServiceTwo.class});

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // and two additional components that just request this using a single interface.
        importStartEvents.addAssertion("ServiceOneChecker", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceTwoChecker", AssertionService.SERVICE_SUCCESS);
        // validate each of the imported references
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceOne", TestServiceOne.class));
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceTwo", TestServiceTwo.class));
        // validate this is in the reference export list
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceOne"));
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceTwo"));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", new Class[] { TestServiceOne.class, TestServiceTwo.class});
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(new Class[] { TestServiceOne.class, TestServiceTwo.class},
                "(osgi.service.blueprint.compname=ServiceOne)"));

        controller.run();
    }


    /**
     * This tests the use of an imported registraton listener.
     */
    public void testRegistrationListenerImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.  The export jar will export both the target "good" service
        // and secondary "bad" service.  We should resolve to the good service.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_multiple_import.jar",
                getWebServer()+"www/ServiceOne_service_listener.jar",
                getWebServer()+"www/registration_listener_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // Check the registration of both of these classes.  Note, the validation
        // requests each of these interface individually, but the properties should be the same for all.
        exportStartEvents.addValidator(new ServiceRegistrationValidator(new Class[] { TestServiceOne.class, TestServiceTwo.class}, "ServiceOne"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne",
                new Class[] { TestServiceOne.class, TestServiceTwo.class},
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, null)));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", new Class[] { TestServiceOne.class, TestServiceTwo.class});


        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we inject two properties, each of which does a service test using different interfaces.
        // We expect two of these events.
        // and two additional components that just request this using a single interface.
        importStartEvents.addAssertion("ServiceOneChecker", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceTwoChecker", AssertionService.SERVICE_SUCCESS);
        // validate each of the imported references
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceOne", TestServiceOne.class));
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceTwo", TestServiceTwo.class));
        // validate this is in the reference export list
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceOne"));
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceTwo"));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", new Class[] { TestServiceOne.class, TestServiceTwo.class});
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(new Class[] { TestServiceOne.class, TestServiceTwo.class},
                "(osgi.service.blueprint.compname=ServiceOne)"));

        controller.run();
    }


    /**
     * This tests the use of an imported registraton listener, using inline syntax in this case
     */
    public void testInlineRegistrationListenerImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.  The export jar will export both the target "good" service
        // and secondary "bad" service.  We should resolve to the good service.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_multiple_import.jar",
                getWebServer()+"www/ServiceOne_inline_service_listener.jar",
                getWebServer()+"www/registration_listener_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // Check the registration of both of these classes.  Note, the validation
        // requests each of these interface individually, but the properties should be the same for all.
        exportStartEvents.addValidator(new ServiceRegistrationValidator(new Class[] { TestServiceOne.class, TestServiceTwo.class}, "ServiceOne"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne",
                new Class[] { TestServiceOne.class, TestServiceTwo.class},
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, null)));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", new Class[] { TestServiceOne.class, TestServiceTwo.class});


        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we inject two properties, each of which does a service test using different interfaces.
        // We expect two of these events.
        // and two additional components that just request this using a single interface.
        importStartEvents.addAssertion("ServiceOneChecker", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceTwoChecker", AssertionService.SERVICE_SUCCESS);
        // validate each of the imported references
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceOne", TestServiceOne.class));
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceTwo", TestServiceTwo.class));
        // validate this is in the reference export list
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceOne"));
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceTwo"));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", new Class[] { TestServiceOne.class, TestServiceTwo.class});
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(new Class[] { TestServiceOne.class, TestServiceTwo.class},
                "(osgi.service.blueprint.compname=ServiceOne)"));

        controller.run();
    }


    /**
     * This tests the use of concrete class implementations as the exported interface.
     */
    public void testConcreteInterface() throws Exception {
        // NB:  We're only going to test the export portion of this by using a bundle.
        // A concrete class import is not permitted, so we need to validate these
        // results using just the OSGi APIs.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/Service_concrete_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(0);
        // Check the registration of both of these classes.  Note, the validation
        // requests each of these interface individually, but the properties should be the same for all.
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestGoodService.class, "ServiceOne"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne",
                TestGoodService.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, null)));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestGoodService.class);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(0);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestGoodService.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestGoodService.class,
                "(osgi.service.blueprint.compname=ServiceOne)"));

        controller.run();
    }


    /**
     * This tests the auto export of implemented interfaces.
     */
    public void testAutoInterface() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.  The export jar will export both the target "good" service
        // and secondary "bad" service.  We should resolve to the good service.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/Service_auto_interface_import.jar",
                getWebServer()+"www/Service_auto_interface_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // Verify all of these interfaces are registered as services for the named component.
        // requests each of these interface individually, but the properties should be the same for all.
        exportStartEvents.addValidator(new ServiceRegistrationValidator(
                new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class},
                "ServiceInterfaces"));
        // this is a belt-and-braces check to make sure only the interface classes were published
        exportStartEvents.addValidator(new ServiceUnregistrationValidator(
                new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class},
                null));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceInterfacesService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceInterfaces",
                new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class},
                ServiceMetadata.AUTO_EXPORT_INTERFACES, 0, null, null, null)));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", new Class[] { TestServiceOne.class, TestServiceTwo.class,
                    TestServiceAllSubclass.class, ComponentTestInfo.class});

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // this imports 3 services and checks them, so we should get 3 interface events.
        importStartEvents.addAssertion("ServiceInterfaces", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceInterfaces", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceInterfaces", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", new Class[] { TestServiceOne.class,
                    TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class});
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(
                new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class},
                "(osgi.service.blueprint.compname=ServiceInterfaces)"));

        controller.run();
    }


    /**
     * This tests the auto export of concreate class hierarchy
     */
    public void testAutoHierarchy() throws Exception {
        // NB:  exporting the hiearchy of concrete classes exports classes we can't
        // actually import using a blueprint bundle.  So we'll just test the export
        // via OSGi service registry interactions rather than a blueprint bundle.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/Service_auto_hierarchy_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(0);
        // Verify all of these classes are registered as services for the named component.
        // requests each of these interface individually, but the properties should be the same for all.
        exportStartEvents.addValidator(new ServiceRegistrationValidator(
                new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class},
                "ServiceClasses"));
        // this is a belt-and-braces check to make sure only the hierarchy classes were published
        exportStartEvents.addValidator(new ServiceUnregistrationValidator(
                new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class},
                null));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceClassesService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceClasses",
                new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class},
                ServiceMetadata.AUTO_EXPORT_CLASS_HIERARCHY, 0, null, null, null)));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", new Class[] { TestGoodService.class, TestGoodServiceSubclass.class,
                    BaseTestComponent.class});

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(0);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", new Class[] { TestGoodService.class, TestGoodServiceSubclass.class,
                    BaseTestComponent.class});
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(
                new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class},
                "(osgi.service.blueprint.compname=ServiceClasses)"));

        controller.run();
    }


    /**
     * This tests the importing of concrete classes, which is considered an error
     */
    public void testConcreteClassImport() throws Exception {
        // NB:  exporting the hiearchy of concrete classes exports classes we can't
        // actually import using a blueprint bundle.  So we'll just export thhould
        // still work.  The export jar will export both the target "good" service
        // and secondary "bad" service.  We should resolve to the good service.
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
                getWebServer()+"www/Service_auto_hierarchy_import.jar");
        // add a setup bundle that exports the services we're interested in
        controller.addSetupBundle(getWebServer()+"www/Service_auto_hierarchy_export.jar");

        // this import should fail because we're using concrete classes rather than
        // interfaces on the <reference> tags.  This is a normal error test
        controller.run();
    }


    /**
     * This tests the auto export of all interface and hierarchy classes
     */
    public void testAutoAll() throws Exception {
        // NB:  We're only going to load the jar that imports the interfaces because
        // concrete classes are not permitted for a a blueprint import.  The export of
        // the hierarchy classes will be handled by the validators.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/Service_auto_interface_import.jar",
                getWebServer()+"www/Service_auto_all_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // Verify all of these classes are registered as services for the named component.
        // requests each of these interface individually, but the properties should be the same for all.
        exportStartEvents.addValidator(new ServiceRegistrationValidator(
                new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class,
                    TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class},
                "ServiceAll"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceAllService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceAll",
                new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class,
                    TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class},
                ServiceMetadata.AUTO_EXPORT_ALL_CLASSES, 0, null, null, null)));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED",
                new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class,
                    TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class});

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we only see the service events from the interface imports
        importStartEvents.addAssertion("ServiceInterfaces", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceInterfaces", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceInterfaces", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING",
                new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class,
                    TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class});
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(
                new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class,
                    TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class},
                "(osgi.service.blueprint.compname=ServiceAll)"));

        controller.run();
    }


    /**
     * Test accessing a service using an exported ServiceFactory instance.
     */
    public void testFactoryExport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_property_import.jar",
                getWebServer()+"www/ServiceOne_factory_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);

        // we make this request, but need to associate it with the FACTORY_GET event to impose ordering.
        TestEvent request = new ComponentAssertion("ServiceOneProperty", AssertionService.SERVICE_REQUEST);
        // add this to the list, then create the linked event
        exportStartEvents.addEvent(request);
        // this is the get of the service instance
        TestEvent factoryGet = new ComponentAssertion("ServiceOneFactory", AssertionService.SERVICE_FACTORY_GET);
        // this enforces an event ordering between these two
        factoryGet.addDependency(request);
        exportStartEvents.addEvent(factoryGet);
        // the is the factory component creation
        exportStartEvents.addAssertion("ServiceOneFactory", AssertionService.BEAN_CREATED);
        // this the service instance getting created as a result of the factory getting called
        exportStartEvents.addAssertion("ServiceOneFactory_0", AssertionService.BEAN_CREATED);
        // validate that the service has been registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOneFactory"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneFactoryService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOneFactory", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, null)));
        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);
        // this will be the registration event
        exportStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_REGISTERED));

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // also validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, null, ReferencedService.DEFAULT_TIMEOUT)));
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // this is the unget of the service instance
        exportStopEvents.addAssertion("ServiceOneFactory", AssertionService.SERVICE_FACTORY_UNGET);
        // the is the factory shutdown
        exportStopEvents.addAssertion("ServiceOneFactory", AssertionService.BEAN_DESTROY_METHOD);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, null));

        // and the listener event as well
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNREGISTERED));

        controller.run();
    }


    /**
     * Test accessing a service using a prototype scope exported ServiceFactory instance.
     */
    public void testPrototypeFactoryExport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_property_import.jar",
                getWebServer()+"www/ServiceOne_prototype_factory_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);

        // we make this request, but need to associate it with the FACTORY_GET event to impose ordering.
        TestEvent request = new ComponentAssertion("ServiceOneProperty", AssertionService.SERVICE_REQUEST);
        // add this to the list, then create the linked event
        exportStartEvents.addEvent(request);
        // this is the unget of the service instance
        TestEvent factoryGet = new ComponentAssertion("ServiceOneFactory", AssertionService.SERVICE_FACTORY_GET);
        // this enforces an event ordering between these two
        factoryGet.addDependency(request);
        exportStartEvents.addEvent(factoryGet);
        // the is the factory component creation
        exportStartEvents.addAssertion("ServiceOneFactory", AssertionService.BEAN_CREATED);
        // Even though the factory is prototype scope, only one instance is requested BECAUSE this
        // implements ServiceFactory.  So seeing an additiona creation event here is an error.
        exportStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneFactory", AssertionService.BEAN_CREATED));
        // this the service instance getting created as a result of the factory getting called
        exportStartEvents.addAssertion("ServiceOneFactory_0", AssertionService.BEAN_CREATED);
        // validate that the service has been registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOneFactory"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneFactoryService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOneFactory", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, null)));
        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);
        // this will be the registration event
        exportStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_REGISTERED));

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // also validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, null, ReferencedService.DEFAULT_TIMEOUT)));
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // this is the unget of the service instance
        exportStopEvents.addAssertion("ServiceOneFactory", AssertionService.SERVICE_FACTORY_UNGET);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, null));

        // and the listener event as well
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNREGISTERED));

        controller.run();
    }


    /**
     * Test injecting a ServiceRegistration object into a reference.
     */
    public void testRegistrationInjection() throws Exception {
        // NB:  Only one jar for this test.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/Service_registration_injection.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        // this is the registration injection.
        exportStartEvents.addAssertion("ServiceRegistrationChecker", AssertionService.SERVICE_SUCCESS);
        // validate that the service has been registered
        // this will be run after all of the events have settled down, so this shoulbe
        // reflect the modified properties.
        Hashtable props = new Hashtable();
        props.put("component.name", "ServiceRegistrationChecker");
        props.put("component.version", "1");
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne", null, props));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, null)));
        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);
        // this should be modified, and have the new service properties available.
        exportStartEvents.addServiceEvent("MODIFIED", TestServiceOne.class);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, null));

        controller.run();
    }


    /**
     * Test injecting an inline ServiceRegistration object into a reference.
     */
    public void testInlineRegistrationInjection() throws Exception {
        // NB:  Only one jar for this test.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/inline_service_registration_injection.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        // this is the registration injection.
        exportStartEvents.addAssertion("ServiceRegistrationChecker", AssertionService.SERVICE_SUCCESS);
        // validate that the service has been registered
        // this will be run after all of the events have settled down, so this shoulbe
        // reflect the modified properties.
        Hashtable props = new Hashtable();
        props.put("component.name", "ServiceRegistrationChecker");
        props.put("component.version", "1");
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, null, null, props));
        // also validate the metadata for the exported service.  The inline component is
        // marked as lazy because it is inline
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService(null, ServiceMetadata.ACTIVATION_LAZY,
                new TestComponentValue(new BeanComponent(null, TestGoodServiceSubclass.class, null, null, null,
                        new TestArgument[] { new StringArgument("ServiceOne") },
                        null, null, BeanMetadata.ACTIVATION_LAZY, null)),
                new Class [] { TestServiceOne.class }, ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, null)));
        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);
        // this should be modified, and have the new service properties available.
        exportStartEvents.addServiceEvent("MODIFIED", TestServiceOne.class);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, null));

        controller.run();
    }


    /**
     * Test that the dependency wait event message is sent out and that
     * satisfying the wait condition will allow things to proceed to
     * completion.
     */
    public void testDependencyWait() throws Exception {
        // We're only going to load one jar for this test.  The unstatisfied
        // dependency will be handled by a service that's registered when
        // the WAITING blueprint event is received.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_import.jar");

        // create a ServiceManager instance with a single managed service.  This
        // will not be started initially.
        ServiceManager serviceManager = new ServiceManagerImpl(getContext(),
                new ManagedService("ServiceOne", new TestGoodService("ServiceOne"), TestServiceOne.class, getContext(), null, false));

        // now the importing side.  The key event is the blueprint WAITING event.  We'll use that
        // to trigger the ServiceManager to register the required service
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // look for a blueprint event that can trigger the service manager to register
        // the dependent service
        Properties filterProps = new Properties();
        filterProps.put(org.osgi.framework.Constants.OBJECTCLASS, new String[] { TestServiceOne.class.getName() });

        importStartEvents.addEvent(new BlueprintContainerEvent("GRACE_PERIOD", null, new Properties[] { filterProps }, null));
        importStartEvents.addEvent(new BlueprintAdminEvent("GRACE_PERIOD", null, new Properties[] { filterProps }, new ServiceManagerRegister(serviceManager)));
        // these will be triggered by the satisfied wait
        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        // add a cleanup processor for the exported services.
        exportStopEvents.addTerminator(new ServiceManagerUnregister(serviceManager));

        controller.run();
    }

    /**
     * Just a simple export/import with a registration listener.  This should see
     * the initial service registration and the final service unregistration
     */
    public void testListenerExport() throws Exception {
        // We only do the export and then shut this back down again.  That will
        // cause the events of interests to be fired.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_listener_export.jar");
        // We're really only interesting the listener events, but we'll take a look
        // at the metadata as well
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        Hashtable props = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        props.put("service.interface.name", TestServiceOne.class.getName());
        props.put("service.component.name", "ServiceOneService");
        exportStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_REGISTERED, props));
        // we're expecting some listener metadata on the export.
        TestRegistrationListener[] listeners = new TestRegistrationListener[] {
            new TestRegistrationListener("ServiceOneListener", "registered", "unregistered")
        };
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, listeners)));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNREGISTERED, props));

        controller.run();
    }


    /**
     * A test of multiple signature matching with a registration listener and singleton
     * scope.
     */
    public void testRegistrationListenerSingletonSignature() throws Exception {
        // We only do the export and then shut this back down again.  That will
        // cause the events of interests to be fired.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/service_listener_singleton_signature.jar");
        // We're really only interesting the listener events
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        Hashtable propsOne = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        propsOne.put("service.interface.name", TestServiceOne.class.getName());
        propsOne.put("service.component.name", "ServiceOneService");
        propsOne.put("service.null.object", Boolean.FALSE);

        Hashtable propsTwo = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        propsTwo.put("service.interface.name", TestServiceTwo.class.getName());
        propsTwo.put("service.component.name", "ServiceOneService");
        propsTwo.put("service.null.object", Boolean.FALSE);

        Hashtable propsFactory = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        propsFactory.put("service.interface.name", ServiceFactory.class.getName());

        // we should see events for both of these signatures because of the assignability assertions
        exportStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_REGISTERED, propsOne));
        exportStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_REGISTERED, propsTwo));
        // however, this one should be an error
        exportStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_REGISTERED, propsFactory));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        // we should see events for both of these signatures because of the assignability assertions
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNREGISTERED, propsOne));
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNREGISTERED, propsTwo));
        // however, this one should be an error
        exportStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNREGISTERED, propsFactory));

        controller.run();
    }


    /**
     * Listener test, but only the registered method is specified in the config.
     */
    public void testListenerRegisteredExport() throws Exception {
        // We only do the export and then shut this back down again.  That will
        // cause the events of interests to be fired.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_listener_registered_export.jar");
        // We're really only interesting the listener events, but we'll take a look
        // at the metadata as well
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        Hashtable props = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        props.put("service.interface.name", TestServiceOne.class.getName());
        props.put("service.component.name", "ServiceOneService");
        exportStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_REGISTERED, props));
        // we're expecting some listener metadata on the export.
        TestRegistrationListener[] listeners = new TestRegistrationListener[] {
            new TestRegistrationListener("ServiceOneListener", "registered", null)
        };
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, listeners)));


        controller.run();
    }


    /**
     * Listener test, but only the unregistered method is specified in the config.
     */
    public void testListenerUnregisteredExport() throws Exception {
        // We only do the export and then shut this back down again.  That will
        // cause the events of interests to be fired.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_listener_unregistered_export.jar");
        // We're really only interesting the listener events, but we'll take a look
        // at the metadata as well
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        Hashtable props = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        props.put("service.interface.name", TestServiceOne.class.getName());
        props.put("service.component.name", "ServiceOneService");
        // no registered event
        // we're expecting some listener metadata on the export.
        TestRegistrationListener[] listeners = new TestRegistrationListener[] {
            new TestRegistrationListener("ServiceOneListener", null, "unregistered")
        };
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, listeners)));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNREGISTERED, props));

        controller.run();
    }


    /**
     * Just a simple export/import with a registration listener with a prototype instance component.
     * The service listeners should be passed null on the registration/unregistration events.
     */
    public void testPrototypeListenerExport() throws Exception {
        // We only do the export and then shut this back down again.  That will
        // cause the events of interests to be fired.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_listener_export.jar");
        // We're really only interesting the listener events, but we'll take a look
        // at the metadata as well
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        Hashtable props = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        props.put("service.interface.name", TestServiceOne.class.getName());
        props.put("service.component.name", "ServiceOneService");
        exportStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_REGISTERED, props));
        // we're expecting some listener metadata on the export.
        TestRegistrationListener[] listeners = new TestRegistrationListener[] {
            new TestRegistrationListener("ServiceOneListener", "registered", "unregistered")
        };
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, listeners)));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNREGISTERED, props));

        controller.run();
    }


    /**
     * Just a simple export/import with a registration listener with an inline instance component.
     * The service listeners should be passed null on the registration/unregistration events.
     */
    public void testInlineListenerExport() throws Exception {
        // We only do the export and then shut this back down again.  That will
        // cause the events of interests to be fired.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_inline_listener_export.jar");
        // We're really only interesting the listener events, but we'll take a look
        // at the metadata as well
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        Hashtable props = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        props.put("service.interface.name", TestServiceOne.class.getName());
        props.put("service.component.name", "ServiceOneService");
        exportStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_REGISTERED, props));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNREGISTERED, props));

        controller.run();
    }


    /**
     * A test to verify that an exported component can service as a listener to its
     * own service registration/unregistration.
     */
    public void testCircularListenerExport() throws Exception {
        // We only do the export and then shut this back down again.  That will
        // cause the events of interests to be fired.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/circular_listener_export.jar");
        // We're really only interesting the listener events, but we'll take a look
        // at the metadata as well
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        Hashtable props = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        props.put("service.interface.name", TestServiceOne.class.getName());
        props.put("service.component.name", "ServiceOneService");
        exportStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_REGISTERED, props));
        // we're expecting some listener metadata on the export.
        TestRegistrationListener[] listeners = new TestRegistrationListener[] {
            new TestRegistrationListener("ServiceOneListener", "registered", "unregistered")
        };
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOneListener", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, listeners)));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNREGISTERED, props));

        controller.run();
    }

    /**
     * Just a export a service with multiple registration listeners.
     * The service exports multiple interfaces, and has one listener for each
     * interface.
     */
    public void testMultipleListenerExport() throws Exception {
        // We only do the export and then shut this back down again.  That will
        // cause the events of interests to be fired.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_multiple_listener_export.jar");
        // We're really only interesting the listener events, but we'll take a look
        // at the metadata as well
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        Hashtable props1 = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        props1.put("service.interface.name", TestServiceOne.class.getName());
        props1.put("service.component.name", "ServiceOneService");
        Hashtable props2 = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        props2.put("service.interface.name", TestServiceTwo.class.getName());
        props2.put("service.component.name", "ServiceOneService");
        exportStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_REGISTERED, props1));
        exportStartEvents.addEvent(new ComponentAssertion("ServiceTwoListener", AssertionService.SERVICE_REGISTERED, props2));
        // we're expecting some listener metadata on the export.
        TestRegistrationListener[] listeners = new TestRegistrationListener[] {
            new TestRegistrationListener("ServiceOneListener", "registered", "unregistered"),
                    new TestRegistrationListener("ServiceTwoListener", "registered", "unregistered")
        };
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, listeners)));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNREGISTERED, props1));
        exportStopEvents.addEvent(new ComponentAssertion("ServiceTwoListener", AssertionService.SERVICE_UNREGISTERED, props2));

        controller.run();
    }

    /**
     * Just a export a service with a single registration listener that implements
     * multiple matching registration/unregistration methods.  These will match
     * the different interfaces exported for the service.
     */
    public void testMultipleListenerMethodExport() throws Exception {
        // We only do the export and then shut this back down again.  That will
        // cause the events of interests to be fired.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_multiple_listener_method_export.jar");
        // We're really only interesting the listener events, but we'll take a look
        // at the metadata as well
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        Hashtable props1 = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        props1.put("service.interface.name", TestServiceOne.class.getName());
        props1.put("service.component.name", "ServiceOneService");
        Hashtable props2 = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        props2.put("service.interface.name", TestServiceTwo.class.getName());
        props2.put("service.component.name", "ServiceOneService");
        // these come from the same listener object, but different calls
        exportStartEvents.addEvent(new ComponentAssertion("ServiceOneTwoListener", AssertionService.SERVICE_REGISTERED, props1));
        exportStartEvents.addEvent(new ComponentAssertion("ServiceOneTwoListener", AssertionService.SERVICE_REGISTERED, props2));
        // we're expecting some listener metadata on the export.
        // listener is an inner component
        TestRegistrationListener[] listeners = new TestRegistrationListener[] {
            new TestRegistrationListener(null, "registered", "unregistered"),
        };
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_EAGER, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, listeners)));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneTwoListener", AssertionService.SERVICE_UNREGISTERED, props1));
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneTwoListener", AssertionService.SERVICE_UNREGISTERED, props2));

        controller.run();
    }


    /**
     * A service listener test.  This should only see calls on startup and shutdown.
     * This is the same as the test above, but the listener is an inline imported service
     */
    public void testServiceImportedListenerImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_service_listener_import.jar",
                getWebServer()+"www/ServiceOne_export.jar",
                getWebServer()+"www/reference_listener_export.jar");
        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we're expecting some listener metadata on the import.
        BindingListener[] listeners = new BindingListener[] {
            new BindingListener("ServiceOneListener", "bind", "unbind"),
        };
        // validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        Hashtable props = new Hashtable();
        props.put("service.interface.name", TestServiceOne.class.getName());
        props.put("service.listener.type", "interface");
        props.put("osgi.service.blueprint.compname", "ServiceOne");
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props));
        controller.run();
    }


    /**
     * A service listener test.  This should only see calls on startup and shutdown.
     * This is the same as the test above, but the listener is an imported service
     * specified as an inline component.
     */
    public void testInlineServiceImportedListenerImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_inline_service_listener_import.jar",
                getWebServer()+"www/ServiceOne_export.jar",
                getWebServer()+"www/reference_listener_export.jar");
        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we're expecting some listener metadata on the import.
        BindingListener[] listeners = new BindingListener[] {
            new BindingListener("ServiceOneListener", "bind", "unbind")
        };
        // validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        Hashtable props = new Hashtable();
        props.put("service.interface.name", TestServiceOne.class.getName());
        props.put("service.listener.type", "interface");
        props.put("osgi.service.blueprint.compname", "ServiceOne");
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props));
        controller.run();
    }


    /**
     * A service listener test with just the bind method specified for the listener.
     */
    public void testReferenceListenerBindOnly() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/service_listener_bind_only.jar",
                getWebServer()+"www/ServiceOne_export.jar");
        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we're expecting some listener metadata on the import.
        BindingListener[] listeners = new BindingListener[] {
            new BindingListener("ServiceOneListener", "bind", null),
        };
        // validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        Hashtable props = new Hashtable();
        props.put("service.interface.name", TestServiceOne.class.getName());
        props.put("service.listener.type", "interface");
        props.put("osgi.service.blueprint.compname", "ServiceOne");
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props));

        controller.run();
    }


    /**
     * A service listener test with just the bind method specified for the listener.
     */
    public void testReferenceListenerUnbindOnly() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/service_listener_unbind_only.jar",
                getWebServer()+"www/ServiceOne_export.jar");
        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we're expecting some listener metadata on the import.
        BindingListener[] listeners = new BindingListener[] {
            new BindingListener("ServiceOneListener", null, "unbind"),
        };
        // validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        Hashtable props = new Hashtable();
        props.put("service.interface.name", TestServiceOne.class.getName());
        props.put("service.listener.type", "interface");
        props.put("osgi.service.blueprint.compname", "ServiceOne");

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props));

        controller.run();
    }


    /*
     * A service listener test.  Similar to the above, but the listener also has the service
     * injected, thus creating a circular reference.  The spec guarantees this will work.
     */
    public void testCircularServiceListenerImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/circular_listener_import.jar",
                getWebServer()+"www/ServiceOne_export.jar");
        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we're expecting some listener metadata on the import.
        BindingListener[] listeners = new BindingListener[] {
            new BindingListener("ServiceOneListener", "bind", "unbind"),
        };
        // validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        Hashtable props = new Hashtable();
        props.put("service.interface.name", TestServiceOne.class.getName());
        props.put("service.listener.type", "interface");
        props.put("osgi.service.blueprint.compname", "ServiceOne");
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props));
        controller.run();
    }


    /*
     * A service listener test.  This should only see calls on startup and shutdown.
     */
    public void testServiceMultipleListenerImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_multiple_listener_import.jar",
                getWebServer()+"www/ServiceOne_multiple_export.jar");
        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we're expecting some listener metadata on the import.
        BindingListener[] listeners = new BindingListener[] {
            new BindingListener("ServiceOneListener", "bind", "unbind"),
            // this is done with an inner component
            new BindingListener(new TestComponentValue(new BeanComponent(null, ServiceOneListener.class, null, null, null, null, null,
            null, BeanMetadata.ACTIVATION_LAZY, null)), "bind", "unbind"),
        };
        // validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        Hashtable props1 = new Hashtable();
        props1.put("service.interface.name", TestServiceOne.class.getName());
        props1.put("service.listener.type", "interface");
        props1.put("osgi.service.blueprint.compname", "ServiceOne");
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));

        Hashtable props2 = new Hashtable();
        props2.put("service.interface.name", TestServiceOne.class.getName());
        props2.put("service.listener.type", "interface");
        props2.put("osgi.service.blueprint.compname", "ServiceOne");
        importStartEvents.addEvent(new ComponentAssertion("ServiceTwoListener", AssertionService.SERVICE_BIND, props2));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        importStopEvents.addEvent(new ComponentAssertion("ServiceTwoListener", AssertionService.SERVICE_UNBIND, props2));
        controller.run();
    }


    /*
     * A service listener test.  This should only see calls on startup and shutdown.
     */
    public void testServiceMultipleListenerMethodImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_multiple_listener_method_import.jar",
                getWebServer()+"www/ServiceOne_multiple_export.jar");
        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we're expecting some listener metadata on the import.
        BindingListener[] listeners = new BindingListener[] {
            new BindingListener("ServiceOneTwoListener", "bind", "unbind"),
        };
        // validate the metadata for the imported services
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceTwo", TestServiceTwo.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        Hashtable props1 = new Hashtable();
        props1.put("service.interface.name", TestServiceOne.class.getName());
        props1.put("service.listener.type", "interface");
        props1.put("osgi.service.blueprint.compname", "ServiceOne");
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneTwoListener", AssertionService.SERVICE_BIND, props1));

        Hashtable props2 = new Hashtable();
        props2.put("service.interface.name", TestServiceTwo.class.getName());
        props2.put("service.listener.type", "interface");
        props2.put("osgi.service.blueprint.compname", "ServiceOne");
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneTwoListener", AssertionService.SERVICE_BIND, props2));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneTwoListener", AssertionService.SERVICE_UNBIND, props1));
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneTwoListener", AssertionService.SERVICE_UNBIND, props2));
        controller.run();
    }


    /*
     * A service listener test.  This uses the ServiceReference signature form.
     */
    public void testServiceListenerReferenceMethodImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_listener_reference_method_import.jar",
                getWebServer()+"www/ServiceOne_multiple_export.jar");
        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we're expecting some listener metadata on the import.
        BindingListener[] listeners = new BindingListener[] {
            new BindingListener("ServiceOneListener", "bindReference", "unbindReference"),
        };
        // validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        Hashtable props1 = new Hashtable();
        props1.put("service.listener.type", "reference");
        props1.put("osgi.service.blueprint.compname", "ServiceOne");
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        controller.run();
    }


    /*
     * A service listener test.  This uses the method signature that doesn't take a Map instance
     */
    public void testServiceListenerNoMapMethodImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/ServiceOne_listener_nomap_method_import.jar",
                getWebServer()+"www/ServiceOne_multiple_export.jar");
        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we're expecting some listener metadata on the import.
        BindingListener[] listeners = new BindingListener[] {
            new BindingListener("ServiceOneListener", "bindNoMap", "unbindNoMap"),
        };
        // validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                null, null, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
        // this will tell us which listener type we called
        Hashtable props1 = new Hashtable();
        props1.put("service.listener.type", "interface_nomap");
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        controller.run();
    }


    /*
     * This tests the behavior of unregistered manadatory services and
     */
    public void testUnregisteredServiceDependency() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/unregistered_dependency_import.jar",
                getWebServer()+"www/managed_one_service_export.jar");
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
        importStartEvents.addAssertion("UnregisteredDependencyChecker", AssertionService.BEAN_INIT_METHOD);

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // the final UNBIND operation
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        controller.run();
    }


    /**
     * This tests the behavior of manadatory services unregistering after module completion.
     * This variation tests replacing the service with another suitable service.
     */
    public void testReplacementServiceDependency() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/replacement_dependency_import.jar");

        controller.addSetupBundle(getWebServer()+"www/managed_replacement_dependency_export.jar");

        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // metadata issues have been well tested elsewhere.  We're going to focus on the service dynamics.

        // the test component will handle all of the validation checking for this
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("DependencyChecker", AssertionService.BEAN_INIT_METHOD);

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        controller.run();
    }


    /**
     * This tests the behavior of manadatory services unregistering after module completion.
     * This service proxy will need to wait for a replacement to be registered.
     */
    public void testWaitingServiceDependency() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/replacement_dependency_import.jar");

        // create a ServiceManager instance with two instances of an injected service.
        // we will register both, and the test will unregister the first.
        ServiceManager serviceManager = new ServiceManagerImpl(getContext(),
                new ManagedService[] {
            // this one is registered from the start
            new ManagedService("ServiceOneA", new TestGoodService("ServiceOneA"), TestServiceOne.class, getContext(), null, true),
            // this one gets registered as a replacement
                    new ManagedService("ServiceOneB", new TestGoodService("ServiceOneB"), TestServiceOne.class, getContext(), null, false)
        });

        // make this registered
        serviceManager.register();

        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // metadata issues have been well tested elsewhere.  We're going to focus on the service dynamics.

        // Ok, when the WAITING event is triggered, we register the second service.
        Properties filterProps = new Properties();
        filterProps.put(org.osgi.framework.Constants.OBJECTCLASS, new String[] { TestServiceOne.class.getName() });

        importStartEvents.addEvent(new BlueprintContainerEvent("WAITING", null, new Properties[] { filterProps }, null ));
        importStartEvents.addEvent(new BlueprintAdminEvent("WAITING", null, new Properties[] { filterProps }, new ServiceManagerRegister(serviceManager, "ServiceOneB")));

        // the test component will handle all of the validation checking for this
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("DependencyChecker", AssertionService.BEAN_INIT_METHOD);

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // add a cleanup processor for the exported services.
        importStopEvents.addTerminator(new ServiceManagerDestroy(serviceManager));
        controller.run();
    }


    /*
     * This tests the behavior of optional availability services and listener events.
     */
    public void testUnavailableServiceDependency() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/unavailable_dependency_import.jar",
                getWebServer()+"www/managed_one_unavailable_service_export.jar");
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
        // this is the bind operation that occurs after the service manager is nudged.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // this is followed by an UNBIND operation when the service goes away again.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("UnavailableDependencyChecker", AssertionService.BEAN_INIT_METHOD);

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // the final UNBIND operation on module context shutdown.  Since this service is
        // not currently bound, we should not see this.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        controller.run();
    }


    /**
     * This tests the behavior of optional availability services and listener events.
     * This is the same ad testUnavailableServiceDependency(), but the availability and
     * timeout options are specified using the components default-* attributes.
     */
    public void testUnavailableServiceDefaultDependency() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/unavailable_dependency_default_import.jar",
                getWebServer()+"www/managed_one_unavailable_service_export.jar");
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
        // this is the bind operation that occurs after the service manager is nudged.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // this is followed by an UNBIND operation when the service goes away again.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("UnavailableDependencyChecker", AssertionService.BEAN_INIT_METHOD);

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // the final UNBIND operation on module context shutdown.  Since this service is
        // not currently bound, we should not see this.
        importStopEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        controller.run();
    }


    /*
     * This tests the behavior of a service rebind to an alternative service.
     */
    public void testServiceRebind() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/rebound_dependency_import.jar",
                getWebServer()+"www/managed_two_service_export.jar");
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
        // binding events for the second service should send these.
        Hashtable props2 = new Hashtable();
        props2.put("service.interface.name", TestServiceOne.class.getName());
        props2.put("service.listener.type", "interface");
        props2.put("test.service.name", "BadService");
        // this is the the initial bind operation.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // According to the spec, if there is a service immediately available, then we won't see the
        // UNBIND happening.  So this would be a failure if this shows up
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        // we should, however, see a BIND event for the replacement service
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props2));
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("ReboundDependencyChecker", AssertionService.BEAN_INIT_METHOD);


        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // the final UNBIND operation on module context shutdown.  This needs to be for the replacement
        // service, since that is the one currently bound.
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props2));
        controller.run();
    }


    /**
     * This tests the behavior of a service rebind when an alternative service
     * with a higher ranking becomes available.  This should NOT rebind the service
     */
    public void testServiceRankingRebind() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/rebound_dependency_ranking_import.jar",
                getWebServer()+"www/managed_two_service_ranking_export.jar");
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
        // binding events for the second service should send these.
        Hashtable props2 = new Hashtable();
        props2.put("service.interface.name", TestServiceOne.class.getName());
        props2.put("service.listener.type", "interface");
        props2.put("test.service.name", "BadService");
        // this is the the initial bind operation.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // According to the spec, if there is a service immediately available, then we won't see the
        // UNBIND happening.  So this would be a failure if this shows up
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        // we should also not see a BIND event for the replacement service...we stay with the original
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props2));
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("ReboundDependencyChecker", AssertionService.BEAN_INIT_METHOD);


        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // the final UNBIND operation on module context shutdown.  This needs to be for the original service
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        controller.run();
    }


    /*
     * This tests the damping behavior of ServiceRegistration proxies
     */
    public void testServiceRegistrationProxy() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/service_registration_proxy.jar",
                getWebServer()+"www/managed_one_service_export.jar");
        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // metadata issues have been well tested elsewhere.  We're going to focus on the service dynamics.
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("ServiceRegistrationChecker", AssertionService.BEAN_INIT_METHOD);
        controller.run();
    }


    /**
     * Tests the lazy initialization aspect of a <service> element and all of the
     * bits that are can be dependencies of a <service> element.
     */
    public void testLazyServiceRegistration() throws Exception {
        // NB:  Only one jar for this test.
        ThreePhaseTestController controller = new ThreePhaseTestController(getContext(),
                getWebServer()+"www/Service_lazy_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        // There will all sorts of events that will signal a failure if we see them
        exportStartEvents.addFailureEvent(new ComponentAssertion("Trigger", AssertionService.BEAN_CREATED));
        exportStartEvents.addFailureEvent(new ComponentAssertion("Depends1", AssertionService.BEAN_CREATED));
        exportStartEvents.addFailureEvent(new ComponentAssertion("Depends2", AssertionService.BEAN_CREATED));
        exportStartEvents.addFailureEvent(new ComponentAssertion("ServiceOne", AssertionService.BEAN_CREATED));
        exportStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.BEAN_CREATED));

        // activation is separate from enablement.  This service should be registered at the end of
        // the first phase
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, null));
        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);

        // ok, now we'll request the trigger component, and that should fire off a whole sequence of events
        MetadataEventSet exportMiddleEvents = controller.getMiddleEvents();
        // this will start the initialization cascade
        exportMiddleEvents.addInitializer(new LazyComponentStarter("Trigger"));

        // these all get created now
        exportMiddleEvents.addEvent(new ComponentAssertion("Trigger", AssertionService.BEAN_CREATED));
        exportMiddleEvents.addEvent(new ComponentAssertion("ServiceOne", AssertionService.BEAN_CREATED));
        exportMiddleEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.BEAN_CREATED));

        TestEvent depends1 = new ComponentAssertion("Depends1", AssertionService.BEAN_CREATED);
        TestEvent depends2 = new ComponentAssertion("Depends2", AssertionService.BEAN_CREATED);

        exportMiddleEvents.addEvent(depends1);
        exportMiddleEvents.addEvent(depends2);

        // and initialized
        exportMiddleEvents.addEvent(new ComponentAssertion("Trigger", AssertionService.BEAN_INIT_METHOD));
        exportMiddleEvents.addEvent(new ComponentAssertion("Depends1", AssertionService.BEAN_INIT_METHOD));
        exportMiddleEvents.addEvent(new ComponentAssertion("Depends2", AssertionService.BEAN_INIT_METHOD));
        exportMiddleEvents.addEvent(new ComponentAssertion("ServiceOne", AssertionService.BEAN_INIT_METHOD));
        exportMiddleEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.BEAN_INIT_METHOD));
        // our listener should get called, but not before the depends-on relationships are finished.
        TestEvent register = new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_REGISTERED);
        register.addDependency(depends1);
        register.addDependency(depends2);
        exportMiddleEvents.addEvent(register);

        // validate that the service has been registered
        // this will be run after all of the events have settled down, so this shoulbe
        // reflect the modified properties.
        Hashtable props = new Hashtable();
        props.put("service.property.integer", new Integer(999));
        exportMiddleEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne", null, props));
        // also validate the metadata for the exported service (explicitly declared lazy)
        // we're expecting some listener metadata on the export.
        TestRegistrationListener[] listeners = new TestRegistrationListener[] {
            new TestRegistrationListener("ServiceOneListener", "registered", "unregistered")
        };
        exportMiddleEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService",
                ServiceMetadata.ACTIVATION_LAZY, "ServiceOne", TestServiceOne.class,
                ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, new String[] { "Depends1", "Depends2" }, listeners)));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        exportStopEvents.addEvent(new ComponentAssertion("Trigger", AssertionService.BEAN_DESTROY_METHOD));
        exportStopEvents.addEvent(new ComponentAssertion("Depends1", AssertionService.BEAN_DESTROY_METHOD));
        exportStopEvents.addEvent(new ComponentAssertion("Depends2", AssertionService.BEAN_DESTROY_METHOD));
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOne", AssertionService.BEAN_DESTROY_METHOD));
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.BEAN_DESTROY_METHOD));
        // an event from our listener
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNREGISTERED));
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, null));

        controller.run();
    }


    /*
     * This tests depends-on and lazy initialization behavior of a <reference> element
     */
    public void testLazyReference() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        ThreePhaseTestController controller = new ThreePhaseTestController(getContext(),
                getWebServer()+"www/lazy_reference.jar",
                getWebServer()+"www/managed_two_service_export.jar");
        // The export jar has been well covered already in other tests.  We'll just focus
        // on the import listener details.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // There will all sorts of events that will signal a failure if we see them
        importStartEvents.addFailureEvent(new ComponentAssertion("ReboundDependencyChecker", AssertionService.BEAN_CREATED));
        importStartEvents.addFailureEvent(new ComponentAssertion("Depends1", AssertionService.BEAN_CREATED));
        importStartEvents.addFailureEvent(new ComponentAssertion("Depends2", AssertionService.BEAN_CREATED));
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.BEAN_CREATED));

        MetadataEventSet importMiddleEvents = controller.getMiddleEvents(0);
        // this will start the initialization cascade
        importMiddleEvents.addInitializer(new LazyComponentStarter("ReboundDependencyChecker"));

        // these all get created now
        importMiddleEvents.addEvent(new ComponentAssertion("ReboundDependencyChecker", AssertionService.BEAN_CREATED));
        TestEvent depends1 = new ComponentAssertion("Depends1", AssertionService.BEAN_CREATED);
        TestEvent depends2 = new ComponentAssertion("Depends2", AssertionService.BEAN_CREATED);

        importMiddleEvents.addEvent(depends1);
        importMiddleEvents.addEvent(depends2);
        importMiddleEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.BEAN_CREATED));

        // and initialized
        importMiddleEvents.addEvent(new ComponentAssertion("Depends1", AssertionService.BEAN_INIT_METHOD));
        importMiddleEvents.addEvent(new ComponentAssertion("Depends2", AssertionService.BEAN_INIT_METHOD));
        importMiddleEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.BEAN_INIT_METHOD));
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        Hashtable props1 = new Hashtable();
        props1.put("service.interface.name", TestServiceOne.class.getName());
        props1.put("service.listener.type", "interface");
        props1.put("test.service.name", "ServiceOneA");
        // binding events for the second service should send these.
        Hashtable props2 = new Hashtable();
        props2.put("service.interface.name", TestServiceOne.class.getName());
        props2.put("service.listener.type", "interface");
        props2.put("test.service.name", "BadService");
        // this is the the initial bind operation.  Ensure the depends-on relationships are handled first.
        TestEvent bind = new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1);
        bind.addDependency(depends1);
        bind.addDependency(depends2);

        importMiddleEvents.addEvent(bind);

        // According to the spec, if there is a service immediately available, then we won't see the
        // UNBIND happening.  So this would be a failure if this shows up
        importMiddleEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        // we should, however, see a BIND event for the replacement service
        importMiddleEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props2));
        // this indicates successful completion of the test phase
        importMiddleEvents.addAssertion("ReboundDependencyChecker", AssertionService.BEAN_INIT_METHOD);

        // we're expecting some listener metadata on the import.
        BindingListener[] listeners = new BindingListener[] {
            new BindingListener("ServiceOneListener", "bind", "unbind"),
        };

        // also validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importMiddleEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_LAZY,
                null, null, new String[] { "Depends1", "Depends2" }, listeners, ReferencedService.DEFAULT_TIMEOUT)));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // the final UNBIND operation on module context shutdown.  This needs to be for the replacement
        // service, since that is the one currently bound.
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props2));

        importStopEvents.addEvent(new ComponentAssertion("ReboundDependencyChecker", AssertionService.BEAN_DESTROY_METHOD));
        importStopEvents.addEvent(new ComponentAssertion("Depends1", AssertionService.BEAN_DESTROY_METHOD));
        importStopEvents.addEvent(new ComponentAssertion("Depends2", AssertionService.BEAN_DESTROY_METHOD));
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.BEAN_DESTROY_METHOD));
        controller.run();
    }


    /*
     * Test that service <reference> elements will not request a service until
     * the proxy is actually called.  We will not make any calls on the proxies, so
     * we should never see a GET request from the service factory.
     */
    public void testLazyServiceGet() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/lazy_service_get.jar",
                getWebServer()+"www/ServiceOne_factory_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // nothing we do here should cause a GET request for the service, so this is a failure if we see one.
        TestEvent factoryGet = new ComponentAssertion("ServiceOneFactory", AssertionService.SERVICE_FACTORY_GET);
        exportStartEvents.addFailureEvent(factoryGet);

        // now the importing side.  We mostly look for the listener variations here.  This listener has implemented
        // all of the different signatures, so we should see a lot of activity from the listeners.
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        Hashtable propsServiceOne = new Hashtable();
        propsServiceOne.put("service.interface.name", TestServiceOne.class.getName());
        propsServiceOne.put("service.listener.type", "interface");

        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsServiceOne));
        importStartEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_BIND, propsServiceOne));

        Hashtable propsServiceOneNoMap = new Hashtable();
        propsServiceOneNoMap.put("service.interface.name", TestServiceOne.class.getName());
        propsServiceOneNoMap.put("service.listener.type", "interface_nomap");

        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsServiceOneNoMap));
        importStartEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_BIND, propsServiceOneNoMap));

        Hashtable propsObject = new Hashtable();
        propsObject.put("service.interface.name", Object.class.getName());
        propsObject.put("service.listener.type", "interface");

        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsObject));
        importStartEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_BIND, propsObject));

        Hashtable propsObjectNoMap = new Hashtable();
        propsObjectNoMap.put("service.interface.name", Object.class.getName());
        propsObjectNoMap.put("service.listener.type", "interface_nomap");

        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsObjectNoMap));
        importStartEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_BIND, propsObjectNoMap));

        Hashtable propsServiceReference = new Hashtable();
        propsServiceReference.put("service.listener.type", "reference");

        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsServiceReference));
        importStartEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_BIND, propsServiceReference));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);

        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsServiceOne));
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsServiceOneNoMap));
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsObject));
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsObjectNoMap));
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsServiceReference));

        importStopEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_UNBIND, propsServiceOne));
        importStopEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_UNBIND, propsServiceOneNoMap));
        importStopEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_UNBIND, propsObject));
        importStopEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_UNBIND, propsObjectNoMap));
        importStopEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_UNBIND, propsServiceReference));

        controller.run();
    }


    /*
     * Test that service <reference> elements will request a service object without
     * specifying an interface.  The proxy should just be an object with no interfaces
     * from the service object implemented.
     */
    public void testInterfacelessReference() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/reference_no_interface.jar",
                getWebServer()+"www/ServiceOne_factory_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // nothing we do here should cause a GET request for the service, so this is a failure if we see one.
        TestEvent factoryGet = new ComponentAssertion("ServiceOneFactory", AssertionService.SERVICE_FACTORY_GET);
        exportStartEvents.addFailureEvent(factoryGet);

        // now the importing side.  We mostly look for the listener variations here.  This listener has implemented
        // all of the different signatures, but we should only see ones for the Object types and the ServiceReference
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        BindingListener[] listeners = new BindingListener[] {
            new BindingListener("ServiceOneListener", "bind", "unbind"),
        };

        // validate the metadata specifics here, since the lack of an interface is a unique item for this one.
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", null,
                ServiceReferenceMetadata.AVAILABILITY_MANDATORY, ServiceReferenceMetadata.ACTIVATION_EAGER,
                "ServiceOneFactory", null, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));

        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferenceCollection("ServiceList",
                null, ServiceReferenceMetadata.AVAILABILITY_MANDATORY,
                ServiceReferenceMetadata.ACTIVATION_EAGER, "ServiceOneFactory", null, null,
                new BindingListener[] { new BindingListener("ServiceListListener", "bind", "unbind")},
                ReferenceListMetadata.USE_SERVICE_OBJECT)));

        Hashtable propsObject = new Hashtable();
        propsObject.put("service.interface.name", Object.class.getName());
        propsObject.put("service.listener.type", "interface");

        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsObject));
        importStartEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_BIND, propsObject));

        Hashtable propsObjectNoMap = new Hashtable();
        propsObjectNoMap.put("service.interface.name", Object.class.getName());
        propsObjectNoMap.put("service.listener.type", "interface_nomap");

        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsObjectNoMap));
        importStartEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_BIND, propsObjectNoMap));

        Hashtable propsServiceReference = new Hashtable();
        propsServiceReference.put("service.listener.type", "reference");

        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, propsServiceReference));
        importStartEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_BIND, propsServiceReference));

        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);

        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsObject));
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsObjectNoMap));
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, propsServiceReference));

        importStopEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_UNBIND, propsObject));
        importStopEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_UNBIND, propsObjectNoMap));
        importStopEvents.addEvent(new ComponentAssertion("ServiceListListener", AssertionService.SERVICE_UNBIND, propsServiceReference));

        controller.run();
    }
    /*
     * Just a simple export/import for a service with an unsatisfied dependency.  According to
     * section 121.6.11, the manager must call the listener with the initial registration state.
     * In this test, there will be two services, one that is registered at activation and one
     * unregistered.
     */
    public void testRegistrationListenerInitialState() throws Exception {
        // We only do the export and then shut this back down again.  That will
        // cause the events of interests to be fired.
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/registration_listener_initial_state.jar");
        // We're really only interesting the listener events, but we'll take a look
        // at the metadata as well
        MetadataEventSet exportStartEvents = controller.getStartEvents();
        Hashtable activeProps = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        activeProps.put("service.interface.name", TestServiceOne.class.getName());
        activeProps.put("service.component.name", "ServiceOneActive");
        exportStartEvents.addEvent(new ComponentAssertion("registeredListener", AssertionService.SERVICE_REGISTERED, activeProps));

        Hashtable inactiveProps = new Hashtable();
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        inactiveProps.put("service.interface.name", TestServiceOne.class.getName());
        // this service should get an unregistered call
        exportStartEvents.addEvent(new ComponentAssertion("unregisteredListener", AssertionService.SERVICE_UNREGISTERED, inactiveProps));
        // we'll fail this if we see a registered event
        exportStartEvents.addFailureEvent(new ComponentAssertion("unregisteredListener", AssertionService.SERVICE_REGISTERED, inactiveProps));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        exportStopEvents.addEvent(new ComponentAssertion("registeredListener", AssertionService.SERVICE_UNREGISTERED, activeProps));
        // and we should not see a second unregistered event for this
        exportStopEvents.addFailureEvent(new ComponentAssertion("unregisteredListener", AssertionService.SERVICE_UNREGISTERED, inactiveProps));

        controller.run();
    }
}
