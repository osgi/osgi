package org.osgi.service.networkadapter;

import java.net.SocketException;

/**
 * This interface represents network interface information.
 * <br>
 * Network interface information service is set the following information as service property.<br>
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
 */
public interface NetworkAdapter {

    /**
     * The key string of "networkAdapter.type" service property.<br>
     * Network Interface Type is specified.<br>
     */
    public String NETWORKADAPTER_TYPE = "networkAdapter.type";

    /**
     * The key string of "networkAdapter.displayName" service property.<br>
     * Network Interface Display Name is specified.<br>
     */
    public String NETWORKADAPTER_DISPLAYNAME = "networkAdapter.displayName";

    /**
     * The key string of "networkAdapter.name" service property.<br>
     * Network Interface Name is specified.<br>
     */
    public String NETWORKADAPTER_NAME = "networkAdapter.name";

    /**
     * The key string of "networkAdapter.hardwareAddress" service property.<br>
     * Hardware Address is specified.<br>
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
     */
    public String NETWORKADAPTER_PARENT = "networkAdapter.parent";

    /**
     * The key string of "networkAdapter.subInterface" service property.<br>
     * Service PID of the NetworkAdapter service which is subinterface of this NetworkAdapter is specified.<br>
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
     * @return Network Interface Type
     */
    public String getNetworkAdapterType();

    /**
     * Returns the network interface display name of "networkAdapter.displayname" service property value.<br>
     *
     * @return Network Interface Display Name
     */
    public String getDisplayName();

    /**
     * Returns the network interface name of "networkAdapter.name" service property value.<br>
     *
     * @return Network Interface Name
     */
    public String getName();

    /**
     * Returns the MAC address of "networkAdapter.hardwareAddress" service property value.<br>
     *
     * @return Hardware Address
     */
    public byte[] getHardwareAddress();

    /**
     * Returns the Maximum Transmission Unit (MTU) of this interface.<br>
     * @return The value of the MTU for that interface.<br>
     * @throws java.net.SocketException If an I/O error occurs.<br>
     */
    public int getMTU() throws SocketException;

    //XXX: 現時点で実装は残しておく．
    /**
     * Returns whether a network interface is a loopback interface.<br>
     * @return true if the interface is a loopback interface.<br>
     * @throws java.net.SocketException If an I/O error occurs.<br>
     */
    public boolean isLoopback() throws SocketException;

    //XXX: 現時点で実装は残しておく．
    /**
     * Returns whether a network interface is a point to point interface.<br>
     * @return true if the interface is a point to point interface.<br>
     * @throws java.net.SocketException If an I/O error occurs.<br>
     */
    public boolean isPointToPoint() throws SocketException;

    //XXX: 現時点で実装は残しておく．
    /**
     * Returns whether a network interface is up and running.<br>
     * @return true if the interface is up and running.<br>
     * @throws java.net.SocketException If an I/O error occurs.<br>
     */
    public boolean isUp() throws SocketException;

    //XXX: 現時点で実装は残しておく．
    /**
     * Returns whether this interface is a virtual interface (also called subinterface).
     * Virtual interfaces are, on some systems, interfaces created as a child of a physical interface 
     * and given different settings (like address or MTU). Usually the name of the interface will the name of 
     * the parent followed by a colon (:) and a number identifying the child since there can be several 
     * virtual interfaces attached to a single physical interface.<br>
     * @return true if this interface is a virtual interface.<br>
     */
    public boolean isVirtual();

    //XXX: 現時点で実装は残しておく．
    /**
     * Returns whether a network interface supports multicasting or not.<br>
     * @return true if the interface supports Multicasting.<br>
     * @throws java.net.SocketException If an I/O error occurs.<br>
     */
    public boolean supportsMulticast() throws SocketException;

}
