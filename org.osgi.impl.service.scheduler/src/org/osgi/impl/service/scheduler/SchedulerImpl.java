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

package org.osgi.impl.service.scheduler;

import java.io.*;
import java.util.*;
import java.security.*;
import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.log.LogService;
import org.osgi.service.application.Scheduler;

public class SchedulerImpl implements Scheduler, Runnable {
    private BundleContext   bc;
    private TreeSet         scheduledApps;
    private boolean         stopped;
    private Thread          schedulerThread;

    public SchedulerImpl( BundleContext bc ) {
        this.bc = bc;
        scheduledApps = new TreeSet();
        loadScheduledApplications();
        stopped = false;
        schedulerThread = new Thread(this);
        schedulerThread.start();
    }

    public void stop() {
        stopped = true;
        schedulerThread.interrupt();
    }

    public synchronized ScheduledApplication addScheduledApplication(
            ApplicationDescriptor appDescriptor, Map arguments, Date date, boolean launchOnOverdue ) {

        AccessController.checkPermission( new ApplicationAdminPermission(
            appDescriptor.getApplicationPID(), ApplicationAdminPermission.SCHEDULE ) );

        ScheduledApplication app = new ScheduledApplicationImpl(this, bc,
                appDescriptor, arguments, date, launchOnOverdue);
        scheduledApps.add(app);
        saveScheduledApplications();
        schedulerThread.interrupt();
        return app;
    }

    public synchronized void removeScheduledApplication(
            ScheduledApplication scheduledApplication) throws Exception {

        AccessController.checkPermission( new ApplicationAdminPermission(
            scheduledApplication.getApplicationDescriptor().getApplicationPID(),
            ApplicationAdminPermission.SCHEDULE ) );

        scheduledApps.remove(scheduledApplication);
        saveScheduledApplications();
    }

    private synchronized void loadScheduledApplications() {
        try {
            File schedApps = bc.getDataFile("ScheduledApplications");
            if( !schedApps.exists() )
                return;
            FileInputStream stream = new FileInputStream(schedApps);
            ObjectInputStream is = new ObjectInputStream(stream);
            scheduledApps = (TreeSet) is.readObject();
            is.close();
            Iterator it = scheduledApps.iterator();
            while (it.hasNext()) {
                ScheduledApplicationImpl schedApp = (ScheduledApplicationImpl) it
                        .next();
                schedApp.validate(this, bc);
            }
        }
        catch (Exception e) {
            log(bc, LogService.LOG_ERROR,
                "Exception occurred at loading the scheduled applications!", e);
        }
    }

    private synchronized void saveScheduledApplications() {
        try {
            File schedApps = bc.getDataFile("ScheduledApplications");
            FileOutputStream stream = new FileOutputStream(schedApps);
            ObjectOutputStream os = new ObjectOutputStream(stream);
            os.writeObject(scheduledApps);
            os.close();
        }
        catch (Exception e) {
            log(bc, LogService.LOG_ERROR,
                "Exception occurred at saving the scheduled applications!", e);
        }
    }

    public void run() {
        while (!stopped) {
            long currentTime = System.currentTimeMillis();
            long startTime;
            do {
                startTime = currentTime + 1000000000000l; /* means infinity */
                try {
                    ScheduledApplicationImpl schedApp = (ScheduledApplicationImpl) scheduledApps
                            .first();
                    startTime = schedApp.getDate().getTime();
                    if (startTime <= currentTime) {
                        long diffTime = currentTime - startTime;

                        if( diffTime < 60000 || schedApp.launchOnOverdue() ) { /* after 1 min, it's overdue */
                            try {
                                schedApp.getApplicationDescriptor().launch(
                                        schedApp.getArguments());
                            }
                            catch (Exception e) {
                                log(bc, LogService.LOG_ERROR,
                                    "Exception occurred at launching a scheduled application!", e);
                            }
                        }
                        schedApp.remove();
                        saveScheduledApplications();
                    }
                }
                catch (NoSuchElementException e) {
                }
            } while (startTime <= currentTime);
            long sleepTime = startTime - currentTime;
            try {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e) {
            }
        }
    }

    static boolean log( BundleContext bc, int severity, String message, Throwable throwable) {
        System.out.println("Serverity:" + severity + " Message:" + message
                + " Throwable:" + throwable);

        ServiceReference serviceRef = bc.getServiceReference("org.osgi.service.log.LogService");
        if (serviceRef != null) {
            LogService logService = (LogService) bc.getService(serviceRef);
            if (logService != null) {
                try {
                    logService.log(severity, message, throwable);
                    return true;
                }
                finally {
                    bc.ungetService(serviceRef);
                }
            }
        }
        return false;
    }
}
