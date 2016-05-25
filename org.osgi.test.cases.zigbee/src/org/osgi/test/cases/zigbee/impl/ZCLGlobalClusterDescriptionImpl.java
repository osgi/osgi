
package org.osgi.test.cases.zigbee.impl;

import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptions.ZCLGlobalClusterDescription;

/**
 * Mocked impl.
 * 
 * @author $Id$
 */
public class ZCLGlobalClusterDescriptionImpl implements ZCLGlobalClusterDescription {

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
	public ZCLGlobalClusterDescriptionImpl(int id, String name, String domain, ZCLClusterDescription client, ZCLClusterDescription server) {
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

	public String toString() {
		return "" + this.getClass().getName() + "[id: " + id + ", name: " + name + ", desc: " + desc + ", domain: " + domain + ", client: " + client + ", server: " + server + "]";
	}

}
