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

package org.osgi.test.cases.blueprint.services;

import java.lang.Comparable;

/**
 * Concrete target for a service reference.  This is a subclass of our base
 * test service to have a deeper hierarchy for service registrations.  This implements
 * all of the interface methods plus an additional concrete method that can be accessed.
 * This is a subclass of the base test component so we can track creation events when
 * lazy initialization is used.
 */
public class TestGoodService extends BaseTestComponent implements TestServiceAllSubclass, Comparable {
    // a service identifier that can be used to identify different service
    // instances.  This is also used for testing service sorting.
    public int serviceId = 0;

    /**
     * A null argument constructor is required for exporting
     * as a service interface.
     */
    public TestGoodService() {
        super("ProxyTestGoodService");
    }

    public TestGoodService(String componentId) {
        super(componentId);
    }

    public TestGoodService(String componentId, int serviceId) {
        super(componentId);
        this.serviceId = serviceId;
    }

    /**
     * A method that can be called for test verification purposes.  This
     * method is part of the concrete class, not the interfaces.
     *
     * @return always returns true
     */
    public boolean testGood() {
        return true;
    }

    /**
     * A method that can be called for test verification purposes.
     *
     * @return always returns true
     */
    public boolean testOne() {
        return true;
    }

    /**
     * A method that can be called for test verification purposes.
     *
     * @return always returns true
     */
    public boolean testTwo() {
        return true;
    }
    /**
     * A method that can be called for test verification purposes.
     *
     * @return always returns true
     */
    public boolean testTwoSubclass() {
        return true;
    }

    /**
     * A method that can be called for test verification purposes.
     *
     * @return always returns true
     */
    public boolean testAllSubclass() {
        return true;
    }

    /**
     * This is used to allow multiple services to return a unique
     * service id.  This is used for testing reference collections and
     * also for testing reference list sorting.
     *
     * @return An identifier for this service instance.
     */
    public int getServiceId() {
        return serviceId;
    }

    /**
     * This is used for service collection matching.  This is generally
     * the component id of the service.  When testing collections, this
     * must be unique among the collections.
     *
     * @return The string name for the service instance.
     */
    public String getServiceName() {
        return componentId;
    }

    /**
     * Perform an equals comparison on this service instance.
     *
     * @param other  The other service to compare.
     *
     * @return true if the two services are considered equivalent.
     */
    public boolean equals(Object other) {
        // compare based on the service id.
        if (other instanceof TestServiceOne) {
            return componentId.equals(((TestServiceOne)other).getServiceName());
        }
        return false;
    }

    /**
     * The compareTo() method required by the Comparable interface.
     *
     * @param other
     *
     * @return
     */
    public int compareTo(Object other) {
        return serviceId - ((TestServiceOne)other).getServiceId();
    }
}

