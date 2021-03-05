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
 * Jun 20, 2005  Luiz Felipe Guimaraes
 * 1             Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.NonAtomic.TestNonAtomicPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ReadOnly.TestReadOnlyPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>setDefaultNodeValue</code> method of DmtSession, 
 * according to MEG specification.
 */
public class SetDefaultNodeValue implements TestInterface {
	private DmtTestControl tbc;

	public SetDefaultNodeValue(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
	    //DmtException.METADATA_MISMATCH is tested in org.osgi.test.cases.dmt.plugins.tbc.MetaNode.MetaData.MetaData.java
	    //DmtException.INVALID_URI,URI_TOO_LONG,PERMISSION_DENIED are tested in org.osgi.test.cases.dmt.main.tb1.DmtSession.TestExceptions.java
	    //DmtIllegalStateException (timeout or closed session) is tested in org.osgi.test.cases.dmt.main.tb1.TestExceptions.java
	    //SecurityException is tested in org.osgi.test.cases.dmt.main.tb1.DmtSession.TestExceptions.java
        prepare();
		testSetDefaultNodeValue001();
		testSetDefaultNodeValue002();
		testSetDefaultNodeValue003();
		testSetDefaultNodeValue004();
		testSetDefaultNodeValue005();
		testSetDefaultNodeValue006();
		testSetDefaultNodeValue007();
		testSetDefaultNodeValue008();
		testSetDefaultNodeValue009();
		testSetDefaultNodeValue010();
		testSetDefaultNodeValue011();
	}
	
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown 
	 * if nodeUri points to a non-existing node 
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testSetDefaultNodeValue001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setDefaultNodeValue(TestExecPluginActivator.INEXISTENT_NODE);
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is NODE_NOT_FOUND",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that no exception is thrown 
	 * if the specified node is an interior node 
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testSetDefaultNodeValue002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setDefaultNodeValue(TestExecPluginActivator.INTERIOR_NODE);
			DefaultTestBundleControl.pass("Asserts that no Exception is thrown if nodeUri is an interior node and DmtSession.setDefaultNodeValue(String) is called");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);		
		} finally {
			tbc.closeSession(session);
		}
	}
	

    
	/**
	 * This method asserts that setDefaultNodeValue is executed when the right Acl is set (Remote) 
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testSetDefaultNodeValue003");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.LEAF_NODE, DmtConstants.PRINCIPAL, Acl.REPLACE | Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.LEAF_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			session.setDefaultNodeValue(TestExecPluginActivator.LEAF_NODE);
			DefaultTestBundleControl.pass("setDefaultNodeValue correctly executed");
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.LEAF_NODE);
            
		}
	}
	/**
	 * This method asserts that setDefaultNodeValue is executed when the right DmtPermission is set 
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testSetDefaultNodeValue004");
            tbc.setPermissions(new PermissionInfo[] {
                    new PermissionInfo(DmtPermission.class.getName(), DmtConstants.OSGi_ROOT, DmtPermission.GET),
                    new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.REPLACE)});
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setDefaultNodeValue(TestExecPluginActivator.LEAF_NODE);
			DefaultTestBundleControl.pass("setDefaultNodeValue correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
			tbc.cleanUp(session, null);
		}
	}
	
	/**
	 * This method asserts that a relative URI can be used with setDefaultNodeValue
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testSetDefaultNodeValue005");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.setDefaultNodeValue(TestExecPluginActivator.LEAF_RELATIVE);

			DefaultTestBundleControl.pass("A relative URI can be used with setDefaultNodeValue.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts if DmtIllegalStateException is thrown if this method is called 
	 * when the session is LOCK_TYPE_SHARED
	 * 
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testSetDefaultNodeValue006");
			session = tbc.getDmtAdmin().getSession(
				TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_SHARED);
			session.setDefaultNodeValue(TestExecPluginActivator.LEAF_NODE);
			DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			DefaultTestBundleControl.pass("DmtIllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin is read-only
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testSetDefaultNodeValue007");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setDefaultNodeValue(TestReadOnlyPluginActivator.LEAF_NODE);
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin 
	 * does not support non-atomic writing
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue008() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testSetDefaultNodeValue008");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setDefaultNodeValue(TestNonAtomicPluginActivator.LEAF_NODE);
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
    /**
     * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
     * if the session is atomic and the plugin is read-only 
     * 
     * @spec DmtSession.setDefaultNodeValue(String)
     */
    private void testSetDefaultNodeValue009() {
        DmtSession session = null;
        try {
			DefaultTestBundleControl.log("#testRenameNode009");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.setDefaultNodeValue(TestReadOnlyPluginActivator.LEAF_NODE);
            DefaultTestBundleControl.failException("", DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserting that DmtException code is TRANSACTION_ERROR",
                    DmtException.TRANSACTION_ERROR, e.getCode());
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtException.class, e);
        } finally {
            tbc.closeSession(session);
        }
    }
    /**
     * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
     * if the session is atomic and the plugin does not support non-atomic writing
     * 
     * @spec DmtSession.setDefaultNodeValue(String)
     */
    private void testSetDefaultNodeValue010() {
        DmtSession session = null;
        try {
			DefaultTestBundleControl.log("#testRenameNode010");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.setDefaultNodeValue(TestNonAtomicPluginActivator.LEAF_NODE);
            DefaultTestBundleControl.failException("", DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserting that DmtException code is TRANSACTION_ERROR",
                    DmtException.TRANSACTION_ERROR, e.getCode());
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtException.class, e);
        } finally {
            tbc.closeSession(session);
        }
    }
	
	/**
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue011() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testSetDefaultNodeValue011");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.setDefaultNodeValue("");

			DefaultTestBundleControl.pass("Asserts that an empty string as relative URI means the root " +
				"URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
}
