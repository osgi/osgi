/*
 * Copyright (c) OSGi Alliance (2004, 2016). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.permissionadmin.tb1;

import static junit.framework.TestCase.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.permissionadmin.service.PermissionSignatureTBCService;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.reflect.MethodCall;
import org.osgi.test.support.wiring.Wiring;

/**
 * A bundle that registers a PermissionSignatureTBCService and does a privileged
 * operations.
 * 
 * @author Petia Sotirova
 * @version 1.0
 */
public class Activator implements BundleActivator,
		PermissionSignatureTBCService, SynchronousBundleListener {

	private BundleContext	bc;
	private Object			startLevel;
	private PermissionAdmin	permissionAdmin;
	private Object			packageAdmin;

	public void start(BundleContext context) throws Exception {
		this.bc = context;
		context.registerService(PermissionSignatureTBCService.class.getName(),
				this, null);

		startLevel = getService("org.osgi.service.startlevel.StartLevel");
		permissionAdmin = (PermissionAdmin) getService(PermissionAdmin.class
				.getName());
		packageAdmin = getService("org.osgi.service.packageadmin.PackageAdmin");
	}

	public void stop(BundleContext context) throws Exception {
		// empty
	}

	public void bundleChanged(BundleEvent event) {
		// empty
	}

	private Object getService(String serviceName) {
		ServiceReference< ? > reference = bc.getServiceReference(serviceName);
		if (reference != null) {
			return bc.getService(reference);
		}
		return null;
	}

	public Dictionary<String,String> callBundle_getHeaders(Bundle bundle) {
		return bundle.getHeaders();
	}

	public Dictionary<String,String> callBundle_getHeaders(Bundle bundle,
			String localeString) {
		return bundle.getHeaders(localeString);
	}

	public String callBundle_getLocation(Bundle bundle) {
		return bundle.getLocation();
	}

	public URL callBundle_getResource(Bundle bundle, String name) {
		return bundle.getResource(name);
	}

	public Enumeration<URL> callBundle_getResources(Bundle bundle, String name)
			throws IOException {
		return bundle.getResources(name);
	}

	public URL callBundle_getEntry(Bundle bundle, String name) {
		return bundle.getEntry(name);
	}

	public Enumeration<String> callBundle_getEntryPaths(Bundle bundle,
			String path) {
		return bundle.getEntryPaths(path);
	}

	public Class< ? > callBundle_loadClass(Bundle bundle, String name)
			throws ClassNotFoundException {
		return bundle.loadClass(name);
	}

	public void callBundle_stop(Bundle bundle) throws BundleException {
		bundle.stop();
	}

	public void callBundle_uninstall(Bundle bundle) throws BundleException {
		bundle.uninstall();
	}

	public void callBundle_update(Bundle bundle) throws BundleException {
		bundle.update();
	}

	public void callBundle_update(Bundle bundle, InputStream in)
			throws BundleException {
		bundle.update(in);
	}

	public void callBundle_start(Bundle bundle) throws BundleException {
		bundle.start();
	}

	// from BundleContext class
	public Bundle callBundleContext_installBundle(BundleContext context,
			String location) throws BundleException {
		return context.installBundle(location);
	}

	public Bundle callBundleContext_installBundle(BundleContext context,
			String location, InputStream input) throws BundleException {
		return context.installBundle(location, input);
	}

	public void callBundleContext_addBundleListener(BundleContext context) {
		context.addBundleListener(this);
	}

	public void callBundleContext_removeBundleListener(BundleContext context) {
		context.removeBundleListener(this);
	}

	// from StartLevel service
	public void callStartLevel_setBundleStartLevel(Bundle bundle, Integer level) {
		if (startLevel == null) {
			fail("No StartLevel service");
		}
		log("###StartLevel.setBundleStartLevel(" + bundle + "," + level + ")");
		MethodCall setBundleStartLevel = new MethodCall(
				"org.osgi.service.startlevel.StartLevel",
				"setBundleStartLevel", Bundle.class, Integer.TYPE);
		try {
			setBundleStartLevel.invoke(startLevel, bundle, level);
		}
		catch (NoSuchMethodException e) {
			OSGiTestCase.fail("missing method", e);
		}
		catch (IllegalAccessException e) {
			OSGiTestCase.fail("method access exception", e);
		}
		catch (RuntimeException r) {
			throw r;
		}
		catch (Throwable t) {
			OSGiTestCase.fail("method exception", t);
		}
	}

	public void callStartLevel_setStartLevel(Integer level) {
		if (startLevel == null) {
			fail("No StartLevel service");
		}
		log("###StartLevel.setStartLevel(" + level + ")");
		MethodCall setStartLevel = new MethodCall(
				"org.osgi.service.startlevel.StartLevel", "setStartLevel",
				Integer.TYPE);
		try {
			setStartLevel.invoke(startLevel, level);
		}
		catch (NoSuchMethodException e) {
			OSGiTestCase.fail("missing method", e);
		}
		catch (IllegalAccessException e) {
			OSGiTestCase.fail("method access exception", e);
		}
		catch (RuntimeException r) {
			throw r;
		}
		catch (Throwable t) {
			OSGiTestCase.fail("method exception", t);
		}
	}

	public void callStartLevel_setInitialBundleStartLevel(Integer level) {
		if (startLevel == null) {
			fail("No StartLevel service");
		}
		log("###StartLevel.setInitialBundleStartLevel(" + level + ")");
		MethodCall setInitialBundleStartLevel = new MethodCall(
				"org.osgi.service.startlevel.StartLevel",
				"setInitialBundleStartLevel", Integer.TYPE);
		try {
			setInitialBundleStartLevel.invoke(startLevel, level);
		}
		catch (NoSuchMethodException e) {
			OSGiTestCase.fail("missing method", e);
		}
		catch (IllegalAccessException e) {
			OSGiTestCase.fail("method access exception", e);
		}
		catch (RuntimeException r) {
			throw r;
		}
		catch (Throwable t) {
			OSGiTestCase.fail("method exception", t);
		}
	}

	public void callBundleStartLevel_setStartLevel(Bundle bundle, Integer level) {
		int l = level.intValue();
		log("###BundleStartLevel.setStartLevel(" + bundle + "," + l + ")");
		BundleStartLevel bsl = bundle.adapt(BundleStartLevel.class);
		bsl.setStartLevel(l);
	}

	public void callFrameworkStartLevel_setStartLevel(Integer level) {
		int l = level.intValue();
		log("###FrameworkStartLevel.setStartLevel(" + l + ")");
		Bundle systemBundle = bc.getBundle(0);
		FrameworkStartLevel fsl = systemBundle.adapt(FrameworkStartLevel.class);
		fsl.setStartLevel(l);
	}

	public void callFrameworkStartLevel_setInitialBundleStartLevel(Integer level) {
		int l = level.intValue();
		log("###FrameworkStartLevel.setInitialBundleStartLevel(" + l + ")");
		Bundle systemBundle = bc.getBundle(0);
		FrameworkStartLevel fsl = systemBundle.adapt(FrameworkStartLevel.class);
		fsl.setInitialBundleStartLevel(l);
	}

	// from PermisssionAdmin service
	public void callPermissionAdmin_setPermissions(String location,
			PermissionInfo[] permissions) {
		log("###PermissionAdmin.setPermissions(" + location + ","
				+ toString(permissions) + ")");
		permissionAdmin.setPermissions(location, permissions);
	}

	public void callPermissionAdmin_setDefaultPermissions(
			PermissionInfo[] permissions) {
		log("###PermissionAdmin.setDefaultPermissions(" + toString(permissions)
				+ ")");
		permissionAdmin.setDefaultPermissions(permissions);
	}

	// from PackageAdmin service
	public void callPackageAdmin_refreshPackages(Bundle[] bundles) {
		if (packageAdmin == null) {
			fail("No PackageAdmin service");
		}
		log("###PackageAdmin.refreshPackages(" + toString(bundles) + ")");
		MethodCall refreshPackages = new MethodCall(
				"org.osgi.service.packageadmin.PackageAdmin",
				"refreshPackages", Bundle[].class);
		try {
			refreshPackages.invoke(packageAdmin, (Object) bundles);
		}
		catch (NoSuchMethodException e) {
			OSGiTestCase.fail("missing method", e);
		}
		catch (IllegalAccessException e) {
			OSGiTestCase.fail("method access exception", e);
		}
		catch (RuntimeException r) {
			throw r;
		}
		catch (Throwable t) {
			OSGiTestCase.fail("method exception", t);
		}
	}

	public boolean callPackageAdmin_resolveBundles(Bundle[] bundles) {
		if (packageAdmin == null) {
			fail("No PackageAdmin service");
		}
		log("###PackageAdmin.resolveBundles(" + toString(bundles) + ")");
		MethodCall resolveBundles = new MethodCall(
				"org.osgi.service.packageadmin.PackageAdmin", "resolveBundles",
				Bundle[].class);
		try {
			Boolean result = (Boolean) resolveBundles.invoke(packageAdmin,
					(Object) bundles);
			return result.booleanValue();
		}
		catch (NoSuchMethodException e) {
			OSGiTestCase.fail("missing method", e);
		}
		catch (IllegalAccessException e) {
			OSGiTestCase.fail("method access exception", e);
		}
		catch (RuntimeException r) {
			throw r;
		}
		catch (Throwable t) {
			OSGiTestCase.fail("method exception", t);
		}
		return false;
	}

	public void callFrameworkWiring_refreshBundles(Bundle... bundles) {
		log("###FrameworkWiring.refreshBundles(" + toString(bundles) + ")");
		Wiring.synchronousRefreshBundles(bc, bundles);
	}

	public boolean callFrameworkWiring_resolveBundles(Bundle... bundles) {
		log("###FrameworkWiring.resolveBundles(" + toString(bundles) + ")");
		return Wiring.resolveBundles(bc, bundles);
	}

	public static void log(String message) {
		System.out.println(message);
	}

	private static String toString(Object[] array) {
		if (array == null) {
			return "null";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(array[i]);
		}
		sb.append("}");
		return sb.toString();
	}
}
