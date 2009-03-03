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
import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.blueprint.context.ModuleContext;
import org.osgi.service.blueprint.context.NoSuchComponentException;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;

import junit.framework.Assert;

/**
 * Validate the constructor metadata for a component built using a
 * factory class.
 */
public class FactoryConstructorValidator extends MetadataValidator {
    // the external component id
    protected String componentId;
    // the factory method name
    protected String factoryMethod;
    // optional name of a factory component
    protected String factoryComponent;
    // the list of expected parameters
    protected TestParameter[] parms;

    /**
     * Validate the factory constructor metadata.
     *
     * @param componentId
     *               The id of the target component.
     * @param factoryMethod
     *               The name of the factory method used to construct the component.
     * @param factoryComponent
     *               The factoryComponent reference id.
     */
    public FactoryConstructorValidator(String componentId, String factoryMethod, String factoryComponent) {
        this(componentId, factoryMethod, factoryComponent, new TestParameter[0]);
    }


    /**
     * Validate the factory constructor metadata.
     *
     * @param componentId
     *               The id of the target component.
     * @param factoryMethod
     *               The name of the factory method used to construct the component.
     * @param factoryComponent
     *               The factoryComponent reference id.
     * @param parms  An array of constructor type validators for the expected constructor
     *               argument set.
     */
    public FactoryConstructorValidator(String componentId, String factoryMethod, String factoryComponent, TestParameter[] parms) {
        super();
        this.factoryMethod = factoryMethod;
        this.factoryComponent = factoryComponent;
        this.componentId = componentId;
        this.parms = parms;
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
        // ensure we have everything initialized
        super.validate(testContext);
        // validation is done by the metadata wrapper.
        moduleMetadata.validateFactoryConstructorMetadata(componentId, factoryMethod, factoryComponent, parms);
    }
}

