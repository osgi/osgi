/*
 * Copyright (c) OSGi Alliance (2004, 2020). All Rights Reserved.
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

package org.osgi.test.cases.dmt.tc3.tbc.ExecPlugin;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>execute<code> method, 
 * according to MEG specification
 */
public class Execute {

	private DmtTestControl tbc;

	public Execute(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
	    testExecute001();
		testExecute002();
		testExecute003();
		testExecute004();
		
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the call of execute method 
	 * to the correct plugin.
	 * 
	 * @spec ExecPlugin.execute(DmtSession,String[],String,String)
	 */
	public void testExecute001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testExecute001");
			String data = "data";
			String correlator = "correlator";
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.execute(TestExecPluginActivator.ROOT, correlator, data);
			TestCase.assertEquals("Asserts that DmtAdmin fowarded "+ TestExecPlugin.EXECUTE+" to the correct plugin",TestExecPlugin.EXECUTE,DmtConstants.TEMPORARY);
			TestCase.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",correlator,DmtConstants.PARAMETER_2);
			TestCase.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",data,DmtConstants.PARAMETER_3);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the DmtException thrown by the plugin
	 * 
	 * @spec ExecPlugin.execute(DmtSession,String[],String,String)
	 */
	public void testExecute002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testExecute002");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.execute(TestExecPluginActivator.INTERIOR_NODE_EXCEPTION,"test", "test");
			
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestExecPluginActivator.INTERIOR_NODE_EXCEPTION, e
					.getURI());			
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.CONCURRENT_ACCESS, e
					.getCode());
			TestCase.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestExecPlugin.EXECUTE)>-1);	
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}
	
	/**
	 * Asserts that DmtAdmin correctly forwards the call of execute method 
	 * to the correct plugin.
	 * 
	 * @spec ExecPlugin.execute(DmtSession,String[],String,String)
	 */
	public void testExecute003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testExecute003");
			String data = "data";

			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.execute(TestExecPluginActivator.ROOT, data);
			TestCase.assertEquals("Asserts that DmtAdmin fowarded "+ TestExecPlugin.EXECUTE+" to the correct plugin",TestExecPlugin.EXECUTE,DmtConstants.TEMPORARY);
			TestCase.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",data,DmtConstants.PARAMETER_3);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}

	/**
	 * Asserts that DmtAdmin correctly forwards the DmtException thrown by the plugin
	 * 
	 * @spec ExecPlugin.execute(DmtSession,String[],String,String)
	 */
	public void testExecute004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testExecute004");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.execute(TestExecPluginActivator.INTERIOR_NODE_EXCEPTION,"test");
			
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestExecPluginActivator.INTERIOR_NODE_EXCEPTION, e
					.getURI());			
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.CONCURRENT_ACCESS, e
					.getCode());
			TestCase.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestExecPlugin.EXECUTE)>-1);	
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
            tbc.cleanUp(session,true);
		}
	}
	
}
