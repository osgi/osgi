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

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.osgi.service.blueprint.container.BlueprintListener;
import org.osgi.service.blueprint.container.BlueprintEvent;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

/**
 * A listener used to listen for replay events from
 * already published bundles.
 */
public class ReplayListener extends BaseTestComponent implements BlueprintListener {
    // once we see a non-replay event, then there should not be any additional replay events broadcast.
    protected boolean nonReplaySeen;
    // our creation bundle context
    protected BundleContext context;
    // our ServiceRegistration instance
    protected ServiceRegistration registration;

    public ReplayListener(String componentId, BundleContext context) {
        super(componentId);
        this.context = context;
    }


    /**
     * Register the listener with the service registry.
     */
    public void register() {
        registration = context.registerService(BlueprintListener.class.getName(), this, null);
    }


    /**
     * unregister the listener from the service registry.
     */
    public void unregister() {
        registration.unregister();
        registration = null;
    }


    /**
     * Method implemented for the BlueprintListener interface. This
     * rebroadcasts replay events as ComponentAssertions.
     *
     * @param e      The source event.
     */
    public void blueprintEvent(BlueprintEvent e) {
        // if this is a replay event, then queue it up for test consumption.
        if (e.isReplay()) {
            Hashtable props = new Hashtable();
            props.put(AssertionService.REPLAY_EVENT, e);
            if (nonReplaySeen) {
                AssertionService.fail(this, "replay event seen after a non-replay event");
            }
            else {
                // rebroadcast this
                AssertionService.sendEvent(this, AssertionService.LISTENER_REPLAY, props);
            }
        }
        else {
            // no more replay events should be allowed now
            nonReplaySeen = true;
        }
    }
}
