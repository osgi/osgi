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

import java.util.List;
import java.util.Set;

import java.text.SimpleDateFormat;
import org.osgi.service.blueprint.namespace.NamespaceHandler;
import org.osgi.service.blueprint.reflect.CollectionBasedServiceReferenceComponentMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceComponentMetadata;
import org.osgi.service.blueprint.reflect.ServiceExportComponentMetadata;

import org.osgi.test.cases.blueprint.components.namespace.NamespaceChecker;
import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.TestGoodServiceSubclass;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains tests related with the framework class loading policies.
 *
 * @author left
 * @version $Revision$
 */
public class TestNamespaceHandler extends DefaultTestBundleControl {
	/*
	 * Tests a simple managed bundle with a single component, no explicit header specified.
	 */
	public void testDateHandlerExample() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/date_namespace_checker.jar");

        // this installs and starts the bundle containing the namespace handler before
        // starting the test
        controller.addSetupBundle(getWebServer()+"www/date_namespace_handler.jar");

        // we're mostly interested not receiving any failure-related events, but
        // we'll check some of the metadata to make sure that a)  Everything ended up
        // in there, and b)  That the metadata APIs still work through our injected
        // helper classes
        MetadataEventSet startEvents = controller.getStartEvents();
        // and the validate the component metadata
        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("format1", SimpleDateFormat.class,
            new TestParameter[] { new TestParameter("yyyy.MM.dd G 'at' HH:mm:ss z", 0) }, null)));

        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("format2", SimpleDateFormat.class,
            new TestParameter[] { new TestParameter("yyyy-MM-dd HH:mm", 0) },
            new TestProperty[] { new TestProperty("true", "lenient")})));

        // and this tests the inner component metadata
        startEvents.addValidator(new PropertyMetadataValidator("checker3",
            new TestProperty(new TestComponentValue(
            new LocalComponent(SimpleDateFormat.class, null,
                new TestParameter[] { new TestParameter("yyyy-MM-dd HH:mm", 0) },
                new TestProperty[] { new TestProperty("false", "lenient")})),
            "format")));

        controller.run();
    }


	/*
	 * Test decoration of components by a namespace handler.
	 */
	public void testDecorate() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/decorate_namespace.jar");

        // this installs and starts the bundle containing the namespace handler before
        // starting the test
        controller.addSetupBundle(getWebServer()+"www/test_namespace_handler.jar");

        // we're mostly interested that the metadata we've updated has been maintained, but
        // there are a couple of events that we'll expect to see also.
        MetadataEventSet startEvents = controller.getStartEvents();
        // and the validate the component metadata
        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("decorate1", NamespaceChecker.class, "init", null,
            new TestParameter[] { new StringParameter("decorate1") }, null)));

        // and the validate the component metadata
        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("decorate2", NamespaceChecker.class,
            new TestParameter[] { new StringParameter("decorate2") },
            new TestProperty[] { new TestProperty(new TestReferenceValue("bundle"), "bundle") })));

        // and the validate the component metadata
        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("decorate3", NamespaceChecker.class,
            new TestParameter[] { new StringParameter("decorate3") },
            new TestProperty[] { new TestProperty(new TestReferenceValue("bundle"), "bundle") })));

        // we should see an init method call here
        startEvents.addAssertion("decorate1", AssertionService.COMPONENT_INIT_METHOD);
        // 2 & 3 should see the setter calls
        startEvents.addAssertion("decorate2", AssertionService.METHOD_CALLED);
        startEvents.addAssertion("decorate3", AssertionService.METHOD_CALLED);

        controller.run();
    }


	/**
	 * Tests the component registry and the ability to use non-implementation created
     * metadata instances for different component replacements
	 */
	public void testComponentCopy() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/component_copy.jar");

        // this installs and starts the bundle containing the namespace handler before
        // starting the test
        controller.addSetupBundle(getWebServer()+"www/test_namespace_handler.jar");

        // We're mostly interested that the component metadata has remained what we expect to see
        // and that there were no errors
        MetadataEventSet startEvents = controller.getStartEvents();

        // and the collection metadata
        startEvents.addValidator(new ComponentMetadataValidator(new ReferenceCollection("list",
            NamespaceHandler.class, ServiceReferenceComponentMetadata.OPTIONAL_AVAILABILITY, null,
            null, List.class, null,
            CollectionBasedServiceReferenceComponentMetadata.ORDER_BASIS_SERVICES,
            CollectionBasedServiceReferenceComponentMetadata.MEMBER_TYPE_SERVICES)));

        startEvents.addValidator(new ComponentMetadataValidator(new ReferenceCollection("set",
            NamespaceHandler.class, ServiceReferenceComponentMetadata.OPTIONAL_AVAILABILITY, null,
            null, Set.class, null,
            CollectionBasedServiceReferenceComponentMetadata.ORDER_BASIS_SERVICES,
            CollectionBasedServiceReferenceComponentMetadata.MEMBER_TYPE_SERVICES)));

        // also validate the metadata for the imported service (this one only has a single import, so easy to locate)
        startEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("handler", NamespaceHandler.class,
            ServiceReferenceComponentMetadata.MANDATORY_AVAILABILITY, null, null, ReferencedService.DEFAULT_TIMEOUT)));

        // and the validate the component metadata
        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("ServiceOne", TestGoodServiceSubclass.class, null, null,
            new TestParameter[] { new StringParameter("ServiceOne") }, null)));

        startEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOne", TestServiceOne.class,
            ServiceExportComponentMetadata.EXPORT_MODE_DISABLED, 0, null, null, null)));

        controller.run();
    }


	/*
	 * Test error from a decorate() namespace method call
	 */
	public void testDecorateError() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_decorate.jar");

        // this installs and starts the bundle containing the namespace handler before
        // starting the test
        controller.addSetupBundle(getWebServer()+"www/test_namespace_handler.jar");
        controller.run();
    }


	/*
	 * Test error from a parse() namespace method call
	 */
	public void testParseError() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_parse.jar");

        // this installs and starts the bundle containing the namespace handler before
        // starting the test
        controller.addSetupBundle(getWebServer()+"www/test_namespace_handler.jar");
        controller.run();
    }
}
