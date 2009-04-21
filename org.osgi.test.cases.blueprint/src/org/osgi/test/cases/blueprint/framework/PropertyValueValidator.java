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
import org.osgi.test.cases.blueprint.services.ValueDescriptor;

/**
 * Validate the value of a property stored in an initilized component.
 */
public class PropertyValueValidator extends MetadataValidator {
    // the external component id
    protected String componentId;
    // the property comparison value
    protected ValueDescriptor value;

    /**
     * Create a property validator.
     *
     * @param componentId
     *               The target componet id.
     * @param propertyName
     *               The name of the property value.
     * @param value  The expected property value.
     */
    public PropertyValueValidator(String componentId, String propertyName, Object value) {
        this(componentId, propertyName, value, null);
    }

    /**
     * Create a property validator.
     *
     * @param componentId
     *               The target componet id.
     * @param propertyName
     *               The name of the property value.
     * @param value  The expected property value.
     * @param clz    The expected class of the value.
     */
    public PropertyValueValidator(String componentId, String propertyName, Object value, Class clz) {
        this.componentId = componentId;
        // add the validator appropriate for this
        this.value = new ValueDescriptor(propertyName, value, clz);
    }


    /**
     * Create a property validator.
     *
     * @param componentId
     *               The target componet id.
     * @param propertyName
     *               The name of the property value.
     * @param value  The expected property value.
     * @param clz    The expected class of the value.
     */
    public PropertyValueValidator(String componentId, ValueDescriptor value) {
        this.componentId = componentId;
        this.value = value;
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
        // the value must be equal
        assertEquals("Property " + value.getName() + " value in component " + componentId, value, blueprintMetadata.getComponentPropertyValue(componentId, value.getName()));
    }
}

