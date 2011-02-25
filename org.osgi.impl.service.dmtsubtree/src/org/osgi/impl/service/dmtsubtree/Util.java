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


package org.osgi.impl.service.dmtsubtree;

import java.util.Vector;

import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.dmtsubtree.mapping.VendorPluginInfo;


public class Util implements Constants {
	
	private Activator activator;
	
	public Util( Activator activator ) {
		this.activator = activator;
	}
	
	/*
	 * extracts and array of VendorPluginInfos from the properties of the given
	 * ServiceReference
	 */
	public VendorPluginInfo[] extractVendorPluginInfo (
			ServiceReference pluginRef) {
		Vector pluginInfos = new Vector();
		Object tmp = pluginRef.getProperty(_CONF_DATA_ROOT_URIs);
		if ( ! (tmp instanceof String[]) ) {
			throw new RuntimeException( "the '"+_CONF_DATA_ROOT_URIs+"' property is not a String[]");
		}
		String[] dataRootURIs = (String[]) tmp; 

		tmp = pluginRef.getProperty( _CONF_PATHS );
		if ( ! (tmp instanceof String[]) ) {
			throw new RuntimeException( "the '" + _CONF_PATHS + "' property is not a String[]");
		}
		String[] configPaths = (String[]) tmp;

		tmp = pluginRef.getProperty( _CONF_MULTIPLES );
		if ( ! (tmp instanceof Boolean[]) ) {
			activator.logInfo( "the '" + _CONF_MULTIPLES + "' property is missing or not a Boolean[]");
			// creating default multiples array
			Boolean[] m = new Boolean[configPaths.length];
			for (int i = 0; i < m.length; i++) {
				m[i] = new Boolean(false);
			}
			tmp = m;
		}
		Boolean[] multiples = (Boolean[]) tmp;

		tmp = pluginRef.getProperty( _CONF_DMT_ACTIONS );
		String[] dmtActions = null;
		if ( (tmp == null ) || ! (tmp instanceof String[]) ) {
			activator.logInfo( "the '" + _CONF_DMT_ACTIONS + "' property is missing or not a String[]");
		}
		else {
			dmtActions = (String[]) tmp;
			if ( dmtActions != null ) {
				// length of dmtActions must match number of configurationPaths
				if ( dmtActions.length != configPaths.length ) {
					throw new RuntimeException( "the number of dmtAction -Strings does not match the number of hgConfigurationPath-Strings --> ignoring this DataPlugin");
				}
			}
		}

		if (dataRootURIs != null && configPaths != null && multiples != null
				&& dataRootURIs.length == configPaths.length && dataRootURIs.length == multiples.length) {
			for (int i = 0; i < dataRootURIs.length; i++) {
				String dataRootURI = dataRootURIs[i];
				String configurationPath = configPaths[i];
				boolean multiple = (multiples[i] != null && multiples[i].booleanValue() );
				// ensure that the configurationPath is absolute
				if ( ! configurationPath.startsWith( "./" )) {
					activator
					.logError(
							"Invalid registration properties: the configurationPath '" + configurationPath + "' is not absolute --> ignoring this path",
							null);
					continue;
				}
				VendorPluginInfo pluginInfo = new VendorPluginInfo(dataRootURI,
						configurationPath, multiple);
				if ( dmtActions != null ) {
					pluginInfo.setDmtActionNode( dmtActions[i] );
				}
				pluginInfos.addElement(pluginInfo);
			}
		}
		else {
			activator
					.logError(
							"Invalid registration properties: the sizes of the dataRootURIs, configurationPaths and configurationMultiples Arrays don't match --> ignoring this DataPlugin",
							null);
		}
		return (VendorPluginInfo[]) pluginInfos
				.toArray(new VendorPluginInfo[0]);
	}

	
	/*
	 * utility method to dump the properties of a ServiceReference
	 */
	public void dumpProperties(ServiceReference ref) {
		if (ref == null)
			return;
		String[] keys = ref.getPropertyKeys();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			activator.logDebug(key + ": " + ref.getProperty(key));
		}
	}
	
	public int countSlashes( String s ) {
		int count = 0;
		int p = -1;
		while ( (p = s.indexOf( '/', p+1 )) != -1) 
			count++;
		return count;
	}


}
