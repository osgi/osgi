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
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.NoSuchComponentException;
import org.osgi.service.blueprint.reflect.ComponentMetadata;

/**
 * Validator to test the conditions under which a NoSuchComponentException
 * should be thrown and validate the information in the exception
 */
public class NoSuchComponentExceptionValidator extends MetadataValidator {
    /**
     * Validates the metadata for a modules exported services.
     */
    public NoSuchComponentExceptionValidator() {
        super();
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

        BlueprintContainer container = blueprintMetadata.getTargetBlueprintContainer();
        // first with the component instance
        try {
            container.getComponentInstance("YADAyadaYADA");
            fail("Expected NoSuchComponentException not thrown");
        } catch (NoSuchComponentException e) {
            assertEquals("Incorrect id in NoSuchComponentException", "YADAyadaYADA", e.getComponentId());
        }
        try {
            container.getComponentMetadata("YADAyadaYADA");
            fail("Expected NoSuchComponentException not thrown");
        } catch (NoSuchComponentException e) {
            assertEquals("Incorrect id in NoSuchComponentException", "YADAyadaYADA", e.getComponentId());
        }
    }
}

