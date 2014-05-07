/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.networkadapter.NetworkAddress;
import org.osgi.service.networkadapter.NetworkAdapter;

class NetworkIfTracker {

    private static NetworkIfTracker instance = new NetworkIfTracker();

    private Map idMap = new HashMap();

    private NetworkIfTracker() {
    }

    public static NetworkIfTracker getInstance() {
        return instance;
    }

    void open() {
    }

    void close() {
        idMap.clear();
    }

    synchronized void addNetworkAdapter(String networkAdapterId, NetworkIfData networkIfData, String networkAddressId, String addressVersion, String addressScope, String address, int length) {

        String networkAdapterPID = "org.osgi.impl.service.nwifinfo." + networkAdapterId;
        String networkAdapterType = networkIfData.getNwIfType();

        if (networkAddressId != null) {
            addNetworkAddress(networkAdapterPID, networkAdapterType, networkAddressId, addressVersion, addressScope, address, length);

            idMap.put(networkAddressId, networkAdapterId);
        }

        NetworkAdapter networkAdapter = new NetworkAdapterImpl(networkAdapterId, networkIfData.getMTU());

        Dictionary prop = new Properties();
        prop.put(Constants.SERVICE_PID, networkAdapterPID);
        prop.put(NetworkAdapter.NETWORKADAPTER_TYPE, networkAdapterType);
        if (networkIfData.getDisplayName() != null) {
            prop.put(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME, networkIfData.getDisplayName());
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
        }
        if (networkIfData.getSubInterface() != null) {
            prop.put(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE, networkIfData.getSubInterface());
        }
        NetworkIfManager.getInstance().putNetworkAdapterProp(networkAdapterId, prop);

        ServiceRegistration reg = Activator.getContext().registerService(NetworkAdapter.class.getName(), networkAdapter, prop);
        NetworkIfManager.getInstance().putNetworkAdapterReg(networkAdapterId, reg);
    }

    synchronized void addNetworkAddress(String networkAdapterId, String networkAddressId, String addressVersion, String addressScope, String address, int length) {

        ServiceRegistration reg = NetworkIfManager.getInstance().getNetworkAdapterReg(networkAdapterId);

        String networkAdapterPID = (String) reg.getReference().getProperty(Constants.SERVICE_PID);
        String networkAdapterType = (String) reg.getReference().getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
        addNetworkAddress(networkAdapterPID, networkAdapterType, networkAddressId, addressVersion, addressScope, address, length);

        idMap.put(networkAddressId, networkAdapterId);
    }

    synchronized void modifiedNetworkAddress(String networkAddressId, String addressVersion, String addressScope, String address, int length) {

        Dictionary prop = NetworkIfManager.getInstance().getNetworkAddressProp(networkAddressId);
        if (prop == null) {
            throw new IllegalArgumentException("Not NetworkAddress.");
        }

        prop.put(NetworkAddress.IPADDRESS, address);
        prop.put(NetworkAddress.IPADDRESS_VERSION, addressVersion);
        prop.put(NetworkAddress.IPADDRESS_SCOPE, addressScope);
        prop.put(NetworkAddress.SUBNETMASK_LENGTH, new Integer(length));

        ServiceRegistration reg = NetworkIfManager.getInstance().getNetworkAddressReg(networkAddressId);
        reg.setProperties(prop);
    }

    synchronized void removedNetworkAdapter(String networkAdapterId) {

        ServiceRegistration reg = NetworkIfManager.getInstance().removeNetworkAdapterReg(networkAdapterId);
        if (reg != null) {
            reg.unregister();
        }

        for (Iterator iterator = idMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String targetNetworkAdapterId = (String)entry.getValue();

            if (targetNetworkAdapterId.equals(networkAdapterId)) {
                String targetNetworkAddressId = (String)entry.getKey();
                reg = NetworkIfManager.getInstance().removeNetworkAddressReg(targetNetworkAddressId);
                if (reg != null) {
                    reg.unregister();
                }
                NetworkIfManager.getInstance().removeNetworkAddressProp(targetNetworkAddressId);

                iterator.remove();
            }
        }

    }

    synchronized void removedNetworkAddress(String networkAddressId) {

        ServiceRegistration reg = NetworkIfManager.getInstance().removeNetworkAddressReg(networkAddressId);
        if (reg != null) {
            reg.unregister();
        }

        NetworkIfManager.getInstance().removeNetworkAddressProp(networkAddressId);

        idMap.remove(networkAddressId);
    }

    private void addNetworkAddress(String networkAdapterPID, String networkAdapterType, String networkAddressId, String addressVersion, String addressScope, String address, int length) {

        Dictionary prop = new Properties();
        prop.put(NetworkAddress.NETWORKADAPTER_TYPE, networkAdapterType);
        prop.put(NetworkAddress.IPADDRESS_VERSION, addressVersion);
        prop.put(NetworkAddress.IPADDRESS_SCOPE, addressScope);
        prop.put(NetworkAddress.IPADDRESS, address);
        prop.put(NetworkAddress.SUBNETMASK_LENGTH, new Integer(length));
        prop.put(NetworkAddress.NETWORKADAPTER_PID, networkAdapterPID);
        NetworkIfManager.getInstance().putNetworkAddressProp(networkAddressId, prop);

        NetworkAddress networkAddress = new NetworkAddressImpl(networkAddressId);

        ServiceRegistration reg = Activator.getContext().registerService(NetworkAddress.class.getName(), networkAddress, prop);
        NetworkIfManager.getInstance().putNetworkAddressReg(networkAddressId, reg);
    }
}
