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
 * Feb 11, 2005  André Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getNodeValue, setNodeValue
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeValue, setNodeValue<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetSetNodeValue {
	private DmtTestControl tbc;

	public GetSetNodeValue(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetSetNodeValue001();
		testGetSetNodeValue002();
		testGetSetNodeValue003();
		testGetSetNodeValue004();
		testGetSetNodeValue005();
		testGetSetNodeValue006();
		testGetSetNodeValue007();
		testGetSetNodeValue008();
		testGetSetNodeValue009();
		testGetSetNodeValue010();
		testGetSetNodeValue011();
		testGetSetNodeValue012();
		testGetSetNodeValue013();
		testGetSetNodeValue014();
		testGetSetNodeValue015();
		testGetSetNodeValue016();
	}

	/**
	 * @testID testGetSetNodeValue001
	 * @testDescription This method asserts that a leaf node value is correctly
	 *                  set and get.
	 */
	private void testGetSetNodeValue001() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		String leaf = internal + "/maxsize";
		DmtData data = new DmtData(10);
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().createLeafNode(leaf, new DmtData(0));
			tbc.getSession().setNodeValue(leaf, data);
			tbc.assertEquals("Asserting leaf node value", data, tbc
					.getSession().getNodeValue(leaf));
		} catch (DmtException e) {
			tbc.fail("[Not expected]" + e.getMessage());
	    } catch (Exception e) {
	    	tbc.fail("Unexpected exception: "+e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { leaf, internal });
		}
	}

	/**
	 * @testID testGetSetNodeValue002
	 * @testDescription This method tests that setting a node value as null, it
	 *                  will be set to OMA null format, which is the same as
	 *                  DmtData.NULL_FORMAT.
	 */
	private void testGetSetNodeValue002() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		String leaf = internal + "/maxsize";
		DmtData data = new DmtData(DmtData.FORMAT_NULL);
		try {
			tbc.getSession().createLeafNode(leaf, null);
			tbc.getSession().setNodeValue(leaf,
					new DmtData(DmtData.FORMAT_NULL));
			tbc.assertEquals("Asserting null format", data, tbc.getSession()
					.getNodeValue(leaf));
		} catch (DmtException e) {
			tbc.fail("[Not expected]" + e.getMessage());
	    } catch (Exception e) {
	    	tbc.fail("Unexpected exception: "+e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { leaf, internal });
		}
	}

	/**
	 * @testID testGetSetNodeValue003
	 * @testDescription It sets the value of the leaf node on the createLeafNode
	 *                  method and checks if it was executed properly.
	 */
	private void testGetSetNodeValue003() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		String leaf = internal + "/maxsize";
		DmtData data = new DmtData(10);
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().createLeafNode(leaf, data);
			tbc.assertEquals("Asserting same leaf node value", data, tbc
					.getSession().getNodeValue(leaf));
		} catch (DmtException e) {
			tbc.fail("[Not expected]" + e.getMessage());
	    } catch (Exception e) {
	    	tbc.fail("Unexpected exception: "+e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { internal });
		}
	}

	/**
	 * @testID testGetSetNodeValue004
	 * @testDescription Simulates a NODE_NOT_FOUND exception.
	 */
	private void testGetSetNodeValue004() {
		try {
			tbc.getSession().setNodeValue(DmtTestControl.INVALID_NODE,
					new DmtData("value"));
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.NODE_NOT_FOUND, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}

		// getNodeValue test
		try {
			tbc.getSession().getNodeValue(DmtTestControl.INVALID_NODE);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.NODE_NOT_FOUND, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetSetNodeValue005
	 * @testDescription
	 * @testDescription Simulates a COMMAND_NOT_ALLOWED exception.
	 */
	private void testGetSetNodeValue005() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		try {
			tbc.getSession().createInteriorNode(internal,
					"http://url.of.ddf.org/ddf.dtd");
			tbc.getSession().setNodeValue(internal, new DmtData("value"));
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeValue test
		try {
			tbc.getSession().getNodeValue(internal);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { internal });
		}
	}

	/**
	 * @testID testGetSetNodeValue006
	 * @testDescription Simulates a URI_TOO_LONG exception.
	 */
	private void testGetSetNodeValue006() {
		try {
			tbc.getSession().setNodeValue(DmtTestControl.URI_LONG,
					new DmtData("value"));
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.URI_TOO_LONG, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeValue test
		try {
			tbc.getSession().getNodeValue(DmtTestControl.URI_LONG);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.URI_TOO_LONG, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetSetNodeValue007
	 * @testDescription Simulates an INVALID_URI exception.
	 */
	private void testGetSetNodeValue007() {
		try {
			tbc.getSession().setNodeValue(DmtTestControl.INVALID_URI,
					new DmtData("value"));
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.INVALID_URI, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeValue test
		try {
			tbc.getSession().getNodeValue(DmtTestControl.INVALID_URI);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.INVALID_URI, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetSetNodeValue008
	 * @testDescription Simulates an PERMISSION_DENIED exception.
	 */
	private void testGetSetNodeValue008() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		String leaf = internal + "/maxsize";
		DmtData data = new DmtData(10);
		// setup
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().createLeafNode(leaf, data);
			tbc.getSession().setNodeAcl(leaf, new DmtAcl("Exec=*"));
		} catch (DmtException e1) {
			tbc.fail("Exception should not happen");
			e1.printStackTrace();
		}
		// setNodeValue test
		try {
			tbc.getSession().setNodeValue(leaf, new DmtData("value"));
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeValue test
		try {
			tbc.getSession().getNodeValue(leaf);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { leaf, internal });
		}
	}

	/**
	 * @testID testGetSetNodeValue009
	 * @testDescription Simulates an PERMISSION_DENIED exception.
	 */
	private void testGetSetNodeValue009() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		String leaf = internal + "/maxsize";
		DmtData data = new DmtData(10);
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().createLeafNode(leaf, data);
			tbc.getSession().setNodeAcl(leaf, new DmtAcl("Get=*"));
			tbc.getSession().setNodeValue(leaf, new DmtData("value"));
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeValue test
		try {
			tbc.getSession().setNodeAcl(leaf, new DmtAcl("Add=*"));
			tbc.getSession().getNodeValue(leaf);
			tbc.failException("[Exception not happen]", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { leaf, internal });
		}
	}

	/**
	 * @testID testGetSetNodeValue010
	 * @testDescription Simulates an OTHER_ERROR exception.
	 */
	private void testGetSetNodeValue010() {
		DmtSession session = tbc.getSession(DmtTestControl.OSGi_CFG,
				DmtSession.LOCK_TYPE_ATOMIC);
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		String leaf = internal + "/maxsize";
		DmtData data = new DmtData(10);
		try {
			tbc.getSession().setNodeValue(leaf, new DmtData("value"));
			tbc.failException("[Exception not happen]", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.OTHER_ERROR, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeValue test
		try {
			tbc.getSession().getNodeValue(leaf);
			tbc.failException("[Exception not happen]", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.OTHER_ERROR, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			// resets the session
			tbc.setState();
		}
	}

	/**
	 * @testID testGetSetNodeValue011
	 * @testDescription Simulates a COMMAND_FAILED exception.
	 */
	private void testGetSetNodeValue011() {
		// TODO Define how to test a COMMAND_FAILED exception
	}

	/**
	 * @testID testGetSetNodeValue011
	 * @testDescription Simulates a DATA_STORE_FAILURE exception.
	 */
	private void testGetSetNodeValue012() {
		// TODO Define how to test a DATA_STORE_FAILURE exception
	}

	/**
	 * @testID testGetSetNodeValue013
	 * @testDescription Simulates an IllegalStateException for the
	 *                  cases in which the session is closed.
	 */
	private void testGetSetNodeValue013() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		String leaf = internal + "/maxsize";
		DmtData data = new DmtData(10);
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().createLeafNode(leaf, new DmtData(0));
			// close case
			tbc.getSession().close();
		} catch (DmtException e1) {
			tbc.fail("DmtException should not occur");
		}
		// setNodeValue test
		try {
			tbc.getSession().setNodeValue(leaf, new DmtData("value"));
			tbc.failException("[Exception not happen]",
					IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
		// getNodeValue test
		try {
			tbc.getSession().getNodeValue(leaf);
			tbc.failException("[Exception not happen]",
					IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { leaf, internal });
		}
	}

	/**
	 * @testID testGetSetNodeValue014
	 * @testDescription Simulates an IllegalStateException for the
	 *                  cases in which the session is rolled back.
	 */
	private void testGetSetNodeValue014() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		String leaf = internal + "/maxsize";
		DmtData data = new DmtData(10);
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().createLeafNode(leaf, new DmtData(0));
			// rollback case
			tbc.getSession().rollback();
		} catch (DmtException e1) {
			tbc.fail("DmtException should not occur");
		}
		// setNodeValue test
		try {
			tbc.getSession().setNodeValue(leaf, new DmtData("value"));
			tbc.failException("[Exception not happen]",
					IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
		// getNodeValue test
		try {
			tbc.getSession().getNodeValue(leaf);
			tbc.failException("[Exception not happen]",
					IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { leaf, internal });
		}
	}

	/**
	 * @testID testGetSetNodeValue015
	 * @testDescription Simulates a FORMAT_NOT_SUPPORTED exception.
	 */
	private void testGetSetNodeValue015() {
		// TODO Define how to test a FORMAT_NOT_SUPPORTED exception
	}

	/**
	 * @testID testGetSetNodeValue016
	 * @testDescription Simulates a TRANSACTION_ERROR exception.
	 */
	private void testGetSetNodeValue016() {
		// TODO Define how to test a TRANSACTION_ERROR exception
	}
}
