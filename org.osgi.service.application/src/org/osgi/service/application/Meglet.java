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
	private ApplicationHandle appHandle = null;

	private ServiceRegistration serviceListenerRegistration = null;
	private Vector listenedTopics = new Vector();


	public Meglet() {}

	protected void start( Map args, InputStream stateStorage ) throws Exception {}
	protected void stop( OutputStream stateStorage ) throws Exception {}
	public void handleEvent(Event event) {}

	protected final void requestStop() {
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

	public final void startApplication( Map args ) throws Exception {
		start( args, null );
	}

	public final void stopApplication() throws Exception {
		listenedTopics.clear();
		changeServiceRegistration();

		stop( null );

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

	public abstract void suspendApplication() throws Exception;

	public abstract void resumeApplication() throws Exception;

	void init( ApplicationHandle appHandle, BundleContext bc ) {
		this.bc = bc;
		this.appHandle = appHandle;
		changeServiceRegistration();
	}
}
