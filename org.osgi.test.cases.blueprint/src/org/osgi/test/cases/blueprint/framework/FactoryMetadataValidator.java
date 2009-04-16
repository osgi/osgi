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
 * Validate the constructor metadata for a component built using a
 * factory class.
 */
public class FactoryMetadataValidator extends MetadataValidator {
    // the external component id
    protected String componentId;
    // the factory method name
    protected String factoryMethodName;
    // The static factory class name.
    protected String staticFactoryClassName;
    // the factory TestComponentValue
    protected TestValue factoryTestComponentValue;

    /**
     * Validate the factory constructor metadata.
     *
     * @param componentId
     *               The id of the target component.
     * @param factoryMethod
     *               The name of the factory method used to construct the component.
     * @param staticFactoryClassName
     *               The static factory class name.
     */
    public FactoryMetadataValidator(String componentId, String factoryMethodName, String factoryComponent) {
        this(componentId, factoryMethodName, factoryComponent, null);
    }


    /**
     * Validate the factory constructor metadata.
     *
     * @param componentId
     *               The id of the target component.
     * @param factoryMethod
     *               The name of the factory method used to construct the component.
     * @param staticFactoryClassName
     *               The static factory class name.
     * @param factoryTestComponentValue
     *               The instance factory TestComponentValue.
     */
    public FactoryMetadataValidator(String componentId, String factoryMethodName, String staticFactoryClassName, TestValue factoryTestComponentValue) {
        super();
        this.factoryMethodName = factoryMethodName;
        this.staticFactoryClassName = staticFactoryClassName;
        this.componentId = componentId;
        this.factoryTestComponentValue = factoryTestComponentValue;
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
        moduleMetadata.validateFactoryMetadata(componentId, factoryMethodName, staticFactoryClassName, factoryTestComponentValue);
    }
}

