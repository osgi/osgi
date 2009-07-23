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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.RefMetadata;
import org.osgi.service.blueprint.reflect.RegistrationListener;
import org.osgi.service.blueprint.reflect.ServiceMetadata;
import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.Target;

import org.osgi.test.cases.blueprint.services.TestUtil;

import junit.framework.Assert;

/**
 * Validate the metadata for a single service exported in a BlueprintContainer.
 */
public class ExportedService extends Assert implements TestComponentMetadata {
    // the optional assigned service id
    protected String serviceId;
    // the activation mode
    protected int activation;
    // the expected export mode for the export.
    protected int exportMode;
    // the exported target component.
    protected TestValue target;
    // the set of exported interfaces
    protected List serviceInterfaces;
    // an optional set of service properties
    protected List serviceProperties;
    // the exported service ranking
    protected int serviceRanking;
    // the set of explicit dependencies
    protected List dependencies;
    // any registration listeners that might be attached to this export
    protected TestRegistrationListener[] listeners;

    /**
     * Create an exported service definition.
     *
     * @param serviceId  The id specified on the service reference tag.
     * @param activation
     *                   The eager/lazy activation mode
     * @param componentId
     *                   The id of the target component (can be null if this service
     *                   uses an inner component).
     * @param serviceInterface
     *                   The service interface class we expect to be exported.
     * @param exportMode The metadata export mode.
     * @param ranking    The service ranking.
     * @param props      Any additional service properties that were specified on the service.
     * @param deps       A set of explicit dependencies
     * @param listeners  Any optional registration listeners.
     */
    public ExportedService(String serviceId, int activation, String componentId, Class serviceInterface, int exportMode, int ranking, MapValueEntry[] props,
            String[] deps, TestRegistrationListener[] listeners) {
        this(serviceId, activation, componentId, new Class[] { serviceInterface }, exportMode, ranking, props, deps, listeners);
    }


    /**
     * Create an exported service definition.
     *
     * @param serviceId  The id specified on the service reference tag.
     * @param activation
     *                   The eager/lazy activation mode
     * @param componentId
     *                   The id of the target component (can be null if this service
     *                   uses an inner component).
     * @param serviceInterfaces
     *                   The service interface classes we expect to be exported.
     * @param exportMode The metadata export mode.
     * @param ranking    The service ranking.
     * @param props      Any additional service properties that were specified on the service.
     * @param deps       A set of explicit dependencies
     * @param listeners  Any optional registration listeners.
     */
    public ExportedService(String serviceId, int activation, String componentId, Class[] interfaces, int exportMode, int ranking, MapValueEntry[] props,
        String[] deps, TestRegistrationListener[] listeners) {
        this(serviceId, activation, new TestRefValue(componentId), interfaces, exportMode, ranking, props, deps, listeners);
    }

    /**
     * Create an exported service definition.
     *
     * @param serviceId  The id specified on the service reference tag.
     * @param activation
     *                   The eager/lazy activation mode
     * @param target
     *                   The source of the exported service item.
     * @param serviceInterfaces
     *                   The service interface classes we expect to be exported.
     * @param exportMode The metadata export mode.
     * @param ranking    The service ranking.
     * @param props      Any additional service properties that were specified on the service.
     * @param deps       A set of explicit dependencies
     * @param listeners  Any optional registration listeners.
     */
    public ExportedService(String serviceId, int activation, TestValue target, Class[] interfaces, int exportMode, int ranking, MapValueEntry[] props,
        String[] deps, TestRegistrationListener[] listeners) {
        this.serviceId = serviceId;
        this.activation = activation;
        this.target = target;
        this.exportMode = exportMode;
        this.serviceRanking = ranking;
        if (props != null) {
            this.serviceProperties = new ArrayList();
            // copy this over to the list
            for (int i = 0; i < props.length; i++) {
                serviceProperties.add(props[i]);
            }
        }
        // convert this into a set
        this.serviceInterfaces = new ArrayList();
        for (int i = 0; i < interfaces.length; i++) {
            serviceInterfaces.add(interfaces[i].getName());
        }

        dependencies = new ArrayList();
        // handle the dependency tracking
        if (deps != null) {
            for (int i = 0; i < deps.length; i++) {
                dependencies.add(deps[i]);
            }
        }

        this.listeners = listeners;
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
        // we only handle Service export component references.
        if (!(componentMeta instanceof ServiceMetadata)) {
            return false;
        }

        ServiceMetadata meta = (ServiceMetadata)componentMeta;

        // if we have an assigned service id check on that first.  We consider
        // this to be a target match, since the service id must be unique.
        if (serviceId != null) {
            if (meta.getId() != null) {
                return serviceId.equals(meta.getId());
            }
            // not a match
            return false;
        }

        // match on the interfaces first
        if (!serviceInterfaces.equals(meta.getInterfaces())) {
            return false;
        }

        Target component = meta.getServiceComponent();
        // We always have a component to export...verify the type.
        return target.equals(component);
    }


    /**
     * Perform additional validation on a service.
     *
     * @param meta   The service metadata
     *
     * @exception Exception
     */
    public void validate(BlueprintMetadata blueprintMetadata, ComponentMetadata componentMeta) throws Exception {
        assertTrue("Component type mismatch", componentMeta instanceof ServiceMetadata);
        ServiceMetadata meta = (ServiceMetadata)componentMeta;
        assertEquals(serviceId, meta.getId());
        assertEquals(activation, meta.getActivation());
        assertEquals(exportMode, meta.getAutoExport());
        assertEquals(serviceRanking, meta.getRanking());
        assertEquals(dependencies, meta.getDependsOn());
        // given listeners to validate?
        if (listeners != null) {
            Collection metaListeners = meta.getRegistrationListeners();
            assertEquals(listeners.length, metaListeners.size());
            Iterator i = metaListeners.iterator();
            while (i.hasNext()) {
                RegistrationListener metaListener = (RegistrationListener)i.next();
                assertNotNull("Could not locate registration listener on compnent " + meta.getId(), locateListener(metaListener, listeners));
            }
        }

        // for some tests, the service properties are necessary to disambiguate
        // the service.  If we have some specified, then a match is required on
        // all of these as well.
        if (serviceProperties != null) {
            List propEntries = meta.getServiceProperties();
            // validate the size first
            assertEquals("Service properties value size mismatch", serviceProperties.size(), propEntries.size());
            // now validate each of the entries
            for (int i = 0; i < serviceProperties.size(); i++) {
                MapValueEntry entry = (MapValueEntry)serviceProperties.get(i);
                // validate the real entry
                entry.validate(blueprintMetadata, (MapEntry)propEntries.get(i));
            }
        }
    }


    /**
     * Search for a matching metadata element in an expected list of
     * listeners.
     *
     * @param meta      The source metadata element.
     * @param listeners Our list of expected listeners.
     *
     * @return The matching expected element, or null if no match was found.
     */
    protected TestRegistrationListener locateListener(RegistrationListener meta, TestRegistrationListener[] listeners) {
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i].matches(meta)) {
                return listeners[i];
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
        return serviceId;
    }


    public String toString() {
        return "Exported service name=" + serviceId + ", interfaces=" + serviceInterfaces;
    }
}

