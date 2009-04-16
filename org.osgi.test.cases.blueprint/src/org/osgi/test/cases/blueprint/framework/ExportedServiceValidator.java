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
 * Validate the metadata for all of the exported services
 * defined in a ModuleContext.
 */
public class ExportedServiceValidator extends MetadataValidator {
    // the expected set of exported services for this bundle.
    protected ExportedService[] expectedServices;

    /**
     * Validates the metadata for a modules exported services.
     *
     * @param expectedServices
     *               The list of expected exported services.
     */
    public ExportedServiceValidator(ExportedService[] services) {
        super();
        expectedServices = services;
    }

    /**
     * Validates the metadata for a modules exported services.
     *
     * @param expectedService
     *               The single exported service we're expecting.
     */
    public ExportedServiceValidator(ExportedService expectedService) {
        super();
        this.expectedServices = new ExportedService[] { expectedService };
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
        moduleMetadata.validateExportedServices(expectedServices);
    }
}

