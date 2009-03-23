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

package org.osgi.test.cases.blueprint.framework;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Controller for a extender bundle lifecycle tests
 * where the stopping of the extender bundle controls
 * the action.
 */
public class ExtenderStopController extends StandardTestController {
    // the extender bundle we're working on
    protected Bundle extender;

    public ExtenderStopController(BundleContext testContext, Bundle extender) {
        super(testContext);
        this.extender = extender;
    }


    /**
     * Add a standard set of bundle stop events for this event type.
     *
     * @param bundle The bundle the event set is tracking.
     * @param moduleMetadata
     *               The ModuleMetadata context for this event set.
     * @param events The created event set.
     */
    public void addStopEvents(Bundle bundle, ModuleMetadata moduleMetadata, EventSet events) {
        // this is the same as the standard test set, except for the bundle stopper.  That
        // event is managed by stopping the extender bundle

        // we should see the module context unregistered during shutdown.
        events.addServiceEvent("UNREGISTERING", "org.osgi.service.blueprint.context.ModuleContext");
        // now standard blueprint revents.
        events.addBlueprintEvent("DESTROYING");
        events.addBlueprintEvent("DESTROYED");

        // at the end of everything, there should no longer be a module context associated with the
        // component bundle.
        events.addValidator(new NoModuleContextValidator());
        // the bundle should still be in the started state when everything settles down
        events.addValidator(new BundleStateValidator(Bundle.ACTIVE));
        // this needs to perform some cleanup when everything is done,
        // so add it to the terminator list.
        events.addTerminator(moduleMetadata);
    }



    /**
     * Perform any controller-related testcase cleanup steps.
     *
     * @param runner The test case running controlling the tests.
     */
    public void cleanup() throws Exception {
        super.cleanup();
        // restart the extender bundle
        extender.start();
    }

    public void setup() throws Exception {
        // get the first set of stop events and add an initializer to the stop phase to
        // shut down the extender bundle.
        EventSet events = getStopEvents(0);

        // we start this test phase out by stopping the bundle.  Everything else flows
        // from that.
        events.addInitializer(new TestBundleStopper(extender));
    }
}


