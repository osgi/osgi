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

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.TestGoodService;
import org.osgi.test.cases.blueprint.services.TestGoodServiceSubclass;
import org.osgi.test.cases.blueprint.services.TestServiceAllSubclass;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.TestServiceTwo;
import org.osgi.test.cases.blueprint.services.TestServiceTwoSubclass;


/**
 * A base class for implementing components that test service dynamics by causing
 * changes in the registration state of services.  This class is injected with a
 * ServiceManager reference that can be used to register/deregister services.
 * The subclasses use the init() method to trigger state changes that the test driver
 * code will validate via the events the state changes cause (or possibly even
 * directly in the init() code).
 */
public class ServiceReferenceChecker extends BaseTestComponent {

    public ServiceReferenceChecker(String componentId) {
        super(componentId);
    }

    public ServiceReferenceChecker(String componentId, TestGoodService service) {
        super(componentId);
        AssertionService.assertNotNull(this, "Null TestGoodService service object injected", service);
        checkService(service.testGood());
    }

    public ServiceReferenceChecker(String componentId, TestServiceTwoSubclass service) {
        super(componentId);
        AssertionService.assertNotNull(this, "Null TestServiceTwoSubclass service object injected", service);
        checkService(service.testTwoSubclass());
    }

    public ServiceReferenceChecker(String componentId, TestServiceOne service) {
        super(componentId);
        AssertionService.assertNotNull(this, "Null TestServiceOne service object injected", service);
        checkService(service.testOne());
    }

    public ServiceReferenceChecker(String componentId, TestServiceTwo service) {
        super(componentId);
        AssertionService.assertNotNull(this, "Null TestServiceTwo service object injected", service);
        checkService(service.testTwo());
    }

    /**
     * Inject a reference to TestGoodService into this component;
     *
     * @param service The service instance
     */
    public void setGood(TestGoodService service) {
        AssertionService.assertNotNull(this, "Null TestGoodService service object injected", service);
        checkService(service.testGood());
    }

    /**
     * Inject a reference to TestGoodService into this component;
     *
     * @param service The service instance
     */
    public void setGoodSubclass(TestGoodServiceSubclass service) {
        AssertionService.assertNotNull(this, "Null TestGoodServiceSubclass service object injected", service);
        checkService(service.testGoodSubclass());
    }

    /**
     * Inject a reference to TestServiceOne into this component;
     *
     * @param service The service instance
     */
    public void setOne(TestServiceOne service) {
        AssertionService.assertNotNull(this, "Null TestServiceOne service object injected", service);
        checkService(service.testOne());
    }

    /**
     * Inject a reference to TestServiceTwo into this component;
     *
     * @param service The service instance
     */
    public void setTwo(TestServiceTwo service) {
        AssertionService.assertNotNull(this, "Null TestServiceTwo service object injected", service);
        checkService(service.testTwo());
    }

    /**
     * Inject a reference to TestServiceAllSubclass into this component;
     *
     * @param service The service instance
     */
    public void setAllSubclass(TestServiceAllSubclass service) {
        AssertionService.assertNotNull(this, "Null TestServiceAllSubclass service object injected", service);
        checkService(service.testAllSubclass());
    }

    /**
     * Inject a reference to TestServiceTwoSubclass into this component;
     *
     * @param service The service instance
     */
    public void setTwoSubclass(TestServiceTwoSubclass service) {
        AssertionService.assertNotNull(this, "Null TestServiceTwoSubclass service object injected", service);
        checkService(service.testTwoSubclass());
    }


    /**
     * Utility method for validating the service implementation.
     *
     * @param result The result of the service call.
     */
    protected void checkService(boolean result) {
        if (result) {
            AssertionService.sendEvent(this, AssertionService.SERVICE_SUCCESS);
        }
        else {
            AssertionService.assertTrue(this, "Incorrect service result received", result);
        }
    }
}

