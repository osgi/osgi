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
 * Feb 25, 2005  Luiz Felipe Guimaraes
 * 11            Implemente MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.Others;

import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtConstants;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.ExecPlugin.TestExecPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Plugins.OverlappingDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Plugins.OverlappingExecPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Plugins.OverlappingSubtreeDataPluginActivator;
import org.osgi.test.cases.dmt.plugins.tbc.Plugins.ToBeOverlappedDataPlugin;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates that overlappings plugins are not allowed. 
 * (Only ExecPlugin is allowed to overlap a DataPlugin)
 */
public class OverlappingPlugins {

	private DmtTestControl tbc;

	public OverlappingPlugins(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
	    testOverlappingPlugins001();
	    testOverlappingPlugins002();
	    testOverlappingPlugins003();
	    testOverlappingPlugins004();
	}

	/**
	 * Asserts that a DataPlugin cannot be overlapped by another DataPlugin.
	 * 
	 * @spec 117.6 Plugins
	 */
	private void testOverlappingPlugins001() {
		DmtSession session = null;
		try {
			tbc.log("#testOverlappingPlugins001");

			session = tbc.getDmtAdmin().getSession(OverlappingDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			String nodeTitle = session.getNodeTitle(OverlappingDataPluginActivator.ROOT);
			tbc.assertEquals("Asserts that a DataPlugin cannot be overlapped by other DataPlugin",TestDataPlugin.GETNODETITLE,nodeTitle);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session,true);
		}
	}
	/**
	 * Asserts that a it is not possible for a plugin to control the same, 
	 * or part of the same, subtree that another plugins controls.
	 * 
	 * @spec 117.6 Plugins
	 */
	private void testOverlappingPlugins002() {
		DmtSession session = null;
		try {
			tbc.log("#testOverlappingPlugins002");
			session = tbc.getDmtAdmin().getSession(OverlappingSubtreeDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			String nodeTitle = session.getNodeTitle(OverlappingSubtreeDataPluginActivator.ROOT);
			tbc.assertEquals("Asserts that a it is not possible for a plugin to control the same, or part of the same, " +
					"subtree that another plugins controls.",TestDataPlugin.GETNODETITLE,nodeTitle);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session,true);

		}
	}
	/**
	 * Asserts that an ExecPlugin can overlap a DataPlugin.
	 * 
	 * @spec 117.6 Plugins
	 */
	private void testOverlappingPlugins003() {
		DmtSession session = null;
		try {
			tbc.log("#testOverlappingPlugins003");

			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);
			String nodeTitle = session.getNodeTitle(TestExecPluginActivator.ROOT);
			tbc.assertEquals("Asserts that DmtAdmin allows that an ExecPlugin overlaps a DataPlugin",ToBeOverlappedDataPlugin.MESSAGE,nodeTitle);
			
			session.execute(TestExecPluginActivator.ROOT, "");
			tbc.assertEquals("Asserts that DmtAdmin allows that an ExecPlugin overlaps a DataPlugin",TestExecPlugin.EXECUTE,DmtConstants.TEMPORARY);
			
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session,true);
		}
	}
	
	/**
	 * Asserts that an ExecPlugin cannot overlap another ExecPlugin.
	 * 
	 * @spec 117.6 Plugins
	 */
	private void testOverlappingPlugins004() {
		DmtSession session = null;
		try {
			tbc.log("#testOverlappingPlugins004");

			session = tbc.getDmtAdmin().getSession(OverlappingExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);
			String nodeTitle = session.getNodeTitle(OverlappingExecPluginActivator.ROOT);
			tbc.assertEquals("Asserts that DmtAdmin does not allow that an ExecPlugin overlaps another ExecPlugin",ToBeOverlappedDataPlugin.MESSAGE,nodeTitle);
			
			session.execute(OverlappingExecPluginActivator.ROOT, "");
			tbc.assertEquals("Asserts that DmtAdmin does not allow that an ExecPlugin overlaps another ExecPlugin",TestExecPlugin.EXECUTE,DmtConstants.TEMPORARY);
			
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session,true);
		}
	}
}
