package org.osgi.meglet;

import java.io.*;
import java.util.*;

import org.osgi.service.component.*;
import org.osgi.service.event.*;
import org.osgi.framework.*;

import org.osgi.service.log.LogService;

/**
 * Abstract base class for Meglets. Developers are expected to extend this class
 * to implement the behavior and event handling for their applications.
 * <p>
 * This class provides many convenience methods which cannot be overridden.
 */
public abstract class Meglet implements EventHandler, ComponentInstance {

	/**
	 * Stores the component context received in the activate() method.
	 */
	private ComponentContext componentContext;

	/**
	 * Returns the component context assigned to the Meglet instance.
	 * 
	 * @return the component context
	 */
	protected final ComponentContext getComponentContext() {
		return componentContext;
	}

	/**
	 * This method is called by the framework to start this application
	 * instance. Must not be called directly.
	 * 
	 * @param args
	 *            The startup parameters for the application instance as
	 *            key-value pairs.
	 * @param stateStorage
	 *            the input stream from where the application can load its saved
	 *            inner state. It is null if a brand new instance of the Meglet
	 *            is started, so no previously saved state exist to be loaded.
	 *            It is not mandated to close the stream.
	 * 
	 * @throws Exception
	 *             if any error occures
	 */
	public void start(Map args, InputStream stateStorage) throws Exception {
	}

	/**
	 * This method is called by the framework to stop this application instance.
	 * Must not be called directly. The method must free all the used resources.
	 * 
	 * @param stateStorage
	 *            the output stream to where the application can save its inner
	 *            state. If it is null then the Meglet instance is stopped
	 *            permanently so saving its inner state is not expected. It is
	 *            not mandated to close the stream.
	 * 
	 * @throws Exception
	 *             if any error occures
	 */
	public void stop(OutputStream stateStorage) throws Exception {
	}

	/**
	 * Receives events. By default it does nothing so the developer has to
	 * override it to deal with events.
	 * <p>
	 * This method must not be called directly.
	 * 
	 * @param event
	 *            the event to be handled, not null
	 */
	public void handleEvent(Event event) {
		// default implementation does nothing
	}

	/**
	 * The application can ask the environment for suspending. The environment
	 * must call the stop() method at some point with non-null stateStorage
	 * parameter.
	 */
	protected final void requestSuspend() {
		class SuspenderThread extends Thread {
			public void run() {
        
				try {
					appHandle.suspend();
				}catch( Exception e ) {
					e.printStackTrace();
				}          
			};
		}
		
		SuspenderThread st = new SuspenderThread();
		st.start();
	}

	/**
	 * The application can ask the environment for stopping. The environment
	 * must call the stop() method at some point with null stateStorage
	 * parameter.
	 */
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

	/**
	 * Retrieves the event channel service if available. Otherwise returns null.
	 * 
	 * @return the event channel service, or null if it is not available
	 */
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

	/**
	 * Retrieves the log service if available. Otherwise returns null.
	 * 
	 * @return the retrieved log service, or null if it is not available
	 */
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

	/**
	 * Registers the Meglet to listen to events with the specified topic and
	 * eventFilter.
	 * 
	 * @param topic
	 *            the topic of the event the subscriber is interested to, it may
	 *            contain a trailing asterisk as wildcard, the empty string is
	 *            treated as "*", must not be null
	 * @param eventFilter
	 *            LDAP filter for the event as defined in Generic Event
	 *            Mechanism RFC, may be null
	 * @throws NullPointerException
	 *             if topic is null
	 */
	protected final void registerForEvents(String topic, String eventFilter) {
		/* TODO */
		listenedTopics.add( topic );
		changeServiceRegistration();
	}

	/**
	 * Unregisters the Meglet not to listen to the specified event. It works
	 * only if it was registered by the registerForEvents() method to listen to
	 * event with exactly the same topic and eventFilter.
	 * 
	 * @param topic
	 *            the topic of the event the subscriber is interested to, it may
	 *            contain a trailing asterisk as wildcard, the empty string is
	 *            treated as "*", must not be null
	 * @param eventFilter
	 *            LDAP filter for the event as defined in Generic Event
	 *            Mechanism RFC, may be null
	 * @throws NullPointerException
	 *             if topic is null
	 */
	protected final void unregisterForEvents(String topic, String eventFilter) {
		/* TODO */
		listenedTopics.remove( topic );
		changeServiceRegistration();
	}

	/**
	 * Retrieves the service properties of the application descriptor of this
	 * application instance.
	 * 
	 * @return the properties of the application descriptor
	 */
	protected final Dictionary getProperties() {
		return null;
	}

	/**
	 * Called by the framework when this component is activated. Called before
	 * the start().
	 * 
	 * @param context
	 *            the component context received from the framework
	 */
	protected final void activate(ComponentContext context) {
		componentContext = context;
	}

	/**
	 * Called by the framework when this component is deactivated. Called after
	 * the stop().
	 * 
	 * @param context
	 *            the component context received from the framework
	 */
	protected final void deactivate(ComponentContext context) {
		componentContext = null;
	}

	private ServiceReference eventAdminServiceRef = null;
	private EventAdmin eventAdmin = null;
	private ServiceReference logServiceServiceRef = null;
	private LogService logService = null;

	private BundleContext bc = null;
	private MegletHandle appHandle = null;

	private ServiceRegistration serviceListenerRegistration = null;
	private Vector listenedTopics = new Vector();

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

	private void init( MegletHandle appHandle, BundleContext bc ) {
		this.bc = bc;
		this.appHandle = appHandle;
		changeServiceRegistration();
	}
	
	private final void startApplication( Map args, InputStream stateStorage ) throws Exception {
		start( args, stateStorage );
	}

	private final void stopApplication( OutputStream stateStorage ) throws Exception {
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
}