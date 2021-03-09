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

package org.osgi.test.cases.dmt.tc2.tbc;

import org.osgi.service.dmt.DmtEvent;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.test.support.OSGiTestCaseProperties;

public class DmtConstants {

	public static final String	OSGi_ROOT			= OSGiTestCaseProperties
															.getProperty("org.osgi.service.dmt.osgi.root");

	public static final String OSGi_LOG = OSGi_ROOT + "/Log";
	
	public static final String UNICODE = "%&@#$©Å?";
	
	public static final String ACLSTR = "Add=*&Delete=*&Replace=*&Get=*";

	public static final String TITLE = "Title";

	public static final String PRINCIPAL = "www.cesar.org.br";

	public static final String PRINCIPAL_2 = "www.cin.ufpe.br";
	
	public static final String PRINCIPAL_3 = "www.motorola.com";

	public static final String MIMETYPE = "text/html";
	
	public static final int INVALID_LOCKMODE = 3;
	
	public static final int ALL_DMT_EVENTS = DmtEvent.ADDED | DmtEvent.COPIED | DmtEvent.DELETED | DmtEvent.RENAMED | DmtEvent.REPLACED | DmtEvent.SESSION_CLOSED | DmtEvent.SESSION_OPENED;
	
	public static final String ALL_ACTIONS = DmtPermission.ADD + "," + DmtPermission.DELETE + "," +DmtPermission.EXEC + "," + DmtPermission.GET + "," + DmtPermission.REPLACE;
	 
	public static final String ALL_NODES = "*";
    
	public static final String DDF = "http://www.openmobilealliance.org/tech/DTD/OMA-SyncML-DMDDF-V1_2_0.dtd";
	
	//Events constants
	public static final String	ADDED				= "org/osgi/service/dmt/DmtEvent/ADDED";

	public static final String	DELETED				= "org/osgi/service/dmt/DmtEvent/DELETED";

	public static final String	REPLACED			= "org/osgi/service/dmt/DmtEvent/REPLACED";

	public static final String	RENAMED				= "org/osgi/service/dmt/DmtEvent/RENAMED";

	public static final String	COPIED				= "org/osgi/service/dmt/DmtEvent/COPIED";

	public static final String	SESSION_OPENED		= "org/osgi/service/dmt/DmtEvent/SESSION_OPENED";
	
	public static final String	SESSION_CLOSED		= "org/osgi/service/dmt/DmtEvent/SESSION_CLOSED";

	public static final String SESSION_ID = "session.id";

	public static final String NODES = "nodes";

	public static final String NEWNODES = "newnodes";

	public static final String TOPIC = "event.topics";
	
	public static final int WAITING_TIME;

	public static final int TIMEOUT;
	
    public static final boolean SUPPORTS_NODE_TITLE;
    
    public static final boolean SUPPORTS_NODE_TIMESTAMP;
    
    public static final boolean SUPPORTS_NODE_SIZE;
    
    public static final boolean SUPPORTS_NODE_VERSION;
    
    public static final boolean SUPPORTS_ASYNCHRONOUS_NOTIFICATION;
    
    static {
		SUPPORTS_NODE_TITLE = OSGiTestCaseProperties.getBooleanProperty(
				"org.osgi.test.cases.dmt.tc2.supports_node_title", true);
		SUPPORTS_NODE_TIMESTAMP = OSGiTestCaseProperties.getBooleanProperty(
				"org.osgi.test.cases.dmt.tc2.supports_node_timestamp", true);
		SUPPORTS_NODE_SIZE = OSGiTestCaseProperties.getBooleanProperty(
				"org.osgi.test.cases.dmt.tc2.supports_node_size", true);
		SUPPORTS_NODE_VERSION = OSGiTestCaseProperties.getBooleanProperty(
				"org.osgi.test.cases.dmt.tc2.supports_node_version", true);
		TIMEOUT = OSGiTestCaseProperties.getIntegerProperty(
				"org.osgi.test.cases.dmt.tc2.timeout", 30000) + 1000;
		WAITING_TIME = OSGiTestCaseProperties.getIntegerProperty(
				"org.osgi.test.cases.dmt.tc2.wait_for_event", 1200);
    	
		SUPPORTS_ASYNCHRONOUS_NOTIFICATION = OSGiTestCaseProperties
				.getBooleanProperty(
										"org.osgi.test.cases.dmt.tc2.supports_asynchronous_notifications",
						true);
	}
	
    /**
     * This method returns the String corresponding to the code of the DmtExceptions's format
     * @param code The DmtException code format
     * @return The String corresponding to the code of the DmtException's format
     */
    public static String getDmtExceptionCodeText(int code) {
        switch(code) {
        case org.osgi.service.dmt.DmtException.NODE_NOT_FOUND:           
        	return "NODE_NOT_FOUND";
        case org.osgi.service.dmt.DmtException.COMMAND_NOT_ALLOWED:      
        	return "COMMAND_NOT_ALLOWED";
        case org.osgi.service.dmt.DmtException.FEATURE_NOT_SUPPORTED:    
        	return "FEATURE_NOT_SUPPORTED";
        case org.osgi.service.dmt.DmtException.URI_TOO_LONG:             
        	return "URI_TOO_LONG";
        case org.osgi.service.dmt.DmtException.NODE_ALREADY_EXISTS:      
        	return "NODE_ALREADY_EXISTS";
        case org.osgi.service.dmt.DmtException.PERMISSION_DENIED:        
        	return "PERMISSION_DENIED";
        case org.osgi.service.dmt.DmtException.COMMAND_FAILED:           
        	return "COMMAND_FAILED";
        case org.osgi.service.dmt.DmtException.DATA_STORE_FAILURE:       
        	return "DATA_STORE_FAILURE";
        case org.osgi.service.dmt.DmtException.ROLLBACK_FAILED:          
        	return "ROLLBACK_FAILED";
        case org.osgi.service.dmt.DmtException.REMOTE_ERROR:             
        	return "REMOTE_ERROR";
        case org.osgi.service.dmt.DmtException.METADATA_MISMATCH:        
        	return "METADATA_MISMATCH";
        case org.osgi.service.dmt.DmtException.INVALID_URI:             
        	return "INVALID_URI";
        case org.osgi.service.dmt.DmtException.CONCURRENT_ACCESS:       
        	return "CONCURRENT_ACCESS";
        case org.osgi.service.dmt.DmtException.ALERT_NOT_ROUTED:         
        	return "ALERT_NOT_ROUTED";
        case org.osgi.service.dmt.DmtException.TRANSACTION_ERROR:        
        	return "TRANSACTION_ERROR";
        case org.osgi.service.dmt.DmtException.SESSION_CREATION_TIMEOUT: 
        	return "SESSION_CREATION_TIMEOUT";
        }
        return null;
    }



	

}
