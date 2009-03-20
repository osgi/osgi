/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.blueprint.java5.components.injection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import org.osgi.test.cases.blueprint.components.serviceimport.DependencyDriver;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.ManagedService;
import org.osgi.test.cases.blueprint.services.ServiceManager;
import org.osgi.test.cases.blueprint.services.TestGoodService;
import org.osgi.test.cases.blueprint.services.TestGoodServiceSubclass;
import org.osgi.test.cases.blueprint.services.TestServiceAllSubclass;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.TestServiceTwo;
import org.osgi.test.cases.blueprint.services.TestServiceTwoSubclass;


/**
 * Concrete target for testing import of list reference collections.  This component
 * is instantiated with a set of descriptors that are what it expects to see in
 * the injected collection and will use that information to validate the result.
 * This is just a base class for creating these tests.  Individual tests will
 * operate by overriding the init method to validate the results.
 */
public class GenericCollectionChecker extends DependencyDriver {
    // the injected set of references.
    protected List<ServiceReference> injectedList;
    // the injected set of references.
    protected Set<ServiceReference> injectedSet;
    // an injected collection item
    protected Collection<ServiceReference> injectedCollection;

    // our bundle context (used for requesting service instances)
    protected BundleContext bundleContext;

    protected GenericCollectionChecker(String componentId) {
        super(componentId);
    }

    /**
     * Set the injected reference list.
     *
     * @param l
     */
    public void setList(List<ServiceReference> l) {
        injectedList = l;
    }

    /**
     * Set the injected reference list.
     *
     * @param l
     */
    public void setSet(Set<ServiceReference> l) {
        injectedSet = l;
    }

    /**
     * Set the injected reference list.
     *
     * @param l
     */
    public void setCollection(Collection<ServiceReference> l) {
        injectedCollection = l;
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
            ServiceReference service = (ServiceReference)injectedList.get(i);
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
     * Validate a collection injected as a collection
     *
     * @param expected The expected set.
     */
    protected void validateCollection(ManagedService[] expected) {
        validateCollection(expected, injectedCollection);
    }

    /**
     * Validate a collection of service instances.
     *
     * @param expected The expected set of services.
     * @param source   The collection source.
     */
    protected void validateCollection(ManagedService[] expected, Collection<ServiceReference> source) {
        AssertionService.assertNotNull(this, "Null collection reference injected", source);
        AssertionService.assertEquals(this, "Incorrect reference collection size", expected.length, source.size());
        int i = 0;
        Iterator<ServiceReference> iterator = source.iterator();
        while (iterator.hasNext()) {
            // this can only be a ServiceReference
            ServiceReference service = iterator.next();
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
    protected ManagedService locateMatchingService(ServiceReference service, ManagedService[] expected) {
        for (int i = 0; i < expected.length; i++) {
            // have the managed service do the checking
            if (expected[i].isService(service)) {
                return expected[i];
            }
        }
        return null;
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

    /**
     * This is fairly generic, so validate whichever one
     * we got injected with.
     *
     * @param expected The expected set of services.
     */
    protected void validate(ManagedService[] expected) {
        if (injectedList != null) {
            validateList(expected);
        }
        else if (injectedSet != null) {
            validateSet(expected);
        }
        else {
            validateCollection(expected);
        }
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
        // this should be an empty list now
        validate(serviceManager.getActiveServices());
        // now register all of the services in our list
        serviceManager.registerServices();
        // now validate against the active services
        validate(serviceManager.getActiveServices());
        // now make them all go away
        serviceManager.unregisterServices();
        // this should be back to an empty list
        validate(new ManagedService[0]);
        // send out the initialized message to indicate we've ocmpleted.
        super.init();
    }
}

