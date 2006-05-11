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
 * July 18, 2005 Alexandre Santos
 * 11            Implement DMT Use Cases 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession;

import info.dmtree.Acl;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;

/**
 * @author Alexandre Santos
 * 
 * This test case validates the implementation of <code>nodeChanged</code> method, 
 * according to MEG specification
 */
public class NodeChanged {

	private DmtTestControl tbc;

	public NodeChanged(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testNodeChanged001();
	}

	/**
	 * Asserts that our plugin implementation is notified when the 
	 * given node has changed outside the scope of our plugin.
	 * 
	 * @spec ReadableDataSession.nodeChanged(String[])
	 */
	private void testNodeChanged001() {
		DmtSession session = null;
		try {
			tbc.log("#testNodeChanged001");
			String[] principal = { "www.cesar.org.br" };
			int[] perm = { info.dmtree.Acl.GET
					| info.dmtree.Acl.EXEC };

			Acl acl = new Acl(principal, perm);

			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			TestDataPlugin.setNodeChangedThrowsException(true);
			session.setNodeAcl(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION, acl);
			
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.INTERIOR_NODE_EXCEPTION, e
				.getURI());			
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.DATA_STORE_FAILURE, e
				.getCode());
			tbc.assertTrue(
					"Asserts that DmtAdmin fowarded the DmtException with the correct message. ",
					e.getMessage().indexOf(TestDataPlugin.NODECHANGED) > -1);

		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
			tbc.cleanAcl(TestDataPluginActivator.INTERIOR_NODE);
			TestDataPlugin.setNodeChangedThrowsException(false);
		}
	}

}
