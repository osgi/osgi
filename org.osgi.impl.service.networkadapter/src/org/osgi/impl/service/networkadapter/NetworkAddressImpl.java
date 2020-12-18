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

import java.net.InetAddress;
import java.util.Dictionary;

import org.osgi.service.networkadapter.NetworkAddress;

/**
 * Implementation of NetworkAddress.
 */
public class NetworkAddressImpl implements NetworkAddress {

	/**
	 * The identifier of own.
	 */
    private String id;

    /**
     * Constructor.
     *
     * @param id The identifier of own.
     */
    NetworkAddressImpl(String id) {

        this.id = id;
    }

    @Override
	public String getNetworkAdapterType() {
       return (String)getProperty(NETWORKADAPTER_TYPE);
    }

    @Override
	public String getIpAddressVersion() {
        return (String)getProperty(IPADDRESS_VERSION);
    }

    @Override
	public String getIpAddressScope() {
        return (String)getProperty(IPADDRESS_SCOPE);
    }

    @Override
	public String getIpAddress() {
        return (String)getProperty(IPADDRESS);
    }

    @Override
	public InetAddress getInetAddress() {
        return NetworkIfUtil.getInetAddress(getIpAddress());
    }

    @Override
	public int getSubnetMaskLength() {
        return ((Integer)getProperty(SUBNETMASK_LENGTH)).intValue();
    }

    @Override
	public String getNetworkAdapterPid() {
        return (String)getProperty(NETWORKADAPTER_PID);
    }

    @Override
	public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append("NetworkAddress[");
        sb.append(getNetworkAdapterType());
        sb.append(", ");
        sb.append(getIpAddressVersion());
        sb.append(", ");
        sb.append(getIpAddressScope());
        sb.append(", ");
        sb.append(getIpAddress());
        sb.append(", ");
        sb.append(getSubnetMaskLength());
        sb.append(", ");
        sb.append(getNetworkAdapterPid());
        sb.append("]");

        return sb.toString();
    }

    /**
     * Method to get a service property.
     *
     * @param key Key of service property.
     * @return Value of service property.
     */
    private Object getProperty(String key) {

		Dictionary<String,Object> prop = NetworkIfManager.getInstance()
				.getNetworkAddressProp(id);
        return prop.get(key);
    }
}
