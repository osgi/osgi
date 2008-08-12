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
 * ============  ====================================================================
 * Nov 10, 2005  Andre Assad
 * 260           Deployment spec changes
 * ============  ====================================================================
 */

package org.osgi.test.cases.deploymentadmin.tc1.tbc.BundleInfo;

import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingBundle;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;

/**
 * @author Andre Assad
 * 
 * This Test Class Validates the implementation of
 * <code>getVersion<code> method, according to MEG reference
 * documentation.
 */
public class GetVersion {
    private DeploymentTestControl tbc;

    public GetVersion(DeploymentTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testGetVersion001();
    }
    
    /**
     * This test case installs a deployment package and then gets its symbolic
     * name through BundleInfo interface
     * 
     * @spec BundleInfo.getSymbolicName()
     */
    private void testGetVersion001() {
        tbc.log("#testGetVersion001");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            
            TestingBundle[] tBundle = testDP.getBundles();
            BundleInfo[] bInfo = dp.getBundleInfos();
            tbc.assertEquals("Asserts that getDeploymentPackage returns the correct bundle symbolic name", tBundle[0].getName(), bInfo[0].getSymbolicName());
            tbc.assertEquals("Asserts that getDeploymentPackage returns the correct bundle symbolic name", tBundle[1].getName(), bInfo[1].getSymbolicName());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
}
