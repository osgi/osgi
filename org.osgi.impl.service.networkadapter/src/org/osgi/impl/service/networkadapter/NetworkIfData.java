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

class NetworkIfData {

    private String nwifType;
    private String displayName;
    private String name;
    private byte[] hardwareAddress;
    private int mtu;
    private boolean isLoopback;
    private boolean isPointToPoint;
    private boolean isUp;
    private boolean isVirtual;
    private boolean supportsMulticast;
    private String parent;
    private String[] subInterface;

    /**
     * Constructor.
     * <br>
     * The parameters are as follows.<br>
     * <ul>
     * <li>index 0  : networkAdapter type
     * <li>index 1  : displayName
     * <li>index 2  : name
     * <li>index 3  : MAC address (Separator ":")
     * <li>index 4  : MTU
     * <li>index 5  : is loopback
     * <li>index 6  : is point to point
     * <li>index 7  : is up
     * <li>index 8  : is virtual
     * <li>index 9  : supports multicast
     * <li>index 10 : parent
     * <li>index 11 : subInterface (Separator ":")
     * <li>index 12 : IP address version (non-use)
     * <li>index 13 : IP address scope (non-use)
     * <li>index 14 : address (non-use)
     * <li>index 15 : subnetmask length (non-use)
     * </ul>
     *
     * @param parameters The parameter mentioned above
     */
    NetworkIfData(String[] parameters) {

        nwifType = parameters[0];
        displayName = parameters[1];
        name = parameters[2];
        hardwareAddress = NetworkIfUtil.toByteArrayHardwareAddress(parameters[3]);
        mtu = Integer.parseInt(parameters[4]);
        isLoopback = Boolean.valueOf(parameters[5]).booleanValue();
        isPointToPoint = Boolean.valueOf(parameters[6]).booleanValue();
        isUp = Boolean.valueOf(parameters[7]).booleanValue();
        isVirtual = Boolean.valueOf(parameters[8]).booleanValue();
        supportsMulticast = Boolean.valueOf(parameters[9]).booleanValue();
        parent = parameters[10];
        subInterface = NetworkIfUtil.toStringArray(parameters[11]);
    }

    String getNwIfType() {
        return nwifType;
    }

    String getDisplayName() {
        return displayName;
    }

    String getName() {
        return name;
    }

    byte[] getHardwareAddress() {
        return hardwareAddress;
    }

    int getMTU() {
        return mtu;
    }

    boolean isLoopback() {
        return isLoopback;
    }

    boolean isPointToPoint() {
        return isPointToPoint;
    }

    boolean isUp() {
        return isUp;
    }

    boolean isVirtual() {
        return isVirtual;
    }

    boolean supportsMulticast() {
        return supportsMulticast;
    }

    public String getParent() {
        return parent;
    }

    public String[] getSubInterface() {
        return subInterface;
    }
}
