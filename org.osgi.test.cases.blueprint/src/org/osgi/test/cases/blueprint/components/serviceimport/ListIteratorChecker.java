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

import java.util.Iterator;

import org.osgi.service.blueprint.container.ServiceUnavailableException;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.TestServiceOne;


/**
 * Tests the behavior guarantees for <ref-list> iterators.
 */
public class ListIteratorChecker extends ReferenceCollectionChecker {

    public ListIteratorChecker(String componentId) {
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
        // ok, we're starting with a full list of services.  Get the iterator,
        // call hasNext(), unregister all of the services and call next().
        // This should return a proxy object, but calling a method on it
        // should give an error
        Iterator i = injectedList.iterator();
        AssertionService.assertTrue(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());
        // unregister everything
        serviceManager.unregisterServices();
        // this should return an object without error
        TestServiceOne service = (TestServiceOne)i.next();
        try {
            // if this call succeeds, this is a failure, and will be raised as such
            AssertionService.assertFalse(this, "Unexpected service test result", service.testOne());
        } catch (ServiceUnavailableException e) {
            // We expect to get here
        }

        // this iterator should now return false because everything has gone
        AssertionService.assertFalse(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());

        // get a fresh iterator
        i = injectedList.iterator();

        // empty collection, this should also be false
        AssertionService.assertFalse(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());
        // register a single service instance
        serviceManager.registerService("ServiceOneA");

        // we've added one item, but this should still be false because of the previous call
        AssertionService.assertFalse(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());

        // this collection now has one item in it.
        i = injectedList.iterator();
        // this should be true
        AssertionService.assertTrue(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());
        // this should return an object without error
        service = (TestServiceOne)i.next();
        // this call should also succeed.
        AssertionService.assertTrue(this, "Unexpected service test result", service.testOne());

        // register a second service instance
        serviceManager.registerService("ServiceOneB");
        // since we have not gotten a false hasNext() result, the new service should be
        // appended to the end and available now

        // this should be true
        AssertionService.assertTrue(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());
        // this should return an object without error
        service = (TestServiceOne)i.next();
        // this call should also succeed.
        AssertionService.assertTrue(this, "Unexpected service test result", service.testOne());
        // this should now be false.
        AssertionService.assertFalse(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());

        // now we have two items in the list...step past the first one, then unregister it.  The second
        // should still be returned.

        // this collection now has two items in it.
        i = injectedList.iterator();
        // this should be true
        AssertionService.assertTrue(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());
        // this should return an object without error
        service = (TestServiceOne)i.next();
        // The ordering of the services in the list is unspecified, so get the name
        // from the first one so we have a target for the unregistration.
        String firstServiceName = service.getServiceName();
        serviceManager.unregisterService(firstServiceName);

        // this should be true
        AssertionService.assertTrue(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());
        // this should return an object without error
        service = (TestServiceOne)i.next();
        // this call should also succeed.
        AssertionService.assertTrue(this, "Unexpected service test result", service.testOne());
        // reregister the first service...this should be appended to the end again
        serviceManager.registerService(firstServiceName);

        // this should be true
        AssertionService.assertTrue(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());
        // this should return an object without error
        service = (TestServiceOne)i.next();
        // this call should also succeed.
        AssertionService.assertTrue(this, "Unexpected service test result", service.testOne());
        super.init();
    }
}

