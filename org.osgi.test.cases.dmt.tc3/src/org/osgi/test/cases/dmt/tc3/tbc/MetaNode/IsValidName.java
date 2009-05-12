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
 * Jul 11, 2005  Luiz Felipe Guimarães
 * 1             Implement TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.MetaNode;

import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;

/**
 * This test case validates the implementation of <code>isValidName</code> method of MetaNode, 
 * according to MEG specification

 */
public class IsValidName {
	private DmtTestControl tbc;

	public IsValidName(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsValidName001();
		testIsValidName002();
	}

	/**
	 * Asserts that true is returned from the MetaNode of the specified node
	 * if the name passed is valid
	 * 
	 * @spec MetaNode.isValidName(String)
	 */
	public void testIsValidName001() {
		DmtSession session = null;
		try {
			tbc.log("#testIsValidName001");
			session = tbc.getDmtAdmin().getSession(
					TestMetaNodeDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			MetaNode metanode = session
					.getMetaNode(TestMetaNodeDataPluginActivator.ROOT);

			tbc.assertTrue("Asserts isValidName method",
					metanode.isValidName(TestMetaNode.DEFAULT_VALID_NAMES[0]));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	

	/**
	 * Asserts that false is returned from the MetaNode of the specified node
	 * if the name passed is invalid
	 * 
	 * @spec MetaNode.isValidName(String)
	 */
	public void testIsValidName002() {
		DmtSession session = null;
		try {
			tbc.log("#testIsValidName002");
			session = tbc.getDmtAdmin().getSession(
					TestMetaNodeDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			MetaNode metanode = session
					.getMetaNode(TestMetaNodeDataPluginActivator.ROOT);

			tbc.assertTrue("Asserts isValidName method",
					!metanode.isValidName("invalid"));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

}
