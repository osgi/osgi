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

public class DAConstants {
    
    // debug mode
    public static final boolean DEBUG               = false;

    // headers in the main section of DPs
    public static final String DP_NAME              = "DeploymentPackage-SymbolicName";
    public static final String DP_VERSION           = "DeploymentPackage-Version";
    public static final String DP_FIXPACK           = "DeploymentPackage-FixPack";
    public static final String LOC_PATH             = "Bundle-Localization";
    public static final String BUNDLE_LOCALIZATION  = "Bundle-Localization";
    
    // headers in the name sections of DPs
    public static final String MISSING              = "DeploymentPackage-Missing";
    public static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";
    public static final String BUNDLE_VERSION       = "Bundle-Version";
    public static final String RP_PID               = "Resource-Processor";
    public static final String CUSTOMIZER           = "DeploymentPackage-Customizer";

    // used system properties 
    public static final String KEYSTORE_TYPE   	    = "org.osgi.impl.service.deploymentadmin.keystore.type";
    public static final String KEYSTORE_PATH   	    = "org.osgi.impl.service.deploymentadmin.keystore.file";
    public static final String KEYSTORE_PWD    	    = "org.osgi.impl.service.deploymentadmin.keystore.pwd";
    public static final String DELIVERED_AREA       = "org.osgi.impl.service.deploymentadmin.deliveredarea";
    public static final String USER_PROMPT          = "org.osgi.impl.service.deploymentadmin.userprompt";
    public static final String SESSION_TIMEOUT      = "org.osgi.impl.service.deploymentadmin.sessiontimeout";
    public static final String SYSTEM_DP            = "org.osgi.impl.service.deploymentadmin.systemdp";
    
    // default location of the l10n files
    public static final String DEF_LOC_PATH         = "OSGI-INF/l10n/bundle";
    
    // event related constants
    public static final String TOPIC_INSTALL		= "org/osgi/service/deployment/INSTALL";
    public static final String TOPIC_UNINSTALL		= "org/osgi/service/deployment/UNINSTALL";
    public static final String TOPIC_COMPLETE		= "org/osgi/service/deployment/COMPLETE";
    public static final String EVENTPROP_DPNAME		= "deploymentpackage.name";
    public static final String EVENTPROP_SUCCESSFUL	= "successful";
    
}
