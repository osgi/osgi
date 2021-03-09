/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.dmt.tc3.tbc.ConfigurationPlugin;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class ConfigPluginActivator implements BundleActivator {
    static final String DMT_CONFIG_PLUGIN_SERVICE_PID = 
        "org.osgi.impl.service.dmt.config";
    
    static final String[] PLUGIN_ROOT_PATH = new String[] {
        ".", "OSGi", "Configuration"
    };
    static final String PLUGIN_ROOT_URI = "./OSGi/Configuration";

	private ServiceRegistration< ? >								servReg;
	private ServiceTracker<ConfigurationAdmin,ConfigurationAdmin>	configTracker;
	private ServiceTracker<LogService,LogService>					logTracker;
	private ConfigPlugin        configPlugin;

    
	@Override
	public void start(BundleContext bc) throws BundleException {
		System.out.println("Configuration plugin activation started.");
        
		// looking up the Configuration Admin and the Log service
        configTracker = 
				new ServiceTracker<>(bc, ConfigurationAdmin.class, null);
        configTracker.open();
        
		logTracker = new ServiceTracker<>(bc, LogService.class, null);
        logTracker.open();
        
		// creating the service
		configPlugin = new ConfigPlugin(configTracker, logTracker);
        
		// registering the service
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put("dataRootURIs", new String[] { PLUGIN_ROOT_URI });
        properties.put("service.pid", DMT_CONFIG_PLUGIN_SERVICE_PID);
        String[] services = new String[] {
                DataPlugin.class.getName(),
                ManagedService.class.getName()
        };
		servReg = bc.registerService(services, configPlugin, properties);
		System.out.println("Configuration plugin activation finished successfully.");
	}

	@Override
	public void stop(BundleContext bc) throws BundleException {
        // unregistering the service
		servReg.unregister();
        
		// stopping the trackers
		configTracker.close();
		logTracker.close();
	}
}
