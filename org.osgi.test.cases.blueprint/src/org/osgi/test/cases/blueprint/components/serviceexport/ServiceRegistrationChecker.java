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

import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.TestUtil;

/**
 * Concrete target for testing import of ServiceRegistrations.  This will update
 * the service properties injected ServiceRegistration, which should trigger a
 * MODIFIED service event and be visible to the test controller for verification.
 */
public class ServiceRegistrationChecker extends BaseTestComponent {
    // our injected set of properties to update
    protected Hashtable setProperties;

    public ServiceRegistrationChecker(String componentId, Map props) {
        super(componentId);

        // we need a dictionary to update the service properties.
        setProperties = new Hashtable();
        // copy these entries
        setProperties.putAll(props);
    }

    public void setRegistration(ServiceRegistration reg) {
        ServiceReference ref = reg.getReference();
        AssertionService.sendEvent(this, AssertionService.SERVICE_SUCCESS);
        // copy all of the existing properties
        setProperties.putAll(TestUtil.getProperties(ref));
        reg.setProperties(setProperties);
    }
}

