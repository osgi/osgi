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

/* REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 26/01/2005   André Assad
 * 1            Implement TCK
 * ===========  ==============================================================
 * 15/02/2005   Leonardo Barros
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtReadOnly#getChildNodeNames
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getChildNodeNames<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetChildNodeNames {

	// TODO checks if it is necessary to test DmtTestControl.URI_LONG
	// TODO implements TC to test
	// PERMISSION_DENIED,COMMAND_FAILED,COMMAND_NOT_ALLOWED
	// TODO tests if IllegalStateException is thrown when session is invalidated
	// because of timeout

	private DmtTestControl tbc;

	/**
	 * Constructor
	 * 
	 * @param tbc
	 */
	public GetChildNodeNames(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetChildNodeNames001();
		testGetChildNodeNames002();
		testGetChildNodeNames003();
		testGetChildNodeNames004();
		testGetChildNodeNames005();
		testGetChildNodeNames006();
		testGetChildNodeNames007();
		testGetChildNodeNames008();
	}

	/**
	 * @testID testGetChildNodeNames001
	 * @testDescription This method asserts that a DmtException is thrown
	 *                  whenever a tbc.getSession() tries to retrieve the child
	 *                  node names of an invalid node
	 */
	private void testGetChildNodeNames001() {
		try {
			tbc.getSession().getChildNodeNames(
					DmtTestControl.INVALID_NODE);
			tbc.fail("No DmtException thrown.");
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.fail("Exception incorrectly thrown");
		}
	}

	/**
	 * @testID testGetChildNodeNames002
	 * @testDescription This method asserts that all the children names of the
	 *                  node are found on the String array returned by the
	 *                  method under test.
	 */
	private void testGetChildNodeNames002() {
		String[] node = { "node1", "node2", "node3" };
		int found = 0;

		try {
			tbc.getSession().createInteriorNode(
					DmtTestControl.OSGi_LOG + node[0]);
			tbc.getSession().createInteriorNode(
					DmtTestControl.OSGi_LOG + node[1]);
			tbc.getSession().createInteriorNode(
					DmtTestControl.OSGi_LOG + node[2]);

			String[] nodeNames = tbc.getSession().getChildNodeNames(
					DmtTestControl.OSGi_LOG);

			for (int i = 0; i < nodeNames.length; i++) {
				for (int j = 0; j < node.length; j++) {
					if (nodeNames[i].equals(node[j])) {
						found++;
					}
				}
			}
		} catch (Exception e) {
			tbc.fail("Exception incorrectly thrown");
		}
		tbc.assertEquals("Asserts the number of children nodes", found,
				node.length);
	}

	/**
	 * @testID testGetChildNodeNames003
	 * @testDescription This method asserts that all the entries on the String
	 *                  array returned by the method under test are not null.
	 */
	private void testGetChildNodeNames003() {
		boolean hasNull = false;
		try {
			for (int i = 0; i < tbc.getSession().getChildNodeNames(
					DmtTestControl.OSGi_LOG).length
					&& !hasNull; i++) {
				if (tbc.getSession().getChildNodeNames(
						DmtTestControl.OSGi_LOG)[i] == null) {
					hasNull = true;
				}
			}
		} catch (Exception e) {
			tbc.fail("Exception incorrectly thrown");
		}
		tbc.assertTrue("Asserts null entry on the children nodes", !hasNull);
	}

	/**
	 * @testID testGetChildNodeNames004
	 * @testDescription This method asserts if getChildNodeNames throws
	 *                  DmtException with the correct code
	 */
	private void testGetChildNodeNames004() {
		try {
			tbc.getSession().getChildNodeNames(
					DmtTestControl.INVALID_URI);
			tbc.fail("No DmtException thrown.");
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Exception incorrectly thrown");
		}
	}

	/**
	 * @testID testGetChildNodeNames005
	 * @testDescription This method asserts if getChildNodeNames throws
	 *                  DmtException with the correct code
	 */
	private void testGetChildNodeNames005() {
		try {
			DmtSession session = tbc.getSession(DmtTestControl.OSGi_LOG);
			tbc.getSession().getChildNodeNames(DmtTestControl.OSGi_ROOT);
			tbc.fail("No DmtException thrown.");
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Exception incorrectly thrown");
		}
	}

	/**
	 * @testID testGetChildNodeNames006
	 * @testDescription This method asserts if IllegalStateException is
	 *                  correctly thrown
	 */
	private void testGetChildNodeNames006() {
		try {
			DmtSession session = tbc.getSession(DmtTestControl.OSGi_LOG);
			session.close();
			tbc.getSession().getChildNodeNames(DmtTestControl.OSGi_LOG);
			tbc.fail("No DmtException thrown.");
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Exception incorrectly thrown");
		}
	}

	/**
	 * @testID testGetChildNodeNames007
	 * @testDescription This method asserts if IllegalStateException is
	 *                  correctly thrown
	 */
	private void testGetChildNodeNames007() {
		try {
			DmtSession session = tbc.getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.rollback();
			tbc.getSession().getChildNodeNames(DmtTestControl.OSGi_LOG);
			tbc.fail("No DmtException thrown.");
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Exception incorrectly thrown");
		}
	}

	/**
	 * @testID testGetChildNodeNames008
	 * @testDescription This method asserts if null is returned when there is no
	 *                  children
	 */
	private void testGetChildNodeNames008() {
		try {
			tbc.getSession().createInteriorNode(
					DmtTestControl.OSGi_LOG + "/test");
			tbc.assertNull("Asserts if getChildNodeNames returns null", tbc
					.getSession().getChildNodeNames(
							DmtTestControl.OSGi_LOG + "/test"));
		} catch (Exception e) {
			tbc.fail("Exception incorrectly thrown");
		}
	}

}
