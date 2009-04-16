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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.service.blueprint.reflect.BindingListenerMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceComponentMetadata;


/**
 * A ServiceReferenceComponentMetadata implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class ServiceReferenceComponentMetadataImpl extends ComponentMetadataImpl implements ServiceReferenceComponentMetadata {
    private Collection bindingListeners = new ArrayList();
    private int serviceAvailability = MANDATORY_AVAILABILITY;
    private Set interfaceNames = new HashSet();
    private String filter;
    private String componentName;


    public ServiceReferenceComponentMetadataImpl() {
        this((String)null);
    }

    public ServiceReferenceComponentMetadataImpl(String name) {
        super(name);
    }


    public ServiceReferenceComponentMetadataImpl(ServiceReferenceComponentMetadata source) {
        super(source);

        Iterator i = source.getBindingListeners().iterator();
        while (i.hasNext()) {
            bindingListeners.add(new BindingListenerMetadataImpl((BindingListenerMetadata)i.next()));
        }

        interfaceNames.addAll(source.getInterfaceNames());
        serviceAvailability = source.getServiceAvailabilitySpecification();
        filter = source.getFilter();
        componentName = source.getComponentName();
    }


    /**
     * The set of listeners registered to receive bind and unbind events for
     * backing services.
     *
     * @return an immutable collection of registered BindingListenerMetadata
     */
    public Collection getBindingListeners() {
        return bindingListeners;
    }

    public void addBindingListener(BindingListenerMetadata listener) {
        bindingListeners.add(listener);
    }


    /**
     * Whether or not a matching service is required at all times.
     *
     * @return one of MANDATORY_AVAILABILITY or OPTIONAL_AVAILABILITY
     */
    public int getServiceAvailabilitySpecification() {
        return serviceAvailability;
    }

    public void setServiceAvailabilitySpecification(int s) {
        serviceAvailability = s;
    }

    /**
     * The interface types that the matching service must support
     *
     * @return an immutable set of type names
     */
    public Set getInterfaceNames() {
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

