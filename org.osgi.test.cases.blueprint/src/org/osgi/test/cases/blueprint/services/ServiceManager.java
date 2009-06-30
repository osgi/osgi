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
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

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
public interface ServiceManager {
    /**
     * Explicitly register ourselves as a service.  This is done
     * via explicit call when we're being used outside of a blueprint
     * context.  When used in a blueprint context, that task is delegated
     * to the blueprint BlueprintContainer.
     */
    public void register();

    /**
     * Explicitly unregister ourselves as a service.  This is done
     * via explicit call when we're being used outside of a blueprint
     * context.  When used in a blueprint context, that task is delegated
     * to the blueprint BlueprintContainer.
     */
    public void unregister();

    /**
     * Add a service to the managed set.  This can be a replacement, which
     * will unregister an existing service with the same name.
     *
     * @param service The service descriptor.
     */
    public void addService(ManagedService service);

    /**
     * Add a set of services to the manager.
     *
     * @param services The array of services.
     */
    public void addServices(ManagedService[] services);

    /**
     * Add a List of services to the manager.
     *
     * @param services The List of services.
     */
    public void addServices(List services);

    /**
     * Start a named service instance managed by this service manager.
     *
     * @param name   The target service name.
     */
    public void registerService(String name);

    /**
     * Register all of the managed services.
     */
    public void registerServices();

    /**
     * Stop a named service instance.
     *
     * @param name   The target service name.
     */
    public void unregisterService(String name);

    /**
     * Unregister all of the managed services.
     */
    public void unregisterServices();

    /**
     * Toggle the state of a named service.
     *
     * @param name   The target service name.
     */
    public void toggleService(String name);

    /**
     * Toggle the state of all managed services.
     */
    public void toggleServices();

    /**
     * Retrieve the set of active services managed by this service
     * instance.
     *
     * @return The service set.
     */
    public ManagedService[] getActiveServices();

    /**
     * A method used for general cleanup.  Also used when a registration
     * service is defined in a managed bundle context.
     */
    public void destroy();


    /**
     * Create a filter for a ServiceManager client using the
     * ServiceManager's bundle context.
     *
     * @param filterString
     *               The source for the filter.
     *
     * @return A created filter instance.
     * @exception InvalidSyntaxException
     */
    public Filter createFilter(String filterString) throws InvalidSyntaxException;
}
