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
 * Jun 08, 2005  Luiz Felipe Guimaraes
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc3.tbc.Others;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;
import org.osgi.test.cases.dmt.tc3.tbc.Plugins.FatalExceptionDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.Plugins.FatalExceptionDataPluginActivator;
import org.osgi.test.cases.dmt.tc3.tbc.Plugins.NewDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.Plugins.NewDataPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;
 
public class UseCases {

	private DmtTestControl tbc;

	public UseCases(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testUseCases001();
		testUseCases002();
		testUseCases003();
		testUseCases004();
		testUseCases005();
		testUseCases006();
		testUseCases007();
	}

	/**
	 * Asserts that the order of the close() calls are the reverse order of joining 
     * the session and that a close() calls the commit() on each plugin that participates 
     * of the session
     * 
     * @spec 117.6.6 Plugins and Transactions
	 */
	public void testUseCases001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testUseCases001");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(NewDataPluginActivator.INEXISTENT_NODE);
			session.createInteriorNode(FatalExceptionDataPluginActivator.INEXISTENT_NODE);
			session.close();
			TestCase.assertEquals("Asserts that the order of the close() calls " +
					"are the reverse order of joining the session and that a " +
					"close() calls the commit() on each plugin that participates of the session",
					FatalExceptionDataPlugin.COMMIT + NewDataPlugin.COMMIT +
					FatalExceptionDataPlugin.CLOSE + NewDataPlugin.CLOSE,DmtConstants.TEMPORARY);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session,true);
		}

	}
	
	/**
	 * Asserts that the order of the commit() calls are the reverse order of joining the session
     * 
     * @spec 117.6.6 Plugins and Transactions
	 */
	public void testUseCases002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testUseCases002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(NewDataPluginActivator.INEXISTENT_NODE);
			session.createInteriorNode(FatalExceptionDataPluginActivator.INEXISTENT_NODE);
			session.commit();
			TestCase.assertEquals("Asserts that the order of the commit() calls are the reverse order " +
					"of joining the session",
					FatalExceptionDataPlugin.COMMIT + NewDataPlugin.COMMIT,DmtConstants.TEMPORARY);			
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
            tbc.cleanUp(session,true);
		}

	}
	
	/**
	 * Asserts that the order of the rollback() calls are the reverse order of joining the session
     * 
     * @spec 117.6.6 Plugins and Transactions
	 */
	public void testUseCases003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testUseCases003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(NewDataPluginActivator.INEXISTENT_NODE);
			session.createInteriorNode(FatalExceptionDataPluginActivator.INEXISTENT_NODE);
			session.rollback();
			TestCase.assertEquals("Asserts that the order of the rollback() calls are the reverse order " +
					"of joining the session",
					FatalExceptionDataPlugin.ROLLBACK + NewDataPlugin.ROLLBACK,DmtConstants.TEMPORARY);			
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
            tbc.cleanUp(session,true);
		}

	}
	/**
	 * Asserts that if a plugin throws an exception, the order of the rollback() calls are 
     * the reverse order of joining the session
     * 
     * @spec 117.6.6 Plugins and Transactions
	 */
	public void testUseCases004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testUseCases004");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(NewDataPluginActivator.INEXISTENT_NODE);
			//The method below throws a fatal exception
			session.createLeafNode(FatalExceptionDataPluginActivator.INEXISTENT_NODE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that if a plugin throws an exception, the order of the rollback() calls " +
					"are the reverse order of joining the session",
					FatalExceptionDataPlugin.ROLLBACK + NewDataPlugin.ROLLBACK + FatalExceptionDataPlugin.CLOSE + NewDataPlugin.CLOSE,DmtConstants.TEMPORARY);
			TestCase.assertEquals("Asserts that when a fatal exception is thrown, the session becomes STATE_INVALID",DmtSession.STATE_INVALID,session.getState());
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
            tbc.cleanUp(session,true);
		}

	}
	
	/**
	 * Asserts that the session remains open for further commands after a non-fatal plugin exception
     * 
     * @spec 117.6.6 Plugins and Transactions
	 */
	public void testUseCases005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testUseCases005");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			session.getNodeType(FatalExceptionDataPluginActivator.TEST_EXCEPTION_PLUGIN_ROOT);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that the session remains open for further commands " +
					"after a non-fatal plugin exception",
					DmtSession.STATE_OPEN,session.getState());		
			TestCase.assertEquals("None fatal errors do not rollback the session.", "", DmtConstants.TEMPORARY);
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
            tbc.cleanUp(session,true);
		}

	}
	
	/**
	 * Asserts that if a plugin is unregistered, rollback() is called in each plugin that 
     * participates of the session (in reverse order) except by the plugin rolled back.
     * 
     * @spec 117.6.6 Plugins and Transactions
	 */
	public void testUseCases006() {
		DmtSession session = null;
		FatalExceptionDataPluginActivator fatalExceptionActivator = tbc.getFatalExceptionDataPluginActivator();
		try {
			DefaultTestBundleControl.log("#testUseCases006");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(NewDataPluginActivator.INEXISTENT_NODE);
			session.createInteriorNode(FatalExceptionDataPluginActivator.INEXISTENT_NODE);
			//a plugin is unregistered while in use by a session
			fatalExceptionActivator.stop(tbc.getContext());

			//an DmtIllegalStateException must thrown
			session.getChildNodeNames(FatalExceptionDataPluginActivator.TEST_EXCEPTION_PLUGIN_ROOT);
			DefaultTestBundleControl.failException("",DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			TestCase.assertEquals("Asserts that if a plugin is unregistered, rollback() is called in each plugin " +
				"that participates of the session (in reverse order)",
				FatalExceptionDataPlugin.ROLLBACK + NewDataPlugin.ROLLBACK + FatalExceptionDataPlugin.CLOSE + NewDataPlugin.CLOSE,DmtConstants.TEMPORARY);
			
			TestCase.assertEquals("Asserts that when a fatal exception is thrown, the session becomes STATE_INVALID",DmtSession.STATE_INVALID,session.getState());
				
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtIllegalStateException.class, e);
		} finally {
            tbc.cleanUp(session,true);
			try {
				fatalExceptionActivator.start(tbc.getContext());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}
	
	/**
	 * Asserts that a plugin can be associated with more than one node.
     * 
     * 117.6.3 Associating a Subtree
	 */
	public void testUseCases007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testUseCases007");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);
			session.close();
			
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT2,
					DmtSession.LOCK_TYPE_SHARED);
			DefaultTestBundleControl.pass("Asserts that a plugin can be associated with more than one node.");			
			
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
		} finally {
            tbc.cleanUp(session,true);
		}

	}
    
}
