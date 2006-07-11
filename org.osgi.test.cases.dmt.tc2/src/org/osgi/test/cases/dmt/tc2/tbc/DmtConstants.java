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

package org.osgi.test.cases.dmt.tc2.tbc;

import info.dmtree.DmtEvent;
import info.dmtree.security.DmtPermission;

public class DmtConstants {

	public static final String OSGi_ROOT = System.getProperty("info.dmtree.osgi.root");

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
	public static final String ADDED = "info/dmtree/DmtEvent/ADDED";

	public static final String DELETED = "info/dmtree/DmtEvent/DELETED";

	public static final String REPLACED = "info/dmtree/DmtEvent/REPLACED";

	public static final String RENAMED = "info/dmtree/DmtEvent/RENAMED";

	public static final String COPIED = "info/dmtree/DmtEvent/COPIED";

	public static final String SESSION_OPENED = "info/dmtree/DmtEvent/SESSION_OPENED";
	
	public static final String SESSION_CLOSED = "info/dmtree/DmtEvent/SESSION_CLOSED";

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
    	if (System.getProperty("org.osgi.test.cases.dmt.tc2.supports_node_title")!=null) {
    		SUPPORTS_NODE_TITLE = Boolean.getBoolean("org.osgi.test.cases.dmt.tc2.supports_node_title");
    	} else {
    		SUPPORTS_NODE_TITLE = true;
    	}
    	if (System.getProperty("org.osgi.test.cases.dmt.tc2.supports_node_timestamp")!=null) {
    		SUPPORTS_NODE_TIMESTAMP = Boolean.getBoolean("org.osgi.test.cases.dmt.tc2.supports_node_timestamp");
    	} else {
    		SUPPORTS_NODE_TIMESTAMP = true;
    	}
    	if (System.getProperty("org.osgi.test.cases.dmt.tc2.supports_node_size")!=null) {
    		SUPPORTS_NODE_SIZE= Boolean.getBoolean("org.osgi.test.cases.dmt.tc2.supports_node_size");
    	} else {
    		SUPPORTS_NODE_SIZE= true;
    	}
    	if (System.getProperty("org.osgi.test.cases.dmt.tc2.supports_node_version")!=null) {
    		SUPPORTS_NODE_VERSION = Boolean.getBoolean("org.osgi.test.cases.dmt.tc2.supports_node_version");
    	} else {
    		SUPPORTS_NODE_VERSION = true;
    	}
    	if (System.getProperty("org.osgi.test.cases.dmt.tc2.timeout")!=null) {
    		TIMEOUT = Integer.parseInt(System.getProperty("org.osgi.test.cases.dmt.tc2.timeout")) + 1000;
    	} else {
    		TIMEOUT = 31000;
    	}
    	if (System.getProperty("org.osgi.test.cases.dmt.tc2.wait_for_event")!=null) {
    		WAITING_TIME = Integer.parseInt(System.getProperty("org.osgi.test.cases.dmt.tc2.wait_for_event"));
    	} else {
    		WAITING_TIME = 1200;
    	}
    	
    	if (System.getProperty("org.osgi.test.cases.dmt.tc2.supports_asynchronous_notifications")!=null) {
    		SUPPORTS_ASYNCHRONOUS_NOTIFICATION = Boolean.getBoolean("org.osgi.test.cases.dmt.tc2.supports_node_version");
    	} else {
    		SUPPORTS_ASYNCHRONOUS_NOTIFICATION = true;
    	}
    	
    }
	
    /**
     * This method returns the String corresponding to the code of the DmtExceptions's format
     * @param code The DmtException code format
     * @return The String corresponding to the code of the DmtException's format
     */
    public static String getDmtExceptionCodeText(int code) {
        switch(code) {
        case info.dmtree.DmtException.NODE_NOT_FOUND:           
        	return "NODE_NOT_FOUND";
        case info.dmtree.DmtException.COMMAND_NOT_ALLOWED:      
        	return "COMMAND_NOT_ALLOWED";
        case info.dmtree.DmtException.FEATURE_NOT_SUPPORTED:    
        	return "FEATURE_NOT_SUPPORTED";
        case info.dmtree.DmtException.URI_TOO_LONG:             
        	return "URI_TOO_LONG";
        case info.dmtree.DmtException.NODE_ALREADY_EXISTS:      
        	return "NODE_ALREADY_EXISTS";
        case info.dmtree.DmtException.PERMISSION_DENIED:        
        	return "PERMISSION_DENIED";
        case info.dmtree.DmtException.COMMAND_FAILED:           
        	return "COMMAND_FAILED";
        case info.dmtree.DmtException.DATA_STORE_FAILURE:       
        	return "DATA_STORE_FAILURE";
        case info.dmtree.DmtException.ROLLBACK_FAILED:          
        	return "ROLLBACK_FAILED";
        case info.dmtree.DmtException.REMOTE_ERROR:             
        	return "REMOTE_ERROR";
        case info.dmtree.DmtException.METADATA_MISMATCH:        
        	return "METADATA_MISMATCH";
        case info.dmtree.DmtException.INVALID_URI:             
        	return "INVALID_URI";
        case info.dmtree.DmtException.CONCURRENT_ACCESS:       
        	return "CONCURRENT_ACCESS";
        case info.dmtree.DmtException.ALERT_NOT_ROUTED:         
        	return "ALERT_NOT_ROUTED";
        case info.dmtree.DmtException.TRANSACTION_ERROR:        
        	return "TRANSACTION_ERROR";
        case info.dmtree.DmtException.SESSION_CREATION_TIMEOUT: 
        	return "SESSION_CREATION_TIMEOUT";
        }
        return null;
    }



	

}
