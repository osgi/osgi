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
package org.osgi.impl.service.megcontainer;

import java.util.*;

import org.osgi.framework.*;

import java.lang.reflect.*;

import org.osgi.meglet.Meglet;
import org.osgi.service.application.*;
import org.osgi.service.application.meglet.*;
import org.osgi.service.log.LogService;


/**
 * Specialization of the application descriptor. Represents a Meglet and
 * provides generic methods inherited from the application descriptor. It is a
 * service.
 */
 
public final class MegletDescriptorImpl implements MegletDescriptor.Delegate {
	private Properties				props;
	private Hashtable					names;
	private Hashtable					icons;
	private BundleContext			bc;
	private String						startClass;
	private String						pid;
	private Bundle						bundle;
	private String      			defaultLanguage;
	private boolean     			locked;
	private MegletDescriptor  megletDescriptor;
	private MegletContainer   megletContainer;
	private static int        instanceCounter;

	/**
	 * @param bc
	 * @param props
	 * @param names
	 * @param icons
	 * @param defaultLanguage
	 * @param startClass
	 * @param bundle
	 * @param impl
	 */

 	public void init( BundleContext bc, Properties props, Map names, Map icons, String defaultLang, String startClass, 
 										Bundle bundle, MegletContainer megletContainer ) throws Exception {

 		this.bc = bc;
 		this.megletContainer = megletContainer;
		this.props = new Properties();
		this.props.putAll(props);
		this.names = new Hashtable(names);
		this.icons = new Hashtable(icons);
		this.startClass = startClass;
		this.bundle = bundle;
		if (names.size() == 0 || icons.size() == 0
				|| !props.containsKey("application.bundle.id")
				|| !props.containsKey( MegletDescriptor.APPLICATION_PID )
				|| !props.containsKey( MegletDescriptor.APPLICATION_VERSION ))
			throw new Exception("Invalid MEG container input!");
		if( !names.containsKey( defaultLang ) )
			throw new Exception("Invalid default language!");
		this.defaultLanguage = defaultLang;
		pid = props.getProperty( MegletDescriptor.APPLICATION_PID );
	}


	/**
	 * @return
	 */
	public long getBundleId() {
		return Long.parseLong( props.getProperty("application.bundle.id") );
	}

	/**
	 * @return
	 */
	public boolean isSingleton() {
		String singleton = props.getProperty( MegletDescriptor.APPLICATION_SINGLETON );
		return singleton == null || singleton.equalsIgnoreCase("true");
	}


	/**
	 * @return
	 */
	public String getStartClass() {
		return startClass;
	}


	public String getPID() {
		return pid;
	}

	/**
	 * Retrieves the bundle context of the container to which the specialization
	 * of the application descriptor belongs
	 * 
	 * @return the bundle context of the container
	 * 
	 * @throws IllegalStateException
	 *             if the Meglet descriptor is unregistered
	 */
	protected BundleContext getBundleContext() {
		return bc;
	}

	public Map getPropertiesSpecific(String locale) {

		Hashtable properties = new Hashtable();
		String localizedName = (String) names.get(locale);
		if (localizedName == null) {
			if( ( localizedName = (String) names.get( defaultLanguage ) ) == null ) {
				Enumeration enum = names.keys();
				String firstKey = (String) enum.nextElement();
				localizedName = (String) names.get(firstKey);
				locale = firstKey;
			} else
				locale = defaultLanguage;
		}
		properties.put( MegletDescriptor.APPLICATION_NAME, localizedName);
		properties.put( MegletDescriptor.APPLICATION_ICON, icons.get(locale) );

		properties.put("application.bundle.id", props.getProperty("application.bundle.id"));
		properties.put( MegletDescriptor.APPLICATION_VERSION, props.getProperty( MegletDescriptor.APPLICATION_VERSION ));
		properties.put( MegletDescriptor.APPLICATION_VENDOR, props.getProperty( MegletDescriptor.APPLICATION_VENDOR ));
		String singleton = props.getProperty( MegletDescriptor.APPLICATION_SINGLETON );
		if (singleton == null || singleton.equalsIgnoreCase("true"))
			properties.put( MegletDescriptor.APPLICATION_SINGLETON, "true");
		else
			properties.put( MegletDescriptor.APPLICATION_SINGLETON, "false");
		String autostart = props.getProperty( MegletDescriptor.APPLICATION_AUTOSTART );
		if (autostart != null && autostart.equalsIgnoreCase("true"))
			properties.put( MegletDescriptor.APPLICATION_AUTOSTART, "true");
		else
			properties.put( MegletDescriptor.APPLICATION_AUTOSTART, "false");
		String visible = props.getProperty( MegletDescriptor.APPLICATION_VISIBLE );
		if (visible != null && visible.equalsIgnoreCase("false"))
			properties.put( MegletDescriptor.APPLICATION_VISIBLE, "false");
		else
			properties.put( MegletDescriptor.APPLICATION_VISIBLE, "true");
		boolean launchable = false;
		try {
			launchable = megletContainer.isLaunchable( this );
		}catch (Exception e) {
			MegletContainer.log( bc, LogService.LOG_ERROR,
				"Exception occurred at searching the Meglet container reference!", e);
		}
		properties.put("application.locked", (new Boolean( locked )).toString());
		properties.put("application.launchable", (new Boolean(launchable)).toString());
		properties.put("application.type", "MEG");
		properties.put( MegletDescriptor.APPLICATION_PID, new String( pid ) );
		return properties;
	}

	/**
	 * Called by launch() to create and start a new instance in an application
	 * model specific way. It also creates and registeres the application handle
	 * to represent the newly created and started instance.
	 * 
	 * @param arguments
	 *            the startup parameters of the new application instance, may be
	 *            null
	 * 
	 * @return the service reference to the application model specific
	 *         application handle for the newly created and started instance.
	 * 
	 * @throws Exception
	 *             if any problem occures.
	 */
	public ApplicationHandle launchSpecific( Map args ) throws Exception {
		Meglet meglet = megletContainer.createMegletInstance( this, false );
		if (meglet == null)
			throw new Exception("Cannot create meglet instance!");
		
		return createMegletHandleByReflection( meglet, args ); 
	}
	
	public void lockSpecific() {
		locked = true;
	}
	
	public void unlockSpecific() {
		locked = false;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public MegletDescriptor getMegletDescriptor() {
		return megletDescriptor;
	}
	
	static synchronized String createNewInstanceID( String pid ) {
		return new String( pid + ":" + instanceCounter++ );
	}

	public MegletHandle createMegletHandleByReflection( Meglet meglet, Map args ) {
				
		/* That's because of the idiot abstract classes in the API */

		try {
			Class megletHandleClass = MegletHandle.class;
			Constructor constructor = megletHandleClass.getDeclaredConstructor( new Class[] { String.class, MegletDescriptor.class } );
			constructor.setAccessible( true );
			MegletHandle megletHandle = (MegletHandle) constructor.newInstance( 
					new Object[] { createNewInstanceID( pid ), megletDescriptor } );
			
			Field delegate = megletHandleClass.getDeclaredField( "delegate" );
			delegate.setAccessible( true );
			
			MegletHandleImpl megHnd = (MegletHandleImpl)delegate.get( megletHandle );
						
			Method registerListenerMethod = Meglet.class.getDeclaredMethod( "init",
					new Class [] { MegletHandle.class, BundleContext.class } );
			registerListenerMethod.setAccessible( true );
			registerListenerMethod.invoke( meglet, new Object [] { megletHandle, bc  } );			
			
			megHnd.init( bc, megletContainer, meglet );
			megHnd.startHandle( args );
			
			return megletHandle;
		}catch( Exception e )
		{
			MegletContainer.log(bc, LogService.LOG_ERROR,
					"Exception occurred at creating meglet handle!", e);
			return null;
		}		
	}
	
	public void setMegletDescriptor( MegletDescriptor descriptor ) {
		megletDescriptor = descriptor;
	}
}
