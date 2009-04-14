/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.blueprint.framework;

import java.util.Iterator;
import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.context.ComponentDefinitionException;
import org.osgi.service.blueprint.context.ModuleContext;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.LocalComponentMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceComponentMetadata;

/**
 * Generic validator to verify that a named component is included in the
 * getLocalComponentMetadata() ccllection
 */
public class GetReferencedServicesMetadataValidator extends MetadataValidator {
    // the id of the target component
    protected String componentId;

    /**
     * Validates the metadata for a modules exported services.
     *
     * @param expectedServices
     *               The list of expected exported services.
     */
    public GetReferencedServicesMetadataValidator(String componentId) {
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

        ModuleContext context = moduleMetadata.getTargetModuleContext();
        // get the collection list
        Collection metadata = context.getReferencedServicesMetadata();
        Iterator i = metadata.iterator();

        while (i.hasNext()) {
            ServiceReferenceComponentMetadata meta = (ServiceReferenceComponentMetadata)i.next();
            // if we find a match, just return
            if (componentId.equals(meta.getName())) {
                try {
                    // this is an immutability test for the collection.  This should throw an error
                    i.remove();
                    fail("Immutable getReferencedServicesMetadata() test failure for component " + componentId);
                } catch (Throwable e) {
                }
                return;
            }
        }
        fail("Expected ServiceReferenceComponentMetadata instance for component " + componentId);
    }
}

