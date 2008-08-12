/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.deploymentadmin;

import org.osgi.framework.Constants;

public class DAConstants {
    
    // debug mode
    public static final boolean DEBUG               = false;
    
    // headers in the main section of DPs
    public static final String DP_NAME              = "DeploymentPackage-SymbolicName";
    public static final String DP_VERSION           = "DeploymentPackage-Version";
    public static final String DP_FIXPACK           = "DeploymentPackage-FixPack";
    public static final String LOC_PATH             = Constants.BUNDLE_LOCALIZATION;
    public static final String BUNDLE_LOCALIZATION  = Constants.BUNDLE_LOCALIZATION;
    
    // headers in the name sections of DPs
    public static final String RES_NAME             = "Name";
    public static final String MISSING              = "DeploymentPackage-Missing";
    public static final String BUNDLE_SYMBOLIC_NAME = Constants.BUNDLE_SYMBOLICNAME;
    public static final String BUNDLE_VERSION       = Constants.BUNDLE_VERSION;
    public static final String RP_PID               = "Resource-Processor";
    public static final String CUSTOMIZER           = "DeploymentPackage-Customizer";

    // used system properties 
    public static final String KEYSTORE_TYPE   	    = "org.osgi.impl.service.deploymentadmin.keystore.type";
    public static final String KEYSTORE_PATH   	    = "org.osgi.impl.service.deploymentadmin.keystore.file";
    public static final String KEYSTORE_FW_URL	    = "osgi.framework.keystore";
    public static final String KEYSTORE_PWD    	    = "org.osgi.impl.service.deploymentadmin.keystore.pwd";
    public static final String DELIVERED_AREA       = "org.osgi.impl.service.deploymentadmin.deliveredarea";
    public static final String USER_PROMPT          = "org.osgi.impl.service.deploymentadmin.userprompt";
    public static final String SESSION_TIMEOUT      = "org.osgi.impl.service.deploymentadmin.sessiontimeout";
    
    // default location of the l10n files
    public static final String DEF_LOC_PATH         = "OSGI-INF/l10n/bundle";
    
    // location of bundles are in a DP will have this location prefix 
    public static final String LOCATION_PREFIX      = "osgi-dp:";

    // event related constants
    public static final String TOPIC_INSTALL		= "org/osgi/service/deployment/INSTALL";
    public static final String TOPIC_UNINSTALL		= "org/osgi/service/deployment/UNINSTALL";
    public static final String TOPIC_COMPLETE		= "org/osgi/service/deployment/COMPLETE";
    public static final String EVENTPROP_DPNAME		= "deploymentpackage.name";
    public static final String EVENTPROP_SUCCESSFUL	= "successful";
    
	// generic alert types
    public static final String ALERT_TYPE_INS_ACT          = "org.osgi.deployment.installandactivate";
    public static final String ALERT_TYPE_DELIVERED_REMOVE = "org.osgi.deployment.delivered.remove";
    public static final String ALERT_TYPE_DELOYED_REMOVE   = "org.osgi.deployment.deployed.remove";
    public static final String ALERT_TYPE_DWNL_INS_ACT     = "org.osgi.deployment.downloadandinstallandactivate";
	
    // Deployment subtree root
    public static final String DMT_DEPLOYMENT_ROOT;

    static {
    	String prop = "info.dmtree.osgi.root";
    	String val = System.getProperty(prop);
    	if (null == val)
    		throw new RuntimeException(prop + " system property is not defined.");
    	DMT_DEPLOYMENT_ROOT = val + "/Deployment/Inventory/Deployed/";
    }

}
