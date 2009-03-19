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
import org.osgi.framework.BundleContext;

/**
 * Validate the lifecycle information for a component.
 */
public class ComponentLifeCycleValidator extends MetadataValidator {
    // the external component id
    protected String componentId;
    // the component class name
    protected String className;
    // the component init method name
    protected String initMethod;
    // the component destroy method name
    protected String destroyMethod;

    /**
     * Validate the life-cycle metadata for a component.
     *
     * @param componentId
     *                  The id of the target component.
     * @param className The expected class name for the component.
     */
    public ComponentLifeCycleValidator(String componentId, String className) {
        this(componentId, className, null, null);
    }

    /**
     * Create a life-cycle validator.
     *
     * @param componentId
     *                   The target component id.
     * @param className  The component class name.
     * @param initMethod The expected init-method (can be null).
     * @param destroyMethod
     *                   The expected destroy-method (can be null).
     */
    public ComponentLifeCycleValidator(String componentId, String className, String initMethod, String destroyMethod) {
        super();
        this.componentId = componentId;
        this.className = className;
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
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
        moduleMetadata.validateLifeCycle(componentId, className, initMethod, destroyMethod);
    }
}

