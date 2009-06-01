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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.cm.ConfigurationAdmin;

public class ConfigurationManagerImpl implements ConfigurationManager {

    // configuration admin service
    protected ConfigurationAdmin admin;
    // configurations to create in this bundle
    protected Map managedConfigs = new HashMap();


    public ConfigurationManagerImpl(ConfigurationAdmin admin) {
        this.admin = admin;
    }

    public ConfigurationManagerImpl(ConfigurationAdmin admin, ManagedConfigurationInterface config) {
        this(admin);
        this.addConfig(config);
    }

    public ConfigurationManagerImpl(ConfigurationAdmin admin, List configlist) {
        this(admin);
        this.addConfigs(configlist);
    }

    // getter
    public ManagedConfigurationInterface getConfig(String id){
        return (ManagedConfigurationInterface)this.managedConfigs.get(id);
    }

    public ConfigurationAdmin getConfigurationAdmin(){
        return this.admin;
    }

    /**
     * Add a configuration.
     *
     * @param config
     *            The configuration.
     */
    public void addConfig(ManagedConfigurationInterface config) {
        // Id could be either the pid or the factory-pid
        // when id = pid, the config represents a ManagedConfiguration that one-one maps to a configuration object
        // when id = factory-pid, the config represents a ManagedConfigurationFactory that contains many configuration objects
        this.managedConfigs.put(config.getId(), config);
        config.create(this.admin);
    }

    /**
     * Add a List of configurations.
     *
     * @param managedConfigs
     *            The List of configurations.
     */
    public void addConfigs(List configlist) {
        for (int i = 0; i < configlist.size(); i++) {
            this.addConfig((ManagedConfigurationInterface) configlist.get(i));
        }
    }

    /**
     * Remove all the configurations.
     */
    public void removeAllConfigs(){
        Iterator i = this.managedConfigs.values().iterator();
        while (i.hasNext()){
            ManagedConfigurationInterface config = (ManagedConfigurationInterface)i.next();
            config.remove(this.admin);
        }
        this.managedConfigs.clear();
    }


}
