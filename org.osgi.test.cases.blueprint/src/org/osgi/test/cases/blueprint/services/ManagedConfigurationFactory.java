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
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class ManagedConfigurationFactory implements ManagedConfigurationInterface {
    // The persistent id of this configuration set
    private String factoryPid;
    // the list of configurations we manage.
    private List/*<Dictionary>*/ dicList;
    // the retrieved Configuration instance
    private List/*<Configuration>*/ configList;

    public ManagedConfigurationFactory(String factoryPid, List dicList) {
        this.factoryPid = factoryPid;
        this.dicList = dicList;
    }


    public String getId() {
        return this.factoryPid;
    }

    /**
     * Register this service instance.
     */
    public void create(ConfigurationAdmin admin) {
        try {
            // make sure this is removed.
            remove(admin);

            for (int i = 0; i < dicList.size(); i++) {
                Dictionary props = (Dictionary)dicList.get(i);
                Configuration config = admin.createFactoryConfiguration(this.factoryPid);
                // create a config for this props set
                config.update(props);
                configList.add(config);
            }
        } catch (Exception e) {
            // just ignore errors for the test (which should not occur)
        }
    }


    /**
     * Remove this admin configuration.  Used for cleanup
     * and to ensure this doesn't exist before starting a
     * test.
     *
     * @param admin  The admin service instance.
     */
    public void remove(ConfigurationAdmin admin) {
        try {
            // remove any reference we might have cached.
            this.dicList = null;
            String spec = '(' + Constants.SERVICE_PID + '=' + this.factoryPid + ')'; //?
            // delete any configs that match this pid.
            Configuration[] configs = admin.listConfigurations(spec);
            if (configs != null) {
                for (int i = 0; i < configs.length; i++) {
                    configs[i].delete();
                }
            }
        } catch (Exception e) {
            // just ignore errors for the test (which should not occur)
        }
    }


    /**
     * Add a property set of managed properties.
     *
     * @param newProps The new property set.
     */
    public void add(ConfigurationAdmin admin, Dictionary newProps) {
        try {
            Configuration config = admin.createFactoryConfiguration(this.factoryPid);
            config.update(newProps);
            dicList.add(newProps);
        } catch (Exception e) {
            // just ignore errors for the test (which should not occur)
        }
    }


    /**
     * Update a new set of properties for this configuration.
     *
     * @param newProps The new property set.
     */
    public void update(ConfigurationAdmin admin, Dictionary newProps) {
        try {
            String spec = '(' + Constants.SERVICE_PID + '=' + this.factoryPid + ')';
            // all configs that match this factory pid.
            Configuration[] configs = admin.listConfigurations(spec);
            if (configs != null) {
                // just update the first one
                configs[0].update(newProps);
                // configs[0] should have stay in the configList
            }
        } catch (Exception e) {
            // just ignore errors for the test (which should not occur)
        }
    }



}

