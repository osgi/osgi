/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.service.application;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import java.security.*;
import java.io.*;

public abstract class ApplicationDescriptor {

	public final static String APPLICATION_AUTOSTART   = "application.autostart";
	public final static String APPLICATION_ICON        = "application.icon";
	public final static String APPLICATION_LAUNCHABLE  = "application.launchable";
	public final static String APPLICATION_LOCKED      = "application.locked";
	public final static String APPLICATION_NAME        = "application.name";
	public final static String APPLICATION_PID         = "service.pid";
	public final static String APPLICATION_SINGLETON   = "application.singleton";
	public final static String APPLICATION_VENDOR      = "application.vendor";
	public final static String APPLICATION_VERSION     = "application.version";
	public final static String APPLICATION_VISIBLE     = "application.visible";
	
	private static Vector lockedApplications = null;
	private static Object synchronizer = new Object();

	public abstract String getApplicationPID();

	public abstract Map getProperties (String locale);

	public final ServiceReference launch( Map args )
			throws SingletonException, Exception {

		AccessController.checkPermission( new ApplicationAdminPermission(
			getApplicationPID(), ApplicationAdminPermission.LAUNCH ) );

		BundleContext bc = getBundleContext();

		Map props = getProperties("en");
		String isLocked = (String)props.get("application.locked");
		if (isLocked != null && isLocked.equalsIgnoreCase("true"))
			throw new Exception("Application is locked, can't launch!");
		String isLaunchable = (String)props.get("application.launchable");
 		if (isLaunchable == null || !isLaunchable.equalsIgnoreCase("true"))
 			throw new Exception("Cannot launch the application!");
		String isSingleton = (String)props.get("application.singleton");
		if (isSingleton == null || isSingleton.equalsIgnoreCase("true")) {
			ServiceReference[] appHandles = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationHandle", null);
			if (appHandles != null)
				for (int k = 0; k != appHandles.length; k++) {
					ApplicationHandle handle = (ApplicationHandle) bc
							.getService(appHandles[k]);
					ServiceReference appDescRef = handle.getApplicationDescriptor();
					ApplicationDescriptor appDesc = (ApplicationDescriptor)bc.getService( appDescRef );
					bc.ungetService( appDescRef );
					bc.ungetService(appHandles[k]);
					if (appDesc == this)
						throw new Exception("Singleton Exception!");
				}
		}
		return launchSpecific( args );
	}

	protected abstract ServiceReference launchSpecific( Map args ) throws Exception;

	public final boolean isLocked() {
		String pid = getApplicationPID();

		if( lockedApplications == null )
			lockedApplications = loadVector( "LockedApplications" );
		return lockedApplications.contains( pid );
	}

	public final void lock() throws Exception {
		String pid = getApplicationPID();

		AccessController.checkPermission( new ApplicationAdminPermission( pid,
				ApplicationAdminPermission.LOCK ) );

		if( lockedApplications == null )
			lockedApplications = loadVector( "LockedApplications" );

		boolean save = false;
		synchronized( synchronizer ) {
			if (!lockedApplications.contains( pid )) {
				lockedApplications.add( pid );
				save = true;
			}
		}
		if( save )
			saveVector( lockedApplications, "LockedApplications" );
	}

	public void unLock() throws Exception {
		String pid = getApplicationPID();

		AccessController.checkPermission( new ApplicationAdminPermission( pid,
				ApplicationAdminPermission.LOCK ) );

		if( lockedApplications == null )
			lockedApplications = loadVector( "LockedApplications" );
		boolean save = false;
		synchronized( synchronizer ) {
			if (lockedApplications.contains( pid )) {
				lockedApplications.remove( pid );
				save = true;
			}
		}
		if( save )
			saveVector( lockedApplications, "LockedApplications" );
	}

	protected abstract BundleContext getBundleContext();

	public ServiceReference schedule( Map arguments, String topic, String eventFilter, 
			                              boolean recurring )
																		throws Exception {
		ServiceReference serviceRef = getBundleContext()
				.getServiceReference("org.osgi.service.application.Scheduler");
		if (serviceRef == null)
			throw new Exception( "Scheduler service not found!" );

		Scheduler scheduler = (Scheduler) getBundleContext().getService(serviceRef);
		if (scheduler == null)
			throw new Exception( "Scheduler service not found!" );

		try {
			return scheduler.addScheduledApplication(getApplicationPID(), arguments, topic, eventFilter, recurring);
		}finally {
			getBundleContext().ungetService(serviceRef);
		}
	}
	
	private Vector loadVector(String fileName) {
		synchronized( synchronizer ) {
			Vector resultVector = new Vector();
			try {
				File vectorFile = getBundleContext().getDataFile(fileName);
				if (vectorFile.exists()) {
					FileInputStream stream = new FileInputStream(vectorFile);
					String codedIds = "";
					byte[] buffer = new byte[1024];
					int length;
					while ((length = stream.read(buffer, 0, buffer.length)) > 0)
						codedIds += new String(buffer);
					stream.close();
					if (!codedIds.equals("")) {
						int index = 0;
						while (index != -1) {
							int comma = codedIds.indexOf(',', index);
							String name;
							if (comma >= 0)
								name = codedIds.substring(index, comma);
							else
								name = codedIds.substring(index);
							resultVector.add(name.trim());
							index = comma;
						}
					}
				}
			}
			catch (Exception e) {
				log( LogService.LOG_ERROR,
					"Exception occurred at loading '" + fileName + "'!", e);
			}
		return resultVector;
		}
	}

	private void saveVector(Vector vector, String fileName) {
		synchronized( synchronizer ) {
			try {
				File vectorFile = getBundleContext().getDataFile(fileName);
				FileOutputStream stream = new FileOutputStream(vectorFile);
				for (int i = 0; i != vector.size(); i++)
					stream.write((((i == 0) ? "" : ",") + (String) vector.get(i))
							.getBytes());
				stream.close();
			}
			catch (Exception e) {
				log( LogService.LOG_ERROR,
					"Exception occurred at saving '" + fileName + "'!", e);
			}
		}
	}

	protected boolean log( int severity, String message, Throwable throwable) {
		System.out.println("Serverity:" + severity + " Message:" + message
				+ " Throwable:" + throwable);

		ServiceReference serviceRef = getBundleContext()
				.getServiceReference("org.osgi.service.log.LogService");
		if (serviceRef != null) {
			LogService logService = (LogService) getBundleContext().getService(serviceRef);
			if (logService != null) {
				try {
					logService.log(severity, message, throwable);
					return true;
				}
				finally {
					getBundleContext().ungetService(serviceRef);
				}
			}
		}
		return false;
	}
}
