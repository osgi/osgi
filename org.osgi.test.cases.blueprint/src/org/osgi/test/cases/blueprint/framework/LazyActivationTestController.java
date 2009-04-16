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

