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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import junit.framework.Assert;

/**
 * Verify that there is no service associated with a bundle in the
 * service registry at the end of a test phase.
 */
public class NoServiceValidator extends Assert implements TestValidator, BundleAware {
    // the bundle the service should be part of
    protected Bundle bundle;
    // the name of the interface to validate
    protected String interfaceName;
    // any filter string to use
    protected String filter;

    /**
     * Create a service unregistration validator.
     *
     * @param interfaceName
     *               The target interface name.
     */
    public NoServiceValidator(String interfaceName) {
        this(interfaceName, null);
    }


    /**
     * Create a service unregistration validator.
     *
     * @param interfaceName
     *               The target interface name.
     * @param filter A string filter for narrowing the service search.
     */
    public NoServiceValidator(String interfaceName, String filter) {
        this.interfaceName = interfaceName;
        this.filter = filter;
    }

    /**
     * Inject the event set's bundler into this validator instance.
     *
     * @param bundle The bundle associated with the hosting event set.
     */
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Perform any additional validation checks at the end of a test phase.
     * This can perform any validation action needed beyond just
     * event verification.  One good use is to ensure that specific
     * services are actually in the services registry or validating
     * the service properties.
     *
     * @param testContext
     *               The BundleContext for the test (used for inspecting the test
     *               environment).
     *
     * @exception Exception
     */
    public void validate(BundleContext testContext) throws Exception {
        ServiceReference[] refs = testContext.getServiceReferences(interfaceName, filter);
        if (refs != null && bundle == refs[0].getBundle()) {
            fail("Unexpected " + interfaceName + " service located for bundle " + bundle.getSymbolicName());
        }
    }
}


