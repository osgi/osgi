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
 * The realization of the ScheduledApplication interface
 */
public class ScheduledApplicationImpl implements ScheduledApplication,
		Comparable, Serializable {
	private ApplicationDescriptor	appDesc;
	private Date					schedDate;
	private Hashtable				args;
	private BundleContext			bc;
	private String					appUID;

	public ScheduledApplicationImpl(BundleContext bc,
			ApplicationDescriptor appDesc, Map args, Date schedDate) {
		this.bc = bc;
		this.appDesc = appDesc;
		this.schedDate = schedDate;
		this.args = new Hashtable(args);
		this.appUID = appDesc.getUniqueID();
	}

	public void validate(ApplicationManager appMan, BundleContext bc)
			throws Exception {
		this.bc = bc;
		appDesc = appMan.getAppDescriptor(appUID);
	}

	public Date getDate() {
		return (Date) schedDate.clone();
	}

	public Map getLaunchArgs() {
		return args;
	}

	public ApplicationDescriptor getApplicationDescriptor() {
		return appDesc;
	}

	public void remove() {
		try {
			ServiceReference appManReference = bc
					.getServiceReference("org.osgi.service.application.ApplicationManager");
			if (appManReference != null) {
				ApplicationManagerImpl appMan = (ApplicationManagerImpl) bc
						.getService(appManReference);
				appMan.removeScheduledApplication(this);
				bc.ungetService(appManReference);
			}
		}
		catch (Exception e) {
		}
	}

	public int compareTo(Object schedObj) {
		ScheduledApplicationImpl schedApp = (ScheduledApplicationImpl) schedObj;
		int compResult = getDate().compareTo(schedApp.getDate());
		if (compResult != 0)
			return compResult;
		if (getLaunchArgs().size() < schedApp.getLaunchArgs().size())
			return -1;
		if (getLaunchArgs().size() > schedApp.getLaunchArgs().size())
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
}
