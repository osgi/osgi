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
import java.util.List;
import java.util.Properties;

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

