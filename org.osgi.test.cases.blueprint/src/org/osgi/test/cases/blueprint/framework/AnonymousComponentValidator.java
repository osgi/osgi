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

import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.reflect.ComponentMetadata;

/**
 * Validate the characteristics of an anonymous top-level
 * component in a blueprint context.
 *
 * NOTE:  This validator assumes there is just a single
 * top-level component defined for the test and it also
 * does not have an assigned component id.
 */
public class AnonymousComponentValidator extends MetadataValidator {
    // the expected type of component we expect to find
    protected Class type;

    /**
     * Validates that a component does not have any constructor args
     *
     * @param componentId
     *               The id of the target component.
     */
    public AnonymousComponentValidator(Class type) {
        super();
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
        // get the full set of ids.
        Set ids = blueprintMetadata.getComponentIds();
        assertEquals("Incorrect ID set size", 1, ids.size());
        // get the single id
        String id = (String)ids.toArray()[0];

        assertEquals("Invalid initial character for generated component name", '.', id.charAt(0));
        // now validate we can get everything using this id
        ComponentMetadata meta = blueprintMetadata.getComponentMetadata(id);
        assertTrue("Incorrect metadata type", type.isInstance(meta));
        // this raises an assertion if this is not found
        Object component = blueprintMetadata.getComponent(id);
    }
}

