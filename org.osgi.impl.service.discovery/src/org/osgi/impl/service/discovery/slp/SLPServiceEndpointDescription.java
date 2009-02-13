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
import java.util.Random;
import java.util.StringTokenizer;

import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;
import org.osgi.service.log.LogService;

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
	// RFC2608: reserved are: `(' / `)' / `,' / `\' / `!' / `<' / `=' / `>' /
	// `~' / CTL
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

	private static final String	LINE_SEPARATOR							= System
																				.getProperty("line.separator");

	private static final String	STRING_LIFETIME							= "lifetime";

	private static final String	STRING_PORT								= "port";

	private static final String	STRING_HOST								= "host";

	private static final String	STRING_PROTOCOL							= "protocol";

	public static final String	SLP_SERVICEURL							= "slp.servieURL";

	private static final String	STRING_TYPE								= "type";

	private static final String	STRING_SERVICE_OSGI						= "service:osgi";

	private static int			port									= -1;										// TODO

	private String				endpointID								= null;

	// Java interfaces and associated ServiceURLs. Each interface has its own
	// ServiceURL.
	private Map				/* <String, ServiceURL> */serviceURLs	= new HashMap();

	private final Map			listOfJSLPSEDs							= Collections
																				.synchronizedMap(new HashMap());

	private Map					properties								= new HashMap();

	/**
	 * 
	 * @param interfaceNames
	 * @param interfacesAndVersions
	 * @param endPointInterfaces
	 * @param props
	 * @throws ServiceLocationException
	 */
	public SLPServiceEndpointDescription(
			final Collection/* <String> */interfaceNames,
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
				String interfaceAndVersion = (String) versionIterator.next();
				int separatorIndex = interfaceAndVersion
						.indexOf(ServicePublication.SEPARATOR);
				// if separator doesn't exist or it's index is invalid (at the
				// very beginning, at the very end)
				if (separatorIndex <= 0
						|| (separatorIndex + 1) == interfaceAndVersion.length()) {
					break;
				}
				String interfaceName = interfaceAndVersion.substring(0,
						separatorIndex);
				String version = interfaceAndVersion
						.substring(separatorIndex + 1);
				if (interfaceName != null && interfaceName.length() > 0
						&& version != null && version.length() > 0) {
					interfaceAndVersionsMap.put(interfaceName, version);
				}
			}
		}

		// separate given java interface and endpoint interface and put it in a
		// map
		Map endPointInterfacesMap = new HashMap();
		if (endPointInterfaces != null) {
			Iterator endpIterator = endPointInterfaces.iterator();
			while (endpIterator.hasNext()) {
				String interfaceAndEndpoint = (String) endpIterator.next();
				int separatorIndex = interfaceAndEndpoint
						.indexOf(ServicePublication.SEPARATOR);
				// if separator doesn't exist or it's index is invalid (at the
				// very beginning, at the very end)
				if (separatorIndex <= 0
						|| (separatorIndex + 1) == interfaceAndEndpoint
								.length()) {
					break;
				}
				String interfaceName = interfaceAndEndpoint.substring(0,
						separatorIndex);
				String endpInterface = interfaceAndEndpoint
						.substring(separatorIndex + 1);
				if (interfaceName != null && interfaceName.length() > 0
						&& endpInterface != null && endpInterface.length() > 0) {
					endPointInterfacesMap.put(interfaceName, endpInterface);
				}
			}
		}

		// create interface-specific SEDs
		Iterator it = interfaceNames.iterator();
		while (it.hasNext()) {
			String ifName = (String) it.next();

			JSlpSED jslpSED = new JSlpSED();
			jslpSED.setInterfaceName(ifName);
			jslpSED.setVersion((String) interfaceAndVersionsMap.get(ifName));
			jslpSED.setEndpointInterface((String) endPointInterfacesMap
					.get(ifName));
			listOfJSLPSEDs.put(ifName, jslpSED);
		}
		if (endpntID != null) {
			this.endpointID = endpntID;
		}
		else {
			// TODO use java.util.UUID
			this.endpointID = getUUID();
			System.out.println(endpointID);
		}

		if (props != null) {
			this.properties = new HashMap(props);
		}
		addInterfacesAndVersionsToProperties(interfaceNames,
				interfacesAndVersions, endPointInterfaces, endpointID);
	}

	/**
	 * adds the endpoint interfaces and versions to the properties map
	 * 
	 * @throws ServiceLocationException
	 */
	private void addInterfacesAndVersionsToProperties(
			Collection interfaceNames, Collection versions,
			Collection endPointInterfaces, String endpntID)
			throws ServiceLocationException {
		// Create a service url for each interface and gather also version
		// and
		// endpoint-interface information.
		synchronized (listOfJSLPSEDs) {
			Iterator it = listOfJSLPSEDs.values().iterator();
			while (it.hasNext()) {
				JSlpSED slpSED = (JSlpSED) it.next();
				serviceURLs.put(slpSED.getInterfaceName(),
						SLPServiceEndpointDescription.createServiceURL(slpSED
								.getInterfaceName(), slpSED.getVersion(),
								slpSED.getEndpointInterface(), properties));

			}
		}

		if (properties == null) {
			properties = new HashMap();
		}
		properties.put(ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME,
				interfaceNames);
		if (versions != null) {
			properties.put(
					ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION,
					versions);
		}
		if (endPointInterfaces != null) {
			properties.put(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME,
					endPointInterfaces);
		}

		if (endpntID != null) {
			properties.put(ServicePublication.PROP_KEY_ENDPOINT_ID, endpntID);
		}
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
		return new HashMap(properties);
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
		if (endpointID != null) {
			sb.append("EndpointID = ");
			sb.append(endpointID);
			sb.append(LINE_SEPARATOR);
		}
		synchronized (listOfJSLPSEDs) {
			Iterator it = listOfJSLPSEDs.values().iterator();
			int i = 1;
			while (it.hasNext()) {
				sb.append("Interface ");
				sb.append(i);
				sb.append(LINE_SEPARATOR);
				sb.append((JSlpSED) it.next());
				i++;
			}
		}
		String key;
		Object value;
		Iterator it = properties.keySet().iterator();
		if (it.hasNext()) {
			sb.append("properties=" + LINE_SEPARATOR);
		}
		while (it.hasNext()) {
			key = (String) it.next();
			value = properties.get(key);
			if (value == null) {
				value = "<null>";
			}

			sb.append("\t");
			sb.append(key);
			sb.append("=");
			sb.append(value.toString());
			sb.append(LINE_SEPARATOR);
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
		JSlpSED jSED = ((JSlpSED) listOfJSLPSEDs.get(interfaceName));
		if (jSED != null) {
			return jSED.getEndpointInterface();
		}
		else {
			return null;
		}
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getVersion(java.lang.String)
	 */
	public String getVersion(String interfaceName) {
		JSlpSED jSED = ((JSlpSED) listOfJSLPSEDs.get(interfaceName));
		if (jSED != null) {
			return jSED.getVersion();
		}
		else {
			return null;
		}
	}

	/**
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolSpecificServiceDescription#getLocation()
	 */
	public URL getLocation() {
		Object urlObject = getProperty(ServicePublication.PROP_KEY_ENDPOINT_LOCATION);
		if (urlObject instanceof URL) {
			return (URL) urlObject;
		}
		else
			if (urlObject instanceof String) {
				try {
					return new URL((String) urlObject);
				}
				catch (MalformedURLException e) {
					throw new RuntimeException(e.getMessage());
				}
			}
			else {
				throw new RuntimeException(
						"Service location property is not of expected type URL or String. Property = "
								+ urlObject.toString());
			}
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
		if (key.equals(ServicePublication.PROP_KEY_ENDPOINT_ID)) {
			endpointID = (String) value;
		}
		properties.put(key, value);
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
			path = appendPropertyToURLPath(path,
					ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME,
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
			protocol = (String) properties.get(STRING_PROTOCOL);
			host = (String) properties.get(STRING_HOST);
			port = (String) properties.get(STRING_PORT);
			lifeTime = (Integer) properties.get(STRING_LIFETIME);
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
				SLPHandlerImpl
						.log(
								LogService.LOG_ERROR,
								"Exception occured while trying to get the name of local host.",
								e);
				// TODO log and rethrow
			}
		}
		if (port == null) {
			int freePort = findFreePort();
			if (freePort > 0) {
				port = String.valueOf(freePort);
			}
		}
		int lifetime = lifeTime != null ? lifeTime.intValue()
				: ServiceURL.LIFETIME_DEFAULT;
		String interf = convertJavaInterface2Path(interfaceName);
		StringBuffer buff = new StringBuffer();
		buff.append(STRING_SERVICE_OSGI);
		buff.append(interf);
//		if (properties == null || properties.get(ServicePublication.PROP_KEY_ENDPOINT_LOCATION) == null) {
			buff.append((protocol != null ? protocol + "://" : "://"));
			buff.append(host);
			buff.append((port != null ? ":" + port : ""));
//		}
//		else {
//			buff.append(".");
//			buff.append(properties
//					.get(ServicePublication.PROP_KEY_ENDPOINT_LOCATION));
//		}
		buff.append("/");
		buff.append(path);
		return new ServiceURL(buff.toString(), lifetime);
	}

	/**
	 * TODO: move to utils
	 * 
	 * @return an unused port
	 */
	private static synchronized int findFreePort() {
		// TODO check usage of static var port here
		if (port == -1) {
			ServerSocket server;
			try {
				server = new ServerSocket(0);
				port = server.getLocalPort();
				server.close();
			}
			catch (IOException e) {
				// getting free port failed. Return current port value, which is
				// -1;
			}
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
		// retrieve main interface
		String interfaceName = convertPath2JavaInterface(serviceURL
				.getServiceType().getConcreteTypeName());
		SLPHandlerImpl.log(LogService.LOG_DEBUG, "retrieve data from "
				+ serviceURL + " for interface " + interfaceName);
		if (interfaceName == null) {
			throw new IllegalArgumentException(
					"Interface information is missing!");
		}
		// Retrieve additional properties from SLP ServiceURL itself
		Map props = new HashMap();
		props.put(STRING_TYPE, serviceURL.getServiceType().toString());
		if (serviceURL.getHost() != null) {
			props.put(STRING_HOST, serviceURL.getHost());
		}
		if (serviceURL.getPort() != 0) {
			props.put(STRING_PORT, (new Integer(serviceURL.getPort()))
					.toString());
		}
		if (serviceURL.getProtocol() != null) {
			props.put(STRING_PROTOCOL, serviceURL.getProtocol());
		}
		props.put(STRING_LIFETIME, new Integer(serviceURL.getLifetime()));

		// retrieve properties from ServiceURL attributes
		retrievePropertiesFromPath(serviceURL.getURLPath(), props);

		endpointID = (String) props
				.get(ServicePublication.PROP_KEY_ENDPOINT_ID);
		if (endpointID == null) {
			endpointID = getUUID();
		}

		JSlpSED jslpSED = new JSlpSED();
		jslpSED.setInterfaceName(interfaceName);

		if (serviceURL != null) {
			jslpSED.setSlpServiceURL(serviceURL);
		}

		// Get version info
		String version = null;
		String versionsValue = (String) props
				.get(ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION);
		if (versionsValue != null) {
			version = versionsValue;
		}
		else {
			// TODO log error
			version = org.osgi.framework.Version.emptyVersion.toString();
		}
		jslpSED.setVersion(version);

		// Put interface and version information to properties since base for
		// matching
		// check first if we have to add interfaceNames to the existing list of
		// interfaces in case of multiple interfaces
		Collection interfaceNames = (Collection) properties
				.get(ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME);
		if (interfaceNames == null) {
			interfaceNames = new ArrayList();
		}
		interfaceNames.add(interfaceName);
		props.put(ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME,
				interfaceNames);

		// set version if exists
		Collection versions = (Collection) properties
				.get(ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION);
		if (versions == null) {
			versions = new ArrayList();
		}
		versions.add(interfaceName + ServicePublication.SEPARATOR + version);
		props.put(ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION,
				versions);

		// Set endpoint-interface if it exists
		Collection endpointInterfacesValues = (Collection) properties
				.get(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME);
		if (endpointInterfacesValues == null) {
			endpointInterfacesValues = new ArrayList();
		}
		String endpointInterfacesValue = (String) props
				.get(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME);
		if (endpointInterfacesValue != null) {
			jslpSED.setEndpointInterface(endpointInterfacesValue);
			endpointInterfacesValues.add(endpointInterfacesValue);
			props.put(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME,
					endpointInterfacesValues);
		}

		properties = props;

		return jslpSED;
	}

	/**
	 * @return
	 */
	private String getUUID() {
		return String.valueOf((new Random()).nextInt());
	}

	/**
	 * 
	 * @param path
	 * @param properties
	 */
	public static void retrievePropertiesFromPath(String path,
			final Map properties) {
		if (path != null && path.trim() != "") {
			// strip off the "/?" in front of the path
			path = path.substring(2);

			StringTokenizer st = new StringTokenizer(path, "=,");

			String key;
			String value;

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

		if (!filterSLPSpecificProperties(properties).equals(
				filterSLPSpecificProperties(descr.getProperties()))) {
			return false;
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

	private Map filterSLPSpecificProperties(Map in) {
		Map out = new HashMap(in);
		out.remove(SLP_SERVICEURL);
		out.remove(STRING_LIFETIME);
		out.remove(STRING_PORT);
		out.remove(STRING_PROTOCOL);
		out.remove(STRING_TYPE);
		out.remove(STRING_HOST);
		return out;
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

			if (endpointID != null) {
				result = 37 * result + endpointID.hashCode();
			}

			if (properties != null) {
				result = 37 * result + properties.hashCode();
			}

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

	/**
	 * This method merges to SEDs together if the both have the same endpointID.
	 * 
	 * @param descr1 base object
	 * @param descr2 object to merge
	 * @return a new SED that combines descr1 and descr2; null if a
	 *         ServiceLocationException has been occured
	 */
	public static SLPServiceEndpointDescription mergeServiceEndpointDescriptions(
			ServiceEndpointDescription descr1, ServiceEndpointDescription descr2) {
		SLPHandlerImpl.log(LogService.LOG_DEBUG, "going to merge this stuff: "
				+ descr1 + " and " + descr2);
		Collection versions = new ArrayList();
		Collection interfaces = new ArrayList();
		Collection endpointInterfaces = new ArrayList();
		Map properties = new HashMap();
		// merge interfaces of both SEDs
		if (!interfaces.containsAll(descr1.getProvidedInterfaces())) {
			interfaces.addAll(descr1.getProvidedInterfaces());
		}
		if (!interfaces.containsAll(descr2.getProvidedInterfaces())) {
			interfaces.addAll(descr2.getProvidedInterfaces());
		}

		// merge versions of both SEDs
		Iterator it = interfaces.iterator();
		while (it.hasNext()) {
			String interfaceName = (String) it.next();
			if ((descr1.getVersion(interfaceName) != null)
					&& (!versions.contains(interfaceName
							+ ServicePublication.SEPARATOR
							+ descr1.getVersion(interfaceName)))) {
				versions.add(interfaceName + ServicePublication.SEPARATOR
						+ descr1.getVersion(interfaceName));
			}
			if ((descr2.getVersion(interfaceName) != null)
					&& (!versions.contains(interfaceName
							+ ServicePublication.SEPARATOR
							+ descr2.getVersion(interfaceName)))) {
				versions.add(interfaceName + ServicePublication.SEPARATOR
						+ descr2.getVersion(interfaceName));
			}
		}
		// merge endpointInterfaces of both SEDs
		it = interfaces.iterator();
		while (it.hasNext()) {
			String interfaceName = (String) it.next();
			if ((descr1.getEndpointInterfaceName(interfaceName) != null)
					&& (!endpointInterfaces.contains(interfaceName
							+ ServicePublication.SEPARATOR
							+ descr1.getEndpointInterfaceName(interfaceName)))) {
				endpointInterfaces.add(interfaceName
						+ ServicePublication.SEPARATOR
						+ descr1.getEndpointInterfaceName(interfaceName));
			}
			if ((descr2.getEndpointInterfaceName(interfaceName) != null)
					&& (!endpointInterfaces.contains(interfaceName
							+ ServicePublication.SEPARATOR
							+ descr2.getEndpointInterfaceName(interfaceName)))) {
				endpointInterfaces.add(interfaceName
						+ ServicePublication.SEPARATOR
						+ descr2.getEndpointInterfaceName(interfaceName));
			}
		}

		// merge properties
		properties.putAll(descr1.getProperties());
		properties.putAll(descr2.getProperties());

		try {
			SLPServiceEndpointDescription sed = new SLPServiceEndpointDescription(
					interfaces, versions, endpointInterfaces, properties,
					descr1.getEndpointID());
			return sed;
		}
		catch (ServiceLocationException e) {
			SLPHandlerImpl.log(LogService.LOG_ERROR,
					"Exception occured during SED creation ", e);
		}
		return null;
	}
}
