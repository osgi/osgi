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

	private Map/* <String, String> */javaInterfaceAndFilters;
	private Map/* <String, String> */javaAndEndpointInterfaces;
	private Map/* <String, Object> */properties;

	// Java interfaces and associated ServiceURLs. Each interface has its own
	// ServiceURL.
	private Map/* <String, ServiceURL> */serviceURLs;

	public SLPServiceDescriptionAdapter(final Map javaInterfaceAndFilters,
			final Map javaAndEndpointInterfaces, final Map properties)
			throws ServiceLocationException {
		// check the java interface map for validity
		if (javaInterfaceAndFilters == null) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must not be null.");
		}
		if (javaInterfaceAndFilters.size() <= 0) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must contain at least one service interface name.");
		}

		// create and copy maps
		this.javaInterfaceAndFilters = new HashMap(javaInterfaceAndFilters);
		this.javaAndEndpointInterfaces = new HashMap();
		if (javaAndEndpointInterfaces != null)
			this.javaAndEndpointInterfaces.putAll(javaAndEndpointInterfaces);
		this.properties = new HashMap();
		if (properties != null)
			this.properties.putAll(properties);

		addInterfacesAndVersionsToProperties();
	}

	public SLPServiceDescriptionAdapter(final ServiceURL serviceURL) {
		this.javaInterfaceAndFilters = new HashMap();
		this.javaAndEndpointInterfaces = new HashMap();
		this.properties = new HashMap();
		this.serviceURLs = new HashMap();

		String interfaceName = retrieveDataFromServiceURL(serviceURL,
				javaInterfaceAndFilters, javaAndEndpointInterfaces, properties);
		this.serviceURLs.put(interfaceName, serviceURL);
	}

	private void addInterfacesAndVersionsToProperties()
			throws ServiceLocationException {
		// create arrays for java-interface, version and endpoint-interface
		// info. Array indexes correlate.
		int interfaceNmb = this.javaInterfaceAndFilters.size();
		String[] javaInterfaces = new String[interfaceNmb];
		String[] versions = new String[interfaceNmb];
		String[] endpointInterfaces = this.javaAndEndpointInterfaces.size() > 0 ? new String[interfaceNmb]
				: null;

		// Create a service url for each interface and gather also version and
		// endpoint-interface information.
		this.serviceURLs = new HashMap();
		Iterator intfIterator = this.javaInterfaceAndFilters.keySet()
				.iterator();
		for (int i = 0; intfIterator.hasNext(); i++) {
			Object currentInterface = intfIterator.next();
			if (currentInterface instanceof String) {
				javaInterfaces[i] = (String) currentInterface;
				versions[i] = (String) this.javaInterfaceAndFilters
						.get(javaInterfaces[i]);
				if (endpointInterfaces != null) {
					endpointInterfaces[i] = (String) this.javaAndEndpointInterfaces
							.get(javaInterfaces[i]);
				}
				this.serviceURLs.put(javaInterfaces[i], createServiceURL(
						javaInterfaces[i], versions[i],
						endpointInterfaces != null ? endpointInterfaces[i]
								: null, this.properties));
			} else {
				// TODO: throw exception
			}
		}

		// added version and endpoint-interface information to the properties
		this.properties.put(ServiceEndpointDescription.PROP_KEY_INTERFACE_NAME,
				javaInterfaces);
		this.properties.put(ServiceEndpointDescription.PROP_KEY_VERSION,
				versions);
		if (this.javaAndEndpointInterfaces.size() > 0) {
			this.properties
					.put(
							ServiceEndpointDescription.PROP_KEY_PROTOCOL_SPECIFIC_INTERFACE_NAME,
							endpointInterfaces);
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

		Iterator intfIterator = this.javaInterfaceAndFilters.keySet()
				.iterator();
		while (intfIterator.hasNext()) {
			String interfaceName = (String) intfIterator.next();
			sb.append("interface=").append(interfaceName).append("\n");
			sb.append("version=").append(
					this.javaInterfaceAndFilters.get(interfaceName)).append(
					"\n");
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
	public String[] getInterfaceNames() {
		return (String[]) this.javaInterfaceAndFilters.keySet().toArray(
				new String[1]);
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getProtocolSpecificInterfaceName(java.lang.String)
	 */
	public String getProtocolSpecificInterfaceName(String interfaceName) {
		return (String) this.javaAndEndpointInterfaces.get(interfaceName);
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getVersion(java.lang.String)
	 */
	public String getVersion(String interfaceName) {
		return (String) this.javaInterfaceAndFilters.get(interfaceName);
	}

	/**
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolSpecificServiceDescription#getLocation()
	 */
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
		String interf = convertJavaInterface2Path(interfaceName);
		String path = createStringFromProperties(properties);
		if (version != null)
			path = appendPropertyToURLPath(path,
					ServiceEndpointDescription.PROP_KEY_VERSION, version);
		if (endpointInterface != null)
			path = appendPropertyToURLPath(
					path,
					ServiceEndpointDescription.PROP_KEY_PROTOCOL_SPECIFIC_INTERFACE_NAME,
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

		// Get endpoint-interface if it exists
		Object endpointInterfacesValue = properties
				.get(ServiceEndpointDescription.PROP_KEY_PROTOCOL_SPECIFIC_INTERFACE_NAME);
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

				// TODO de-escape escaped chars
				try {
					while (st.hasMoreTokens()) {
						key = st.nextToken();
						if (st.hasMoreTokens())
							value = st.nextToken();
						else
							value = "";

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
		StringTokenizer tokenizer = new StringTokenizer(value, SLPServiceDescriptionAdapter.ESCAPING_CHARACTER);
		//TODO finish
		return null;
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

		boolean isReservedChar = false;
		// if there is only one token
		if (tokenizer.countTokens() == 1) {
			String token = tokenizer.nextToken();
			// then it might be a reserved char
			if (token.length() == 1) {
				// if it's a reserved char
				if (reservedChars.indexOf(token.charAt(0)) == -1)
					isReservedChar = true;
				else
					return value;
			}
			// apparently there are no reserved chars
			else
				return value;
		}
		StringBuffer buffer = new StringBuffer();
		while (tokenizer.hasMoreTokens()) {
			if (isReservedChar) {
				String token = tokenizer.nextToken();
				if (token.length() != 1) {
					// TODO log
					throw new RuntimeException(
							"Error while escaping reserved characters.");
				}
				buffer.append(ESCAPING_CHARACTER + Integer.toHexString(token.charAt(0)));
				isReservedChar = false;
			} else {
				buffer.append(tokenizer.nextToken());
				// next token will be reserved char
				isReservedChar = true;
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
		if (properties != null && descr.getProperties() == null) {
			return false;
		}
		if (properties == null && descr.getProperties() != null) {
			return false;
		}
		if (properties == null && descr.getProperties() == null) {
			return true;
		}
		// if (properties.size() != descr.getProperties().size()) {
		// return false;
		// }
		Iterator it = properties.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (descr.getProperties().containsKey(key)) {
				if (descr.getProperty(key) instanceof String
						&& (properties.get(key) instanceof String[])) {
					if (!properties.get(key).toString().equals(
							(descr.getProperty(key)))) {
						return false;
					}
				} else {
					if (!properties.get(key).equals(descr.getProperty(key))) {
						return false;
					}
				}
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return super.hashCode();
	}
}
