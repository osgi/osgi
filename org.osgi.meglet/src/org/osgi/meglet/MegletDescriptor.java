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
package org.osgi.meglet;

import java.util.*;
import org.osgi.framework.*;
import java.lang.reflect.*;
import org.osgi.service.application.*;
import org.osgi.service.log.LogService;


/**
 * Specialization of the application descriptor. Represents a Meglet and
 * provides generic methods inherited from the application descriptor. It is a
 * service.
 */
 
public final class MegletDescriptor extends ApplicationDescriptor {
	private Properties		props;
	private Hashtable		names;
	private Hashtable		icons;
	private BundleContext	bc;
	private String			startClass;
	private String			pid;
	private Bundle			bundle;
	private MegletContainer	megletContainer;
	private String      defaultLanguage;

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

	public MegletDescriptor(BundleContext bc, Properties props, Map names,
			Map icons, String defaultLang, String startClass, Bundle bundle, MegletContainer mc ) throws Exception {
		this.bc = bc;
		this.props = new Properties();
		this.props.putAll(props);
		this.names = new Hashtable(names);
		this.icons = new Hashtable(icons);
		this.startClass = startClass;
		this.bundle = bundle;
		this.megletContainer = mc;
		if (names.size() == 0 || icons.size() == 0
				|| !props.containsKey("application.bundle.id")
				|| !props.containsKey( APPLICATION_PID )
				|| !props.containsKey( APPLICATION_VERSION ))
			throw new Exception("Invalid MEG container input!");
		if( !names.containsKey( defaultLang ) )
			throw new Exception("Invalid default language!");
		this.defaultLanguage = defaultLang;
		pid = props.getProperty( APPLICATION_PID );
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
		String singleton = props.getProperty( APPLICATION_SINGLETON );
		return singleton == null || singleton.equalsIgnoreCase("true");
	}


	/**
	 * @return
	 */
	public String getStartClass() {
		return startClass;
	}

	/**
	 * Returns the unique identifier of the represented Meglet.
	 * 
	 * @return the unique identifier of the represented Meglet
	 * 
	 * @throws IllegalStateException
	 *             if the Meglet descriptor is unregistered
	 */
	public String getPID() {
		return new String( pid );
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

	public Map getProperties(String locale) {
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
		properties.put( APPLICATION_NAME, localizedName);
		properties.put( APPLICATION_ICON, icons.get(locale) );

		properties.put("application.bundle.id", props.getProperty("application.bundle.id"));
		properties.put( APPLICATION_VERSION, props.getProperty( APPLICATION_VERSION ));
		properties.put( APPLICATION_VENDOR, props.getProperty( APPLICATION_VENDOR ));
		String singleton = props.getProperty( APPLICATION_SINGLETON );
		if (singleton == null || singleton.equalsIgnoreCase("true"))
			properties.put( APPLICATION_SINGLETON, "true");
		else
			properties.put( APPLICATION_SINGLETON, "false");
		String autostart = props.getProperty( APPLICATION_AUTOSTART );
		if (autostart != null && autostart.equalsIgnoreCase("true"))
			properties.put( APPLICATION_AUTOSTART, "true");
		else
			properties.put( APPLICATION_AUTOSTART, "false");
		String visible = props.getProperty( APPLICATION_VISIBLE );
		if (visible != null && visible.equalsIgnoreCase("false"))
			properties.put( APPLICATION_VISIBLE, "false");
		else
			properties.put( APPLICATION_VISIBLE, "true");
		boolean launchable = false;
		try {
			launchable = megletContainer.isLaunchable( this );
		}catch (Exception e) {
			log( LogService.LOG_ERROR,
				"Exception occurred at searching the Meglet container reference!", e);
		}
		properties.put("application.locked", (new Boolean(isLocked())).toString());
		properties.put("application.launchable", (new Boolean(launchable)).toString());
		properties.put("application.type", "MEG");
		properties.put( APPLICATION_PID, new String( pid ) );
		return properties;
	}

	void initMeglet( Meglet meglet, MegletHandle handle ) throws Exception {
		Class megletClass = Meglet.class;
		Method setupMethod = megletClass.getDeclaredMethod( "init", new Class [] {
										MegletHandle.class, BundleContext.class } );
		setupMethod.setAccessible( true );
		setupMethod.invoke( meglet, new Object [] { handle, bc } );
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
	protected ServiceReference launchSpecific( Map args ) throws Exception {
		Meglet meglet = megletContainer.createMegletInstance( this, false );
		MegletHandle appHandle = new MegletHandle( megletContainer, meglet, this, bc);
		if (meglet == null)
			throw new Exception("Cannot create meglet instance!");

		initMeglet( meglet, appHandle );

		return appHandle.startHandle( args );
	}
}
