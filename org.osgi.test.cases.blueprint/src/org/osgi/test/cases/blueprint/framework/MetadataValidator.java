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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import junit.framework.Assert;

/**
 * Base class for all validators that use a BlueprintMetadata instance
 * to perform the validation operations.
 */
public class MetadataValidator extends Assert implements TestValidator, MetadataAware {
    // our method context wrapper
    protected BlueprintMetadata blueprintMetadata;
    // our associated bundle (retrieved from the injected metadata)
    protected Bundle bundle;

    public MetadataValidator() {
        this.blueprintMetadata = null;
    }

    /**
     * Inject a module context into this validator instance.
     *
     * @param moduleContext
     *               The new module context.
     */
    public void setBlueprintMetadata(BlueprintMetadata blueprintMetadata) {
        this.blueprintMetadata = blueprintMetadata;
        // save the bundle also
        this.bundle = blueprintMetadata.getTargetBundle();
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
        // this will retrieve the module context, raising an error if
        // it could not be retrieved.
        blueprintMetadata.getBlueprintContainer();
    }
}

