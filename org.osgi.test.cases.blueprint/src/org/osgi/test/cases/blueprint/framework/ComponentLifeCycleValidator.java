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

