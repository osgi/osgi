/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.monitor;

import java.util.Arrays;

import org.osgi.service.monitor.MonitoringJob;

/** 
 * Stores information about a monitoring job.  Can be used to
 * represent local or remote, and time or change based jobs.
 */
public class MonitoringJobImpl implements MonitoringJob, Runnable {
    private MonitorAdminImpl monitorAdmin;

    private String initiator;
    private String[] kpiNames;
    private int reportCount;
    private long schedule;

    private boolean local;
    private boolean running;

    // The subset of kpiNames that is monitored.  This differs from kpiNames
    // only in case of remote monitoring jobs with trap references.
    private String[] monitoredKpiNames;
    
    // For change-based jobs, to count events for each monitored KPI.
    private int[] callCounters;

    MonitoringJobImpl(MonitorAdminImpl monitorAdmin, String initiator, 
                      String[] kpiNames, long schedule, int reportCount, 
                      boolean local) {

        this.monitorAdmin = monitorAdmin;

        this.initiator = initiator;
        this.kpiNames = kpiNames;
        this.schedule = schedule;
        this.reportCount = reportCount;

        this.local = local;
        
        if(local || kpiNames.length == 1)
        	monitoredKpiNames = kpiNames;
        else // the first KPI is to be monitored, the rest are only references
            monitoredKpiNames = new String[] { kpiNames[0] };

        running = true;

        if(isChangeBased()) {
            callCounters = new int[monitoredKpiNames.length];
            Arrays.fill(callCounters, 0);
        } else                  // timer based
            (new Thread(this)).start();
    }

    boolean isChangeBased() {
        return schedule == 0;
    }

    // returns true if kpiName is monitored by this job AND
    // this method has been called 'reportCount' times for this kpi
    boolean isNthCall(String kpiName) {
        if(!isChangeBased())
            throw new IllegalStateException(
                    "isNthCall() can only be called for change-based jobs.");
        
        int i = Arrays.asList(monitoredKpiNames).indexOf(kpiName);
        if(i < 0)
            return false;

        callCounters[i] = (callCounters[i] + 1) % reportCount;
        return callCounters[i] == 0;
    }

    public synchronized void stop() {
        if(running) {
            monitorAdmin.jobStopped(this);
            running = false;
        }
    }

    public String getInitiator() {
        return initiator;
    }

    public String[] getKpiNames() {
        return (String[]) kpiNames.clone();
    }

    public long getSchedule() {
        return schedule;
    }

    public int getReportCount() {
        return reportCount;
    }

    public boolean isLocal() {
        return local;
    }

    public void run() {
        if(isChangeBased())
            throw new IllegalStateException("Cannot run change-based job!");

        sleep();

        while(running) {
            monitorAdmin.scheduledUpdate(kpiNames, this);
            if(reportCount > 0) {
                reportCount--;
                if(reportCount == 0)
                    stop();
            }

            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(schedule*1000);
        } catch(InterruptedException e) {}
    }
}
