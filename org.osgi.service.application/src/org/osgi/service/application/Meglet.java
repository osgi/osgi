/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
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

package org.osgi.service.application;

import java.util.*;
import java.io.*;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.service.event.*;

/**
 * A MEG Application. a.k.a Meglet
 */

public abstract class Meglet implements EventHandler {

	private ServiceReference eventAdminServiceRef = null;
	private EventAdmin eventAdmin = null;
	private ServiceReference logServiceServiceRef = null;
	private LogService logService = null;

	private BundleContext bc = null;
	private MegletHandle appHandle = null;

	private ServiceRegistration serviceListenerRegistration = null;
	private Vector listenedTopics = new Vector();


	public Meglet() {}

	protected void start( Map args, InputStream stateStorage ) throws Exception {}
	protected void stop( OutputStream stateStorage ) throws Exception {}
	public void handleEvent(Event event) {}

	protected final void requestStop() {
    class StopperThread extends Thread {
      public void run() {
        
        try {
          appHandle.destroy();
        }catch( Exception e ) {
          e.printStackTrace();
        }          
      }
    };
    
    StopperThread st = new StopperThread();
    st.start();
	}

  protected final void requestSuspend() {
    class SuspenderThread extends Thread {
      public void run() {
        
        try {
          appHandle.suspend();
        }catch( Exception e ) {
          e.printStackTrace();
        }          
      }
    };
    
    SuspenderThread st = new SuspenderThread();
    st.start();
  }

	protected final EventAdmin getEventAdmin() {
		if( eventAdmin != null )
			return eventAdmin;

		try {
			eventAdminServiceRef = bc.getServiceReference( "org.osgi.service.event.EventAdmin" );
			if (eventAdminServiceRef != null) {
				eventAdmin = (EventAdmin) bc.getService( eventAdminServiceRef );
				return eventAdmin;
			}
		} catch( Exception e ) {}

		return null;
	}

	protected final LogService getLogService() {
		if( logService != null )
			return logService;

		try {
			logServiceServiceRef = bc.getServiceReference( "org.osgi.service.log.LogService" );
			if (logServiceServiceRef != null) {
				logService = (LogService) bc.getService( logServiceServiceRef );
				return logService;
			}
		} catch( Exception e ) {}

		return null;
	}

	private void changeServiceRegistration() {
		if( bc == null )
			return;
		if( listenedTopics.size() == 0 ) {
			if( serviceListenerRegistration != null ) {
				serviceListenerRegistration.unregister();
				serviceListenerRegistration = null;
			}
		} else {
			String topic[] = new String [ listenedTopics.size() ];
			for (int k = 0; k != listenedTopics.size(); k++)
				topic[ k ] = (String) listenedTopics.get(k);

			Hashtable props = new Hashtable();
			props.put( EventConstants.EVENT_TOPIC, topic );
			if ( serviceListenerRegistration == null )
				serviceListenerRegistration = bc.registerService(
					EventHandler.class.getName(), this, props);
			else
				serviceListenerRegistration.setProperties( props );
		}
	}

	protected final void registerForEvents( String topicName ) {
		listenedTopics.add( topicName );
		changeServiceRegistration();
	}

	protected final void unregisterForEvents( String topicName ) {
		listenedTopics.remove( topicName );
		changeServiceRegistration();
	}

	public final void startApplication( Map args, InputStream stateStorage ) throws Exception {
		start( args, stateStorage );
	}

	public final void stopApplication( OutputStream stateStorage ) throws Exception {
		listenedTopics.clear();
		changeServiceRegistration();

		stop( stateStorage );

		if( eventAdminServiceRef != null ) {
			bc.ungetService( eventAdminServiceRef );
			eventAdmin = null;
		}

		if( logServiceServiceRef != null ) {
			bc.ungetService( logServiceServiceRef );
			logService = null;
		}
	};

	protected final Dictionary getApplicationProperties() {
		return null;
	}

	void init( MegletHandle appHandle, BundleContext bc ) {
		this.bc = bc;
		this.appHandle = appHandle;
		changeServiceRegistration();
	}
}