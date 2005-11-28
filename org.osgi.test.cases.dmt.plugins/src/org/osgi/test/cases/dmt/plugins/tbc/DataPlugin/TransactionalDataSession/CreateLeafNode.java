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
package org.osgi.test.cases.dmt.plugins.tbc.DataPlugin.TransactionalDataSession;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtConstants;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.plugins.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.plugins.tbc.DataPlugin.TestDataPluginActivator;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>createLeafNode</code> method, 
 * according to MEG specification
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
	 * Asserts that DmtAdmin correctly forwards the call of createLeafNode to the correct plugin,
     * using DmtSession.createLeafNode(String)
	 * 
	 * @spec ReadWriteDataSession.createLeafNode(String[],DmtData,String)
	 */
	private void testCreateLeafNode001() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode001");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE);
			tbc.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.CREATELEAFNODE+" to the correct plugin",TestDataPlugin.CREATELEAFNODE,DmtConstants.TEMPORARY);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session,true);
		}	

	}

	/**
	 * Asserts that DmtAdmin correctly forwards the DmtException thrown by the plugin,
     * using DmtSession.createLeafNode(String)
	 * 
	 * @spec ReadWriteDataSession.createLeafNode(String[],DmtData,String)
	 */
	private void testCreateLeafNode002() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode002");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION);
			
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION, e
					.getURI());			
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.INVALID_URI, e
					.getCode());
			tbc.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestDataPlugin.CREATELEAFNODE)>-1);
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(session,true);
		}
	}
	/**
	 * Asserts that DmtAdmin correctly forwards the call of createLeafNode to the correct plugin,
     * using DmtSession.createLeafNode(String,DmtData)
	 * 
	 * @spec ReadWriteDataSession.createLeafNode(String[],DmtData,String)
	 */
	private void testCreateLeafNode003() {
		DmtSession session = null;
		DmtData data = new DmtData(8);
		try {
			tbc.log("#testCreateLeafNode003");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE,data);
			tbc.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.CREATELEAFNODE+" to the correct plugin",TestDataPlugin.CREATELEAFNODE,DmtConstants.TEMPORARY);
			tbc.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",data.toString(),DmtConstants.PARAMETER_2);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session,true);
		}	

	}

	/**
	 * Asserts that DmtAdmin correctly forwards the DmtException thrown by the plugin,
     * using DmtSession.createLeafNode(String,DmtData)
	 * 
	 * @spec ReadWriteDataSession.createLeafNode(String[],DmtData,String)
	 */
	private void testCreateLeafNode004() {
		DmtSession session = null;
		DmtData data = new DmtData(5);
		try {
			tbc.log("#testCreateLeafNode004");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION,data);
			
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION, e
					.getURI());			
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.INVALID_URI, e
					.getCode());
			tbc.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestDataPlugin.CREATELEAFNODE)>-1);
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(session,true);
		}
	}
	/**
	 * Asserts that DmtAdmin correctly forwards the call of createLeafNode to the correct plugin,
     * using DmtSession.createLeafNode(String,DmtData,String)
	 * 
	 * @spec ReadWriteDataSession.createLeafNode(String[],DmtData,String)
	 */
	private void testCreateLeafNode005() {
		DmtSession session = null;
		DmtData data = new DmtData(8);
		String mimeType = "text/xml";
		try {
			tbc.log("#testCreateLeafNode005");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE,data,mimeType);
			tbc.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.CREATELEAFNODE+" to the correct plugin",TestDataPlugin.CREATELEAFNODE,DmtConstants.TEMPORARY);
			tbc.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",data.toString(),DmtConstants.PARAMETER_2);
			tbc.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",mimeType,DmtConstants.PARAMETER_3);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session,true);
		}	

	}

	/**
	 * Asserts that DmtAdmin correctly forwards the DmtException thrown by the plugin,
     * using DmtSession.createLeafNode(String,DmtData,String)
	 * 
	 * @spec ReadWriteDataSession.createLeafNode(String[],DmtData,String)
	 */
	private void testCreateLeafNode006() {
		DmtSession session = null;
		DmtData data = new DmtData(5);
		String mimeType = "text/xml";
		try {
			tbc.log("#testCreateLeafNode006");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION,data,mimeType);
			
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION, e
					.getURI());			
			tbc.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.INVALID_URI, e
					.getCode());
			tbc.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestDataPlugin.CREATELEAFNODE)>-1);
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(session,true);
		}
	}
}
