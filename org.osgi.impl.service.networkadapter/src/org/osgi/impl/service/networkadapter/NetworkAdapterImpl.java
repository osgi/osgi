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

public class NetworkAdapterImpl implements NetworkAdapter {

    private String id;

    private int mtu;

    NetworkAdapterImpl(String id, int mtu) {

        this.id = id;
        this.mtu = mtu;
    }

    public String getNetworkAdapterType() {
        return (String)getProperty(NETWORKADAPTER_TYPE);
    }

    public String getDisplayName() {
        return (String)getProperty(NETWORKADAPTER_DISPLAYNAME);
    }

    public String getName() {
        return (String)getProperty(NETWORKADAPTER_NAME);
    }

    public byte[] getHardwareAddress() {
        return (byte[])getProperty(NETWORKADAPTER_HARDWAREADDRESS);
    }

    public int getMTU() throws SocketException {
        return mtu;
    }

    public boolean isLoopback() throws SocketException {
        return ((Boolean)getProperty(NETWORKADAPTER_IS_LOOPBACK)).booleanValue();
    }

    public boolean isPointToPoint() throws SocketException {
        return ((Boolean)getProperty(NETWORKADAPTER_IS_POINTTOPOINT)).booleanValue();
    }

    public boolean isUp() throws SocketException {
        return ((Boolean)getProperty(NETWORKADAPTER_IS_UP)).booleanValue();
    }

    public boolean isVirtual() {
        return ((Boolean)getProperty(NETWORKADAPTER_IS_VIRTUAL)).booleanValue();
    }

    public boolean supportsMulticast() throws SocketException {
        return ((Boolean)getProperty(NETWORKADAPTER_SUPPORTS_MULTICAST)).booleanValue();
    }

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

    private Object getProperty(String key) {

        Dictionary prop = NetworkIfManager.getInstance().getNetworkAdapterProp(id);
        return prop.get(key);
    }
}
