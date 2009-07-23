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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;

import org.osgi.service.blueprint.reflect.ReferenceListener;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;

/**
 * A single referenced service in the BlueprintContainer metadata.
 */
public class ReferencedServiceBase extends Assert implements TestComponentMetadata {
    // optional name of the component
    protected String name;
    // the set of exported interfaces
    protected String serviceInterface;
    // the expected service availability.
    protected int serviceAvailability;
    // the service activation style
    protected int activation;
    // the referenced name of the component (used in the filter request)
    protected String componentName;
    // the request filter string
    protected String filter;
    // the list of binding listeners
    protected BindingListener[] listeners;
    // the set of explicit dependencies
    protected List dependencies;

    /**
     * Create a ReferenceService descriptor.
     *
     * @param name       The id of the component (null for inline references)
     * @param interfaceClass
     *                   The referenced interface class.
     * @param availability
     *                   The availability setting.
     * @param activation The activation setting.
     * @param componentName
     *                   The component-name attribute.
     * @param filter     The declared filter string for the reference.
     * @param deps       The declared dependencies for this reference.
     * @param listeners  An expected set of listener metadata.
     */
    public ReferencedServiceBase(String name, Class interfaceClass, int availability, int activation, String componentName, String filter, String[] deps, BindingListener[] listeners) {
        this.name = name;
        this.serviceAvailability = availability;
        this.activation = activation;
        this.filter = filter;
        this.componentName = componentName;
        this.listeners = listeners;
        // the interface class is optional, so only convert the name if we have a class.
        if (interfaceClass != null) {
            this.serviceInterface = interfaceClass.getName();
        }

        dependencies = new ArrayList();
        // handle the dependency tracking
        if (deps != null) {
            for (int i = 0; i < deps.length; i++) {
                dependencies.add(deps[i]);
            }
        }
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
        if (!(componentMeta instanceof ServiceReferenceMetadata)) {
            return false;
        }
        ServiceReferenceMetadata meta = (ServiceReferenceMetadata)componentMeta;

        // match on the interfaces first...but the interface is optional
        if (serviceInterface == null) {
            if (meta.getInterface() != null) {                
                return false;
            }
        }
        else {
            if (!serviceInterface.equals(meta.getInterface())) {
                return false;
            }
        }

        // the component name is an important attribute for matching
        if (componentName != null) {
            if (meta.getComponentName() == null) {
                return false;
            }

            if (!componentName.equals(meta.getComponentName())) {
                return false;
            }
        }

        // if the request filter is null, we're going to pass on the
        // filter checks.  This is largely because the filter used can
        // be generated, and it's a tough assignment to get a match.
        if (filter == null) {
            return true;
        }
        // non-null expected, but the metadata is null.  This is not a match
        if (meta.getFilter() == null) {
            return false;
        }
        // compare the filter
        return filter.equals(meta.getFilter());
    }


    /**
     * Perform additional validation on a service.
     *
     * @param meta   The service metadata
     *
     * @exception Exception
     */
    public void validate(BlueprintMetadata blueprintMetadata, ComponentMetadata componentMeta) throws Exception {
        assertTrue("Component type mismatch", componentMeta instanceof ServiceReferenceMetadata);
        ServiceReferenceMetadata meta = (ServiceReferenceMetadata)componentMeta;
        assertEquals("Explicit dependencies mismatch", dependencies, meta.getDependsOn());
        // if we have a name to compare, they must be equal
        assertEquals(name, getId());
        assertEquals(serviceInterface, meta.getInterface());
        assertEquals("Availability setting mismatch", serviceAvailability, meta.getAvailability());
        assertEquals("Activation setting mismatch", activation, meta.getActivation());
        // we might have a listener list also
        if (listeners != null) {
            Collection bindingListeners = meta.getReferenceListeners();
            assertEquals("Mismatch on binding listener list", listeners.length, bindingListeners.size());
            for (int i = 0; i < listeners.length; i++) {
                BindingListener s = listeners[i];
                ReferenceListener l = locateBindingListener(bindingListeners, s);
                assertNotNull("Missing binding listener (" + s + ") for component " + name, l);
                // do additional validation on the listener definition.
                s.validate(blueprintMetadata, l);
            }
        }

        assertEquals("Component name mismatch", componentName, meta.getComponentName());
        assertEquals("Filter mismatch", filter, meta.getFilter());
    }


    /**
     * Locate a matching metadata value for a listener.
     *
     * @param services
     * @param service  The set of services for this bundle.
     *
     * @return The matching services metadata, or null if no match was found.
     */
    protected ReferenceListener locateBindingListener(Collection listeners, BindingListener listener) {
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            ReferenceListener meta = (ReferenceListener)i.next();
            if (listener.matches(meta)) {
                return meta;
            }
        }
        return null;
    }


    /**
     * Retrieve the name for this component.  Used for validation
     * purposes.
     *
     * @return The String name of the component.  Returns null
     *         if no name has been provided.
     */
    public String getId() {
        return name;
    }
}

