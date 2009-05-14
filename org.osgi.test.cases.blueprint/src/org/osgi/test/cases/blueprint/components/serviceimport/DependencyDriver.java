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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.ServiceManager;
import org.osgi.test.cases.blueprint.services.TestServiceOne;
import org.osgi.test.cases.blueprint.services.TestServiceTwo;
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
    protected Collection     refCollection;

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
     * Inject a reference reference collection
     *
     * @param service The service instance
     */
    public void setCollection(Collection services) {
        refCollection = services;
    }


    /**
     * Retrieve the first service from an injected collection.
     *
     * @return The service instance, or null if the collection is
     *         empty.
     */
    public TestServiceOne getCollectionService() {
        Iterator i = refCollection.iterator();
        if (!i.hasNext()) {
            return null;
        }
        // return the first item
        return (TestServiceOne)i.next();
    }

    /**
     * Some dependency drivers also double as listners, so have this base methods
     * available.
    */
    protected void bind(Class serviceInterface, Map serviceProperties)
  {
        Hashtable props = new Hashtable();
        props.putAll(serviceProperties);
        props.put("service.interface.name", serviceInterface.getName());
        props.put("service.listener.type", "interface");
        AssertionService.sendEvent(this, AssertionService.SERVICE_BIND, props);
    }

    protected void unbind(Class serviceInterface, Map serviceProperties) {
        Hashtable props = new Hashtable();
        props.putAll(serviceProperties);
        props.put("service.interface.name", serviceInterface.getName());
        props.put("service.listener.type", "interface");
        AssertionService.sendEvent(this, AssertionService.SERVICE_UNBIND, props);
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

