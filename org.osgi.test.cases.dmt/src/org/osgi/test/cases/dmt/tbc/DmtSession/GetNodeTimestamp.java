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
 * Jan 31, 2005  André Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  André Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import java.util.Date;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getNodeTimestamp
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeTimestamp<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetNodeTimestamp {
	private DmtTestControl tbc;

	/**
	 * Constructor
	 * 
	 * @param tbc
	 */
	public GetNodeTimestamp(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetNodeTimestamp001();
		testGetNodeTimestamp002();
	}

	/**
	 * @testID testGetNodeTimestamp001
	 * @testDescription This method creates a node and asserts that the last
	 *                  time a node was modified is the current time of the
	 *                  target.
	 */
	private void testGetNodeTimestamp001() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		String leaf = internal + "maxsize";
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().createInteriorNode(leaf);
			Date initialTimeStap = tbc.getSession().getNodeTimestamp(leaf);
			tbc.getSession().setNodeValue(leaf, new DmtData(10));
			Date afterTimeStap = tbc.getSession().getNodeTimestamp(leaf);
			tbc.assertTrue("Assert that a node timestamp has changed", initialTimeStap.before(afterTimeStap));
		} catch (DmtException e) {
			tbc.fail("Unexpected DmtException");
	    } catch (Exception e) {
	    	tbc.fail("Unexpected exception: "+e.getClass().getName());
		} finally {
			// cleanup
			tbc.cleanUp(new String[] { leaf, internal });
		}
	}

	/**
	 * @testID testGetNodeTimestamp002
	 * @testDescription This method simulates a NODE_NOT_FOUND exception.
	 */
	private void testGetNodeTimestamp002() {
		try {
			tbc.getSession().getNodeTimestamp(DmtTestControl.INVALID_NODE);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.NODE_NOT_FOUND, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetNodeTimestamp003
	 * @testDescription Simulates a URI_TOO_LONG exception.
	 */
	private void testGetNodeTimestamp003() {
		try {
			tbc.getSession().getNodeTimestamp(DmtTestControl.URI_LONG);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.URI_TOO_LONG, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetNodeTimestamp004
	 * @testDescription Simulates a INVALID_URI exception.
	 */
	private void testGetNodeTimestamp004() {
		try {
			tbc.getSession().getNodeTimestamp(DmtTestControl.INVALID_URI);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.INVALID_URI, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetNodeTimestamp005
	 * @testDescription Simulates a PERMISSION_DENIED exception.
	 */
	private void testGetNodeTimestamp005() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		// setup
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().setNodeAcl(internal, new DmtAcl("Exec=*"));
		} catch (DmtException e1) {
			tbc.fail("Exception should not happen");
			e1.printStackTrace();
		}
		try {
			tbc.getSession().getNodeTimestamp(internal);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { internal });
		}
	}

	/**
	 * @testID testGetNodeTimestamp006
	 * @testDescription Simulates a OTHER_ERROR exception.
	 */
	private void testGetNodeTimestamp006() {
		tbc.getSession(DmtTestControl.OSGi_APPS,
				DmtSession.LOCK_TYPE_ATOMIC);
		try {
			tbc.getSession().getNodeTimestamp(DmtTestControl.OSGi_LOG);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.OTHER_ERROR, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.setState();
		}
	}

	/**
	 * @testID testGetNodeTimestamp007
	 * @testDescription Simulates a COMMAND_FAILED exception.
	 */
	private void testGetNodeTimestamp007() {
		// TODO Definition of what is a COMMAND_FAILED exception.
	}

	/**
	 * @testID testGetNodeTimestamp008
	 * @testDescription Simulates a COMMAND_NOT_ALLOWED exception.
	 */
	private void testGetNodeTimestamp008() {
		// TODO The COMMAND_NOT_ALLOWED specification does not seems to be
		// fitable to getNodeTimestamp, and setNodeType.
	}

	/**
	 * @testID testGetNodeTimestamp009
	 * @testDescription Simulates a IllegalStateException, on the
	 *                  close session case.
	 */
	private void testGetNodeTimestamp009() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		// setup
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().close();
		} catch (DmtException e1) {
			tbc.fail("DmtException should not occur");
		}
		try {
			tbc.getSession().getNodeTimestamp(internal);
			tbc.failException("#", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { internal });
		}
	}

	/**
	 * @testID testGetNodeTimestamp010
	 * @testDescription Simulates a FORMAT_NOT_SUPPORTED exception.
	 */
	private void testGetNodeTimestamp010() {
		// TODO Definition of the FORMAT_NOT_SUPPORTED

	}
}
