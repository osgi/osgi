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
 * Jul 14, 2005 Eduardo Oliveira
 * 147           Implement MEGTCK for the deployment MO Spec 
 * ===========  ============================================================== 
 */
package org.osgi.test.cases.deploymentadmin.mo.tbc.Areas;

import info.dmtree.DmtData;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;

import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoConstants;
import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoTestControl;

/**
 * @author Eduardo Oliveira
 * 
 * This Test Class Validates the implementation of
 * Mobile Tree for Deployment Management Object, according 
 * to MEG reference documentation.
 */
public class PermanentNodes {
	private DeploymentmoTestControl tbc;

	public PermanentNodes(DeploymentmoTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testPermanentNodes001();
		testPermanentNodes002();
		testPermanentNodes003();
		testPermanentNodes004();
		testPermanentNodes005();
        testPermanentNodes006();
	}

    
    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission of 
     * $/Deployment metanode.
     *
     * @spec 3.6 Deployment Management Object, Table 3-6
     */

	private void testPermanentNodes001() {

		tbc.log("#testPermanentNodes001");

		DmtSession session = null;

		try {
			session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.OSGI_ROOT, DmtSession.LOCK_TYPE_SHARED);

			MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT);

			tbc.assertEquals("Asserts if $/Deployment metanode has Permanet Scope", MetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals("Asserts if $/Deployment metanode has Node Value", DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc.assertTrue("Asserts if $/Deployment metanode has Cadinality 1", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);

			tbc.assertTrue("Asserts if $/Deployment metanode has Get Permission", metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
		} finally {
			tbc.closeSession(session);
		}
	}


    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission of 
     * $/Deployment/Download  metanode.
     *
     * @spec 3.6 Deployment Management Object, Table 3-6
     */

	private void testPermanentNodes002() {

		tbc.log("#testPermanentNodes002");

		DmtSession session = null;

		try {

			session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.OSGI_ROOT, DmtSession.LOCK_TYPE_SHARED);

			MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_DOWNLOAD);

			tbc.assertEquals("Asserts if $/Deployment/Download metanode has Permanet Scope", MetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals("Asserts if $/Deployment/Download metanode has Node Value", DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc.assertTrue("Asserts if $/Deployment/Download metanode has Cadinality 1", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);

			tbc.assertTrue("Asserts if $/Deployment/Download metanode has Get Permission", metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
		} finally {
			tbc.closeSession(session);
		}
	}


    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission of 
     * $/Deployment/Inventory metanode.
     *
     * @spec 3.6 Deployment Management Object, Table 3-6
     */

	private void testPermanentNodes003() {

		tbc.log("#testPermanentNodes003");

		DmtSession session = null;

		try {

			session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.OSGI_ROOT, DmtSession.LOCK_TYPE_SHARED);

			MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_INVENTORY);

			tbc.assertEquals("Asserts if $/Deployment/Inventory metanode has Permanet Scope", MetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals("Asserts if $/Deployment/Inventory metanode has Node Value", DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc.assertTrue("Asserts if $/Deployment/Inventory metanode has Cadinality 1", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);

			tbc.assertTrue("Asserts if $/Deployment/Inventory metanode has Get Permission", metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
		} finally {
			tbc.closeSession(session);
		}
	}


    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission of 
     * $/Deployment/Inventory/Delivered metanode.
     *
     * @spec 3.6 Deployment Management Object, Table 3-6
     */

	private void testPermanentNodes004() {

		tbc.log("#testPermanentNodes004");

		DmtSession session = null;

		try {

			session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.OSGI_ROOT, DmtSession.LOCK_TYPE_SHARED);

			MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DELIVERED);

			tbc.assertEquals("Asserts if $/Deployment/Inventory/Delivered metanode has Permanet Scope", MetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals("Asserts if $/Deployment/Inventory/Delivered metanode has Node Value", DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc.assertTrue("Asserts if $/Deployment/Inventory/Delivered metanode has Cadinality 1", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);

			tbc.assertTrue("Asserts if $/Deployment/Inventory/Delivered metanode has Get Permission", metaNode.can(MetaNode.CMD_GET));


		} catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
		} finally {
			tbc.closeSession(session);
		}
	}

     /**
     * This test asserts the Scope, Type, Cardinality, Get Permission of 
     * $/Deployment/Inventory/Deployed metanode.
     *
     * @spec 3.6 Deployment Management Object, Table 3-6
     */

	private void testPermanentNodes005() {

		tbc.log("#testPermanentNodes005");

		DmtSession session = null;

		try {

			session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.OSGI_ROOT, DmtSession.LOCK_TYPE_SHARED);

			MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_INVENTORY_DEPLOYED);

			tbc.assertEquals("Asserts if $/Deployment/Inventory/Deployed metanode has Permanet Scope", MetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals("Asserts if $/Deployment/Inventory/Deployed metanode has Node Value", DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc.assertTrue("Asserts if $/Deployment/Inventory/Deployed metanode has Cadinality 1", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);

			tbc.assertTrue("Asserts if $/Deployment/Inventory/Deployed metanode has Get Permission", metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
		} finally {
			tbc.closeSession(session);
		}
	}
    
    /**
     * This test asserts the Scope, Type, Cardinality, Get Permission of 
     * $/Deployment/Ext metanode.
     *
     * @spec 3.6 Deployment Management Object, Table 3-6
     */

    private void testPermanentNodes006() {

        tbc.log("#testPermanentNodes006");

        DmtSession session = null;

        try {

            session = tbc.getDmtAdmin().getSession(DeploymentmoConstants.OSGI_ROOT, DmtSession.LOCK_TYPE_SHARED);

            if (session.isNodeUri(DeploymentmoConstants.DEPLOYMENT_EXT)) {
	            MetaNode metaNode = session.getMetaNode(DeploymentmoConstants.DEPLOYMENT_EXT);
	
	            tbc.assertEquals("Asserts if $/Deployment/Ext metanode has Permanent Scope", MetaNode.PERMANENT, metaNode.getScope());
	
	            tbc.assertEquals("Asserts if $/Deployment/Ext metanode has Node Value", DmtData.FORMAT_NODE, metaNode.getFormat());
	
	            tbc.assertTrue("Asserts if $/Deployment/Ext metanode has Cadinality 0 or 1", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
	
	            tbc.assertTrue("Asserts if $/Deployment/Ext metanode has Get Permission", metaNode.can(MetaNode.CMD_GET));
	        } else {
			    tbc.log("# " +DeploymentmoConstants.DEPLOYMENT_EXT + " node does not exist. Metanode will not be tested.");
			}
        } catch (Exception e) {
			tbc.fail("Unexpected exception thrown: " + e.getClass().getName(),
					e);
        } finally {
            tbc.closeSession(session);
        }
    }
    
}
