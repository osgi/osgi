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

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.ServiceReference;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.TestUtil;


/**
 * A base class for different reference listeners used to validate
 * reference registration/deregistration calls.
 */
public class ServiceListener extends BaseTestComponent {

    public ServiceListener(String componentId) {
        super(componentId);
    }

    protected void bind(Class serviceInterface, Map serviceProperties) {
        Hashtable props = new Hashtable();
        if (serviceProperties != null) {
            props.putAll(serviceProperties);
        }
        props.put("service.interface.name", serviceInterface.getName());
        props.put("service.listener.type", "interface");
        AssertionService.sendEvent(this, AssertionService.SERVICE_BIND, props);
    }

    protected void unbind(Class serviceInterface, Map serviceProperties) {
        Hashtable props = new Hashtable();
        if (serviceProperties != null) {
            props.putAll(serviceProperties);
        }
        props.put("service.interface.name", serviceInterface.getName());
        props.put("service.listener.type", "interface");
        AssertionService.sendEvent(this, AssertionService.SERVICE_UNBIND, props);
    }

    public void bindReference(ServiceReference ref) {
        Hashtable props = new Hashtable();
        props.putAll(TestUtil.getProperties(ref));
        props.put("service.listener.type", "reference");
        AssertionService.sendEvent(this, AssertionService.SERVICE_BIND, props);
    }

    public void unbindReference(ServiceReference ref) {
        Hashtable props = new Hashtable();
        props.putAll(TestUtil.getProperties(ref));
        props.put("service.listener.type", "reference");
        AssertionService.sendEvent(this, AssertionService.SERVICE_UNBIND, props);
    }
}

