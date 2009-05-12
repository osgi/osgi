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
 * Jun 08, 2005  Luiz Felipe Guimaraes
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc3.tbc.Others;

import info.dmtree.*;

import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;
import org.osgi.test.cases.dmt.tc3.tbc.Plugins.*;
 
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
			tbc.log("#testUseCases001");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(NewDataPluginActivator.INEXISTENT_NODE);
			session.createInteriorNode(FatalExceptionDataPluginActivator.INEXISTENT_NODE);
			session.close();
			tbc.assertEquals("Asserts that the order of the close() calls " +
					"are the reverse order of joining the session and that a " +
					"close() calls the commit() on each plugin that participates of the session",
					FatalExceptionDataPlugin.COMMIT + NewDataPlugin.COMMIT +
					FatalExceptionDataPlugin.CLOSE + NewDataPlugin.CLOSE,DmtConstants.TEMPORARY);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testUseCases002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(NewDataPluginActivator.INEXISTENT_NODE);
			session.createInteriorNode(FatalExceptionDataPluginActivator.INEXISTENT_NODE);
			session.commit();
			tbc.assertEquals("Asserts that the order of the commit() calls are the reverse order " +
					"of joining the session",
					FatalExceptionDataPlugin.COMMIT + NewDataPlugin.COMMIT,DmtConstants.TEMPORARY);			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testUseCases003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(NewDataPluginActivator.INEXISTENT_NODE);
			session.createInteriorNode(FatalExceptionDataPluginActivator.INEXISTENT_NODE);
			session.rollback();
			tbc.assertEquals("Asserts that the order of the rollback() calls are the reverse order " +
					"of joining the session",
					FatalExceptionDataPlugin.ROLLBACK + NewDataPlugin.ROLLBACK,DmtConstants.TEMPORARY);			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			tbc.log("#testUseCases004");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(NewDataPluginActivator.INEXISTENT_NODE);
			//The method below throws a fatal exception
			session.createLeafNode(FatalExceptionDataPluginActivator.INEXISTENT_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that if a plugin throws an exception, the order of the rollback() calls " +
					"are the reverse order of joining the session",
					FatalExceptionDataPlugin.ROLLBACK + NewDataPlugin.ROLLBACK + FatalExceptionDataPlugin.CLOSE + NewDataPlugin.CLOSE,DmtConstants.TEMPORARY);
			tbc.assertEquals("Asserts that when a fatal exception is thrown, the session becomes STATE_INVALID",DmtSession.STATE_INVALID,session.getState());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
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
			tbc.log("#testUseCases005");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			session.getNodeType(FatalExceptionDataPluginActivator.TEST_EXCEPTION_PLUGIN_ROOT);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that the session remains open for further commands " +
					"after a non-fatal plugin exception",
					DmtSession.STATE_OPEN,session.getState());		
			tbc.assertEquals("None fatal errors do not rollback the session.", "", DmtConstants.TEMPORARY);
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
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
			tbc.log("#testUseCases006");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(NewDataPluginActivator.INEXISTENT_NODE);
			session.createInteriorNode(FatalExceptionDataPluginActivator.INEXISTENT_NODE);
			//a plugin is unregistered while in use by a session
			fatalExceptionActivator.stop(tbc.getContext());

			//an DmtIllegalStateException must thrown
			session.getChildNodeNames(FatalExceptionDataPluginActivator.TEST_EXCEPTION_PLUGIN_ROOT);
			tbc.failException("",DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			tbc.assertEquals("Asserts that if a plugin is unregistered, rollback() is called in each plugin " +
				"that participates of the session (in reverse order)",
				NewDataPlugin.ROLLBACK + NewDataPlugin.CLOSE,DmtConstants.TEMPORARY);
			
			tbc.assertEquals("Asserts that when a fatal exception is thrown, the session becomes STATE_INVALID",DmtSession.STATE_INVALID,session.getState());
				
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
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
			tbc.log("#testUseCases007");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);
			session.close();
			
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT2,
					DmtSession.LOCK_TYPE_SHARED);
			tbc.pass("Asserts that a plugin can be associated with more than one node.");			
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.cleanUp(session,true);
		}

	}
    
}
