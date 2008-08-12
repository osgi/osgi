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
 * 25/01/2005   André Assad
 * 1            Implement TCK
 * ===========  ==============================================================
 * 14/02/2005   Leonardo Barros
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @methodUnderTest org.osgi.service.dmt.Dmt#createInteriorNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>createInteriorNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class CreateInteriorNode {

	// TODO checks if it is necessary to test DmtTestControl.URI_LONG
	// TODO checks testCreateInteriorNode002()
	// TODO tests if IllegalStateException is thrown when session is invalidated
	// because of timeout

	private DmtTestControl tbc;

	private String ddf = "http://www.openmobilealliance.org/tech/DTD/OMA-SyncML-DMDDF-V1_2_0.dtd";

	public CreateInteriorNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testCreateInteriorNode001();
		// testCreateInteriorNode002();
		testCreateInteriorNode003();
		testCreateInteriorNode004();
		testCreateInteriorNode005();
		testCreateInteriorNode006();
		testCreateInteriorNode007();
		testCreateInteriorNode008();
		testCreateInteriorNode009();
	}

	/**
	 * @testID testCreateInteriorNode001
	 * @testDescription This method asserts that a new interior node is created
	 *                  with a null type.
	 */
	private void testCreateInteriorNode001() {
		String internal = DmtTestControl.OSGi_LOG + "/test001";
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.assertNull("Asserting node type", tbc.getSession().getNodeType(
					internal));
		} catch (DmtException e) {
			tbc.fail("Unexpected DmtException");
		} catch (Exception e) {
			tbc.fail("Unexpected exception: "+e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { internal });
		}

	}

	/**
	 * @testID testCreateInteriorNode002
	 * @testDescription This method asserts that an interior node is created
	 *                  with the URL to its DDF document as its type.
	 */
	private void testCreateInteriorNode002() {
		// TODO find a leaf node that allows type settup (besides log leaf
		String internal = DmtTestControl.OSGi_LOG + "/test002";
		try {
			// nodes)
			tbc.getSession().createInteriorNode(internal, ddf);
			tbc.assertEquals("Asserting node type", ddf, tbc.getSession()
					.getNodeType(internal));
		} catch (DmtException e) {
			tbc.fail("Unexpected DmtException");
		} catch (Exception e) {
			tbc.fail("Unexpected exception: "+e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { internal });
		}
	}

	/**
	 * @testID testCreateInteriorNode003
	 * @testDescription This method asserts that when an invalid interior node
	 *                  is created - without passing the URL of the ddf - a
	 *                  DmtException is thrown.
	 */
	private void testCreateInteriorNode003() {
		try {
			tbc.getSession().createInteriorNode(DmtTestControl.INVALID_NODE);
			tbc.fail("createInteriorNode did not throw an exception");
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.fail("Incorrect exception thrown");
		}
	}

	/**
	 * @testID testCreateInteriorNode004
	 * @testDescription This method asserts that when an invalid interior node
	 *                  is created - without passing the URL of the ddf - a
	 *                  DmtException is thrown.
	 */
	private void testCreateInteriorNode004() {
		try {
			tbc.getSession().createInteriorNode(DmtTestControl.URI_LONG);
			tbc.fail("createInteriorNode did not throw an exception");
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Incorrect exception thrown");
		}
	}

	/**
	 * @testID testCreateInteriorNode005
	 * @testDescription This method asserts that when an invalid interior node
	 *                  is created - without passing the URL of the ddf - a
	 *                  DmtException is thrown.
	 */
	private void testCreateInteriorNode005() {
		try {
			tbc.getSession().createInteriorNode(DmtTestControl.INVALID_URI);
			tbc.fail("createInteriorNode did not throw an exception");
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Incorrect exception thrown");
		}
	}

	/**
	 * @testID testCreateInteriorNode006
	 * @testDescription This method asserts that when an invalid interior node
	 *                  is created - without passing the URL of the ddf - a
	 *                  DmtException is thrown.
	 */
	private void testCreateInteriorNode006() {
		try {
			tbc.getSession().createInteriorNode(DmtTestControl.OSGi_LOG);
			tbc.fail("createInteriorNode did not throw an exception");
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.fail("Incorrect exception thrown");
		}
	}

	/**
	 * @testID testCreateInteriorNode007
	 * @testDescription This method asserts that when an invalid interior node
	 *                  is created - without passing the URL of the ddf - a
	 *                  DmtException is thrown.
	 */
	private void testCreateInteriorNode007() {
		try {
			tbc.getSession(DmtTestControl.OSGi_LOG).createInteriorNode(
					DmtTestControl.OSGi_ROOT + "/test");
			tbc.fail("createInteriorNode did not throw an exception");
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Incorrect exception thrown");
		}
	}

	/**
	 * @testID testCreateInteriorNode008
	 * @testDescription This method asserts if IllegalStateException is thrown
	 *                  when must be.
	 */
	private void testCreateInteriorNode008() {
		try {
			DmtSession session = tbc.getSession(DmtTestControl.OSGi_LOG);
			session.close();
			session.createInteriorNode(DmtTestControl.OSGi_ROOT + "/test");
			tbc.fail("createInteriorNode did not throw an exception");
		} catch (DmtException e) {
			tbc.fail("Incorrect exception thrown");
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Incorrect exception thrown");
		}
	}

	/**
	 * @testID testCreateInteriorNode009
	 * @testDescription This method asserts if IllegalStateException is
	 *                  correctly thrown
	 */
	private void testCreateInteriorNode009() {
		try {
			DmtSession session = tbc.getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.rollback();
			tbc.getSession().createInteriorNode(
					DmtTestControl.OSGi_LOG + "/test");
			tbc.fail("No DmtException thrown.");
		} catch (IllegalStateException e) {
			tbc.pass("Asserts IllegalStateException");
		} catch (Exception e) {
			tbc.fail("Asserting DmtException");
		}
	}

}
