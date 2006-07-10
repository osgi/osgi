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
 * 
 */

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

import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.security.DmtPermission;
import info.dmtree.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

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
			tbc.log("#testGetSession001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG);
			tbc.assertEquals("Asserting subtree", DmtConstants.OSGi_LOG,
					session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			tbc.assertNull("Asserting principal", session.getPrincipal());
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
			tbc.log("#testGetSession002");
			session = tbc.getDmtAdmin().getSession(".");
			tbc.assertEquals("Asserting subtree", ".", session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			tbc.assertNull("Asserting principal", session.getPrincipal());
            tbc.assertTrue("asserts that '.' on the parameter subtree gives access to the whole subtree",
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
			tbc.log("#testGetSession003");
			session = tbc.getDmtAdmin().getSession(null);
			tbc.assertEquals("Asserting subtree", ".", session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			tbc.assertNull("Asserting principal", session.getPrincipal());
            tbc.assertTrue("asserts that null on the parameter subtree gives access to the whole subtree",
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
			tbc.log("#testGetSession004");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INEXISTENT_NODE);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
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
			tbc.log("#testGetSession005");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			tbc.assertEquals("Asserting subtree", DmtConstants.OSGi_LOG,
					session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_SHARED, session.getLockType());
			tbc.assertNull("Asserting principal", session.getPrincipal());
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
			tbc.log("#testGetSession006");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc.assertEquals("Asserting subtree", ".", session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_ATOMIC, session.getLockType());
			tbc.assertNull("Asserting principal", session.getPrincipal());
            tbc.assertTrue("asserts that '.' on the parameter subtree gives access to the whole subtree",
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
			tbc.log("#testGetSession007");
			session = tbc.getDmtAdmin().getSession(null,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertEquals("Asserting subtree", ".", session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			tbc.assertNull("Asserting principal", session.getPrincipal());
            tbc.assertTrue("asserts that null on the parameter subtree gives access to the whole subtree",
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
			tbc.log("#testGetSession008");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INEXISTENT_NODE,
					DmtSession.LOCK_TYPE_SHARED);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
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
			tbc.log("#testGetSession009");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtConstants.INVALID_LOCKMODE);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
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
			tbc.log("#testGetSession010");
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					DmtConstants.OSGi_LOG, DmtSession.LOCK_TYPE_ATOMIC);
			tbc.assertEquals("Asserting subtree", DmtConstants.OSGi_LOG,
					session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_ATOMIC, session.getLockType());
			tbc.assertEquals("Asserting principal", DmtConstants.PRINCIPAL,
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
			tbc.log("#testGetSession011");
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					".", DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertEquals("Asserting subtree", ".", session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			tbc.assertEquals("Asserting principal", DmtConstants.PRINCIPAL,
					session.getPrincipal());
            tbc.assertTrue("asserts that '.' on the parameter subtree gives access to the whole subtree",
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
			tbc.log("#testGetSession012");

			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					null, DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertEquals("Asserting subtree", ".", session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			tbc.assertEquals("Asserting principal", DmtConstants.PRINCIPAL,
					session.getPrincipal());
            tbc.assertTrue("asserts that null on the parameter subtree gives access to the whole subtree",
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
			tbc.log("#testGetSession013");

			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.INEXISTENT_NODE, DmtSession.LOCK_TYPE_SHARED);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
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
			tbc.log("#testGetSession014");
			
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					DmtConstants.OSGi_LOG, DmtConstants.INVALID_LOCKMODE);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
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
			tbc.log("#testGetSession015");
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL_2, "*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					DmtConstants.OSGi_LOG, DmtSession.LOCK_TYPE_SHARED);
			tbc.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass("SecurityException was thrown.");
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
			tbc.log("#testGetSession016");

			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE);
			tbc.pass("A leaf node could be opened as session root.");
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
			tbc.log("#testGetSession017");

			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc.pass("A leaf node could be opened as session root.");
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
			tbc.log("#testGetSession018");
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.LEAF_NODE,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc.pass("A leaf node could be opened as session root.");
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
			tbc.log("#testGetSession019");
            //It is from 1 because 0 is 'null' and null is a valid nodeUri in this method
			for (int i=1;i<DmtTestControl.INVALID_URIS.length;i++) {
				try {
				    session = tbc.getDmtAdmin().getSession(DmtTestControl.INVALID_URIS[i].toString());
                    tbc.failException("", DmtException.class);
                } catch (DmtException e) {
                    tbc.assertEquals("DmtException.INVALID_URI is thrown when nodeUri is syntactically invalid",DmtException.INVALID_URI, e.getCode());
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
			tbc.log("#testGetSession020");

            //It is from 1 because 0 is 'null' and null is a valid nodeUri in this method
            for (int i=1;i<DmtTestControl.INVALID_URIS.length;i++) {
                try {
                    session = tbc.getDmtAdmin().getSession(DmtTestControl.INVALID_URIS[i].toString(),DmtSession.LOCK_TYPE_ATOMIC);
                    tbc.failException("", DmtException.class);
                } catch (DmtException e) {
                    tbc.assertEquals("DmtException.INVALID_URI is thrown when nodeUri is syntactically invalid",DmtException.INVALID_URI, e.getCode());
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
			tbc.log("#testGetSession021");

            //It is from 1 because 0 is 'null' and null is a valid nodeUri in this method
            for (int i=1;i<DmtTestControl.INVALID_URIS.length;i++) {
                try {
                    session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,DmtTestControl.INVALID_URIS[i].toString(),DmtSession.LOCK_TYPE_ATOMIC);
                    tbc.failException("", DmtException.class);
                } catch (DmtException e) {
                    tbc.assertEquals("DmtException.INVALID_URI is thrown when nodeUri is syntactically invalid",DmtException.INVALID_URI, e.getCode());
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
			tbc.log("#testGetSession022");

            for (int i=0;i<DmtTestControl.URIS_TOO_LONG.length;i++) {
                try {
                    session = tbc.getDmtAdmin().getSession(DmtTestControl.URIS_TOO_LONG[i].toString());
                    tbc.failException("", DmtException.class);
                } catch (DmtException e) {
                    tbc.assertEquals("DmtException.URI_TOO_LONG is thrown when nodeUri is too long",DmtException.URI_TOO_LONG, e.getCode());
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
			tbc.log("#testGetSession023");

            for (int i=0;i<DmtTestControl.URIS_TOO_LONG.length;i++) {
                try {
                    session = tbc.getDmtAdmin().getSession(DmtTestControl.URIS_TOO_LONG[i].toString(),DmtSession.LOCK_TYPE_ATOMIC);
                    tbc.failException("", DmtException.class);
                } catch (DmtException e) {
                    tbc.assertEquals("DmtException.URI_TOO_LONG is thrown when nodeUri is too long",DmtException.URI_TOO_LONG, e.getCode());
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
			tbc.log("#testGetSession024");
			
            for (int i=0;i<DmtTestControl.URIS_TOO_LONG.length;i++) {
                try {
                    session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,DmtTestControl.URIS_TOO_LONG[i].toString(),DmtSession.LOCK_TYPE_ATOMIC);
                    tbc.failException("", DmtException.class);
                } catch (DmtException e) {
                    tbc.assertEquals("DmtException.URI_TOO_LONG is thrown when nodeUri is too long",DmtException.URI_TOO_LONG, e.getCode());
                }
            }
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

}
