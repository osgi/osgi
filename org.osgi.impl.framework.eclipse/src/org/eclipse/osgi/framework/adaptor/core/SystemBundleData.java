/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor.core;

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.internal.core.Constants;
import org.eclipse.osgi.framework.util.Headers;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

public class SystemBundleData extends AbstractBundleData {
	public static final String OSGI_FRAMEWORK = "osgi.framework"; //$NON-NLS-1$

	public SystemBundleData(AbstractFrameworkAdaptor adaptor) throws BundleException {
		super(adaptor, 0);
		File osgiBase = getOsgiBase();
		createBundleFile(osgiBase);
		manifest = createManifest(osgiBase);
		setMetaData();
		setLastModified(System.currentTimeMillis()); // just set the lastModified to the current time
	}

	private File getOsgiBase() {
		String frameworkLocation = System.getProperty(OSGI_FRAMEWORK);
		if (frameworkLocation != null)
			// TODO assumes the location is a file URL
			return new File(frameworkLocation.substring(5));
		frameworkLocation = System.getProperty("user.dir"); //$NON-NLS-1$
		if (frameworkLocation != null)
			return new File(frameworkLocation);
		return null;
	}

	private Headers createManifest(File osgiBase) throws BundleException {
		InputStream in = null;

		if (osgiBase != null && osgiBase.exists()) {
			try {
				BundleEntry entry = baseBundleFile.getEntry(Constants.OSGI_BUNDLE_MANIFEST);
				if (entry != null)
					in = entry.getInputStream();
			} catch (IOException e) {
				// do nothing here.  in == null
			}
		}

		// If we cannot find the Manifest file from the baseBundleFile then
		// search for the manifest as a classloader resource
		// This allows an adaptor to package the SYSTEMBUNDLE.MF file in a jar.
		if (in == null) {
			in = getManifestAsResource();
		}
		if (Debug.DEBUG && Debug.DEBUG_GENERAL) {
			if (in == null) {
				Debug.println("Unable to find system bundle manifest " + Constants.OSGI_BUNDLE_MANIFEST); //$NON-NLS-1$
			}
		}

		if (in == null)
			throw new BundleException(AdaptorMsg.SYSTEMBUNDLE_MISSING_MANIFEST);
		Headers systemManifest = Headers.parseManifest(in);
		// check the OSGi system package property
		// first check the OSGi R4 spec'ed property
		String systemExportProp = System.getProperty(Constants.OSGI_FRAMEWORK_SYSTEM_PACKAGES);
		if (systemExportProp != null)
			appendManifestValue(systemManifest, Constants.EXPORT_PACKAGE, systemExportProp);
		// now check the original pre OSGi R4 property
		systemExportProp = System.getProperty(Constants.OSGI_SYSTEMPACKAGES);
		if (systemExportProp != null)
			appendManifestValue(systemManifest, Constants.EXPORT_PACKAGE, systemExportProp);
		// now get any extra packages and services that the adaptor wants
		// to export and merge this into the system bundle's manifest
		String exportPackages = adaptor.getExportPackages();
		String exportServices = adaptor.getExportServices();
		String providePackages = adaptor.getProvidePackages();
		if (exportPackages != null)
			appendManifestValue(systemManifest, Constants.EXPORT_PACKAGE, exportPackages);
		if (exportServices != null)
			appendManifestValue(systemManifest, Constants.EXPORT_SERVICE, exportServices);
		if (providePackages != null)
			appendManifestValue(systemManifest, Constants.PROVIDE_PACKAGE, providePackages);
		return systemManifest;
	}

	private InputStream getManifestAsResource() {
		ClassLoader cl = getClass().getClassLoader();
		try {
			// get all manifests in your classloader delegation
			Enumeration manifests = cl != null ? cl.getResources(Constants.OSGI_BUNDLE_MANIFEST) : ClassLoader.getSystemResources(Constants.OSGI_BUNDLE_MANIFEST);
			while (manifests.hasMoreElements()) {
				URL url = (URL) manifests.nextElement();
				try {
					// check each manifest until we find one with the Eclipse-SystemBundle: true header
					Headers headers = Headers.parseManifest(url.openStream());
					if ("true".equals(headers.get(Constants.ECLIPSE_SYSTEMBUNDLE))) //$NON-NLS-1$
						return url.openStream();
				} catch (BundleException e) {
					// ignore and continue to next URL
				}
			}
		} catch (IOException e) {
			// ignore and return null
		}
		return null;
	}

	private void appendManifestValue(Headers systemManifest, String header, String append) {
		String newValue = (String) systemManifest.get(header);
		if (newValue == null) {
			newValue = append;
		} else {
			newValue += "," + append; //$NON-NLS-1$
		}
		systemManifest.set(header, null);
		systemManifest.set(header, newValue);
	}

	private void createBundleFile(File osgiBase) {
		if (osgiBase != null)
			try {
				baseBundleFile = adaptor.createBundleFile(osgiBase, this);
			} catch (IOException e) {
				// should not happen
			}
		else
			baseBundleFile = new BundleFile(osgiBase) {
				public File getFile(String path) {
					return null;
				}

				public BundleEntry getEntry(String path) {
					return null;
				}

				public Enumeration getEntryPaths(String path) {
					return null;
				}

				public void close() {
					// do nothing
				}

				public void open() {
					// do nothing
				}

				public boolean containsDir(String dir) {
					return false;
				}
			};
	}

	private void setMetaData() {
		setActivator((String) manifest.get(Constants.BUNDLE_ACTIVATOR));
		setClassPathString((String) manifest.get(Constants.BUNDLE_CLASSPATH));
		setDynamicImports((String) manifest.get(Constants.DYNAMICIMPORT_PACKAGE));
		setExecutionEnvironment((String) manifest.get(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT));
		setLocation(Constants.SYSTEM_BUNDLE_LOCATION);
		setSymbolicName(AbstractBundleData.parseSymbolicName(manifest));
		String sVersion = (String) manifest.get(Constants.BUNDLE_VERSION);
		if (sVersion != null)
			setVersion(Version.parseVersion(sVersion));
	}

	public BundleClassLoader createClassLoader(ClassLoaderDelegate delegate, BundleProtectionDomain domain, String[] bundleclasspath) {
		return null;
	}

	public File createGenerationDir() {
		return null;
	}

	public String findLibrary(String libname) {
		return null;
	}

	public void installNativeCode(String[] nativepaths) throws BundleException {
		// do nothing
	}

	public File getDataFile(String path) {
		return null;
	}

	public int getStartLevel() {
		return 0;
	}

	public int getStatus() {
		return 0;
	}

	public void close() {
		// do nothing
	}

	public void open() {
		// do nothing
	}

	public void save() {
		// do nothing
	}

	public String[] getBundleSigners() {
		return null; // system bundle cannot be signed
	}
}
