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
 * Sep 08, 2005  Andre Assad
 * 179           Implement review issues
 * ============  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentPackage;

import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;


/**
 * Test class that validates the manifest formats of Deployment Packages
 * 
 * @author Andre Assad
 */
public class ManifestFormat implements TestInterface {
    
    private DeploymentTestControl tbc;
    
    public ManifestFormat(DeploymentTestControl tbc) {
        this.tbc = tbc;
    }
    
    public void run() {
    	prepare();
        testManifestFormat001();
        testManifestFormat002();
        testManifestFormat003();
        testManifestFormat004();
        testManifestFormat005();
        testManifestFormat006();
        testManifestFormat007();
    }
    
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing #getDeploymentPackage",e);
        }
    }
    /**
     * Asserts that Deployment Admin cannot install a deployment package with a
     * wrong DeploymentPackage-SymbolicName manifest.
     * 
     * @spec 114.3.4.1 DeploymentPackage-SymbolicName
     */
    private void testManifestFormat001() {
        tbc.log("#testManifestFormat001");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.WRONG_NAME);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
//            tbc.pass("Wrong DeploymentPackage-SymbolicName manifest cannot be installed");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }),e);
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that Deployment Admin cannot install a deployment package with a
     * wrong DeploymentPackage-Version manifest.
     * 
     * @spec 114.3.4.2 DeploymentPackage-Version
     */
    private void testManifestFormat002() {
        tbc.log("#testManifestFormat002");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.WRONG_VERSION_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
//            tbc.pass("Wrong DeploymentPackage-Version manifest cannot be installed");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }),e);
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that Deployment Admin cannot install a deployment package with a
     * wrong DeploymentPackage-FixPack manifest.
     * 
     * @spec 114.3.4.3 DeploymentPackage-FixPack
     */
    private void testManifestFormat003() {
        tbc.log("#testManifestFormat003");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.WRONG_FIX_PACK);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
//            tbc.pass("Wrong DeploymentPackage-FixPack manifest cannot be installed");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }),e);
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that Deployment Admin cannot install a deployment package with a
     * wrong Bundle-SymbolicName manifest.
     * 
     * @spec 114.3.4.4 Bundle-SymbolicName (Name Section)
     */
    private void testManifestFormat004() {
        tbc.log("#testManifestFormat004");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.WRONG_BSN);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
//            tbc.pass("Wrong Bundle-SymbolicName manifest cannot be installed");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }),e);
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that Deployment Admin cannot install a deployment package with a
     * wrong Bundle-Version manifest.
     * 
     * @spec 114.3.4.5 Bundle-Version (Name Section)
     */
    private void testManifestFormat005() {
        tbc.log("#testManifestFormat005");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.WRONG_BVERSION);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
//            tbc.pass("Wrong Bundle-Version manifest cannot be installed");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }),e);
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that Deployment Admin cannot install a deployment package with a
     * wrong Resource-Processor manifest.
     * 
     * @spec 114.3.4.6 Resource-Processor (Name Section)
     */
    private void testManifestFormat006() {
        tbc.log("#testManifestFormat006");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.WRONG_RP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
//            tbc.pass("Wrong Resource-Processor manifest cannot be installed");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }),e);
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that Deployment Admin cannot install a deployment package with a
     * wrong DeploymentPackage-Missing manifest.
     * 
     * @spec 114.3.4.7 DeploymentPackage-Missing (Name Section)
     */
    private void testManifestFormat007() {
        tbc.log("#testManifestFormat007");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.WRONG_DP_MISSING);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
//            tbc.pass("Wrong DeploymentPackage-Missing manifest cannot be installed");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }),e);
        } finally {
            tbc.uninstall(dp);
        }
    }
}
