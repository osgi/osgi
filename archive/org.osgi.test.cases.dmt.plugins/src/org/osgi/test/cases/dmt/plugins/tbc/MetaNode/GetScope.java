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
 * This test case validates the implementation of <code>getScope</code> method of MetaNode, 
 * according to MEG specification

 */
public class GetScope {
	private DmtTestControl tbc;

	public GetScope(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetScope001();
        testGetScope002();
	}

	/**
	 * Asserts that the correct value is returned from the MetaNode of the specified node
	 * 
	 * @spec MetaNode.getScope()
	 */
	private void testGetScope001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetScope001");
			session = tbc.getDmtAdmin().getSession(
					TestMetaNodeDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			MetaNode metanode = session
					.getMetaNode(TestMetaNodeDataPluginActivator.ROOT);

			tbc.assertEquals("Asserts getScope method", MetaNode.DYNAMIC,
					metanode.getScope());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}
    
    /**
     * Asserts that a non-deletable node can disappear in a recursive DELETE operation issued on one of its parents. 
     * 
     * @spec MetaNode.getScope()
     */
    private void testGetScope002() {
        DmtSession session = null;
        try {
            tbc.log("#testGetScope002");
            session = tbc.getDmtAdmin().getSession(
                    TestMetaNodeDataPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.deleteNode(TestMetaNodeDataPluginActivator.PARENT_OF_NODE_THAT_CANNOT_BE_DELETED);
            tbc.pass("Asserts that a non-deletable node can disappear in a recursive DELETE operation issued on one of its parents.");
            
        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                    + " [Message: " + e.getMessage() + "]");
        } finally {
            tbc.closeSession(session);
        }
    }
}
