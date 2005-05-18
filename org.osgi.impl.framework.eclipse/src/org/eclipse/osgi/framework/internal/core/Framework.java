/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.internal.core;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import org.eclipse.osgi.framework.adaptor.*;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.eventmgr.*;
import org.eclipse.osgi.framework.internal.protocol.ContentHandlerFactory;
import org.eclipse.osgi.framework.internal.protocol.StreamHandlerFactory;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.framework.util.SecureAction;
import org.eclipse.osgi.internal.profile.Profile;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.*;

/**
 * Core OSGi Framework class.
 */
public class Framework implements EventDispatcher, EventPublisher {
	/** FrameworkAdaptor specific functions. */
	protected FrameworkAdaptor adaptor;
	/** Framework properties object.  A reference to the 
	 * System.getProperies() object.  The properties from
	 * the adaptor will be merged into these properties.
	 */
	protected Properties properties;
	/** Has the service space been started */
	protected boolean active;
	/** The bundles installed in the framework */
	protected BundleRepository bundles;
	/** Package Admin object. This object manages the exported packages. */
	protected PackageAdminImpl packageAdmin;
	/** Package Admin object. This object manages the exported packages. */
	protected PermissionAdminImpl permissionAdmin;
	/**
	 * Startlevel object. This object manages the framework and bundle
	 * startlevels
	 */
	protected StartLevelManager startLevelManager;
	/** The ServiceRegistry */
	protected ServiceRegistry serviceRegistry; //TODO This is duplicated from the adaptor, do we really gain ?
	/** next free service id. */
	protected long serviceid;
	/** the VM profile (execution environment */
	private String vmProfile;
	/*
	 * The following EventListeners objects keep track of event listeners
	 * by BundleContext.  Each element is a EventListeners that is the list
	 * of event listeners for a particular BundleContext.  The max number of
	 * elements each of the following lists will have is the number of bundles
	 * installed in the Framework.
	 */
	/** List of BundleContexts for bundle's BundleListeners. */
	protected EventListeners bundleEvent;
	protected static final int BUNDLEEVENT = 1;
	/** List of BundleContexts for bundle's SynchronousBundleListeners. */
	protected EventListeners bundleEventSync;
	protected static final int BUNDLEEVENTSYNC = 2;
	/** List of BundleContexts for bundle's ServiceListeners. */
	protected EventListeners serviceEvent;
	protected static final int SERVICEEVENT = 3;
	/** List of BundleContexts for bundle's FrameworkListeners. */
	protected EventListeners frameworkEvent;
	protected static final int FRAMEWORKEVENT = 4;
	protected static final int BATCHEVENT_BEGIN = Integer.MIN_VALUE + 1;
	protected static final int BATCHEVENT_END = Integer.MIN_VALUE;
	/** EventManager for event delivery. */
	protected EventManager eventManager;
	/* Reservation object for install synchronization */
	protected Hashtable installLock;
	/** System Bundle object */
	protected SystemBundle systemBundle;
	String[] bootDelegation;
	String[] bootDelegationStems;
	boolean bootDelegateAll = false;
	boolean contextBootDelegation = "true".equals(System.getProperty("osgi.context.bootdelegation", "true")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * The AliasMapper used to alias OS Names.
	 */
	protected static AliasMapper aliasMapper = new AliasMapper();
	protected ConditionalPermissionAdminImpl condPermAdmin;
	SecureAction secureAction = new SecureAction();

	/**
	 * Constructor for the Framework instance. This method initializes the
	 * framework to an unlaunched state.
	 *  
	 */
	public Framework(FrameworkAdaptor adaptor) {
		initialize(adaptor);
	}

	/**
	 * Initialize the framework to an unlaunched state. This method is called
	 * by the Framework constructor.
	 *  
	 */
	protected void initialize(FrameworkAdaptor adaptor) {
		if (Profile.PROFILE && Profile.STARTUP)
			Profile.logEnter("Framework.initialze()", null); //$NON-NLS-1$
		long start = System.currentTimeMillis();
		this.adaptor = adaptor;
		active = false;
		installSecurityManager();
		if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
			Debug.println("SecurityManager: " + System.getSecurityManager()); //$NON-NLS-1$
			Debug.println("ProtectionDomain of Framework.class: \n" + this.getClass().getProtectionDomain()); //$NON-NLS-1$
		}
		// set the adaptor of MessageResourceBundle so it can log warnings/errors
		MessageResourceBundle.setAdaptor(adaptor);
		/* initialize the adaptor */
		adaptor.initialize(this);
		if (Profile.PROFILE && Profile.STARTUP)
			Profile.logTime("Framework.initialze()", "adapter initialized"); //$NON-NLS-1$//$NON-NLS-2$
		try {
			adaptor.initializeStorage();
			adaptor.compactStorage();
		} catch (IOException e) /* fatal error */{
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		if (Profile.PROFILE && Profile.STARTUP)
			Profile.logTime("Framework.initialze()", "adapter storage initialized"); //$NON-NLS-1$//$NON-NLS-2$
		/*
		 * This must be done before calling any of the framework getProperty
		 * methods.
		 */
		initializeProperties(adaptor.getProperties());
		/* initialize admin objects */
		packageAdmin = new PackageAdminImpl(this);
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			try {
				permissionAdmin = new PermissionAdminImpl(this, adaptor.getPermissionStorage());
			} catch (IOException e) /* fatal error */{
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
			try {
				condPermAdmin = new ConditionalPermissionAdminImpl(this, adaptor.getPermissionStorage());
			} catch (IOException e) /* fatal error */{
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
		if (Profile.PROFILE && Profile.STARTUP)
			Profile.logTime("Framework.initialze()", "done init props & new PermissionAdminImpl"); //$NON-NLS-1$//$NON-NLS-2$
		startLevelManager = new StartLevelManager(this);
		/* create the event manager and top level event dispatchers */
		eventManager = new EventManager("Framework Event Dispatcher"); //$NON-NLS-1$
		bundleEvent = new EventListeners();
		bundleEventSync = new EventListeners();
		serviceEvent = new EventListeners();
		frameworkEvent = new EventListeners();
		if (Profile.PROFILE && Profile.STARTUP)
			Profile.logTime("Framework.initialze()", "done new EventManager"); //$NON-NLS-1$ //$NON-NLS-2$
		/* create the service registry */
		serviceid = 1;
		serviceRegistry = adaptor.getServiceRegistry();
		// Initialize the installLock; there is no way of knowing 
		// what the initial size should be, at most it will be the number
		// of threads trying to install a bundle (probably a very low number).
		installLock = new Hashtable(10);
		/* create the system bundle */
		createSystemBundle();
		loadVMProfile(); // load VM profile after the system bundle has been created
		setBootDelegation(); //set boot delegation property after system exports have been set
		if (Profile.PROFILE && Profile.STARTUP)
			Profile.logTime("Framework.initialze()", "done createSystemBundle"); //$NON-NLS-1$ //$NON-NLS-2$
		/* install URLStreamHandlerFactory */
		URL.setURLStreamHandlerFactory(new StreamHandlerFactory(systemBundle.context, adaptor));
		/* install ContentHandlerFactory for OSGi URLStreamHandler support */
		URLConnection.setContentHandlerFactory(new ContentHandlerFactory(systemBundle.context));
		if (Profile.PROFILE && Profile.STARTUP)
			Profile.logTime("Framework.initialze()", "done new URLStream/Content HandlerFactory"); //$NON-NLS-1$//$NON-NLS-2$
		/* create bundle objects for all installed bundles. */
		BundleData[] bundleDatas = adaptor.getInstalledBundles();
		bundles = new BundleRepository(bundleDatas == null ? 10 : bundleDatas.length + 1, packageAdmin);
		/* add the system bundle to the Bundle Repository */
		bundles.add(systemBundle);
		if (bundleDatas != null) {
			for (int i = 0; i < bundleDatas.length; i++) {
				try {
					AbstractBundle bundle = AbstractBundle.createBundle(bundleDatas[i], this);
					bundles.add(bundle);
				} catch (BundleException be) {
					// This is not a fatal error. Publish the framework event.
					publishFrameworkEvent(FrameworkEvent.ERROR, systemBundle, be);
				}
			}
		}
		if (Debug.DEBUG && Debug.DEBUG_GENERAL)
			System.out.println("Initialize the framework: " + (System.currentTimeMillis() - start)); //$NON-NLS-1$
		if (Profile.PROFILE && Profile.STARTUP)
			Profile.logExit("Framework.initialize()"); //$NON-NLS-1$
	}

	private void createSystemBundle() {
		try {
			systemBundle = new SystemBundle(this);
		} catch (BundleException e) { // fatal error
			e.printStackTrace();
			throw new RuntimeException(NLS.bind(Msg.OSGI_SYSTEMBUNDLE_CREATE_EXCEPTION, e.getMessage()));
		}
	}

	/**
	 * Initialize the System properties by copying properties from the adaptor
	 * properties object. This method is called by the initialize method.
	 *  
	 */
	protected void initializeProperties(Properties adaptorProperties) {
		properties = System.getProperties();
		Enumeration enumKeys = adaptorProperties.propertyNames();
		while (enumKeys.hasMoreElements()) {
			String key = (String) enumKeys.nextElement();
			if (properties.getProperty(key) == null) {
				properties.put(key, adaptorProperties.getProperty(key));
			}
		}
		properties.put(Constants.FRAMEWORK_VENDOR, Constants.OSGI_FRAMEWORK_VENDOR);
		properties.put(Constants.FRAMEWORK_VERSION, Constants.OSGI_FRAMEWORK_VERSION);
		// Needed for communication with Bundle Server
		properties.put(Constants.OSGI_IMPL_VERSION_KEY, Constants.OSGI_IMPL_VERSION);
		String value = properties.getProperty(Constants.FRAMEWORK_PROCESSOR);
		if (value == null) {
			value = properties.getProperty(Constants.JVM_OS_ARCH);
			if (value != null) {
				properties.put(Constants.FRAMEWORK_PROCESSOR, value);
			}
		}
		value = properties.getProperty(Constants.FRAMEWORK_OS_NAME);
		if (value == null) {
			value = properties.getProperty(Constants.JVM_OS_NAME);
			try {
				String canonicalValue = (String) aliasMapper.aliasOSName(value);
				if (canonicalValue != null) {
					value = canonicalValue;
				}
			} catch (ClassCastException ex) {
				//A vector was returned from the alias mapper.
				//The alias mapped to more than one canonical value
				//such as "win32" for example
			}
			if (value != null) {
				properties.put(Constants.FRAMEWORK_OS_NAME, value);
			}
		}
		value = properties.getProperty(Constants.FRAMEWORK_OS_VERSION);
		if (value == null) {
			value = properties.getProperty(Constants.JVM_OS_VERSION);
			if (value != null) {
				int space = value.indexOf(' ');
				if (space > 0) {
					value = value.substring(0, space);
				}
				properties.put(Constants.FRAMEWORK_OS_VERSION, value);
			}
		}
		value = properties.getProperty(Constants.FRAMEWORK_LANGUAGE);
		if (value == null) {
			value = properties.getProperty(Constants.JVM_USER_LANGUAGE);
			// set default locale for VM
			if (value != null) {
				properties.put(Constants.FRAMEWORK_LANGUAGE, value);
				StringTokenizer tokenizer = new StringTokenizer(value, "_"); //$NON-NLS-1$
				int segments = tokenizer.countTokens();
				try {
					switch (segments) {
						case 2 :
							Locale userLocale = new Locale(tokenizer.nextToken(), tokenizer.nextToken());
							Locale.setDefault(userLocale);
							break;
						case 3 :
							userLocale = new Locale(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
							Locale.setDefault(userLocale);
							break;
					}
				} catch (NoSuchElementException e) {
					// fall through and use the default
				}
			}
		}
		setExecutionEnvironment();
	}

	private void setExecutionEnvironment() {
		String value = properties.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT, ""); //$NON-NLS-1$
		String j2meConfig = properties.getProperty(Constants.J2ME_MICROEDITION_CONFIGURATION);
		String j2meProfile = properties.getProperty(Constants.J2ME_MICROEDITION_PROFILES);
		StringBuffer ee = new StringBuffer(value);
		if (j2meConfig != null && j2meConfig.length() > 0 && j2meProfile != null && j2meProfile.length() > 0) {
			// save the vmProfile based off of the config and profile
			vmProfile = j2meConfig + '_' + j2meProfile;
			int ic = value.indexOf(j2meConfig);
			// append the profile only if it is not already present
			if (!(ic >= 0) || !(ic + j2meConfig.length() < value.length() && value.charAt(ic + j2meConfig.length()) == '/') || !(value.startsWith(j2meProfile, ic + j2meConfig.length() + 1))) {
				if (ee.length() > 0) {
					ee.append(',');
				}
				ee.append(j2meConfig).append('/').append(j2meProfile);
			}

		} else if (value.length() > 0) {
			// just use the first EE defined as our profile
			StringTokenizer st = new StringTokenizer(value, ","); //$NON-NLS-1$
			vmProfile = st.nextToken().replace('/', '_');
		} else {
			String javaSpecVersion = properties.getProperty("java.specification.version"); //$NON-NLS-1$
			// set the profile and EE based off of the java.specification.version
			// TODO We assume J2SE here.  need to support other profiles J2ME/J2EE ...
			if (javaSpecVersion != null) {
				StringTokenizer st = new StringTokenizer(javaSpecVersion, " _-"); //$NON-NLS-1$
				javaSpecVersion = st.nextToken();
				vmProfile = "J2SE-" + javaSpecVersion; //$NON-NLS-1$
				int index = value.indexOf(vmProfile);
				if (index < 0) {
					if (ee.length() > 0)
						ee.append(',');
					ee.append(vmProfile);
				}
			}
		}
		properties.put(Constants.FRAMEWORK_EXECUTIONENVIRONMENT, ee.toString());
	}

	private void setBootDelegation() {
		String bootDelegationProp = properties.getProperty(Constants.OSGI_BOOTDELEGATION);
		if (bootDelegationProp == null)
			return;
		if (bootDelegationProp.trim().length() == 0)
			return;
		String[] bootPackages = ManifestElement.getArrayFromList(bootDelegationProp);
		ArrayList exactMatch = new ArrayList(bootPackages.length);
		ArrayList stemMatch = new ArrayList(bootPackages.length);
		for (int i = 0; i < bootPackages.length; i++) {
			if (bootPackages[i].equals("*")) { //$NON-NLS-1$
				bootDelegateAll = true;
				return;
			} else if (bootPackages[i].endsWith("*")) { //$NON-NLS-1$
				if (bootPackages[i].length() > 2 && bootPackages[i].endsWith(".*")) //$NON-NLS-1$
					stemMatch.add(bootPackages[i].substring(0, bootPackages[i].length() - 1));
			} else {
				exactMatch.add(bootPackages[i]);
			}
		}
		if (exactMatch.size() > 0)
			bootDelegation = (String[]) exactMatch.toArray(new String[exactMatch.size()]);
		if (stemMatch.size() > 0)
			bootDelegationStems = (String[]) stemMatch.toArray(new String[stemMatch.size()]);
	}

	private void loadVMProfile() {
		InputStream in = findVMProfile();
		Properties profileProps = new Properties();
		if (in != null) {
			try {
				profileProps.load(new BufferedInputStream(in));
			} catch (IOException e) {
				// do nothing
			} finally {
				try {
					in.close();
				} catch (IOException ee) {
					// do nothing
				}
			}
		}
		String systemExports = properties.getProperty(Constants.OSGI_FRAMEWORK_SYSTEM_PACKAGES);
		// set the system exports property using the vm profile; only if the property is not already set
		if (systemExports == null) {
			systemExports = profileProps.getProperty(Constants.OSGI_FRAMEWORK_SYSTEM_PACKAGES);
			if (systemExports != null)
				properties.put(Constants.OSGI_FRAMEWORK_SYSTEM_PACKAGES, systemExports);
		}
		// set the org.osgi.framework.bootdelegation property according to the java profile
		String type = properties.getProperty(Constants.OSGI_JAVA_PROFILE_BOOTDELEGATION); // a null value means ignore
		String profileBootDelegation = profileProps.getProperty(Constants.OSGI_BOOTDELEGATION);
		if (Constants.OSGI_BOOTDELEGATION_OVERRIDE.equals(type)) {
			if (profileBootDelegation == null)
				properties.remove(Constants.OSGI_BOOTDELEGATION); // override with a null value
			else
				properties.put(Constants.OSGI_BOOTDELEGATION, profileBootDelegation); // override with the profile value
		} else if (Constants.OSGI_BOOTDELEGATION_NONE.equals(type))
			properties.remove(Constants.OSGI_BOOTDELEGATION); // remove the bootdelegation property in case it was set
	}

	private InputStream findVMProfile() {
		URL url = null;
		// check for the java profile property for a url
		String propJavaProfile = System.getProperty(Constants.OSGI_JAVA_PROFILE);
		if (propJavaProfile != null)
			try {
				// we assume a URL
				url = new URL(propJavaProfile);
			} catch (MalformedURLException e1) {
				// TODO consider logging ...
			}
		if (url == null && vmProfile != null) {
			// look for a profile in the system bundle based on the vm profile
			String javaProfile = vmProfile + ".profile"; //$NON-NLS-1$
			url = systemBundle.getEntry(javaProfile);
			if (url == null)
				url = getClass().getResource(javaProfile);
		}
		if (url != null)
			try {
				return url.openStream();
			} catch (IOException e) {
				// TODO consider logging ...
			}
		return null;
	}

	/**
	 * This method return the state of the framework.
	 *  
	 */
	protected boolean isActive() {
		return (active);
	}

	/**
	 * This method is called to destory the framework instance.
	 *  
	 */
	public synchronized void close() {
		if (active) {
			shutdown();
		}
		synchronized (bundles) {
			List allBundles = bundles.getBundles();
			int size = allBundles.size();
			for (int i = 0; i < size; i++) {
				AbstractBundle bundle = (AbstractBundle) allBundles.get(i);
				bundle.close();
			}
			bundles.removeAllBundles();
		}
		serviceRegistry = null;
		if (bundleEvent != null) {
			bundleEvent.removeAllListeners();
			bundleEvent = null;
		}
		if (bundleEventSync != null) {
			bundleEventSync.removeAllListeners();
			bundleEventSync = null;
		}
		if (serviceEvent != null) {
			serviceEvent.removeAllListeners();
			serviceEvent = null;
		}
		if (frameworkEvent != null) {
			frameworkEvent.removeAllListeners();
			frameworkEvent = null;
		}
		if (eventManager != null) {
			eventManager.close();
			eventManager = null;
		}
		permissionAdmin = null;
		condPermAdmin = null;
		packageAdmin = null;
		adaptor = null;
	}

	/**
	 * Start the framework.
	 * 
	 * When the framework is started. The following actions occur: 1. Event
	 * handling is enabled. Events can now be delivered to listeners. 2. All
	 * bundles which are recorded as started are started as described in the
	 * Bundle.start() method. These bundles are the bundles that were started
	 * when the framework was last stopped. Reports any exceptions that occur
	 * during startup using FrameworkEvents. 3. A FrameworkEvent of type
	 * FrameworkEvent.STARTED is broadcast.
	 *  
	 */
	public synchronized void launch() {
		/* Return if framework already started */
		if (active) {
			return;
		}
		/* mark framework as started */
		active = true;
		/* Resume systembundle */
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			Debug.println("Trying to launch framework"); //$NON-NLS-1$
		}
		systemBundle.resume();
	}

	/**
	 * Stop the framework.
	 * 
	 * When the framework is stopped. The following actions occur: 1. Suspend
	 * all started bundles as described in the Bundle.stop method except that
	 * the bundle is recorded as started. These bundles will be restarted when
	 * the framework is next started. Reports any exceptions that occur during
	 * stopping using FrameworkEvents. 2. Event handling is disabled.
	 *  
	 */
	public synchronized void shutdown() {
		/* Return if framework already stopped */
		if (!active) {
			return;
		}
		/*
		 * set the state of the System Bundle to STOPPING.
		 * this must be done first according to section 4.19.2 from the OSGi R3 spec.  
		 */
		systemBundle.state = AbstractBundle.STOPPING;
		/* call the FrameworkAdaptor.frameworkStopping method first */
		try {
			adaptor.frameworkStopping(systemBundle.getContext());
		} catch (Throwable t) {
			publishFrameworkEvent(FrameworkEvent.ERROR, systemBundle, t);
		}
		/* Suspend systembundle */
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			Debug.println("Trying to shutdown Framework"); //$NON-NLS-1$
		}
		systemBundle.suspend();
		try {
			adaptor.compactStorage();
		} catch (IOException e) {
			publishFrameworkEvent(FrameworkEvent.ERROR, systemBundle, e);
		}
		/* mark framework as stopped */
		active = false;
	}

	/**
	 * Create a new Bundle object.
	 * 
	 * @param bundledata the BundleData of the Bundle to create
	 */
	AbstractBundle createAndVerifyBundle(BundleData bundledata) throws BundleException {
		// TODO Verify the manifest... for example that the same package is imported twice 
		// Check for a bundle already installed with the same symbolic name and version.
		if (bundledata.getSymbolicName() != null) {
			AbstractBundle installedBundle = getBundleBySymbolicName(bundledata.getSymbolicName(), bundledata.getVersion());
			if (installedBundle != null && installedBundle.getBundleId() != bundledata.getBundleID()) {
				throw new BundleException(NLS.bind(Msg.BUNDLE_INSTALL_SAME_UNIQUEID, new Object[] {installedBundle.getSymbolicName(), installedBundle.getVersion().toString(), installedBundle.getLocation()}));
			}
		}
		verifyExecutionEnvironment(bundledata.getManifest());
		return AbstractBundle.createBundle(bundledata, this);
	}

	/**
	 * Verifies that the framework supports one of the required Execution
	 * Environments
	 * 
	 * @param manifest
	 *            BundleManifest of the bundle to verify the Execution
	 *            Enviroment for
	 * @return boolean true if the required Execution Enviroment is available.
	 * @throws BundleException
	 *             if the framework does not support the required Execution
	 *             Environment.
	 */
	protected boolean verifyExecutionEnvironment(Dictionary manifest) throws BundleException {
		String headerValue = (String) manifest.get(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT);
		/* If no required EE is in the manifest return true */
		if (headerValue == null) {
			return true;
		}
		ManifestElement[] bundleRequiredEE = ManifestElement.parseHeader(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT, headerValue);
		if (bundleRequiredEE.length == 0) {
			return true;
		}
		String systemEE = System.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
		if (systemEE != null && !systemEE.equals("")) { //$NON-NLS-1$
			ManifestElement[] systemEEs = ManifestElement.parseHeader(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT, systemEE);
			for (int i = 0; i < systemEEs.length; i++) {
				for (int j = 0; j < bundleRequiredEE.length; j++) {
					if (systemEEs[i].getValue().equals(bundleRequiredEE[j].getValue())) {
						return true;
					}
				}
			}
		}
		/* If we got here then no matching EE is available, throw exception. */
		StringBuffer bundleEE = new StringBuffer(25);
		for (int i = 0; i < bundleRequiredEE.length; i++) {
			if (i > 0) {
				bundleEE.append(","); //$NON-NLS-1$
			}
			bundleEE.append(bundleRequiredEE[i].getValue());
		}
		throw new BundleException(NLS.bind(Msg.BUNDLE_INSTALL_REQUIRED_EE_EXCEPTION, bundleEE.toString()));
	}

	/**
	 * Retrieve the value of the named environment property. Values are
	 * provided for the following properties:
	 * <dl>
	 * <dt><code>org.osgi.framework.version</code>
	 * <dd>The version of the framework.
	 * <dt><code>org.osgi.framework.vendor</code>
	 * <dd>The vendor of this framework implementation.
	 * <dt><code>org.osgi.framework.language</code>
	 * <dd>The language being used. See ISO 639 for possible values.
	 * <dt><code>org.osgi.framework.os.name</code>
	 * <dd>The name of the operating system of the hosting computer.
	 * <dt><code>org.osgi.framework.os.version</code>
	 * <dd>The version number of the operating system of the hosting computer.
	 * <dt><code>org.osgi.framework.processor</code>
	 * <dd>The name of the processor of the hosting computer.
	 * </dl>
	 * 
	 * <p>
	 * Note: These last four properties are used by the <code>Bundle-NativeCode</code>
	 * manifest header's matching algorithm for selecting native code.
	 * 
	 * @param key
	 *            The name of the requested property.
	 * @return The value of the requested property, or <code>null</code> if
	 *         the property is undefined.
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Retrieve the value of the named environment property. Values are
	 * provided for the following properties:
	 * <dl>
	 * <dt><code>org.osgi.framework.version</code>
	 * <dd>The version of the framework.
	 * <dt><code>org.osgi.framework.vendor</code>
	 * <dd>The vendor of this framework implementation.
	 * <dt><code>org.osgi.framework.language</code>
	 * <dd>The language being used. See ISO 639 for possible values.
	 * <dt><code>org.osgi.framework.os.name</code>
	 * <dd>The name of the operating system of the hosting computer.
	 * <dt><code>org.osgi.framework.os.version</code>
	 * <dd>The version number of the operating system of the hosting computer.
	 * <dt><code>org.osgi.framework.processor</code>
	 * <dd>The name of the processor of the hosting computer.
	 * </dl>
	 * 
	 * <p>
	 * Note: These last four properties are used by the <code>Bundle-NativeCode</code>
	 * manifest header's matching algorithm for selecting native code.
	 * 
	 * @param key
	 *            The name of the requested property.
	 * @param def
	 *            A default value is the requested property is not present.
	 * @return The value of the requested property, or the default value if the
	 *         property is undefined.
	 */
	protected String getProperty(String key, String def) {
		return properties.getProperty(key, def);
	}

	/**
	 * Set a system property.
	 * 
	 * @param key
	 *            The name of the property to set.
	 * @param value
	 *            The value to set.
	 * @return The previous value of the property or null if the property was
	 *         not previously set.
	 */
	protected Object setProperty(String key, String value) {
		return properties.put(key, value);
	}

	/**
	 * Install a bundle from a location.
	 * 
	 * The bundle is obtained from the location parameter as interpreted by the
	 * framework in an implementation dependent way. Typically, location will
	 * most likely be a URL.
	 * 
	 * @param location
	 *            The location identifier of the bundle to install.
	 * @return The Bundle object of the installed bundle.
	 */
	public AbstractBundle installBundle(final String location) throws BundleException {
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			Debug.println("install from location: " + location); //$NON-NLS-1$
		}
		final AccessControlContext callerContext = AccessController.getContext();
		return installWorker(location, new PrivilegedExceptionAction() {
			public Object run() throws BundleException {
				/* Map the identity to a URLConnection */
				URLConnection source = adaptor.mapLocationToURLConnection(location);
				/* call the worker to install the bundle */
				return installWorkerPrivileged(location, source, callerContext);
			}
		});
	}

	/**
	 * Install a bundle from an InputStream.
	 * 
	 * <p>
	 * This method performs all the steps listed in
	 * {@link #installBundle(java.lang.String)}, except the bundle's content
	 * will be read from the InputStream. The location identifier specified
	 * will be used as the identity of the bundle.
	 * 
	 * @param location
	 *            The location identifier of the bundle to install.
	 * @param in
	 *            The InputStream from which the bundle will be read.
	 * @return The Bundle of the installed bundle.
	 */
	protected AbstractBundle installBundle(final String location, final InputStream in) throws BundleException {
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			Debug.println("install from inputstream: " + location + ", " + in); //$NON-NLS-1$ //$NON-NLS-2$
		}
		final AccessControlContext callerContext = AccessController.getContext();
		return installWorker(location, new PrivilegedExceptionAction() {
			public Object run() throws BundleException {
				/* Map the InputStream to a URLConnection */
				URLConnection source = new BundleSource(in);
				/* call the worker to install the bundle */
				return installWorkerPrivileged(location, source, callerContext);
			}
		});
	}

	/**
	 * Worker method to install a bundle. It obtains the reservation for the
	 * location and calls the specified action.
	 * 
	 * @param location
	 *            The location identifier of the bundle to install.
	 * @param action
	 *            A PrivilegedExceptionAction which calls the real worker.
	 * @return The {@link AbstractBundle}of the installed bundle.
	 * @exception BundleException
	 *                If the action throws an error.
	 */
	protected AbstractBundle installWorker(String location, PrivilegedExceptionAction action) throws BundleException {
		synchronized (installLock) {
			while (true) {
				/* Check that the bundle is not already installed. */
				AbstractBundle bundle = getBundleByLocation(location);
				/* If already installed, return bundle object */
				if (bundle != null) {
					return bundle;
				}
				Thread current = Thread.currentThread();
				/* Check for and make reservation */
				Thread reservation = (Thread) installLock.put(location, current);
				/* if the location is not already reserved */
				if (reservation == null) {
					/* we have made the reservation and can continue */
					break;
				}
				/* the location was already reserved */
				/*
				 * If the reservation is held by the current thread, we have
				 * recursed to install the same bundle!
				 */
				if (current.equals(reservation)) {
					throw new BundleException(Msg.BUNDLE_INSTALL_RECURSION_EXCEPTION);
				}
				try {
					/* wait for the reservation to be released */
					installLock.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		/* Don't call adaptor while holding the install lock */
		try {
			AbstractBundle bundle = (AbstractBundle) AccessController.doPrivileged(action);
			publishBundleEvent(BundleEvent.INSTALLED, bundle);
			return bundle;
		} catch (PrivilegedActionException e) {
			if (e.getException() instanceof RuntimeException)
				throw (RuntimeException) e.getException();
			throw (BundleException) e.getException();
		} finally {
			synchronized (installLock) {
				/* release reservation */
				installLock.remove(location);
				/* wake up all waiters */
				installLock.notifyAll();
			}
		}
	}

	/**
	 * Worker method to install a bundle. It calls the FrameworkAdaptor object
	 * to install the bundle in persistent storage.
	 * 
	 * @param location
	 *            The location identifier of the bundle to install.
	 * @param source
	 *            The URLConnection from which the bundle will be read.
	 * @return The {@link AbstractBundle}of the installed bundle.
	 * @exception BundleException
	 *                If the provided stream cannot be read.
	 */
	protected AbstractBundle installWorkerPrivileged(String location, URLConnection source, AccessControlContext callerContext) throws BundleException {
		BundleOperation storage = adaptor.installBundle(location, source);
		final AbstractBundle bundle;
		try {
			BundleData bundledata = storage.begin();
			bundle = createAndVerifyBundle(bundledata);
			try {
				// Select the native code paths for the bundle;
				// this is not done by the adaptor because this
				// should be a static algorithm spec'ed by OSGi
				// that we do not allow to be adapted.
				String[] nativepaths = selectNativeCode(bundle);
				if (nativepaths != null) {
					bundledata.installNativeCode(nativepaths);
				}
				bundle.load();
				if (System.getSecurityManager() != null) {
					final boolean extension = (bundledata.getType() & (BundleData.TYPE_BOOTCLASSPATH_EXTENSION | BundleData.TYPE_FRAMEWORK_EXTENSION)) != 0;
					// must check for AllPermission before allow a bundle extension to be installed
					if (extension)
						bundle.hasPermission(new AllPermission());
					try {
						AccessController.doPrivileged(new PrivilegedExceptionAction() {
							public Object run() throws Exception {
								checkAdminPermission(bundle, AdminPermission.LIFECYCLE);
								if (extension) // need special permission to install extension bundles
									checkAdminPermission(bundle, AdminPermission.EXTENSIONLIFECYCLE);
								return null;
							}
						}, callerContext);
					} catch (PrivilegedActionException e) {
						throw e.getException();
					}
				}
				storage.commit(false);
			} catch (Throwable error) {
				synchronized (bundles) {
					bundle.unload();
				}
				bundle.close();
				throw error;
			}
			/* bundle has been successfully installed */
			bundles.add(bundle);
		} catch (Throwable t) {
			try {
				storage.undo();
			} catch (BundleException ee) {
				publishFrameworkEvent(FrameworkEvent.ERROR, systemBundle, ee);
			}
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			if (t instanceof BundleException)
				throw (BundleException) t;
			throw new BundleException(t.getMessage(), t);
		}
		return bundle;
	}

	/**
	 * Selects a native code clause and return a list of the bundle entries for
	 * native code to be installed.
	 * 
	 * @param bundle
	 *            Bundle's manifest
	 * @return a list of Strings of the bundle entries to install or <tt>null</tt>
	 *         if there are no native code clauses.
	 * @throws BundleException
	 *             If there is no suitable clause.
	 */
	String[] selectNativeCode(org.osgi.framework.Bundle bundle) throws BundleException {
		String headerValue = (String) ((AbstractBundle) bundle).getBundleData().getManifest().get(Constants.BUNDLE_NATIVECODE);
		if (headerValue == null) {
			return (null);
		}
		ManifestElement[] elements = ManifestElement.parseHeader(Constants.BUNDLE_NATIVECODE, headerValue);
		ArrayList bundleNativeCodes = new ArrayList(elements.length);
		int length = elements.length;
		boolean optional = false;
		if (elements[length - 1].getValue().equals("*")) { //$NON-NLS-1$
			optional = true;
			length--;
		}
		String processor = getProperty(Constants.FRAMEWORK_PROCESSOR);
		String osname = getProperty(Constants.FRAMEWORK_OS_NAME);
		Version osversion;
		try {
			osversion = Version.parseVersion(getProperty(Constants.FRAMEWORK_OS_VERSION));
		} catch (Exception e) {
			osversion = Version.emptyVersion;
		}
		String language = getProperty(Constants.FRAMEWORK_LANGUAGE);
		for (int i = 0; i < length; i++) {
			BundleNativeCode bnc = new BundleNativeCode(elements[i], (AbstractBundle) bundle);
			// Pass 1: perform processor/osname/filter matching
			// Pass 2: perform osversion matching
			// Pass 3: perform language matching
			// If there is not a match on all three, then the native code clause is not selected.
			if (bnc.matchProcessorOSNameFilter(processor, osname) > 0 && bnc.matchOSVersion(osversion) != null && bnc.matchLanguage(language) > 0)
				bundleNativeCodes.add(bnc);
		}

		if (bundleNativeCodes.size() == 0)
			return noMatches(optional);
		// find the highest ranking one with priority on the osversion then the language
		Iterator iter = bundleNativeCodes.iterator();
		BundleNativeCode highestRanking = (BundleNativeCode) iter.next();
		while (iter.hasNext()) {
			BundleNativeCode bnc = (BundleNativeCode) iter.next();
			if (isBncGreaterThan(bnc, highestRanking, osversion, language))
				highestRanking = bnc;
		}
		return highestRanking.getPaths();
	}

	private boolean isBncGreaterThan(BundleNativeCode candidate, BundleNativeCode highestRanking, Version version, String language) {
		Version currentHigh = highestRanking.matchOSVersion(version);
		Version candidateHigh = candidate.matchOSVersion(version);
		if (currentHigh.compareTo(candidateHigh) < 0)
			return true;
		if (highestRanking.matchLanguage(language) < candidate.matchLanguage(language))
			return true;
		return false;
	}

	/**
	 * Retrieve the bundle that has the given unique identifier.
	 * 
	 * @param id
	 *            The identifier of the bundle to retrieve.
	 * @return A {@link AbstractBundle}object, or <code>null</code> if the
	 *         identifier doesn't match any installed bundle.
	 */
	// changed visibility to gain access through the adaptor
	public AbstractBundle getBundle(long id) {
		synchronized (bundles) {
			return bundles.getBundle(id);
		}
	}

	/**
	 * Retrieve the bundle that has the given symbolic name and version.
	 * 
	 * @param symbolicName
	 *            The symbolic name of the bundle to retrieve
	 * @param version The version of the bundle to retrieve
	 * @return A {@link AbstractBundle}object, or <code>null</code> if the
	 *         identifier doesn't match any installed bundle.
	 */
	public AbstractBundle getBundleBySymbolicName(String symbolicName, Version version) {
		synchronized (bundles) {
			return bundles.getBundle(symbolicName, version);
		}
	}

	/**
	 * Retrieve the BundleRepository of all installed bundles. The list is
	 * valid at the time of the call to getBundles, but the framework is a very
	 * dynamic environment and bundles can be installed or uninstalled at
	 * anytime.
	 * 
	 * @return The BundleRepository.
	 */
	protected BundleRepository getBundles() {
		return (bundles);
	}

	/**
	 * Retrieve a list of all installed bundles. The list is valid at the time
	 * of the call to getBundleAlls, but the framework is a very dynamic
	 * environment and bundles can be installed or uninstalled at anytime.
	 * 
	 * @return An Array of {@link AbstractBundle}objects, one object per installed
	 *         bundle.
	 */
	protected AbstractBundle[] getAllBundles() {
		synchronized (bundles) {
			List allBundles = bundles.getBundles();
			int size = allBundles.size();
			if (size == 0) {
				return (null);
			}
			AbstractBundle[] bundlelist = new AbstractBundle[size];
			allBundles.toArray(bundlelist);
			return (bundlelist);
		}
	}

	/**
	 * Resume a bundle.
	 * 
	 * @param bundle
	 *            Bundle to resume.
	 */
	protected void resumeBundle(AbstractBundle bundle) {
		if (bundle.isActive()) {
			// if bundle is active.
			return;
		}
		try {
			int status = bundle.getBundleData().getStatus();
			if ((status & Constants.BUNDLE_STARTED) == 0) {
				return;
			}
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Trying to start bundle " + bundle); //$NON-NLS-1$
			}
			bundle.resume();
		} catch (BundleException be) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Bundle resume exception: " + be.getMessage()); //$NON-NLS-1$
				Debug.printStackTrace(be.getNestedException());
			}
			publishFrameworkEvent(FrameworkEvent.ERROR, bundle, be);
		}
	}

	/**
	 * Suspend a bundle.
	 * 
	 * @param bundle
	 *            Bundle to suspend.
	 * @param lock
	 *            true if state change lock should be held when returning from
	 *            this method.
	 * @return true if bundle was active and is now suspended.
	 */
	protected boolean suspendBundle(AbstractBundle bundle, boolean lock) {
		boolean changed = false;
		if (!bundle.isActive() || bundle.isFragment()) {
			// if bundle is not active or is a fragment then do nothing.
			return changed;
		}
		try {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Trying to suspend bundle " + bundle); //$NON-NLS-1$
			}
			bundle.suspend(lock);
		} catch (BundleException be) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Bundle suspend exception: " + be.getMessage()); //$NON-NLS-1$
				Debug.printStackTrace(be.getNestedException());
			}
			publishFrameworkEvent(FrameworkEvent.ERROR, bundle, be);
		}
		if (!bundle.isActive()) {
			changed = true;
		}
		return (changed);
	}

	/**
	 * Locate an installed bundle with a given identity.
	 * 
	 * @param location
	 *            string for the bundle
	 * @return Bundle object for bundle with the specified location or null if
	 *         no bundle is installed with the specified location.
	 */
	protected AbstractBundle getBundleByLocation(String location) {
		synchronized (bundles) {
			// this is not optimized; do not think it will get called
			// that much.
			final String finalLocation = location;

			//Bundle.getLocation requires AdminPermission (metadata)
			return (AbstractBundle) AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					List allBundles = bundles.getBundles();
					int size = allBundles.size();
					for (int i = 0; i < size; i++) {
						AbstractBundle bundle = (AbstractBundle) allBundles.get(i);
						if (finalLocation.equals(bundle.getLocation())) {
							return (bundle);
						}
					}
					return (null);
				}
			});
		}
	}

	/**
	 * Locate an installed bundle with a given symbolic name
	 * 
	 * @param symbolicName
	 *            The symbolic name for the bundle
	 * @return Bundle object for bundle with the specified Unique or null if no
	 *         bundle is installed with the specified location.
	 */
	protected AbstractBundle[] getBundleBySymbolicName(String symbolicName) {
		synchronized (bundles) {
			return bundles.getBundles(symbolicName);
		}
	}

	/**
	 * Returns a list of <tt>ServiceReference</tt> objects. This method
	 * returns a list of <tt>ServiceReference</tt> objects for services which
	 * implement and were registered under the specified class and match the
	 * specified filter criteria.
	 * 
	 * <p>
	 * The list is valid at the time of the call to this method, however as the
	 * Framework is a very dynamic environment, services can be modified or
	 * unregistered at anytime.
	 * 
	 * <p>
	 * <tt>filter</tt> is used to select the registered service whose
	 * properties objects contain keys and values which satisfy the filter. See
	 * {@link FilterImpl}for a description of the filter string syntax.
	 * 
	 * <p>
	 * If <tt>filter</tt> is <tt>null</tt>, all registered services are
	 * considered to match the filter.
	 * <p>
	 * If <tt>filter</tt> cannot be parsed, an {@link InvalidSyntaxException}
	 * will be thrown with a human readable message where the filter became
	 * unparsable.
	 * 
	 * <p>
	 * The following steps are required to select a service:
	 * <ol>
	 * <li>If the Java Runtime Environment supports permissions, the caller is
	 * checked for the <tt>ServicePermission</tt> to get the service with the
	 * specified class. If the caller does not have the correct permission,
	 * <tt>null</tt> is returned.
	 * <li>If the filter string is not <tt>null</tt>, the filter string is
	 * parsed and the set of registered services which satisfy the filter is
	 * produced. If the filter string is <tt>null</tt>, then all registered
	 * services are considered to satisfy the filter.
	 * <li>If <code>clazz</code> is not <tt>null</tt>, the set is further
	 * reduced to those services which are an <tt>instanceof</tt> and were
	 * registered under the specified class. The complete list of classes of
	 * which a service is an instance and which were specified when the service
	 * was registered is available from the service's
	 * {@link Constants#OBJECTCLASS}property.
	 * <li>An array of <tt>ServiceReference</tt> to the selected services is
	 * returned.
	 * </ol>
	 * 
	 * @param clazz
	 *            The class name with which the service was registered, or <tt>null</tt>
	 *            for all services.
	 * @param filterstring
	 *            The filter criteria.
	 * @return An array of <tt>ServiceReference</tt> objects, or <tt>null</tt>
	 *         if no services are registered which satisfy the search.
	 * @exception InvalidSyntaxException
	 *                If <tt>filter</tt> contains an invalid filter string
	 *                which cannot be parsed.
	 */
	protected ServiceReference[] getServiceReferences(String clazz, String filterstring, BundleContextImpl context, boolean allservices) throws InvalidSyntaxException {
		FilterImpl filter = (filterstring == null) ? null : new FilterImpl(filterstring);
		ServiceReference[] services = null;
		if (clazz != null) {
			try /* test for permission to get clazz */{
				checkGetServicePermission(clazz);
			} catch (SecurityException se) {
				return (null);
			}
		}
		synchronized (serviceRegistry) {
			services = serviceRegistry.lookupServiceReferences(clazz, filter);
			if (services == null) {
				return null;
			}
			int removed = 0;
			for (int i = services.length - 1; i >= 0; i--) {
				ServiceReferenceImpl ref = (ServiceReferenceImpl) services[i];
				String[] classes = ref.getClasses();
				if (allservices || context.isAssignableTo((ServiceReferenceImpl) services[i])) {
					if (clazz == null)
						try { /* test for permission to the classes */
							checkGetServicePermission(classes);
						} catch (SecurityException se) {
							services[i] = null;
							removed++;
						}
				} else {
					services[i] = null;
					removed++;
				}
			}
			if (removed > 0) {
				ServiceReference[] temp = services;
				services = new ServiceReference[temp.length - removed];
				for (int i = temp.length - 1; i >= 0; i--) {
					if (temp[i] == null)
						removed--;
					else
						services[i - removed] = temp[i];
				}
			}

		}
		return services == null || services.length == 0 ? null : services;
	}

	/**
	 * Method to return the next available service id. This method should be
	 * called while holding the registrations lock.
	 * 
	 * @return next service id.
	 */
	protected long getNextServiceId() {
		long id = serviceid;
		serviceid++;
		return (id);
	}

	/**
	 * Creates a <code>File</code> object for a file in the persistent
	 * storage area provided for the bundle by the framework. If the adaptor
	 * does not have file system support, this method will return <code>null</code>.
	 * 
	 * <p>
	 * A <code>File</code> object for the base directory of the persistent
	 * storage area provided for the context bundle by the framework can be
	 * obtained by calling this method with the empty string ("") as the
	 * parameter.
	 */
	protected File getDataFile(final AbstractBundle bundle, final String filename) {
		return (File) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return bundle.getBundleData().getDataFile(filename);
			}
		});
	}

	/**
	 * Check for specific AdminPermission (RFC 73)
	 */
	protected void checkAdminPermission(Bundle bundle, String action) {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			sm.checkPermission(new AdminPermission(bundle, action));
		}
	}

	/**
	 * Check for permission to register a service.
	 * 
	 * The caller must have permission for ALL names.
	 */
	protected void checkRegisterServicePermission(String[] names) {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			int len = names.length;
			for (int i = 0; i < len; i++) {
				sm.checkPermission(new ServicePermission(names[i], ServicePermission.REGISTER));
			}
		}
	}

	/**
	 * Check for permission to get a service.
	 * 
	 * The caller must have permission for at least ONE name.
	 */
	protected void checkGetServicePermission(String[] names) {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			SecurityException se = null;
			int len = names.length;
			for (int i = 0; i < len; i++) {
				try {
					sm.checkPermission(new ServicePermission(names[i], ServicePermission.GET));
					return;
				} catch (SecurityException e) {
					se = e;
				}
			}
			throw se;
		}
	}

	/**
	 * Check for permission to get a service.
	 */
	protected void checkGetServicePermission(String name) {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			sm.checkPermission(new ServicePermission(name, ServicePermission.GET));
		}
	}

	/**
	 * This is necessary for running from a JXE, otherwise the SecurityManager
	 * is set much later than we would like!
	 */
	protected void installSecurityManager() {
		String securityManager = System.getProperty("java.security.manager"); //$NON-NLS-1$
		if (securityManager != null) {
			SecurityManager sm = System.getSecurityManager();
			if (sm == null) {
				if (securityManager.length() < 1) {
					securityManager = "java.lang.SecurityManager"; //$NON-NLS-1$
				}
				try {
					Class clazz = Class.forName(securityManager);
					sm = (SecurityManager) clazz.newInstance();
					if (Debug.DEBUG && Debug.DEBUG_SECURITY) {
						Debug.println("Setting SecurityManager to: " + sm); //$NON-NLS-1$
					}
					System.setSecurityManager(sm);
					return;
				} catch (ClassNotFoundException e) {
					// do nothing
				} catch (ClassCastException e) {
					// do nothing
				} catch (InstantiationException e) {
					// do nothing
				} catch (IllegalAccessException e) {
					// do nothing
				}
				throw new NoClassDefFoundError(securityManager);
			}
		}
	}

	/**
	 * Deliver a FrameworkEvent.
	 * 
	 * @param type
	 *            FrameworkEvent type.
	 * @param bundle
	 *            Affected bundle or null for system bundle.
	 * @param throwable
	 *            Related exception or null.
	 */
	public void publishFrameworkEvent(int type, org.osgi.framework.Bundle bundle, Throwable throwable) {
		if (frameworkEvent != null) {
			if (bundle == null)
				bundle = systemBundle;
			final FrameworkEvent event = new FrameworkEvent(type, bundle, throwable);
			if (System.getSecurityManager() == null) {
				publishFrameworkEventPrivileged(event);
			} else {
				AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						publishFrameworkEventPrivileged(event);
						return null;
					}
				});
			}
		}
	}

	public void publishFrameworkEventPrivileged(FrameworkEvent event) {
		/* if the event is an error then it should be logged */
		if (event.getType() == FrameworkEvent.ERROR) {
			FrameworkLog frameworkLog = adaptor.getFrameworkLog();
			if (frameworkLog != null)
				frameworkLog.log(event);
		}
		/* queue to hold set of listeners */
		ListenerQueue listeners = new ListenerQueue(eventManager);
		/* queue to hold set of BundleContexts w/ listeners */
		ListenerQueue contexts = new ListenerQueue(eventManager);
		/* synchronize while building the listener list */
		synchronized (frameworkEvent) {
			/* add set of BundleContexts w/ listeners to queue */
			contexts.queueListeners(frameworkEvent, this);
			/* synchronously dispatch to populate listeners queue */
			contexts.dispatchEventSynchronous(FRAMEWORKEVENT, listeners);
		}
		/* dispatch event to set of listeners */
		listeners.dispatchEventAsynchronous(FRAMEWORKEVENT, event);
	}

	/**
	 * Deliver a BundleEvent to SynchronousBundleListeners (synchronous). and
	 * BundleListeners (asynchronous).
	 * 
	 * @param type
	 *            BundleEvent type.
	 * @param bundle
	 *            Affected bundle or null.
	 */
	public void publishBundleEvent(int type, org.osgi.framework.Bundle bundle) {
		if ((bundleEventSync != null) || (bundleEvent != null)) {
			final BundleEvent event = new BundleEvent(type, bundle);
			if (System.getSecurityManager() == null) {
				publishBundleEventPrivileged(event);
			} else {
				AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						publishBundleEventPrivileged(event);
						return null;
					}
				});
			}
		}
	}

	public void publishBundleEventPrivileged(BundleEvent event) {
		/*
		 * We must collect the snapshots of the sync and async listeners
		 * BEFORE we dispatch the event.
		 */
		/* Collect snapshot of SynchronousBundleListeners */
		ListenerQueue listenersSync = null;
		if (bundleEventSync != null) {
			/* queue to hold set of listeners */
			listenersSync = new ListenerQueue(eventManager);
			/* queue to hold set of BundleContexts w/ listeners */
			ListenerQueue contexts = new ListenerQueue(eventManager);
			/* synchronize while building the listener list */
			synchronized (bundleEventSync) {
				/* add set of BundleContexts w/ listeners to queue */
				contexts.queueListeners(bundleEventSync, this);
				/* synchronously dispatch to populate listeners queue */
				contexts.dispatchEventSynchronous(BUNDLEEVENTSYNC, listenersSync);
			}
		}
		/* Collect snapshot of BundleListeners */
		ListenerQueue listenersAsync = null;
		if (bundleEvent != null) {
			/* queue to hold set of listeners */
			listenersAsync = new ListenerQueue(eventManager);
			/* queue to hold set of BundleContexts w/ listeners */
			ListenerQueue contexts = new ListenerQueue(eventManager);
			/* synchronize while building the listener list */
			synchronized (bundleEvent) {
				/* add set of BundleContexts w/ listeners to queue */
				contexts.queueListeners(bundleEvent, this);
				/* synchronously dispatch to populate listeners queue */
				contexts.dispatchEventSynchronous(BUNDLEEVENT, listenersAsync);
			}
		}
		/* Dispatch BundleEvent to SynchronousBundleListeners */
		if (listenersSync != null) {
			listenersSync.dispatchEventSynchronous(BUNDLEEVENTSYNC, event);
		}
		/* Dispatch BundleEvent to BundleListeners */
		if (listenersAsync != null) {
			listenersAsync.dispatchEventAsynchronous(BUNDLEEVENT, event);
		}
	}

	/**
	 * Deliver a ServiceEvent.
	 * 
	 * @param type
	 *            ServiceEvent type.
	 * @param reference
	 *            Affected service reference.
	 */
	public void publishServiceEvent(int type, org.osgi.framework.ServiceReference reference) {
		if (serviceEvent != null) {
			final ServiceEvent event = new ServiceEvent(type, reference);
			if (System.getSecurityManager() == null) {
				publishServiceEventPrivileged(event);
			} else {
				AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						publishServiceEventPrivileged(event);
						return null;
					}
				});
			}
		}
	}

	public void publishServiceEventPrivileged(ServiceEvent event) {
		/* queue to hold set of listeners */
		ListenerQueue listeners = new ListenerQueue(eventManager);
		/* queue to hold set of BundleContexts w/ listeners */
		ListenerQueue contexts = new ListenerQueue(eventManager);
		/* synchronize while building the listener list */
		synchronized (serviceEvent) {
			/* add set of BundleContexts w/ listeners to queue */
			contexts.queueListeners(serviceEvent, this);
			/* synchronously dispatch to populate listeners queue */
			contexts.dispatchEventSynchronous(SERVICEEVENT, listeners);
		}
		/* dispatch event to set of listeners */
		listeners.dispatchEventSynchronous(SERVICEEVENT, event);
	}

	/**
	 * Top level event dispatcher for the framework.
	 * 
	 * @param l
	 *            BundleContext for receiving bundle
	 * @param lo
	 *            BundleContext for receiving bundle
	 * @param action
	 *            Event class type
	 * @param object
	 *            ListenerQueue to populate
	 */
	public void dispatchEvent(Object l, Object lo, int action, Object object) {
		try {
			BundleContextImpl context = (BundleContextImpl) l;
			if (context.isValid()) /* if context still valid */{
				ListenerQueue queue = (ListenerQueue) object;
				switch (action) {
					case BUNDLEEVENT : {
						queue.queueListeners(context.bundleEvent, context);
						break;
					}
					case BUNDLEEVENTSYNC : {
						queue.queueListeners(context.bundleEventSync, context);
						break;
					}
					case SERVICEEVENT : {
						queue.queueListeners(context.serviceEvent, context);
						break;
					}
					case FRAMEWORKEVENT : {
						queue.queueListeners(context.frameworkEvent, context);
						break;
					}
				}
			}
		} catch (Throwable t) {
			if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
				Debug.println("Exception in Top level event dispatcher: " + t.getMessage()); //$NON-NLS-1$
				Debug.printStackTrace(t);
			}
			// allow the adaptor to handle this unexpected error
			adaptor.handleRuntimeError(t);
			publisherror: {
				if (action == FRAMEWORKEVENT) {
					FrameworkEvent event = (FrameworkEvent) object;
					if (event.getType() == FrameworkEvent.ERROR) {
						break publisherror; /* avoid infinite loop */
					}
				}
				BundleContextImpl context = (BundleContextImpl) l;
				publishFrameworkEvent(FrameworkEvent.ERROR, context.bundle, t);
			}
		}
	}

	private String[] noMatches(boolean optional) throws BundleException {
		if (optional) {
			return null;
		}
		throw new BundleException(Msg.BUNDLE_NATIVECODE_MATCH_EXCEPTION);
	}
}
