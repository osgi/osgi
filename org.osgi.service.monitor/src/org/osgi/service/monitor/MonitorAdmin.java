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
 * <p>
 * Note that an alternative but not recommended way of obtaining KPIs is by
 * querying the list of Monitorable services from the service registry and then
 * querying the list of KPI names from the Monitorable services. This way all
 * services which publish KPIs will be returned regardless of whether they do or
 * do not hold the necessary <code>KpiPermission</code> for publishing KPIs.
 * By using the MonitorAdmin to obtain the Monitorables it is guaranteed that
 * only those services will be accessed who are authorized to publish KPIs. It
 * is the responsibility of the MonitorAdmin implementation to check the
 * required permissions and show only those services which pass this check.
 */
public interface MonitorAdmin {

    /**
	 * Returns all the Monitorable services that are currently registered. For
	 * security reasons this method should be used instead of querying the
	 * monitorable services from the service registry.
	 * 
	 * @return the array of Monitorables or <code>null</code> if none are
	 *         registered
	 */
    public Monitorable[] getMonitorables();

    /**
	 * Returns the Monitorable service addressed by its PID. For security
	 * reasons this method should be used instead of querying the monitorable
	 * services from the service registry.
	 * 
	 * @param id the persistent identity of the monitorable service
	 * @return the Monitorable object
	 * @throws IllegalArgumentException if the ID is invalid or points to a non
	 *         existing monitorable service
	 */
    public Monitorable getMonitorable(String id)
            throws IllegalArgumentException;

    /**
	 * Returns a KPI addressed by its ID in [Monitorable_ID]/[KPI_ID] format.
	 * This is a convenience feature for the cases when the full path of the KPI
	 * is known.
	 * <p>
	 * The entity which queries a KPI needs to hold <code>KpiPermission</code>
	 * for the given target with the <code>read</code> action present.
	 * 
	 * @param path the full path of the KPI in [Monitorable_ID]/[KPI_ID] format
	 * @return the KPI object
	 * @throws IllegalArgumentException if the path is invalid or points to a
	 *         non-existing KPI
	 * @throws SecurityException if the caller does not hold a
	 *         <code>KpiPermission</code> for the KPI specified by
	 *         <code>path</code> with the <code>read</code> action present
	 */
    public KPI getKPI(String path) throws IllegalArgumentException;

    /**
	 * Starts a Monitoring Job with the parameters provided. All specified KPIs
	 * must exist when the job is started. The initiator string is used in the
	 * <code>listener.id</code> field of all events triggered by the job, to
	 * allow filtering the events based on the initiator.
	 * <p>
	 * The entity which initiates a Monitoring Job needs to hold
	 * <code>KpiPermission</code> for all the specified target KPIs with the
	 * <code>startjob</code> action present. In case of time based jobs, if
	 * the permission's action string specifies a minimal sampling interval then
	 * the <code>schedule</code> parameter should be at least as great as the
	 * value in the action string.
	 * 
	 * @param initiator the identifier of the entity that initiated the job
	 * @param kpis List of KPIs to be monitored. The KPI names must be given in
	 *        [Monitorable_PID]/[KPI_ID] format.
	 * @param schedule The time in seconds between two measurements. The value 0
	 *        means that instant notification on KPI updates is requested. For
	 *        values greater than 0 the first measurement will be taken when the
	 *        timer expires for the first time, not when this method is called.
	 * @param count For time based monitoring jobs, this should be the number of
	 *        measurements to be taken, or 0 if the measurement must run
	 *        infinitely. For change based monitoring (if schedule is 0), this
	 *        must be a positive interger specifying the number of changes that
	 *        must happen before a new notification is sent.
	 * @return the successfully started job object
	 * @throws IllegalArgumentException if the list of KPI names contains a
	 *         non-existing KPI or the schedule or count parameters are invalid
	 * @throws SecurityException if the caller does not hold
	 *         <code>KpiPermission</code> for all the specified KPIs, with the
	 *         <code>startjob</code> action present, or if the permission does
	 *         not allow starting the job with the given frequency
	 */
    public MonitoringJob startJob(String initiator, String[] kpis,
                                  int schedule, int count)
        throws IllegalArgumentException;

    /**
	 * Returns the list of currently running Monitoring Jobs.
	 * 
	 * @return the list of running jobs
	 */
    public MonitoringJob[] getRunningJobs();
}
