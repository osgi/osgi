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
 * 14/04/2005    Andre Assad
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 * 02/09/2005    Andre Assad
 * 179           Implement Review Issues
 * ============  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tc1.tb1;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin.Cancel;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin.GetDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin.InstallDeploymentPackageAPI;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin.InstallDeploymentPackageUseCases;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin.InstallExceptions;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin.InstallFixPack;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin.ListDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin.UninstallDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.Equals;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.GetBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.GetBundleInfos;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.GetDisplayName;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.GetHeader;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.GetIcon;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.GetName;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.GetResourceHeader;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.GetResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.GetResources;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.GetVersion;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.IsStale;
import org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage.ManifestFormat;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TB1Service;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;

/**
 * @author Andre Assad
 * 
 * Activator of bundle that will test DeploymentAdmin methods
 */

public class Activator implements BundleActivator, TB1Service {
	
	ServiceRegistration sr;
	
	public void start(BundleContext bc) throws Exception {
		sr = bc.registerService(TB1Service.class.getName(), this, null);
		System.out.println("TB1Service started.");

	}

	public void stop(BundleContext arg0) throws Exception {
		sr.unregister();

	}
	
	public TestInterface[] getTestClasses(DeploymentTestControl tbc) {
		return new TestInterface[] {
				new InstallDeploymentPackageAPI((DeploymentTestControl) tbc),
				new InstallDeploymentPackageUseCases((DeploymentTestControl) tbc),
				new InstallFixPack((DeploymentTestControl) tbc),
				new ListDeploymentPackage((DeploymentTestControl) tbc),
				new GetDeploymentPackage((DeploymentTestControl) tbc),
				new UninstallDeploymentPackage((DeploymentTestControl) tbc),
                new Cancel((DeploymentTestControl) tbc),
                new InstallExceptions((DeploymentTestControl) tbc),
                new Equals((DeploymentTestControl) tbc),
                new GetBundle((DeploymentTestControl) tbc),
                new GetBundleInfos((DeploymentTestControl) tbc),
                new GetHeader((DeploymentTestControl) tbc),
                new GetName((DeploymentTestControl) tbc),
                new GetResourceHeader((DeploymentTestControl) tbc),
                new GetResourceProcessor((DeploymentTestControl) tbc),
                new GetResources((DeploymentTestControl) tbc),
                new GetVersion((DeploymentTestControl) tbc),
                new IsStale((DeploymentTestControl) tbc),
                new GetDisplayName(tbc),
                new GetIcon(tbc),
                new ManifestFormat(tbc)
				};
	}
}
