/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.impl.service.zigbee.descriptions;

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
	private String					description	= "";
	private String					domain;

	private ZCLClusterDescription	client		= null;
	private ZCLClusterDescription	server		= null;

	public ZCLGlobalClusterDescriptionImpl(int id, String name, String domain, ZCLClusterDescription client,
			ZCLClusterDescription server) {
		this(id, name, domain);

		this.client = client;
		this.server = server;
	}

	public ZCLGlobalClusterDescriptionImpl(int id, String name, String domain) {
		this.id = id;
		this.name = name;
		this.domain = domain;
	}

	@Override
	public String getClusterDescription() {
		return description;
	}

	@Override
	public int getClusterId() {
		return id;
	}

	@Override
	public String getClusterName() {
		return name;
	}

	@Override
	public String getClusterFunctionalDomain() {
		return domain;
	}

	@Override
	public ZCLClusterDescription getClientClusterDescription() {
		return client;
	}

	@Override
	public ZCLClusterDescription getServerClusterDescription() {
		return server;
	}

	public void setServerClusterDescription(ZCLClusterDescription description) {
		this.server = description;
	}

	public void setClientClusterDescription(ZCLClusterDescription description) {
		this.client = description;
	}

	@Override
	public String toString() {
		return "" + this.getClass().getName() + "[id: " + id + ", name: " + name + ", desc: " + description + ", domain: "
				+ domain + ", client: " + client + ", server: " + server + "]";
	}
}
