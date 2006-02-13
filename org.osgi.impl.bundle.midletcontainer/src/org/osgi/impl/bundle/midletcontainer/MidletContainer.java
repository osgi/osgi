package org.osgi.impl.bundle.midletcontainer;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.microedition.midlet.MIDlet;

import org.osgi.framework.*;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.log.LogService;



public class MidletContainer implements SynchronousBundleListener, ServiceListener {
	private BundleContext	        bc;
	private Vector			          installedMidletBundles;
	private Hashtable	           	bundleDescriptorHash;
	private OATContainerInterface oat;
	ServiceReference              oatRef = null;

	public MidletContainer(BundleContext bc) throws Exception {
		this.bc = bc;
		bundleDescriptorHash = new Hashtable();
		installedMidletBundles = new Vector();
		
		oatRef = bc .getServiceReference(OATContainerInterface.class.getName());
    if (oatRef == null)
	    throw new Exception("Cannot start the MidletContainer as OAT is not running!");
    oat = (OATContainerInterface) bc.getService(oatRef);
    if (oat == null)
	    throw new Exception("Cannot start the MidletContainer as OAT is not running!");
		
		bc.addBundleListener( this );
		
		if( !oatRef.getBundle().equals( bc.getBundle() ) ) /* OAT is provided by me? */
			bc.addServiceListener( this );
		
		Bundle bundles[] = bc.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			if (!installedMidletBundles.contains( bundles[ i ] ) && isMidletBundle( bundles[ i ] ) )
				installMidletBundle(bundles[i]);
		}
	}

	public void installMidletBundle(Bundle bundle) throws IOException, Exception {
		if (installedMidletBundles.contains( bundle ))
			return;
		

    ApplicationDescriptor appDescs[] = parseMidletHeaders(bundle);
		if (appDescs == null)
			throw new Exception("Not a valid MIDlet bundle!");

		String startClasses[] = new String [ appDescs.length ];
		for( int i=0; i != appDescs.length; i++ )
			startClasses[ i ] = ((MidletDescriptor)appDescs[ i ]).getStartClass();
		
		oat.registerOATBundle( bundle, startClasses );
		
		if( bundle.getState() == Bundle.ACTIVE )
			registerApplicationDescriptors( bundle );
		
		installedMidletBundles.add( bundle );
	}

	public void stop() throws Exception {
		
		for (int i = 0; i != installedMidletBundles.size(); i++)
			unregisterApplicationDescriptors((Bundle)installedMidletBundles.get(i));

		bc.ungetService( oatRef );
		bc.removeBundleListener( this );
	}

	private void terminateApplications( MidletDescriptor descs[] ) {
		try {
			for( int i=0; i != descs.length; i++ ) {
				ServiceReference refs[] = bc.getServiceReferences( ApplicationHandle.class.getName(), 
						 "(" + ApplicationHandle.APPLICATION_DESCRIPTOR + "=" + 
						 descs[ i ].getPID() + ")" );
			  if( refs != null && refs.length != 0 ) {
				  for( int j=0; j != refs.length; j++ ) {
				  	final ApplicationHandle appHandle = (ApplicationHandle)bc.getService( refs[ j ] );

				  	class DestroyerThread extends Thread {
							public void run() {
				        
								try {
									if( appHandle != null )
										appHandle.destroy();
								}catch( Exception e ) {
									Activator.log( LogService.LOG_ERROR ,"Cannot terminate the MIDlet!",e);
								}          
							};
						}
						
						DestroyerThread destroyerThread = new DestroyerThread();
						destroyerThread.start();

						try {
							destroyerThread.join( 5000 );
						}catch(InterruptedException e) {}
						
						if( destroyerThread.isAlive() )
							Activator.log( LogService.LOG_ERROR, "Stop method of the application didn't finish at 5s!", null );
						
				  	bc.ungetService( refs[ j ] );
				  }		  				  	
			  }
			}
		}catch( Exception e ) {
			Activator.log( LogService.LOG_ERROR, "Exception occurred at stopping the MIDlet instances!", e );			
		}
	}
	
	private void registerApplicationDescriptors(Bundle bundle) {
		MidletDescriptor descs[] = (MidletDescriptor []) bundleDescriptorHash.get( bundle );
		if (descs == null)
			return;
		for (int i = 0; i != descs.length; i++) {
			descs[ i ].register();
		}
	}

	private void unregisterApplicationDescriptors(Bundle bundle) {
		MidletDescriptor descs[] = (MidletDescriptor []) bundleDescriptorHash.get( bundle );
		if (descs == null) {
			Activator.log( LogService.LOG_ERROR ,"INTERNAL ERROR: no application descriptors found for a MIDlet!", null );
			return;
		}

		terminateApplications( descs );
		
		for (int i = 0; i != descs.length; i++) {
			descs[ i ].unregister();
		}
	}

	public void bundleChanged(BundleEvent event) {
		if (installedMidletBundles.contains(event.getBundle()))
			switch (event.getType()) {
				case BundleEvent.STARTED :
					registerApplicationDescriptors(event.getBundle());
					break;

				case BundleEvent.STOPPING:
				case BundleEvent.STOPPED:
					unregisterApplicationDescriptors(event.getBundle());
					break;

				case BundleEvent.UNINSTALLED :
					if( event.getBundle().getState() == Bundle.ACTIVE )
						unregisterApplicationDescriptors(event.getBundle());
					
  				installedMidletBundles.remove(event.getBundle());
					bundleDescriptorHash.remove(event.getBundle());
					break;

				case BundleEvent.UPDATED :
					installedMidletBundles.remove(event.getBundle());
  				unregisterApplicationDescriptors(event.getBundle());
					bundleDescriptorHash.remove(event.getBundle());
					try {
				    installMidletBundle( event.getBundle() );
					}catch( Exception e ) {
						Activator.log( LogService.LOG_ERROR ,"Exception occurred at installing a Midlet!",e);
					}
					break;					
			}
		else if( event.getType() == BundleEvent.INSTALLED ) {
	  	try {
			  if( isMidletBundle( event.getBundle() ) )
				  installMidletBundle( event.getBundle() );
	  	}catch( Exception e ) {
				Activator.log( LogService.LOG_ERROR ,"Exception occurred at installing a Midlet!",e);
	  	}			
		}
	}

	private boolean isMidletBundle( Bundle b ) {
		try {
			Dictionary dict = b.getHeaders();
			if (dict != null && dict.get("MIDlet-Name") != null
					&& dict.get("MIDlet-Version") != null
					&& dict.get("MIDlet-Vendor") != null
					&& dict.get("MIDlet-1") != null
					&& dict.get("MicroEdition-Profile") != null
					&& dict.get("MicroEdition-Configuration") != null) {
				
					String exports = (String)dict.get( Constants.EXPORT_PACKAGE );
					if( exports != null && exports.trim().length() != 0 ) {
						Activator.log( LogService.LOG_ERROR, "Package export not allowed for MIDlets!", null );
						return false;
					}
					
					String bundleActivator = (String)dict.get( Constants.BUNDLE_ACTIVATOR );
					if( bundleActivator != null && bundleActivator.trim().length() != 0 ) {
						Activator.log( LogService.LOG_ERROR, "MIDlets cannot have Bundle-Activator!", null );
						return false;
					}
					return true;
			}
		}
		catch (Exception e) 
		{
			Activator.log( LogService.LOG_ERROR ,"Unexpected exception at checking whether the bundle is MIDlet suite!",e);
		}
		return false;
	}
	
	private ApplicationDescriptor[] parseMidletHeaders(Bundle bundle) {
		try {
			Dictionary dict = bundle.getHeaders();
			String MIDletSuiteName = (String) dict.get("MIDlet-Name");
			String MIDletVersion = (String) dict.get("MIDlet-Version");
			String MIDletVendor = (String) dict.get("MIDlet-Vendor");
			String MIDletSuiteIcon = (String) dict.get("MIDlet-Icon");
			LinkedList appVector = new LinkedList();
			int midletCounter = 0;
			String midletProps;
			while ((midletProps = (String) dict.get("MIDlet-"
					+ ++midletCounter)) != null) {
				Properties props = new Properties();
				Hashtable names = new Hashtable();
				Hashtable icons = new Hashtable();
				StringTokenizer t = new StringTokenizer(midletProps, ",");
				if (t.hasMoreTokens()) {
					String MIDletName = t.nextToken().trim();
					if (t.hasMoreTokens()) {
						String MIDletIcon = t.nextToken().trim();
						if (t.hasMoreTokens()) {
							String MIDletStartClass = t.nextToken().trim();
							if (MIDletIcon.equals(""))
								MIDletIcon = MIDletSuiteIcon;
							
							if( MIDletIcon == null )
								MIDletIcon = "";
							
							String bundleName = bundle.getLocation();
							if( bundleName == null )
								bundleName = Long.toString( bundle.getBundleId() );
							props.setProperty(ApplicationDescriptor.APPLICATION_LOCATION, bundleName );
							
							props.setProperty("application.bundle.id", Long
									.toString(bundle.getBundleId()));
							props.setProperty(ApplicationDescriptor.APPLICATION_VENDOR,
									MIDletVendor);
							props.setProperty(ApplicationDescriptor.APPLICATION_VISIBLE, "true");
							props.setProperty(ApplicationDescriptor.APPLICATION_VERSION, MIDletVersion);
							String defaultLang = Locale.getDefault()
									.getLanguage();
							names.put(defaultLang, MIDletName);
							
							if( !MIDletIcon.equals("") ) {
								URL url = bundle.getResource( MIDletIcon );
								
								if( url != null )
									icons.put(defaultLang, url);
							}
							String uniqueID = "MIDlet: " + MIDletName + "-" + MIDletVersion + "-" + MIDletSuiteName;
								
							props.put(Constants.SERVICE_PID, uniqueID);

							MidletDescriptor midletDesc = new MidletDescriptor( bc, props, names, icons, defaultLang, MIDletStartClass,
									bundle, this);
							
							appVector.add( midletDesc );
						}
					}
				}
			}
			MidletDescriptor descs[] = new MidletDescriptor[appVector.size()];
			int applicationNum = appVector.size();
			for (int k = 0; k != applicationNum; k++)
				descs[k] = (MidletDescriptor) appVector.removeFirst();

			bundleDescriptorHash.put(bundle, descs);
			return descs;
		}
		catch (Throwable e) {
			Activator.log( LogService.LOG_ERROR, "Exception occurred at parsing a midlet bundle!", e);
		}
		return null;
	}

	OATContainerInterface getOATInterface() {
		return oat;
	}

	public void serviceChanged(ServiceEvent event) {
		if( event.getType() == ServiceEvent.UNREGISTERING ) {
			if( event.getServiceReference() == oatRef ) {
				try {
					Activator.log( LogService.LOG_ERROR ,"Stopping the MIDlet container as OAT has been stopped!", null );
				  bc.getBundle().stop();   /* if the OAT service stops, stop the container also */
				}catch(BundleException e) {
					Activator.log( LogService.LOG_ERROR ,"Failed to stop the MIDlet container!", e);
				}
			}
		}
	}
	
	public static MIDlet getSelfMIDlet() {
		class MySecurityManager extends SecurityManager {
			public Class [] getMyClassContext() {
				return getClassContext();
			}
		}
		
		MySecurityManager mySecurityManager = new MySecurityManager();
		Class []myClassContext = mySecurityManager.getMyClassContext();
		for( int i=0; i != myClassContext.length; i++ ) {
			if( myClassContext[ i ].getClassLoader() instanceof MIDletClassLoader ) {
				return ((MIDletClassLoader)myClassContext[ i ].getClassLoader()).getCorrespondingMIDlet();
			}
		}		
		return null; 
	}
	
	public static String getSelfID() {
		class MySecurityManager extends SecurityManager {
			public Class [] getMyClassContext() {
				return getClassContext();
			}
		}
		
		MySecurityManager mySecurityManager = new MySecurityManager();
		Class []myClassContext = mySecurityManager.getMyClassContext();
		for( int i=0; i != myClassContext.length; i++ ) {
			if( myClassContext[ i ].getClassLoader() instanceof MIDletClassLoader ) {
				return ((MIDletClassLoader)myClassContext[ i ].getClassLoader()).getCorrespondingMIDletID();
			}
		}		
		return null; 
	}
}