
package org.osgi.impl.service.zigbee.descriptions;

import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeGlobalClusterDescription;

/**
 * Mocked impl of ZigBeeGlobalClusterDescription.
 */
public class ZigBeeGlobalClusterDescriptionImpl implements ZigBeeGlobalClusterDescription {

	private int						id;
	private String					name;
	private String					desc;
	private String					domain;
	private ZCLClusterDescription	client	= null;
	private ZCLClusterDescription	server	= null;

	/**
	 * @param id
	 * @param name
	 * @param domain
	 * @param client
	 * @param server
	 */
	public ZigBeeGlobalClusterDescriptionImpl(int id, String name, String domain, ZCLClusterDescription client, ZCLClusterDescription server) {
		this.id = id;
		this.name = name;
		this.domain = domain;
		this.client = client;
		this.server = server;
	}

	public String getClusterDescription() {
		return desc;
	}

	public int getClusterId() {
		return id;
	}

	public String getClusterName() {
		return name;
	}

	public String getClusterFunctionalDomain() {
		// TODO Auto-generated method stub
		return domain;
	}

	public ZCLClusterDescription getClientClusterDescription() {
		return client;
	}

	public ZCLClusterDescription getServerClusterDescription() {
		return server;
	}

}
