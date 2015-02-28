/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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

package org.osgi.service.networkadapter;

import java.net.SocketException;

/**
 * <p>
 * NetworkAdapter is an interface that provides information about single network interfaces provided by the execution environment.
 * </p>
 * <p>
 * If multiple network interfaces are present, NetworkAdapter Services that correspond to each network interface must be registered.
 * Network interface information service is set the following information as service property.
 * </p>
 * <ul>
 * <li>{@link #NETWORKADAPTER_TYPE} : Network Adapter Type
 * <li>{@link #NETWORKADAPTER_DISPLAYNAME} : Network Interface Display Name
 * <li>{@link #NETWORKADAPTER_NAME} : Network Interface Name
 * <li>{@link #NETWORKADAPTER_HARDWAREADDRESS} : Hardware Address
 *
 * <li>{@link #NETWORKADAPTER_IS_UP} : Running status of Network Interface
 * <li>{@link #NETWORKADAPTER_IS_LOOPBACK} : To check loopback interface
 * <li>{@link #NETWORKADAPTER_IS_POINTTOPOINT} : To check point to point interface
 * <li>{@link #NETWORKADAPTER_IS_VIRTUAL} : To check virtual  interface
 * <li>{@link #NETWORKADAPTER_SUPPORTS_MULTICAST} : To check supports multicasting
 * <li>{@link #NETWORKADAPTER_PARENT} : The PID of parent Network Interface
 * <li>{@link #NETWORKADAPTER_SUBINTERFACE} : The PID of sub Network Interface
 * </ul>
 * <p>
 * When a network interface becomes available, NetworkAdapter service associated with the network interface is registered with the service repository.
 * If the network interface becomes unavailable, the corresponding NetworkAdapter service is unregistered.</p>
 * <p>
 * When the attribute values of the network interface are set to the service property changes, NetworkAdapter service is updated.
 * NetworkAdapter interface provides a method corresponding to java.net.NetworkInterface in order to provide information on the associated network interface.
 * However, this interface method does not support the Static method.
 * In addition, because NetworkInterface object or InetAddress object is registered in the service repository as NetworkAdapter and NetworkAdress,
 * the NetworkAdapter interface does not provide a method to get those objects.
 * NetworkAdapter provides a method to retrieve the value of an attribute of a network interface.
 * </p>
 */
public interface NetworkAdapter {

    /**
     * The value string of service property, when information is not available.<br>
     */
    public String EMPTY_STRING = "";

    /**
     * The value byte array of service property, when information is not available.<br>
     */
    public byte[] EMPTY_BYTE_ARRAY = new byte[]{};

    /**
     * The value string array of service property, when information is not available.<br>
     */
    public String[] EMPTY_STRING_ARRAY = new String[]{};

    /**
     * The key string of "networkAdapter.type" service property.<br>
     * Network Interface Type is specified.<br>
     */
    public String NETWORKADAPTER_TYPE = "networkAdapter.type";

    /**
     * The key string of "networkAdapter.displayName" service property.<br>
     * Network Interface display name is specified.<br>
     * {@link #EMPTY_STRING} if no display name is available.<br>
     */
    public String NETWORKADAPTER_DISPLAYNAME = "networkAdapter.displayName";

    /**
     * The key string of "networkAdapter.name" service property.<br>
     * Network Interface Name is specified.<br>
     * {@link #EMPTY_STRING} if no name is available.<br>
     */
    public String NETWORKADAPTER_NAME = "networkAdapter.name";

    /**
     * The key string of "networkAdapter.hardwareAddress" service property.<br>
     * Hardware Address is specified.<br>
     * {@link #EMPTY_BYTE_ARRAY} if no hardware address is available.<br>
     */
    public String NETWORKADAPTER_HARDWAREADDRESS = "networkAdapter.hardwareAddress";

    /**
     * The key string of "networkAdapter.isUp" service property.<br>
     * The value is true when a network interface is up and running, otherwise it is false.<br>
     */
    public String NETWORKADAPTER_IS_UP = "networkAdapter.isUp";

    /**
     * The key string of "networkAdapter.isLoopback" service property.<br>
     * The value is true when a network interface is a loopback interface, otherwise it is false.<br>
     */
    public String NETWORKADAPTER_IS_LOOPBACK = "networkAdapter.isLoopback";

    /**
     * The key string of "networkAdapter.isPointToPoint" service property.<br>
     * The value is true when a network interface is a point to point interface, otherwise it is false.<br>
     */
    public String NETWORKADAPTER_IS_POINTTOPOINT = "networkAdapter.isPointToPoint";

    /**
     * The key string of "networkAdapter.isVirtual" service property.<br>
     * The value is true when a network interface is a virtual interface, otherwise it is false.<br>
     */
    public String NETWORKADAPTER_IS_VIRTUAL = "networkAdapter.isVirtual";

    /**
     * The key string of "networkAdapter.supportsMulticast" service property.<br>
     * The value is true when a network interface supports multicasting, otherwise it is false.<br>
     */
    public String NETWORKADAPTER_SUPPORTS_MULTICAST = "networkAdapter.supportsMulticast";

    /**
     * The key string of "networkAdapter.parent" service property.<br>
     * Service PID of the NetworkAdapter service which is parent of this NetworkAdapter is specified.<br>
     * {@link #EMPTY_STRING} if no parent is available.<br>
     */
    public String NETWORKADAPTER_PARENT = "networkAdapter.parent";

    /**
     * The key string of "networkAdapter.subInterface" service property.<br>
     * Service PID of the NetworkAdapter service which is subinterface of this NetworkAdapter is specified.<br>
     * {@link #EMPTY_STRING_ARRAY} if no subinterface is available.<br>
     */
    public String NETWORKADAPTER_SUBINTERFACE = "networkAdapter.subInterface";

    /**
     * The string of networkadapter type which means the network interface to connect to an external network (i.e. Internet).<br>
     */
    public String WAN = "WAN";

    /**
     * The string of networkadapter type which means the network interface to connect to a local area network.<br>
     */
    public String LAN = "LAN";

    /**
     * Returns the network interface type of "networkAdapter.type" service property value.<br>
     *
     * @return Network Interface Type, or null if "networkAdapter.type" service property value is empty.
     */
    public String getNetworkAdapterType();

    /**
     * Returns the network interface display name of "networkAdapter.displayname" service property value.<br>
     *
     * @return Network Interface display name, or null if "networkAdapter.displayname" service property value is empty.
     */
    public String getDisplayName();

    /**
     * Returns the network interface name of "networkAdapter.name" service property value.<br>
     *
     * @return Network Interface Name, or null if "networkAdapter.name" service property value is empty.
     */
    public String getName();

    /**
     * Returns the MAC address of "networkAdapter.hardwareAddress" service property value.<br>
     *
     * @return Hardware Address, or null if "networkAdapter.hardwareAddress" service property value is empty.
     */
    public byte[] getHardwareAddress();

    /**
     * Returns the Maximum Transmission Unit (MTU) of this interface.<br>
     * @return The value of the MTU for that interface.<br>
     * @throws java.net.SocketException If an I/O error occurs.<br>
     */
    public int getMTU() throws SocketException;

    /**
     * Returns whether a network interface is a loopback interface.<br>
     * @return true if the interface is a loopback interface.<br>
     * @throws java.net.SocketException If an I/O error occurs.<br>
     */
    public boolean isLoopback() throws SocketException;

    /**
     * Returns whether a network interface is a point to point interface.<br>
     * @return true if the interface is a point to point interface.<br>
     * @throws java.net.SocketException If an I/O error occurs.<br>
     */
    public boolean isPointToPoint() throws SocketException;

    /**
     * Returns whether a network interface is up and running.<br>
     * @return true if the interface is up and running.<br>
     * @throws java.net.SocketException If an I/O error occurs.<br>
     */
    public boolean isUp() throws SocketException;

    /**
     * Returns whether this interface is a virtual interface (also called subinterface).
     * Virtual interfaces are, on some systems, interfaces created as a child of a physical interface
     * and given different settings (like address or MTU). Usually the name of the interface will the name of
     * the parent followed by a colon (:) and a number identifying the child since there can be several
     * virtual interfaces attached to a single physical interface.<br>
     * @return true if this interface is a virtual interface.<br>
     */
    public boolean isVirtual();

    /**
     * Returns whether a network interface supports multicasting or not.<br>
     * @return true if the interface supports Multicasting.<br>
     * @throws java.net.SocketException If an I/O error occurs.<br>
     */
    public boolean supportsMulticast() throws SocketException;

}
