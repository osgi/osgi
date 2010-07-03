/*
 * Copyright (c) OSGi Alliance (2004, 2010). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.monitor;

/**
 * The {@code MonitorAdmin} service is a singleton service that handles
 * {@code StatusVariable} query requests and measurement job control
 * requests.
 * <p>
 * Note that an alternative but not recommended way of obtaining
 * {@code StatusVariable}s is that applications having the required
 * {@code ServicePermissions} can query the list of
 * {@code Monitorable} services from the service registry and then query
 * the list of {@code StatusVariable} names from the
 * {@code Monitorable} services. This way all services which publish
 * {@code StatusVariable}s will be returned regardless of whether they do
 * or do not hold the necessary {@code MonitorPermission} for publishing
 * {@code StatusVariable}s. By using the {@code MonitorAdmin} to
 * obtain the {@code StatusVariable}s it is guaranteed that only those
 * {@code Monitorable} services will be accessed who are authorized to
 * publish {@code StatusVariable}s. It is the responsibility of the
 * {@code MonitorAdmin} implementation to check the required permissions
 * and show only those variables which pass this check.
 * <p>
 * The events posted by {@code MonitorAdmin} contain the following
 * properties:
 * <ul>
 * <li>{@code mon.monitorable.pid}: The identifier of the
 * {@code Monitorable}
 * <li>{@code mon.statusvariable.name}: The identifier of the
 * {@code StatusVariable} within the given {@code Monitorable}
 * <li>{@code mon.statusvariable.value}: The value of the
 * {@code StatusVariable}, represented as a {@code String}
 * <li>{@code mon.listener.id}: The identifier of the initiator of the
 * monitoring job (only present if the event was generated due to a monitoring
 * job)
 * </ul>
 * <p>
 * Most of the methods require either a Monitorable ID or a Status Variable path
 * parameter, the latter in [Monitorable_ID]/[StatusVariable_ID] format. These
 * parameters must not be {@code null}, and the IDs they contain must
 * conform to their respective definitions in {@link Monitorable} and
 * {@link StatusVariable}. If any of the restrictions are violated, the method
 * must throw an {@code IllegalArgumentException}.
 * 
 * @version $Id$
 */
public interface MonitorAdmin {

    /**
     * Returns a {@code StatusVariable} addressed by its full path. 
     * The entity which queries a {@code StatusVariable} needs to hold
     * {@code MonitorPermission} for the given target with the
     * {@code read} action present.
     * 
     * @param path the full path of the {@code StatusVariable} in
     *        [Monitorable_ID]/[StatusVariable_ID] format
     * @return the {@code StatusVariable} object
     * @throws java.lang.IllegalArgumentException if {@code path} is
     *         {@code null} or otherwise invalid, or points to a
     *         non-existing {@code StatusVariable}
     * @throws java.lang.SecurityException if the caller does not hold a
     *         {@code MonitorPermission} for the
     *         {@code StatusVariable} specified by {@code path}
     *         with the {@code read} action present
     */
    public StatusVariable getStatusVariable(String path)
            throws IllegalArgumentException, SecurityException;

    /**
     * Returns the names of the {@code Monitorable} services that are
     * currently registered. The {@code Monitorable} instances are not
     * accessible through the {@code MonitorAdmin}, so that requests to
     * individual status variables can be filtered with respect to the
     * publishing rights of the {@code Monitorable} and the reading
     * rights of the caller.
     * <p>
     * The returned array contains the names in alphabetical order. It cannot be
     * {@code null}, an empty array is returned if no
     * {@code Monitorable} services are registered.
     * 
     * @return the array of {@code Monitorable} names
     */
    public String[] getMonitorableNames();

    /**
     * Returns the {@code StatusVariable} objects published by a
     * {@code Monitorable} instance. The {@code StatusVariables}
     * will hold the values taken at the time of this method call. Only those
     * status variables are returned where the following two conditions are met:
     * <ul>
     * <li>the specified {@code Monitorable} holds a
     * {@code MonitorPermission} for the status variable with the
     * {@code publish} action present
     * <li>the caller holds a {@code MonitorPermission} for the status
     * variable with the {@code read} action present
     * </ul>
     * All other status variables are silently ignored, they are omitted from
     * the result.
     * <p>
     * The elements in the returned array are in no particular order. The return
     * value cannot be {@code null}, an empty array is returned if no 
     * (authorized and readable) Status Variables are provided by the given 
     * {@code Monitorable}.
     * 
     * @param monitorableId the identifier of a {@code Monitorable}
     *        instance
     * @return a list of {@code StatusVariable} objects published
     *         by the specified {@code Monitorable}
     * @throws java.lang.IllegalArgumentException if {@code monitorableId}
     *         is {@code null} or otherwise invalid, or points to a
     *         non-existing {@code Monitorable}
     */
    public StatusVariable[] getStatusVariables(String monitorableId)
            throws IllegalArgumentException;

    /**
     * Returns the list of {@code StatusVariable} names published by a
     * {@code Monitorable} instance. Only those status variables are
     * listed where the following two conditions are met:
     * <ul>
     * <li>the specified {@code Monitorable} holds a
     * {@code MonitorPermission} for the status variable with the
     * {@code publish} action present
     * <li>the caller holds a {@code MonitorPermission} for
     * the status variable with the {@code read} action present
     * </ul>
     * All other status variables are silently ignored, their names are omitted
     * from the list.
     * <p>
     * The returned array does not contain duplicates, and the elements are in 
     * alphabetical order. It cannot be {@code null}, an empty array is 
     * returned if no (authorized and readable) Status Variables are provided 
     * by the given {@code Monitorable}.
     * 
     * @param monitorableId the identifier of a {@code Monitorable}
     *        instance
     * @return a list of {@code StatusVariable} objects names
     *         published by the specified {@code Monitorable}
     * @throws java.lang.IllegalArgumentException if {@code monitorableId}
     *         is {@code null} or otherwise invalid, or points to a
     *         non-existing {@code Monitorable}
     */
    public String[] getStatusVariableNames(String monitorableId)
            throws IllegalArgumentException;

    /**
     * Switches event sending on or off for the specified 
     * {@code StatusVariable}s. When the {@code MonitorAdmin} is
     * notified about a {@code StatusVariable} being updated it sends an
     * event unless this feature is switched off. Note that events within a
     * monitoring job can not be switched off. The event sending state of the 
     * {@code StatusVariables} must not be persistently stored. When a 
     * {@code StatusVariable} is registered for the first time in a 
     * framework session, its event sending state is set to ON by default.
     * <p>
     * Usage of the "*" wildcard is allowed in the path argument of this method
     * as a convenience feature. The wildcard can be used in either or both path
     * fragments, but only at the end of the fragments.  The semantics of the 
     * wildcard is that it stands for any matching {@code StatusVariable} 
     * at the time of the method call, it does not affect the event sending 
     * status of {@code StatusVariable}s which are not yet registered. As 
     * an example, when the {@code switchEvents("MyMonitorable/*", false)}
     * method is executed, event sending from all {@code StatusVariables}
     * of the MyMonitorable service are switched off. However, if the
     * MyMonitorable service starts to publish a new {@code StatusVariable}
     * later, it's event sending status is on by default.
     * 
     * @param path the identifier of the {@code StatusVariable}(s) in
     *        [Monitorable_id]/[StatusVariable_id] format, possibly with the 
     *        "*" wildcard at the end of either path fragment
     * @param on {@code false} if event sending should be switched off, 
     *        {@code true} if it should be switched on for the given path
     * @throws java.lang.SecurityException if the caller does not hold
     *         {@code MonitorPermission} with the
     *         {@code switchevents} action or if there is any
     *         {@code StatusVariable} in the {@code path} field for
     *         which it is not allowed to switch event sending on or off as per 
     *         the target field of the permission
     * @throws java.lang.IllegalArgumentException if {@code path} is 
     *         {@code null} or otherwise invalid, or points to a 
     *         non-existing {@code StatusVariable}
     */
    public void switchEvents(String path, boolean on)
        throws IllegalArgumentException, SecurityException;
    
    /**
     * Issues a request to reset a given {@code StatusVariable}.
     * Depending on the semantics of the {@code StatusVariable} this call
     * may or may not succeed: it makes sense to reset a counter to its starting
     * value, but e.g. a {@code StatusVariable} of type String might not
     * have a meaningful default value. Note that for numeric
     * {@code StatusVariable}s the starting value may not necessarily be
     * 0. Resetting a {@code StatusVariable} triggers a monitor event if
     * the {@code StatusVariable} supports update notifications.
     * <p>
     * The entity that wants to reset the {@code StatusVariable} needs to
     * hold {@code MonitorPermission} with the {@code reset}
     * action present. The target field of the permission must match the
     * {@code StatusVariable} name to be reset.
     * 
     * @param path the identifier of the {@code StatusVariable} in
     *        [Monitorable_id]/[StatusVariable_id] format
     * @return {@code true} if the {@code Monitorable} could
     *         successfully reset the given {@code StatusVariable},
     *         {@code false} otherwise
     * @throws java.lang.IllegalArgumentException if {@code path} is 
     *         {@code null} or otherwise invalid, or points to a 
     *         non-existing {@code StatusVariable}
     * @throws java.lang.SecurityException if the caller does not hold
     *         {@code MonitorPermission} with the {@code reset}
     *         action or if the specified {@code StatusVariable} is not
     *         allowed to be reset as per the target field of the permission
     */
    public boolean resetStatusVariable(String path)
            throws IllegalArgumentException, SecurityException;
    
    /**
     * Returns a human readable description of the given 
     * {@code StatusVariable}. The {@code null} value may be returned
     * if there is no description for the given {@code StatusVariable}.
     * <p>
     * The entity that queries a {@code StatusVariable} needs to hold
     * {@code MonitorPermission} for the given target with the
     * {@code read} action present.
     * 
     * @param path the full path of the {@code StatusVariable} in
     *        [Monitorable_ID]/[StatusVariable_ID] format
     * @return the human readable description of this
     *         {@code StatusVariable} or {@code null} if it is not
     *         set
     * @throws java.lang.IllegalArgumentException if {@code path} is 
     *         {@code null} or otherwise invalid, or points to a 
     *         non-existing {@code StatusVariable}
     * @throws java.lang.SecurityException if the caller does not hold a
     *         {@code MonitorPermission} for the
     *         {@code StatusVariable} specified by {@code path}
     *         with the {@code read} action present
     */
    public String getDescription(String path) 
            throws IllegalArgumentException, SecurityException;

    /**
     * Starts a time based {@code MonitoringJob} with the parameters
     * provided. Monitoring events will be sent according to the specified
     * schedule. All specified {@code StatusVariable}s must exist when the
     * job is started. The initiator string is used in the
     * {@code mon.listener.id} field of all events triggered by the job,
     * to allow filtering the events based on the initiator.
     * <p>
     * The {@code schedule} parameter specifies the time in seconds 
     * between two measurements, it must be greater than 0.  The first 
     * measurement will be taken when the timer expires for the first time, not 
     * when this method is called.
     * <p>
     * The {@code count} parameter defines the number of measurements to be
     * taken, and must either be a positive integer, or 0 if the measurement is
     * to run until explicitly stopped.
     * <p>
     * The entity which initiates a {@code MonitoringJob} needs to hold
     * {@code MonitorPermission} for all the specified target
     * {@code StatusVariable}s with the {@code startjob} action
     * present. If the permission's action string specifies a minimal sampling
     * interval then the {@code schedule} parameter should be at least as
     * great as the value in the action string.
     * 
     * @param initiator the identifier of the entity that initiated the job
     * @param statusVariables the list of {@code StatusVariable}s to be
     *        monitored, with each {@code StatusVariable} name given in
     *        [Monitorable_PID]/[StatusVariable_ID] format
     * @param schedule the time in seconds between two measurements
     * @param count the number of measurements to be taken, or 0 for the
     *        measurement to run until explicitly stopped
     * @return the successfully started job object, cannot be {@code null}
     * @throws java.lang.IllegalArgumentException if the list of
     *         {@code StatusVariable} names contains an invalid or 
     *         non-existing {@code StatusVariable}; if 
     *         {@code initiator} is {@code null} or empty; or if the 
     *         {@code schedule} or {@code count} parameters are 
     *         invalid
     * @throws java.lang.SecurityException if the caller does not hold
     *         {@code MonitorPermission} for all the specified
     *         {@code StatusVariable}s, with the {@code startjob}
     *         action present, or if the permission does not allow starting the
     *         job with the given frequency
     */
    public MonitoringJob startScheduledJob(String initiator,
            String[] statusVariables, int schedule, int count)
            throws IllegalArgumentException, SecurityException;

    /**
     * Starts a change based {@code MonitoringJob} with the parameters
     * provided. Monitoring events will be sent when the
     * {@code StatusVariable}s of this job are updated. All specified
     * {@code StatusVariable}s must exist when the job is started, and
     * all must support update notifications. The initiator string is used in
     * the {@code mon.listener.id} field of all events triggered by the
     * job, to allow filtering the events based on the initiator.
     * <p>
     * The {@code count} parameter specifies the number of changes that
     * must happen to a {@code StatusVariable} before a new notification is
     * sent, this must be a positive integer.
     * <p>
     * The entity which initiates a {@code MonitoringJob} needs to hold
     * {@code MonitorPermission} for all the specified target
     * {@code StatusVariable}s with the {@code startjob} action
     * present.
     * 
     * @param initiator the identifier of the entity that initiated the job
     * @param statusVariables the list of {@code StatusVariable}s to be
     *        monitored, with each {@code StatusVariable} name given in
     *        [Monitorable_PID]/[StatusVariable_ID] format
     * @param count the number of changes that must happen to a 
     *        {@code StatusVariable} before a new notification is sent
     * @return the successfully started job object, cannot be {@code null}
     * @throws java.lang.IllegalArgumentException if the list of
     *         {@code StatusVariable} names contains an invalid or 
     *         non-existing {@code StatusVariable}, or one that does not 
     *         support notifications; if the {@code initiator} is 
     *         {@code null} or empty; or if {@code count} is invalid
     * @throws java.lang.SecurityException if the caller does not hold
     *         {@code MonitorPermission} for all the specified
     *         {@code StatusVariable}s, with the {@code startjob}
     *         action present
     */
    public MonitoringJob startJob(String initiator, String[] statusVariables,
            int count) throws IllegalArgumentException, SecurityException;

    /**
     * Returns the list of currently running {@code MonitoringJob}s.
     * Jobs are only visible to callers that have the necessary permissions: to 
     * receive a Monitoring Job in the returned list, the caller must hold all 
     * permissions required for starting the job.  This means that if the caller
     * does not have {@code MonitorPermission} with the proper
     * {@code startjob} action for all the Status Variables monitored by a 
     * job, then that job will be silently omitted from the results.
     * <p>
     * The returned array cannot be {@code null}, an empty array is
     * returned if there are no running jobs visible to the caller at the time 
     * of the call.
     * 
     * @return the list of running jobs visible to the caller
     */
    public MonitoringJob[] getRunningJobs();
}
