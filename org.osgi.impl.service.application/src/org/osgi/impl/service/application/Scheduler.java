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
import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.event.*;
import org.osgi.service.log.LogService;

public class Scheduler implements Runnable, EventHandler {
	private BundleContext	bc;
	private Vector							scheduledApps;
	private boolean							stopped;
	private Thread							schedulerThread;
	private ServiceRegistration	serviceReg;

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
	}

	public void stop() {
		stopped = true;
		schedulerThread.interrupt();
	}

	public synchronized ScheduledApplication addScheduledApplication(ApplicationDescriptor appDesc,
			Map arguments, String topic, String eventFilter, boolean recurring) throws InvalidSyntaxException {

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
				appDesc.getApplicationId(), arguments, topic, eventFilter, recurring);
		
		scheduledApps.add( app );
		changeServiceReg();
		saveScheduledApplications();
		schedulerThread.interrupt();
		app.register();
		return app;
	}

	public synchronized void removeScheduledApplication(
			ScheduledApplication scheduledApplication) throws Exception {

		SecurityManager sm = System.getSecurityManager();
		if( sm != null )
			sm.checkPermission(new ApplicationAdminPermission(
				((ScheduledApplicationImpl)scheduledApplication).getApplicationDescriptor(),
				ApplicationAdminPermission.SCHEDULE_ACTION));

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
			topics.add( ((ScheduledApplication)it.next() ).getTopic() );
		
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

	public synchronized void handleEvent(Event e) {
		Iterator it = scheduledApps.iterator();
		Vector removeList = new Vector();
		
		try {

				while ( it.hasNext() ) {
					ScheduledApplicationImpl schedApp = (ScheduledApplicationImpl) it.next();

					if( !schedApp.isEnabled() )
						continue;
					
				  if ((schedApp.getTopic() != null)
						&& e.matches(bc.createFilter("("
								+ EventConstants.EVENT_TOPIC + "="
								+ schedApp.getTopic() + ")")))
				  	
  					if ((schedApp.getEventFilter() == null) || e.matches(bc.createFilter(schedApp
									.getEventFilter()))) {
  						
  						try {
  							schedApp.setEnabled( false ); // to avoid recursion problems
  							ApplicationDescriptor appDesc = schedApp.getApplicationDescriptor();
  							appDesc.launch(schedApp.getArguments());

  							if (!schedApp.isRecurring())
							    removeList.add( schedApp );
  							else
  								schedApp.setEnabled( true );
  						}catch( Exception ex ) {
  							schedApp.setEnabled( true );
  							throw ex;
  						}
							
					}
				}
		
				it = removeList.iterator();
				while ( it.hasNext() ) {
					ScheduledApplicationImpl schedApp = (ScheduledApplicationImpl) it.next();
					removeScheduledApplication( schedApp );
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

	public void run() {
		while (!stopped) {
			try {
				Thread.sleep( 1000 );
			}
			catch (InterruptedException e) {}
			
			Calendar calendar = Calendar.getInstance();

			Hashtable props = new Hashtable();
			props.put( "year", 				new Integer( calendar.get( Calendar.YEAR ) ) );
			props.put( "month", 			new Byte( (byte)calendar.get( Calendar.MONTH ) ) );
			props.put( "day", 				new Byte( (byte)calendar.get( Calendar.DAY_OF_MONTH ) ) );
			props.put( "day_of_week", new Byte( (byte)calendar.get( Calendar.DAY_OF_WEEK ) ) );
			props.put( "hour", 				new Byte( (byte)calendar.get( Calendar.HOUR_OF_DAY ) ) );
			props.put( "minute", 			new Byte( (byte)calendar.get( Calendar.MINUTE ) ) );
			props.put( "second", 			new Byte( (byte)calendar.get( Calendar.SECOND ) ) );
			
			if( stopped ) /* avoid exception */
				break;
			
		  ServiceReference serviceRef = bc.getServiceReference("org.osgi.service.event.EventAdmin");
			if (serviceRef != null) {
				EventAdmin eventAdmin = (EventAdmin) bc.getService(serviceRef);
				if (eventAdmin != null) {
					try {
						eventAdmin.sendEvent( new Event( "org/osgi/application/timer", props ) );
					}finally {
						bc.ungetService(serviceRef);
					}
				}
			}
		}
	}
}
