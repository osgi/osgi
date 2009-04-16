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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

/**
 * Concrete target for testing import of ServiceReferences.
 * Since we require our BundleContext to check the ServiceReference
 * validity, this test will also serve as a test for Bundle and BundleReference
 * injection.
 */
public class NullServiceReferenceInjection extends BaseTestComponent {
    // The injected BundleContext
    protected BundleContext context;
    // the injected Bundle
    protected Bundle bundle;

    public NullServiceReferenceInjection(String componentId, BundleContext context, Bundle bundle) {
        super(componentId);
        this.context = context;
        this.bundle = bundle;
        AssertionService.assertNotNull(this, "Null BundleContext received", context);
        AssertionService.assertNotNull(this, "Null Bundle received", bundle);
        AssertionService.assertEquals(this, "Bundle/BundleContext mismatch", bundle, context.getBundle());
    }

    public void setReference(ServiceReference ref) throws Throwable {
        AssertionService.assertNull(this, "non-null ServiceReference received", ref);
        AssertionService.sendEvent(this, AssertionService.SERVICE_SUCCESS);
    }
}

