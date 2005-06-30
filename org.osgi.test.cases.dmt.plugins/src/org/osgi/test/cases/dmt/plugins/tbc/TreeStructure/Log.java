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
 * Feb 24, 2005  Luiz Felipe Guimaraes
 * 1             Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.plugins.tbc.TreeStructure;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.plugins.tbc.DmtTestControl;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * @generalDescription This test class validates the RFC87 (Log section)
 */
public class Log {
	private DmtTestControl tbc;

	public Log(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testLog001();
		testLog002();
		testLog003();
		testLog004();
		testLog005();
		testLog006();
		testLog007();
		testLog008();
		testLog009();
		testLog010();
		testLog011();
		testLog012();
		testLog013();
		testLog014();
		testLog015();
		testLog016();
		testLog017();
		testLog018();
		testLog019();
		testLog020();
		testLog021();		
		testLog022();
		testLog023();
		testLog024();
		testLog025();
	}

	/**
	 * @testID testLog001
	 * @testDescription Tests if a leaf node can be created in ./OSGi/log
	 * 					An exception is expected
	 */

	private void testLog001() {
		DmtSession session = null;
		try {
			tbc.log("#testLog001");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(DmtTestControl.LOG_LEAF,new DmtData("test"));
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that a leaf node cannot be created under \"./OSGi/log\"",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_LEAF });			
		}
	}	
	


	//MAXRECORDS
	/**
	 * @testID testLog002
	 * @testDescription Tests if it is possible to create a maxrecords leaf node
	 * 					with an integer
	 */

	private void testLog002() {
		DmtSession session = null;
		try {
			tbc.log("#testLog002");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_MAXRECORDS,new DmtData(10));
			tbc.pass("An integer was created correctly into a \"./OSGi/log/<interior_node>/maxrecords\"");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_MAXRECORDS,DmtTestControl.LOG_SEARCH });		
		}
		
	}
	/**
	 * @testID testLog003
	 * @testDescription Tests if it is possible to create a maxrecords leaf node
	 * 					with a string (an exception must be thrown)
	 */

	private void testLog003() {
		DmtSession session = null;
		try {
			tbc.log("#testLog003");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_MAXRECORDS,new DmtData("a"));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/maxrecords\" leaf node with a string. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_MAXRECORDS,DmtTestControl.LOG_SEARCH });		
		}
		
	}	
	/**
	 * @testID testLog004
	 * @testDescription Tests if it is possible to create a maxrecords leaf node
	 * 					with a boolean (an exception must be thrown)
	 */

	private void testLog004() {
		DmtSession session = null;
		try {
			tbc.log("#testLog004");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_MAXRECORDS,new DmtData(true));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/maxrecords\" leaf node with a boolean. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_MAXRECORDS,DmtTestControl.LOG_SEARCH });		
		}
		
	}	
	/**
	 * @testID testLog005
	 * @testDescription Tests if it is possible to create a maxrecords leaf node
	 * 					with a byte (an exception must be thrown)
	 */

	private void testLog005() {
		DmtSession session = null;
		try {
			tbc.log("#testLog005");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			byte[] bytes = new byte[1024];
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_MAXRECORDS,new DmtData(bytes));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/maxrecords\" leaf node with a byte. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_MAXRECORDS,DmtTestControl.LOG_SEARCH });		
		}
		
	}		
	/**
	 * @testID testLog006
	 * @testDescription Tests if it is possible to create a maxrecords leaf node
	 * 					with a xml (an exception must be thrown)
	 */
	private void testLog006() {
		DmtSession session = null;
		try {
			tbc.log("#testLog006");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_MAXRECORDS,new DmtData(DmtTestControl.XMLSTR,true));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/maxrecords\" leaf node with a xml. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_MAXRECORDS,DmtTestControl.LOG_SEARCH });		
		}
		
	}	
	/**
	 * @testID testLog007
	 * @testDescription Tests if it is possible to create a maxrecords leaf node
	 * 					with a null type (an exception must be thrown)
	 */
	private void testLog007() {
		DmtSession session = null;
		try {
			tbc.log("#testLog007");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_MAXRECORDS,DmtData.NULL_VALUE);
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/maxrecords\" leaf node with a null type. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_MAXRECORDS,DmtTestControl.LOG_SEARCH });		
		}
		
	}		

	//FILTER
	/**
	 * @testID testLog008
	 * @testDescription Tests if it is possible to create a filter leaf node
	 * 					with an string
	 */

	private void testLog008() {
		DmtSession session = null;
		try {
			tbc.log("#testLog008");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_FILTER,new DmtData("test"));
			tbc.pass("A string was created correctly into a \"./OSGi/log/<interior_node>/filter\" leaf node");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_FILTER,DmtTestControl.LOG_SEARCH });		
		}
		
	}
	/**
	 * @testID testLog009
	 * @testDescription Tests if it is possible to create a filter leaf node
	 * 					with an integer (an exception must be thrown)
	 */

	private void testLog009() {
		DmtSession session = null;
		try {
			tbc.log("#testLog009");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_FILTER,new DmtData(10));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/filter\" leaf node with an integer. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_FILTER,DmtTestControl.LOG_SEARCH });		
		}
		
	}	
	/**
	 * @testID testLog010
	 * @testDescription Tests if it is possible to create a filter leaf node
	 * 					with a boolean (an exception must be thrown)
	 */

	private void testLog010() {
		DmtSession session = null;
		try {
			tbc.log("#testLog010");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_FILTER,new DmtData(true));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/filter\" leaf node with a boolean. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_FILTER,DmtTestControl.LOG_SEARCH });		
		}
		
	}	
	/**
	 * @testID testLog011
	 * @testDescription Tests if it is possible to create a filter leaf node
	 * 					with a byte (an exception must be thrown)
	 */

	private void testLog011() {
		DmtSession session = null;
		try {
			tbc.log("#testLog011");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			byte[] bytes = new byte[1024];
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_FILTER,new DmtData(bytes));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/filter\" leaf node with a byte. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_FILTER,DmtTestControl.LOG_SEARCH });		
		}
		
	}		
	/**
	 * @testID testLog012
	 * @testDescription Tests if it is possible to create a filter leaf node
	 * 					with a xml (an exception must be thrown)
	 */
	private void testLog012() {
		DmtSession session = null;
		try {
			tbc.log("#testLog012");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_FILTER,new DmtData(DmtTestControl.XMLSTR,true));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/filter\" leaf node with a xml. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_FILTER,DmtTestControl.LOG_SEARCH });		
		}
		
	}	
	/**
	 * @testID testLog013
	 * @testDescription Tests if it is possible to create a filter leaf node
	 * 					with a null type (an exception must be thrown)
	 */
	private void testLog013() {
		DmtSession session = null;
		try {
			tbc.log("#testLog013");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_FILTER,DmtData.NULL_VALUE);
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/filter\" leaf node with a null type. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_FILTER,DmtTestControl.LOG_SEARCH });		
		}
		
	}			
//	EXCLUDE
	/**
	 * @testID testLog014
	 * @testDescription Tests if it is possible to create a exclude leaf node
	 * 					with an string
	 */

	private void testLog014() {
		DmtSession session = null;
		try {
			tbc.log("#testLog014");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_EXCLUDE,new DmtData("test"));
			tbc.pass("A string was created correctly into a \"./OSGi/log/<interior_node>/exclude\" leaf node");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_EXCLUDE,DmtTestControl.LOG_SEARCH });		
		}
		
	}
	/**
	 * @testID testLog015
	 * @testDescription Tests if it is possible to create a exclude leaf node
	 * 					with an integer (an exception must be thrown)
	 */

	private void testLog015() {
		DmtSession session = null;
		try {
			tbc.log("#testLog015");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_EXCLUDE,new DmtData(10));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/exclude\" leaf node with an integer. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_EXCLUDE,DmtTestControl.LOG_SEARCH });		
		}
		
	}	
	/**
	 * @testID testLog016
	 * @testDescription Tests if it is possible to create a exclude leaf node
	 * 					with a boolean (an exception must be thrown)
	 */

	private void testLog016() {
		DmtSession session = null;
		try {
			tbc.log("#testLog016");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_EXCLUDE,new DmtData(true));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/exclude\" leaf node with a boolean. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_EXCLUDE,DmtTestControl.LOG_SEARCH });		
		}
		
	}	
	/**
	 * @testID testLog017
	 * @testDescription Tests if it is possible to create a exclude leaf node
	 * 					with a byte (an exception must be thrown)
	 */

	private void testLog017() {
		DmtSession session = null;
		try {
			tbc.log("#testLog017");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			byte[] bytes = new byte[1024];
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_EXCLUDE,new DmtData(bytes));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/exclude\" leaf node with a byte. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_EXCLUDE,DmtTestControl.LOG_SEARCH });		
		}
		
	}		
	/**
	 * @testID testLog018
	 * @testDescription Tests if it is possible to create a exclude leaf node
	 * 					with a xml (an exception must be thrown)
	 */
	private void testLog018() {
		DmtSession session = null;
		try {
			tbc.log("#testLog018");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_EXCLUDE,new DmtData(DmtTestControl.XMLSTR,true));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/exclude\" leaf node with a xml. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_EXCLUDE,DmtTestControl.LOG_SEARCH });		
		}
		
	}	
	/**
	 * @testID testLog019
	 * @testDescription Tests if it is possible to create a exclude leaf node
	 * 					with a null type (an exception must be thrown)
	 */
	private void testLog019() {
		DmtSession session = null;
		try {
			tbc.log("#testLog019");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_EXCLUDE,DmtData.NULL_VALUE);
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create a \"./OSGi/log/<interior_node>/exclude\" leaf node with a null type. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_EXCLUDE,DmtTestControl.LOG_SEARCH });		
		}
		
	}	
	
	/**
	 * @testID testLog020
	 * @testDescription Tests if an unexpected leaf node can be created in ./OSGi/log/<search_id>
	 * 					(just maxsize,maxrecords,filter and exclude are expected)
	 */					

	private void testLog020() {
		DmtSession session = null;
		try {
			tbc.log("#testLog020");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_INVALID,new DmtData("test"));
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that it's not possible to create an unexpected leaf node. ",DmtException.METADATA_MISMATCH,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());

		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_INVALID,DmtTestControl.LOG_SEARCH });		
		}
	}	
	/**
	 * @testID testLog021
	 * @testDescription Tests the metanode of the $\Log node.
	 */					

	private void testLog021() {
		DmtSession session = null;
		try {
			tbc.log("#testLog021");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			DmtMetaNode metaNode = session.getMetaNode(DmtTestControl.OSGi_LOG);
			tbc.assertEquals("Asserts that $/Log node is permanent",DmtMetaNode.PERMANENT, metaNode.getScope());
			tbc.assertTrue("Asserts $/Log node cannot be added", !metaNode.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Log node cannot be replaced", !metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Log node cannot be deleted", !metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue("Asserts $/Log node cannot be executed", !metaNode.can(DmtMetaNode.CMD_EXECUTE));
			tbc.assertTrue("Asserts $/Log node can be gotten", metaNode.can(DmtMetaNode.CMD_GET));
			tbc.assertEquals("Asserts $/Log node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Log node cardinality", !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);		
		}
	}
	
	/**
	 * @testID testLog022
	 * @testDescription Tests the metanode of the $/Log/<search_id> node.
	 */					

	private void testLog022() {
		DmtSession session = null;
		try {
			tbc.log("#testLog022");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			DmtMetaNode metaNode = session.getMetaNode(DmtTestControl.LOG_SEARCH);
			tbc.assertEquals("Asserts that $/Log/<search_id> node is dynamic",DmtMetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts $/Log/<search_id> node can be added", metaNode.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Log/<search_id> node cannot be replaced", !metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Log/<search_id> node can be deleted", metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue("Asserts $/Log/<search_id> node can be executed", metaNode.can(DmtMetaNode.CMD_EXECUTE));
			tbc.assertTrue("Asserts $/Log/<search_id> node can be gotten", metaNode.can(DmtMetaNode.CMD_GET));
			tbc.assertEquals("Asserts $/Log/<search_id> node format is an interior node", DmtData.FORMAT_NODE,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Log/<search_id> node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==Integer.MAX_VALUE);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH });
		}
	}

	/**
	 * @testID testLog023
	 * @testDescription Tests the metanode of the $/Log/<search_id>/Filter node.
	 */					

	private void testLog023() {
		DmtSession session = null;
		try {
			tbc.log("#testLog023");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_FILTER, new DmtData("test"));
			DmtMetaNode metaNode = session.getMetaNode(DmtTestControl.LOG_SEARCH_FILTER);
			tbc.assertEquals("Asserts that $/Log/<search_id>/Filter node is dynamic",DmtMetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts $/Log/<search_id>/Filter node can be added", metaNode.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Log/<search_id>/Filter node can be replaced", metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Log/<search_id>/Filter node cannot be deleted", !metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue("Asserts $/Log/<search_id>/Filter node cannot be executed",!metaNode.can(DmtMetaNode.CMD_EXECUTE));
			tbc.assertTrue("Asserts $/Log/<search_id>/Filter node can be gotten", metaNode.can(DmtMetaNode.CMD_GET));
			tbc.assertEquals("Asserts $/Log/<search_id>/Filter node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Log/<search_id>/Filter node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_FILTER,DmtTestControl.LOG_SEARCH });
		}
	}
	
	/**
	 * @testID testLog024
	 * @testDescription Tests the metanode of the $/Log/<search_id>/MaxRecords node.
	 */					

	private void testLog024() {
		DmtSession session = null;
		try {
			tbc.log("#testLog024");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_MAXRECORDS, new DmtData(10));
			DmtMetaNode metaNode = session.getMetaNode(DmtTestControl.LOG_SEARCH_MAXRECORDS);
			tbc.assertEquals("Asserts that $/Log/<search_id>/MaxRecords node is dynamic",DmtMetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts $/Log/<search_id>/MaxRecords node can be added", metaNode.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Log/<search_id>/MaxRecords node can be replaced", metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Log/<search_id>/MaxRecords node can be deleted", metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue("Asserts $/Log/<search_id>/MaxRecords node cannot be executed",!metaNode.can(DmtMetaNode.CMD_EXECUTE));
			tbc.assertTrue("Asserts $/Log/<search_id>/MaxRecords node can be gotten", metaNode.can(DmtMetaNode.CMD_GET));
			tbc.assertEquals("Asserts $/Log/<search_id>/MaxRecords node format is an integer node", DmtData.FORMAT_INTEGER,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Log/<search_id>/MaxRecords node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_MAXRECORDS,DmtTestControl.LOG_SEARCH});
		}
	}
	
	/**
	 * @testID testLog025
	 * @testDescription Tests the metanode of the $/Log/<search_id>/Exclude node.
	 */					

	private void testLog025() {
		DmtSession session = null;
		try {
			tbc.log("#testLog025");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.LOG_SEARCH);
			session.createLeafNode(DmtTestControl.LOG_SEARCH_EXCLUDE, new DmtData("test"));
			DmtMetaNode metaNode = session.getMetaNode(DmtTestControl.LOG_SEARCH_EXCLUDE);
			tbc.assertEquals("Asserts that $/Log/<search_id>/Exclude node is dynamic",DmtMetaNode.DYNAMIC, metaNode.getScope());
			tbc.assertTrue("Asserts $/Log/<search_id>/Exclude node can be added", metaNode.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Log/<search_id>/Exclude node can be replaced", metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Log/<search_id>/Exclude node can be deleted", metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue("Asserts $/Log/<search_id>/Exclude node cannot be executed",!metaNode.can(DmtMetaNode.CMD_EXECUTE));
			tbc.assertTrue("Asserts $/Log/<search_id>/Exclude node can be gotten", metaNode.can(DmtMetaNode.CMD_GET));
			tbc.assertEquals("Asserts $/Log/<search_id>/Exclude node format is a chr node", DmtData.FORMAT_STRING,metaNode.getFormat());
			tbc.assertTrue("Asserts $/Log/<search_id>/Exclude node cardinality", metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence()==1);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session,new String [] { DmtTestControl.LOG_SEARCH_EXCLUDE,DmtTestControl.LOG_SEARCH });
		}
	}
}
