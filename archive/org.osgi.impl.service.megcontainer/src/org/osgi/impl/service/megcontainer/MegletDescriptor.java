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
package org.osgi.impl.service.megcontainer;

import java.util.*;
import java.io.*;
import org.osgi.framework.*;
import org.osgi.service.application.*;
import java.lang.reflect.*;
import org.osgi.service.log.LogService;

/**
 * The MEG container's ApplicationDescriptor realization
 */
public class MegletDescriptor extends ApplicationDescriptor {
	private Properties		props;
	private Hashtable		names;
	private Hashtable		icons;
	private BundleContext	bc;
	private String			startClass;
	private String			pid;
	private Bundle			bundle;
	private MegletContainer	megletContainer;

	MegletDescriptor(BundleContext bc, Properties props, Map names,
			Map icons, String startClass, Bundle bundle, MegletContainer mc ) throws Exception {
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
		pid = props.getProperty( APPLICATION_PID );
	}

	public long getBundleId() {
		return Long.parseLong( props.getProperty("application.bundle.id") );
	}

	public boolean isSingleton() {
		String singleton = props.getProperty( APPLICATION_SINGLETON );
		return singleton == null || singleton.equalsIgnoreCase("true");
	}

	public String getStartClass() {
		return startClass;
	}

	public String getApplicationPID() {
		return new String( pid );
	}

	protected BundleContext getBundleContext() {
		return bc;
	}

	public Map getProperties(String locale) {
		String defaultLanguage = (Locale.getDefault()).getLanguage();
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
			MegletContainer.log(bc, LogService.LOG_ERROR,
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
										ApplicationHandle.class, BundleContext.class } );
		setupMethod.setAccessible( true );
		setupMethod.invoke( meglet, new Object [] { handle, bc } );
	}

  public ServiceReference launchSpecific( Map args ) throws Exception {
		Meglet meglet = megletContainer.createMegletInstance( this );
		MegletHandle appHandle = new MegletHandle( megletContainer, meglet, this, bc);
		if (meglet == null)
			throw new Exception("Cannot create meglet instance!");

		initMeglet( meglet, appHandle );

		return appHandle.startHandle( args );
	}
}
