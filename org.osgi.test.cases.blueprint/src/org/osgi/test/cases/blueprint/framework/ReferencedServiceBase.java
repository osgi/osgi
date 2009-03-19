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
import java.util.Set;

import junit.framework.Assert;

import org.osgi.service.blueprint.reflect.BindingListenerMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceComponentMetadata;

/**
 * A single referenced service in the ModuleContext metadata.
 */
public class ReferencedServiceBase extends Assert implements TestComponentMetadata {
    // optional name of the component
    protected String name;
    // the set of exported interfaces
    protected Set serviceInterfaces;
    // the expected service availability.
    protected int serviceAvailability;
    // the request filter string
    protected String filter;
    // the list of binding listeners
    protected BindingListener[] listeners;


    /**
     * Create a ReferenceService descriptor from a single interface.
     *
     * @param interfaceClass
     *                  A single interface class used for the reference.
     * @param availability
     *                  The availability setting.
     * @param filter    The declared filter string for the reference.
     * @param listeners An expected set of listener metadata.
     */
    public ReferencedServiceBase(String name, Class interfaceClass, int availability, String filter, BindingListener[] listeners) {
        this(name, new Class[] { interfaceClass }, availability, filter, listeners);
    }

    /**
     * Create a ReferenceService descriptor.
     *
     * @param interfaces The set of interfaces to access.
     * @param availability
     *                   The availability setting.
     * @param filter     The declared filter string for the reference.
     * @param listeners  An expected set of listener metadata.
     */
    public ReferencedServiceBase(String name, Class[] interfaces, int availability, String filter, BindingListener[] listeners) {
        this.name = name;
        this.serviceAvailability = availability;
        this.filter = filter;
        this.listeners = listeners;
        // convert this into a set
        this.serviceInterfaces = new HashSet();
        for (int i = 0; i < interfaces.length; i++) {
            serviceInterfaces.add(interfaces[i]);
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
        if (!(componentMeta instanceof ServiceReferenceComponentMetadata)) {
            return false;
        }
        ServiceReferenceComponentMetadata meta = (ServiceReferenceComponentMetadata)componentMeta;

        // match on the interfaces first
        if (!serviceInterfaces.equals(meta.getInterfaceNames())) {
            return false;
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
    public void validate(ModuleMetadata moduleMetadata, ComponentMetadata componentMeta) throws Exception {
        assertTrue("Component type mismatch", componentMeta instanceof ServiceReferenceComponentMetadata);
        ServiceReferenceComponentMetadata meta = (ServiceReferenceComponentMetadata)componentMeta;
        // if we have a name to compare, they must be equal
        if (name != null) {
            assertEquals(name, getName());
        }
        assertEquals(serviceAvailability, meta.getServiceAvailabilitySpecification());
        // we might have a listener list also
        if (listeners != null) {
            Collection bindingListeners = meta.getBindingListeners();
            assertEquals("Mismatch on binding listener list", listeners.length, bindingListeners.size());
            for (int i = 0; i < listeners.length; i++) {
                BindingListener s = listeners[i];
                BindingListenerMetadata l = locateBindingListener(bindingListeners, s);
                assertNotNull("Missing binding listener", l);
                // do additional validation on the listener definition.
                s.validate(moduleMetadata, l);
            }
        }
    }


    /**
     * Locate a matching metadata value for a listener.
     *
     * @param services
     * @param service  The set of services for this bundle.
     *
     * @return The matching services metadata, or null if no match was found.
     */
    protected BindingListenerMetadata locateBindingListener(Collection listeners, BindingListener listener) {
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            BindingListenerMetadata meta = (BindingListenerMetadata)i.next();
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
    public String getName() {
        return name;
    }
}

