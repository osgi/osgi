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
 * A MonitorAdmin implementation handles StatusVariable query requests and
 * measurement job control requests.
 * <p>
 * Note that an alternative but not recommended way of obtaining StatusVariables
 * is that applications having the required ServicePermissions can query the
 * list of Monitorable services from the service registry and then query the
 * list of StatusVariable names from the Monitorable services. This way all
 * services which publish StatusVariables will be returned regardless of whether
 * they do or do not hold the necessary <code>MonitorPermission</code> for
 * publishing StatusVariables. By using the MonitorAdmin to obtain the
 * StatusVariables it is guaranteed that only those Monitorable services will be
 * accessed who are authorized to publish StatusVariables. It is the
 * responsibility of the MonitorAdmin implementation to check the required
 * permissions and show only those services which pass this check.
 */
public interface MonitorAdmin {

    /**
     * Returns a StatusVariable addressed by its ID in
     * [Monitorable_ID]/[StatusVariable_ID] format. The entity which queries a
     * StatusVariable needs to hold <code>MonitorPermission</code> for the
     * given target with the <code>read</code> action present.
     * 
     * @param path
     *            the full path of the StatusVariable in
     *            [Monitorable_ID]/[StatusVariable_ID] format
     * @return the StatusVariable object
     * @throws IllegalArgumentException
     *             if the path is invalid or points to a non-existing
     *             StatusVariable
     * @throws SecurityException
     *             if the caller does not hold a <code>MonitorPermission</code>
     *             for the StatusVariable specified by <code>path</code> with
     *             the <code>read</code> action present
     */
    public StatusVariable getStatusVariable(String path)
            throws IllegalArgumentException;

    /**
     * Returns the names of the Monitorable services that are currently
     * registered. The returned array contains the names in alphabetical order.
     * For security reasons this method should be used instead of querying the
     * monitorable services from the service registry. The Monitorable instances
     * are not accessible through the MonitorAdmin.
     * 
     * @return the array of Monitorable names or <code>null</code> if none are
     *         registered
     * @throws SecurityException
     *             if the caller does not hold a <code>MonitorPermission</code>
     *             with the <code>discover</code> action present. The target
     *             field must be "&#42;/*".
     */
    public String[] getMonitorableNames();

    /**
     * Returns all the StatusVariable objects published by a Monitorable
     * instance. The StatusVariables will hold the values taken at the time of
     * this method call. The array contains the elements in no particular order.
     * <p>
     * The entity which queries the StatusVariable list needs to hold
     * <code>MonitorPermission</code> with the <code>read</code> action
     * present. The target field of the permission must match all the
     * StatusVariables published by the Monitorable, e.g. it may be
     * <code>[Monitorable_ID]/*</code>.
     * 
     * @param monitorableId
     *            The identifier of a Monitorable instance
     * @return the StatusVariable objects published by the specified Monitorable
     * @throws SecurityException
     *             if the caller does not hold <code>MonitorPermission</code>
     *             with the <code>read</code> action or if there is any
     *             StatusVariable published by the Monitorable which is not
     *             allowed to be read as per the target field of the permission
     * @throws IllegalArgumentException
     *             if the monitorableId is invalid or points to a non-existing
     *             Monitorable
     */
    public StatusVariable[] getStatusVariables(String monitorableId);

    /**
     * Returns the list of StatusVariable names published by a Monitorable
     * instance. The array contains the elements in alphabetical order.
     * <p>
     * The entity which queries the StatusVariable list needs to hold
     * <code>MonitorPermission</code> with the <code>discover</code> action
     * present. The target field of the permission must match the identifier of
     * the Monitorable service.
     * 
     * @param monitorableId
     *            The identifier of a Monitorable instance
     * @return the names of the StatusVariable objects published by the
     *         specified Monitorable
     * @throws SecurityException
     *             if the caller does not hold <code>MonitorPermission</code>
     *             with the <code>discover</code> action or if the
     *             StatusVariables published by the given Monitorable are not
     *             allowed to be discovered as per the target field of the
     *             permission
     * @throws IllegalArgumentException
     *             if the monitorableId is invalid or points to a non-existing
     *             Monitorable
     */
    public String[] getStatusVariableNames(String monitorableId);

    /**
     * Switches event sending on or off for certain StatusVariable(s). When the
     * MonitorAdmin is notified about a StatusVariable being updated it sends an
     * event unless this feature is switched off. Note that events within a
     * monitoring job can not be switched off. It is not required that the event
     * sending state of the StatusVariables are persistently stored. When a 
     * StatusVariable is registered it's event sending state is ON by default.
     * <p>
     * Usage of the "*" wildcard is allowed in the path argument of this method
     * as a convenience feature. The semantics of the wildcard is that it stands
     * for any matching StatusVariable at the time of the method call, it does
     * not effect the event sending status of StatusVariables which are not yet
     * registered. As an example, when the
     * <code>switchEvents("MyMonitorable/*", false)</code> method is executed,
     * event sending from all StatusVariables of the MyMonitorable service are
     * switched off. However, if the MyMonitorable service starts to publish a
     * new StatusVariable later, it's event sending status is on by default.
     * 
     * @param path
     *            The identifier of the StatusVariable(s) in
     *            [Monitorable_id]/[StatusVariable_id] format. The "*" wildcard
     *            is allowed in both path fragments.
     * @param on
     *            If <code>false</code> then event sending is switched off, if
     *            <code>true</code> then it is switched on.
     * @throws SecurityException
     *             if the caller does not hold <code>MonitorPermission</code>
     *             with the <code>switchevents</code> action or if there is
     *             any StatusVariable in the path field for which it is not
     *             allowed to switch event sending on or off as per the target
     *             field of the permission
     * @throws IllegalArgumentException
     *             if the path is invalid or points to a non-existing
     *             StatusVariable
     */
    public void switchEvents(String path, boolean on);

    /**
     * Issues a request to reset a given StatusVariable. Depending on the
     * semantics of the StatusVariable this call may or may not succeed: it
     * makes sense to reset a counter to its starting value, but e.g. a
     * StatusVariable of type String might not have a meaningful default value.
     * Note that for numeric StatusVariables the starting value may not
     * necessarily be 0. Resetting a StatusVariable triggers a monitor event if
     * the StatusVariable supports update notifications.
     * <p>
     * The entity that wants to reset the StatusVariable needs to hold
     * <code>MonitorPermission</code> with the <code>reset</code> action
     * present. The target field of the permission must match the StatusVariable
     * name to be reset.
     * 
     * @param path
     *            the identifier of the StatusVariable in
     *            [Monitorable_id]/[StatusVariable_id] format.
     * @return <code>true</code> if the Monitorable could successfully reset
     *         the given StatusVariable, <code>false</code> otherwise
     * @throws IllegalArgumentException
     *             if the path is invalid or points to a non existing
     *             StatusVariable
     * @throws SecurityException
     *             if the caller does not hold <code>MonitorPermission</code>
     *             with the <code>reset</code> action or if the specified
     *             StatusVariable is not allowed to be reset as per the target
     *             field of the permission
     */
    public boolean resetStatusVariable(String path)
            throws IllegalArgumentException;

    /**
     * Starts a time based Monitoring Job with the parameters provided.
     * Monitoring events will be sent according to the specified schedule. All
     * specified StatusVariables must exist when the job is started. The
     * initiator string is used in the <code>mon.listener.id</code> field of
     * all events triggered by the job, to allow filtering the events based on
     * the initiator.
     * <p>
     * The entity which initiates a Monitoring Job needs to hold
     * <code>MonitorPermission</code> for all the specified target
     * StatusVariables with the <code>startjob</code> action present. If the
     * permission's action string specifies a minimal sampling interval then the
     * <code>schedule</code> parameter should be at least as great as the
     * value in the action string.
     * 
     * @param initiator
     *            the identifier of the entity that initiated the job
     * @param statusVariables
     *            List of StatusVariables to be monitored. The StatusVariable
     *            names must be given in [Monitorable_PID]/[StatusVariable_ID]
     *            format.
     * @param schedule
     *            The time in seconds between two measurements. It must be
     *            greater than 0. The first measurement will be taken when the
     *            timer expires for the first time, not when this method is
     *            called.
     * @param count
     *            The number of measurements to be taken, or 0 if the
     *            measurement must run infinitely.
     * @return the successfully started job object
     * @throws IllegalArgumentException
     *             if the list of StatusVariable names contains a non-existing
     *             StatusVariable or the initiator, schedule or count parameters
     *             are invalid
     * @throws SecurityException
     *             if the caller does not hold <code>MonitorPermission</code>
     *             for all the specified StatusVariables, with the
     *             <code>startjob</code> action present, or if the permission
     *             does not allow starting the job with the given frequency
     */
    public MonitoringJob startScheduledJob(String initiator,
            String[] statusVariables, int schedule, int count)
            throws IllegalArgumentException;

    /**
     * Starts a change based Monitoring Job with the parameters provided.
     * Monitoring events will be sent when the StatusVariables of this job are
     * updated. All specified StatusVariables must exist when the job is
     * started, and all must support update notifications. The initiator string
     * is used in the <code>mon.listener.id</code> field of all events
     * triggered by the job, to allow filtering the events based on the
     * initiator.
     * <p>
     * The entity which initiates a Monitoring Job needs to hold
     * <code>MonitorPermission</code> for all the specified target
     * StatusVariables with the <code>startjob</code> action present.
     * 
     * @param initiator
     *            the identifier of the entity that initiated the job
     * @param statusVariables
     *            List of StatusVariables to be monitored. The StatusVariable
     *            names must be given in [Monitorable_PID]/[StatusVariable_ID]
     *            format.
     * @param count
     *            A positive integer specifying the number of changes that must
     *            happen to a StatusVariable before a new notification is sent.
     * @return the successfully started job object
     * @throws IllegalArgumentException
     *             if the list of StatusVariable names contains a non-existing
     *             StatusVariable or one that does not support notifications, or
     *             if the initiator or count parameters are invalid
     * @throws SecurityException
     *             if the caller does not hold <code>MonitorPermission</code>
     *             for all the specified StatusVariables, with the
     *             <code>startjob</code> action present
     */
    public MonitoringJob startJob(String initiator, String[] statusVariables,
            int count) throws IllegalArgumentException;

    /**
     * Returns the list of currently running Monitoring Jobs.
     * 
     * @return the list of running jobs
     */
    public MonitoringJob[] getRunningJobs();
}