package org.osgi.impl.service.monitor;

import java.util.*;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventChannel;
import org.osgi.service.monitor.*;
import org.osgi.util.tracker.ServiceTracker;

public class MonitorAdminImpl implements MonitorAdmin {
    private ServiceTracker tracker;
    private EventChannel eventChannel;
    private Vector jobs;

    public MonitorAdminImpl(ServiceTracker tracker, EventChannel eventChannel) {
        this.tracker = tracker;
        this.eventChannel = eventChannel;
        
        jobs = new Vector();
    }

    public KPI getKPI(String path) throws IllegalArgumentException {
        // TODO check the rights of the caller

        return trustedGetKpi(path);
    }

    public synchronized MonitoringJob startJob(String[] kpiNames, 
                                               int schedule, int count) throws IllegalArgumentException {

        // TODO check the rights of the caller

        if(kpiNames == null || kpiNames.length == 0)
            throw new IllegalArgumentException("KPI name list is null or empty.");

        if(schedule <= 0)
            throw new IllegalArgumentException("Invalid schedule '" + schedule + "', value must be positive.");

        if(count < 0)
            throw new IllegalArgumentException("Invalid report count '" + count + "', value must be non-negative.");

        // TODO make this more efficient: collect KPIs from the same Monitorable, iterate through registered Ms only once
        Hashtable kpis = new Hashtable();
        for(int i = 0; i < kpiNames.length; i++)
            kpis.put(kpiNames[i], trustedGetKpi(kpiNames[i]));

        // TODO find out the ID of the job initiator somehow
        MonitoringJobImpl job = new MonitoringJobImpl(this, eventChannel, null, kpis, schedule, count);
        job.start();

        jobs.add(job);

        return job;
    }
	
    public synchronized MonitoringJob[] getRunningJobs() {
        Iterator i = jobs.iterator();
        while(i.hasNext())
            if(!((MonitoringJobImpl) i.next()).isRunning())
                i.remove();

        return (MonitoringJob[]) jobs.toArray(new MonitoringJob[jobs.size()]);
    }

    synchronized KPI trustedGetKpi(String path) throws IllegalArgumentException {
        if(path == null)
            throw new IllegalArgumentException("Path argument is null.");

        String id = getId(path);
        String kpiName = getKpiName(path);
        
        ServiceReference[] monitorables = tracker.getServiceReferences();

        if(monitorables == null)
            throw new IllegalArgumentException("Monitorable id '" + id + "' not found (no monitorables registered).");

        for(int i = 0; i < monitorables.length; i++) {
            String servicePid = (String) monitorables[i].getProperty("service.pid");
            if(servicePid == null) // TODO can this ever happen?
                throw new IllegalStateException("No service.pid property of Monitorable service.");
            if(servicePid.equals(id)) {
                Monitorable monitorable = (Monitorable) tracker.getService(monitorables[i]);
                if(monitorable == null)
                    throw new IllegalArgumentException(
                        "Monitorable id '" + id + "' not found (monitorable no longer registered.");
                KPI kpi = monitorable.getKpi(kpiName);
                if(kpi == null)
                    throw new IllegalArgumentException("KPI '" + kpiName + "' not found in Monitorable '" + id + "'.");
                return kpi;
            }
        }
        
        throw new IllegalArgumentException("Monitorable id '" + id + "' not found (no matching service registered).");
    }

    static String getId(String path) {
        int pos = path.indexOf('/');
        if(pos < 0)
            throw new IllegalArgumentException(
                "Path '" + path + "' invalid, should have the form '<Monitorable ID>/<KPI name>'");
            
        return path.substring(0, pos);
    }

    // does not check that 'path' contains a '/'; if not, returns 'path' itself
    static String getKpiName(String path) {
        return path.substring(path.indexOf('/') + 1);
    }
}
