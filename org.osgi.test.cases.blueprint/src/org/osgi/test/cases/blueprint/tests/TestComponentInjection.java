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
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.test.cases.blueprint.components.injection.ComponentInjection;
import org.osgi.test.cases.blueprint.components.injection.PrototypeComponentInjection;
import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class contains tests related with the framework class loading policies.
 *
 * @author left
 * @version $Revision$
 */
public class TestComponentInjection extends DefaultTestBundleControl {
	/*
	 * Tests different variations of component injection.
	 */
	public void testReferenceInjection() throws Exception {
        ThreePhaseTestController controller = new ThreePhaseTestController(getContext(),
            getWebServer()+"www/component_reference_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // first creation of a few leaf components
        startEvents.addAssertion("leaf1", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("leaf1", ComponentInjection.class);
        // version using a default lazy-init override
        startEvents.addAssertion("leaf1a", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("leaf1a", ComponentInjection.class);
        startEvents.addAssertion("leaf2", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("leaf2", ComponentInjection.class);
        startEvents.addAssertion("leaf3", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("leaf3", ComponentInjection.class);
        startEvents.addAssertion("leaf4", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("leaf4", ComponentInjection.class);
        startEvents.addAssertion("lazyleaf1", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("lazyleaf1", ComponentInjection.class);

        // we validate metadata for a few select components

        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("lazyleaf1", ComponentInjection.class, null, "init", "destroy",
            new TestArgument[] { new StringArgument("lazyleaf1") } , null,
            null, true, null)));

        // first real test.  This will cause lazyleaf1 to get instantiated in order to be injected.  That's the real test.
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1", ComponentInjection.class);
        startEvents.addValidator(new ArgumentMetadataValidator("comp1", new TestArgument[] {
            new StringArgument("comp1"),
            new ReferenceArgument("lazyleaf1")
        }));

        // second version of this using the inverse default
        startEvents.addAssertion("lazyleaf1a", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("lazyleaf1a", ComponentInjection.class);
        // first real test.  This will cause lazyleaf1a to get instantiated in order to be injected.  That's the real test.
        startEvents.addAssertion("comp1a", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1a", ComponentInjection.class);
        startEvents.addValidator(new ArgumentMetadataValidator("comp1a", new TestArgument[] {
            new StringArgument("comp1a"),
            new ReferenceArgument("lazyleaf1a")
        }));

        // the following components get instantiated in the middle phase
        startEvents.addFailureEvent(new ComponentAssertion("lazyleaf2", AssertionService.COMPONENT_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("lazyleaf3", AssertionService.COMPONENT_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("lazycomp1", AssertionService.COMPONENT_CREATED));

        startEvents.addFailureEvent(new ComponentAssertion("lazyleaf2a", AssertionService.COMPONENT_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("lazyleaf3a", AssertionService.COMPONENT_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("lazycomp1a", AssertionService.COMPONENT_CREATED));

        // now some combinations.  Most of the validation work is performed in the components, but we'll
        // check some metadata here.

        // component referencing two separate leaf nodes
        startEvents.addAssertion("twoleaf", AssertionService.COMPONENT_CREATED);
        startEvents.addValidator(new ArgumentMetadataValidator("twoleaf", new TestArgument[] {
            new StringArgument("twoleaf"),
            new ReferenceArgument("leaf1")
        }));
        startEvents.addValidator(new PropertyMetadataValidator("twoleaf", new TestProperty[] {
            new ReferenceProperty("componentOne", "leaf2")
        }));

        // a more complex dependency graph
        startEvents.addAssertion("twocomp", AssertionService.COMPONENT_CREATED);
        startEvents.addValidator(new ArgumentMetadataValidator("twocomp", new TestArgument[] {
            new StringArgument("twocomp"),
            new ReferenceArgument("twoleaf"),
            new ReferenceArgument("comp1"),
        }));

        // different collection classes
        startEvents.addAssertion("twolist", AssertionService.COMPONENT_CREATED);
        startEvents.addFailureEvent(new ComponentAssertion("twolist", AssertionService.COMPONENT_CREATED));
        // the property is a list, which references two components
        startEvents.addValidator(new PropertyMetadataValidator("twolist", new TestProperty[] {
            new TestProperty(new TestListValue(new TestValue[] {
                new TestRefValue("twoleaf"),
                new TestRefValue("twocomp"),
            }),"componentList")
        }));

        startEvents.addAssertion("twoset", AssertionService.COMPONENT_CREATED);
        // the property is a set, which references two components
        startEvents.addValidator(new PropertyMetadataValidator("twoset", new TestProperty[] {
            new TestProperty( new TestSetValue(new TestValue[] {
                new TestRefValue("twoleaf"),
                new TestRefValue("twocomp"),
            }),"componentSet")
        }));

        startEvents.addAssertion("mapref", AssertionService.COMPONENT_CREATED);
        // the property is a set, which references two components
        startEvents.addValidator(new PropertyMetadataValidator("mapref", new TestProperty[] {
            new TestProperty(new TestMapValue(new MapValueEntry[] {
                new MapValueEntry(new TestRefValue("leaf1"), new TestRefValue("twoleaf")),
                new MapValueEntry(new TestRefValue("leaf2"), new TestRefValue("twocomp")),
            }),"componentMap")
        }));

        // now some dependson tests.  The dependency ordering tests are done in the module
        startEvents.addAssertion("dependsleaf1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("dependsOnOne", AssertionService.COMPONENT_CREATED);

        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("dependsOnOne", ComponentInjection.class, null, "init", "destroy",
            new TestArgument[] { new StringArgument("dependsOnOne") } , null,
            new String[] { "dependsleaf1" }, false, null)));

        startEvents.addAssertion("dependsleaf2", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("dependsleaf3", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("dependsOnTwo", AssertionService.COMPONENT_CREATED);

        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("dependsOnTwo", ComponentInjection.class, null, "init", "destroy",
            new TestArgument[] { new StringArgument("dependsOnTwo") } , null,
            new String[] { "dependsleaf2", "dependsleaf3" }, false, null)));

        // validate the depends on metadata
        startEvents.addValidator(new ComponentDependencyValidator("dependsOnOne", new String[] { "dependsleaf1" }));
        startEvents.addValidator(new ComponentDependencyValidator("dependsOnTwo", new String[] { "dependsleaf2", "dependsleaf3" }));

        // now doing prototype/singleton tests

        // creation events for each of our singletons.
        startEvents.addAssertion("singleton1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("singleton2", AssertionService.COMPONENT_CREATED);
        // this is the first with an explicit scope specified, so validate it
        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("singleton2", ComponentInjection.class, null, "init", "destroy",
            new TestArgument[] { new StringArgument("singleton2") } , null,
            null, false, BeanMetadata.SCOPE_SINGLETON)));

        // now add a failure event for each of these.  This will catch multiple creations.  The
        // first create event will be processed, and any additional ones will be flagged as a failure
        startEvents.addFailureEvent(new ComponentAssertion("singleton1", AssertionService.COMPONENT_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("singleton2", AssertionService.COMPONENT_CREATED));

        // we expect to see 6 (and only 6) instantiations of the prototype components
        startEvents.addAssertion("prototype1", AssertionService.COMPONENT_CREATED);

        // this is the first with a prototype scope
        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("prototype1", PrototypeComponentInjection.class, null, "init", "destroy",
            new TestArgument[] { new StringArgument("prototype1") } , null,
            null, false, BeanMetadata.SCOPE_PROTOTYPE)));

        startEvents.addAssertion("prototype1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("prototype1", AssertionService.COMPONENT_CREATED);
        // only 4 of these also
        startEvents.addAssertion("prototype4", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("prototype4", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("prototype4", AssertionService.COMPONENT_CREATED);

        startEvents.addAssertion("prototype2", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("prototype2", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("prototype2", AssertionService.COMPONENT_CREATED);

        // make sure this is capped
        startEvents.addFailureEvent(new ComponentAssertion("prototype1", AssertionService.COMPONENT_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("prototype2", AssertionService.COMPONENT_CREATED));
        // this one will be explicitly requested in the middle phase.  We should see nothing here
        startEvents.addFailureEvent(new ComponentAssertion("prototype3", AssertionService.COMPONENT_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("prototype4", AssertionService.COMPONENT_CREATED));


        // ok, now we'll request some components via the metadata and see what we get.
        MetadataEventSet middleEvents = controller.getMiddleEvents();
        // this one was not initialized originall
        middleEvents.addInitializer(new LazyComponentStarter("lazyleaf2"));
        middleEvents.addAssertion("lazyleaf2", AssertionService.COMPONENT_CREATED);
        middleEvents.addInitializer(new LazyComponentStarter("lazyleaf2a"));
        middleEvents.addAssertion("lazyleaf2a", AssertionService.COMPONENT_CREATED);
        // this one was created.  Ensure it don't see it created again
        middleEvents.addInitializer(new LazyComponentStarter("lazyleaf1"));
        middleEvents.addFailureEvent(new ComponentAssertion("lazyleaf1", AssertionService.COMPONENT_CREATED));
        middleEvents.addInitializer(new LazyComponentStarter("lazyleaf1a"));
        middleEvents.addFailureEvent(new ComponentAssertion("lazyleaf1a", AssertionService.COMPONENT_CREATED));
        // this will cascade to another lazy-init omponent
        middleEvents.addInitializer(new LazyComponentStarter("lazycomp1"));
        middleEvents.addAssertion("lazycomp1", AssertionService.COMPONENT_CREATED);
        middleEvents.addAssertion("lazyleaf3", AssertionService.COMPONENT_CREATED);

        middleEvents.addInitializer(new LazyComponentStarter("lazycomp1a"));
        middleEvents.addAssertion("lazycomp1a", AssertionService.COMPONENT_CREATED);
        middleEvents.addAssertion("lazyleaf3a", AssertionService.COMPONENT_CREATED);

        // we'll request this twice.  Each time should create a component
        middleEvents.addInitializer(new LazyComponentStarter("prototype3"));
        middleEvents.addInitializer(new LazyComponentStarter("prototype3"));

        middleEvents.addAssertion("prototype3", AssertionService.COMPONENT_CREATED);
        middleEvents.addAssertion("prototype3", AssertionService.COMPONENT_CREATED);
        // cap this creation
        middleEvents.addFailureEvent(new ComponentAssertion("prototype3", AssertionService.COMPONENT_CREATED));

        // now request some of the singletons (including the one declared with bundle scope).
        middleEvents.addInitializer(new LazyComponentStarter("singleton1"));
        middleEvents.addFailureEvent(new ComponentAssertion("singleton1", AssertionService.COMPONENT_CREATED));

        // stop events should proceed as normal, but verify that none of the prototype events get destroy-methods called, and
        // add a couple checks that the
        EventSet stopEvents = controller.getStopEvents();

        // our singletons should be destroyed
        stopEvents.addAssertion("singleton1", AssertionService.COMPONENT_DESTROY_METHOD);
        stopEvents.addAssertion("singleton2", AssertionService.COMPONENT_DESTROY_METHOD);

        // but we should not see destroy events for the prototypes
        stopEvents.addFailureEvent(new ComponentAssertion("prototype1", AssertionService.COMPONENT_DESTROY_METHOD));
        stopEvents.addFailureEvent(new ComponentAssertion("prototype2", AssertionService.COMPONENT_DESTROY_METHOD));
        stopEvents.addFailureEvent(new ComponentAssertion("prototype3", AssertionService.COMPONENT_DESTROY_METHOD));

        controller.run();
    }


	/*
	 * the override of the default components with user defined components.
	 */
	public void testDefaultComponentOverride() throws Exception {
        ThreePhaseTestController controller = new ThreePhaseTestController(getContext(),
            getWebServer()+"www/default_component_override.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // first creation of the override components
        startEvents.addAssertion("blueprintBundle", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("blueprintBundleContext", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("blueprintContainer", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("blueprintConverter", AssertionService.COMPONENT_CREATED);

        // and the injection targets
        startEvents.addAssertion("bundleUser", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("bundleContextUser", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("blueprintContainerUser", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("converterUser", AssertionService.COMPONENT_CREATED);

        // these should all be our local components
        startEvents.addValidator(new ComponentTypeValidator("blueprintBundle", ComponentInjection.class));
        startEvents.addValidator(new ComponentTypeValidator("blueprintBundleContext", ComponentInjection.class));
        startEvents.addValidator(new ComponentTypeValidator("blueprintContainer", ComponentInjection.class));
        startEvents.addValidator(new ComponentTypeValidator("blueprintConverter", ComponentInjection.class));

        controller.run();
    }
}
