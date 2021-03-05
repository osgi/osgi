/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

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

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPlugin;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

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

	@Override
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
			DefaultTestBundleControl.log("#testDmtAdressingUri001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.ROOT+ "/" + DmtConstants.UNICODE);
			DefaultTestBundleControl.pass("It is possible to create a node with full Unicode character set.");
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
			DefaultTestBundleControl.log("#testDmtAdressingUri002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.ROOT+ "/" + "test\\/slash");
			DefaultTestBundleControl.pass("It is possible to create a node containing a slash if a backslash is inserted before it.");
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
			DefaultTestBundleControl.log("#testDmtAdressingUri003");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.ROOT+ "/" + "test\\\\slash");
			DefaultTestBundleControl.pass("It is possible to create a node containing a backslash if a backslash is inserted before it.");
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
			DefaultTestBundleControl.log("#testDmtAdressingUri004");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			int nodeLength = TestExecPluginActivator.INTERIOR_NODE_NAME.length();
			String nodeName = TestExecPluginActivator.INTERIOR_NODE_NAME.substring(0,nodeLength-1) + "\\" +  TestExecPluginActivator.INTERIOR_NODE_NAME.substring(nodeLength-1,nodeLength);
			
			boolean passed = session.isNodeUri(TestExecPluginActivator.ROOT + "/" + nodeName );
			TestCase.assertTrue("The DmtAdmin ignores a backslash when it is not followed by a slash or backslash.",passed);
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
			DefaultTestBundleControl.log("#testDmtAdressingUri005");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT + "/",DmtSession.LOCK_TYPE_EXCLUSIVE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that a URI must not end with the delimiter slash (\"/\")",
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
			DefaultTestBundleControl.log("#testDmtAdressingUri006");
			session = tbc.getDmtAdmin().getSession("./",DmtSession.LOCK_TYPE_EXCLUSIVE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that the root node must not be denoted as ./",
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
			DefaultTestBundleControl.log("#testDmtAdressingUri007");
			String upwardTree = "/../" + TestExecPluginActivator.INTERIOR_NODE_NAME;
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INTERIOR_NODE_COPY + upwardTree,DmtSession.LOCK_TYPE_EXCLUSIVE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that a URI must not be constructed using the character sequence ../ to traverse the tree upwards",
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
			DefaultTestBundleControl.log("#testDmtAdressingUri008");
			String newRoot = "/./" + TestExecPluginActivator.INTERIOR_NODE_NAME;
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT + newRoot,DmtSession.LOCK_TYPE_EXCLUSIVE);
			DefaultTestBundleControl.failException("",DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that the character sequence ./ must not be used anywhere else but in the beginning of a URI.",
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
			DefaultTestBundleControl.log("#testDmtAdressingUri009");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			TestCase.assertTrue("This method asserts that URIs used in the DMT must be treated and interpreted as case sensitive",
					!session.isNodeUri(TestExecPluginActivator.ROOT+ "/" + TestExecPluginActivator.INTERIOR_NODE_NAME.toUpperCase()));
            
            TestCase.assertTrue("This method asserts that URIs used in the DMT must be treated and interpreted as case sensitive",
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
			DefaultTestBundleControl.log("#testDmtAdressingUri010");
			String nodeWithoutRoot = TestExecPluginActivator.ROOT.substring(2,TestExecPluginActivator.ROOT.length());
			session = tbc.getDmtAdmin().getSession(nodeWithoutRoot,DmtSession.LOCK_TYPE_EXCLUSIVE);
			DefaultTestBundleControl.failException("",DmtException.class);
			
		} catch (DmtException e) {
			TestCase.assertEquals("This method asserts that the URI must be given with the root of the management " +
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
			DefaultTestBundleControl.log("#testDmtAdressingUri011");

			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.ROOT+ "/" + lastNodeName);
			String[] newInteriorNodeName = TestExecPlugin.getNewInteriorNodeName();
			TestCase.assertEquals("This method asserts that the slash and backslash must not be escaped using the % escaping.", lastNodeName, newInteriorNodeName[newInteriorNodeName.length-1]);
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
}
