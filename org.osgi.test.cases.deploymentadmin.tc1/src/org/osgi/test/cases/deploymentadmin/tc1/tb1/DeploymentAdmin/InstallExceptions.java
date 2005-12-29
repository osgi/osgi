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
 * Sep 05, 2005  Andre Assad
 * 179           Implement review issues
 * ============  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tc1.tb1.DeploymentAdmin;

import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.tc1.tbc.util.TestingDeploymentPackage;


/**
 * @author Andre Assad
 *  
 * This Test Class Validates exceptions occured during
 * installations of deployment packages, according to MEG reference
 * documentation.
 */
public class InstallExceptions implements TestInterface {
    
    private DeploymentTestControl tbc;
    
    public InstallExceptions(DeploymentTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        prepare();
        testInstallExceptions001();
        testInstallExceptions002();
        testInstallExceptions003();
        testInstallExceptions004();
        testInstallExceptions005();
        testInstallExceptions006();
        testInstallExceptions007();
        testInstallExceptions008();
        testInstallExceptions009();
        testInstallExceptions010();
        testInstallExceptions011();
    }
    
    /**
     * Sets permission needed and wait for PermissionWorker
     */
    private void prepare() {
        try {
            tbc.setDeploymentAdminPermission(DeploymentConstants.DEPLOYMENT_PACKAGE_NAME_ALL, DeploymentConstants.ALL_PERMISSION);
        } catch (Exception e) {
            tbc.fail("Failed to set Permission necessary for testing installDeploymentPackage");
        }
    }
    
    /**
     * Asserts that a bundle must belong to one and only one deployment
     * package throwing DeploymentException.CODE_BUNDLE_SHARING_VIOLATION.
     * 
     * @spec DeploymentException.CODE_BUNDLE_SHARING_VIOLATION
     */         
    private void testInstallExceptions001() {
        tbc.log("#testInstallExceptions001");
        DeploymentPackage dp = null;
        DeploymentPackage dp2 = null;       
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP);
            dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_BUNDLE_SHARING_VIOLATION, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
        } finally {
            tbc.uninstall(new DeploymentPackage[] { dp, dp2 });
        }
    }

    /**
     * Asserts that a bundle must belong to only one deployment
     * package, even if its version is different. A deployment
     * package that installs a bundle that was already in the
     * framework should throw
     * DeploymentException.CODE_BUNDLE_SHARING_VIOLATION.
     * 
     * @spec DeploymentException.CODE_BUNDLE_SHARING_VIOLATION
     */
    private void testInstallExceptions002() {
        tbc.log("#testInstallExceptions002");
        DeploymentPackage dp = null;
        DeploymentPackage dp2 = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            TestingDeploymentPackage testDP2 = tbc.getTestingDeploymentPackage(DeploymentConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP_DIF_VERS);
            dp2 = tbc.installDeploymentPackage(tbc.getWebServer() + testDP2.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_BUNDLE_SHARING_VIOLATION, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
        } finally {
            tbc.uninstall(new DeploymentPackage[] { dp, dp2 });
        }
    }
    
    /**
     * Asserts that a DeploymentException.CODE_MISSING_HEADER is thrown when
     * DeploymentPackage-SymbolicName header is missing.
     * 
     * @spec DeploymentException.CODE_MISSING_HEADER
     */ 
    private void testInstallExceptions003() {
        tbc.log("#testInstallExceptions003");
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_NAME_HEADER_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_MISSING_HEADER, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that a DeploymentException.CODE_MISSING_HEADER is thrown when
     * DeploymentPackage-Version header is missing.
     * 
     * @spec DeploymentException.CODE_MISSING_HEADER
     */ 
    private void testInstallExceptions004() {
        tbc.log("#testInstallExceptions004");
        
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_VERSION_HEADER);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_MISSING_HEADER, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that a DeploymentException.CODE_MISSING_HEADER is thrown when
     * DeploymentPackage-FixPack header is missing froma fix-pack.
     * 
     * @spec DeploymentException.CODE_MISSING_HEADER
     */ 
    private void testInstallExceptions005() {
        tbc.log("#testInstallExceptions005");
        
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_VERSION_HEADER);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_MISSING_HEADER, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }   

    /**
     * Asserts that a DeploymentException.CODE_MISSING_HEADER is thrown when
     * Bundle-SymbolicName header is missing from Deployment Package resource
     * headers.
     * 
     * @spec DeploymentException.CODE_MISSING_HEADER
     */ 
    private void testInstallExceptions006() {
        tbc.log("#testInstallExceptions006");
        
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_BSN_HEADER);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_MISSING_HEADER, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that a DeploymentException.CODE_MISSING_HEADER is thrown when
     * Bundle-Version header is missing from Deployment Package resource
     * headers.
     * 
     * @spec DeploymentException.CODE_MISSING_HEADER
     */ 
    private void testInstallExceptions007() {
        tbc.log("#testInstallExceptions007");
        
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_BSN_HEADER);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_MISSING_HEADER, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that a DeploymentException.CODE_MISSING_HEADER is thrown when
     * Name section header is missing from Deployment Package resource headers.
     * 
     * @spec DeploymentException.CODE_MISSING_HEADER
     */ 
    private void testInstallExceptions008() {
        tbc.log("#testInstallExceptions008");
        
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.MISSING_B_VERSION_HEADER);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_MISSING_HEADER, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * Asserts that a DeploymentException.CODE_BUNDLE_NAME_ERROR is thrown when
     * bundle symbolic name is not the same as defined by the deployment package manifest 
     * 
     * @spec DeploymentException.CODE_BUNDLE_NAME_ERROR
     */     
    private void testInstallExceptions009() {
        tbc.log("#testInstallExceptions009");
        
        DeploymentPackage dp = null;
        try {
            TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP);
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("The code of the DeploymentException is ", DeploymentException.CODE_BUNDLE_NAME_ERROR, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     *  This test case installs an untrusted deployment package.
     * 
     * @spec 114.14.5.15 CODE_SIGNING_ERROR
     */                 
    private void testInstallExceptions010() {
        tbc.log("#testInstallExceptions010");
        
        TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.UNTRUSTED_DP);
        DeploymentPackage dp = null;
        try {
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("DeploymentException thrown signing code error", DeploymentException.CODE_SIGNING_ERROR, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
    
    /**
     * The Resource Processor service with the given PID is not found
     * registered.
     * 
     * @spec 114.14.5.14 CODE_PROCESSOR_NOT_FOUND
     */                 
    private void testInstallExceptions011() {
        tbc.log("#testInstallExceptions011");
        
        TestingDeploymentPackage testDP = tbc.getTestingDeploymentPackage(DeploymentConstants.SIMPLE_RESOURCE_DP);
        DeploymentPackage dp = null;
        try {
            dp = tbc.installDeploymentPackage(tbc.getWebServer() + testDP.getFilename());
            tbc.failException("#", DeploymentException.class);
        } catch (DeploymentException e) {
            tbc.assertEquals("DeploymentException thrown if the resource processor was not found.", DeploymentException.CODE_PROCESSOR_NOT_FOUND, e.getCode());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[] {"DeploymentException", e.getClass().getName() }));
        } finally {
            tbc.uninstall(dp);
        }
    }
}
