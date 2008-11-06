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

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.Vector;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.admin.CacheElementInfo;
import org.apache.jcs.admin.JCSAdminBean;
import org.apache.jcs.engine.control.CompositeCacheManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.impl.service.discovery.AbstractDiscovery;
import org.osgi.impl.service.discovery.InformListenerTask;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.log.LogService;

/**
 * @author Thomas Kiesslich
 * 
 */
public class JCSHandlerImpl extends AbstractDiscovery {
	private static CompositeCacheManager ccm = null;

	// global attributes
	private String regionServices;

	private JCS cacheServices;

	private JCSAdminBean adminBean;

	private String communityName;

	private boolean multicast;

	private String localContainerID;

	private String tcpListenerPort;

	// multicast parameters
	private String udpDiscoveryAddr;

	private String udpDiscoveryPort;

	// TCP parameters
	private ArrayList externalNodes;

	// the property keys to receive the properties from the instance config
	public static final String PROP_KEY_MCAST_ADDRESS = "MulticastAddress";

	public static final String PROP_KEY_MCAST_PORT = "MulticastPort";

	public static final String PROP_KEY_CLUSTER = "CommunityName";

	public static final String PROP_KEY_RECEIVE_IF = "ReceiveInterfaces";

	public static final String PROP_KEY_BIND_ADDRESS = "BindAddress";

	// The JCS multicast address is the network address where multicast
	// calls are sent to; property will be retrieved from from config file.
	// This is the default, if the config property does not exist.
	public static final String DEFAULT_MCAST_ADDRESS = "228.8.8.8";

	// The JCS multicast port is the network address port where multicast
	// calls are sent to; property will be retrieved from from config file.
	// This is the default, if the config property does not exist.
	public static final String DEFAULT_MCAST_PORT = "45567";

	// The JCS listener TCP port to read information from other nodes.
	// The listener port must be used as TCP sender port by the other nodes.
	// In case of multicast the used listener port will be discovered via UDP
	// from each node. In case of TCP only the TCP sender ports must be
	// configured at creation time of the cache.
	// This is the default, if the config property does not exist.
	public static final String DEFAULT_LISTENER_PORT = "7800";

	// The JCS region name is the propagated name, to detect other nodes
	// automatically; property will be retrieved from config file.
	// This is the default, if the config property does not exist.
	public static final String DEFAULT_COMMUNITY = "OSGi EEG";

	private boolean isConfigTCP = false;

	private final int POLLDELAY = 500; // 500 millisec

	private Timer t = null;

	/**
	 * @throws RuntimeException
	 *             if it was not possible to determine a host name or address
	 */
	public JCSHandlerImpl(final BundleContext bc, final LogService logger) {
		super(bc, logger);
		startCache();
	}

	/**
	 * 
	 * 
	 * @see org.osgi.impl.service.discovery.ProtocolHandler#unpublishService(org.osgi.service.discovery.ServiceEndpointDescription)
	 */
	public void unpublishService(ServiceEndpointDescription serviceDescription) {
		validateServiceDescription(serviceDescription);
		log(LogService.LOG_DEBUG, "unpublish service "
				+ serviceDescription.toString());
		if (serviceDescription instanceof JCSServiceEndpointDescription) {
			JCSServiceEndpointDescription svcDescr = (JCSServiceEndpointDescription) serviceDescription;
			log(LogService.LOG_DEBUG, "unpublish service "
					+ (svcDescr.toString()));
			removeInternal(svcDescr.toString());
			// inform listeners about removal
			notifyListenersOnRemovedServiceDescription(serviceDescription);
		}
	}

	/**
	 * 
	 * @param community
	 * @param useMulticast
	 * @param containerID
	 * @return
	 */
	private boolean init(final String community, final boolean useMulticast,
			final String containerID) {
		super.init();
		communityName = community;
		multicast = useMulticast;
		localContainerID = containerID;

		regionServices = DistributedCacheConfig.REGION_PREFIX_SERVICES
				+ communityName;

		Properties props = setJCSProperties();

		if (ccm == null) {
			logProperties(localContainerID
					+ " Initialize distributed Cache JCS with properties: ",
					props);
		} else {
			log(LogService.LOG_DEBUG, localContainerID
					+ " Distributed Cache JCS has been already initialized");
		}

		if (ccm == null) {
			ccm = CompositeCacheManager.getUnconfiguredInstance();
			ccm.configure(props);
		}

		try {
			cacheServices = JCS.getInstance(regionServices);
		} catch (CacheException e) {
			log(
					LogService.LOG_ERROR,
					localContainerID
							+ " Error at configuration of Distributed Cache JCS, cache services",
					e);
			throw new RuntimeException(
					"Error at configuration of Distributed Cache JCS "
							+ e.getMessage());
		}

		adminBean = new JCSAdminBean();

		t = new Timer(false);
		t.schedule(new InformListenerTask(this), 0, POLLDELAY);

		return true;
	}

	/**
	 * @return
	 */
	private Properties setJCSProperties() {
		Properties props = new Properties();

		// default values
		props.put(DistributedCacheConfig.JCS_DEFAULT_KEY,
				DistributedCacheConfig.JCS_DEFAULT_VAL);

		props.put(DistributedCacheConfig.JCS_DEFAULT_CACHEATTR_KEY,
				DistributedCacheConfig.JCS_DEFAULT_CACHEATTR_VAL);

		props.put(DistributedCacheConfig.JCS_DEFAULT_CACHEATTR_MAX_OBJECTS_KEY,
				DistributedCacheConfig.JCS_DEFAULT_CACHEATTR_MAX_OBJECTS_VAL);

		props
				.put(
						DistributedCacheConfig.JCS_DEFAULT_CACHEATTR_MEMORY_CACHE_NAME_KEY,
						DistributedCacheConfig.JCS_DEFAULT_CACHEATTR_MEMORY_CACHE_NAME_VAL);

		props
				.put(
						DistributedCacheConfig.JCS_DEFAULT_CACHEATTR_USE_MEMORY_SHRINKER_KEY,
						DistributedCacheConfig.JCS_DEFAULT_CACHEATTR_USE_MEMORY_SHRINKER_VAL);

		props
				.put(
						DistributedCacheConfig.JCS_DEFAULT_CACHEATTR_SHRINKER_INTERVAL_SECONDS_KEY,
						String.valueOf(DistributedCacheConfig
								.getShrinkerIntervalSeconds()));

		props.put(DistributedCacheConfig.JCS_DEFAULT_ELEMENTATTR_KEY,
				DistributedCacheConfig.JCS_DEFAULT_ELEMENTATTR_VAL);

		props.put(
				DistributedCacheConfig.JCS_DEFAULT_ELEMENTATTR_IS_ETERNAL_KEY,
				DistributedCacheConfig.JCS_DEFAULT_ELEMENTATTR_IS_ETERNAL_VAL);

		props
				.put(
						DistributedCacheConfig.JCS_DEFAULT_ELEMENTATTR_MAX_LIFE_SECONDS_KEY,
						String.valueOf(DistributedCacheConfig
								.getLeaseTimeSeconds()));

		// region for services
		props.put(DistributedCacheConfig.PREFIX_REGION_KEY + regionServices,
				DistributedCacheConfig.REGION_VAL);

		// TCP Lateral Auxiliary
		props.put(DistributedCacheConfig.JCS_AUXILIARY_LTCP_KEY,
				DistributedCacheConfig.JCS_AUXILIARY_LTCP_VAL);

		props.put(DistributedCacheConfig.JCS_AUXILIARY_LTCP_ATTR_KEY,
				DistributedCacheConfig.JCS_AUXILIARY_LTCP_ATTR_VAL);

		props
				.put(
						DistributedCacheConfig.JCS_AUXILIARY_LTCP_ATTR_TCP_LISTENER_PORT_KEY,
						tcpListenerPort);

		if (multicast) {
			props
					.put(
							DistributedCacheConfig.JCS_AUXILIARY_LTCP_ATTR_UDP_DISCOVERY_ENABLED_KEY,
							"true");

			props
					.put(
							DistributedCacheConfig.JCS_AUXILIARY_LTCP_ATTR_UDP_DISCOVERY_ADDR_KEY,
							udpDiscoveryAddr);

			props
					.put(
							DistributedCacheConfig.JCS_AUXILIARY_LTCP_ATTR_UDP_DISCOVERY_PORT_KEY,
							udpDiscoveryPort);
		} else {
			props
					.put(
							DistributedCacheConfig.JCS_AUXILIARY_LTCP_ATTR_UDP_DISCOVERY_ENABLED_KEY,
							"false");

			props
					.put(
							DistributedCacheConfig.JCS_AUXILIARY_LTCP_ATTR_TCP_SERVERS_KEY,
							getFormattedExternalNodes());
		}
		return props;
	}

	/**
	 * Starts the distributed cache with multicast discovery protocol.
	 * 
	 * @param community
	 *            Name of the Symphonia community (cluster).
	 * @param bindAddr
	 *            the own IP-Adresse
	 * @param containerID
	 *            ID of the local container.
	 * @param listenerPort
	 *            This is the port this cache should listen on.
	 * @param multicastAddr
	 *            The address the UDP discovery process should broadcast
	 *            messages to.
	 * @param multicastPort
	 *            The port the UDP discovery process should send messages to.
	 */
	private void startCacheMulticast(final String community,
			final String bindAddr, final String containerID,
			final String listenerPort, final String multicastAddr,
			final String multicastPort) {
		setMulticastParameters(bindAddr, listenerPort, multicastAddr,
				multicastPort);
		init(community, true, containerID);
	}

	/**
	 * Starts the distributed cache with multicast discovery protocol.
	 * 
	 * @param community
	 *            Name of the Symphonia community (cluster).
	 * @param bindAddr
	 *            the own IP-Adresse
	 * @param containerID
	 *            ID of the local container.
	 * @param listenerPort
	 *            This is the port this cache should listen on.
	 * @param externalNodes
	 *            This is the list of external servers this cache should try to
	 *            connect to.
	 */
	private void startCacheTCP(final String community, final String bindAddr,
			final String containerID, final String listenerPort,
			final ArrayList externalNodes) {
		setTCPParameters(bindAddr, listenerPort, externalNodes);
		init(community, false, containerID);
	}

	/**
	 * Starts the distributed cache depending on the configuration.
	 */
	private void startCache() {
		log(LogService.LOG_DEBUG, "initialize discovery for container="
				+ this.localContainerID);

		// retrieve properties from config file
		String clusterName = this.readProperty(PROP_KEY_CLUSTER,
				DEFAULT_COMMUNITY);
		String mcastAddr = this.readProperty(PROP_KEY_MCAST_ADDRESS,
				DEFAULT_MCAST_ADDRESS);
		String mcastPort = this.readProperty(PROP_KEY_MCAST_PORT,
				DEFAULT_MCAST_PORT);

		String host = null;
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (Exception ex) {
			try {
				host = InetAddress.getLocalHost().getHostAddress();
			} catch (Exception exi) {
				throw new RuntimeException(
						"It was not possilbe to determine a host name or address. JCS cannot be started");
			}
		}

		String bindAddress = this.readProperty(PROP_KEY_BIND_ADDRESS, host);
		if (bindAddress.equals("localhost") || bindAddress.equals("127.0.0.1")) {
			// externalize the host address
			bindAddress = host;
			log(LogService.LOG_INFO, "BindAddress has been externalized to: "
					+ bindAddress);
		}

		// get data from system.xml
		// DiscoverySystemConfig systemConfig = new DiscoverySystemConfig();
		// if (!systemConfig.readData()) {
		// throw new ComponentInitializationException("ListenerPort not properly
		// configured in system.xml");
		// }

		// read listener port from system.xml.
		String listenerPort = DEFAULT_LISTENER_PORT;
		// this.localPeer = systemConfig.getLocalPeer();
		// if (this.localPeer != null && (listenerPort =
		// this.localPeer.getPort()) != null && !listenerPort.equals("")) {
		// log(LogService.LOG_INFO, "The listener port has been read from
		// System.xml, port=" + listenerPort);
		// } else {
		// LOG.error(DiscoveryFaultSpec.ERROR_INITIALIZING_DISCOVERY,
		// "ListenerPort not properly configured in system.xml");
		// throw new ComponentInitializationException("ListenerPort not properly
		// configured in system.xml");
		// }

		if (isConfigTCP) {
			// use TCP protocol
			log(LogService.LOG_INFO, "Discovery uses the TCP protocol only");
			// ArrayList<Peer> externalNodes = systemConfig.getExternalNodes();
			startCacheTCP(clusterName, bindAddress, this.localContainerID,
					listenerPort, externalNodes);
		} else {
			// use Multicast protocol
			log(LogService.LOG_INFO, "Discovery uses the multicast protocol");
			startCacheMulticast(clusterName, bindAddress,
					this.localContainerID, listenerPort, mcastAddr, mcastPort);
		}
	}

	/**
	 * Read property.
	 * 
	 * @param prop
	 *            the property to be read
	 * @param defValue
	 *            default value to be used whether the property is unavailable
	 * 
	 * @return the property value
	 */
	private String readProperty(final String prop, final String defValue) {
		boolean defaultValue = false;
		String value = null;

		try {
			value = null;
			// value = this.instanceCfg.getProperty(prop);
			if (value == null || value.equals("")) {
				defaultValue = true;
			}
		} catch (Exception e) {
			defaultValue = true;
		}

		if (defaultValue) {

			log(
					LogService.LOG_INFO,
					"The property ["
							+ prop
							+ "] could not be read from instance, the value will be set to default="
							+ defValue);
			value = defValue;
		} else {
			log(LogService.LOG_INFO, "The property [" + prop
					+ "] has been read from instance with value=" + value);
		}
		return value;
	}

	/**
	 * @see com.siemens.symphonia.discovery.cache.DistributedCache#setMulticastParameters(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	private void setMulticastParameters(final String bindAddr,
			final String listenerPort, final String multicastAddr,
			final String multicastPort) {
		tcpListenerPort = listenerPort;
		udpDiscoveryAddr = multicastAddr;
		udpDiscoveryPort = multicastPort;
	}

	/**
	 * @see com.siemens.symphonia.discovery.cache.DistributedCache#setTCPParameters(java.lang.String,
	 *      java.lang.String, java.util.ArrayList)
	 */
	private void setTCPParameters(final String bindAddr,
			final String listenerPort, final ArrayList externalNodesTCP) {
		tcpListenerPort = listenerPort;
		externalNodes = externalNodesTCP;
	}

	/**
	 * Logs all key/value pairs of Properties in a formatted form.
	 * 
	 * @param text
	 *            Text to be logged in front of the properties.
	 * @param probs
	 *            Properties to be logged.
	 */
	private void logProperties(final String text, final Properties probs) {

		StringBuffer sb = new StringBuffer(text);
		Enumeration e = probs.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			Object value = probs.get(key);
			sb.append("\n    ");
			sb.append(key);
			sb.append(":    ");
			sb.append(value);
		}
		sb.append("\n    Scheduler intervals in ms: ");
		sb.append(DistributedCacheConfig.getSchedulerTimeMs());
		log(LogService.LOG_DEBUG, sb.toString());
	}

	/**
	 * 
	 * @return
	 */
	private HashSet getKeys() {
		List cacheElementInfos = null;
		HashSet keys = null;
		try {
			synchronized (this) {
				cacheElementInfos = adminBean.buildElementInfo(regionServices);
				if (cacheElementInfos != null) {
					keys = new HashSet(cacheElementInfos.size());

					for (int i = 0; i < cacheElementInfos.size(); i++) {
						String key = ((CacheElementInfo) cacheElementInfos
								.get(i)).getKey();
						keys.add(key);
					}
				}
			}
		} catch (Exception e) {
			log(LogService.LOG_ERROR, localContainerID
					+ " Exception during getKeys", e);
			return null;
		}
		if (keys == null) {
			log(LogService.LOG_ERROR, localContainerID + " No keys available");
		}
		return keys;
	}

	/**
	 * @see com.siemens.symphonia.discovery.cache.DistributedCache#get(java.lang.String)
	 */
	private ServiceEndpointDescription get(final String key) {
		if (key == null) {
			throw new IllegalArgumentException("parameter key null");
		}
		if (key.equals("")) {
			throw new IllegalArgumentException("parameter key empty string");
		}
		Object objGet = null;
		try {
			synchronized (this) {
				objGet = cacheServices.get(key);
			}
		} catch (Exception e) {
			log(LogService.LOG_ERROR, "Exception in cache.get(key) for key: "
					+ key, e);
		}
		ServiceEndpointDescription res = null;
		if (objGet == null) {
			log(LogService.LOG_DEBUG, "get for key: " + key + " results null");
		} else {
			if (objGet instanceof ServiceEndpointDescription) {
				res = (ServiceEndpointDescription) objGet;
			} else {
				log(LogService.LOG_ERROR, "get for key: " + key
						+ " results no Resource but: " + objGet);
			}
		}

		return res;
	}

	/**
	 * 
	 * @param key
	 * @param resource
	 */
	private void putInternal(final String key,
			final ServiceEndpointDescription resource) {
		if (key == null) {
			throw new RuntimeException("parameter key null");
		}
		if (resource == null) {
			throw new RuntimeException("parameter resource null");
		}

		try {
			synchronized (this) {
				cacheServices.put(key, resource);
			}
		} catch (CacheException e) {
			throw new RuntimeException(
					"Error on Distributed Cache put(), key = " + key + ", "
							+ e.getMessage());
		}
	}

	/**
	 * 
	 * @param key
	 */
	private void removeInternal(final String key) {
		if (key == null) {
			throw new RuntimeException("parameter key null");
		}
		if (key.equals("")) {
			throw new RuntimeException("parameter key empty string");
		}
		try {
			synchronized (this) {
				cacheServices.remove(key);
			}
		} catch (CacheException e) {
			throw new RuntimeException(
					"Error on Distributed Cache remove(), key = " + key + ", "
							+ e.getMessage());
		}
	}

	/**
	 * Clears all resources allocated in init().
	 */
	public void destroy() {
		t.cancel();
		t = null;
		if (adminBean != null) {
			try {
				adminBean.clearAllRegions();
			} catch (IOException e) {
				e.printStackTrace();
			}
			adminBean = null;
		}
		if (cacheServices != null) {
			try {
				cacheServices.clear();
			} catch (CacheException e) {
				e.printStackTrace();
			}
			cacheServices.dispose();
			cacheServices = null;
		}
		if (ccm != null) {
			ccm.shutDown();
			ccm = null;
		}

	}

	/**
	 * @see com.siemens.symphonia.discovery.cache.DistributedCache#getFormattedExternalNodes()
	 */
	private String getFormattedExternalNodes() {
		StringBuffer sb = new StringBuffer();
		boolean firstNode = true;
		Iterator it = externalNodes.iterator();
		while (it.hasNext()) {
			Peer peer = (Peer) it.next();
			if (firstNode) {
				firstNode = false;
			} else {
				sb.append(",");
			}
			sb.append(peer.getHost());
			sb.append(":");
			sb.append(peer.getPort());
		}
		log(LogService.LOG_DEBUG, localContainerID
				+ " Formatted external Nodes: " + sb);
		return sb.toString();
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.Discovery#findService(java.lang.String,
	 *      java.lang.String)
	 */
	public Collection/*<ServiceEndpointDescription>*/ findService(String interfaceName,
			String filter) {
		Filter f = validateFilter(filter);
		Iterator keyIterator = getKeys().iterator();
		Vector result = new Vector();
		while (keyIterator.hasNext()) {
			ServiceEndpointDescription description = get((String) keyIterator
					.next());
			if (description == null) {
				continue;
			}
			Vector interfaces = new Vector();
			Iterator ifNames = description.getInterfaceNames().iterator();
			while(ifNames.hasNext()){
				interfaces.add((String) ifNames.next());
			}
			if ((interfaceName == null || interfaces.contains(interfaceName))
					&& (f == null || f.match(new Hashtable(description
							.getProperties())))) {
				result.add(description);
			}
		}
		return result;
	}

	/**
	 * 
	 * @see org.osgi.service.discovery.Discovery#publishService(java.util.Map,
	 *      java.util.Map, java.util.Map, boolean)
	 */
	public ServiceEndpointDescription publishService(
			Map javaInterfacesAndVersions,
			Map javaInterfacesAndEndpointInterfaces, Map properties,
			boolean arg3) {
		 JCSServiceEndpointDescription svcDescr = new JCSServiceEndpointDescription(javaInterfacesAndVersions,
				javaInterfacesAndEndpointInterfaces, properties);

		putInternal(svcDescr.toString(), svcDescr);
		notifyListenersOnNewServiceDescription(svcDescr);
		return svcDescr;
	}
}
