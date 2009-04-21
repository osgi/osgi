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
package org.osgi.test.cases.blueprint.namespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.blueprint.reflect.RegistrationListener;
import org.osgi.service.blueprint.reflect.ServiceMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.blueprint.reflect.NonNullMetadata;
import org.osgi.service.blueprint.reflect.Target;


/**
 * A ServiceMetadata implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class ServiceMetadataImpl extends ComponentMetadataImpl implements ServiceMetadata {
    private List serviceProperties;
    private Target serviceComponent;
    private int ranking = 0;
    private List interfaceNames;
    private Collection registrationListeners;
    private int exportMode = AUTO_EXPORT_DISABLED;
    private List dependencies;


    public ServiceMetadataImpl() {
        this((String)null);
    }

    public ServiceMetadataImpl(String name) {
        super(name);
        // create a new one of these, even if empty
        serviceProperties = new ArrayList();
        interfaceNames = new ArrayList();
        registrationListeners = new ArrayList();
        dependencies = new ArrayList();
    }


    public ServiceMetadataImpl(ServiceMetadata source) {
        super(source);
        serviceProperties = new ArrayList();
        setServiceProperties(source.getServiceProperties());
        serviceComponent = (Target)NamespaceUtil.cloneMetadata(source.getServiceComponent());
        ranking = source.getRanking();
        interfaceNames = new ArrayList(source.getInterfaceNames());
        Iterator i = source.getRegistrationListeners().iterator();
        registrationListeners = new ArrayList();
        while (i.hasNext()) {
            registrationListeners.add(new RegistrationListenerImpl((RegistrationListener)i.next()));
        }
        exportMode = source.getAutoExportMode();

        dependencies = new ArrayList();
        dependencies.addAll(source.getExplicitDependencies());
    }



    /**
     * The user declared properties to be advertised with the service.
     *
     * @return List containing the set of user declared service properties (may be
     * empty if no properties were specified).
     */
    public List getServiceProperties() {
        return serviceProperties;
    }

    public void setServiceProperties(List v) {
        Iterator i = v.iterator();
        while (i.hasNext()) {
            MapEntry entry = (MapEntry)i.next();
            serviceProperties.add(new MapEntryImpl(entry));
        }
    }

    /**
     * The component that is to be exported as a service.
     *
     * @return the component to be exported as a service.
     */
    public Target getServiceComponent() {
        return serviceComponent;
    }

    public void setServiceComponent(Target v) {
        serviceComponent = v;
    }

    /**
     * The ranking value to use when advertising the service
     *
     * @return service ranking
     */
    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    /**
     * The type names of the set of interface types that the service should be advertised
     * as supporting.
     *
     * @return an immutable set of (String) type names, or an empty set if using auto-export
     */
    public List getInterfaceNames() {
        return interfaceNames;
    }

    public void addInterfaceName(String name) {
        interfaceNames.add(name);
    }


    /**
     * The listeners that have registered to be notified when the exported service
     * is registered and unregistered with the framework.
     *
     * @return an immutable collection of RegistrationListener
     */
    public Collection getRegistrationListeners() {
        return registrationListeners;
    }

    public void addRegistrationListener(RegistrationListener l) {
        registrationListeners.add(l);
    }

    /**
     * Return the auto-export mode specified.
     *
     * @return One of EXPORT_MODE_DISABLED, EXPORT_MODE_INTERFACES, EXPORT_MODE_CLASS_HIERARCHY, EXPORT_MODE_ALL
     */
    public int getAutoExportMode() {
        return exportMode;
    }

    public void setAutoExportMode(int mode) {
        exportMode = mode;
    }

    /**
     * The names of any components listed in a "depends-on" attribute for this
     * component.
     *
     * @return an immutable set of component names for components that we have explicitly
     * declared a dependency on, or an empty set if none.
     */
    public List/*<RefMetadata>*/ getExplicitDependencies() {
        return dependencies;
    }


    /**
     * Add a new dependency to the explicit list.
     *
     * @param name   The new dependency name.
     */
    public void addDependency(String name) {
        dependencies.add(new RefMetadataImpl(name));
    }
}

