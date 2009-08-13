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
        startEvents.addAssertion("leaf1", AssertionService.BEAN_CREATED);
        startEvents.validateComponent("leaf1", ComponentInjection.class);
        // version using a default lazy-init override
        startEvents.addAssertion("leaf1a", AssertionService.BEAN_CREATED);
        startEvents.validateComponent("leaf1a", ComponentInjection.class);
        startEvents.addAssertion("leaf2", AssertionService.BEAN_CREATED);
        startEvents.validateComponent("leaf2", ComponentInjection.class);
        startEvents.addAssertion("leaf3", AssertionService.BEAN_CREATED);
        startEvents.validateComponent("leaf3", ComponentInjection.class);
        startEvents.addAssertion("leaf4", AssertionService.BEAN_CREATED);
        startEvents.validateComponent("leaf4", ComponentInjection.class);
        startEvents.addAssertion("lazyleaf1", AssertionService.BEAN_CREATED);
        startEvents.validateComponent("lazyleaf1", ComponentInjection.class);

        // we validate metadata for a few select components

        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("lazyleaf1", ComponentInjection.class, null, "init", "destroy",
            new TestArgument[] { new StringArgument("lazyleaf1") } , null,
            null, BeanMetadata.ACTIVATION_LAZY, null)));

        // first real test.  This will cause lazyleaf1 to get instantiated in order to be injected.  That's the real test.
        startEvents.addAssertion("comp1", AssertionService.BEAN_CREATED);
        startEvents.validateComponent("comp1", ComponentInjection.class);
        startEvents.addValidator(new ArgumentMetadataValidator("comp1", new TestArgument[] {
            new StringArgument("comp1"),
            new ReferenceArgument("lazyleaf1")
        }));

        // second version of this using the inverse default
        startEvents.addAssertion("lazyleaf1a", AssertionService.BEAN_CREATED);
        startEvents.validateComponent("lazyleaf1a", ComponentInjection.class);
        // first real test.  This will cause lazyleaf1a to get instantiated in order to be injected.  That's the real test.
        startEvents.addAssertion("comp1a", AssertionService.BEAN_CREATED);
        startEvents.validateComponent("comp1a", ComponentInjection.class);
        startEvents.addValidator(new ArgumentMetadataValidator("comp1a", new TestArgument[] {
            new StringArgument("comp1a"),
            new ReferenceArgument("lazyleaf1a")
        }));

        // the following components get instantiated in the middle phase
        startEvents.addFailureEvent(new ComponentAssertion("lazyleaf2", AssertionService.BEAN_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("lazyleaf3", AssertionService.BEAN_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("lazycomp1", AssertionService.BEAN_CREATED));

        startEvents.addFailureEvent(new ComponentAssertion("lazyleaf2a", AssertionService.BEAN_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("lazyleaf3a", AssertionService.BEAN_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("lazycomp1a", AssertionService.BEAN_CREATED));

        // now some combinations.  Most of the validation work is performed in the components, but we'll
        // check some metadata here.

        // component referencing two separate leaf nodes
        startEvents.addAssertion("twoleaf", AssertionService.BEAN_CREATED);
        startEvents.addValidator(new ArgumentMetadataValidator("twoleaf", new TestArgument[] {
            new StringArgument("twoleaf"),
            new ReferenceArgument("leaf1")
        }));
        startEvents.addValidator(new PropertyMetadataValidator("twoleaf", new TestProperty[] {
            new ReferenceProperty("componentOne", "leaf2")
        }));

        // a more complex dependency graph
        startEvents.addAssertion("twocomp", AssertionService.BEAN_CREATED);
        startEvents.addValidator(new ArgumentMetadataValidator("twocomp", new TestArgument[] {
            new StringArgument("twocomp"),
            new ReferenceArgument("twoleaf"),
            new ReferenceArgument("comp1"),
        }));

        // different collection classes
        startEvents.addAssertion("twolist", AssertionService.BEAN_CREATED);
        startEvents.addFailureEvent(new ComponentAssertion("twolist", AssertionService.BEAN_CREATED));
        // the property is a list, which references two components
        startEvents.addValidator(new PropertyMetadataValidator("twolist", new TestProperty[] {
            new TestProperty(new TestListValue(new TestValue[] {
                new TestRefValue("twoleaf"),
                new TestRefValue("twocomp"),
            }),"componentList")
        }));

        startEvents.addAssertion("twoset", AssertionService.BEAN_CREATED);
        // the property is a set, which references two components
        startEvents.addValidator(new PropertyMetadataValidator("twoset", new TestProperty[] {
            new TestProperty( new TestSetValue(new TestValue[] {
                new TestRefValue("twoleaf"),
                new TestRefValue("twocomp"),
            }),"componentSet")
        }));

        startEvents.addAssertion("mapref", AssertionService.BEAN_CREATED);
        // the property is a set, which references two components
        startEvents.addValidator(new PropertyMetadataValidator("mapref", new TestProperty[] {
            new TestProperty(new TestMapValue(new MapValueEntry[] {
                new MapValueEntry(new TestRefValue("leaf1"), new TestRefValue("twoleaf")),
                new MapValueEntry(new TestRefValue("leaf2"), new TestRefValue("twocomp")),
            }),"componentMap")
        }));

        // now some dependson tests.  The dependency ordering tests are done in the module,
        // but we can also validate using the event ordering
        TestEvent dependsleaf1 = new ComponentAssertion("dependsleaf1", AssertionService.BEAN_CREATED);
        startEvents.addEvent(dependsleaf1);
        TestEvent dependsOnOne = new ComponentAssertion("dependsOnOne", AssertionService.BEAN_CREATED);
        dependsOnOne.addDependency(dependsleaf1);
        startEvents.addEvent(dependsOnOne);

        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("dependsOnOne", ComponentInjection.class, null, "init", "destroy",
            new TestArgument[] { new StringArgument("dependsOnOne") } , null,
            new String[] { "dependsleaf1" }, BeanMetadata.ACTIVATION_EAGER, null)));

        TestEvent dependsleaf2 = new ComponentAssertion("dependsleaf2", AssertionService.BEAN_CREATED);
        startEvents.addEvent(dependsleaf2);
        TestEvent dependsleaf3 = new ComponentAssertion("dependsleaf3", AssertionService.BEAN_CREATED);
        startEvents.addEvent(dependsleaf3);
        TestEvent dependsOnTwo = new ComponentAssertion("dependsOnTwo", AssertionService.BEAN_CREATED);
        dependsOnTwo.addDependency(dependsleaf2);
        dependsOnTwo.addDependency(dependsleaf3);
        startEvents.addEvent(dependsOnTwo);

        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("dependsOnTwo", ComponentInjection.class, null, "init", "destroy",
            new TestArgument[] { new StringArgument("dependsOnTwo") } , null,
            new String[] { "dependsleaf2", "dependsleaf3" }, BeanMetadata.ACTIVATION_EAGER, null)));

        // validate the depends on metadata
        startEvents.addValidator(new ComponentDependencyValidator("dependsOnOne", new String[] { "dependsleaf1" }));
        startEvents.addValidator(new ComponentDependencyValidator("dependsOnTwo", new String[] { "dependsleaf2", "dependsleaf3" }));

        // now doing prototype/singleton tests

        // creation events for each of our singletons.
        startEvents.addAssertion("singleton1", AssertionService.BEAN_CREATED);
        startEvents.addAssertion("singleton2", AssertionService.BEAN_CREATED);
        // this is the first with an explicit scope specified, so validate it
        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("singleton2", ComponentInjection.class, null, "init", "destroy",
            new TestArgument[] { new StringArgument("singleton2") } , null,
            null, BeanMetadata.ACTIVATION_EAGER, BeanMetadata.SCOPE_SINGLETON)));

        // now add a failure event for each of these.  This will catch multiple creations.  The
        // first create event will be processed, and any additional ones will be flagged as a failure
        startEvents.addFailureEvent(new ComponentAssertion("singleton1", AssertionService.BEAN_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("singleton2", AssertionService.BEAN_CREATED));

        // this is the first with a prototype scope
        startEvents.addValidator(new ComponentMetadataValidator(
            new BeanComponent("prototype1", PrototypeComponentInjection.class, null, "init", null,
            new TestArgument[] { new StringArgument("prototype1") } , null,
            null, BeanMetadata.ACTIVATION_LAZY, BeanMetadata.SCOPE_PROTOTYPE)));

        // we should see 3 created, and all 3 should end up in the set because
        // they will not compare equal
        startEvents.addAssertion("prototype1", AssertionService.BEAN_CREATED);
        startEvents.addAssertion("prototype1", AssertionService.BEAN_CREATED);
        startEvents.addAssertion("prototype1", AssertionService.BEAN_CREATED);

        startEvents.addAssertion("prototype1", AssertionService.BEAN_INIT_METHOD);
        startEvents.addAssertion("prototype1", AssertionService.BEAN_INIT_METHOD);
        startEvents.addAssertion("prototype1", AssertionService.BEAN_INIT_METHOD);

        // only 3 of these also
        startEvents.addAssertion("prototype4", AssertionService.BEAN_CREATED);
        startEvents.addAssertion("prototype4", AssertionService.BEAN_CREATED);
        startEvents.addAssertion("prototype4", AssertionService.BEAN_CREATED);

        startEvents.addAssertion("prototype4", AssertionService.BEAN_INIT_METHOD);
        startEvents.addAssertion("prototype4", AssertionService.BEAN_INIT_METHOD);
        startEvents.addAssertion("prototype4", AssertionService.BEAN_INIT_METHOD);

        startEvents.addAssertion("prototype2", AssertionService.BEAN_CREATED);
        startEvents.addAssertion("prototype2", AssertionService.BEAN_CREATED);
        startEvents.addAssertion("prototype2", AssertionService.BEAN_CREATED);

        startEvents.addAssertion("prototype2", AssertionService.BEAN_INIT_METHOD);
        startEvents.addAssertion("prototype2", AssertionService.BEAN_INIT_METHOD);
        startEvents.addAssertion("prototype2", AssertionService.BEAN_INIT_METHOD);

        // this is an inline version, but should behave the same as a referenced one
        startEvents.addAssertion("inlinePrototype1", AssertionService.BEAN_CREATED);
        startEvents.addAssertion("inlinePrototype1", AssertionService.BEAN_INIT_METHOD);

        // make sure this is capped
        startEvents.addFailureEvent(new ComponentAssertion("prototype1", AssertionService.BEAN_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("prototype2", AssertionService.BEAN_CREATED));
        // this one will be explicitly requested in the middle phase.  We should see nothing here
        startEvents.addFailureEvent(new ComponentAssertion("prototype3", AssertionService.BEAN_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("prototype4", AssertionService.BEAN_CREATED));


        // ok, now we'll request some components via the metadata and see what we get.
        MetadataEventSet middleEvents = controller.getMiddleEvents();
        // this one was not initialized originall
        middleEvents.addInitializer(new LazyComponentStarter("lazyleaf2"));
        middleEvents.addAssertion("lazyleaf2", AssertionService.BEAN_CREATED);
        middleEvents.addInitializer(new LazyComponentStarter("lazyleaf2a"));
        middleEvents.addAssertion("lazyleaf2a", AssertionService.BEAN_CREATED);
        // this one was created.  Ensure it don't see it created again
        middleEvents.addInitializer(new LazyComponentStarter("lazyleaf1"));
        middleEvents.addFailureEvent(new ComponentAssertion("lazyleaf1", AssertionService.BEAN_CREATED));
        middleEvents.addInitializer(new LazyComponentStarter("lazyleaf1a"));
        middleEvents.addFailureEvent(new ComponentAssertion("lazyleaf1a", AssertionService.BEAN_CREATED));
        // this will cascade to another lazy-init omponent
        middleEvents.addInitializer(new LazyComponentStarter("lazycomp1"));
        middleEvents.addAssertion("lazycomp1", AssertionService.BEAN_CREATED);
        middleEvents.addAssertion("lazyleaf3", AssertionService.BEAN_CREATED);

        middleEvents.addInitializer(new LazyComponentStarter("lazycomp1a"));
        middleEvents.addAssertion("lazycomp1a", AssertionService.BEAN_CREATED);
        middleEvents.addAssertion("lazyleaf3a", AssertionService.BEAN_CREATED);

        // we'll request this twice.  Each time should create a component
        middleEvents.addInitializer(new LazyComponentStarter("prototype3"));
        middleEvents.addInitializer(new LazyComponentStarter("prototype3"));

        middleEvents.addAssertion("prototype3", AssertionService.BEAN_CREATED);
        middleEvents.addAssertion("prototype3", AssertionService.BEAN_CREATED);
        // cap this creation
        middleEvents.addFailureEvent(new ComponentAssertion("prototype3", AssertionService.BEAN_CREATED));

        // now request some of the singletons (including the one declared with bundle scope).
        middleEvents.addInitializer(new LazyComponentStarter("singleton1"));
        middleEvents.addFailureEvent(new ComponentAssertion("singleton1", AssertionService.BEAN_CREATED));

        // stop events should proceed as normal, but verify that none of the prototype events get destroy-methods called, and
        // add a couple checks that the
        EventSet stopEvents = controller.getStopEvents();

        // our singletons should be destroyed
        stopEvents.addAssertion("singleton1", AssertionService.BEAN_DESTROY_METHOD);
        stopEvents.addAssertion("singleton2", AssertionService.BEAN_DESTROY_METHOD);
        controller.run();
    }


    /**
     * verify that destroy-method is a config error on prototype scope beans.
     */
    public void testPrototypeDestroy_Method() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_prototype_destroy_method.jar");
        controller.run();
    }


    /**
     * verify that explicitly setting activation to eager and scope to prototype is an error.
     * (section 121.5.2 of the spec)
     */
    public void testEagerPrototype() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/error_eager_prototype.jar");
        controller.run();
    }


    /**
     * Test the example shown in section 12.2.6 of the
     * blueprint spec of a 3-bean cycle and how it must
     * be broken.  This will test that the different
     * events occur in the correct order.
     *
     * @exception Exception
     */
    public void testCycleBreaking() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/dependency_cycle_breaking.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // this will be the first event
        TestEvent cCreated = new ComponentAssertion("c", AssertionService.BEAN_CREATED);
        startEvents.addEvent(cCreated);
        // this occurs next...constrain it to be after the creation of C
        TestEvent bCreated = new ComponentAssertion("b", AssertionService.BEAN_CREATED);
        bCreated.addDependency(cCreated);
        startEvents.addEvent(bCreated);
        // a must be injected with an instance of b in the constructor, so that's next
        // in the chain.
        TestEvent aCreated = new ComponentAssertion("a", AssertionService.BEAN_CREATED);
        aCreated.addDependency(bCreated);
        startEvents.addEvent(aCreated);
        // a will now be injected into an instance of c.
        TestEvent aInjected = new ComponentAssertion("c", AssertionService.BEAN_PROPERTY_SET);
        aInjected.addDependency(aCreated);
        startEvents.addEvent(aInjected);

        // and finally, the init-method of C will be run.
        TestEvent cInit = new ComponentAssertion("c", AssertionService.BEAN_INIT_METHOD);
        cInit.addDependency(aInjected);
        startEvents.addEvent(cInit);

        // the events are now ordered and queued up, kick this into action.
        controller.run();
    }


    /**
     * Test the example shown in section 12.2.6 of the
     * blueprint spec of a self-referential singleton.
     *
     * @exception Exception
     */
    public void testSingletonCycle() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/singleton_cycle.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // this will be the first event
        TestEvent aCreated = new ComponentAssertion("a", AssertionService.BEAN_CREATED);
        startEvents.addEvent(aCreated);
        // a will now be injected into an instance of a.
        TestEvent aInjected = new ComponentAssertion("a", AssertionService.BEAN_PROPERTY_SET);
        aInjected.addDependency(aCreated);
        startEvents.addEvent(aInjected);

        // and finally, the init-method of a will be run.
        TestEvent aInit = new ComponentAssertion("a", AssertionService.BEAN_INIT_METHOD);
        aInit.addDependency(aInjected);
        startEvents.addEvent(aInit);

        // the events are now ordered and queued up, kick this in motion.
        controller.run();
    }


    /**
     * Cycles can only be broken at singleton components, so a self-referential
     * prototype scope bean like the one used with the singletons must result in an
     * error.
     *
     * @exception Exception
     */
    public void testPrototypeCycle() throws Exception {
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/prototype_cycle.jar");
        controller.run();
    }


    /**
     * verify that recursive calls to getComponentInstance() from a bean constructor are
     * detected as an error.
     */
    public void testRecursiveConstructor() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/recursive_constructor.jar");
        controller.run();
    }


    /**
     * verify that recursive calls to getComponentInstance() from a bean property setter works
     * when a reference cycle can be broken.
     */
    public void testRecursivePropertyInjection() throws Exception {
        // this should just be the standard error set
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/recursive_property_injection.jar");

        // self checking test case that will raise error events for failures
        controller.run();
    }


    /**
     * verify that recursive calls to getComponentInstance() from a bean init-method works
     * when a refernence cycle can be broken.
     */
    public void testRecursiveInitMethod() throws Exception {
        // this should just be the standard error set
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/recursive_init-method.jar");

        // self checking test case that will raise error events for failures
        controller.run();
    }


    /**
     * verify that recursive calls to getComponentInstance() from a bean property setter are
     * detected as an error when the cycle cannot be broken.
     */
    public void testRecursivePrototypePropertyInjection() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/recursive_prototype_property_injection.jar");
        controller.run();
    }


    /**
     * verify that recursive calls to getComponentInstance() from a bean init-method are
     * detected as an error when the cycle cannot be broken.
     */
    public void testRecursivePrototypeInitMethod() throws Exception {
        // this should just be the standard error set
        StandardErrorTestController controller = new StandardErrorTestController(getContext(),
            getWebServer()+"www/recursive_prototype_init-method.jar");
        controller.run();
    }


    /**
     * Test the getMetdata() variations using a combination
     * of all metadata types and id variations.  This will
     * verify that all of the expected comments, including
     * the inline ones and environment components, can be
     * retrieved.
     */
    public void testMetadataSampler() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/metadata_sampler.jar");
        MetadataEventSet startEvents = controller.getStartEvents();
        // this is a special validator just for this test
        startEvents.addValidator(new MetadataSamplerValidator());
        controller.run();
    }


    /**
     * Test of the <idref> attribute and associated metadata.
     */
    public void testIdrefInjection() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/idref_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        // validate the value
        startEvents.validateComponentArgument("comp1", "arg1", "refComp");
        // and also validate the metadata.  This should be the corresponding idref metadata
        startEvents.addValidator(new ArgumentMetadataValidator("comp1", new TestArgument[] {
            new TestArgument(new TestIdRefValue("refComp"), null)
        }));
        controller.run();
    }
}
