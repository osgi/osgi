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

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import org.osgi.service.dmt.DmtAlertSender;
import org.osgi.service.dmt.DmtAlertItem;
import org.osgi.service.dmt.DmtException;

import org.osgi.service.event.EventChannel;
import org.osgi.service.event.ChannelEvent;

import org.osgi.service.monitor.*;

import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the Monitor Admin service.  Monitoring jobs can be
 * manipulated through the MonitorAdmin interface, event update
 * notifications are received through the UpdateListener interface.
 * <p>
 * The list of active jobs (MonitoringJob objects) is stored in a
 * vector.  Time-based jobs start a timer thread when created, and
 * call the <code>scheduledUpdate</code> method each time the timer
 * expires.  Change-based jobs are checked at each KPI change (when
 * the <code>updated</code> method is called).  When a job stops or is
 * stopped, it calles the <code>jobStopped</code> method to notify the
 * Monitor Admin that the job is no longer active.
 * <p>
 * When the monitoring client has to be notified, the following action
 * is taken depending on whether the job was started locally or
 * remotely.  For local jobs, an event is created and sent to the
 * monitoring topic (with change-induced events only one event is sent
 * for all interested listeners); for remote jobs, an alert is
 * assembled and sent (through the DMT Admin) to the initiator of the
 * job.
 */
public class MonitorAdminImpl implements MonitorAdmin, UpdateListener {
    private static final String MONITOR_EVENT_TOPIC = "org.osgi.service.monitor.MonitorEvent";
    private static final int MONITORING_ALERT_CODE = 1226;

    private BundleContext bc;
    private ServiceTracker tracker;
    private EventChannel eventChannel;
    private DmtAlertSender alertSender;
    private Vector jobs;

    public MonitorAdminImpl(BundleContext bc, ServiceTracker tracker, EventChannel eventChannel, DmtAlertSender alertSender) {
        this.bc = bc;
        this.tracker = tracker;
        this.eventChannel = eventChannel;
        this.alertSender = alertSender;

        jobs = new Vector();
    }

    public KPI getKPI(String path) throws IllegalArgumentException {
        // TODO check caller permissions, check publisher permissions

        return trustedGetKpi(path);
    }

    public MonitoringJob startJob(String initiator, String[] kpiNames, int schedule, int count) 
        throws IllegalArgumentException {

        // TODO check caller permissions, check publisher permissions

        return startJob(initiator, kpiNames, schedule, count, true);
    }

    public synchronized MonitoringJob[] getRunningJobs() {
        return (MonitoringJob[]) jobs.toArray(new MonitoringJob[jobs.size()]);
    }

    public Monitorable[] getMonitorables() {
        // TODO check caller permissions, check publisher permissions
    	Vector monitorables = new Vector();
        ServiceReference[] monitorableRefs = tracker.getServiceReferences();

        if(monitorableRefs == null)
            return null;
        
        for(int i = 0; i < monitorableRefs.length; i++) {
            // TODO should this check be done here?
            String servicePid = (String) monitorableRefs[i].getProperty("service.pid");
            Monitorable monitorable = (Monitorable) tracker.getService(monitorableRefs[i]);
            if(servicePid != null && monitorable != null)
                monitorables.add(monitorable);
        }
        
        int size = monitorables.size();
        return size == 0 ? null : 
            (Monitorable[]) monitorables.toArray(new Monitorable[size]);
    }

    public Monitorable getMonitorable(String id) throws IllegalArgumentException {
        // TODO check caller permissions, check publisher permissions
        return trustedGetMonitorable(id);
    }

    public synchronized void updated(KPI kpi) {
        sendEvent(kpi, null);

        Vector listeners = new Vector();
        Vector remoteJobs = new Vector();

        Iterator i = jobs.iterator();
        while(i.hasNext()) {
            MonitoringJobImpl job = (MonitoringJobImpl) i.next();
            if(job.isChangeBased() && job.isNthCall(kpi.getPath())) {
                if(job.isLocal())
                    listeners.add(job.getInitiator());
                else
                    remoteJobs.add(job);
            }
        }

        int matchNum = listeners.size();
        if(matchNum == 1)
            sendEvent(kpi, listeners.firstElement());
        else if(matchNum > 1)
            sendEvent(kpi, (String[]) listeners.toArray(new String[matchNum]));

        Iterator j = remoteJobs.iterator();
        while(j.hasNext()) {
            MonitoringJob job = (MonitoringJob) j.next();
            sendAlert(getValidKpis(job.getKpiNames()), job.getInitiator());
        }
    }

    private void sendEvent(KPI kpi, Object initiators) {
        Hashtable properties = new Hashtable();
        properties.put("monitorable.pid", getId(kpi.getPath()));
        properties.put("kpi.name", kpi.getID());
        if(initiators != null)
            properties.put("listener.id", initiators);
        ChannelEvent event = new ChannelEvent(MONITOR_EVENT_TOPIC, properties);
        eventChannel.postEvent(event);
    }

    private void sendAlert(List kpis, String initiator) {
        DmtAlertItem[] items = new DmtAlertItem[kpis.size()];
        
        Iterator iterator = kpis.iterator();
        for(int i = 0; iterator.hasNext(); i++) {
			KPI kpi = (KPI) iterator.next();
			items[i] = createAlertItem(kpi);
        }

        try {
            alertSender.sendAlert(initiator, MONITORING_ALERT_CODE, items);
        } catch(DmtException e) {
            // TODO what to do here?    (codes: ALERT_NOT_ROUTED, REMOTE_ERROR)
            System.out.println("MonitorAdmin: error delivering alert:");
            e.printStackTrace(System.out);
        }
    }
    
    private DmtAlertItem createAlertItem(KPI kpi) {
        String data = null;

        switch(kpi.getType()) {
        case KPI.TYPE_STRING:  data = kpi.getString();       break;
        case KPI.TYPE_INTEGER: data = "" + kpi.getInteger(); break;
        case KPI.TYPE_FLOAT:   data = "" + kpi.getFloat();   break;
        case KPI.TYPE_OBJECT:  data = "" + kpi.getObject();  break;
        }

        return new DmtAlertItem(Activator.PLUGIN_ROOT + "/" + kpi.getPath(),
        		                "x-oma-trap:" + kpi.getPath(),
                                "chr", // TODO send result in XML format
                                null, data);
    }

    synchronized MonitoringJob startJob(String initiator, String[] kpiNames, int schedule, int count, boolean local)
        throws IllegalArgumentException {

        if(initiator == null || kpiNames.length == 0)
            throw new IllegalArgumentException("Initiator ID (event listener ID) is null or empty.");

        if(kpiNames == null || kpiNames.length == 0)
            throw new IllegalArgumentException("KPI name list is null or empty.");

        if(schedule < 0)
            throw new IllegalArgumentException("Invalid schedule '" + schedule + "', value must be non-negative.");

        if(count < 0)
            throw new IllegalArgumentException("Invalid report count '" + count + "', value must be non-negative.");

        if(schedule == 0 && count == 0)
            throw new IllegalArgumentException("Count parameter cannot be 0 in case of changed-based monitoring.");

        // Check that all paths point to valid KPIs
        // TODO make this more efficient: collect KPIs from the same Monitorable, iterate through registered Ms only once
        for(int i = 0; i < kpiNames.length; i++)
            trustedGetKpi(kpiNames[i]);

        MonitoringJobImpl job = new MonitoringJobImpl(this, initiator, kpiNames, schedule, count, local);

        jobs.add(job);

        return job;
    }

    synchronized void jobStopped(MonitoringJob job) {
        jobs.remove(job);
    }

    synchronized void scheduledUpdate(String[] paths, MonitoringJob job) {
        String initiator = job.getInitiator();
        Vector kpis = getValidKpis(paths);
        
        if(job.isLocal()) {
        	Iterator i = kpis.iterator();
            while (i.hasNext())
				sendEvent((KPI) i.next(), initiator);
        } else
            sendAlert(kpis, initiator);
    }

    private static String getId(String path) {
        int pos = path.indexOf('/');
        if(pos < 0)
            throw new IllegalArgumentException(
                "Path '" + path + "' invalid, should have the form '<Monitorable ID>/<KPI name>'");

        return path.substring(0, pos);
    }

    // does not check that 'path' contains a '/'; if not, returns 'path' itself
    private static String getKpiName(String path) {
        return path.substring(path.indexOf('/') + 1);
    }

    private Vector getValidKpis(String[] paths) {
        Vector kpis = new Vector();
        for (int i = 0; i < paths.length; i++) {
            try {
                kpis.add(trustedGetKpi(paths[i]));
            } catch(IllegalArgumentException e) {
                // Ignore KPIs that are (temporarily) unavailable
            }           
        }
        return kpis;
    }

    private KPI trustedGetKpi(String path) throws IllegalArgumentException {
        if(path == null)
            throw new IllegalArgumentException("Path argument is null.");

        KPI kpi = trustedGetMonitorable(getId(path)).getKpi(getKpiName(path));
        if(kpi == null)
        	throw new IllegalArgumentException("KPI '" + getKpiName(path) + 
                    "' not found in Monitorable '" + getId(path) + "'.");
        return kpi;
    }
    
    private Monitorable trustedGetMonitorable(String id) throws IllegalArgumentException {
    	if(id == null)
            throw new IllegalArgumentException("Monitorable ID argument is null.");
        
        // TODO check ID for filter-illegal characters
        ServiceReference[] refs;
        try {
            refs = bc.getServiceReferences(Monitorable.class.getName(), "(service.pid=" + id + ")");
        } catch(InvalidSyntaxException e) {
            // should not be reached if the above TODO is done
            throw new IllegalArgumentException("Invalid characters in Monitorable ID '" + id + 
                                               "': ." + e.getMessage());
        }

        if(refs == null)
            throw new IllegalArgumentException("Monitorable id '" + id + "' not found (no matching service registered).");

        // TODO is it allowed to give the tracker a reference that was not given by it?
        Monitorable monitorable = (Monitorable) tracker.getService(refs[0]);
        if(monitorable == null)
            throw new IllegalArgumentException("Monitorable id '" + id + "' not found (monitorable no longer registered.");

        return monitorable;
    }
}
