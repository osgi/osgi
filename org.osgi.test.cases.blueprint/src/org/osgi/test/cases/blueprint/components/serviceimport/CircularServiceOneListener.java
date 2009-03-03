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

import java.util.Map;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.TestServiceOne;


/**
 * A base class for different reference listeners used to validate
 * reference registration/deregistration calls.
 */
public class CircularServiceOneListener extends ServiceOneListener {
    // an injected service we can test in the service listeners
    protected TestServiceOne injectedService;


    public CircularServiceOneListener(String componentId) {
        super(componentId);
    }

    public void setOne(TestServiceOne service) {
        // the service must not be null and the method must return true.
        AssertionService.assertNotNull(this, "Null service reference received", service);
        checkService(service.testOne());
        injectedService = service;
    }

    /**
     * Override of the default bind/unbind methods to
     * verify that our held service reference is also good.
     *
     * @param service
     * @param serviceProperties
     */
    public void bind(TestServiceOne service, Map serviceProperties) {
        // save the reference if we don't have one yet
        if (injectedService == null) {
            injectedService = service;
        }
        // this should be a good service call, always
        AssertionService.assertTrue(this, "Bad service call", service.testOne());
        bind(TestServiceOne.class, serviceProperties);
    }

    public void unbind(TestServiceOne service, Map serviceProperties) {
        // this should be a good service call, always
        AssertionService.assertTrue(this, "Bad service call", service.testOne());
        if (injectedService != null) {
            // test the injected one as well
            AssertionService.assertTrue(this, "Bad service call", injectedService.testOne());
        }
        // clear this out
        injectedService = null;
        unbind(TestServiceOne.class, serviceProperties);
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


