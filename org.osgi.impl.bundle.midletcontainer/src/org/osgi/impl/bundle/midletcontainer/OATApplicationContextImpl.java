/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.impl.bundle.midletcontainer;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import org.osgi.application.ApplicationContext;
import org.osgi.application.ApplicationServiceEvent;
import org.osgi.application.ApplicationServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.log.LogService;

public class OATApplicationContextImpl implements ApplicationContext, ServiceListener {
	
	BundleContext bc = null;
	Map startupParams = null;
	LinkedList serviceList = null;
	LinkedList registeredServiceList = null;
	LinkedList serviceListenerList = null;
	Vector mandatoryServiceList = null;
	Vector mandatoryTargetList = null;
	OATApplicationData oatAppData = null;
	ApplicationHandle appHandle = null;
	Object mainClass = null;
	
	String instanceId = null;
	String applicationId = null;

	class Service {
		OATServiceData    serviceData;
		Object            serviceObject;
		ServiceReference  serviceReference;
	}
	
	class ServiceListenerImpl {
		
		public ServiceListenerImpl( ApplicationServiceListener listener, String []interfaces, Filter []filters ) {
			this.listener = listener;
			this.filters = filters;
			this.interfaces = interfaces;
		}
		
		public boolean match( String []ifaces, Hashtable props ) {
			for( int n=0; n != ifaces.length; n++ )
				for( int p=0; p != interfaces.length; p++ ) {
					if( ifaces[ n ].equals( interfaces[ p ])) {
						if( filters[ n ] == null )
							return true;
						if( filters[ n ].match( props ) )
							return true;
					}
				}
			return false;
		}
		
		ApplicationServiceListener listener;
		Filter []filters;
		String []interfaces;
	}
		
	public OATApplicationContextImpl( Bundle bundle, Map startupParams, OATApplicationData appData, ApplicationHandle appHandle, Object mainClass ) {
		bc = bundle.getBundleContext();
		this.startupParams = startupParams;
		this.mainClass = mainClass;
		serviceList = new LinkedList();
		registeredServiceList = new LinkedList();
		serviceListenerList = new LinkedList();
		oatAppData = appData;
		
		mandatoryServiceList = new Vector();
		mandatoryTargetList = new Vector();
		
		for( int i=0; i != appData.getServices().length; i++ ) {
			OATServiceData service = appData.getServices()[ i ];
			if( service.getCardinality() == OATServiceData.CARDINALITY1_1 ||
					service.getCardinality() == OATServiceData.CARDINALITY1_n )
				mandatoryServiceList.add( service.getInterface() );
			  mandatoryTargetList.add( service.getTarget() );
		}			
		
		this.appHandle = appHandle;
		bc.addServiceListener( this );
		
		instanceId = appHandle.getInstanceId();
		applicationId = appHandle.getApplicationDescriptor().getApplicationId();
	}

	public void addServiceListener(ApplicationServiceListener listener, String referenceName)
	throws IllegalArgumentException {
		if( referenceName == null )
			throw new NullPointerException( "Reference name cannot be null!" );
		
		addServiceListener( listener, new String[] {referenceName} );
	}

	public void addServiceListener(ApplicationServiceListener listener, String []referenceNames)
			throws IllegalArgumentException {
		
		if( listener == null )
			throw new NullPointerException( "ApplicationServiceListener cannot be null!" );
		if( referenceNames == null )
			throw new NullPointerException( "Reference names cannot be null!" );
		if( referenceNames.length == 0 )
			throw new IllegalArgumentException( "At least one reference name must be given!" );
		
		if( appHandle == null )
			throw new IllegalStateException( "Application is not running!" );

		Filter filters[] = new Filter[ referenceNames.length ];
		String interfaces[] = new String[ referenceNames.length ];
		
		for( int q=0 ; q != referenceNames.length; q++ ) {
			int i = 0;
			for( ; i != oatAppData.getServices().length; i++ )
				if( oatAppData.getServices()[ i ].getName().equals( referenceNames[ q ] )) {
					interfaces[ q ] = oatAppData.getServices()[ i ].getInterface();
					try {
						String filterStr = oatAppData.getServices()[ i ].getTarget();
						filters[ q ] = filterStr == null ? null : bc.createFilter( filterStr );
					}catch( InvalidSyntaxException e ) {
						throw new IllegalArgumentException();
					}
					break;
				}
			if( i == oatAppData.getServices().length )
				throw new IllegalArgumentException();
		}

		removeServiceListener( listener );		
		serviceListenerList.add( new ServiceListenerImpl( listener, interfaces, filters ) );
	}
	
	public Map getStartupParameters() {
		if( appHandle == null )
			throw new IllegalStateException( "Application is not running!" );
		
		return startupParams;
	}
	
	public Object locateService(String referenceName) {
		if( referenceName == null )
			throw new NullPointerException( "Reference name cannot be null!" );
		if( appHandle == null )
			throw new IllegalStateException( "Application is not running!" );
		
		for( int i=0; i != oatAppData.getServices().length; i++ ) {
			if( oatAppData.getServices()[ i ].getName().equals( referenceName )) {				
				
				/* checks whether a reference exists in the service list */
				
				Iterator iterator = serviceList.iterator();
				while( iterator.hasNext() ) {
					Service serv = (Service)iterator.next();
					
					if( serv.serviceData.getName().equals( referenceName ) ) {
						if( serv.serviceReference.getBundle() == null ) {
							if( removeService( serv ) )
								throw new RuntimeException( "Static service terminated!" ); /* termination is requested */
							iterator = serviceList.iterator();
						} else							
						  return serv.serviceObject;
					}
				}
				
				/* getting the service references */
				
				OATServiceData service = oatAppData.getServices()[ i ];
				
				ServiceReference refs[] = null;
				
				try {
		      refs = bc.getServiceReferences( service.getInterface(),
		    	                                service.getTarget() );
				}catch( InvalidSyntaxException e ) 
				{
					Activator.log( LogService.LOG_ERROR, "Invalid filter syntax for reference '" + referenceName + "'!", e );
					throw new IllegalArgumentException();
				}
				
		    if( refs == null || refs.length == 0 ) {
		    	if( service.getCardinality() == OATServiceData.CARDINALITY1_1 || 
		    			service.getCardinality() == OATServiceData.CARDINALITY1_n ) {
		    		requestTermination();
		    		throw new RuntimeException( "The requested service not found!" );
		    	}
		    	return null;
		    }
		    
		    ServiceReference selectedReference = refs[ 0 ];
		    
		    Integer selRanking = (Integer)refs[ 0 ].getProperty( Constants.SERVICE_RANKING );		    
		    int              selectedRanking   = (selRanking == null) ? 0 : selRanking.intValue();
		    long             selectedServiceId = ((Long)refs[ 0 ].getProperty( Constants.SERVICE_ID )).longValue(); 
		    
		    for( int j=1; j < refs.length; j++ ) {
			    Integer rankW = (Integer)refs[ j ].getProperty( Constants.SERVICE_RANKING );		    
			    int  rank = (rankW == null) ? 0 : rankW.intValue();
		    	long serviceID = ((Long)refs[ j ].getProperty( Constants.SERVICE_ID )).longValue();
		    	
		    	if( rank > selectedRanking || ( rank == selectedRanking && serviceID < selectedServiceId) )
		    		selectedReference = refs[ j ];
		    }

		    Service srv = new Service();
		    srv.serviceData = service;
		    srv.serviceReference = selectedReference;
		    srv.serviceObject = bc.getService( selectedReference );
		    
		    serviceList.add( srv );
		    return srv.serviceObject;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public Object[] locateServices(String referenceName) {
		if( referenceName == null ) /* TODO: add it into the specification!!! */
			throw new NullPointerException( "Reference name cannot be null!" );
		if( appHandle == null )
			throw new IllegalStateException( "Application is not running!" );
		
		for( int i=0; i != oatAppData.getServices().length; i++ ) {
			if( oatAppData.getServices()[ i ].getName().equals( referenceName )) {
								
				/* getting the service references */
				
				OATServiceData service = oatAppData.getServices()[ i ];
				
				ServiceReference refs[] = null;
				
				try {
		      refs = bc.getServiceReferences( service.getInterface(),
		    	                                service.getTarget() );
				}catch( InvalidSyntaxException e ) 
				{
					Activator.log( LogService.LOG_ERROR, "Invalid filter syntax for reference '" + referenceName + "'!", e );
					return new Object[ 0 ];
				}
				
				if( refs == null || refs.length == 0 ) {
		    	if( service.getCardinality() == OATServiceData.CARDINALITY1_1 || 
		    			service.getCardinality() == OATServiceData.CARDINALITY1_n ) {
		    		requestTermination();
		    		throw new RuntimeException( "The requested service not found!" );
		    	}
		    	return null;					
				}
				
				Object[] result = new Object[ refs.length ];
				for( int j=0; j != refs.length; j++ ) {
					Service serv = getServiceByReference( refs[ j ] );
					if( serv != null )
						result[ j ] = serv.serviceObject;
					else {
				    serv = new Service();
				    serv.serviceData = service;
				    serv.serviceReference = refs[ j ];
				    result[ j ] = serv.serviceObject = bc.getService( refs[ j ] );						
				    serviceList.add( serv );
					}
				}
				return result;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public ServiceRegistration registerService(String clazz, Object service,
			Dictionary properties) {
		if( appHandle == null )
			throw new IllegalStateException( "Application is not running!" );
		
		if( service == null )
			throw new IllegalArgumentException( "Service cannot be null!" );
		
		if( service == mainClass )
			throw new SecurityException( "Registering the base class of the application is insecure and forbidden!" );
		
		ServiceRegistration servReg = bc.registerService( clazz, service, properties );
		registeredServiceList.add( servReg );
		return servReg;
	}
	
	public ServiceRegistration registerService(String[] clazzes,
			Object service, Dictionary properties) {
		if( appHandle == null )
			throw new IllegalStateException( "Application is not running!" );
		
		if( service == null )
			throw new IllegalArgumentException( "Service cannot be null!" );
		
		if( service == mainClass )
			throw new SecurityException( "Registering the base class of the application is insecure and forbidden!" );
		
		ServiceRegistration servReg = bc.registerService( clazzes, service, properties );
		registeredServiceList.add( servReg );
		return servReg;
	}
	
	public void removeServiceListener(ApplicationServiceListener listener) {
		if( appHandle == null )
			throw new IllegalStateException( "Application is not running!" );
		
		Iterator iter = serviceListenerList.iterator();
		
		while( iter.hasNext() ) {
			ServiceListenerImpl servListener = (ServiceListenerImpl)iter.next();
			if( servListener.listener == listener )
				iter.remove();
		}
	}
	
	void destroy()
	{
		serviceListenerList.clear();
		
		while( !serviceList.isEmpty() ) {
			Service serv = (Service)serviceList.removeFirst();
			if( serv.serviceObject != null ) {
				try {
					bc.ungetService( serv.serviceReference );
				}catch( Exception e ){
					Activator.log( LogService.LOG_ERROR, "Cannot unget reference '" + serv.serviceData.getInterface() + "'!", e );					
				}
			}
		}
		
		while( !registeredServiceList.isEmpty() ) {
			ServiceRegistration servReg = (ServiceRegistration)registeredServiceList.removeFirst();
			if( servReg.getReference().getBundle() != null ) /* service was not unregistered? */
				servReg.unregister();
		}
				
		try {
			bc.removeServiceListener( this );
		}catch( Exception e ) {
			Activator.log( LogService.LOG_ERROR, "Cannot remove application service listener!", e );								
		}
		
		appHandle = null;
		bc = null;
		oatAppData = null;
		serviceList = null;
		startupParams = null;
	}
	
	private void terminateImmediately() {
		Thread destroyerThread = requestTermination();		
		try {
			destroyerThread.join( 5000 );
		}catch(InterruptedException e) {}
		
		if( destroyerThread.isAlive() )
			Activator.log( LogService.LOG_ERROR, "Stop method of the application didn't finish at 5s!", null );		
	}
	
	private boolean removeService( Service service ) {
		serviceList.remove( service );
		bc.ungetService( service.serviceReference );
  	if( service.serviceData.getPolicy() == OATServiceData.STATIC ) {
  		terminateImmediately();
  		return true;
  	}
  	return false;
	}
	
	private Service getServiceByReference( ServiceReference ref ) {
		Iterator iterator = serviceList.iterator();
		while( iterator.hasNext() ) {
			Service serv = (Service)iterator.next();
			
			if( serv.serviceReference == ref ) 
				return serv;
		}
		return null;
	}
	
	private Thread requestTermination() {
		class DestroyerThread extends Thread {
			public void run() {
        
				try {
					if( appHandle != null )
					  appHandle.destroy();
				}catch( Exception e ) {
				}          
			};
		}
		
		DestroyerThread st = new DestroyerThread();
		st.start();	
		
		return st;
	}

	public void serviceChanged(ServiceEvent event) {
		Service serv = getServiceByReference( event.getServiceReference() );
		
		Hashtable eventHash = new Hashtable();
		String []propKeys = event.getServiceReference().getPropertyKeys();
		
		for( int w=0; w != propKeys.length; w++ )
			eventHash.put( propKeys[ w ], event.getServiceReference().getProperty( propKeys[ w ]) );
		
		Iterator iter = serviceListenerList.iterator();
		while( iter.hasNext() ) {
			ServiceListenerImpl servListener = (ServiceListenerImpl)  iter.next();
			
			if( !servListener.match( (String [])event.getServiceReference().getProperty( Constants.OBJECTCLASS ), eventHash ) )
				continue;
			
			Object serviceObject = ( serv == null ) ? null : serv.serviceObject;
			
			servListener.listener.serviceChanged( 
					new ApplicationServiceEvent( event.getType(), event.getServiceReference(), serviceObject ) );
		}
		
		if( event.getType() == ServiceEvent.UNREGISTERING ) {			
			if( serv != null )
				if( removeService( serv ) )
					return;
				
			/* checking whether a mandatory service was stopped */
			
			Object classes[] = (Object []) event.getServiceReference().
			                                 getProperty( Constants.OBJECTCLASS );
			if( classes == null )
				return;
			for( int i = 0; i != classes.length; i++ ) {
				String service = (String)classes[ i ];
				if( mandatoryServiceList.contains( service ) ) {
					int index = mandatoryServiceList.indexOf( service );
					String target = (String)mandatoryTargetList.get( index );
					
					ServiceReference refs[] = null;
					
					try {
					  refs = bc.getServiceReferences( service, target );
					}catch( InvalidSyntaxException e ) {}
					
					if( refs == null || refs.length == 0 ||
							((refs.length == 1 ) && refs[ 0 ] == event.getServiceReference())) {
						terminateImmediately();
						return;
					}
				}
			}
		}
	}

	public Map getServiceProperties(Object serviceObject) {
		if( serviceObject == null )
			throw new NullPointerException();

		if( appHandle == null )
			throw new IllegalStateException( "Application is not running!" );
		
		Iterator iter = serviceList.iterator();
		
		while( iter.hasNext() ) {
			Service service = (Service)iter.next();
			
			if( service.serviceObject == serviceObject ) {
				HashMap props = new HashMap();
				
				String []keys = service.serviceReference.getPropertyKeys();
				for( int i = 0; i != keys.length; i++ )
					props.put( keys[ i ], service.serviceReference.getProperty( keys[ i ] ) );
				
				return props;
			}
		}
		throw new IllegalArgumentException( "Invalid service object!" );
	}

	public String getInstanceId() {
		return instanceId;
	}

	public String getApplicationId() {
		return applicationId;
	}
}
