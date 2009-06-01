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

package org.osgi.test.cases.blueprint.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * An interface for a ServiceManager service to be made available
 * for some tests.  Some of the more complicated tests require
 * control over service creation/deletion from other bundles.
 * The service manager instance allows the managed blueprint
 * test components access to these services and the ability to
 * register/deregister the services as needed.
 *
 * For service collection tests, this service also allows retrieval
 * of active service lists for comparison purposes.
 */
public class ServiceManagerImpl implements ServiceManager {
    // our list of managed services
    protected Map services = new HashMap();
    // our creation bundle context
    protected BundleContext context;
    // our ServiceRegistration instance
    protected ServiceRegistration registration;

    /**
     * A null constructor is required to be able to export this
     * as a service interface.
     */
    public ServiceManagerImpl() {
    }

    public ServiceManagerImpl(BundleContext context) {
        this.context = context;
    }

    public ServiceManagerImpl(BundleContext context, ManagedService[] services) {
        this(context);
        addServices(services);
    }

    public ServiceManagerImpl(BundleContext context, ManagedService service) {
        this(context, new ManagedService[] { service });
    }

    public ServiceManagerImpl(BundleContext context, List services) {
        this(context);
        addServices(services);
    }

    /**
     * Explicitly register ourselves as a service.  This is done
     * via explicit call when we're being used outside of a blueprint
     * context.  When used in a blueprint context, that task is delegated
     * to the blueprint BlueprintContainer.
     */
    public void register() {
        if (registration == null) {
            registration = context.registerService("org.osgi.test.cases.blueprint.services.ServiceManager", this, null);
        }
    }

    /**
     * Explicitly unregister ourselves as a service.  This is done
     * via explicit call when we're being used outside of a blueprint
     * context.  When used in a blueprint context, that task is delegated
     * to the blueprint BlueprintContainer.
     */
    public void unregister() {
        if (registration != null) {
            registration.unregister();
            registration = null;
        }
    }


    /**
     * Add a service to the managed set.  This can be a replacement, which
     * will unregister an existing service with the same name.
     *
     * @param service The service descriptor.
     */
    public void addService(ManagedService service) {
        // if we have an existing one here, make sure it's
        // unregistered.
        unregisterService(service.name);
        services.put(service.name, service);
        // if asked to register this immediately, do so
        if (service.start) {
            service.register();
        }
    }

    /**
     * Add a set of services to the manager.
     *
     * @param services The array of services.
     */
    public void addServices(ManagedService[] services) {
        // add all of the services to our managed set
        for (int i = 0; i < services.length; i++) {
            addService(services[i]);
        }
    }

    /**
     * Add a List of services to the manager.
     *
     * @param services The List of services.
     */
    public void addServices(List services) {
        // add all of the services to our managed set
        for (int i = 0; i < services.size(); i++) {
            addService((ManagedService)services.get(i));
        }
    }


    /**
     * Start a named service instance managed by this service manager.
     *
     * @param name   The target service name.
     */
    public void registerService(String name) {
        ManagedService service = (ManagedService)services.get(name);
        if (service != null) {
            service.register();
            // since we're dealing with asynchronous events here, sleep for a little bit
            // to allow everything to update.
            sleep();
        }
    }

    /**
     * Register all of the managed services.
     */
    public void registerServices() {
        // ask each service instance to register itself.
        Iterator i = services.values().iterator();
        while (i.hasNext()) {
            ManagedService service = (ManagedService)i.next();
            service.register();
        }
        // since we're dealing with asynchronous events here, sleep for a little bit
        // to allow everything to update.
        sleep();
    }


    /**
     * Stop a named service instance.
     *
     * @param name   The target service name.
     */
    public void unregisterService(String name) {
        ManagedService service = (ManagedService)services.get(name);
        if (service != null) {
            service.unregister();
            // since we're dealing with asynchronous events here, sleep for a little bit
            // to allow everything to update.
            sleep();
        }
    }

    /**
     * Unregister all of the managed services.
     */
    public void unregisterServices() {
        // ask each service instance to register itself.
        Iterator i = services.values().iterator();
        while (i.hasNext()) {
            ManagedService service = (ManagedService)i.next();
            service.unregister();
        }
        // since we're dealing with asynchronous events here, sleep for a little bit
        // to allow everything to update.
        sleep();
    }


    /**
     * Toggle the state of a named service.
     *
     * @param name   The target service name.
     */
    public void toggleService(String name) {
        ManagedService service = (ManagedService)services.get(name);
        if (service != null) {
            service.toggle();
            // since we're dealing with asynchronous events here, sleep for a little bit
            // to allow everything to update.
            sleep();
        }
    }

    /**
     * Toggle the state of all managed services.
     */
    public void toggleServices() {
        // ask each service instance to register itself.
        Iterator i = services.values().iterator();
        while (i.hasNext()) {
            ManagedService service = (ManagedService)i.next();
            service.toggle();
        }
        // since we're dealing with asynchronous events here, sleep for a little bit
        // to allow everything to update.
        sleep();
    }

    /**
     * Retrieve the set of active services managed by this service
     * instance.
     *
     * @return The service set.
     */
    public ManagedService[] getActiveServices() {
        List result = new ArrayList();
        // ask each service instance to register itself.
        Iterator i = services.values().iterator();
        while (i.hasNext()) {
            // only add the registered services
            ManagedService service = (ManagedService)i.next();
            if (service.isRegistered()) {
                result.add(service);
            }
        }
        return (ManagedService[])result.toArray(new ManagedService[result.size()]);
    }


    public void sleep() {
        try {
            // tenth a second should be sufficiently long, likely longer than is needed.
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
    }


    /**
     * A method used for general cleanup.  Also used when a registration
     * service is defined in a managed bundle context.
     */
    public void destroy() {
        unregisterServices();
        // unregister ourselves, if needed.
        unregister();
    }
}
