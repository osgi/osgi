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
package org.osgi.impl.service.application;

import java.io.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.application.*;

/**
 * The realization of the Application Manager
 */
public class ApplicationManagerImpl implements ApplicationManager, Runnable {
	private BundleContext	bc;
	private Vector			lockedApplications;
	private TreeSet			scheduledApps;
	private boolean			stopped;

	public ApplicationManagerImpl(BundleContext bc) throws Exception {
		this.bc = bc;
		lockedApplications = new Vector();
		scheduledApps = new TreeSet();
		loadLockedApplications();
		loadScheduledApplications();
		stopped = false;
		Thread thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		stopped = true;
	}

	public ApplicationDescriptor[] getAppDescriptors() {
		try {
			ServiceReference[] references = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationDescriptor", null);
			if (references == null || references.length == 0)
				return null;
			ApplicationDescriptor appDescs[] = new ApplicationDescriptor[references.length];
			for (int i = 0; i != references.length; i++) {
				appDescs[i] = (ApplicationDescriptor) bc
						.getService(references[i]);
				bc.ungetService(references[i]);
			}
			return appDescs;
		}
		catch (Exception e) {
			return null;
		}
	}

	public ApplicationDescriptor getAppDescriptor(String appUID)
			throws Exception {
		ApplicationDescriptor appDescs[] = getAppDescriptors();
		if (appDescs == null)
			throw new Exception("Application ID not found!");
		for (int i = 0; i != appDescs.length; i++)
			if (appDescs[i].getUniqueID().equals(appUID))
				return appDescs[i];
		throw new Exception("Application ID not found!");
	}

	public ApplicationHandle launchApplication(
			ApplicationDescriptor appDescriptor, Map args)
			throws SingletonException, Exception {
		if (isLocked(appDescriptor))
			throw new Exception("Application is locked, can't launch!");
		Map props = appDescriptor.getProperties("en");
		Object isSingleton = props.get("singleton");
		if (isSingleton != null && isSingleton.equals("true")) {
			ServiceReference[] appHandles = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationHandle", null);
			if (appHandles != null)
				for (int k = 0; k != appHandles.length; k++) {
					ApplicationHandle handle = (ApplicationHandle) bc
							.getService(appHandles[k]);
					ApplicationDescriptor appDesc = handle.getAppDescriptor();
					bc.ungetService(appHandles[k]);
					if (appDesc == appDescriptor)
						throw new Exception("Singleton Exception!");
				}
		}
		ServiceReference[] references = bc.getServiceReferences(
				"org.osgi.service.application.ApplicationContainer",
				"(application_type=" + appDescriptor.getContainerID() + ")");
		if (references == null || references.length == 0)
			throw new Exception("Container " + appDescriptor.getContainerID()
					+ " not found!");
		ApplicationContainer container = (ApplicationContainer) bc
				.getService(references[0]);
		if (container == null)
			throw new Exception("Container " + appDescriptor.getContainerID()
					+ " not found!");
		ApplicationHandleImpl appHandle = new ApplicationHandleImpl(
				appDescriptor, bc);
		ApplicationContext context = new ApplicationContextImpl(args);
		Application application = container.createApplication(context,
				appHandle);
		if (application == null)
			throw new Exception("Invalid application object received!");
		bc.ungetService(references[0]);
		appHandle.setApplication(application);
		application.startApplication();
		appHandle.applicationStarted();
		appHandle.registerAppHandle();
		return (ApplicationHandle) appHandle;
	}

	public synchronized ScheduledApplication addScheduledApplication(
			ApplicationDescriptor appDescriptor, Map arguments, Date date) {
		ScheduledApplication app = new ScheduledApplicationImpl(bc,
				appDescriptor, arguments, date);
		scheduledApps.add(app);
		saveScheduledApplications();
		return app;
	}

	public synchronized void removeScheduledApplication(
			ScheduledApplication scheduledApplication) throws Exception {
		scheduledApps.remove(scheduledApplication);
		saveScheduledApplications();
	}

	public synchronized ScheduledApplication[] getScheduledApplications()
			throws IOException {
		ScheduledApplication[] apps = new ScheduledApplication[scheduledApps
				.size()];
		Iterator it = scheduledApps.iterator();
		for (int i = 0; i != scheduledApps.size() && it.hasNext(); i++)
			apps[i] = (ScheduledApplication) it.next();
		return apps;
	}

	public void lock(ApplicationDescriptor appDescriptor) throws Exception {
		String appUID = appDescriptor.getUniqueID();
		if (!lockedApplications.contains(appUID)) {
			lockedApplications.add(appUID);
			saveLockedApplications();
		}
	}

	public void unLock(ApplicationDescriptor appDescriptor) throws Exception {
		String appUID = appDescriptor.getUniqueID();
		if (lockedApplications.contains(appUID)) {
			lockedApplications.remove(appUID);
			saveLockedApplications();
		}
	}

	public boolean isLocked(ApplicationDescriptor appDescriptor)
			throws Exception {
		return lockedApplications.contains(appDescriptor.getUniqueID());
	}

	public String[] getSupportedMimeTypes() {
		/* TODO TODO TODO TODO TODO */
		return null;
	}

	private void loadLockedApplications() throws Exception {
		File lockedApps = bc.getDataFile("LockedApplications");
		if (lockedApps.exists()) {
			FileInputStream stream = new FileInputStream(lockedApps);
			int length;
			while ((length = stream.read()) != -1) {
				byte[] b = new byte[length];
				int received = 0;
				while (received != length) {
					int len = stream.read(b, received, length - received);
					received += len;
					if (len == -1)
						break;
				}
				lockedApplications.add(new String(b));
			}
			stream.close();
		}
	}

	private void saveLockedApplications() throws Exception {
		File lockedApps = bc.getDataFile("LockedApplications");
		FileOutputStream stream = new FileOutputStream(lockedApps);
		for (int i = 0; i != lockedApplications.size(); i++) {
			String appUID = (String) lockedApplications.get(i);
			stream.write(appUID.length());
			stream.write(appUID.getBytes());
		}
		stream.close();
	}

	private synchronized void loadScheduledApplications() {
		try {
			File schedApps = bc.getDataFile("ScheduledApplications");
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
		}
	}

	public void run() {
		while (!stopped) {
			long currentTime = System.currentTimeMillis();
			long startTime;
			do {
				startTime = currentTime + 10000;
				try {
					ScheduledApplicationImpl schedApp = (ScheduledApplicationImpl) scheduledApps
							.first();
					startTime = schedApp.getDate().getTime();
					if (startTime <= currentTime) {
						try {
							launchApplication(schedApp
									.getApplicationDescriptor(), schedApp
									.getLaunchArgs());
						}
						catch (Exception e) {
						}
						schedApp.remove();
					}
				}
				catch (NoSuchElementException e) {
				}
			} while (startTime <= currentTime);
			long sleepTime = startTime - currentTime;
			if (sleepTime > 500)
				sleepTime = 500;
			try {
				Thread.sleep(sleepTime);
			}
			catch (InterruptedException e) {
			}
		}
	}
}
