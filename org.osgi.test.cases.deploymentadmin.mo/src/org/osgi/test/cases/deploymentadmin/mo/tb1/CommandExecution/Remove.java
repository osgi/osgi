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
 * Jul 1, 2005  Leonardo Barros
 * 97           Implement MEGTCK for the deployment MO Spec
 * ===========  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.mo.tb1.CommandExecution;

import info.dmtree.DmtData;
import info.dmtree.DmtSession;

import java.io.File;

import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoConstants;
import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoTestControl;
import org.osgi.test.cases.deploymentadmin.mo.tbc.TestInterface;

/**
 * @author Leonardo Barros
 * 
 * This Test Class Validates the implementation of Command Execution - Remove,
 * according to MEG reference documentation.
 */

public class Remove implements TestInterface {
	private DeploymentmoTestControl tbc;

	public Remove(DeploymentmoTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testRemove001();
		testRemove002();
		testRemove003();
		testRemove004();
	}

	/**
	 * This test asserts if a deployment package is removed when the node
	 * $/Deployment/Inventory/Deployed/node_id/Operations/Remove is executed
	 * It also tests if the alert sent is the expected. 
	 * 
	 * @spec 3.6.5.1 Remove Command
	 */
    private void testRemove001() {
        tbc.log("#testRemove001");
        
        DmtSession session = null;
        
        //Backups the artifact and, after the execute method removes, moves it again to the delivered area 
        String archiveName =DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_DP] +".dp";
        File fileSrc = DeploymentmoTestControl.copyArtifact(archiveName);
		File fileDestiny = DeploymentmoTestControl.getFile(archiveName);
		
		try {
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			//Installs a deployment package
			String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);

			 synchronized (tbc) {
					session.execute(DeploymentmoConstants.getDeliveredOperationsInstallAndActivate(DeploymentmoConstants.SIMPLE_DP), null);
					tbc.wait(DeploymentmoConstants.TIMEOUT);
             }

             String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
             tbc.assertTrue("Asserts that the Deployed subtree was created",initialChildren.length+1==finalChildren.length);
             String nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);
             
             //Uninstalls a deployment package
             initialChildren = finalChildren;
             
             tbc.resetCommandValues();
             
 			  synchronized (tbc) {
 	   			 session.execute(DeploymentmoConstants.getDeployedOperationsRemove(nodeId), null);
                  tbc.wait(DeploymentmoConstants.TIMEOUT);
              }

              finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
              tbc.assertTrue("Asserts that the Deployed subtree was removed ",initialChildren.length-1==finalChildren.length);
             
             tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_DEPLOYED_REMOVE,
                 DeploymentmoConstants.getDeployedNodeId(nodeId),
                 new DmtData(200));


		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName(), e);
		} finally {
			DeploymentmoTestControl.renameFileForced(fileSrc, fileDestiny);
		    tbc.closeSession(session);
			
		}
        
    }
    
    /**
	 * This test asserts that the artifact (deloyment package) is removed from this subtree when
	 * $/Deployment/Inventory/Delivered/[node_id]/Operations/Remove is executed.
	 * It also tests if the alert sent is the expected. 
	 * 
	 * @spec 3.6.5.1 Remove Command
	 */
    private void testRemove002() {
        tbc.log("#testRemove002");
        DmtSession session = null;
        //Backups the artifact and, after the execute method removes, moves it again to the delivered area 
        String archiveName =DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_DP] +".dp";
        File fileSrc = DeploymentmoTestControl.copyArtifact(archiveName);
		File fileDestiny = DeploymentmoTestControl.getFile(archiveName);
		
		try {
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			synchronized (tbc) {
        session.execute(DeploymentmoConstants.SIMPLE_DP_DELIVERED_OPERATIONS_REMOVE, null);
        tbc.wait(DeploymentmoConstants.TIMEOUT);
      }
			tbc.assertTrue("Asserts that the artifact is removed from this subtree when " +
					"$/Deployment/Inventory/Delivered/[node_id]/Operations/Remove is executed",
					!session.isNodeUri(DeploymentmoConstants.SIMPLE_DP_DELIVERED));

             tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_DELIVERED_REMOVE,
                 DeploymentmoConstants.getDeliveredNodeId(DeploymentmoConstants.SIMPLE_DP),
                 new DmtData(200));
             

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName(), e);
		} finally {
			DeploymentmoTestControl.renameFileForced(fileSrc, fileDestiny);
		    tbc.closeSession(session);
			
		}
        
    }
    
    /**
	 * This test asserts if a bundle is removed when the node
	 * $/Deployment/Inventory/Deployed/node_id/Operations/Remove is executed
	 * It also tests if the alert sent is the expected. 
	 * 
	 * @spec 3.6.5.1 Remove Command
	 */
    private void testRemove003() {
        tbc.log("#testRemove003");
        
        DmtSession session = null;
        
        //Backups the artifact and, after the execute method removes, copies it again to the delivered area 
        String archiveName =DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_BUNDLE];
        File fileSrc = DeploymentmoTestControl.copyArtifact(archiveName);
		File fileDestiny = DeploymentmoTestControl.getFile(archiveName);
		
		try {
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			//Installs a bundle
			String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);

			 synchronized (tbc) {
				 session.execute(DeploymentmoConstants.getDeliveredOperationsInstallAndActivate(DeploymentmoConstants.SIMPLE_BUNDLE), null);
                 tbc.wait(DeploymentmoConstants.TIMEOUT);
             }

             String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
             tbc.assertTrue("Asserts that the Deployed subtree was created",initialChildren.length+1==finalChildren.length);
             String nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);
             
             //Uninstalls a bundle
             initialChildren = finalChildren;
             tbc.resetCommandValues();
 			  synchronized (tbc) {
 	   			 session.execute(DeploymentmoConstants.getDeployedOperationsRemove(nodeId), null);
                  tbc.wait(DeploymentmoConstants.TIMEOUT);
              }

              finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
              tbc.assertTrue("Asserts that the Deployed subtree was removed ",initialChildren.length-1==finalChildren.length);
             
             
             tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_DEPLOYED_REMOVE,
                 DeploymentmoConstants.getDeployedNodeId(nodeId),
                 new DmtData(200));


		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName(), e);
		} finally {
			DeploymentmoTestControl.renameFileForced(fileSrc, fileDestiny);
		    tbc.closeSession(session);
			
		}
        
    }
    /**
	 * This test asserts that the artifact (bundle) is removed from this subtree when
	 * $/Deployment/Inventory/Delivered/[node_id]/Operations/Remove is executed.
	 * It also tests if the alert sent is the expected. 
	 * 
	 * @spec 3.6.5.1 Remove Command
	 */
    private void testRemove004() {
        tbc.log("#testRemove004");
        DmtSession session = null;
        
        //Backups the artifact and, after the execute method removes, copies it again to the delivered area 
        String archiveName =DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_BUNDLE];
        File fileSrc = DeploymentmoTestControl.copyArtifact(archiveName);
		File fileDestiny = DeploymentmoTestControl.getFile(archiveName);
		
		try {
			session = tbc.getDmtAdmin().getSession(
					DeploymentmoConstants.PRINCIPAL,
					DeploymentmoConstants.OSGI_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			
			String bundleNode = DeploymentmoConstants.DEPLOYMENT_INVENTORY_DELIVERED
			+ "/" + DeploymentmoConstants.MAP_CODE_TO_ARTIFACT[DeploymentmoConstants.SIMPLE_BUNDLE];

			String bundleRemoveNode = bundleNode + "/Operations/Remove";

      synchronized (tbc) {
        session.execute(bundleRemoveNode, null);
        tbc.wait(DeploymentmoConstants.TIMEOUT);
      }
			tbc.assertTrue("Asserts that the artifact is removed from this subtree when " +
					"$/Deployment/Inventory/Delivered/[node_id]/Operations/Remove is executed",
					!session.isNodeUri(bundleNode));
             tbc.assertAlertValues(DeploymentmoConstants.ALERT_TYPE_DELIVERED_REMOVE,
                 DeploymentmoConstants.getDeliveredNodeId(DeploymentmoConstants.SIMPLE_BUNDLE),
                 new DmtData(200));


		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName(), e);
		} finally {
			DeploymentmoTestControl.renameFileForced(fileSrc, fileDestiny);
		    tbc.closeSession(session);
			
		}
        
    }
}
