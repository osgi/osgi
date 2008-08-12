/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved. Implementation
 * of certain elements of the OSGi Specification may be subject to third party
 * intellectual property rights, including without limitation, patent rights
 * (such a third party may or may not be a member of the OSGi Alliance). The
 * OSGi Alliance is not responsible and shall not be held responsible in any
 * manner for identifying or failing to identify any or all such third party
 * intellectual property rights. This document and the information contained
 * herein are provided on an "AS IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO ANY WARRANTY
 * THAT THE USE OF THE INFORMATION HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY
 * IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN
 * NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF
 * BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT,
 * INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL
 * DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE INFORMATION
 * CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ============  ==============================================================
 * Jun 20, 2005  Eduardo Oliveira
 * 97            Implement MEGTCK for the DeploymentMO Spec
 * ============  ==============================================================
 * Jul 14, 2005  Eduardo Oliveira
 * 162           Implement MEGTCK for the DeploymentMO Spec
 * ============  ==============================================================
 */

package org.osgi.test.cases.deploymentadmin.mo.tbc.Areas;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;

import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoConstants;
import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoTestControl;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.MessagesConstants;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingArtifact;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingDeploymentPackage;
import org.osgi.test.cases.deploymentadmin.mo.tbc.util.TestingDlota;

/**
 * @author Eduardo Oliveira 
 * 
 * This Test Class Validates the implementation of Mobile Tree for Deployment Management Object, 
 * according to MEG reference documentation.
 */
public class DeployedSubtree {

	private DeploymentmoTestControl	tbc;
    private String nodeId;
    private String bundleId;

	public DeployedSubtree(DeploymentmoTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        try {
            prepare();
            testDeployedSubtree001();
            testDeployedSubtree002();
            testDeployedSubtree003();
            testDeployedSubtree004();
            testDeployedSubtree005();
            testDeployedSubtree006();
            testDeployedSubtree007();
            testDeployedSubtree008();
            testDeployedSubtree009();
            testDeployedSubtree010();
            testDeployedSubtree011();
            testDeployedSubtree012();
            testDeployedSubtree013();
            testDeployedSubtree014();
            testDeployedSubtree015();
            testDeployedSubtree016();
            testDeployedSubtree017();
            testDeployedSubtree018();
        } finally {
            unprepare();
        }
	}

	/**
     * Clean up so other test cases are not affected
     */
    private void unprepare() {
        DmtSession session=null;
        try {
            session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.DEPLOYMENT, DmtSession.LOCK_TYPE_EXCLUSIVE);
            String nodeUri =DeploymentmoConstants.getDeployedOperationsRemove(nodeId);
            if (session.isNodeUri(nodeUri)) {
                String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                
                synchronized (tbc) {
                    session.execute(DeploymentmoConstants.getDeployedOperationsRemove(nodeId), null);
                    tbc.wait(DeploymentmoConstants.TIMEOUT);
                }

                String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
                if (initialChildren.length<=finalChildren.length) {
                    tbc.log("The node "+ nodeUri + " was not removed, many tests will be affected");
                }
            }
        } catch (Exception e) {
	        tbc.fail(e.getMessage());
        } finally {
            tbc.closeSession(session);

        }
    }

    /**
     * Installs an Artifact to create the Deployed Subtree automatically
     */
 
    private void prepare() {
        TestingDlota dlota = null;
        DmtSession session=null;
        try {
            
            session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.DEPLOYMENT, DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.createInteriorNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST);
            TestingArtifact artifact = tbc.getArtifact(DeploymentmoConstants.SIMPLE_DP);
            TestingDeploymentPackage dp = artifact.getDeploymentPackage();
            dlota = artifact.getDlota();
            tbc.generateDLOTA(dlota);
            session.setNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_ID, new DmtData(dp.getFilename()));
            session.setNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_URI, new DmtData(DeploymentmoConstants.DLOTA_PATH + artifact.getDlota().getFilename()));
            session.setNodeValue(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_ENVTYPE, new DmtData(DeploymentmoConstants.ENVTYPE));                

            String[] initialChildren = session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);
            
            synchronized (tbc) {
                session.execute(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV, null);
                tbc.wait(DeploymentmoConstants.TIMEOUT);
            }

            String[] finalChildren= session.getChildNodeNames(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);

            tbc.assertTrue("Asserts that the node was created",initialChildren.length+1==finalChildren.length);
            
            nodeId = DeploymentmoTestControl.getNodeId(initialChildren,finalChildren);
			
            String[] children = session.getChildNodeNames(DeploymentmoConstants.getDeployedExtBundles(nodeId));
			if (children.length==0) {
			    tbc.fail("$/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/[bundle_id] was not created");
			} else {
			    bundleId = children[0];
			}
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[]{DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST});
            if (dlota.getDlotaFile() != null) {
                dlota.getDlotaFile().delete();
            }
            tbc.resetCommandValues();
        }
    }

 

    /**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt; metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree001() {
		tbc.log("#testDeployedSubtree001");
		DmtSession session = null;
		try {
		    session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.getDeployedNodeId(nodeId));
            
			tbc.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id] metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id] metanode has Node Type",
					DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id] metanode has Cardinality 0..*",
					metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id] metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/ID metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree002() {
		tbc.log("#testDeployedSubtree002");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedID(nodeId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/ID metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/ID metanode has String Type",
					DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/ID metanode has Cardinality 1",
					!metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);

			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/ID metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/EnvType metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree003() {
		tbc.log("#testDeployedSubtree003");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedEnvType(nodeId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/EnvType metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/EnvType Download metanode has String Type",
					DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/EnvType metanode has Cardinality 1",
					!metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/EnvType metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));
		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Operations metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree004() {
		tbc.log("#testDeployedSubtree004");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedOperations(nodeId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Operations metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Operations Download metanode has Node Type",
					DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Operations metanode has Cardinality 1",
					!metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Operations metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission and Execute Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Operations/Remove
	 * metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree005() {
		tbc.log("#testDeployedSubtree005");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedOperationsRemove(nodeId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Operations/Remove metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Operations/Remove metanode has null Type",
					DmtData.FORMAT_NULL, metaNode.getFormat());
					
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Operations/Remove metanode has Cardinality 1",
					!metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);

			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Operations/Remove metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Operations/Remove metanode has Execute Permission",
					metaNode.can(MetaNode.CMD_EXECUTE));
		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Operations/Ext
	 * metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree006() {
		tbc.log("#testDeployedSubtree006");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			String nodeUri = DeploymentmoConstants.getDeployedOperationsExt(nodeId);
				if (session.isNodeUri(nodeUri)) {
					MetaNode metaNode = session.getMetaNode(nodeUri);
					tbc
						.assertEquals(
							"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Operations/Ext metanode has Node Type",
							DmtData.FORMAT_NODE, metaNode.getFormat());
					tbc
						.assertTrue(
							"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Operations/Ext metanode has Cardinality 0 or 1",
							metaNode.isZeroOccurrenceAllowed()
								&& metaNode.getMaxOccurrence() == 1);
	
					tbc
						.assertTrue(
							"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Operations/Ext metanode has Get Permission",
							metaNode.can(MetaNode.CMD_GET));
	
				} else {
				    tbc.log("# " + nodeUri + " node does not exist. Metanode will not be tested.");
				}
		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Ext metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree007() {
		tbc.log("#testDeployedSubtree007");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedExt(nodeId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext metanode has Node Type",
					DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext metanode has Cardinality 1",
					!metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Ext/Manifest metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree008() {
		tbc.log("#testDeployedSubtree008");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.getDeployedExtManifest(nodeId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Manifest metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Manifest metanode has String Type",
					DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Manifest metanode has Cardinality 0 or 1",
					metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Manifest metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Ext/Signers metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree009() {
		tbc.log("#testDeployedSubtree009");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedExtSigners(nodeId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Signers metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Signers metanode has Node Type",
					DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Signers metanode has Cardinality 0 or 1",
					metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Signers metanode Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Ext/Signers/&lt;signer&gt;
	 * metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree010() {
		tbc.log("#testDeployedSubtree010");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			
			String[] signerChildren = session.getChildNodeNames(DeploymentmoConstants.getDeployedExtSigners(nodeId));
			
			if (signerChildren.length<=0) {
				tbc.fail("./OSGi/Deployment/Inventory/Deployed/[node_id]/Ext/Signers/[signer] does not exist");
			}
			String signer = signerChildren[0];
			
			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedExtSignersSignerId(nodeId,signer));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Signers/<signer> metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Signers/<signer> metanode has String Type",
					DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Signers/<signer> metanode has Cardinality 1..*",
					!metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == Integer.MAX_VALUE);

			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Signers/<signer> metanode Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Ext/PackageType
	 * metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree011() {
		tbc.log("#testDeployedSubtree011");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedExtPackageType(nodeId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/PackageType metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/PackageType metanode has Integer Type",
					DmtData.FORMAT_INTEGER, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/PackageType metanode has Cardinality 1",
					!metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);

			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/PackageType metanode Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Ext/Bundles metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree012() {
		tbc.log("#testDeployedSubtree012");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);

			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedExtBundles(nodeId));

			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles metanode has Node Type",
					DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles metanode has Cardinality 0 or 1",
					metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);

			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission of
	 * $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Ext/Bundles/&lt;bundle_id&gt;
	 * metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree013() {
		tbc.log("#testDeployedSubtree013");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);

			
			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedExtBundlesBundleId(nodeId,bundleId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id> metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id> metanode has Node Type",
					DmtData.FORMAT_NODE, metaNode.getFormat());
			
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id> metanode has Cardinality 1..*",
					!metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id> metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	
	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Ext/Bundles/
	 * &lt;bundle_id&gt;/Location metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree014() {
		tbc.log("#testDeployedSubtree014");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedExtBundlesLocation(nodeId,bundleId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Location metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			
			tbc.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Location metanode has String Type",
					DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Location metanode has Cardinality 1",
					!metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Location metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Ext/Bundles/
	 * &lt;bundle_id&gt;/Manifest metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree015() {
		tbc.log("#testDeployedSubtree015");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);

			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedExtBundlesManifest(nodeId,bundleId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Manifest metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Manifest metanode has String Type",
					DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Manifest metanode has Cardinality 1",
					!metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);

			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Manifest metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));
			
		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Ext/Bundles/
	 * &lt;bundle_id&gt;/State metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree016() {
		tbc.log("#testDeployedSubtree016");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);

			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedExtBundlesState(nodeId,bundleId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/State metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/State metanode has Integer Type",
					DmtData.FORMAT_INTEGER, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/State metanode has Cardinality 1",
					!metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/State metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission
	 * of $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Ext/Bundles/
	 * &lt;bundle_id&gt;/Signers metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree017() {
		tbc.log("#testDeployedSubtree017");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			
			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedExtBundlesSigners(nodeId,bundleId));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Signers metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Signers metanode has Node Type",
					DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Signers metanode has Cardinality 0 or 1",
					metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == 1);
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Signers metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * This test asserts the Scope, Type, Cardinality, Get Permission  of
	 * $/Deployment/Inventory/Deployed/&lt;node_id&gt;/Ext/Bundles/&lt;bundle_id&gt;/Signers/
	 * &lt;signer&gt; metanode.
	 * 
	 * @spec 3.6.4 Deployed, Table 3-9
	 */
	private void testDeployedSubtree018() {
		tbc.log("#testDeployedSubtree018");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
				DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED, DmtSession.LOCK_TYPE_SHARED);
			
			String[] signerChildren = session.getChildNodeNames(DeploymentmoConstants.getDeployedExtBundlesSigners(nodeId, bundleId));
			
			if (signerChildren.length<=0) {
				tbc.fail("$/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Signers/<signer> does not exist");
			}
			String signer = signerChildren[0];
			
			MetaNode metaNode = session
				.getMetaNode(DeploymentmoConstants.getDeployedExtBundlesSignersSignerId(nodeId,bundleId,signer));
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Signers/<signer> metanode has Automatic Scope",
					MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
				.assertEquals(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Signers/<signer> metanode has String Type",
					DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
				.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Signers/<signer> metanode has Cardinality 1..*",
					!metaNode.isZeroOccurrenceAllowed()
						&& metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
			
			tbc.assertTrue(
					"Asserts if $/Deployment/Inventory/Deployed/[node_id]/Ext/Bundles/<bundle_id>/Signers/<signer> metanode has Get Permission",
					metaNode.can(MetaNode.CMD_GET));
			
			
		} catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
		} finally {
		    tbc.closeSession(session);
		}
	}
	
}
