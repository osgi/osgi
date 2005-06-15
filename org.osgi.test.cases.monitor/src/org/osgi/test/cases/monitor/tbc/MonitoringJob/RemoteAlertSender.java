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

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.monitor.StatusVariable;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @generalDescription This Test Class Validates the process of starting
 *                     a job remotely.
 */
public class RemoteAlertSender {
    private MonitorTestControl tbc;

    /**
     * @param tbc
     */
    public RemoteAlertSender(MonitorTestControl tbc) {
        this.tbc = tbc;
    }
    
    public void run() {
    	testRemoteAlertSender001();
    	testRemoteAlertSender002();
    }
    
	/**
	 * @testID testRemoteAlertSender001
	 * @testDescription Tests if a monitoring job started remotely advise
	 *                  the remote server after changes at statusvariable using the event based job.
	 */    
    public synchronized void testRemoteAlertSender001() {
    	tbc.log("#testRemoteAlertSender001");
    	DmtSession session = null;
        try {
        	session = tbc.getDmtAdmin().getSession(".");					
			session.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId
			session.setNodeValue(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[1], new DmtData("EV"));
			session.setNodeValue(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[2], new DmtData(MonitorTestControl.COUNT));
			session.setNodeValue(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[3], new DmtData(true));
					
			tbc.resetAlert();
			
			tbc.getMonitorListener().updated(
					MonitorTestControl.SV_MONITORABLEID1,
					new StatusVariable(MonitorTestControl.SV_NAME1, StatusVariable.CM_CC,
							"test1"));		
			
			wait(MonitorTestControl.TIMEOUT);
			
			tbc.assertTrue("Asserting if our implementation of RemoteAlertSender was called.", tbc.isReceivedAlert());
			tbc.assertEquals("Asserting if we receive the correct value of serverId.", MonitorTestControl.REMOTE_SERVER, tbc.getServerId());
			tbc.assertNull("Asserting if we receive the correct value of correlator(null).", tbc.getCorrelator());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			try {
				session.setNodeValue(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[3], new DmtData(false));
				session.deleteNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]);
				session.close();
			} catch (DmtException e1) {
				tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e1.getClass().getName());
			}
		}
    }
    
	/**
	 * @testID testRemoteAlertSender002
	 * @testDescription Tests if a monitoring job started remotely advise
	 *                  the remote server after changes at statusvariable using the time based job.
	 */    
    public synchronized void testRemoteAlertSender002() {
    	tbc.log("#testRemoteAlertSender002");
    	DmtSession session = null;
        try {
        	session = tbc.getDmtAdmin().getSession(".");					
			session.createInteriorNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId
			session.setNodeValue(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[1], new DmtData("TM"));
			session.setNodeValue(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[2], new DmtData(MonitorTestControl.COUNT));
			session.setNodeValue(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[3], new DmtData(true));
					
			tbc.resetAlert();
			
			tbc.getMonitorListener().updated(
					MonitorTestControl.SV_MONITORABLEID1,
					new StatusVariable(MonitorTestControl.SV_NAME1, StatusVariable.CM_CC,
							"test1"));
			
			wait(MonitorTestControl.TIMEOUT);
			
			tbc.assertTrue("Asserting if our implementation of RemoteAlertSender was called.", tbc.isReceivedAlert());
			tbc.assertEquals("Asserting if we receive the correct value of serverId.", MonitorTestControl.REMOTE_SERVER, tbc.getServerId());
			tbc.assertNull("Asserting if we receive the correct value of correlator(null).", tbc.getCorrelator());			
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			try {
				session.setNodeValue(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[3], new DmtData(false));
				session.deleteNode(MonitorTestControl.DMT_URI_MONITORABLE1_PROPERTIES[0]);
				session.close();
			} catch (DmtException e1) {
				tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e1.getClass().getName());
			}
		}
    }           

}
