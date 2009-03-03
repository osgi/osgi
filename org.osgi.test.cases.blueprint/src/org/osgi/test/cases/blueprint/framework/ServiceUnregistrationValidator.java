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

