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
 * Feb 14, 2005  Luiz Felipe Guimaraes
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 * Feb 24, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc3.tbc.MetaNode;

import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;

/**
 * This test case validates the implementation of <code>can</code> method of MetaNode, 
 * according to MEG specification
 */
public class Can {
	private DmtTestControl tbc;

	public Can(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testCan001();
        testCan002();
	}

	/**
	 * Verifies if a node returns true for all operations its MetaNode allows
	 * 
	 * @spec MetaNode.can(int) 
	 */
	private void testCan001() {
		DmtSession session = null;
		try {
		tbc.log("#testCan001");
		session = tbc.getDmtAdmin().getSession(
				TestMetaNodeDataPluginActivator.ROOT,
				DmtSession.LOCK_TYPE_SHARED);	
		
		MetaNode metanode = session.getMetaNode(TestMetaNodeDataPluginActivator.ROOT);
		
		tbc.assertTrue("Asserts can method with ADD as argument", metanode.can(MetaNode.CMD_ADD));
		tbc.assertTrue("Asserts can method with DELETE as argument", metanode.can(MetaNode.CMD_DELETE));
		tbc.assertTrue("Asserts can method with EXECUTE as argument", metanode.can(MetaNode.CMD_EXECUTE));
		tbc.assertTrue("Asserts can method with GET as argument", metanode.can(MetaNode.CMD_GET));
		tbc.assertTrue("Asserts can method with REPLACE as argument", metanode.can(MetaNode.CMD_REPLACE));
        
        tbc.assertTrue("Asserts can method returns false when the argument is an invalid value", !metanode.can(200));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
    
    /**
     * Asserts that, if getMetaNode returns null, all operations is valid
     * 
     * @spec MetaNode.can(int) 
     */
    private void testCan002() {
        DmtSession session = null;
        try {
        tbc.log("#testCan002");
        session = tbc.getDmtAdmin().getSession(
                TestMetaNodeDataPluginActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);   
        
        session.createInteriorNode(TestMetaNodeDataPluginActivator.INEXISTENT_NODE_WITHOUT_METANODE);
        tbc.pass("Asserts that if getMetaNode returns null, the add operation is allowed");
        
        session.deleteNode(TestMetaNodeDataPluginActivator.NODE_WITHOUT_METANODE);
        tbc.pass("Asserts that if getMetaNode returns null, the delete operation is allowed");
        
        session.execute(TestMetaNodeDataPluginActivator.NODE_WITHOUT_METANODE,null);
        tbc.pass("Asserts that if getMetaNode returns null, the exec operation is allowed");
        
        session.getChildNodeNames(TestMetaNodeDataPluginActivator.NODE_WITHOUT_METANODE);
        tbc.pass("Asserts that if getMetaNode returns null, the get operation is allowed");
        
        session.setNodeType(TestMetaNodeDataPluginActivator.NODE_WITHOUT_METANODE,"text/xml");
        tbc.pass("Asserts that if getMetaNode returns null, the exec operation is allowed");
        
        
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.closeSession(session);
        }
    }
}
