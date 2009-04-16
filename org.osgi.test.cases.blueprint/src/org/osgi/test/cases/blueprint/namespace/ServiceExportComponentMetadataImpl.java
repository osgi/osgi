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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.osgi.service.blueprint.reflect.RegistrationListenerMetadata;
import org.osgi.service.blueprint.reflect.ServiceExportComponentMetadata;
import org.osgi.service.blueprint.reflect.Value;


/**
 * A ServiceExportComponentMetadata implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class ServiceExportComponentMetadataImpl extends ComponentMetadataImpl implements ServiceExportComponentMetadata {
    private Map serviceProperties;
    private Value exportedComponent;
    private int ranking = 0;
    private Set interfaceNames;
    private Collection registrationListeners;
    private int exportMode = EXPORT_MODE_DISABLED;


    public ServiceExportComponentMetadataImpl() {
        this((String)null);
    }

    public ServiceExportComponentMetadataImpl(String name) {
        super(name);
        // create a new one of these, even if empty
        serviceProperties = new HashMap();
        interfaceNames = new HashSet();
        registrationListeners = new ArrayList();
    }


    public ServiceExportComponentMetadataImpl(ServiceExportComponentMetadata source) {
        super(source);
        serviceProperties = new HashMap(source.getServiceProperties());
        exportedComponent = NamespaceUtil.cloneValue(exportedComponent);
        ranking = source.getRanking();
        interfaceNames = new HashSet(source.getInterfaceNames());
        Iterator i = source.getRegistrationListeners().iterator();
        registrationListeners = new ArrayList();
        while (i.hasNext()) {
            registrationListeners.add(new RegistrationListenerMetadataImpl((RegistrationListenerMetadata)i.next()));
        }
        exportMode = source.getAutoExportMode();
    }



    /**
     * The user declared properties to be advertised with the service.
     *
     * @return Map containing the set of user declared service properties (may be
     * empty if no properties were specified).
     */
    public Map getServiceProperties() {
        return serviceProperties;
    }

    public void setServiceProperties(Map p) {
        serviceProperties = p;
    }

    /**
     * The component that is to be exported as a service. Value must refer to a component and
     * therefore be either a ComponentValue or ReferenceValue.
     *
     * @return the component to be exported as a service.
     */
    public Value getExportedComponent() {
        return exportedComponent;
    }

    public void setExportedComponent(Value v) {
        exportedComponent = v;
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
    public Set getInterfaceNames() {
        return interfaceNames;
    }

    public void addInterfaceName(String name) {
        interfaceNames.add(name);
    }


    /**
     * The listeners that have registered to be notified when the exported service
     * is registered and unregistered with the framework.
     *
     * @return an immutable collection of RegistrationListenerMetadata
     */
    public Collection getRegistrationListeners() {
        return registrationListeners;
    }

    public void addRegistrationListener(RegistrationListenerMetadata l) {
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
}

