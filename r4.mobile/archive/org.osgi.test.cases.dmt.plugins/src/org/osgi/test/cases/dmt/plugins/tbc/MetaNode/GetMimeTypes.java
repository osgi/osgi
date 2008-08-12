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
 */

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jan 26, 2005  Leonardo Barros
 * 1             Implement TCK
 * ============  ==============================================================
 * Feb 25, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.MetaNode;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;

/**
 * This test case validates the implementation of <code>getMimeTypes</code> method of MetaNode, 
 * according to MEG specification

 */
public class GetMimeTypes {
	private DmtTestControl tbc;

	public GetMimeTypes(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetMimeTypes001();
        testGetMimeTypes002();
        testGetMimeTypes003();
        testGetMimeTypes004();
        testGetMimeTypes005();
	}

	/**
	 * Asserts that the correct value is returned from the MetaNode of the specified node
	 * 
	 * @spec MetaNode.getMimeTypes()
	 */
	private void testGetMimeTypes001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetMimeTypes001");
			session = tbc.getDmtAdmin().getSession(
					TestMetaNodeDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			MetaNode metanode = session
					.getMetaNode(TestMetaNodeDataPluginActivator.ROOT);

			tbc.assertEquals("Asserts getMimeTypes method",
					TestMetaNode.DEFAULT_MIME_TYPES, metanode.getMimeTypes());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, true);
		}
	}
    
    /**
     * If DmtSession.getMetaNode returns null for a node, DmtSession.createLeafNode can be called without throwing any exceptions
     * using the DmtSession.createLeafNode(String)
     * 
     * @spec MetaNode.getMimeTypes()
     */
    private void testGetMimeTypes002() {
        DmtSession session = null;
        try {
            tbc.log("#testGetMimeTypes002");
            session = tbc.getDmtAdmin().getSession(
                    TestMetaNodeDataPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createLeafNode(TestMetaNodeDataPluginActivator.INEXISTENT_LEAF_NODE_WITHOUT_METANODE);
            tbc.pass("If DmtSession.getMetaNode returns null for a node, DmtSession.createLeafNode can be called without throwing any exceptions");

        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                    + " [Message: " + e.getMessage() + "]");
        } finally {
            tbc.cleanUp(session, true);
        }
    }
    
    /**
     * If DmtSession.getMetaNode returns null for a node, DmtSession.createLeafNode can be called without throwing any exceptions
     * using the DmtSession.createLeafNode(String,DmtData)
     * 
     * @spec MetaNode.getMimeTypes()
     */
    private void testGetMimeTypes003() {
        DmtSession session = null;
        try {
            tbc.log("#testGetMimeTypes003");
            session = tbc.getDmtAdmin().getSession(
                    TestMetaNodeDataPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createLeafNode(TestMetaNodeDataPluginActivator.INEXISTENT_LEAF_NODE_WITHOUT_METANODE,null);
            tbc.pass("If DmtSession.getMetaNode returns null for a node, DmtSession.createLeafNode can be called without throwing any exceptions");

        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                    + " [Message: " + e.getMessage() + "]");
        } finally {
        	tbc.cleanUp(session, true);
        }
    }
    
    /**
     * If DmtSession.getMetaNode returns null for a node, DmtSession.createLeafNode can be called without throwing any exceptions
     * using the DmtSession.createLeafNode(String,DmtData,String)
     * 
     * @spec MetaNode.getMimeTypes()
     */
    private void testGetMimeTypes004() {
        DmtSession session = null;
        try {
            tbc.log("#testGetMimeTypes004");
            session = tbc.getDmtAdmin().getSession(
                    TestMetaNodeDataPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createLeafNode(TestMetaNodeDataPluginActivator.INEXISTENT_LEAF_NODE_WITHOUT_METANODE,null,null);
            tbc.pass("If DmtSession.getMetaNode returns null for a node, DmtSession.createLeafNode can be called without throwing any exceptions");

        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                    + " [Message: " + e.getMessage() + "]");
        } finally {
        	tbc.cleanUp(session, true);
        }
    }
    /**
     * If DmtSession.getMetaNode returns null for a node, DmtSession.setNodeType can be called 
     * without throwing any exceptions
     * 
     * @spec MetaNode.getMimeTypes()
     */
    private void testGetMimeTypes005() {
        DmtSession session = null;
        try {
            tbc.log("#testGetMimeTypes005");
            session = tbc.getDmtAdmin().getSession(
                    TestMetaNodeDataPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.setNodeType(TestMetaNodeDataPluginActivator.NODE_WITHOUT_METANODE,null);
            tbc.pass("If DmtSession.getMetaNode returns null for a node, DmtSession.createLeafNode can be called without throwing any exceptions");

        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                    + " [Message: " + e.getMessage() + "]");
        } finally {
        	tbc.cleanUp(session, true);
        }
    }

}
