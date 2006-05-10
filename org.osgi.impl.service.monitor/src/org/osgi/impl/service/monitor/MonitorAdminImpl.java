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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.notification.AlertItem;
import info.dmtree.notification.NotificationService;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.service.monitor.MonitorAdmin;
import org.osgi.service.monitor.MonitorListener;
import org.osgi.service.monitor.MonitorPermission;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.MonitoringJob;
import org.osgi.service.monitor.StatusVariable;
import org.osgi.util.tracker.ServiceTracker;

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
    
    private BundleContext bc;
    private ServiceTracker tracker;
    private EventAdmin eventChannel;
    private NotificationService alertSender;
    private Vector jobs;
    
    private Set quietVars; // no automatic events for the listed status vars

    MonitorAdminImpl(BundleContext bc, ServiceTracker tracker, 
            EventAdmin eventChannel, NotificationService alertSender) {
        this.bc = bc;
        this.tracker = tracker;
        this.eventChannel = eventChannel;
        this.alertSender = alertSender;

        jobs = new Vector();
        
        quietVars = new HashSet();
    }

    public StatusVariable getStatusVariable(String pathStr)
            throws IllegalArgumentException {
        Path path = Path.getPath(pathStr);
        checkPermission(pathStr, MonitorPermission.READ);
        
        return trustedGetStatusVariable(path);
    }

    public String[] getStatusVariableNames(String monitorableId) {
        Path.checkName(monitorableId, "Monitorable ID");

        Monitorable monitorable = new MonitorableWrapper(monitorableId);
        String[] varNames = monitorable.getStatusVariableNames();
        
        // Only add status variables names that the caller has read permissions 
        // for (not very efficient if there is no security manager)
        
        List readableVarNames = new Vector();
        for (int i = 0; i < varNames.length; i++) {
            try {
                checkPermission(monitorableId + '/' + varNames[i], 
                                MonitorPermission.READ);
                readableVarNames.add(varNames[i]);
            } catch(SecurityException e) {
                // varNames[i] not added to readableVarNames
            }
        }

        return sortedArrayFromStringList(readableVarNames);
    }
    
    public StatusVariable[] getStatusVariables(String monitorableId) {
        Path.checkName(monitorableId, "Monitorable ID");

        Monitorable monitorable = new MonitorableWrapper(monitorableId);
        String[] varNames = monitorable.getStatusVariableNames();
        
        // Only add status variables that the caller has read permissions for
        // (not very efficient if there is no security manager)
        
        List readableVars = new Vector();
        for (int i = 0; i < varNames.length; i++) {
            try {
                checkPermission(monitorableId + '/' + varNames[i], 
                                MonitorPermission.READ);
                readableVars.add(monitorable.getStatusVariable(varNames[i]));
            } catch(SecurityException e) {
                // varNames[i] not retrieved
            }
        }

        return (StatusVariable[]) readableVars.toArray(new StatusVariable[] {});
    }
    
    public boolean resetStatusVariable(String pathStr)
            throws IllegalArgumentException {
        Path path = Path.getPath(pathStr);
        checkPermission(pathStr, MonitorPermission.RESET);
        
        Monitorable monitorable = new MonitorableWrapper(path.getMonId());
        return monitorable.resetStatusVariable(path.getVarId());
    }
    
    public String getDescription(String pathStr)
            throws IllegalArgumentException {
        Path path = Path.getPath(pathStr);
        checkPermission(pathStr, MonitorPermission.READ);
        
        Monitorable monitorable = new MonitorableWrapper(path.getMonId());
        return monitorable.getDescription(path.getVarId());
    }
    
    // If events are turned off for a path containing *, the list of actual
    // variables is generated at the time of the call, based on the currently
    // exported Status Variables.
    public void switchEvents(String pathStr, boolean on) {
        // publisher perms not checked, but this has no security implications

        Path path = Path.getPath(pathStr, true);
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
            throw new IllegalArgumentException("Invalid report count '" + 
                    count + "', value must be positive.");

        return startJob(initiator, varNames, 0, count, true);
    }
    
    public MonitoringJob startScheduledJob(String initiator, String[] varNames, 
            int schedule, int count) throws IllegalArgumentException {
        checkJobStartCommon(initiator, varNames);

        if(schedule <= 0)
            throw new IllegalArgumentException("Invalid schedule '" + 
                    schedule + "', value must be positive.");

        if(count < 0)
            throw new IllegalArgumentException("Invalid report count '" 
                    + count + "', value must be non-negative.");

        return startJob(initiator, varNames, schedule, count, true);
    }
    
    public synchronized MonitoringJob[] getRunningJobs() {
        List visibleJobs = new Vector();
        
        Iterator iter = jobs.iterator();
        while (iter.hasNext()) {
            MonitoringJobImpl job = (MonitoringJobImpl) iter.next();
            
            String reqPermission = MonitorPermission.STARTJOB;
            if(!job.isChangeBased()) // scheduled job
                reqPermission += ":" + job.getSchedule();
            
            String[] names = job.getStatusVariableNames();
            try {
                for(int i = 0; i < names.length; i++)
                    checkPermission(names[i], reqPermission);
                visibleJobs.add(job);
            } catch(SecurityException e) {
                // job not added to visibleJobs
            }
        }
        
        return (MonitoringJob[]) 
            visibleJobs.toArray(new MonitoringJob[visibleJobs.size()]);
    }

    // No read or publish permissions are checked for listing monitorable names,
    // because it would be very inefficient to check for MonitorPermissions
    // with "<monId>/*anything*" target.
    public String[] getMonitorableNames() {
        return sortedArrayFromStringList(trustedGetMonitorableNames());
    }

    private String[] sortedArrayFromStringList(List list) {
        int size = list.size();
        if(size == 0)
            return new String[] {};
        
        String[] names = (String[]) list.toArray(new String[size]);
        Arrays.sort(names);
        return names;
    }
    
    public synchronized void updated(String monitorableId, StatusVariable var) 
            throws IllegalArgumentException {
        Path.checkName(monitorableId, "Monitorable ID");
        
        if(var == null)
            throw new IllegalArgumentException(
                    "Status Variable parameter is null.");
        
        String varId = var.getID();

        // ignore update indication if the Monitorable does not have publish
        // permissions for the given variable
        if(!new MonitorableWrapper(monitorableId).hasPublishPermission(varId))
            return;
        
        if(!quietVars.contains(new Path(monitorableId, varId)))
            sendEvent(monitorableId, var, null);

        Vector listeners = new Vector();
        Vector remoteJobs = new Vector();

        Path path = new Path(monitorableId, var.getID());
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
                    listeners.toArray(new String[matchNum]));

        Iterator j = remoteJobs.iterator();
        while(j.hasNext()) {
            MonitoringJobImpl job = (MonitoringJobImpl) j.next();
            sendAlert(job.getStatusVariablePaths(), job.getInitiator());
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
        final Event event = new Event(MONITOR_EVENT_TOPIC, properties);
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                eventChannel.postEvent(event);
                return null;
            }
        });
    }

    private void sendAlert(Path[] paths, String initiator) {
        List itemList = new Vector();
        for (int i = 0; i < paths.length; i++) {
            try {
                StatusVariable var = trustedGetStatusVariable(paths[i]);
                itemList.add(new AlertItem(
                        Activator.PLUGIN_ROOT + "/" + paths[i],
                        "x-oma-trap:" + paths[i], null, createData(var)));
            } catch(IllegalArgumentException e) {
                // Ignore Status Variables that are (temporarily) unavailable                
            }
        }
        
        AlertItem[] items = (AlertItem[]) 
            itemList.toArray(new AlertItem[itemList.size()]);

        try {
            alertSender.sendNotification(initiator, MONITORING_ALERT_CODE, null, 
                                  items);
        } catch(DmtException e) {
            // expected error codes: ALERT_NOT_ROUTED, REMOTE_ERROR
            log(LogService.LOG_WARNING, 
                    "Error delivering alert to remote server", e);
        }
    }

    static DmtData createData(StatusVariable var) {
        int type = var.getType();
        switch(type) {
        case StatusVariable.TYPE_STRING:  return new DmtData(var.getString());
        case StatusVariable.TYPE_INTEGER: return new DmtData(var.getInteger());
        case StatusVariable.TYPE_FLOAT:   return new DmtData(var.getFloat());
        case StatusVariable.TYPE_BOOLEAN: return new DmtData(var.getBoolean());
        default:
            throw new IllegalArgumentException(
                    "Unknown Status Variable type '" + type + "'.");
        }
    }
    
    private static String getStatusVariableString(StatusVariable var) {
        
        int type = var.getType(); 
        switch(type) {
        case StatusVariable.TYPE_STRING:  return var.getString();
        case StatusVariable.TYPE_INTEGER: return Integer.toString(var.getInteger());
        case StatusVariable.TYPE_FLOAT:   return Float.toString(var.getFloat());
        case StatusVariable.TYPE_BOOLEAN: return var.getBoolean() ? "true" : "false";
        }
        
        // never reached
        throw new IllegalArgumentException("Unknown StatusVariable type '" +
                                           type + "'.");
    }
    
    private void addImpliedVariables(Path path) {
        String monId = path.getMonId();
        String varId = path.getVarId();
        
        if(monId.endsWith("*")) {
            String monIdPrefix = monId.substring(0, monId.length()-1);
            Iterator i = trustedGetMonitorableNames().iterator();
            while(i.hasNext()) {
                String monName = (String) i.next();
                if(monName.startsWith(monIdPrefix))
                    addImpliedVariables(monName, varId);
            }
        }
        else
            addImpliedVariables(monId, varId);
    }
    
    private void addImpliedVariables(String monId, String varId) {
        Monitorable monitorable = new MonitorableWrapper(monId);
        if(varId.endsWith("*")) {
            String varIdPrefix = varId.substring(0, varId.length()-1);
            String[] varNames = monitorable.getStatusVariableNames();
            for (int i = 0; i < varNames.length; i++)
                if(varNames[i].startsWith(varIdPrefix))
                    quietVars.add(new Path(monId, varNames[i]));
        }
        else {
            monitorable.getStatusVariable(varId);
            quietVars.add(new Path(monId, varId));
        }
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
        return implies(pattern.getMonId(), entry.getMonId()) &&
            implies(pattern.getVarId(), entry.getVarId());
    }

    private boolean implies(String pattern, String entry) {
        return pattern.endsWith("*")
                    ? entry.startsWith(pattern.substring(0, pattern.length()-1))
                    : entry.equals(pattern);
    }

    synchronized MonitoringJob startJob(String initiator, String[] varNames,
            int schedule, int count, boolean local) {
        // OPTIMIZE collect SVs from the same Monitorable, iterate through registered Ms only once
        
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
        
        Path[] paths = new Path[varNames.length];
        for (int i = 0; i < varNames.length; i++) {
            paths[i] = Path.getPath(varNames[i]);
            checkPermission(varNames[i], reqPermission);
            Monitorable monitorable = new MonitorableWrapper(paths[i].getMonId());

            // check that the status variable exists
            monitorable.getStatusVariable(paths[i].getVarId());
            
            if(schedule == 0) // change-based job
                // check that the status variable supports change notification 
                if(!monitorable.notifiesOnChange(paths[i].getVarId()))
                    throw new IllegalArgumentException("Status variable '" + 
                            varNames[i] + "' does not support notifications.");
        }
        
        MonitoringJobImpl job = new MonitoringJobImpl(this, initiator, paths, 
                schedule, count, local);

        jobs.add(job);

        return job;
    }
    
    synchronized void jobStopped(MonitoringJob job) {
        jobs.remove(job);
    }

    // called by MonitoringJob, paths are assumed to be checked previously
    synchronized void scheduledUpdate(Path[] paths, MonitoringJob job) {
        String initiator = job.getInitiator();
        
        if(job.isLocal()) {
            for (int i = 0; i < paths.length; i++) {
                try {
                    sendEvent(paths[i].getMonId(), 
                              trustedGetStatusVariable(paths[i]), initiator);
                } catch(IllegalArgumentException e) {
                    // Ignore status vars that are (temporarily) unavailable                    
                }
            }
        } else
            sendAlert(paths, initiator);
    }

    private StatusVariable trustedGetStatusVariable(Path path) {
        Monitorable monitorable = new MonitorableWrapper(path.getMonId());
        return monitorable.getStatusVariable(path.getVarId());        
    }

    /**
     * Returns the IDs of all registered Monitorables that have a "service.pid"
     * property. Results are not filtered with regard to PUBLISH permissions,
     * these are only checked on the Status Variable level.
     * 
     * The list of Monitorables is retrieved using the permissions of the
     * MonitorAdmin bundle, caller permissions must be checked before this
     * method is called.
     */
    private List trustedGetMonitorableNames() {
        ServiceReference[] monitorableRefs = (ServiceReference[]) 
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return tracker.getServiceReferences();
                }
            });

        Vector monitorableNames = new Vector();

        if(monitorableRefs == null)
            return monitorableNames;
        
        for(int i = 0; i < monitorableRefs.length; i++) {
            String servicePid = (String) monitorableRefs[i].getProperty("service.pid");
            
            try {
                Path.checkName(servicePid, "service.pid");
                monitorableNames.add(servicePid);
            } catch(IllegalArgumentException e) {
                log(LogService.LOG_WARNING, "Ignoring Monitorable service " +
                        "because \"service.pid\" property is invalid.", e);
            }
        }
        
        return monitorableNames;
    }
    
    
    /**
     * Checks whether there exists a Monitorable with the given ID. If there is
     * no such Monitorable, an exception is thrown. The caller does not need
     * ServicePermissions for this operation to succeed.
     * <p>
     * This is used by the MonitorPlugin where no specific status variable is
     * needed, only the information that a given Monitorable exists.
     * 
     * @param monitorableId the ID of the Monitorable to be retrieved
     * @throws IllegalArgumentException if the monitorable ID is invalid, or no
     *         Monitorable is registered with the given ID
     */
    void checkMonitorable(String monitorableId) throws IllegalArgumentException {
        Path.checkName(monitorableId, "Monitorable ID");
        new MonitorableWrapper(monitorableId);
    }

    private static void checkJobStartCommon(String initiator, String[] varNames) {
        Path.checkString(initiator, "Initiator ID parameter");
        if (varNames == null || varNames.length == 0)
            throw new IllegalArgumentException(
                    "Status variable name list is null or empty.");
    }
    
    private static void checkPermission(String target, String action) {
        SecurityManager sm = System.getSecurityManager();
        if(sm != null)
            sm.checkPermission(new MonitorPermission(target, action));
    }

    private void log(int severity, String message, Throwable throwable) {
        System.out.println("Log entry | Serverity: " + severity + 
                " Message: " + message + " Throwable: " + throwable);

        ServiceReference serviceRef = bc.getServiceReference(LogService.class.getName());
        if (serviceRef == null)
            return;
        
        LogService logService = (LogService) bc.getService(serviceRef);
        if (logService == null)
            return;
        
        logService.log(severity, message, throwable);
        
        bc.ungetService(serviceRef);
    }
    
    // The Bundle object is not stored, but retrieved at the start of each 
    // method from the ServiceReference, to ensure that the service is still
    // registered.
    private class MonitorableWrapper implements Monitorable {
        private String           id;
        private Monitorable      monitorable;
        private ServiceReference reference;

        /**
         * Retrieves and stores the Monitorable with the specified ID. The ID is
         * matched against the "service.pid" property of the Monitorable
         * registrations. The caller does not need ServicePermissions for this
         * operation to succeed.
         * 
         * @param monitorableId the ID of the Monitorable to be wrapped, must be
         *        non-null, non-empty, and must not contain illegal characters
         * @throws IllegalArgumentException if no Monitorable is registered with
         *         the given ID
         */
        public MonitorableWrapper(String monitorableId) 
                throws IllegalArgumentException {            
            id = monitorableId;
            
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    // initialize 'monitorable' and 'reference' variables
                    retrieveMonitorable(); 
                    return null;
                }
            });
        }

        private void retrieveMonitorable() throws IllegalArgumentException {
            ServiceReference[] refs = tracker.getServiceReferences();
            if (refs == null)
                throw new IllegalArgumentException("Monitorable id '" + id
                        + "' not found (no matching services registered)");
            
            for (int i = 0; i < refs.length; i++)
                if (id.equals(refs[i].getProperty("service.pid"))) {
                    monitorable = (Monitorable) tracker.getService(refs[i]);
                    if (monitorable == null)
                        throw new IllegalArgumentException("Monitorable id '"
                                + id + "' not found (monitorable no longer registered.");
                    reference = refs[i];
                    return;
                }
                
            throw new IllegalArgumentException("Monitorable id '" + id
                    + "' not found (no matching service registered).");
        }

        public String[] getStatusVariableNames() {
            Bundle bundle = getBundle();
            String[] names = monitorable.getStatusVariableNames();
            if(names == null) // only for stability (names must not be null)
                return new String[] {};
            
            // filter out all status variable names 
            // - that the Monitorable does not have PUBLISH permissions for
            // - that contain illegal characters
            // - that appear multiple times in the list

            Set validNameList = new HashSet();
            for (int i = 0; i < names.length; i++) {
                MonitorPermission publishPermission =
                    new MonitorPermission(id + '/' + names[i], 
                                          MonitorPermission.PUBLISH);
                if(bundle.hasPermission(publishPermission)) {
                    try {
                        Path.checkName(names[i], null);
                        validNameList.add(names[i]);
                    } catch(IllegalArgumentException e) {
                        // "add" call was skipped if names[i] was invalid
                    }
                }
            }
            
            return (String[]) 
                validNameList.toArray(new String[validNameList.size()]);
        }

        // precondition: varId must be validated by Path.checkName
        public StatusVariable getStatusVariable(String varId) 
                throws IllegalArgumentException {
            checkPublishPermission(varId);
            return monitorable.getStatusVariable(varId);
        }

        // precondition: varId must be validated by Path.checkName
        public boolean notifiesOnChange(String varId) 
                throws IllegalArgumentException {
            checkPublishPermission(varId);
            return monitorable.notifiesOnChange(varId);
        }

        // precondition: varId must be validated by Path.checkName
        public boolean resetStatusVariable(String varId)
                throws IllegalArgumentException {
            checkPublishPermission(varId);
            return monitorable.resetStatusVariable(varId);
        }

        // precondition: varId must be validated by Path.checkName
        public String getDescription(String varId) 
                throws IllegalArgumentException {
            checkPublishPermission(varId);
            return monitorable.getDescription(varId);
        }
        
        boolean hasPublishPermission(String varId) {
            MonitorPermission publishPermission =
                new MonitorPermission(id + "/" + varId, MonitorPermission.PUBLISH);
            return getBundle().hasPermission(publishPermission);
        }
        
        private void checkPublishPermission(String varId) {
            if(!hasPublishPermission(varId))
                throw new IllegalArgumentException("Status variable '" + 
                        id + "/" + varId + "' does not exist");
        }
        
        private Bundle getBundle() {
            Bundle bundle = reference.getBundle();
            if(bundle == null)
                throw new IllegalArgumentException(
                        "Unable to retrieve status variable(s): Monitorable '" +
                        id + "' has been unregistered.");
            return bundle;
        }
    }
}