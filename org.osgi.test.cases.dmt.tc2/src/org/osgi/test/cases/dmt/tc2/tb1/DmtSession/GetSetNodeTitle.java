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

import org.osgi.service.dmt.*;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.*;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.NonAtomic.TestNonAtomicPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ReadOnly.TestReadOnlyPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getNodeTitle, setNodeTitle</code> method, 
 * according to MEG specification
 */
public class GetSetNodeTitle implements TestInterface {
	private DmtTestControl tbc;

	public GetSetNodeTitle(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
        if (DmtConstants.SUPPORTS_NODE_TITLE) {
    		testGetSetNodeTitle001();
    		testGetSetNodeTitle002();
    		testGetSetNodeTitle003();
    		testGetSetNodeTitle004();
    		testGetSetNodeTitle005();
    		testGetSetNodeTitle006();
    		testGetSetNodeTitle007();
    		testGetSetNodeTitle008();
    		testGetSetNodeTitle009();
    		testGetSetNodeTitle010();
    		testGetSetNodeTitle011();
    		testGetSetNodeTitle012();
    		testGetSetNodeTitle013();
    		testGetSetNodeTitle014();
    		testGetSetNodeTitle015();
        } else {
            testGetSetNodeTitleFeatureNotSupported001();
            testGetSetNodeTitleFeatureNotSupported002();
        }
	}

    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	private void testGetSetNodeTitle001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeTitle(TestExecPluginActivator.INEXISTENT_NODE, DmtConstants.TITLE);
            DefaultTestBundleControl.failException("", DmtException.class);
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
	 * @spec DmtSession.getNodeTitle(String)
	 */
	private void testGetSetNodeTitle002() {	
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle002");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);			
			session.getNodeTitle(TestExecPluginActivator.INEXISTENT_NODE);
            DefaultTestBundleControl.failException("", DmtException.class);
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
	 * This method asserts that getNodeTitle is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.getNodeTitle(String)
	 */
	private void testGetSetNodeTitle003() {

		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle003");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			session.getNodeTitle(TestExecPluginActivator.INTERIOR_NODE);
			DefaultTestBundleControl.pass("getNodeTitle correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}  finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);	
            
		}
	}
	/**
	 * This method asserts that setNodeTitle is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	private void testGetSetNodeTitle004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle004");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.REPLACE | Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeTitle(TestExecPluginActivator.INTERIOR_NODE,DmtConstants.TITLE);
			DefaultTestBundleControl.pass("setNodeTitle correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
            
		}

	}

	
	/**
	 * This method asserts that setNodeTitle is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	private void testGetSetNodeTitle005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle005");
            tbc.setPermissions(new PermissionInfo[] {
                    new PermissionInfo(DmtPermission.class.getName(), DmtConstants.OSGi_ROOT, DmtPermission.GET),
                    new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.REPLACE)});
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeTitle(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.TITLE);
			DefaultTestBundleControl.pass("setNodeTitle correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
			
		}
	}
	/**
	 * This method asserts that getNodeTitle is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.getNodeTitle(String)
	 */
	private void testGetSetNodeTitle006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle006");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.GET));
			session.getNodeTitle(TestExecPluginActivator.INTERIOR_NODE);
			DefaultTestBundleControl.pass("getNodeTitle correctly executed");
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
	 * @spec DmtSession.getNodeTitle(String)
	 * 
	 */
	private void testGetSetNodeTitle007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle007");
			
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeTitle(TestExecPluginActivator.LEAF_RELATIVE);

			DefaultTestBundleControl.pass("A relative URI can be used with getNodeTitle.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * This method asserts that relative URI works as described.
	 * 
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	private void testGetSetNodeTitle008() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle008");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.setNodeTitle(TestExecPluginActivator.LEAF_RELATIVE, "temp");

			DefaultTestBundleControl.pass("A relative URI can be used with setNodeTitle.");
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
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	private void testGetSetNodeTitle009() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle009");
			session = tbc.getDmtAdmin().getSession(
				TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_SHARED);
			session.setNodeTitle(TestExecPluginActivator.LEAF_RELATIVE, "temp");
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
	 * @spec DmtSession.setNodeTitle(String,DmtData)
	 */
	private void testGetSetNodeTitle010() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle010");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.setNodeTitle(TestReadOnlyPluginActivator.LEAF_NODE,DmtConstants.TITLE);
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
	 * @spec DmtSession.setNodeTitle(String,DmtData)
	 */
	private void testGetSetNodeTitle011() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle011");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.setNodeTitle(TestNonAtomicPluginActivator.LEAF_NODE,DmtConstants.TITLE);
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
	 * @spec DmtSession.setNodeTitle(String,DmtData)
	 */
	private void testGetSetNodeTitle012() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle012");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			session.setNodeTitle(TestNonAtomicPluginActivator.LEAF_NODE,DmtConstants.TITLE);
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
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin 
	 * is read-only 
	 * 
	 * @spec DmtSession.setNodeTitle(String,DmtData)
	 */
	private void testGetSetNodeTitle013() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle013");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			session.setNodeTitle(TestReadOnlyPluginActivator.LEAF_NODE,DmtConstants.TITLE);
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
	 * @spec DmtSession.getNodeTitle(String)
	 * 
	 */
	private void testGetSetNodeTitle014() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle014");
			
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeTitle("");

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
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	private void testGetSetNodeTitle015() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSetNodeTitle015");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.setNodeTitle("", "temp");

			DefaultTestBundleControl.pass("Asserts that an empty string as relative URI means the root " +
			"URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
	
    
    /**
     * Asserts that if the DmtAdmin service implementation does not support this method,
     * DmtException.FEATURE_NOT_SUPPORTED is thrown
     * 
     * @spec DmtSession.getNodeTitle(String)
     */
    private void testGetSetNodeTitleFeatureNotSupported001() { 
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testGetSetNodeTitleFeatureNotSupported001");
            session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);            
            session.getNodeTitle(TestExecPluginActivator.INTERIOR_NODE);
            DefaultTestBundleControl.failException("", DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals(
                    "Asserting that DmtException code is FEATURE_NOT_SUPPORTED",
                    DmtException.FEATURE_NOT_SUPPORTED, e.getCode());
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtException.class, e);
        } finally {
            tbc.closeSession(session);
        }
    }
    
    /**
     * Asserts that if the DmtAdmin service implementation does not support this method,
     * DmtException.FEATURE_NOT_SUPPORTED is thrown
     * 
     * @spec DmtSession.setNodeTitle(String,String)
     */
    private void testGetSetNodeTitleFeatureNotSupported002() { 
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testGetSetNodeTitleFeatureNotSupported002");
            session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);            
            session.setNodeTitle(TestExecPluginActivator.INTERIOR_NODE,DmtConstants.TITLE);
            DefaultTestBundleControl.failException("", DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals(
                    "Asserting that DmtException code is FEATURE_NOT_SUPPORTED",
                    DmtException.FEATURE_NOT_SUPPORTED, e.getCode());
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtException.class, e);
        } finally {
            tbc.closeSession(session);
        }
    }
}
