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

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class ManagedConfiguration implements ManagedConfigurationInterface {
    // The pid of this configuration object
    private String pid;
    // the configuation properties
    private Dictionary dic;
    // the retrieved Configuration instance
    private Configuration config;



    public ManagedConfiguration(String pid, Dictionary dic) {
        this.pid = pid;
        this.dic = dic;
    }

    public String getId() {
        return this.pid;
    }

    /**
     * Register this service instance.
     */
    public void create(ConfigurationAdmin admin) {
        try {
            // make sure this is removed.
            remove(admin);
            // create and update the properties.
            config = admin.getConfiguration(pid);
            config.update(dic);
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
            config = null;
            String spec = '(' + Constants.SERVICE_PID + '=' + pid + ')';
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
     * Update a new set of properties for this configuration.
     *
     * @param newProps The new property set.
     */
    public void update(ConfigurationAdmin admin, Dictionary newProps) {
        try {
            dic = newProps;
            config.update(dic);
        } catch (Exception e) {
            // just ignore errors for the test (which should not occur)
        }
    }


    /**
     * this is a NOP for a non-factory configuration.
     *
     * @param newProps The new property set.
     */
    public void add(ConfigurationAdmin admin, Dictionary newProps) {
        // NOP

    }



}


