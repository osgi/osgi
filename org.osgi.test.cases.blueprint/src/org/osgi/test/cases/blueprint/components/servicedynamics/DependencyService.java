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

package org.osgi.test.cases.blueprint.components.servicedynamics;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.TestServiceDynamicsInterface;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.TestServiceTwo;


/**
 * A base class for testing service dynamics.  This component can be injected with
 * service instances, and can also export some of our service interfaces.  Other than
 * that, this really does nothing of note.
 */
public class DependencyService extends BaseTestComponent implements TestServiceDynamicsInterface {
    // two test services to test reference validity
    protected TestServiceOne serviceOne;
    protected TestServiceTwo serviceTwo;

    protected DependencyService(String componentId) {
        super(componentId);
    }

    /**
     * Inject a reference to TestServiceOne into this component;
     *
     * @param service The service instance
     */
    public void setOne(TestServiceOne service) {
        AssertionService.assertNotNull(this, "Null service reference injected", service);
        serviceOne = service;
    }

    /**
     * Inject a reference to TestServiceTwo into this component;
     *
     * @param service The service instance
     */
    public void setTwo(TestServiceTwo service) {
        AssertionService.assertNotNull(this, "Null service reference injected", service);
        serviceTwo = service;
    }

    /**
     * A method that can be called for test verification purposes.
     *
     * @return always returns true
     */
    public boolean ping() {
        return true;
    }
}

