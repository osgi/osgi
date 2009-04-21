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
 * Validate a set of properties for a component.
 */
public class PropertyMetadataValidator extends MetadataValidator {
    // the external component id
    protected String componentId;
    // the list of expected properties
    protected TestProperty[] props;

    /**
     * Validates that a component does not have any property injection.
     *
     * @param componentId
     *               The id of the target component.
     */
    public PropertyMetadataValidator(String componentId) {
        this(componentId, new TestProperty[0]);
    }

    /**
     * Validates a single component property.
     *
     * @param componentId
     *               The target component.
     * @param prop   The property definition.
     */
    public PropertyMetadataValidator(String componentId, TestProperty prop) {
        this(componentId, new TestProperty[] { prop });
    }

    /**
     * Validates a set of properties for a component.
     *
     * @param componentId
     *               The target component id.
     * @param props  An array of property descriptors to validate.
     */
    public PropertyMetadataValidator(String componentId, TestProperty[] props) {
        super();
        this.componentId = componentId;
        this.props = props;
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
        blueprintMetadata.validatePropertyMetadata(componentId, props);
    }
}

