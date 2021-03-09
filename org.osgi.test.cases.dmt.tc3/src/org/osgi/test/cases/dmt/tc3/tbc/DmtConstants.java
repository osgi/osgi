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

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Aug 03, 2005  Luiz Felipe Guimaraes
 * 163           [MEGTCK][DMT] Move Test Control Constants to a diferent class
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc;

import org.osgi.service.dmt.DmtData;
import org.osgi.test.support.OSGiTestCaseProperties;

public class DmtConstants  {

	public static String TEMPORARY = "";
	
	public static String PARAMETER_2 = "";
	
	public static String PARAMETER_3 = "";
	
	public static final String	OSGi_ROOT				= OSGiTestCaseProperties
																.getProperty("org.osgi.service.dmt.osgi.root");

	public static final String OSGi_LOG = OSGi_ROOT + "/Log";
	
	public static final String OSGi_CFG = OSGi_ROOT + "/Configuration";
	
	public static final String XMLSTR = "<?xml version=\"1.0\"?><data><name>data name</name><value>data value</value></data>";

	public static final String LOG_SEARCH = OSGi_LOG + "/search";

	public static final String LOG_SEARCH_MAXRECORDS = LOG_SEARCH + "/MaxRecords";

	public static final String LOG_SEARCH_FILTER = LOG_SEARCH + "/Filter";

	public static final String LOG_SEARCH_EXCLUDE = LOG_SEARCH + "/Exclude";

	public static final String LOG_SEARCH_RESULT = LOG_SEARCH + "/LogResult";
	
	public static final String DDF = "http://www.openmobilealliance.org/tech/DTD/OMA-SyncML-DMDDF-V1_2_0.dtd";
	
    /**
     * This method creates a default DmtData, just to make tests easier 
     * @param format The DmtData format
     * @return A DmtData with a specified format
     */
    public static DmtData getDmtData(int format) {
        switch (format) {
            case DmtData.FORMAT_BASE64:
                return new DmtData(new byte[] {1} ,true);
            case DmtData.FORMAT_BINARY:
                return new DmtData(new byte[] {1});
            case DmtData.FORMAT_BOOLEAN:
                return new DmtData(true);
            case DmtData.FORMAT_DATE:
                return new DmtData("19811001",DmtData.FORMAT_DATE);
            case DmtData.FORMAT_FLOAT:
                return new DmtData(1.1F);
            case DmtData.FORMAT_INTEGER:
                return new DmtData(1);
            case DmtData.FORMAT_NULL:
                return DmtData.NULL_VALUE;
            case DmtData.FORMAT_STRING:
                return new DmtData("TestString");
            case DmtData.FORMAT_TIME:
                return new DmtData("120000",DmtData.FORMAT_TIME);
            case DmtData.FORMAT_XML:
                return new DmtData("TestString",DmtData.FORMAT_XML);
        }
        return null;
    }
    
    /**
     * This method returns the String corresponding to the code of the DmtData's format
     * @param code The DmtData code format
     * @return The String corresponding to the code of the DmtData's format
     */
    public static String getDmtDataCodeText(int code) {
        switch(code) {
        case DmtData.FORMAT_BASE64:           
            return "FORMAT_BASE64";
        case DmtData.FORMAT_BINARY:           
            return "FORMAT_BINARY";
        case DmtData.FORMAT_BOOLEAN:           
            return "FORMAT_BOOLEAN";
        case DmtData.FORMAT_DATE:           
            return "FORMAT_DATE";
        case DmtData.FORMAT_FLOAT:           
            return "FORMAT_FLOAT";
        case DmtData.FORMAT_INTEGER:           
            return "FORMAT_INTEGER";
        case DmtData.FORMAT_NODE:           
            return "FORMAT_NODE";
        case DmtData.FORMAT_NULL:           
            return "FORMAT_NULL";
        case DmtData.FORMAT_STRING:           
            return "FORMAT_STRING";
        case DmtData.FORMAT_TIME:           
            return "FORMAT_TIME";
        case DmtData.FORMAT_XML:           
            return "FORMAT_XML";
        }
        return null;
    }
}
