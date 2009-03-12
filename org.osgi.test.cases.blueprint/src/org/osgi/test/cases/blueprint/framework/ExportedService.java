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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ComponentValue;
import org.osgi.service.blueprint.reflect.ReferenceValue;
import org.osgi.service.blueprint.reflect.RegistrationListenerMetadata;
import org.osgi.service.blueprint.reflect.ServiceExportComponentMetadata;
import org.osgi.service.blueprint.reflect.Value;
import org.osgi.test.cases.blueprint.services.TestUtil;

import junit.framework.Assert;

/**
 * Validate the metadata for a single service exported in a ModuleContext.
 */
public class ExportedService extends Assert implements TestComponentMetadata {
    // the optional assigned service id
    protected String serviceId;
    // the expected export mode for the export.
    protected int exportMode;
    // the exported component name (can be null if we expect an anonymous component)
    protected String componentId;
    // the set of exported interfaces
    protected Set serviceInterfaces;
    // an optional set of service properties
    protected Map serviceProperties;
    // the exported service ranking
    protected int serviceRanking;
    // the set of explicit dependencies
    protected Set dependencies;
    // any registration listeners that might be attached to this export
    protected RegistrationListener[] listeners;

    /**
     * Create an exported service definition.
     *
     * @param serviceId  The id specified on the service reference tag.
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
    public ExportedService(String serviceId, String componentId, Class serviceInterface, int exportMode, int ranking, Map props,
            String[] deps, RegistrationListener[] listeners) {
        this(serviceId, componentId, new Class[] { serviceInterface }, exportMode, ranking, props, deps, listeners);
    }


    /**
     * Create an exported service definition.
     *
     * @param serviceId  The id specified on the service reference tag.
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
    public ExportedService(String serviceId, String componentId, Class[] interfaces, int exportMode, int ranking, Map props,
        String[] deps, RegistrationListener[] listeners) {
        this.serviceId = serviceId;
        this.componentId = componentId;
        this.exportMode = exportMode;
        this.serviceRanking = ranking;
        this.serviceProperties = props;
        // convert this into a set
        this.serviceInterfaces = new HashSet();
        for (int i = 0; i < interfaces.length; i++) {
            serviceInterfaces.add(interfaces[i]);
        }
        dependencies = new HashSet();
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
        if (!(componentMeta instanceof ServiceExportComponentMetadata)) {
            return false;
        }

        ServiceExportComponentMetadata meta = (ServiceExportComponentMetadata)componentMeta;

        // if we have an assigned service id check on that first.  We consider
        // this to be a target match, since the service id must be unique.
        if (serviceId != null) {
            if (meta.getName() != null) {
                return serviceId.equals(meta.getName());
            }
        }

        // match on the interfaces first
        if (!serviceInterfaces.equals(meta.getInterfaceNames())) {
            return false;
        }
        Value component = meta.getExportedComponent();
        // if we have an explicit component id, we need to verify this
        if (componentId != null) {
            // this must be a reference to a component
            if (!(component instanceof ReferenceValue)) {
                return false;
            }
            // the component names must match
            if (!componentId.equals(((ReferenceValue)component).getComponentName())) {
                return false;
            }
        }
        else {
            // this must be an inner component
            // this must be a reference to a component
            if (!(component instanceof ComponentValue)) {
                return false;
            }
        }
        // for some tests, the service properties are necessary to disambiguate
        // the service.  If we have some specified, then a match is required on
        // all of these as well.
        if (serviceProperties != null) {
            return TestUtil.containsAll(serviceProperties, meta.getServiceProperties());
        }
        return true;
    }


    /**
     * Perform additional validation on a service.
     *
     * @param meta   The service metadata
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, ComponentMetadata componentMeta) throws Exception {
        assertTrue("Component type mismatch", componentMeta instanceof ServiceExportComponentMetadata);
        ServiceExportComponentMetadata meta = (ServiceExportComponentMetadata)componentMeta;
        assertEquals(exportMode, meta.getAutoExportMode());
        assertEquals(serviceRanking, meta.getRanking());
        assertEquals(dependencies, meta.getExplicitDependencies());
        // given listeners to validate?
        if (listeners != null) {
            Collection metaListeners = meta.getRegistrationListeners();
            assertEquals(listeners.length, metaListeners.size());
            Iterator i = metaListeners.iterator();
            while (i.hasNext()) {
                RegistrationListenerMetadata metaListener = (RegistrationListenerMetadata)i.next();
                assertNotNull(locateListener(metaListener, listeners));
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
    protected RegistrationListener locateListener(RegistrationListenerMetadata meta, RegistrationListener[] listeners) {
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
    public String getName() {
        return serviceId;
    }


    public String toString() {
        return "Exported service name=" + serviceId + ", interfaces=" + serviceInterfaces;
    }
}

