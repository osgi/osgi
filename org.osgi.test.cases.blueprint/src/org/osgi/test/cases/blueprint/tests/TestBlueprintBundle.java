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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.reflect.BeanMetadata;

import org.osgi.test.cases.blueprint.components.comp1.SimpleTestComponent;
import org.osgi.test.cases.blueprint.components.comp1.AltSimpleTestComponent;
import org.osgi.test.cases.blueprint.components.factory.SimpleInstanceFactory;
import org.osgi.test.cases.blueprint.components.staticfactory.SimpleStaticFactory;
import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains tests related with the framework class loading policies.
 *
 * @author left
 * @version $Revision$
 */
public class TestBlueprintBundle extends DefaultTestBundleControl {
    /**
     * Tests a simple managed bundle with a single component, no explicit header specified.
     */
    public void testStartComponentDefault() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1_no_header.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        // make sure the name is in the component list
        startEvents.addValidator(new ComponentNamePresenceValidator("comp1"));
        startEvents.addValidator(new GetBeanMetadataValidator("comp1"));

        // and the validate the component metadata
        startEvents.addValidator(new ComponentMetadataValidator(
                new BeanComponent("comp1", SimpleTestComponent.class, new TestArgument[0], null)));

        controller.run();
    }

    /**
     * Single control file, referenced by an explicit header using "*.xml" in the path.
     */
    public void testStartComponentWildcard() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1_wildcard_header.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        controller.run();

    }

    /**
     * Tests no explicit header specified, multiple control files in the OSGI-INF directory,
     * each configuring a component
     */
    public void testStartComponentMultiple() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1a_no_header.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp2", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        startEvents.validateComponent("comp2", SimpleTestComponent.class);

        // validate both names too
        startEvents.addValidator(new ComponentNamePresenceValidator("comp1"));
        startEvents.addValidator(new ComponentNamePresenceValidator("comp2"));

        // and the meta data for both components
        startEvents.addValidator(new ComponentMetadataValidator(
                new BeanComponent("comp1", SimpleTestComponent.class, new TestArgument[] {
            new StringArgument("comp1")} , null)));

        startEvents.addValidator(new ComponentMetadataValidator(
                new BeanComponent("comp2", AltSimpleTestComponent.class, new TestArgument[] {
            new StringArgument("comp2")} , null)));
        controller.run();
    }


    /**
     * Tests an explicitly specified config file on the header with more than two files
     * in the OSGI-INF/blueprint directory.  Only one file should be picked up
     */
    public void testStartComponentExplicit() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1_explicit_config.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        // if we see comp2 instantiated, this is an error
        startEvents.addFailureEvent(new ComponentAssertion("comp2", AssertionService.COMPONENT_CREATED));
        controller.run();
    }


    /**
     * Tests two explicitly specified config files on the header with attributes or directives
     * included in the syntax.  The attributes should be ignored.
     */
    public void testStartComponentAttributes() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1_attributes.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp2", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        startEvents.validateComponent("comp2", SimpleTestComponent.class);
        controller.run();
    }


    /**
     * Tests two explicitly specified config files on the header with attributes or directives
     * included in the syntax.  The multiple paths are part of the same syntactic unit as the
     * directives and attributes rather than being separated by commans.  Both files should be processed
     * and the attributes ignored. .
     */
    public void testStartComponentAttributes2() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1_attributes2.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp2", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        startEvents.validateComponent("comp2", SimpleTestComponent.class);
        controller.run();
    }


    /**
     * Tests an explicitly specified config file on the header located in a directory other
     * than OSGI-INF/blueprint directory.  Only one file should be picked up
     */
    public void testStartComponentDifferentDir() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1_different_dir.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        // if we see comp2 instantiated, this is an error
        startEvents.addFailureEvent(new ComponentAssertion("comp2", AssertionService.COMPONENT_CREATED));
        controller.run();
    }


    /**
     * Tests an explicitly specified config file on the header located in a directory other
     * than OSGI-INF/blueprint directory.  Only the directory name is specified.
     */
    public void testStartComponentDirOnly() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1_dir_only.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        // if we see comp2 instantiated, this is an error
        startEvents.addFailureEvent(new ComponentAssertion("comp2", AssertionService.COMPONENT_CREATED));
        controller.run();
    }


    /**
     * Tests a simple managed bundle with a single component, no component id specified.
     */
    public void testNoNameDefault() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1_no_name.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);

        // and the validate the component metadata.  The wildcard indicates
        // we're looking for the nearest match
        startEvents.addValidator(new ComponentMetadataValidator(
                new BeanComponent("*", SimpleTestComponent.class, new TestArgument[] {
            new StringArgument("comp1")} , null)));

        // validate that the correct anonymous compoents were created
        startEvents.addValidator(new AnonymousComponentValidator(BeanMetadata.class));

        controller.run();
    }


    /**
     * Tests a bundle with a number of different init/destroy combinations
     */
    public void testInitDestroy() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1_init_destroy.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // this creates several components with different init/destroy combos.
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_INIT_METHOD);
        startEvents.addAssertion("comp2", AssertionService.COMPONENT_CREATED);
        startEvents.addFailureEvent(new ComponentAssertion("comp2", AssertionService.COMPONENT_INIT_METHOD));
        startEvents.addAssertion("comp3", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp3", AssertionService.COMPONENT_INIT_METHOD);

        // validate the metadata for all components
        startEvents.addValidator(new ComponentMetadataValidator(
                new BeanComponent("comp1", SimpleTestComponent.class, "init", null,
                new TestArgument[] { new StringArgument("comp1")} , null)));

        startEvents.addValidator(new ComponentMetadataValidator(
                new BeanComponent("comp2", SimpleTestComponent.class, null, "destroy",
                new TestArgument[] { new StringArgument("comp2")} , null)));

        startEvents.addValidator(new ComponentMetadataValidator(
                new BeanComponent("comp3", SimpleTestComponent.class, "init", "destroy",
                new TestArgument[] { new StringArgument("comp3")} , null)));

        // stop events occur at the end
        EventSet stopEvents = controller.getStopEvents();
        stopEvents.addFailureEvent(new ComponentAssertion("comp1", AssertionService.COMPONENT_DESTROY_METHOD));
        stopEvents.addAssertion("comp2", AssertionService.COMPONENT_DESTROY_METHOD);
        stopEvents.addAssertion("comp3", AssertionService.COMPONENT_DESTROY_METHOD);
        controller.run();
    }


    /**
     * Tests a static factory with different name/id combinations.
     */
    public void testStaticFactory() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/static_factory.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // check all the component creation events
        startEvents.addAssertion("static-comp1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp2_id", AssertionService.COMPONENT_CREATED);

        BeanComponent comp2_id =
                new BeanComponent("comp2_id", SimpleStaticFactory.class, "createSimple",
                new TestArgument[] { new StringArgument("comp2_id")} , null);

        startEvents.addValidator(new ComponentMetadataValidator(comp2_id));

        BeanComponent comp1 =
                new BeanComponent("comp1", SimpleStaticFactory.class, "createSimple",
                new TestArgument[0], null);

        // validate the metadata for all components
        startEvents.addValidator(new ComponentMetadataValidator(comp1));

        // make sure this was created with the correct class
        // NOTE:  the "comp1" is the XML file component id.  "static-comp1" is an internal id created by the factory
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        startEvents.validateComponent("comp2_id", SimpleTestComponent.class);
        controller.run();
    }


    /**
     * Tests an instance factory derived from a component of the same ModuleContext.
     */
    public void testComponentFactory() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/component_instance_factory.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // check all the component creation events
        startEvents.addAssertion("instance-comp1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp2_id", AssertionService.COMPONENT_CREATED);

        BeanComponent comp1 =
                new BeanComponent("comp1", "createSimple",
                new TestArgument[0], null);
        comp1.setFactoryComponent(new TestRefValue("compFactory"));

        // validate the metadata for all components
        startEvents.addValidator(new ComponentMetadataValidator(comp1));

        BeanComponent comp2_id =
                new BeanComponent("comp2_id", "createSimple",
                new TestArgument[] { new StringArgument("comp2_id")} , null);
        comp2_id.setFactoryComponent(new TestRefValue("compFactory"));

        startEvents.addValidator(new ComponentMetadataValidator(comp2_id));

        // make sure this was created with the correct class
        // NOTE:  the "comp1" is the XML file component id.  "instance-comp1" is an internal id created by the factory
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        startEvents.validateComponent("comp2_id", SimpleTestComponent.class);
        controller.run();
    }


    /**
     * Tests an instance factory derived from a service refeference.
     */
    public void testServiceFactory() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/service_instance_factory.jar");

        TestService factoryService = new TestService(getContext(), new SimpleInstanceFactory(), "org.osgi.test.cases.blueprint.components.factory.SimpleInstanceFactory", null);
        MetadataEventSet startEvents = controller.getStartEvents();
        // this gets our service started
        startEvents.addInitializer(factoryService);
        // check all the component creation events
        startEvents.addAssertion("instance-comp1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp2_id", AssertionService.COMPONENT_CREATED);

        BeanComponent comp1 =
                new BeanComponent("comp1", "createSimple",
                new TestArgument[0], null);
        comp1.setFactoryComponent(new TestRefValue("compFactory"));

        // validate the metadata for all components
        startEvents.addValidator(new ComponentMetadataValidator(comp1));

        BeanComponent comp2_id =
                new BeanComponent("comp2_id", "createSimple",
                new TestArgument[] { new StringArgument("comp2_id")} , null);
        comp2_id.setFactoryComponent(new TestRefValue("compFactory"));

        // make sure this was created with the correct class
        // NOTE:  the "comp1" is the XML file component id.  "instance-comp1" is an internal id created by the factory
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        startEvents.validateComponent("comp2_id", SimpleTestComponent.class);
        // stop events occur at the end phase
        EventSet stopEvents = controller.getStopEvents();
        // this deregisters the service on completion.
        stopEvents.addTerminator(factoryService);
        controller.run();
    }


    /**
     * Tests injection of the ModuleContext into a component
     */
    public void testModuleContextAware() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/module_context_aware.jar");

        MetadataEventSet startEvents = controller.getStartEvents();
        // check all the component creation events
        startEvents.addAssertion("ContextAware", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("ContextAware", AssertionService.MODULE_CONTEXT_INJECTED);
        startEvents.addAssertion("ContextAware", AssertionService.COMPONENT_INIT_METHOD);

        // This is a bonus part of the test.  Check that the default component exist and are of the
        // correct type.
        startEvents.addValidator(new ComponentTypeValidator("bundle", Bundle.class));
        startEvents.addValidator(new ComponentTypeValidator("bundleContext", BundleContext.class));
        startEvents.addValidator(new ComponentTypeValidator("blueprintContainer", BlueprintContainer.class));
        startEvents.addValidator(new ComponentTypeValidator("blueprintConverter", Converter.class));

        // and finally, these should be listed in the set of component names
        startEvents.addValidator(new ComponentNamePresenceValidator("bundle"));
        startEvents.addValidator(new ComponentNamePresenceValidator("bundleContext"));
        startEvents.addValidator(new ComponentNamePresenceValidator("blueprintContainer"));
        startEvents.addValidator(new ComponentNamePresenceValidator("conversionService"));

        // now verify that these have associated metadata
        startEvents.addValidator(new ComponentMetadataPresenceValidator("bundle", BeanMetadata.class));
        startEvents.addValidator(new ComponentMetadataPresenceValidator("bundleContext", BeanMetadata.class));
        startEvents.addValidator(new ComponentMetadataPresenceValidator("blueprintContainer", BeanMetadata.class));
        startEvents.addValidator(new ComponentMetadataPresenceValidator("conversionService", BeanMetadata.class));

        // if we receive the above events and no assertion failures, then everything has worked.
        controller.run();
    }


    /**
     * Tests that component ids are case insensitive
     */
    public void testComponentIdCase() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1_id.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // all lowercase version
        startEvents.addAssertion("comp1_id", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1_id", SimpleTestComponent.class);
        // mixed case version
        startEvents.addAssertion("Comp1_ID", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("Comp1_ID", AltSimpleTestComponent.class);
        // make sure the name is in the component list
        startEvents.addValidator(new ComponentNamePresenceValidator("comp1_id"));
        startEvents.addValidator(new GetBeanMetadataValidator("comp1_id"));
        // validate both versions
        startEvents.addValidator(new ComponentNamePresenceValidator("Comp1_ID"));
        startEvents.addValidator(new GetBeanMetadataValidator("Comp1_ID"));

        // and the validate the component metadata
        startEvents.addValidator(new ComponentMetadataValidator(
                new BeanComponent("comp1_id", SimpleTestComponent.class,
                new TestArgument[] {new StringArgument("comp1_id")}, null)));

        startEvents.addValidator(new ComponentMetadataValidator(
                new BeanComponent("Comp1_ID", SimpleTestComponent.class,
                new TestArgument[] {new StringArgument("Comp1_ID")}, null)));

        controller.run();
    }
}