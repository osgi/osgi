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

import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.deploymentadmin.DeploymentException;

/**
 * Represents the context of a DP   
 */
public class DeploymentPackageCtx {
    
    private final Logger              logger; 
    private final BundleContext       bundleContext;
    private final DeploymentAdminImpl da;
    
    private static DeploymentPackageCtx INSTANCE;
    
    static DeploymentPackageCtx getInstance(Logger logger, 
            BundleContext bundleContext, 
            DeploymentAdminImpl da) 
    {
        if (null == INSTANCE)
            INSTANCE = new DeploymentPackageCtx(logger,
                    bundleContext, da);
        return INSTANCE;
    }
    
    private DeploymentPackageCtx(Logger logger, 
            BundleContext bundleContext, 
            DeploymentAdminImpl da) 
    {
        this.logger = logger;
        this.bundleContext = bundleContext;
        this.da = da;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public Logger getLogger() {
        return logger;
    }

    public void checkPermission(DeploymentPackageImpl impl, String action) {
        da.checkPermission(impl, action);
    }

    public void uninstall(DeploymentPackageImpl dp) throws DeploymentException {
        da.uninstall(dp);
    }

    public boolean uninstallForced(DeploymentPackageImpl dp) throws DeploymentException {
        return da.uninstallForced(dp);
    }

    public Set getDeploymentPackages() {
        return new HashSet(da.getDeploymentPackages());
    }

}
