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
package org.osgi.test.cases.networkadapter.util;

import java.util.StringTokenizer;

public class NetworkIfTestUtil {

    private static final int MACADDRESS_TOKEN_COUNT = 6;
    private static final int MACADDRESS_TOKEN_LENGTH = 2;
    private static final int HEX_NUMBER = 16;
    private static final String SEPARATOR_COLON = ":";
    private static final int SHIFT_COUNT = 4;

    public static final String PROP_NETWORK_ADAPTER_TYPE = "network.adapter.type";
    public static final String PROP_DISPLAYNAME = "displayname";
    public static final String PROP_NAME = "name";
    public static final String PROP_HARDWAREADDRESS = "hardwareaddress";
    public static final String PROP_MTU = "mtu";
    public static final String PROP_ISLOOPBACK = "is.loopback";
    public static final String PROP_ISPOINTTOPOINT = "is.point.to.point";
    public static final String PROP_ISUP = "is.up";
    public static final String PROP_ISVIRTUAL = "is.virtual";
    public static final String PROP_SUPPORTSMULTICAST = "supports.multicast";
    public static final String PROP_PARENT = "parent";
    public static final String PROP_SUBINTERFACE = "sub.interface";

    public static final String PROP_HOST_IPADDRESS_4 = "host.ipaddress.4";
    public static final String PROP_HOST_MASKLENGTH_4 = "host.masklength.4";

    public static final String PROP_PRIVATE_IPADDRESS_4 = "private.ipaddress.4";
    public static final String PROP_PRIVATE_MASKLENGTH_4 = "private.masklength.4";
    public static final String PROP_PRIVATE_IPADDRESS_4_1 = "private.ipaddress.4.1";
    public static final String PROP_PRIVATE_MASKLENGTH_4_1 = "private.masklength.4.1";

    public static final String PROP_SHARED_IPADDRESS_4 = "shared.ipaddress.4";
    public static final String PROP_SHARED_MASKLENGTH_4 = "shared.masklength.4";

    public static final String PROP_LOOPBACK_IPADDRESS_4 = "loopback.ipaddress.4";
    public static final String PROP_LOOPBACK_MASKLENGTH_4 = "loopback.masklength.4";

    public static final String PROP_LINKLOCAL_IPADDRESS_4 = "linklocal.ipaddress.4";
    public static final String PROP_LINKLOCAL_MASKLENGTH_4 = "linklocal.masklength.4";

    public static final String PROP_GLOBAL_IPADDRESS_4 = "global.ipaddress.4";
    public static final String PROP_GLOBAL_MASKLENGTH_4 = "global.masklength.4";

    public static final String PROP_LOOPBACK_IPADDRESS_6 = "loopback.ipaddress.6";
    public static final String PROP_LOOPBACK_MASKLENGTH_6 = "loopback.masklength.6";

    public static final String PROP_UNSPECIFIED_IPADDRESS_6 = "unspecified.ipaddress.6";
    public static final String PROP_UNSPECIFIED_MASKLENGTH_6 = "unspecified.masklength.6";

    public static final String PROP_UNIQUELOCAL_IPADDRESS_6 = "uniquelocal.ipaddress.6";
    public static final String PROP_UNIQUELOCAL_MASKLENGTH_6 = "uniquelocal.masklength.6";

    public static final String PROP_LINKEDSCOPEDUNICAST_IPADDRESS_6 = "linkedscopedunicast.ipaddress.6";
    public static final String PROP_LINKEDSCOPEDUNICAST_MASKLENGTH_6 = "linkedscopedunicast.masklength.6";

    public static final String PROP_GLOBAL_IPADDRESS_6 = "global.ipaddress.6";
    public static final String PROP_GLOBAL_MASKLENGTH_6 = "global.masklength.6";



    // for loopback interface.
    public static final String PROP_LB_NETWORK_ADAPTER_TYPE = "lb.network.adapter.type";
    public static final String PROP_LB_DISPLAYNAME = "lb.displayname";
    public static final String PROP_LB_NAME = "lb.name";
    public static final String PROP_LB_HARDWAREADDRESS = "lb.hardwareaddress";
    public static final String PROP_LB_MTU = "lb.mtu";
    public static final String PROP_LB_ISUP = "lb.is.up";
    public static final String PROP_LB_ISVIRTUAL = "lb.is.virtual";
    public static final String PROP_LB_SUPPORTSMULTICAST = "lb.supports.multicast";
    public static final String PROP_LB_PARENT = "lb.parent";
    public static final String PROP_LB_SUBINTERFACE = "lb.sub.interface";
    public static final String PROP_LB_IPADDRESS_VERSION = "lb.ipaddress.version";
    public static final String PROP_LB_IPADDRESS_SCOPE = "lb.ipaddress.scope";
    public static final String PROP_LB_IPADDRESS = "lb.ipaddress";
    public static final String PROP_LB_MASKLENGTH = "lb.masklength";

    // for point to point interface
    public static final String PROP_PTP_NETWORK_ADAPTER_TYPE = "ptp.network.adapter.type";
    public static final String PROP_PTP_DISPLAYNAME = "ptp.displayname";
    public static final String PROP_PTP_NAME = "ptp.name";
    public static final String PROP_PTP_HARDWAREADDRESS = "ptp.hardwareaddress";
    public static final String PROP_PTP_MTU = "ptp.mtu";
    public static final String PROP_PTP_ISUP = "ptp.is.up";
    public static final String PROP_PTP_ISVIRTUAL = "ptp.is.virtual";
    public static final String PROP_PTP_SUPPORTSMULTICAST = "ptp.supports.multicast";
    public static final String PROP_PTP_PARENT = "ptp.parent";
    public static final String PROP_PTP_SUBINTERFACE = "ptp.sub.interface";
    public static final String PROP_PTP_IPADDRESS_VERSION = "ptp.ipaddress.version";
    public static final String PROP_PTP_IPADDRESS_SCOPE = "ptp.ipaddress.scope";
    public static final String PROP_PTP_IPADDRESS = "ptp.ipaddress";
    public static final String PROP_PTP_MASKLENGTH = "ptp.masklength";

    // for the interface of the start/not start state.
    public static final String PROP_UP_NETWORK_ADAPTER_TYPE = "up.network.adapter.type";
    public static final String PROP_UP_DISPLAYNAME = "up.displayname";
    public static final String PROP_UP_NAME = "up.name";
    public static final String PROP_UP_HARDWAREADDRESS = "up.hardwareaddress";
    public static final String PROP_UP_MTU = "up.mtu";
    public static final String PROP_UP_ISLOOPBACK = "up.is.loopback";
    public static final String PROP_UP_ISPOINTTOPOINT = "up.is.point.to.point";
    public static final String PROP_UP_ISVIRTUAL = "up.is.virtual";
    public static final String PROP_UP_SUPPORTSMULTICAST = "up.supports.multicast";
    public static final String PROP_UP_PARENT = "up.parent";
    public static final String PROP_UP_SUBINTERFACE = "up.sub.interface";
    public static final String PROP_UP_IPADDRESS_VERSION = "up.ipaddress.version";
    public static final String PROP_UP_IPADDRESS_SCOPE = "up.ipaddress.scope";
    public static final String PROP_UP_IPADDRESS = "up.ipaddress";
    public static final String PROP_UP_MASKLENGTH = "up.masklength";

    // for virtual interface
    public static final String PROP_VIR_NETWORK_ADAPTER_TYPE = "vir.network.adapter.type";
    public static final String PROP_VIR_DISPLAYNAME = "vir.displayname";
    public static final String PROP_VIR_NAME = "vir.name";
    public static final String PROP_VIR_HARDWAREADDRESS = "vir.hardwareaddress";
    public static final String PROP_VIR_MTU = "vir.mtu";
    public static final String PROP_VIR_ISLOOPBACK = "vir.is.loopback";
    public static final String PROP_VIR_ISPOINTTOPOINT = "vir.is.point.to.point";
    public static final String PROP_VIR_ISUP = "vir.is.up";
    public static final String PROP_VIR_SUPPORTSMULTICAST = "vir.supports.multicast";
    public static final String PROP_VIR_PARENT = "vir.parent";
    public static final String PROP_VIR_SUBINTERFACE = "vir.sub.interface";
    public static final String PROP_VIR_IPADDRESS_VERSION = "vir.ipaddress.version";
    public static final String PROP_VIR_IPADDRESS_SCOPE = "vir.ipaddress.scope";
    public static final String PROP_VIR_IPADDRESS = "vir.ipaddress";
    public static final String PROP_VIR_MASKLENGTH = "vir.masklength";

    // for the interface supporting/not supporting multicast
    public static final String PROP_MUL_NETWORK_ADAPTER_TYPE = "mul.network.adapter.type";
    public static final String PROP_MUL_DISPLAYNAME = "mul.displayname";
    public static final String PROP_MUL_NAME = "mul.name";
    public static final String PROP_MUL_HARDWAREADDRESS = "mul.hardwareaddress";
    public static final String PROP_MUL_MTU = "mul.mtu";
    public static final String PROP_MUL_ISLOOPBACK = "mul.is.loopback";
    public static final String PROP_MUL_ISPOINTTOPOINT = "mul.is.point.to.point";
    public static final String PROP_MUL_ISUP = "mul.is.up";
    public static final String PROP_MUL_ISVIRTUAL = "mul.is.virtual";
    public static final String PROP_MUL_PARENT = "mul.parent";
    public static final String PROP_MUL_SUBINTERFACE = "mul.sub.interface";
    public static final String PROP_MUL_IPADDRESS_VERSION = "mul.ipaddress.version";
    public static final String PROP_MUL_IPADDRESS_SCOPE = "mul.ipaddress.scope";
    public static final String PROP_MUL_IPADDRESS = "mul.ipaddress";
    public static final String PROP_MUL_MASKLENGTH = "mul.masklength";

    public static byte[] toByteArrayMac(String mac) {

        byte[] macByteArray = null;
        try {
            StringTokenizer tokenizer = new StringTokenizer(mac, SEPARATOR_COLON);

            if (tokenizer.countTokens() != MACADDRESS_TOKEN_COUNT) {
                throw new IllegalArgumentException("The number of colons must be 6.");
            }

            macByteArray = new byte[MACADDRESS_TOKEN_COUNT];

            for (int byteIndex = 0; byteIndex < MACADDRESS_TOKEN_COUNT; byteIndex++) {
                char[] hexDigit = tokenizer.nextToken().toCharArray();

                if (hexDigit.length != MACADDRESS_TOKEN_LENGTH) {
                    throw new IllegalArgumentException(
                            "Number of array elements must be 2. <hexDigit.length=" + hexDigit.length + ">");
                }

                int hex1 = Character.digit(hexDigit[0], HEX_NUMBER);
                int hex2 = Character.digit(hexDigit[1], HEX_NUMBER);

                if ((hex1 == -1) || (hex2 == -1)) {
                    throw new IllegalArgumentException(
                            "can not be converted to hexadecimal. <hex1=" + hex1 + ", hex2=" + hex2 + ">");
                }

                hex1 = hex1 << SHIFT_COUNT;
                int hex = hex1 | hex2;
                macByteArray[byteIndex] = (byte) hex;
            }

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw (IllegalArgumentException)e;
            }
            throw new IllegalArgumentException(
                    "value must be format of the MACAddress. <macAddressString=" + mac + ">");
        }
        return macByteArray;
    }

    public static String[] toStringArray(String str) {

        if (str == null || str.length() == 0) {
            return new String[0];
        }

        StringTokenizer tokenizer = new StringTokenizer(str, SEPARATOR_COLON);
        String[] subInterfaceArray = new String[tokenizer.countTokens()];

        for (int i = 0; i < subInterfaceArray.length; i++) {
            subInterfaceArray[i] = tokenizer.nextToken();
        }

        return subInterfaceArray;
    }
}
