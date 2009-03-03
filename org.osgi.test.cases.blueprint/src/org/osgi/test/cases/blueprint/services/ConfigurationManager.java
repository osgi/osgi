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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;

public class ConfigurationManager {

    // configuration admin service
    protected ConfigurationAdmin admin;
    // configurations to create in this bundle
    protected Map managedConfigs = new HashMap();


    public ConfigurationManager(ConfigurationAdmin admin) {
        this.admin = admin;
    }

    public ConfigurationManager(ConfigurationAdmin admin, ManagedConfigurationInterface config) {
        this(admin);
        this.addConfig(config);
    }

    public ConfigurationManager(ConfigurationAdmin admin, List configlist) {
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

}
