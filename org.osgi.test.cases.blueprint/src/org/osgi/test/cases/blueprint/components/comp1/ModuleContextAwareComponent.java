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

package org.osgi.test.cases.blueprint.components.comp1;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.context.ModuleContext;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

public class ModuleContextAwareComponent extends BaseTestComponent {
    // an injected bundle
    protected Bundle bundle;
    // an injected bundleContext;
    protected BundleContext bundleContext;
    // the injected ModuleContext
    protected ModuleContext moduleContext;

    public ModuleContextAwareComponent(String componentId) {
        super(componentId);
    }


    /**
     * Property setting method that will thrown an exception.
     *
     * @param value  The injected string value.
     */
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }


    /**
     * Property setting method that will thrown an exception.
     *
     * @param value  The injected string value.
     */
    public void setBundleContext(BundleContext bundleContext) {
        // there's no defined ordering for property injections, so we'll handle
        // this one silently.  The injected value is required by the setModuleContext method
        this.bundleContext = bundleContext;
    }


	/**
	 * Set the module context of the module in which the implementor is
	 * executing.
	 *
	 * @param context the module context in which the implementor of
	 * this interface is executing.
	 */
	public void setModuleContext(ModuleContext context) {
        // save this for the init method to check
        this.moduleContext = context;
        AssertionService.assertNotNull(this, "null ModuleContext injectioned", context);
        // send an event indicating this has occurred.
        AssertionService.sendEvent(this, AssertionService.MODULE_CONTEXT_INJECTED);
    }


    public void init() {
        // validate the information is consistent
        AssertionService.assertEquals(this, "Mismatch in ModuleContext BundleContext", bundleContext, moduleContext.getBundleContext());
        AssertionService.assertEquals(this, "Mismatch in ModuleContext Bundle", bundle, moduleContext.getBundleContext().getBundle());
        super.init();
    }
}


