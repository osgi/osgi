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
 * Jan 31, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import info.dmtree.Acl;
import info.dmtree.DmtException;
import info.dmtree.security.DmtPermission;
import info.dmtree.security.DmtPrincipalPermission;
import info.dmtree.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getNodeTimestamp</code> method of DmtSession, 
 * according to MEG specification.
 */
public class GetNodeTimestamp implements TestInterface {

	private DmtTestControl tbc;

	public GetNodeTimestamp(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
        if (DmtConstants.SUPPORTS_NODE_TIMESTAMP) {
    		testGetNodeTimestamp001();
    		testGetNodeTimestamp002();
    		testGetNodeTimestamp003();
    		testGetNodeTimestamp004();
    		testGetNodeTimestamp005();
        } else {
            testGetNodeTimestampFeatureNotSupported001();
        }
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.getNodeTimestamp(String)
	 */
	private void testGetNodeTimestamp001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			session.getNodeTimestamp(TestExecPluginActivator.INEXISTENT_NODE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException's code is NODE_NOT_FOUND",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	
	/**
	 * This method asserts that getNodeTimestamp is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.getNodeTimestamp(String)
	 */
	private void testGetNodeTimestamp002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp002");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE);

			tbc.pass("getNodeTimestamp correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
            
		}

	}

	/**
	 * This method asserts that getNodeVersion is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.getNodeTimestamp(String)
	 */
	private void testGetNodeTimestamp003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));
			
			session.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.pass("getNodeTimestamp correctly executed");
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
	 * @spec DmtSession.getNodeTimestamp(String)
	 */
	private void testGetNodeTimestamp004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp004");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE_NAME);

			tbc.pass("A relative URI can be used with getNodeTimestamp.");
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
	 * @spec DmtSession.getNodeTimestamp(String)
	 */
	private void testGetNodeTimestamp005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp005");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeTimestamp("");

			tbc.pass("Asserts that an empty string as relative URI means the root " +
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
     * @spec DmtSession.getNodeTimestamp(String)
     */
    private void testGetNodeTimestampFeatureNotSupported001() {
        DmtSession session = null;
        try {
            tbc.log("#testGetNodeTimestampFeatureNotSupported001");

            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_SHARED);
            session.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE);

            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals(
                    "Asserting that DmtException's code is FEATURE_NOT_SUPPORTED",
                    DmtException.FEATURE_NOT_SUPPORTED, e.getCode());
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtException.class, e);
        } finally {
            tbc.closeSession(session);
        }
    }	
}
