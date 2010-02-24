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
 * Ago 18, 2005  Andre Assad
 * 162           Refactoring
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
public class DeliveredSubtree {

    private DeploymentmoTestControl tbc;
    private DmtSession session;

    public DeliveredSubtree(DeploymentmoTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        try {
            session = openSession();
            testDeliveredSubtree001();
            testDeliveredSubtree002();
            testDeliveredSubtree003();
            testDeliveredSubtree004();
            testDeliveredSubtree005();
            testDeliveredSubtree006();
            testDeliveredSubtree007();
            testDeliveredSubtree008();
            testDeliveredSubtree009();
            testDeliveredSubtree010();
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * Open this test case session.
     */
    private DmtSession openSession() {
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DELIVERED,
                DmtSession.LOCK_TYPE_EXCLUSIVE);
        } catch (DmtException e) {
			tbc.fail("Failed to open the Testing Delivered Subtree session", e);
        }
        return session;
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission
     * of $/Deployment/Inventory/Delivered/&lt;node_id&gt; metanode.
     * 
     * @spec 3.6.3 Delivered, Table 3-8
     */
    private void testDeliveredSubtree001() {
        tbc.log("#testDeliveredSubtree001");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.SIMPLE_DP_DELIVERED);
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id] metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id] metanode has Node Type",
                    DmtData.FORMAT_NODE, metaNode.getFormat());


            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id] metanode has Cardinality 0..*",
                    metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id] metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
            
        } catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission
     * of $/Deployment/Inventory/Delivered/&lt;node_id&gt;/ID metanode.
     * 
     * @spec 3.6.3 Delivered, Table 3-8
     */
    private void testDeliveredSubtree002() {
        tbc.log("#testDeliveredSubtree002");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.SIMPLE_DP_DELIVERED_ID);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/ID metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/ID Download metanode has String Type",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/ID metanode has Cardinality 1",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/ID metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));

        } catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission
     * of $/Deployment/Inventory/Delivered/&lt;node_id&gt;/Data metanode.
     * 
     * @spec 3.6.3 Delivered, Table 3-8
     */
    private void testDeliveredSubtree003() {
        tbc.log("#testDeliveredSubtree003");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.SIMPLE_DP_DELIVERED_DATA);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Data metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Data Download metanode has Binary Type",
                    DmtData.FORMAT_BINARY, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Data metanode has Cardinality 1",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Data metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission
     * of $/Deployment/Inventory/Delivered/&lt;node_id&gt;/Descriptor metanode.
     * 
     * @spec 3.6.3 Delivered, Table 3-8
     */
    private void testDeliveredSubtree004() {
        tbc.log("#testDeliveredSubtree004");
        try {
            if (session.isNodeUri(DeploymentmoConstants.SIMPLE_DP_DELIVERED_DESCRIPTOR)) {
                MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.SIMPLE_DP_DELIVERED_DESCRIPTOR);
                
                tbc.assertEquals(
                        "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Descriptor metanode has Automatic Scope",
                        MetaNode.AUTOMATIC, metaNode.getScope());
                
                tbc.assertEquals(
                        "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Descriptor Download metanode has XML Type",
                        DmtData.FORMAT_XML, metaNode.getFormat());
                
                tbc.assertTrue(
                        "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Descriptor metanode has Cardinality 0 or 1",
                        metaNode.isZeroOccurrenceAllowed()
                            && metaNode.getMaxOccurrence() == 1);
                
                tbc.assertTrue(
                        "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Descriptor metanode has Get Permission",
                        metaNode.can(MetaNode.CMD_GET));

            } else {
                tbc.log("# " + DeploymentmoConstants.SIMPLE_DP_DELIVERED_DESCRIPTOR + " node does not exist. Metanode will not be tested.");
            }
        } catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission
     * of $/Deployment/Inventory/Delivered/&lt;node_id&gt;/Operations metanode.
     * 
     * @spec 3.6.3 Delivered, Table 3-8
     */
    private void testDeliveredSubtree005() {
        tbc.log("#testDeliveredSubtree005");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.SIMPLE_DP_DELIVERED_OPERATIONS);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations Download metanode has Node Type",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations metanode has Cardinality 1",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission and Execute Permission
     * of $/Deployment/Inventory/Delivered/&lt;node_id&gt;/Operations/Remove
     * metanode.
     * 
     * @spec 3.6.3 Delivered, Table 3-8
     */
    private void testDeliveredSubtree006() {
        tbc.log("#testDeliveredSubtree006");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.SIMPLE_DP_DELIVERED_OPERATIONS_REMOVE);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/Remove metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/Remove Download metanode has null Type",
                    DmtData.FORMAT_NULL, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/Remove metanode has Cardinality 1",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/Remove metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/Remove metanode has Execute Permission",
                    metaNode.can(MetaNode.CMD_EXECUTE));
        } catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission and Execute Permission
     * of $/Deployment/Inventory/Delivered/&lt;node_id&gt;/Operations/InstallAndActivate
     * metanode.
     * 
     * @spec 3.6.3 Delivered, Table 3-8
     */
    private void testDeliveredSubtree007() {
        tbc.log("#testDeliveredSubtree007");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.SIMPLE_DP_DELIVERED_OPERATIONS_INSTALLANDACTIVATE);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/InstallAndActivate metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/InstallAndActivate Download metanode has null Type",
                    DmtData.FORMAT_NULL, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/InstallAndActivate metanode has Cardinality 1",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/InstallAndActivate metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/InstallAndActivate metanode has Execute Permission",
                    metaNode.can(MetaNode.CMD_EXECUTE));
        } catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission
     * of $/Deployment/Inventory/Delivered/&lt;node_id&gt;/Operations/Ext
     * metanode.
     * 
     * @spec 3.6.3 Delivered, Table 3-8
     */
    private void testDeliveredSubtree008() {
        tbc.log("#testDeliveredSubtree008");
        try {
            if (session.isNodeUri(DeploymentmoConstants.SIMPLE_DP_DELIVERED_OPERATIONS_EXT)) {
	            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.SIMPLE_DP_DELIVERED_OPERATIONS_EXT);
	            
	            tbc.assertEquals(
	                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/Ext Download metanode has Node Type",
	                    DmtData.FORMAT_NODE, metaNode.getFormat());
	            
	            tbc.assertTrue(
	                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/Ext metanode has Cardinality 0 or 1",
	                    metaNode.isZeroOccurrenceAllowed()
	                        && metaNode.getMaxOccurrence() == 1);
	            
	            tbc.assertTrue(
	                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Operations/Ext metanode has Get Permission",
	                    metaNode.can(MetaNode.CMD_GET));
            
            } else {
                tbc.log("# "+DeploymentmoConstants.SIMPLE_DP_DELIVERED_OPERATIONS_EXT+" node does not exist");
            }
        } catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission
     * of $/Deployment/Inventory/Delivered/&lt;node_id&gt;/EnvType metanode.
     * 
     * @spec 3.6.3 Delivered, Table 3-8
     */
    private void testDeliveredSubtree009() {
        tbc.log("#testDeliveredSubtree009");
        try {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.SIMPLE_DP_DELIVERED_ENVTYPE);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/EnvType metanode has Automatic Scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/EnvType Download metanode has String Type",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/EnvType metanode has Cardinality 1",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/EnvType metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
        }
    }

    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission
     * of $/Deployment/Inventory/Delivered/&lt;node_id&gt;/Ext metanode.
     * 
     * @spec 3.6.3 Delivered, Table 3-8
     */
    private void testDeliveredSubtree010() {
        tbc.log("#testDeliveredSubtree010");
        try {
            if (session.isNodeUri(DeploymentmoConstants.SIMPLE_DP_DELIVERED_EXT)) {
            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.SIMPLE_DP_DELIVERED_EXT);
            
            tbc.assertEquals(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Ext Download metanode has Node Type",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Ext metanode has Cardinality 0 or 1 ",
                    metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Deployment/Inventory/Delivered/[node_id]/Ext metanode has Get Permission",
                    metaNode.can(MetaNode.CMD_GET));
            
            } else {
                tbc.log("# "+DeploymentmoConstants.SIMPLE_DP_DELIVERED_EXT+" node does not exist. Metanode will not be tested.");
            }
        } catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
        }
    }
}
