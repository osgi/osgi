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
 * @version $Revision$
 */
public class SLPServiceDescriptionAdapter implements ServiceEndpointDescription {
	private Map/* <String, String> */javaInterfaceAndFilters;
	private Map/* <String, String> */javaAndEndpointInterfaces;
	private Map/* <String, Object> */properties;
	private Map/* <String, ServiceURL> */serviceURLs;

	public SLPServiceDescriptionAdapter(String javaInterface, String version,
			String endpointInterface, Map properties)
			throws ServiceLocationException {
		Map javaInterfaceAndFilters = new HashMap();
		javaInterfaceAndFilters.put(javaInterface, version);

		Map javaAndEndpointInterfaces = new HashMap();
		javaAndEndpointInterfaces.put(javaInterface, endpointInterface);

		init(javaInterfaceAndFilters, javaAndEndpointInterfaces, properties);
	}

	public SLPServiceDescriptionAdapter(Map javaInterfaceAndFilters,
			Map javaAndEndpointInterfaces, Map properties)
			throws ServiceLocationException {
		init(javaInterfaceAndFilters, javaAndEndpointInterfaces, properties);
	}

	public SLPServiceDescriptionAdapter(final ServiceURL serviceURL) {
		String interfaceName = convertPath2JavaInterface(serviceURL
				.getServiceType().getConcreteTypeName());
		if (interfaceName == null) {
			throw new IllegalArgumentException(
					"Interface information is missing!");
		}

		this.javaInterfaceAndFilters = new HashMap();
		this.javaAndEndpointInterfaces = new HashMap();
		this.properties = new HashMap();
		this.serviceURLs = new HashMap();

		// Retrieve properties
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
		addPropertiesFromPath(serviceURL.getURLPath());

		// Try to get version
		String version;
		try {
			version = (String) properties
					.get(ServiceEndpointDescription.PROP_KEY_VERSION);
		} catch (Exception e) {
			// TODO log
			version = "*";
		}

		// Add interface
		this.javaInterfaceAndFilters.put(interfaceName, version);

		// Store service URL
		this.serviceURLs.put(interfaceName, serviceURL);
	}

	private void init(Map javaInterfaceAndFilters,
			Map javaAndEndpointInterfaces, Map properties)
			throws ServiceLocationException {
		if (javaInterfaceAndFilters == null) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must not be null.");
		}
		if (javaInterfaceAndFilters.size() <= 0) {
			throw new IllegalArgumentException(
					"Given set of Java interfaces must contain at least one service interface name.");
		}

		this.javaInterfaceAndFilters = javaInterfaceAndFilters;
		this.javaAndEndpointInterfaces = javaAndEndpointInterfaces != null ? javaAndEndpointInterfaces
				: new HashMap();
		this.properties = properties != null ? properties : new HashMap();

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
				if (this.javaAndEndpointInterfaces.size() > 0) {
					endpointInterfaces[i] = (String) this.javaAndEndpointInterfaces
							.get(javaInterfaces[i]);
				}
				this.serviceURLs.put(javaInterfaces[i], createServiceURL(
						javaInterfaces[i], this.properties));
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

	public int compare(Object var0, Object var1) {
		return 0;
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

	private void addPropertiesFromPath(String path) {
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

	public void setProperties(Map props) {
		properties = props;
	}

	public static SLPServiceDescriptionAdapter[] getSLPServiceDescriptions(
			final ServiceEndpointDescription serviceDescription)
			throws ServiceLocationException {
		// TODO validate ServiceEndpointDescription

		// create for each interface an own SLPServiceDescription
		String[] javaInterfaces = serviceDescription.getInterfaceNames();
		SLPServiceDescriptionAdapter[] slpSvcDescriptions = new SLPServiceDescriptionAdapter[javaInterfaces.length];
		for (int i = 0; i < javaInterfaces.length; i++) {
			slpSvcDescriptions[i] = new SLPServiceDescriptionAdapter(
					javaInterfaces[i],
					serviceDescription.getVersion(javaInterfaces[i]),
					serviceDescription
							.getProtocolSpecificInterfaceName(javaInterfaces[i]),
					serviceDescription.getProperties());
		}

		return slpSvcDescriptions;
	}

	public static ServiceURL createServiceURL(String interfaceName,
			Map properties) throws ServiceLocationException {
		String interf = convertJavaInterface2Path(interfaceName);
		String path = getPathFromProperties(properties);

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

	public static String convertJavaInterface2Path(
			final String javaInterfaceName) {
		return javaInterfaceName != null ? ":"
				+ javaInterfaceName.replace('.', '/') : "";
	}

	public static String convertPath2JavaInterface(
			final String interfaceNameEncodedAsPath) {
		return interfaceNameEncodedAsPath != null ? interfaceNameEncodedAsPath
				.replace('/', '.') : null;
	}

	public static String getPathFromProperties(final Map properties2) {
		StringBuffer sb = new StringBuffer();

		String key;
		Object value;

		if (properties2 != null && !properties2.isEmpty()) {
			sb.append("?");

			for (Iterator i = properties2.keySet().iterator(); i.hasNext();) {
				key = (String) i.next();
				value = properties2.get(key);
				if (value == null) {
					value = "<null>";
				}

				sb.append(key).append("=").append(value.toString()).append(",");
			}
		}

		return sb.toString();
	}
}
