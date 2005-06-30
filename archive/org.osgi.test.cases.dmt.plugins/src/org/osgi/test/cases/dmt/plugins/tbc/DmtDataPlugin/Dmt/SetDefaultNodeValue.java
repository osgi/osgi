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
 * Mar 02, 2005  Luiz Felipe Guimaraes
 * 11            Implement DMT Use Cases 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.Dmt;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.TestDataPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * @methodUnderTest org.osgi.service.dmt.Dmt#setDefaultNodeValue
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>setDefaultNodeValue<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class SetDefaultNodeValue {

	private DmtTestControl tbc;

	public SetDefaultNodeValue(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testSetDefaultNodeValue001();
		testSetDefaultNodeValue002();
	}

	/**
	 * @testID testSetDefaultNodeValue001
	 * @testDescription Asserts that DmtAdmin correctly forwards the call of
	 *                  DmtSession methods to the correct plugin.
	 */
	private void testSetDefaultNodeValue001() {
		DmtSession session = null;
		try {
			tbc.log("#testSetDefaultNodeValue001");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setDefaultNodeValue(TestDataPluginActivator.LEAF_NODE);
			tbc.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.SETDEFAULTNODEVALUE+" to the correct plugin",TestDataPlugin.SETDEFAULTNODEVALUE,DmtTestControl.TEMPORARY);

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY = "";

		}
		
	}

	/**
	 * @testID testSetDefaultNodeValue002
	 * @testDescription Forces a DmtException in order to check if Dmt#rollback
	 *                  correctly forwards the exception to DmtAdmin.
	 */
	private void testSetDefaultNodeValue002() {
		DmtSession session = null;
		try {
			tbc.log("#testSetDefaultNodeValue002");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);	
			session.setDefaultNodeValue(TestDataPluginActivator.LEAF_NODE_EXCEPTION);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.LEAF_NODE_EXCEPTION, e
					.getURI());			
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.ALERT_NOT_ROUTED, e
					.getCode());
			tbc.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestDataPlugin.SETDEFAULTNODEVALUE)>-1);	
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
}
