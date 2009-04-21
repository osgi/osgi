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

import org.osgi.service.blueprint.reflect.Listener;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;


/**
 * Metadata describing a reference to a service that is to be imported into the module
 * context from the OSGi service registry.
 *
 */
public class ServiceReferenceMetadataImpl extends ComponentMetadataImpl implements ServiceReferenceMetadata {
    private Collection listeners = new ArrayList();
    private int serviceAvailability = AVAILABILITY_MANDATORY;
    private List interfaceNames = new ArrayList();
    private String filter;
    private String componentName;

    public ServiceReferenceMetadataImpl() {
        this((String)null);
    }

    public ServiceReferenceMetadataImpl(String name) {
        super(name);
    }


    public ServiceReferenceMetadataImpl(ServiceReferenceMetadata source) {
        super(source);

        Iterator i = source.getServiceListeners().iterator();
        while (i.hasNext()) {
            listeners.add(new ListenerImpl((Listener)i.next()));
        }

        interfaceNames.addAll(source.getInterfaceNames());
        serviceAvailability = source.getAvailability();
        filter = source.getFilter();
        componentName = source.getComponentName();
    }


    /**
     * The set of listeners registered to receive bind and unbind events for
     * backing services.
     *
     * @return an immutable collection of registered BindingListenerMetadata
     */
    public Collection getServiceListeners() {
        return listeners;
    }

    public void addServiceListener(Listener listener) {
        listeners.add(listener);
    }


    /**
     * Whether or not a matching service is required at all times.
     *
     * @return one of MANDATORY_AVAILABILITY or OPTIONAL_AVAILABILITY
     */
    public int getAvailability() {
        return serviceAvailability;
    }

    public void setAvailability(int s) {
        serviceAvailability = s;
    }

    /**
     * The interface types that the matching service must support
     *
     * @return an immutable set of type names
     */
    public List getInterfaceNames() {
        return interfaceNames;
    }

    public void addInterfaceName(String name) {
        interfaceNames.add(name);
    }


    /**
     * The filter expression that a matching service must pass
     *
     * @return filter expression
     */
    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

	/**
	 * The value of the component name attribute, if specified.
	 *
	 * @return the component name attribute value, or null if the attribute was not specified
	 */
	public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String name) {
        componentName = name;
    }

}

