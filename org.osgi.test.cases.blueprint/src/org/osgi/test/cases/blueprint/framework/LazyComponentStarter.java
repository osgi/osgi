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
 * A testphase initializer used to force lazy component instantiation
 * by requesting a component instance via the metadata.
 */
public class LazyComponentStarter implements TestInitializer, MetadataAware {
    // the target component id
    protected String componentId;
    // our method context wrapper
    protected ModuleMetadata moduleMetadata;

    /**
     * Create the starter component.
     *
     * @param componentId
     *               The target component id.
     */
    public LazyComponentStarter(String componentId)
    {
        this.componentId = componentId;
    }

    /**
     * Inject a module context into this validator instance.
     *
     * @param moduleContext
     *               The new module context.
     */
    public void setModuleMetadata(ModuleMetadata moduleMetadata) {
        this.moduleMetadata = moduleMetadata;
    }

    /**
     * Perform any additional test phase setup actions.
     *
     * @param testContext
     *               The BundleContext for the test (used for inspecting the test
     *               environment).
     *
     * @exception Exception
     */
    public void start(BundleContext testContext) throws Exception {
        // just retrieve the component.  This will serve as the first
        // request and trigger the action
        moduleMetadata.getComponent(componentId);
    }
}

