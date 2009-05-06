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

import java.util.List;
import java.util.Set;

import java.text.SimpleDateFormat;
import org.osgi.service.blueprint.namespace.NamespaceHandler;
import org.osgi.service.blueprint.reflect.RefCollectionMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;
import org.osgi.service.blueprint.reflect.ServiceMetadata;

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
            new BeanComponent("format1", SimpleDateFormat.class,
            new TestArgument[] { new TestArgument("yyyy.MM.dd G 'at' HH:mm:ss z", 0) }, null)));

        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("format2", SimpleDateFormat.class,
            new TestArgument[] { new TestArgument("yyyy-MM-dd HH:mm", 0) },
            new TestProperty[] { new TestProperty("true", "lenient")})));

        // and this tests the inner component metadata
        startEvents.addValidator(new PropertyMetadataValidator("checker3",
            new TestProperty(new TestComponentValue(
            new BeanComponent(SimpleDateFormat.class, null,
                new TestArgument[] { new TestArgument("yyyy-MM-dd HH:mm", 0) },
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
            new BeanComponent("decorate1", NamespaceChecker.class, "init", null,
            new TestArgument[] { new StringArgument("decorate1") }, null)));

        // and the validate the component metadata
        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("decorate2", NamespaceChecker.class,
            new TestArgument[] { new StringArgument("decorate2") },
            new TestProperty[] { new TestProperty(new TestRefValue("bundle"), "bundle") })));

        // and the validate the component metadata
        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("decorate3", NamespaceChecker.class,
            new TestArgument[] { new StringArgument("decorate3") },
            new TestProperty[] { new TestProperty(new TestRefValue("bundle"), "bundle") })));

        // we should see an init method call here
        startEvents.addAssertion("decorate1", AssertionService.COMPONENT_INIT_METHOD);
        // 2 & 3 should see the setter calls
        startEvents.addAssertion("decorate2", AssertionService.METHOD_CALLED);
        startEvents.addAssertion("decorate3", AssertionService.METHOD_CALLED);

        // these are created from classes not imported by the bundle, but we
        // should see the creation events
        startEvents.addAssertion("Good", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("FactoryGood", AssertionService.COMPONENT_CREATED);

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
            NamespaceHandler.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, List.class, null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)));

        startEvents.addValidator(new ComponentMetadataValidator(new ReferenceCollection("set",
            NamespaceHandler.class, ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null,
            null, Set.class, null,
            RefCollectionMetadata.ORDERING_BASIS_SERVICE,
            RefCollectionMetadata.MEMBER_TYPE_SERVICE_INSTANCE)));

        // also validate the metadata for the imported service (this one only has a single import, so easy to locate)
        startEvents.addValidator(new ComponentMetadataValidator(new ReferencedService("handler", NamespaceHandler.class,
            ServiceReferenceMetadata.AVAILABILITY_MANDATORY, null, null, ReferencedService.DEFAULT_TIMEOUT)));

        // and the validate the component metadata
        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("ServiceOne", TestGoodServiceSubclass.class, null, null,
            new TestArgument[] { new StringArgument("ServiceOne") }, null)));

        startEvents.addValidator(new ExportedServiceValidator(new ExportedService("ServiceOneService", "ServiceOne", TestServiceOne.class,
            ServiceMetadata.AUTO_EXPORT_DISABLED, 0, null, null, null)));

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
