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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.container.NoSuchComponentException;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ReferenceListMetadata;
import org.osgi.service.blueprint.reflect.ReferenceMetadata;
import org.osgi.service.blueprint.reflect.ServiceMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;

/**
 * This is a validator that is specific to one particular
 * test case.  The blueprint bundle is configured with a
 * variety of component types, using named components,
 * anonymous components, and inline components for each
 * of the defined component types.  This will then
 * validate all of the BlueprintContainer API combinations
 * that we expect to find.
 */
public class MetadataSamplerValidator extends MetadataValidator {
    // the id of the target component
    protected String componentId;

    /**
     * Validates the metadata for a modules exported services.
     */
    public MetadataSamplerValidator() {
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

        BlueprintContainer context = blueprintMetadata.getTargetBlueprintContainer();

        // environment components...don't have a subtype
        ComponentMetadata blueprintContainer = null;
        ComponentMetadata blueprintBundle = null;
        ComponentMetadata blueprintBundleContext = null;
        ComponentMetadata blueprintConverter = null;

        // our bean components
        BeanMetadata serviceBean = null;
        BeanMetadata itemsBean = null;
        BeanMetadata anonBean = null;
        BeanMetadata inlineBean = null;

        // our service components
        ServiceMetadata namedService = null;
        ServiceMetadata anonService = null;
        ServiceMetadata inlineService = null;

        // our reference components
        ReferenceMetadata namedReference = null;
        ReferenceMetadata anonReference = null;
        ReferenceMetadata inlineReference = null;

        // our reference llist components
        ReferenceListMetadata namedReferenceList = null;
        ReferenceListMetadata anonReferenceList = null;
        ReferenceListMetadata inlineReferenceList = null;

        // get a list of all metadata elements and look for the specific ones we
        // want.
        Collection meta = context.getMetadata(ComponentMetadata.class);

        assertEquals("Incorrect metadata list size", 17, meta.size());

        Iterator it = meta.iterator();
        while (it.hasNext()) {
            ComponentMetadata md = (ComponentMetadata)it.next();
            String id = md.getId();
            if (md instanceof BeanMetadata) {
                // only one inline bean expected
                if (id == null) {
                    inlineBean = (BeanMetadata)md;
                }
                // and we only have one anonymous one
                else if (id.startsWith(".")) {
                    anonBean = (BeanMetadata)md;
                }
                else if (id.equals("ServiceOne")) {
                    serviceBean = (BeanMetadata)md;
                }
                else if (id.equals("inlineItems")) {
                    itemsBean = (BeanMetadata)md;
                }
                else {
                    fail("Unexpected top-level bean located: " + id);
                }
            }
            else if (md instanceof ServiceMetadata) {
                // only one inline bean expected
                if (id == null) {
                    inlineService = (ServiceMetadata)md;
                }
                // and we only have one anonymous one
                else if (id.startsWith(".")) {
                    anonService = (ServiceMetadata)md;
                }
                else if (id.equals("NamedService")) {
                    namedService = (ServiceMetadata)md;
                }
                else {
                    fail("Unexpected top-level service located: " + id);
                }
            }
            else if (md instanceof ReferenceMetadata) {
                // only one inline bean expected
                if (id == null) {
                    inlineReference = (ReferenceMetadata)md;
                }
                // and we only have one anonymous one
                else if (id.startsWith(".")) {
                    anonReference = (ReferenceMetadata)md;
                }
                else if (id.equals("NamedReference")) {
                    namedReference = (ReferenceMetadata)md;
                }
                else {
                    fail("Unexpected top-level reference located: " + id);
                }
            }
            else if (md instanceof ReferenceListMetadata) {
                // only one inline bean expected
                if (id == null) {
                    inlineReferenceList = (ReferenceListMetadata)md;
                }
                // and we only have one anonymous one
                else if (id.startsWith(".")) {
                    anonReferenceList = (ReferenceListMetadata)md;
                }
                else if (id.equals("NamedReferenceList")) {
                    namedReferenceList = (ReferenceListMetadata)md;
                }
                else {
                    fail("Unexpected top-level reference located: " + id);
                }
            }
            else {
                // one of the named components
                assertNotNull("Unexpected component metadata found with null id", id);
                if (id.equals("blueprintContainer")) {
                    blueprintContainer = md;
                }
                else if (id.equals("blueprintBundle")) {
                    blueprintBundle = md;
                }
                else if (id.equals("blueprintBundleContext")) {
                    blueprintBundleContext = md;
                }
                else if (id.equals("blueprintConverter")) {
                    blueprintConverter = md;
                }
                else {
                    fail("Unexpected top-level metadata component located: " + id);
                }
            }
        }

        // now set up some lists for performing smaller request validation
        ComponentMetadata[] allMetadata = new ComponentMetadata[] {
            blueprintContainer,
            blueprintBundle,
            blueprintBundleContext,
            blueprintConverter,

            // our bean components
            serviceBean,
            itemsBean,
            anonBean,
            inlineBean,

            // our service components
            namedService,
            anonService,
            inlineService,

            // our reference components
            namedReference,
            anonReference,
            inlineReference,

            // our reference llist components
            namedReferenceList,
            anonReferenceList,
            inlineReferenceList,
        };

        // verify that we have each of the required items
        for (int i = 0; i < allMetadata.length; i++) {
            assertNotNull("Non located metadata item in position " + i, allMetadata[i]);
        }

        // now verification sublists
        ComponentMetadata[] beanMetadata = new ComponentMetadata[] {
            // our bean components
            serviceBean,
            itemsBean,
            anonBean,
            inlineBean,
        };

        ComponentMetadata[] serviceMetadata = new ComponentMetadata[] {
            // our service components
            namedService,
            anonService,
            inlineService,
        };

        ComponentMetadata[] referenceMetadata = new ComponentMetadata[] {
            // our reference components
            namedReference,
            anonReference,
            inlineReference,
        };

        ComponentMetadata[] referenceListMetadata = new ComponentMetadata[] {
            // our reference list components
            namedReferenceList,
            anonReferenceList,
            inlineReferenceList,
        };


        // this is a combination of the reference and reference-list items
        ComponentMetadata[] serviceReferenceMetadata = new ComponentMetadata[] {
            // our reference components
            namedReference,
            anonReference,
            inlineReference,

            // our reference list components
            namedReferenceList,
            anonReferenceList,
            inlineReferenceList,
        };

        // gather the set of names we expect to find in the collection
        Set expectedNames = new HashSet();
        for (int i = 0; i < allMetadata.length; i++) {
            String id = allMetadata[i].getId();
            if (id != null) {
                expectedNames.add(id);
            }
        }

        assertEquals("Mismatch on component id set", expectedNames, context.getComponentIds());

        // now verify each of the request lists...we've already done the all-inclusive list
        validateMetadataList(context.getMetadata(BeanMetadata.class), beanMetadata);
        validateMetadataList(context.getMetadata(ServiceMetadata.class), serviceMetadata);
        validateMetadataList(context.getMetadata(ReferenceMetadata.class), referenceMetadata);
        validateMetadataList(context.getMetadata(ReferenceListMetadata.class), referenceListMetadata);
        validateMetadataList(context.getMetadata(ServiceReferenceMetadata.class), serviceReferenceMetadata);

        // some some simple tests for error cases for a couple of container APIs
        try {
            Object x = context.getComponentInstance("XXXXXX");
            fail("NoSuchComponentException exposed for request for unknown component instance");
        } catch (NoSuchComponentException e) {
            // exception expected
        }

        try {
            Object x = context.getComponentMetadata("XXXXXX");
            fail("NoSuchComponentException exposed for request for unknown component metadata");
        } catch (NoSuchComponentException e) {
            // exception expected
        }
    }

    /**
     * Validate a collection returned for a metadata
     * query against an expected set.
     *
     * @param source
     * @param target
     *
     * @exception Exception
     */
    public void validateMetadataList(Collection source, ComponentMetadata[] target) throws Exception {
        Set targetSet = new HashSet();
        for (int i = 0; i < target.length; i++) {
            targetSet.add(target[i]);
        }

        Set sourceSet = new HashSet(source);

        assertEquals("Mismatch in expected returned metadata", targetSet, sourceSet);
    }
}

