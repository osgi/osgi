package org.osgi.impl.service.midletcontainer;

import java.io.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.impl.service.application.OATContainerInterface;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.log.LogService;

public class MidletContainer implements BundleListener, ServiceListener {
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
		
    oat.registerOATBundle( bundle );

    ApplicationDescriptor appDescs[] = parseMidletHeaders(bundle);
		if (appDescs == null)
			throw new Exception("Not a valid MIDlet bundle!");

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
						 descs[ i ].getProperties( null ).get( ApplicationDescriptor.APPLICATION_PID ) + ")" );
			  if( refs != null && refs.length != 0 ) {
				  for( int j=0; j != refs.length; j++ ) {
				  	final ApplicationHandle appHandle = (ApplicationHandle)bc.getService( refs[ j ] );

				  	class DestroyerThread extends Thread {
							public void run() {
				        
								try {
									if( appHandle != null )
										appHandle.destroy();
								}catch( Exception e ) {
								}          
							};
						}
						
						DestroyerThread destroyerThread = new DestroyerThread();
						destroyerThread.start();

						try {
							destroyerThread.join( 5000 );
						}catch(InterruptedException e) {}
						
						if( destroyerThread.isAlive() )
							log( bc, LogService.LOG_ERROR, "Stop method of the application didn't finish at 5s!", null );
						
				  	bc.ungetService( refs[ j ] );
				  }		  				  	
			  }
			}
		}catch( InvalidSyntaxException e ) {}
	}
	
	private void registerApplicationDescriptors(Bundle bundle) {
		MidletDescriptor descs[] = (MidletDescriptor []) bundleDescriptorHash.get( bundle );
		if (descs == null)
			return;
		for (int i = 0; i != descs.length; i++)
			descs[ i ].register();
	}

	private void unregisterApplicationDescriptors(Bundle bundle) {
		MidletDescriptor descs[] = (MidletDescriptor []) bundleDescriptorHash.get( bundle );
		if (descs == null)
			return;

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

				case BundleEvent.STOPPED :
					unregisterApplicationDescriptors(event.getBundle());
					break;

				case BundleEvent.UNINSTALLED :
					installedMidletBundles.remove(event.getBundle());
					unregisterApplicationDescriptors(event.getBundle());
					bundleDescriptorHash.remove(event.getBundle());
					break;

				case BundleEvent.UPDATED :
					installedMidletBundles.remove(event.getBundle());
  				unregisterApplicationDescriptors(event.getBundle());
					bundleDescriptorHash.remove(event.getBundle());
					try {
				    installMidletBundle( event.getBundle() );
					}catch( Exception e ) {}
					break;					
			}
		else if( event.getType() == BundleEvent.INSTALLED ) {
	  	try {
			  if( isMidletBundle( event.getBundle() ) )
				  installMidletBundle( event.getBundle() );
	  	}catch( Exception e ) {}			
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
					&& dict.get("MicroEdition-Configuration") != null)
				return true;
		}
		catch (Exception e) {}
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
							
							String bundleName = bundle.getSymbolicName();
							if( bundleName == null )
								bundleName = Long.toString( bundle.getBundleId() );
							props.setProperty(ApplicationDescriptor.APPLICATION_PACKAGE, bundleName );
							
							props.setProperty("application.bundle.id", Long
									.toString(bundle.getBundleId()));
							props.setProperty(ApplicationDescriptor.APPLICATION_VENDOR,
									MIDletVendor);
							props.setProperty(ApplicationDescriptor.APPLICATION_VISIBLE, "true");
							props.setProperty(ApplicationDescriptor.APPLICATION_VERSION, MIDletVersion);
							String defaultLang = Locale.getDefault()
									.getLanguage();
							names.put(defaultLang, MIDletName);
							icons.put(defaultLang, MIDletIcon);
							String uniqueID = "MIDlet: " + MIDletName + "-" + MIDletVersion + "-" + MIDletSuiteName;
							uniqueID = mangle( bc, uniqueID );
								
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
			log(bc, 1, "Exception occurred at parsing a midlet bundle!", e);
		}
		return null;
	}

	static boolean log(BundleContext bc, int severity, String message,
			Throwable throwable) {
		System.out.println("Serverity:" + severity + " Message:" + message
				+ " Throwable:" + throwable);
		ServiceReference serviceRef = bc
				.getServiceReference("org.osgi.service.log.LogService");
		if (serviceRef != null) {
			LogService logService = (LogService) bc.getService(serviceRef);
			if (logService != null) {
				try {
					logService.log(severity, message, throwable);
				}
				finally {
					bc.ungetService(serviceRef);
				}
				return true;
			}
		}
		return false;
	}

	OATContainerInterface getOATInterface() {
		return oat;
	}

	public void serviceChanged(ServiceEvent event) {
		if( event.getType() == ServiceEvent.UNREGISTERING ) {
			if( event.getServiceReference() == oatRef ) {
				try {
				  bc.getBundle().stop();   /* if the OAT service stops, stop the container also */
				}catch(BundleException e) {}
			}
		}
	}
	
	static String mangle( BundleContext bc, String in ) {
		ServiceReference dmtAdminRef = bc.getServiceReference( DmtAdmin.class.getName() );
    if(dmtAdminRef == null)      
    	return in;
    
    DmtAdmin dmtAdmin = (DmtAdmin) bc.getService( dmtAdminRef );
    if( dmtAdmin == null )
    	return in;
		
    String result = dmtAdmin.mangle( in );
    
    bc.getService( dmtAdminRef );
    
    return result;
	}
}