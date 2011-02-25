/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.impl.service.dmtsubtree.mapping;

import java.io.IOException;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.impl.service.dmtsubtree.Activator;
import org.osgi.impl.service.dmtsubtree.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class IDPersistence  {

	private static final String	_PID	= "DMTSubtree_Persistence";

	private Activator			activator;

	public IDPersistence(Activator activator) {
		this.activator = activator;
	}

	/**
	 * stores an eventHandlerProxy in the configuration
	 * 
	 * @param ehp
	 */
	public void storeIDMapping(String dataRootUri, String hgConfigurationPath,
			String id) {
		ConfigurationAdmin ca = (ConfigurationAdmin) activator.cfgTracker
				.getService();
		if (ca != null) {
			try {
				// store only, if not there already
				if ( getIDMapping(dataRootUri, hgConfigurationPath, id) == null ) {
					Dictionary props = new Hashtable();
					props.put("dataRootURI", dataRootUri);
					props.put(Constants._CONF_PATHS, hgConfigurationPath);
					props.put("id", id);
	
					activator.logDebug("storing idMapping for: " + dataRootUri
							+ "::" + hgConfigurationPath + "=" + id);
					Configuration config = ca.createFactoryConfiguration(_PID);
					config.update(props);
				}
			}
			catch (IOException x) {
				// TODO Auto-generated catch block
				activator.logError(
						"unable to create new factory configuration for pid: "
								+ _PID, x);
			}
		}
	}

	public Configuration getIDMapping(String dataRootUri, String hgConfigurationPath,
			String id) {
		Configuration configuration = null;
		ConfigurationAdmin ca = (ConfigurationAdmin) activator.cfgTracker
				.getService();
		if (ca != null) {
			try {
				StringBuffer filter = new StringBuffer();
				filter.append( "(&" );
				filter.append( "(" + ConfigurationAdmin.SERVICE_FACTORYPID + "=" + _PID + ")");
				filter.append( "(dataRootURI=" + dataRootUri + ")");
				filter.append( "(" + Constants._CONF_PATHS + "=" + hgConfigurationPath + ")");
				filter.append( "(id=" + id + ")");
				filter.append( ")" );
				
				Configuration[] configs = ca.listConfigurations( filter.toString() );
				if ( configs != null && configs.length > 0 )
					configuration = configs[0];
			}
			catch (IOException x) {
				// TODO Auto-generated catch block
				activator.logError(
						"unable to create new factory configuration for pid: "
								+ _PID, x);
			}
			catch (InvalidSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return configuration;
	}

	/**
	 * returns a Vector of LinkInfo's Each LinkInfo describes the callback of
	 * the (former) local EventHandlerProxy.
	 */
	public Configuration[] getIDMappings() {
		Configuration[] configs = null;
		ConfigurationAdmin ca = (ConfigurationAdmin) activator.cfgTracker
				.getService();
		if (ca != null) {

			String filter = "(" + ConfigurationAdmin.SERVICE_FACTORYPID + "="
					+ _PID + ")";
			activator.logDebug("IDPersistence: getIDMappings() for filter = "
					+ filter);
			try {
				configs = ca.listConfigurations(filter);
			}
			catch (IOException x) {
				activator.logError(
						"unable to read factory configurations for pid: "
								+ _PID, x);
			}
			catch (InvalidSyntaxException x) {
				activator.logError(
						"unable to read factory configurations for pid: "
								+ _PID, x);
			}
		}
		return configs;
	}

}
