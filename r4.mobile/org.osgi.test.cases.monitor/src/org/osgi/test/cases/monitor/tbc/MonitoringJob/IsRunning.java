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

import org.osgi.service.monitor.MonitoringJob;
import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>isRunning</code> method, according to MEG reference
 * documentation.
 */
public class IsRunning {
    
    private MonitorTestControl tbc;

    public IsRunning(MonitorTestControl tbc) {
        this.tbc = tbc;
    }
    
    public void run() {
    	testIsRunning001();
    	testIsRunning002();
    }
    
	/**
	 * This method asserts that true is returned when a job is 
	 * running until it is explicitely stopped.
	 * 
	 * @spec MonitoringJob.isRunning()
	 */
    private void testIsRunning001() {
    	tbc.log("#testIsRunning001");
    	MonitoringJob mj = null;
		try {
			tbc.stopRunningJobs();
			
			mj = tbc.getMonitorAdmin().startJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.COUNT + 1);

			tbc
			.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "a monitoring job returns true to isRunning()." }),
					mj.isRunning());
			
			mj.stop();

			tbc
			.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "a monitoring job returns false to isRunning()." }),
					!mj.isRunning());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			mj.stop();
		}
	}        
    
	/**
	 * This method asserts that true is returned for a time based job
	 * until the given number of measurements have been made.
	 *  
	 * @spec MonitoringJob.isRunning()
	 */   
    private synchronized void testIsRunning002() {
    	tbc.log("#testIsRunning002");
    	MonitoringJob mj = null;
		try {
			tbc.stopRunningJobs();
			
			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.SCHEDULE+1,
					MonitorConstants.COUNT + 1); // schedule for 2 seconds and count for 2 measurements 

			tbc
			.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "a monitoring job returns true to isRunning()." }),
					mj.isRunning());
			
			wait(MonitorConstants.SHORT_TIMEOUT*3);
			
			tbc
			.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "a monitoring job returns false to isRunning()." }),
					!mj.isRunning());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			mj.stop();
		}
	}     

}
