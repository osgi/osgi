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

package org.osgi.impl.service.discovery.slp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;

import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;

/**
 * 
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class JSlpSED {

	private String				interfaceName		= null;
	private String				version				= null;
	private String				endpointInterface	= null;
	private Map					properties			= null;

	private SLPServiceEndpointDescription slpServiceDescr = null;
	private static final String	LINE_SEPARATOR		= System
															.getProperty("line.separator");

	public JSlpSED(SLPServiceEndpointDescription descr) {
		slpServiceDescr = descr;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, Object value) {

		if (key.equals(ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME)) {
			interfaceName = (String) value;
		}

		if (key.equals(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME)) {
			endpointInterface = combineValue((String) value);
		}
		if (key.equals(ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION)) {
			version = combineValue((String) value);
		}
		properties.put(key, value);
	}
	
	private String combineValue(String value) {
		return interfaceName + ServicePublication.SEPARATOR + value;
	}

	/**
	 * @return the interfaceName
	 */
	public String getInterfaceName() {
		return interfaceName;
	}

	/**
	 * @param interfaceName the interfaceName to set
	 */
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the endpointInterface
	 */
	public String getEndpointInterface() {
		return endpointInterface;
	}

	/**
	 * @param endpointInterface the endpointInterface to set
	 */
	public void setEndpointInterface(String endpointInterface) {
		this.endpointInterface = endpointInterface;
	}

	/**
	 * @return the properties
	 */
	public Map getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map properties) {
		this.properties = properties;
	}

	/**
	 * adds the endpointinterfaces and versions to the properties map and
	 * creates a service url per interface
	 * 
	 * @throws ServiceLocationException
	 */
	public void addInterfacesAndVersionsToProperties()
			throws ServiceLocationException {
		// Create a service url for each interface and gather also version
		// and
		// endpoint-interface information.

		slpServiceDescr.put(interfaceName, SLPServiceEndpointDescription.createServiceURL(interfaceName, version,
				endpointInterface, properties));
		if (properties == null) {
			properties = new HashMap();
		}
		properties.put(ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME,
				interfaceName);
		if (version != null) {
			properties.put(
					ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION,
					version);
		}
		if (endpointInterface != null) {
			properties.put(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME,
					endpointInterface);
		}
	}

	/**
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object serviceDescription) {
		if (!(serviceDescription instanceof ServiceEndpointDescription)) {
			return false;
		}

		ServiceEndpointDescription descr = (ServiceEndpointDescription) serviceDescription;

		Collection descrInterfaces = descr.getProvidedInterfaces();
		if (descrInterfaces == null) {
			throw new RuntimeException(
					"The service does not contain requiered parameter interfaces. "
							+ descr);
		}

		if (!descrInterfaces.contains(interfaceName)) {
			return false;
		}

		// compare interface names, versions and endpoint interface names
		if ((version != null && (!version.equals(descr
				.getVersion(interfaceName))))
				|| (version == null && descr.getVersion(interfaceName) != null)) {
			return false;
		}

		if ((endpointInterface != null && (!endpointInterface.equals(descr
				.getEndpointInterfaceName(interfaceName))))
				|| (endpointInterface == null && descr
						.getEndpointInterfaceName(interfaceName) != null))
			return false;

		// compare properties field
		return properties.equals(descr.getProperties());
	}

	/**
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = 17;

		result = 37 * result + interfaceName.hashCode();
		if (endpointInterface != null) {
			result = 37 * result + endpointInterface.hashCode();
		}

		if (version != null) {
			result = 37 * result + version.hashCode();
		}

		result = 37 * result + properties.hashCode();
		// TODO implement more

		/*
		 * not significant member variables but rather derived/composite values
		 * 
		 * result = 37 result + serviceURLs.hashCode(); // TODO implement more
		 * // exacting result = 37 result + port;
		 */

		return result;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("interface=").append(interfaceName).append(LINE_SEPARATOR);
		if (version != null) {
			sb.append("version=").append(version).append(LINE_SEPARATOR);
		}
		ServiceURL svcURL = slpServiceDescr.getServiceURL(interfaceName);
		sb.append("serviceURL=")
				.append(svcURL != null ? svcURL.toString() : "").append(
						LINE_SEPARATOR);
		sb.append("properties=" + LINE_SEPARATOR);
		String key;
		Object value;
		for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
			key = (String) i.next();
			value = properties.get(key);
			if (value == null) {
				value = "<null>";
			}

			sb.append(key).append("=").append(value.toString()).append(
					LINE_SEPARATOR);
		}

		return sb.toString();
	}
}
