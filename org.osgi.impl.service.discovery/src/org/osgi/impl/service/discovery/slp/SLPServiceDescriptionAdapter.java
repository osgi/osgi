package org.osgi.impl.service.discovery.slp;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.osgi.service.discovery.ServiceDescription;

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
public class SLPServiceDescriptionAdapter implements ServiceDescription {
	private String interfaceName;
	private Map properties = new HashMap();
	private ServiceURL serviceURL;
	private Dictionary attributes;

	public SLPServiceDescriptionAdapter(
			final ServiceDescription serviceDescription)
			throws ServiceLocationException {
		this.interfaceName = serviceDescription.getInterfaceName();
		this.attributes = (Hashtable) serviceDescription.getProperties();

		String interf = convertInterfaceName(interfaceName);
		String protocol = (String) serviceDescription.getProperty("protocol");
		String host = (String) serviceDescription.getProperty("host");
		String port = (String) serviceDescription.getProperty("port");

		Integer lifeTime = (Integer) serviceDescription.getProperty("lifetime");
		int lifetime = lifeTime != null ? lifeTime.intValue()
				: ServiceURL.LIFETIME_DEFAULT;

		String path = getPathFromProperties(properties);
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
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.serviceURL = new ServiceURL("service:osgi" + interf
				+ (protocol != null ? protocol + "://" : "://")
				+ (host != null ? host : hostname)
				+ (port != null ? ":" + port : "") + "/" + path, lifetime);
	}

	public SLPServiceDescriptionAdapter(final ServiceURL serviceURL) {
		this.interfaceName = convertInterfaceFromURL(serviceURL
				.getServiceType().getConcreteTypeName());
		if (this.interfaceName == null) {
			throw new IllegalArgumentException(
					"Interface information is missing!");
		}

		this.properties = new HashMap();
		this.serviceURL = serviceURL;

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
	}

	public String getInterfaceName() {
		return interfaceName;
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

	public ServiceURL getServiceURL() {
		return serviceURL;
	}

	public String getServiceType() {
		return serviceURL != null ? serviceURL.getServiceType().toString()
				: null;
	}

	public List getScopes() {
		return null;
	}

	public String getNamingAuthority() {
		return serviceURL != null ? serviceURL.getServiceType()
				.getNamingAuthority() : null;
	}

	public String getFilter() {
		return null;
	}

	public Dictionary getAttributes() {
		return attributes;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("Service:\n");
		sb.append("interface=").append(getInterfaceName()).append("\n");
		sb.append("serviceURL=").append(
				serviceURL != null ? serviceURL.toString() : "").append("\n");
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
		
		sb.append("attributes=\n");

		Enumeration e = attributes.keys();
		while(e.hasMoreElements()) {
			key = (String) e.nextElement();
			value = attributes.get(key);
			if (value == null) {
				value = "<null>";
			}
			sb.append(key).append("=").append(value.toString()).append("\n");
		}

		return sb.toString();
	}

	private String convertInterfaceName(final String interfaceName2) {
		return interfaceName2 != null ? ":" + interfaceName2.replace('.', '/')
				: "";
	}

	private String convertInterfaceFromURL(final String interfaceName2) {
		return interfaceName2 != null ? interfaceName2.replace('/', '.') : null;
	}

	private String getPathFromProperties(final Map properties2) {
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
						value = st.nextToken();

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

	public URL getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getPropertyKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProtocolSpecificInterfaceName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAtttributes(Dictionary attrs) {
		attributes = attrs;
	}
}
