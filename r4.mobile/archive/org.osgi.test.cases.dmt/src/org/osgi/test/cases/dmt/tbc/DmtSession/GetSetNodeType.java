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
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getNodeType, setNodeType
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeType, setNodeType<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetSetNodeType {
	private DmtTestControl tbc;

	private String xmlStr = "<?xml version=\"1.0\"?><data><name>data name</name><value>data value</value></data>";

	private String url = "http://www.openmobilealliance.org/tech/DTD/OMA-SyncML-DMDDF-V1_2_0.dtd";

	public GetSetNodeType(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetSetNodeType001();
		testGetSetNodeType002();
		testGetSetNodeType003();
	}

	/**
	 * @testID testGetSetNodetype001
	 * @testDescription This method asserts that a leaf node type is correctly
	 *                  setup.
	 */
	private void testGetSetNodeType001() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		String leaf = internal + "/filter";
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().createLeafNode(leaf, new DmtData(xmlStr));
			tbc.getSession().setNodeType(leaf, "text/xml");
			tbc.assertEquals("Asserting leaf node mime type", "text/xml", tbc
					.getSession().getNodeType(leaf));
		} catch (DmtException e) {
			tbc.fail("Unexcpected DmtException");
	    } catch (Exception e) {
	    	tbc.fail("Unexpected exception: "+e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { leaf, internal });
		}
	}

	/**
	 * @testID testGetSetNodetype002
	 * @testDescription This method asserts that an interior node type is
	 *                  correctly setup.
	 */
	private void testGetSetNodeType002() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		String leaf = internal + "/filter";
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().setNodeType(internal, url);
			tbc.assertEquals("Asserting leaf node mime type", url, tbc
					.getSession().getNodeType(internal));
		} catch (DmtException e) {
			tbc.fail("Unexcpected DmtException");
	    } catch (Exception e) {
	    	tbc.fail("Unexpected exception: "+e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { internal });
		}
	}

	/**
	 * @testID testGetSetNodeType003
	 * @testDescription This method simulates a NODE_NOT_FOUND exception.
	 */
	private void testGetSetNodeType003() {
		// setNodeType test
		try {
			tbc.getSession().setNodeType("./invalid/node", "text/xml");
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.NODE_NOT_FOUND, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeType test
		try {
			tbc.getSession().getNodeType("./invalid/node");
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.NODE_NOT_FOUND, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetSetNodeType004
	 * @testDescription Simulates a URI_TOO_LONG exception.
	 */
	private void testGetSetNodeType004() {
		// setNodeType test
		try {
			tbc.getSession().setNodeType(DmtTestControl.URI_LONG,
					"text/xml");
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.URI_TOO_LONG, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeType test
		try {
			tbc.getSession().getNodeType(DmtTestControl.URI_LONG);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.URI_TOO_LONG, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetSetNodeType005
	 * @testDescription Simulates a INVALID_URI exception.
	 */
	private void testGetSetNodeType005() {
		// setNodeType test
		try {
			tbc.getSession().setNodeType(DmtTestControl.INVALID_URI,
					"text/xml");
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.INVALID_URI, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeType test
		try {
			tbc.getSession().getNodeType(DmtTestControl.INVALID_URI);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.INVALID_URI, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}

	/**
	 * @testID testGetSetNodeType006
	 * @testDescription Simulates a PERMISSION_DENIED exception.
	 */
	private void testGetSetNodeType006() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		// setup
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().setNodeAcl(internal, new DmtAcl("Exec=*"));
		} catch (DmtException e1) {
			tbc.fail("Exception should not happen");
			e1.printStackTrace();
		}
		// setNodeType test
		try {
			tbc.getSession().setNodeType(internal, url);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeType test
		try {
			tbc.getSession().getNodeType(internal);
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
	 * @testID testGetSetNodeType007
	 * @testDescription Simulates a OTHER_ERROR exception.
	 */
	private void testGetSetNodeType007() {
		tbc.getSession(DmtTestControl.OSGi_APPS,
				DmtSession.LOCK_TYPE_ATOMIC);
		// setNodeType test
		try {
			tbc.getSession().setNodeType(DmtTestControl.OSGi_LOG,
					"text/xml");
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.OTHER_ERROR, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
		// getNodeType test
		try {
			tbc.getSession().getNodeType(DmtTestControl.OSGi_LOG);
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
	 * @testID testGetSetNodeType008
	 * @testDescription Simulates a COMMAND_FAILED exception.
	 */
	private void testGetSetNodeType008() {
		// TODO Definition of what is a COMMAND_FAILED exception.
	}

	/**
	 * @testID testGetSetNodeType009
	 * @testDescription Simulates a COMMAND_NOT_ALLOWED exception.
	 */
	private void testGetSetNodeType009() {
		// TODO The COMMAND_NOT_ALLOWED specification does not seems to be
		// fitable to getNodeType, and setNodeType.
	}

	/**
	 * @testID testGetSetNodeType010
	 * @testDescription Simulates a IllegalStateException, on the
	 *                  close session case.
	 */
	private void testGetSetNodeType010() {
		String internal = DmtTestControl.OSGi_LOG + "/internal";
		// setup
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().close();
		} catch (DmtException e1) {
			tbc.fail("DmtException should not occur");
		}
		// setNodeType test
		try {
			tbc.getSession().setNodeType(internal, "text/xml");
			tbc.failException("#", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
		// setNodeType test
		try {
			tbc.getSession().getNodeType(internal);
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
	 * @testID testGetSetNodeType011
	 * @testDescription Simulates a FORMAT_NOT_SUPPORTED exception.
	 */
	private void testGetSetNodeType011() {
		// TODO Definition of the FORMAT_NOT_SUPPORTED

	}
}
