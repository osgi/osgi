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

package org.osgi.test.cases.blueprint.framework;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.StringValueDescriptor;
import org.osgi.test.cases.blueprint.services.ValueDescriptor;

public class ReferenceComponentValidator extends MetadataValidator {
    // the target componentId.
    protected String componentId;
    // the service instance interfaces
    protected Class[] interfaces;

    public ReferenceComponentValidator(String componentId, Class iface) {
        this(componentId, new Class[] { iface });
    }

    public ReferenceComponentValidator(String componentId, Class[] interfaces) {
        this.componentId = componentId;
        this.interfaces = interfaces;
    }

    public void validate(BundleContext testContext) throws Exception {
        // ensure we have everything initialized
        super.validate(testContext);
        // the value must be equal

        Object componentObject = blueprintMetadata.getComponent(componentId);
        assertNotNull("Null reference component found for " + componentId, componentObject);
        for (int i = 0; i < interfaces.length; i++) {
            assertTrue("Service reference object for " + componentId + " does not implement " + interfaces[i].getName(),
                interfaces[i].isInstance(componentObject));
        }
    }
}


