/*
 * ============================================================================
 * (c) Copyright 2004 Nokia This material, including documentation and any
 * related computer programs, is protected by copyright controlled by Nokia and
 * its licensors. All rights are reserved.
 * 
 * These materials have been contributed to the Open Services Gateway Initiative
 * (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms
 * of, the OSGi Member Agreement specifically including, but not limited to, the
 * license rights and warranty disclaimers as set forth in Sections 3.2 and 12.1
 * thereof, and the applicable Statement of Work. All company, brand and product
 * names contained within this document may be trademarks that are the sole
 * property of the respective owners. The above notice must be included on all
 * copies of this document.
 * ============================================================================
 */

package org.osgi.impl.service.application;

import java.io.*;
import java.security.Guard;
import java.security.GuardedObject;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.event.*;
import org.osgi.service.log.LogService;

class Defender implements Guard {
	public void checkGuard(Object var0) throws SecurityException {
		SecurityManager sm = System.getSecurityManager();
		if( sm != null ) {
			sm.checkPermission(new TopicPermission( ScheduledApplication.TRIGGERING_EVENT, TopicPermission.SUBSCRIBE ));
		}		
	}
}

public class Scheduler implements Runnable, EventHandler {
	private BundleContext	bc;
	private Vector							scheduledApps;
	private boolean							stopped;
	private Thread							schedulerThread;
	private ServiceRegistration	            serviceReg;
	private LinkedList                      eventQueue;
	private boolean                         inEventHandling;
	private static int                      schedNum = 1;

	public Scheduler(BundleContext bc) {
		this.bc = bc;
		scheduledApps = new Vector();
		loadScheduledApplications();
		stopped = false;
		schedulerThread = new Thread(this);
		schedulerThread.start();
		
		Hashtable props = new Hashtable();
		props.put( EventConstants.EVENT_TOPIC, getRequiredTopics() );
		
		serviceReg = bc.registerService( "org.osgi.service.event.EventHandler", this, props );
		
		eventQueue = new LinkedList();
		inEventHandling = false;
	}

	public void stop() {
		stopped = true;
		schedulerThread.interrupt();
	}

	public synchronized ScheduledApplication addScheduledApplication( String id, ApplicationDescriptor appDesc,
				Map arguments, String topic, String eventFilter, boolean recurring) throws InvalidSyntaxException, ApplicationException {

		if( id == null )
			id = generateKey( appDesc.getApplicationId() );
		else
			checkKey( id, appDesc.getApplicationId() );
		
		SecurityManager sm = System.getSecurityManager();
		if( sm != null ) {
			sm.checkPermission(new ApplicationAdminPermission( appDesc, 
					ApplicationAdminPermission.SCHEDULE_ACTION));
		}

		if( topic == null )
			throw new NullPointerException();
		
		if( topic.equals("") )
			topic = "*";
		
		if( eventFilter != null )
			bc.createFilter( eventFilter ); // throws InvalidSyntaxException if any problem occurs
		
		ScheduledApplicationImpl app = new ScheduledApplicationImpl(this, bc,
				appDesc.getApplicationId(), arguments, topic, eventFilter, recurring, id);
		
		scheduledApps.add( app );
		saveScheduledApplications();
		changeServiceReg();
		app.register();
		schedulerThread.interrupt();
		return app;
	}

	public synchronized void removeScheduledApplication(
			ScheduledApplication scheduledApplication) throws Exception {

		// if the appDesc of the schedule was already uninstalled, everyone can remove the schedule
		ApplicationDescriptor appDesc = ((ScheduledApplicationImpl)scheduledApplication).getApplicationDescriptor(); 
		
		SecurityManager sm = System.getSecurityManager();
		if( appDesc != null && sm != null )
			sm.checkPermission(new ApplicationAdminPermission(
				appDesc, ApplicationAdminPermission.SCHEDULE_ACTION));

		scheduledApps.remove( scheduledApplication );
		changeServiceReg();
		saveScheduledApplications();
		((ScheduledApplicationImpl) scheduledApplication).unregister();
	}

	private void changeServiceReg() {
		Hashtable props = new Hashtable();
		props.put( EventConstants.EVENT_TOPIC, getRequiredTopics() );
		serviceReg.setProperties( props );
	}
	
	private synchronized String [] getRequiredTopics() {
		Vector topics = new Vector();
		
		Iterator it = scheduledApps.iterator();
		while( it.hasNext() )
			topics.add( ((ScheduledApplicationImpl)it.next() ).getRequiredTopic() );
		
		String topicsArray[] = new String [ topics.size() ];
		for( int i=0; i != topics.size(); i++ )
			topicsArray[ i ] = (String)topics.get( i );		
		return topicsArray;
	}
	
	private synchronized void loadScheduledApplications() {
		try {
			File schedApps = bc.getDataFile("ScheduledApplications");
			if (!schedApps.exists())
				return;
			FileInputStream stream = new FileInputStream(schedApps);
			ObjectInputStream is = new ObjectInputStream(stream);
			scheduledApps = (Vector) is.readObject();
			is.close();
			Iterator it = scheduledApps.iterator();
			while (it.hasNext()) {
				ScheduledApplicationImpl schedApp = (ScheduledApplicationImpl) it
						.next();
				schedApp.validate(this, bc);
				schedApp.register();
			}
		}
		catch (Exception e) {
			Activator.log( LogService.LOG_ERROR,
					"Exception occurred at loading the scheduled applications!",
					e);
		}
	}

	String generateKey( String pid ) throws InvalidSyntaxException {
		ServiceReference refs[] = bc.getServiceReferences( ScheduledApplication.class.getName(), 
				"(" + ApplicationDescriptor.class.getName() + "=" + pid + ")" );
		while(true) {
			String plannedSchedID = "S" + schedNum++;
			
			boolean found = true;
			if( refs != null ) {
				for( int p=0; p != refs.length; p++ ) {
					Object actID = refs[ p ].getProperty( ScheduledApplication.SCHEDULE_ID );
					if( actID != null && actID.equals( plannedSchedID ) ) {
						found = false;
						break;
					}
				}
			}
			if( found )
				return plannedSchedID;
		}
	}

	void checkKey( String key, String pid ) throws ApplicationException, InvalidSyntaxException {
		ServiceReference refs[] = bc.getServiceReferences( ScheduledApplication.class.getName(), 
				"(" + ScheduledApplication.APPLICATION_PID + "=" + pid + ")" );
		if( refs != null ) {
			for( int p=0; p != refs.length; p++ ) {
				Object actID = refs[ p ].getProperty( ScheduledApplication.SCHEDULE_ID );
				if( actID != null && actID.equals( key ) )
					throw new ApplicationException( ApplicationException.APPLICATION_DUPLICATE_SCHEDULE_ID );
			}
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
			Activator.log( LogService.LOG_ERROR,
					"Exception occurred at saving the scheduled applications!",
					e);
		}
	}

	public synchronized void handleEvent(Event event) {
		eventQueue.add( event );
		if( inEventHandling )
			return;
		
		inEventHandling = true;
		
		while( eventQueue.size() != 0 ) {
		
			Event e = (Event)eventQueue.removeFirst();
			
			Iterator it = scheduledApps.iterator();
			Vector removeList = new Vector();
		
			while ( it.hasNext() ) {
				ScheduledApplicationImpl schedApp = (ScheduledApplicationImpl) it.next();

				if( !schedApp.isValid() )
					continue;
					
				try {						
					if ((schedApp.getTopic() != null)
							&& e.matches(bc.createFilter("("
									+ EventConstants.EVENT_TOPIC + "="
									+ schedApp.getTopic() + ")")))
				  	
					if ((schedApp.getEventFilter() == null) || e.matches(bc.createFilter(schedApp
								.getEventFilter()))) {
  						
						ApplicationDescriptor appDesc = schedApp.getApplicationDescriptor();
						
						if( appDesc == null )        /* is the application descriptor uninstalled? */
							throw new Exception( "Doesn't find the registered application descriptor!" );
						else {
							Map args = schedApp.getArguments();
							if( args == null )
								args = new Hashtable();
							
							args.put( ScheduledApplication.TRIGGERING_EVENT, new GuardedObject( event, new Defender() ) );
							appDesc.launch( args );

							if (!schedApp.isRecurring())
								removeList.add( schedApp );
						}
  					}
				}
				catch (Exception ex) {
					ex.printStackTrace();
					Activator.log(
							LogService.LOG_ERROR,
							"Exception occurred at scheduling an application!",
							ex);
				}
			}
			
			it = removeList.iterator();
			while ( it.hasNext() ) {
				ScheduledApplicationImpl schedApp = (ScheduledApplicationImpl) it.next();
				try {
					removeScheduledApplication( schedApp );
				}catch( Exception exx ) {
					Activator.log(
							LogService.LOG_ERROR,
							"Error at removing a scheduled application!",
							exx);					
				}
			}
		}
		
		inEventHandling = false;
	}

	public void run() {
		while (!stopped) {
			try {
				Thread.sleep( 60000 );
			}
			catch (InterruptedException e) {}
			
			Calendar calendar = Calendar.getInstance();

			Hashtable props = new Hashtable();
			props.put( ScheduledApplication.YEAR,			new Integer( calendar.get( Calendar.YEAR ) ) );
			props.put( ScheduledApplication.MONTH,			new Integer( calendar.get( Calendar.MONTH ) ) );
			props.put( ScheduledApplication.DAY_OF_MONTH,	new Integer( calendar.get( Calendar.DAY_OF_MONTH ) ) );
			props.put( ScheduledApplication.DAY_OF_WEEK,	new Integer( calendar.get( Calendar.DAY_OF_WEEK ) ) );
			props.put( "day_of_week",						new Integer( calendar.get( Calendar.DAY_OF_WEEK ) ) );
			props.put( ScheduledApplication.HOUR_OF_DAY,	new Integer( calendar.get( Calendar.HOUR_OF_DAY ) ) );
			props.put( ScheduledApplication.MINUTE,			new Integer( calendar.get( Calendar.MINUTE ) ) );
			
			if( stopped ) /* avoid exception */
				break;
			
			ServiceReference serviceRef = bc.getServiceReference("org.osgi.service.event.EventAdmin");
			if (serviceRef != null) {
				EventAdmin eventAdmin = (EventAdmin) bc.getService(serviceRef);
				if (eventAdmin != null) {
					try {
						eventAdmin.sendEvent( new Event( ScheduledApplication.TIMER_TOPIC, props ) );
					}finally {
						bc.ungetService(serviceRef);
					}
				}
			}
		}
	}
}
