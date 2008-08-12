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
 * Jul 12, 2005  Luiz Felipe Guimaraes
 * 173           [MEGTCK][DMT] Changes on interface names and plugins
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tb1.DmtSession;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>mangle</code> method of DmtSession, 
 * according to MEG specification
 */

public class Mangle implements TestInterface {
	private DmtTestControl tbc;

	public Mangle(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testMangle001();
		testMangle002();
		testMangle003();
		testMangle004();
		testMangle005();
		testMangle006();
	}
    private void prepare() {
        //This method do not throw SecurityException, so, if it is checking for DmtPermission an exception is
        //incorrectly thrown.
        tbc.setPermissions(new PermissionInfo[0]);
    }
	/**
	 * Asserts that IllegalArgumentException is thrown if nodeName is empty
	 * 
	 * @spec DmtSession.mangle(String)
	 */
	private void testMangle001() {
	    DmtSession session = null;
		try {
		    tbc.log("#testMangle001");
		    session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,DmtSession.LOCK_TYPE_SHARED);
			session.mangle("");
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException is thrown if nodeName is empty");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalArgumentException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * Asserts that NullPointerException is thrown if nodeName is null
	 * 
	 * @spec DmtSession.mangle(String)
	 */
	private void testMangle002() {
	    DmtSession session = null;
		try {
			tbc.log("#testMangle002");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,DmtSession.LOCK_TYPE_SHARED);
			session.mangle(null);
			tbc.failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass("NullPointerException is thrown if nodeName is null");
		} catch (Exception e) {
			tbc.fail("Expected " + NullPointerException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
		    tbc.closeSession(session);
		}
	}

	/**
	 * Asserts that if the nodeName doesnt exceed the limit defined by implementation and 
	 * doesnt contain '\' or '/', the returned value remain unchanged
	 * 
	 * @spec DmtSession.mangle(String) 
	 */
	private void testMangle003() {
	    DmtSession session = null;
		try {
			tbc.log("#testMangle003");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,DmtSession.LOCK_TYPE_SHARED);
			StringBuffer nodeName = new StringBuffer(DmtConstants.MAXIMUM_NODE_LENGTH);
			for (int i=0;i<DmtConstants.MAXIMUM_NODE_LENGTH;i++) {
				nodeName.append("a");
			}
			String expectedNodeName = nodeName.toString();
			tbc.assertEquals("Asserts that if the nodeName doesnt exceed the limit defined by implementation and " +
					"doesnt contain '\' or '/', the returned value remain unchanged", 
					expectedNodeName,session.mangle(expectedNodeName));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
		    tbc.closeSession(session);
		}
	}

	
	
	
	/**
	 * Asserts that a slash in the nameNode is escaped using a backslash slash
	 * 
	 * @spec DmtSession.mangle(String)
	 */
	private void testMangle004() {
	    DmtSession session = null;
		try {
			tbc.log("#testMangle004");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,DmtSession.LOCK_TYPE_SHARED);
			String nodeName = "text/html";
			String expectedNodeName = "text\\/html";
			tbc.assertEquals("Asserts that a slash in the nameNode is escaped using a backslash slash", 
					expectedNodeName, 
					session.mangle(nodeName));

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
		    tbc.closeSession(session);
		}
	}
	/**
	 * Asserts that a backslash in the nameNode is escaped using a backslash slash
	 * @spec DmtSession.mangle(String)
	 */
	private void testMangle005() {
	    DmtSession session = null;
		try {
			tbc.log("#testMangle005");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,DmtSession.LOCK_TYPE_SHARED);
			String nodeName = "a\\b";
			String expectedNodeName = "a\\\\b";
			tbc.assertEquals("Asserts that a backslash in the nameNode is escaped using a backslash slash", 
					expectedNodeName, 
					session.mangle(nodeName));

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
		    tbc.closeSession(session);
		}
	}
	/**
	 * Asserts that if the length of the name does exceed the limit, the specified mechanism 
	 * is used to normalize.
	 * 
	 * @spec DmtSession.mangle(String)
	 */
	
	private void testMangle006() {
	    DmtSession session = null;
		try {
			tbc.log("#testMangle006");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,DmtSession.LOCK_TYPE_SHARED);
			int nodeLength = DmtConstants.MAXIMUM_NODE_LENGTH + 1;
			StringBuffer nodeName = new StringBuffer(nodeLength);
			for (int i=0;i<nodeLength;i++) {
				nodeName.append("a");
			}
			
			String expectedNodeName = tbc.getHash(nodeName.toString());
			
			tbc.assertEquals("Asserts that if the length of the name does exceed the limit, " +
					"the specified mechanism is used to normalize.", 
					expectedNodeName, 
					session.mangle(nodeName.toString()));

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
		    tbc.closeSession(session);
		}
	}
}
