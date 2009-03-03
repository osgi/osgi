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
import java.util.Map;

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
public class BindUnbindListChecker extends ReferenceCollectionChecker {

    public BindUnbindListChecker(String componentId) {
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
        // ok, all we do is toggle these back and forth and let the
        // listeners handle things
        serviceManager.toggleServices();
        // restored to original state
        serviceManager.toggleServices();
        // and exit in the other state
        serviceManager.toggleServices();
        super.init();
    }


    /**
     * some methods for doubling as service listeners.
     */
    public void bind(TestServiceOne service, Map serviceProperties) {
        // we're working with a collection that should always have a single
        // item on the bind/unbind calls.
        TestServiceOne refService = (TestServiceOne)injectedList.get(0);
        AssertionService.assertTrue(this, "Bad service call", refService.testOne());

        // do normal "I was called broadcast"
        super.bind(service, serviceProperties);
    }

    public void unbind(TestServiceOne service, Map serviceProperties) {
        // we're working with a collection that should always have a single
        // item on the bind/unbind calls.
        TestServiceOne refService = (TestServiceOne)injectedList.get(0);
        AssertionService.assertTrue(this, "Bad service call", refService.testOne());

        // do normal "I was called broadcast"
        super.unbind(service, serviceProperties);
    }
}

