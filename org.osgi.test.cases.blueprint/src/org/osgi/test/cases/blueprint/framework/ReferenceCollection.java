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

import org.osgi.service.blueprint.reflect.RefListMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;

/**
 * A single referenced service in the BlueprintContainer metadata.
 */
public class ReferenceCollection extends ReferencedServiceBase {
    // the collection member type specifier
    protected int memberType;

    /**
     * Create a ReferenceService descriptor.
     *
     * @param name       The registered component id for the collection.
     * @param interfaces The set of interfaces to access.
     * @param availability
     *                   The availability setting.
     * @param initialization
     *                   The initialization setting for the collection.
     * @param filter     The declared filter string for the reference.
     * @param deps       Explicit dependencies for the collection.
     * @param listeners  An expected set of listener metadata.
     * @param memberType The collection member type
     */
    public ReferenceCollection(String name, Class interfaceClass, int availability, int initialization,
            String filter, String[] deps, BindingListener[] listeners, int memberType) {
        super(name, interfaceClass, availability, initialization, filter, deps, listeners);
        this.memberType = memberType;
    }


    /**
     * Perform additional validation on a service.
     *
     * @param meta   The service metadata
     *
     * @exception Exception
     */
    public void validate(BlueprintMetadata blueprintMetadata, ServiceReferenceMetadata metadata) throws Exception {
        assertTrue("Mismatch on service reference type", metadata instanceof RefListMetadata);
        // do the base validation
        super.validate(blueprintMetadata, metadata);
        RefListMetadata meta = (RefListMetadata)metadata;
        assertEquals(memberType, meta.getMemberType());
    }

    /**
     * Determine if this descriptor matches the basic attributes of
     * an exported service.  This is used to locate potential matches.
     *
     * @param meta   The candidate exported service
     *
     * @return true if this service matches on all of the specifics.
     */
    public boolean matches(ComponentMetadata componentMeta) {
        // we only handle service reference component references.
        if (!(componentMeta instanceof RefListMetadata)) {
            return false;
        }

        RefListMetadata meta = (RefListMetadata)componentMeta;
        if (memberType != meta.getMemberType()) {
            return false;
        }

        // match on the remaining bits
        return super.matches(componentMeta);
    }
}

