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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.osgi.test.cases.blueprint.components.serviceimport.ServiceTwoListener;
import org.osgi.test.cases.blueprint.framework.*;
import org.osgi.test.cases.blueprint.services.*;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This class tests actions performed on starting and stopping of the
 * extender bundle.
 *
 * @version $Revision$
 */
public class TestExtenderLifeCycle extends DefaultTestBundleControl {
    // the retrieved extender bundle
    protected Bundle extender = null;


    /**
     * Tests extender bundle startup.  This should locate
     * and start all installed blueprint bundles.
     *
     * @exception Exception
     */
	public void testExtenderStart() throws Exception {
        // go locate the current extender bundle
        Bundle extenderBundle = getExtenderBundle();
        // we want this in a stopped state initially.
        extenderBundle.stop();

        StandardTestController controller = new StandardTestController(getContext(),
            getWebServer()+"www/comp1_no_header.jar");
        // give this a little larger time out
        controller.setTimeout(30000);

        // now add some more selected tests to be sitting idle waiting for
        controller.addBundle(getWebServer()+"www/ServiceOne_import.jar");
        controller.addBundle(getWebServer()+"www/ServiceOne_export.jar");
        controller.addBundle(getWebServer()+"www/comp1_explicit_config.jar");

        // get the start events for the last installed bundle...that's the last one
        // to get its startup stuff processed
        MetadataEventSet startEvents = controller.getStartEvents(3);

        // have this start with a little delay behind the others
        startEvents.addInitializer(new TestBundleStarter(extenderBundle, 1000));
        // this should run through all of the normal startup/shutdown events from here.
        controller.run();
    }


    /**
     * Test the orderly shutdown of managed bundles when the
     * extender bundle is stopped.
     *
     * @exception Exception
     */
    public void testExtenderStop() throws Exception {
        // this test uses something other than the standard set of
        // events/validators/controllers, so we'll hand construct the
        // test phases rather than using the standard controller.
        ExtenderStopController controller = new ExtenderStopController(getContext(), getExtenderBundle());
        // no imports or exports on this one
        controller.addBundle(getWebServer()+"www/comp1_no_header.jar");

        // the import jar has a dependency on the export one, so the import should be
        // shutdown first
        controller.addBundle(getWebServer()+"www/ServiceTwoSubclass_export.jar");
        controller.addBundle(getWebServer()+"www/ServiceTwoSubclass_import.jar");
        // there's a circular reference relationship between these two bundles...this
        // will shutdown the first installed one first.
        controller.addBundle(getWebServer()+"www/circular_ref_one.jar");
        controller.addBundle(getWebServer()+"www/circular_ref_two.jar");

        // now create dependencies between the events for these bundles to
        // verify that these are done in the correct order.
        EventSet events = controller.getStopEvents(0);
        TestEvent template = new BlueprintContainerEvent("DESTROYED");

        // locate the bundle event for the first bundle that should be destroyed
        TestEvent firstStarted = events.locateEvent(template);

        // this is a bundle that is importing, but not exporting.  This was
        // started after the first started, so will be destroyed first
        events = controller.getStopEvents(2);
        TestEvent importing = events.locateEvent(template);

        // this is exporting services used by the importing bundle, it must be destroyed after
        // the importing one.
        events = controller.getStopEvents(1);
        TestEvent exporting = events.locateEvent(template);

        // this is in a circular relationship with another bundle.  The services
        // exported by these bundles have default service rankings, so the bundle
        // with the highest registered service id will be stopped first, which should
        // be the second one started.
        events = controller.getStopEvents(3);
        TestEvent circular1 = events.locateEvent(template);
        // the other circular item
        events = controller.getStopEvents(4);
        TestEvent circular2 = events.locateEvent(template);

        /*
         * Module service usage information and shutdown order:
         *
         * Iteration 0:
         *    firstStarted : 0
         *    export       : 1
         *    import       : 0
         *    circular1    : 1
         *    circular2    : 1
         *
         * Result: Must close all bundles that do not have services in
         *         use.  Candidates are firstStarted and import.
         *         Import is destroyed first since it was installed later,
         *         followed by firstStarted.
         *
         * Iteration 1:
         *    export       : 0
         *    circular1    : 1
         *    circular2    : 1
         *
         *  Result: Usage of export goes to 0, so this is a repeat of rule 1.
         *          Export is destroyed next.
         *
         * Iteration 2:
         *    circular1    : 1
         *    circular2    : 1
         *
         *  Result: Usage of each is 1 and all services have default ranking.
         *          Since ciruclar2 was installed later,
         *          it must be destroyed first.
         *
         * Iteration 3:
         *    circular1    : 0
         *
         *  Result: Finally circular1 is destroyed.
         */

        // now set up the shutdown order.

        firstStarted.addDependency(importing);
        exporting.addDependency(firstStarted);
        circular2.addDependency(exporting);
        circular1.addDependency(circular2);

        controller.run();
    }


    /**
     * A second test of orderly shutdown using the ranking rule to
     * determine how circular references are broken.
     *
     * @exception Exception
     */
    public void testExtenderRankedStop() throws Exception {
        // this test uses something other than the standard set of
        // events/validators/controllers, so we'll hand construct the
        // test phases rather than using the standard controller.
        ExtenderStopController controller = new ExtenderStopController(getContext(), getExtenderBundle());
        // no imports or exports on this one
        controller.addBundle(getWebServer()+"www/comp1_no_header.jar");

        // the import jar has a dependency on the export one, so the import should be
        // shutdown first
        controller.addBundle(getWebServer()+"www/ServiceTwoSubclass_export.jar");
        controller.addBundle(getWebServer()+"www/ServiceTwoSubclass_import.jar");
        // there's a circular reference relationship between these two bundles...the
        // first has the lowest ranked registered service that is in use, the second
        // has the one with the highest registered service id.  The ranking rule
        // predominates, forcing the circular_ref_two bundle to be stopped first.
        controller.addBundle(getWebServer()+"www/circular_ref_two.jar");
        controller.addBundle(getWebServer()+"www/circular_ref_one_ranked.jar");

        // now create dependencies between the events for these bundles to
        // verify that these are done in the correct order.
        EventSet events = controller.getStopEvents(0);
        TestEvent template = new BlueprintContainerEvent("DESTROYED");

        // locate the bundle event for the first bundle that should be destroyed
        TestEvent firstStarted = events.locateEvent(template);

        // this is a bundle that is importing, but not exporting.  This was
        // started after the first started, so will be destroyed first
        events = controller.getStopEvents(2);
        TestEvent importing = events.locateEvent(template);

        // this is exporting services used by the importing bundle, it must be destroyed after
        // the importing one.
        events = controller.getStopEvents(1);
        TestEvent exporting = events.locateEvent(template);

        // this is in a circular relationship with another bundle.  The first bundle
        // exports a higher ranked service, so it should be stopped last.
        events = controller.getStopEvents(4);
        TestEvent circular1 = events.locateEvent(template);
        // the other circular item
        events = controller.getStopEvents(3);
        TestEvent circular2 = events.locateEvent(template);

        /*
         * Module service usage information and shutdown order:
         *
         * Iteration 0:
         *    firstStarted : 0
         *    export       : 1
         *    import       : 0
         *    circular2    : 1
         *    circular1    : 1
         *
         * Result: Must close all bundles that do not have services in
         *         use.  Candidates are firstStarted and import.
         *         Import is destroyed first since it was installed later,
         *         followed by firstStarted.
         *
         * Iteration 1:
         *    export       : 0
         *    circular2    : 1
         *    circular1    : 1
         *
         *  Result: Usage of export goes to 0, so this is a repeat of rule 1.
         *          Export is destroyed next.
         *
         * Iteration 3:
         *    circular2    : 1
         *    circular1    : 1
         *
         *  Result: Since usage of each is 1 and even though circular1 was installed later,
         *          circular2 has a lower ranking service so it must be destroyed first.
         *
         * Iteration 4:
         *    circular1    : 0
         *
         *  Result: Finally circular1 is destroyed.
         */

        // now set up the shutdown order.

        firstStarted.addDependency(importing);
        exporting.addDependency(firstStarted);
        circular2.addDependency(exporting);
        circular1.addDependency(circular2);

        controller.run();
    }


    /**
     * Retrieve the extender bundle for a test.
     *
     * @return The extender Bundle instance.
     * @exception Exception
     */
    protected Bundle getExtenderBundle() throws Exception {
        if (extender == null) {
            /* load a simple test bundle and run the test.  The extender bundle will */
            // be obtained in the context of running this
            StandardTestController controller = new StandardTestController(getContext(),
                getWebServer()+"www/comp1_id.jar");
            controller.run();
            extender = BlueprintContainerEvent.getExtenderBundle();
        }
        return extender;
    }
}
