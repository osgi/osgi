/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

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

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

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
	public void testCan001() {
		DmtSession session = null;
		try {
		DefaultTestBundleControl.log("#testCan001");
		session = tbc.getDmtAdmin().getSession(
				TestMetaNodeDataPluginActivator.ROOT,
				DmtSession.LOCK_TYPE_SHARED);	
		
		MetaNode metanode = session.getMetaNode(TestMetaNodeDataPluginActivator.ROOT);
		
		TestCase.assertTrue("Asserts can method with ADD as argument", metanode.can(MetaNode.CMD_ADD));
		TestCase.assertTrue("Asserts can method with DELETE as argument", metanode.can(MetaNode.CMD_DELETE));
		TestCase.assertTrue("Asserts can method with EXECUTE as argument", metanode.can(MetaNode.CMD_EXECUTE));
		TestCase.assertTrue("Asserts can method with GET as argument", metanode.can(MetaNode.CMD_GET));
		TestCase.assertTrue("Asserts can method with REPLACE as argument", metanode.can(MetaNode.CMD_REPLACE));
        
        TestCase.assertTrue("Asserts can method returns false when the argument is an invalid value", !metanode.can(200));
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
    
    /**
     * Asserts that, if getMetaNode returns null, all operations is valid
     * 
     * @spec MetaNode.can(int) 
     */
    public void testCan002() {
        DmtSession session = null;
        try {
        DefaultTestBundleControl.log("#testCan002");
        session = tbc.getDmtAdmin().getSession(
                TestMetaNodeDataPluginActivator.ROOT,
                DmtSession.LOCK_TYPE_EXCLUSIVE);   
        
        session.createInteriorNode(TestMetaNodeDataPluginActivator.INEXISTENT_NODE_WITHOUT_METANODE);
        DefaultTestBundleControl.pass("Asserts that if getMetaNode returns null, the add operation is allowed");
        
        session.deleteNode(TestMetaNodeDataPluginActivator.NODE_WITHOUT_METANODE);
        DefaultTestBundleControl.pass("Asserts that if getMetaNode returns null, the delete operation is allowed");
        
        session.execute(TestMetaNodeDataPluginActivator.NODE_WITHOUT_METANODE,null);
        DefaultTestBundleControl.pass("Asserts that if getMetaNode returns null, the exec operation is allowed");
        
        session.getChildNodeNames(TestMetaNodeDataPluginActivator.NODE_WITHOUT_METANODE);
        DefaultTestBundleControl.pass("Asserts that if getMetaNode returns null, the get operation is allowed");
        
        session.setNodeType(TestMetaNodeDataPluginActivator.NODE_WITHOUT_METANODE,"text/xml");
        DefaultTestBundleControl.pass("Asserts that if getMetaNode returns null, the exec operation is allowed");
        
        
        } catch (Exception e) {
        	DmtTestControl.failUnexpectedException(e);
        } finally {
            tbc.closeSession(session);
        }
    }
}
