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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 30/03/2005   Alexandre Alves
 * 32           Implement DMT Structure validation for monitoring
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.TreeStructure;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Leonardo Barros
 * 
 * This test class validates the implementation of tree structure, according to
 * MEG reference documentation.
 */
public class TreeStructure {
	private MonitorTestControl tbc;

	public TreeStructure(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testTreeStructure001();
		testTreeStructure002();
		testTreeStructure003();
		testTreeStructure004();
		testTreeStructure005();
		testTreeStructure006();
		testTreeStructure007();
		testTreeStructure008();
		testTreeStructure009();
		testTreeStructure010();
		testTreeStructure011();
		testTreeStructure012();
		testTreeStructure013();
		testTreeStructure014();
		testTreeStructure015();
		testTreeStructure016();
	}

	/**
	 * This method asserts if $/Monitor is a valid node and asserts Type,
	 * Cardinality, Add Permission, Get Permission, Replace Permission, Delete
	 * Permission and Execute Permission according to Table 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure001() {
		tbc.log("#testTreeStructure001");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			tbc.assertTrue("Asserts if ./OSGi/Monitor is a valid node", session
					.isNodeUri(MonitorTestControl.DMT_OSGI_MONITOR));

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_OSGI_MONITOR);

			tbc.assertEquals("Asserts $/Monitor metanode scope",
					DmtMetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals("Asserts $/Monitor metanode format",
					DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc.assertTrue("Asserts $/Monitor metanode cardinality", !metaNode
					.isZeroOccurrenceAllowed()
					&& metaNode.getMaxOccurrence() == 1);

			tbc.assertTrue("Asserts $/Monitor metanode ADD", !metaNode
					.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Monitor metanode GET", metaNode
					.can(DmtMetaNode.CMD_GET));
			tbc.assertTrue("Asserts $/Monitor metanode REPLACE", !metaNode
					.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Monitor metanode DELETE", !metaNode
					.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue("Asserts $/Monitor metanode EXECUTE", !metaNode
					.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Monitor/cesar is a valid node and asserts Type,
	 * Cardinality, Add Permission, Get Permission, Replace Permission, Delete
	 * Permission and Execute Permission according to Table 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure002() {
		tbc.log("#testTreeStructure002");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_OSGI_MONITOR,
					DmtSession.LOCK_TYPE_SHARED);

			tbc.assertTrue("Asserts if ./OSGi/Monitor is a valid node", session
					.isNodeUri(MonitorTestControl.DMT_URI_MONITORABLE1));

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE1);

			tbc.assertEquals("Asserts $/Monitor/cesar metanode scope",
					DmtMetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals("Asserts $/Monitor/cesar metanode format",
					DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar metanode cardinality",
							metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == Integer.MAX_VALUE);

			tbc.assertTrue("Asserts $/Monitor/cesar metanode ADD", !metaNode
					.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Monitor/cesar metanode GET", metaNode
					.can(DmtMetaNode.CMD_GET));
			tbc.assertTrue("Asserts $/Monitor/cesar metanode REPLACE",
					!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Monitor/cesar metanode DELETE", !metaNode
					.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue("Asserts $/Monitor/cesar metanode EXECUTE",
					!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Monitor/cesar/test0 is a valid node and asserts
	 * Type, Cardinality, Add Permission, Get Permission, Replace Permission,
	 * Delete Permission and Execute Permission according to Table 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure003() {
		tbc.log("#testTreeStructure003");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if ./OSGi/Monitor/cesar/test0 is a valid node",
							session
									.isNodeUri(MonitorTestControl.DMT_URI_MONITORABLE1_SV1));

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE1_SV1);

			tbc.assertEquals("Asserts $/Monitor/cesar/test0 metanode scope",
					DmtMetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals("Asserts $/Monitor/cesar/test0 metanode format",
					DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0 metanode cardinality",
							metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == Integer.MAX_VALUE);

			tbc.assertTrue("Asserts $/Monitor/cesar/test0 metanode ADD",
					!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Monitor/cesar/test0 metanode GET",
					metaNode.can(DmtMetaNode.CMD_GET));
			tbc.assertTrue("Asserts $/Monitor/cesar/test0 metanode REPLACE",
					!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Monitor/cesar/test0 metanode DELETE",
					!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue("Asserts $/Monitor/cesar/test0 metanode EXECUTE",
					!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Monitor/cesar/test0/TrapID is a valid node and
	 * asserts Type, Cardinality, Add Permission, Get Permission, Replace
	 * Permission, Delete Permission and Execute Permission according to Table
	 * 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure004() {
		tbc.log("#testTreeStructure004");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if ./OSGi/Monitor/cesar/test0/TrapID is a valid node",
							session
									.isNodeUri(MonitorTestControl.DMT_URI_MONITORABLE_SV1_TRAPID));

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE_SV1_TRAPID);

			tbc.assertEquals(
					"Asserts $/Monitor/cesar/test0/TrapID metanode scope",
					DmtMetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals(
					"Asserts $/Monitor/cesar/test0/TrapID metanode format",
					DmtData.FORMAT_STRING, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/TrapID metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);

			tbc.assertTrue("Asserts $/Monitor/cesar/test0/TrapID metanode ADD",
					!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Monitor/cesar/test0/TrapID metanode GET",
					metaNode.can(DmtMetaNode.CMD_GET));
			tbc.assertTrue(
					"Asserts $/Monitor/cesar/test0/TrapID metanode REPLACE",
					!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue(
					"Asserts $/Monitor/cesar/test0/TrapID metanode DELETE",
					!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue(
					"Asserts $/Monitor/cesar/test0/TrapID metanode EXECUTE",
					!metaNode.can(DmtMetaNode.CMD_EXECUTE));

			tbc
					.assertEquals(
							"Asserting the value stored in /Monitor/cesar/test0/TrapID",
							MonitorTestControl.SVS[0],
							session
									.getNodeValue(
											MonitorTestControl.DMT_URI_MONITORABLE_SV1_TRAPID)
									.getString());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Monitor/cesar/test0/CM is a valid node and
	 * asserts Type, Cardinality, Add Permission, Get Permission, Replace
	 * Permission, Delete Permission and Execute Permission according to Table
	 * 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure005() {
		tbc.log("#testTreeStructure005");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if ./OSGi/Monitor/cesar/test0/CM is a valid node",
							session
									.isNodeUri(MonitorTestControl.DMT_URI_MONITORABLE_SV1_CM));

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE_SV1_CM);

			tbc.assertEquals("Asserts $/Monitor/cesar/test0/CM metanode scope",
					DmtMetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals(
					"Asserts $/Monitor/cesar/test0/CM metanode format",
					DmtData.FORMAT_STRING, metaNode.getFormat());

			tbc.assertTrue(
					"Asserts $/Monitor/cesar/test0/CM metanode cardinality",
					!metaNode.isZeroOccurrenceAllowed()
							&& metaNode.getMaxOccurrence() == 1);

			tbc.assertTrue("Asserts $/Monitor/cesar/test0/CM metanode ADD",
					!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Monitor/cesar/test0/CM metanode GET",
					metaNode.can(DmtMetaNode.CMD_GET));
			tbc.assertTrue("Asserts $/Monitor/cesar/test0/CM metanode REPLACE",
					!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Monitor/cesar/test0/CM metanode DELETE",
					!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue("Asserts $/Monitor/cesar/test0/CM metanode EXECUTE",
					!metaNode.can(DmtMetaNode.CMD_EXECUTE));

			tbc.assertEquals(
					"Asserting the value stored in /Monitor/cesar/test0/CM",
					"CC", session.getNodeValue(
							MonitorTestControl.DMT_URI_MONITORABLE_SV1_CM)
							.getString());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Monitor/cesar/test0/Results is a valid node and
	 * asserts Type, Cardinality, Add Permission, Get Permission, Replace
	 * Permission, Delete Permission and Execute Permission according to Table
	 * 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure006() {
		tbc.log("#testTreeStructure006");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if ./OSGi/Monitor/cesar/test0/Results is a valid node",
							session
									.isNodeUri(MonitorTestControl.DMT_URI_MONITORABLE_SV1_RESULTS));

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE_SV1_RESULTS);

			tbc.assertEquals(
					"Asserts $/Monitor/cesar/test0/Results metanode scope",
					DmtMetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals(
					"Asserts $/Monitor/cesar/test0/Results metanode format",
					DmtData.FORMAT_STRING, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Results metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);

			tbc.assertTrue(
					"Asserts $/Monitor/cesar/test0/Results metanode ADD",
					!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue(
					"Asserts $/Monitor/cesar/test0/Results metanode GET",
					metaNode.can(DmtMetaNode.CMD_GET));
			tbc.assertTrue(
					"Asserts $/Monitor/cesar/test0/Results metanode REPLACE",
					!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue(
					"Asserts $/Monitor/cesar/test0/Results metanode DELETE",
					!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue(
					"Asserts $/Monitor/cesar/test0/Results metanode EXECUTE",
					!metaNode.can(DmtMetaNode.CMD_EXECUTE));

			tbc
					.assertEquals(
							"Asserting the value stored in /Monitor/cesar/test0/Results",
							"test1",
							session
									.getNodeValue(
											MonitorTestControl.DMT_URI_MONITORABLE_SV1_RESULTS)
									.getString());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Monitor/cesar/test0/Server is a valid node and
	 * asserts Type, Cardinality, Add Permission, Get Permission, Replace
	 * Permission, Delete Permission and Execute Permission according to Table
	 * 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure007() {
		tbc.log("#testTreeStructure007");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if ./OSGi/Monitor/cesar/test0/Server is a valid node",
							session
									.isNodeUri(MonitorTestControl.DMT_URI_MONITORABLE_SV1_SERVER));

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE_SV1_SERVER);

			tbc.assertEquals(
					"Asserts $/Monitor/cesar/test0/Server metanode scope",
					DmtMetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals(
					"Asserts $/Monitor/cesar/test0/Server metanode format",
					DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);

			tbc.assertTrue("Asserts $/Monitor/cesar/test0/Server metanode ADD",
					!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Monitor/cesar/test0/Server metanode GET",
					metaNode.can(DmtMetaNode.CMD_GET));
			tbc.assertTrue(
					"Asserts $/Monitor/cesar/test0/Server metanode REPLACE",
					!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue(
					"Asserts $/Monitor/cesar/test0/Server metanode DELETE",
					!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue(
					"Asserts $/Monitor/cesar/test0/Server metanode EXECUTE",
					!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Monitor/cesar/test0/Server/remoteServer is a
	 * valid node and asserts Type, Cardinality, Add Permission, Get Permission,
	 * Replace Permission, Delete Permission and Execute Permission according to
	 * Table 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure008() {
		tbc.log("#testTreeStructure008");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]);

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer metanode scope",
							DmtMetaNode.DYNAMIC, metaNode.getScope());

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer metanode cardinality",
							metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == Integer.MAX_VALUE);

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer metanode ADD",
							metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer metanode GET",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer metanode REPLACE",
							!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer metanode DELETE",
							metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer metanode EXECUTE",
							metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0] });
		}
	}

	/**
	 * This method asserts if $/Monitor/cesar/test0/Server/remoteServer/ServerId
	 * is a valid node and asserts Type, Cardinality, Add Permission, Get
	 * Permission, Replace Permission, Delete Permission and Execute Permission
	 * according to Table 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure009() {
		tbc.log("#testTreeStructure009");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[4]);

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/ServerId metanode scope",
							DmtMetaNode.AUTOMATIC, metaNode.getScope());

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/ServerId metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/ServerId metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/ServerId metanode ADD",
							!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/ServerId metanode GET",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/ServerId metanode REPLACE",
							metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/ServerId metanode DELETE",
							!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/ServerId metanode EXECUTE",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

			tbc
					.assertEquals(
							"Asserting the value stored in /Monitor/cesar/test0/Server/remoteServer/ServerId",
							MonitorTestControl.REMOTE_SERVER,
							session
									.getNodeValue(
											MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[4])
									.getString());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0] });
		}
	}

	/**
	 * This method asserts if $/Monitor/cesar/test0/Server/remoteServer/Enabled
	 * is a valid node and asserts Type, Cardinality, Add Permission, Get
	 * Permission, Replace Permission, Delete Permission and Execute Permission
	 * according to Table 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure010() {
		tbc.log("#testTreeStructure010");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[3]);

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Enabled metanode scope",
							DmtMetaNode.AUTOMATIC, metaNode.getScope());

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Enabled metanode format",
							DmtData.FORMAT_BOOLEAN, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Enabled metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Enabled metanode ADD",
							!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Enabled metanode GET",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Enabled metanode REPLACE",
							metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Enabled metanode DELETE",
							!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Enabled metanode EXECUTE",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

			tbc
					.assertTrue(
							"Asserting the value stored in /Monitor/cesar/test0/Server/remoteServer/Enabled",
							!session
									.getNodeValue(
											MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[4])
									.getBoolean());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0] });
		}
	}

	/**
	 * This method asserts if
	 * $/Monitor/cesar/test0/Server/remoteServer/Reporting is a valid node and
	 * asserts Type, Cardinality, Add Permission, Get Permission, Replace
	 * Permission, Delete Permission and Execute Permission according to Table
	 * 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure011() {
		tbc.log("#testTreeStructure011");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[5]);

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting metanode scope",
							DmtMetaNode.AUTOMATIC, metaNode.getScope());

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting metanode ADD",
							!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting metanode GET",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting metanode REPLACE",
							!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting metanode DELETE",
							!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting metanode EXECUTE",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0] });
		}
	}

	/**
	 * This method asserts if
	 * $/Monitor/cesar/test0/Server/remoteServer/Reporting/Type is a valid node
	 * and asserts Type, Cardinality, Add Permission, Get Permission, Replace
	 * Permission, Delete Permission and Execute Permission according to Table
	 * 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure012() {
		tbc.log("#testTreeStructure012");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[1]);

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Type metanode scope",
							DmtMetaNode.AUTOMATIC, metaNode.getScope());

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Type metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Type metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Type metanode ADD",
							!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Type metanode GET",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Type metanode REPLACE",
							metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Type metanode DELETE",
							!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Type metanode EXECUTE",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0] });
		}
	}

	/**
	 * This method asserts if
	 * $/Monitor/cesar/test0/Server/remoteServer/Reporting/Value is a valid node
	 * and asserts Type, Cardinality, Add Permission, Get Permission, Replace
	 * Permission, Delete Permission and Execute Permission according to Table
	 * 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure013() {
		tbc.log("#testTreeStructure013");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[2]);

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Value metanode scope",
							DmtMetaNode.AUTOMATIC, metaNode.getScope());

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Value metanode format",
							DmtData.FORMAT_INTEGER, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Value metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Value metanode ADD",
							!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Value metanode GET",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Value metanode REPLACE",
							metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Value metanode DELETE",
							!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/Reporting/Value metanode EXECUTE",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0] });
		}
	}

	/**
	 * This method asserts if $/Monitor/cesar/test0/Server/remoteServer/TrapRef
	 * is a valid node and asserts Type, Cardinality, Add Permission, Get
	 * Permission, Replace Permission, Delete Permission and Execute Permission
	 * according to Table 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure014() {
		tbc.log("#testTreeStructure014");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[6]);

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef metanode scope",
							DmtMetaNode.AUTOMATIC, metaNode.getScope());

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef metanode ADD",
							!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef metanode GET",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef metanode REPLACE",
							!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef metanode DELETE",
							!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef metanode EXECUTE",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0] });
		}
	}

	/**
	 * This method asserts if
	 * $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId is a valid node
	 * and asserts Type, Cardinality, Add Permission, Get Permission, Replace
	 * Permission, Delete Permission and Execute Permission according to Table
	 * 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure015() {
		tbc.log("#testTreeStructure015");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId

			session
					.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[7]);

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[7]);

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId metanode scope",
							DmtMetaNode.DYNAMIC, metaNode.getScope());

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId metanode cardinality",
							metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == Integer.MAX_VALUE);

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId metanode ADD",
							metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId metanode GET",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId metanode REPLACE",
							!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId metanode DELETE",
							metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId metanode EXECUTE",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.cleanUp(session, new String[] {
					MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[7],
					MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0] });
		}
	}

	/**
	 * This method asserts if
	 * $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId/TrapRefID is a
	 * valid node and asserts Type, Cardinality, Add Permission, Get Permission,
	 * Replace Permission, Delete Permission and Execute Permission according to
	 * Table 3-4
	 * 
	 * @spec 3.4 Monitor Management Object, Table 3-4
	 */
	private void testTreeStructure016() {
		tbc.log("#testTreeStructure016");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					MonitorTestControl.DMT_URI_MONITORABLE1,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId

			session
					.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[7]);

			DmtMetaNode metaNode = session
					.getMetaNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[8]);

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId/TrapRefID metanode scope",
							DmtMetaNode.AUTOMATIC, metaNode.getScope());

			tbc
					.assertEquals(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId/TrapRefID metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId/TrapRefID metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);

			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId/TrapRefID metanode ADD",
							!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId/TrapRefID metanode GET",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId/TrapRefID metanode REPLACE",
							metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId/TrapRefID metanode DELETE",
							!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Monitor/cesar/test0/Server/remoteServer/TrapRef/testId/TrapRefID metanode EXECUTE",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.cleanUp(session, new String[] {
					MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[7],
					MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0] });
		}
	}

}
