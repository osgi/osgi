/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.monitor;

/**
 * A MonitorAdmin implementation handles KPI query requests and measurement job
 * control requests.
 *  
 */
public interface MonitorAdmin {
	/**
	 * Returns a KPI addressed by its ID in [Monitorable_ID]/[KPI_ID] format.
	 * This is a convenience feature for the cases when the full path of the KPI
	 * is known. KPIs can also be obtained by querying the list of Monitorable
	 * services from the service registry and then querying the list of KPI
	 * names from the Monitorable services.
	 * 
	 * @param path the full path of the KPI in [Monitorable_ID]/[KPI_ID] format
	 * @return the KPI object
	 * @throws IllegalArgumentException if the path is invalid or points to a
	 *         non existing KPI
	 */
	public KPI getKPI(String path) throws IllegalArgumentException;

	/**
	 * Starts a Monitoring Job with the parameters provided. If the list of KPI
	 * names contains a non existing KPI or the schedule or count parameters are
	 * invalid then IllegalArgumentException is thrown. The initiator string is
	 * used in the <code>listener.id</code> field of all events triggered by
	 * the job, to allow filtering the events based on the initiator.
	 * 
	 * @param initiator the identifier of the entity that initiated the job
	 * @param kpis list of KPIs to be monitored. The KPI names must be given in
	 *        [Monitorable_PID]/[KPI_ID] format
	 * @param schedule The time in seconds between two measurements. The value 0
	 *        means that instant notification on KPI updates is requested. For
	 *        values greater than 0 the first measurement will be taken when the
	 *        timer expires for the first time, not when this method is called.
	 * @param count For time based monitoring jobs, this should be the number of
	 *        measurements to be taken, or 0 if the measurement must run
	 *        infinitely. For change based monitoring (if schedule is 0), this
	 *        must be a positive interger that specifies the number of changes
	 *        that must happen before a new notification is sent.
	 * @return the successfully started job object
	 * @throws IllegalArgumentException
	 */
	public MonitoringJob startJob(String initiator, String[] kpis,
			int schedule, int count) throws IllegalArgumentException;

	/**
	 * Returns the list of currently running Monitoring Jobs.
	 * 
	 * @return the list of running jobs
	 */
	public MonitoringJob[] getRunningJobs();
}
