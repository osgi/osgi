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
 * Apr 17, 2006  Luiz Felipe Guimaraes
 * 283           [MEGTCK][DMT] DMT API changes
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc3.tbc.MetaNode;

import info.dmtree.DmtSession;
import info.dmtree.MetaNode;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;

/**
 * This test case validates the implementation of <code>getExtensionProperty</code> method of MetaNode, 
 * according to MEG specification

 */
public class GetExtensionProperty {
	private DmtTestControl tbc;

	public GetExtensionProperty(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetExtensionProperty001();
	}

	/**
	 * Asserts that the correct value is returned from the MetaNode of the specified node
	 * 
	 * @spec MetaNode.getExtensionProperty()
	 */
	private void testGetExtensionProperty001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetExtensionProperty001");
			session = tbc.getDmtAdmin().getSession(
					TestMetaNodeDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			MetaNode metanode = session
					.getMetaNode(TestMetaNodeDataPluginActivator.ROOT);

			tbc.assertEquals("Asserts getExtensionProperty method",
					TestMetaNode.DEFAULT_EXTENSION_PROPERTY, metanode
							.getExtensionProperty(""));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

}
