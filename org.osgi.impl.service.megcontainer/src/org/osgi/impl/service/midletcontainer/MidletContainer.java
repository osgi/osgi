package org.osgi.impl.service.midletcontainer;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.impl.service.application.OATContainerInterface;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.log.LogService;

class MidletBundleDescriptor {
	public ApplicationDescriptor	applications[];
	public ServiceRegistration		serviceRegistrations[];
}

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
		
		terminateContainerApplications();
		
		for (int i = 0; i != installedMidletBundles.size(); i++)
			unregisterApplicationDescriptors((Bundle)installedMidletBundles.get(i));

		bc.ungetService( oatRef );
		bc.removeBundleListener( this );
	}

	private void terminateContainerApplications() {
		try {
		  ServiceReference refs[] = bc.getServiceReferences( ApplicationHandle.class.getName(), null );
		  if( refs == null || refs.length == 0 )
		  	return;
		  
		  for( int i=0; i != refs.length; i++ ) {
		  	ApplicationHandle appHandle = (ApplicationHandle)bc.getService( refs[ i ] );
		  	if( appHandle instanceof MidletHandle ) {
		  		try {
		  		  appHandle.destroy(); /* TODO, what to do if the app doesn't stop? */
		  		}catch( Exception e ) {}
		  		bc.ungetService( refs[ i ] );
		  	}		  	
		  }
		}catch( InvalidSyntaxException e ) {}
	}
	
	private void registerApplicationDescriptors(Bundle bundle) {
		MidletBundleDescriptor desc = (MidletBundleDescriptor) bundleDescriptorHash.get( bundle );
		if (desc == null)
			return;
		for (int i = 0; i != desc.applications.length; i++) {
			if (desc.serviceRegistrations[i] != null) {
				desc.serviceRegistrations[i].unregister();
				desc.serviceRegistrations[i] = null;
			}
			Dictionary properties = new Hashtable(desc.applications[i]
					.getProperties(Locale.getDefault().getLanguage()));
			String pid = (String) properties.get(Constants.SERVICE_PID);
			properties.put(Constants.SERVICE_PID, pid);
			desc.serviceRegistrations[i] = bc.registerService(
					ApplicationDescriptor.class.getName(),
					desc.applications[i], properties);
		}

	}

	private void unregisterApplicationDescriptors(Bundle bundle) {
		MidletBundleDescriptor desc = (MidletBundleDescriptor) bundleDescriptorHash.get( bundle );
		if (desc == null)
			return;
		for (int i = 0; i != desc.serviceRegistrations.length; i++)
			if (desc.serviceRegistrations[i] != null) {
				desc.serviceRegistrations[i].unregister();
				desc.serviceRegistrations[i] = null;
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
							if( uniqueID.length() > 32 ) {
								uniqueID = getHash( uniqueID );
							}
							props.put(Constants.SERVICE_PID, uniqueID);

							MidletDescriptor midletDesc = new MidletDescriptor( bc, props, names, icons, defaultLang, MIDletStartClass,
									bundle, this);
							
							appVector.add( midletDesc );
						}
					}
				}
			}
			ApplicationDescriptor descs[] = new ApplicationDescriptor[appVector
					.size()];
			int applicationNum = appVector.size();
			for (int k = 0; k != applicationNum; k++)
				descs[k] = (ApplicationDescriptor) appVector.removeFirst();

			MidletBundleDescriptor descriptor = new MidletBundleDescriptor();
			descriptor.applications = new ApplicationDescriptor[applicationNum];
			for (int l = 0; l != applicationNum; l++) {
				descriptor.applications[l] = descs[l];
			}

			descriptor.serviceRegistrations = new ServiceRegistration[applicationNum];
			bundleDescriptorHash.put(bundle, descriptor);
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

  private static final char base64table[] = {
      'A','B','C','D','E','F','G','H',
      'I','J','K','L','M','N','O','P',
      'Q','R','S','T','U','V','W','X',
      'Y','Z','a','b','c','d','e','f',
      'g','h','i','j','k','l','m','n',
      'o','p','q','r','s','t','u','v',
      'w','x','y','z','0','1','2','3',
      '4','5','6','7','8','9','+','_', // !!! this differs from base64
  };
	
	private String getHash(String from) throws Exception {
    MessageDigest md = MessageDigest.getInstance("SHA");
		  
	  byte[] byteStream;
  	try {
	  	byteStream = from.getBytes("UTF-8");
	  }
		catch (UnsupportedEncodingException e) {
		 // There's no way UTF-8 encoding is not implemented...
		 throw new IllegalStateException("there's no UTF-8 encoder here!");
		}
		byte[] digest = md.digest(byteStream);
		
		//  very dumb base64 encoder code. There is no need for multiple lines
		// or trailing '='-s....
		// also, we hardcoded the fact that sha-1 digests are 20 bytes long
		StringBuffer sb = new StringBuffer(digest.length*2);
		for(int i=0;i<6;i++) {
			int d0 = digest[i*3]&0xff;
			int d1 = digest[i*3+1]&0xff;
			int d2 = digest[i*3+2]&0xff;
			sb.append(base64table[d0>>2]);
			sb.append(base64table[(d0<<4|d1>>4)&63]);
			sb.append(base64table[(d1<<2|d2>>6)&63]);
			sb.append(base64table[d2&63]);
		}
		int d0 = digest[18]&0xff;
		int d1 = digest[19]&0xff;
		sb.append(base64table[d0>>2]);
		sb.append(base64table[(d0<<4|d1>>4)&63]);
		sb.append(base64table[(d1<<2)&63]);
		
		return sb.toString();
	}
}