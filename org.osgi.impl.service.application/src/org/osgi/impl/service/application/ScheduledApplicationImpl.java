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
import org.osgi.service.log.LogService;

/**
 * The realization of the ScheduledApplication interface
 */
public class ScheduledApplicationImpl implements ScheduledApplication, Serializable {
	private BundleContext				bc;
	private Scheduler				scheduler;
	
	private String							pid;
	private Hashtable						args;
	private String							topic;
	private String  						eventFilter;
	private boolean							recurring;

	private ServiceRegistration	serviceReg;

	public ScheduledApplicationImpl(Scheduler scheduler, BundleContext bc,
			String pid, Map args, String topic, String eventFilter, boolean recurring ) {
		this.scheduler = (Scheduler)scheduler;
		this.bc = bc;
		this.pid = pid;
		if( args != null )
			this.args = new Hashtable( args );
		else
			this.args = null;
		this.topic = topic;
		this.eventFilter = eventFilter;
		this.recurring = recurring;
	}

	void validate(Scheduler scheduler, BundleContext bc)
			throws Exception {
		this.bc = bc;
		this.scheduler = (Scheduler)scheduler;
	}
	
	String getPid() {
		return pid;
	}

	public Map getArguments() {
		if( args == null )
			return null;
		return new Hashtable( args );
	}

	public String getTopic() {
		return topic;
	}

	public String getEventFilter() {
		return eventFilter;
	}

	public boolean isRecurring() {
		return recurring;
	}

	public ServiceReference getApplicationDescriptor() {
		try {
			ServiceReference refs[] = bc.getServiceReferences( 
					"org.osgi.service.application.ApplicationDescriptor",
					"(" + ApplicationDescriptor.APPLICATION_PID + "=" + pid +")" );
			if( refs != null && refs.length != 0 )
				return refs[ 0 ];
		}catch( InvalidSyntaxException e ) {}
		return null;
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
			Scheduler.log( bc, LogService.LOG_ERROR,
				"Exception occurred at removing a scheduled application!", e);
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(pid);
		out.writeObject(args);
		out.writeObject(topic);
		out.writeObject(eventFilter);
		out.writeObject( new Boolean( recurring ) );
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		bc = null;
		pid= (String) in.readObject();
		args = (Hashtable) in.readObject();
		topic = (String) in.readObject();
		eventFilter = (String) in.readObject();
		Boolean recurring = (Boolean) in.readObject();		
		this.recurring = recurring.booleanValue();
	}
	
	ServiceReference getReference() {
		return serviceReg.getReference();
	}
}
