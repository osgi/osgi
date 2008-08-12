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
 * Date           Author(s)
 * CR             Headline
 * ============   ==============================================================
 * 27 Dec, 2005   Luiz Felipe Guimaraes
 * 179            Implement TCK Review 
 * ============   ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin;

import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

/**
 * @author Luiz Felipe Guimarães
 * 
 */
public class InstallPackageThread extends Thread {
	private DeploymentTestControl tbc;
	private TestingDeploymentPackage testDP;
	DeploymentPackage dp = null;
	
	private boolean installed=false;//true if the DP is already installed
	private boolean uninstalled=false;//true if the DP is already uninstalled

	private boolean uninstall=false;//true if you want to uninstall the DP
	private boolean uninstalling=false;//true if the DP is uninstalling
	private boolean running=false;//true if this Thread is running
	
	public static final int EXCEPTION_NOT_THROWN = -1;
	private int exceptionCodeInstall = EXCEPTION_NOT_THROWN;
	private int exceptionCodeUninstall = EXCEPTION_NOT_THROWN;
	
    protected InstallPackageThread(DeploymentTestControl tbc, TestingDeploymentPackage testDP) {
    	this.tbc = tbc;
        this.testDP = testDP;
    }

    public void uninstallDP(boolean blockDP) {
    	if (isAlive()) {
    		uninstall=true;
    		//If a blocked DP is being uninstalled, you cannot wait until this Thread die, otherwise it waits forever
    		if (!blockDP) {
	        	try {
					this.join(DeploymentConstants.SHORT_TIMEOUT);
				} catch (InterruptedException e) {
					
				}
    		}
    	}
    	
    }
 
    public boolean isInstalled() {
    	return installed;
    }
    
    public boolean isRunning() {
    	return running;
    }
    public boolean isUninstalled() {
    	return uninstalled;
    }
    
    public int getDepExceptionCodeInstall() {
    	return exceptionCodeInstall;
    }
    
    public int getDepExceptionCodeUninstall() {
    	return exceptionCodeUninstall;
    }
    public DeploymentPackage getDeploymentPackage() {
    	return dp;
    }
    
    public boolean isUninstalling() {
    	return uninstalling;
    }
    
	public synchronized void run() {
		try {
			try {
				running=true;
				dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
				installed=(dp==null?false:true);
			} catch (Exception e) {
				if (e instanceof DeploymentException) {
					exceptionCodeInstall = ((DeploymentException)e).getCode();
				}
			}
		} finally {
			while (!uninstall) {
				try {
					this.wait(100);
				} catch (InterruptedException e) { }
			}
			if (isInstalled()) {
				try {
					uninstalling = true;
					dp.uninstall();
					uninstalled=true;
				} catch (Exception e) {
					if (e instanceof DeploymentException) {
						exceptionCodeUninstall = ((DeploymentException)e).getCode();
					}
				}
			}
		}
		
	}
}
