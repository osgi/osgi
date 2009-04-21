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

package org.osgi.test.cases.blueprint.components.comp1;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.context.BlueprintContext;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

public class BlueprintContextAwareComponent extends BaseTestComponent {
    // an injected bundle
    protected Bundle bundle;
    // an injected bundleContext;
    protected BundleContext bundleContext;
    // the injected BlueprintContext
    protected BlueprintContext moduleContext;

    public BlueprintContextAwareComponent(String componentId) {
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
        // this one silently.  The injected value is required by the setBlueprintContext method
        this.bundleContext = bundleContext;
    }


	/**
	 * Set the module context of the module in which the implementor is
	 * executing.
	 *
	 * @param context the module context in which the implementor of
	 * this interface is executing.
	 */
	public void setBlueprintContext(BlueprintContext context) {
        // save this for the init method to check
        this.moduleContext = context;
        AssertionService.assertNotNull(this, "null BlueprintContext injectioned", context);
        // send an event indicating this has occurred.
        AssertionService.sendEvent(this, AssertionService.MODULE_CONTEXT_INJECTED);
    }


    public void init() {
        // validate the information is consistent
        AssertionService.assertEquals(this, "Mismatch in BlueprintContext BundleContext", bundleContext, moduleContext.getBundleContext());
        AssertionService.assertEquals(this, "Mismatch in BlueprintContext Bundle", bundle, moduleContext.getBundleContext().getBundle());
        super.init();
    }
}


