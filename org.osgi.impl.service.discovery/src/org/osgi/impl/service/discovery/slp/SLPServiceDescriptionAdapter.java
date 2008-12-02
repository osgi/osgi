package org.osgi.impl.service.discovery.slp;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.osgi.framework.Constants;
import org.osgi.service.discovery.ServiceEndpointDescription;

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

	private Map/* <String, String> */javaInterfaceAndVersions;
	private Map/* <String, String> */javaAndEndpointInterfaces;
	private Map/* <String, Object> */properties;

	// Java interfaces and associated ServiceURLs. Each interface has its own
	// ServiceURL.
	private Map/* <String, ServiceURL> */serviceURLs;

	private static final String ARRAYELEMENTSEPERATOR = ",";

	public SLPServiceDescriptionAdapter(final Map interfacesAndVersions,
			final Map endPointInterfaces, final Map props)
			throws ServiceLocationException {
		// check the java interface map for validity
		if (interfacesAndVersions == null) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must not be null.");
		}
		if (interfacesAndVersions.size() <= 0) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must contain at least one service interface name.");
		}

		// create and copy maps
		javaInterfaceAndVersions = new HashMap(interfacesAndVersions);
		javaAndEndpointInterfaces = new HashMap();
		if (endPointInterfaces != null) {
			javaAndEndpointInterfaces.putAll(endPointInterfaces);
		}
		properties = new HashMap();
		if (props != null) {
			properties.putAll(props);
		}

		addInterfacesAndVersionsToProperties();
	}

	public SLPServiceDescriptionAdapter(final ServiceURL serviceURL) {
		javaInterfaceAndVersions = new HashMap();
		javaAndEndpointInterfaces = new HashMap();
		properties = new HashMap();
		serviceURLs = new HashMap();

		String interfaceName = retrieveDataFromServiceURL(serviceURL,
				javaInterfaceAndVersions, javaAndEndpointInterfaces, properties);
		serviceURLs.put(interfaceName, serviceURL);
	}

	private void addInterfacesAndVersionsToProperties()
			throws ServiceLocationException {
		// create arrays for java-interface, version and endpoint-interface
		// info. Array indexes correlate.
		int interfaceNmb = javaInterfaceAndVersions.size();
		String[] javaInterfaces = new String[interfaceNmb];
		String[] versions = new String[interfaceNmb];
		String[] endpointInterfaces = javaAndEndpointInterfaces.size() > 0 ? new String[interfaceNmb]
				: null;

		// Create a service url for each interface and gather also version and
		// endpoint-interface information.
		serviceURLs = new HashMap();
		Iterator intfIterator = javaInterfaceAndVersions.keySet().iterator();
		for (int i = 0; intfIterator.hasNext(); i++) {
			Object currentInterface = intfIterator.next();
			if (currentInterface instanceof String) {
				javaInterfaces[i] = (String) currentInterface;
				versions[i] = (String) javaInterfaceAndVersions
						.get(javaInterfaces[i]);
				if (endpointInterfaces != null) {
					endpointInterfaces[i] = (String) javaAndEndpointInterfaces
							.get(javaInterfaces[i]);
				}
				serviceURLs.put(javaInterfaces[i], createServiceURL(
						javaInterfaces[i], versions[i],
						endpointInterfaces != null ? endpointInterfaces[i]
								: null, properties));
			} else {
				// TODO: throw exception
			}
		}
		StringBuffer buff = new StringBuffer();
		for (int j = 0; j < javaInterfaces.length; j++) {
			buff.append(javaInterfaces[j]);
			if (j != javaInterfaces.length - 1) {
				buff.append(ARRAYELEMENTSEPERATOR);
			}
		}
		// added version and endpoint-interface information to the properties
		properties.put(Constants.OBJECTCLASS, buff.toString());
		buff = new StringBuffer();
		for (int j = 0; j < versions.length; j++) {
			buff.append(versions[j]);
			if (j != versions.length - 1) {
				buff.append(ARRAYELEMENTSEPERATOR);
			}
		}
		properties.put(ServiceEndpointDescription.PROP_KEY_VERSION, buff
				.toString());
		if (javaAndEndpointInterfaces.size() > 0) {
			buff = new StringBuffer();
			for (int j = 0; j < endpointInterfaces.length; j++) {
				buff.append(endpointInterfaces[j]);
				if (j != endpointInterfaces.length - 1) {
					buff.append(ARRAYELEMENTSEPERATOR);
				}
			}
			properties
					.put(
							ServiceEndpointDescription.PROP_KEY_ENDPOINT_INTERFACE_NAME,
							buff.toString());
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

	public String toString() {
		StringBuffer sb = new StringBuffer("Service:\n");

		Iterator intfIterator = this.javaInterfaceAndVersions.keySet()
				.iterator();
		while (intfIterator.hasNext()) {
			String interfaceName = (String) intfIterator.next();
			sb.append("interface=").append(interfaceName).append("\n");
			sb.append("version=").append(
					javaInterfaceAndVersions.get(interfaceName)).append("\n");
			ServiceURL svcURL = getServiceURL(interfaceName);
			sb.append("serviceURL=").append(
					svcURL != null ? svcURL.toString() : "").append("\n");
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
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getInterfaceNames()
	 */
	public Collection getProvidedInterfaces() {
		return javaInterfaceAndVersions.keySet();
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getProtocolSpecificInterfaceName(java.lang.String)
	 */
	public String getEndpointInterfaceName(String interfaceName) {
		return (String) javaAndEndpointInterfaces.get(interfaceName);
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getVersion(java.lang.String)
	 */
	public String getVersion(String interfaceName) {
		return (String) javaInterfaceAndVersions.get(interfaceName);
	}

	/**
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolSpecificServiceDescription#getLocation()
	 */
	public URL getLocation() {
		try {
			return new URL((String) properties
					.get(ServiceEndpointDescription.PROP_KEY_LOCATION));
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
					ServiceEndpointDescription.PROP_KEY_VERSION, version);
		if (endpointInterface != null)
			path = appendPropertyToURLPath(
					path,
					ServiceEndpointDescription.PROP_KEY_ENDPOINT_INTERFACE_NAME,
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
	 * @param serviceURL
	 * @param javaInterfaceAndFilters
	 * @param javaAndEndpointInterfaces
	 * @param properties
	 * @return
	 */
	public static String retrieveDataFromServiceURL(
			final ServiceURL serviceURL,
			final Map/* <String, String> */javaInterfaceAndFilters,
			final Map/* <String, String> */javaAndEndpointInterfaces,
			final Map/* <String, Object> */properties) {
		// retrieve main interface
		String interfaceName = convertPath2JavaInterface(serviceURL
				.getServiceType().getConcreteTypeName());
		if (interfaceName == null) {
			throw new IllegalArgumentException(
					"Interface information is missing!");
		}

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
				.get(ServiceEndpointDescription.PROP_KEY_VERSION);
		if (versionsValue instanceof String) {
			version = (String) versionsValue;
		} else {
			// TODO log error
			version = SLPServiceDescriptionAdapter.UnknownVersion;
		}
		javaInterfaceAndFilters.put(interfaceName, version);

		// Put interface and version information to properties since base for
		// matching
		properties.put(Constants.OBJECTCLASS, new String[] { interfaceName });
		properties.put(ServiceEndpointDescription.PROP_KEY_VERSION,
				new String[] { version });

		// Get endpoint-interface if it exists
		Object endpointInterfacesValue = properties
				.get(ServiceEndpointDescription.PROP_KEY_ENDPOINT_INTERFACE_NAME);
		if (endpointInterfacesValue instanceof String) {
			javaAndEndpointInterfaces.put(interfaceName,
					endpointInterfacesValue);
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
		Map sdJavaInterfaceAndVersions = new HashMap();
		Map sdJavaAndEndpointInterfaces = new HashMap();
		Iterator interfaces = descr.getProvidedInterfaces().iterator();
		while (interfaces.hasNext()) {
			String interfaceName = (String) interfaces.next();
			sdJavaInterfaceAndVersions.put(interfaceName, descr
					.getVersion(interfaceName));
			if (descr.getEndpointInterfaceName(interfaceName) != null) {
				sdJavaAndEndpointInterfaces.put(interfaceName, descr
						.getEndpointInterfaceName(interfaceName));
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
		return super.hashCode();
	}
}
