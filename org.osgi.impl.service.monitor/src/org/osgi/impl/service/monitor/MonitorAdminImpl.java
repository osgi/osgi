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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.dmt.*;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.monitor.*;
import org.osgi.util.tracker.ServiceTracker;

// TODO add event parameter name constants, if they are used in other parts of the spec
// TODO remove all remains of Object var type, if there are no objections
/**
 * Provides the Monitor Admin service. Monitoring jobs can be manipulated
 * through the MonitorAdmin interface, event update notifications are received
 * through the MonitorListener interface.
 * <p>
 * The list of active jobs (MonitoringJob objects) is stored in a vector.
 * Time-based jobs start a timer thread when created, and call the
 * <code>scheduledUpdate</code> method each time the timer expires.
 * Change-based jobs are checked at each StatusVariable change (when the
 * <code>updated</code> method is called). When a job stops or is stopped, it
 * calles the <code>jobStopped</code> method to notify the Monitor Admin that
 * the job is no longer active.
 * <p>
 * When the monitoring client has to be notified, the following action is taken
 * depending on whether the job was started locally or remotely. For local jobs,
 * an event is created and sent to the monitoring topic (with change-induced
 * events only one event is sent for all interested listeners); for remote jobs,
 * an alert is assembled and sent (through the DMT Admin) to the initiator of
 * the job.
 */
public class MonitorAdminImpl implements MonitorAdmin, MonitorListener {
    private static final String MONITOR_EVENT_TOPIC = "org/osgi/service/monitor/MonitorEvent";
    private static final int MONITORING_ALERT_CODE = 1226;
    
    // TODO update format for sending alarms
    private static final MessageFormat kpiTag = 
        new MessageFormat("<kpi type=\"{0}\" cardinality=\"{1}\">\n{2}</kpi>");
    private static final MessageFormat valueTag =
        new MessageFormat("    <value>{0}</value>\n");

    private BundleContext bc;
    private ServiceTracker tracker;
    private EventAdmin eventChannel;
    private DmtAdmin alertSender;
    private Vector jobs;
    
    private Set quietVars; // no automatic events for the listed status vars

    MonitorAdminImpl(BundleContext bc, ServiceTracker tracker, 
            EventAdmin eventChannel, DmtAdmin alertSender) {
        this.bc = bc;
        this.tracker = tracker;
        this.eventChannel = eventChannel;
        this.alertSender = alertSender;

        jobs = new Vector();
        
        quietVars = new HashSet();
    }

    public StatusVariable getStatusVariable(String pathStr)
            throws IllegalArgumentException {
        // TODO check publisher permissions
        Path path = Path.getPath(pathStr);
        checkPermission(pathStr, MonitorPermission.READ);
        
        return trustedGetStatusVariable(path);
    }

    public String[] getStatusVariableNames(String monitorableId) {
        // TODO check publisher permissions
        
        checkString(monitorableId, "Monitorable ID");
        checkPermission(monitorableId + "/*", MonitorPermission.DISCOVER);
        
        Monitorable monitorable = trustedGetMonitorable(monitorableId);
        String[] varNames = monitorable.getStatusVariableNames();
        Arrays.sort(varNames);
        return varNames;
    }
    
    public StatusVariable[] getStatusVariables(String monitorableId) {
        // TODO check publisher permissions
        
        checkString(monitorableId, "Monitorable ID");
        // TODO change javadoc to request monitorableId/* READ permission 
        checkPermission(monitorableId + "/*", MonitorPermission.READ);
        
        Monitorable monitorable = trustedGetMonitorable(monitorableId);
        String[] varNames = monitorable.getStatusVariableNames();
        
        StatusVariable[] vars = new StatusVariable[varNames.length];
        for (int i = 0; i < vars.length; i++)
            vars[i] = monitorable.getStatusVariable(varNames[i]);

        return vars;
    }
    
    public boolean resetStatusVariable(String pathStr)
            throws IllegalArgumentException {
        // TODO check publisher permissions

        Path path = Path.getPath(pathStr);
        checkPermission(pathStr, MonitorPermission.RESET);
        
        Monitorable monitorable = trustedGetMonitorable(path.monId);
        return monitorable.resetStatusVariable(path.varId);
    }
    
    // If events are turned off for a path containing *, the list of actual
    // variables is generated at the time of the call, based on the currently
    // exported Status Variables.
    public void switchEvents(String pathStr, boolean on) {
        // publisher perms not checked, but this has no security implications

        Path path = Path.getPath(pathStr);
        checkPermission(pathStr, MonitorPermission.SWITCHEVENTS);
        
        if(on) 
            removeImpliedVariables(path);
        else 
            addImpliedVariables(path);
    }
    
    public MonitoringJob startJob(String initiator, String[] varNames, 
            int count) throws IllegalArgumentException {
        checkJobStartCommon(initiator, varNames);
        
        if(count <= 0)
            throw new IllegalArgumentException("Invalid report count '" + count + "', value must be positive.");

        return startJob(initiator, varNames, 0, count, true);
    }
    
    public MonitoringJob startScheduledJob(String initiator, String[] varNames, 
            int schedule, int count) throws IllegalArgumentException {
        checkJobStartCommon(initiator, varNames);

        if(schedule <= 0)
            throw new IllegalArgumentException("Invalid schedule '" + schedule + "', value must be positive.");

        if(count < 0)
            throw new IllegalArgumentException("Invalid report count '" + count + "', value must be non-negative.");

        return startJob(initiator, varNames, schedule, count, true);
    }
    
    public synchronized MonitoringJob[] getRunningJobs() {
        return (MonitoringJob[]) jobs.toArray(new MonitoringJob[jobs.size()]);
    }

    public String[] getMonitorableNames() {
        // TODO check publisher permissions
        
        checkPermission("*/*", MonitorPermission.DISCOVER);

        return trustedGetMonitorableNames();
    }

    public synchronized void updated(String monitorableId, StatusVariable var) {
        sendEvent(monitorableId, var, null);

        Vector listeners = new Vector();
        Vector remoteJobs = new Vector();

        String path = monitorableId + "/" + var.getID();
        Iterator i = jobs.iterator();
        while(i.hasNext()) {
            MonitoringJobImpl job = (MonitoringJobImpl) i.next();
            if(job.isChangeBased() && job.isNthCall(path)) {
                if(job.isLocal())
                    listeners.add(job.getInitiator());
                else
                    remoteJobs.add(job);
            }
        }

        int matchNum = listeners.size();
        if(matchNum == 1)
            sendEvent(monitorableId, var, listeners.firstElement());
        else if(matchNum > 1)
            sendEvent(monitorableId, var, 
                    (String[]) listeners.toArray(new String[matchNum]));

        Iterator j = remoteJobs.iterator();
        while(j.hasNext()) {
            MonitoringJob job = (MonitoringJob) j.next();
            sendAlert(job.getStatusVariableNames(), job.getInitiator());
        }
    }

    private void sendEvent(String monitorableId, StatusVariable var, 
            Object initiators) {
        
        Hashtable properties = new Hashtable();
        properties.put("mon.monitorable.pid", monitorableId);
        properties.put("mon.statusvariable.name", var.getID());
        properties.put("mon.statusvariable.value", getStatusVariableString(var));
        if(initiators != null)
            properties.put("mon.listener.id", initiators);
        Event event = new Event(MONITOR_EVENT_TOPIC, properties);
        eventChannel.postEvent(event);
    }

    private void sendAlert(String[] paths, String initiator) {
        List itemList = new Vector();
        for (int i = 0; i < paths.length; i++) {
            try {
                StatusVariable var = 
                    trustedGetStatusVariable(Path.getPathNoCheck(paths[i]));
                itemList.add(new DmtAlertItem(Activator.PLUGIN_ROOT + "/" + paths[i],
                        "x-oma-trap:" + paths[i], "xml", createXml(var)));
            } catch(IllegalArgumentException e) {
                // Ignore Status Variables that are (temporarily) unavailable                
            }
        }
        
        DmtAlertItem[] items = (DmtAlertItem[]) 
            itemList.toArray(new DmtAlertItem[itemList.size()]);

        try {
            alertSender.sendAlert(initiator, MONITORING_ALERT_CODE, items);
        } catch(DmtException e) {
            // TODO what to do here?    (codes: ALERT_NOT_ROUTED, REMOTE_ERROR)
            System.out.println("MonitorAdmin: error delivering alert:");
            e.printStackTrace(System.out);
        }
    }

    static String createXml(StatusVariable var) {
        String value = getStatusVariableString(var);
        
        int type = var.getType();
        switch(type) {
        case StatusVariable.TYPE_STRING:
            return createScalarXml("string",  value);
        case StatusVariable.TYPE_LONG: 
            return createScalarXml("long", value);
        case StatusVariable.TYPE_DOUBLE:
            return createScalarXml("double", value);
        case StatusVariable.TYPE_BOOLEAN:
            return createScalarXml("boolean", value);
//        case KPI.TYPE_OBJECT:  return createObjectXml(kpi.getObject());
        }
        
        throw new IllegalArgumentException("Unknown Status Variable type '" + 
                                           type + "'.");
    }

    private static String createScalarXml(String type, String data) {
        String result = kpiTag.format(new Object[] { type, "scalar", 
                                      valueTag.format(new Object[] { data })});
        return result;
    }

    /*
    private static String createObjectXml(Object data) {
        if(data.getClass().isArray())
            return createArrayXml(data);
        else if(data instanceof Vector) {
            Vector vector = (Vector) data;
            Object[] array;
            if(vector.size() == 0)
                array = new String[] {};
            else
                array = (Object[])
                    Array.newInstance(vector.firstElement().getClass(),
                                      vector.size());
            return createArrayXml(vector.toArray(array));
        }
        else
            return createScalarXml(data.getClass().getName().toLowerCase(), 
                                   data.toString());
    }

    private static String createArrayXml(Object array) {
        String type = array.getClass().getComponentType().getName().toLowerCase();

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < Array.getLength(array); i++)
            sb.append(valueTag.format(new Object[] { Array.get(array, i).toString() }));
        
        return kpiTag.format(new Object[] { type, "array", sb.toString() });
    }
    */

    private static String getStatusVariableString(StatusVariable var) {
        
        int type = var.getType(); 
        switch(type) {
        case StatusVariable.TYPE_BOOLEAN: return Boolean.toString(var.getBoolean());
        case StatusVariable.TYPE_DOUBLE:  return Double.toString(var.getDouble());
        case StatusVariable.TYPE_LONG:    return Long.toString(var.getLong());
        case StatusVariable.TYPE_STRING:  return var.getString();
        }
        
        // never reached
        throw new IllegalArgumentException("Unknown StatusVariable type '" +
                                           type + "'.");
    }
    
    private void addImpliedVariables(Path path) {
        if(path.monId.endsWith("*")) {
            String monIdPrefix = 
                path.monId.substring(0, path.monId.length()-1);
            String[] monNames = trustedGetMonitorableNames();
            for (int i = 0; i < monNames.length; i++) {
                if(monNames[i].startsWith(monIdPrefix))
                    addImpliedVariables(monNames[i], path.varId);
            }
        }
        else
            addImpliedVariables(path.monId, path.varId);
    }
    
    private void addImpliedVariables(String monId, String varId) {
        Monitorable monitorable = trustedGetMonitorable(monId);
        if(varId.endsWith("*")) {
            String varIdPrefix = varId.substring(0, varId.length()-1);
            String[] varNames = monitorable.getStatusVariableNames();
            for (int i = 0; i < varNames.length; i++)
                if(varNames[i].startsWith(varIdPrefix))
                    quietVars.add(new Path(monId, varNames[i]));
        }
        else
            monitorable.getStatusVariable(varId);
            quietVars.add(new Path(monId, varId));
    }

    private void removeImpliedVariables(Path path) {
        Iterator i = quietVars.iterator();
        while (i.hasNext()) {
            Path quietVar = (Path) i.next();
            if(implies(path, quietVar))
                i.remove();
        }
    }

    private boolean implies(Path pattern, Path entry) {
        return implies(pattern.monId, entry.monId) &&
            implies(pattern.varId, entry.varId);
    }

    private boolean implies(String pattern, String entry) {
        return pattern.endsWith("*")
                    ? entry.startsWith(pattern.substring(0, pattern.length()-1))
                    : entry.equals(pattern);
    }

    synchronized MonitoringJob startJob(String initiator, String[] varNames,
            int schedule, int count, boolean local) {
        // TODO check publisher permissions
        // OPTIMIZE collect status variables from the same Monitorable, iterate through registered Ms only once
        
        String reqPermission = MonitorPermission.STARTJOB;
        if(schedule != 0) // scheduled job
            reqPermission += ":" + schedule;

        // remove duplicates while leaving the first element in place
        Set varNameSet = new HashSet(Arrays.asList(varNames));
        if(varNameSet.size() < varNames.length) {
            String first = varNames[0];
            varNames = new String[varNameSet.size()];
            varNameSet.remove(first);
            varNames[0] = first;
            Iterator iter = varNameSet.iterator();
            for(int i = 1; iter.hasNext(); i++)
                varNames[i] = (String) iter.next();
        }
        
        for (int i = 0; i < varNames.length; i++) {
            Path path = Path.getPath(varNames[i]);
            checkPermission(varNames[i], reqPermission);
            Monitorable monitorable = trustedGetMonitorable(path.monId);

            // check that the status variable exists
            monitorable.getStatusVariable(path.varId);
            
            if(schedule == 0) // change-based job
                // check that the status variable supports change notification 
                if(!monitorable.notifiesOnChange(path.varId))
                    throw new IllegalArgumentException("Status variable '" + 
                            varNames[i] + "' does not support notifications.");
        }
        
        MonitoringJobImpl job = new MonitoringJobImpl(this, initiator, varNames, 
                schedule, count, local);

        jobs.add(job);

        return job;
    }
    
    synchronized void jobStopped(MonitoringJob job) {
        jobs.remove(job);
    }

    // called by MonitoringJob, paths are assumed to be checked previously
    synchronized void scheduledUpdate(String[] paths, MonitoringJob job) {
        String initiator = job.getInitiator();
        
        if(job.isLocal()) {
            for (int i = 0; i < paths.length; i++) {
                try {
                    Path path = Path.getPathNoCheck(paths[i]);
                    sendEvent(path.monId, trustedGetStatusVariable(path), 
                              initiator);
                } catch(IllegalArgumentException e) {
                    // Ignore status vars that are (temporarily) unavailable                    
                }
            }
        } else
            sendAlert(paths, initiator);
    }

    private StatusVariable trustedGetStatusVariable(Path path) {
        Monitorable monitorable = trustedGetMonitorable(path.monId);
        return monitorable.getStatusVariable(path.varId);        
    }

    private String[] trustedGetMonitorableNames() {
        // get the list of monitorables using our permissions, caller is already checked
        ServiceReference[] monitorableRefs = (ServiceReference[]) 
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return tracker.getServiceReferences();
                }
            });

        if(monitorableRefs == null)
            return null;
        
        Vector monitorableNames = new Vector();
        for(int i = 0; i < monitorableRefs.length; i++) {
            // TODO should this check be done here?
            String servicePid = (String) monitorableRefs[i].getProperty("service.pid");
            if(servicePid != null)
                monitorableNames.add(servicePid);
        }
        
        int size = monitorableNames.size();
        if(size == 0)
            return null;
        
        String[] names = (String[]) monitorableNames.toArray(new String[size]);
        Arrays.sort(names);
        return names;
    }
    
    
    /**
     * Checks whether there exists a Monitorable with the given ID. If there is
     * no such Monitorable, an exception is thrown. The caller does not need
     * ServicePermissions for this operation to succeed.
     * <p>
     * This is used by the MonitorPlugin where no specific status variable is
     * needed, only the information that a given Monitorable exists.
     * 
     * @param monitorableId the ID of the Monitorable to be retrieved, must be
     *        non-null and non-empty
     * @throws IllegalArgumentException if no Monitorable is registered with the
     *         given ID
     */
    void checkMonitorable(String monitorableId) throws IllegalArgumentException {
        trustedGetMonitorable(monitorableId);
    }

    /**
     * Retrieves the Monitorable registered with the specified ID (taken from
     * the "service.pid" property). The caller does not need ServicePermissions
     * for this operation to succeed.
     * 
     * @param monitorableId the ID of the Monitorable to be retrieved, must be
     *        non-null and non-empty
     * @return the Monitorable with the given ID
     * @throws IllegalArgumentException if no Monitorable is registered with the
     *         given ID
     */
    private Monitorable trustedGetMonitorable(final String monitorableId)
            throws IllegalArgumentException {
        return (Monitorable) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return privilegedGetMonitorable(monitorableId);
            }
        });
    }
    
    private Monitorable privilegedGetMonitorable(String monitorableId)
            throws IllegalArgumentException {
        ServiceReference[] refs = tracker.getServiceReferences();
        if(refs == null)
            throw new IllegalArgumentException("Monitorable id '" + monitorableId +
                    "' not found (no matching services registered)");

        for (int i = 0; i < refs.length; i++)
            if(monitorableId.equals(refs[i].getProperty("service.pid"))) {
                Monitorable monitorable = (Monitorable) tracker.getService(refs[i]);
                if(monitorable == null)
                    throw new IllegalArgumentException("Monitorable id '" + monitorableId + 
                            "' not found (monitorable no longer registered.");
                return monitorable; 
            }

        throw new IllegalArgumentException("Monitorable id '" + monitorableId + 
                "' not found (no matching service registered).");
    }
    

    private static void checkJobStartCommon(String initiator, String[] varNames) {
        checkString(initiator, "Initiator ID");
        if (varNames == null || varNames.length == 0)
            throw new IllegalArgumentException(
                    "Status variable name list is null or empty.");
    }
    
    private static void checkPermission(String target, String action) {
        SecurityManager sm = System.getSecurityManager();
        if(sm != null)
            sm.checkPermission(new MonitorPermission(target, action));
    }

    private static void checkString(String str, String paramName) {
        if(str == null || str.length() == 0)
            throw new IllegalArgumentException(
                    paramName + " parameter is null or empty.");
    }
}

// Utility class to parse and store Status Variable path entries
class Path {
    String monId;
    String varId;
    
    static Path getPath(String pathStr) {
        if(pathStr == null)
            throw new IllegalArgumentException("Path argument is null.");
        
        int pos = pathStr.indexOf('/');
        if(pos < 0)
            throw new IllegalArgumentException(
                    "Path '" + pathStr + "' invalid, should have the form " + 
                    "'<Monitorable ID>/<Status Variable ID>'");
        
        Path path = new Path(pathStr.substring(0, pos), 
                pathStr.substring(pos + 1));
        
        if(path.monId.length() == 0)
            throw new IllegalArgumentException("Monitorable ID empty.");
        if(path.varId.length() == 0)
            throw new IllegalArgumentException("Status Variable ID empty.");
        
        return path;
    }
    
    static Path getPathNoCheck(String pathStr) {
        int pos = pathStr.indexOf('/');
        
        return new Path(pathStr.substring(0, pos), 
                pathStr.substring(pos + 1));
    }

    Path(String monId, String varId) {
        this.monId = monId;
        this.varId = varId;
    }

    public boolean equals(Object o) {
        if(!(o instanceof Path))
            return false;
        
        return ((Path) o).monId.equals(monId) && ((Path) o).varId.equals(varId);
    }
    
    public int hashCode() {
        return monId.hashCode() ^ varId.hashCode();
    }
}
