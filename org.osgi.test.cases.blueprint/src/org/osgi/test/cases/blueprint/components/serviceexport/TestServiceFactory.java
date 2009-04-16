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

package org.osgi.test.cases.blueprint.components.serviceexport;

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.TestGoodService;


/**
 * Concrete target for a service reference.  This is a subclass of our base
 * test service to have a deeper hierarchy for service registrations.  This implements
 * all of the interface methods plus an additional concrete method that can be accessed.
 * This is a subclass of the base test component so we can track creation events when
 * lazy initialization is used.
 */
public class TestServiceFactory extends BaseTestComponent implements ServiceFactory {
    // the instance qualifier we add to each instantiated child service
    protected int instanceCount;

    public TestServiceFactory(String componentId) {
        super(componentId);
    }

    public Object getService(Bundle bundle, ServiceRegistration registration) {
        String newId = componentId + "_" + instanceCount++;

        Hashtable props = new Hashtable();
        props.put("instance.id", newId);
        // send a GET event for this.
        AssertionService.sendEvent(this, AssertionService.SERVICE_FACTORY_GET, props);
        // create an instance using a counter so we keep track of this
        return new TestGoodService(newId);
    }

    public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
        Hashtable props = new Hashtable();
        props.put("instance.id", ((BaseTestComponent)service).getComponentId());
        // send a UNGET event for this.
        AssertionService.sendEvent(this, AssertionService.SERVICE_FACTORY_UNGET, props);
    }
}

