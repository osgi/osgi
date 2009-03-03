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

package org.osgi.test.cases.blueprint.components.serviceimport;

import java.util.Iterator;
import java.util.Collection;

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
 * Test for injection of an empty List of services no services are available.  This
 * will also register a service using the service manager to see that the new
 * registration is picked up.
 */
public class ReferenceCollectionSortChecker extends ReferenceCollectionChecker {
    // our expected sort order (provided as a property)
    protected String[] sortOrder;

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

        validateSortOrder(i, order);
    }
}

