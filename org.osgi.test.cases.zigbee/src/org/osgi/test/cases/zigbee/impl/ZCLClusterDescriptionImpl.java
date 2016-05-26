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

package org.osgi.test.cases.zigbee.impl;

import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptions.ZCLCommandDescription;
import org.osgi.service.zigbee.descriptions.ZCLGlobalClusterDescription;

/**
 * Mocked impl.
 * 
 * @author $Id$
 */
public class ZCLClusterDescriptionImpl implements ZCLClusterDescription {

	private int							id;
	private ZCLGlobalClusterDescription	global;

	/**
	 * @param id
	 * @param global
	 */
	public ZCLClusterDescriptionImpl(int id, ZCLGlobalClusterDescription global) {
		this.id = id;
		this.global = global;
	}

	public int getId() {
		return id;
	}

	public ZCLGlobalClusterDescription getGlobalClusterDescription() {
		return global;
	}

	public ZCLCommandDescription[] getReceivedCommandDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public ZCLCommandDescription[] getGeneratedCommandDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public ZCLAttributeDescription[] getAttributeDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toString() {
		return "" + this.getClass().getName() + "[id: " + id + ", global: " + global + "]";
	}

}
