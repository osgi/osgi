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
 * Apr 04, 2005	Alexandre Santos
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.MonitoringJob;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.notification.AlertItem;

import org.osgi.service.monitor.StatusVariable;
import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestingMonitorable;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Class Validates the remote monitoring job.
 * 
 */
public class RemoteAlertSender {
    private MonitorTestControl tbc;

    public RemoteAlertSender(MonitorTestControl tbc) {
        this.tbc = tbc;
    }
    
    public void run() {
    	testRemoteAlertSender001();
    	testRemoteAlertSender002();
    }
    
	/**
	 * This method asserts if a remote monitoring job advise
	 * the remote server after changes at statusvariable using
	 * the change based job.
	 * 
	 * @spec 120.4.4 Monitoring Job
	 */    
    private synchronized void testRemoteAlertSender001() {
    	tbc.log("#testRemoteAlertSender001");
    	DmtSession session = null;
        try {
        	session = tbc.getDmtAdmin().getSession(".");					
			session.createInteriorNode(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId
			session.setNodeValue(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[1], new DmtData("EV"));
			session.setNodeValue(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[2], new DmtData(MonitorConstants.COUNT));
			session.setNodeValue(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[3], new DmtData(true));
					
			tbc.resetAlert();
			
			TestingMonitorable monitorable = tbc.getMonitorableInterface();
			StatusVariable sv = new StatusVariable(MonitorConstants.SV_NAME1,
					StatusVariable.CM_CC, "test1");
			monitorable.setStatusVariable(sv);			
			
			tbc.getMonitorListener().updated(
					MonitorConstants.SV_MONITORABLEID1,
					sv);		
			
			tbc.assertTrue("Asserting if our implementation of RemoteAlertSender was called.", tbc.isReceivedAlert());
			tbc.assertEquals("Asserting if we receive the correct value of serverId.", MonitorConstants.REMOTE_SERVER, tbc.getServerId());
			tbc.assertNull("Asserting if we receive the correct value of correlator(null).", tbc.getCorrelator());
			
			AlertItem[] alerts = tbc.getAlerts();
			tbc.assertNotNull("Asserting that a non-null value was passed.", alerts);
			tbc.assertEquals("Asserting that only one AlertItem was passed.", 1, alerts.length);
			tbc.assertEquals("Asserting the source value in the AlertItem object.", MonitorConstants.DMT_URI_MONITORABLE1_SV1, alerts[0].getSource());
			tbc.assertEquals("Asserting the oma trap format in the AlertItem object.", MonitorConstants.MONITOR_XML_MONITORABLE1_SV1, alerts[0].getType());
			tbc.assertNull("Asserting that null is returned when getMark is called in AlertItem object.", alerts[0].getMark());
			tbc.assertEquals("Asserting if the value in AlertItem object is the expected.", "test1", alerts[0].getData().getString());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			try {
				session.setNodeValue(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[3], new DmtData(false));
				session.deleteNode(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[0]);
				session.close();
			} catch (DmtException e1) {
				tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e1.getClass().getName());
			}
			//Monitorable was changed, restore its state 
			tbc.reinstallMonitorable1();
		}
    }
    
    
	/**
	 * This method asserts if a remote monitoring job send an alert
	 * every 1 second indefinitely.
	 * 
	 * @spec 120.4.4 Monitoring Job
	 */    
    private void testRemoteAlertSender002() {
    	tbc.log("#testRemoteAlertSender002");
    	DmtSession session = null;
        try {
			tbc.resetAlert();        	
        	
        	session = tbc.getDmtAdmin().getSession(".");					
			session.createInteriorNode(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId
			session.setNodeValue(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[1], new DmtData("TM"));
			session.setNodeValue(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[2], new DmtData(MonitorConstants.SCHEDULE));
			session.setNodeValue(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[3], new DmtData(true));
							
			tbc.sleep0(MonitorConstants.SHORT_TIMEOUT);			
			
			tbc.assertTrue("Asserting if our implementation of RemoteAlertSender was called.", tbc.isReceivedAlert());
			tbc.assertEquals("Asserting if we receive the correct value of serverId.", MonitorConstants.REMOTE_SERVER, tbc.getServerId());
			tbc.assertNull("Asserting if we receive the correct value of correlator(null).", tbc.getCorrelator());			

			AlertItem[] alerts = tbc.getAlerts();
			tbc.assertNotNull("Asserting that a non-null value was passed.", alerts);
			tbc.assertEquals("Asserting that only one AlertItem was passed.", 1, alerts.length);
			tbc.assertEquals("Asserting the source value in the AlertItem object.", MonitorConstants.DMT_URI_MONITORABLE1_SV1, alerts[0].getSource());
			tbc.assertEquals("Asserting the oma trap format in the AlertItem object.", MonitorConstants.MONITOR_XML_MONITORABLE1_SV1, alerts[0].getType());
			tbc.assertNull("Asserting that null is returned when getMark is called in AlertItem object.", alerts[0].getMark());
			tbc.assertEquals("Asserting if the value in AlertItem object is the expected.", "test", alerts[0].getData().getString());			
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			try {
				session.setNodeValue(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[3], new DmtData(false));
				session.deleteNode(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[0]);
				session.close();
			} catch (DmtException e1) {
				tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e1.getClass().getName());
			}
		}
    }           

}
