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
 * Validate the blueprint inferred constructor information against
 * the expected.
 */
public class PartialConstructorMetadataValidator extends MetadataValidator {
    // the external component id
    protected String componentId;
    // the list of expected parameters.  This can be a partial list, so
    // we'll only validate the ones that are there.
    protected TestParameter[] parms;


    /**
     * Validates that a component does not have any constructor args
     *
     * @param componentId
     *               The id of the target component.
     */
    public PartialConstructorMetadataValidator(String componentId) {
        this(componentId, new TestParameter[0]);
    }

    /**
     * Create a constructor validator.
     *
     * @param componentId
     *               The target component id.
     * @param parms  An array of parameters definitions we expect to find in the metadata.
     */
    public PartialConstructorMetadataValidator(String componentId, TestParameter[] parms) {
        super();
        this.componentId = componentId;
        this.parms = parms;
    }


    /**
     * Create a constructor validator.
     *
     * @param componentId
     *               The target component id.
     * @param parm   A single parameter we wish to validate.  Other parameters, if specified, are ignored
     */
    public PartialConstructorMetadataValidator(String componentId, TestParameter parm) {
        this(componentId, new TestParameter[] { parm });
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
        moduleMetadata.validatePartialConstructorMetadata(componentId, parms);
    }
}

