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

package org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.DmtReadOnly;

import org.osgi.service.dmt.*;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.TestDataPluginActivator;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtReadOnly#getNodeValue
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getMetaData<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetNodeValue {
	private DmtTestControl tbc;

	public GetNodeValue(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        testGetNodeValue001();
        testGetNodeValue002();
	}

	/**
	 * @testID testGetNodeValue001
	 * @testDescription Asserts that DmtAdmin correctly forwards the call of
	 *                  getNodeValue to the correct plugin.
	 */
	private void testGetNodeValue001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeValue001");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			DmtData nodeValue = session.getNodeValue(TestDataPluginActivator.LEAF_NODE);
			tbc.assertTrue("Asserts that DmtAdmin fowarded "+ TestDataPlugin.GETNODEVALUE
					+" to the correct plugin",nodeValue.equals(TestDataPlugin.GETNODEVALUE_VALUE));

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetNodeValue002
	 * @testDescription Forces a DmtException in order to check if
	 *                  DmtReadOnly#getNodeValue correctly forwards the
	 *                  exception to DmtAdmin.
	 */
	private void testGetNodeValue002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeValue002");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeValue(TestDataPluginActivator.LEAF_NODE_EXCEPTION);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.LEAF_NODE_EXCEPTION, e
					.getURI());			
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.FORMAT_NOT_SUPPORTED, e
					.getCode());
			tbc.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestDataPlugin.GETNODEVALUE)>-1);			
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
    
}
