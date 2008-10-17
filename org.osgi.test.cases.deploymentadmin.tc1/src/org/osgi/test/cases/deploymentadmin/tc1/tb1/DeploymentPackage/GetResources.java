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
 */

package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage;

import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingResource;

/**
 * @author Luiz Felipe Guimaraes This Test Class Validates the implementation of
 *         <code>getResources</code> method of DeploymenPackage, according to
 *         MEG specification
 */
public class GetResources implements TestInterface  {

    private DeploymentTestControl tbc;

    public GetResources(DeploymentTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
    	prepare();
    	testGetResources001();
        testGetResources002();
        testGetResources003();
    }
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing #getDeploymentPackage");
        }
    }
    /**
     * Asserts that it returns an array of strings representing the resources
     * that are specified in the manifest of the deployment package
     * 
     * @spec DeploymentPackage.getResources()
     */
    private void testGetResources001() {
        tbc.log("#testGetResources001");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_PROCESSOR_DP);
            TestingResource testResource[] = testDP.getResources();
            TestingBundle testBundle[] = testDP.getBundles();
            
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            String[] resources = dp.getResources();
            int resourceLengthExpected = testResource.length + testBundle.length;
            int resourceLengthGot = resources.length;
            int found = 0;
            if (resourceLengthExpected == resourceLengthGot) {
                for (int i = 0; i < testResource.length; i++) {
                    for (int j = 0; j < resourceLengthGot; j++) {
                        if (resources[j].equals(testResource[i].getName())) {
                            found++;
                            break;
                        }
                    }
                }
                for (int i = 0; i < testBundle.length; i++) {
                    for (int j = 0; j < resourceLengthGot; j++) {
                        if (resources[j].equals(testBundle[i].getFilename())) {
                            found++;
                            break;
                        }
                    }
                }
            } else {
                tbc.fail("The number of resources received from DP is not #resources + #bundles");
            }
            tbc.assertTrue("Asserts that it returns the requested resources.",
                found == resourceLengthExpected);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.uninstall(dp);
        }
    }

    /**
     * Asserts that it returns an array zero dimensional if there is no
     * resources. It also tests if only DeploymentAdminPermission with "metadata" is needed.
     * 
     * @spec DeploymentPackage.getResources()
     */
    private void testGetResources002() {
        tbc.log("#testGetResources002");
        DeploymentPackage dp = null;
        DeploymentPackage fixDP = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_UNINSTALL_BUNDLE_DP);
            fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+ testFixDP.getFilename());
            
            tbc.setDeploymentAdminPermission(DeploymentConstants.getDPNameFilter(fixDP.getName()), DeploymentAdminPermission.METADATA);
            String[] resources = fixDP.getResources();
            tbc.assertTrue("Asserts that it returns the requested resources", resources.length == 0);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
        	prepare();
            tbc.uninstall(new DeploymentPackage[]{dp, fixDP});
        }
    }
    
    /**
	 * Asserts that SecurityException is thrown if the caller doesn't have 
	 * DeploymentAdminPermission with "metadata" action
     * 
     * @spec DeploymentPackage.getResources()
     */
    private void testGetResources003() {
        tbc.log("#testGetResources003");
        DeploymentPackage dp = null;
        DeploymentPackage fixDP = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            TestingDeploymentPackage testFixDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_UNINSTALL_BUNDLE_DP);
            fixDP = tbc.installDeploymentPackage(tbc.getWebServer()+ testFixDP.getFilename());
            
            tbc.setMininumPermission();
            fixDP.getResources();
			tbc.failException("", SecurityException.class);
        } catch (SecurityException e) {
//            tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { "SecurityException" }));			
		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"SecurityException", e.getClass().getName() }));
		} finally {
			prepare();
			tbc.uninstall(new DeploymentPackage[]{dp, fixDP});
        }
    }
}
