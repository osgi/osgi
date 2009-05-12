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
 * Feb 25, 2005  Andre Assad
 * 11            Implement DMT Use Cases 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession;

import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>copy</code> method,
 * according to MEG specification
 */
public class Copy {

	private DmtTestControl tbc;

	public Copy(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testCopy001();
		testCopy002();
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the call of DmtSession methods 
	 * to the correct plugin.
	 * 
	 * @spec ReadWriteDataSession.copy(String[],String[],boolean)
	 */
	public void testCopy001() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy001");

			session = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.copy(TestDataPluginActivator.INTERIOR_NODE,
					TestDataPluginActivator.INEXISTENT_NODE, true);

			tbc.assertEquals("Asserts that DmtAdmin fowarded "
					+ TestDataPlugin.COPY
					+ " to the correct plugin",
					TestDataPlugin.COPY, DmtConstants.TEMPORARY);

			tbc.assertEquals("Asserts that DmtAdmin fowarded "
					+ TestDataPlugin.COPY
					+ " to the correct plugin",
					TestDataPluginActivator.INEXISTENT_NODE,
					DmtConstants.PARAMETER_2);

			tbc.assertEquals("Asserts that DmtAdmin fowarded "
					+ TestDataPlugin.COPY
					+ " to the correct plugin", String.valueOf(true),
					DmtConstants.PARAMETER_3);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the DmtException thrown by the plugin
	 * 
	 * @spec ReadWriteDataSession.copy(String[],String[],boolean)
	 */
	public void testCopy002() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy002");
			session = tbc.getDmtAdmin().getSession(
					TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.copy(TestDataPluginActivator.INTERIOR_NODE_EXCEPTION3,
					TestDataPluginActivator.INEXISTENT_NODE, true);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc
					.assertEquals(
							"Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ",
							TestDataPluginActivator.INTERIOR_NODE_EXCEPTION3, e
									.getURI());
			tbc
					.assertEquals(
							"Asserts that DmtAdmin fowarded the DmtException with the correct code: ",
							DmtException.URI_TOO_LONG, e.getCode());
			tbc
					.assertTrue(
							"Asserts that DmtAdmin fowarded the DmtException with the correct message. ",
							e.getMessage().indexOf(
									TestDataPlugin.COPY) > -1);
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}
}
