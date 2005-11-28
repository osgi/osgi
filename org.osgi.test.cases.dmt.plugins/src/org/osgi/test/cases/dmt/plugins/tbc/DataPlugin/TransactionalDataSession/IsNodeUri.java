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
 * Mar 04, 2005  Andre Assad
 * 11		     Implement DMT Use Cases
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.DataPlugin.TransactionalDataSession;

import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.DataPlugin.TestDataPluginActivator;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>isNodeUri</code> method, 
 * according to MEG specification
 */
public class IsNodeUri {
	private DmtTestControl tbc;

	public IsNodeUri(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsNodeUri001();
		testIsNodeUri002();
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the call of isNodeUri to the correct plugin.
	 * 
	 * @spec ReadableDataSession.isNodeUri(String[])
	 */
	private void testIsNodeUri001() {
		DmtSession session = null;
		try {
			tbc.log("#testIsNodeUri001");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc.assertTrue("Asserts that DmtAdmin fowarded "+ TestDataPlugin.ISNODEURI
					+" to the correct plugin",session.isNodeUri(TestDataPluginActivator.INTERIOR_NODE));

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the call of isNodeUri to the correct plugin.
	 * 
	 * @spec ReadableDataSession.isNodeUri(String[])
	 */
	private void testIsNodeUri002() {
		DmtSession session = null;
		try {
			tbc.log("#testIsNodeUri002");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc.assertTrue("Asserts that DmtAdmin fowarded "+ TestDataPlugin.ISNODEURI
					+" to the correct plugin",!session.isNodeUri(TestDataPluginActivator.INEXISTENT_NODE));

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}
}
