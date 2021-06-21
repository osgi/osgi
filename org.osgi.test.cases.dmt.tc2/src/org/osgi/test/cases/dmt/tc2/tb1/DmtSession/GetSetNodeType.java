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
 * Jan 31, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
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
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getNodeType, setNodeType</code> method, 
 * according to MEG specification
 */
public class GetSetNodeType implements TestInterface {
	private DmtTestControl tbc;

	public GetSetNodeType(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
		testGetSetNodeType001();
		testGetSetNodeType002();
		testGetSetNodeType003();
		testGetSetNodeType004();
		testGetSetNodeType005();
		testGetSetNodeType006();
		testGetSetNodeType007();
		testGetSetNodeType008();
		testGetSetNodeType009();
		testGetSetNodeType010();
		testGetSetNodeType011();
		testGetSetNodeType012();
		testGetSetNodeType013();
		testGetSetNodeType014();
		testGetSetNodeType015();
		testGetSetNodeType016();
	}
    
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.setNodeType(String,String)
	 */
	private void testGetSetNodeType001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeType(TestExecPluginActivator.INEXISTENT_NODE, "text/xml");
			DefaultTestBundleControl.failException("#",DmtException.class);
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
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.getNodeType(String)
	 */
	private void testGetSetNodeType002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType002");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeType(TestExecPluginActivator.INEXISTENT_NODE);
			DefaultTestBundleControl.failException("#",DmtException.class);
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
     * This method asserts that null can be passed as type
     * 
     * @spec DmtSession.setNodeType(String,String)
     */
    private void testGetSetNodeType003() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testGetSetNodeType003");
            session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
                    DmtSession.LOCK_TYPE_ATOMIC);
            session.setNodeType(TestExecPluginActivator.INTERIOR_NODE,null);
            DefaultTestBundleControl.pass("'null' could be passed as type parameter (DmtSession.setNodeType) ");
        } catch (Exception e) {
            tbc.failUnexpectedException(e);
        } finally {
            tbc.closeSession(session);
        }
        
    }
	
	/**
	 * This method asserts that getNodeType is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.getNodeType(String)
	 */
	private void testGetSetNodeType004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType004");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			session.getNodeType(TestExecPluginActivator.INTERIOR_NODE);
			DefaultTestBundleControl.pass("getNodeType correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
            
		}
	}
	/**
	 * This method asserts that setNodeType is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.setNodeType(String,String)
	 */
	private void testGetSetNodeType005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType005");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.REPLACE | Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			
			session.setNodeType(TestExecPluginActivator.INTERIOR_NODE,DmtConstants.DDF);
			DefaultTestBundleControl.pass("setNodeType correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}  finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);		
            
		}

	}

	/**
	 * This method asserts that getNodeType is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.getNodeType(String)
	 */
	private void testGetSetNodeType006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType006");	
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.GET));
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeType(TestExecPluginActivator.INTERIOR_NODE);
			DefaultTestBundleControl.pass("getNodeType correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}
	}
	/**
	 * This method asserts that setNodeType is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.setNodeType(String,String)
	 */
	private void testGetSetNodeType007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType007");
            tbc.setPermissions(new PermissionInfo[] {
                    new PermissionInfo(DmtPermission.class.getName(), DmtConstants.OSGi_ROOT, DmtPermission.GET),
                    new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.REPLACE)});
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeType(TestExecPluginActivator.INTERIOR_NODE,"text/xml");
			DefaultTestBundleControl.pass("setNodeType correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}
	}

	
	/**
	 * This method asserts that relative URI works as described.
	 * 
	 * @spec DmtSession.getNodeType(String)
	 */
	private void testGetSetNodeType008() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType008");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeType(TestExecPluginActivator.LEAF_RELATIVE);

			DefaultTestBundleControl.pass("A relative URI can be used with getNodeType.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * This method asserts that relative URI works as described.
	 * 
	 * @spec DmtSession.setNodeType(String,String)
	 */
	private void testGetSetNodeType009() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType009");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.setNodeType(TestExecPluginActivator.LEAF_RELATIVE, "text/xml");

			DefaultTestBundleControl.pass("A relative URI can be used with setNodeType.");
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
	 * @spec DmtSession.setNodeType(String,String)
	 */
	private void testGetSetNodeType010() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType010");
			session = tbc.getDmtAdmin().getSession(
				TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_SHARED);
			session.setNodeType(TestExecPluginActivator.LEAF_RELATIVE, DmtConstants.MIMETYPE);
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
	 * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
	 * if the session is atomic and the plugin is read-only
	 * 
	 * @spec DmtSession.setNodeType(String,String)
	 */
	private void testGetSetNodeType011() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType011");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeType(TestReadOnlyPluginActivator.LEAF_NODE, DmtConstants.MIMETYPE);
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
	 * @spec DmtSession.setNodeType(String,String)
	 */
	private void testGetSetNodeType012() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType012");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeType(TestNonAtomicPluginActivator.LEAF_NODE, DmtConstants.MIMETYPE);
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
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin 
	 * does not support non-atomic writing
	 * 
	 * @spec DmtSession.setNodeType(String,String)
	 */
	private void testGetSetNodeType013() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType013");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeType(TestNonAtomicPluginActivator.LEAF_NODE, DmtConstants.MIMETYPE);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin is read-only 
	 * 
	 * @spec DmtSession.setNodeType(String,String)
	 */
	private void testGetSetNodeType014() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType014");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeType(TestReadOnlyPluginActivator.LEAF_NODE, DmtConstants.MIMETYPE);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
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
	 * @spec DmtSession.getNodeType(String)
	 */
	private void testGetSetNodeType015() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType015");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeType("");


			DefaultTestBundleControl.pass("Asserts that an empty string as relative URI means the root " +
					"URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with
	 * 
	 * @spec DmtSession.setNodeType(String,String)
	 */
	private void testGetSetNodeType016() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeType016");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.setNodeType("", "text/xml");


			DefaultTestBundleControl.pass("Asserts that an empty string as relative URI means the root " +
					"URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
}
