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
 * ===========  ==============================================================
 * 28/02/2005   Andre Assad
 * 11           Implement DMT Use Cases 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TransactionalDataSession;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tc3.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc3.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPlugin;
import org.osgi.test.cases.dmt.tc3.tbc.DataPlugin.TestDataPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

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
	public void testCreateLeafNode001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode001");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE);
			TestCase.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.CREATELEAFNODE+" to the correct plugin",TestDataPlugin.CREATELEAFNODE,DmtConstants.TEMPORARY);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
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
	public void testCreateLeafNode002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode002");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION);
			
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION, e
					.getURI());			
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.INVALID_URI, e
					.getCode());
			TestCase.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestDataPlugin.CREATELEAFNODE)>-1);
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
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
	public void testCreateLeafNode003() {
		DmtSession session = null;
		DmtData data = new DmtData(8);
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode003");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE,data);
			TestCase.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.CREATELEAFNODE+" to the correct plugin",TestDataPlugin.CREATELEAFNODE,DmtConstants.TEMPORARY);
			TestCase.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",data.toString(),DmtConstants.PARAMETER_2);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
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
	public void testCreateLeafNode004() {
		DmtSession session = null;
		DmtData data = new DmtData(5);
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode004");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION,data);
			
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION, e
					.getURI());			
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.INVALID_URI, e
					.getCode());
			TestCase.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestDataPlugin.CREATELEAFNODE)>-1);
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
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
	public void testCreateLeafNode005() {
		DmtSession session = null;
		DmtData data = new DmtData(8);
		String mimeType = "text/xml";
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode005");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE,data,mimeType);
			TestCase.assertEquals("Asserts that DmtAdmin fowarded "+ TestDataPlugin.CREATELEAFNODE+" to the correct plugin",TestDataPlugin.CREATELEAFNODE,DmtConstants.TEMPORARY);
			TestCase.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",data.toString(),DmtConstants.PARAMETER_2);
			TestCase.assertEquals("Asserts that DmtAdmin the parameter was fowarded to the correct plugin without modification",mimeType,DmtConstants.PARAMETER_3);
		} catch (Exception e) {
			DmtTestControl.failUnexpectedException(e);
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
	public void testCreateLeafNode006() {
		DmtSession session = null;
		DmtData data = new DmtData(5);
		String mimeType = "text/xml";
		try {
			DefaultTestBundleControl.log("#testCreateLeafNode006");
			session = tbc.getDmtAdmin().getSession(TestDataPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createLeafNode(TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION,data,mimeType);
			
			DefaultTestBundleControl.failException("#", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct subtree: ", TestDataPluginActivator.INEXISTENT_LEAF_NODE_EXCEPTION, e
					.getURI());			
			TestCase.assertEquals("Asserts that DmtAdmin fowarded the DmtException with the correct code: ", DmtException.INVALID_URI, e
					.getCode());
			TestCase.assertTrue("Asserts that DmtAdmin fowarded the DmtException with the correct message. ", e
					.getMessage().indexOf(TestDataPlugin.CREATELEAFNODE)>-1);
		} catch (Exception e) {
			DmtTestControl.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session,true);
		}
	}
}
