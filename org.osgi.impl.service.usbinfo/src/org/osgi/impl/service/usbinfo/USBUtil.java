///*
// * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.osgi.impl.service.usbinfo;
//
//import java.util.StringTokenizer;
//
//class USBUtil {
//
//    private static final String SEPARATOR_COMMAS = ",";
//
//    static int[] toIntArray(String str) {
//
//        StringTokenizer tokenizer = new StringTokenizer(str, SEPARATOR_COMMAS);
//        int[] intArray = new int[tokenizer.countTokens()];
//
//        for (int i = 0; i < intArray.length; i++) {
//            intArray[i] = Integer.decode(tokenizer.nextToken()).intValue();
//        }
//
//        return intArray;
//    }
//
//    static String[] toStringArray(String str) {
//
//        StringTokenizer tokenizer = new StringTokenizer(str, SEPARATOR_COMMAS);
//        String[] stringArray = new String[tokenizer.countTokens()];
//
//        for (int i = 0; i < stringArray.length; i++) {
//            stringArray[i] = tokenizer.nextToken();
//        }
//
//        return stringArray;
//    }
//}
