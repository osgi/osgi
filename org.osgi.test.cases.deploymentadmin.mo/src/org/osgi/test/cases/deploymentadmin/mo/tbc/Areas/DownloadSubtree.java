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

/**
 * @author Eduardo Oliveira 
 * 
 * This Test Class Validates the implementation of Mobile Tree for Deployment Management Object, 
 * according to MEG reference documentation.
 */
public class DownloadSubtree {

    private DeploymentmoTestControl tbc;
    private DmtSession session;

    public DownloadSubtree(DeploymentmoTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        try {
            prepare();
            testDownloadSubtree001();
            testDownloadSubtree002();
            testDownloadSubtree003();
            testDownloadSubtree004();
            testDownloadSubtree005();
            testDownloadSubtree006();
            testDownloadSubtree007();
            testDownloadSubtree008();
            testDownloadSubtree009();
        } finally {
            unprepare();
        }
    }

    /**
     * Clean up, so other test case are not impacted
     */
    private void unprepare() {
        tbc.cleanUp(session, new String[]{DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST});
        tbc.closeSession(session);
    }

    /**
     * Preparation for testing Download subtree metanodes
     */
    private void prepare() {
        try {
            session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD, DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.createInteriorNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST);
        } catch (DmtException e) {
            tbc.fail("Failed to prepare the "+DeploymentmoConstants.DEPLOYMENT_DOWNLOAD+" session");
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Add Permission, Get
     * Permission, and Delete Permission 
     * of $/Deployment/Download/&lt;node_id&gt; metanode.
     * 
     * @spec 3.6.2 Download Subtree, Table 3-7
     */
    private void testDownloadSubtree001() {
        tbc.log("#testDownloadSubtree001");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id] metanode has Dynamic Scope",
                    MetaNode.DYNAMIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]  metanode has Node Type",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id] metanode has Cardinality 0..*",
                    metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id] metanode has Add Permission",
                    metaNode.can(MetaNode.CMD_ADD));
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id] metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id] metanode has Delete Permission",
                    metaNode.can(MetaNode.CMD_DELETE));
            
        } catch (Exception e) {
            tbc.fail("Unexpected exception thrown: " + e.getClass().getName());
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get
     * Permission and Replace permission
     * of $/Deployment/Download/&lt;node_id&gt;/URI metanode.
     * 
     * @spec 3.6.2 Download Subtree, Table 3-7
     */
    private void testDownloadSubtree002() {
        tbc.log("#testDownloadSubtree002");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_URI);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]/URI metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]/URI metanode has String Type",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/URI metanode has Cardinality 1",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/URI metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/URI metanode has Replace Permission",
                    metaNode.can(MetaNode.CMD_REPLACE));
            
        } catch (Exception e) {
            tbc.fail("Unexpected exception thrown: " + e.getClass().getName());
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get
     * Permission and Replace permission
     * of $/Deployment/Download/&lt;node_id&gt;/ID metanode.
     * 
     * @spec 3.6.2 Download Subtree, Table 3-7
     */
    private void testDownloadSubtree003() {
        tbc.log("#testDownloadSubtree003");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_ID);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]/ID metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]/ID metanode has String Type",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/ID metanode has Cardinality 1",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/ID metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/ID metanode has Replace Permission",
                    metaNode.can(MetaNode.CMD_REPLACE));
            
        } catch (Exception e) {
            tbc.fail("Unexpected exception thrown: " + e.getClass().getName());
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission
     * of $/Deployment/Download/&lt;node_id&gt;/Operations metanode.
     * 
     * @spec 3.6.2 Download Subtree, Table 3-7
     */
    private void testDownloadSubtree004() {
        tbc.log("#testDownloadSubtree004");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_OPERATIONS);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]/Operations metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]/Operations metanode has Node Type",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/Operations metanode has Cardinality 1",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/Operations metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
            tbc.fail("Unexpected exception thrown: " + e.getClass().getName());
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get
     * Permission and Execute Permission
     * of $/Deployment/Download/&lt;node_id&gt;/Operations/DownloadAndInstallAndActivate metanode.
     * 
     * @spec 3.6.2 Download Subtree, Table 3-7
     */
    private void testDownloadSubtree005() {
        tbc.log("#testDownloadSubtree005");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_DOWN_INST_ACTIV);
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]/Operations/DownloadAndInstallAndActivate metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]/Operations/DownloadAndInstallAndActivate metanode has null Type",
                    DmtData.FORMAT_NULL, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/Operations/DownloadAndInstallAndActivate metanode has Cardinality 1",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/Operations/DownloadAndInstallAndActivate metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/Operations/DownloadAndInstallAndActivate metanode has Execute Permission",
                    metaNode.can(MetaNode.CMD_EXECUTE));
        } catch (Exception e) {
            tbc.fail("Unexpected exception thrown: " + e.getClass().getName());
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission of 
     * $/Deployment/Download/&lt;node_id&gt;/Operations/Ext metanode.
     * 
     * @spec 3.6.2 Download Subtree, Table 3-7
     */
    private void testDownloadSubtree006() {
        tbc.log("#testDownloadSubtree006");
        try {
            if (session.isNodeUri(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_OPERATIONS_EXT)) {
                MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_OPERATIONS_EXT);
                
                tbc.assertEquals(
                        "Asserts if $/Deployment/Download/[node_id]/Operations/Ext metanode has Node Value",
                        DmtData.FORMAT_NODE, metaNode.getFormat());
                
                tbc.assertTrue(
                        "Asserts if $/Deployment/Download/[node_id]/Operations/Ext metanode has Cardinality 0 or 1",
                        metaNode.isZeroOccurrenceAllowed()
                            && metaNode.getMaxOccurrence() == 1);
                
                tbc.assertTrue(
                        "Asserts if $/Deployment/Download/[node_id]/Operations/Ext metanode has Get Permission",
                        metaNode.can(MetaNode.CMD_GET));
                
            } else {
            	tbc.log("# "+DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_OPERATIONS_EXT + " node does not exist. Metanode will not be tested");
            }
        } catch (Exception e) {
            tbc.fail("Unexpected exception thrown: " + e.getClass().getName());
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission
     * of $/Deployment/Download/&lt;node_id&gt;/Status metanode.
     * 
     * @spec 3.6.2 Download Subtree, Table 3-7
     */
    private void testDownloadSubtree007() {
        tbc.log("#testDownloadSubtree007");
        try {
            if (!session.isNodeUri(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_STATUS)) {
                session.createLeafNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_STATUS);
            }
            
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_STATUS);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]/Status metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]/Status metanode has Integer Type",
                    DmtData.FORMAT_INTEGER, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/Status metanode has Cardinality 0 or 1",
                    metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/Status metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
            tbc.fail("Unexpected exception thrown: " + e.getClass().getName());
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get
     * Permission and Replace permission
     * of $/Deployment/Download/&lt;node_id&gt;/EnvType metanode.
     * 
     * @spec 3.6.2 Download Subtree, Table 3-7
     */
    private void testDownloadSubtree008() {
        tbc.log("#testDownloadSubtree008");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_ENVTYPE);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]/EnvType metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Download/[node_id]/EnvType metanode has String Type",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/EnvType metanode has Cardinality 1",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/EnvType metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Download/[node_id]/EnvType metanode has Replace Permission",
                    metaNode.can(MetaNode.CMD_REPLACE));
            
        } catch (Exception e) {
            tbc.fail("Unexpected exception thrown: " + e.getClass().getName());
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission
     * of $/Deployment/Download/&lt;node_id&gt;/Ext metanode.
     * 
     * @spec 3.6.2 Download Subtree, Table 3-7
     */
    private void testDownloadSubtree009() {
        tbc.log("#testDownloadSubtree009");
        try {
            if (session.isNodeUri(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_EXT)) {
                MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_EXT);
                
                tbc.assertEquals(
                        "Asserts if $/Deployment/Download/[node_id]/Ext metanode has Automatic Scope",
                        MetaNode.AUTOMATIC, metaNode.getScope());
                
                tbc.assertEquals(
                        "Asserts if $/Deployment/Download/[node_id]/Ext metanode has Node Type",
                        DmtData.FORMAT_NODE, metaNode.getFormat());
                
                tbc.assertTrue(
                        "Asserts if $/Deployment/Download/[node_id]/Ext metanode has Cardinality 0 or 1",
                        metaNode.isZeroOccurrenceAllowed()
                            && metaNode.getMaxOccurrence() == 1);
                
                tbc.assertTrue(
                        "Asserts if $/Deployment/Download/[node_id]/Ext metanode has Get Permission",
                        metaNode.can(MetaNode.CMD_GET));
                
            } else {
            	tbc.log("# "+DeploymentmoConstants.DEPLOYMENT_DOWNLOAD_TEST_EXT+" node does not exist. Metanode will not be tested");
            }
        } catch (Exception e) {
            tbc.fail("Unexpected exception thrown: " + e.getClass().getName());
        }
    }
}
