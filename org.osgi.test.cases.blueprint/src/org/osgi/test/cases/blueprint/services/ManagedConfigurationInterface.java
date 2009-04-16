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

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationAdmin;

public interface ManagedConfigurationInterface {



    public String getId();

    /**
     * Register this service instance.
     */
    public void create(ConfigurationAdmin admin);


    /**
     * Remove this admin configuration.  Used for cleanup
     * and to ensure this doesn't exist before starting a
     * test.
     *
     * @param admin  The admin service instance.
     */
    public void remove(ConfigurationAdmin admin);


    /**
     * Update a new set of properties for this configuration.
     *
     * @param newProps The new property set.
     */
    public void update(ConfigurationAdmin admin, Dictionary newProps);


    /**
     * this is a NOP for a non-factory configuration.
     *
     * @param newProps The new property set.
     */
    public void add(ConfigurationAdmin admin, Dictionary newProps);
}



