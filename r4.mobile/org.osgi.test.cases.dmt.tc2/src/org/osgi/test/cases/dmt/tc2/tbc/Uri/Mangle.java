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
 * 118           Implement sendAlert
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tbc.Uri;

import info.dmtree.Uri;

import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>mangle</code> method of DmtAdmin, 
 * according to MEG specification
 */

public class Mangle implements TestInterface {
	private DmtTestControl tbc;

	public Mangle(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testMangle001();
		testMangle002();
		testMangle003();
		testMangle004();
		testMangle005();
		testMangle006();
	}
	/**
	 * Asserts that IllegalArgumentException is thrown if nodeName is empty
	 * 
	 * @spec DmtAdmin.mangle(String)
	 */
	private void testMangle001() {
		try {
			tbc.log("#testMangle001");
			Uri.mangle("");
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException is thrown if nodeName is empty");
		} catch (Exception e) {
			tbc.failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Asserts that NullPointerException is thrown if nodeName is null
	 * 
	 * @spec DmtAdmin.mangle(String)
	 */
	private void testMangle002() {
		try {
			tbc.log("#testMangle002");
			Uri.mangle(null);
			tbc.failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass("NullPointerException is thrown if nodeName is null");
		} catch (Exception e) {
			tbc.failExpectedOtherException(NullPointerException.class, e);
		}
	}

	/**
	 * Asserts that if the nodeName doesnt exceed the limit defined by implementation and 
	 * doesnt contain '\' or '/', the returned value remain unchanged
	 * 
	 * @spec DmtAdmin.mangle(String) 
	 */
	private void testMangle003() {
		try {
			tbc.log("#testMangle003");
			
			
			StringBuffer nodeName = new StringBuffer(Uri.getMaxSegmentNameLength());
			for (int i=0;i<Uri.getMaxSegmentNameLength();i++) {
				nodeName.append("a");
			}
			String expectedNodeName = nodeName.toString();
			tbc.assertEquals("Asserts that if the nodeName doesnt exceed the limit defined by implementation and " +
					"doesnt contain '\' or '/', the returned value remain unchanged", 
					expectedNodeName,Uri.mangle(expectedNodeName));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}

	
	
	
	/**
	 * Asserts that a slash in the nameNode is escaped using a backslash slash
	 * 
	 * @spec DmtAdmin.mangle(String)
	 */
	private void testMangle004() {
		try {
			tbc.log("#testMangle004");
			String nodeName = "text/html";
			String expectedNodeName = "text\\/html";
			tbc.assertEquals("Asserts that a slash in the nameNode is escaped using a backslash slash", 
					expectedNodeName, 
					Uri.mangle(nodeName));

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
	/**
	 * Asserts that a backslash in the nameNode is escaped using a backslash slash
	 * @spec DmtAdmin.mangle(String)
	 */
	private void testMangle005() {
		try {
			tbc.log("#testMangle005");
			String nodeName = "a\\b";
			String expectedNodeName = "a\\\\b";
			tbc.assertEquals("Asserts that a backslash in the nameNode is escaped using a backslash slash", 
					expectedNodeName, 
					Uri.mangle(nodeName));

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
	/**
	 * Asserts that if the length of the name does exceed the limit, the specified mechanism 
	 * is used to normalize.
	 * 
	 * @spec DmtAdmin.mangle(String)
	 */
	
	private void testMangle006() {
		try {
			tbc.log("#testMangle006");
			int nodeLength = Uri.getMaxSegmentNameLength() + 1;
			StringBuffer nodeName = new StringBuffer(nodeLength);
			for (int i=0;i<nodeLength;i++) {
				nodeName.append("a");
			}
			
			String expectedNodeName = tbc.getHash(nodeName.toString());

			tbc.assertEquals("Asserts that if the length of the name does exceed the limit, " +
					"the specified mechanism is used to normalize.", 
					expectedNodeName, 
					Uri.mangle(nodeName.toString()));

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
}
