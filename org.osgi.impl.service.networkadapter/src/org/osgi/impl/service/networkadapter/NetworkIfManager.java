/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.networkadapter;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.service.networkadapter.NetworkAddress;

/**
 * The class which manages ServiceRegistration and the service property.
 * <br>
 * This class is a class using the Singleton pattern.
 */
class NetworkIfManager {

    /**
     * The only instance of this class.
     */
    private static NetworkIfManager instance = new NetworkIfManager();

    /**
     * Map which manages the service property of the NetworkAdapter service.
     * <br>
     * Key: NetworkAdapter ID
     * Value: service property
     */
	private Map<String,Dictionary<String,Object>>			networkAdapterPropMap	= new HashMap<>();

    /**
     * Map which manages the ServiceRegistration of the NetworkAdapter service.
     * <br>
     * Key: NetworkAdapter ID
     * Value: ServiceRegistration
     */
	private Map<String,ServiceRegistration<NetworkAdapter>>	networkAdapterRegMap	= new HashMap<>();

    /**
     * Map which manages the service property of the NetworkAddress service.
     * <br>
     * Key: NetworkAddress ID
     * Value: service property
     */
	private Map<String,Dictionary<String,Object>>			networkAddressPropMap	= new HashMap<>();

    /**
     * Map which manages the ServiceRegistration of the NetworkAddress service.
     * <br>
     * Key: NetworkAddress ID
     * Value: ServiceRegistration
     */
	private Map<String,ServiceRegistration<NetworkAddress>>	networkAddressRegMap	= new HashMap<>();

    /**
     * Constructor.
     */
    private NetworkIfManager() {
    }

    /**
     * The method to acquire instance.
     * <br>
     * @return The only instance of this class.
     */
    public static NetworkIfManager getInstance() {
        return instance;
    }

    /**
     * The method that unregister of all services and releases the resource.
     * <br>
     * It is necessary to call in the state that monitoring of the network information by Native ended.
     */
    synchronized void close() {

        // Unregisters all services.
		for (Iterator<Entry<String,ServiceRegistration<NetworkAddress>>> iterator = networkAddressRegMap
				.entrySet()
				.iterator(); iterator.hasNext();) {
			Entry<String,ServiceRegistration<NetworkAddress>> entry = iterator
					.next();
			ServiceRegistration<NetworkAddress> reg = entry.getValue();
            reg.unregister();
        }
        networkAddressRegMap.clear();
        networkAddressPropMap.clear();

		for (Iterator<Entry<String,ServiceRegistration<NetworkAdapter>>> iterator = networkAdapterRegMap
				.entrySet()
				.iterator(); iterator.hasNext();) {
			Entry<String,ServiceRegistration<NetworkAdapter>> entry = iterator
					.next();
			ServiceRegistration<NetworkAdapter> reg = entry.getValue();
            reg.unregister();
        }
        networkAdapterRegMap.clear();
        networkAdapterPropMap.clear();
    }

	synchronized void putNetworkAdapterProp(String id,
			Dictionary<String,Object> prop) {
        networkAdapterPropMap.put(id, prop);
    }

	synchronized Dictionary<String,Object> getNetworkAdapterProp(String id) {
        return networkAdapterPropMap.get(id);
    }

	synchronized Dictionary<String,Object> removeNetworkAdapterProp(String id) {
        return networkAdapterPropMap.remove(id);
    }


	synchronized void putNetworkAddressProp(String id,
			Dictionary<String,Object> prop) {
        networkAddressPropMap.put(id, prop);
    }

	synchronized Dictionary<String,Object> getNetworkAddressProp(String id) {
        return networkAddressPropMap.get(id);
    }

	synchronized Dictionary<String,Object> removeNetworkAddressProp(String id) {
        return networkAddressPropMap.remove(id);
    }


	synchronized void putNetworkAdapterReg(String id,
			ServiceRegistration<NetworkAdapter> reg) {
        networkAdapterRegMap.put(id, reg);
    }

	synchronized ServiceRegistration<NetworkAdapter> getNetworkAdapterReg(
			String id) {
        return networkAdapterRegMap.get(id);
    }

	synchronized ServiceRegistration<NetworkAdapter> removeNetworkAdapterReg(
			String id) {
        return networkAdapterRegMap.remove(id);
    }


	synchronized void putNetworkAddressReg(String id,
			ServiceRegistration<NetworkAddress> reg) {
        networkAddressRegMap.put(id, reg);
    }

	synchronized ServiceRegistration<NetworkAddress> getNetworkAddressReg(
			String id) {
        return networkAddressRegMap.get(id);
    }

	synchronized ServiceRegistration<NetworkAddress> removeNetworkAddressReg(
			String id) {
        return networkAddressRegMap.remove(id);
    }
}
