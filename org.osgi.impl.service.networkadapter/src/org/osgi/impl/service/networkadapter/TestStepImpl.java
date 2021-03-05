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

import java.util.StringTokenizer;

import org.osgi.service.networkadapter.NetworkAddress;
import org.osgi.test.support.step.TestStep;

public class TestStepImpl implements TestStep {
    private static final String	SEPARATOR_COMMA	= ",";
    private static final String	SPACE			= " ";
    private static final String	EMPTY			= "";

    public String[] executeTestStep(String command, String[] parameters) {

        try {
            if (Commands.ADD_NETWORK_ADAPTER.equals(command)) {
                return addNetworkAdapter(parameters);

            } else if (Commands.ADD_NETWORK_ADDRESS.equals(command)) {
                return addNetworkAddress(parameters);

            } else if (Commands.MODIFY_NETWORK_ADDRESS.equals(command)) {
                modifyNetworkAddress(parameters);

            } else if (Commands.REMOVE_NETWORK_ADAPTER.equals(command)) {
                removeNetworkAdapter(parameters);

            } else if (Commands.REMOVE_NETWORK_ADDRESS.equals(command)) {
                removeNetworkAddress(parameters);

            } else {
                throw new IllegalArgumentException("The stepId is not supported.");
            }

        } catch (Exception e) {
            e.printStackTrace();

            if (e instanceof IllegalArgumentException) {
                throw (IllegalArgumentException)e;
            }

            throw new IllegalArgumentException(e.getMessage());
        }

        return null;
    }

    /**
     * The informations are as follows.<br>
     * <ul>
     * <li>index 0  : networkAdapter type
     * <li>index 1  : displayName (permits the null)
     * <li>index 2  : name
     * <li>index 3  : MAC address (Separator ":")
     * <li>index 4  : MTU
     * <li>index 5  : is loopback
     * <li>index 6  : is point to point
     * <li>index 7  : is up
     * <li>index 8  : is virtual
     * <li>index 9  : supports multicast
     * <li>index 10 : parent
     * <li>index 11 : sub interface (Separator ":") (permits the null)
     * <li>index 12 : IP address version (non-use)
     * <li>index 13 : IP address scope (non-use)
     * <li>index 14 : address (non-use)
     * <li>index 15 : subnetmask length (non-use)
     * </ul>
     *
     * @param type Add Adapter type
     * @return informations String Array
     */
    private String[] getAdapterInfo(String type) {
        String[] info = new String[16];

        if (type.equals("loopback")) {
            info[0] = "test.lb.network.adapter.type";
            info[1] = "test.lb.displayname";
            info[2] = "test.lb.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "true";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_LOOPBACK;
            info[14] = "127.0.0.1";
            info[15] = "8";

        } else if (type.equals("point-to-point")) {
            info[0] = "test.ptp.network.adapter.type";
            info[1] = "test.ptp.displayname";
            info[2] = "test.ptp.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "true";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
            info[14] = "192.168.1.20";
            info[15] = "24";

        } else if (type.equals("up")) {
            info[0] = "test.up.network.adapter.type";
            info[1] = "test.up.displayname";
            info[2] = "test.up.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
            info[14] = "192.168.1.30";
            info[15] = "24";

        } else if (type.equals("down")) {
            info[0] = "test.down.network.adapter.type";
            info[1] = "test.down.displayname";
            info[2] = "test.down.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "true";
            info[7] = "false";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
            info[14] = "192.168.1.30";
            info[15] = "24";

        } else if (type.equals("virtual")) {
            info[0] = "test.vir.network.adapter.type";
            info[1] = "test.vir.displayname";
            info[2] = "test.vir.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "true";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
            info[14] = "192.168.1.40";
            info[15] = "24";

        } else if (type.equals("multicast")) {
            info[0] = "test.mul.network.adapter.type";
            info[1] = "test.mul.displayname";
            info[2] = "test.mul.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "true";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
            info[14] = "192.168.1.50";
            info[15] = "24";

        } else if (type.equals("no-multicast")) {
            info[0] = "test.mul.network.adapter.type";
            info[1] = "test.mul.displayname";
            info[2] = "test.mul.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
            info[14] = "192.168.1.50";
            info[15] = "24";

        } else if (type.equals("no-address")) {
            info[0] = "test.network.adapter.type";
            info[1] = "test.displayname";
            info[2] = "test.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = null;
            info[13] = null;
            info[14] = null;
            info[15] = null;

        } else if (type.equals("ipv4-host")) {
            info[0] = "test.network.adapter.type";
            info[1] = "test.displayname";
            info[2] = "test.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_HOST;
            info[14] = "0.1.1.10";
            info[15] = "24";

        } else if (type.equals("ipv4-private-use")) {
            info[0] = "test.network.adapter.type";
            info[1] = "test.displayname";
            info[2] = "test.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
            info[14] = "10.1.1.10";
            info[15] = "24";

        } else if (type.equals("ipv4-shared")) {
            info[0] = "test.network.adapter.type";
            info[1] = "test.displayname";
            info[2] = "test.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_SHARED;
            info[14] = "100.64.1.10";
            info[15] = "24";

        } else if (type.equals("ipv4-loopback")) {
            info[0] = "test.network.adapter.type";
            info[1] = "test.displayname";
            info[2] = "test.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_LOOPBACK;
            info[14] = "127.0.0.1";
            info[15] = "32";

        } else if (type.equals("ipv4-linklocal")) {
            info[0] = "test.network.adapter.type";
            info[1] = "test.displayname";
            info[2] = "test.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_LINKLOCAL;
            info[14] = "169.254.1.10";
            info[15] = "24";

        } else if (type.equals("ipv4-global")) {
            info[0] = "test.network.adapter.type";
            info[1] = "test.displayname";
            info[2] = "test.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_4;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_GLOBAL;
            info[14] = "1.1.1.10";
            info[15] = "24";

        } else if (type.equals("ipv6-loopback")) {
            info[0] = "test.network.adapter.type";
            info[1] = "test.displayname";
            info[2] = "test.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_6;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_LOOPBACK;
            info[14] = "::1";
            info[15] = "128";

        } else if (type.equals("ipv6-unspecified")) {
            info[0] = "test.network.adapter.type";
            info[1] = "test.displayname";
            info[2] = "test.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_6;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_UNSPECIFIED;
            info[14] = "::";
            info[15] = "128";

        } else if (type.equals("ipv6-uniquelocal")) {
            info[0] = "test.network.adapter.type";
            info[1] = "test.displayname";
            info[2] = "test.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_6;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_UNIQUE_LOCAL;
            info[14] = "fec0::10";
            info[15] = "64";

        } else if (type.equals("ipv6-linkedscopedunicast")) {
            info[0] = "test.network.adapter.type";
            info[1] = "test.displayname";
            info[2] = "test.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_6;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_LINKED_SCOPED_UNICAST;
            info[14] = "fe80::10";
            info[15] = "64";

        } else if (type.equals("ipv6-global")) {
            info[0] = "test.network.adapter.type";
            info[1] = "test.displayname";
            info[2] = "test.name";
            info[3] = "aa:bb:cc:dd:ee:ff";
            info[4] = "1500";
            info[5] = "false";
            info[6] = "false";
            info[7] = "true";
            info[8] = "false";
            info[9] = "false";
            info[10] = "";
            info[11] = "";
            info[12] = NetworkAddress.IPADDRESS_VERSION_6;
            info[13] = NetworkAddress.IPADDRESS_SCOPE_GLOBAL;
            info[14] = "2001::10";
            info[15] = "64";

        } else {
            throw new IllegalArgumentException("unsupport type");
        }

        return info;
    }

    /**
     * The method that adds the network IF,IP address.
     * <br>
     * The parameters are as follows.<br>
     * <ul>
     * <li>index 0  : type
     * </ul>
     * Based on an appointed parameter, registers the NetworkAdapter service and NetworkAddress service.<br>
     *
     * @param parameters The parameter mentioned above
     * @return String Array {NetworkAdapter ID ,NetworkAddress ID}
     */
    private String[] addNetworkAdapter(String[] parameters) {

        String[] info = getAdapterInfo(parameters[0]);

        String networkAdapterId = NetworkIfUtil.getNetworkAdapterId(info[2], info[3]);
        NetworkIfData networkIfData = new NetworkIfData(info);
        String addressVersion = info[12];
        String addressScope = info[13];
        String address = info[14];
        int length = -1;
        if (info[15] != null && !EMPTY.equals(info[15])) {
            length = Integer.parseInt(info[15]);
        }

        String networkAddressId = null;
        if (addressVersion != null && addressScope != null &&  address != null || length != -1) {
            networkAddressId = NetworkIfUtil.getNetworkAddressId(networkAdapterId);
        }

        NetworkIfTracker.getInstance().addNetworkAdapter(networkAdapterId, networkIfData, networkAddressId, addressVersion, addressScope, address, length);

        return new String[]{networkAdapterId, networkAddressId};
    }

    /**
     * The informations are as follows.<br>
     * <ul>
     * <li>index 0 : NetworkAdapter/NetworkAddress ID
     * <li>index 1 : IP address version
     * <li>index 2 : IP address scope
     * <li>index 3 : address
     * <li>index 4 : subnetmask length
     * </ul>
     * @param type Add Address type
     * @return informations String Array
     */
    private String[] getAddressInfo(String[] parameters) {
        String[] info = new String[5];

        if (parameters[0].equals("ipv6-uniquelocal")) {
            info[0] = parameters[1];
            info[1] = NetworkAddress.IPADDRESS_VERSION_6;
            info[2] = NetworkAddress.IPADDRESS_SCOPE_UNIQUE_LOCAL;
            info[3] = "fec0::10";
            info[4] = "64";

        } else if (parameters[0].equals("ipv4-private-use_2")) {
            info[0] = parameters[1];
            info[1] = NetworkAddress.IPADDRESS_VERSION_4;
            info[2] = NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE;
            info[3] = "10.1.1.20";
            info[4] = "24";

        } else if (parameters[0].equals("ipv4-global")) {
            info[0] = parameters[1];
            info[1] = NetworkAddress.IPADDRESS_VERSION_4;
            info[2] = NetworkAddress.IPADDRESS_SCOPE_GLOBAL;
            info[3] = "1.1.1.10";
            info[4] = "24";

        } else {
            throw new IllegalArgumentException("unsupport type");
        }

        return info;
    }

    /**
     * The method that adds the IP address.
     * <br>
     * The parameters are as follows.<br>
     * <ul>
     * <li>index 0 : type
     * <li>index 1 : ID
     * </ul>
     * Based on an appointed parameter, registers the NetworkAddress service.<br>
     *
     * @param parameters The parameter mentioned above
     * @return String Array{NetworkAddress ID}
     */
    private String[] addNetworkAddress(String[] parameters) {

        String[] info = getAddressInfo(parameters);

        String networkAdapterId = info[0];
        String networkAddressId = NetworkIfUtil.getNetworkAddressId(networkAdapterId);

        String addressVersion = info[1];
        String addressScope = info[2];
        String address = info[3];
        int length = Integer.parseInt(info[4]);

        NetworkIfTracker.getInstance().addNetworkAddress(networkAdapterId, networkAddressId, addressVersion, addressScope, address, length);

        return new String[]{networkAddressId};
    }

    /**
     * The method that modifies the IP address.
     * <br>
     * The parameters are as follows.<br>
     * <ul>
     * <li>index 0 : type
     * <li>index 1 : ID
     * </ul>
     *
     * @param parameters The parameter mentioned above
     */
    private void modifyNetworkAddress(String[] parameters) {

        String[] info = getAddressInfo(parameters);

        String networkAddressId = info[0];
        String addressVersion = info[1];
        String addressScope = info[2];
        String address = info[3];
        int length = Integer.parseInt(info[4]);

        NetworkIfTracker.getInstance().modifiedNetworkAddress(networkAddressId, addressVersion, addressScope, address, length);
    }

    /**
     * The method that removes network IF.
     * <br>
     * The parameters are as follows.<br>
     * <ul>
     * <li>index 0 : NetworkAdapter ID
     * </ul>
     *
     * @param parameters The parameter mentioned above
     */
    private void removeNetworkAdapter(String[] parameters) {

        String networkAdapterId = parameters[0];

        NetworkIfTracker.getInstance().removedNetworkAdapter(networkAdapterId);
    }

    /**
     * The method that removes network IF and IP address.
     * <br>
     * The parameters are as follows.<br>
     * <ul>
     * <li>index 0 : NetworkAddress ID
     * </ul>
     *
     * @param parameters The parameter mentioned above
     */
    private void removeNetworkAddress(String[] parameters) {
        String networkAddressId = parameters[0];
        NetworkIfTracker.getInstance().removedNetworkAddress(networkAddressId);
    }

    @Override
	public String execute(String stepId, String userPrompt) {
        String command = stepId;
        userPrompt = userPrompt.substring(userPrompt.indexOf(SEPARATOR_COMMA));
        String[] parameters = toStringArray(userPrompt);
        String[] ids = executeTestStep(command, parameters);
        return toString(ids);
    }

    private String[] toStringArray(String str) {
        if (str == null || str.length() == 0) {
            return new String[0];
        }
        StringTokenizer tokenizer = new StringTokenizer(str, SEPARATOR_COMMA);
        String[] stringArray = new String[tokenizer.countTokens()];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = tokenizer.nextToken();
            if (SPACE.equals(stringArray[i])) {
                stringArray[i] = null;
            }
        }
        return stringArray;
    }

    private String toString(String[] stringArray) {
        if (stringArray == null || stringArray.length == 0){
            return new String();
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i] == null || EMPTY.equals(stringArray[i])) {
                buf.append(SPACE);
            } else {
                buf.append(stringArray[i]);
            }
                buf.append(SEPARATOR_COMMA);
        }
        return buf.toString();
    }
}
