/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
package org.osgi.impl.service.discovery.jcs;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.service.discovery.ServiceEndpointDescription;

/**
 * @author Thomas Kiesslich
 * 
 */
public class JCSServiceEndpointDescription implements
		ServiceEndpointDescription, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5654544975269539970L;

	private Map javaInterfacesAndVersions = null;
	private Map javaInterfacesAndEnpointInterfaces = null;
	private Map properties = null;

	private static final String ARRAYELEMENTSEPERATOR = ",";

	/**
	 * 
	 * @param javaInterfacesAndVersions
	 * @param javaInterfacesAndEndpointInterfaces
	 * @param properties
	 */
	public JCSServiceEndpointDescription(Map interfacesAndVersions,
			Map endpointInterfaces, Map props) {
		if (interfacesAndVersions == null) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must not be null.");
		}
		if (interfacesAndVersions.size() <= 0) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must contain at least one service interface name.");
		}
		javaInterfacesAndVersions = new HashMap(interfacesAndVersions);
		if (endpointInterfaces != null) {
			javaInterfacesAndEnpointInterfaces = new HashMap(endpointInterfaces);
		} else {
			javaInterfacesAndEnpointInterfaces = new HashMap();
		}
		if (props != null) {
			properties = new HashMap(props);
		} else {
			properties = new HashMap();
		}
		addInterfacesAndVersionsToProperties();
	}

	/**
	 * converts the interfaces, versions and protocol specific interfaces into
	 * comma seperated strings and put them into the properties.
	 */
	private void addInterfacesAndVersionsToProperties() {
		// create arrays for java-interface, version and endpoint-interface
		// info. Array indexes correlate.
		int interfaceNmb = javaInterfacesAndVersions.size();
		String[] javaInterfaces = new String[interfaceNmb];
		String[] versions = new String[interfaceNmb];
		String[] endpointInterfaces = javaInterfacesAndEnpointInterfaces.size() > 0 ? new String[interfaceNmb]
				: null;

		Iterator intfIterator = javaInterfacesAndVersions.keySet().iterator();
		for (int i = 0; intfIterator.hasNext(); i++) {
			Object currentInterface = intfIterator.next();
			if (currentInterface instanceof String) {
				javaInterfaces[i] = (String) currentInterface;
				versions[i] = (String) javaInterfacesAndVersions
						.get(javaInterfaces[i]);
				if (endpointInterfaces != null) {
					endpointInterfaces[i] = (String) javaInterfacesAndEnpointInterfaces
							.get(javaInterfaces[i]);
				}
			} else {
				// TODO: throw exception
			}
		}
		// added java-interface information to the properties
		StringBuffer buff = new StringBuffer();
		for (int j = 0; j < javaInterfaces.length; j++) {
			buff.append(javaInterfaces[j]);
			if (j != javaInterfaces.length - 1) {
				buff.append(ARRAYELEMENTSEPERATOR);
			}
		}
		properties.put(Constants.OBJECTCLASS, buff
				.toString());

		// added version to the properties
		buff = new StringBuffer();
		for (int j = 0; j < versions.length; j++) {
			buff.append(versions[j]);
			if (j != versions.length - 1) {
				buff.append(ARRAYELEMENTSEPERATOR);
			}
		}
		properties.put(ServiceEndpointDescription.PROP_KEY_VERSION, buff
				.toString());

		// added protocol specific-interface information to the properties
		if (javaInterfacesAndEnpointInterfaces.size() > 0) {
			buff = new StringBuffer();
			for (int j = 0; j < endpointInterfaces.length; j++) {
				buff.append(endpointInterfaces[j]);
				if (j != endpointInterfaces.length - 1) {
					buff.append(ARRAYELEMENTSEPERATOR);
				}
			}
			properties
					.put(
							ServiceEndpointDescription.PROP_KEY_PROTOCOL_SPECIFIC_INTERFACE_NAME,
							buff.toString());
		}
	}

	public Collection/*<String>*/ getInterfaceNames() {
		return javaInterfacesAndVersions.keySet();
	}

	public URL getLocation() {
		try {
			return new URL((String) properties
					.get(ServiceEndpointDescription.PROP_KEY_SERVICE_LOCATION));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getProperties()
	 */
	public Map getProperties() {
		return properties;
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getProperty(java.lang.String)
	 */
	public Object getProperty(final String key) {
		return properties.get(key);
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getPropertyKeys()
	 */
	public Collection getPropertyKeys() {
		return properties.keySet();
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getProtocolSpecificInterfaceName(java.lang.String)
	 */
	public String getProtocolSpecificInterfaceName(String interfaceName) {
		return (String) javaInterfacesAndEnpointInterfaces.get(interfaceName);
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getVersion(java.lang.String)
	 */
	public String getVersion(String interfaceName) {
		return (String) javaInterfacesAndVersions.get(interfaceName);
	}

}
