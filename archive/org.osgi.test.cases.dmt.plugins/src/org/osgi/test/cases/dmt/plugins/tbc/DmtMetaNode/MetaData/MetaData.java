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
 * Jun 08, 2005  Luiz Felipe Guimaraes
 * 11            Implement TCK Use Cases
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.plugins.tbc.DmtMetaNode.MetaData;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;

/**
 * @generalDescription This Test Case Validates that the use cases of the specification.
 */
public class MetaData {

	private DmtTestControl tbc;
	private static DmtData LEAF_VALUE = new DmtData(100);
	private static String MIMETYPE = "text/xml";
	public MetaData(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testMetaData001();
		testMetaData002();
		testMetaData003();
		testMetaData004();
		testMetaData005();
		testMetaData006();
		testMetaData007();
		testMetaData008();
		testMetaData009();
		testMetaData010();
		testMetaData011();
		testMetaData012();
		testMetaData013();
		testMetaData014();
		testMetaData015();
		testMetaData016();
		testMetaData017();
		testMetaData018();
		testMetaData019();
		testMetaData020();
		testMetaData021();
		testMetaData022();
		testMetaData023();
		testMetaData024();
		testMetaData025();
		testMetaData026();	
		testMetaData027();
		testMetaData028();
		testMetaData029();
		testMetaData030();
		testMetaData031();
		testMetaData032();
		testMetaData033();
		testMetaData034();
		testMetaData035();
		testMetaData036();	
		testMetaData037();
		testMetaData038();
		testMetaData039();
		testMetaData040();
		testMetaData041();
		testMetaData042();
		testMetaData043();
		testMetaData044();
		testMetaData045();
		testMetaData046();
	}


	/**
	 * @testID testMetaData001
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown
	 * 					if meta-data defines it as an interior node,
	 */
	private void testMetaData001() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData001");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode(); 
			TestPluginMetaDataActivator.metaNodeDefault.setValidValues(new DmtData[] { new DmtData(100)});
			//TestDataPlugin returns the metaNodeDefault, an interior node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_NODE, new DmtData(100));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data defines it as an interior node. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}

	}
	
	/**
	 * @testID testMetaData002
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified value.
	 */
	private void testMetaData002() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData002");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			//TestDataPlugin metadata only allows DmtData as an integer with the value 100
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setValidValues(new DmtData[] { new DmtData(100)});
			
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, new DmtData(99));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified value. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}

	}	
	
	/**
	 * @testID testMetaData003
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified node name
	 */
	private void testMetaData003() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData003");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			//It's not a valid name for this node
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setValidNames(new String[] {TestPluginMetaDataActivator.LEAF_NODE_STRING  });
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setValidValues(new DmtData[] { new DmtData(100)});
			
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE_INVALID_NAME, new DmtData(100));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified node name, Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}

	}		
	

	/**
	 * @testID testMetaData004
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified format
	 */
	private void testMetaData004() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData005");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			
			//It's not a valid format for this node			
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, new DmtData(true));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified format. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	
	/**
	 * @testID testMetaData005
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified format
	 */
	private void testMetaData005() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData005");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			
			//It's not a valid format for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, new DmtData("<test></test>",true));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified format. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	
	/**
	 * @testID testMetaData006
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified format
	 */
	private void testMetaData006() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData006");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			
			//It's not a valid format for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, new DmtData(new byte[] {1}));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified format. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	
	/**
	 * @testID testMetaData007
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the add operation
	 */
	private void testMetaData007() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData007");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setCanAdd(false);
			
			//It's not a valid operation for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE,LEAF_VALUE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the add operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	
	/**
	 * @testID testMetaData008
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown
	 * 					if meta-data defines it as an interior node.
	 */
	private void testMetaData008() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData008");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(false);
			//TestDataPlugin metadata returns an interior node   
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_NODE, LEAF_VALUE, MIMETYPE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data defines it as an interior node. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}

	}
	
	/**
	 * @testID testMetaData009
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified value.
	 */
	private void testMetaData009() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData009");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setValidValues(new DmtData[]{new DmtData(100)});
			
			//TestDataPlugin metadata only allows DmtData as an integer with the value 100
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, new DmtData(99),MIMETYPE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified value. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}

	}	
	
	/**
	 * @testID testMetaData010
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified mimeType.
	 */
	private void testMetaData010() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData010");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setMimeTypes(new String[]{"text/xml"});
			
			//TestDataPlugin metadata only allows mimeType "text/xml", this call must not be fowarded to this plugin
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, LEAF_VALUE,"text/plain");
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified mimeType. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}

	}		
	
	/**
	 * @testID testMetaData011
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified node name
	 */
	private void testMetaData011() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData011");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setValidNames(new String[] {TestPluginMetaDataActivator.LEAF_NODE_STRING  });

			//It's not a valid name for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE_INVALID_NAME, LEAF_VALUE,MIMETYPE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified node name. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}

	}		
	

	/**
	 * @testID testMetaData012
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified format
	 */
	private void testMetaData012() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData012");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			
			//It's not a valid format for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, new DmtData(true),MIMETYPE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified format. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	
	/**
	 * @testID testMetaData013
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified format
	 */
	private void testMetaData013() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData013");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);			
			
			//It's not a valid format for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, new DmtData("<test></test>",true),MIMETYPE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified format. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	
	/**
	 * @testID testMetaData014
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified format
	 */
	private void testMetaData014() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData014");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			
			//It's not a valid format for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, new DmtData(new byte[] {1}),MIMETYPE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified format. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	
	/**
	 * @testID testMetaData015
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the add operation
	 */
	private void testMetaData015() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData015");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setCanAdd(false);
			
			//It's not a valid operation for this node
			session.createLeafNode(TestPluginMetaDataActivator.INEXISTENT_LEAF_NODE, LEAF_VALUE,MIMETYPE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the add operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}		
	/**
	 * @testID testMetaData016
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't have a default value
	 */
	private void testMetaData016() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData016");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);

			//There is no default value
			session.setDefaultNodeValue(TestPluginMetaDataActivator.LEAF_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't doesn't have a default value. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	
	/**
	 * @testID testMetaData017
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the replace operation
	 */
	private void testMetaData017() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData017");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setDefaultValue(new DmtData(100));
			TestPluginMetaDataActivator.metaNodeDefault.setCanReplace(false);
			
			session.setDefaultNodeValue(TestPluginMetaDataActivator.LEAF_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't doesn't allow the replace operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData018
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified value.
	 */
	private void testMetaData018() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData018");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setValidValues(new DmtData[]{new DmtData(100)});
			
			session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,new DmtData(101));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified value. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	/**
	 * @testID testMetaData019
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified format.
	 */
	private void testMetaData019() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData019");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			
			session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,new DmtData("100"));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified format. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	/**
	 * @testID testMetaData020
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified format.
	 */
	private void testMetaData020() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData020");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			
			session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,new DmtData(false));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified format. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	/**
	 * @testID testMetaData021
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified format.
	 */
	private void testMetaData021() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData021");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			
			session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,new DmtData(new byte[] {1}));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified format. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	/**
	 * @testID testMetaData022
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified format.
	 */
	private void testMetaData022() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData022");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			
			session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,new DmtData("<test></test>",true));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified format. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	/**
	 * @testID testMetaData023
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow a value less than the specified.
	 */
	private void testMetaData023() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData023");
			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setMin(90);
			
			session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,new DmtData(89));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow a value less than the specified. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData024
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow a value greater than the specified.
	 */
	private void testMetaData024() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData024");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setMax(110);
			
			session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,new DmtData(111));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow a value greater than the specified. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	
	/**
	 * @testID testMetaData025
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the replace operation.
	 */
	private void testMetaData025() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData025");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setFormat(DmtData.FORMAT_INTEGER);
			TestPluginMetaDataActivator.metaNodeDefault.setCanReplace(false);
			
			session.setNodeValue(TestPluginMetaDataActivator.LEAF_NODE,LEAF_VALUE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the replace operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData026
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified node name.
	 */
	private void testMetaData026() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData026");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setValidNames(new String[] {TestPluginMetaDataActivator.INTERIOR_NODE_STRING  });

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE_INVALID_NAME);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified node name. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	/**
	 * @testID testMetaData027
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the specified node name.
	 */
	private void testMetaData027() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData027");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setValidNames(new String[] {TestPluginMetaDataActivator.INTERIOR_NODE_STRING  });

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE_INVALID_NAME,DmtTestControl.DDF);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow the specified node name. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData028
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the add operation.
	 */
	private void testMetaData028() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData028");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanAdd(false);

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the add operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	/**
	 * @testID testMetaData029
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the add operation.
	 */
	private void testMetaData029() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData029");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanAdd(false);

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE,DmtTestControl.DDF);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the add operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	/**
	 * @testID testMetaData030
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow deleting the last occurrence of this node.
	 */
	private void testMetaData030() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData030");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setMaxOccurrence(1);

			session.deleteNode(TestPluginMetaDataActivator.INTERIOR_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.METADATA_MISMATCH is thrown " +
					"if meta-data doesn't allow deleting the last occurrence of this node. Message: " + e.getMessage(),DmtException.METADATA_MISMATCH,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}

	/**
	 * @testID testMetaData031
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the delete operation.
	 */
	private void testMetaData031() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData031");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanDelete(false);

			session.deleteNode(TestPluginMetaDataActivator.INTERIOR_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the delete operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData032
	 * @testDescription Asserts that DmtException.METADATA_MISMATCH is thrown 
	 * 					if meta-data doesn't allow the replace operation for this node.
	 */
	private void testMetaData032() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData032");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanReplace(false);
			
			session.renameNode(TestPluginMetaDataActivator.INTERIOR_NODE,TestPluginMetaDataActivator.INEXISTENT_NODE_STRING);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the replace operation for this node. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	/**
	 * @testID testMetaData033
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow the replace operation.
	 */
	private void testMetaData033() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData033");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setLeaf(true);
			TestPluginMetaDataActivator.metaNodeDefault.setCanReplace(false);

			session.setNodeTitle(TestPluginMetaDataActivator.LEAF_NODE,"Title");
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the replace operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData034
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow the replace operation.
	 */
	private void testMetaData034() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData034");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanReplace(false);

			session.setNodeType(TestPluginMetaDataActivator.INTERIOR_NODE,DmtTestControl.DDF);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the replace operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	
	/**
	 * @testID testMetaData035
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow the get operation.
	 */
	private void testMetaData035() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData035");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeType(TestPluginMetaDataActivator.INTERIOR_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the get operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}		
	
	/**
	 * @testID testMetaData036
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow the get operation.
	 */
	private void testMetaData036() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData036");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getChildNodeNames(TestPluginMetaDataActivator.INTERIOR_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the get operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData037
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow the get operation.
	 */
	private void testMetaData037() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData037");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeSize(TestPluginMetaDataActivator.LEAF_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the get operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData038
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow the get operation.
	 */
	private void testMetaData038() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData038");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeTimestamp(TestPluginMetaDataActivator.INTERIOR_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the get operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData039
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow the get operation.
	 */
	private void testMetaData039() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData039");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeTitle(TestPluginMetaDataActivator.INTERIOR_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the get operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData040
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow the get operation.
	 */
	private void testMetaData040() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData040");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeValue(TestPluginMetaDataActivator.LEAF_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the get operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData041
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow the get operation.
	 */
	private void testMetaData041() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData041");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeVersion(TestPluginMetaDataActivator.LEAF_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the get operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	/**
	 * @testID testMetaData042
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow the get operation.
	 */
	private void testMetaData042() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData042");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.isLeafNode(TestPluginMetaDataActivator.LEAF_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the get operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData043
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow the get operation.
	 */
	private void testMetaData043() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData043");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getEffectiveNodeAcl(TestPluginMetaDataActivator.LEAF_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the get operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}	
	/**
	 * @testID testMetaData044
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow the get operation.
	 */
	private void testMetaData044() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData044");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setCanGet(false);

			session.getNodeAcl(TestPluginMetaDataActivator.LEAF_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow the get operation. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	
	/**
	 * @testID testMetaData045
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow deleting a permanent node.
	 */
	private void testMetaData045() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData045");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setScope(DmtMetaNode.PERMANENT);

			session.deleteNode(TestPluginMetaDataActivator.INTERIOR_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow deleting a permanent node. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
	/**
	 * @testID testMetaData046
	 * @testDescription Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * 					if meta-data doesn't allow creating a permanent node.
	 */
	private void testMetaData046() {
		DmtSession session = null;
		try {
			tbc.log("#testMetaData046");

			session = tbc.getDmtAdmin().getSession(
					TestPluginMetaDataActivator.TEST_DATA_PLUGIN_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			TestPluginMetaDataActivator.metaNodeDefault = new TestPluginMetaDataMetaNode();
			TestPluginMetaDataActivator.metaNodeDefault.setScope(DmtMetaNode.PERMANENT);

			session.createInteriorNode(TestPluginMetaDataActivator.INEXISTENT_NODE);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.COMMAND_NOT_ALLOWED is thrown " +
					"if meta-data doesn't allow creating a permanent node. Message: " + e.getMessage(),DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			tbc.assertTrue("Asserts that the plugin's method was not called",DmtTestControl.TEMPORARY=="");
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
			DmtTestControl.TEMPORARY="";
			
		}
	}
}

