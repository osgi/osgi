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

import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptions.ZCLCommandDescription;
import org.osgi.service.zigbee.descriptions.ZCLGlobalClusterDescription;

/**
 * @author $Id$
 */
public class ZCLClusterDescriptionImpl implements ZCLClusterDescription {

	private ZCLGlobalClusterDescription		global;
	@SuppressWarnings("unused")
	private boolean							isServer;
	private ZCLAttributeDescriptionImpl[]	attributeDescriptions;
	private ZCLCommandDescription[]			commandDescriptions;

	public ZCLClusterDescriptionImpl(ZCLAttributeDescriptionImpl[] attributeDescriptions, ZCLCommandDescription[] commandDescriptions, ZCLGlobalClusterDescription global, boolean isServer) {
		this.global = global;
		this.isServer = isServer;
		this.attributeDescriptions = attributeDescriptions;
		this.commandDescriptions = commandDescriptions;
	}

	@Override
	public int getId() {
		return global.getClusterId();
	}

	@Override
	public ZCLGlobalClusterDescription getGlobalClusterDescription() {
		return global;
	}

	@Override
	public ZCLCommandDescription[] getReceivedCommandDescriptions() {
		return commandDescriptions;
	}

	@Override
	public ZCLCommandDescription[] getGeneratedCommandDescriptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ZCLAttributeDescription[] getAttributeDescriptions() {
		return attributeDescriptions;
	}

	@Override
	public String toString() {
		return "" + this.getClass().getName() + "[id: " + global.getClusterId() + ", global: " + global + "]";
	}
}
