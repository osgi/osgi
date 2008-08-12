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

import org.osgi.service.dmt.DmtAcl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getNodeTitle,setNodeTitle
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeTitle, setNodeTitle<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetSetNodeTitle {
	private DmtTestControl tbc;

	private String title = "Ninja";

	public GetSetNodeTitle(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
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
	}

	/**
	 * @testID testGetSetNodeTitle001
	 * @testDescription This method asserts that a node title is correctly set
	 *                  and get.
	 */
	private void testGetSetNodeTitle001() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().setNodeTitle(internal, title);
			tbc.assertEquals("Asserting node title", title, tbc.getSession()
					.getNodeTitle(internal));
		} catch (DmtException e) {
			tbc.fail("Not Expected DmtException");
	    } catch (Exception e) {
	    	tbc.fail("Unexpected exception: "+e.getClass().getName());			
		} finally {
			tbc.cleanUp(new String[] { internal });
		}
	}

	/**
	 * @testID testGetSetNodeTitle002
	 * @testDescription This method simulates a NODE_NOT_FOUND exception.
	 */
	private void testGetSetNodeTitle002() {
		// setNodeTitle test
		try {
			tbc.getSession().setNodeTitle("./invalid/node", title);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.NODE_NOT_FOUND, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeTitle test
		try {
			tbc.getSession().getNodeTitle("./invalid/node");
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.NODE_NOT_FOUND, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetSetNodeTitle003
	 * @testDescription Simulates a URI_TOO_LONG exception.
	 */
	private void testGetSetNodeTitle003() {
		// setNodeTitle test
		try {
			tbc.getSession()
					.setNodeTitle(DmtTestControl.URI_LONG, title);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.URI_TOO_LONG, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeTitle test
		try {
			tbc.getSession().getNodeTitle(DmtTestControl.URI_LONG);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.URI_TOO_LONG, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetSetNodeTitle004
	 * @testDescription Simulates a INVALID_URI exception.
	 */
	private void testGetSetNodeTitle004() {
		// setNodeTitle test
		try {
			tbc.getSession().setNodeTitle(DmtTestControl.INVALID_URI,
					title);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.INVALID_URI, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeTitle test
		try {
			tbc.getSession().getNodeTitle(DmtTestControl.INVALID_URI);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.INVALID_URI, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetSetNodeTitle005
	 * @testDescription Simulates a PERMISSION_DENIED exception.
	 */
	private void testGetSetNodeTitle005() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		// setup
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().setNodeAcl(internal, new DmtAcl("Exec=*"));
		} catch (DmtException e1) {
			tbc.fail("Exception should not happen");
			e1.printStackTrace();
		}
		// setNodeTitle test
		try {
			tbc.getSession().setNodeTitle(internal, title);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeTitle test
		try {
			tbc.getSession().getNodeTitle(internal);
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
	 * @testID testGetSetNodeTitle006
	 * @testDescription Simulates a OTHER_ERROR exception.
	 */
	private void testGetSetNodeTitle006() {
		tbc.getSession(DmtTestControl.OSGi_APPS,
				DmtSession.LOCK_TYPE_ATOMIC);
		// setNodeTitle test
		try {
			tbc.getSession()
					.setNodeTitle(DmtTestControl.OSGi_LOG, title);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.OTHER_ERROR, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeTitle test
		try {
			tbc.getSession().getNodeTitle(DmtTestControl.OSGi_LOG);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.OTHER_ERROR, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetSetNodeTitle007
	 * @testDescription Simulates a COMMAND_FAILED exception.
	 */
	private void testGetSetNodeTitle007() {
		// TODO Definition of what is a COMMAND_FAILED exception.
	}

	/**
	 * @testID testGetSetNodeTitle008
	 * @testDescription Simulates a COMMAND_NOT_ALLOWED exception.
	 */
	private void testGetSetNodeTitle008() {
		// TODO The COMMAND_NOT_ALLOWED specification does not seems to be
		// fitable to getNodeTitle, and setNodeTitle.
	}

	/**
	 * @testID testGetSetNodeTitle009
	 * @testDescription Simulates a IllegalStateException, on the
	 *                  close session case.
	 */
	private void testGetSetNodeTitle009() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		// setup
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().close();
		} catch (DmtException e1) {
			tbc.fail("DmtException should not occur");
		}
		// setNodeTitle test
		try {
			tbc.getSession().setNodeTitle(internal, title);
			tbc.failException("#", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
		// getNodeTitle test
		try {
			tbc.getSession().getNodeTitle(internal);
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
	 * @testID testGetSetNodeTitle010
	 * @testDescription Simulates a IllegalStateException, on the
	 *                  rollback session case.
	 */
	private void testGetSetNodeTitle010() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		// setup
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().rollback();
		} catch (DmtException e1) {
			tbc.fail("DmtException should not occur");
		}
		// setNodeTitle test
		try {
			tbc.getSession().setNodeTitle(internal, title);
			tbc.failException("#", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
		// getNodeTitle test
		try {
			tbc.getSession().getNodeTitle(internal);
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
}
