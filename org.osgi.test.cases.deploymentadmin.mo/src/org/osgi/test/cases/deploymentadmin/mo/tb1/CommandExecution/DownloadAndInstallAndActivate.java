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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * Jun 20, 2005 Eduardo Oliveira
 * 97           Implement MEGTCK for the deployment MO Spec 
 * ===========  ==============================================================
 * Jul 15, 2005 Eduardo Oliveira
 * 147          Implement MEGTCK for the deployment MO Spec 
 * ===========  ==============================================================
 * Aug 25, 2005 Andre Assad
 * 147          Rework after formal inspection 
 * ===========  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.mo.tb1.CommandExecution;

import java.io.File;
import java.util.Iterator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoConstants;
import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoTestControl;
import org.osgi.test.cases.deploymentadmin.mo.tbc.SessionWorker;
import org.osgi.test.cases.deploymentadmin.mo.tbc.TestInterface;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.BundleListenerImpl;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingArtifact;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingBlockingResourceProcessor;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingDlota;

/**
 * @author Eduardo Oliveira
 * 
 * This Test Class Validates the implementation of
 * Command Execution - DownloadAndInstallAndActivate, 
 * according to MEG reference documentation.
 */
public class DownloadAndInstallAndActivate implements TestInterface {
   
    private DeploymentmoTestControl tbc;
    
    private DmtSession session;

    private SessionWorker worker1;

    public DownloadAndInstallAndActivate(DeploymentmoTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
    	testDownloadAndInstallAndActivate001();
        testDownloadAndInstallAndActivate002();
        testDownloadAndInstallAndActivate003();
        testDownloadAndInstallAndActivate004();
        testDownloadAndInstallAndActivate005();
        testDownloadAndInstallAndActivate006();
        testDownloadAndInstallAndActivate007();
        testDownloadAndInstallAndActivate008();
        testDownloadAndInstallAndActivate009();
        testDownloadAndInstallAndActivate010();
        testDownloadAndInstallAndActivate011();
        testDownloadAndInstallAndActivate012();
        testDownloadAndInstallAndActivate013();
        testDownloadAndInstallAndActivate014();
        testDownloadAndInstallAndActivate015();
        testDownloadAndInstallAndActivate016();
    	testDownloadAndInstallAndActivate017();
        testDownloadAndInstallAndActivate018();
  		testDownloadAndInstallAndActivate019();
        testDownloadAndInstallAndActivate020();
        testDownloadAndInstallAndActivate021();
        testDownloadAndInstallAndActivate022();
        testDownloadAndInstallAndActivate023();
        testDownloadAndInstallAndActivate024();
        testDownloadAndInstallAndActivate025();
        testDownloadAndInstallAndActivate026();
        testDownloadAndInstallAndActivate027();
        testDownloadAndInstallAndActivate028();
        testDownloadAndInstallAndActivate029();
    }


    private void prepare() {
        try {
        	session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.PRINCIPAL,DeploymentmoConstants.DEPLOYMENT, DmtSession.LOCK_TYPE_EXCLUSIVE);
        	session.createInteriorNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST);
        } catch (Exception e) {
            tbc.fail("Failed to prepare DownloadAndInstallAndActivate Command Execution: " + e.getMessage());
        }
    }


    private void unprepare(File dlotaFile) {
        tbc.cleanUp(session, new String[]{DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST});
        if (dlotaFile != null) {
            dlotaFile.delete();
        }
    }

    /**
     * This test asserts that $/Deployment/Download/[node_id]/State is IDLE (10)
     * if no download is being done
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command 
     */
    private void testDownloadAndInstallAndActivate001() {
        tbc.log("#testDownloadAndInstallAndActivate001");
        prepare();
        try {
            if (preCondition("dlota.xml", DeploymentmoConstants.SIMPLE_DP_NAME)) {
                tbc.assertEquals("Asserting if State is IDLE(10).", 
                    session.getNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_STATUS).getInt(), 10);
            } else {
                tbc.log("Precondition for testDownloadAndInstallAndActivate001 not satisfied");
            }
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            unprepare(null);
            
        }
    }

    /**
     * This test asserts that a node is created at Deployed subtree when Deployment completes succesfully. 
     * It also tests the alert values (result code 200).
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate002() {
        tbc.log("#testDownloadAndInstallAndActivate002");
        TestingDlota dlota = null;
        prepare();
        String nodeId = "";
        try {
            TestingArtifact artifact = tbc.getArtifact(DeploymentmoConstants.SIMPLE_DP);
            TestingDeploymentPackage dp = artifact.getDeploymentPackage();
            dlota = artifact.getDlota();
            tbc.generateDLOTA(dlota);
            if (preCondition(dp.getFilename(), artifact.getDlota().getFilename())) {
                String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                
                synchronized (tbc) {
                    session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);
                    tbc.wait(DeploymentmoConstants.TIMEOUT);
                }

                String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);

                tbc.assertTrue("Asserts that the node was created",initialChildren.length+1==finalChildren.length);
                
                nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);
                
                tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_DOWNLOADANDINSTALLANDACTIVATE,
                    DeploymentmoConstants.getDeployedNodeId(nodeId),
                    new DmtData(200));
                
            } else {
                tbc.log("Precondition for testDownloadAndInstallAndActivate002 not satisfied");
            }
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
        	if (!nodeId.equals("")) {
        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
        	}
            unprepare(dlota.getDlotaFile());
            tbc.resetCommandValues();
        }
    }


    /**
     * This test asserts that $/Deployment/Download/[node_id]/State is DOWNLOAD_FAILED (20)
     * when there is a failure in the download
     * 
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate003() {
        tbc.log("#testDownloadAndInstallAndActivate003");

        TestingDlota dlota = null;
        prepare();
        try {
            
            // This artifact will cause a deployment failure
            TestingArtifact artifact = tbc.getArtifact(DeploymentmoConstants.DOWNLOAD_FAILED_DP);
            TestingDeploymentPackage dp = artifact.getDeploymentPackage();
            dlota = artifact.getDlota();
            
            tbc.generateDLOTA(artifact.getDlota());
            
            if (preCondition(dp.getFilename(), artifact.getDlota().getFilename())) {
                String[] initialChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                session.execute(
                        DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);
                
                tbc.assertEquals("Asserts if the download failed",
                        DeploymentmoConstants.CODE_DOWNLOAD_FAILED,session.getNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_STATUS).getInt());
                
                String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);

                tbc.assertTrue("Asserts that the node was not created",initialChildren.length==finalChildren.length);
                
            } else {
                tbc.log("Precondition for testDownloadAndInstallAndActivate003 not satisfied");
            }
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            unprepare(dlota.getDlotaFile());
            tbc.resetCommandValues();
        }
    }
 
    /**
     * This test asserts that $/Deployment/Download/[node_id]/State follows the order
     * IDLE, STREAMING, DEPLOYED
     * (This test is executed only if the implementation is streaming)
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate004() {
        tbc.log("#testDownloadAndInstallAndActivate004");
        if (DeploymentmoConstants.IS_STREAMING) {
	        TestingDlota dlota = null;
	        prepare();
	        String nodeId = "";
	        try {
	            
	            TestingArtifact artifact = tbc.getArtifact(DeploymentmoConstants.SIMPLE_DP);
	            TestingDeploymentPackage dp = artifact.getDeploymentPackage();
	            dlota = artifact.getDlota();
	            
	            tbc.generateDLOTA(artifact.getDlota());
	            
	            if (preCondition(dp.getFilename(), artifact.getDlota().getFilename())) {
	            	String[] initialChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
	            	
	            	int[] states = new int[3];
	                int actualState=0;
	                //The first state (idle) is gotten before execute method is called, so, if it is returned 
	                //again in the while below it is ignored.
	            	states[actualState] = session.getNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_STATUS).getInt();
	            	actualState++;
	            	
	            	session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);

		            boolean passed =false;
		            long initial = System.currentTimeMillis();
		            long actual = System.currentTimeMillis();
		            while (!passed && (actual - initial < DeploymentmoConstants.TIMEOUT)) {
		            	int nodeValue = session.getNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_STATUS).getInt();
		                if (nodeValue != states[actualState-1]) {
		                	states[actualState] = nodeValue;
		                	actualState++;
		                    
		                }
		                actual = System.currentTimeMillis();
		            }
		            
		            tbc.assertEquals("Asserts if the idle code was gotten in the specified order",states[0], DeploymentmoConstants.CODE_IDLE);
	                tbc.assertEquals("Asserts if the streaming code was gotten in the specified order",states[1], DeploymentmoConstants.CODE_STREAMING);
	                tbc.assertEquals("Asserts if the deployed code was gotten in the specified order",states[2], DeploymentmoConstants.CODE_DEPLOYED);
	                
	                String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
	                nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);
	            } else {
	                tbc.log("Precondition for testDownloadAndInstallAndActivate004 not satisfied");
	            }
	        } catch (Exception e) {
	            tbc.fail(MessagesConstants.getMessage(
	                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
	        } finally {
	        	if (!nodeId.equals("")) {
	        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
	        	}
	            unprepare(dlota.getDlotaFile());
	            tbc.resetCommandValues();
	        }
        } else {
            tbc.log("#Download is not done by streaming, STREMING code wont be tested");
        }
    }
    
    
    /**
     * This test asserts that $/Deployment/Download/[node_id]/State follows the order 
     * IDLE, DOWNLOADING, DOWNLOADED, DEPLOYING, DEPLOYED 
     * (This test is executed only if the implementation is not streaming)
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate005() {
        tbc.log("#testDownloadAndInstallAndActivate005");
        if (!DeploymentmoConstants.IS_STREAMING) {
	        TestingDlota dlota = null;
	        prepare();
	        try {
	            
	            TestingArtifact artifact = tbc.getArtifact(DeploymentmoConstants.SIMPLE_DP);
	            TestingDeploymentPackage dp = artifact.getDeploymentPackage();
	            dlota = artifact.getDlota();
	            
	            tbc.generateDLOTA(artifact.getDlota());
	            
	            if (preCondition(dp.getFilename(), artifact.getDlota().getFilename())) {
	                int[] states = new int[5];
	                int actualState=0;
	                //The first state (idle) is gotten before execute method is called, so, if it is returned 
	                //again in the while below it is ignored.
	            	states[actualState] = session.getNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_STATUS).getInt();
	            	actualState++;
	            	
	            	session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);

		            boolean passed =false;
		            long initial = System.currentTimeMillis();
		            long actual = System.currentTimeMillis();
		            while (!passed && (actual - initial < DeploymentmoConstants.TIMEOUT)) {
		            	int nodeValue = session.getNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_STATUS).getInt();
		                if (nodeValue != states[actualState-1]) {
		                	states[actualState] = nodeValue;
		                	actualState++;
		                    
		                }
		                actual = System.currentTimeMillis();
		            }
		            
		            tbc.assertEquals("Asserts if the idle code was gotten in the specified order",states[0], DeploymentmoConstants.CODE_IDLE);
	                tbc.assertEquals("Asserts if the downloading code was gotten in the specified order",states[1], DeploymentmoConstants.CODE_DOWNLOADING);
	                tbc.assertEquals("Asserts if the downloaded code was gotten in the specified order",states[2], DeploymentmoConstants.CODE_DOWNLOADED);
	                tbc.assertEquals("Asserts if the deploying code was gotten in the specified order",states[3], DeploymentmoConstants.CODE_DEPLOYING);
	                tbc.assertEquals("Asserts if the deployed code was gotten in the specified order",states[4], DeploymentmoConstants.CODE_DEPLOYED);
	                

	
	            } else {
	                tbc.log("Precondition for testDownloadAndInstallAndActivate005 not satisfied");
	            }
	        } catch (Exception e) {
	            tbc.fail(MessagesConstants.getMessage(
	                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
	        } finally {
	            unprepare(dlota.getDlotaFile());
	            tbc.resetCommandValues();
	        }
        } else {
            tbc.log("#Download is done by streaming, DOWNLOADING, DOWNLOADED and DEPLOYING codes wont be tested");
        }
    }
    
    /**
     * This test asserts that $/Deployment/Download/[node_id]/State is DEPLOYMENT_FAILED (70) 
     * when the deployment failed
     * 
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate006() {
        
        tbc.log("#testDownloadAndInstallAndActivate006");
        TestingDlota dlota = null;
        prepare();
        try {

            // This artifact will cause a deployment failure
            TestingArtifact artifact = tbc.getArtifact(DeploymentmoConstants.MISSING_NAME_HEADER_DP);
            TestingDeploymentPackage dp = artifact.getDeploymentPackage();
            dlota = artifact.getDlota();
            tbc.generateDLOTA(artifact.getDlota());
            
            if (preCondition(dp.getFilename(), artifact.getDlota().getFilename())) {
                String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                
                synchronized (tbc) {
                	session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);
                    tbc.wait(DeploymentmoConstants.TIMEOUT);
                }
                
                tbc.assertEquals("Asserts if the deployment failed",
                    DeploymentmoConstants.CODE_DEPLOYMENT_FAILED,session.getNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_STATUS).getInt());
                
                String[] finalChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                
                tbc.assertTrue("Asserts that the node was not created",initialChildren.length==finalChildren.length);
                
            } else {
                tbc.log("Precondition for testDownloadAndInstallAndActivate006 not satisfied");
            }
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            unprepare(dlota.getDlotaFile());
            tbc.resetCommandValues();
        }
    }
    

    /**
     * This test asserts that DmtException is thrown if $/Deployment/Download/[node_id]/EnvType 
     * is not "OSGi.R4"
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate007() {
        tbc.log("#testDownloadAndInstallAndActivate007");
        try {
            TestingArtifact artifact = tbc.getArtifact(DeploymentmoConstants.SIMPLE_DP);
            session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.PRINCIPAL,DeploymentmoConstants.DEPLOYMENT, DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            session.createInteriorNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST);
            
            session.setNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_URI, new DmtData(DeploymentmoConstants.DLOTA_PATH + artifact.getDlota().getFilename()));

            session.setNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_ID, new DmtData(DeploymentmoConstants.SIMPLE_DP_NAME));

            session.setNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_ENVTYPE, new DmtData("OSGi.X"));
            
            session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);

            tbc.failException("",DmtException.class);
        } catch (DmtException e) {
            tbc.pass("DmtException was thrown when $/Deployment/Download/[node_id]/EnvType was not \"OSGi.R4\"");
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_THROWN, new String[]{ DmtException.class.getName(),e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[]{DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST});
            tbc.resetCommandValues();
        }
    }
    
     /**
     * This test asserts the result code is 408 if the URL is malformed.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate008() {
        tbc.log("#testDownloadAndInstallAndActivate008");
        prepare();
        try {

            if (preCondition("id", "http://inv@lid$")) {
                
                synchronized (tbc) {
                    session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);
                    tbc.wait(DeploymentmoConstants.TIMEOUT);
                }

                tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_DOWNLOADANDINSTALLANDACTIVATE,
                    null, new DmtData(408));
                
            } else {
                tbc.log("Precondition for testDownloadAndInstallAndActivate008 not satisfied");
            }
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            unprepare(null);
            tbc.resetCommandValues();
        }
    }
    
    /**
     * This test asserts that the node values created at Deployed subtree are the specified.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate009() {
        tbc.log("#testDownloadAndInstallAndActivate009");
        TestingDlota dlota = null;
        prepare();
        String nodeId = "";
        try {
            TestingArtifact artifact = tbc.getArtifact(DeploymentmoConstants.SIMPLE_DP);
            TestingDeploymentPackage dp = artifact.getDeploymentPackage();
            dlota = artifact.getDlota();
            tbc.generateDLOTA(dlota);
            if (preCondition(dp.getFilename(), artifact.getDlota().getFilename())) {
                String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                
                synchronized (tbc) {
                    session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);
                    tbc.wait(DeploymentmoConstants.TIMEOUT);
                }

                String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                tbc.assertTrue("Asserts that the node was created",initialChildren.length+1==finalChildren.length);
                nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);
                
                //Deployment Package "simple.dp"
    			tbc.assertEquals(
    							"Asserting the node ID",
    							DeploymentmoConstants.SIMPLE_DP_NAME,
    							session.getNodeValue(DeploymentmoConstants.getDeployedID(nodeId)).toString());

    			tbc.assertEquals(
    							"Asserting the node EnvType",
    							DeploymentmoConstants.ENVTYPE,
    							session.getNodeValue(DeploymentmoConstants.getDeployedEnvType(nodeId)).toString());
    			
    			String manifest = session.getNodeValue(DeploymentmoConstants.getDeployedExtManifest(nodeId)).toString().trim();
    			boolean passed = true;
    			Iterator iterator = DeploymentmoConstants.simpleDpManifest.iterator();
    			while (iterator.hasNext()) {
    				String element = (String)iterator.next();
    				if (manifest.indexOf(element) < 0) {
    					passed=false;
    				}
    			}
    			
    			tbc.assertTrue("Asserting the content of the Deployment Package manifest",passed);
    									

    			tbc.assertEquals(
    							"Asserting the package type",
    							DeploymentmoConstants.OSGI_DP,
    							session.getNodeValue(DeploymentmoConstants.getDeployedExtPackageType(nodeId)).getInt());
    			
    			String[] signerChildren = session.getChildNodeNames(DeploymentmoConstants.getDeployedExtSigners(nodeId));
    			
    			if (signerChildren.length<=0) {
    				tbc.fail("./OSGi/Deployment/Inventory/Deployed/[node_id]/Ext/Signers/[signer] does not exist");
    			}
    			String signer = signerChildren[0];
    			
    			tbc.assertEquals("Asserting the signer of the deployment package",
    					DeploymentmoConstants.SIMPLE_DP_SIGNER,
    					session.getNodeValue(DeploymentmoConstants.getDeployedExtSignersSignerId(nodeId, signer)).toString());

    			
    			//Bundle "bundles.tb1"
    			Bundle bundle1 = tbc.getBundle(DeploymentmoConstants.SIMPLE_DP_BUNDLE1_SYMBNAME);
    			tbc.assertNotNull("The bundle was installed in the framework", bundle1);
    			String bundleId = bundle1.getBundleId() + "";
    			
    			tbc.assertTrue("Asserting that the bundle id is the same as the specified",
    					session.isNodeUri(DeploymentmoConstants.getDeployedExtBundlesBundleId(nodeId, bundleId)));

    			tbc.assertEquals("Asserting bundle's state",
    					DeploymentmoConstants.SIMPLE_DP_BUNDLE1_STATE,
    					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesState(nodeId, bundleId)).getInt());
    			
    			tbc.assertEquals("Asserting that the manifest of the first bundle is the same as the specified",
    					DeploymentmoConstants.SIMPLE_DP_BUNDLE1_MANIFEST,
    					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesManifest(nodeId, bundleId)).toString().trim());
    			
    			tbc.assertEquals("Asserting that the location of the first bundle is the same as the specified",
    					DeploymentmoConstants.SIMPLE_DP_BUNDLE1_LOCATION,
    					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesLocation(nodeId, bundleId)).toString());

    			//Bundle "bundles.tb2"
    			Bundle bundle2 = tbc.getBundle(DeploymentmoConstants.SIMPLE_DP_BUNDLE2_SYMBNAME);
    			tbc.assertNotNull("The bundle was installed in the framework", bundle1);
    			bundleId = bundle2.getBundleId() + "";
    			
    			tbc.assertTrue("Asserting that the bundle id is the same as the specified",
    					session.isNodeUri(DeploymentmoConstants.getDeployedExtBundlesBundleId(nodeId, bundleId)));

    			tbc.assertEquals("Asserting bundle's state",
    					DeploymentmoConstants.SIMPLE_DP_BUNDLE2_STATE,
    					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesState(nodeId, bundleId)).getInt());
    			
    			tbc.assertEquals("Asserting that the manifest of the second bundle is the same as the specified",
    					DeploymentmoConstants.SIMPLE_DP_BUNDLE2_MANIFEST,
    					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesManifest(nodeId, bundleId)).toString().trim());
    			
    			tbc.assertEquals("Asserting that the location of the second bundle is the same as the specified",
    					DeploymentmoConstants.SIMPLE_DP_BUNDLE2_LOCATION,
    					session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesLocation(nodeId, bundleId)).toString());

                
            } else {
                tbc.log("Precondition for testDownloadAndInstallAndActivate002 not satisfied");
            }
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
        	if (!nodeId.equals("")) {
        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
        	}
            unprepare(dlota.getDlotaFile());
            tbc.resetCommandValues();
        }
    }
    
    /**
	 * This test asserts that DownloadAndInstallAndActivate command updates the subtree when 
	 * a sub-tree under the $/Deployment/Inventory/Deployed node with the same ID node 
	 * already exist.
	 * 
	 * @spec 3.6.5.2 InstallAndActivate Command
	 */

	private void testDownloadAndInstallAndActivate010() {
		tbc.log("#testDownloadAndInstallAndActivate010");
		prepare();
		TestingDlota dlota=null;
		//Installs 'simple.dp'
		String nodeId = executeNodeAndGetNewNodeName(DeploymentmoConstants.SIMPLE_DP);

		try {
			
			//Updates 'simple.dp' (which has the content of simple_fix_pack.dp)
            TestingArtifact artifact = tbc.getArtifact(DeploymentmoConstants.SIMPLE_DP);

            String fileName = (artifact.isBundle())?artifact.getBundle().getFilename():artifact.getDeploymentPackage().getFilename();
            
            
            
            dlota = new TestingDlota("simple_dp.xml", DeploymentmoConstants.SERVER
                    + "www/update/" + artifact.getDeploymentPackage().getFilename(), 4768, DeploymentmoConstants.ENVIRONMENT_DP);
            tbc.generateDLOTA(dlota,artifact.isBundle());
            
            if (!preCondition(fileName, dlota.getFilename())) {
            	tbc.log("Precondition for this method was not satisfied");


            } else {
	             String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                
	        	synchronized (tbc) {
	            	session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);
	                tbc.wait(DeploymentmoConstants.TIMEOUT);
	            }
	
				 String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
				 
				 tbc.assertTrue("Asserts that the at Deployed subtree was not created (it is an update)",initialChildren.length==finalChildren.length);
	             
	             tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_DOWNLOADANDINSTALLANDACTIVATE,
	                 DeploymentmoConstants.getDeployedNodeId(nodeId),
	                 new DmtData(200));
	
	
	            //Deployment Package "simple.dp" (which has the content of simple_fix_pack.dp)
				tbc.assertEquals(
								"Asserting the node ID",
								DeploymentmoConstants.SIMPLE_DP_NAME,
								session.getNodeValue(DeploymentmoConstants.getDeployedID(nodeId)).toString());
	
				tbc.assertEquals(
								"Asserting the node EnvType",
								DeploymentmoConstants.ENVTYPE,
								session.getNodeValue(DeploymentmoConstants.getDeployedEnvType(nodeId)).toString());
				
				String manifest = session.getNodeValue(DeploymentmoConstants.getDeployedExtManifest(nodeId)).toString().trim();
				boolean passed = true;
				Iterator iterator = DeploymentmoConstants.simpleFixPackManifest.iterator();
				while (iterator.hasNext()) {
					String element = (String)iterator.next();
					if (manifest.indexOf(element) < 0) {
						passed=false;
					}
				}
				
				tbc.assertTrue("Asserting the content of the Deployment Package manifest",passed);
	
				tbc.assertEquals(
								"Asserting the package type",
								DeploymentmoConstants.OSGI_DP,
								session.getNodeValue(DeploymentmoConstants.getDeployedExtPackageType(nodeId)).getInt());
				
				String[] signerChildren = session.getChildNodeNames(DeploymentmoConstants.getDeployedExtSigners(nodeId));
				
				if (signerChildren.length<=0) {
					tbc.fail("./OSGi/Deployment/Inventory/Deployed/[node_id]/Ext/Signers/[signer] does not exist");
				}
				String signer = signerChildren[0];
				
				tbc.assertEquals("Asserting the signer of the deployment package",
						DeploymentmoConstants.SIMPLE_DP_SIGNER,
						session.getNodeValue(DeploymentmoConstants.getDeployedExtSignersSignerId(nodeId, signer)).toString());
	
				
				//Bundle "bundles.tb1"
				Bundle bundle1 = tbc.getBundle(DeploymentmoConstants.SIMPLE_FIX_PACK_BUNDLE1_SYMBNAME);
				tbc.assertNotNull("The bundle was installed in the framework", bundle1);
				String bundleId = bundle1.getBundleId() + "";
				
				tbc.assertTrue("Asserting that the bundle id is the same as the specified",
						session.isNodeUri(DeploymentmoConstants.getDeployedExtBundlesBundleId(nodeId, bundleId)));
				
				tbc.assertEquals("Asserting bundle's state",
						DeploymentmoConstants.SIMPLE_FIX_PACK_BUNDLE1_STATE,
						session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesState(nodeId, bundleId)).getInt());
				
				tbc.assertTrue("Asserting that the manifest of the first bundle is the same as the specified",
						session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesManifest(nodeId, bundleId)).toString().
						indexOf(DeploymentmoConstants.SIMPLE_FIX_PACK_BUNDLE1_MANIFEST) > -1);
				
				tbc.assertEquals("Asserting that the location of the first bundle is the same as the specified",
						DeploymentmoConstants.SIMPLE_FIX_PACK_BUNDLE1_LOCATION,
						session.getNodeValue(DeploymentmoConstants.getDeployedExtBundlesLocation(nodeId, bundleId)).toString());
	
				//Bundle "bundles.tb2"
				Bundle bundle2 = tbc.getBundle(DeploymentmoConstants.SIMPLE_DP_BUNDLE2_SYMBNAME);
				tbc.assertNull("Asserts that the bundle 2 was removed from the framework", bundle2);
	
				String[] children = session.getChildNodeNames(DeploymentmoConstants.getDeployedExtBundles(nodeId));
				tbc.assertTrue("Asserting that there is only one bundle node in DMT",children.length==1);
            }
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		} finally {
        	if (!nodeId.equals("")) {
        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
        	}
		    unprepare(dlota.getDlotaFile());
			tbc.resetCommandValues();
		}
	}
    /**
     * This test asserts that a bundle can be downloaded and successfully installed 
     * (result code 200)
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate011() {
    	tbc.log("#testDownloadAndInstallAndActivate011");
        assertResultCode(DeploymentmoConstants.SIMPLE_BUNDLE, 200);
    }
    
    /**
     * This test asserts that the result code is 250 if the installation was successful 
     * but with bundle start warning.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate012() {
    	tbc.log("#testDownloadAndInstallAndActivate012");
        assertResultCode(DeploymentmoConstants.BUNDLE_THROWS_EXCEPTION_DP, 250);
    }
    /**
     * This test asserts the result code is 401 if the user cancelled the download.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate013() {
        tbc.log("#testDownloadAndInstallAndActivate013");
        tbc.log("#Test case only valid if the user cancelled the download");
        assertResultCode(DeploymentmoConstants.SIMPLE_DP, 401);
    }

    /**
     * This test asserts the result code is 404 if the content is not acceptable
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate014() {
    	tbc.log("#testDownloadAndInstallAndActivate014");
        assertResultCode(DeploymentmoConstants.NOT_ACCEPTABLE_CONTENT, 404);
    }
    
    /**
     * This test asserts the result code 410 (Download Descriptor error) is sent when 
     * the device could not interpret the DLOTA Download Descriptor.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate015() {
        tbc.log("#testDownloadAndInstallAndActivate015");
        try {

            session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.PRINCIPAL,DeploymentmoConstants.DEPLOYMENT, DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.createInteriorNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST);

            session.setNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_URI, new DmtData(DeploymentmoConstants.SERVER + "www/invalid_dlota.xml"));
            session.setNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_ID, new DmtData(DeploymentmoConstants.SIMPLE_DP_NAME));
            session.setNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_ENVTYPE, new DmtData("OSGi.R4"));
            
            synchronized (tbc) {
            	session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);
                tbc.wait(DeploymentmoConstants.TIMEOUT);
            }

            tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_DOWNLOADANDINSTALLANDACTIVATE,null,new DmtData(410));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{ e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[]{DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST});
            tbc.resetCommandValues();
        }
    }
    
    /**
     * This test asserts the result code is 450 if the manifest is not the first file in the stream
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate016() {
        tbc.log("#testDownloadAndInstallAndActivate016");
        assertResultCode(DeploymentmoConstants.MANIFEST_NOT_1ST_FILE, 450);
    }
    
    /**
     * This test asserts the result code is 451 if the dp is missing a header.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate017() {
        tbc.log("#testDownloadAndInstallAndActivate017");
        assertResultCode(DeploymentmoConstants.MISSING_NAME_HEADER_DP, 451);
    }
    
    /**
     * This test asserts the result code is 453 if the fixpack target is missing.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate018() {
        tbc.log("#testDownloadAndInstallAndActivate018");
        assertResultCode(DeploymentmoConstants.FIX_PACK_LOWER_RANGE_DP, 453);
    }
    /**
     * This test asserts the result code is 454 if a bundle in the Deployment
     * Package is marked as an absent bundle but there is no such bundle in the
     * target deployment package.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate019() {
    	tbc.log("#testDownloadAndInstallAndActivate019");
        assertResultCode(DeploymentmoConstants.RESOURCE_PROCESSOR_DP,DeploymentmoConstants.SIMPLE_NO_BUNDLE_DP,DeploymentmoConstants.MISSING_BUNDLE_FIX_PACK, 454);
    }
    /**
     * This test asserts the result code is 455 if a resource in the deployment
     * package is marked as an absent resource but there is no such resource in
     * the target deployment package.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate020() {
    	tbc.log("#testDownloadAndInstallAndActivate020");
        assertResultCode(DeploymentmoConstants.RESOURCE_PROCESSOR_DP,DeploymentmoConstants.SIMPLE_NO_RESOURCE_DP,DeploymentmoConstants.MISSING_RESOURCE_FIX_PACK, 455);
    }
    
    /**
     * This test asserts the result code is 456 if signature authentication failed.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate021() {
        tbc.log("#testDownloadAndInstallAndActivate021");
        assertResultCode(DeploymentmoConstants.SIMPLE_UNSIGNED_DP, 456);
    }
    
    /**
     * This test asserts the result code is 457 if the bundle symbolic name is not 
     * the same as defined by the deployment package manifest.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate022() {
        tbc.log("#testDownloadAndInstallAndActivate022");
        assertResultCode(DeploymentmoConstants.SYMB_NAME_DIFFERENT_FROM_MANIFEST_DP, 457);
    }
    
    /**
     * This test asserts the result code is 458 if the matched resource processor service 
     * is a customizer from another deployment package.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate023() {
        tbc.log("#testDownloadAndInstallAndActivate023");
        assertResultCode(DeploymentmoConstants.RESOURCE_PROCESSOR_CUSTOMIZER, DeploymentmoConstants.RP_FROM_OTHER_DP,458);
    }
    
    /**
     * This test asserts the result code is 459 (no such resource). A resource was passed to a 
     * matched resource processor but the resource processor can not manage this resource.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate024() {
    	tbc.log("#testDownloadAndInstallAndActivate024");
    	prepare();
    	 
        String rp = executeNodeAndGetNewNodeName(DeploymentmoConstants.RP_THROWS_NO_SUCH_RESOURCE);
        //Installs a resource associated with the resource processor above
        String dp = executeNodeAndGetNewNodeName(DeploymentmoConstants.DP_INSTALLS_RESOURCE_FOR_RP4);
        try {
            synchronized (tbc) {
                //Uninstalls a resource associated with the resource processor above, so the rp throws an exception at dropped. 
            	session.execute(DeploymentmoConstants.getDeployedOperationsRemove(dp),null);
                tbc.wait(DeploymentmoConstants.TIMEOUT);
            }
            tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_DOWNLOADANDINSTALLANDACTIVATE,null,new DmtData(459));
            
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{ e.getClass().getName()}));
        } finally {
        	if (!rp.equals("")) {
        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(rp));
        	}
        	unprepare(null);
            tbc.resetCommandValues();
        }
    }

    /**
     * This test asserts the result code is 460 if a bundle with the same symbolic name already exists.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate025() {
    	tbc.log("#testDownloadAndInstallAndActivate025");
        assertResultCode(DeploymentmoConstants.SIMPLE_DP, DeploymentmoConstants.DP_CONTAINING_A_BUNDLE_FROM_OTHER_DP,460);
    }
    
    /**
     * This test asserts the result code is 461 if a side effect of any 
     * resource already exists.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate026() {
    	tbc.log("#testDownloadAndInstallAndActivate026");
        assertResultCode(DeploymentmoConstants.NON_CUSTOMIZER_RP,DeploymentmoConstants.SIMPLE_RESOURCE_DP, DeploymentmoConstants.DP_THROWS_RESOURCE_VIOLATION,461);
    }
    
    /**
     * This test asserts the result code is 462 if resource processor is not able to commit
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate027() {
    	tbc.log("#testDownloadAndInstallAndActivate027");
        assertResultCode(DeploymentmoConstants.RP_NOT_ABLE_TO_COMMIT, 462);
    }
    
    /**
     * This test asserts the result code is 464 if the resource processor 
     * required by the package is not found.
     *
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private void testDownloadAndInstallAndActivate028() {
    	tbc.log("#testDownloadAndInstallAndActivate028");
        assertResultCode(DeploymentmoConstants.SIMPLE_RESOURCE_DP, 464);
    }
    
    /**
     * This test asserts the result code is 465 if the Deployment Manager is
     * busy with other request, blocked deployment operation was not started,
     * because a time out encountered.
     * 
     * @spec 3.6.5.3 DownloadAndInstallAndActivate Command
     */
    private synchronized void testDownloadAndInstallAndActivate029() {
        tbc.log("#testDownloadAndInstallAndActivate029");
        TestingBlockingResourceProcessor testBlockRP = null;
        try {
            TestingArtifact artifact = tbc.getArtifact(DeploymentmoConstants.BLOCK_SESSION_RESOURCE_PROCESSOR);
            TestingDeploymentPackage testDP = artifact.getDeploymentPackage();
            
            worker1 = new SessionWorker(tbc,testDP);
            worker1.start();
            
            int count = 0;
            BundleListenerImpl listener = tbc.getListener();
            while ((count < DeploymentmoConstants.TIMEOUT) &&
                !((listener.getCurrentType() == BundleEvent.STARTED) && 
                (listener.getCurrentBundle().getSymbolicName().indexOf(DeploymentmoConstants.PID_RESOURCE_PROCESSOR3) != -1))) {
                count++;
                wait(1);
            }
            
            testBlockRP = (TestingBlockingResourceProcessor) tbc.getService(
                ResourceProcessor.class, "(service.pid=" + DeploymentmoConstants.PID_RESOURCE_PROCESSOR3 + ")");
            tbc.assertNotNull("Blocking Resource Processor was registered", testBlockRP);
            
            assertResultCode(DeploymentmoConstants.SIMPLE_DP, 465);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            if (testBlockRP != null) {
                testBlockRP.setReleased(true);
            }
        }
    }
 
    /**
     * @param ID Artifact name
     * @param dlotaFileName Path to DLOTA
     * @return true if the pre-condition is satisfied
     */
    private boolean preCondition(String ID, String dlotaFileName) {
        try {
            session.setNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_ID, new DmtData(ID));
            session.setNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_URI, new DmtData(DeploymentmoConstants.DLOTA_PATH + dlotaFileName));
            session.setNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_ENVTYPE, new DmtData(DeploymentmoConstants.ENVTYPE));
        } catch (DmtException e) {
            tbc.log("# Failed to set download node values");
            return false;
        }
        return true;
    }

    /**
     * Asserts the result code when only one deployment package is needed
     * @param dpCode The Deployment Package code
     * @param alertCode The alert code to be checked
     */
    private void assertResultCode(int dpCode,int alertCode) {
        TestingDlota dlota = null;
        prepare();
        try {
            TestingArtifact artifact = tbc.getArtifact(dpCode);

            String fileName = (artifact.isBundle())?artifact.getBundle().getFilename():artifact.getDeploymentPackage().getFilename();
            
            dlota = artifact.getDlota();
            tbc.generateDLOTA(dlota,artifact.isBundle());
            if (preCondition(fileName, dlota.getFilename())) {
                synchronized (tbc) {
                	session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);
                    tbc.wait(DeploymentmoConstants.TIMEOUT);
                }

                tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_DOWNLOADANDINSTALLANDACTIVATE,null,new DmtData(alertCode));
            } else {
                tbc.log("Precondition for this method was not satisfied");
            }
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            unprepare(dlota.getDlotaFile());
            tbc.resetCommandValues();
        }
    }
    /**
     * Asserts the result code when two deployment packages are needed
     * @param firstDpCode The first Deployment Package to be installed
     * @param secondDpCode The second Deployment Package to be installed
     * @param alertCode The alert code to be checked
     */
    private void assertResultCode(int firstDpCode,int secondDpCode,int alertCode) {
        TestingDlota dlota = null;
        TestingDlota dlota2 = null;
        prepare();
        String nodeId = "";
        try {
            TestingArtifact artifact = tbc.getArtifact(firstDpCode);
            String fileName = (artifact.isBundle())?artifact.getBundle().getFilename():artifact.getDeploymentPackage().getFilename();
            dlota = artifact.getDlota();
            tbc.generateDLOTA(dlota,artifact.isBundle());
            
            TestingArtifact artifact2 = tbc.getArtifact(secondDpCode);
            String fileName2 = (artifact2.isBundle())?artifact2.getBundle().getFilename():artifact2.getDeploymentPackage().getFilename();
            dlota2 = artifact2.getDlota();
            tbc.generateDLOTA(dlota2,artifact2.isBundle());
            
            if (preCondition(fileName, dlota.getFilename())) {
            	String[] initialChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);

            	synchronized (tbc) {
                	session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);
                    tbc.wait(DeploymentmoConstants.TIMEOUT);
                }

                String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                tbc.assertTrue("Asserts that the node was created",initialChildren.length+1==finalChildren.length);
                nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);

                if (preCondition(fileName2, dlota2.getFilename())) {
                    
                	synchronized (tbc) {
                        session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);
                        tbc.wait(DeploymentmoConstants.TIMEOUT);
                    }
                    tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_DOWNLOADANDINSTALLANDACTIVATE,null,new DmtData(alertCode));
                    
                } else {
                    tbc.fail("Precondition for this method was not satisfied");
                }
            } else {
                tbc.fail("Precondition for this method was not satisfied");
            }
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
        	if (!nodeId.equals("")) {
        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
        	}
            unprepare(dlota.getDlotaFile());
            if (null!=dlota2) {
            	dlota2.getDlotaFile().delete();
            }
            tbc.resetCommandValues();
        }
    	
    }
    /**
     * Asserts the result code when two deployment packages are needed
     * @param firstDpCode The first Deployment Package to be installed
     * @param secondDpCode The second Deployment Package to be installed
     * @param alertCode The alert code to be checked
     */
    private void assertResultCode(int firstDpCode,int secondDpCode,int thirdDpCode,int alertCode) {
    	//The first DP should install successfully, so it is removed at finally
    	String nodeId = "";
    	prepare();
    	try {
    		nodeId = executeNodeAndGetNewNodeName(firstDpCode);
    		session.close();
        	//The second DP should install successfully, so it is removed in the final of the method below
    		assertResultCode(secondDpCode, thirdDpCode, alertCode);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
    	} finally {
        	if (!nodeId.equals("")) {
        		tbc.executeRemoveNode(session,DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
        	}
        	unprepare(null);

    	}
    }
    

    private String executeNodeAndGetNewNodeName(int dpCode) {
        TestingDlota dlota = null;
        String nodeId = "";
        try {
            TestingArtifact artifact = tbc.getArtifact(dpCode);

            String fileName = (artifact.isBundle())?artifact.getBundle().getFilename():artifact.getDeploymentPackage().getFilename();
            
            dlota = artifact.getDlota();
            tbc.generateDLOTA(dlota,artifact.isBundle());
            if (preCondition(fileName, dlota.getFilename())) {
            	String[] initialChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                synchronized (tbc) {
                	session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);
                    tbc.wait(DeploymentmoConstants.TIMEOUT);
                }
                String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                tbc.assertTrue("Asserts that the node was created",initialChildren.length+1==finalChildren.length);
                nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);

            } else {
                tbc.log("Precondition for this method was not satisfied");
            }
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.resetCommandValues();
        }
        return nodeId;
    }
    
    
}
