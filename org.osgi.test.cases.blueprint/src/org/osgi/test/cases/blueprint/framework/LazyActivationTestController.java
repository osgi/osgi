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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Controller for a standard, bundle test involving lazy activation of
 * a bundle.  A lot of the standard lifecycle events are not set up
 * automatically for the bundles used for this test.
 */
public class LazyActivationTestController extends ThreePhaseTestController {
    public LazyActivationTestController(BundleContext testContext) {
        super(testContext);
    }

    public LazyActivationTestController(BundleContext testContext, String bundle1) throws Exception {
        this(testContext);
        addBundle(bundle1);
    }

    /**
     * Add a standard set of bundle start events for this event type.
     *
     * @param bundle The bundle the event set is tracking.
     * @param moduleMetadata                                              1
     *               The ModuleMetadata context for this event set.
     * @param events The created event set.
     */
    public void addStartEvents(Bundle bundle, ModuleMetadata moduleMetadata, EventSet events) {
        // this will kick the STARTING process, but the bundle will not fully initialize
        // until we do something to force classloading
        events.addInitializer(new TestBundleStarter(bundle));
        // we always expect to see a STARING bundle event, but not STARTED
        events.addBundleEvent("STARTING");
        // we should not see a Modudle context getting registered yet.
        events.addFailureEvent(new ServiceTestEvent("REGISTERED", "org.osgi.service.blueprint.context.ModuleContext"));
        // we should not see any of the standard blueprint events
        events.addFailureEvent(new BlueprintEvent("CREATING"));
        events.addFailureEvent(new BlueprintEvent("CREATED"));
        events.addFailureEvent(new ModuleContextEvent("CREATED"));
        events.addFailureEvent(new BundleTestEvent("STARTED"));
        // the bundle should be in the STARTING state when everything settles down
        events.addValidator(new BundleStateValidator(Bundle.STARTING));
    }


    /**
     * Add a standard set of bundle middle events for this event type.
     *
     * @param bundle The bundle the event set is tracking.
     * @param moduleMetadata
     *               The ModuleMetadata context for this event set.
     * @param events The created event set.
     */
    public void addMiddleEvents(Bundle bundle, ModuleMetadata moduleMetadata, EventSet events) {

        // these events would normally be expected in the first phase, but won't show up
        // until the second phase because of the LAZY_ACTIVATION

        // we always expect to see a started bundle event
        events.addBundleEvent("STARTED");
        // we should see a service registered for the module context.
        events.addServiceEvent("REGISTERED", "org.osgi.service.blueprint.context.ModuleContext");
        // now standard blueprint revents.
        events.addBlueprintEvent("CREATING");
        events.addBlueprintEvent("CREATED");
        events.addModuleContextEvent("CREATED");

        // this needs to be the first validator of the set, since
        // it initializes the module context.
        events.addValidator(moduleMetadata);
        // this needs to perform some cleanup when everything is done,
        // so add it to the terminator list.
        events.addTerminator(moduleMetadata);
        // the bundle should be in the ACTIVE state when everything settles down
        events.addValidator(new BundleStateValidator(Bundle.ACTIVE));
    }
}

