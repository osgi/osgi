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

import java.util.Iterator;
import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.BeanMetadata;

/**
 * Generic validator to verify that a named component is included in the
 * getBeanComponentMetadata() ccllection
 */
public class GetBeanMetadataValidator extends MetadataValidator {
    // the id of the target component
    protected String componentId;

    /**
     * Validates the metadata for a modules exported services.
     *
     * @param expectedServices
     *               The list of expected exported services.
     */
    public GetBeanMetadataValidator(String componentId) {
        super();
        this.componentId = componentId;
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

        BlueprintContainer context = blueprintMetadata.getTargetBlueprintContainer();
        // get the collection list
        Collection metadata = context.getBeanComponentsMetadata();
        Iterator i = metadata.iterator();

        while (i.hasNext()) {
            BeanMetadata meta = (BeanMetadata)i.next();
            // if we find a match, just return
            if (componentId.equals(meta.getId())) {
                try {
                    // this is an immutability test for the collection.  This should throw an error
                    i.remove();
                    fail("Immutable getBeanComponentsMetadata() test failure for component " + componentId);
                } catch (Throwable e) {
                }
                return;
            }
        }
        fail("Expected BeanMetadata instance for component " + componentId);
    }
}

