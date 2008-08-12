package org.osgi.impl.bundle.midletcontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import org.osgi.service.log.LogService;

import javax.microedition.midlet.MIDlet;
import org.osgi.framework.*;
import org.osgi.service.application.*;

public final class MidletDescriptor extends ApplicationDescriptor implements ServiceListener {
	private Properties					props;
	private Hashtable			  		names;
	private Hashtable			  		icons;
	private BundleContext				bc;
	private String				  		startClass;
	private String				  		pid;
	private Bundle				  		bundle;
	private String				  		defaultLanguage;
	private boolean				  		locked;
	private boolean             registeredLaunchable;
	private MidletContainer			midletContainer;
	private static int			    instanceCounter;
	private ServiceRegistration serviceReg;
	private Hashtable           serviceProps;
	private boolean             initlock;

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
		this.serviceReg = null;
		if (names.size() == 0 || icons.size() == 0
				|| !props.containsKey("application.bundle.id")
				|| !props.containsKey(Constants.SERVICE_PID)
				|| !props.containsKey(ApplicationDescriptor.APPLICATION_VERSION)
				|| !props.containsKey(ApplicationDescriptor.APPLICATION_LOCATION ))
			throw new Exception("Invalid MEG container input!");
		if (!names.containsKey(defaultLang)) {
			throw new Exception("Invalid default language!");
		}
		else {
			defaultLanguage = defaultLang;
			pid = props.getProperty(Constants.SERVICE_PID);
		}
		
		initlock = true;
		getProperties( null );
	}

	Bundle getBundle() {
		return bundle;
	}

	public String getPID() {
		return pid;
	}

	public Map getPropertiesSpecific(String locale) {
		if( initlock ) {
			initlock = false;
			Hashtable properties = new Hashtable();
			properties.put(ApplicationDescriptor.APPLICATION_LOCKED, new Boolean(locked));
			return properties;
		}
		checkBundle();
		Hashtable properties = new Hashtable();
		if( locale == null )
			locale = "";
		String localizedName = (String) names.get(locale);
		if (localizedName == null) {
			if ((localizedName = (String) names.get(defaultLanguage)) == null) {
				Enumeration enumeration = names.keys();
				String firstKey = (String) enumeration.nextElement();
				localizedName = (String) names.get(firstKey);
				locale = firstKey;
			}
			else {
				localizedName = (String) names.get(defaultLanguage);
			}
			locale = defaultLanguage;
		}
		properties.put(ApplicationDescriptor.APPLICATION_NAME, localizedName);
		if( icons.get( locale ) != null )
			properties.put(ApplicationDescriptor.APPLICATION_ICON, icons.get(locale));
		else
			properties.put(ApplicationDescriptor.APPLICATION_ICON, bc.getBundle().getResource( "TestIcon.gif" ) );
		properties.put("application.bundle.id", props
				.getProperty("application.bundle.id"));
		properties.put(ApplicationDescriptor.APPLICATION_VERSION, props
				.getProperty(ApplicationDescriptor.APPLICATION_VERSION));
		properties.put(ApplicationDescriptor.APPLICATION_LOCATION, props
				.getProperty(ApplicationDescriptor.APPLICATION_LOCATION));
		properties.put(ApplicationDescriptor.APPLICATION_VENDOR, props
				.getProperty(ApplicationDescriptor.APPLICATION_VENDOR));
		String visible = props.getProperty(ApplicationDescriptor.APPLICATION_VISIBLE);
		if (visible != null && visible.equalsIgnoreCase("false"))
			properties.put(ApplicationDescriptor.APPLICATION_VISIBLE, new Boolean( false ) );
		else
			properties.put(ApplicationDescriptor.APPLICATION_VISIBLE, new Boolean( true ) );
		boolean launchable = false;
		try {
			launchable = isLaunchable();
		}
		catch (Exception e) {
			Activator.log( LogService.LOG_ERROR ,"Exception occurred at searching the Midlet container reference!",e);
		}
		properties.put(ApplicationDescriptor.APPLICATION_LOCKED, new Boolean(locked));
		properties.put(ApplicationDescriptor.APPLICATION_LAUNCHABLE, new Boolean(launchable));
		properties.put(ApplicationDescriptor.APPLICATION_CONTAINER, "MIDlet");
		properties.put(Constants.SERVICE_PID, new String(pid));
		
		if( serviceReg != null ) {
			ServiceReference ref = serviceReg.getReference();
			String keys[] = ref.getPropertyKeys();
			for( int q=0; q != keys.length; q++ )
				properties.put( keys[ q ], ref.getProperty( keys[ q ] ) );
		}
		
		return properties;
	}

	public ApplicationHandle launchSpecific(Map args) throws Exception {
		checkBundle();
		String instID = createNewInstanceID(bc, pid);
		MIDlet midlet = createMidletInstance( instID );
		if (midlet == null)
			throw new Exception("Cannot create meglet instance!");
		else {
			MidletHandle midHnd = new MidletHandle(bc, instID, this, 
					                                   midletContainer, midlet );
			midHnd.startHandle(args);
			return midHnd;
		}
	}

	public void lockSpecific() {
		checkBundle();
		locked = true;
		if( serviceReg != null ) {
			serviceProps.put( ApplicationDescriptor.APPLICATION_LOCKED, new Boolean( true ));
			serviceProps.put( ApplicationDescriptor.APPLICATION_LAUNCHABLE, new Boolean( false ));
			registeredLaunchable = false;
			serviceReg.setProperties( serviceProps ); // if lock changes, change the service registration properties also
		}
	}

	public void unlockSpecific() {
		checkBundle();
		locked = false;
		if( serviceReg != null ) {
			serviceProps.put( ApplicationDescriptor.APPLICATION_LOCKED, new Boolean( false ));
			registeredLaunchable = isLaunchable();
			serviceProps.put( ApplicationDescriptor.APPLICATION_LAUNCHABLE, new Boolean( registeredLaunchable ));
			serviceReg.setProperties( serviceProps ); // if lock changes, change the service registration properties also
		}
	}

	static synchronized String createNewInstanceID(BundleContext bc, String pid) {
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
			Activator.log( LogService.LOG_ERROR, "Exception occurred at checking if the midlet is launchable!",	e);
		}
		return false;
	}	

	public MIDlet createMidletInstance( final String instID ) throws Exception {
		return (MIDlet)AccessController.doPrivileged(new PrivilegedExceptionAction() {
			public java.lang.Object run() throws Exception {
				Class mainClass = bundle.loadClass(startClass);
				String mainClassFileName = startClass.replace( '.', '/' ) + ".class";
				
				URL url = bundle.getResource( mainClassFileName );
				if( url == null )
					throw new Exception( "Internal error!" );
				String urlName = url.toString();
				if( !urlName.endsWith( mainClassFileName ) )
					throw new Exception( "Internal error!" );
				String location = urlName.substring( 0, urlName.length() - mainClassFileName.length() );
				
				MIDletClassLoader loader = new MIDletClassLoader(mainClass.getClassLoader(),
						bundle, mainClass.getProtectionDomain(), location );
				Class midletClass = loader.loadClass(startClass);
				Constructor constructor = midletClass
						.getDeclaredConstructor(new Class[0]);
				constructor.setAccessible(true);
				MIDlet app = (MIDlet) constructor.newInstance(new Object[0]);
				loader.setCorrespondingMIDlet( app, instID );
				return app;
		  }});
	}
	
	void register() {
		serviceProps = new Hashtable( getProperties(Locale.getDefault().getLanguage()));
		registeredLaunchable = ((Boolean)serviceProps.get( ApplicationDescriptor.APPLICATION_LAUNCHABLE )).booleanValue();
		if( serviceReg == null ) {
			serviceReg = bc.registerService(ApplicationDescriptor.class.getName(),this, serviceProps);		
			bc.addServiceListener( this );
		}else
			serviceReg.setProperties( serviceProps );
	}
	
	void unregister() {
		if (serviceReg != null) {
			bc.removeServiceListener( this );
			serviceReg.unregister();
			serviceReg = null;
		}
	}

	public void serviceChanged(ServiceEvent event) {
		boolean launchable = isLaunchable();
		if( serviceReg != null && launchable != registeredLaunchable ) {
			serviceProps.put( ApplicationDescriptor.APPLICATION_LAUNCHABLE, new Boolean( launchable ) );
			registeredLaunchable = launchable;
			serviceReg.setProperties( serviceProps );
		}
	}

	String getStartClass() {
		return startClass;
	}
	
	private void checkBundle() {
		if( bundle.getState() != Bundle.ACTIVE && bundle.getState() != Bundle.STARTING &&
				bundle.getState() != Bundle.STOPPING )
			throw new IllegalStateException();
	}
	
	public boolean matchDNChain(final String pattern) {
		if( pattern == null )
			throw new NullPointerException( "Pattern cannot be null!" );
			
		checkBundle(); // TODO throw IllegalStateException if the AppDesc is invalid
		
		final Bundle bundle = this.bundle;
		try {
		  return ((Boolean)AccessController.doPrivileged(new PrivilegedExceptionAction() {
			  public Object run() throws Exception {			
					Method getBundleData = null;
					
					Class bundleClass = bundle.getClass();					
					do {
						try {
							getBundleData = bundleClass.getDeclaredMethod( "getBundleData", new Class[0] );
							break;
						} catch( NoSuchMethodException e ) {
							bundleClass = bundleClass.getSuperclass();
							if( bundleClass == null )
								throw e;
						}
					}while( true );
					
					getBundleData.setAccessible( true );
					
					Object data = getBundleData.invoke( bundle, new Class [0] );
					if( data == null )
						return new Boolean( false );
					
					Method matchDNChain = null;
					
					Class  matchDNClass = data.getClass();					
					do {
						try {
							matchDNChain = matchDNClass.getDeclaredMethod( "matchDNChain", new Class[] { String.class } );
							break;
						} catch( NoSuchMethodException e ) {
							matchDNClass = matchDNClass.getSuperclass();
							if( matchDNClass == null )
								throw e;
						}
					}while( true );
					matchDNChain.setAccessible( true );
					
					return matchDNChain.invoke( data, new Object [] { pattern } );
			  }
		  })).booleanValue();
		}catch(PrivilegedActionException e ) {
			Activator.log( LogService.LOG_ERROR, "Exception occurred at matching the DN chain!",	e);
			return false;
		}
	}

	protected boolean isLaunchableSpecific() {
		checkBundle();
		return isLaunchable();
	}
}
