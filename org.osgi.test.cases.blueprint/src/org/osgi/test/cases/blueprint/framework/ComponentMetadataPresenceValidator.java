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
import org.osgi.service.blueprint.reflect.ComponentMetadata;

/**
 * Generic validator to just verify that a metadata element is present and
 * is of the correct type.
 */
public class ComponentMetadataPresenceValidator extends MetadataValidator {
    // the id of the target component
    protected String componentId;
    // the type of metadata we expect to find
    protected Class type;

    /**
     * Validates the metadata for a modules exported services.
     *
     * @param expectedServices
     *               The list of expected exported services.
     */
    public ComponentMetadataPresenceValidator(String componentId, Class type) {
        super();
        this.componentId = componentId;
        this.type = type;
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

        // get the named metadata (this will throw an assertion failure if not there)
        ComponentMetadata meta = blueprintMetadata.getComponentMetadata(componentId);
        assertTrue("Wrong metadata type for component " + componentId, type.isInstance(meta));
    }
}

