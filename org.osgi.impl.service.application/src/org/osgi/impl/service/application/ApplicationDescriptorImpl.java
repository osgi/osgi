/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.impl.service.application;

import java.io.*;
import java.util.*;
//import java.security.*; /* TODO */

import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.application.ApplicationDescriptor.Delegate;

public class ApplicationDescriptorImpl implements Delegate {
	private ApplicationDescriptor descriptor;
	private boolean								locked;
	private static Properties			locks;
	private Scheduler             scheduler;
	
	public synchronized void setApplicationDescriptor(ApplicationDescriptor d) {
		descriptor = d;
	}

	public boolean isLocked() {
		return doLock(true, false );
	}

	synchronized boolean doLock(boolean query, boolean newState) {
		try {
			File f = Activator.bc.getDataFile("locks");
			if ( locks == null ) {
				locks = new Properties();
				if ( f.exists() )
					locks.load( new FileInputStream(f));
			}
			boolean current = locks.containsKey(descriptor.getPID()); 
			if ( query || newState == current )
				return current;

			if ( current )
				locks.remove(descriptor.getPID());
			else
				locks.put(descriptor.getPID(), "locked");
			locks.save(new FileOutputStream(f), "Saved " + new Date());
			return newState;
		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
		return false;
	}
	
	public void lock() {
		doLock(false, true);
	}

	public void unlock() {
		doLock(false, false);
	}

	public ServiceReference schedule(Map args, String topic, String filter, boolean recurs) {
		return scheduler.addScheduledApplication( descriptor.getPID(), args, topic, filter, recurs );
	}

	public void launch(Map arguments) throws Exception {
//		AccessController.checkPermission( new ApplicationAdminPermission(  /* TODO */
//				descriptor.getPID(), ApplicationAdminPermission.LAUNCH ) );

			Map props = descriptor.getProperties("en");
			String isLocked = (String)props.get("application.locked");
			if (isLocked != null && isLocked.equalsIgnoreCase("true"))
				throw new Exception("Application is locked, can't launch!");
			String isLaunchable = (String)props.get("application.launchable");
	 		if (isLaunchable == null || !isLaunchable.equalsIgnoreCase("true"))
	 			throw new Exception("Cannot launch the application!");
			String isSingleton = (String)props.get("application.singleton");
			if (isSingleton == null || isSingleton.equalsIgnoreCase("true")) {
				ServiceReference[] appHandles = Activator.bc.getServiceReferences(
						"org.osgi.service.application.ApplicationHandle", null);
				if (appHandles != null)
					for (int k = 0; k != appHandles.length; k++) {
						ApplicationHandle handle = (ApplicationHandle) Activator.bc
								.getService(appHandles[k]);
						ApplicationDescriptor appDesc = handle.getApplicationDescriptor();
						Activator.bc.ungetService(appHandles[k]);
						if ( appDesc == descriptor )
							throw new Exception("Singleton Exception!");
					}
			}
	}
}
