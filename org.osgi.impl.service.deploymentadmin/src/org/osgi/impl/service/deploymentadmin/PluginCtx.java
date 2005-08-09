/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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
 */
package org.osgi.impl.service.deploymentadmin;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.BundleContext;
import org.osgi.impl.service.deploymentadmin.api.DownloadAgent;
import org.osgi.impl.service.deploymentadmin.plugin.PluginDeployed;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.dmt.DmtAdmin;

public class PluginCtx {
    
    private final Logger              logger; 
    private final BundleContext       bundleContext;
    private final DeploymentAdminImpl da;
    
    private static PluginCtx INSTANCE;
    
    static PluginCtx getInstance(Logger logger, 
            BundleContext bundleContext, 
            DeploymentAdminImpl da) 
    {
        if (null == INSTANCE)
            INSTANCE = new PluginCtx(logger,
                    bundleContext, da);
        return INSTANCE;
    }
    
    private PluginCtx(Logger logger, 
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

    public DownloadAgent getDownloadAgent() {
        return da.getDownloadAgent();
    }

//    public DeploymentPackageImpl installDeploymentPackage(InputStream is) throws DeploymentException {
//        return (DeploymentPackageImpl) da.installDeploymentPackage(is);
//    }

    public PluginDeployed getDeployedPlugin() {
        return da.getDeployedPlugin();
    }

    public void save() throws IOException {
        da.save();
    }

    public DmtAdmin getDmtAdmin() {
        return da.getDmtAdmin();
    }

//    public DeploymentPackage[] listDeploymentPackages() {
//        return da.listDeploymentPackages();
//    }

    public DeploymentAdmin getDeploymentAdmin() {
        return da;
    }

}
