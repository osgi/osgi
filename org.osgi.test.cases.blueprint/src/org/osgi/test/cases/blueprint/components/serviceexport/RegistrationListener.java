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
import java.util.Map;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;


/**
 * A base class for different reference listeners used to validate
 * reference registration/deregistration calls.
 */
public class RegistrationListener extends BaseTestComponent {

    public RegistrationListener(String componentId) {
        super(componentId);
    }

    protected void registered(Class serviceInterface, Map serviceProperties) {
        Hashtable props = new Hashtable();
        props.putAll(serviceProperties);
        props.put("service.interface.name", serviceInterface.getName());
        AssertionService.sendEvent(this, AssertionService.SERVICE_REGISTERED, props);
    }

    protected void unregistered(Class serviceInterface, Map serviceProperties) {
        Hashtable props = new Hashtable();
        props.putAll(serviceProperties);
        props.put("service.interface.name", serviceInterface.getName());
        AssertionService.sendEvent(this, "UNSERVICE_REGISTERED", props);
    }
}

