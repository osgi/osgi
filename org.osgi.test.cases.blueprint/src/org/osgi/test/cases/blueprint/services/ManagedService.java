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
package org.osgi.test.cases.blueprint.services;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.Constants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.ServiceReference;

public class ManagedService {
    // The registered name of this service
    public String name;
    // the owning bundle context.
    public BundleContext context;
    // The exported interface names
    public String[] interfaces;
    // The properties published with this service
    public Properties props;
    // The service instance
    public Object service;
    // a ranking element used on the definition
    public int ranking;
    // the current ServiceRegistration for this object
    protected ServiceRegistration registration;
    // the "start on initialization flag"
    public boolean start;

    /**
     * Constructor for creating this as a blueprint component.
     */
    public ManagedService() {
    }

    public ManagedService(String name, Object service, Class interfaceClass, BundleContext context, Properties props, boolean start) {
        this(name, service, new Class[] { interfaceClass }, context, props, start);
    }

    public ManagedService(String name, Object service, Class[] interfaceClasses, BundleContext context, Properties props, boolean start) {
        this.name = name;
        this.service = service;
        this.interfaces = new String[interfaceClasses.length];
        for (int i = 0; i < interfaceClasses.length; i++) {
            interfaces[i] = interfaceClasses[i].getName();
        }

        this.context = context;
        this.props = props;
        // we will register at least the service name as a property, so create a table if
        // not given one.
        if (this.props == null) {
            this.props = new Properties();
        }
        // add the service name
        this.props.put("test.service.name", name);
        // nothing registered yet
        registration = null;
        this.start = start;
    }

    // we need some setter methods so that we can create instances of this
    // inside blueprint bundles.

    public void setName(String name) {
        this.name = name;
        if (props == null) {
            props = new Properties();
        }
        // add the service name
        props.put("test.service.name", name);
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }

    public void setInterfaces(Set interfaceNames) {
        this.interfaces = new String[interfaceNames.size()];
        Iterator i = interfaceNames.iterator();
        int index = 0;
        while (i.hasNext()) {
            interfaces[index++] = (String)i.next();
        }
    }

    public void setProperties(Properties props) {
        // we include the name inside the properties, so don't
        // remove any existing properties bundle.
        if (this.props == null) {
            this.props = props;
        }
        else {
            this.props.putAll(props);
        }
    }

    public void setService(Object service) {
        this.service = service;
    }

    public void setBundleContext(BundleContext context) {
        this.context = context;
    }

    public void setRanking(int r) {
        ranking = r;
        if (props == null) {
            props = new Properties();
        }
        // add the service name
        props.put(Constants.SERVICE_RANKING, new Integer(r));
    }

    public void setStart(boolean s) {
        start = s;
    }


    /**
     * Register this service instance.
     */
    public void register() {
        // belt-and-braces in case things get out of sync
        if (registration == null) {
            // go register this service
            registration = context.registerService(interfaces, service, props);
        }
    }


    /**
     * unregister this service instance.
     */
    public void unregister() {
        // belt-and-braces in case things get out of sync
        if (registration != null) {
            // unregister and delete the registration reference
            registration.unregister();
            registration = null;
        }
    }


    /**
     * Toggle the service registration state between registered/unregistered.
     */
    public void toggle() {
        if (isRegistered()) {
            unregister();
        }
        else {
            register();
        }
    }


    /**
     * Test if this service is registered currently.
     *
     * @return true if the service is registered, false otherwise.
     */
    public boolean isRegistered() {
        return registration != null;
    }

    /**
     * Compare a registration value against a service reference.  This
     * matches largely on the registered name property.
     *
     * @param ref    The source ServiceReference.
     *
     * @return True if the ServiceReference instance is for this descriptor,
     *         false for a mismatch.
     */
    public boolean equals(ServiceReference ref) {
        String refName = (String)ref.getProperty("test.service.name");
        // can't match if there's no name property set.
        if (refName == null) {
            return false;
        }
        // compare the names.
        return refName.equals(name);
    }

    /**
     * Get the registered service id for this service.  Returns null
     * if the service is not currently registered.
     *
     * @return The Long service id for the service.
     */
    public Long getServiceId() {
        if (!isRegistered()) {
            return null;
        }

        ServiceReference ref = registration.getReference();
        return (Long)ref.getProperty(Constants.SERVICE_ID);
    }


    /**
     * Validate if this is a service instance matches
     * the one defined here.
     *
     * @param service The service instance.
     *
     * @return true if this is a service match, false otherwise.
     */
    public boolean isService(Object checkedService) {
        // sometimes the expected list comes from the ServiceManager, but in some tests,
        // this is encoded in the blueprint configuration file.  Our preferred means is
        // direct comparison, but we can also matching using just the name.
        if (service != null) {
            // This is dependent upon our service instances overriding the equals() method.
            // this also ensures that any service proxies that the blueprint service creates
            // is overriding the equals method appropriate.
            return checkedService.equals(service);
        }
        else {
            return ((TestServiceOne)checkedService).getServiceName().equals(name);
        }
    }


    /**
     * Validate if this is a service instance matches
     * the one defined here.
     *
     * @param service The service instance.
     *
     * @return true if this is a service match, false otherwise.
     */
    public boolean isService(ServiceReference serviceRef) {
        Object checkedService = context.getService(serviceRef);
        boolean result = isService(checkedService);
        context.ungetService(serviceRef);
        return result;
    }
}

