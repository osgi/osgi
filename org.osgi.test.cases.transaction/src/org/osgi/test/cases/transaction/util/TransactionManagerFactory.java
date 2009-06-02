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

package org.osgi.test.cases.transaction.util;

import javax.transaction.TransactionManager;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @version $Rev$ $Date$
 */
public class TransactionManagerFactory {

    private static ServiceReference _tmRef;
    private static TransactionManager _tm;
    private static BundleContext _context;

    public static void setBundleContext(BundleContext context) {
        _context = context;
    }

    public static TransactionManager getTransactionManager() {
        return getTransactionManager(0);
    }

    public static TransactionManager getTransactionManager(int waitTime) {
        if (_tm != null) {
            return _tm;
        }
        
        if (waitTime == 0) {
            // get TransactionManager from Service Reference
            _tmRef = _context.getServiceReference(TransactionManager.class
                    .getName());
        }

        if (waitTime > 0) {
            boolean done = false;
            int count = 0;
            while (!done) {
                // get TransactionManager from Service Reference
                _tmRef = _context.getServiceReference(TransactionManager.class
                        .getName());

                // check if we are able to get a valid _tmRef. If not, wait a
                // second
                // and try again
                if (_tmRef == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    count++;
                    if (count == waitTime) {
                        System.out.println("cannot get TransactionManager after "
                                        + count + " seconds");
                        done = true;
                    }

                } else {
                    System.out.println("able to get TransactionManager after "
                            + count + " seconds");
                    done = true;
                }
            }
        }

        if (_tmRef != null) {
            _tm = (TransactionManager) _context.getService(_tmRef);
        }
       
        return _tm;
    }

}
