/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
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
 */
package org.osgi.service.monitor;

/**
 * A Monitoring Job is a request for scheduled or event based notifications on
 * update of a set of StatusVariables. The job is a data structure that holds a
 * non-empty list of StatusVariable names, an identification of the initiator of
 * the job, and the sampling parameters. There are two kinds of monitoring jobs:
 * time based and change based. Time based jobs take samples of all
 * StatusVariables with a specified frequency. The number of samples to be taken
 * before the job finishes may be specified. Change based jobs are only
 * interested in the changes of the monitored StatusVariables. In this case, the
 * number of changes that must take place between two notifications can be
 * specified.
 * <p>
 * The job can be started on the MonitorAdmin interface. Running the job
 * (querying the StatusVariables, listening to changes, and sending out
 * notifications on updates) is the task of the MonitorAdmin implementation.
 * <p>
 * Whether a monitoring job keeps track dynamically of the StatusVariables it
 * monitors is not specified. This means that if we monitor a StatusVariable of
 * a Monitorable service which disappears and later reappears then it is
 * implementation specific whether we still receive updates of the
 * StatusVariable changes or not.
 */
public interface MonitoringJob {
    /**
     * Stops a Monitoring Job. Note that a time based job can also stop
     * automatically if the specified number of samples have been taken.
     */
    public void stop();

    /**
     * Returns the identitifier of the principal who initiated the job. This is
     * set at the time when startJob() is called at the MonitorAdmin interface.
     * This string holds the ServerID if the operation was initiated from a
     * remote manager, or an arbitrary ID of the initiator entity in the local
     * case (used for addressing notification events).
     * 
     * @return the ID of the initiator
     */
    public String getInitiator();

    /**
     * Returns the list of StatusVariable names that are the targets of this
     * measurement job. For time based jobs, the MonitorAdmin will iterate
     * through this list and query all StatusVariables when its timer set by the
     * job's frequency rate expires.
     * 
     * @return the target list of the measurement job in
     *         [Monitorable_ID]/[StatusVariable_ID] format
     */
    public String[] getStatusVariableNames();

    /**
     * Returns the delay (in seconds) between two samples. If this call returns
     * N (greater than 0) then the MonitorAdmin queries each StatusVariable that
     * belongs to this job every N seconds. The value 0 means that the job is
     * not scheduled but event based: in this case instant
     * notification on changes is requested (at every nth change of the value,
     * as specified by the report count parameter).
     * 
     * @return the delay (in seconds) between samples, or 0 for change based
     *         jobs
     */
    public long getSchedule();

    /**
     * Returns the number of times MonitorAdmin will query the StatusVariables
     * (for time based jobs), or the number of changes of a StatusVariable
     * between notifications (for change based jobs). Time based jobs with
     * non-zero report count will take getReportCount()*getSchedule() time to
     * finish. Time based jobs with 0 report count and change based jobs do not
     * stop automatically, but all jobs can be stopped with the {@link #stop} method.
     * 
     * @return the number of measurements to be taken, or the number of changes
     *         between notifications
     */
    public int getReportCount();

    /**
     * Returns whether the job was started locally or remotely.
     * 
     * @return <code>true</code> if the job was started from the local device,
     *         <code>false</code> if the job was initiated from a remote
     *         management server through the device management tree
     */
    public boolean isLocal();
}
