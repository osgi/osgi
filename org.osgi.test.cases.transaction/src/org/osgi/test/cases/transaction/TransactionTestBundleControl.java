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
package org.osgi.test.cases.transaction;

import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 *
 * abstract class TransactionTestBundleControl, provides a waitSomeTime method to wait for a
 * default time 30 seconds or user specified time at the beginning of the test to allow the transaction 
 * services to have enough time to be registered.
 * 
 *
 */
public abstract class TransactionTestBundleControl extends DefaultTestBundleControl {

    private final int DEFAULTTIME = 30;
    
    /*
     * let's wait a little bit to make sure the services are registerred.
     */
    protected void waitSomeTime() throws InterruptedException {
            int t = getWaitTime();
            log("let's wait " + t + " seconds to make sure the services are registered.");
            synchronized(this) {
                wait(t * 1000);
            }
    }
    
    /*
     * get the wait time in seconds, check the user preference first
     */
    private int getWaitTime() {
        // check if wait time is specified in system property
            String p;
            int waitTime = DEFAULTTIME;
            // First check if the user has a preference.
            p = getContext().getProperty("org.osgi.test.cases.transaction.waittime");
            if (p == null) {
                p = System.getProperty("org.osgi.test.cases.transaction.waittime");
            }
            if (p != null) {
                if (Integer.parseInt(p) > 0) {
                    waitTime = Integer.parseInt(p);
                }
            } 
            return waitTime;
    }
}
