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

import java.net.SocketException;
import java.util.Dictionary;

import org.osgi.service.networkadapter.NetworkAdapter;

/**
 * Implementation of NetworkAdapter.
 */
public class NetworkAdapterImpl implements NetworkAdapter {

	/**
	 * The identifier of own.
	 */
    private String id;

	/**
	 * The MTU of own.
	 */
    private int mtu;

    /**
     * Constructor.
     *
     * @param id The identifier of own.
     * @param mtu The MTU of own.
     */
    NetworkAdapterImpl(String id, int mtu) {

        this.id = id;
        this.mtu = mtu;
    }

    @Override
	public String getNetworkAdapterType() {
        return (String)getProperty(NETWORKADAPTER_TYPE);
    }

    @Override
	public String getDisplayName() {
        return (String)getProperty(NETWORKADAPTER_DISPLAYNAME);
    }

    @Override
	public String getName() {
        return (String)getProperty(NETWORKADAPTER_NAME);
    }

    @Override
	public byte[] getHardwareAddress() {
        return (byte[])getProperty(NETWORKADAPTER_HARDWAREADDRESS);
    }

    @Override
	public int getMTU() throws SocketException {
        return mtu;
    }

    @Override
	public boolean isLoopback() throws SocketException {
        return ((Boolean)getProperty(NETWORKADAPTER_IS_LOOPBACK)).booleanValue();
    }

    @Override
	public boolean isPointToPoint() throws SocketException {
        return ((Boolean)getProperty(NETWORKADAPTER_IS_POINTTOPOINT)).booleanValue();
    }

    @Override
	public boolean isUp() throws SocketException {
        return ((Boolean)getProperty(NETWORKADAPTER_IS_UP)).booleanValue();
    }

    @Override
	public boolean isVirtual() {
        return ((Boolean)getProperty(NETWORKADAPTER_IS_VIRTUAL)).booleanValue();
    }

    @Override
	public boolean supportsMulticast() throws SocketException {
        return ((Boolean)getProperty(NETWORKADAPTER_SUPPORTS_MULTICAST)).booleanValue();
    }

    @Override
	public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append("NetworkAdapter[");
        sb.append(getNetworkAdapterType());
        sb.append(", ");
        sb.append(getDisplayName());
        sb.append(", ");
        sb.append(getName());
        sb.append(", ");
        sb.append(NetworkIfUtil.toStringHardwareAddress(getHardwareAddress()));
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
				.getNetworkAdapterProp(id);
        return prop.get(key);
    }
}
