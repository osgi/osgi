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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.ServiceReference;

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
import org.osgi.test.cases.blueprint.services.TestUtil;


/**
 * A base class for implementing components that test service dynamics by causing
 * changes in the registration state of services.  This class is injected with a
 * ServiceManager reference that can be used to register/deregister services.
 * The subclasses use the init() method to trigger state changes that the test driver
 * code will validate via the events the state changes cause (or possibly even
 * directly in the init() code).
 */
public class DependencyDriver extends BaseTestComponent {
    // Our injected ServiceManager reference.  This is used to test service
    // dynamics of reference collections by allowing us to register/deregister
    // components defined in other bundles.
    protected ServiceManager serviceManager;
    // two test services to test reference validity
    protected TestServiceOne serviceOne;
    protected TestServiceTwo serviceTwo;

    protected DependencyDriver(String componentId) {
        super(componentId);
    }

    /**
     * Set the ServiceManager if this test requires it.
     *
     * @param manager The ServiceManager instance.
     */
    public void setServiceManager(ServiceManager manager) {
        serviceManager = manager;
    }

    /**
     * Inject a reference to TestServiceOne into this component;
     *
     * @param service The service instance
     */
    public void setOne(TestServiceOne service) {
        serviceOne = service;
    }

    /**
     * Inject a reference to TestServiceTwo into this component;
     *
     * @param service The service instance
     */
    public void setTwo(TestServiceTwo service) {
        serviceTwo = service;
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
    }

    /**
     * Some dependency drivers also double as listners, so have this base methods
     * available.
    */
    protected void bind(Class serviceInterface, Map serviceProperties) {
        Hashtable props = new Hashtable();
        props.putAll(serviceProperties);
        props.put("service.interface.name", serviceInterface.getName());
        props.put("service.listener.type", "interface");
        AssertionService.sendEvent(this, AssertionService.SERVICE_BIND);
    }

    protected void unbind(Class serviceInterface, Map serviceProperties) {
        Hashtable props = new Hashtable();
        props.putAll(serviceProperties);
        props.put("service.interface.name", serviceInterface.getName());
        props.put("service.listener.type", "interface");
        AssertionService.sendEvent(this, AssertionService.SERVICE_UNBIND);
    }

    protected void bindReference(ServiceReference ref) {
        Hashtable props = new Hashtable();
        props.putAll(TestUtil.getProperties(ref));
        props.put("service.listener.type", "reference");
        AssertionService.sendEvent(this, AssertionService.SERVICE_BIND, props);
    }

    protected void unbindReference(ServiceReference ref) {
        Hashtable props = new Hashtable();
        props.putAll(TestUtil.getProperties(ref));
        props.put("service.listener.type", "reference");
        AssertionService.sendEvent(this, AssertionService.SERVICE_UNBIND, props);
    }
}

