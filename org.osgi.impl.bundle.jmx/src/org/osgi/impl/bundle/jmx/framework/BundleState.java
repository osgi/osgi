/*
 * Copyright 2008 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.impl.bundle.jmx.framework;

import java.io.IOException;
import java.util.ArrayList;

import javax.management.Notification;
import javax.management.openmbean.TabularData;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.impl.bundle.jmx.Monitor;
import org.osgi.jmx.codec.OSGiBundle;
import org.osgi.jmx.codec.OSGiBundleEvent;
import org.osgi.jmx.codec.Util;
import org.osgi.jmx.framework.BundleStateMBean;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

/** 
 * 
 */
public class BundleState extends Monitor implements BundleStateMBean {
	public BundleState(BundleContext bc, StartLevel sl, PackageAdmin admin) {
		this.bc = bc;
		this.sl = sl;
		this.admin = admin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getBundleDependencies()
	 */
	public long[] getDependencies(long bundleIdentifier) throws IOException {
		return Util.getBundleDependencies(bundle(bundleIdentifier), admin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getBundles()
	 */
	public TabularData getBundles() throws IOException {
		try {
			ArrayList<OSGiBundle> bundles = new ArrayList<OSGiBundle>();
			for (Bundle bundle : bc.getBundles()) {
				bundles.add(new OSGiBundle(bc, admin, sl, bundle));
			}
			TabularData table = OSGiBundle.tableFrom(bundles);
			return table;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getExportedPackages()
	 */
	public String[] getExportedPackages(long bundleId) throws IOException {
		ExportedPackage[] packages = admin.getExportedPackages(bundle(bundleId));
		if (packages == null) {
			return new String[0];
		}
		String[] ep = new String[packages.length];
		for (int i = 0; i < packages.length; i++) {
			ep[i] = packages[i].getName() + ";" + packages[i].getVersion();
		}
		return ep;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getFragments()
	 */
	public long[] getFragments(long bundleId) throws IOException {
		return Util.getBundleFragments(bundle(bundleId), admin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getHeaders(long)
	 */
	public TabularData getHeaders(long bundleId) throws IOException {
		return OSGiBundle.headerTable(bundle(bundleId));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getHosts(long)
	 */
	public long[] getHosts(long fragment) throws IOException {
		Bundle[] hosts = admin.getHosts(bundle(fragment));
		if (hosts == null) {
			return new long[0];
		}
		return Util.bundleIds(hosts);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getImportedPackages()
	 */
	public String[] getImportedPackages(long bundleId) throws IOException {
		return Util.getBundleImportedPackages(bundle(bundleId), bc, admin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getLastModified(long)
	 */
	public long getLastModified(long bundleId) throws IOException {
		return bundle(bundleId).getLastModified();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getRegisteredServices()
	 */
	public long[] getRegisteredServices(long bundleId) throws IOException {
		return Util.serviceIds(bundle(bundleId).getRegisteredServices());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getRequiringBundles()
	 */
	public long[] getRequiringBundles(long bundleIdentifier) throws IOException {
		return Util.getBundlesRequiring(bundle(bundleIdentifier), bc, admin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getServicesInUse()
	 */
	public long[] getServicesInUse(long bundleIdentifier) throws IOException {
		return Util.serviceIds(bundle(bundleIdentifier).getServicesInUse());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getStartLevel(long)
	 */
	public int getStartLevel(long bundleId) throws IOException {
		return sl.getBundleStartLevel(bundle(bundleId));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getState(long)
	 */
	public String getState(long bundleId) throws IOException {
		return Util.getBundleState(bundle(bundleId));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#getSymbolicName(long)
	 */
	public String getSymbolicName(long bundleId) throws IOException {
		return bundle(bundleId).getSymbolicName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#isBundlePersistentlyStarted(long)
	 */
	public boolean isPersistentlyStarted(long bundleId) throws IOException {
		return Util.isBundlePersistentlyStarted(bundle(bundleId), sl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#isFragment(long)
	 */
	public boolean isFragment(long bundleId) throws IOException {
		return Util.isBundleFragment(bundle(bundleId), admin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#isRemovalPending(long)
	 */
	public boolean isRemovalPending(long bundleId) throws IOException {
		return Util.isRequiredBundleRemovalPending(bundle(bundleId), bc, admin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.core.BundleStateMBean#isRequired(long)
	 */
	public boolean isRequired(long bundleId) throws IOException {
		return Util.isBundleRequired(bundle(bundleId), bc, admin);
	}

	private Bundle bundle(long bundleId) {
		Bundle b = bc.getBundle(bundleId);
		if (b == null) {
			throw new IllegalArgumentException("Bundle with id: " + bundleId
					+ " does not exist");
		}
		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.Monitor#addListener()
	 */
	@Override
	protected void addListener() {
		bundleListener = getBundleListener();
		bc.addBundleListener(bundleListener);
	}

	protected BundleListener getBundleListener() {
		return new BundleListener() {
			public void bundleChanged(BundleEvent bundleEvent) {
				Notification notification = new Notification(BUNDLE_EVENT_TYPE,
						objectName, sequenceNumber++);
				notification.setUserData(new OSGiBundleEvent(bundleEvent)
						.asCompositeData());
				sendNotification(notification);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.Monitor#removeListener()
	 */
	@Override
	protected void removeListener() {
		if (bundleListener != null) {
			bc.removeBundleListener(bundleListener);
		}
	}

	protected BundleListener bundleListener;
	protected BundleContext bc;
	protected StartLevel sl;
	protected PackageAdmin admin;

}
