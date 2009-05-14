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
import info.dmtree.DmtException;
import info.dmtree.DmtSession;

import org.osgi.test.cases.policy.tbc.PolicyConstants;
import org.osgi.test.cases.policy.tbc.PolicyTestControl;
import org.osgi.test.cases.policy.tbc.TestInterface;
import org.osgi.test.cases.policy.tbc.util.MessagesConstants;


/**
 * @author Andre Assad 
 * 
 * This Test Class Validates the implementation of
 * TreeStructure, according to MEG reference documentation.
 */
public class MetaNode implements TestInterface {
    
    private PolicyTestControl tbc;

    public MetaNode(PolicyTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testMetaNode001();
        testMetaNode002();
        testMetaNode003();
        testMetaNode004();
        testMetaNode005();
        testMetaNode006();
        testMetaNode007();
        testMetaNode008();
        testMetaNode009();
        testMetaNode010();
        testMetaNode011();
        testMetaNode012();
        testMetaNode013();
        testMetaNode014();
    }
    
    /**
     * This test asserts Scope, Type, Cardinality, Get Permission
     * of $/Policy/Java/LocationPermission metanode
     *
     * @spec 3.7.2 Location Permission Management Object, Table 15
     */
    private void testMetaNode001() {
        tbc.log("#testMetaNode001");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.POLICY_JAVA_NODE, DmtSession.LOCK_TYPE_SHARED);
            
            info.dmtree.MetaNode metaNode = session.getMetaNode(PolicyConstants.LOCATION_PERMISSION_NODE);
            
            tbc.assertEquals(
                    "Asserts if $/Policy/Java/LocationPermission is a permanent node",
                    info.dmtree.MetaNode.PERMANENT, metaNode.getScope());
            
            tbc.assertEquals(
                "Asserts format of $/Policy/Java/LocationPermission node",
                DmtData.FORMAT_NODE, metaNode.getFormat());
            
            tbc.assertTrue(
                "Asserts cardinality of $/Policy/Java/LocationPermission node",
                !metaNode.isZeroOccurrenceAllowed()
                    && metaNode.getMaxOccurrence() == 1);
                       
            tbc.assertTrue(
                "Asserts if $/Policy/Java/LocationPermission node can get",
                metaNode.can(info.dmtree.MetaNode.CMD_GET));
            
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }


    /**
     * This test asserts Scope, Type, Cardinality, Add Permission, Get Permission,
     * Replace Permission, Delete Permission of
     * $/Policy/Java/LocationPermission/Default metanode
     *
     * @spec 3.7.2 Location Permission Management Object, Table 15
     */
    private void testMetaNode002() {
        tbc.log("#testMetaNode002");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.LOCATION_PERMISSION_NODE,
                DmtSession.LOCK_TYPE_ATOMIC);

            if (!session.isNodeUri(PolicyConstants.DEFAULT_PERMISSION_NODE)) {
                session.createLeafNode(PolicyConstants.DEFAULT_PERMISSION_NODE, new DmtData(""));
            }

            info.dmtree.MetaNode metaNode = session.getMetaNode(PolicyConstants.DEFAULT_PERMISSION_NODE);

            tbc.assertEquals(
                    "Asserts if $/Policy/Java/LocationPermission/Default node is dynamic",
                    info.dmtree.MetaNode.DYNAMIC, metaNode.getScope());

            tbc.assertEquals(
                    "Asserts format of $/Policy/Java/LocationPermission/Default node",
                    DmtData.FORMAT_STRING, metaNode.getFormat());

            tbc.assertTrue(
                    "Asserts cardinality $/Policy/Java/LocationPermission/Default node",
                    metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);

            tbc.assertTrue(
                    "Asserts if $/Policy/Java/LocationPermission/Default node can add",
                    metaNode.can(info.dmtree.MetaNode.CMD_ADD));

            tbc.assertTrue(
                    "Asserts if $/Policy/Java/LocationPermission/Default node can get",
                    metaNode.can(info.dmtree.MetaNode.CMD_GET));

            tbc.assertTrue(
                    "Asserts if $/Policy/Java/LocationPermission/Default node can replace",
                    metaNode.can(info.dmtree.MetaNode.CMD_REPLACE));

            tbc.assertTrue(
                    "Asserts if $/Policy/Java/LocationPermission/Default node can delete",
                    metaNode.can(info.dmtree.MetaNode.CMD_DELETE));

        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                    e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }


    /**
     * This test asserts Scope, Type, Cardinality, Add Permission, Get
     * Permission and Delete Permission 
     * of $/Policy/Java/LocationPermission/<location>metanode
     * 
     * @spec 3.7.2 Location Permission Management Object, Table 15
     */
    private void testMetaNode003() {
        tbc.log("#testMetaNode003");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.LOCATIONS_NODE, DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(PolicyConstants.TEST_NODE);
            
            info.dmtree.MetaNode metaNode = session
                .getMetaNode(PolicyConstants.TEST_NODE);
            
            tbc.assertEquals(
                    "Asserts if $/Policy/Java/LocationPermission/<location> node is dynamic",
                    info.dmtree.MetaNode.DYNAMIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts format of $/Policy/Java/LocationPermission/<location> node",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts cardinality of $/Policy/Java/LocationPermission/<location> node",
                    metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/LocationPermission/<location> node can add",
                    metaNode.can(info.dmtree.MetaNode.CMD_ADD));
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/LocationPermission/<location> node can get",
                    metaNode.can(info.dmtree.MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/LocationPermission/<location> node can delete",
                    metaNode.can(info.dmtree.MetaNode.CMD_DELETE));
            
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[]{PolicyConstants.TEST_NODE});
        }
    }


    /**
     * This test asserts Scope, Type, Cardinality, Get Permission 
     * and Replace Permission of
     * $/Policy/Java/LocationPermission/ <location>/PermissionInfo metanode
     * 
     * @spec 3.7.2 Location Permission Management Object, Table 15
     */
    private void testMetaNode004() {
        tbc.log("#testMetaNode004");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.LOCATIONS_NODE, DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(PolicyConstants.TEST_NODE);
            
            info.dmtree.MetaNode metaNode = session.getMetaNode(PolicyConstants.TEST_NODE_PERMISSION);
            
            tbc.assertEquals(
                    "Asserts if $/Policy/Java/LocationPermission/<location>/PermissionInfo node id automatic",
                    info.dmtree.MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts format of $/Policy/Java/LocationPermission/<location>/PermissionInfo node",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts cardinality of $/Policy/Java/LocationPermission/<location>/PermissionInfo node",
                    !metaNode.isZeroOccurrenceAllowed()&& metaNode.getMaxOccurrence() == 1);
                        
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/LocationPermission/<location>/PermissionInfo node can get",
                    metaNode.can(info.dmtree.MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/LocationPermission/<location>/PermissionInfo node can replace",
                    metaNode.can(info.dmtree.MetaNode.CMD_REPLACE));
            
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[]{PolicyConstants.TEST_NODE});
        }
    }


    /**
     * This test asserts Scope, Type, Cardinality, Get Permission
     * and Replace Permission of $/Policy/Java/LocationPermission/<location>/Location
     * metanode.
     * 
     * @spec 3.7.2 Location Permission Management Object, Table 15
     */
    private void testMetaNode005() {
        tbc.log("#testMetaNode005");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.LOCATIONS_NODE, DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(PolicyConstants.TEST_NODE);
            info.dmtree.MetaNode metaNode = session.getMetaNode(PolicyConstants.TEST_NODE_LOCATION);
            
            tbc.assertEquals(
                    "Asserts if $/Policy/Java/LocationPermission/<location>/Location node is automatic",
                    info.dmtree.MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts format of $/Policy/Java/LocationPermission/<location>/Location node",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts cardinality of $/Policy/Java/LocationPermission/<location>/Location node",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
                       
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/LocationPermission/<location>/Location node can get",
                    metaNode.can(info.dmtree.MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/LocationPermission/<location>/Location node can replace",
                    metaNode.can(info.dmtree.MetaNode.CMD_REPLACE));
            
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[]{PolicyConstants.TEST_NODE});
        }
    }

    /**
     * This test asserts Scope, Type, Cardinality and Get
     * Permission of $/Policy/Java/DmtPrincipalPermission metanode
     * 
     * @spec 3.7.3 Dmt Principal Permission Management Object, Table 16
     */
    private void testMetaNode006() {
        tbc.log("#testMetaNode006");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.POLICY_JAVA_NODE, DmtSession.LOCK_TYPE_SHARED);
            
            info.dmtree.MetaNode metaNode = session.getMetaNode(PolicyConstants.PRINCIPAL_PERMISSION_NODE);
            
            tbc.assertEquals(
                    "Asserts if $/Policy/Java/DmtPrincipalPermission node is permanent",
                    info.dmtree.MetaNode.PERMANENT, metaNode.getScope());
            
            tbc.assertEquals(
                "Asserts format of $/Policy/Java/DmtPrincipalPermission node",
                DmtData.FORMAT_NODE, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts cardinality of $/Policy/Java/DmtPrincipalPermission node",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                "Asserts if $/Policy/Java/DmtPrincipalPermission node can get",
                metaNode.can(info.dmtree.MetaNode.CMD_GET));
            
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This test asserts Scope, Type, Cardinality, Add Permission, Get Permission
     * and Delete Permission of $/Policy/Java/DmtPrincipalPermission/<principal> metanode
     *
     * @spec 3.7.3 Dmt Principal Permission Management Object, Table 16
     */
    private void testMetaNode007() {
        tbc.log("#testMetaNode007");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.PRINCIPAL_PERMISSION_NODE,
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(PolicyConstants.PRINCIPAL);
            info.dmtree.MetaNode metaNode = session.getMetaNode(PolicyConstants.PRINCIPAL);
            
            tbc.assertEquals(
                    "Asserts if $/Policy/Java/DmtPrincipalPermission/<principal> node is dynamic",
                    info.dmtree.MetaNode.DYNAMIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts format of $/Policy/Java/DmtPrincipalPermission/<principal> node",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts cardinality of $/Policy/Java/DmtPrincipalPermission/<principal> node",
                    metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/DmtPrincipalPermission/<principal> node can add",
                    metaNode.can(info.dmtree.MetaNode.CMD_ADD));
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/DmtPrincipalPermission/<principal> node can get",
                    metaNode.can(info.dmtree.MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/DmtPrincipalPermission/<principal> node can delete",
                    metaNode.can(info.dmtree.MetaNode.CMD_DELETE));
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[]{PolicyConstants.PRINCIPAL});
        }
    }

    /**
     * This test asserts Scope, Type, Cardinality, Get Permission and Replace Permission
     * of $/Policy/Java/DmtPrincipalPermission/ <principal>/Principal metanode
     * 
     * @spec 3.7.3 Dmt Principal Permission Management Object, Table 16
     */
    private void testMetaNode008() {
        tbc.log("#testMetaNode008");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.PRINCIPAL_PERMISSION_NODE,
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(PolicyConstants.PRINCIPAL);
            info.dmtree.MetaNode metaNode = session.getMetaNode(PolicyConstants.PRINCIPAL_LOCATION);
            
            tbc.assertEquals(
                    "Asserts if $/Policy/Java/DmtPrincipalPermission/<principal>/Principal node is automatic",
                    info.dmtree.MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts format of $/Policy/Java/DmtPrincipalPermission/<principal>/Principal node",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts cardinality of $/Policy/Java/DmtPrincipalPermission/<principal>/Principal node",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/DmtPrincipalPermission/<principal>/Principal node can get",
                    metaNode.can(info.dmtree.MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/DmtPrincipalPermission/<principal>/Principal node can replace",
                    metaNode.can(info.dmtree.MetaNode.CMD_REPLACE));
            
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[]{PolicyConstants.PRINCIPAL});
        }
    }

    /**
     * This test asserts Scope, Type, Cardinality, Get Permission and Replace Permission
     * of $/Policy/Java/DmtPrincipalPermission/ <principal>/PermissionInfo metanode
     * 
     * @spec 3.7.3 Dmt Principal Permission Management Object, Table 16
     */
    private void testMetaNode009() {
        tbc.log("#testMetaNode009");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.PRINCIPAL_PERMISSION_NODE,
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(PolicyConstants.PRINCIPAL);
            
            info.dmtree.MetaNode metaNode = session.getMetaNode(PolicyConstants.PRINCIPAL_PERMISSION);
            
            tbc.assertEquals(
                    "Asserts if $/Policy/Java/DmtPrincipalPermission/<principal>/PermissionInfo is automatic",
                    info.dmtree.MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts format of $/Policy/Java/DmtPrincipalPermission/<principal>/PermissionInfo node",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts cardinality of $/Policy/Java/DmtPrincipalPermission/<principal>/PermissionInfo node",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/DmtPrincipalPermission/<principal>/PermissionInfo node can get",
                    metaNode.can(info.dmtree.MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/DmtPrincipalPermission/<principal>/PermissionInfo node can replace",
                    metaNode.can(info.dmtree.MetaNode.CMD_REPLACE));
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[]{PolicyConstants.PRINCIPAL});
        }
    }

    /**
     * This test asserts Scope, Type, Cardinality and Get Permission
     * of $/Policy/Java/ConditionalPermission metanode
     * 
     * @spec 3.7.4 Conditional Permission Management Object, Table 17
     */
    private void testMetaNode010() {
        tbc.log("#testMetaNode010");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.POLICY_JAVA_NODE,
                    DmtSession.LOCK_TYPE_SHARED);

            info.dmtree.MetaNode metaNode = session.getMetaNode(PolicyConstants.CONDITIONAL_PERMISSION_NODE);

            tbc.assertEquals(
                    "Asserts if $/Policy/Java/ConditionalPermission node is permanent",
                    info.dmtree.MetaNode.PERMANENT, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts format of $/Policy/Java/ConditionalPermission node",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts cardinality of $/Policy/Java/ConditionalPermission node",
                    metaNode.isZeroOccurrenceAllowed()&& metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/ConditionalPermission node can get",
                    metaNode.can(info.dmtree.MetaNode.CMD_GET));
            
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }


    /**
     * This test asserts Scope, Type, Cardinality, Add Permission, Get Permission
     * and Delete Permission of $/Policy/Java/ConditionalPermission/<hash> metanode
     *
     * @spec 3.7.4 Conditional Permission Management Object, Table 17
     */
    private void testMetaNode011() {
        tbc.log("#testMetaNode011");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.POLICY_JAVA_NODE, DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(PolicyConstants.CONDITIONAL_PERMISSION_NODE
                    + "/" + PolicyConstants.CONDITION_NAME);
            
            info.dmtree.MetaNode metaNode = session
                .getMetaNode(PolicyConstants.CONDITIONAL_PERMISSION_NODE + "/"+ PolicyConstants.CONDITION_NAME);
            
            tbc.assertEquals(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash> node is dynamic",
                    info.dmtree.MetaNode.DYNAMIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts format of $/Policy/Java/ConditionalPermission/<hash> node",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts cardinality of $/Policy/Java/ConditionalPermission/<hash> node",
                    metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash> node can add",
                    metaNode.can(info.dmtree.MetaNode.CMD_ADD));
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash> node can get",
                    metaNode.can(info.dmtree.MetaNode.CMD_GET));
                      
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash> node can delete",
                    metaNode.can(info.dmtree.MetaNode.CMD_DELETE));
            
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session,
                new String[]{PolicyConstants.CONDITIONAL_PERMISSION_NODE + "/"
                    + PolicyConstants.CONDITION_NAME});
        }
    }

    /**
     * This test asserts Scope, Type, Cardinality, Get Permission and Replace Permission
     * of $/Policy/Java/ConditionalPermission/&lt;hash&gt;/ConditionInfo metanode
     * 
     * @spec 3.7.4 Conditional Permission Management Object, Table 17
     */
    private void testMetaNode012() {
        tbc.log("#testMetaNode012");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(PolicyConstants.POLICY_JAVA_NODE, DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(PolicyConstants.CONDITIONAL_PERMISSION_NODE
                    + "/" + PolicyConstants.CONDITION_NAME);
            
            info.dmtree.MetaNode metaNode = session
                .getMetaNode(PolicyConstants.CONDITIONAL_PERMISSION_NODE + "/"
                    + PolicyConstants.CONDITIONAL_CONDITIONINFO);
            
            tbc.assertEquals(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo is automatic",
                    info.dmtree.MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts format of $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo node",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts cardinality of $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo node",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo node can get",
                    metaNode.can(info.dmtree.MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash>/ConditionInfo node can replace",
                    metaNode.can(info.dmtree.MetaNode.CMD_REPLACE));
            
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session,
                new String[]{PolicyConstants.CONDITIONAL_PERMISSION_NODE + "/"
                    + PolicyConstants.CONDITION_NAME});
        }
    }

    /**
     * This test asserts Scope, Type, Cardinality, Get Permission and Replace Permission
     * of $/Policy/Java/ConditionalPermission/&lt;hash&gt;/PermissionInfo metanode
     * 
     * @spec 3.7.4 Conditional Permission Management Object, Table 17
     */
    private void testMetaNode013() {
        tbc.log("#testMetaNode013");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(
                PolicyConstants.POLICY_JAVA_NODE, DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(PolicyConstants.CONDITIONAL_PERMISSION_NODE
                    + "/" + PolicyConstants.CONDITION_NAME);
            
            info.dmtree.MetaNode metaNode = session
                .getMetaNode(PolicyConstants.CONDITIONAL_PERMISSION_NODE + "/"
                    + PolicyConstants.CONDITIONAL_PERMISSIONINFO);
            
            tbc.assertEquals(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo node is automatic",
                    info.dmtree.MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts format $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo node",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts cardinality of $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo node",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo node can get",
                    metaNode.can(info.dmtree.MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash>/PermissionInfo node can replace",
                    metaNode.can(info.dmtree.MetaNode.CMD_REPLACE));
            
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session,
                new String[]{PolicyConstants.CONDITIONAL_PERMISSION_NODE + "/"
                    + PolicyConstants.CONDITION_NAME});
        }
    }
    
    /**
     * This test asserts Scope, Type, Cardinality, Get Permission and Replace Permission
     * of $/Policy/Java/ConditionalPermission/&lt;hash&gt;/Name metanode
     * 
     * @spec 3.7.4 Conditional Permission Management Object, Table 17
     */
    private void testMetaNode014() {
        tbc.log("#testMetaNode014");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(
                PolicyConstants.POLICY_JAVA_NODE, DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(PolicyConstants.CONDITIONAL_PERMISSION_NODE
                    + "/" + PolicyConstants.CONDITION_NAME);
            
            info.dmtree.MetaNode metaNode = session
                .getMetaNode(PolicyConstants.CONDITIONAL_PERMISSION_NODE + "/"
                    + PolicyConstants.CONDITION_NAME_NODE);
            
            tbc.assertEquals(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash>/Name node is automatic",
                    info.dmtree.MetaNode.AUTOMATIC, metaNode.getScope());
            
            tbc.assertEquals(
                    "Asserts format $/Policy/Java/ConditionalPermission/<hash>/Name node",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            
            tbc.assertTrue(
                    "Asserts cardinality of $/Policy/Java/ConditionalPermission/<hash>/Name node",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash>/Name node can get",
                    metaNode.can(info.dmtree.MetaNode.CMD_GET));
            
            tbc.assertTrue(
                    "Asserts if $/Policy/Java/ConditionalPermission/<hash>/Name node can replace",
                    metaNode.can(info.dmtree.MetaNode.CMD_REPLACE));
            
        } catch (Exception e) {
          tbc.fail(MessagesConstants.getMessage(
              MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{
                  e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session,
                new String[]{PolicyConstants.CONDITIONAL_PERMISSION_NODE + "/"
                    + PolicyConstants.CONDITION_NAME});
        }
    }    
}
