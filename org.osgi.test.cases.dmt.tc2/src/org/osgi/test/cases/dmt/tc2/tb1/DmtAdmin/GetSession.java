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
 * Feb 11, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtAdmin;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getSession</code> method of DmtAdmin, 
 * according to MEG specification
 */
public class GetSession implements TestInterface {
	private DmtTestControl tbc;
	
	public GetSession(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
		testGetSession001();
		testGetSession002();
		testGetSession003();
		testGetSession004();
		testGetSession005();
		testGetSession006();
		testGetSession007();
		testGetSession008();
		testGetSession009();
        testGetSession010();
		testGetSession011();
		testGetSession012();
		testGetSession013();
		testGetSession014();
		testGetSession015();
		testGetSession016();
		testGetSession017();
		testGetSession018();
		testGetSession019();
		testGetSession020();
		testGetSession021();
		testGetSession022();
		testGetSession023();
		testGetSession024();
	}

    private void prepare() {
        tbc.setPermissions(
            new PermissionInfo[] {
            new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS),
            new PermissionInfo(DmtPrincipalPermission.class.getName(), DmtConstants.PRINCIPAL, "*") }
            );
    }
	/**
	 * This method asserts that a session is opened with the specified subtree, lock type (the default is
	 * LOCK_TYPE_EXCLUSIVE) and principal.
	 * 
	 * @spec DmtAdmin.getSession(String)
	 */
	private void testGetSession001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG);
			TestCase.assertEquals("Asserting subtree", DmtConstants.OSGi_LOG,
					session.getRootUri());
			TestCase.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			TestCase.assertNull("Asserting principal", session.getPrincipal());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that "." on the parameter subtree gives access to the whole subtree
	 * 
	 * @spec DmtAdmin.getSession(String)
	 */
	private void testGetSession002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession002");
			session = tbc.getDmtAdmin().getSession(".");
			TestCase.assertEquals("Asserting subtree", ".", session.getRootUri());
			TestCase.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			TestCase.assertNull("Asserting principal", session.getPrincipal());
            TestCase.assertTrue("asserts that '.' on the parameter subtree gives access to the whole subtree",
                session.isNodeUri(TestExecPluginActivator.INTERIOR_NODE));
            
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that null on the parameter subtree gives access to the whole subtree
	 * 
	 * @spec DmtAdmin.getSession(String)
	 */
	private void testGetSession003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession003");
			session = tbc.getDmtAdmin().getSession(null);
			TestCase.assertEquals("Asserting subtree", ".", session.getRootUri());
			TestCase.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			TestCase.assertNull("Asserting principal", session.getPrincipal());
            TestCase.assertTrue("asserts that null on the parameter subtree gives access to the whole subtree",
                session.isNodeUri(TestExecPluginActivator.INTERIOR_NODE));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown if a session is opened with an inexistent node 
	 * 
	 * @spec DmtAdmin.getSession(String)
	 */
	private void testGetSession004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession004");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INEXISTENT_NODE);
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting if the exception's code is NODE_NOT_FOUND.",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.closeSession(session);
		}
	}

		/**
	 * This method asserts that a session is opened with the specified subtree, lock type and principal.
	 * 
	 * @spec DmtAdmin.getSession(String,int)
	 */
	private void testGetSession005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession005");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			TestCase.assertEquals("Asserting subtree", DmtConstants.OSGi_LOG,
					session.getRootUri());
			TestCase.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_SHARED, session.getLockType());
			TestCase.assertNull("Asserting principal", session.getPrincipal());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that "." on the parameter subtree gives access to the whole subtree
	 * 
	 * @spec DmtAdmin.getSession(String,int)
	 */
	private void testGetSession006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession006");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			TestCase.assertEquals("Asserting subtree", ".", session.getRootUri());
			TestCase.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_ATOMIC, session.getLockType());
			TestCase.assertNull("Asserting principal", session.getPrincipal());
            TestCase.assertTrue("asserts that '.' on the parameter subtree gives access to the whole subtree",
                session.isNodeUri(TestExecPluginActivator.INTERIOR_NODE));
            
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that null on the parameter subtree gives access to the whole subtree
	 * 
	 * @spec DmtAdmin.getSession(String,int)
	 */
	private void testGetSession007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession007");
			session = tbc.getDmtAdmin().getSession(null,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			TestCase.assertEquals("Asserting subtree", ".", session.getRootUri());
			TestCase.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			TestCase.assertNull("Asserting principal", session.getPrincipal());
            TestCase.assertTrue("asserts that null on the parameter subtree gives access to the whole subtree",
                session.isNodeUri(TestExecPluginActivator.INTERIOR_NODE));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown if a session is opened with an inexistent node
	 * 
	 * @spec DmtAdmin.getSession(String,int)
	 */
	private void testGetSession008() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession008");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INEXISTENT_NODE,
					DmtSession.LOCK_TYPE_SHARED);
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting if the exception's code is NODE_NOT_FOUND.",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.closeSession(session);
		}
	}

	

	/**
	 * This method asserts that DmtException.COMMAND_FAILED is thrown if a session is opened using an invalid lock
	 * 
	 * @spec DmtAdmin.getSession(String,int)
	 */
	private void testGetSession009() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession009");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtConstants.INVALID_LOCKMODE);
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting if the exception's code is COMMAND_FAILED.",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	

	/**
	 * This method asserts that a session is opened with the specified subtree, lock type and principal. 
	 * 
	 * @spec DmtAdmin.getSession(String,String,int)
	 */
	private void testGetSession010() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession010");
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					DmtConstants.OSGi_LOG, DmtSession.LOCK_TYPE_ATOMIC);
			TestCase.assertEquals("Asserting subtree", DmtConstants.OSGi_LOG,
					session.getRootUri());
			TestCase.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_ATOMIC, session.getLockType());
			TestCase.assertEquals("Asserting principal", DmtConstants.PRINCIPAL,
					session.getPrincipal());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that "." on the parameter subtree gives access to the whole subtree
	 * 
	 * @spec DmtAdmin.getSession(String,String,int)
	 */
	private void testGetSession011() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession011");
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					".", DmtSession.LOCK_TYPE_EXCLUSIVE);
			TestCase.assertEquals("Asserting subtree", ".", session.getRootUri());
			TestCase.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			TestCase.assertEquals("Asserting principal", DmtConstants.PRINCIPAL,
					session.getPrincipal());
            TestCase.assertTrue("asserts that '.' on the parameter subtree gives access to the whole subtree",
                session.isNodeUri(TestExecPluginActivator.INTERIOR_NODE));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that null on the parameter subtree gives access to the whole subtree
	 * 
	 * @spec DmtAdmin.getSession(String,String,int)
	 */
	private void testGetSession012() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession012");

			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					null, DmtSession.LOCK_TYPE_EXCLUSIVE);
			TestCase.assertEquals("Asserting subtree", ".", session.getRootUri());
			TestCase.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			TestCase.assertEquals("Asserting principal", DmtConstants.PRINCIPAL,
					session.getPrincipal());
            TestCase.assertTrue("asserts that null on the parameter subtree gives access to the whole subtree",
                session.isNodeUri(TestExecPluginActivator.INTERIOR_NODE));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown if a session is opened with an inexistent node
	 * 
	 * @spec DmtAdmin.getSession(String,String,int)
	 */
	private void testGetSession013() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession013");

			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.INEXISTENT_NODE, DmtSession.LOCK_TYPE_SHARED);
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting if the exception's code is NODE_NOT_FOUND.",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
			
		}
	}



	/**
	 * This method asserts that DmtException.COMMAND_FAILED is thrown if a session is opened using an invalid lock
	 * 
	 * @spec DmtAdmin.getSession(String,String,int)
	 */
	private void testGetSession014() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession014");
			
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					DmtConstants.OSGi_LOG, DmtConstants.INVALID_LOCKMODE);
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting if the exception's code is COMMAND_FAILED.",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	

	/**
	 * This method asserts if SecurityException is thrown when the caller 
	 * does not have the required DmtPrincipalPermission
	 * 
	 * @spec DmtAdmin.getSession(String,String,int)
	 */
	private void testGetSession015() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession015");
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL_2, "*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					DmtConstants.OSGi_LOG, DmtSession.LOCK_TYPE_SHARED);
			DefaultTestBundleControl.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			DefaultTestBundleControl.pass("SecurityException was thrown.");
		} catch (Exception e) {
			tbc.failExpectedOtherException(SecurityException.class, e);
		} finally {
			tbc.closeSession(session);
            tbc.setPermissions(
                new PermissionInfo[] {
                new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS),
                new PermissionInfo(DmtPrincipalPermission.class.getName(), DmtConstants.PRINCIPAL, "*") }
                );
		}
	}

	/**
	 * This method asserts if a leaf node can be opened as session root
	 *  
	 * @spec DmtAdmin.getSession(String)
	 */
	private void testGetSession016() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession016");

			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE);
			DefaultTestBundleControl.pass("A leaf node could be opened as session root.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);

		}
	}
	
	/**
	 * This method asserts if a leaf node can be opened as session root
	 *  
	 * @spec DmtAdmin.getSession(String,int)
	 */
	private void testGetSession017() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession017");

			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE,
					DmtSession.LOCK_TYPE_ATOMIC);
			DefaultTestBundleControl.pass("A leaf node could be opened as session root.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);

		}
	}
	
	/**
	 * This method asserts if a leaf node can be opened as session root
	 *  
	 * @spec DmtAdmin.getSession(String,String,int)
	 */
	private void testGetSession018() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession018");
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.LEAF_NODE,
					DmtSession.LOCK_TYPE_ATOMIC);
			DefaultTestBundleControl.pass("A leaf node could be opened as session root.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * nodeUri is syntactically invalid
	 * 
	 * @spec DmtAdmin.getSession(String)
	 */
	private void testGetSession019() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession019");
            //It is from 1 because 0 is 'null' and null is a valid nodeUri in this method
			for (int i=1;i<DmtTestControl.INVALID_URIS.length;i++) {
				try {
				    session = tbc.getDmtAdmin().getSession(DmtTestControl.INVALID_URIS[i].toString());
                    DefaultTestBundleControl.failException("", DmtException.class);
                } catch (DmtException e) {
                    TestCase.assertEquals("DmtException.INVALID_URI is thrown when nodeUri is syntactically invalid",DmtException.INVALID_URI, e.getCode());
                }
			}
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * nodeUri is syntactically invalid
	 * 
	 * @spec DmtAdmin.getSession(String,int)
	 */
	private void testGetSession020() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession020");

            //It is from 1 because 0 is 'null' and null is a valid nodeUri in this method
            for (int i=1;i<DmtTestControl.INVALID_URIS.length;i++) {
                try {
                    session = tbc.getDmtAdmin().getSession(DmtTestControl.INVALID_URIS[i].toString(),DmtSession.LOCK_TYPE_ATOMIC);
                    DefaultTestBundleControl.failException("", DmtException.class);
                } catch (DmtException e) {
                    TestCase.assertEquals("DmtException.INVALID_URI is thrown when nodeUri is syntactically invalid",DmtException.INVALID_URI, e.getCode());
                }
            }

			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * nodeUri is syntactically invalid
	 * 
	 * @spec DmtAdmin.getSession(String,String,int)
	 */
	private void testGetSession021() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession021");

            //It is from 1 because 0 is 'null' and null is a valid nodeUri in this method
            for (int i=1;i<DmtTestControl.INVALID_URIS.length;i++) {
                try {
                    session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,DmtTestControl.INVALID_URIS[i].toString(),DmtSession.LOCK_TYPE_ATOMIC);
                    DefaultTestBundleControl.failException("", DmtException.class);
                } catch (DmtException e) {
                    TestCase.assertEquals("DmtException.INVALID_URI is thrown when nodeUri is syntactically invalid",DmtException.INVALID_URI, e.getCode());
                }
            }
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.URI_TOO_LONG is thrown when  
	 * nodeUri is too long
	 * 
	 * @spec DmtAdmin.getSession(String)
	 */
	private void testGetSession022() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession022");

            for (int i=0;i<DmtTestControl.URIS_TOO_LONG.length;i++) {
                try {
                    session = tbc.getDmtAdmin().getSession(DmtTestControl.URIS_TOO_LONG[i].toString());
                    DefaultTestBundleControl.failException("", DmtException.class);
                } catch (DmtException e) {
                    TestCase.assertEquals("DmtException.URI_TOO_LONG is thrown when nodeUri is too long",DmtException.URI_TOO_LONG, e.getCode());
                }
            }
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.URI_TOO_LONG is thrown when  
	 * nodeUri is too long
	 * 
	 * @spec DmtAdmin.getSession(String,int)
	 */
	private void testGetSession023() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession023");

            for (int i=0;i<DmtTestControl.URIS_TOO_LONG.length;i++) {
                try {
                    session = tbc.getDmtAdmin().getSession(DmtTestControl.URIS_TOO_LONG[i].toString(),DmtSession.LOCK_TYPE_ATOMIC);
                    DefaultTestBundleControl.failException("", DmtException.class);
                } catch (DmtException e) {
                    TestCase.assertEquals("DmtException.URI_TOO_LONG is thrown when nodeUri is too long",DmtException.URI_TOO_LONG, e.getCode());
                }
            }
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.URI_TOO_LONG is thrown when  
	 * nodeUri is too long
	 * 
	 * @spec DmtAdmin.getSession(String,String,int)
	 */
	private void testGetSession024() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetSession024");
			
            for (int i=0;i<DmtTestControl.URIS_TOO_LONG.length;i++) {
                try {
                    session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,DmtTestControl.URIS_TOO_LONG[i].toString(),DmtSession.LOCK_TYPE_ATOMIC);
                    DefaultTestBundleControl.failException("", DmtException.class);
                } catch (DmtException e) {
                    TestCase.assertEquals("DmtException.URI_TOO_LONG is thrown when nodeUri is too long",DmtException.URI_TOO_LONG, e.getCode());
                }
            }
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

}
