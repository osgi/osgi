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

package org.osgi.test.cases.blueprint.components.serviceimport;

import java.util.Collection;
import java.util.Iterator;


/**
 * Test for injection of an empty List of services no services are available.  This
 * will also register a service using the service manager to see that the new
 * registration is picked up.
 */
public class ReferenceCollectionSortChecker extends ReferenceCollectionChecker {
    public ReferenceCollectionSortChecker(String componentId) {
        super(componentId);
    }

    /**
     * This is the actual test method.  After the component is
     * instantiated, the init method is called to drive the
     * component test logic.  Each concrete test will override this
     * and verify that the injected results are correct.  Tests
     * dealing with service dynamics will use the ServiceManager to
     * modify the state of the registered services.
     */
    public void init() {

        testCollection(injectedList, sortOrder);
        testCollection(injectedSet, sortOrder);
        super.init();
    }


    /**
     * Run a series of tests on a collection to verify the sorting
     * order and also some basics of service dynamics and iterators.
     *
     * @param source The source collection.
     * @param order  The expected order of the elements.
     */
    protected void testCollection(Collection source, String[] order) {
        // we process only one collection to avoid interference from
        // the service registrations/deregistrations
        if (source == null) {
            return;
        }
        // first, validate the the original sort order
        validateSortOrder(source.iterator(), order);
        // we're going to unregister one of the services, then retest
        // the sort order.
        int midpoint = order.length / 2 + 1;
        // unregister something in the middle
        serviceManager.unregisterService(order[midpoint]);
        validateSortOrder(source.iterator(), deleteElement(order, midpoint));
        // retrieve the iterator first, then register the midpoint service
        // again.  The iterator should see the update, even though it was
        // created before the service was registered.
        Iterator i = source.iterator();
        // now reregister and validate we have the original back.
        serviceManager.registerService(order[midpoint]);
        validateSortOrder(source.iterator(), order);

        // unregister the first service.  We're then going to request
        // an iterator, move past the first element, then register the
        // service again and validate the remaining order.  The registered
        // service should not show up because we've advanced past the
        // sort position of the added element.
        serviceManager.unregisterService(order[0]);
        i = source.iterator();
        // couple a hasNext() and next() call to advance the position.
        // the "next" one should be returned from index position 2.
        i.hasNext();
        i.next();
        // now reregister and complete the iteration.
        serviceManager.registerService(order[0]);

        validateSortOrder(i, slice(order, 2, 3));
    }
}

