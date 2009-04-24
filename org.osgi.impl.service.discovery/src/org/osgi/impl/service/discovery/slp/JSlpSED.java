/* 
 * Copyright (c) 2008, 2009 Siemens Enterprise Communications GmbH & Co. KG, 
 * Germany. All rights reserved.
 *
 * Siemens Enterprise Communications GmbH & Co. KG is a Trademark Licensee 
 * of Siemens AG.
 *
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Siemens Enterprise Communications 
 * GmbH & Co. KG and its licensors. All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 */
package org.osgi.impl.service.discovery.slp;

import java.util.Collection;

import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;

import ch.ethz.iks.slp.ServiceURL;

/**
 * 
 * 
 * TODO Add Javadoc comment for this type.
 * @author Thomas Kiesslich
 * @version $Revision$
 */
public final class JSlpSED {

	private static final String	LINE_SEPARATOR		= System
															.getProperty("line.separator");

	private String				interfaceName		= null;
	private String				version				= null;
	private String				endpointInterface	= null;
	private ServiceURL			slpServiceURL		= null;

	/**
	 * Adds a property to the existing map.
	 * 
	 * @param key property key
	 * @param value the value of the key
	 */
	public void addProperty(final String key, final Object value) {

		if (key.equals(ServicePublication.SERVICE_INTERFACE_NAME)) {
			interfaceName = (String) value;
		}

		if (key.equals(ServicePublication.ENDPOINT_INTERFACE_NAME)) {
			if (!((String) value).startsWith(interfaceName)) {
				endpointInterface = combineValue((String) value);
			}
		}

		if (key.equals(ServicePublication.SERVICE_INTERFACE_VERSION)) {
			if (!((String) value).startsWith(interfaceName)) {
				version = combineValue((String) value);
			}
		}

		if (key.equals(SLPServiceEndpointDescription.SLP_SERVICEURL)) {
			slpServiceURL = (ServiceURL) value;
		}
		// properties.put(key, value);
	}

	/**
	 * Preceding a value with the interface name and a separator.
	 * 
	 * @param value to append
	 * @return the complete string
	 */
	private String combineValue(final String value) {
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
	public void setInterfaceName(final String interfaceName) {
		this.interfaceName = interfaceName;
	}

	/**
	 * @return the slpServiceURL
	 */
	public ServiceURL getSlpServiceURL() {
		return slpServiceURL;
	}

	/**
	 * @param slpServiceURL the slpServiceURL to set
	 */
	public void setSlpServiceURL(ServiceURL slpServiceURL) {
		this.slpServiceURL = slpServiceURL;
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
	public void setVersion(final String version) {
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
	public void setEndpointInterface(final String endpointInterface) {
		this.endpointInterface = endpointInterface;
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

		// compare interface names
		if (!descrInterfaces.contains(interfaceName)) {
			return false;
		}

		// compare versions 
		if ((version != null && (!version.equals(descr
				.getVersion(interfaceName))))
				|| (version == null && descr.getVersion(interfaceName) != null)) {
			return false;
		}

		//compare endpoint interfaces
		if ((endpointInterface != null && (!endpointInterface.equals(descr
				.getEndpointInterfaceName(interfaceName))))
				|| (endpointInterface == null && descr
						.getEndpointInterfaceName(interfaceName) != null)) {
			return false;
		}

		// compare slpServiceURL
		if ((slpServiceURL != null && (!slpServiceURL.equals((String) descr
				.getProperty(SLPServiceEndpointDescription.SLP_SERVICEURL))))
				|| (slpServiceURL == null && descr
						.getProperty(SLPServiceEndpointDescription.SLP_SERVICEURL) != null)) {
			return false;
		}

		return true;
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
		
		if (slpServiceURL != null) {
			result = 37 * result + slpServiceURL.hashCode();
		}

		return result;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\tinterface=").append(interfaceName).append(LINE_SEPARATOR);
		if (version != null) {
			sb.append("\tversion=").append(version).append(LINE_SEPARATOR);
		}
		if (endpointInterface != null) {
			sb.append("\tendpointInterface=").append(endpointInterface).append(
					LINE_SEPARATOR);
		}
		if (slpServiceURL != null) {
			sb.append("\t")
					.append(SLPServiceEndpointDescription.SLP_SERVICEURL)
					.append("=").append(slpServiceURL).append(LINE_SEPARATOR);
		}
		return sb.toString();
	}
}
