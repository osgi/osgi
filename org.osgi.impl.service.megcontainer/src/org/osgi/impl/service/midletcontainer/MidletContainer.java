package org.osgi.impl.service.midletcontainer;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import javax.microedition.midlet.MIDlet;
import org.osgi.framework.*;
import org.osgi.impl.service.application.OATContainerInterface;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.application.midlet.MidletDescriptor;
import org.osgi.service.application.midlet.MidletHandle;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;
import org.w3c.dom.*;

class MEGBundleDescriptor {
	public ApplicationDescriptor	applications[];
	public ServiceRegistration		serviceRegistrations[];
	public long						bundleID;
}

public class MidletContainer implements BundleListener, EventHandler {
	private BundleContext	bc;
	private Vector			bundleIDs;
	private Hashtable		bundleHash;
	private int				height;
	private int				width;
	private OATContainerInterface oat;
	ServiceReference oatRef = null;

	public MidletContainer(BundleContext bc) throws Exception {
		this.bc = bc;
		bundleHash = new Hashtable();
		bundleIDs = new Vector();
		
		oatRef = bc .getServiceReference(OATContainerInterface.class.getName());
    if (oatRef == null)
	    throw new Exception("Cannot start the MidletContainer as OAT is not running!");
    oat = (OATContainerInterface) bc.getService(oatRef);
    if (oat == null)
	    throw new Exception("Cannot start the MidletContainer as OAT is not running!");
		
		bc.addBundleListener(this);
		Bundle bundles[] = bc.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			String id = (new StringBuffer(String.valueOf(bundles[i].getBundleId()))).toString();
			if (!bundleIDs.contains(id) && bundles[i].getBundleId() != 0L)
				checkAndRegister(bundles[i]);
		}

	}

	public void installApplication(long bundleID) throws IOException, Exception {
		if (bundleIDs.contains(Long.toString(bundleID)))
			return;
		
    oat.registerOATBundle( bc.getBundle( bundleID ) );
		
		ApplicationDescriptor appDescs[] = registerBundle(bundleID);
		if (appDescs == null) {
			throw new Exception("Not a valid MIDlet bundle!");
		}
		else {
			bundleIDs.add(Long.toString(bundleID));
			return;
		}
	}

	public MIDlet createMidletInstance(MidletDescriptorImpl appDesc) throws Exception {
		MEGBundleDescriptor desc = getBundleDescriptor(appDesc.getBundleId());
		Bundle appBundle = bc.getBundle(appDesc.getBundleId());
		Class mainClass = appBundle.loadClass(appDesc.getStartClass());
		ClassLoader loader = new MIDletClassLoader(getClass().getClassLoader(),
				appBundle, mainClass.getProtectionDomain());
		Class midletClass = loader.loadClass(appDesc.getStartClass());
		Constructor constructor = midletClass
				.getDeclaredConstructor(new Class[0]);
		constructor.setAccessible(true);
		MIDlet app = (MIDlet) constructor.newInstance(new Object[0]);
		return app;
	}

	private MEGBundleDescriptor getBundleDescriptor(long bundleID)
			throws Exception {
		MEGBundleDescriptor desc = (MEGBundleDescriptor) bundleHash
				.get(new Long(bundleID));
		if (desc == null)
			throw new Exception(
					"Application wasn't installed onto the midlet container!");
		else
			return desc;
	}

	private int getApplicationIndex(MEGBundleDescriptor desc,
			ApplicationDescriptor appDesc) throws Exception {
		for (int i = 0; i != desc.applications.length; i++)
			if (desc.applications[i] == appDesc)
				return i;

		throw new Exception(
				"Application wasn't installed onto the midlet container!");
	}

	public boolean isLaunchable(MidletDescriptorImpl appDesc) {
		try {
			if (appDesc.isLocked())
				return false;
			if( !oat.isLaunchable( appDesc.getBundle(), appDesc.getStartClass() ) )
				return false;
			MEGBundleDescriptor desc = getBundleDescriptor(appDesc
					.getBundleId());
			if (desc == null)
				return false;
			return true;
		}
		catch (Exception e) {
			log(bc, 1, "Exception occurred at checking if the midlet is launchable!",	e);
		}
		return false;
	}

	public void stop() throws Exception {
		
		terminateContainerApplications();
		
		for (int i = 0; i != bundleIDs.size(); i++)
			unregisterApplicationDescriptors(Long.parseLong((String) bundleIDs
					.get(i)));

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
	
	private ApplicationDescriptor[] registerBundle(long bundleID) {
		ApplicationDescriptor appDescs[] = parseManifestHeaders(bundleID);
		if (appDescs == null) {
			return null;
		}
		else {
			registerApplicationDescriptors(bundleID);
			return appDescs;
		}
	}

	private void registerApplicationDescriptors(long bundleID) {
		MEGBundleDescriptor desc = (MEGBundleDescriptor) bundleHash
				.get(new Long(bundleID));
		if (desc == null)
			return;
		for (int i = 0; i != desc.applications.length; i++) {
			if (desc.serviceRegistrations[i] != null) {
				desc.serviceRegistrations[i].unregister();
				desc.serviceRegistrations[i] = null;
			}
			Dictionary properties = new Hashtable(desc.applications[i]
					.getProperties(Locale.getDefault().getLanguage()));
			String pid = (String) properties.get("service.pid");
			properties.put("service.pid", pid);
			desc.serviceRegistrations[i] = bc.registerService(
					"org.osgi.service.application.ApplicationDescriptor",
					desc.applications[i], properties);
		}

	}

	private void unregisterApplicationDescriptors(long bundleID) {
		MEGBundleDescriptor desc = (MEGBundleDescriptor) bundleHash
				.get(new Long(bundleID));
		if (desc == null)
			return;
		for (int i = 0; i != desc.serviceRegistrations.length; i++)
			if (desc.serviceRegistrations[i] != null) {
				desc.serviceRegistrations[i].unregister();
				desc.serviceRegistrations[i] = null;
			}

	}

	public void bundleChanged(BundleEvent event) {
		long bundleID = event.getBundle().getBundleId();
		String bundleStr = Long.toString(bundleID);
		if (bundleIDs.contains(bundleStr))
			switch (event.getType()) {
				case 2 : // '\002'
					registerApplicationDescriptors(bundleID);
					break;

				case 4 : // '\004'
					unregisterApplicationDescriptors(bundleID);
					break;

				case 16 : // '\020'
					bundleIDs.remove(bundleStr);
					unregisterApplicationDescriptors(bundleID);
					bundleHash.remove(new Long(bundleID));
					break;

				case 8 : // '\b'
					unregisterApplicationDescriptors(bundleID);
					registerApplicationDescriptors(bundleID);
					break;
			}
		else if (event.getType() == 1)
			checkAndRegister(event.getBundle());
	}

	void checkAndRegister(Bundle b) {
		try {
			Map map = convertObsoleteDictionaryToMap(b.getHeaders());
			if (map != null && map.containsKey("MIDlet-Name")
					&& map.containsKey("MIDlet-Version")
					&& map.containsKey("MIDlet-Vendor")
					&& map.containsKey("MIDlet-1")
					&& map.containsKey("MicroEdition-Profile")
					&& map.containsKey("MicroEdition-Configuration"))
				installApplication(b.getBundleId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log(bc, 1, "Exception occurred at installing the midlet!", e);
		}
	}

	private String getAttributeValue(Node node, String attribName) {
		NamedNodeMap nnm = node.getAttributes();
		if (nnm != null) {
			int len = nnm.getLength();
			for (int i = 0; i < len; i++) {
				Attr attr = (Attr) nnm.item(i);
				if (attr.getNodeName().equals(attribName))
					return attr.getNodeValue();
			}

		}
		return null;
	}

	private ApplicationDescriptor[] parseManifestHeaders(long bundleID) {
		try {
			Map manifest = convertObsoleteDictionaryToMap(bc
					.getBundle(bundleID).getHeaders());
			String MIDletSuiteName = (String) manifest.get("MIDlet-Name");
			String MIDletVersion = (String) manifest.get("MIDlet-Version");
			String MIDletVendor = (String) manifest.get("MIDlet-Vendor");
			String MIDletSuiteIcon = (String) manifest.get("MIDlet-Icon");
			LinkedList appVector = new LinkedList();
			int midletCounter = 0;
			String midletProps;
			while ((midletProps = (String) manifest.get("MIDlet-"
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
									.toString(bundleID));
							props.setProperty("application.vendor",
									MIDletVendor);
							props.setProperty("application.visible", "true");
							props.setProperty("application.version",
									MIDletVersion);
							String defaultLang = Locale.getDefault()
									.getLanguage();
							names.put(defaultLang, MIDletName);
							icons.put(defaultLang, MIDletIcon);
							String uniqueID = MIDletName + "-"
									+ MIDletSuiteName + "-" + MIDletVersion;
							props.put("service.pid", uniqueID);
							appVector.add(createMidletDescriptorByReflection(
									props, names, icons, defaultLang,
									MIDletStartClass, bc.getBundle(bundleID)));
						}
					}
				}
			}
			ApplicationDescriptor descs[] = new ApplicationDescriptor[appVector
					.size()];
			int applicationNum = appVector.size();
			for (int k = 0; k != applicationNum; k++)
				descs[k] = (ApplicationDescriptor) appVector.removeFirst();

			MEGBundleDescriptor descriptor = new MEGBundleDescriptor();
			descriptor.applications = new ApplicationDescriptor[applicationNum];
			for (int l = 0; l != applicationNum; l++) {
				descriptor.applications[l] = descs[l];
			}

			descriptor.serviceRegistrations = new ServiceRegistration[applicationNum];
			descriptor.bundleID = bundleID;
			bundleHash.put(new Long(bundleID), descriptor);
			return descs;
		}
		catch (Throwable e) {
			log(bc, 1, "Exception occurred at parsing a midlet bundle!", e);
		}
		return null;
	}

	public void handleEvent(Event event) {
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

	public ServiceReference getReference(MidletDescriptorImpl megDesc) {
		MEGBundleDescriptor desc = (MEGBundleDescriptor) bundleHash
				.get(new Long(megDesc.getBundleId()));
		if (desc == null)
			return null;
		for (int i = 0; i != desc.applications.length; i++)
			if (megDesc.getMidletDescriptor() == desc.applications[i])
				return desc.serviceRegistrations[i].getReference();

		return null;
	}

	public MidletDescriptor createMidletDescriptorByReflection(
			Properties props, Hashtable names, Hashtable icons,
			String defaultLang, String startClass, Bundle bundle) {
		String pid = (String) props.get("service.pid");
		try {
			Class midletDescriptorClass = org.osgi.service.application.midlet.MidletDescriptor.class;
			Constructor constructor = midletDescriptorClass
					.getDeclaredConstructor(new Class[] {java.lang.String.class});
			constructor.setAccessible(true);
			MidletDescriptor midletDescriptor = (MidletDescriptor) constructor
					.newInstance(new Object[] {pid});
			Field delegate = midletDescriptorClass.getDeclaredField("delegate");
			delegate.setAccessible(true);
			MidletDescriptorImpl megDesc = (MidletDescriptorImpl) delegate
					.get(midletDescriptor);
			megDesc.init(bc, props, names, icons, defaultLang, startClass,
					bundle, this);
			return midletDescriptor;
		}
		catch (Exception e) {
			log(bc, 1, "Exception occurred at creating midlet descriptor!", e);
		}
		return null;
	}

	private Map convertObsoleteDictionaryToMap(Dictionary dict) {
		Hashtable convertedTable = new Hashtable();
		if (dict == null)
			return null;
		String key;
		for (Enumeration enum = dict.keys(); enum.hasMoreElements(); convertedTable
				.put(key, dict.get(key)))
			key = (String) enum.nextElement();

		return convertedTable;
	}
	
	OATContainerInterface getOATInterface() {
		return oat;
	}
}