package org.osgi.impl.service.monitor;

import java.util.*;
import org.osgi.service.event.*;
import org.osgi.service.monitor.*;

public class MonitoringJobImpl extends Thread implements MonitoringJob {
    private static final String MONITOR_EVENT_TOPIC = "org.osgi.service.monitor.MonitorEvent";

    private MonitorAdminImpl monitorAdmin;
    private EventChannel eventChannel;

    private String initiator;
    private Hashtable kpis;
    private int reportCount;
    private long schedule;

    private boolean running;

    MonitoringJobImpl(MonitorAdminImpl monitorAdmin, EventChannel eventChannel,
                      String initiator, Hashtable kpis, long schedule, 
                      int reportCount) {

        super("MonitoringJob");

        this.monitorAdmin = monitorAdmin;
        this.eventChannel = eventChannel;

        this.initiator = initiator;
        this.kpis = kpis;
        this.schedule = schedule;
        this.reportCount = reportCount;

        running = true;
    }

    boolean isRunning() {
        return running;
    }

   // Is final in JRE
//    public synchronized void stop() {
//        if(running)
//            running = false;
//    }
        
    public String getInitiator() {
        return initiator;
    }
        
    public String[] getKpiNames() {
        return (String[]) kpis.keySet().toArray(new String [0]);
    }
        
    public long getSchedule() {
        return schedule;
    }
        
    public int getReportCount() {
        return reportCount;
    }

    public void run() {
        // TODO generate events
        // TODO what to do with invalid KPI names?

        while(running) {
            try {
                sleep(schedule*60000);
            } catch(InterruptedException e) {
                if(!running)
                    break;
            }
            
            Iterator i = kpis.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                if(!updateKpi((String) entry.getKey(), (KPI) entry.getValue()))
                    i.remove();
            }
            
            if(reportCount > 0) {
                reportCount--;
                if(reportCount == 0)
                    stop();
            }
        }

    }

    // returns true if update was successful, false if the key was not found
    private boolean updateKpi(String path, KPI oldKpi) {

        KPI newKpi = monitorAdmin.trustedGetKpi(path);

        if(newKpi == null || !oldKpi.equals(newKpi)) {
            Map properties = new Hashtable();
            properties.put("monitorable.pid", MonitorAdminImpl.getId(path));
            properties.put("kpi.name", oldKpi.getID());
            ChannelEvent event = new ChannelEvent(MONITOR_EVENT_TOPIC, properties);
            eventChannel.postEvent(event);
        }

        return newKpi != null;
    }
}


