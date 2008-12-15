package org.osgi.impl.service.discovery.slp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
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
public class SLPServiceDescriptionAdapter implements ServiceEndpointDescription {
	public static final String UnknownVersion = "*";

	// reserved are: `(' / `)' / `,' / `\' / `!' / `<' / `=' / `>' / `~' / CTL
	// TODO: handle CTL
	public static final String RESERVED_CHARS_IN_ATTR_VALUES = "(),\\!<>=~";

	// RFC2608: Any character except reserved, * / CR / LF / HTAB / `_'.
	public static final String RESERVED_CHARS_IN_ATTR_KEYS = RESERVED_CHARS_IN_ATTR_VALUES
			+ '*' + 0x0D + 0x0A + 0x09 + '_';

	public static final String ESCAPING_CHARACTER = "\\";

	private Collection /* <String> */javaInterfaces; // mandatory
	private Collection /* <String> */javaInterfaceAndVersions = new ArrayList(); // optional
	private Collection /* <String> */javaAndEndpointInterfaces = new ArrayList(); // optional
	private Map/* <String, Object> */properties = new HashMap(); // optional
	private String endpointID;

	// Java interfaces and associated ServiceURLs. Each interface has its own
	// ServiceURL.
	private Map/* <String, ServiceURL> */serviceURLs;

	private static int port = -1;

	/**
	 * 
	 * @param interfaceNames
	 * @param interfacesAndVersions
	 * @param endPointInterfaces
	 * @param props
	 * @throws ServiceLocationException
	 */
	public SLPServiceDescriptionAdapter(
			final Collection/* <String */interfaceNames,
			final Collection/* <String> */interfacesAndVersions,
			final Collection/* <String> */endPointInterfaces, final Map props,
			final String endpntID) throws ServiceLocationException {
		// check the java interface map for validity
		if (interfacesAndVersions == null) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must not be null.");
		}
		if (interfacesAndVersions.size() <= 0) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must contain at least one service interface name.");
		}

		// create and copy collections and maps
		javaInterfaces = new ArrayList(interfaceNames);
		if (interfacesAndVersions != null) {
			javaInterfaceAndVersions = new ArrayList(interfacesAndVersions);
		}
		if (endPointInterfaces != null) {
			javaAndEndpointInterfaces = new ArrayList(endPointInterfaces);
		}
		if (props != null) {
			properties.putAll(props);
		}

		endpointID = endpntID;
		addInterfacesAndVersionsToProperties();
	}

	/**
	 * 
	 * @param serviceURL
	 */
	public SLPServiceDescriptionAdapter(final ServiceURL serviceURL) {
		javaInterfaceAndVersions = new ArrayList();
		javaAndEndpointInterfaces = new ArrayList();
		javaInterfaces = new ArrayList();
		properties = new HashMap();
		serviceURLs = new HashMap();

		String interfaceName = retrieveDataFromServiceURL(serviceURL,
				javaInterfaces, javaInterfaceAndVersions,
				javaAndEndpointInterfaces, properties);
		serviceURLs.put(interfaceName, serviceURL);
	}

	/**
	 * adds the endpointinterfaces and versions to the properties map and
	 * creates a service url per interface
	 * 
	 * @throws ServiceLocationException
	 */
	private void addInterfacesAndVersionsToProperties()
			throws ServiceLocationException {
		// Create a service url for each interface and gather also version and
		// endpoint-interface information.
		serviceURLs = new HashMap();
		Iterator intfIterator = javaInterfaces.iterator();
		// walk over the provided interfaces
		for (int i = 0; intfIterator.hasNext(); i++) {
			String currentInterface = (String) intfIterator.next();
			String version = null;
			String endpointInterface = null;
			// get the version for that interface
			if (javaInterfaceAndVersions != null) {
				Iterator versionIterator = javaInterfaceAndVersions.iterator();
				while (versionIterator.hasNext()) {
					String interfaceAndVersion = (String) versionIterator
							.next();
					if (interfaceAndVersion.indexOf(currentInterface) >= 0) {
						version = interfaceAndVersion;

					}
				}
			}
			if (javaAndEndpointInterfaces != null) {
				// walk over the endpoint interfaces to find the corresponding
				// endpoint IF
				Iterator endpIterator = javaAndEndpointInterfaces.iterator();
				while (endpIterator.hasNext()) {
					String endpInterface = (String) endpIterator.next();
					if (endpInterface.indexOf(currentInterface) > 0) {
						endpointInterface = endpInterface
								.substring(endpInterface
										.indexOf(ServicePublication.SEPARATOR));
					}
				}
			}
			serviceURLs.put(currentInterface, createServiceURL(
					currentInterface, version, endpointInterface, properties));
		}
		properties.put(ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME,
				javaInterfaces);
		if (javaInterfaceAndVersions != null) {
			properties.put(
					ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION,
					javaInterfaceAndVersions);
		}
		if (javaAndEndpointInterfaces != null) {
			properties.put(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME,
					javaAndEndpointInterfaces);
		}
	}

	public Map getProperties() {
		return properties;
	}

	public Object getProperty(final String key) {
		return properties.get(key);
	}

	public Collection keys() {
		return properties.keySet();
	}

	public ServiceURL getServiceURL(String interfaceName) {
		return (ServiceURL) serviceURLs.get(interfaceName);
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("Service:\n");

		if (javaInterfaceAndVersions != null) {
			Iterator intfIterator = javaInterfaceAndVersions.iterator();
			while (intfIterator.hasNext()) {
				StringTokenizer interfaceName = new StringTokenizer(
						(String) intfIterator.next(),
						ServicePublication.SEPARATOR);
				sb.append("interface=").append(interfaceName.nextToken())
						.append("\n");
				sb.append("version=").append(interfaceName.nextToken()).append(
						"\n");
				ServiceURL svcURL = getServiceURL(interfaceName.toString());
				sb.append("serviceURL=").append(
						svcURL != null ? svcURL.toString() : "").append("\n");
			}
		} else {
			Iterator intfIterator = javaInterfaces.iterator();
			while (intfIterator.hasNext()) {
				String interf = (String) intfIterator.next();
				sb.append("interface=").append(interf).append("\n");
				ServiceURL svcURL = getServiceURL(interf);
				sb.append("serviceURL=").append(
						svcURL != null ? svcURL.toString() : "").append("\n");
			}
		}

		sb.append("properties=\n");
		String key;
		Object value;
		for (Iterator i = properties.keySet().iterator(); i.hasNext();) {
			key = (String) i.next();
			value = properties.get(key);
			if (value == null) {
				value = "<null>";
			}

			sb.append(key).append("=").append(value.toString()).append("\n");
		}

		return sb.toString();
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getProvidedInterfaces()
	 */
	public Collection getProvidedInterfaces() {
		return javaInterfaces;
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getEndpointInterfaceName(java.lang.String)
	 */
	public String getEndpointInterfaceName(String interfaceName) {
		if (javaAndEndpointInterfaces != null && interfaceName != null) {
			Iterator it = javaAndEndpointInterfaces.iterator();
			while (it.hasNext()) {
				String t = (String) it.next();
				if (!t.equals("")) {
					StringTokenizer tokenizer = new StringTokenizer(t,
							ServicePublication.SEPARATOR);
					String token = tokenizer.nextToken();
					if (interfaceName.equals(token)) {
						return tokenizer.nextToken();
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getVersion(java.lang.String)
	 */
	public String getVersion(String interfaceName) {
		if (javaInterfaceAndVersions != null && interfaceName != null) {
			Iterator it = javaInterfaceAndVersions.iterator();
			while (it.hasNext()) {
				StringTokenizer tokenizer = new StringTokenizer((String) it
						.next(), ServicePublication.SEPARATOR);
				if (interfaceName.equals(tokenizer.nextToken())) {
					return tokenizer.nextToken();
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolSpecificServiceDescription#getLocation()
	 */
	public URL getLocation() {
		try {
			return new URL((String) properties
					.get(ServicePublication.PROP_KEY_ENDPOINT_LOCATION));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolSpecificServiceDescription#getPropertyKeys()
	 */
	public Collection getPropertyKeys() {
		return properties.keySet();
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, Object value) {
		if (key.equals(ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME)) {
			if (javaInterfaces == null) {
				javaInterfaces = new ArrayList();
			}
			javaInterfaces.add((String) value);
		}
		if (key.equals(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME)) {
			if (javaAndEndpointInterfaces == null) {
				javaAndEndpointInterfaces = new ArrayList();
			}
			javaAndEndpointInterfaces.add((String) value);
		}
		if (key.equals(ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION)) {
			if (javaInterfaceAndVersions == null) {
				javaInterfaceAndVersions = new ArrayList();
			}
			javaInterfaceAndVersions.add((String) value);
		}
		properties.put(key, value);
	}

	/**
	 * 
	 * @param props
	 */
	public void setProperties(Map props) {
		properties = props;
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
			} catch (UnknownHostException e) {
				e.printStackTrace();
				// TODO log and rethrow
			}
		}
		if (port == null) {
			try {
				port = String.valueOf(findFreePort());
			} catch (IOException e) {
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
	 * @param javaInterfaceAndFilters
	 * @param javaAndEndpointInterfaces
	 * @param properties
	 * @return
	 */
	public static String retrieveDataFromServiceURL(
			final ServiceURL serviceURL,
			final Collection/* <String> */javaInterfaces,
			final Collection/* <String> */javaInterfaceAndFilters,
			final Collection/* <String> */javaAndEndpointInterfaces,
			final Map/* <String, Object> */properties) {
		// retrieve main interface
		String interfaceName = convertPath2JavaInterface(serviceURL
				.getServiceType().getConcreteTypeName());
		if (interfaceName == null) {
			throw new IllegalArgumentException(
					"Interface information is missing!");
		}
		javaInterfaces.add(interfaceName);
		// Retrieve additional properties from SLP ServiceURL itself
		properties.put("slp.serviceURL", serviceURL);
		properties.put("type", serviceURL.getServiceType().toString());
		if (serviceURL.getHost() != null) {
			properties.put("host", serviceURL.getHost());
		}
		if (serviceURL.getPort() != 0) {
			properties.put("port", new Integer(serviceURL.getPort()));
		}
		if (serviceURL.getProtocol() != null) {
			properties.put("protocol", serviceURL.getProtocol());
		}
		properties.put("lifetime", new Integer(serviceURL.getLifetime()));

		// retrieve properties from ServiceURL attributes
		retrievePropertiesFromPath(serviceURL.getURLPath(), properties);

		// Get version info
		String version;
		Object versionsValue = properties
				.get(ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION);
		if (versionsValue instanceof String) {
			version = (String) versionsValue;
		} else {
			// TODO log error
			version = SLPServiceDescriptionAdapter.UnknownVersion;
		}
		javaInterfaceAndFilters.add(version);

		// Put interface and version information to properties since base for
		// matching
		properties.put(Constants.OBJECTCLASS, interfaceName);
		properties.put(ServicePublication.PROP_KEY_SERVICE_INTERFACE_VERSION,
				version);

		// Get endpoint-interface if it exists
		Object endpointInterfacesValue = properties
				.get(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME);
		if (endpointInterfacesValue instanceof String) {
			javaAndEndpointInterfaces.add(interfaceName
					+ ServicePublication.SEPARATOR + endpointInterfacesValue);
		}

		// return main interface name
		return interfaceName;
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
						} else {
							value = "";
						}

						// TODO check whether got list of values --> create
						// array. This should be done befor deescaping chars.

						value = deescapeReservedChars(value);

						properties.put(key, value);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
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
		} else {
			path.append(",");
		}

		if (value == null) {
			value = "<null>";
		}

		path.append(escapeReservedChars(key,
				SLPServiceDescriptionAdapter.RESERVED_CHARS_IN_ATTR_KEYS));
		path.append("=");

		if (value instanceof Object[]) {
			Object[] arrayValue = (Object[]) value;
			for (int i = 0; i < arrayValue.length;) {
				path
						.append(escapeReservedChars(
								arrayValue[i].toString(),
								SLPServiceDescriptionAdapter.RESERVED_CHARS_IN_ATTR_VALUES));
				if (++i != arrayValue.length)
					path.append(",");
			}
		} else
			path
					.append(escapeReservedChars(
							value.toString(),
							SLPServiceDescriptionAdapter.RESERVED_CHARS_IN_ATTR_VALUES));
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
				SLPServiceDescriptionAdapter.ESCAPING_CHARACTER);
		StringBuffer buffer = null;
		if (tokenizer.hasMoreTokens()) {
			buffer = new StringBuffer(tokenizer.nextToken());
		} else {
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
			} else {
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
		List sdJavaInterfaceAndVersions = new ArrayList();
		List sdJavaAndEndpointInterfaces = new ArrayList();
		Collection interfaces = descr.getProvidedInterfaces();
		if (interfaces == null) {
			throw new RuntimeException(
					"The service does not contain requiered parameter interfaces. "
							+ descr);
		}
		Iterator interfacesIterator = interfaces.iterator();
		while (interfacesIterator.hasNext()) {
			String interfaceName = (String) interfacesIterator.next();
			sdJavaInterfaceAndVersions.add(interfaceName
					+ ServicePublication.SEPARATOR
					+ descr.getVersion(interfaceName));
			if (descr.getEndpointInterfaceName(interfaceName) != null) {
				sdJavaAndEndpointInterfaces.add(interfaceName
						+ ServicePublication.SEPARATOR
						+ descr.getEndpointInterfaceName(interfaceName));
			}
		}
		// interface and versions field
		if (!((javaInterfaceAndVersions == sdJavaInterfaceAndVersions) || (javaInterfaceAndVersions != null && javaInterfaceAndVersions
				.equals(sdJavaInterfaceAndVersions)))) {
			return false;
		}
		// interface and endpoints field
		if (!((javaAndEndpointInterfaces == sdJavaAndEndpointInterfaces) || (javaAndEndpointInterfaces != null && javaAndEndpointInterfaces
				.equals(sdJavaAndEndpointInterfaces)))) {
			return false;
		}
		// properties field
		if (properties != descr.getProperties()) {
			if (properties != null && descr.getProperties() != null) {
				if (properties.isEmpty() && !descr.getProperties().isEmpty()) {
					return false;
				}
				Iterator it = properties.keySet().iterator();
				while (it.hasNext()) {
					String nextKey = (String) it.next();
					if (descr.getProperty(nextKey) != null) {
						if (properties.get(nextKey).equals(
								descr.getProperty(nextKey))) {

						} else {
							return false;
						}
					} else {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * TODO implement hashCode() appropriatly
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = 17;
		Iterator it = javaInterfaces.iterator();
		while (it.hasNext()) {
			result = 37 * result + ((String) it.next()).hashCode();
		}
		it = javaAndEndpointInterfaces.iterator();
		while (it.hasNext()) {
			result = 37 * result + ((String) it.next()).hashCode();
		}
		it = javaInterfaceAndVersions.iterator();
		while (it.hasNext()) {
			result = 37 * result + ((String) it.next()).hashCode();
		}
		result = 37 * result + endpointID.hashCode();
		result = 37 * result + serviceURLs.hashCode(); // TODO implement more
														// exacting
		result = 37 * result + properties.hashCode(); // TODO implement more
														// exacting
		result = 37 * result + port;
		return result;
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getEndpointID()
	 */
	public String getEndpointID() {
		return endpointID;
	}
}
