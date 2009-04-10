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

