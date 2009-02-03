/* 
 * Copyright (c) 2008, 2009 Siemens Enterprise Communications GmbH & Co. KG, 
 * Germany. All rights reserved.
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.osgi.framework.Constants;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;

import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.ServiceURL;

/**
 * 
 * SLP encoding:
 * 
 * <code>service:osgi:/my/company/MyService://<protocol://><host><:port><?path></code>
 * 
 * In the <code>path</code> part service properties are listed.
 * 
 * SLP Spec: http://www.ietf.org/rfc/rfc2608.txt
 * 
 * @version $Revision$
 */
public class SLPServiceEndpointDescription implements
		ServiceEndpointDescription {
	// reserved are: `(' / `)' / `,' / `\' / `!' / `<' / `=' / `>' / `~' / CTL
	// TODO: handle CTL
	public static final String	RESERVED_CHARS_IN_ATTR_VALUES			= "(),\\!<>=~;/?:@&=+";

	// RFC2608: Any character except reserved, * / CR / LF / HTAB / `_'.
	public static final String	RESERVED_CHARS_IN_ATTR_KEYS				= RESERVED_CHARS_IN_ATTR_VALUES
																				+ '*'
																				+ 0x0D
																				+ 0x0A
																				+ 0x09
																				+ '_';

	public static final String	ESCAPING_CHARACTER						= "\\";

	private static int			port									= -1;										// TODO

	// private Collection /* <String> */javaInterfaces = new ArrayList(); //
	// mandatory
	// private Collection /* <String> */javaInterfaceAndVersions = new
	// ArrayList(); // optional
	// private Collection /* <String> */javaAndEndpointInterfaces = new
	// ArrayList(); // optional
	// private Map /* <String, Object> */properties = new HashMap(); // optional
	private String				endpointID;

	// Java interfaces and associated ServiceURLs. Each interface has its own
	// ServiceURL.
	private Map				/* <String, ServiceURL> */serviceURLs	= new HashMap();

	private static final String	LINE_SEPARATOR							= System
																				.getProperty("line.separator");

	private final Map			listOfJSLPSEDs							= Collections
																				.synchronizedMap(new HashMap());

	/**
	 * 
	 * @param interfaceNames
	 * @param interfacesAndVersions
	 * @param endPointInterfaces
	 * @param props
	 * @throws ServiceLocationException
	 */
	public SLPServiceEndpointDescription(
			final Collection/* <String */interfaceNames,
			final Collection/* <String> */interfacesAndVersions,
			final Collection/* <String> */endPointInterfaces, final Map props,
			final String endpntID) throws ServiceLocationException {
		// check the java interface map for validity
		if (interfaceNames == null) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must not be null.");
		}
		if (interfaceNames.size() <= 0) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must contain at least one service interface name.");
		}
		
		// separate given interface and version strings and put it in a map 
		Map interfaceAndVersionsMap = new HashMap();		
		if (interfacesAndVersions != null) {
			Iterator versionIterator = interfacesAndVersions.iterator();
			while (versionIterator.hasNext()) {
				String interfaceAndVersion = (String) versionIterator
						.next();
				int separatorIndex = interfaceAndVersion.indexOf(ServicePublication.SEPARATOR);
				// if separator doesn't exist or it's index is invalid (at the very beginning, at the very end)
				if (separatorIndex <= 0 || (separatorIndex+1) == interfaceAndVersion.length()) {
					break;
				}
				String interfaceName = interfaceAndVersion.substring(0, separatorIndex);
				String version = interfaceAndVersion.substring(separatorIndex+1);
				if(interfaceName != null && interfaceName.length() > 0 && version != null && version.length() > 0){
					interfaceAndVersionsMap.put(interfaceName, version);					
				}
			}
		}
		
		// separate given java interface and endpoint interface and put it in a map 
		Map endPointInterfacesMap = new HashMap();		
		if (endPointInterfaces != null) {
			Iterator endpIterator = endPointInterfaces.iterator();
			while (endpIterator.hasNext()) {
				String interfaceAndEndpoint = (String) endpIterator.next();
				int separatorIndex = interfaceAndEndpoint.indexOf(ServicePublication.SEPARATOR);
				// if separator doesn't exist or it's index is invalid (at the very beginning, at the very end)
				if (separatorIndex <= 0 || (separatorIndex+1) == interfaceAndEndpoint.length()) {
					break;
				}
				String interfaceName = interfaceAndEndpoint.substring(0, separatorIndex);
				String endpInterface = interfaceAndEndpoint.substring(separatorIndex+1);
				if(interfaceName != null && interfaceName.length() > 0 && endpInterface != null && endpInterface.length() > 0){
					endPointInterfacesMap.put(interfaceName, endpInterface);					
				}
			}
		}
	
		// create interface-specific SEDs  
		Iterator it = interfaceNames.iterator();
		while (it.hasNext()) {
			String ifName = (String) it.next();
			
			JSlpSED jslpSED = new JSlpSED(this);
			jslpSED.setInterfaceName(ifName);
			jslpSED.setVersion((String)interfaceAndVersionsMap.get(ifName));
			jslpSED.setEndpointInterface((String)endPointInterfacesMap.get(ifName));
			jslpSED.setProperties(props);
			jslpSED.addInterfacesAndVersionsToProperties();//TODO: check
			listOfJSLPSEDs.put(ifName, jslpSED);
		}
		this.endpointID = endpntID;
	}

	/**
	 * 
	 * @param serviceURL
	 */
	public SLPServiceEndpointDescription(final ServiceURL serviceURL) {
		JSlpSED jslpSED = retrieveDataFromServiceURL(serviceURL);
		serviceURLs.put(jslpSED.getInterfaceName(), serviceURL);
		listOfJSLPSEDs.put(jslpSED.getInterfaceName(), jslpSED);
	}

	public Map getProperties() {
		Map result = new HashMap();
		synchronized (listOfJSLPSEDs) {
			Iterator it = listOfJSLPSEDs.values().iterator();
			while (it.hasNext()) {
				result.putAll(((JSlpSED) it.next()).getProperties());
			}
		}
		return result;
	}

	public Object getProperty(final String key) {
		return getProperties().get(key);
	}

	public ServiceURL getServiceURL(String interfaceName) {
		return (ServiceURL) serviceURLs.get(interfaceName);
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("Service:" + LINE_SEPARATOR);
		synchronized (listOfJSLPSEDs) {
			Iterator it = listOfJSLPSEDs.values().iterator();
			while (it.hasNext()) {
				sb.append((JSlpSED) it.next());
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getProvidedInterfaces()
	 */
	public Collection getProvidedInterfaces() {
		List l = new ArrayList();
		synchronized (listOfJSLPSEDs) {
			Iterator it = listOfJSLPSEDs.values().iterator();
			while (it.hasNext()) {
				l.add(((JSlpSED) it.next()).getInterfaceName());
			}
		}
		return l;
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getEndpointInterfaceName(java.lang.String)
	 */
	public String getEndpointInterfaceName(String interfaceName) {
		return ((JSlpSED) listOfJSLPSEDs.get(interfaceName))
				.getEndpointInterface();
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getVersion(java.lang.String)
	 */
	public String getVersion(String interfaceName) {
		return ((JSlpSED) listOfJSLPSEDs.get(interfaceName)).getVersion();
	}

	/**
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolSpecificServiceDescription#getLocation()
	 */
	public URL getLocation() {
		try {
			return new URL(
					(String) getProperty(ServicePublication.PROP_KEY_ENDPOINT_LOCATION));
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @return
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getPropertyKeys()
	 */
	public Collection getPropertyKeys() {
		return getProperties().keySet();
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, Object value) {
		synchronized (listOfJSLPSEDs) {
			Iterator it = listOfJSLPSEDs.values().iterator();
			while (it.hasNext()) {
				((JSlpSED) it.next()).addProperty(key, value);
			}
		}
	}

	/**
	 * 
	 * @param interfaceName
	 * @param version
	 * @param endpointInterface
	 * @param properties
	 * @return
	 * @throws ServiceLocationException
	 */
	public static ServiceURL createServiceURL(String interfaceName,
			String version, String endpointInterface, Map properties)
			throws ServiceLocationException {

		String path = createStringFromProperties(properties);

		// add interface as property to enable LDAP filtering on it
		if (interfaceName != null) {
			path = appendPropertyToURLPath(path, Constants.OBJECTCLASS,
					interfaceName);
		}
		if (version != null)
			path = appendPropertyToURLPath(path,
					ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION,
					version);
		if (endpointInterface != null)
			path = appendPropertyToURLPath(path,
					ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME,
					endpointInterface);

		String protocol = null;
		String host = null;
		String port = null;
		Integer lifeTime = null;

		// init from properties if given
		if (properties != null) {
			protocol = (String) properties.get("protocol");
			host = (String) properties.get("host");
			port = (String) properties.get("port");
			lifeTime = (Integer) properties.get("lifetime");
		}
		if (host == null) {
			String hostname = null;
			try {
				InetAddress addr = InetAddress.getLocalHost();
				// Get hostname
				hostname = addr.getHostName();
				if (hostname == null || hostname.equals("")) {
					// if hostname is NULL or empty string
					// set hostname to ip address
					hostname = addr.getHostAddress();
				}
				host = hostname;
			}
			catch (UnknownHostException e) {
				e.printStackTrace();
				// TODO log and rethrow
			}
		}
		if (port == null) {
			try {
				port = String.valueOf(findFreePort());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		int lifetime = lifeTime != null ? lifeTime.intValue()
				: ServiceURL.LIFETIME_DEFAULT;
		String interf = convertJavaInterface2Path(interfaceName);
		StringBuffer buff = new StringBuffer();
		buff.append("service:osgi");
		buff.append(interf);
		buff.append((protocol != null ? protocol + "://" : "://"));
		buff.append(host);
		buff.append((port != null ? ":" + port : ""));
		buff.append("/");
		buff.append(path);
		return new ServiceURL(buff.toString(), lifetime);
	}

	/**
	 * 
	 * @return an unused port
	 * @throws IOException
	 */
	private static synchronized int findFreePort() throws IOException {
		if (port == -1) {
			ServerSocket server = new ServerSocket(0);
			port = server.getLocalPort();
			server.close();
		}
		return port;
	}

	/**
	 * 
	 * @param serviceURL
	 * @param javaInterfaceAndVersion
	 * @param javaAndEndpointInterfaces
	 * @param properties
	 * @return
	 */
	private JSlpSED retrieveDataFromServiceURL(final ServiceURL serviceURL) {
		Map properties = new HashMap();
		
		// retrieve main interface
		String interfaceName = convertPath2JavaInterface(serviceURL
				.getServiceType().getConcreteTypeName());
		if (interfaceName == null) {
			throw new IllegalArgumentException(
					"Interface information is missing!");
		}
		// Put interface information to properties since base for matching
		properties.put(Constants.OBJECTCLASS, new String[] {interfaceName});//TODO check whether OBJECTCLASSS is the right key
		
		// Retrieve additional properties from SLP ServiceURL itself
		properties.put("slp.serviceURL", serviceURL);
		properties.put("type", serviceURL.getServiceType().toString());
		if (serviceURL.getHost() != null) {
			properties.put("host", serviceURL.getHost());
		}
		if (serviceURL.getPort() != 0) {
			properties.put("port", (new Integer(serviceURL.getPort()))
					.toString());
		}
		if (serviceURL.getProtocol() != null) {
			properties.put("protocol", serviceURL.getProtocol());
		}
		properties.put("lifetime", new Integer(serviceURL.getLifetime()));

		// retrieve properties from ServiceURL attributes
		retrievePropertiesFromPath(serviceURL.getURLPath(), properties);

		JSlpSED jslpSED = new JSlpSED(this);
		jslpSED.setInterfaceName(interfaceName);
		
		// Get version info
		Object versionsValue = properties
				.get(ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION);
		if (versionsValue != null && versionsValue instanceof String) {
			jslpSED.setVersion((String) versionsValue);
		}
		else {
			// TODO version optional -> check whether we shouldn't treat it as error here and anywhere else
			// TODO log error 
			jslpSED.setVersion(org.osgi.framework.Version.emptyVersion.toString());
		}		
		// Overwrite version property with a interface:version value style since base for matching
		properties.put(ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION,
				interfaceName + ServicePublication.SEPARATOR
				+ jslpSED.getVersion());

		// Get endpoint-interface if it exists
		String endpointInterfacesValue = (String) properties
				.get(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME);
		if (endpointInterfacesValue != null) {
			jslpSED.setEndpointInterface(endpointInterfacesValue);
		}
		// Overwrite endpoint-interface property with a interface:endpoint-interface value style since base for matching
		properties.put(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME,
				interfaceName + ServicePublication.SEPARATOR
				+ jslpSED.getEndpointInterface());
		
		jslpSED.setProperties(properties);
		
		return jslpSED;
	}

	/**
	 * 
	 * @param path
	 * @param properties
	 */
	public static void retrievePropertiesFromPath(String path,
			final Map properties) {
		try {
			if (path != null && path.trim() != "") {
				path = path.substring(2); // strip off the "/?" in front of
				// the
				// path

				StringTokenizer st = new StringTokenizer(path, "=,");

				String key;
				String value;

				try {
					while (st.hasMoreTokens()) {
						key = deescapeReservedChars(st.nextToken());

						if (st.hasMoreTokens()) {
							value = st.nextToken();
						}
						else {
							value = "";
						}

						// TODO check whether got list of values --> create
						// array. This should be done befor deescaping chars.

						value = deescapeReservedChars(value);

						properties.put(key, value);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			return;
		}
	}

	/**
	 * 
	 * @param javaInterfaceName
	 * @return
	 */
	public static String convertJavaInterface2Path(
			final String javaInterfaceName) {
		return javaInterfaceName != null ? ":"
				+ javaInterfaceName.replace('.', '/') : "";
	}

	/**
	 * 
	 * @param interfaceNameEncodedAsPath
	 * @return
	 */
	public static String convertPath2JavaInterface(
			final String interfaceNameEncodedAsPath) {
		return interfaceNameEncodedAsPath != null ? interfaceNameEncodedAsPath
				.replace('/', '.') : null;
	}

	/**
	 * 
	 * @param properties
	 * @return
	 */
	private static String createStringFromProperties(final Map properties) {
		StringBuffer sb = new StringBuffer();
		String key = null;
		Object value = null;
		if ((properties != null) && !properties.isEmpty()) {
			for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
				key = (String) i.next();
				value = properties.get(key);
				appendPropertyToURLPath(sb, key, value);
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param path
	 * @param key
	 * @param value
	 */
	private static void appendPropertyToURLPath(StringBuffer path, String key,
			Object value) {
		if (path == null)
			throw new IllegalArgumentException("Given StringBuffer is null.");

		if (path.length() == 0) {
			path.append("?");
		}
		else {
			path.append(",");
		}

		if (value == null) {
			value = "<null>";
		}

		path.append(escapeReservedChars(key,
				SLPServiceEndpointDescription.RESERVED_CHARS_IN_ATTR_KEYS));
		path.append("=");

		if (value instanceof Object[]) {
			Object[] arrayValue = (Object[]) value;
			for (int i = 0; i < arrayValue.length;) {
				path
						.append(escapeReservedChars(
								arrayValue[i].toString(),
								SLPServiceEndpointDescription.RESERVED_CHARS_IN_ATTR_VALUES));
				if (++i != arrayValue.length)
					path.append(",");
			}
		}
		else
			path
					.append(escapeReservedChars(
							value.toString(),
							SLPServiceEndpointDescription.RESERVED_CHARS_IN_ATTR_VALUES));
	}

	/**
	 * 
	 * @param path
	 * @param key
	 * @param value
	 * @return
	 */
	public static String appendPropertyToURLPath(String path, String key,
			Object value) {
		StringBuffer buffer = new StringBuffer(path);
		appendPropertyToURLPath(buffer, key, value);
		return buffer.toString();
	}

	/**
	 * TODO: create test case using reserved chars
	 * 
	 * @param value
	 * @param reservedChars
	 * @return
	 */
	public static String deescapeReservedChars(String value) {
		// tokenize escapedchars as extra chars
		StringTokenizer tokenizer = new StringTokenizer(value,
				SLPServiceEndpointDescription.ESCAPING_CHARACTER);
		StringBuffer buffer = null;
		if (tokenizer.hasMoreTokens()) {
			buffer = new StringBuffer(tokenizer.nextToken());
		}
		else {
			buffer = new StringBuffer();
		}
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.length() >= 2) {
				buffer.append((char) Integer
						.parseInt(token.substring(0, 2), 16));
				buffer.append(token.substring(2));
			}
		}
		return buffer.toString();
	}

	/**
	 * TODO: create test case using reserved chars
	 * 
	 * @param value
	 * @param reservedChars
	 * @return
	 */
	public static String escapeReservedChars(String value, String reservedChars) {
		// tokenize reserved chars as extra chars
		StringTokenizer tokenizer = new StringTokenizer(value, reservedChars,
				true);

		StringBuffer buffer = new StringBuffer();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.length() == 1
					&& reservedChars.indexOf(token.charAt(0)) != -1) {
				buffer.append(ESCAPING_CHARACTER
						+ Integer.toHexString(token.charAt(0)));
			}
			else {
				buffer.append(token);
			}
		}

		return buffer.toString();
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

		// if one has an EndpointID
		if (this.endpointID != null || descr.getEndpointID() != null) {
			if (this.endpointID != null) {
				return this.endpointID.equals(descr.getEndpointID());
			}
			else {
				// we don't have an endpointID but only the other.
				return false;
			}
		}

		Collection descrInterfaces = descr.getProvidedInterfaces();
		if (descrInterfaces == null) {
			throw new RuntimeException(
					"The service does not contain requiered parameter interfaces. "
							+ descr);
		}
		boolean found = false;
		synchronized (listOfJSLPSEDs) {
			Iterator it = listOfJSLPSEDs.values().iterator();
			while (it.hasNext()) {
				JSlpSED sed = (JSlpSED) it.next();
				if (sed.equals(serviceDescription)) {
					found = true;
				}
			}
		}
		return found;
	}

	/**
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		// In case endpointID has been provided by DSW / another Discovery or
		// has been generated
		if (endpointID != null) {
			return endpointID.hashCode();
		}
		else {
			int result = 17;
			synchronized (listOfJSLPSEDs) {
				Iterator it = listOfJSLPSEDs.values().iterator();
				while (it.hasNext()) {
					result = 37 * result + ((JSlpSED) it.next()).hashCode();
				}
			}

			if (endpointID != null)
				result = 37 * result + endpointID.hashCode();

			return result;
		}
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getEndpointID()
	 */
	public String getEndpointID() {
		return endpointID;
	}

	/**
	 * 
	 * @param interfaceName
	 * @param url
	 */
	protected void put(String interfaceName, ServiceURL url) {
		serviceURLs.put(interfaceName, url);
	}

}
