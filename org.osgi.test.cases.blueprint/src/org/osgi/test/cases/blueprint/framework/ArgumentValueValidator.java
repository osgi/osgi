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
import org.osgi.test.cases.blueprint.services.StringValueDescriptor;
import org.osgi.test.cases.blueprint.services.ValueDescriptor;

/**
 * Validate the injected value of an argument in a component.
 */
public class ArgumentValueValidator extends MetadataValidator {
    // the external component id
    protected String componentId;
    // the property comparison value
    protected ValueDescriptor value;

    /**
     * Constructor for an argument validator.
     *
     * @param componentId
     *               The target component owning the argument.
     * @param argumentName
     *               The name of the argument.  These are generally named "arg1",
     *               "arg2", etc.
     * @param value  The expected value.  These are compared using the equals() method.
     */
    public ArgumentValueValidator(String componentId, String argumentName, Object value) {
        this(componentId, argumentName, value, null);
    }


    /**
     * Constructor for an argument validator.
     *
     * @param componentId
     *               The target component owning the argument.
     * @param argumentName
     *               The name of the argument.  These are generally named "arg1",
     *               "arg2", etc.
     * @param value  The expected value.  These are compared using the equals() method.
     * @param clz    The class of object we expect this to be.
     */
    public ArgumentValueValidator(String componentId, String argumentName, Object value, Class clz) {
        this.componentId = componentId;
        // add the validator appropriate for this
        this.value = new StringValueDescriptor(argumentName, value, clz);
    }


    /**
     * Constructor for an argument validator.
     *
     * @param componentId
     *               The target component owning the argument.
     * @param argumentName
     *               The name of the argument.  These are generally named "arg1",
     *               "arg2", etc.
     * @param value  The expected value.  These are compared using the equals() method.
     * @param clz    The class of object we expect this to be.
     */
    public ArgumentValueValidator(String componentId, ValueDescriptor value) {
        this.componentId = componentId;
        // add the validator appropriate for this
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
        assertEquals("Argument " + value.getName() + " value in component " + componentId, value, moduleMetadata.getComponentArgumentValue(componentId, value.getName()));
    }
}

