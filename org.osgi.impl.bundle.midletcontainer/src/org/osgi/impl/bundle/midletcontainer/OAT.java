/*
 * $Header$
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

import org.osgi.framework.*;

import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import org.osgi.service.application.*;
import org.osgi.service.log.LogService;

public class OAT implements OATContainerInterface, SynchronousBundleListener {
	
	private Hashtable oatHashtable = null;
	private Hashtable oatAppDescHash = null;
	private Hashtable oatBaseClasses = null;
	private ServiceRegistration containerService = null;
	private BundleContext bc = null;

	public OAT() {
		oatHashtable = new Hashtable();
		oatAppDescHash = new Hashtable();
		oatBaseClasses = new Hashtable();
	}
	
	public void start( BundleContext bc ) throws Exception {
		this.bc = bc;
		setApplicationContextHashRef();
		
		containerService = bc.registerService( OATContainerInterface.class.getName(), this, new Hashtable() );
		bc.addBundleListener( this );
	}
	
	public void stop( BundleContext bc ) throws Exception {
		containerService.unregister();
		bc.removeBundleListener( this );
		
		oatHashtable = null;
		bc = null;
	}
	
	public void registerOATBundle(Bundle bundle, String baseClasses[] ) throws Exception {
		oatBaseClasses.put( bundle, baseClasses );
		getOATAppDatas( bundle );
	}
	
	public void createApplicationContext(Object mainClass, ApplicationHandle appHandle, Map args, Bundle bundle )
			throws Exception {
		
		if( !isLaunchable( bundle, mainClass.getClass().getName() ) )
			throw new Exception( "ApplicationContext can only be created for launchable applications!" );
		
		OATApplicationData []oatAppDatas = getOATAppDatas( bundle );
		if( oatAppDatas == null )
			throw new Exception( "Application bundle is not registered in OAT" );
		
		for( int i = 0; i != oatAppDatas.length; i++) {			
			if( oatAppDatas[ i ].getBaseClass().equals( mainClass.getClass().getName() ) ) {
				OATApplicationContextImpl appCtx = new OATApplicationContextImpl( bundle, args, oatAppDatas[ i ], appHandle, mainClass );						
				oatHashtable.put( mainClass, appCtx );
				return;
			}
		}
		throw new Exception( "Application base class is not registered in OAT" );		
	}

	public void removeApplicationContext(Object mainClass) throws Exception {
		OATApplicationContextImpl appCtx = (OATApplicationContextImpl)oatHashtable.remove( mainClass );
		appCtx.destroy();
	}
	
	private void setApplicationContextHashRef() throws Exception {
		Class appFrameworkClass = org.osgi.application.Framework.class;
		Field field = appFrameworkClass.getDeclaredField( "appContextHash" );
		field.setAccessible(true);
		field.set( null, oatHashtable);		
	}

	public void bundleChanged(BundleEvent event) {
		if( event.getType() == BundleEvent.UNINSTALLED ) {
			oatAppDescHash.remove( event.getBundle() );
			oatBaseClasses.remove( event.getBundle() );
			/* Do we need to terminate all of the applications? */
		}
	}

	public boolean isLaunchable(Bundle bundle, String baseClass) {
		try {
			OATApplicationData []oatAppDatas = getOATAppDatas( bundle );
			if( oatAppDatas == null )
				return false;

			for( int i = 0; i != oatAppDatas.length; i++) {			
				if( oatAppDatas[ i ].getBaseClass().equals( baseClass ) ) {
					OATApplicationData appData = oatAppDatas[ i ];
					
					for( int j = 0; j != appData.getServices().length; j++ ) {
						OATServiceData service = appData.getServices()[ j ];
						
						if( service.getCardinality() == OATServiceData.CARDINALITY1_1 ||
								service.getCardinality() == OATServiceData.CARDINALITY1_n ) {
						
							ServiceReference refs[] = null;
					    try {
						    refs = bc.getServiceReferences( service.getInterface(), service.getTarget() );
					    }catch( InvalidSyntaxException e ) {
							  Activator.log( LogService.LOG_ERROR, "Invalid filter syntax for reference '" + service.getName() + "'!", e );
							  return false;
						  }
					    
					    if( refs == null || refs.length == 0 )
					    	return false;					    	
						}						
					}
					return true;
				}
			}			
		}catch( Exception e ){
			return false;			
		}
		return false;
	}

	private OATApplicationData [] getOATAppDatas(Bundle bundle) throws Exception {
		OATApplicationData []oatAppDatas = (OATApplicationData [])oatAppDescHash.get( bundle );
		if( oatAppDatas != null )
			return oatAppDatas;

		if( bundle.getState() != Bundle.ACTIVE )
			return null;
		
		String []baseClasses = (String [])oatBaseClasses.get( bundle );
		if( baseClasses == null )
			return null;
		
		URL url = bc.getBundle( bundle.getBundleId() ).getResource(	"OSGI-INF/app/apps.xml");

		try {
		  oatAppDatas = (new OATXMLParser( bc )).parse( url );
		  if( oatAppDatas.length != baseClasses.length )
		  	throw new Exception( "Inconsistency between the apps.xml and container settings!" );
		  
		  List asList = Arrays.asList( baseClasses );
		  for( int q = 0; q != baseClasses.length; q++ )
		  	if( !asList.contains( oatAppDatas[ q ].getBaseClass() ) )
			  	throw new Exception( "Inconsistency between the apps.xml and container settings!" );
		  
		}catch( Exception e ) {
		  Activator.log( LogService.LOG_ERROR, "Exception occurred at parsing apps.xml file!", e );
		  
			oatAppDatas = new OATApplicationData[ baseClasses.length ];
			for( int i=0; i != baseClasses.length; i++ )
				oatAppDatas[ i ] = new OATApplicationData( baseClasses[ i ], new OATServiceData[ 0 ] );
		}

		oatAppDescHash.put( bundle, oatAppDatas );
		return oatAppDatas;
	}
}
