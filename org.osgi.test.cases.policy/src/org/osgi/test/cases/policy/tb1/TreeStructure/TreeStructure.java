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
 * 25/08/2005   Andre Assad
 * CR 178       Policy Plugins Refactoring
 * ===========  ==============================================================
 */
package org.osgi.test.cases.policy.tb1.TreeStructure;

import info.dmtree.DmtData;
import info.dmtree.DmtSession;

import java.util.Enumeration;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.policy.tbc.PolicyConstants;
import org.osgi.test.cases.policy.tbc.PolicyTestControl;
import org.osgi.test.cases.policy.tbc.TestInterface;
import org.osgi.test.cases.policy.tbc.util.MessagesConstants;

/**
 * @author Leonardo Barros
 * 
 * This Test Class Validates the implementation of
 * TreeStructure, according to MEG reference documentation.
 */
public class TreeStructure implements TestInterface {
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
    }

    /**
     * This test asserts if $/Policy/Java is a valid node
     * 
     * @spec 3.7 Policy Management Object, Figure 3-12
     */

	private void testTreeStructure001() {
        tbc.log("#testTreeStructure001");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.OSGI_ROOT,
                DmtSession.LOCK_TYPE_SHARED);
            
            tbc.assertTrue("Asserts if $/Policy/Java is a valid node", 
                session.isNodeUri(PolicyConstants.POLICY_JAVA_NODE));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This test asserts if $/Policy/Java/LocationPermission is a valid node
     *
     * @spec 3.7 Policy Management Object, Figure 3-12
     */

	private void testTreeStructure002() {
        tbc.log("#testTreeStructure002");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.OSGI_ROOT,
                DmtSession.LOCK_TYPE_SHARED);

            tbc.assertTrue(
                "Asserts if $/Policy/Java/LocationPermission is a valid node",
                session.isNodeUri(PolicyConstants.LOCATION_PERMISSION_NODE));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }


    /**
     * This test asserts if $/Policy/Java/LocationPermission/Locations is a valid node
     *
     * @spec 3.7.2 Location Permission Management Object, Figure 3-13
     */
	private void testTreeStructure003() {
        tbc.log("#testTreeStructure003");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.OSGI_ROOT,
                DmtSession.LOCK_TYPE_SHARED);

            tbc.assertTrue(
                    "Asserts if $/Policy/Java/LocationPermission/Locations is a valid node",
                    session.isNodeUri(PolicyConstants.LOCATIONS_NODE));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This test asserts if $/Policy/Java/DmtPrincipalPermission is a valid node
     *
     * @spec 3.7 Policy Management Object, Figure 3-12
     */
	private void testTreeStructure004() {
        tbc.log("#testTreeStructure004");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.OSGI_ROOT,
                DmtSession.LOCK_TYPE_SHARED);

            tbc.assertTrue(
                    "Asserts if $/Policy/Java/DmtPrincipalPermission is a valid node",
                    session.isNodeUri(PolicyConstants.PRINCIPAL_PERMISSION_NODE));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }


    /**
     * This test asserts if $/Policy/Java/ConditionalPermission is a valid node
     *
     * @spec 3.7 Policy Management Object, Figure 3-12
     */    
	private void testTreeStructure005() {
        tbc.log("#testTreeStructure005");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.OSGI_ROOT,
                DmtSession.LOCK_TYPE_SHARED);

            tbc.assertTrue(
                    "Asserts if $/Policy/Java/ConditionalPermission is a valid node",
                    session.isNodeUri(PolicyConstants.CONDITIONAL_PERMISSION_NODE));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

	
    /**
     * This test asserts if the correct permission is added in the
     * Permission Admin when setting permissions through DMT structure
     *
     * @spec 3.7.2 Location Permission Management Object
     */    
	private void testTreeStructure006() {
        tbc.log("#testTreeStructure006");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.LOCATIONS_NODE, DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(PolicyConstants.TEST_NODE);
            session.setNodeValue(PolicyConstants.TEST_NODE_LOCATION,
                new DmtData(PolicyConstants.TEST_NODE));
            
            PermissionInfo pInfo = new PermissionInfo(AdminPermission.class.getName(), "*", "*");
            
            session.setNodeValue(PolicyConstants.TEST_NODE_PERMISSION, new DmtData(pInfo.getEncoded() + "\n"));
            session.close();
            
            PermissionInfo pi[] = tbc.getPermissionAdmin().getPermissions(PolicyConstants.TEST_NODE);
            
            tbc.assertEquals(
                    "Asserts if the correct permission is added in the permission admin when setting permissions through DMT structure",
                    pInfo.getEncoded(), pi[0].getEncoded());
            
            session = tbc.getDmtAdmin().getSession(PolicyConstants.LOCATIONS_NODE, DmtSession.LOCK_TYPE_ATOMIC);
            session.deleteNode(PolicyConstants.TEST_NODE);
            session.close();

            pi = tbc.getPermissionAdmin().getPermissions(PolicyConstants.TEST_NODE);
            tbc.assertNull("Asserts if permission was removed from permission admin", pi);
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }


    /**
     * This test asserts if the correct permission is added in DMT when
     * setting permissions through permission admin service
     *
     * @spec 3.7.2 Location Permission Management Object
     */
	private void testTreeStructure007() {
        tbc.log("#testTreeStructure007");
        DmtSession session = null;
        try {
            PermissionInfo pInfo = new PermissionInfo(AdminPermission.class.getName(), "*", "*");
            
            tbc.getPermissionAdmin().setPermissions(PolicyConstants.TEST_NODE,
                new PermissionInfo[]{pInfo});
            
            session = tbc.getDmtAdmin().getSession(
                PolicyConstants.LOCATIONS_NODE, DmtSession.LOCK_TYPE_SHARED);
            
            tbc.assertEquals(
                    "Asserts if the correct permission is added in DMT when setting permissions through permission admin service",
                    pInfo.getEncoded() + "\n", session.getNodeValue(
                        PolicyConstants.TEST_NODE_PERMISSION).getString());
            
            session.close();
            
            tbc.getPermissionAdmin().setPermissions(PolicyConstants.TEST_NODE, null);
            
            session = tbc.getDmtAdmin().getSession(
                PolicyConstants.LOCATIONS_NODE, DmtSession.LOCK_TYPE_SHARED);

            tbc.assertTrue("Asserts if permission was removed from DMT",
                !session.isNodeUri(PolicyConstants.TEST_NODE));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }


    /**
     * This test asserts if ConditionPermission is added in conditional
     * permission service when setting the permission in DMT
     *
     * @spec 3.7.4 Conditional Permission Management Object
     */
	private void testTreeStructure008() {
        tbc.log("#testTreeStructure008");
        DmtSession session = null;
        try {
            PermissionInfo pInfo1 = new PermissionInfo(ServicePermission.class.getName(), "org.osgi.service.http.HttpService", "register");
            PermissionInfo pInfo2 = new PermissionInfo(PackagePermission.class.getName(), "org.osgi.*", "IMPORT");
            ConditionInfo cInfo = new ConditionInfo(BundleLocationCondition.class.getName(),
                new String[]{PolicyConstants.PRINCIPAL});
            
            session = tbc.getDmtAdmin().getSession(PolicyConstants.CONDITIONAL_PERMISSION_NODE,
                DmtSession.LOCK_TYPE_ATOMIC);
            
            
            session.createInteriorNode(PolicyConstants.CONDITION_NAME);
            session.setNodeValue(PolicyConstants.CONDITIONAL_PERMISSIONINFO,
                new DmtData(pInfo1.getEncoded() + "\n" + pInfo2.getEncoded() + "\n"));
            session.setNodeValue(PolicyConstants.CONDITIONAL_CONDITIONINFO,
                new DmtData(cInfo.getEncoded() + "\n"));
            session.setNodeValue(PolicyConstants.CONDITION_NAME_NODE,
                    new DmtData(PolicyConstants.CONDITION_NAME));
            session.close();
            
            boolean conditionInserted = false;
            Enumeration en = tbc.getConditionalPermissionAdmin().getConditionalPermissionInfos();

            while (en.hasMoreElements()) {
                ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) en.nextElement();
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
            
            tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE,
                            new String[]{"conditions were inserted in conditional permission service"}),conditionInserted);
            
            session = tbc.getDmtAdmin().getSession(PolicyConstants.CONDITIONAL_PERMISSION_NODE,
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.deleteNode(PolicyConstants.CONDITION_NAME);
            session.close();
            
            conditionInserted = false;
            en = tbc.getConditionalPermissionAdmin().getConditionalPermissionInfos();
            while (en.hasMoreElements()) {
                ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) en.nextElement();
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
            
            tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE,
                            new String[]{"conditions were removed from conditional permission service"}),!conditionInserted);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }


    /**
     * This test asserts if ConditionalPermission is added in DMT when
     * setting the permission through the conditional
     * permission service
     *
     * @spec 3.7.4 Conditional Permission Management Object
     */
	private void testTreeStructure009() {
        tbc.log("#testTreeStructure009");
        DmtSession session = null;
        try {
            PermissionInfo pInfo1 = new PermissionInfo(ServicePermission.class.getName(), 
                "org.osgi.service.http.HttpService", "register");
            PermissionInfo pInfo2 = new PermissionInfo(PackagePermission.class.getName(), 
                "org.osgi.*", "IMPORT");
            ConditionInfo cInfo = new ConditionInfo(BundleLocationCondition.class.getName(),
                new String[]{PolicyConstants.PRINCIPAL});
            
            tbc.getConditionalPermissionAdmin().setConditionalPermissionInfo(
            		PolicyConstants.CONDITION_NAME,
                new ConditionInfo[]{cInfo},
                new PermissionInfo[]{pInfo1, pInfo2});
            
            session = tbc.getDmtAdmin().getSession(PolicyConstants.CONDITIONAL_PERMISSION_NODE,
                DmtSession.LOCK_TYPE_ATOMIC);
            
            tbc.assertTrue("Asserts if ConditionalPermission is added in DMT",
                session.isNodeUri(PolicyConstants.CONDITION_NAME));
            tbc.assertEquals("Asserts ConditionInfo node value", cInfo.getEncoded() + "\n", 
                session.getNodeValue(PolicyConstants.CONDITIONAL_CONDITIONINFO).getString());
            tbc.assertEquals("Asserts PermissionInfo node value", pInfo1.getEncoded()
                + "\n" + pInfo2.getEncoded() + "\n", session.getNodeValue(
                PolicyConstants.CONDITIONAL_PERMISSIONINFO).getString());
            
            session.deleteNode(PolicyConstants.CONDITION_NAME);
            session.close();
            
            boolean conditionInserted = false;
            Enumeration en = tbc.getConditionalPermissionAdmin().getConditionalPermissionInfos();
            
            while (en.hasMoreElements()) {
                ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) en.nextElement();
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
            
            tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE,
                            new String[]{"conditions were removed from conditional permission service"}),!conditionInserted);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }
	
    /**
     * This test asserts if the absence of a Default node is equivalent
     * to having All Permission as the default permission
     *
     * @spec 3.7.3 Dmt Principal Permission Management Object, Table 3-10
     */

    private void testTreeStructure010() {
		tbc.log("#testTreeStructure010");
		DmtSession session = null;
		PermissionInfo info[] = null;
		try {
			info = tbc.getPermissionAdmin().getDefaultPermissions();

			session = tbc.getDmtAdmin().getSession(
					PolicyConstants.LOCATION_PERMISSION_NODE,
					DmtSession.LOCK_TYPE_ATOMIC);

			if (session.isNodeUri(PolicyConstants.DEFAULT_PERMISSION_NODE)) {
				session.deleteNode(PolicyConstants.DEFAULT_PERMISSION_NODE);
			}

			session.close();

			PermissionInfo infos[] = tbc.getPermissionAdmin()
					.getDefaultPermissions();
			
			tbc
					.assertNull(
							"Asserts if null is returned by getDefaultPermissions when there is no default permission set.",
							infos);
			
			tbc.getPermissionAdmin()
			.setDefaultPermissions(null);
			
			tbc.pass("Asserts if this bundle can execute commands, like setDefaultPermissions when there is no default permission set.");

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			tbc.closeSession(session);
			tbc.getPermissionAdmin().setDefaultPermissions(info);
		}
	}	   

}
