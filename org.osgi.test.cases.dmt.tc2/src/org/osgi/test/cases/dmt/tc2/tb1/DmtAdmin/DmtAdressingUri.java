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
 * 118           Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtAdmin;

import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.security.DmtPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPlugin;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 * This Test Class Validates the DMT Adressing URI, according to MEG specification.
 * 
 */

public class DmtAdressingUri implements TestInterface {
	private DmtTestControl tbc;

	public DmtAdressingUri(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testDmtAdressingUri001();
		testDmtAdressingUri002();
		testDmtAdressingUri003();
		testDmtAdressingUri004();
		testDmtAdressingUri005();
		testDmtAdressingUri006();
		testDmtAdressingUri007();
		testDmtAdressingUri008();
		testDmtAdressingUri009();
		testDmtAdressingUri010();
		testDmtAdressingUri011();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method tests if it is possible to create a node with full Unicode character set.
	 * 
	 * @spec 117.4.1 The DMT Addressing URI
	 */
	private void testDmtAdressingUri001() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtAdressingUri001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.ROOT+ "/" + DmtConstants.UNICODE);
			tbc.pass("It is possible to create a node with full Unicode character set.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method tests if it is possible to create a node containing a slash if a backslash is inserted before it.
	 * 
	 * @spec 117.4.1 The DMT Addressing URI
	 */
	private void testDmtAdressingUri002() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtAdressingUri002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.ROOT+ "/" + "test\\/slash");
			tbc.pass("It is possible to create a node containing a slash if a backslash is inserted before it.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method tests if it is possible to create a node containing a backslash if a backslash is inserted before it.
	 * 
	 * @spec 117.4.1 The DMT Addressing URI
	 */
	private void testDmtAdressingUri003() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtAdressingUri003");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.ROOT+ "/" + "test\\\\slash");
			tbc.pass("It is possible to create a node containing a backslash if a backslash is inserted before it.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that the DmtAdmin ignores a backslash when it is not followed by a slash or backslash.
	 * 
	 * @spec 117.4.1 The DMT Addressing URI
	 */
	private void testDmtAdressingUri004() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtAdressingUri004");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			int nodeLength = TestExecPluginActivator.INTERIOR_NODE_NAME.length();
			String nodeName = TestExecPluginActivator.INTERIOR_NODE_NAME.substring(0,nodeLength-1) + "\\" +  TestExecPluginActivator.INTERIOR_NODE_NAME.substring(nodeLength-1,nodeLength);
			
			boolean passed = session.isNodeUri(TestExecPluginActivator.ROOT + "/" + nodeName );
			tbc.assertTrue("The DmtAdmin ignores a backslash when it is not followed by a slash or backslash.",passed);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This test asserts that a URI must not end with the delimiter slash ("/"). 
	 * 
	 * @spec 117.4.1 The DMT Addressing URI
	 */
	private void testDmtAdressingUri005() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtAdressingUri005");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT + "/",DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that a URI must not end with the delimiter slash (\"/\")",
					DmtException.INVALID_URI, e.getCode());			
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This test asserts that the root node must be denoted as . and not ./
	 * 
	 * @spec 117.4.1 The DMT Addressing URI
	 */
	private void testDmtAdressingUri006() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtAdressingUri006");
			session = tbc.getDmtAdmin().getSession("./",DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that the root node must not be denoted as ./",
					DmtException.INVALID_URI, e.getCode());			
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This test asserts that a URI must not be constructed using the character sequence 
	 * ../ to traverse the tree upwards
	 * 
	 * @spec 117.4.1 The DMT Addressing URI
	 */
	private void testDmtAdressingUri007() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtAdressingUri007");
			String upwardTree = "/../" + TestExecPluginActivator.INTERIOR_NODE_NAME;
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INTERIOR_NODE_COPY + upwardTree,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that a URI must not be constructed using the character sequence ../ to traverse the tree upwards",
					DmtException.INVALID_URI, e.getCode());			
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This test asserts that the character sequence ./ must not be used anywhere else but 
	 * in the beginning of a URI.
	 * 
	 * @spec 117.4.1 The DMT Addressing URI
	 */
	private void testDmtAdressingUri008() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtAdressingUri008");
			String newRoot = "/./" + TestExecPluginActivator.INTERIOR_NODE_NAME;
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT + newRoot,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that the character sequence ./ must not be used anywhere else but in the beginning of a URI.",
					DmtException.INVALID_URI, e.getCode());			
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that URIs used in the DMT must be treated and interpreted as case sensitive
	 * 
	 * @spec 117.4.1 The DMT Addressing URI
	 */
	private void testDmtAdressingUri009() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtAdressingUri009");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("This method asserts that URIs used in the DMT must be treated and interpreted as case sensitive",
					!session.isNodeUri(TestExecPluginActivator.ROOT+ "/" + TestExecPluginActivator.INTERIOR_NODE_NAME.toUpperCase()));
            
            tbc.assertTrue("This method asserts that URIs used in the DMT must be treated and interpreted as case sensitive",
                !session.isNodeUri(TestExecPluginActivator.ROOT+ "/" + TestExecPluginActivator.INTERIOR_NODE_NAME.toLowerCase()));
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that the URI must be given with the root of the management 
	 * tree as the starting point, ensuring that the first node of an absolute URI must be the dot 
	 * 
	 * @spec 117.4.1 The DMT Addressing URI
	 */
	private void testDmtAdressingUri010() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtAdressingUri010");
			String nodeWithoutRoot = TestExecPluginActivator.ROOT.substring(2,TestExecPluginActivator.ROOT.length());
			session = tbc.getDmtAdmin().getSession(nodeWithoutRoot,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.failException("",DmtException.class);
			
		} catch (DmtException e) {
			tbc.assertEquals("This method asserts that the URI must be given with the root of the management " +
					"tree as the starting point",DmtException.COMMAND_FAILED, e.getCode());			
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);

		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that the slash and backslash must not be escaped using the % escaping.
	 * 
	 * @spec 117.4.1 The DMT Addressing URI
	 */
	private void testDmtAdressingUri011() {
		DmtSession session = null;
		String lastNodeName = "test%2Fslash%5C";
		try {
			tbc.log("#testDmtAdressingUri011");

			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.ROOT+ "/" + lastNodeName);
			String[] newInteriorNodeName = TestExecPlugin.getNewInteriorNodeName();
			tbc.assertEquals("This method asserts that the slash and backslash must not be escaped using the % escaping.", lastNodeName, newInteriorNodeName[newInteriorNodeName.length-1]);
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
}
