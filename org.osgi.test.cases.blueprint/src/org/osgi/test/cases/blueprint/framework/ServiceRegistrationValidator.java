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

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.TestUtil;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

/**
 * Validate that a target service is registered at the requested
 * test phase boundary.
 */
public class ServiceRegistrationValidator extends Assert implements TestValidator, BundleAware {
    // the bundle the service should be part of
    protected Bundle bundle;
    // the name of the interface to validate
    protected String[] interfaceNames;
    // a component name this service should be registered under (optional)
    protected String componentName;
    // any filter string to use
    protected String filter;
    // and service properties to validate on
    protected Dictionary serviceProperties;


    /**
     * Create a registration validator.
     *
     * @param interfaceName
     *               The target interface name.
     * @param componentName
     *               The component name we expect to find registered as a service
     *               property (can be null for an inner component).
     */
    public ServiceRegistrationValidator(String interfaceName, String componentName) {
        this(new String[] { interfaceName }, componentName, null, null);
    }


    /**
     * Create a registration validator.
     *
     * @param interfaceClass
     *               The target interface class.
     * @param componentName
     *               The component name we expect to find registered as a service
     *               property (can be null for an inner component).
     */
    public ServiceRegistrationValidator(Class interfaceClass, String componentName) {
        this(new Class[] { interfaceClass }, componentName, null, null);
    }


    /**
     * Create a registration validator.
     *
     * @param interfaceNames
     *               The list of interface names we expect to find this registered under.
     * @param componentName
     *               The component name we expect to find registered as a service
     *               property (can be null for an inner component).
     */
    public ServiceRegistrationValidator(String[] interfaceNames, String componentName) {
        this(interfaceNames, componentName, null, null);
    }


    /**
     * Create a registration validator.
     *
     * @param interfaceClasses
     *               The list of interfaces we expect to find this registered under.
     * @param componentName
     *               The component name we expect to find registered as a service
     *               property (can be null for an inner component).
     */
    public ServiceRegistrationValidator(Class[] interfaceClasses, String componentName) {
        this(interfaceClasses, componentName, null, null);
    }


    /**
     * Create a registration validator.
     *
     * @param interfaceName
     *               The expected interface name.
     * @param componentName
     *               The component name we expect to find registered as a service
     *               property (can be null for an inner component).
     * @param filter A lookup filter for the service.
     * @param props  A set of properties we expect to have set on the registration.
     */
    public ServiceRegistrationValidator(String interfaceName, String componentName, String filter, Dictionary props) {
        this(new String[] { interfaceName }, componentName, filter, props);
    }


    /**
     * Create a registration validator.
     *
     * @param interfaceClass
     *               The expected interface.
     * @param componentName
     *               The component name we expect to find registered as a service
     *               property (can be null for an inner component).
     * @param filter A lookup filter for the service.
     * @param props  A set of properties we expect to have set on the registration.
     */
    public ServiceRegistrationValidator(Class interfaceClass, String componentName, String filter, Dictionary props) {
        this(new Class[] { interfaceClass }, componentName, filter, props);
    }


    /**
     * Create a registration validator.
     *
     * @param interfaceNames
     *               The set of interface names we expect to find this under.
     * @param componentName
     *               The component name we expect to find registered as a service
     *               property (can be null for an inner component).
     * @param filter A lookup filter for the service.
     * @param serviceProperties
     */
    public ServiceRegistrationValidator(String[] interfaceNames, String componentName, String filter, Dictionary serviceProperties) {
        this.bundle = null;   // we'll resolve this later
        this.interfaceNames = interfaceNames;
        this.componentName = componentName;
        this.filter = filter;
        this.serviceProperties = serviceProperties;
    }


    /**
     * Create a registration validator.
     *
     * @param interfaceClasses
     *               The set of interfaces we expect to find this under.
     * @param componentName
     *               The component name we expect to find registered as a service
     *               property (can be null for an inner component).
     * @param filter A lookup filter for the service.
     * @param serviceProperties
     */
    public ServiceRegistrationValidator(Class[] interfaceClasses, String componentName, String filter, Dictionary serviceProperties) {
        this.bundle = null;   // we'll resolve this later
        this.interfaceNames = new String[interfaceClasses.length];
        this.componentName = componentName;
        this.filter = filter;
        this.serviceProperties = serviceProperties;

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
     * Validate just a single service registration for an interface name.
     *
     * @param testContext
     *               The test context.
     * @param interfaceName
     *               The target interface name.
     *
     * @exception Exception
     */
    protected void validateService(BundleContext testContext, String interfaceName) throws Exception {
        ServiceReference[] refs = testContext.getServiceReferences(interfaceName, filter);
        assertNotNull("No " + interfaceName + " service located for bundle " + bundle.getSymbolicName() + " with filter " + filter, refs);
        // We might have services registered from multiple bundles, so scan this list
        // looking for one from the target bundle and validate that information.
        for (int i = 0; i < refs.length; i++) {
            try {
                if (bundle == refs[i].getBundle()) {
                    // validate the component name property if one is expected
                    if (componentName != null) {
                        assertEquals("Incorrect component name on exported service for interface " + interfaceName + " in bundle " + bundle.getSymbolicName() + ", service=" +refs[i],
                            componentName, refs[i].getProperty("osgi.service.blueprint.compname"));
                    }

                    // might have service properties to validate also
                    if (serviceProperties != null) {
                        assertTrue("Mismatch on expected service properties for interface " + interfaceName + " in bundle " + bundle.getSymbolicName(),
                            TestUtil.containsAll(serviceProperties, refs[i]));
                    }
                    // we're finished
                    return;
                }
            } catch (AssertionFailedError e) {
                // if we only have one possible match and we got a validation failure, then
                // just rethrow that error.  Otherwise, this means we didn't find the one
                // we're looking for.  If we fail on all of them, we'll give the registration
                // not found error
                if (refs.length == 1) {
                    throw e;
                }
            }
        }
        // none found with the target bundle
        fail("No " + interfaceName + " service located for bundle " + bundle.getSymbolicName());
    }
}

