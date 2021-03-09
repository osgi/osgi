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
package org.osgi.test.cases.networkadapter.util;

import java.util.StringTokenizer;

public class NetworkIfTestUtil {

    private static final int MACADDRESS_TOKEN_COUNT = 6;
    private static final int MACADDRESS_TOKEN_LENGTH = 2;
    private static final int HEX_NUMBER = 16;
    private static final String SEPARATOR_COLON = ":";
    private static final int SHIFT_COUNT = 4;

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
