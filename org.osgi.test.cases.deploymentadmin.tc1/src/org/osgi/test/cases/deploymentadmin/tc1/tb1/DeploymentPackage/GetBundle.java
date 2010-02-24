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
 * 14/04/2005    Luiz Felipe Guimaraes
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 * May 26, 2005  Andre Assad
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage;



import org.osgi.framework.Bundle;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

/**
 * @author Luiz Felipe Guimaraes
 *
 *  This Test Class Validates the implementation of <code>getBundle</code> method of DeploymentPackage,
 *  according to MEG specification
 */

public class GetBundle implements TestInterface  {
	private DeploymentTestControl tbc;
	
	public GetBundle(DeploymentTestControl tbc){
		this.tbc = tbc;
	}
	
	public void run() {
		prepare();
		testGetBundle001();
		testGetBundle002();
        testGetBundle003();
        testGetBundle004();
	}
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing #getDeploymentPackage",e);
        }
    }
	/**
	 * Asserts that a bundle instance is part of the installed deployment package.
	 * 
	 * @spec DeploymentPackage.getBundle(String)
	 */
	private void testGetBundle001() {
		tbc.log("#testGetBundle001");
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			TestingBundle[] testBundle = testDP.getBundles();
			Bundle bundle = dp.getBundle(testBundle[0].getName());
			tbc.assertNotNull("Bundle "+ testBundle[0].getName() +" is part of the deployment package " + testDP.getName(), bundle);
			tbc.assertEquals("Asserting the bundle's name",testBundle[0].getName(),bundle.getSymbolicName());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		} finally {
			tbc.uninstall(dp);
		}
	}

	/**
	 * Asserts that a null is returned when a bundle that are not part of the deployment package 
	 * is passed as symbolicName parameter
	 * 
	 * @spec DeploymentPackage.getBundle(String)
	 */
	private void testGetBundle002() {
		tbc.log("#testGetBundle002");
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			Bundle bundle = dp.getBundle(DeploymentConstants.INVALID_NAME);
			tbc.assertNull("Bundle "+ DeploymentConstants.INVALID_NAME +" is not part of the deployment package " + testDP.getName(), bundle);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
		} finally {
			tbc.uninstall(dp);
		}
	}
    
    /**
     * Asserts that when a fix-pack removes a bundle then getBundle mustn't
     * return it. It also assures that only DeploymentAdminPermission.METADATA
     * is needed to call this method.
     * 
     * @spec DeploymentPackage.getBundle(String)
     */
    private void testGetBundle003() {
        tbc.log("#testGetBundle003");
        DeploymentPackage dp = null;
        DeploymentPackage fixDp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);           
            TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_UNINSTALL_BUNDLE_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            
            TestingBundle[] testBundle = testDP.getBundles();
            
            tbc.assertEquals("Asserting the number of installed bundles.", 2, dp.getBundleInfos().length);
            
            fixDp = tbc.installDeploymentPackage(tbc.getWebServer() + testFixDP.getFilename());
            
            tbc.setDeploymentAdminPermission(DeploymentConstants.getDPNameFilter(testDP.getName()), DeploymentAdminPermission.METADATA);
            
            Bundle bundle = dp.getBundle(testBundle[0].getName());

            tbc.assertNull("The removed bundles was not returned by getBundle after an installation of a fix-pack that removes that bundles.", bundle);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }),e);
        } finally {
        	prepare();
            tbc.uninstall(new DeploymentPackage[] { dp, fixDp });
        }
    }    
    
	/**
	 * Asserts that SecurityException is thrown if the caller doesn't have 
	 * DeploymentAdminPermission with "metadata" action 
	 * 
	 * @spec DeploymentPackage.getBundle(String)
	 */
	private void testGetBundle004() {
		tbc.log("#testGetBundle004");
		DeploymentPackage dp = null;
		try {
			TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
			dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
			
			tbc.setMininumPermission();
			dp.getBundle(testDP.getBundles()[0].getName());
			tbc.failException("", SecurityException.class);
        } catch (SecurityException e) {
//            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));			
		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }),e);
		} finally {
			prepare();
			tbc.uninstall(dp);
		}
	}
}