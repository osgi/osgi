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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;

import org.osgi.service.blueprint.container.ServiceUnavailableException;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.TestServiceTwo;


/**
 * Tests the behavior guarantees for <reference-list> iterators.
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
        // do a little collection method testing
        AssertionService.assertFalse(this, "Unexpected isEmpty() result", injectedList.isEmpty());
        // this is the first item proxy
        TestServiceOne firstService = (TestServiceOne)injectedList.get(0);

        // this call should also succeed.
        AssertionService.assertTrue(this, "Unexpected service test result", firstService.testOne());
        AssertionService.assertTrue(this, "Unexpected contains() result", injectedList.contains(firstService));

        List testList = new ArrayList();
        testList.add(firstService);
        AssertionService.assertTrue(this, "Unexpected containsAll() result", injectedList.containsAll(testList));
        AssertionService.assertEquals(this, "Unexpected indexOf result", 0, injectedList.indexOf(firstService));
        AssertionService.assertEquals(this, "Unexpected lastIndexOf result", 0, injectedList.lastIndexOf(firstService));
        AssertionService.assertEquals(this, "Unexpected size() result", 2, injectedList.size());

        // grab a sublist
        List subList = injectedList.subList(0, 1);
        AssertionService.assertEquals(this, "Unexpected size() result", 0, subList.size());
        AssertionService.assertSame(this, "Unexpected proxy object returned from sublist", firstService, subList.get(0));

        // ok, we're starting with a full list of services.  Get the iterator,
        // call hasNext(), unregister all of the services and call next().
        // This should return a proxy object, but calling a method on it
        // should give an error
        Iterator i = injectedList.iterator();
        // test the list iterator in lock step with the iterator
        ListIterator li = injectedList.listIterator();
        AssertionService.assertTrue(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());
        AssertionService.assertTrue(this, "Unexpected result from ref collection ListIterator.hasNext()", li.hasNext());
        AssertionService.assertFalse(this, "Unexpected result from ref collection ListIterator.hasPrevious()", li.hasPrevious());
        AssertionService.assertEquals(this, "Unexpected result from ref collection ListIterator.nextIndex()", 0, li.nextIndex());
        AssertionService.assertEquals(this, "Unexpected result from ref collection ListIterator.previousIndex()", -1, li.previousIndex());
        // unregister everything
        serviceManager.unregisterServices();
        // this should return an object without error
        TestServiceOne service = (TestServiceOne)i.next();
        TestServiceOne service2 = (TestServiceOne)li.next();

        // these should be the same proxy
        AssertionService.assertSame(this, "Unexpected proxy object returned from sublist", service, service2);
        try {
            // if this call succeeds, this is a failure, and will be raised as such
            AssertionService.assertFalse(this, "Unexpected service test result", service.testOne());
        } catch (ServiceUnavailableException e) {
            // We expect to get here
        }

        // this iterator should now return false because everything has gone
        AssertionService.assertFalse(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());
        AssertionService.assertFalse(this, "Unexpected result from ref collection ListIterator.hasNext()", li.hasNext());

        // now repeat the tests above against the empty list
        AssertionService.assertFalse(this, "Unexpected contains() result", injectedList.contains(firstService));

        AssertionService.assertFalse(this, "Unexpected containsAll() result", injectedList.containsAll(testList));
        AssertionService.assertEquals(this, "Unexpected indexOf result", -1, injectedList.indexOf(firstService));
        AssertionService.assertEquals(this, "Unexpected lastIndexOf result", -1, injectedList.lastIndexOf(firstService));
        AssertionService.assertEquals(this, "Unexpected size() result", 0, injectedList.size());

        // get a fresh iterator
        i = injectedList.iterator();
        // use the indexed form of listIterator now
        li = injectedList.listIterator(0);

        // empty collection, this should also be false
        AssertionService.assertFalse(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());
        AssertionService.assertFalse(this, "Unexpected result from ref collection ListIterator.hasNext()", li.hasNext());
        AssertionService.assertFalse(this, "Unexpected result from ref collection ListIterator.hasPrevious()", li.hasPrevious());
        AssertionService.assertEquals(this, "Unexpected result from ref collection ListIterator.nextIndex()", 0, li.nextIndex());
        AssertionService.assertEquals(this, "Unexpected result from ref collection ListIterator.previousIndex()", -1, li.previousIndex());
        // register a single service instance
        serviceManager.registerService("ServiceOneA");

        // we've added one item, but this should still be false because of the previous call
        AssertionService.assertFalse(this, "Unexpected result from ref collection Iterator.hasNext()", i.hasNext());

        // this collection now has one item in it...do a few more method tests

        // this is the first item proxy
        firstService = (TestServiceOne)injectedList.get(0);

        testList = new ArrayList();
        testList.add(firstService);
        // these should be equal
        AssertionService.assertTrue(this, "Unexpected result from ref collection equals", injectedList.equals(testList));

        Object[] objArray = injectedList.toArray();

        AssertionService.assertEquals(this, "Unexpected array size from toArray()", 1, objArray.length);
        AssertionService.assertSame(this, "Unexpected proxy object returned from sublist", firstService, objArray[0]);

        TestServiceOne[] serviceArray = (TestServiceOne[])injectedList.toArray(new TestServiceOne[0]);

        AssertionService.assertEquals(this, "Unexpected array size from toArray()", 1, serviceArray.length);
        AssertionService.assertSame(this, "Unexpected proxy object returned from sublist", firstService, serviceArray[0]);

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
        // one last test.  A remove operation should throw an exception
        try {
            i.remove();
            AssertionService.fail(this, "Iterator.remove() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }
        // now some immutability tests for the list iterator
        li = injectedList.listIterator(0);

        li.hasNext();

        try {
            li.remove();
            AssertionService.fail(this, "ListIterator.remove() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        try {
            li.add("ABC");
            AssertionService.fail(this, "ListIterator.add() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        try {
            li.set("ABC");
            AssertionService.fail(this, "ListIterator.set() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        // now immutablity tests for the rest of the list methods

        try {
            injectedList.add("ABC");
            AssertionService.fail(this, "List.add() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        try {
            injectedList.add(0, "ABC");
            AssertionService.fail(this, "List.add() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        try {
            injectedList.addAll(testList);
            AssertionService.fail(this, "List.addAll() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        try {
            injectedList.addAll(0, testList);
            AssertionService.fail(this, "List.addAll() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        try {
            injectedList.clear();
            AssertionService.fail(this, "List.clear() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        try {
            injectedList.remove(0);
            AssertionService.fail(this, "List.remove() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        try {
            injectedList.remove("ABC");
            AssertionService.fail(this, "List.remove() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        try {
            injectedList.removeAll(testList);
            AssertionService.fail(this, "List.removeAll() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        try {
            injectedList.retainAll(testList);
            AssertionService.fail(this, "List.retainAll() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        try {
            injectedList.set(0, "ABC");
            AssertionService.fail(this, "List.set() did not throw exception");
        } catch (UnsupportedOperationException e) {
            // expect to reach here
        }

        super.init();
    }
}

