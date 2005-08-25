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

class EventSubscribe {
	public static final int	START	= 1;
	public static final int	STOP	= 2;
	public static final int	SUSPEND	= 3;
	public static final int	RESUME	= 4;
	public static final int	HANDLE	= 5;
	public String			eventTopic[];
	public int				eventAction[];
}

class Dependencies {
	public String	dependentFields[];
	public String	requiredService[];
}

class MEGBundleDescriptor {
	public ApplicationDescriptor	applications[];
	public ServiceRegistration		serviceRegistrations[];
	public EventSubscribe			eventSubscribes[];
	public Dependencies				dependencies[];
	public long						bundleID;
	public boolean					autoStartRequired;
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
		
		oatRef = bc .getServiceReference(OATContainerInterface.class.getName());
    if (oatRef == null)
	    throw new Exception("Cannot start the MidletContainer as OAT is not running!");
    oat = (OATContainerInterface) bc.getService(oatRef);
    if (oat == null)
	    throw new Exception("Cannot start the MidletContainer as OAT is not running!");
		
		bundleIDs = loadVector("BundleIDs");
		for (int j = 0; j < bundleIDs.size(); j++) {
			Bundle bundle = bc.getBundle(Long.parseLong((String) bundleIDs
					.get(j)));
			if (bundle == null || bundle.getState() == 1) {
				bundleIDs.remove(j--);
				saveVector(bundleIDs, "BundleIDs");
			}
		}

		for (int i = 0; i != bundleIDs.size(); i++)
			registerBundle(Long.parseLong((String) bundleIDs.get(i)));

		bc.addBundleListener(this);
		Bundle bundles[] = bc.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			String id = (new StringBuffer(String.valueOf(bundles[i]
					.getBundleId()))).toString();
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
			saveVector(bundleIDs, "BundleIDs");
			checkAutoStart(bundleID, true);
			return;
		}
	}

	public MIDlet createMidletInstance(MidletDescriptorImpl appDesc,
			Vector serviceVector, boolean resume) throws Exception {
		if (!checkSingletonity(appDesc, resume))
			throw new Exception("Singleton Exception!");
		MEGBundleDescriptor desc = getBundleDescriptor(appDesc.getBundleId());
		int i = getApplicationIndex(desc, appDesc.getMidletDescriptor());
		Dependencies deps = desc.dependencies[i];
		String depResult = checkDependencies(appDesc, deps);
		if (depResult != null)
			throw new Exception(
					"Can't start the application because of failed dependencies!");
		Bundle appBundle = bc.getBundle(appDesc.getBundleId());
		Class mainClass = appBundle.loadClass(appDesc.getStartClass());
		ClassLoader loader = new MIDletClassLoader(getClass().getClassLoader(),
				appBundle, mainClass.getProtectionDomain());
		Class midletClass = loader.loadClass(appDesc.getStartClass());
		Constructor constructor = midletClass
				.getDeclaredConstructor(new Class[0]);
		constructor.setAccessible(true);
		MIDlet app = (MIDlet) constructor.newInstance(new Object[0]);
		for (int j = 0; j != deps.dependentFields.length; j++) {
			Field field = midletClass.getDeclaredField(deps.dependentFields[i]);
			ServiceReference ref = bc
					.getServiceReference(deps.requiredService[i]);
			serviceVector.add(ref);
			Object serviceObj = bc.getService(ref);
			field.setAccessible(true);
			field.set(app, serviceObj);
		}

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

	private String checkDependencies(MidletDescriptorImpl appDesc,
			Dependencies deps) {
		try {
			for (int i = 0; i != deps.requiredService.length; i++) {
				String serviceToCheck = deps.requiredService[i];
				if (serviceToCheck != null) {
					ServiceReference refs[] = bc.getServiceReferences(
							serviceToCheck, null);
					if (refs == null || refs.length == 0)
						return "Dependencies failed because of a missing service("
								+ serviceToCheck + ")!";
				}
			}

			return null;
		}
		catch (Exception e) {
			log(
					bc,
					1,
					"Exception occurred at checking the dependencies of a midlet!",
					e);
		}
		return "Exception occurred at checking the dependencies!";
	}

	private boolean checkSingletonity(MidletDescriptorImpl appDesc,
			boolean resume) {
		if (!appDesc.isSingleton())
			return true;
		try {
			ServiceReference appList[] = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.descriptor=" + appDesc.getPID() + ")");
			if (appList == null || appList.length == 0)
				return true;
			if (!resume) {
				return false;
			}
			else {
				ApplicationHandle appHandle = (ApplicationHandle) bc
						.getService(appList[0]);
				boolean result = appHandle.getState().equals("PAUSED");
				bc.ungetService(appList[0]);
				return result;
			}
		}
		catch (Exception e) {
			log(
					bc,
					1,
					"Exception occurred at checking the singletonity of a midlet!",
					e);
		}
		return false;
	}

	public boolean isLaunchable(MidletDescriptorImpl appDesc) {
		try {
			if (!checkSingletonity(appDesc, false))
				return false;
			if (appDesc.isLocked())
				return false;
			if( !oat.isLaunchable( appDesc.getBundle(), appDesc.getStartClass() ) )
				return false;
			MEGBundleDescriptor desc = getBundleDescriptor(appDesc
					.getBundleId());
			if (desc == null)
				return false;
			int i = getApplicationIndex(desc, appDesc.getMidletDescriptor());
			if (i == -1) {
				return false;
			}
			else {
				String depResult = checkDependencies(appDesc,
						desc.dependencies[i]);
				return true;
			}
		}
		catch (Exception e) {
			log(
					bc,
					1,
					"Exception occurred at checking if the midlet is launchable!",
					e);
		}
		return false;
	}

	private void checkAutoStart(long bundleID, boolean isInstalling) {
		try {
			MEGBundleDescriptor desc = getBundleDescriptor(bundleID);
			if (bc.getBundle(bundleID).getState() != 32) {
				if (isInstalling)
					desc.autoStartRequired = true;
				return;
			}
			if (!isInstalling && !desc.autoStartRequired)
				return;
			ApplicationDescriptor appDescs[] = desc.applications;
			for (int i = 0; i != appDescs.length; i++)
				if (appDescs[i].getProperties("en")
						.get("application.autostart").equals("true"))
					appDescs[i].launch(null);

			desc.autoStartRequired = false;
		}
		catch (Exception e) {
			log(
					bc,
					1,
					"Exception occurred at checking trying to autostart a midlet!",
					e);
		}
	}

	public void unregisterAllApplications() throws Exception {
		for (int i = 0; i != bundleIDs.size(); i++)
			unregisterApplicationDescriptors(Long.parseLong((String) bundleIDs
					.get(i)));

		bc.ungetService( oatRef );
		bc.removeBundleListener( this );
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
					checkAutoStart(bundleID, false);
					break;

				case 4 : // '\004'
					unregisterApplicationDescriptors(bundleID);
					break;

				case 16 : // '\020'
					bundleIDs.remove(bundleStr);
					saveVector(bundleIDs, "BundleIDs");
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

	private Vector loadVector(String fileName) {
		Vector resultVector = new Vector();
		try {
			File vectorFile = bc.getDataFile(fileName);
			if (vectorFile.exists()) {
				FileInputStream stream = new FileInputStream(vectorFile);
				String codedIds = "";
				byte buffer[] = new byte[1024];
				int length;
				while ((length = stream.read(buffer, 0, buffer.length)) > 0)
					codedIds = codedIds + new String(buffer);
				stream.close();
				if (!codedIds.equals("")) {
					int comma;
					for (int index = 0; index != -1; index = comma) {
						comma = codedIds.indexOf(',', index);
						String name;
						if (comma >= 0)
							name = codedIds.substring(index, comma);
						else
							name = codedIds.substring(index);
						resultVector.add(name.trim());
					}

				}
			}
		}
		catch (Exception e) {
			log(bc, 1, "Exception occurred at loading '" + fileName + "'!", e);
		}
		return resultVector;
	}

	private void saveVector(Vector vector, String fileName) {
		try {
			File vectorFile = bc.getDataFile(fileName);
			FileOutputStream stream = new FileOutputStream(vectorFile);
			for (int i = 0; i != vector.size(); i++)
				stream.write(((i != 0 ? "," : "") + (String) vector.get(i))
						.getBytes());

			stream.close();
		}
		catch (Exception e) {
			log(bc, 1, "Exception occurred at saving '" + fileName + "'!", e);
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
			LinkedList eventVector = new LinkedList();
			LinkedList dependencyVector = new LinkedList();
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
							LinkedList eventTopic = new LinkedList();
							LinkedList eventAction = new LinkedList();
							LinkedList requiredServices = new LinkedList();
							LinkedList requiredPackages = new LinkedList();
							props.setProperty("application.bundle.id", Long
									.toString(bundleID));
							props.setProperty("application.vendor",
									MIDletVendor);
							props.setProperty("application.singleton", "true");
							props.setProperty("application.visible", "true");
							props.setProperty("application.version",
									MIDletVersion);
							props.setProperty("application.autostart", "false");
							String defaultLang = Locale.getDefault()
									.getLanguage();
							names.put(defaultLang, MIDletName);
							icons.put(defaultLang, MIDletIcon);
							String uniqueID = MIDletName + "-"
									+ MIDletSuiteName + "-" + MIDletVersion;
							props.put("service.pid", uniqueID);
							EventSubscribe subscribe = new EventSubscribe();
							Dependencies deps = new Dependencies();
							String dependentFields = (String) manifest
									.get("Dependent-Field-" + midletCounter);
							if (dependentFields != null) {
								LinkedList depVector = new LinkedList();
								for (StringTokenizer tokens = new StringTokenizer(
										dependentFields, ","); tokens
										.hasMoreTokens(); depVector.add(tokens
										.nextToken().trim()));
								deps.dependentFields = new String[depVector
										.size()];
								deps.requiredService = new String[depVector
										.size()];
								int counter = 0;
								while (depVector.size() != 0)
									deps.dependentFields[counter++] = (String) depVector
											.removeFirst();
								Bundle appBundle = bc.getBundle(bundleID);
								ClassLoader loader = new MIDletClassLoader(
										getClass().getClassLoader(), appBundle,
										getClass().getProtectionDomain());
								Class startClass = loader
										.loadClass(MIDletStartClass);
								for (int i = 0; i != deps.dependentFields.length; i++) {
									Field field = startClass
											.getDeclaredField(deps.dependentFields[i]);
									deps.requiredService[i] = field.getType()
											.getName();
									if (deps.requiredService[i]
											.equals(org.osgi.framework.BundleContext.class
													.getName()))
										deps.requiredService[i] = null;
								}

							}
							eventVector.add(subscribe);
							appVector.add(createMidletDescriptorByReflection(
									props, names, icons, defaultLang,
									MIDletStartClass, bc.getBundle(bundleID)));
							dependencyVector.add(deps);
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
			descriptor.autoStartRequired = false;
			descriptor.applications = new ApplicationDescriptor[applicationNum];
			descriptor.eventSubscribes = new EventSubscribe[applicationNum];
			descriptor.dependencies = new Dependencies[applicationNum];
			for (int l = 0; l != applicationNum; l++) {
				descriptor.applications[l] = descs[l];
				descriptor.eventSubscribes[l] = (EventSubscribe) eventVector
						.removeFirst();
				descriptor.dependencies[l] = (Dependencies) dependencyVector
						.removeFirst();
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
		try {
			for (Enumeration megBundles = bundleHash.keys(); megBundles
					.hasMoreElements();) {
				Object key = megBundles.nextElement();
				MEGBundleDescriptor bundleDesc = (MEGBundleDescriptor) bundleHash
						.get(key);
				for (int i = 0; i != bundleDesc.eventSubscribes.length; i++)
					if (bundleDesc.eventSubscribes[i] != null
							&& bundleDesc.eventSubscribes[i].eventTopic != null) {
						for (int j = 0; j != bundleDesc.eventSubscribes[i].eventTopic.length; j++) {
							org.osgi.framework.Filter topicFilter = bc
									.createFilter("(event.topics="
											+ bundleDesc.eventSubscribes[i].eventTopic[j]
											+ ")");
							if (event.matches(topicFilter))
								switch (bundleDesc.eventSubscribes[i].eventAction[j]) {
									default :
										break;

									case 1 : // '\001'
										bundleDesc.applications[i].launch(null);
										break;

									case 2 : // '\002'
									case 3 : // '\003'
									case 4 : // '\004'
										String pid = (String) bundleDesc.applications[i]
												.getProperties("").get(
														"service.pid");
										ServiceReference references[] = bc
												.getServiceReferences(
														"org.osgi.service.application.ApplicationHandle",
														"(application.descriptor="
																+ pid + ")");
										if (references == null
												|| references.length == 0)
											break;
										for (int k = 0; k != references.length; k++) {
											MidletHandle handle = (MidletHandle) bc
													.getService(references[k]);
											switch (bundleDesc.eventSubscribes[i].eventAction[j]) {
												case 2 : // '\002'
													handle.destroy();
													break;

												case 3 : // '\003'
													handle.pause();
													break;

												case 4 : // '\004'
													handle.resume();
													break;
											}
											bc.ungetService(references[k]);
										}

										break;
								}
						}

					}

			}

		}
		catch (Exception e) {
			log(bc, 1, "Exception occurred at processing a channel event!", e);
		}
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