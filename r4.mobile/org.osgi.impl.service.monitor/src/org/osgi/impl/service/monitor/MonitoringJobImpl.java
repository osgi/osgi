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
    private Path[] varPaths;
    private int reportCount;
    private int schedule;

    private boolean local;
    private boolean running;

    // The subset of varPaths that is monitored.  This differs from varPaths
    // only in case of remote monitoring jobs with trap references.
    private Path[] monitoredVarPaths;
    
    // For change-based jobs, to count events for each monitored status var.
    private int[] callCounters;
    
    private String infoString;

    MonitoringJobImpl(MonitorAdminImpl monitorAdmin, String initiator, 
                      Path[] varPaths, int schedule, int reportCount, 
                      boolean local) {

        this.monitorAdmin = monitorAdmin;

        this.initiator = initiator;
        this.varPaths = varPaths;
        this.schedule = schedule;
        this.reportCount = reportCount;

        this.local = local;
        
        if(local || varPaths.length == 1)
        	monitoredVarPaths = varPaths;
        else // the first var. is to be monitored, the rest are only references
            monitoredVarPaths = new Path[] { varPaths[0] };

        infoString = null;
        running = true;

        if(isChangeBased()) {
            callCounters = new int[monitoredVarPaths.length];
            Arrays.fill(callCounters, 0);
        } else // time based
            (new Thread(this)).start();
    }

    boolean isChangeBased() {
        return schedule == 0;
    }

    // returns true if varName is monitored by this job AND
    // this method has been called 'reportCount' times for this status variable
    boolean isNthCall(Path varName) {
        if(!isChangeBased())
            throw new IllegalStateException(
                    "isNthCall() can only be called for change-based jobs.");
        
        int i = Arrays.asList(monitoredVarPaths).indexOf(varName);
        if(i < 0)
            return false;

        callCounters[i] = (callCounters[i] + 1) % reportCount;
        return callCounters[i] == 0;
    }
    
    Path[] getStatusVariablePaths() {
        return varPaths;
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

    public String[] getStatusVariableNames() {
        String[] names = new String[varPaths.length];
        for (int i = 0; i < varPaths.length; i++)
            names[i] = varPaths[i].toString();

        return names;
    }

    public int getSchedule() {
        return schedule;
    }

    public int getReportCount() {
        return reportCount;
    }

    public boolean isLocal() {
        return local;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public String toString() {
        if(infoString == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("MonitoringJob(").append(initiator).append(", ");
            sb.append(Arrays.asList(varPaths).toString()).append(", ");
            if(isChangeBased())
                sb.append("change:").append(reportCount);
            else {
                sb.append("scheduled:");
                if(reportCount != 0)
                    sb.append(reportCount).append('x');
                sb.append(schedule).append("sec");
            }
            sb.append(", ").append(local ? "local" : "remote").append(", ");
            sb.append(running ? "running" : "stopped").append(")");
            
            infoString = sb.toString();
        }
        
        return infoString;
    }

    public void run() {
        if(isChangeBased())
            throw new IllegalStateException("Cannot run change-based job!");

        sleep();

        while(running) {
            monitorAdmin.scheduledUpdate(varPaths, this);
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
