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

import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.dmtsubtree.Constants;


/**
 * Data holder class for a Vendor DataPlugin.
 * 
 *  
 * @version $Revision$
 */
public class VendorPluginInfo implements Constants {
	
	// mandatory props
	private String	dataRootURI;
	private String	configurationPath;
	private boolean	multiple;
	
	// optional props
	private String dmtActionNode;

	/**
	 * creates a VendorPluginInfo from a ServiceReference of with objectclass=org.osgi.impl.service.dmtsubtree.VendorDataPlugin
	 * @param ref
	 */
	public VendorPluginInfo( ServiceReference ref ) {
		this.dataRootURI = (String) ref.getProperty( _CONF_DATA_ROOT_URIs );
		this.configurationPath = (String) ref.getProperty( _CONF_PATHS );
		this.multiple = "true".equalsIgnoreCase( (String) ref.getProperty( _CONF_MULTIPLES ));
	}
	
	public VendorPluginInfo( String dataRootURI, String hgConfigurationPath, boolean multiple ) {
		this.dataRootURI = dataRootURI;
		this.configurationPath = hgConfigurationPath;
		this.multiple = multiple;
	}
	

	/**
	 * @return the dataRootURI
	 */
	public String getDataRootURI() {
		return dataRootURI;
	}
	/**
	 * @param dataRootURI the dataRootURI to set
	 */
	public void setDataRootURI(String dataRootURI) {
		this.dataRootURI = dataRootURI;
	}
	/**
	 * @return the hgConfigurationPath
	 */
	public String getConfigurationPath() {
		return configurationPath;
	}
	/**
	 * @param hgConfigurationPath the hgConfigurationPath to set
	 */
	public void setConfigurationPath(String configurationPath) {
		this.configurationPath = configurationPath;
	}
	/**
	 * @return the multiple
	 */
	public boolean isMultiple() {
		return multiple;
	}
	/**
	 * @param multiple the multiple to set
	 */
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	

	/**
	 * @return the dmtActionNode
	 */
	public String getDmtActionNode() {
		return dmtActionNode;
	}

	/**
	 * @param dmtActionNode the dmtActionNode to set
	 */
	public void setDmtActionNode(String dmtActionNode) {
		this.dmtActionNode = dmtActionNode;
	}

	/**
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if ( obj == null || !( obj instanceof VendorPluginInfo) )
			return false;
		VendorPluginInfo vpi = (VendorPluginInfo) obj;
		return this.dataRootURI.equals( vpi.dataRootURI ) && this.configurationPath.equals( vpi.configurationPath ) 
				&& this.multiple == vpi.multiple;
	}
	
	public int hashCode() {
		return dataRootURI.hashCode() + configurationPath.hashCode() + new Boolean( multiple ).hashCode();
	}
	
}
