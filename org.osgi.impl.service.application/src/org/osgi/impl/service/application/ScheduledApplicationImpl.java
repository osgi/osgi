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
	private BundleContext                   bc;
	private Scheduler                       scheduler;
	
	private String                          pid;
	private HashMap                         args;
	private String                          topic;
	private String                          eventFilter;
	private boolean                         recurring;
	private boolean                         invalid;
	private String                          id;
	
	public static final long serialVersionUID = 0x81212314;

	private ServiceRegistration	serviceReg;

	public ScheduledApplicationImpl(Scheduler scheduler, BundleContext bc,
			String pid, Map args, String topic, String eventFilter, boolean recurring, String id ) {
		this.scheduler = (Scheduler)scheduler;
		this.bc = bc;
		this.pid = pid;
		if( args != null )
			this.args = new HashMap( args );
		else
			this.args = null;
		this.topic = topic;
		this.eventFilter = eventFilter;
		this.recurring = recurring;
		this.id = id;
		
		invalid = true;
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
		checkValidity();		
		if( args == null )
			return null;
		return new HashMap( args );
	}

	String getRequiredTopic() {
		return topic;
	}
	
	public String getTopic() {
		checkValidity();		
		return topic;
	}

	public String getEventFilter() {
		checkValidity();		
		return eventFilter;
	}

	public boolean isRecurring() {
		checkValidity();		
		return recurring;
	}

	public ApplicationDescriptor getApplicationDescriptor() {
		checkValidity();
		
		try {
			ServiceReference refs[] = bc.getServiceReferences( 
					"org.osgi.service.application.ApplicationDescriptor",
					"(" + ApplicationDescriptor.APPLICATION_PID + "=" + pid +")" );
			if( refs != null && refs.length != 0 ) {
				ApplicationDescriptor appDesc = (ApplicationDescriptor)bc.getService( refs[ 0 ] );
				bc.ungetService( refs[ 0 ] );
				return appDesc;
			}
		}catch( InvalidSyntaxException e ) {}
		return null;
	}

	void register() {
		Hashtable props = new Hashtable();
		props.put( ScheduledApplication.APPLICATION_PID, getPid() );
		props.put( ScheduledApplication.SCHEDULE_ID, id );
		
		serviceReg = bc.registerService( "org.osgi.service.application.ScheduledApplication", 
				this, props );
		invalid = false;
	}
	
	void unregister() {
		if( serviceReg != null )
		  serviceReg.unregister();
		serviceReg = null;
		invalid = true;
	}
	
	public void remove() {
		checkValidity();
		
		try {
			scheduler.removeScheduledApplication( this );
		}
		catch (Exception e) {
			Activator.log( LogService.LOG_ERROR,
				"Exception occurred at removing a scheduled application!", e);
			
			if( e instanceof SecurityException )
				throw (SecurityException)e;
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(pid);
		out.writeObject(args);
		out.writeObject(topic);
		out.writeObject(eventFilter);
		out.writeObject( new Boolean( recurring ) );
		out.writeObject(id);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		bc = null;
		pid= (String) in.readObject();
		args = (HashMap) in.readObject();
		topic = (String) in.readObject();
		eventFilter = (String) in.readObject();
		Boolean recurring = (Boolean) in.readObject();		
		this.recurring = recurring.booleanValue();
		id= (String)in.readObject();		
	}
	
	boolean isValid() {
		return !invalid;
	}
	
	private void checkValidity() {
		if( invalid )
			throw new IllegalStateException();
	}
	
	ServiceReference getReference() {
		return serviceReg.getReference();
	}

	public String getScheduleId() {
		return id;
	}
}
