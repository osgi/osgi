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

import junit.framework.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Validate that a particular set of services is no longer registered.
 */
public class ServiceUnregistrationValidator extends Assert implements TestValidator, BundleAware {
    // the bundle the service should be part of
    protected Bundle bundle;
    // the name of the interface to validate
    protected String[] interfaceNames;
    // any filter string to use
    protected String filter;

    public ServiceUnregistrationValidator(String interfaceName, String filter) {
        this(new String[] { interfaceName }, filter);
    }

    public ServiceUnregistrationValidator(String[] interfaceNames, String filter) {
        this.interfaceNames = interfaceNames;
        this.filter = filter;
    }

    public ServiceUnregistrationValidator(Class interfaceClass, String filter) {
        this(new Class[] { interfaceClass }, filter);
    }

    public ServiceUnregistrationValidator(Class[] interfaceClasses, String filter) {
        this.filter = filter;
        this.interfaceNames = new String[interfaceClasses.length];

        for (int i = 0; i < interfaceClasses.length; i++) {
            interfaceNames[i] = interfaceClasses[i].getName();
        }
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
        // we can register under multiple interface names, so we validate each one
        // in the list.
        for (int i = 0; i < interfaceNames.length; i++) {
            validateService(testContext, interfaceNames[i]);
        }
    }


    /**
     * Validate a single service Unregistration.
     *
     * @param testContext
     *               The bundle test context.
     * @param interfaceName
     *               The interface name we're looking for.
     *
     * @exception Exception
     */
    protected void validateService(BundleContext testContext, String interfaceName) throws Exception {
        ServiceReference[] refs = testContext.getServiceReferences(interfaceName, filter);
        // Generally, we expect this to be null.  However, for some tests, there might be
        // services registered from multiple bundles, so make sure there's not one from our
        // target bundle.
        if (refs != null) {
            for (int i = 0; i < refs.length; i++) {
                assertTrue("Service " + interfaceName + " service located for bundle " + bundle.getSymbolicName(), refs[i].getBundle() != bundle);
            }
        }
    }
}

