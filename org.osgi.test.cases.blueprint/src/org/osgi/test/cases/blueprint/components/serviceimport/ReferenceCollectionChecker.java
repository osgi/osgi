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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.ManagedService;
import org.osgi.test.cases.blueprint.services.TestServiceOne;


/**
 * Concrete target for testing import of list reference collections.  This component
 * is instantiated with a set of descriptors that are what it expects to see in
 * the injected collection and will use that information to validate the result.
 * This is just a base class for creating these tests.  Individual tests will
 * operate by overriding the init method to validate the results.
 */
public class ReferenceCollectionChecker extends DependencyDriver {
    // the injected set of references.
    protected List injectedList;
    // the injected set of references.
    protected Set injectedSet;
    // indicates where this is a list of instances or references.
    protected boolean usesReferences = false;
    // our bundle context (used for requesting service instances)
    protected BundleContext bundleContext;

    // our expected sort order (provided as a property)
    protected String[] sortOrder;

    protected ReferenceCollectionChecker(String componentId) {
        super(componentId);
    }

    /**
     * Set the injected reference list.
     *
     * @param l
     */
    public void setList(List l) {
        injectedList = l;
    }

    /**
     * Set the injected reference list.
     *
     * @param l
     */
    public void setSet(Set l) {
        injectedSet = l;
    }


    /**
     * Set the sort order.  This also tests secondarily tests the
     * ability to convert a List into an array of Strings.
     *
     * @param order  The injected order.
     */
    public void setSortOrder(String[] order) {
        sortOrder = order;
    }


    public void setUseReferences(boolean b) {
        usesReferences = b;
    }

    public void setBundleContext(BundleContext context) {
        this.bundleContext = context;
    }

    /**
     * Validate a list of services against an expectation.
     *
     * @param expected The expected set of services.
     */
    protected void validateList(ManagedService[] expected) {

        validateCollection(expected, injectedList);
        // for lists, we also validate the iteration using the get method.
        for (int i = 0; i < injectedList.size(); i++) {
            TestServiceOne service = (TestServiceOne)injectedList.get(i);
            if (locateMatchingService(service, expected) == null) {
                AssertionService.fail(this, "List.get(" + i + ") returned unexpected service");
                return;
            }
        }
    }

    /**
     * Validate the injected set collection against
     * expected criteria.
     *
     * @param expected The expected set.
     */
    protected void validateSet(ManagedService[] expected) {
        validateCollection(expected, injectedSet);
    }

    /**
     * Validate a collection of service instances.
     *
     * @param expected The expected set of services.
     * @param source   The collection source.
     */
    protected void validateCollection(ManagedService[] expected, Collection source) {
        AssertionService.assertNotNull(this, "Null collection reference injected", source);
        AssertionService.assertEquals(this, "Incorrect reference collection size", expected.length, source.size());
        int i = 0;
        Iterator iterator = source.iterator();
        while (iterator.hasNext()) {
            // this can be either a service instance or a ServiceReference
            Object service = iterator.next();
            if (locateMatchingService(service, expected) == null) {
                AssertionService.fail(this, "Unexpected service instance located in list: " + service);
            }
            i++;      // count the number of services we process
        }
        AssertionService.assertEquals(this, "Incorrect number of iterated services", expected.length, i);
    }


    /**
     * Search a list of managed service elements looking for a service
     * match.
     *
     * @param service  The service instance.
     * @param expected The list of expected elements.
     *
     * @return The matching ManagedService element.
     */
    protected ManagedService locateMatchingService(Object service, ManagedService[] expected) {
        if (usesReferences) {
            for (int i = 0; i < expected.length; i++) {
                AssertionService.assertTrue(this, "Service reference instance expected in ref collection", service instanceof ServiceReference);
                // have the managed service do the checking
                if (expected[i].isService((ServiceReference)service)) {
                    return expected[i];
                }
            }
            return null;
        }
        else {
            for (int i = 0; i < expected.length; i++) {
                // have the managed service do the checking
                if (expected[i].isService(service)) {
                    return expected[i];
                }
            }
            return null;
        }
    }


    /**
     * Validate the sorting order for items returned from a
     * reference collection iterator.  The list of names is the
     * set of service names we expect to see, in the expected order.
     * Failures will be returned for mismatches in count and ordering.
     *
     * @param i      The iterator supplying the objects.  This might be positioned
     *               past the first item for some service dynamics tests.
     * @param names  The array of names we expect, in the indicated order.
     */
    protected void validateSortOrder(Iterator i, String[] names) {
        int counter = 0;
        while (i.hasNext()) {
            AssertionService.assertTrue(this, "Mismatch on iteration size", counter < names.length);
            checkServiceName(i.next(), names[counter++]);
        }
        AssertionService.assertEquals(this, "Truncated iteration sequence", counter, names.length);
    }


    /**
     * Check for a service name match during sort tests.
     *
     * @param service The service object, which may be a service instance or
     *                a ServiceReference.
     * @param name
     */
    protected void checkServiceName(Object service, String name) {
        if (usesReferences) {
            Object checkedService = bundleContext.getService((ServiceReference)service);
            AssertionService.assertEquals(this, "Mismatch on sorted name order", name, ((TestServiceOne)checkedService).getServiceName());
            bundleContext.ungetService((ServiceReference)service);
        }
        else {
            AssertionService.assertEquals(this, "Mismatch on sorted name order", name, ((TestServiceOne)service).getServiceName());
        }
    }


    /**
     * Create an array by slicing out an element at the given
     * position.
     *
     * @param array  The source array.
     * @param index  The index to remove.
     *
     * @return A new, smaller array with the target element deleted.
     */
    protected String[] deleteElement(String[] array, int index) {
        String[] newArray = new String[array.length-1];
        if (index > 0) {
            // copy the front part
            System.arraycopy(array, 0, newArray, 0, index);
        }
        // and any potential trailing part
        if (index < array.length - 1) {
            System.arraycopy(array, index + 1, newArray, index, array.length - (index + 1));
        }
        return newArray;
    }


    /**
     * Create an array by slicing out a set of elements.
     *
     * @param array  The source array.
     * @param index  The slice starting index.
     * @param size   The size of the slice.
     *
     * @return A new, smaller array created from the slice positions.
     */
    protected String[] slice(String[] array, int index, int size) {
        String[] newArray = new String[size];
        System.arraycopy(array, index, newArray, 0, size);
        return newArray;
    }


    /**
     * some methods for doubling as service listeners.
     */
    public void bind(TestServiceOne service, Map serviceProperties) {
        // the spec says the service reference is guaranteed to be good for
        // the life of the call, so test this out by calling the service.
        AssertionService.assertTrue(this, "Bad service call", service.testOne());
        bind(TestServiceOne.class, serviceProperties);
    }

    public void unbind(TestServiceOne service, Map serviceProperties) {
        // the spec says the service reference is guaranteed to be good for
        // the life of the call, so test this out by calling the service.
        AssertionService.assertTrue(this, "Bad service call", service.testOne());
        unbind(TestServiceOne.class, serviceProperties);
    }
}
