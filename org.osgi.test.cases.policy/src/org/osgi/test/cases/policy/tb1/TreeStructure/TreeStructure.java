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
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR 7IMPLIED,
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
 * 07/03/2005   Leonardo Barros
 * CR 33        Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.policy.tb1.TreeStructure;

import java.util.Enumeration;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.policy.tbc.PolicyTestControl;
import org.osgi.test.cases.policy.tbc.TestInterface;
import org.osgi.test.cases.policy.tbc.util.MessagesConstants;

/**
 * @generalDescription This class tests the management tree structure for
 *                     Permissions according with MEG specification
 */
public class TreeStructure implements TestInterface {
	// TODO some metanode tests are missing due to misunderstood information in
	// Spec
	private PolicyTestControl tbc;

	public TreeStructure(PolicyTestControl tbc) {
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
	}

	/**
	 * @testID testTreeStructure001
	 * @testDescription Asserts if $/Policy/Java is a valid node
	 */
	public void testTreeStructure001() {
		tbc.log("#testTreeStructure001");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(PolicyTestControl.OSGI_ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			tbc.assertTrue("Asserts if $/Policy/Java is a valid node", session
					.isNodeUri(PolicyTestControl.POLICY_JAVA_NODE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure002
	 * @testDescription Asserts if $/Policy/Java/LocationPermission is a valid
	 *                  node
	 */
	public void testTreeStructure002() {
		tbc.log("#testTreeStructure002");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(PolicyTestControl.OSGI_ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Policy/Java/LocationPermission is a valid node",
							session
									.isNodeUri(PolicyTestControl.LOCATION_PERMISSION_NODE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure003
	 * @testDescription Asserts if $/Policy/Java/PrincipalPermission is a valid
	 *                  node
	 */
	public void testTreeStructure003() {
		tbc.log("#testTreeStructure003");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(PolicyTestControl.OSGI_ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Policy/Java/PrincipalPermission is a valid node",
							session
									.isNodeUri(PolicyTestControl.PRINCIPAL_PERMISSION_NODE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure004
	 * @testDescription Asserts if $/Policy/Java/ConditionalPermission is a
	 *                  valid node
	 */
	public void testTreeStructure004() {
		tbc.log("#testTreeStructure004");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(PolicyTestControl.OSGI_ROOT,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertTrue(
							"Asserts if $/Policy/Java/ConditionalPermission is a valid node",
							session
									.isNodeUri(PolicyTestControl.CONDITIONAL_PERMISSION_NODE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure005
	 * @testDescription Asserts if the correct permission is added in the
	 *                  permission admin when setting permissions from DMT
	 */
	public void testTreeStructure005() {
		tbc.log("#testTreeStructure005");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.LOCATION_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createInteriorNode(PolicyTestControl.TEST_NODE);

			session.setNodeValue(PolicyTestControl.TEST_NODE_LOCATION,
					new DmtData(PolicyTestControl.TEST_NODE));

			PermissionInfo pInfo = new PermissionInfo(AdminPermission.class
					.getName(), "*", "*");

			session.setNodeValue(PolicyTestControl.TEST_NODE_PERMISSION,
					new DmtData(pInfo.getEncoded() + "\n"));

			session.close();

			PermissionInfo pi[] = tbc.getPermissionAdmin().getPermissions(
					PolicyTestControl.TEST_NODE);

			String permEnc = pi[0].getEncoded() + "\n";

			tbc
					.assertEquals(
							"Asserts if the correct permission is added in the permission admin when setting permissions from DMT",
							pInfo.getEncoded() + "\n", permEnc);

			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.LOCATION_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.deleteNode(PolicyTestControl.TEST_NODE);

			session.close();

			pi = tbc.getPermissionAdmin().getPermissions(
					PolicyTestControl.TEST_NODE);

			tbc
					.assertNull(
							"Asserts that permission was removed from permission admin",
							pi);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure006
	 * @testDescription Asserts if the correct permission is added in DMT when
	 *                  setting permissions through permission admin service
	 */
	public void testTreeStructure006() {
		tbc.log("#testTreeStructure006");
		DmtSession session = null;
		try {
			PermissionInfo pInfo = new PermissionInfo(AdminPermission.class
					.getName(), "*", "*");

			tbc.getPermissionAdmin()
					.setPermissions(PolicyTestControl.TEST_NODE,
							new PermissionInfo[] { pInfo });

			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.LOCATION_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_SHARED);

			tbc
					.assertEquals(
							"Asserts if the correct permission is added in DMT when setting permissions through permission admin service",
							pInfo.getEncoded(),
							session
									.getNodeValue(PolicyTestControl.TEST_NODE_PERMISSION));

            session.close();

			tbc.getPermissionAdmin().setPermissions(
					PolicyTestControl.TEST_NODE, null);

			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.LOCATION_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_SHARED);

			tbc.assertTrue("Asserts that permission was removed from DMT",
					session.isNodeUri(PolicyTestControl.TEST_NODE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure007
	 * @testDescription Asserts if the absence of a Default node is equivalent to
	 *                  having All Permission as the default permission
	 */
	public void testTreeStructure007() {
		tbc.log("#testTreeStructure007");
		DmtSession session = null;
		PermissionInfo info[] = null;
		try {
			info = tbc.getPermissionAdmin().getDefaultPermissions();

			PermissionInfo perm = new PermissionInfo(
					java.security.AllPermission.class.getName(), null, null);

			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.LOCATION_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

            if(session.isNodeUri(PolicyTestControl.DEFAULT_PERMISSION_NODE)) {
                session.deleteNode(PolicyTestControl.DEFAULT_PERMISSION_NODE);
            }

			session.close();

			PermissionInfo infos[] = tbc.getPermissionAdmin()
					.getDefaultPermissions();

			tbc
					.assertEquals(
							"Asserts if the absence of aDefault node is equivalent to having AllPermission as the default permission",
							perm.getEncoded(), infos[0].getEncoded());

			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.LOCATION_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createInteriorNode("Default");

			session.setNodeValue("Default", new DmtData(perm.getEncoded()
					+ "\n"));

			session.close();

			tbc.assertEquals("Asserts if the default permission is restored",
					perm.getEncoded() + "\n", tbc.getPermissionAdmin()
							.getDefaultPermissions()[0].getEncoded()
							+ "\n");

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
			tbc.getPermissionAdmin().setDefaultPermissions(info);
		}
	}

	/**
	 * @testID testTreeStructure008
	 * @testDescription Asserts that creating a Default node with an empty
	 *                  string makes the default permissions empty
	 */
	public void testTreeStructure008() {
		tbc.log("#testTreeStructure008");
		DmtSession session = null;
		PermissionInfo info[] = null;
		try {
			info = tbc.getPermissionAdmin().getDefaultPermissions();

			PermissionInfo perm = new PermissionInfo(
					java.security.AllPermission.class.getName(), null, null);

			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.LOCATION_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
            if(!session.isNodeUri(PolicyTestControl.DEFAULT_PERMISSION_NODE)) {
                session.createLeafNode(PolicyTestControl.DEFAULT_PERMISSION_NODE,new DmtData(""));
            }

			session.close();

			PermissionInfo infos[] = tbc.getPermissionAdmin()
					.getDefaultPermissions();

			tbc
					.assertNull(
							"Asserts that permission was removed from permission admin",
							infos);

			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.LOCATION_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.setNodeValue("Default", new DmtData(perm.getEncoded()
					+ "\n"));

			session.close();

			tbc.assertEquals("Asserts if the default permission is restored",
					perm.getEncoded() + "\n", tbc.getPermissionAdmin()
							.getDefaultPermissions()[0].getEncoded()
							+ "\n");

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
			tbc.getPermissionAdmin().setDefaultPermissions(info);
		}
	}

	/**
	 * @testID testTreeStructure009
	 * @testDescription Asserts if ConditionPermission is added in condtional
	 *                  permission service when setting the permission in DMT
	 */
	public void testTreeStructure009() {
		tbc.log("#testTreeStructure009");
		DmtSession session = null;
		try {
			PermissionInfo pInfo1 = new PermissionInfo(ServicePermission.class
					.getName(), "org.osgi.service.http.HttpService", "register");

			PermissionInfo pInfo2 = new PermissionInfo(PackagePermission.class
					.getName(), "org.osgi.*", "IMPORT");

			ConditionInfo cInfo = new ConditionInfo(
					BundleLocationCondition.class.getName(),
					new String[] { PolicyTestControl.PRINCIPAL });

			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.CONDITIONAL_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createInteriorNode(PolicyTestControl.CONDITION_HASH);

			session.setNodeValue(PolicyTestControl.CONDITIONAL_PERMISSIONINFO,
					new DmtData(pInfo1.getEncoded() + "\n"
							+ pInfo2.getEncoded() + "\n"));

			session.setNodeValue(PolicyTestControl.CONDITIONAL_CONDITIONINFO,
					new DmtData(cInfo.getEncoded() + "\n"));

			session.close();

			boolean conditionInserted = false;

			Enumeration en = tbc.getConditionalPermissionAdmin()
					.getConditionalPermissionInfos();

			while (en.hasMoreElements()) {
				ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) en
						.nextElement();

				ConditionInfo ci[] = cpi.getConditionInfos();
				if (ci != null) {
					for (int i = 0; i < ci.length; i++) {
						if (ci[i].getEncoded().equals(cInfo.getEncoded())) {
							conditionInserted = true;
							break;
						}
					}
					if (conditionInserted) {
						break;
					}
				}
			}

			tbc.assertTrue(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_TRUE,
					new String[] { "conditions were inserted" }),
					conditionInserted);

			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.CONDITIONAL_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.deleteNode(PolicyTestControl.CONDITION_HASH);

			session.close();

			conditionInserted = false;

			en = tbc.getConditionalPermissionAdmin()
					.getConditionalPermissionInfos();

			while (en.hasMoreElements()) {
				ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) en
						.nextElement();

				ConditionInfo ci[] = cpi.getConditionInfos();
				if (ci != null) {
					for (int i = 0; i < ci.length; i++) {
						if (ci[i].getEncoded().equals(cInfo.getEncoded())) {
							conditionInserted = true;
							break;
						}
					}
					if (conditionInserted) {
						break;
					}
				}
			}

			tbc.assertTrue(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_TRUE,
					new String[] { "conditions were removed" }),
					!conditionInserted);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure010
	 * @testDescription Asserts if ConditionalPermission is added in DMT when
	 *                  setting the permission through the conditional
	 *                  permission service
	 */
	public void testTreeStructure010() {
		tbc.log("#testTreeStructure010");
		DmtSession session = null;
		try {
			PermissionInfo pInfo1 = new PermissionInfo(ServicePermission.class
					.getName(), "org.osgi.service.http.HttpService", "register");

			PermissionInfo pInfo2 = new PermissionInfo(PackagePermission.class
					.getName(), "org.osgi.*", "IMPORT");

			ConditionInfo cInfo = new ConditionInfo(
					BundleLocationCondition.class.getName(),
					new String[] { PolicyTestControl.PRINCIPAL });

			tbc.getConditionalPermissionAdmin().addConditionalPermissionInfo(
					new ConditionInfo[] { cInfo },
					new PermissionInfo[] { pInfo1, pInfo2 });

			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.CONDITIONAL_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.assertTrue("Asserts if ConditionalPermission is added in DMT",
					session.isNodeUri(PolicyTestControl.CONDITION_HASH));

			tbc.assertEquals("Asserts ConditionInfo node value", cInfo
					.getEncoded()
					+ "\n", session
					.getNodeValue(PolicyTestControl.CONDITIONAL_CONDITIONINFO));

			tbc
					.assertEquals(
							"Asserts PermissionInfo node value",
							pInfo1.getEncoded() + "\n" + pInfo2.getEncoded()
									+ "\n",
							session
									.getNodeValue(PolicyTestControl.CONDITIONAL_PERMISSIONINFO));

			session.deleteNode(PolicyTestControl.CONDITION_HASH);

			session.close();

			boolean conditionInserted = false;

			Enumeration en = tbc.getConditionalPermissionAdmin()
					.getConditionalPermissionInfos();

			while (en.hasMoreElements()) {
				ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) en
						.nextElement();

				ConditionInfo ci[] = cpi.getConditionInfos();
				if (ci != null) {
					for (int i = 0; i < ci.length; i++) {
						if (ci[i].getEncoded().equals(cInfo.getEncoded())) {
							conditionInserted = true;
							break;
						}
					}
					if (conditionInserted) {
						break;
					}
				}
			}

			tbc.assertTrue(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_TRUE,
					new String[] { "conditions were removed" }),
					!conditionInserted);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure011
	 * @testDescription Asserts $/Policy/Java/LocationPermission metanode
	 */
	public void testTreeStructure011() {
		tbc.log("#testTreeStructure011");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.POLICY_JAVA_NODE,
					DmtSession.LOCK_TYPE_SHARED);

			DmtMetaNode metaNode = session
					.getMetaNode(PolicyTestControl.LOCATION_PERMISSION_NODE);

			tbc.assertEquals(
					"Asserts $/Policy/Java/LocationPermission metanode",
					DmtMetaNode.PERMANENT, metaNode.getScope());

			tbc.assertEquals(
					"Asserts $/Policy/Java/LocationPermission metanode",
					DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc.assertTrue("Asserts $/Policy/Java/LocationPermission metanode",
					!metaNode.isZeroOccurrenceAllowed()
							&& metaNode.getMaxOccurrence() == 1);

			tbc.assertTrue("Asserts $/Policy/Java/LocationPermission metanode",
					metaNode.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue("Asserts $/Policy/Java/LocationPermission metanode",
					!metaNode.can(DmtMetaNode.CMD_GET));
			tbc.assertTrue("Asserts $/Policy/Java/LocationPermission metanode",
					!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue("Asserts $/Policy/Java/LocationPermission metanode",
					metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue("Asserts $/Policy/Java/LocationPermission metanode",
					!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							DmtException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure012
	 * @testDescription Asserts $/Policy/Java/LocationPermission/Default
	 *                  metanode
	 */
	public void testTreeStructure012() {
		tbc.log("#testTreeStructure012");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.LOCATION_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_SHARED);

            if(!session.isNodeUri(PolicyTestControl.DEFAULT_PERMISSION_NODE)) {
                session.createLeafNode(PolicyTestControl.DEFAULT_PERMISSION_NODE,new DmtData(""));
            }
            
			DmtMetaNode metaNode = session
					.getMetaNode(PolicyTestControl.DEFAULT_PERMISSION_NODE);

			tbc
					.assertEquals(
							"Asserts $/Policy/Java/LocationPermission/Default metanode",
							DmtMetaNode.DYNAMIC, metaNode.getScope());

			tbc
					.assertEquals(
							"Asserts $/Policy/Java/LocationPermission/Default metanode",
							DmtData.FORMAT_STRING, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/Default metanode",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);

			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/Default metanode",
							!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/Default metanode",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/Default metanode",
							metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/Default metanode",
							metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/Default metanode",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							DmtException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure013
	 * @testDescription Asserts $/Policy/Java/LocationPermission/ <location>
	 *                  metanode
	 */
	public void testTreeStructure013() {
		tbc.log("#testTreeStructure013");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.LOCATION_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createInteriorNode(PolicyTestControl.TEST_NODE);

			DmtMetaNode metaNode = session
					.getMetaNode(PolicyTestControl.TEST_NODE);

			tbc
					.assertEquals(
							"Asserts $/Policy/Java/LocationPermission/<location> metanode",
							DmtMetaNode.DYNAMIC, metaNode.getScope());

			tbc
					.assertEquals(
							"Asserts $/Policy/Java/LocationPermission/<location> metanode",
							DmtData.FORMAT_NODE, metaNode.getFormat());

			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/<location> metanode",
							metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);

			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/<location> metanode",
							metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/<location> metanode",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/<location> metanode",
							!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/<location> metanode",
							!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/<location> metanode",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							DmtException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(session, new String[] { PolicyTestControl.TEST_NODE });
		}
	}

	/**
	 * @testID testTreeStructure014
	 * @testDescription Asserts $/Policy/Java/LocationPermission/
	 *                  <location>/PermissionInfo metanode
	 */
	public void testTreeStructure014() {
		tbc.log("#testTreeStructure014");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.LOCATION_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createInteriorNode(PolicyTestControl.TEST_NODE);

			session.createLeafNode(PolicyTestControl.TEST_NODE_PERMISSION);

			DmtMetaNode metaNode = session
					.getMetaNode(PolicyTestControl.TEST_NODE_PERMISSION);

			tbc
					.assertEquals(
							"Asserts $/Policy/Java/LocationPermission/<location>/PermissionInfo metanode",
							DmtMetaNode.DYNAMIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Policy/Java/LocationPermission/<location>/PermissionInfo metanode",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/<location>/PermissionInfo metanode",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/<location>/PermissionInfo metanode",
							!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/<location>/PermissionInfo metanode",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/<location>/PermissionInfo metanode",
							metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/<location>/PermissionInfo metanode",
							metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/LocationPermission/<location>/PermissionInfo metanode",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							DmtException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(session, new String[] { PolicyTestControl.TEST_NODE });
		}
	}

	/**
	 * @testID testTreeStructure015
	 * @testDescription Asserts $/Policy/Java/PrincipalPermission metanode
	 */
	public void testTreeStructure015() {
		tbc.log("#testTreeStructure015");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.POLICY_JAVA_NODE,
					DmtSession.LOCK_TYPE_SHARED);

			DmtMetaNode metaNode = session
					.getMetaNode(PolicyTestControl.PRINCIPAL_PERMISSION_NODE);

			tbc.assertEquals(
					"Asserts $/Policy/Java/PrincipalPermission metanode",
					DmtMetaNode.PERMANENT, metaNode.getScope());
			tbc.assertEquals(
					"Asserts $/Policy/Java/PrincipalPermission metanode",
					DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc.assertTrue(
					"Asserts $/Policy/Java/PrincipalPermission metanode",
					!metaNode.isZeroOccurrenceAllowed()
							&& metaNode.getMaxOccurrence() == 1);
			tbc.assertTrue(
					"Asserts $/Policy/Java/PrincipalPermission metanode",
					!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc.assertTrue(
					"Asserts $/Policy/Java/PrincipalPermission metanode",
					!metaNode.can(DmtMetaNode.CMD_GET));
			tbc.assertTrue(
					"Asserts $/Policy/Java/PrincipalPermission metanode",
					!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc.assertTrue(
					"Asserts $/Policy/Java/PrincipalPermission metanode",
					!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc.assertTrue(
					"Asserts $/Policy/Java/PrincipalPermission metanode",
					!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							DmtException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure016
	 * @testDescription Asserts $/Policy/Java/PrincipalPermission/ <principal>
	 *                  metanode
	 */
	public void testTreeStructure016() {
		tbc.log("#testTreeStructure016");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.PRINCIPAL_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createInteriorNode(PolicyTestControl.PRINCIPAL);

			DmtMetaNode metaNode = session
					.getMetaNode(PolicyTestControl.PRINCIPAL);

			tbc
					.assertEquals(
							"Asserts $/Policy/Java/PrincipalPermission/<principal> metanode",
							DmtMetaNode.DYNAMIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Policy/Java/PrincipalPermission/<principal> metanode",
							DmtData.FORMAT_NODE, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal> metanode",
							metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal> metanode",
							metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal> metanode",
							!metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal> metanode",
							!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal> metanode",
							metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal> metanode",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							DmtException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(session, new String[] { PolicyTestControl.PRINCIPAL });
		}
	}

	/**
	 * @testID testTreeStructure017
	 * @testDescription Asserts $/Policy/Java/PrincipalPermission/
	 *                  <principal>/Principal metanode
	 */
	public void testTreeStructure017() {
		tbc.log("#testTreeStructure017");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.PRINCIPAL_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createInteriorNode(PolicyTestControl.PRINCIPAL);

			session.createLeafNode(PolicyTestControl.PRINCIPAL_LOCATION);

			DmtMetaNode metaNode = session
					.getMetaNode(PolicyTestControl.PRINCIPAL_LOCATION);

			tbc
					.assertEquals(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/Principal metanode",
							DmtMetaNode.DYNAMIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/Principal metanode",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/Principal metanode",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/Principal metanode",
							metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/Principal metanode",
							!metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/Principal metanode",
							!metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/Principal metanode",
							!metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/Principal metanode",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							DmtException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(session, new String[] { PolicyTestControl.PRINCIPAL });
		}
	}

	/**
	 * @testID testTreeStructure018
	 * @testDescription Asserts $/Policy/Java/PrincipalPermission/
	 *                  <principal>/PermissionInfo metanode
	 */
	public void testTreeStructure018() {
		tbc.log("#testTreeStructure018");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.PRINCIPAL_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createInteriorNode(PolicyTestControl.PRINCIPAL);

			session.createLeafNode(PolicyTestControl.PRINCIPAL_PERMISSION);

			DmtMetaNode metaNode = session
					.getMetaNode(PolicyTestControl.PRINCIPAL_PERMISSION);

			tbc
					.assertEquals(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/PermissionInfo metanode",
							DmtMetaNode.DYNAMIC, metaNode.getScope());
			tbc
					.assertEquals(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/PermissionInfo metanode",
							DmtData.FORMAT_STRING, metaNode.getFormat());
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/PermissionInfo metanode",
							!metaNode.isZeroOccurrenceAllowed()
									&& metaNode.getMaxOccurrence() == 1);
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/PermissionInfo metanode",
							!metaNode.can(DmtMetaNode.CMD_ADD));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/PermissionInfo metanode",
							metaNode.can(DmtMetaNode.CMD_GET));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/PermissionInfo metanode",
							metaNode.can(DmtMetaNode.CMD_REPLACE));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/PermissionInfo metanode",
							metaNode.can(DmtMetaNode.CMD_DELETE));
			tbc
					.assertTrue(
							"Asserts $/Policy/Java/PrincipalPermission/<principal>/PermissionInfo metanode",
							!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							DmtException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(session, new String[] { PolicyTestControl.PRINCIPAL });
		}
	}

	/**
	 * @testID testTreeStructure019
	 * @testDescription Asserts $/Policy/Java/ConditionalPermission metanode
	 */
	public void testTreeStructure019() {
		tbc.log("#testTreeStructure019");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.POLICY_JAVA_NODE,
					DmtSession.LOCK_TYPE_SHARED);


				DmtMetaNode metaNode = session
						.getMetaNode(PolicyTestControl.CONDITIONAL_PERMISSION_NODE);

				tbc.assertEquals(
						"Asserts $/Policy/Java/ConditionalPermission metanode",
						DmtMetaNode.PERMANENT, metaNode.getScope());
				tbc.assertEquals(
						"Asserts $/Policy/Java/ConditionalPermission metanode",
						DmtData.FORMAT_NODE, metaNode.getFormat());
				tbc.assertTrue(
						"Asserts $/Policy/Java/ConditionalPermission metanode",
						metaNode.isZeroOccurrenceAllowed()
								&& metaNode.getMaxOccurrence() == 1);
				tbc.assertTrue(
						"Asserts $/Policy/Java/ConditionalPermission metanode",
						!metaNode.can(DmtMetaNode.CMD_ADD));
				tbc.assertTrue(
						"Asserts $/Policy/Java/ConditionalPermission metanode",
						!metaNode.can(DmtMetaNode.CMD_GET));
				tbc.assertTrue(
						"Asserts $/Policy/Java/ConditionalPermission metanode",
						!metaNode.can(DmtMetaNode.CMD_REPLACE));
				tbc.assertTrue(
						"Asserts $/Policy/Java/ConditionalPermission metanode",
						!metaNode.can(DmtMetaNode.CMD_DELETE));
				tbc.assertTrue(
						"Asserts $/Policy/Java/ConditionalPermission metanode",
						!metaNode.can(DmtMetaNode.CMD_EXECUTE));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							DmtException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure020
	 * @testDescription Asserts $/Policy/Java/ConditionalPermission/ <hash>
	 *                  metanode
	 */
	public void testTreeStructure020() {
		tbc.log("#testTreeStructure020");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.POLICY_JAVA_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);


				session
						.createInteriorNode(PolicyTestControl.CONDITIONAL_PERMISSION_NODE
								+ "/" + PolicyTestControl.CONDITION_HASH);

				DmtMetaNode metaNode = session
						.getMetaNode(PolicyTestControl.CONDITIONAL_PERMISSION_NODE
								+ "/" + PolicyTestControl.CONDITION_HASH);

				tbc
						.assertEquals(
								"Asserts $/Policy/Java/ConditionalPermission/<hash> metanode",
								DmtMetaNode.DYNAMIC, metaNode.getScope());
				tbc
						.assertEquals(
								"Asserts $/Policy/Java/ConditionalPermission/<hash> metanode",
								DmtData.FORMAT_NODE, metaNode.getFormat());
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash> metanode",
								metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash> metanode",
								metaNode.can(DmtMetaNode.CMD_ADD));
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash> metanode",
								!metaNode.can(DmtMetaNode.CMD_GET));
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash> metanode",
								!metaNode.can(DmtMetaNode.CMD_REPLACE));
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash> metanode",
								metaNode.can(DmtMetaNode.CMD_DELETE));
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash> metanode",
								!metaNode.can(DmtMetaNode.CMD_EXECUTE));

				tbc
						.cleanUp(
								session,
								new String[] { PolicyTestControl.CONDITIONAL_PERMISSION_NODE
										+ "/"
										+ PolicyTestControl.CONDITION_HASH });

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							DmtException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure021
	 * @testDescription Asserts $/Policy/Java/ConditionalPermission/
	 *                  <hash>/ConditionInfo metanode
	 */
	public void testTreeStructure021() {
		tbc.log("#testTreeStructure021");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.POLICY_JAVA_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

				session
						.createInteriorNode(PolicyTestControl.CONDITIONAL_PERMISSION_NODE
								+ "/" + PolicyTestControl.CONDITION_HASH);

				DmtMetaNode metaNode = session
						.getMetaNode(PolicyTestControl.CONDITIONAL_PERMISSION_NODE
								+ "/"
								+ PolicyTestControl.CONDITIONAL_CONDITIONINFO);

				tbc
						.assertEquals(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo metanode",
								DmtMetaNode.DYNAMIC, metaNode.getScope());
				tbc
						.assertEquals(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo metanode",
								DmtData.FORMAT_STRING, metaNode.getFormat());
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo metanode",
								!metaNode.isZeroOccurrenceAllowed()
										&& metaNode.getMaxOccurrence() == 1);
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo metanode",
								metaNode.can(DmtMetaNode.CMD_ADD));
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo metanode",
								metaNode.can(DmtMetaNode.CMD_GET));
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo metanode",
								metaNode.can(DmtMetaNode.CMD_REPLACE));
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo metanode",
								metaNode.can(DmtMetaNode.CMD_DELETE));
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo metanode",
								!metaNode.can(DmtMetaNode.CMD_EXECUTE));

				tbc
						.cleanUp(
								session,
								new String[] { PolicyTestControl.CONDITIONAL_PERMISSION_NODE
										+ "/"
										+ PolicyTestControl.CONDITION_HASH });

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							DmtException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testTreeStructure022
	 * @testDescription Asserts $/Policy/Java/ConditionalPermission/
	 *                  <hash>/PermissionInfo metanode
	 */
	public void testTreeStructure022() {
		tbc.log("#testTreeStructure022");
		DmtSession session = null;
		try {
			session = tbc.getDmtAdmin().getSession(
					PolicyTestControl.POLICY_JAVA_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);


				session
						.createInteriorNode(PolicyTestControl.CONDITIONAL_PERMISSION_NODE
								+ "/" + PolicyTestControl.CONDITION_HASH);


				DmtMetaNode metaNode = session
						.getMetaNode(PolicyTestControl.CONDITIONAL_PERMISSION_NODE
								+ "/"
								+ PolicyTestControl.CONDITIONAL_PERMISSIONINFO);

				tbc
						.assertEquals(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo metanode",
								DmtMetaNode.DYNAMIC, metaNode.getScope());
				tbc
						.assertEquals(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo metanode",
								DmtData.FORMAT_STRING, metaNode.getFormat());
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo metanode",
								!metaNode.isZeroOccurrenceAllowed()
										&& metaNode.getMaxOccurrence() == 1);
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo metanode",
								!metaNode.can(DmtMetaNode.CMD_ADD));
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo metanode",
								metaNode.can(DmtMetaNode.CMD_GET));
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo metanode",
								metaNode.can(DmtMetaNode.CMD_REPLACE));
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo metanode",
								metaNode.can(DmtMetaNode.CMD_DELETE));
				tbc
						.assertTrue(
								"Asserts $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo metanode",
								!metaNode.can(DmtMetaNode.CMD_EXECUTE));

				tbc
						.cleanUp(
								session,
								new String[] { PolicyTestControl.CONDITIONAL_PERMISSION_NODE
										+ "/"
										+ PolicyTestControl.CONDITION_HASH });

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							DmtException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
		}
	}

}
