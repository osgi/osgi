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

import org.osgi.test.cases.blueprint.components.comp1.SimpleTestComponent;
import org.osgi.test.cases.blueprint.components.factory.SimpleInstanceFactory;
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
	/*
	 * Tests a simple managed bundle with a single component, no explicit header specified.
	 */
	public void testStartComponentDefault() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/comp1_no_header.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1", SimpleTestComponent.class);

        // and the validate the component metadata
        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("comp1", SimpleTestComponent.class, new TestParameter[0], null)));

        controller.run();
    }

	/*
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
	/*
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

        // and the meta data for both components
        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("comp1", SimpleTestComponent.class, new TestParameter[] {
            new StringParameter("comp1") } , null)));

        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("comp2", SimpleTestComponent.class, new TestParameter[] {
            new StringParameter("comp2") } , null)));
        controller.run();
    }

	/*
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

	/*
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

	/*
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

	/*
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

	/*
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
            new LocalComponent("*", SimpleTestComponent.class, new TestParameter[] {
            new StringParameter("comp1") } , null)));

        controller.run();
    }

	/*
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
            new LocalComponent("comp1", SimpleTestComponent.class, "init", null,
            new TestParameter[] { new StringParameter("comp1") } , null)));

        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("comp2", SimpleTestComponent.class, null, "destroy",
            new TestParameter[] { new StringParameter("comp2") } , null)));

        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("comp3", SimpleTestComponent.class, "init", "destroy",
            new TestParameter[] { new StringParameter("comp3") } , null)));

        // stop events occur at the end
        EventSet stopEvents = controller.getStopEvents();
        stopEvents.addFailureEvent(new ComponentAssertion("comp1", AssertionService.COMPONENT_DESTROY_METHOD));
        stopEvents.addAssertion("comp2", AssertionService.COMPONENT_DESTROY_METHOD);
        stopEvents.addAssertion("comp3", AssertionService.COMPONENT_DESTROY_METHOD);
        controller.run();
    }

	/*
	 * Tests a bundle with a default init method and various component overrides.
	 */
	public void testDefaultInitDestroy() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/comp1_default_init_destroy.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // tests with a default init specified
        // accept default
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_INIT_METHOD);
        // redirect default to a different method.  Will assert a failure if the
        // override doesn't take.
        startEvents.addAssertion("comp1a", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp1a", AssertionService.COMPONENT_INIT_METHOD);
        // this overrides the default entirely.  This is a failure if we get
        // init called.
        startEvents.addAssertion("comp1b", AssertionService.COMPONENT_CREATED);
        startEvents.addFailureEvent(new ComponentAssertion("comp1b", AssertionService.COMPONENT_INIT_METHOD));
        // init override accepted, explicit destroy specified
        startEvents.addAssertion("comp1c", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp1c", AssertionService.COMPONENT_INIT_METHOD);
        // Does not implement the default method.  Should still get created without error.
        startEvents.addAssertion("comp1d", AssertionService.COMPONENT_CREATED);

        // tests with default destroy specified
        // accept default.
        startEvents.addAssertion("comp2", AssertionService.COMPONENT_CREATED);
        startEvents.addFailureEvent(new ComponentAssertion("comp2", AssertionService.COMPONENT_INIT_METHOD));
        // override default destroy
        startEvents.addAssertion("comp2a", AssertionService.COMPONENT_CREATED);
        startEvents.addFailureEvent(new ComponentAssertion("comp2a", AssertionService.COMPONENT_INIT_METHOD));
        // explicitly disable default destroy
        startEvents.addAssertion("comp2b", AssertionService.COMPONENT_CREATED);
        startEvents.addFailureEvent(new ComponentAssertion("comp2b", AssertionService.COMPONENT_INIT_METHOD));
        // explicit init specified
        startEvents.addAssertion("comp2c", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp2c", AssertionService.COMPONENT_INIT_METHOD);
        // does not implement the init method
        startEvents.addAssertion("comp2d", AssertionService.COMPONENT_CREATED);

        // tests with both default init and destroy specified.
        // accept default
        startEvents.addAssertion("comp3", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp3", AssertionService.COMPONENT_INIT_METHOD);
        // init method disabled, destroy method overridden to another.  Will assert
        // a failure if either override doesn't work.
        startEvents.addAssertion("comp3a", AssertionService.COMPONENT_CREATED);
        // same as above, but init and destroy reversed.
        startEvents.addAssertion("comp3b", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp3b", AssertionService.COMPONENT_INIT_METHOD);
        // class that does not implement either default method.
        startEvents.addAssertion("comp3c", AssertionService.COMPONENT_CREATED);

        // stop events occur at the end phase
        EventSet stopEvents = controller.getStopEvents();
        // tests with a default init specified.  For most of these, destroy getting called
        // is an error.
        stopEvents.addFailureEvent(new ComponentAssertion("comp1", AssertionService.COMPONENT_DESTROY_METHOD));
        stopEvents.addFailureEvent(new ComponentAssertion("comp1a", AssertionService.COMPONENT_DESTROY_METHOD));
        stopEvents.addFailureEvent(new ComponentAssertion("comp1b", AssertionService.COMPONENT_DESTROY_METHOD));
        // we expect destroy for this one.
        stopEvents.addAssertion("comp1c", AssertionService.COMPONENT_DESTROY_METHOD);

        // tests with a default destroy
        // accept default
        stopEvents.addAssertion("comp2", AssertionService.COMPONENT_DESTROY_METHOD);
        // override default.  We'll get an assertion failure of the
        // default version is called.
        stopEvents.addAssertion("comp2a", AssertionService.COMPONENT_DESTROY_METHOD);
        // disabled the calling of the destroy method
        stopEvents.addFailureEvent(new ComponentAssertion("comp2b", AssertionService.COMPONENT_DESTROY_METHOD));
        // explicit init method specified
        stopEvents.addAssertion("comp2c", AssertionService.COMPONENT_DESTROY_METHOD);

        // tests with both default init and destroy specified.
        // accept default
        stopEvents.addAssertion("comp3", AssertionService.COMPONENT_DESTROY_METHOD);
        // override default.  We'll get an assertion failure of the
        // default version is called.
        stopEvents.addAssertion("comp3a", AssertionService.COMPONENT_DESTROY_METHOD);
        // disabled the calling of the destroy method
        stopEvents.addFailureEvent(new ComponentAssertion("comp3b", AssertionService.COMPONENT_DESTROY_METHOD));
        // class that doesn't implement the default.  No event raised, but shouldn't cause an errors.

        controller.run();
    }

	/*
	 * Tests a static factory with different name/id combinations.
	 */
	public void testStaticFactory() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/static_factory.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // check all the component creation events
        startEvents.addAssertion("static-comp1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp2_id", AssertionService.COMPONENT_CREATED);

        LocalComponent comp1 =
            new LocalComponent("comp1", SimpleTestComponent.class,
            new TestParameter[0], null);
        comp1.setFactoryMethod("createSimple");

        // validate the metadata for all components
        startEvents.addValidator(new ComponentMetadataValidator(comp1));

        LocalComponent comp2_id =
            new LocalComponent("comp2_id", SimpleTestComponent.class,
            new TestParameter[] { new StringParameter("comp2_id") } , null);
        comp2_id.setFactoryMethod("createSimple");

        startEvents.addValidator(new ComponentMetadataValidator(comp2_id));

        // make sure this was created with the correct class
        // NOTE:  the "comp1" is the XML file component id.  "static-comp1" is an internal id created by the factory
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        startEvents.validateComponent("comp2_id", SimpleTestComponent.class);
        controller.run();
    }

	/*
	 * Tests an instance factory derived from a component of the same ModuleContext.
	 */
	public void testComponentFactory() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/component_instance_factory.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // check all the component creation events
        startEvents.addAssertion("instance-comp1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("comp2_id", AssertionService.COMPONENT_CREATED);

        LocalComponent comp1 =
            new LocalComponent("comp1", null,
            new TestParameter[0], null);
        comp1.setFactoryMethod("createSimple");
        comp1.setFactoryComponent(new TestReferenceValue("compFactory"));

        // validate the metadata for all components
        startEvents.addValidator(new ComponentMetadataValidator(comp1));

        LocalComponent comp2_id =
            new LocalComponent("comp2_id", null,
            new TestParameter[] { new StringParameter("comp2_id") } , null);
        comp2_id.setFactoryMethod("createSimple");
        comp2_id.setFactoryComponent(new TestReferenceValue("compFactory"));

        startEvents.addValidator(new ComponentMetadataValidator(comp2_id));

        // make sure this was created with the correct class
        // NOTE:  the "comp1" is the XML file component id.  "instance-comp1" is an internal id created by the factory
        startEvents.validateComponent("comp1", SimpleTestComponent.class);
        startEvents.validateComponent("comp2_id", SimpleTestComponent.class);
        controller.run();
    }

	/*
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

        LocalComponent comp1 =
            new LocalComponent("comp1", null,
            new TestParameter[0], null);
        comp1.setFactoryMethod("createSimple");
        comp1.setFactoryComponent(new TestReferenceValue("compFactory"));

        // validate the metadata for all components
        startEvents.addValidator(new ComponentMetadataValidator(comp1));

        LocalComponent comp2_id =
            new LocalComponent("comp2_id", null,
            new TestParameter[] { new StringParameter("comp2_id") } , null);
        comp2_id.setFactoryMethod("createSimple");
        comp2_id.setFactoryComponent(new TestReferenceValue("compFactory"));

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

	/*
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
        // if we receive the above events and no assertion failures, then everything has worked.
        controller.run();
    }
}