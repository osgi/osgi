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

import java.net.InetAddress;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.StringTokenizer;

class NetworkIfUtil {

    /**
     * The number of the tokens when divided an MAC address in ":"(colon).
     */
    private static final int MACADDRESS_TOKEN_COUNT = 6;

    /**
     * The length of the tokens when divided an MAC address in ":"(colon).
     */
    private static final int MACADDRESS_TOKEN_LENGTH = 2;

    /**
     * The fixed number to express a hex digit.
     */
    private static final int HEX_NUMBER = 16;

    /**
     * Separator ":"(colon).
     */
    private static final String SEPARATOR_COLON = ":";

    /**
     * Separator ":"(at mark).
     */
    private static final String SEPARATOR_AT = "@";

    /**
     * The number of times to shift at the time of MAC address generation.
     */
    private static final int SHIFT_COUNT = 4;

    /**
     * The index for NetworkAddress ID.
     */
    private static int NETWORKADDRESS_INDEX = 0;

    /**
     * The method to get NetworkAdapter ID.
     * <br>
     * @param name Network IF name
     * @param macAddress MAC address character string
     * @return NetworkAdapter ID
     */
    static String getNetworkAdapterId(String name, String macAddress) {
        return name + SEPARATOR_AT + macAddress;
    }

    /**
     * The method to get NetworkAddress ID.
     * <br>
     * @param networkAdapterId NetworkAdapter ID
     * @return NetworkAddress ID
     */
    static String getNetworkAddressId(String networkAdapterId) {

        NETWORKADDRESS_INDEX++;
        return networkAdapterId + SEPARATOR_AT + NETWORKADDRESS_INDEX;
    }

    /**
     * The method to get InetAddress than IP address character string.
     * <br>
     * @param address IP address character string
     * @return IP address
     */
    static InetAddress getInetAddress(final String address) {

        InetAddress inetAddress = null;
        try {
			inetAddress = AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress>() {
                @Override
				public InetAddress run() throws Exception {
                    return InetAddress.getByName(address);
                }
            });
        } catch (PrivilegedActionException e) {
            new IllegalArgumentException(e.getException().getMessage());
        }

        return inetAddress;
    }

    /**
     * The method to convert byte array of the MAC address into character string expression.
     * <br>
     * @param mac Byte array of the MAC address
     * @return Character string of the MAC address
     */
    static String toStringHardwareAddress(byte[] mac) {

        StringBuffer strbuf = new StringBuffer();
        for (int i = 0; i < mac.length; i++) {
            int bt = mac[i] & 0xff;

            if (bt < 0x10) {
                strbuf.append("0");
            }

            strbuf.append(Integer.toHexString(bt));
            strbuf.append(":");
        }

        String macStr = strbuf.toString();
        macStr = macStr.substring(0, macStr.length() - 1).toUpperCase();

        return macStr;
    }

    /**
     * The method to convert character string of the MAC address into byte array expression.
     * <br>
     * @param mac Character string of the MAC address
     * @return Byte array of the MAC address
     */
    static byte[] toByteArrayHardwareAddress(String mac) {

        byte[] macByteArray = null;
        try {
            StringTokenizer tokenizer = new StringTokenizer(mac, SEPARATOR_COLON);

            // When number of partitions is not 6, throws of an exception.
            if (tokenizer.countTokens() != MACADDRESS_TOKEN_COUNT) {
                throw new IllegalArgumentException("The number of colons must be 6.");
            }

            macByteArray = new byte[MACADDRESS_TOKEN_COUNT];

            for (int byteIndex = 0; byteIndex < MACADDRESS_TOKEN_COUNT; byteIndex++) {
                // Gets divided character string with a char type.
                char[] hexDigit = tokenizer.nextToken().toCharArray();

                // When a number of element of the sequence of hexDigit is not 2, throws of an exception.
                if (hexDigit.length != MACADDRESS_TOKEN_LENGTH) {
                    throw new IllegalArgumentException(
                            "Number of array elements must be 2. <hexDigit.length=" + hexDigit.length + ">");
                }

                // Converts it into a hex digit.
                int hex1 = Character.digit(hexDigit[0], HEX_NUMBER);
                int hex2 = Character.digit(hexDigit[1], HEX_NUMBER);

                // When cannot convert it into a hex digit, throws of an exception.
                if ((hex1 == -1) || (hex2 == -1)) {
                    throw new IllegalArgumentException(
                            "can not be converted to hexadecimal. <hex1=" + hex1 + ", hex2=" + hex2 + ">");
                }
                // 4 bits shifts to the left.
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

    /**
     * The method to convert character string of the separator ":"(colon) into String array expression.
     * <br>
     * @param str Character string of the separator ":"(colon)
     * @return String array
     */
    static String[] toStringArray(String str) {

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
