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
import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.application.Scheduler;
import org.osgi.service.log.LogService;

/**
 * The realization of the ScheduledApplication interface
 */
public class ScheduledApplicationImpl implements ScheduledApplication,
		Comparable, Serializable {
	private ApplicationDescriptor	appDesc;
	private Date								schedDate;
	private Hashtable						args;
	private BundleContext				bc;
	private String							appUID;
	private boolean							launchOnOverdue;
	private SchedulerImpl				scheduler;
	private ServiceRegistration	serviceReg;

	public ScheduledApplicationImpl(Scheduler scheduler, BundleContext bc,
			ApplicationDescriptor appDesc, Map args, Date schedDate, boolean launchOnOverdue ) {
		this.scheduler = (SchedulerImpl)scheduler;
		this.bc = bc;
		this.appDesc = appDesc;
		this.schedDate = schedDate;
		this.args = new Hashtable(args);
		this.appUID = appDesc.getApplicationPID();
		this.launchOnOverdue = launchOnOverdue;
	}

	public void validate(Scheduler scheduler, BundleContext bc)
			throws Exception {
		this.bc = bc;
		this.scheduler = (SchedulerImpl)scheduler;

		try {
			ServiceReference[] references = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationDescriptor", null);
			if (references == null || references.length == 0)
				return;

			for (int i = 0; i != references.length; i++) {
				try {
					ApplicationDescriptor appDesc = (ApplicationDescriptor) bc
						.getService(references[i]);
					if( appDesc.getApplicationPID().equals( appUID ) ) {
						this.appDesc = appDesc;
						break;
					}
					bc.ungetService(references[i]);
				}catch( SecurityException e ) {}
			}
		}
		catch (Exception e) {
			SchedulerImpl.log( bc, LogService.LOG_ERROR,
				"Exception occurred at searching the application descriptors!", e);
		}
	}

	public Date getDate() {
		return (Date) schedDate.clone();
	}

	public boolean launchOnOverdue() {
		return launchOnOverdue;
	}

	public Map getArguments() {
		return new Hashtable( args );
	}

	public ApplicationDescriptor getApplicationDescriptor() {
		return appDesc;
	}

	void register() {
		serviceReg = bc.registerService( "org.osgi.service.application.ScheduledApplication", 
				this, null );

	}
	
	void unregister() {
		if( serviceReg != null )
		  serviceReg.unregister();
		serviceReg = null;
	}
	
	public void remove() {
		try {
			scheduler.removeScheduledApplication( this );
		}
		catch (Exception e) {
			SchedulerImpl.log( bc, LogService.LOG_ERROR,
				"Exception occurred at removing a scheduled application!", e);
		}
	}

	public int compareTo(Object schedObj) {
		ScheduledApplicationImpl schedApp = (ScheduledApplicationImpl) schedObj;
		int compResult = getDate().compareTo(schedApp.getDate());
		if (compResult != 0)
			return compResult;
		if (getArguments().size() < schedApp.getArguments().size())
			return -1;
		if (getArguments().size() > schedApp.getArguments().size())
			return 1;
		if (schedApp.equals(this))
			return 0;
		if (hashCode() < schedApp.hashCode())
			return -1;
		else
			return 1;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(appUID);
		out.writeObject(schedDate);
		out.writeObject(args);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		appDesc = null;
		bc = null;
		appUID = (String) in.readObject();
		schedDate = (Date) in.readObject();
		args = (Hashtable) in.readObject();
	}
	
	ServiceReference getReference() {
		return serviceReg.getReference();
	}
}
