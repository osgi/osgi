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
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;
import org.osgi.service.blueprint.reflect.ReferenceMetadata;

/**
 * A single referenced service in the BlueprintContainer metadata.
 */
public class ReferencedService extends ReferencedServiceBase implements TestComponentMetadata {
    // the default timeout value
    public static final int DEFAULT_TIMEOUT = 300000;

    // the service timeout value
    protected long timeout;

    /**
     * Create a ReferenceService descriptor.
     *
     * @param name      The registered name of the reference (can be null for inline
     *                  definitions).
     * @param interfaceClass
     *                  The single optional interface class.
     * @param availability
     *                  The availability setting.
     * @param initialization
     *                  The lazy/eager initialization setting.
     * @param componentName
     *                  The component-name attribute.
     * @param filter    The declared filter string for the reference.
     * @param deps      The collection if explicit depends-on relationships.
     * @param listeners An expected set of listener metadata.
     * @param timeout   The service call timeout interval for damped services.
     */
    public ReferencedService(String name, Class interfaceClass, int availability, int initialization,
            String componentName, String filter, String[] deps, BindingListener[] listeners, long timeout) {
        super(name, interfaceClass, availability, initialization, componentName, filter, deps, listeners);
        this.timeout = timeout;
    }


    /**
     * Perform additional validation on a service.
     *
     * @param meta   The service metadata
     *
     * @exception Exception
     */
    public void validate(BlueprintMetadata blueprintMetadata, ServiceReferenceMetadata metadata) throws Exception {
        assertTrue("Mismatch on service reference type", metadata instanceof ReferenceMetadata);
        // do the base validation
        super.validate(blueprintMetadata, metadata);
        assertEquals(timeout, ((ReferenceMetadata)metadata).getTimeout());
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
        if (!(componentMeta instanceof ReferenceMetadata)) {
            return false;
        }

        // match on the remaining bits
        return super.matches(componentMeta);
    }
}

