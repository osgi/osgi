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
 * ===========  ==============================================================
 * 28/02/2005   Andre Assad
 * 11           Implement DMT Use Cases 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.Dmt;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.DmtDataPlugin.TestDataPluginActivator;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#createLeafNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>createLeafNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class CreateLeafNode {
	private DmtTestControl tbc;

	public CreateLeafNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
 		testCreateLeafNode001();
		testCreateLeafNode002();
		testCreateLeafNode003();
		testCreateLeafNode004();
		testCreateLeafNode005();
		testCreateLeafNode006();
	}

	/**
	 * @testID testCreateLeafNode001
	 * @testDescription Asserts that DmtAdmin correctly forwards the call of
	 *                  Dmt#createLeafNode(String nodeUri) to the correct
	 *                  plugin.
	 */
	private void testCreateLeafNode001() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode001");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE);
			tbc.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.CREATELEAFNODE_1+" to the correct plugin",TestDataPlugin.CREATELEAFNODE_1,DmtTestControl.TEMPORARY);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY = "";
		}
		
	}

	/**
	 * @testID testCreateLeafNode002
	 * @testDescription Forces a DmtException in order to check if
	 *                  Dmt#createLeafNode(String nodeUri) correctly forwards
	 *                  the exception to DmtAdmin.
	 */
	private void testCreateLeafNode002() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode002");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION, e
					.getURI());			
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.CONCURRENT_ACCESS, e
					.getCode());
			tbc.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestDataPlugin.CREATELEAFNODE_1)>-1);	
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateInteriorNode003
	 * @testDescription Asserts that DmtAdmin correctly forwards the call of
	 *                  Dmt#createLeafNode(java.lang.String nodeUri, DmtData
	 *                  value) to the correct plugin.
	 */
	private void testCreateLeafNode003() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode003");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			DmtData data = new DmtData(10);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE,data);
			tbc.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.CREATELEAFNODE_2+" to the correct plugin",TestDataPlugin.CREATELEAFNODE_2,DmtTestControl.TEMPORARY);
			tbc.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",data.toString(),DmtTestControl.PARAMETER_2);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY = "";
			DmtTestControl.PARAMETER_2 = "";
		}
	}

	/**
	 * @testID testCreateLeafNode004
	 * @testDescription Forces a DmtException in order to check if
	 *                  Dmt#createLeafNode(java.lang.String nodeUri, DmtData
	 *                  value) correctly forwards the exception to DmtAdmin.
	 */
	private void testCreateLeafNode004() {
		DmtSession session = null;
		DmtData data = new DmtData(20);
		try {
			tbc.log("#testCreateLeafNode004");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION,data);
			
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION, e
					.getURI());			
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.FORMAT_NOT_SUPPORTED, e
					.getCode());
			tbc.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestDataPlugin.CREATELEAFNODE_2)>-1);	
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateInteriorNode005
	 * @testDescription Asserts that DmtAdmin correctly forwards the call of
	 *                  Dmt#createLeafNode(java.lang.String nodeUri, DmtData
	 *                  value, java.lang.String mimeType) to the correct plugin.
	 */
	private void testCreateLeafNode005() {
		DmtSession session = null;
		DmtData data = new DmtData(8);
		String mimeType = "text/xml";
		try {
			tbc.log("#testCreateLeafNode005");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE,data,mimeType);
			tbc.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.CREATELEAFNODE_3+" to the correct plugin",TestDataPlugin.CREATELEAFNODE_3,DmtTestControl.TEMPORARY);
			tbc.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",data.toString(),DmtTestControl.PARAMETER_2);
			tbc.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",mimeType,DmtTestControl.PARAMETER_3);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY = "";
			DmtTestControl.PARAMETER_2 = "";
			DmtTestControl.PARAMETER_3 = "";
		}	

	}

	/**
	 * @testID testCreateLeafNode006
	 * @testDescription Forces a DmtException in order to check if
	 *                  Dmt#createLeafNode(java.lang.String nodeUri, DmtData
	 *                  value, java.lang.String mimeType) correctly forwards the
	 *                  exception to DmtAdmin.
	 */
	private void testCreateLeafNode006() {
		DmtSession session = null;
		DmtData data = new DmtData(5);
		String mimeType = "text/xml";
		try {
			tbc.log("#testCreateLeafNode006");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION,data,mimeType);
			
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION, e
					.getURI());			
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.INVALID_URI, e
					.getCode());
			tbc.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestDataPlugin.CREATELEAFNODE_3)>-1);	
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
}