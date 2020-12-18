/*
 * Copyright (c) OSGi Alliance (2014, 2020). All Rights Reserved.
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

package org.osgi.impl.service.networkadapter;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.service.networkadapter.NetworkAddress;

/**
 * The class which is informed of the change of the network information from Native.
 * <br>
 * When network information was operated by StepService, it is called back.<br>
 * This class is a class using the Singleton pattern.
 */
class NetworkIfTracker {

    /**
     * The only instance of this class.
     */
    private static NetworkIfTracker instance = new NetworkIfTracker();

    /**
     * Map which manages NetworkAddress service and the NetworkAdapter service.
     * <br>
     * Key: NetworkAddress ID
     * Value: NetworkAdapter ID
     */
	private Map<String,String>		idMap		= new HashMap<>();

    /**
     * Constructor.
     */
    private NetworkIfTracker() {
    }

    /**
     * The method to acquire instance.
     * <br>
     * @return The only instance of this class.
     */
    public static NetworkIfTracker getInstance() {
        return instance;
    }

    /**
     * The method to start monitoring of the network information.
     * <br>
     * Originally, starts monitoring of the network information using Native.<br>
     * When network information had a change by starting monitoring, added/modified/removed method is called back.<br>
     */
    void open() {
		// empty
    }

    /**
     * The method to end monitoring of the network information.
     * <br>
     * Originally, ends monitoring of the network information using Native.<br>
     */
    void close() {
        idMap.clear();
    }

    /**
     * The method that is called back at the time of network information addition by Native.
     * <br>
     * @param networkAdapterId NetworkAdapter ID
     * @param networkIfData NetworkIfData
     * @param networkAddressId NetworkAddress ID
     * @param addressVersion IP address version
     * @param addressScope IP address scope
     * @param address IP address
     * @param length subnetmask length
     */
    synchronized void addNetworkAdapter(String networkAdapterId, NetworkIfData networkIfData, String networkAddressId, String addressVersion, String addressScope, String address, int length) {

        String networkAdapterPID = "org.osgi.impl.service.networkadapter." + networkAdapterId;
        String networkAdapterType = networkIfData.getNwIfType();

        if (networkAddressId != null) {
            // Registers new NetworkAddress service.
            addNetworkAddress(networkAdapterPID, networkAdapterType, networkAddressId, addressVersion, addressScope, address, length);

            // Adds relations with NetworkAdapter.
            idMap.put(networkAddressId, networkAdapterId);
        }

        NetworkAdapter networkAdapter = new NetworkAdapterImpl(networkAdapterId, networkIfData.getMTU());

		Dictionary<String,Object> prop = new Hashtable<>();
        prop.put(Constants.SERVICE_PID, networkAdapterPID);
        prop.put(NetworkAdapter.NETWORKADAPTER_TYPE, networkAdapterType);
        if (networkIfData.getDisplayName() != null) {
            prop.put(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, networkIfData.getDisplayName());
        } else {
            prop.put(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, NetworkAdapter.EMPTY_STRING);
        }
        prop.put(NetworkAdapter.NETWORKADAPTER_NAME, networkIfData.getName());
        prop.put(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS, networkIfData.getHardwareAddress());
        prop.put(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK, Boolean.valueOf(networkIfData.isLoopback()));
        prop.put(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT, Boolean.valueOf(networkIfData.isPointToPoint()));
        prop.put(NetworkAdapter.NETWORKADAPTER_IS_UP, Boolean.valueOf(networkIfData.isUp()));
        prop.put(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL, Boolean.valueOf(networkIfData.isVirtual()));
        prop.put(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST, Boolean.valueOf(networkIfData.supportsMulticast()));
        if (networkIfData.getParent() != null) {
            prop.put(NetworkAdapter.NETWORKADAPTER_PARENT, networkIfData.getParent());
        } else {
            prop.put(NetworkAdapter.NETWORKADAPTER_PARENT, NetworkAdapter.EMPTY_STRING);
        }
        if (networkIfData.getSubInterface() != null) {
            prop.put(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE, networkIfData.getSubInterface());
        } else {
            prop.put(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE, NetworkAdapter.EMPTY_STRING_ARRAY);
        }
        NetworkIfManager.getInstance().putNetworkAdapterProp(networkAdapterId, prop);

		ServiceRegistration<NetworkAdapter> reg = Activator.getContext()
				.registerService(NetworkAdapter.class, networkAdapter, prop);
        NetworkIfManager.getInstance().putNetworkAdapterReg(networkAdapterId, reg);
    }

    /**
     * The method that is called back at the time of IP address information addition by Native.
     * <br>
     * @param networkAdapterId NetworkAdapter ID
     * @param networkAddressId NetworkAddress ID
     * @param addressVersion IP address version
     * @param addressScope IP address scope
     * @param address IP address
     * @param length subnetmask length
     */
    synchronized void addNetworkAddress(String networkAdapterId, String networkAddressId, String addressVersion, String addressScope, String address, int length) {

        // Gets existing NetworkAdapter service.
		ServiceRegistration<NetworkAdapter> reg = NetworkIfManager.getInstance()
				.getNetworkAdapterReg(networkAdapterId);

        // Registers new NetworkAddress service.
        String networkAdapterPID = (String) reg.getReference().getProperty(Constants.SERVICE_PID);
        String networkAdapterType = (String) reg.getReference().getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
        addNetworkAddress(networkAdapterPID, networkAdapterType, networkAddressId, addressVersion, addressScope, address, length);

        // Adds relations with NetworkAdapter.
        idMap.put(networkAddressId, networkAdapterId);
    }

    /**
     * The method that is called back at the time of IP address information modification by Native.
     * <br>
     * IP address or subnetmask length may be changed.
     * <br>
     * @param networkAddressId NetworkAddress ID
     * @param addressVersion IP address version
     * @param addressScope IP address scope
     * @param address IP address
     * @param length subnetmask length
     */
    synchronized void modifiedNetworkAddress(String networkAddressId, String addressVersion, String addressScope, String address, int length) {

		Dictionary<String,Object> prop = NetworkIfManager.getInstance()
				.getNetworkAddressProp(networkAddressId);
        if (prop == null) {
            throw new IllegalArgumentException("Not NetworkAddress.");
        }

        prop.put(NetworkAddress.IPADDRESS, address);
        prop.put(NetworkAddress.IPADDRESS_VERSION, addressVersion);
        prop.put(NetworkAddress.IPADDRESS_SCOPE, addressScope);
        prop.put(NetworkAddress.SUBNETMASK_LENGTH, Integer.valueOf(length));

		ServiceRegistration<NetworkAddress> reg = NetworkIfManager.getInstance()
				.getNetworkAddressReg(networkAddressId);
        reg.setProperties(prop);
    }

    /**
     * The method that is called back at the time of network IF information deletion by Native.
     * <br>
     * Unregisters the NetworkAdapter service and the associated NetworkAddress service.
     *
     * @param networkAdapterId NetworkAdapter ID
     */
    synchronized void removedNetworkAdapter(String networkAdapterId) {

		ServiceRegistration<NetworkAdapter> reg = NetworkIfManager.getInstance()
				.removeNetworkAdapterReg(networkAdapterId);
        if (reg != null) {
            reg.unregister();
        }

		for (Iterator<Entry<String,String>> iterator = idMap.entrySet()
				.iterator(); iterator.hasNext();) {
			Entry<String,String> entry = iterator.next();
            String targetNetworkAdapterId = entry.getValue();

            if (targetNetworkAdapterId.equals(networkAdapterId)) {
                String targetNetworkAddressId = entry.getKey();
				ServiceRegistration<NetworkAddress> removeNetworkAddressReg = NetworkIfManager
						.getInstance()
						.removeNetworkAddressReg(targetNetworkAddressId);
				if (removeNetworkAddressReg != null) {
					removeNetworkAddressReg.unregister();
                }
                NetworkIfManager.getInstance().removeNetworkAddressProp(targetNetworkAddressId);

                iterator.remove();
            }
        }
    }

    /**
     * The method that is called back at the time of IP address information deletion by Native.
     * <br>
     * @param networkAddressId NetworkAddress ID
     */
    synchronized void removedNetworkAddress(String networkAddressId) {

		ServiceRegistration<NetworkAddress> reg = NetworkIfManager.getInstance()
				.removeNetworkAddressReg(networkAddressId);
        if (reg != null) {
            reg.unregister();
        }

        NetworkIfManager.getInstance().removeNetworkAddressProp(networkAddressId);

        // Removes the relations with NetworkAdapter.
        idMap.remove(networkAddressId);
    }

    private void addNetworkAddress(String networkAdapterPID, String networkAdapterType, String networkAddressId, String addressVersion, String addressScope, String address, int length) {

		Dictionary<String,Object> prop = new Hashtable<>();
        prop.put(NetworkAddress.NETWORKADAPTER_TYPE, networkAdapterType);
        prop.put(NetworkAddress.IPADDRESS_VERSION, addressVersion);
        prop.put(NetworkAddress.IPADDRESS_SCOPE, addressScope);
        prop.put(NetworkAddress.IPADDRESS, address);
        prop.put(NetworkAddress.SUBNETMASK_LENGTH, Integer.valueOf(length));
        prop.put(NetworkAddress.NETWORKADAPTER_PID, networkAdapterPID);
        NetworkIfManager.getInstance().putNetworkAddressProp(networkAddressId, prop);

        NetworkAddress networkAddress = new NetworkAddressImpl(networkAddressId);

		ServiceRegistration<NetworkAddress> reg = Activator.getContext()
				.registerService(NetworkAddress.class, networkAddress, prop);
        NetworkIfManager.getInstance().putNetworkAddressReg(networkAddressId, reg);
    }
}
