/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 * 
 */

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

package org.osgi.test.cases.dmt.tc1.tbc;

import info.dmtree.DmtData;
import info.dmtree.security.DmtPermission;

public class DmtConstants {

	public static final String OSGi_ROOT = System.getProperty("info.dmtree.osgi.root");

	public static final String OSGi_LOG = OSGi_ROOT + "/Log";
	
	public static final String OSGi_CONFIGURATION = OSGi_ROOT + "/Configuration";
	
	public static final String INVALID = "%&@#&$#";
	
	public static final String TITLE = "Title";

	public static final String PRINCIPAL = "www.cesar.org.br";

	public static final String PRINCIPAL_2 = "www.cin.ufpe.br";
	
	public static final String PRINCIPAL_3 = "www.motorola.com";

	public static final String MIMETYPE = "text/html";
	
	public static final String MESSAGE = "Default message";
	
	public static final String ACTIONS = DmtPermission.GET + "," +DmtPermission.REPLACE+","+DmtPermission.ADD;
	
	public static final String DIFFERENT_ACTIONS = DmtPermission.ADD + "," + DmtPermission.EXEC + "," +DmtPermission.GET; 
	
	public static final String ALL_ACTIONS = DmtPermission.ADD + "," + DmtPermission.DELETE + "," +DmtPermission.EXEC + "," + DmtPermission.GET + "," + DmtPermission.REPLACE;
	 
	public static final String REMOTE_SERVER = "remoteServer";


	/**
	 * This method returns the String of the corresponding get to the DmtData format. 
	 * E.g: FORMAT_XML returns getXml()
	 * 
	 * @param code The DmtData format code
	 * @return It returns the String of the corresponding get to the DmtData format. 
	 */
    public static String getExpectedDmtDataMethod(int code) {
    	switch (code) {
    	case DmtData.FORMAT_BASE64:
    		return "getBase64()";
    	
    	case DmtData.FORMAT_BINARY:
    		return "getBinary()";
    		
    	case DmtData.FORMAT_BOOLEAN:
    	    return "getBoolean()";
    		
    	case DmtData.FORMAT_DATE:
    		return "getDate()";
    		
    	case DmtData.FORMAT_FLOAT:
    		return "getFloat()";
    		
    	case DmtData.FORMAT_INTEGER:
    		return "getInt()";
    		
    	case DmtData.FORMAT_STRING:
    		return "getString()";
    		
    	case DmtData.FORMAT_TIME:
    		return "getTime()";
    		
    	case DmtData.FORMAT_XML:
    		return "getXml()";
    		
    	case DmtData.FORMAT_RAW_BINARY:
    		return "getRawBinary()";
    		
    	case DmtData.FORMAT_RAW_STRING:
    		return "getRawString()";    		
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
        case DmtData.FORMAT_RAW_BINARY:           
        	return "FORMAT_RAW_BINARY";
        case DmtData.FORMAT_RAW_STRING:           
        	return "FORMAT_RAW_STRING";
        }
        return null;
    }
    /**
     * This method creates a default DmtData, just to make tests easier 
     * @param format The DmtData format
     * @param differentValue If we need a different value of the default, this flag must be 'true'
     * @return A DmtData with a specified format
     */
    public static DmtData getDmtData(int format,boolean differentValue) {
    	switch (format) {
    		case DmtData.FORMAT_BASE64:
    			if (differentValue) { 
    				return new DmtData(new byte[] {2} ,true);
    			}
    			return new DmtData(new byte[] {1} ,true);
    
    		case DmtData.FORMAT_BINARY:
    			if (differentValue) {
    				return new DmtData(new byte[] {2});
    			}
    			return new DmtData(new byte[] {1});
    			
    		case DmtData.FORMAT_BOOLEAN:
    			if (differentValue) {	
    				return new DmtData(false);
    			
    			} 
    			return new DmtData(true);
    			
    		case DmtData.FORMAT_DATE:
    			if (differentValue) {
    				return new DmtData("19811002",DmtData.FORMAT_DATE);
    			} 
    			return new DmtData("19811001",DmtData.FORMAT_DATE);
    			
    		case DmtData.FORMAT_FLOAT:
    			if (differentValue) {
    				return new DmtData(1.11F);
    			} 
    			return new DmtData(1.1F);
    			
    		case DmtData.FORMAT_INTEGER:
    			if (differentValue) {
    				return new DmtData(2);
    			} 
    			return new DmtData(1);
    			
    		case DmtData.FORMAT_NULL:
    			return DmtData.NULL_VALUE;
    			
    		case DmtData.FORMAT_STRING:
    			if (differentValue) {
    				return new DmtData("TestString ");//Extra space
    			} 
    			return new DmtData("TestString");
    			
    		case DmtData.FORMAT_TIME:
    			if (differentValue) {
    				return new DmtData("120001",DmtData.FORMAT_TIME);	
    			} 
    			return new DmtData("120000",DmtData.FORMAT_TIME);
    
    		case DmtData.FORMAT_XML:
    			if (differentValue) {
    				return new DmtData("TestString ",DmtData.FORMAT_XML);
    			} 
    			return new DmtData("TestString",DmtData.FORMAT_XML);
    		case DmtData.FORMAT_RAW_BINARY:
    			if (differentValue) {
    				return new DmtData("format1",new byte[0]);
    			} 
    			return new DmtData("format2",new byte[0]);  
    			
    		case DmtData.FORMAT_RAW_STRING:
    			if (differentValue) {
    				return new DmtData("format1","data");
    			} 
    			return new DmtData("format2","data");    	    			
    	}
    	return null;
    }
    /**
     * This method creates a default DmtData, just to make tests easier 
     * @param format The DmtData format
     * @return A DmtData with a specified format
     */
    public static DmtData getDmtData(int format) {
    	return getDmtData(format,false);
    }
    
}
