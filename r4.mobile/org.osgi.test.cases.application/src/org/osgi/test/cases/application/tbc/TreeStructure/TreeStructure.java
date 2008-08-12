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
 * 24/05/2005  	Alexandre Santos
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */

package org.osgi.test.cases.application.tbc.TreeStructure;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationException;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Santos This Test Class Validates the TreeStructure
 *         described in MEG documentation.
 */
public class TreeStructure {

	private ApplicationTestControl tbc;

	/**
	 * @param tbc
	 */
	public TreeStructure(ApplicationTestControl tbc) {
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
		testTreeStructure017();
		testTreeStructure018();
		testTreeStructure019();
		testTreeStructure020();
		testTreeStructure021();
		testTreeStructure022();
		testTreeStructure023();
		testTreeStructure024();
		testTreeStructure025();
		testTreeStructure026();
		testTreeStructure027();
		testTreeStructure028();
		testTreeStructure029();
		testTreeStructure030();
		testTreeStructure031();
		testTreeStructure032();
		testTreeStructure033();
		testTreeStructure034();
		testTreeStructure035();
		testTreeStructure036();
		testTreeStructure037();
		testTreeStructure038();
		testTreeStructure039();
		testTreeStructure040();
		testTreeStructure041();
		testTreeStructure042();
		testTreeStructure043();
		testTreeStructure044();
		testTreeStructure045();
		testTreeStructure046();
		testTreeStructure047();
		testTreeStructure048();
		testTreeStructure049();
		testTreeStructure050();
		testTreeStructure051();
		testTreeStructure052();
	}

	/**
	 * This method asserts if $/Application is a valid node and asserts Type,
	 * Cardinality, Get Permission according to Table 3.6
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure001() {
		tbc.log("#testTreeStructure001");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);

			tbc.assertTrue("Asserts if $/Application is a valid node", session
					.isNodeUri(ApplicationConstants.OSGI_APPLICATION));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION);

			tbc.assertEquals("Asserts $/Application metanode scope",
					MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertEquals("Asserts $/Application metanode format",
					DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc.assertTrue("Asserts $/Application metanode cardinality",
					!metaNode.isZeroOccurrenceAllowed()
							&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue("Asserts $/Application metanode GET", metaNode
					.can(MetaNode.CMD_GET));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;is a valid node and asserts
	 * Type, Cardinality, Get Permission according to Table 3.6
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure002() {
		tbc.log("#testTreeStructure002");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt; is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID);

			tbc.assertEquals("Asserts $/Application/&lt;app_id&gt; metanode scope",
					MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertEquals("Asserts $/Application/&lt;app_id&gt; metanode format",
					DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt; metanode cardinality",
							metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
			tbc.assertTrue("Asserts $/Application/&lt;app_id&gt; metanode GET",
					metaNode.can(MetaNode.CMD_GET));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Name is a valid node and
	 * asserts Type, Cardinality, Get Permission according to Table 3.6 . Then,
	 * asserts the value of the node.
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure003() {
		tbc.log("#testTreeStructure003");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Name is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_NAME));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_NAME);

			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Name metanode scope",
					MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Name metanode format",
					DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc.assertTrue(
					"Asserts $/Application/&lt;app_id&gt;/Name metanode cardinality",
					!metaNode.isZeroOccurrenceAllowed()
							&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue("Asserts $/Application/&lt;app_id&gt;/Name metanode GET",
					metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertEquals(
							"Asserting the value of $/Application/&lt;app_id&gt;/Name",
							ApplicationConstants.APPLICATION_NAME,
							session
									.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_NAME).getString());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Application/ &lt;app_id&gt;/IconURI is a valid node
	 * and asserts Type, Cardinality, Get Permission according to Table 3.6 .
	 * Then, asserts the value of the node.
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure004() {
		tbc.log("#testTreeStructure004");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/IconURI is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_ICONURI));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_ICONURI);

			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/IconURI metanode scope",
					MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/IconURI metanode format",
					DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/IconURI metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue(
					"Asserts $/Application/&lt;app_id&gt;/IconURI metanode GET",
					metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertEquals(
							"Asserting the value of $/Application/&lt;app_id&gt;/IconURI",
							tbc.getServiceProperty(ApplicationDescriptor.class.getName(), "application.icon", ApplicationConstants.TEST_PID).toString(),
							session
									.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_ICONURI).getString());
			
			InputStream stream = new URL(session
									.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_ICONURI).getString()).openStream();
			
			tbc.assertNotNull("Asserting if the icon stream was returned.", stream);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Application/ &lt;app_id&gt;/Vendor is a valid node and
	 * asserts Type, Cardinality, Get Permission according to Table 3.6 . Then,
	 * asserts the value of the node.
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure005() {
		tbc.log("#testTreeStructure005");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Vendor is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_VENDOR));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_VENDOR);

			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Vendor metanode scope",
					MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Vendor metanode format",
					DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Vendor metanode cardinality",
							metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue(
					"Asserts $/Application/&lt;app_id&gt;/Vendor metanode GET",
					metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertEquals(
							"Asserting the value of $/Application/&lt;app_id&gt;/Vendor",
							"Cesar",
							session
									.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_VENDOR).getString());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Application/ &lt;app_id&gt;/Version is a valid node
	 * and asserts Type, Cardinality, Get Permission according to Table 3.6 .
	 * Then, asserts the value of the node.
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure006() {
		tbc.log("#testTreeStructure006");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Version is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_VERSION));
			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_VERSION);
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Version metanode scope",
					MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Version metanode format",
					DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Version metanode cardinality",
							metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue(
					"Asserts $/Application/&lt;app_id&gt;/Version metanode GET",
					metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertEquals(
							"Asserting the value of $/Application/&lt;app_id&gt;/Version",
							ApplicationConstants.APP_VERSION,
							session
									.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_VERSION).getString());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Locked is a valid node and
	 * asserts Type, Cardinality, Get Permission according to Table 3.6 . Then,
	 * asserts the value of the node.
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure007() {
		tbc.log("#testTreeStructure007");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Locked is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_LOCKED));
			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_LOCKED);
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Locked metanode scope",
					MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Locked metanode format",
					DmtData.FORMAT_BOOLEAN, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Locked metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue(
					"Asserts $/Application/&lt;app_id&gt;/Locked metanode GET",
					metaNode.can(MetaNode.CMD_GET));
			tbc.assertTrue(
					"Asserting the value of $/Application/&lt;app_id&gt;/Locked",
					!session.getNodeValue(
							ApplicationConstants.OSGI_APPLICATION_APPID_LOCKED)
							.getBoolean());

			tbc.getAppDescriptor().lock();

			tbc.assertTrue(
					"Asserting the value of $/Application/&lt;app_id&gt;/Locked",
					session.getNodeValue(
							ApplicationConstants.OSGI_APPLICATION_APPID_LOCKED)
							.getBoolean());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.getAppDescriptor().unlock();
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/ContainerID is a valid
	 * node and asserts Type, Cardinality, Get Permission according to Table 3.6 .
	 * Then, asserts the value of the node.
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure008() {
		tbc.log("#testTreeStructure008");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/ContainerID is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_CONTAINERID));
			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_CONTAINERID);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/ContainerID metanode scope",
							MetaNode.PERMANENT, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/ContainerID metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/ContainerID metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue(
					"Asserts $/Application/&lt;app_id&gt;/ContainerID metanode GET",
					metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This test case starts a scheduled application via ApplicationDescriptor
	 * and verifies if the relative nodes were created in DMT.
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure009() {
		tbc.log("#testTreeStructure009");
		DmtSession session = null;
		ScheduledApplication sa = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			cleanSchedulesNodes(session);

			Map hash = new HashMap();
			hash.put("name", "value");
			sa = tbc.getAppDescriptor().schedule(null, hash,
					ApplicationConstants.TIMER_EVENT,
					ApplicationConstants.EVENT_FILTER, true);

			String[] nodes = session
					.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES);
			tbc
			.assertEquals(
					"Asserting that there is only one ApplicationHandle running.",
					1, nodes.length);
			
			updateScheduleIdConstants(nodes[0]);			

			String[] args = session
					.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS);
			tbc.assertNotNull("Scheduled arguments subtree was created", args);

			updateScheduleArgumentsIdConstants(args[0]);
			
			String name = session
					.getNodeValue(
							ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_NAME).getString();
			String value = session
					.getNodeValue(
							ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_VALUE).getString();

			tbc
					.assertEquals(
							"Asserts if parameter name  passed as an argument to the scheduled application was created.",
							"name", name);
			tbc
					.assertEquals(
							"Asserts if parameter value passed as an argument to the scheduled application was created.",
							"value", value);
			
			value = session
			.getNodeValue(
					ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER).getString();
			
			tbc
			.assertEquals(
					"Asserts if the topic filter passed as argument to the scheduled application was the same stored in dmt.",
					ApplicationConstants.TIMER_EVENT, value);	
			
			value = session
			.getNodeValue(
					ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER).getString();
			
			tbc
			.assertEquals(
					"Asserts if the event filter passed as argument to the scheduled application was the same stored in dmt.",
					ApplicationConstants.EVENT_FILTER, value);
			
			tbc
			.assertTrue(
					"Asserts if the recurring parameter passed to the scheduled application was the same stored in dmt.",
					session
					.getNodeValue(
							ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_RECURRING).getBoolean());			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {			
			sa.remove();
			tbc.destroyHandles();
			tbc.closeSession(session);
		}
	}

	private void cleanSchedulesNodes(DmtSession session) {
		try {
			String[] nodes = session.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES);
			for (int i=0; i<nodes.length; i++) {
				session.deleteNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES + "/" + nodes[i]);
			}
		} catch (DmtException e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
		
	}
	
	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Ext is a valid node and
	 * asserts Type, Cardinality, Get Permission according to Table 3.6 .
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure010() {
		tbc.log("#testTreeStructure010");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			
			if (session
					.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_EXT)) {
				MetaNode metaNode = session
						.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_EXT);
				tbc.assertEquals(
						"Asserts $/Application/&lt;app_id&gt;/Ext metanode scope",
						MetaNode.PERMANENT, metaNode.getScope());
				tbc.assertEquals(
						"Asserts $/Application/&lt;app_id&gt;/Ext metanode format",
						DmtData.FORMAT_NODE, metaNode.getFormat());
				tbc
						.assertTrue(
								"Asserts $/Application/&lt;app_id&gt;/Ext metanode cardinality",
								metaNode.isZeroOccurrenceAllowed()
										&& metaNode.getMaxOccurrence() == 1);
				tbc.assertTrue(
						"Asserts $/Application/&lt;app_id&gt;/Ext metanode GET",
						metaNode.can(MetaNode.CMD_GET));
			}
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Application/ &lt;app_id&gt;/Instances is a valid node
	 * and asserts Type, Cardinality, Get Permission according to Table 3.6 .
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure011() {
		tbc.log("#testTreeStructure011");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Instances is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES));
			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES);
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Instances metanode scope",
					MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Instances metanode format",
					DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Instances metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue(
					"Asserts $/Application/&lt;app_id&gt;/Instances metanode GET",
					metaNode.can(MetaNode.CMD_GET));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Application/ &lt;app_id&gt;/Operations is a valid node
	 * and asserts Type, Cardinality, Get Permission according to Table 3.8 .
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure012() {
		tbc.log("#testTreeStructure012");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Operations is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS));
			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS);
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Operations metanode scope",
					MetaNode.PERMANENT, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue(
					"Asserts $/Application/&lt;app_id&gt;/Operations metanode GET",
					metaNode.can(MetaNode.CMD_GET));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Application/ &lt;app_id&gt;/Schedules is a valid node
	 * and asserts Type, Cardinality, Get Permission according to Table 3.6 .
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure013() {
		tbc.log("#testTreeStructure013");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Schedules is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES));
			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES);
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Schedules metanode scope",
					MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Schedules metanode format",
					DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue(
					"Asserts $/Application/&lt;app_id&gt;/Schedules metanode GET",
					metaNode.can(MetaNode.CMD_GET));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Application/ &lt;app_id&gt;/Operations/Lock is a valid
	 * node and asserts Type, Cardinality, Get Permission, Execute Permission
	 * according to Table 3.8 .
	 * 
	 * @spec 3.5.4 Launching new application instaces
	 */
	private void testTreeStructure014() {
		tbc.log("#testTreeStructure014");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Operations/Lock is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LOCK));
			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LOCK);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Lock metanode scope",
							MetaNode.PERMANENT, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Lock metanode format",
							DmtData.FORMAT_NULL, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Lock metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Lock metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Lock metanode EXECUTE",
							metaNode.can(MetaNode.CMD_EXECUTE));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if executing the $/Application/
	 * &lt;app_id&gt;/Operations/Lock node, it really locks the ApplicationDescriptor.
	 * 
	 * @spec 3.5.4 Launching new application instaces
	 */
	private void testTreeStructure015() {
		tbc.log("#testTreeStructure015");
		DmtSession session = null;
		ApplicationHandle handle = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.getAppDescriptor().lock();
			session
					.execute(
							ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LOCK,
							null);
			handle = tbc.getAppDescriptor().launch(null);

			tbc.failException("", Exception.class);
		} catch (Exception e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { Exception.class.getName() }));
		} finally {
			tbc.getAppDescriptor().unlock();
			tbc.closeSession(session);
			tbc.cleanUp(handle);
		}
	}

	/**
	 * This method asserts if $/Application/ &lt;app_id&gt;/Operations/Unlock is a
	 * valid node and asserts Type, Cardinality, Get Permission and Execute
	 * Permission according to Table 3.8 .
	 * 
	 * @spec 3.5.4 Launching new application instaces
	 */
	private void testTreeStructure016() {
		tbc.log("#testTreeStructure016");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Operations/Unlock is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_UNLOCK));
			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_UNLOCK);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Unlock metanode scope",
							MetaNode.PERMANENT, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Unlock metanode format",
							DmtData.FORMAT_NULL, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Unlock metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Unlock metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Unlock metanode EXECUTE",
							metaNode.can(MetaNode.CMD_EXECUTE));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if executing the $/Application/
	 * &lt;app_id&gt;/Operations/Unlock node, it really unlocks the
	 * ApplicationDescriptor.
	 * 
	 * @spec 3.5.4 Launching new application instaces
	 */
	private void testTreeStructure017() {
		tbc.log("#testTreeStructure017");
		DmtSession session = null;
		ApplicationHandle handle = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.getAppDescriptor().lock();
			session
					.execute(
							ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_UNLOCK,
							null);
			handle = tbc.getAppDescriptor().launch(null);
			tbc
					.assertNotNull(
							"Asserting if a non-null value is returned, and the execute method has really worked.",
							handle);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
			tbc.cleanUp(handle);
		}
	}

	/**
	 * This method asserts if $/Application/ &lt;app_id&gt;/Operations/Launch is a
	 * valid node and asserts Type, Cardinality, Get Permission according to
	 * Table 3.8 .
	 * 
	 * @spec 3.5.4 Launching new application instaces
	 */
	private void testTreeStructure018() {
		tbc.log("#testTreeStructure018");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Operations/Launch is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH));
			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch metanode scope",
							MetaNode.PERMANENT, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch metanode GET",
							metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;
	 * is a valid node and asserts Type, Cardinality, Add Permission, Get
	 * Permission, Delete Permission and Execute Permission according to Table
	 * 3.8 .
	 * 
	 * @spec 3.5.4 Launching new application instaces
	 */
	private void testTreeStructure019() {
		tbc.log("#testTreeStructure019");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateLaunchIdConstants("/Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);

			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt; metanode scope",
							MetaNode.DYNAMIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt; metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt; metanode cardinality",
							metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt; metanode ADD",
							metaNode.can(MetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt; metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt; metanode DELETE",
							metaNode.can(MetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt; metanode EXECUTE",
							metaNode.can(MetaNode.CMD_EXECUTE));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
		}
	}

	/**
	 * This method asserts if $/Application/ &lt;app_id&gt;/Operations/Launch/
	 * &lt;launch_id&gt;/Arguments/&lt;arg_id&gt; is a valid node and asserts Type,
	 * Cardinality, Add Permission, Get Permission, Delete Permission according
	 * to Table 3.7 .
	 * 
	 * @spec 3.5.4 Launching new application instaces
	 */
	private void testTreeStructure020() {
		tbc.log("#testTreeStructure020");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateLaunchIdConstants("/Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID);

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID);

			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt; metanode scope",
							MetaNode.DYNAMIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt; metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt; metanode cardinality",
							metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt; metanode ADD",
							metaNode.can(MetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt; metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt; metanode DELETE",
							metaNode.can(MetaNode.CMD_DELETE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
		}
	}

	/**
	 * This method asserts if $/Application/ &lt;app_id&gt;/Operations/Launch/
	 * &lt;launch_id&gt;/Arguments/&lt;arg_id&gt;/Name is a valid node and asserts Type,
	 * Cardinality, Get Permission, Replace Permission according to Table 3.7 .
	 * 
	 * @spec 3.5.3 Application Arguments
	 */
	private void testTreeStructure021() {
		tbc.log("#testTreeStructure021");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateLaunchIdConstants("/Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);
			
			session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID);

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID_NAME);

			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt;/Name metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt;/Name metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt;/Name metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt;/Name metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt;/Name metanode REPLACE",
							metaNode.can(MetaNode.CMD_REPLACE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
		}
	}

	/**
	 * This method asserts if $/Application/ &lt;app_id&gt;/Operations/Launch/
	 * &lt;launch_id&gt;/Arguments/&lt;arg_id&gt;/Value is a valid node and asserts Type,
	 * Cardinality, Get Permission, Replace Permission according to Table 3.7 .
	 * 
	 * @spec 3.5.3 Application Arguments
	 */
	private void testTreeStructure022() {
		tbc.log("#testTreeStructure022");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateLaunchIdConstants("/Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);
			
			session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID);

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID_VALUE);

			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt;/Value metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt;/Value metanode format",
							(DmtData.FORMAT_BINARY | DmtData.FORMAT_STRING
									| DmtData.FORMAT_INTEGER
									| DmtData.FORMAT_FLOAT
									| DmtData.FORMAT_BOOLEAN | DmtData.FORMAT_NULL),
							metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt;/Value metanode cardinality",
							metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt;/Value metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments/&lt;arg_id&gt;/Value metanode REPLACE",
							metaNode.can(MetaNode.CMD_REPLACE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
		}
	}

	/**
	 * This method asserts if executing $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;,
	 * this method is executed synchronously, and if the Result/InstanceID node
	 * is updated with the instance identifier of the newly created application
	 * instance, if the Result/Status node is updated to OK and if the
	 * Result/Message node is set to an empty string.
	 * 
	 * @spec 3.5.4 Launching new application instaces
	 */
	private void testTreeStructure023() {
		tbc.log("#testTreeStructure023");
		DmtSession session = null;
		ApplicationHandle handle = null;
		try {
			tbc.destroyHandles();
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			updateLaunchIdConstants("/Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);
			session
					.execute(
							ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID,
							null);

			handle = tbc.getAppHandle();

			tbc
					.assertNotNull(
							"Asserting that exist a ApplicationHandle in service registry.",
							handle);
			tbc
					.assertEquals(
							"Asserting if the Result/InstanceID node is updated with the instance identifier of the newly created application instance.",
							handle.getInstanceId(),
							session
									.getNodeValue(
											ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_INSTANCEID)
									.getString());
			tbc
					.assertEquals(
							"Asserting if the Result/Status node is updated to OK",
							"OK",
							session
									.getNodeValue(
											ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_STATUS)
									.getString());
			tbc
					.assertEquals(
							"Asserting if the Result/Message node is set to an empty string",
							"",
							session
									.getNodeValue(
											ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_MESSAGE)
									.getString());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
			tbc.cleanUp(handle);
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Operations/Launch/
	 * &lt;launch_id&gt;/Arguments is a valid node and asserts Type, Cardinality, Get
	 * Permission according to Table 3.7 .
	 * 
	 * @spec 3.5.3 Application Arguments
	 */
	private void testTreeStructure024() {
		tbc.log("#testTreeStructure024");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateLaunchIdConstants("/Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);		
			
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Operations/Launch/Arguments is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS);

			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Arguments metanode GET",
							metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/State
	 * is a valid node and asserts Type, Cardinality, Get Permission according to
	 * Table 3.9 .
	 * 
	 * @spec 3.5.6 Application Instances
	 */
	private void testTreeStructure025() {
		tbc.log("#testTreeStructure025");
		DmtSession session = null;
		ApplicationHandle handle = null;
		try {
			handle = tbc.getAppDescriptor().launch(null);

			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);

			String[] nodes = session
			.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES);

			tbc
			.assertEquals(
					"Asserting that there is only one ApplicationHandle running.",
					1, nodes.length);		
			
			updateInstanceIdConstants("/"+nodes[0]);
				
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/State is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_STATE));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_STATE);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/State metanode scope",
							MetaNode.PERMANENT, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/State metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/State metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/State metanode GET",
							metaNode.can(MetaNode.CMD_GET));

			DmtData value = session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_STATE);
			tbc.assertEquals("Asserting if the node value matches the application.state value", ApplicationHandle.RUNNING, value.getString());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
			tbc.cleanUp(handle);
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations
	 * is a valid node and asserts Type, Cardinality, Get Permission according 
	 * to Table 3.9 .
	 * 
	 * @spec 3.5.6 Application Instances
	 */
	private void testTreeStructure026() {
		tbc.log("#testTreeStructure026");
		DmtSession session = null;
		ApplicationHandle handle = null;
		try {
			handle = tbc.getAppDescriptor().launch(null);

			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			
			String[] nodes = session
			.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES);

			tbc
			.assertEquals(
					"Asserting that there is only one ApplicationHandle running.",
					1, nodes.length);		
			
			updateInstanceIdConstants("/"+nodes[0]);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations metanode scope",
							MetaNode.PERMANENT, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations metanode GET",
							metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
			tbc.cleanUp(handle);
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Stop
	 * is a valid node and asserts Type, Cardinality, Get Permission and Execute Permission
	 * according to Table 3.9 .
	 * 
	 * @spec 3.5.6 Application Instances
	 */
	private void testTreeStructure027() {
		tbc.log("#testTreeStructure027");
		DmtSession session = null;
		ApplicationHandle handle = null;
		try {
			handle = tbc.getAppDescriptor().launch(null);

			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			
			String[] nodes = session
			.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES);

			tbc
			.assertEquals(
					"Asserting that there is only one ApplicationHandle running.",
					1, nodes.length);		
			
			updateInstanceIdConstants("/"+nodes[0]);
			
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Stop is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_STOP));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_STOP);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Stop metanode scope",
							MetaNode.PERMANENT, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Stop metanode format",
							DmtData.FORMAT_NULL, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Stop metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Stop metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Stop metanode EXECUTE",
							metaNode.can(MetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
			tbc.cleanUp(handle);
		}
	}

	/**
	 * This method asserts if executing $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Stop ,
	 * it really stops the ApplicationHandle.
	 * 
	 * @spec 3.5.6 Application Instances
	 */
	private void testTreeStructure028() {
		tbc.log("#testTreeStructure028");
		DmtSession session = null;
		ApplicationHandle handle = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			handle = tbc.getAppDescriptor().launch(null);

			tbc
					.assertNotNull(
							"Asserting that exist a ApplicationHandle in service registry.",
							handle);			
			
			String[] nodes = session.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES);

			tbc
			.assertEquals(
					"Asserting that there is only one ApplicationHandle running.",
					1, nodes.length);		
			
			updateInstanceIdConstants("/"+nodes[0]);			

			session
					.execute(
							ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_STOP,
							null);

			tbc.assertNull(
					"Asserting if the ApplicationHandle was really stopped.",
					tbc.getAppHandle());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
			tbc.cleanUp(handle);
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Ext
	 * is a valid node and asserts Type, Cardinality, Get Permission according to Table 3.9 .
	 * 
	 * @spec 3.5.6 Application Instances
	 */
	private void testTreeStructure029() {
		tbc.log("#testTreeStructure029");
		DmtSession session = null;
		ApplicationHandle handle = null;
		try {
			handle = tbc.getAppDescriptor().launch(null);

			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			
			String[] nodes = session
			.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES);

			tbc
			.assertEquals(
					"Asserting that there is only one ApplicationHandle running.",
					1, nodes.length);		
			
			updateInstanceIdConstants("/"+nodes[0]);			

			if (session
					.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_EXT)) {

				tbc
						.assertTrue(
								"Asserts if $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Ext is a valid node",
								session
										.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_EXT));

				MetaNode metaNode = session
						.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_EXT);
				tbc
						.assertEquals(
								"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Ext metanode scope",
								MetaNode.PERMANENT, metaNode.getScope());
				tbc
						.assertEquals(
								"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Ext metanode format",
								DmtData.FORMAT_NODE, metaNode.getFormat());
				tbc
						.assertTrue(
								"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Ext metanode cardinality",
								metaNode.isZeroOccurrenceAllowed()
										&& metaNode.getMaxOccurrence() == 1);
				tbc
						.assertTrue(
								"Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/Operations/Ext metanode GET",
								metaNode.can(MetaNode.CMD_GET));
			}

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
			tbc.cleanUp(handle);
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Schedule/&lt;schedule_id&gt; is
	 * a valid node and asserts Type, Cardinality, Add Permission, Get
	 * Permission, Delete Permission according to Table 3.10 .
	 * 
	 * @spec 3.5.7 Scheduling applications
	 */
	private void testTreeStructure030() {
		tbc.log("#testTreeStructure030");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateScheduleIdConstants("Cesar");			

			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedule/&lt;schedule_id&gt; metanode scope",
							MetaNode.DYNAMIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedule/&lt;schedule_id&gt; metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedule/&lt;schedule_id&gt; metanode cardinality",
							metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedule/&lt;schedule_id&gt; metanode ADD",
							metaNode.can(MetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedule/&lt;schedule_id&gt; metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedule/&lt;schedule_id&gt; metanode DELETE",
							metaNode.can(MetaNode.CMD_DELETE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments
	 * is a valid node and asserts Type, Cardinality, Get Permission according to
	 * Table 3.10 .
	 * 
	 * @spec 3.5.7 Scheduling applications
	 */
	private void testTreeStructure031() {
		tbc.log("#testTreeStructure031");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			updateScheduleIdConstants("Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments metanode GET",
							metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Enabled
	 * is a valid node and asserts Type, Cardinality, Get Permission, Replace Permission
	 * according to Table 3.10 .
	 * 
	 * @spec 3.5.7 Scheduling applications
	 */
	private void testTreeStructure032() {
		tbc.log("#testTreeStructure032");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateScheduleIdConstants("Cesar");

			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Enabled is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Enabled metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Enabled metanode format",
							DmtData.FORMAT_BOOLEAN, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Enabled metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Enabled metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Enabled metanode REPLACE",
							metaNode.can(MetaNode.CMD_REPLACE));

			tbc
					.assertTrue(
							"Asserting if the default value is false.",
							!session
									.getNodeValue(
											ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED)
									.getBoolean());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/TopicFilter
	 * is a valid node and asserts Type, Cardinality, Get Permission, Replace Permission 
	 * according to Table 3.10 .
	 * 
	 * @spec 3.5.7 Scheduling applications
	 */
	private void testTreeStructure033() {
		tbc.log("#testTreeStructure033");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			updateScheduleIdConstants("Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/TopicFilter is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/TopicFilter metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/TopicFilter metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/TopicFilter metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/TopicFilter metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/TopicFilter metanode REPLACE",
							metaNode.can(MetaNode.CMD_REPLACE));

			tbc
					.assertEquals(
							"Asserting if the default value is an empty string.", "",
							session
									.getNodeValue(
											ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER)
									.getString());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/EventFilter
	 * is a valid node and asserts Type, Cardinality, Get Permission, Replace Permission
	 * according to Table 3.10 .
	 * 
	 * @spec 3.5.7 Scheduling applications
	 */
	private void testTreeStructure034() {
		tbc.log("#testTreeStructure034");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			updateScheduleIdConstants("Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/EventFilter is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/EventFilter metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/EventFilter metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/EventFilter metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/EventFilter metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/EventFilter metanode REPLACE",
							metaNode.can(MetaNode.CMD_REPLACE));

			tbc
					.assertEquals(
							"Asserting if the default value is an empty string.",
							"",
							session
									.getNodeValue(
											ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER)
									.getString());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Recurring
	 * is a valid node and asserts Type, Cardinality, Get Permission, Replace Permission
	 * according to Table 3.10 .
	 * 
	 * @spec 3.5.7 Scheduling applications
	 */
	private void testTreeStructure035() {
		tbc.log("#testTreeStructure035");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			updateScheduleIdConstants("Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Recurring is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_RECURRING));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_RECURRING);
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Recurring metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Recurring metanode format",
							DmtData.FORMAT_BOOLEAN, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Recurring metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Recurring metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Recurring metanode REPLACE",
							metaNode.can(MetaNode.CMD_REPLACE));

			tbc
					.assertTrue(
							"Asserting if the default value is false.",							
							!session
									.getNodeValue(
											ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_RECURRING)
									.getBoolean());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Name
	 * is a valid node and asserts Type, Cardinality, Get Permission, Replace Permission
	 * according to Table 3.10 .
	 * 
	 * @spec 3.5.7 Scheduling applications
	 */
	private void testTreeStructure036() {
		tbc.log("#testTreeStructure036");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			updateScheduleIdConstants("Cesar");
			
			session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID);
			
			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Name is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_NAME));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_NAME);

			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Name metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Name metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Name metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Name metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Name metanode REPLACE",
							metaNode.can(MetaNode.CMD_REPLACE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Value
	 * is a valid node and asserts Type, Cardinality, Get Permission, Replace Permission
	 * according to Table 3.10 .
	 * 
	 * @spec 3.5.7 Scheduling applications
	 */
	private void testTreeStructure037() {
		tbc.log("#testTreeStructure037");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateScheduleIdConstants("Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Value is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_VALUE));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_VALUE);

			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Value metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Value metanode format",
							(DmtData.FORMAT_BINARY | DmtData.FORMAT_STRING
									| DmtData.FORMAT_INTEGER
									| DmtData.FORMAT_FLOAT
									| DmtData.FORMAT_BOOLEAN | DmtData.FORMAT_NULL), metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Value metanode cardinality",
							metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Value metanode GET",
							metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/Arguments/&lt;arg_id&gt;/Value metanode REPLACE",
							metaNode.can(MetaNode.CMD_REPLACE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/ApplicationID is a valid
	 * node and asserts Type, Cardinality, Get Permission according to
	 * Table 3.6
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure038() {
		tbc.log("#testTreeStructure038");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/ApplicationID is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_APPLICATION_ID));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_APPLICATION_ID);

			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/ApplicationID metanode scope",
							MetaNode.PERMANENT, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/ApplicationID metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/ApplicationID metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/ApplicationID metanode GET",
							metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Location is a valid node
	 * and asserts Type, Cardinality, Get Permission according to Table 3.6 .
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure039() {
		tbc.log("#testTreeStructure039");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Location is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_LOCATION));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_LOCATION);

			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Location metanode scope",
					MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Location metanode format",
					DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Location metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue(
					"Asserts $/Application/&lt;app_id&gt;/Location metanode GET",
					metaNode.can(MetaNode.CMD_GET));

			tbc
					.assertTrue(
							"Asserting if the location is equal to the defined in service properties.",
							session
									.getNodeValue(
											ApplicationConstants.OSGI_APPLICATION_APPID_LOCATION)
									.getString()
									.equals(
											tbc
													.getServiceProperty(
															"org.osgi.service.application.ApplicationDescriptor",
															ApplicationConstants.APPLICATION_LOCATION, ApplicationConstants.TEST_PID)));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result
	 * is a valid node and asserts Type, Cardinality, Get Permission according to Table 3.8 .
	 * 
	 * @spec 3.5.4 Launching new application instances
	 */
	private void testTreeStructure040() {
		tbc.log("#testTreeStructure040");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateLaunchIdConstants("/Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Operations/&lt;launch_id&gt;/Result is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT);

			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result metanode format",
							DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result metanode GET",
							metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/InstanceID
	 * is a valid node and asserts Type, Cardinality, Get Permission according to Table 3.8 .
	 * 
	 * @spec 3.5.4 Launching new application instances
	 */
	private void testTreeStructure041() {
		tbc.log("#testTreeStructure041");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateLaunchIdConstants("/Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/InstanceID is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_INSTANCEID));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_INSTANCEID);

			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/InstanceID metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/InstanceID metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/InstanceID metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/InstanceID metanode GET",
							metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/Status
	 * is a valid node and asserts Type, Cardinality, Get Permission according to Table 3.8 .
	 * 
	 * @spec 3.5.4 Launching new application instances
	 */
	private void testTreeStructure042() {
		tbc.log("#testTreeStructure042");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateLaunchIdConstants("/Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/Status is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_STATUS));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_STATUS);

			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/Status metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/Status metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/Status metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/Status metanode GET",
							metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
		}
	}

	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/Message
	 * is a valid node and asserts Type, Cardinality, Get Permission according to Table 3.8 .
	 * 
	 * @spec 3.5.4 Launching new application instances
	 */
	private void testTreeStructure043() {
		tbc.log("#testTreeStructure043");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateLaunchIdConstants("/Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/Message is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_MESSAGE));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_MESSAGE);

			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/Message metanode scope",
							MetaNode.AUTOMATIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/Message metanode format",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/Message metanode cardinality",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;/Result/Message metanode GET",
							metaNode.can(MetaNode.CMD_GET));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
		}
	}

	/**
	 * This method starts a scheduled application by Dmt.
	 * 
	 * @spec 3.5.7 Scheduling applications
	 */
	private void testTreeStructure044() {
		tbc.log("#testTreeStructure044");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			ScheduledApplication sa = tbc.getScheduledApplication();
			while (sa != null) {
				sa.remove();
				sa = tbc.getScheduledApplication();
			}

			updateScheduleIdConstants("Cesar");
			
			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID);
			
			session.setNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_NAME, new DmtData("Name"));
			session.setNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_VALUE, new DmtData("Value"));

			session
					.setNodeValue(
							ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER,
							new DmtData(ApplicationConstants.TIMER_EVENT));
			session
					.setNodeValue(
							ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER,
							new DmtData(ApplicationConstants.EVENT_FILTER));

			session
					.setNodeValue(
							ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_RECURRING,
							new DmtData(true));

			session
					.setNodeValue(
							ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED,
							new DmtData(true));

			sa = tbc.getScheduledApplication();

			tbc.assertNotNull(
					"Asserting that a Scheduled Application was registered.",
					sa);

			Map map = sa.getArguments();

			tbc.assertTrue(
					"Asserting that exist a property name called 'Name'", map.containsKey( "Name"));
			tbc.assertEquals(
					"Asserting if the value in 'Name' property is Value",
					"Value", map.get("Name"));

			tbc
					.assertTrue(
							"Asserting if the scheduled application registered has recurring set to true.",
							sa.isRecurring());

			tbc.assertEquals("Asserting the topic filter",
					ApplicationConstants.TIMER_EVENT, sa.getTopic());
			tbc.assertEquals("Asserting the filter value",
					ApplicationConstants.EVENT_FILTER, sa.getEventFilter());

			session
					.setNodeValue(
							ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED,
							new DmtData(false));

			sa = tbc.getScheduledApplication();

			tbc.assertNull(
					"Asserting that a Scheduled Application was unregistered.",
					sa);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc
					.cleanUp(
							session,
							new String[] {
									ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS
											+ "/Name",
									ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS
											+ "/Value",
									ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
			tbc.destroyHandles();
		}
	}
	
	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;
	 * is a valid node and asserts Type, Cardinality, Get Permission according
	 * to Table 3.9 .
	 * 
	 * @spec 3.5.6 Application Instances
	 */
	private void testTreeStructure045() {
		tbc.log("#testTreeStructure045");
		DmtSession session = null;
		ApplicationHandle handle = null;
		try {
			handle = tbc.getAppDescriptor().launch(null);

			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			
			String[] nodes = session
			.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES);

			tbc
			.assertEquals(
					"Asserting that there is only one ApplicationHandle running.",
					1, nodes.length);		
			
			updateInstanceIdConstants("/"+nodes[0]);
			
			MetaNode metaNode = session
            .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID);
        
        tbc
            .assertEquals(
                "Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt; metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
        
        tbc
            .assertEquals(
                "Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt; metanode format",
                DmtData.FORMAT_NODE, metaNode.getFormat());
        
        tbc
            .assertTrue(
                "Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt; metanode cardinality",
                metaNode.isZeroOccurrenceAllowed()
                    && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
                  
        tbc
            .assertTrue(
                "Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt; metanode GET",
                metaNode.can(MetaNode.CMD_GET));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
			tbc.cleanUp(handle);
		}
	}
	
	/**
	 * This method asserts the values of Result/InstanceID, Result/Status
	 * and Result/Message after a failed execution of
	 * $/Application/&lt;app_id&gt;/Operations/Launch/&lt;launch_id&gt;.
	 * 
	 * @spec 3.5.4 Launching new application instaces
	 */
	private void testTreeStructure046() {
		tbc.log("#testTreeStructure046");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			updateLaunchIdConstants("/Cesar");

			session
					.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);
			
			//lock the descriptor to fail the launch
			tbc.getAppDescriptor().lock();
			
			session
					.execute(
							ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID,
							null);

			tbc
					.assertEquals(
							"Asserting if the Result/InstanceID is empty.",
							"",
							session
									.getNodeValue(
											ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_INSTANCEID)
									.getString());
			tbc
					.assertEquals(
							"Asserting if the Result/Status has the fully wualified class name of the Java Exception.",
							ApplicationException.class.getName(),
							session
									.getNodeValue(
											ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_STATUS)
									.getString());
			tbc
					.assertTrue(
							"Asserting if the Result/Message node is not empty.",
							!session
									.getNodeValue(
											ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_MESSAGE)
									.getString().equals(""));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.getAppDescriptor().unlock();
			tbc
					.cleanUp(
							session,
							new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
		}
	}
	
	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/InstanceID
	 * is a valid node and asserts Type, Cardinality, Get Permission according
	 * to Table 3.9 .
	 * 
	 * @spec 3.5.6 Application Instances
	 */
	private void testTreeStructure047() {
		tbc.log("#testTreeStructure047");
		DmtSession session = null;
		ApplicationHandle handle = null;
		try {
			handle = tbc.getAppDescriptor().launch(null);

			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);
			
			String[] nodes = session
			.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES);

			tbc
			.assertEquals(
					"Asserting that there is only one ApplicationHandle running.",
					1, nodes.length);		
			
			updateInstanceIdConstants("/"+nodes[0]);

			MetaNode metaNode = session
            .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_INSTANCEID);
        
        tbc
            .assertEquals(
                "Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/InstanceID metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
        
        tbc
            .assertEquals(
                "Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/InstanceID metanode format",
                DmtData.FORMAT_STRING, metaNode.getFormat());
        
        tbc
            .assertTrue(
                "Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt;/InstanceID metanode cardinality",
                !metaNode.isZeroOccurrenceAllowed()
                    && metaNode.getMaxOccurrence() == 1);
                  
        tbc
            .assertTrue(
                "Asserts $/Application/&lt;app_id&gt;/Instances/&lt;instance_id&gt; metanode GET",
                metaNode.can(MetaNode.CMD_GET));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
			tbc.cleanUp(handle);
		}
	}	
	
	/**
	 * This method asserts if $/Application/&lt;app_id&gt;/Valid is a valid node and
	 * asserts Type, Cardinality, Get Permission according to Table 3.6 . Then,
	 * asserts the value of the node.
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure048() {
		tbc.log("#testTreeStructure048");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Valid is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_VALID));

			MetaNode metaNode = session
					.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_VALID);

			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Valid metanode scope",
					MetaNode.PERMANENT, metaNode.getScope());
			tbc.assertEquals(
					"Asserts $/Application/&lt;app_id&gt;/Valid metanode format",
					DmtData.FORMAT_BOOLEAN, metaNode.getFormat());
			tbc.assertTrue(
					"Asserts $/Application/&lt;app_id&gt;/Valid metanode cardinality",
					!metaNode.isZeroOccurrenceAllowed()
							&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue("Asserts $/Application/&lt;app_id&gt;/Valid metanode GET",
					metaNode.can(MetaNode.CMD_GET));
			tbc
					.assertTrue("Asserting the value of $/Application/&lt;app_id&gt;/Valid",							
							session
									.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_VALID).getBoolean());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * This method asserts that when the ApplicationDescriptor is unregistered, the 
	 * $/Application/&lt;app_id&gt;/Valid is set to false.
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure049() {
		tbc.log("#testTreeStructure049");
		DmtSession session = null;
		ScheduledApplication sa = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Valid is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_VALID));

			sa = tbc.getAppDescriptor().schedule(null, null, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);
			
			tbc.unregisterDescriptor();

			tbc
					.assertTrue("Asserting the value of $/Application/&lt;app_id&gt;/Valid",							
							!session
									.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_VALID).getBoolean());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			sa.remove();
			tbc.installDescriptor();
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * This method asserts that when the ApplicationDescriptor is unregistered and
	 * still exist a ScheduledApplication, the node $/Application/&lt;app_id&gt;/Valid is 
	 * not removed and returns an empty value for every nodes except Valid, 
	 * ApplicationId and Schedules.
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure050() {
		tbc.log("#testTreeStructure050");
		DmtSession session = null;
		ScheduledApplication sa = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Valid is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_VALID));

			sa = tbc.getAppDescriptor().schedule(null, null, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);
			
			tbc.unregisterDescriptor();

			tbc
					.assertEquals("Asserting the value of $/Application/&lt;app_id&gt;/Name", "",														
							session
									.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_NAME).getString());
			
			tbc
			.assertTrue("Asserting the value of $/Application/&lt;app_id&gt;/ApplicationID", session
							.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_NAME).getString().equals(""));
			
			tbc
			.assertEquals("Asserting the value of $/Application/&lt;app_id&gt;/IconURI", "",														
					session
							.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_ICONURI).getString());
			
			if (session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_VENDOR)) {

				tbc.assertEquals("Asserting the value of $/Application/&lt;app_id&gt;/Vendor",
								"", session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_VENDOR).getString());

			}
			
			if (session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_VERSION)) {
				tbc
					.assertEquals("Asserting the value of $/Application/&lt;app_id&gt;/Version", "",														
							session
								.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_VERSION).getString());
			}
			
			tbc
			.assertTrue("Asserting the value of $/Application/&lt;app_id&gt;/Locked", !session
							.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_LOCKED).getBoolean());
			
			tbc
			.assertEquals("Asserting the value of $/Application/&lt;app_id&gt;/ContainerID", "",														
					session
							.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_CONTAINERID).getString());
			
			tbc
			.assertEquals("Asserting the value of $/Application/&lt;app_id&gt;/Location", "",														
					session
							.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_LOCATION).getString());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			sa.remove();
			tbc.installDescriptor();
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that after removing the schedules, the 
	 * $/Application/&lt;app_id&gt; must be removed.
	 * 
	 * @spec 3.5.1 Applications Descriptors
	 */
	private void testTreeStructure051() {
		tbc.log("#testTreeStructure051");
		DmtSession session = null;
		ScheduledApplication sa = null;
		try {
			session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Application/&lt;app_id&gt;/Valid is a valid node",
							session
									.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_VALID));

			sa = tbc.getAppDescriptor().schedule(null, null, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);
			
			tbc.unregisterDescriptor();
			
			sa.remove();
			
			while (tbc.getScheduledApplication() != null) {
				
			}

			tbc
			.assertTrue(
					"Asserts if $/Application/&lt;app_id&gt;/Valid is a valid node",
					!session
							.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_VALID));
						
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {			
			tbc.installDescriptor();
			tbc.closeSession(session);
		}
	}
    
    /**
     * This method validates $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/EventFilter
     * according to Table 3.9. If the default value of this node is empty, then
     * an empty string must be converted to a null object in the associated
     * method call.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure052() {
        tbc.log("#testTreeStructure052");
        DmtSession session = null;
        ScheduledApplication sa = null;
        try {
            session = tbc.getDmtAdmin().getSession(ApplicationConstants.OSGI_APPLICATION,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            sa = tbc.getAppDescriptor().schedule("temp", null, ApplicationConstants.TIMER_EVENT, null, true);

            updateScheduleIdConstants("temp");

            tbc.assertTrue(
                            "Asserts if $/Application/&lt;app_id&gt;/Schedules/&lt;schedule_id&gt;/EventFilter is a valid node",
                            session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER));

            // asserting the default value of this node is empty
            tbc.assertEquals("Asserting if the default value is an empty string.",
                            "",session.getNodeValue(
                            ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER).getString());
            
            
            // asserts empty string must be converted to a null object
            tbc.assertNull("An empty string must be converted to a null object", sa.getEventFilter());

        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e.getClass().getName() }));
        } finally {
            tbc.cleanUp(session,new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    }   
		
	private void updateLaunchIdConstants(String value) {	
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH + value;
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID + "/Result";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_INSTANCEID = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT + "/InstanceID";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_STATUS = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT + "/Status";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_MESSAGE = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT + "/Message";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID + "/Arguments";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS + "/1";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID_NAME = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID + "/Name";
	    ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID_VALUE = ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID + "/Value";		
	}
	
	private void updateInstanceIdConstants(String value) {
		ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID = ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES + value;
		ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_STATE = ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID + "/State";
		ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_INSTANCEID = ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID + "/InstanceID";
		ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS = ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID + "/Operations";
		ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_STOP = ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS + "/Stop";
		ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_EXT = ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS + "/Ext";		
	}	
	
	private void updateScheduleIdConstants(String value) {
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID = ApplicationConstants.OSGI_APPLICATION_APPID + "/Schedules/" + value;
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID + "/Arguments";
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS + "/Id";
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_NAME = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID + "/Name";
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_VALUE = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID + "/Value";
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID + "/Enabled";
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID + "/TopicFilter";
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID + "/EventFilter";
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_RECURRING = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID + "/Recurring";		
	}
	
	private void updateScheduleArgumentsIdConstants(String value) {
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID + "/Arguments";
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS + "/" + value;
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_NAME = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID + "/Name";
		ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_VALUE = ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID + "/Value";				
	}
	

}