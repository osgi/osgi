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
package org.osgi.impl.service.deploymentadmin;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import org.osgi.framework.*;
import org.osgi.impl.service.deploymentadmin.api.*;
import org.osgi.impl.service.megcontainer.MEGContainer;
import org.osgi.service.application.ApplicationContainer;
import org.osgi.util.tracker.ServiceTracker;

public class DeploymentAdmin implements PackageHandler, BundleActivator {
	// the DeploymentAdmin notifies the MEG Container that a bundle was
	// installed
	private static final String	CONTAINER_MEG	= "MEG";
	// the install(InputStream stream, String packageType, Map data) method
	// uses this key to retrieve the bundle location string from the go Map
	private static final String	LOCATION		= "location";
	private BundleContext		context;
	// tracks the MEG Container
	private ServiceTracker		trackerMegCont;

	public void start(BundleContext context) throws Exception {
		this.context = context;
		// it uses the MEG Container to notify about bundle installation
		Filter filter = context.createFilter("(&(" + Constants.OBJECTCLASS
				+ "=" + ApplicationContainer.class.getName() + ")("
				+ PackageHandler.PACKAGETYPE + "=" + CONTAINER_MEG + "))");
		trackerMegCont = new ServiceTracker(context, filter, null);
		trackerMegCont.open();
		// registers itself as a PackageHandler
		Dictionary dict = new Hashtable();
		dict.put(PackageHandler.PACKAGETYPE, PACKAGETYPE_BUNDLE + ","
				+ PACKAGETYPE_SUITE);
		// unregistered by the OSGi framework
		context.registerService(PackageHandler.class.getName(), this, dict);
	}

	public void stop(BundleContext context) throws Exception {
		trackerMegCont.close();
	}

	public void install(InputStream stream, String packageType, Map data)
			throws PackageHandlerException {
		// bundle
		if (PACKAGETYPE_BUNDLE.equals(packageType)) {
			String location = (String) data.get(LOCATION);
			installBundle(stream, location);
		}
		// suite
		else
			if (PACKAGETYPE_SUITE.equals(packageType)) {
				installSuite(stream);
				// error
			}
			else {
				throw new PackageHandlerException("Package type " + packageType
						+ " is not supported");
			}
	}

	// installs a bundle
	private void installBundle(InputStream stream, String location)
			throws PackageHandlerException {
		try {
			Bundle b = context.installBundle(location, stream);
			// TODO is it needed?
			b.start();
			//((MEGContainer) trackerMegCont.getService()).installApplication(b
			//		.getBundleId());
		}
		catch (BundleException e) {
			throw new PackageHandlerException(e.getMessage());
		}
		catch (Exception e) {
            e.printStackTrace();
			throw new PackageHandlerException(e.getClass().getName()
					+ " exception occured during MEG container invocation");
		}
	}

	// installs the suite
	private void installSuite(InputStream stream)
			throws PackageHandlerException {
		try {
			WrappedJarInputStream wjis = new WrappedJarInputStream(stream);
			int ch;
			JarEntry entry = wjis.getNextJarEntry();
			while (null != entry) {
				ByteArrayInputStream bis = getStream(entry, wjis);
				if (null != bis) {
					try {
						Bundle b = context.installBundle(entry.getName(), bis);
						// TODO is it needed?
						b.start();
						((MEGContainer) trackerMegCont.getService())
								.installApplication(b.getBundleId());
					}
					catch (BundleException e) {
						throw new PackageHandlerException(e.getMessage());
					}
					finally {
						try {
							bis.close();
						}
						catch (IOException ee) {
						}
					}
				}
				wjis.closeEntry();
				entry = wjis.getNextJarEntry();
			}
		}
		catch (IOException e) {
			throw new PackageHandlerException(e.getClass().getName()
					+ " exception occured during suite installation");
		}
		catch (Exception e) {
			throw new PackageHandlerException(e.getClass().getName()
					+ " exception occured during MEG container invocation");
		}
	}

	private ByteArrayInputStream getStream(JarEntry entry,
			WrappedJarInputStream wjis) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			int ch = wjis.read();
			while (-1 != ch) {
				bos.write(ch);
				ch = wjis.read();
			}
			return new ByteArrayInputStream(bos.toByteArray());
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			try {
				bos.close();
			}
			catch (IOException ee) {
			}
		}
	}

	// uninstalls a bundle
	public void uninstall(long bundleID) throws PackageHandlerException {
		Bundle b = context.getBundle(bundleID);
		try {
			b.uninstall();
		}
		catch (BundleException e) {
			throw new PackageHandlerException(
					"Unable to uninstall the bundle (id: " + bundleID
							+ "). Cause: " + e.getMessage());
		}
	}
}
