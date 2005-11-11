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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
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
    }

    /**
     * This method asserts if $/Application is a valid node and asserts Type,
     * Cardinality, Add Permission, Get Permission, Replace Permission, Delete
     * Permission and Execute Permission according to Table 6
     * 
     * @spec 3.5.1 Applications Descriptors
     */
    private void testTreeStructure001() {
        tbc.log("#testTreeStructure001");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
            
            tbc.assertTrue("Asserts if $/Application is a valid node",
                session.isNodeUri(ApplicationConstants.OSGI_APPLICATION));
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION);
            
            tbc.assertEquals("Asserts $/Application metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals("Asserts $/Application metanode format",
                DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc.assertTrue("Asserts $/Application metanode cardinality",
                !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue("Asserts $/Application metanode GET", metaNode.can(MetaNode.CMD_GET));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if $/Application/<app_id>is a valid node and asserts
     * Type, Cardinality, Add Permission, Get Permission, Replace Permission,
     * Delete Permission and Execute Permission according to Table 6
     * 
     * @spec 3.5.1 Applications Descriptors
     */
    private void testTreeStructure002() {
        tbc.log("#testTreeStructure002");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            
            tbc.assertTrue("Asserts if $/Application/<app_id> is a valid node",
                session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID));
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID);
            
            tbc.assertEquals("Asserts $/Application/<app_id> metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals("Asserts $/Application/<app_id> metanode format",
                DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc.assertTrue(
                "Asserts $/Application/<app_id> metanode cardinality", 
                metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
            tbc.assertTrue("Asserts $/Application/<app_id> metanode GET",
                metaNode.can(MetaNode.CMD_GET));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if $/Application/<app_id>/Name is a valid node and
     * asserts Type, Cardinality, Add Permission, Get Permission, Replace
     * Permission, Delete Permission and Execute Permission according to Table
     * 6. Then, asserts the value of the node.
     * 
     * @spec 3.5.1 Applications Descriptors
     */
    private void testTreeStructure003() {
        tbc.log("#testTreeStructure003");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
            
            tbc.assertTrue(
                    "Asserts if $/Application/<app_id>/Name is a valid node",
                    session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_NAME));
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_NAME);
            
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Name metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Name metanode format",
                DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/Name metanode cardinality",
                !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue("Asserts $/Application/<app_id>/Name metanode GET",
                metaNode.can(MetaNode.CMD_GET));
            tbc.assertEquals(
                    "Asserting the value of $/Application/<app_id>/Name",
                    ApplicationConstants.APPLICATION_NAME,
                    session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_NAME));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/IconURI is a valid node
     * and asserts Type, Cardinality, Add Permission, Get Permission, Replace
     * Permission, Delete Permission and Execute Permission according to Table
     * 6. Then, asserts the value of the node.
     * 
     * @spec 3.5.1 Applications Descriptors
     */
    private void testTreeStructure004() {
        tbc.log("#testTreeStructure004");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            
            tbc.assertTrue(
                    "Asserts if $/Application/<app_id>/IconURI is a valid node",
                    session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_ICONURI));
            
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_ICONURI);
            
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/IconURI metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/IconURI metanode format",
                DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/IconURI metanode cardinality",
                !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/IconURI metanode GET", metaNode.can(MetaNode.CMD_GET));
            tbc.assertEquals(
                    "Asserting the value of $/Application/<app_id>/IconURI", "/TestIcon.gif",
                    session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_ICONURI));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/Vendor is a valid node and
     * asserts Type, Cardinality, Add Permission, Get Permission, Replace
     * Permission, Delete Permission and Execute Permission according to Table
     * 6. Then, asserts the value of the node.
     * 
     * @spec 3.5.1 Applications Descriptors
     */
    private void testTreeStructure005() {
        tbc.log("#testTreeStructure005");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
            
            tbc.assertTrue(
                    "Asserts if $/Application/<app_id>/Vendor is a valid node",
                    session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_VENDOR));
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_VENDOR);
            
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Vendor metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Vendor metanode format",
                DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/Vendor metanode cardinality",
                metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/Vendor metanode GET", metaNode.can(MetaNode.CMD_GET));
            tbc.assertEquals(
                    "Asserting the value of $/Application/<app_id>/Vendor", "Cesar",
                    session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_VENDOR));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/Version is a valid node
     * and asserts Type, Cardinality, Add Permission, Get Permission, Replace
     * Permission, Delete Permission and Execute Permission according to Table
     * 6. Then, asserts the value of the node.
     * 
     * @spec 3.5.1 Applications Descriptors
     */
    private void testTreeStructure006() {
        tbc.log("#testTreeStructure006");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            tbc
                .assertTrue(
                    "Asserts if $/Application/<app_id>/Version is a valid node",
                    session
                        .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_VERSION));
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_VERSION);
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Version metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Version metanode format",
                DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/Version metanode cardinality",
                metaNode.isZeroOccurrenceAllowed()
                    && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/Version metanode GET", metaNode
                    .can(MetaNode.CMD_GET));
            tbc
                .assertEquals(
                    "Asserting the value of $/Application/<app_id>/Version",
                    ApplicationConstants.OSGI_APPLICATION_VERSION,
                    session
                        .getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_VERSION));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/Locked is a valid node and
     * asserts Type, Cardinality, Add Permission, Get Permission, Replace
     * Permission, Delete Permission and Execute Permission according to Table
     * 6. Then, asserts the value of the node.
     * 
     * @spec 3.5.1 Applications Descriptors
     */
    private void testTreeStructure007() {
        tbc.log("#testTreeStructure007");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            tbc
                .assertTrue(
                    "Asserts if $/Application/<app_id>/Locked is a valid node",
                    session
                        .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_LOCKED));
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_LOCKED);
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Locked metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Locked metanode format",
                DmtData.FORMAT_BOOLEAN, metaNode.getFormat());
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/Locked metanode cardinality",
                !metaNode.isZeroOccurrenceAllowed()
                    && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/Locked metanode GET", metaNode
                    .can(MetaNode.CMD_GET));
            tbc.assertTrue(
                "Asserting the value of $/Application/<app_id>/Locked",
                !session.getNodeValue(
                    ApplicationConstants.OSGI_APPLICATION_APPID_LOCKED)
                    .getBoolean());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/ContainerID is a valid
     * node and asserts Type, Cardinality, Add Permission, Get Permission,
     * Replace Permission, Delete Permission and Execute Permission according to
     * Table 6. Then, asserts the value of the node.
     * 
     * @spec 3.5.1 Applications Descriptors
     */
    private void testTreeStructure008() {
        tbc.log("#testTreeStructure008");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            tbc
                .assertTrue(
                    "Asserts if $/Application/<app_id>/ContainerID is a valid node",
                    session
                        .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_CONTAINERID));
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_CONTAINERID);
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/ContainerID metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/ContainerID metanode format",
                DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/ContainerID metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/ContainerID metanode GET",
                metaNode.can(MetaNode.CMD_GET));
            tbc
                .assertEquals(
                    "Asserting the value of $/Application/<app_id>/ContainerID",
                    ApplicationConstants.OSGI_APPLICATION_CONTAINER,
                    session
                        .getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_CONTAINERID));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }
    
    /**
     * This test case starts a scheduled application via ApplicationDescriptor
     * and verifies if the relative nodes were created in DMT.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure009() {
        tbc.log("#testTreeStructure009");
        DmtSession session = null;
        ScheduledApplication sa = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            
            Map hash = new HashMap();
            hash.put("name", "value");
            sa = tbc.getAppDescriptor().schedule(hash,ApplicationConstants.TIMER_EVENT,
                ApplicationConstants.EVENT_FILTER, true);
            
            String[] nodes = session.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);
            tbc.assertNotNull("Scheduled subtree was created", nodes);
            
            String[] args = session.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS);
            tbc.assertNotNull("Scheduled arguments subtree was created", args);
            
            String name = session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS+nodes[0]+"/Name").getString();
            String value = session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS+nodes[0]+"/Value").getString();
            
            tbc.assertEquals("Asserts if parameter name  passed as an argument to the scheduled application was created.", "name", name);
            tbc.assertEquals("Asserts if parameter value passed as an argument to the scheduled application was created.", "value", value);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            sa.remove();
            tbc.closeSession(session);
        } 
    }

    /**
     * This method asserts if $/Application/ <app_id>/Ext is a valid node and
     * asserts Type, Cardinality, Add Permission, Get Permission, Replace
     * Permission, Delete Permission and Execute Permission according to Table
     * 6.
     * 
     * @spec 3.5.1 Applications Descriptors
     */
    private void testTreeStructure010() {
        tbc.log("#testTreeStructure010");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            tbc
                .assertTrue(
                    "Asserts if $/Application/<app_id>/Ext is a valid node",
                    session
                        .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_EXT));
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_EXT);
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Ext metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Ext metanode format",
                DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/Ext metanode cardinality",
                !metaNode.isZeroOccurrenceAllowed()
                    && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue("Asserts $/Application/<app_id>/Ext metanode GET",
                metaNode.can(MetaNode.CMD_GET));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/Instances is a valid node
     * and asserts Type, Cardinality, Add Permission, Get Permission, Replace
     * Permission, Delete Permission and Execute Permission according to Table
     * 6.
     * 
     * @spec 3.5.1 Applications Descriptors
     */
    private void testTreeStructure011() {
        tbc.log("#testTreeStructure011");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            tbc
                .assertTrue(
                    "Asserts if $/Application/<app_id>/Instances is a valid node",
                    session
                        .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES));
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES);
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Instances metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Instances metanode format",
                DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Instances metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/Instances metanode GET",
                metaNode.can(MetaNode.CMD_GET));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/Operations is a valid node
     * and asserts Type, Cardinality, Add Permission, Get Permission, Replace
     * Permission, Delete Permission and Execute Permission according to Table
     * 6.
     * 
     * @spec 3.5.1 Applications Descriptors
     */
    private void testTreeStructure012() {
        tbc.log("#testTreeStructure012");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            tbc
                .assertTrue(
                    "Asserts if $/Application/<app_id>/Operations is a valid node",
                    session
                        .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS));
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS);
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Operations metanode scope",
                MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Operations metanode format",
                DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Operations metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/Operations metanode GET",
                metaNode.can(MetaNode.CMD_GET));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/Schedules is a valid node
     * and asserts Type, Cardinality, Add Permission, Get Permission, Replace
     * Permission, Delete Permission and Execute Permission according to Table
     * 6.
     * 
     * @spec 3.5.1 Applications Descriptors
     */
    private void testTreeStructure013() {
        tbc.log("#testTreeStructure013");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            tbc
                .assertTrue(
                    "Asserts if $/Application/<app_id>/Schedules is a valid node",
                    session
                        .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES));
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES);
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Schedules metanode scope",
                MetaNode.DYNAMIC, metaNode.getScope());
            tbc.assertEquals(
                "Asserts $/Application/<app_id>/Schedules metanode format",
                DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Schedules metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/Schedules metanode GET",
                metaNode.can(MetaNode.CMD_GET));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/Operations/Lock is a valid
     * node and asserts Type, Cardinality, Add Permission, Get Permission,
     * Replace Permission, Delete Permission and Execute Permission according to
     * Table 8.
     * 
     * @spec 3.5.4 Launching new application instaces
     */
    private void testTreeStructure014() {
        tbc.log("#testTreeStructure014");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            tbc
                .assertTrue(
                    "Asserts if $/Application/<app_id>/Operations/Lock is a valid node",
                    session
                        .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LOCK));
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LOCK);
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Lock metanode scope",
                    MetaNode.PERMANENT, metaNode.getScope());
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Lock metanode format",
                    DmtData.FORMAT_NULL, metaNode.getFormat());
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Lock metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                "Asserts $/Application/<app_id>/Operations/Lock metanode GET",
                metaNode.can(MetaNode.CMD_GET));
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Lock metanode EXECUTE",
                    metaNode.can(MetaNode.CMD_EXECUTE));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if executing the $/Application/
     * <app_id>/Operations/Lock node, it really locks the ApplicationDescriptor.
     * 
     * @spec 3.5.4 Launching new application instaces
     */
    private void testTreeStructure015() {
        tbc.log("#testTreeStructure015");
        DmtSession session = null;
        ApplicationHandle handle = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            tbc.getAppDescriptor().unlock();
            session.execute(
                ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LOCK,
                null);
            handle = tbc.getAppDescriptor().launch(null);
            tbc
                .assertNull(
                    "Asserting if null is returned, and the execute method has really worked.",
                    handle);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
            tbc.cleanUp(handle);
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/Operations/Unlock is a
     * valid node and asserts Type, Cardinality, Add Permission, Get Permission,
     * Replace Permission, Delete Permission and Execute Permission according to
     * Table 8.
     * 
     * @spec 3.5.4 Launching new application instaces
     */
    private void testTreeStructure016() {
        tbc.log("#testTreeStructure016");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            tbc
                .assertTrue(
                    "Asserts if $/Application/<app_id>/Operations/Unlock is a valid node",
                    session
                        .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_UNLOCK));
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_UNLOCK);
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Unlock metanode scope",
                    MetaNode.PERMANENT, metaNode.getScope());
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Unlock metanode format",
                    DmtData.FORMAT_NULL, metaNode.getFormat());
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Unlock metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Unlock metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Unlock metanode EXECUTE",
                    metaNode.can(MetaNode.CMD_EXECUTE));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if executing the $/Application/
     * <app_id>/Operations/Unlock node, it really unlocks the
     * ApplicationDescriptor.
     * 
     * @spec 3.5.4 Launching new application instaces
     */
    private void testTreeStructure017() {
        tbc.log("#testTreeStructure017");
        DmtSession session = null;
        ApplicationHandle handle = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            tbc.getAppDescriptor().lock();
            session.execute(
                ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_UNLOCK,
                null);
            handle = tbc.getAppDescriptor().launch(null);
            tbc
                .assertNotNull(
                    "Asserting if a non-null value is returned, and the execute method has really worked.",
                    handle);
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
            tbc.cleanUp(handle);
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/Operations/Launch is a
     * valid node and asserts Type, Cardinality, Add Permission, Get Permission,
     * Replace Permission, Delete Permission and Execute Permission according to
     * Table 8.
     * 
     * @spec 3.5.4 Launching new application instaces
     */
    private void testTreeStructure018() {
        tbc.log("#testTreeStructure018");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            tbc
                .assertTrue(
                    "Asserts if $/Application/<app_id>/Operations/Launch is a valid node",
                    session
                        .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH));
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH);
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch metanode scope",
                    MetaNode.PERMANENT, metaNode.getScope());
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch metanode format",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch metanode GET",
                    metaNode.can(MetaNode.CMD_GET));

        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
        }
    }

    /**
     * This method asserts if $/Application/<app_id>/Operations/Launch/<launch_id>
     * is a valid node and asserts Type, Cardinality, Add Permission, Get
     * Permission, Replace Permission, Delete Permission and Execute Permission
     * according to Table 8.
     * 
     * @spec 3.5.4 Launching new application instaces
     */
    private void testTreeStructure019() {
        tbc.log("#testTreeStructure019");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);
            
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);
            
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id> metanode scope",
                    MetaNode.DYNAMIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id> metanode format",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id> metanode cardinality",
                    metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id> metanode ADD",
                    metaNode.can(MetaNode.CMD_ADD));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id> metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id> metanode DELETE",
                    metaNode.can(MetaNode.CMD_DELETE));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id> metanode EXECUTE",
                    metaNode.can(MetaNode.CMD_EXECUTE));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session,
                    new String[]{ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID});
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/Operations/Launch/
     * <launch_id>/Arguments/<arg_id>is a valid node and asserts Type,
     * Cardinality, Add Permission, Get Permission, Replace Permission, Delete
     * Permission and Execute Permission according to Table 7.
     * 
     * @spec 3.5.3 Application Arguments
     */
    private void testTreeStructure020() {
        tbc.log("#testTreeStructure020");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);
            
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID);
            
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id> metanode scope",
                    MetaNode.DYNAMIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id> metanode format",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc .assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id> metanode cardinality",
                    metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id> metanode ADD",
                    metaNode.can(MetaNode.CMD_ADD));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id> metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id> metanode DELETE",
                    metaNode.can(MetaNode.CMD_DELETE));

        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session,
                    new String[]{ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID});
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/Operations/Launch/
     * <launch_id>/Arguments/ <arg_id>/Name is a valid node and asserts Type,
     * Cardinality, Add Permission, Get Permission, Replace Permission, Delete
     * Permission and Execute Permission according to Table 7.
     * 
     * @spec 3.5.3 Application Arguments
     */
    private void testTreeStructure021() {
        tbc.log("#testTreeStructure021");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);
            
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID_NAME);
            
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id>/Name metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id>/Name metanode format",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id>/Name metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id>/Name metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id>/Name metanode REPLACE",
                    metaNode.can(MetaNode.CMD_REPLACE));

        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session,
                    new String[]{ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID});
        }
    }

    /**
     * This method asserts if $/Application/ <app_id>/Operations/Launch/
     * <launch_id>/Arguments/ <arg_id>/Value is a valid node and asserts Type,
     * Cardinality, Add Permission, Get Permission, Replace Permission, Delete
     * Permission and Execute Permission according to Table 7.
     * 
     * @spec 3.5.3 Application Arguments
     */
    private void testTreeStructure022() {
        tbc.log("#testTreeStructure022");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);
            
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID_VALUE);
            
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id>/Value metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id>/Value metanode format",
                    (DmtData.FORMAT_BINARY | DmtData.FORMAT_STRING
                        | DmtData.FORMAT_INTEGER | DmtData.FORMAT_FLOAT
                        | DmtData.FORMAT_BOOLEAN | DmtData.FORMAT_NULL),
                    metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id>/Value metanode cardinality",
                    metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id>/Value metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments/<arg_id>/Value metanode REPLACE",
                    metaNode.can(MetaNode.CMD_REPLACE));

        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session,
                    new String[]{ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID});
        }
    }

    /**
     * This method asserts if executing $/Application/<app_id>/Operations/Launch/<launch_id>,
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
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
            
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);
            session.execute(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID,null);
            
            handle = tbc.getAppHandle();
            
            tbc.assertNotNull(
                    "Asserting that exist a ApplicationHandle in service registry.",handle);
            tbc.assertEquals(
                    "Asserting if the Result/InstanceID node is updated with the instance identifier of the newly created application instance.",
                    handle.getInstanceId(),
                    session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_INSTANCEID).getString());
            tbc.assertEquals(
                    "Asserting if the Result/Status node is updated to OK",
                    "OK",session.getNodeValue(
                            ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_STATUS).getString());
            tbc.assertEquals(
                    "Asserting if the Result/Message node is set to an empty string","",
                    session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_MESSAGE).getString());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session,
                    new String[]{ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID});
            tbc.cleanUp(handle);
        }
    }

    /**
     * This method asserts if $/Application/<app_id>/Operations/Launch/
     * <launch_id>/Arguments is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 7.
     * 
     * @spec 3.5.3 Application Arguments
     */
    private void testTreeStructure024() {
        tbc.log("#testTreeStructure024");
        DmtSession session = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);

            tbc.assertTrue(
                "Asserts if $/Application/<app_id>/Operations/Launch/Arguments is a valid node",
                session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS));

            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS);
            
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments metanode format",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Arguments metanode GET",
                    metaNode.can(MetaNode.CMD_GET));

        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc
                .cleanUp(
                    session,
                    new String[]{ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID});
        }
    }

    /**
     * This method asserts if a node is created with the same
     * name of the service.pid of the newly instance. Then,
     * it asserts if $/Application/<app_id>/Instances/<instance_id>
     * is a valid node and asserts Type, Cardinality, Add Permission, Get
     * Permission, Replace Permission, Delete Permission and Execute Permission
     * according to Table 6. At last, it asserts if the Result/InstanceID node is updated
     * with the instance identifier of the newly created application instance,
     * if the Result/Status node is updated to OK and if the Result/Message node
     * is set to an empty string.
     * 
     * @spec 3.5.6 Application Instances
     */
    private void testTreeStructure025() {
        tbc.log("#testTreeStructure025");
        DmtSession session = null;
        ApplicationHandle handle = null;
        try {
            Hashtable hash = new Hashtable();
            hash.put("Name", "Value");
            tbc.getAppDescriptor().launch(hash);
            
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            
            String[] nodes = session
                .getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES);
            
            tbc.assertEquals(
                "Asserting that there is only one ApplicationHandle running.",
                1, nodes.length);
            
            tbc
                .assertEquals(
                    "Asserting if the name of the returned node is equal to the service.pid of the ApplicationHandle.",
                    ApplicationConstants.OSGI_APPLICATION_HANDLE_SERVICE_PID,
                    nodes[0]);
                        
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID);
            
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Instances/<instance_id> metanode scope",
                    MetaNode.PERMANENT, metaNode.getScope());
            
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Instances/<instance_id> metanode format",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Instances/<instance_id> metanode cardinality",
                    metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
                      
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Instances/<instance_id> metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
                      
            tbc
                .assertEquals(
                    "Asserting if the Result/InstanceID node is updated with the instance identifier of the newly created application instance.",
                    handle.getInstanceId(),
                    session
                        .getNodeValue(
                            ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT)
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
            
            nodes = session.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS);
        
        tbc.assertEquals(
            "Asserting that there is only one ApplicationHandle running.",
            1, nodes.length);
               
        DmtData value = session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS+"\\"+nodes[0]+"\\Name");
        tbc.assertEquals("Asserting if the passed parameters name is stored in Dmt.", "Name", value.getString());    
        value = session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS+"\\"+nodes[0]+"\\Value");
        tbc.assertEquals("Asserting if the passed parameters value is stored in Dmt.", "Value", value.getString());
        
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
            tbc.cleanUp(handle);
        }
    }
    
    /**
     * This method asserts if $/Application/<app_id>/Instances/<instance_id>/State
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 9.
     * 
     * @spec 3.5.6 Application Instances
     */
    private void testTreeStructure026() {
        tbc.log("#testTreeStructure026");
        DmtSession session = null;
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);

            tbc
            .assertTrue(
                "Asserts if $/Application/<app_id>/Instances/<instance_id>/State is a valid node",
                session
                    .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_STATE));
 
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_STATE);
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/State metanode scope",
                    MetaNode.PERMANENT, metaNode.getScope());
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/State metanode format",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/State metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/State metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            
            //TODO check State value using getNodeValue.
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.closeSession(session);
            tbc.cleanUp(handle);
        }
    }
    
    /**
     * This method asserts if $/Application/<app_id>/Instances/<instance_id>/Operations
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 9.
     * 
     * @spec 3.5.6 Application Instances
     */
    private void testTreeStructure027() {
        tbc.log("#testTreeStructure027");
        DmtSession session = null;
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);

            tbc
            .assertTrue(
                "Asserts if $/Application/<app_id>/Instances/<instance_id>/Operations is a valid node",
                session
                    .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS));
 
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS);
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations metanode scope",
                    MetaNode.PERMANENT, metaNode.getScope());
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations metanode format",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc
                .closeSession(session);
            tbc.cleanUp(handle);
        }
    }
    
    /**
     * This method asserts if $/Application/<app_id>/Instances/<instance_id>/Operations/Stop
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 9.
     * 
     * @spec 3.5.6 Application Instances
     */
    private void testTreeStructure028() {
        tbc.log("#testTreeStructure028");
        DmtSession session = null;
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);

            tbc
            .assertTrue(
                "Asserts if $/Application/<app_id>/Instances/<instance_id>/Operations/Stop is a valid node",
                session
                    .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_STOP));
 
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_STOP);
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations/Stop metanode scope",
                    MetaNode.PERMANENT, metaNode.getScope());
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations/Stop metanode format",
                    DmtData.FORMAT_NULL, metaNode.getFormat());
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations/Stop metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations/Stop metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations/Stop metanode EXECUTE",
                    metaNode.can(MetaNode.CMD_EXECUTE));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc
                .closeSession(session);
            tbc.cleanUp(handle);
        }
    }      
    
    /**
     * This method asserts if executing $/Application/<app_id>/Instances/<instance_id>/Operations/Stop ,
     * it really stops the ApplicationHandle.
     * 
     * @spec 3.5.6 Application Instances
     */
    private void testTreeStructure029() {
        tbc.log("#testTreeStructure029");
        DmtSession session = null;
        ApplicationHandle handle = null;
        try {
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);

            
            handle = tbc.getAppDescriptor().launch(null);
            
            tbc
                .assertNotNull(
                    "Asserting that exist a ApplicationHandle in service registry.",
                    handle);
            
            session.execute(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_STOP, null);
            
            tbc.assertNull("Asserting if the ApplicationHandle was really stopped.", tbc.getAppHandle());
                       
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc
                .closeSession(session);
            tbc.cleanUp(handle);
        }
    }
    
    /**
     * This method asserts if $/Application/<app_id>/Instances/<instance_id>/Operations/Ext
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 9.
     * 
     * @spec 3.5.6 Application Instances
     */
    private void testTreeStructure030() {
        tbc.log("#testTreeStructure030");
        DmtSession session = null;
        ApplicationHandle handle = null;
        try {
            handle = tbc.getAppDescriptor().launch(null);
            
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);

            tbc
            .assertTrue(
                "Asserts if $/Application/<app_id>/Instances/<instance_id>/Operations/Ext is a valid node",
                session
                    .isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_EXT));
 
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_EXT);
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations/Ext metanode scope",
                    MetaNode.PERMANENT, metaNode.getScope());
            tbc
                .assertEquals(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations/Ext metanode format",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations/Ext metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed()
                        && metaNode.getMaxOccurrence() == 1);
            tbc
                .assertTrue(
                    "Asserts $/Application/<app_id>/Instances/<instance_id>/Operations/Ext metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc
                .closeSession(session);
            tbc.cleanUp(handle);
        }
    }  
    
    /**
     * This method asserts if $/Application/<app_id>/Schedule/<schedule_id>
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure031() {
        tbc.log("#testTreeStructure031");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedule/<schedule_id> metanode scope",
                    MetaNode.DYNAMIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedule/<schedule_id> metanode format",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedule/<schedule_id> metanode cardinality",
                    metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == Integer.MAX_VALUE);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedule/<schedule_id> metanode ADD",
                    metaNode.can(MetaNode.CMD_ADD));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedule/<schedule_id> metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedule/<schedule_id> metanode DELETE",
                    metaNode.can(MetaNode.CMD_DELETE));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    }
    
    /**
     * This method asserts if $/Application/<app_id>/Schedules/<schedule_id>/Arguments
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure032() {
        tbc.log("#testTreeStructure032");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

            tbc.assertTrue(
                "Asserts if $/Application/<app_id>/Schedules/<schedule_id>/Arguments is a valid node",
                session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS));            
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS);
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments metanode format",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e
                    .getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    } 
    
    /**
     * This method asserts if $/Application/<app_id>/Schedules/<schedule_id>/Enabled
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure033() {
        tbc.log("#testTreeStructure033");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

            tbc.assertTrue(
                "Asserts if $/Application/<app_id>/Schedules/<schedule_id>/Enabled is a valid node",
                session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED));            
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED);
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Enabled metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Enabled metanode format",
                    DmtData.FORMAT_BOOLEAN, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Enabled metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Enabled metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Enabled metanode REPLACE",
                    metaNode.can(MetaNode.CMD_REPLACE));
            
            tbc.assertTrue("Asserting if the default value is false.", !session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED).getBoolean());            
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    }    
    
    /**
     * This method asserts if $/Application/<app_id>/Schedules/<schedule_id>/TopicFilter
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure034() {
        tbc.log("#testTreeStructure034");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

            tbc.assertTrue(
                "Asserts if $/Application/<app_id>/Schedules/<schedule_id>/TopicFilter is a valid node",
                session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER));            
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER);
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/TopicFilter metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/TopicFilter metanode format",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/TopicFilter metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/TopicFilter metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/TopicFilter metanode REPLACE",
                    metaNode.can(MetaNode.CMD_REPLACE));
            
            tbc.assertEquals("Asserting if the default value is an empty string.", "", session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER).getString());
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    }
    
    /**
     * This method asserts if $/Application/<app_id>/Schedules/<schedule_id>/EventFilter
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure035() {
        tbc.log("#testTreeStructure035");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

            tbc.assertTrue(
                "Asserts if $/Application/<app_id>/Schedules/<schedule_id>/EventFilter is a valid node",
                session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER));            
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER);
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/EventFilter metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/EventFilter metanode format",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/EventFilter metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/EventFilter metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/EventFilter metanode REPLACE",
                    metaNode.can(MetaNode.CMD_REPLACE));
            
            tbc.assertEquals("Asserting if the default value is an empty string.", "", session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER).getString());
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    }   
    
    /**
     * This method asserts if $/Application/<app_id>/Schedules/<schedule_id>/Recurring
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure036() {
        tbc.log("#testTreeStructure036");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

            tbc.assertTrue(
                "Asserts if $/Application/<app_id>/Schedules/<schedule_id>/Recurring is a valid node",
                session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_RECURRING));            
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_RECURRING);
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Recurring metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Recurring metanode format",
                    DmtData.FORMAT_BOOLEAN, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Recurring metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Recurring metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Recurring metanode REPLACE",
                    metaNode.can(MetaNode.CMD_REPLACE));
            
            tbc.assertEquals("Asserting if the default value is an empty string.", "", session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER).getString());
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    }    
    
    /**
     * This method asserts if $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Name
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure037() {
        tbc.log("#testTreeStructure037");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

            String[] nodes = session.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS);
            
            tbc.assertEquals("Asserting that one parameter was created.", 1, nodes.length);
            
            tbc.assertTrue(
                "Asserts if $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Name is a valid node",
                session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS+nodes[0]+"/Name"));            
            
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS+nodes[0]+"/Name");
            
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Name metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Name metanode format",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Name metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Name metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Name metanode REPLACE",
                    metaNode.can(MetaNode.CMD_REPLACE));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    } 
            
    /**
     * This method asserts if $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Value
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure038() {
        tbc.log("#testTreeStructure038");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);

            String[] nodes = session.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS);
            
            tbc.assertEquals("Asserting that one parameter was created.", 1, nodes.length);
            
            tbc.assertTrue(
                "Asserts if $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Value is a valid node",
                session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS+nodes[0]+"/Value"));            
            
            MetaNode metaNode = session
                .getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS+nodes[0]+"/Value");
            
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Value metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Value metanode format",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Value metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Value metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Schedules/<schedule_id>/Arguments/<arg_id>/Value metanode REPLACE",
                    metaNode.can(MetaNode.CMD_REPLACE));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    }
    
    /**
     * This method asserts if $/Application/<app_id>/ApplicationID
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure039() {
        tbc.log("#testTreeStructure039");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);

            tbc.assertTrue(
                    "Asserts if $/Application/<app_id>/ApplicationID is a valid node",
                    session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_APPLICATION_ID));            
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_APPLICATION_ID);
            
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/ApplicationID metanode scope",
                    MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/ApplicationID metanode format",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/ApplicationID metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/ApplicationID metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    }
    
    /**
     * This method asserts if $/Application/<app_id>/Location
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure040() {
        tbc.log("#testTreeStructure040");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);

            tbc.assertTrue(
                    "Asserts if $/Application/<app_id>/Location is a valid node",
                    session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_LOCATION));            
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_LOCATION);
            
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Location metanode scope",
                    MetaNode.PERMANENT, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Location metanode format",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Location metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Location metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    }
    
    /**
     * This method asserts if $/Application/<app_id>/Operations/Launch/<launch_id>/Result
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure041() {
        tbc.log("#testTreeStructure041");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);

            tbc.assertTrue(
                    "Asserts if $/Application/<app_id>/Schedules/<launch_id>/Result is a valid node",
                    session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT));
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT);
            
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Result metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Result metanode format",
                    DmtData.FORMAT_NODE, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Result metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Result metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
        }
    }
    
    /**
     * This method asserts if $/Application/<app_id>/Operations/Launch/<launch_id>/Result/InstanceID
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure042() {
        tbc.log("#testTreeStructure042");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);

            tbc.assertTrue(
                    "Asserts if $/Application/<app_id>/Operations/Launch/<launch_id>/Result is a valid node",
                    session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_INSTANCEID));
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_INSTANCEID);
            
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Result metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Result metanode format",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Result metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Result metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
        }
    }
    
    /**
     * This method asserts if $/Application/<app_id>/Operations/Launch/<launch_id>/Result/Status
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure043() {
        tbc.log("#testTreeStructure043");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);

            tbc.assertTrue(
                    "Asserts if $/Application/<app_id>/Operations/Launch/<launch_id>/Status is a valid node",
                    session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_STATUS));
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_STATUS);
            
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Status metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Status metanode format",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Status metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Status metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
        }
    }
    
    /**
     * This method asserts if $/Application/<app_id>/Operations/Launch/<launch_id>/Result/Message
     * is a valid node and asserts Type, Cardinality, Add
     * Permission, Get Permission, Replace Permission, Delete Permission and
     * Execute Permission according to Table 10.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure044() {
        tbc.log("#testTreeStructure044");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID);

            tbc.assertTrue(
                    "Asserts if $/Application/<app_id>/Operations/Launch/<launch_id>/Status is a valid node",
                    session.isNodeUri(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_MESSAGE));
            
            MetaNode metaNode = session.getMetaNode(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_MESSAGE);
            
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Status metanode scope",
                    MetaNode.AUTOMATIC, metaNode.getScope());
            tbc.assertEquals(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Status metanode format",
                    DmtData.FORMAT_STRING, metaNode.getFormat());
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Status metanode cardinality",
                    !metaNode.isZeroOccurrenceAllowed() && metaNode.getMaxOccurrence() == 1);
            tbc.assertTrue(
                    "Asserts $/Application/<app_id>/Operations/Launch/<launch_id>/Status metanode GET",
                    metaNode.can(MetaNode.CMD_GET));
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID });
        }
    }
    
    /**
     * This method starts a scheduled application and asserts 
     * the data stored in dmt if they are equal to the passed as parameter.
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure045() {
        tbc.log("#testTreeStructure045");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
            
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);
            
            String[] nodes = session.getChildNodeNames(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES);
            
            tbc.assertEquals("Asserting if exists only one node inside schedules node.", 1, nodes.length);
            
            boolean value = session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH + nodes[0] + "/Enabled").getBoolean();
            
            tbc.assertTrue("Asserting if the schedule is running.", value);
            
            String topic = session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH + nodes[0] + "Topic").getString();
            
            tbc.assertEquals("Asserting if the topic stored in Dmt was equal to the topic passed.", ApplicationConstants.TIMER_EVENT, topic);
            
            String filter = session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH + nodes[0] + "EventFilter").getString();
            
            tbc.assertEquals("Asserting if the filter stored in Dmt was equal to the filter passed.", ApplicationConstants.EVENT_FILTER, filter);
            
            value = session.getNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH + nodes[0] + "/Recurring").getBoolean();
            
            tbc.assertTrue("Asserting if the recurring is really true.", value);
            
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    }    
    
    /**
     * This method starts a scheduled application by Dmt. 
     * 
     * @spec 3.5.7 Scheduling applications
     */
    private void testTreeStructure046() {
        tbc.log("#testTreeStructure046");
        DmtSession session = null;
        try {            
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_SHARED);
            
            // verify if there was a ScheduledApplication in the registry, if yes remove
            ScheduledApplication sa = tbc.getScheduledApplication();
            if (sa!=null) {
                sa.remove();
                sa = null;
            }
            
            session.createInteriorNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID);
            
            session.createLeafNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS+"/Name", new DmtData("Name"));
            session.createLeafNode(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS+"/Value", new DmtData("Value"));
            
            session.setNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER, new DmtData(ApplicationConstants.TIMER_EVENT));
            session.setNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER, new DmtData(ApplicationConstants.EVENT_FILTER));
            
            session.setNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_RECURRING, new DmtData(true));
            
            session.setNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED, new DmtData(true));
            
            sa = tbc.getScheduledApplication();
            
            tbc.assertNotNull("Asserting that a Scheduled Application was registered.", sa);
            
            Map map = sa.getArguments();
            
            tbc.assertNotNull("Asserting that exist a property name called 'Name'", map.get("Name"));
            tbc.assertEquals("Asserting if the value in 'Name' property is Value", "Value", map.get("Name"));
            
            tbc.assertTrue("Asserting if the scheduled application registered has recurring set to true.", sa.isRecurring());
            
            tbc.assertEquals("Asserting the topic filter", ApplicationConstants.TIMER_EVENT, sa.getTopic());
            tbc.assertEquals("Asserting the filter value", ApplicationConstants.EVENT_FILTER, sa.getEventFilter());
            
            session.setNodeValue(ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED, new DmtData(false));
            
            sa = tbc.getScheduledApplication();
            
            tbc.assertNull("Asserting that a Scheduled Application was unregistered.", sa);           
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                MessagesConstants.UNEXPECTED_EXCEPTION, new String[]{e.getClass().getName()}));
        } finally {
            tbc.cleanUp(session, new String[] { ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS + "/Name", 
                    ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS + "/Value", ApplicationConstants.OSGI_APPLICATION_APPID_SCHEDULES_ID });
        }
    }
}