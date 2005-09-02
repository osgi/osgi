package org.osgi.impl.service.midletcontainer;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import javax.microedition.midlet.MIDlet;
import org.osgi.framework.*;
import org.osgi.service.application.*;

public final class MidletDescriptor extends ApplicationDescriptor {
	private Properties			props;
	private Hashtable			  names;
	private Hashtable			  icons;
	private BundleContext		bc;
	private String				  startClass;
	private String				  pid;
	private Bundle				  bundle;
	private String				  defaultLanguage;
	private boolean				  locked;
	private MidletContainer	midletContainer;
	private static int			instanceCounter;

	public MidletDescriptor(BundleContext bc, Properties props, Map names, Map icons,
			String defaultLang, String startClass, Bundle bundle,
			MidletContainer midletContainer) throws Exception {
		
		super( (String)props.get(Constants.SERVICE_PID) );

		this.bc = bc;
		this.midletContainer = midletContainer;
		this.props = new Properties();
		this.props.putAll(props);
		this.names = new Hashtable(names);
		this.icons = new Hashtable(icons);
		this.startClass = startClass;
		this.bundle = bundle;
		if (names.size() == 0 || icons.size() == 0
				|| !props.containsKey("application.bundle.id")
				|| !props.containsKey(Constants.SERVICE_PID)
				|| !props.containsKey(ApplicationDescriptor.APPLICATION_VERSION))
			throw new Exception("Invalid MEG container input!");
		if (!names.containsKey(defaultLang)) {
			throw new Exception("Invalid default language!");
		}
		else {
			defaultLanguage = defaultLang;
			pid = props.getProperty(Constants.SERVICE_PID);
			return;
		}
	}

	public long getBundleId() {
		return Long.parseLong(props.getProperty("application.bundle.id"));
	}

	Bundle getBundle() {
		return bundle;
	}

	public String getStartClass() {
		return startClass;
	}

	public String getPID() {
		return pid;
	}

	protected BundleContext getBundleContext() {
		return bc;
	}

	public Map getPropertiesSpecific(String locale) {
		Hashtable properties = new Hashtable();
		if( locale == null )
			locale = "";
		String localizedName = (String) names.get(locale);
		if (localizedName == null) {
			if ((localizedName = (String) names.get(defaultLanguage)) == null) {
				Enumeration enum = names.keys();
				String firstKey = (String) enum.nextElement();
				localizedName = (String) names.get(firstKey);
				locale = firstKey;
			}
			else {
				localizedName = (String) names.get(defaultLanguage);
			}
			locale = defaultLanguage;
		}
		properties.put(ApplicationDescriptor.APPLICATION_NAME, localizedName);
		properties.put(ApplicationDescriptor.APPLICATION_ICON, icons.get(locale));
		properties.put("application.bundle.id", props
				.getProperty("application.bundle.id"));
		properties.put(ApplicationDescriptor.APPLICATION_VERSION, props
				.getProperty(ApplicationDescriptor.APPLICATION_VERSION));
		properties.put(ApplicationDescriptor.APPLICATION_VENDOR, props
				.getProperty(ApplicationDescriptor.APPLICATION_VENDOR));
		String visible = props.getProperty(ApplicationDescriptor.APPLICATION_VISIBLE);
		if (visible != null && visible.equalsIgnoreCase("false"))
			properties.put(ApplicationDescriptor.APPLICATION_VISIBLE, "false");
		else
			properties.put(ApplicationDescriptor.APPLICATION_VISIBLE, "true");
		boolean launchable = false;
		try {
			launchable = isLaunchable();
		}
		catch (Exception e) {
			MidletContainer.log(bc,1,"Exception occurred at searching the Midlet container reference!",e);
		}
		properties.put(ApplicationDescriptor.APPLICATION_LOCKED, (new Boolean(locked)).toString());
		properties.put(ApplicationDescriptor.APPLICATION_LAUNCHABLE, (new Boolean(launchable))
				.toString());
		properties.put(ApplicationDescriptor.APPLICATION_CONTAINER, "MIDlet");
		properties.put(Constants.SERVICE_PID, new String(pid));
		return properties;
	}

	public ApplicationHandle launchSpecific(Map args) throws Exception {
		MIDlet midlet = createMidletInstance();
		if (midlet == null)
			throw new Exception("Cannot create meglet instance!");
		else {
			MidletHandle midHnd = new MidletHandle(bc, createNewInstanceID(pid), this, 
					                                   midletContainer, midlet, startClass );
      midHnd.startHandle(args);
			return midHnd;
		}
	}

	public void lockSpecific() {
		locked = true;
	}

	public void unlockSpecific() {
		locked = false;
	}

	static synchronized String createNewInstanceID(String pid) {
		return new String(pid + ":" + instanceCounter++);
	}
	
	public boolean isLaunchable() {
		try {
			if ( locked )
				return false;
			if( !midletContainer.getOATInterface().isLaunchable( getBundle(), startClass ) )
				return false;
			return true;
		}
		catch (Exception e) {
			MidletContainer.log(bc, 1, "Exception occurred at checking if the midlet is launchable!",	e);
		}
		return false;
	}	

	public MIDlet createMidletInstance() throws Exception {
		return (MIDlet)AccessController.doPrivileged(new PrivilegedExceptionAction() {
			public java.lang.Object run() throws Exception {
				Bundle appBundle = bc.getBundle(getBundleId());
				Class mainClass = appBundle.loadClass(getStartClass());
				String mainClassFileName = getStartClass().replace( '.', '/' ) + ".class";
				
				URL url = appBundle.getResource( mainClassFileName );
				if( url == null )
					throw new Exception( "Internal error!" );
				String urlName = url.toString();
				if( !urlName.endsWith( mainClassFileName ) )
					throw new Exception( "Internal error!" );
				String location = urlName.substring( 0, urlName.length() - mainClassFileName.length() );
				
				ClassLoader loader = new MIDletClassLoader(mainClass.getClassLoader(),
						appBundle, mainClass.getProtectionDomain(), location );
				Class midletClass = loader.loadClass(getStartClass());
				Constructor constructor = midletClass
						.getDeclaredConstructor(new Class[0]);
				constructor.setAccessible(true);
				MIDlet app = (MIDlet) constructor.newInstance(new Object[0]);
				return app;
		  }});
	}
}
