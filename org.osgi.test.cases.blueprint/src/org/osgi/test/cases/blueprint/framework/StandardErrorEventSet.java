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
 * An event set for processing all standard events we expect to
 * see from an "error" test case.  An error test is one that
 * starts a bundle and processes the error events.  No Module context
 * artifacts should exist at the end of this test.
 */
public class StandardErrorEventSet extends EventSet {
    public StandardErrorEventSet(BundleContext testBundle, Bundle bundle) {
        super(testBundle, bundle);
        // we add an initializer to start our bundle when the test starts
        addInitializer(new TestBundleStarter(bundle));
        // we always expect to see a started bundle event, even though
        // the managed bundle portion of this will give an error
        addBundleEvent("STARTED");
        // no BlueprintContext service should be published for this bundle.  This is an error if
        // one shows up
        addFailureEvent(new ServiceTestEvent("REGISTERED", "org.osgi.service.blueprint.context.BlueprintContext"));
        // we should be seeing a failure event published for this.
        // other events are not really determined.
        addBlueprintEvent("FAILURE");
        addBlueprintContextEvent("FAILED");

        // at the end of everything, there should not be a method context associated with this bundle
        addValidator(new NoBlueprintContextValidator());
        // the bundle should be in the ACTIVE state when everything settles down
        addValidator(new BundleStateValidator(Bundle.ACTIVE));
    }
}

