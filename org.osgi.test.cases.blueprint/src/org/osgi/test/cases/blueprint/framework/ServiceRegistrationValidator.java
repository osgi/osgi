/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.blueprint.framework;

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.TestUtil;

import junit.framework.Assert;

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
        assertNotNull("No " + interfaceName + " service located for bundle " + bundle.getSymbolicName(), refs);
        // We might have services registered from multiple bundles, so scan this list
        // looking for one from the target bundle and validate that information.
        for (int i = 0; i < refs.length; i++) {
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
        }
        // none found with the target bundle
        fail("No " + interfaceName + " service located for bundle " + bundle.getSymbolicName());
    }
}

