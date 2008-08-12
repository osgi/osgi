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
 * Feb 23, 2005	Alexandre Santos
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.MonitoringJob;

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;

import org.osgi.service.monitor.MonitoringJob;
import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>isLocal</code> method, according to MEG reference
 * documentation.
 */
public class IsLocal {
    
    private MonitorTestControl tbc;

    public IsLocal(MonitorTestControl tbc) {
        this.tbc = tbc;
    }
    
    public void run() {
    	testIsLocal001();
    	testIsLocal002();
    }
    
	/**
	 * This method asserts that this method returns true when we are
	 * using a local monitoring job.
	 * 
	 * @spec MonitoringJob.isLocal()
	 */
    private void testIsLocal001() {
    	tbc.log("#testIsLocal001");
    	MonitoringJob mj = null;
		try {
			tbc.stopRunningJobs();
			
			mj = tbc.getMonitorAdmin().startJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.COUNT + 1);

			tbc
			.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "a local monitoring job returns true to isLocal()." }),
					mj.isLocal());

			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			mj.stop();
		}
	}        
    
	/**
	 * This method asserts that this method returns false when we are
	 * using a remote monitoring job.
	 * 
	 * @spec MonitoringJob.isLocal()
	 */   
    private void testIsLocal002() {
    	tbc.log("#testIsLocal002");
    	DmtSession session = null;
        try {
        	tbc.stopRunningJobs();
        	
        	session = tbc.getDmtAdmin().getSession(".");					
			session.createInteriorNode(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[0]); // serverId
			session.setNodeValue(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[1], new DmtData("EV"));
			session.setNodeValue(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[2], new DmtData(MonitorConstants.COUNT));
			session.setNodeValue(MonitorConstants.DMT_URI_MONITORABLE1_PROPERTIES[3], new DmtData(true));
					
			MonitoringJob[] jobs = tbc.getMonitorAdmin().getRunningJobs();
			if (jobs.length!=1) {
				tbc.fail("A remote monitoring job was started and it wasn't returned in getRunningJobs() result.");
			} else {
				tbc
				.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "a remote monitoring job returns false to isLocal()." }),
						!jobs[0].isLocal());				
			}

        } catch (DmtException e) {
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
