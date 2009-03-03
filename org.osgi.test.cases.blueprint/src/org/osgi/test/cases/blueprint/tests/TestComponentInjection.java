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

import org.osgi.service.blueprint.reflect.LocalComponentMetadata;
import org.osgi.test.cases.blueprint.components.injection.ComponentInjection;
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
            new LocalComponent("lazyleaf1", ComponentInjection.class, null, null,
            new TestParameter[] { new StringParameter("lazyleaf1") } , null,
            null, true, null)));

        // first real test.  This will cause lazyleaf1 to get instantiated in order to be injected.  That's the real test.
        startEvents.addAssertion("comp1", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1", ComponentInjection.class);
        startEvents.addValidator(new ConstructorMetadataValidator("comp1", new TestParameter[] {
            new StringParameter("comp1"),
            new ReferenceParameter("lazyleaf1")
        }));

        // second version of this using the inverse default
        startEvents.addAssertion("lazyleaf1a", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("lazyleaf1a", ComponentInjection.class);
        // first real test.  This will cause lazyleaf1a to get instantiated in order to be injected.  That's the real test.
        startEvents.addAssertion("comp1a", AssertionService.COMPONENT_CREATED);
        startEvents.validateComponent("comp1a", ComponentInjection.class);
        startEvents.addValidator(new ConstructorMetadataValidator("comp1a", new TestParameter[] {
            new StringParameter("comp1a"),
            new ReferenceParameter("lazyleaf1a")
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
        startEvents.addValidator(new ConstructorMetadataValidator("twoleaf", new TestParameter[] {
            new StringParameter("twoleaf"),
            new ReferenceParameter("leaf1")
        }));
        startEvents.addValidator(new PropertyMetadataValidator("twoleaf", new TestProperty[] {
            new ReferenceProperty("componentOne", "leaf2")
        }));

        // a more complex dependency graph
        startEvents.addAssertion("twocomp", AssertionService.COMPONENT_CREATED);
        startEvents.addValidator(new ConstructorMetadataValidator("twocomp", new TestParameter[] {
            new StringParameter("twocomp"),
            new ReferenceParameter("leaf1"),
            new ReferenceParameter("twoleaf"),
            new ReferenceParameter("comp1"),
        }));

        // different collection classes
        startEvents.addAssertion("twolist", AssertionService.COMPONENT_CREATED);
        startEvents.addFailureEvent(new ComponentAssertion("twolist", AssertionService.COMPONENT_CREATED));
        // the property is a list, which references two components
        startEvents.addValidator(new PropertyMetadataValidator("twolist", new TestProperty[] {
            new TestProperty(new TestListValue(new TestValue[] {
                new TestReferenceValue("twoleaf"),
                new TestReferenceValue("twocomp"),
            }),"componentOne")
        }));

        startEvents.addAssertion("twoset", AssertionService.COMPONENT_CREATED);
        // the property is a set, which references two components
        startEvents.addValidator(new PropertyMetadataValidator("twoset", new TestProperty[] {
            new TestProperty( new TestSetValue(new TestValue[] {
                new TestReferenceValue("twoleaf"),
                new TestReferenceValue("twocomp"),
            }),"componentOne")
        }));

        startEvents.addAssertion("mapref", AssertionService.COMPONENT_CREATED);
        // the property is a set, which references two components
        startEvents.addValidator(new PropertyMetadataValidator("mapref", new TestProperty[] {
            new TestProperty(new TestMapValue(new MapValueEntry[] {
                new MapValueEntry(new TestReferenceValue("leaf1"), new TestReferenceValue("twoleaf")),
                new MapValueEntry(new TestReferenceValue("leaf2"), new TestReferenceValue("twocomp")),
            }),"componentOne")
        }));

        // now some dependson tests.  The dependency ordering tests are done in the module
        startEvents.addAssertion("dependleaf1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("dependsOnOne", AssertionService.COMPONENT_CREATED);

        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("dependsOnOne", ComponentInjection.class, null, null,
            new TestParameter[] { new StringParameter("dependsOnOne") } , null,
            new String[] { "dependsleaf1" }, false, null)));

        startEvents.addAssertion("dependleaf2", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("dependleaf3", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("dependsOnTwo", AssertionService.COMPONENT_CREATED);

        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("dependsOnTwo", ComponentInjection.class, null, null,
            new TestParameter[] { new StringParameter("dependsOnTwo") } , null,
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
            new LocalComponent("singleton2", ComponentInjection.class, null, null,
            new TestParameter[] { new StringParameter("singleton2") } , null,
            null, false, LocalComponentMetadata.SCOPE_SINGLETON)));
        startEvents.addAssertion("singleton3", AssertionService.COMPONENT_CREATED);

        // singleton3 is bundle scope, but since it is not exported as a service, should behave like a singleton
        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("singleton3", ComponentInjection.class, null, null,
            new TestParameter[] { new StringParameter("singleton3") } , null,
            null, false, LocalComponentMetadata.SCOPE_BUNDLE)));

        // now add a failure event for each of these.  This will catch multiple creations.  The
        // first create event will be processed, and any additional ones will be flagged as a failure
        startEvents.addFailureEvent(new ComponentAssertion("singleton1", AssertionService.COMPONENT_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("singleton2", AssertionService.COMPONENT_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("singleton3", AssertionService.COMPONENT_CREATED));

        // we expect to see 3 (and only 3) instantiations of the prototype components
        startEvents.addAssertion("prototype1", AssertionService.COMPONENT_CREATED);

        // this is the first with a prototype scope
        startEvents.addValidator(new ComponentMetadataValidator(
            new LocalComponent("prototype1", ComponentInjection.class, null, null,
            new TestParameter[] { new StringParameter("prototype1") } , null,
            null, false, LocalComponentMetadata.SCOPE_PROTOTYPE)));

        startEvents.addAssertion("prototype1", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("prototype1", AssertionService.COMPONENT_CREATED);

        startEvents.addAssertion("prototype2", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("prototype2", AssertionService.COMPONENT_CREATED);
        startEvents.addAssertion("prototype2", AssertionService.COMPONENT_CREATED);

        // make sure this is capped
        startEvents.addFailureEvent(new ComponentAssertion("prototype1", AssertionService.COMPONENT_CREATED));
        startEvents.addFailureEvent(new ComponentAssertion("prototype2", AssertionService.COMPONENT_CREATED));
        // this one will be explicitly requested in the middle phase.  We should see nothing here
        startEvents.addFailureEvent(new ComponentAssertion("prototype3", AssertionService.COMPONENT_CREATED));


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

        middleEvents.addAssertion("prototype2", AssertionService.COMPONENT_CREATED);
        middleEvents.addAssertion("prototype2", AssertionService.COMPONENT_CREATED);
        // cap this creation
        middleEvents.addFailureEvent(new ComponentAssertion("prototype3", AssertionService.COMPONENT_CREATED));

        // now request some of the singletons (including the one declared with bundle scope).
        middleEvents.addInitializer(new LazyComponentStarter("singleton1"));
        middleEvents.addInitializer(new LazyComponentStarter("singleton3"));
        middleEvents.addFailureEvent(new ComponentAssertion("singleton1", AssertionService.COMPONENT_CREATED));
        middleEvents.addFailureEvent(new ComponentAssertion("singleton3", AssertionService.COMPONENT_CREATED));

        // stop events should proceed as normal, but verify that none of the prototype events get destroy-methods called, and
        // add a couple checks that the
        EventSet stopEvents = controller.getStopEvents();

        // our singletons should be destroyed
        stopEvents.addAssertion("singleton1", AssertionService.COMPONENT_DESTROY_METHOD);
        stopEvents.addAssertion("singleton2", AssertionService.COMPONENT_DESTROY_METHOD);
        stopEvents.addAssertion("singleton3", AssertionService.COMPONENT_DESTROY_METHOD);

        // but we should not see destroy events for the prototypes
        stopEvents.addFailureEvent(new ComponentAssertion("prototype1", AssertionService.COMPONENT_DESTROY_METHOD));
        stopEvents.addFailureEvent(new ComponentAssertion("prototype2", AssertionService.COMPONENT_DESTROY_METHOD));
        stopEvents.addFailureEvent(new ComponentAssertion("prototype3", AssertionService.COMPONENT_DESTROY_METHOD));

        controller.run();
    }
}
