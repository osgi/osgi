package org.osgi.service.networkadapter;

import java.net.InetAddress;

/**
 * <p>
 * This interface represents an IP address information.
 * </p>
 * <p>
 * NetworkAddress interface provides information of IP addresses available in which execution
 * environment on a Network Interface Information Service bundle is running.
 * IP address information service is set the following information as service property.
 * </p>
 * <ul>
 * <li>{@link #NETWORKADAPTER_TYPE} : Network Interface Type
 * <li>{@link #IPADDRESS_VERSION} : IP Address Version
 * <li>{@link #IPADDRESS_SCOPE} : IP Address Scope
 * <li>{@link #IPADDRESS} : IP Address
 * <li>{@link #SUBNETMASK_LENGTH} : Subnet Mask Length(IPv4) or Prefix Length(IPv6)
 * <li>{@link #NETWORKADAPTER_PID} : Service PID of the NetworkAdapter service to which this service belongs
 * </ul>
 * <p>
 * NetworkAddress service is registered with the service repository for each available IP address.
 * When associated IP addresses are deleted, or the network interface to which the IP address is bound becomes unavailable, the NetworkAddress service is unregistered.
 * When the associated IP address changes, NetworkAddress service is updated.
 * The user bundle can detect the change of IP address by monitoring the registration or unregistering, updating of NetworkAddress service.
 * Because IP addresses are bound to the network interface, if any, Service PID of the associated NetworkAdapter service and its network interface type are set to service property.
 * NetworkAdapter service MUST be registered after the all associated NetworkAddress services are registered. On the other hand,
 * when unregistering services, after associated NetworkAdapter service is unregistered, NetworkAddress of all related services are unregistered.
 * </p>
 */
public interface NetworkAddress {

    /**
     * The value integer of service property, when information is not available.<br>
     */
    public Integer EMPTY_INTEGER = new Integer(-1);

    /**
     * The key string of "networkAdapter.type" service property.<br>
     * Network Interface Type is specified.
     */
    public String NETWORKADAPTER_TYPE = "networkAdapter.type";

    /**
     * The key string of "ipAddress.version" service property.<br>
     * IP Address Type is specified.
     */
    public String IPADDRESS_VERSION = "ipAddress.version";

    /**
     * The key string of "ipAddress.scope" service property.<br>
     * IP Address Type is specified.
     */
    public String IPADDRESS_SCOPE = "ipAddress.scope";

    /**
     * The key string of "ipAddress" service property.<br>
     * IP Address is specified.
     */
    public String IPADDRESS = "ipAddress";

    /**
     * The key string of "subnetmask.length" service property.<br>
     * Subnet Mask Length(IPv4) or Prefix Length(IPv6) is specified.
     * {@link #EMPTY_INTEGER} if no length is available.<br>
     */
    public String SUBNETMASK_LENGTH = "subnetmask.length";

    /**
     * The key string of "networkAdapter.pid" service property.<br>
     * Service PID of the interface information service to which it belongs is specified.
     */
    public String NETWORKADAPTER_PID = "networkAdapter.pid";

    /**
     * The string of IP address version which means IP address version 4.<br>
     */
    public String IPADDRESS_VERSION_4 = "IPV4";

    /**
     * The string of IP address version which means IP address version 6.<br>
     */
    public String IPADDRESS_VERSION_6 = "IPV6";

    /**
     * The string of IP address scope which means global address.<br>
     * The global address is defined as the address other than the address defined in the RFC6890.<br>
     */
    public String IPADDRESS_SCOPE_GLOBAL = "GLOBAL";

    /**
     * The string of IP address scope which means "Private-Use Networks".<br>
     * See RFC6890 for the definition of "Private-Use Networks".<br>
     */
    public String IPADDRESS_SCOPE_PRIVATE_USE = "PRIVATE_USE";

    /**
     * The string of IP address scope which means "Loopback".<br>
     * See RFC6890 for the definition of "Loopback".<br>
     */
    public String IPADDRESS_SCOPE_LOOPBACK = "LOOPBACK";

    /**
     * The string of IP address scope which means "Link Local".<br>
     * See RFC6890 for the definition of "Link Local".<br>
     */
    public String IPADDRESS_SCOPE_LINKLOCAL = "LINKLOCAL";

    /**
     * The string of IP address scope which means "Unique-Local".<br>
     * See RFC6890 for the definition of "Unique-Local".<br>
     */
    public String IPADDRESS_SCOPE_UNIQUE_LOCAL = "UNIQUE_LOCAL";

    /**
     * The string of IP address scope which means "Unspecified Address".<br>
     * See RFC6890 for the definition of "Unspecified Address".<br>
     */
    public String IPADDRESS_SCOPE_UNSPECIFIED = "UNSPECIFIED";

    /**
     * The string of IP address scope which means "This host on this network".<br>
     * See RFC6890 for the definition of "This host on this network".<br>
     */
    public String IPADDRESS_SCOPE_HOST = "HOST";

    /**
     * The string of IP address scope which means "Shared Address Space".<br>
     * See RFC6890 for the definition of "Shared Address Space".<br>
     */
    public String IPADDRESS_SCOPE_SHARED = "SHARED";

    /**
     * The string of IP address scope which means "Linked-Scoped Unicast".<br>
     * See RFC6890 for the definition of "Linked-Scoped Unicast".<br>
     */
    public String IPADDRESS_SCOPE_LINKED_SCOPED_UNICAST = "LINKED_SCOPED_UNICAST";

    /**
     * Returns the network interface type of "networkAdapter.type" service property value.<br>
     *
     * @return Network Interface Type, or null if "networkAdapter.type" service property value is empty.
     */
    public String getNetworkAdapterType();

    /**
     * Returns the IP address version of "ipaddress.version" service property value.<br>
     *
     * @return IP Address Version, or null if "ipaddress.version" service property value is empty.
     */
    public String getIpAddressVersion();

    /**
     * Returns the IP address scope of "ipaddress.scope" service property value.<br>
     *
     * @return IP Address Scope, or null if "ipaddress.scope" service property value is empty.
     */
    public String getIpAddressScope();

    /**
     * Returns the IP address of "ipaddress" service property value.<br>
     *
     * @return IP Address string, or null if "ipaddress" service property value is empty.
     */
    public String getIpAddress();

    /**
     * Returns the InetAddress object of this IP address.<br>
     * <br>
     * Returned object is created from "ipaddress" service property value.<br>
     *
     * @return InetAddress, or null if "ipaddress" service property value is empty.
     */
    public InetAddress getInetAddress();

    /**
     * Returns the "subnetmask.length" service property value.<br>
     *
     * @return Subnet Mask Length(IPv4) or Prefix Length(IPv6), or -1 if "subnetmask.length" service property value is empty.
     */
    public int getSubnetMaskLength();

    /**
     * Returns the "networkadapter.pid" service property value.<br>
     *
     * @return Service ID of the interface information service to which it belongs, or null if "networkadapter.pid" service property value is empty.
     */
    public String getNetworkAdapterPid();

}
