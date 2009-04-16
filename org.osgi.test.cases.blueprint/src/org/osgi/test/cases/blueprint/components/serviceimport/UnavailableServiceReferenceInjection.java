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

import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

/**
 * Concrete target for testing import of ServiceReferences for a
 * service that is not available (and not required).  This should
 * inject a null value.
 */
public class UnavailableServiceReferenceInjection extends BaseTestComponent {

    public UnavailableServiceReferenceInjection(String componentId) {
        super(componentId);
    }

    public void setReference(ServiceReference ref) {
        AssertionService.assertNull(this, "Non-null ServiceReference received", ref);
        AssertionService.sendEvent(this, AssertionService.SERVICE_SUCCESS);
    }
}

