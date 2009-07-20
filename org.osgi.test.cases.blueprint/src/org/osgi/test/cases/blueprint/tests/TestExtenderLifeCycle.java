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

        // now set up the ordering dependencies.

        // neither of these export services, so the last installed goes first
        firstStarted.addDependency(importing);

        // the exporting one can only be destroyed after both bundles
        // that don't export any services.
        exporting.addDependency(firstStarted);
        exporting.addDependency(importing);

        // the second circular bundle will be stopped first, but only
        // after the other bundles
        circular2.addDependency(exporting);
        // and this can only be destroyed after the cycle was broken.
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

        // this is in a circular relationship with another bundle.  The first bundle
        // exports a higher ranked service, so it should be stopped first.
        events = controller.getStopEvents(3);
        TestEvent circular1 = events.locateEvent(template);
        // the other circular item
        events = controller.getStopEvents(4);
        TestEvent circular2 = events.locateEvent(template);

        // now set up the ordering dependencies.

        // neither of these export services, so the last installed goes first
        firstStarted.addDependency(importing);

        // the exporting one can only be destroyed after both bundles
        // that don't export any services.
        exporting.addDependency(firstStarted);
        exporting.addDependency(importing);

        // the first circular bundle will be stopped first, but only
        // after the other bundles
        circular1.addDependency(exporting);
        // and this can only be destroyed after the cycle was broken.
        circular2.addDependency(circular1);

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
