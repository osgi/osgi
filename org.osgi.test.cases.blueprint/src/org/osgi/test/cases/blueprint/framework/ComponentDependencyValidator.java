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

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;

/**
 * Validate the depends-on list for a component definition.
 */
public class ComponentDependencyValidator extends MetadataValidator {
    // the target component id
    protected String componentId;
    // the set of names we expect to find
    protected List names;

    /**
     * Create the validator.
     *
     * @param componentId
     *                 The target component id.
     * @param nameList The list of components that this component should be dependent on.
     */
    public ComponentDependencyValidator(String componentId, String[] nameList) {
        this.componentId = componentId;
        this.names = new ArrayList();
        // convert this into a set that can be validate directly
        for (int i = 0; i < nameList.length; i++) {
            names.add(nameList[i]);
        }
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
        assertEquals("Named component dependencies for " + componentId, names, blueprintMetadata.getComponentDependencies(componentId));
    }
}

