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

import java.util.Hashtable;

import org.osgi.service.blueprint.reflect.ServiceExportComponentMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceComponentMetadata;
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
	/*
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
        exportStartEvents.addAssertion("ServiceOne", AssertionService.COMPONENT_CREATED);
        // validate that the service has been registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne"));
        // this will validate the getComponent() result and check this is also in getComponentNames();
        exportStartEvents.addValidator(new ServiceComponentValidator("ServiceOneService"));
        exportStartEvents.addValidator(new ComponentNamePresenceValidator("ServiceOneService"));
        exportStartEvents.addValidator(new GetExportedServicesMetadataValidator("ServiceOneService"));

        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOne", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, null, null)));
        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // also validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
            ServiceReferenceComponentMetadata.MANDATORY_AVAILABILITY, null, null, ReferencedService.DEFAULT_TIMEOUT)));
        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);
        // validate that the component is the correct type
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceOne", TestServiceOne.class));
        importStartEvents.addValidator(new ComponentNamePresenceValidator("ServiceOne"));
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceOne"));

        // do some validation of the import metadata
        importStartEvents.addValidator(new ConstructorMetadataValidator("ServiceOneConstructor", new TestParameter[] {
            new StringParameter("ServiceOneConstructor"),
            new ReferenceParameter("ServiceOne", TestServiceOne.class)
        }));

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceOneProperty", new TestProperty[] {
            new ReferenceProperty("one", "ServiceOne")
        }));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // the is the component creation
        exportStopEvents.addAssertion("ServiceOne", AssertionService.COMPONENT_DESTROY_METHOD);
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
        exportStartEvents.addAssertion("ServiceOne", AssertionService.COMPONENT_CREATED);
        // We should see both of these registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne"));
        // this should be registered under a lot of different interface names, and also has service properties
        Hashtable serviceProps = new Hashtable();
        serviceProps.put("serviceType", "Bad");
        serviceProps.put("autoExport", "All");
        exportStartEvents.addValidator(new ServiceRegistrationValidator(
            new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceTwoSubclass.class, TestServiceAllSubclass.class,
                TestGoodService.class, TestGoodServiceSubclass.class, TestBadService.class, BaseTestComponent.class, ComponentTestInfo.class },
           "BadService", null, serviceProps));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOne", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, null, null)));

        // This is the metadata for the bad service, which specifies quite a few things, including a lazy-init on the component.
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("BadServiceService", null,
            new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceTwoSubclass.class, TestServiceAllSubclass.class,
                TestGoodService.class, TestGoodServiceSubclass.class, TestBadService.class },
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 2, serviceProps, null, null)));
        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);
        // a very complex service event
        exportStartEvents.addServiceEvent("REGISTERED", new Class[] { TestServiceOne.class, TestServiceTwo.class,
            TestServiceTwoSubclass.class, TestServiceAllSubclass.class, TestGoodService.class,
            TestGoodServiceSubclass.class, TestBadService.class, BaseTestComponent.class, ComponentTestInfo.class }, serviceProps);

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // we reuse this to validate multiple entities
        ReferencedService service = new ReferencedService(null, TestServiceOne.class,
            ServiceReferenceComponentMetadata.MANDATORY_AVAILABILITY, null, null, ReferencedService.DEFAULT_TIMEOUT);

        // also validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ReferencedServiceValidator(service));

        // do some validation of the import metadata of the injected parameter.  Since these are
        // embedded elements, they're reuse the definition above
        importStartEvents.addValidator(new ConstructorMetadataValidator("ServiceOneConstructor", new TestParameter[] {
            new StringParameter("ServiceOneConstructor"),
            new TestParameter(new TestComponentValue(service))
        }));

        importStartEvents.addValidator(new PropertyMetadataValidator("ServiceOneProperty", new TestProperty[] {
            new TestProperty(new TestComponentValue(service), "one")
        }));

        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // the is the component creation
        exportStopEvents.addAssertion("ServiceOne", AssertionService.COMPONENT_DESTROY_METHOD);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        exportStopEvents.addServiceEvent("UNREGISTERING", new Class[] { TestServiceOne.class, TestServiceTwo.class,
            TestServiceTwoSubclass.class, TestServiceAllSubclass.class, TestGoodService.class,
            TestGoodServiceSubclass.class, TestBadService.class }, serviceProps);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, "(osgi.service.blueprint.compname=ServiceOne)"));
        // and make sure all of the bad ones are unregistered also
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(
            new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceTwoSubclass.class, TestServiceAllSubclass.class,
                TestGoodService.class, TestGoodServiceSubclass.class, TestBadService.class, BaseTestComponent.class, ComponentTestInfo.class },
            "(osgi.service.blueprint.compname=BadService)"));

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
            getWebServer()+"www/ServiceOne_import.jar",
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
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOne", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 3, null, null, null)));

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
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOne", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, serviceProps, null, null)));


        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class, serviceProps);

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);

        ReferencedService service = new ReferencedService(null, TestServiceOne.class,
            ServiceReferenceComponentMetadata.MANDATORY_AVAILABILITY, "(serviceType=Good)", null, ReferencedService.DEFAULT_TIMEOUT);

        // do some validation of the import metadata of the injected parameter.  Since these are
        // embedded elements, they're reuse the definition above
        importStartEvents.addValidator(new ConstructorMetadataValidator("ServiceOneConstructor", new TestParameter[] {
            new StringParameter("ServiceOneConstructor"),
            new TestParameter(new TestComponentValue(service))
        }));


        service = new ReferencedService(null, TestServiceOne.class,
            ServiceReferenceComponentMetadata.MANDATORY_AVAILABILITY, "(!(serviceType=Bad))", null, ReferencedService.DEFAULT_TIMEOUT);

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
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("GoodServiceService", "ServiceOne", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, new String[] { "Depends1", "Depends2"}, null)));

        // these are lazy-inited components, but we should see them get created
        // because of the depends-on attribute
        exportStartEvents.addAssertion("Depends1", AssertionService.COMPONENT_CREATED);
        exportStartEvents.addAssertion("Depends2", AssertionService.COMPONENT_CREATED);

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
        exportStartEvents.addValidator(new ServiceRegistrationValidator(new Class[] { TestServiceOne.class, TestServiceTwo.class }, "ServiceOne"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOne",
            new Class[] { TestServiceOne.class, TestServiceTwo.class },
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, null, null)));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", new Class[] { TestServiceOne.class, TestServiceTwo.class });

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // we inject two properties, each of which does a service test using different interfaces.
        // We expect two of these events.
        importStartEvents.addAssertion("ServiceOneMultipleChecker", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneMultipleChecker", AssertionService.SERVICE_SUCCESS);
        // and two additional components that just request this using a single interface.
        importStartEvents.addAssertion("ServiceOneChecker", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceTwoChecker", AssertionService.SERVICE_SUCCESS);
        // validate each of the imported references
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceOneMultiple", new Class[]  { TestServiceOne.class, TestServiceTwo.class } ));
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceOne", TestServiceOne.class));
        importStartEvents.addValidator(new ReferenceComponentValidator("ServiceTwo", TestServiceTwo.class));
        // validate this is in the reference export list
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceOne"));
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceTwo"));
        importStartEvents.addValidator(new GetReferencedServicesMetadataValidator("ServiceOneMultiple"));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", new Class[] { TestServiceOne.class, TestServiceTwo.class });
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(new Class[] { TestServiceOne.class, TestServiceTwo.class },
            "(osgi.service.blueprint.compname=ServiceOne)"));

        controller.run();
    }


	/**
	 * This tests the use of concrete class implementations as the exported interface.
	 */
	public void testConcreteInterface() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.  The export jar will export both the target "good" service
        // and secondary "bad" service.  We should resolve to the good service.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/Service_concrete_import.jar",
            getWebServer()+"www/Service_concrete_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // Check the registration of both of these classes.  Note, the validation
        // requests each of these interface individually, but the properties should be the same for all.
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestGoodService.class, "ServiceOne"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOne",
            TestGoodService.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, null, null)));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestGoodService.class);

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // just a single checker service this time.
        // We expect two of these events.
        importStartEvents.addAssertion("ServiceOneChecker", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
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
            new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class },
            "ServiceInterfaces"));
        // this is a belt-and-braces check to make sure only the interface classes were published
        exportStartEvents.addValidator(new ServiceUnregistrationValidator(
            new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class },
            null));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceInterfacesService", "ServiceInterfaces",
            new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class },
            ServiceExportComponentMetadata.EXPORT_MODE_INTERFACES, 0, null, null, null)));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", new Class[] { TestServiceOne.class, TestServiceTwo.class,
            TestServiceAllSubclass.class, ComponentTestInfo.class });

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
            TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class });
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(
            new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class },
            "(osgi.service.blueprint.compname=ServiceInterfaces)"));

        controller.run();
    }


	/**
	 * This tests the auto export of concreate class hierarchy
	 */
	public void testAutoHierarchy() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.  The export jar will export both the target "good" service
        // and secondary "bad" service.  We should resolve to the good service.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/Service_auto_hierarchy_import.jar",
            getWebServer()+"www/Service_auto_hierarchy_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // Verify all of these classes are registered as services for the named component.
        // requests each of these interface individually, but the properties should be the same for all.
        exportStartEvents.addValidator(new ServiceRegistrationValidator(
            new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class },
            "ServiceClasses"));
        // this is a belt-and-braces check to make sure only the hierarchy classes were published
        exportStartEvents.addValidator(new ServiceUnregistrationValidator(
            new Class[] { TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class },
            null));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceClassesService", "ServiceClasses",
            new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class },
            ServiceExportComponentMetadata.EXPORT_MODE_CLASS_HIERARCHY, 0, null, null, null)));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", new Class[] { TestGoodService.class, TestGoodServiceSubclass.class,
            BaseTestComponent.class });

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // this imports 2 services and checks them, so we should get 2 interface events.
        importStartEvents.addAssertion("ServiceHierarchy", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceHierarchy", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", new Class[] { TestGoodService.class, TestGoodServiceSubclass.class,
            BaseTestComponent.class });
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(
            new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class  },
            "(osgi.service.blueprint.compname=ServiceClasses)"));

        controller.run();
    }


	/**
	 * This tests the auto export of all interface and hierarchy classes
	 */
	public void testAutoAll() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.  The export jar will export both the target "good" service
        // and secondary "bad" service.  We should resolve to the good service.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/Service_auto_all_import.jar",
            getWebServer()+"www/Service_auto_all_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // Verify all of these classes are registered as services for the named component.
        // requests each of these interface individually, but the properties should be the same for all.
        exportStartEvents.addValidator(new ServiceRegistrationValidator(
            new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class,
            TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class },
            "ServiceAll"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceAllService", "ServiceAll",
            new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class,
            TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class },
            ServiceExportComponentMetadata.EXPORT_MODE_ALL, 0, null, null, null)));

        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED",
            new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class,
            TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class });

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // this imports 2 services and checks them, so we should get 2 interface events.
        importStartEvents.addAssertion("ServiceHierarchy", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceHierarchy", AssertionService.SERVICE_SUCCESS);
        // and 3 more from the other service.
        importStartEvents.addAssertion("ServiceInterfaces", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceInterfaces", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceInterfaces", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING",
            new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class,
            TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class });
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(
            new Class[] { TestGoodService.class, TestGoodServiceSubclass.class, BaseTestComponent.class,
            TestServiceOne.class, TestServiceTwo.class, TestServiceAllSubclass.class, ComponentTestInfo.class },
            "(osgi.service.blueprint.compname=ServiceAll)"));

        controller.run();
    }


	/*
	 * Test accessing a service using an exported ServiceFactory instance.
	 */
	public void testFactoryExport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ServiceOne_import.jar",
            getWebServer()+"www/ServiceOne_factory_export.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // the is the factory component creation
        exportStartEvents.addAssertion("ServiceOneFactory", AssertionService.COMPONENT_CREATED);
        // this the service instance getting created as a result of the factory getting called
        exportStartEvents.addAssertion("ServiceOneFactory_0", AssertionService.COMPONENT_CREATED);
        // validate that the service has been registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOneFactory"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneFactoryService", "ServiceOneFactory", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, null, null)));
        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        // also validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
            ServiceReferenceComponentMetadata.MANDATORY_AVAILABILITY, null, null, ReferencedService.DEFAULT_TIMEOUT)));
        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // this is the unget of the service instance
        exportStopEvents.addAssertion("ServiceOneFactory", AssertionService.SERVICE_FACTORY_UNGET);
        // the is the factory shutdown
        exportStopEvents.addAssertion("ServiceOneFactory", AssertionService.COMPONENT_DESTROY_METHOD);
        // we should see a service event here indicating this is being deregistered
        exportStopEvents.addServiceEvent("UNREGISTERING", TestServiceOne.class);
        // and there should not be a registration active anymore
        exportStopEvents.addValidator(new ServiceUnregistrationValidator(TestServiceOne.class, null));

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
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOne", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, null, null)));
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
	 * Similar to the single import/export test, but the importing component
     * receives the ServiceReference object, not the service
	 */
	public void testServiceReferenceInjection() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/Service_reference_injection.jar",
            getWebServer()+"www/ServiceOne_export.jar");
        // the exporting side has been widely tested and this test does nothing new,
        // adding anything to that so we can skip

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents(0);
        importStartEvents.addAssertion("ServiceReferenceChecker", AssertionService.SERVICE_SUCCESS);
        controller.run();
    }


	/**
	 * Similar to the single import/export test, but the importing component
     * receives the ServiceReference object.  However, since the service will not
     * be available, a null value will be injected.
	 */
	public void testServiceReferenceNullInjection() throws Exception {
        // NB:  This only has a single jar because we're not dependent upon a service
        // being there.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/Service_reference_null_injection.jar");

        // now the importing side.  We've got a couple of service injections to validate, plus the injection
        // results
        MetadataEventSet importStartEvents = controller.getStartEvents();
        importStartEvents.addAssertion("ServiceReferenceChecker", AssertionService.SERVICE_SUCCESS);
        controller.run();
    }


	/**
	 * A test of bundle scope.  A single service with bundle scope will be requested from
     * three different bundles with two references each.  We should see 3 instances of the
     * component created.
	 */
	public void testBundleScopeExport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ServiceOne_bundle_import1.jar",
            getWebServer()+"www/ServiceOne_bundle_export.jar");

        // add two different bundle that will request the same service
        controller.addBundle(getWebServer()+"www/ServiceOne_bundle_import2.jar");
        controller.addBundle(getWebServer()+"www/ServiceOne_bundle_import3.jar");
        // we add different validation stuff to each jar.  We'll start with the
        // export jar
        MetadataEventSet exportStartEvents = controller.getStartEvents(1);
        // the is the component creation.  We should see this 3 times.
        exportStartEvents.addAssertion("ServiceOne", AssertionService.COMPONENT_CREATED);
        exportStartEvents.addAssertion("ServiceOne", AssertionService.COMPONENT_CREATED);
        exportStartEvents.addAssertion("ServiceOne", AssertionService.COMPONENT_CREATED);
        // this one will catch getting more than 3 created.
        exportStartEvents.addFailureEvent(new ComponentAssertion("ServiceOne", AssertionService.COMPONENT_CREATED));
        // validate that the service has been registered
        exportStartEvents.addValidator(new ServiceRegistrationValidator(TestServiceOne.class, "ServiceOne"));
        // also validate the metadata for the exported service
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("GoodServiceService", "ServiceOne", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, null, null)));
        // we should see a service event here indicating this was registered
        exportStartEvents.addServiceEvent("REGISTERED", TestServiceOne.class);

        // We'll not check for anything specific on the imports.  Anything bad that goes wrong will generate
        // a terminating event.

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents(1);
        // With prototype scope, the destroy should still run (I believe).  We should see
        // a destroy event for each bundle that releases the service.
        exportStopEvents.addAssertion("ServiceOne", AssertionService.COMPONENT_DESTROY_METHOD);
        exportStopEvents.addAssertion("ServiceOne", AssertionService.COMPONENT_DESTROY_METHOD);
        exportStopEvents.addAssertion("ServiceOne", AssertionService.COMPONENT_DESTROY_METHOD);
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
        ServiceManager serviceManager = new ServiceManager(getContext(),
            new ManagedService("ServiceOne", new TestGoodService("ServiceOne"), TestServiceOne.class, getContext(), null, false));

        // now the importing side.  The key event is the blueprint WAITING event.  We'll use that
        // to trigger the ServiceManager to register the required service
        MetadataEventSet importStartEvents = controller.getStartEvents(0);

        // look for a blueprint event that can trigger the service manager to register
        // the dependent service
        importStartEvents.addEvent(new BlueprintEvent("WAITING", null, new ServiceManagerRegister(serviceManager)));
        // these will be triggered by the satisfied wait
        importStartEvents.addAssertion("ServiceOneConstructor", AssertionService.SERVICE_SUCCESS);
        importStartEvents.addAssertion("ServiceOneProperty", AssertionService.SERVICE_SUCCESS);

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        // add a cleanup processor for the exported services.
        exportStopEvents.addTerminator(new ServiceManagerUnregister(serviceManager));

        controller.run();
    }

	/*
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
        RegistrationListener[] listeners = new RegistrationListener[] {
            new RegistrationListener("ServiceOneListener", "register", "unregister")
        };
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOne", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, null, listeners)));

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
        RegistrationListener[] listeners = new RegistrationListener[] {
            new RegistrationListener("ServiceOneListener", "register", "unregister")
        };
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOneListener", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, null, listeners)));

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
        RegistrationListener[] listeners = new RegistrationListener[] {
            new RegistrationListener("ServiceOneListener", "register", "unregister"),
            new RegistrationListener("ServiceTwoListener", "register", "unregister")
        };
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOne", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, null, listeners)));

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
        RegistrationListener[] listeners = new RegistrationListener[] {
            new RegistrationListener("ServiceOneTwoListener", "register", "unregister"),
        };
        exportStartEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOne", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, null, listeners)));

        // now some expected termination stuff
        EventSet exportStopEvents = controller.getStopEvents();
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneTwoListener", AssertionService.SERVICE_UNREGISTERED, props1));
        exportStopEvents.addEvent(new ComponentAssertion("ServiceOneTwoListener", AssertionService.SERVICE_UNREGISTERED, props2));

        controller.run();
    }


	/*
	 * A service listener test.  This should only see calls on startup and shutdown.
	 */
	public void testServiceListenerImport() throws Exception {
        // NB:  We're going to load the import jar first, since starting that
        // one first might result in a dependency wait in the second.  This should
        // still work.
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/ServiceOne_listener_import.jar",
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
            ServiceReferenceComponentMetadata.MANDATORY_AVAILABILITY, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
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
            ServiceReferenceComponentMetadata.MANDATORY_AVAILABILITY, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
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
            new BindingListener(new TestComponentValue(new LocalComponent(null, ServiceTwoListener.class, null, null)), "bind", "unbind"),
        };
        // validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
            ServiceReferenceComponentMetadata.MANDATORY_AVAILABILITY, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
        // the first property comes from the called method signature, the
        // second should be passed to the registration listener.
        Hashtable props1 = new Hashtable();
        props1.put("service.interface.name", TestServiceOne.class.getName());
        props1.put("service.listener.type", "interface");
        props1.put("osgi.service.blueprint.compname", "ServiceOne");
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));

        Hashtable props2 = new Hashtable();
        props2.put("service.interface.name", TestServiceTwo.class.getName());
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
        // validate the metadata for the imported service (this one only has a single import, so easy to locate)
        importStartEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("ServiceOne", TestServiceOne.class,
            ServiceReferenceComponentMetadata.MANDATORY_AVAILABILITY, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
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
            ServiceReferenceComponentMetadata.MANDATORY_AVAILABILITY, null, listeners, ReferencedService.DEFAULT_TIMEOUT)));
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
        props1.put("osgi.service.blueprint.compname", "ServiceOneA");
        // this is the initial bind operation at ModuleContext creation
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // this is followed by an UNBIND operation when the service goes away
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        // And then rebound again by the driver code.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("UnregisteredDependencyChecker", AssertionService.COMPONENT_INIT_METHOD);

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
        importStartEvents.addAssertion("DependencyChecker", AssertionService.COMPONENT_INIT_METHOD);

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
        ServiceManager serviceManager = new ServiceManager(getContext(),
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
        importStartEvents.addEvent(new BlueprintEvent("WAITING", null, new ServiceManagerRegister(serviceManager, "ServiceOneB")));

        // the test component will handle all of the validation checking for this
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("DependencyChecker", AssertionService.COMPONENT_INIT_METHOD);

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
        props1.put("osgi.service.blueprint.compname", "ServiceOneA");
        // this is the bind operation that occurs after the service manager is nudged.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // this is followed by an UNBIND operation when the service goes away again.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("UnavailableDependencyChecker", AssertionService.COMPONENT_INIT_METHOD);

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
        props1.put("osgi.service.blueprint.compname", "ServiceOneA");
        // this is the bind operation that occurs after the service manager is nudged.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // this is followed by an UNBIND operation when the service goes away again.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("UnavailableDependencyChecker", AssertionService.COMPONENT_INIT_METHOD);

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
        props1.put("osgi.service.blueprint.compname", "ServiceOneA");
        // binding events for the second service should send these.
        Hashtable props2 = new Hashtable();
        props2.put("service.interface.name", TestServiceOne.class.getName());
        props2.put("service.listener.type", "interface");
        props2.put("osgi.service.blueprint.compname", "ServiceOneB");
        // this is the the initial bind operation.
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props1));
        // According to the spec, if there is a service immediately available, then we won't see the
        // UNBIND happening.  So this would be a failure if this shows up
        importStartEvents.addFailureEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props1));
        // we should, however, see a BIND event for the replacement service
        importStartEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_BIND, props2));
        // this indicates successful completion of the test phase
        importStartEvents.addAssertion("UnregisteredDependencyChecker", AssertionService.COMPONENT_INIT_METHOD);


        // now some expected termination stuff
        EventSet importStopEvents = controller.getStopEvents(0);
        // the final UNBIND operation on module context shutdown.  This needs to be for the replacement
        // service, since that is the one currently bound.
        importStopEvents.addEvent(new ComponentAssertion("ServiceOneListener", AssertionService.SERVICE_UNBIND, props2));
        controller.run();
    }
}
