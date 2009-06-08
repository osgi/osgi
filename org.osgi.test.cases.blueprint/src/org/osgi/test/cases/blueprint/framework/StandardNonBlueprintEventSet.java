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
 * An event set for processing all standard events for bundles that
 * should not be handled by the installed blueprint extender.
 */
public class StandardNonBlueprintEventSet extends EventSet {
    public StandardNonBlueprintEventSet(BundleContext testBundle, Bundle bundle) {
        super(testBundle, bundle);
        // we add an initializer to start our bundle when the test starts
        addInitializer(new TestBundleStarter(bundle));
        // end this a little short of the error timeout
        addInitializer(new TimedEventTrigger(8000));
        // we always expect to see a started bundle event, even though
        // the managed bundle portion of this will give an error
        addBundleEvent("STARTED");
        // no BlueprintContainer service should be published for this bundle.  This is an error if
        // one shows up
        addFailureEvent(new ServiceTestEvent("REGISTERED", "org.osgi.service.blueprint.container.BlueprintContainer"));
        // any of these lifecycle events for this bundle are a failure, these bundles should just be ignored
        addFailureEvent(new BlueprintContainerEvent("CREATED"));
        addFailureEvent(new BlueprintContainerEvent("CREATING"));
        addFailureEvent(new BlueprintContainerEvent("DESTROYING"));
        addFailureEvent(new BlueprintContainerEvent("DESTROYED"));
        addFailureEvent(new BlueprintContainerEvent("FAILURE"));
        addFailureEvent(new BlueprintContainerEvent("GRACE_PERIOD"));

        addFailureEvent(new BlueprintAdminEvent("CREATED"));
        addFailureEvent(new BlueprintAdminEvent("CREATING"));
        addFailureEvent(new BlueprintAdminEvent("DESTROYING"));
        addFailureEvent(new BlueprintAdminEvent("DESTROYED"));
        addFailureEvent(new BlueprintAdminEvent("FAILURE"));
        addFailureEvent(new BlueprintAdminEvent("GRACE_PERIOD"));

        // at the end of everything, there should not be a method context associated with this bundle
        addValidator(new NoBlueprintContainerValidator());
        // the bundle should be in the ACTIVE state when everything settles down
        addValidator(new BundleStateValidator(Bundle.ACTIVE));
    }
}

