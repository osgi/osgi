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

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.blueprint.services.AssertionService;

/**
 * A test initializer to start a thread that will trigger an event
 * at a given timer point.  This is useful for triggering asynchronous
 * events when there's no good event available to trigger an action
 */
public class TimedEventTrigger implements TestInitializer, Runnable {
    protected long delay = 0;

    // how long to wait before triggering the event
    public TimedEventTrigger(long delay) {
        this.delay = delay;
    }


    /**
     * Perform any additional test phase cleanup actions.
     *
     * @param testContext
     *               The BundleContext for the test (used for inspecting the test
     *               environment).
     *
     * @exception Exception
     */
    public void start(BundleContext testContext) throws Exception {
        // create a new thread and fire this up
        Thread newThread = new Thread(this, "Blueprint event trigger");
        newThread.start();
    }


    /**
     * The run method for this starter.  This will wait the delay
     * amount, then call the performAction() method to perform
     * the needed delay method.
     */
    public void run() {
        try {
            Thread.sleep(delay);
            performAction();
        } catch (InterruptedException e) {
            // if we're interrupted, I guess we should skip the action
        }
    }


    /**
     * Perform the requested action at the end of the timeout.
     * Subclasses can override this if they wish.
     */
    protected void performAction() {
        AssertionService.sendEvent(AssertionService.GENERAL_EVENT);
    }
}

