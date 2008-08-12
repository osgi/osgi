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
 * Mar 10, 2005	Eduardo Oliveira
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
 * <code>stop</code> method, according to MEG reference
 * documentation.
 */
public class Stop {
	private MonitorTestControl tbc;

	public Stop(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testStop001();
		testStop002();
	}

	
	/**
	 * This method asserts if a change based monitoringJob is really stopped after
	 * a stop call.
	 * 
	 * @spec MonitoringJob.stop()
	 */
	private void testStop001() {
		tbc.log("#testStop001");
		try {
			tbc.stopRunningJobs();
			
			MonitoringJob job = tbc.getMonitorAdmin().startJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.COUNT);
			job.stop();

			MonitoringJob[] jobs = tbc.getMonitorAdmin().getRunningJobs();
			for (int i = 0; i < jobs.length; i++) {
				if ((jobs[i].getInitiator()
						.equals(MonitorConstants.INITIATOR))
						&& (jobs[i].getStatusVariableNames()
								.equals(MonitorConstants.SVS))
						&& (jobs[i].getReportCount() == MonitorConstants.COUNT)
						) {
					tbc
							.fail("The MonitoringJob was started and after a stop call, it didn't stop.");
				}
			}
			tbc.pass("The MonitoringJob was started and stopped with success.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

	/**
	 * This method asserts if a time based monitoringJob is really stopped after
	 * a stop call.
	 * 
	 * @spec MonitoringJob.stop()
	 */
	private void testStop002() {
		tbc.log("#testStop002");
		try {
			tbc.stopRunningJobs();

			MonitoringJob job = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.SCHEDULE, MonitorConstants.COUNT);
			job.stop();

			MonitoringJob[] jobs = tbc.getMonitorAdmin().getRunningJobs();
			for (int i = 0; i < jobs.length; i++) {
				if ((jobs[i].getInitiator()
						.equals(MonitorConstants.INITIATOR))
						&& (jobs[i].getStatusVariableNames()
								.equals(MonitorConstants.SVS))
						&& (jobs[i].getReportCount() == MonitorConstants.COUNT)
						&& (jobs[i].getSchedule() == MonitorConstants.SCHEDULE)) {
					tbc
							.fail("The MonitoringJob was started and after a stop call, it didn't stop.");
				}
			}
			tbc.pass("The MonitoringJob was started and stopped with success.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
	

}
